package de.avetana.bluetooth.test.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.Authenticator;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.connection.ConnectionNotifier;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.hci.LinkQuality;
import de.avetana.bluetooth.hci.Rssi;
import de.avetana.bluetooth.l2cap.L2CAPConnectionNotifierImpl;
import de.avetana.bluetooth.sdp.SDPConstants;
import de.avetana.bluetooth.util.BTAddress;

/**
 * <b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
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
 * An utility class used to test the functionalities of the Avetana JSR 82 Implementation.
 * This class could also be taken as an example of how JSR82 works. I suggest furthermore to have a look at the
 * de.avetana.bluetooth.util.ServiceFinderPane, which is pure JSR 82 code (except the UI part, of course).</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Julien Campana
 * @version 1.2
 */

public class JSRTest extends JFrame implements ActionListener {

  // inquiry and view select local devices
  private JButton m_inquire;

  // list of available local devices
  private JComboBox m_localDevices;
  // list of supported protocols
  private JComboBox m_protocols;
  // Server application
  private JToggleButton m_offerService = new JToggleButton ("Offer service");
  // Options for existing connections
  private JButton m_encryptLink, m_authenticateLink, m_switchMaster;
  // Remote Device infos
  private JButton m_remote=new JButton("Get remote device infos");
  // Local device properties
  private JButton m_localAddress, m_localName, m_localDevClass, m_record, m_property, m_setlocalDevClass, m_discoverable;
  // Service Finder as a Panel.
  private ServiceFinderPane m_servicePanel;
  // Functionalities panel
  private JPanel m_rightPanel;
  // Split the window into two resizable panels
  private JSplitPane m_split;
  // Client connection
  private JRadioButton m_client;
  // Server connection
  private JRadioButton m_server;
  // Groupping client and sever connections
  private ButtonGroup clientServer;
  // Current connection status
  private JLabel serviceStatus = new JLabel ("Idle");
  
  //Thread that sends data while sendBut is pressed (Test)
  //private Thread sendThread = null;

  // Connection options.
  private JCheckBox m_authentication=new JCheckBox("Enable authentication");
  private JCheckBox m_encrypt=new JCheckBox("Enable encryption");
  private JCheckBox m_master=new JCheckBox("Connect as master");

  // Connection URL
  private JTextField connectionURL = new JTextField (30);
  // Connection button
  private JToggleButton connectTo = new JToggleButton ("Connect");
  // Data exchange info and commands
  //private Thread sendThread;
  //private JToggleButton sendData = new JToggleButton ("Send data");
  private JButton sendData = new JButton ("Send data");
  private JLabel dataReceived = new JLabel ("Received 0");

  // Connection streams. These streams are only used with BluetoothStream connections (RFCOMN)
  private InputStream is = null;
  private OutputStream os = null;

  // The connection instance. Can be an L2CAPConnectionImpl or an RFCOMMConnectionImpl, depending on
  // the protocol choosen. (VERSION 1.2)
  private Connection streamCon = null;
  // Connection notifier for SDP server profiles
  private Connection notify=null;
  
  // Own thread for receiving and sending data for the protocols, which use connection streams.
  private DStreamThread receiverThread = null;
  
  // L2CAP Data needs to be polled from the stream in order to clear the InputBuffer
  private JButton l2CAPBut = new JButton ("poll");
  // Warn the user if the os specific stack does not support some security options.
  private JLabel securityNotAvailable;

  //javax.bluetooth.* classical classes
  //Saving this two instances avoid to continuily call the static methods (getLocalDevice(), getDiscoveryAgent()) of
  //these two classes
  private DiscoveryAgent m_agent;
  private LocalDevice m_local;
  
  private boolean obexDisconnected = false;

  // Current selected protocol
  private int currentIndex=0;

  // OS version
  private int m_os=0;
  // Constant variables used to identify the OS.
  private final int LINUX=0x0;
  private final int WINDOWS=0x1;
  private final int MACOSX=0x2;

  private static final int rfcommPackLen = 100;
  
  /*
   * Matrix of possibilites:
   * propertes[0][] -> L2CAP
   * properties[1][] -> RFCOMM
   * properties[2][] -> OBEX
   *
   * properties[x][0] -> authenticate
   * properties[x][1] -> encrypt
   * properties[x][2] -> master
   *
   * The possible values are in {0,1}.
   *
   */
  private int properties[][];

  /*
   * Which UUID is used during the service search?
   * uuidList[protocol_index] gives the answer
   */
  private final UUID[] uuidList=new UUID[] {new UUID(0x100), new UUID(0x3), new UUID(0x8)};

  /**
   * The result of the last service search is stored in the local preferences of java.
   * Each protocol has its own stored list.
   */
  private final String[] localPref=new String[]{"lastBTSearchL2CAP",
                                                "lastBTSearchRFComm",
                                                "lastBTSearchOBEX"};



  public JSRTest() throws Exception {
     super ("Avetana Bluetooth Utility");
     try {
       // Initialize the java stack.
       initStack();
       // Initialize the security options, which depends on the operating system
       initOptions();
       // draw the UI
       jbInit();
       pack();
       setLocationRelativeTo(null);
       setVisible (true);
//       BlueZ.removeServiceByID(0x00010004l);
     }catch(Exception ex) {
       showError((ex.getMessage()!=null && !ex.getMessage().trim().equals(""))?
                 ex.getMessage():
                 "An error occured while loading the application!");
       ex.printStackTrace();
       System.exit(0);
     }
   }

