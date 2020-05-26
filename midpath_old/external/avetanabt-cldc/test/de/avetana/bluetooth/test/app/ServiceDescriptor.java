package de.avetana.bluetooth.test.app;


import java.io.Serializable;

import javax.bluetooth.DataElement;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;


/**
 * An helper class for the ServiceFinder class.
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
 * This is an helper class for the ServiceFinder class. This class has nothing to do with the JSR82 specification
 * and just exists in order to provide a serializable Service description. The implementation as well as the
 * the possibilities of this class are very basic.
 *
 *
 * @author Julien Campana
 */

public class ServiceDescriptor implements java.io.Serializable{

  private class BTAddress2 extends BTAddress implements Serializable {

	public BTAddress2(BTAddress addr) {
		super (addr);
	}
	  
  }

	
  private BTAddress2 m_addr;
  private String m_remoteName;
  private String m_serviceName;
  private String m_serviceURL;;

  /**
   * Creates a new ServiceDescriptor object and sets the BTAddress of the remote device
   * @param addr The BTAddress of the remote device
   */
  public ServiceDescriptor(BTAddress addr) {m_addr= new BTAddress2 (addr);}

  /**
   * Creates a new ServiceDescriptor object and sets the remote device
   * @param dev The remote device
   * @throws Exception If the BT address of this device could not be parsed.
   */
  public ServiceDescriptor(RemoteDevice dev) throws Exception{
    m_addr=new BTAddress2 (BTAddress.parseString(dev.getBluetoothAddress()));
  }

  /**
   * Sets the name of the remote device
   * @param name The name of the remote device
   */
  public void setRemoteName(String name) { m_remoteName=name; }

  /**
   * Sets the name if the record
   * @param name The name of the record
   */
  public void setServiceName(String name) { m_serviceName=name; }

  /**
   * Sets the URL requested to connect to this record
   * @param URL The connection URL
   */
  public void setServiceURL(String URL) { m_serviceURL=URL; }

  /**
   * Sets the BT address of the remote device
   * @param newAdr The BT address of the remote device
   */
  public void setBTAddress(BTAddress newAdr) { m_addr=new BTAddress2(newAdr); }

  /**
   * Returns the name of the remote device this service belongs to
   * @return The name of the remote device this service belongs to
   */
  public String getRemoteName() { return m_remoteName; }

  /**
   * Returns the BT address of the remote device this service belongs to.
   * @return The BT address of the remote device this service belongs to.
   */
  public BTAddress getBTAddress() { return m_addr; }

  /**
   * Returns the connection URL for this service
   * @return The connection URL for this service
   */
  public String getServiceURL() { return m_serviceURL; }

  /**
   * Returns the name of the service
   * @return The name of the service
   */
  public String getServiceName() { return m_serviceName; }

  /**
   * Parses the Service record and fills the variables of this class.
   * @param rec The service record
   * @param security The security flags
   * @param master Is the local device master?
   */
  public void parseServiceRecord(ServiceRecord rec, int security, boolean master) {
    try {
      m_serviceURL=rec.getConnectionURL(security, master);
    }catch(Exception ex) {ex.printStackTrace();}
    DataElement dat=(DataElement)rec.getAttributeValue(256); //Service name is attribute 0x100
    if(dat!=null && dat.getDataType()==DataElement.STRING)
      m_serviceName=(String)dat.getValue();
  }

  /**
   * Returns the String representation used in the JTree of the ServiceFinder class.
   * @return The String representation used in the JTree of the ServiceFinder class.
   */
  public String toString() {
    String retour="";
    retour += (m_serviceName!=null?m_serviceName:(m_addr!=null?m_addr.toString():""));
//    retour += (m_serviceURL!=null?m_serviceURL+")":"No valid URL found)");
    return retour;
  }
}