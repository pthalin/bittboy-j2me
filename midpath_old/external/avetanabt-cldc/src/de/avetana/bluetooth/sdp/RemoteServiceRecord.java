package de.avetana.bluetooth.sdp;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.util.BTAddress;

/**
 * The class used to manage remote service records.
 *
 * <br><br><b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description:</b><br>
 * <dd>This class is used to manage the remote service records. As requested by the JSR82 Specification, the class
 * is an instance of javax.bluetooth.ServiceRecord. <br>
 * <dd>A remote service search will always return RemoteServiceRecord objects. Indeed, the common use of a JSR82 implementation
 * is to first make a service search, then to select the desired service, to retrieve te connection URL and finally to connect with the remote device.
 * That's why the method getConnectionURL(..) of this class is one of the most used.
 *
 * The parts to handle Microsoft ServiceRecords (readElement()....) of this code are from the bluecove project http://sourceforge.net/projects/bluecove
 *
 * @author Julien Campana
 */
public class RemoteServiceRecord extends SDPServiceRecord {

	
  //public byte[] raw = new byte[0];
  /**
   * The remote device this service belongs to
   */
  private RemoteDevice m_remote;
  /* if no uuid is specified, the service search method will use
  the uuid 0x1002 (PublicBrowseGroup, see therefore the sdp assigned numbers
  at http://www.bluetooth.org)
  */
  private final byte[][] m_uuid= new byte[][] { {0x10, 0x02} };

  /**
   * The internal DiscoveryListener used to re-populate the service record
   */
  private InternListener m_internListener;

  /**
   * Default constructor: creates a RemoteServiceRecord object, which extends SDPServiceRecord
   * @see de.avetana.bluetooth.sdp.SDPServiceRecord
   */
  public RemoteServiceRecord() {
    super();
  }

  /**
   * Creates a RemoteServiceRecord object and sets the remote device this service belongs to.
   * @param badr The BT address of the remote device
   */
  public RemoteServiceRecord(String badr) {
    this();
    m_remote=new RemoteDevice(badr);
  }

  /**
   * Returns the remote device this service belongs to
   * @return The remote device this service belongs to
   */
  public RemoteDevice getHostDevice() {
    return m_remote;
  }

