package de.avetana.bluetooth.connection;

import java.util.Enumeration;
import java.util.Hashtable;

import de.avetana.bluetooth.util.BTAddress;

/**
 * The class used to manage connection URLs.
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
 * This class is used to store the different connection URLs. It parses the URL represented as string, verifies their
 * correctness, identifies the protocols and stores all connection attributes in easy-to.use objects.<br>
 * This utility class is therefore used among most of the classes of the Avetana JSR82 implementation
 *
 * @author Julien Campana
 */

public class JSR82URL {

  /**
   * The variable that identifies the L2CAP protocol.
   * (Note: the UUID of the protocol was not used their because the numbers 0x100, 0x3, 0x8 are
   * not a clear sequence that can be used for indexing arrays).
   */
  public static final short PROTOCOL_L2CAP=0x0;

  /**
   * The variable that identifies the RFCOMM protocol.
   * (Note: the UUID of the protocol was not used their because the numbers 0x100, 0x3, 0x8 are
   * not a clear sequence that can be used for indexing arrays).
   */
  public static final short PROTOCOL_RFCOMM=0x1;

  /**
   * The variable that identifies the OBEX protocol.
   * (Note: the UUID of the protocol was not used their because the numbers 0x100, 0x3, 0x8 are
   * not a clear sequence that can be used for indexing arrays).
   */
  public static final short PROTOCOL_OBEX=0x2;

  /**
   * The hashtable storing the connection Options.<br>
   * Example of the content of this hashtable<br>
   * key           |    value        | <br>
   * --------------------------------- <br>
   * authenticate  |  Boolean.FALSE  | <br>
   * encrypt       |  Boolean.FALSE  | <br>
   * name          |  "JSRTest"      | <br>
   * --------------------------------- <br>
   */
  private Hashtable m_parameters=new Hashtable();

  /**
   *   PSM or channel number, depending on the protocol used.
   */
  private int m_attrNumber;

  /**
   * The BT address of the remote device.<br>
   * If m_address is null, then the connection URL represents a server URL.
   */
  private BTAddress m_address;

  /**
   * Short integer storing the protocol.
   * @see de.avetana.bluetooth.connection.PROTOCOL_L2CAP
   * @see de.avetana.bluetooth.connection.PROTOCOL_RFCOMM
   * @see de.avetana.bluetooth.connection.PROTOCOL_OBEX
   */
  private short m_protocol;

  /**
   * In the case of a server connection URL, the local UUID is stored within this variable
   */
  private String m_localUUID=null;

  /**
   * Creates a new instance of JSR82URL, initializes the different class variables and parses the string given in argument
   * @param url A string representation of a connection URL.
   * @throws BadURLFormat If the connection URL is not valid.
   * @throws Exception If some other exception occurs.
   */
  public JSR82URL(String url) throws BadURLFormat, Exception{

    String toWork=url;
    if(url==null || url.trim().equals("")) throw new BadURLFormat("The URL could not retrieved: Protocol not supported!");
    if(url.startsWith("btspp://")) {
      m_protocol=PROTOCOL_RFCOMM;
      toWork=toWork.substring("btspp://".length());
    } else if(url.startsWith("btl2cap://")) {
      m_protocol=PROTOCOL_L2CAP;
      toWork=toWork.substring("btl2cap://".length());
    } else if(url.startsWith("btgoep://")) {
      m_protocol=PROTOCOL_OBEX;
      toWork=toWork.substring("btgoep://".length());
    }
    else throw new BadURLFormat("Bar URL Format: URL must begin with the protocol name (btspp or btl2cap or btgoep)!");

    if(toWork.startsWith("localhost:")) {
      m_address=null;
      toWork=toWork.substring("localhost:".length());
    } else {
      try {
        m_address=BTAddress.parseString(toWork.substring(0,12));
      }catch(Exception ex) {throw new BadURLFormat("The address of the remote device is not a valid Bluetooth address!");}
      toWork=toWork.substring(12);
    }
    int channel=toWork.indexOf(':');
    int ser=toWork.indexOf(';');

    if(channel==-1) {
      if(ser==-1) {
        if(m_address==null) m_localUUID=toWork.trim();
      } else {
         if(m_address==null) m_localUUID=toWork.substring(0,ser).trim();
        try {
          parseURL(toWork.substring(ser + 1));
        }catch(Exception ex) {}
      }
      this.m_attrNumber = 0;
      if(m_address==null && (m_localUUID.length()!=32 && m_localUUID.length()!=8))
        throw new BadURLFormat("The Service Class ID must be a 32 or 128-bits UUID! " +m_localUUID + " " + m_localUUID.length());
    }
    else {
       if(m_address==null) m_localUUID=toWork.substring(0,channel);
       if(ser==-1) {
         try {
           m_attrNumber=Integer.parseInt(toWork.substring(channel+1).trim(), m_protocol == PROTOCOL_L2CAP ? 16 : 10);
         }
         catch(Exception ex) {
           throw new BadURLFormat("Bad channel Number!");
         }
       }
       else {
         try {
	        m_attrNumber=Integer.parseInt(toWork.substring(channel+1, ser).trim(), m_protocol == PROTOCOL_L2CAP ? 16 : 10);
         }catch(Exception ex) {
           throw new BadURLFormat("Bad channel Number! " + toWork);
         }
         parseURL(toWork.substring(ser+1));
       }
     }

  }