   /**
    * Initializes the security options, which depends on the operating system.
    * This method aims to facilitate the integration of future security possibilities.
    * (if a new security option is supported by the stack, just set the property to 1).
    */
   public void initOptions() {
     String sys=System.getProperty("os.name");
     if (sys.equalsIgnoreCase("mac os x")) m_os=MACOSX;
     else if(sys.equalsIgnoreCase("linux")) m_os=LINUX;
     else m_os=WINDOWS;

     properties=new int[3][3];
     switch(m_os) {
       case LINUX:
         for(int i=0;i<3;i++) properties[0][i]=1;
         for(int i=0;i<3;i++) properties[1][i]=1;
         for(int i=0;i<3;i++) properties[2][i]=0;
         break;
       case WINDOWS:
         for(int u=0;u<3;u++)
           for(int i=0;i<3;i++)
             properties[u][i]=1;
         break;
       case MACOSX:
         for(int i=0;i<3;i++) properties[i][0]=1;
         for(int i=0;i<3;i++) properties[i][1]=1;
         for(int i=0;i<3;i++) properties[i][2]=0;
         break;
     }
   }

   /**
    * Inits and draws the UI.
    * @throws Exception
    */
   private void jbInit() throws Exception {
     JPanel top=new JPanel();
     top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));


     JPanel proto=new JPanel(new BorderLayout());
     m_protocols=new JComboBox(new Object[]{"L2CAP","RFCOMM", "OBEX"});
     proto.add(new JLabel("Choose the protocol the service supports : "), BorderLayout.WEST);
     proto.add(m_protocols, BorderLayout.CENTER);
     Container con = this.getContentPane();
     con.setLayout(new BorderLayout());
     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

     JPanel inquiry=new JPanel();
     inquiry.setLayout(new BoxLayout(inquiry, BoxLayout.X_AXIS));
     m_inquire=new JButton("View all remote devices");
     inquiry.add(m_inquire);
     inquiry.add(Box.createVerticalStrut(10));
     inquiry.add(new JLabel("Local devices : "));
     this.m_localDevices=new JComboBox(new String[]{"Default (first available)"});
     inquiry.add(m_localDevices);

     inquiry.setBorder(BorderFactory.createTitledBorder("Inquiry/Local Device"));

     top.add(inquiry);
     con.add (top, BorderLayout.NORTH);

     JPanel sdp=new JPanel();
     sdp.setLayout(new BorderLayout());
     sdp.add(proto, BorderLayout.NORTH);


     m_servicePanel=new ServiceFinderPane(this,"lastBTSearchRFComm", new UUID[]{new UUID("3", true)});
     m_rightPanel=new JPanel();
     m_rightPanel.setLayout(new BoxLayout(m_rightPanel, BoxLayout.Y_AXIS));
     m_client=new JRadioButton("Client Connection", true);
     m_server=new JRadioButton("Server Connection", true);

     clientServer=new ButtonGroup();
     clientServer.add(m_client);
     clientServer.add(m_server);

     JPanel statusPanel=new JPanel(new FlowLayout());
     statusPanel.add(new JLabel("Connection Status :"));
     statusPanel.add(serviceStatus);
     serviceStatus.setForeground(new Color(156, 175, 84));


     JPanel clSer=new JPanel();
     clSer.setLayout(new GridBagLayout());
     GridBagConstraints c=new GridBagConstraints();
     clSer.setBorder(BorderFactory.createTitledBorder("Connection Framework"));
     JPanel clientURL=new JPanel(new FlowLayout());
     clientURL.add(new JLabel("URL:"));
     clientURL.add(connectionURL);
     int i=-1;
     c.gridx=++i;c.gridy=0;c.anchor=GridBagConstraints.WEST;c.weightx=0;c.weighty=0;c.gridwidth=3;
     clSer.add(statusPanel, c);
     c.gridy=++i; c.gridwidth = 1;
     clSer.add(m_client,c);
     c.gridx++;
     clSer.add (connectTo, c);
     c.gridy=++i; c.gridx = 0; c.insets=new Insets(1,15,0,0); c.gridwidth = 3;
     clSer.add(clientURL,c);
     c.gridy=++i;c.insets=new Insets(1,0,1,1); c.gridwidth = 1;
     clSer.add(m_server,c);
     c.gridx=1;c.insets=new Insets(1,5,0,0);
     clSer.add(m_offerService, c);
     c.gridx =0;c.gridy=++i;c.insets=new Insets(1,1,1,1);
     clSer.add(m_authentication,c);
     c.gridy=++i;
     clSer.add(m_encrypt,c);
     c.gridx =1;c.anchor=GridBagConstraints.EAST;
     securityNotAvailable=new JLabel();
     securityNotAvailable.setForeground(Color.red);
     clSer.add(securityNotAvailable,c);

     c.gridx =0;c.gridy=++i;c.insets=new Insets(1,1,1,1);c.anchor=GridBagConstraints.WEST;
     clSer.add(m_master,c);

     JPanel dataExchange=new JPanel(new GridBagLayout());
     dataExchange.setBorder(BorderFactory.createTitledBorder("Data Exchange"));
     c=new GridBagConstraints();
     c.gridx=0;c.anchor=GridBagConstraints.WEST;c.gridy=0;c.weightx=1;c.weighty=1;
     dataExchange.add(this.sendData,c);
     c.gridx=1;c.anchor=GridBagConstraints.EAST;
     dataExchange.add(this.dataReceived,c);
     c.gridx=2;c.anchor=GridBagConstraints.EAST;
     dataExchange.add(this.l2CAPBut,c);

     JPanel activeConnection=new JPanel(new GridLayout(0,2));
     activeConnection.setBorder(BorderFactory.createTitledBorder("Change the state of an active connection"));
     this.m_encryptLink = new JButton("Encrypt connection");
     this.m_authenticateLink = new JButton("Authenticate remote device");
     this.m_switchMaster =new JButton("Switch Master/Slave");
     activeConnection.add(this.m_encryptLink,c);
     activeConnection.add(this.m_authenticateLink,c);
     activeConnection.add(m_switchMaster,c);
     activeConnection.add(m_remote,c);

     JPanel localDevice=new JPanel(new GridLayout(0,2));
     localDevice.setBorder(BorderFactory.createTitledBorder("My Local Device"));
     m_localAddress=new JButton("Get device address");
     m_localName=new JButton("Get device name");
     m_localDevClass=new JButton("Get device class");
     m_record=new JButton("Get server Service record");
     m_setlocalDevClass=new JButton("Set device class");
     m_discoverable=new JButton("Get discoverable mode");

     currentIndex=JSR82URL.PROTOCOL_RFCOMM;
     m_protocols.setSelectedIndex(currentIndex);
     getProperties();

     localDevice.add(m_localAddress,c);
     localDevice.add(m_localName,c);
     localDevice.add(m_localDevClass,c);
     localDevice.add(m_discoverable,c);
     localDevice.add(m_record,c);
     localDevice.add(m_setlocalDevClass,c);


     m_rightPanel.add(clSer);
     m_rightPanel.add(Box.createVerticalStrut(5));
     m_rightPanel.add(dataExchange);
     m_rightPanel.add(Box.createVerticalStrut(5));
     m_rightPanel.add(activeConnection);
     m_rightPanel.add(Box.createVerticalStrut(5));
     m_rightPanel.add(localDevice);

     m_split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_servicePanel,
                            m_rightPanel);
     sdp.add(m_split, BorderLayout.CENTER);

     sdp.setBorder(BorderFactory.createTitledBorder("Service Search / Connection Factory"));

     con.add(sdp, BorderLayout.CENTER);
     enableConnAttributes(false);
     addListeners();
     chooseClientConnection();
     m_servicePanel.addMouseListenerToTree(new MouseAdapter() {
       public void mouseClicked(MouseEvent e) {
         if(e.getClickCount() == 2) selectService();
       }
     });
     
     m_authentication.addActionListener(new ActionListener () {

		public void actionPerformed(ActionEvent e) {
			if (!m_authentication.isSelected()) m_encrypt.setSelected(false);
			
		}
    	 
     });
    
     m_encrypt.addActionListener(new ActionListener () {

 		public void actionPerformed(ActionEvent e) {
 			if (m_encrypt.isSelected()) m_authentication.setSelected(true);
 			
 		}
     	 
      });

   }

   /**
    * Inits the stack.
    *
    * @throws Exception
    */
   public void initStack() throws Exception{
	   de.avetana.bluetooth.stack.BluetoothStack.init(new de.avetana.bluetooth.stack.AvetanaBTStack());
     m_local=LocalDevice.getLocalDevice();
     m_agent=m_local.getDiscoveryAgent();
   }

   /**
    * Enables or disables all Widgets related with a connection.
    * @param enable <code>true</code> - Enable the widgets.<br>
    *               <code>false</code> - Disable them.
    */
   public void enableConnAttributes(boolean enable) {
     this.sendData.setEnabled(enable);
     this.dataReceived.setEnabled(enable);
     m_encryptLink.setEnabled(enable);
     m_authenticateLink.setEnabled(enable);
     m_switchMaster.setEnabled(enable);
     m_remote.setEnabled(enable);
     l2CAPBut.setEnabled(enable && (streamCon instanceof L2CAPConnection || streamCon instanceof ClientSession));
     if(streamCon instanceof L2CAPConnection) {
       m_authentication.setEnabled(!enable);
       m_encrypt.setEnabled(!enable);
       m_master.setEnabled(!enable);
     }
   }

   //Adds listeners to the widgets.
   //This method is only defined to better centralize some actions.
   private void addListeners() {
     //General conf
     m_inquire.addActionListener(this);

     // Protocol list
     m_protocols.addActionListener(this);

     // Service Finder Panel
     this.m_servicePanel.m_select.addActionListener(this);

     //L2CAP-Polling
     this.l2CAPBut.addActionListener(this);

     // Connection Pane
     connectTo.addActionListener(this);
     sendData.addActionListener(this);
     m_offerService.addActionListener(this);
     m_client.addActionListener(this);
     m_server.addActionListener(this);

     // Setting connection options for an EXISTING connection
     m_encryptLink.addActionListener(this);
     m_authenticateLink.addActionListener(this);
     m_switchMaster.addActionListener(this);

     // Properties of remote device
     m_remote.addActionListener(this);

     // Properties of local device
     m_localAddress.addActionListener(this);
     m_localName.addActionListener(this);
     m_localDevClass.addActionListener(this);
     m_record.addActionListener(this);
     m_setlocalDevClass.addActionListener(this);
     m_discoverable.addActionListener(this);
   }

   /**
    * Methods called when the user click on the radio button "Client connection"
    */
   private void chooseClientConnection() {
     if(m_offerService.isSelected() == true) {
       int i=JOptionPane.showConfirmDialog(null, "You are currently offering a service (server connection).\n"+
           "This test programm does not allow client AND server connections to co-exsits\n"+
           "If you choose 'yes', the server connection will therefore be closed and the service removed",
           "Warning",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
       if (i == JOptionPane.OK_OPTION ) {
         revokeService();
       } else return;
     }
     m_offerService.setEnabled(false);
     connectionURL.setEnabled(true);
     connectTo.setEnabled(true);
   }

   /**
    * Methods called when the user click on the radio button "Server connection"
    */
   public void chooseServerConnection() {
     if(connectTo.isSelected() == true) {
       int i=JOptionPane.showConfirmDialog(null, "You are currently connected (client connection).\n"+
           "This test programm does not allow client AND server connections to co-exsits\n"+
           "If you choose 'yes', the client connection will therefore be closed",
           "Warning",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.WARNING_MESSAGE);
       if (i == JOptionPane.OK_OPTION ) {
         try {
           closeConnection();
         }
         catch(Exception ex) {
           ex.printStackTrace();
         }
       } else return;
     }
     m_offerService.setEnabled(true);
     connectionURL.setEnabled(false);
     connectTo.setEnabled(false);
   }

   /**
    * Closes the connection managed by this class
    * @throws Exception
    */
   public void closeConnection() throws Exception{
//     System.out.println ("Closing Connection 1");
     if (this.receiverThread != null) { receiverThread.stopReading(); receiverThread = null; }
//     System.out.println ("Closing Connection 2");
     if (is != null) { is.close(); is = null; }
//     System.out.println ("Closing Connection 3");
     if (os != null) { os.close(); os = null; }
//     System.out.println ("Closing Connection 4");
     if (streamCon != null) { streamCon.close(); streamCon = null; }
//     System.out.println ("Closing Connection 5");
     enableConnAttributes(false);
//     System.out.println ("Closing Connection 6");
     l2CAPBut.setEnabled(false);
   }

   /**
    * Method called when the user chooses a protocol in the protocol list.
    */
   private void chooseProtocol() {
     int index=m_protocols.getSelectedIndex();
     if(index==-1) return;

      if(index!=currentIndex && (connectTo.isSelected() || m_offerService.isSelected())) {
       int i=JOptionPane.showConfirmDialog(null, "A connection exists. Do you want to close it?","Change protocol",
                                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
       if(i==JOptionPane.OK_OPTION) {
         try {
           if(connectTo.isSelected() && streamCon!=null) closeConnection();
           else revokeService();
         }catch(Exception ex) {
           ex.printStackTrace();
           showError("Connection could not be closed!");
           return;
         }
       } else return;
     }
     currentIndex=index;
     getProperties();
   }

   /**
    * Enables / Disables the security options, depending on possibilities offered by the stack.
    */
   public void getProperties() {
     boolean oneNotEnable=false;
     for(int i=0;i<3;i++) {
       if(properties[currentIndex][i]==0) oneNotEnable=true;
     }
     m_authentication.setEnabled(properties[currentIndex][0]==1);
     m_encrypt.setEnabled(properties[currentIndex][1]==1);
     m_master.setEnabled(properties[currentIndex][2]==1);
     securityNotAvailable.setText(oneNotEnable?"Some security options are not avalaible for your OS!":"");
     connectionURL.setText("");
     this.m_servicePanel.setLocalPref(localPref[currentIndex],new UUID[]{this.uuidList[currentIndex]});
   }

   /**
    * Shows an information message in a popup window
    * @param message The message
    * @param title The title of the popup
    */
   public void showInfo(String message, String title) {
     JOptionPane.showMessageDialog(null, message, title,JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Shows an error message in a popup window
    * @param message The message
    */
   public void showError(String message) {
     JOptionPane.showMessageDialog(null, message, "Error",JOptionPane.ERROR_MESSAGE);
   }

   /**
    * Methods called when an user selects a service in the ServiceFinderPane and double-clicks on it or
    * click on the button "select"
    */
   public void selectService() {
     if(m_server.isSelected()) {
       showInfo("This option is only available for client connections", "Warning");
       return;
     }
     if(m_servicePanel.getSelectedService() != null) {
       try {
         setConnectionURLClient();
       }catch(Exception ex) {
         ex.printStackTrace();
         showError(ex.getMessage());
       }
     }
   }

   /**
    * Sets the right connection url in the appropriated Text Field
    * @throws Exception
    */
   public void setConnectionURLClient() throws Exception{
     JSR82URL url=new JSR82URL(m_servicePanel.getSelectedService().getServiceURL());
     if(currentIndex!=url.getProtocol()) throw new Exception(m_servicePanel.getSelectedService().getServiceURL() + "!=" + currentIndex);
     url.setParameter("encrypt", new Boolean(m_encrypt.isSelected()));
     url.setParameter("authenticate", new Boolean(m_authentication.isSelected()));
     url.setParameter("master", new Boolean(m_master.isSelected()));
     connectionURL.setText(url.toString());
   }

   /**
    * Shows information about the remote device (name, device class, BT address ..etc..)
    */
   public void getRemoteDevInfos() {
   	 RemoteDevice rd = null;
   	 String name = "unknown";
   	 int rssi = 0;
	 int lq = 0;
   	 try {
     	rd = RemoteDevice.getRemoteDevice(streamCon);
		name = rd.getFriendlyName(false);
		rssi = Rssi.getRssi(rd.getBTAddress());
		lq = LinkQuality.getLinkQuality(rd.getBTAddress());
	} catch (Exception e) {
		showInfo (e.getMessage(), "Error");
		return;
	}
     showInfo("Remote Device Address, Name, Rssi und Quality\n" + rd.getBluetoothAddress() + " " + name + " " + rssi + " " + lq,"Info");
   }

   /**
    * Shows a dialog containing a list of all BT devices.
    */
   public void startInquiry() {
     try {
       IntDeviceFinder myFinder=new IntDeviceFinder(this,true);
       myFinder.setVisible(true);
     }catch(Exception ex) {ex.printStackTrace();}
   }

   /**
    * Turns on/off the encryption of an existing ACL link
    */
   public void encryptLink() {
     try {
       RemoteDevice dev=((BTConnection)streamCon).getRemoteDevice();
       dev.encrypt(streamCon, !m_encrypt.isEnabled());
     }catch(Exception ex) {
       showError(ex.getMessage());
     }
   }

   /**
    * Authenticates the remote device connected with the local device
    */
   public void authenticateLink() {
     try {
       RemoteDevice dev=((BTConnection)streamCon).getRemoteDevice();
             
       JOptionPane.showMessageDialog(this, "Authentification " + (dev.authenticate() ? "successfull" : "Non successfull"));
     }catch(Exception ex) {
       showError(ex.getMessage());
     }
   }

   /**
    * Switches the state of the local device between Master and Slave
    */
   public void switchMaster() {
     try {
       throw new Exception("not yet implemented!");
     }catch(Exception ex) {
       showError(ex.getMessage());
     }

   }

   /**
    * Reacts to an action
    * @param e
    */
   public void actionPerformed(ActionEvent e) {
     try {
       if (e.getSource() == m_protocols) chooseProtocol();
       else if (e.getSource() == m_inquire) startInquiry();
       else if (e.getSource() == m_servicePanel.m_select) selectService();
       else if (e.getSource() == m_client) chooseClientConnection();
       else if (e.getSource() == m_server ) chooseServerConnection();
       else if (e.getSource() == m_offerService && m_offerService.isSelected() == true) offerService();
       else if (e.getSource() == m_remote)  	getRemoteDevInfos();
       else if (e.getSource() == m_offerService && m_offerService.isSelected() == false) revokeService();
       else if (e.getSource() == m_encryptLink) encryptLink();
       else if (e.getSource() == m_authenticateLink) authenticateLink();
       else if (e.getSource() == m_switchMaster) switchMaster();
       else if(e.getSource() == m_localAddress) {
         showInfo("Local BT address : "+BTAddress.transform(m_local.getBluetoothAddress()),"Local Bluetooth address");
       }
       else if(e.getSource() ==   m_localName) {
         showInfo("Local BT name : "+m_local.getFriendlyName(),"Local Bluetooth device name");
       }
       else if(e.getSource() ==   m_localDevClass) {
         DeviceClass cl=m_local.getDeviceClass();
         String message="Minor : "+cl.getMinorDeviceClass()+"\n"+
                        "Major : "+cl.getMajorDeviceClass()+"\n"+
                        "Service class : "+cl.getServiceClasses();
         showInfo(message,"Local Device Class");
       }
       else if(e.getSource() ==   m_record) {
         showInfo("Not yet implemented","Info");
       }
       else if(e.getSource() ==   m_setlocalDevClass) {
         showInfo("Not yet implemented","Info");
       }
       else if(e.getSource() ==m_discoverable) {
         int mode=m_local.getDiscoverable();
         if (mode == DiscoveryAgent.GIAC) m_local.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
         else m_local.setDiscoverable(DiscoveryAgent.GIAC);
         showInfo("Previouse mode : "+mode, "Local discoverable mode");
       }

       else if (e.getSource() == connectTo && connectTo.isSelected() == true) {
         System.out.println ("Trying to connect to " + connectionURL.getText() + " " + streamCon);
         streamCon = Connector.open(connectionURL.getText());
         System.out.println ("Connected to " + connectionURL.getText() + " " + streamCon);
         
         if(streamCon instanceof StreamConnection) {
           is = ((StreamConnection)streamCon).openInputStream();
           os = ((StreamConnection)streamCon).openOutputStream();
           if (this.receiverThread != null) { receiverThread.stopReading(); receiverThread = null; }
           receiverThread = new DStreamThread();
           receiverThread.start();
           this.l2CAPBut.setEnabled(false);
         } else if (streamCon instanceof L2CAPConnection) {
           is = null;os = null;
           if(receiverThread!=null) {receiverThread.stopReading();receiverThread=null;}
           this.l2CAPBut.setEnabled(true);
           System.out.println ("ReceiveMTU=" + ((L2CAPConnection)streamCon).getReceiveMTU() + " TransmitMTU=" + ((L2CAPConnection)streamCon).getTransmitMTU());
         } else if (streamCon instanceof ClientSession) {
            is = null;os = null;
            this.l2CAPBut.setEnabled(true);
            if(receiverThread!=null) {receiverThread.stopReading();receiverThread=null;}
         }
         enableConnAttributes(true);
       }
       else if (e.getSource() == connectTo && connectTo.isSelected() == false) {
         closeConnection();
       }
       else if (e.getSource() == sendData) {
         if(m_protocols.getSelectedIndex() == JSR82URL.PROTOCOL_RFCOMM) {
            byte b[] = new byte[rfcommPackLen];
            for (int i = 0; i < b.length; i++) {	
				b[i] = (byte) (0x100 * Math.random());
			}
            /*byte b[] = new byte[] { (byte) 0x00, 0x00,
                    0x07, 0x0, (byte)0xfd}; */
        	    int csum = 0;
			for (int i = 0; i < b.length; i++) {	
				//b[i] = (byte) (0x100 * Math.random());
				csum += (int)(b[i] & 0xff);
			}
			System.out.println ("Checksum of burst " + (csum % 1024));
            int count = 0;
            boolean sendForever = System.getProperty("de.avetana.bluetooth.test.sendForever", "false").equals("true");
            int sendTimes = Integer.parseInt(System.getProperty("de.avetana.bluetooth.test.sendPackets", "1"));
			int countTimes = 0;
            do {
            	countTimes++;
            		os.write(b);
            		count += b.length;
            		System.out.println ("Sent " + count + " bytes");
            } while (sendForever || countTimes < sendTimes);
         	//System.out.println ("Wrote " + (int)(b[0] & 0xff) + " " + (int)(b[1] & 0xff));
            /*
            if (sendData.isSelected() && sendThread == null) {
            		Runnable r = new Runnable() {
            			public void run() {
            				while (sendThread != null) {
            					try {
									os.write (new byte[JSRTest.rfcommPackLen]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									sendThread = null;
									sendData.setSelected(false);
								}
            				}
            			}
            		};
            		sendThread = new Thread (r);
            		sendThread.start();
            } else if (!sendData.isSelected()) sendThread = null;
            */
         }
         else if(m_protocols.getSelectedIndex() == JSR82URL.PROTOCOL_L2CAP) {
         	byte[] b = new byte[(int)((double)((L2CAPConnection)streamCon).getTransmitMTU() * Math.random())];
            for (int i = 0; i < b.length; i++) b[i] = (byte) (256 * Math.random());
         	//byte[] b1 = new byte[] { 1,2,3,4,5 };
         	
         	//byte[] b2 = new byte[] { 11,22,33,44,55 };
            //((L2CAPConnection)streamCon).send(b1);       	
         	//((L2CAPConnection)streamCon).send(b2);       	
         	((L2CAPConnection)streamCon).send(b);       	
         	System.out.println ("Sent " + b.length + " bytes ");
         	//((L2CAPConnection)streamCon).close(); 
         }
         else if(m_protocols.getSelectedIndex() == JSR82URL.PROTOCOL_OBEX) {
            final ClientSession cs = (ClientSession)streamCon;
            HeaderSet hs = cs.createHeaderSet();
            /*cs.setAuthenticator(new Authenticator() {

				public PasswordAuthentication onAuthenticationChallenge(String description, boolean isUserIdRequired, boolean isFullAccess) {
					System.out.println ("onAuthenticationChallenge -" + description);
					return new PasswordAuthentication ("mgm".getBytes(), "abcdefg".getBytes());
				}

				public byte[] onAuthenticationResponse(byte[] userName) {
					System.out.println ("onAuthenticationResponse");
					return "abcdefgh".getBytes();
				}
            	
            });*/
            //hs.createAuthenticationChallenge("client realm", true, true);
            HeaderSet hs2 = cs.connect(hs);
            System.out.println ("Connected with response code " + hs2.getResponseCode());
      
            hs = cs.createHeaderSet();
            
            //This way sending a vcard works on any machine I know of.
           
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("avetana.vcf");
            byte b[] = new byte[is.available()];
            is.read(b);
            hs.setHeader (HeaderSet.NAME, "avetana.vcf");
            //hs.setHeader (HeaderSet.TYPE, "text/x-vcard");
            hs.setHeader (HeaderSet.LENGTH, new Long(b.length));
            hs.setHeader (0x48, b); // if everything fits inside a packet, the data can be packed in the PUT command
            
            final Vector status = new Vector();
            Runnable r = new Runnable() {

				public void run() {
					try {
						Thread.currentThread().sleep (15000);
						if (status.size() == 0) { 
							System.out.println ("Closing connection for timeout");
							cs.close();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
            	
            };
            new Thread (r).start();
            Operation po = cs.put(hs);
            status.add ("foo");
            //po.openOutputStream().write (b);
            po.close();

            
            	//This way, it should work, but it does not on the S55, because the type is not recognised when
            //Data is not sent within the PUT-Command as it is in the example above
/*            hs.setHeader(HeaderSet.NAME, "img.jpg");
            hs.setHeader (HeaderSet.TYPE, "image/jpg");
            File f = new File ("/tmp/img.jpg");
            hs.setHeader (HeaderSet.LENGTH, new Long(f.length()));
            Operation po = cs.put(hs);
            OutputStream os = po.openOutputStream();
            InputStream is = new FileInputStream(f);
            byte b[] = new byte[6000];
            int read;
            while ((read = is.read(b)) > 0) {
            		os.write(b, 0, read);
            }
            os.close();
            is.close();
            po.close();
            */
            
          /*
            // 	Example on how to send a simple text message
            //  Does not work on some phones that do not know about text messages 
            byte text[] = "Test Message from avetanaBlueooth".getBytes("iso-8859-1");
            hs.setHeader (HeaderSet.NAME, "test.txt");
            hs.setHeader (HeaderSet.TYPE, "text");
            //hs.setHeader(0x49, text); // if everything fits inside a packet, the data can be packed in the PUT command
            Operation po = cs.put(hs);
            po.openOutputStream().write(text);
            po.close();
            */
            cs.disconnect(null);
         }
       } else if (e.getSource() == dataReceived) {
       } else if (e.getSource() == this.l2CAPBut) {
        if (m_protocols.getSelectedIndex() == JSR82URL.PROTOCOL_L2CAP) { 
        		if (!((L2CAPConnection)streamCon).ready()) {
        			dataReceived.setText("No Packet");
        		} else {
        			byte b[] = new byte[((L2CAPConnection)streamCon).getReceiveMTU()];
        			int j = ((L2CAPConnection)streamCon).receive (b);
        			this.dataReceived.setText("received " + j + "(" + b.length + ")");
        		}
        } else if (m_protocols.getSelectedIndex() == JSR82URL.PROTOCOL_OBEX) {
        		ClientSession cs = (ClientSession)streamCon;
            HeaderSet hs = cs.createHeaderSet();
            cs.connect(null);
            hs.setHeader(HeaderSet.TYPE, "text/x-vcard");
            Operation po = cs.get(hs);
            HeaderSet respSet = po.getReceivedHeaders();
            byte[] b = new byte[po.openInputStream().available()];
            po.openInputStream().read(b);
            File f = File.createTempFile("obex", ".txt");
            FileOutputStream fos = new FileOutputStream (f);
            fos.write(b);
            fos.close();
            System.out.println ("Object written to " + f.getAbsolutePath());
            serviceStatus.setText ("Object received " + respSet.getHeader(HeaderSet.NAME) + " size " + po.getLength());
            po.close();
            cs.disconnect(null);
        }
       }
     }
     catch (Exception e2) {
       e2.printStackTrace();
       showError(e2.getMessage());
     }
   }

   //used in OBEX authentication
   private boolean authenticationFailed = false;
   
   /**
    * Offers service : SDP server!
    */
   private void offerService() {
     Runnable r=null;
     if(m_protocols.getSelectedIndex()==JSR82URL.PROTOCOL_RFCOMM) {
       r = new Runnable() {
         public void run () {
           try {
             //JSR82URL url=new JSR82URL("btspp://localhost:0dad43655df111d69f6e00039353e858;name=JSRTest");
             //JSR82URL url=new JSR82URL("btspp://localhost:00112233445566778899aabbccddeeff;name=JSRTest");
//        	 JSR82URL url=new JSR82URL("btspp://localhost:27012f0c68af4fbf8dbe6bbaf7ab651b:11;name=JSRTest");
             
       	 JSR82URL url=new JSR82URL("btspp://localhost:" + new UUID(SDPConstants.UUID_DIALUP_NETWORKING) + ";name=JSRTest");
        	 
        	 url.setParameter("encrypt", new Boolean(m_encrypt.isSelected()));
             url.setParameter("authenticate", new Boolean(m_authentication.isSelected()));
             url.setParameter("master", new Boolean(m_master.isSelected()));
             System.out.println(url.toString());
             notify = (ConnectionNotifier)Connector.open(url.toString());
//             notify = (ConnectionNotifier)Connector.open("btspp://localhost:27012f0c68af4fbf8dbe6bbaf7ab651b:22;name=JSRTest");
             int secSet = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
             if (m_authentication.isSelected() && m_encrypt.isSelected()) secSet = ServiceRecord.AUTHENTICATE_ENCRYPT;
             else if (m_authentication.isSelected() && !m_encrypt.isSelected()) secSet = ServiceRecord.AUTHENTICATE_NOENCRYPT;
             serviceStatus.setText("ready " + LocalDevice.getLocalDevice().getRecord(notify).getConnectionURL(secSet, m_master.isSelected()));

             streamCon = ((StreamConnectionNotifier)notify).acceptAndOpen();
             serviceStatus.setText("connected with fid="+((BTConnection)streamCon).getConnectionID());
             is = ((StreamConnection)streamCon).openInputStream();
             os = ((StreamConnection)streamCon).openOutputStream();
             if (receiverThread != null) receiverThread.stopReading();
             receiverThread = new DStreamThread();
             receiverThread.start();
             enableConnAttributes(true);
             JSRTest.this.l2CAPBut.setEnabled(false);
             } catch (Exception e) { e.printStackTrace(); }
             
         }
       };
     } else if(m_protocols.getSelectedIndex()==JSR82URL.PROTOCOL_L2CAP) {
       r = new Runnable() {
         public void run () {
           try {
             JSR82URL url=new JSR82URL("btl2cap://localhost:ce37ca6e288a409a9796191882ee44fc;name=L2CAPTest;receiveMTU=672");
             url.setParameter("encrypt", new Boolean(m_encrypt.isSelected()));
             url.setParameter("authenticate", new Boolean(m_authentication.isSelected()));
             url.setParameter("master", new Boolean(m_master.isSelected()));

             notify = (ConnectionNotifier)Connector.open(url.toString());
             int secSet = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
             if (m_authentication.isSelected() && m_encrypt.isSelected()) secSet = ServiceRecord.AUTHENTICATE_ENCRYPT;
             else if (m_authentication.isSelected() && !m_encrypt.isSelected()) secSet = ServiceRecord.AUTHENTICATE_NOENCRYPT;
             serviceStatus.setText("ready " + LocalDevice.getLocalDevice().getRecord(notify).getConnectionURL(secSet, m_master.isSelected()));

             streamCon = ((L2CAPConnectionNotifierImpl)notify).acceptAndOpen();
             serviceStatus.setText("connected with fid="+((BTConnection)streamCon).getConnectionID());
             System.out.println ("ReceiveMTU=" + ((L2CAPConnection)streamCon).getReceiveMTU() + " TransmitMTU=" + ((L2CAPConnection)streamCon).getTransmitMTU());
             JSRTest.this.l2CAPBut.setEnabled(true);
             enableConnAttributes(true);
             } catch (Exception e) { e.printStackTrace(); }
         }
       };
     }else if(m_protocols.getSelectedIndex()==JSR82URL.PROTOCOL_OBEX) {
		obexDisconnected = false;
		r = new Runnable() {
			public void run() {
				try {
					//Obex must be offered with only the OBEX-ObjetPush UUID. It will not be
					//recognised as an OBEX Service with a other UUID
		    	notify = Connector.open("btgoep://localhost:" + new UUID (SDPConstants.UUID_OBEX_OBJECT_PUSH) + ";name=OBEXTest;authenticate=false;master=false;encrypt=false");
	             int secSet = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
	             if (m_authentication.isSelected() && m_encrypt.isSelected()) secSet = ServiceRecord.AUTHENTICATE_ENCRYPT;
	             else if (m_authentication.isSelected() && !m_encrypt.isSelected()) secSet = ServiceRecord.AUTHENTICATE_NOENCRYPT;
	             serviceStatus.setText("ready " + LocalDevice.getLocalDevice().getRecord(notify).getConnectionURL(secSet, m_master.isSelected()));
				
				Authenticator authHandler = null;/*new Authenticator() {

					public PasswordAuthentication onAuthenticationChallenge(String description, boolean isUserIdRequired, boolean isFullAccess) {
						System.out.println ("onAuthenticationChallenge server desc " + description);
						return new PasswordAuthentication ("mgm".getBytes(), "abcdefg".getBytes());
					}

					public byte[] onAuthenticationResponse(byte[] userName) {
						System.out.println ("onAuthenticationResponse");
						return "abcdefg".getBytes();
					}
	            	
	            };*/
/*
				ServiceRecord sr = LocalDevice.getLocalDevice().getRecord(notify);
				DataElement testEl = new DataElement (DataElement.DATSEQ);
				testEl.addElement(new DataElement (DataElement.U_INT_1, 0xff));
				sr.setAttributeValue(0x303, testEl);
				sr.setAttributeValue(0x100, new DataElement (DataElement.STRING, "OBEX Object Push"));
				LocalDevice.getLocalDevice().updateRecord(sr);
*/
	            authenticationFailed = false;
				((SessionNotifier)notify).acceptAndOpen(new ServerRequestHandler() {
					
					public int onConnect (HeaderSet request, HeaderSet response) {
						serviceStatus.setText ("RequestHandler got connect");
						
						//response.setHeader(HeaderSetImpl.CONNECTION_ID, new Long(12345));
						
						int retCode = ResponseCodes.OBEX_HTTP_OK;
						//If an authenticationResponseHeader is in the Request and authenticationFailed
						//has not been called by the SessionNofifier, you can assume that authentication was
						//successfull.
						//you could also (alternatively) test whether onAuthenticationResponse has been called
						//and not onAuthenticationFailure
/*
						if (request.getHeader(HeaderSetImpl.AUTH_RESPONSE) != null && authenticationFailed == false)
							retCode = ResponseCodes.OBEX_HTTP_OK;
						else {
							response.createAuthenticationChallenge("Server realm", true, true);
							retCode = ResponseCodes.OBEX_HTTP_UNAUTHORIZED;
						}
						*/
						System.out.println ("Returning ret code " + Integer.toHexString(retCode));
						
						return retCode;
					}
					
					public int onPut (Operation op) {

						try {
							java.io.InputStream is = op.openInputStream();
						serviceStatus.setText ("Got data bytes " + is.available() + " name " + op.getReceivedHeaders().getHeader(HeaderSet.NAME) + " type " + op.getType());
						System.out.println ("Got data bytes " + is.available() + " name " + op.getReceivedHeaders().getHeader(HeaderSet.NAME) + " type " + op.getType());
						File f = File.createTempFile("obex", ".tmp");
						FileOutputStream fos = new FileOutputStream (f);
						byte b[] = new byte[1000];
						int len;
						while (is.available() > 0 && (len = is.read(b)) > 0) {
							fos.write (b, 0, len);
						}
						fos.close();
						System.out.println ("Wrote data to " + f.getAbsolutePath());
						op.sendHeaders(null);
//						is.close();
//						System.out.println("InputStream closed");
						} catch (Exception e) { e.printStackTrace(); }
						return 0xa0;
					}
					
					public void onDisconnect (HeaderSet req, HeaderSet resp) {
						serviceStatus.setText("Disconnected");
						obexDisconnected = true;
					}
					
					public int onGet (Operation op) {
						
						
						try {
							HeaderSet hs = op.getReceivedHeaders();

							int[] hl = hs.getHeaderList();
							for (int i = 0;i < hl.length;i++) {
								System.out.println ("Received header " + hl[i] + " : " + hs.getHeader(hl[i]));
							}
						
							op.openOutputStream().write ("Test Message from avetanaBluetooth".getBytes());
						/*InputStream is = new FileInputStream ("/Users/gmelin/eclipse-workspace/avetanabt/de/avetana/bluetooth/JSRTest.java");
						byte b[] = new byte[10000];
						int len = 0;
						while ((len = is.read (b)) > 0) {
							op.openOutputStream().write(b, 0, len);
							System.out.println ("Added " + len + " bytes to OK");
						}*/
						HeaderSet set = op.getReceivedHeaders();
						set.setHeader(HeaderSet.TYPE, "text/plain");
						set.setHeader(HeaderSet.NAME, "msg.txt");
						op.sendHeaders (set);
						} catch (IOException e) {
							e.printStackTrace();
							return 0xc5;
						}
						return 0xa0;
					}
					
					public void onAuthenticationFailure (byte[] user) {
						System.out.println ("Authentication failed");
						authenticationFailed = true;
					}
				}, authHandler);
								
				} catch (Exception e) { e.printStackTrace(); }
			}
		};
		
     }
     if(r!=null) {
       new Thread (r).start();
     }
   }

   /**
    * Closes a connection and delete the service from the local BCC. For server connection only.
    */
   private void revokeService() {
     try {
       closeConnection();
       if (notify != null) { obexDisconnected = true; notify.close(); notify = null; }
       serviceStatus.setText("closed");
       m_offerService.setSelected(false);
     } catch (Exception e) { e.printStackTrace();  serviceStatus.setText("error");}
   }


   /**
    * Thread used to read data from an RFCOMM connection
    */

   private class DStreamThread extends Thread {

     private boolean running;
     private int received;

     public DStreamThread() {
       super();
     }

     public void run() {
       running = true;
       received = 0;
       byte b[] = new byte[rfcommPackLen];
       try {
		   int csum = 0, csumc = 0, a = 0;
         while (running) {
           //int v1 = is.read();
           //int v2 = is.read();
           //System.out.println (v1 + " " + v2);
           dataReceived.setText("Received " + received);
           if (is.available() > 0)
            a = is.read(b);
           else {
               a = 0;
        	   try { synchronized (this) { wait(100); }} catch (Exception e) {}
           }
		   /*for (int i = 0;i < a;i++) {
			   System.out.print (" " + Integer.toHexString((int)(b[i] & 0xff)));
			   csum += (int)(b[i] & 0xff);
		   }
		   System.out.println ();
		   csumc += a;
		   if (csumc >= rfcommPackLen) {
			   System.out.println ("Checksum after " + csumc + " is " + (csum % 1024));
			   csumc = csum = 0;
		   }*/
           received += a;
           //os.write (new byte[100]);
         }
       } catch (Exception e) {e.printStackTrace(); if (m_offerService.isSelected()) revokeService(); if (connectTo.isSelected()) { connectTo.setSelected(false); try { closeConnection(); } catch (Exception ec) {}} }
     }

     public void stopReading() {
       running = false;
     }
   }

   public static void main(String[] args) throws Exception {
	   LocalDevice.getLocalDevice();
//     JSRTest JSRTest1 = new JSRTest();
     JSRTest JSRTest2 = new JSRTest();
  }
}