  /**
   * JSR82 Specification: <br>
   * Retrieves the values by contacting the remote Bluetooth device
   * for a set of service attribute IDs of a service that is available
   * on a Bluetooth device.  (This involves going over the air and
   * contacting the remote device for the attribute values.)  The system
   * might impose a limit on the number of service attribute ID
   * values one can request at a time.  Applications can obtain the value of this limit as a String by calling
   * <code>LocalDevice.getProperty("bluetooth.sd.attr.retrievable.max")</code>.
   * The method is blocking and will return when the results of the request are available.  Attribute IDs whose
   * values could be obtained are added to this service record. If
   * there exist attribute IDs for which values are retrieved this
   * will cause the old values to be overwritten. If the remote
   * device cannot be reached, an <code>IOException</code> will be thrown.
   * @param attrIDs the list of service attributes IDs whose value
   * are to be retrieved; the number of attributes cannot exceed the
   * property <code>bluetooth.sd.attr.retrievable.max</code>; the
   * attributes in the request must be legal, i.e. their values are
   * in the range of [0, 2<sup>16</sup>-1]. The input attribute IDs
   * can include attribute IDs from the default attribute set too.
   * @return <code>true</code> if the request was successful in retrieving values for some or all of the attribute IDs;
   * <code>false</code> if it was unsuccessful in retrieving any values
   * @exception IOException if the local device is unable to connect
   * to the remote Bluetooth device that was the source of this <code>ServiceRecord</code>; if this
   * <code>ServiceRecord</code> was deleted from the SDDB of the remote device
   * @exception IllegalArgumentException if the size of <code>attrIDs</code> exceeds the system specified limit as
   * defined by <code>bluetooth.sd.attr.retrievable.max</code>; if the
   * <code>attrIDs</code> array length is zero; if any of their values are not in the range of [0, 2<sup>16</sup>-1]; if
   * <code>attrIDs</code> has duplicate values
   * @exception NullPointerException if <code>attrIDs</code> is <code>null</code>
   * @exception RuntimeException if this <code>ServiceRecord</code>
   * describes a service on the local device rather than a service on a remote device
     */
  public boolean populateRecord(int[] attr) throws java.io.IOException {
    final int[] attrs=attr;
    m_internListener=new InternListener();
    Runnable r=new Runnable() {
      public void run() {
    	  //LibLoader.cremeInit(this);
        String addr=m_remote.getBluetoothAddress();
        try {addr=BTAddress.transform(addr);}catch(Exception ex) {}
        try {
          BlueZ.searchServices(addr,m_uuid,attrs,m_internListener);
        }catch(Exception ex) {
           m_internListener.setResponse(0);
        }
        //LibLoader.cremeOut(this);
      }
    };
    try {
		BlueZ.executor.execute(r);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
    while(m_internListener.resp==-1) {
      try {
        Thread.currentThread().sleep(100);
      }catch(Exception ex) {return false;}
    }
    return (m_internListener.resp==1);
  }

  /**
   * JSR82 Specification:<br>
   * Returns a String including optional parameters that can be used by a client to connect to the service described by this
   * <code>ServiceRecord</code>.  The return value can be used as the
   * first argument to <code>Connector.open()</code>. In the case of a
   * Serial Port service record, this string might look like
   * "btspp://0050CD00321B:3;authenticate=true;encrypt=false;master=true", where "0050CD00321B" is the Bluetooth
   * address of the device that provided this <code>ServiceRecord</code>, "3" is the RFCOMM
   * server channel mentioned in this <code>ServiceRecord</code>, and
   * there are three optional parameters related to security and master/slave roles. <P>
   * If this method is called on a <code>ServiceRecord</code> returned
   * from <code>LocalDevice.getRecord()</code>, it will return the
   * connection string that a remote device will use to connect to this service.<br>
   * AvetanaBluetooth:<br>
   * The implementation of getConnectionURL() only supports RFCOMM and L2CAP.
   * @see #NOAUTHENTICATE_NOENCRYPT
   * @see #AUTHENTICATE_NOENCRYPT
   * @see #AUTHENTICATE_ENCRYPT
   * @param requiredSecurity determines whether authentication or encryption are required for a connection
   * @param mustBeMaster <code>true</code> indicates that this device
   * must play the role of master in connections to this service;
   * <code>false</code> indicates that the local device is willing to be either the master or the slave
   * @return a string that can be used to connect to the service or <code>null</code> if the ProtocolDescriptorList in this
   * ServiceRecord is not formatted according to the Bluetooth specification
   * @exception IllegalArgumentException if <code>requiredSecurity</code> is not one of the constants
   * <code>NOAUTHENTICATE_NOENCRYPT</code>, <code>AUTHENTICATE_NOENCRYPT</code>, or <code>AUTHENTICATE_ENCRYPT</code>
     */
  public String getConnectionURL(int requiredSecurity, boolean mustBeMaster) throws IllegalArgumentException {
    if (m_remote == null) throw new IllegalArgumentException("No remote device found");
    String url = "";
    DataElement protocolDescriptorListElement = (DataElement)m_attributes.get(new Integer(4));
    if (protocolDescriptorListElement == null)
      throw new IllegalArgumentException("Protocol Descriptor is missing. You should maybe populate this Service Record with attrId=0x0004");
    Enumeration protocolDescriptorList = (Enumeration)protocolDescriptorListElement.getValue();
    long l2capPSM=-1;
    long rfcommChannel=-1;
    boolean isObex = false;
    while (protocolDescriptorList.hasMoreElements()) {
      DataElement protocolDescriptorElement = (DataElement)protocolDescriptorList.nextElement();
      if (protocolDescriptorElement == null)
        throw new IllegalArgumentException("Protocol Descriptor is missing. You should maybe populate this Service Record with attrId=0x0004");
      Enumeration protocolParameterList = (Enumeration)protocolDescriptorElement.getValue();
      if (protocolParameterList.hasMoreElements()) {
        DataElement protocolDescriptor = (DataElement)protocolParameterList.nextElement();
        if (protocolDescriptor != null) {
          if (protocolDescriptor.getDataType() == DataElement.UUID) {
            UUID protocolDescriptorUUID = (UUID)protocolDescriptor.getValue();
            long lg=protocolDescriptorUUID.toLong();
            if(lg == 0x100 || lg == 0x3) { // l2cap or rfcomm
              if (protocolParameterList.hasMoreElements()) {
                DataElement protocolPSMElement = (DataElement)protocolParameterList.nextElement();
                if (protocolPSMElement != null) {
                  if (lg==0x3)// && protocolPSMElement.getDataType() == DataElement.U_INT_1)
                    rfcommChannel = protocolPSMElement.getLong();
                  else if (lg==0x100)// && protocolPSMElement.getDataType() == DataElement.U_INT_2)
                    l2capPSM = protocolPSMElement.getLong();
                }
              }
            }
            else if(lg == 0x0008) isObex = true;
            else continue;
          }
        }
      }
    }
    if (isObex && rfcommChannel != -1) url += "btgoep://"+m_remote.getBluetoothAddress()+":"+rfcommChannel;
    else if (rfcommChannel!=-1) url+="btspp://"+m_remote.getBluetoothAddress()+":"+rfcommChannel;
    else if (l2capPSM!=-1) url+="btl2cap://"+m_remote.getBluetoothAddress()+":"+Long.toString(l2capPSM, 16);
    if(url.equals("")) return null;
    url+=";";

    if (requiredSecurity == ServiceRecord.AUTHENTICATE_ENCRYPT) url += "authenticate=true;encrypt=true;";
    else if (requiredSecurity == ServiceRecord.AUTHENTICATE_NOENCRYPT) url += "authenticate=true;encrypt=false;";
    if (requiredSecurity == ServiceRecord.NOAUTHENTICATE_NOENCRYPT) url += "authenticate=false;encrypt=false;";
    url += "master=" + mustBeMaster;
    return url;
  }

  /**
   * This method is not yet supported by the implementation.
   * @param parm1
   */
  public void setDeviceServiceClasses(int parm1) {
    throw new java.lang.Error("Method setDeviceServiceClasses() not yet implemented.");
  }

  private class InternListener implements DiscoveryListener {

    private int resp=-1;

    public InternListener() {}

    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
      for(int i=0;i<servRecord.length;i++) {
        RemoteServiceRecord myRec=(RemoteServiceRecord)servRecord[i];
        if(myRec.getRecordHandle()==RemoteServiceRecord.this.m_recordHandle) {
          int[] attr=myRec.getAttributeIDs();
          for(int u=0;u<attr.length;u++) {
            DataElement dat=myRec.getAttributeValue(attr[u]);
            if(dat!=null) setAttributeValue(attr[u],dat);
          }
        }
      }
    }
    public void serviceSearchCompleted(int transID, int respCode) {
      switch(respCode) {
        case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
          resp=1;
          break;
        default:
          resp=0;
          break;
      }
    }

    public void setResponse(int i) {resp=i;}

    public void inquiryCompleted(int discType) {}

  }