  // Help method used to analyse the URL parameters.
  private void parseURL(String analyse) throws Exception {
    if(analyse==null || analyse.trim().equals("")) return;
	
    while (analyse.length() > 0) {
    	 int pos = analyse.indexOf (";");
	 String arg = "";
	 if (pos != -1) {
		 arg = analyse.substring(0, pos);
		 analyse = analyse.substring (pos + 1);
	 } else {
		 arg = analyse;
		 analyse = "";
	 }
		 
      try {
        int index=arg.indexOf('=');
        String param=arg.substring(0,index).toLowerCase();
        String value=arg.substring(index+1);
        if(param.equals("name") || param.toLowerCase().equals("receivemtu") ||
           param.toLowerCase().equals("transmitmtu"))
          m_parameters.put(param.toLowerCase(), value);
        else if(param.equals("master")) {
          boolean b;
          if(value.toLowerCase().equals("false")) b=false;
          else b=true;
          m_parameters.put("master", new Boolean(b));
        } else
          m_parameters.put(param, new Boolean(value.toLowerCase().equals("true")));
      }catch(Exception e) {}
    }
    if(m_parameters.get("master")==null) m_parameters.put("master", new Boolean (true));
    if(!isAuthenticated() && isEncrypted())
      throw new BadURLFormat("Combination authenticate=false and encrypt=true not allowed. Encryption requests authentication!");
    if(!isAuthenticated() & isAuthorized())
      throw new BadURLFormat("Combination authenticate=false and authorize=true not allowed!");
  }

  private boolean getBoolParam(String name) {
    try { 
    		if (m_parameters == null || m_parameters.get (name) == null) return false;
    		return ((Boolean)m_parameters.get(name)).booleanValue(); 
    	}catch(Exception ex) {return false;}
  }

  /**
   * Returns the value of the option <i>Authenticate</i>.
   * @return <code>true</code> - If the connection option <i>authenticate</i> is set to true.<br>
   *         <code>false</code> - Otherwise.
   */
  public boolean isAuthenticated() {return getBoolParam("authenticate");}

  /**
   * Returns the value of the option <i>encrypt</i>.
   * @return <code>true</code> - If the connection option <i>authenticate</i> is set to true.<br>
   *         <code>false</code> - Otherwise.
   */
  public boolean isEncrypted() {return getBoolParam("encrypt");}

  /**
   * Returns the value of the option <i>authorize</i>.
   * @return <code>true</code> - If the connection option <i>authorize</i> is set to true.<br>
   *         <code>false</code> - Otherwise.
   */
  public boolean isAuthorized() {return getBoolParam("authorize");}

  /**
   * Returns the value of the option <i>master</i>.
   * @return <code>true</code> - If the connection option <i>master</i> is set to true.<br>
   *         <code>false</code> - Otherwise
   */
  public boolean isLocalMaster() {return getBoolParam("master");}

  /**
   * Sets the PSM or the channel number (depending on the protocol used). This "number" is meant to be
   * as universal as possible and does therefore not identify any protocol-specific variable.
   * @param num The new PSM or channel number.
   */
  public void setAttrNumber (int num) {
    this.m_attrNumber = num;
  }

  /**
   * Returns the string representation of the connection url.
   * @return The string representation of the connection url.
   */
  public String toString() {
    String retour="";
    switch(m_protocol) {
      case PROTOCOL_L2CAP:
        retour="btl2cap://";break;
      case PROTOCOL_RFCOMM:
        retour="btspp://";break;
      case PROTOCOL_OBEX:
        retour="btgoep://";break;
    }
    if(m_address==null) retour+="localhost:"+m_localUUID;
    else retour+=m_address.toStringSep(false);
    if(m_address != null) retour+=":"+ (m_protocol == PROTOCOL_L2CAP ? Integer.toHexString(m_attrNumber) : "" + m_attrNumber);
    String[] paramKey=getParameterKeys();
    for(int i=0;i<paramKey.length;i++) {
      try {
        Object obj=m_parameters.get(paramKey[i]);
        if(obj!=null) retour+=";"+paramKey[i]+"="+obj.toString();
      }catch(Exception ex) {ex.printStackTrace();}
    }
    return retour;
  }