  private static int read (Object[] rSource) {
	  int pos = ((Integer)rSource[1]).intValue() ;
	  rSource[1] = new Integer(pos + 1);
	  return (int)(((byte[])rSource[0])[pos] & 0xff);
  }
  
  private static long readLong(Object[] rSource, int size) throws IOException {

      long result = 0;

      for(int i = 0; i < size; i++)
          result = result<<8|read(rSource);
			
      return result;
  }

  private static byte[] readBytes(Object[] rSource, int size) throws IOException  {

      byte[] result = new byte[size];

      for(int i = 0; i < size; i++)
          result[i] = (byte)read(rSource);

      return result;
  }


  private static String hexString(byte[] b) throws IOException {

      StringBuffer buf = new StringBuffer();

      for(int i = 0; i < b.length; i++) {
          buf.append(Integer.toHexString(b[i]>>4&0xf));
          buf.append(Integer.toHexString(b[i]&0xf));
      }
		
      return buf.toString();

  }

  private static DataElement readElement(byte[] data, int rPos) throws IOException {
	  Object[] rSource = new Object[] { data, new Integer(rPos) };
	  return readElement (rSource);
  }  
  
  private static DataElement readElement(Object[] rSource) throws IOException {

      int header = read(rSource);
      int type = header>>3&0x1f;
      int size = header&0x07;

      switch(type) {			

      case 0:			// NULL
          return new DataElement(DataElement.NULL);
      case 1:			// U_INT
          switch(size) {
          case 0:
              return new DataElement(DataElement.U_INT_1, readLong(rSource, 1));
          case 1:
              return new DataElement(DataElement.U_INT_2, readLong(rSource, 2));
          case 2:
              return new DataElement(DataElement.U_INT_4, readLong(rSource, 4));
          case 3:
              return new DataElement(DataElement.U_INT_8, readBytes(rSource, 8));
          case 4:
              return new DataElement(DataElement.U_INT_16, readBytes(rSource, 16));
          default:
              throw new IOException();
          }
      case 2:			// INT
          switch(size) {
          case 0:
              return new DataElement(DataElement.INT_1, (long)(byte)readLong(rSource, 1));
          case 1:
              return new DataElement(DataElement.INT_2, (long)(short)readLong(rSource, 2));
          case 2:
              return new DataElement(DataElement.INT_4, (long)(int)readLong(rSource, 4));
          case 3:
              return new DataElement(DataElement.INT_8, readLong(rSource, 8));
          case 4:
              return new DataElement(DataElement.INT_16, readBytes(rSource, 16));
          default:
              throw new IOException();
          }
      case 3:			// UUID
          {

              UUID uuid = null;
              switch(size) {
              case 1:
                  uuid = new UUID(readLong(rSource, 2));
                  break;
              case 2:
                  uuid = new UUID(readLong(rSource, 4));
                  break;
              case 4:
                  uuid = new UUID(hexString(readBytes(rSource, 16)), false);
                  break;
              default:
                  throw new IOException();
              }

              return new DataElement(DataElement.UUID, uuid);
          }

      case 4:			// STRING
          {	
              int length = -1;

              switch(size) {
              case 5:
                  length = (int)readLong(rSource, 1);
                  break;				
              case 6:
                  length = (int)readLong(rSource, 2);
                  break;				
              case 7:
                  length = (int)readLong(rSource, 4);
                  break;
              default:
                  throw new IOException();
              }


              return new DataElement(DataElement.STRING, new String(readBytes(rSource, length), "UTF-8"));
          }

      case 5:			// BOOL
          return new DataElement(readLong(rSource, 1) != 0);

      case 6:			// DATSEQ
          {	
              long length;
		
              switch(size) {
              case 5:
                  length = readLong(rSource, 1);
                  break;				
              case 6:
                  length = readLong(rSource, 2);
                  break;				
              case 7:
                  length = readLong(rSource, 4);
                  break;
              default:
                  throw new IOException();
              }

              DataElement element = new DataElement(DataElement.DATSEQ);

              for(int end = (((Integer)rSource[1]).intValue()+(int)length); ((Integer)rSource[1]).intValue() < end; ) {
                  element.addElement(readElement(rSource));
              }
              
              return element;

          }

      case 7:			// DATALT
          {	
              long length;

              switch(size) {
              case 5:
                  length = readLong(rSource, 1);
                  break;				
              case 6:
                  length = readLong(rSource, 2);
                  break;				
              case 7:
                  length = readLong(rSource, 4);
                  break;
              default:
                  throw new IOException();
              }

              DataElement element = new DataElement(DataElement.DATALT);

              for(int end = (((Integer)rSource[1]).intValue()+(int)length); ((Integer)rSource[1]).intValue() < end; ) {
                  element.addElement(readElement(rSource));
              }

              return element;
          }

      case 8:			// URL
          {	
              int length = -1;
		
              switch(size) {
              case 5:
                  length = (int)readLong(rSource, 1);
                  break;				
              case 6:
                  length = (int)readLong(rSource, 2);
                  break;				
              case 7:
                  length = (int)readLong(rSource, 4);
                  break;
              default:
                  throw new IOException();
              }

              return new DataElement(DataElement.URL, new String(readBytes(rSource, length)));

          }

      default:
          throw new IOException();

      }


  }

  public static ServiceRecord[] createServiceRecordCE(String adr, byte[][] uuids, int[] attrs, byte[] data) throws IOException {

	  ServiceRecord[] srx = new ServiceRecord[1];
	  srx[0] = new RemoteServiceRecord ("001122334455");
	  srx[0].setAttributeValue(256, new DataElement (DataElement.STRING, "Test"));
	  //if (true) return srx;
	  
	  try {
	  DataElement deAll = readElement(data, 0);
	  
	  Vector v = new Vector();
	  
	  Object o = deAll.getValue();
	  
	  if (o == null) throw new Exception ("No value in DataElement");
	  if (!(o instanceof Enumeration)) throw new Exception ("ClassCastException " + o.getClass());
	  
	  Enumeration en = (Enumeration)o;
	  
	  while (en.hasMoreElements()) {
		  Object o2 = en.nextElement();
		  
		  if (o2 == null) throw new Exception ("Inner Element is null");
		  if (!(o2 instanceof DataElement)) throw new Exception ("Inner Element no DataElement");
		  
		  DataElement de = (DataElement) o2;
		  Object o3 = createServiceRecord(adr, uuids, attrs, de);
		  if (o3 != null) v.addElement(o3);
	  }
	  
	  if (v.size() == 0) return null;
	  ServiceRecord sr[] = new ServiceRecord[v.size()];
	  v.copyInto(sr);
	  return sr;
	  } catch (Exception e) {
		  ServiceRecord[] sr = new ServiceRecord[1];
		  sr[0] = new RemoteServiceRecord ("001122334455");
		  sr[0].setAttributeValue(256, new DataElement (DataElement.STRING, "" + e.getClass() + " " + e.getMessage()));
		  return sr;
	  }
	  
  }