  /**
   * Returns the array of connection options.
   * @return The array representation of all connection options.
   */
  public String[] getParameterKeys() {
    Enumeration obj=m_parameters.keys();
    String[] retour=new String[m_parameters.size()];
    for (int i = 0;i < retour.length;i++) retour[i] = (String)obj.nextElement();
    return retour;
  }

  /**
   * Returns the hashtable containing all connection options. The usual options are
   * <center>
   * <table border=1 cellspacing=5>
   *   <TR><TH>Option Name</TH><TH>Object type</TH><TH>Protocol</TH><TH>Server/Client connections?</TH><TR>
   *   <TR>
   *     <TD><i>name</i></TD>
   *     <TD>String</TD>
   *     <TD>All</TD>
   *     <TD>Server</TD>
   *   </TR>
   *   <TR>
   *     <TD><i>receiveMTU</i></TD>
   *     <TD>String</TD>
   *     <TD>L2CAP</TD>
   *     <TD>Both</TD>
   *   </TR>
   *   <TR>
   *     <TD><i>transmitMTU</i>
   *     </TD><TD>String</TD>
   *     <TD>L2CAP</TD>
   *     <TD>Both</TD>
   *   </TR>
   *   <TR>
   *     <TD><i>authenticate</i></TD>
   *     <TD>Boolean</TD>
   *     <TD>All</TD>
   *     <TD>Both</TD>
   *   </TR>
   *   <TR>
   *     <TD><i>master</i></TD>
   *     <TD>Boolean</TD>
   *     <TD>All</TD>
   *     <TD>Both</TD>
   *   </TR>
   *   <TR><TD><i>encrypt</i></TD><TD>Boolean</TD><TD>All</TD><TD>Both</TD></TR>
   *   <TR><TD><i>authorize</i></TD><TD>Boolean</TD><TD>All</TD><TD>Both</TD></TR>
   *   <TR><TD><i>Other options</i></TD><TD>Boolean</TD><TD>?</TD><TD>?</TD></TR>
   * </table>
   * </center>
   * <u>Note</u>: the options receiveMTU and transmitMTU, which are only available with the L2CAP protocol, have
   * a default value set to <code>null</code>. Please keep in mind that the default values given in the above table
   * are the values FOR the methods of this class. The L2CAP connection classes are using a default value for receiveMTU
   * set to 672, for example.
   * @return The hashtable containin all connection options.
   */
  public Hashtable getParameters() {
    return m_parameters;
  }

  /**
   * Returns the value of the option named <i>paramName</i>.
   * @param paramName The option's name.
   * @return the value of this option or null if the option is not set.
   */
  public Object getParameter(String paramName) {
      Object obj=m_parameters.get(paramName);
      if(obj==null) return m_parameters.get(paramName.toLowerCase());
      return obj;
  }

  /**
   * Sets the value of a connection option.
   * @param name The name of the option
   * @param value The value of this option.
   */
  public void setParameter(String name, Object value) {
    m_parameters.remove(name);
    m_parameters.put(name, value);
  }

  /**
   * Sets the protocol.
   * @param protocol The protocol code of the new protocol.
   */
  public void setProtocol(short protocol) {
    m_protocol=protocol;
  }

  /**
   * Returns the BT device address or null in the case of a server connection URL.
   * @return <ul><li>The BT device address if it exists</li><li>null - Otherwise</li></ul>
   */
  public BTAddress getBTAddress() {return m_address;}

  /**
   * Returns the string representation of the local service UUID or null in the case of a client connection URL.
   * @return <ul><li>The string representation of the local service UUID if it exists</li><li>null - Otherwise</li></ul>
   */
  public String getLocalServiceUUID() {return m_localUUID;}

  /**
   * Returns the value of the PSM or of the channel number.
   * @return The value of the PSM or of the channel number.
   */
  public int getAttrNumber() {return m_attrNumber;}

  /**
   * Returns the integer code of protocol used by this connection URL.
   * @return The integer code of protocol used by this connection URL
   */
  public short getProtocol() {return m_protocol;}
}