  public static ServiceRecord createServiceRecord(String adr, byte[][] uuids, int[] attrs, byte[] data) throws IOException {
	  ServiceRecord sr = createServiceRecord(adr, uuids, attrs, readElement(data, 0));
	  //((RemoteServiceRecord)sr).raw = data;

	  return sr;
  }
  
  public static ServiceRecord createServiceRecord(String adr, byte[][] uuids, int[] attrs, DataElement deAll) throws IOException {
	  
	  SDPServiceRecord srec = new RemoteServiceRecord(adr);

	  try {
	  
	  UUID[] uuid = new UUID[uuids.length];
	  for (int i = 0;i < uuid.length;i++) uuid[i] = new UUID(uuids[i]);
	  
	  boolean inReq = true;
	  for (int i = 0;i < uuid.length;i++) {
		  if (!checkForUUID (deAll, uuid[i])) 
			  inReq = false;
	  }
	  if (inReq == false) return null;
	  
	  Enumeration en = (Enumeration)deAll.getValue();
	  
	  while (en.hasMoreElements()) {
		  DataElement de = (DataElement) en.nextElement();
		  int attID = (int)de.getLong();
		  inReq = false;
		  for (int i = 0;i < attrs.length;i++) {
			  if (attID == attrs[i]) {
				  inReq = true;
				  break;
			  }
		  }
		  de = (DataElement) en.nextElement();
		  if (!inReq) continue;
		  srec.setAttributeValue(attID, de);
	  }
	  } catch (Exception e) { e.printStackTrace();
/*	  for (int i = 0;i < data.length;i++) {
		  System.out.print ("(byte)0x" + Integer.toHexString(data[i] & 0xff) + ", "); }
	  	System.out.println(); */
	  }
 	  return srec;
  }

	private static boolean checkForUUID(DataElement de, UUID uuid) {
		if (de.getDataType() == DataElement.UUID) return uuid.equals((UUID)de.getValue());
		else if (de.getDataType() == DataElement.DATALT || de.getDataType() == DataElement.DATSEQ) {  
			Enumeration en = (Enumeration)de.getValue();
			  while (en.hasMoreElements()) {
				  DataElement des = (DataElement) en.nextElement();
				  if (checkForUUID (des, uuid)) return true;
			  }
		}
		return false;
	}

 	//static byte[] ba = new byte[] { (byte)0x35, (byte)0x7d, (byte)0x09, (byte)0x00, (byte)0x00, (byte)0x0a, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x03, (byte)0x09, (byte)0x00, (byte)0x01, (byte)0x35, (byte)0x03, (byte)0x19, (byte)0x11, (byte)0x06, (byte)0x09, (byte)0x00, (byte)0x04, (byte)0x35, (byte)0x13, (byte)0x35, (byte)0x05, (byte)0x1a, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x35, (byte)0x05, (byte)0x19, (byte)0x00, (byte)0x03, (byte)0x08, (byte)0x0f, (byte)0x35, (byte)0x03, (byte)0x19, (byte)0x00, (byte)0x08, (byte)0x09, (byte)0x00, (byte)0x05, (byte)0x35, (byte)0x03, (byte)0x19, (byte)0x10, (byte)0x02, (byte)0x09, (byte)0x00, (byte)0x06, (byte)0x35, (byte)0x09, (byte)0x09, (byte)0x65, (byte)0x6e, (byte)0x09, (byte)0x00, (byte)0x6a, (byte)0x09, (byte)0x01, (byte)0x00, (byte)0x09, (byte)0x00, (byte)0x09, (byte)0x35, (byte)0x08, (byte)0x35, (byte)0x06, (byte)0x19, (byte)0x11, (byte)0x06, (byte)0x09, (byte)0x01, (byte)0x00, (byte)0x09, (byte)0x01, (byte)0x00, (byte)0x25, (byte)0x12, (byte)0x4f, (byte)0x42, (byte)0x45, (byte)0x58, (byte)0x20, (byte)0x46, (byte)0x69, (byte)0x6c, (byte)0x65, (byte)0x20, (byte)0x54, (byte)0x72, (byte)0x61, (byte)0x6e, (byte)0x73, (byte)0x66, (byte)0x65, (byte)0x72, (byte)0x09, (byte)0x03, (byte)0x03, (byte)0x35, (byte)0x02, (byte)0x08, (byte)0xff, (byte)0x09, (byte)0x07, (byte)0x77, (byte)0x1c, (byte)0x6f, (byte)0x6d, (byte)0x98, (byte)0xf2, (byte)0x3c, (byte)0x3a, (byte)0x11, (byte)0xd6, (byte)0x95, (byte)0x6a, (byte)0x00, (byte)0x03, (byte)0x93, (byte)0x53, (byte)0xe8, (byte)0x58 };
	/*static String ba = "35 c2 35 5b 9 0 0 a 0 1 0 4 9 0 1 35 3 19 11 1 9 0 4 35 c 35 3 19 1 0 35 5 19 0 3 8 3 9 0 5 35 3 19 10 2 9 0 6 35 9 9 65 6e 9 0 6a 9 1 0 9 0 9 35 8 35 6 19 11 1 9 1 0 9 1 0 25 12 42 6c 75 65 74 6f 6f 74 68 2d 50 44 41 2d 53 79 6e 63 35 63 9 0 0 a 0 1 0 1 9 0 1 35 6 19 11 12 19 12 3 9 0 4 35 c 35 3 19 1 0 35 5 19 0 3 8 1 9 0 5 35 3 19 10 2 9 0 6 35 9 9 65 6e 9 0 6a 9 1 0 9 0 9 35 8 35 6 19 11 3 9 11 8 9 1 0 25 17 42 6c 75 65 74 6f 6f 74 68 20 41 75 64 69 6f 20 47 61 74 65 77 61 79";
	
	public static void main (String args[]) throws Exception {
		
		StringTokenizer st = new StringTokenizer (ba);
		byte[] b = new byte[st.countTokens()];
		
		for (int i = 0;i < b.length;i++) {
			b[i] = (byte)(Integer.parseInt(st.nextToken(), 16) & 0xff);
		}
		
		ServiceRecord[] de = createServiceRecordCE("000d9305170e", new byte[0][0], new int[] {0, 1, 2, 3, 4, 5, 6, 7, 256 }, b);
		for (int i = 0;i < de.length;i++)
			System.out.println ("Service Record " + de[i]);
	}*/

}