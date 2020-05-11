package de.avetana.bluetooth.test.app;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.avetana.bluetooth.sdp.*;

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
 * <b>Description:</b><br> JPanel that finds every bluetooth device with Services matching a search kriteria. The search kriteria (an Array of UUIDs)
 * is given by the programmer as a parameter of the ServiceFinder constructor. If this parameterr is null, the ServiceFinder
 * class will then look for every Services accepting RFCOMM connections.
 *
 * @author Julien Campana
 */


public class ServiceFinderPane extends JPanel implements Cancelable, ActionListener, DiscoveryListener, TreeSelectionListener{

  private JTree m_sensorList;
  private DiscoveryAgent m_agent;
  public ServiceDescriptor selected;
  private int maxServiceSearch=1;
  private int nbOfServiceSearch=0;
  public static volatile ProgressDialog m_dialog;
  private UUID[] m_search;
  private Window m_owner;
  private DefaultMutableTreeNode m_top;
  private Hashtable nameCache = new Hashtable();
  private Vector m_remote=new Vector();
  public JButton m_refresh, m_select;
  private JPanel m_commandPanel;
  private String m_localPrefName;
  private int serviceSearchTransID = -1;

  /**
   * Constructs a new ServiceFinderPane object
   * @param owner The window owner of the JPanel
   * @param localPrefName The list of all services found are stored in the local user preferences. Each preference
   * stored has a name. This variable allows the user to set this name
   * @param a_search The search kriteria (an array of UUIds)
   * @throws Exception
   */
  public ServiceFinderPane(Window owner, String localPrefName, UUID[] a_search) throws Exception{
    super ();
    m_owner=owner;
    m_sensorList=new JTree();
    m_sensorList.addTreeSelectionListener(this);
    setLocalPref(localPrefName, a_search);
    initStack();


    setLayout(new BorderLayout());
    JScrollPane myPane=new JScrollPane(m_sensorList);
    add(myPane, BorderLayout.CENTER);

    m_commandPanel = new JPanel ();

    m_refresh = new JButton ("Refresh");
    m_select = new JButton ("Select");
    m_commandPanel.add(m_refresh);
    m_commandPanel.add(m_select);
 //   if (System.getProperty("os.name").equalsIgnoreCase("mac os x")) butPan.add(macScan);
 //   c.add(butPan, BorderLayout.SOUTH);
    m_refresh.addActionListener(this);
    m_select.addActionListener(this);
    add(m_commandPanel, BorderLayout.SOUTH);
  }

  /**
   * Adds a mouse listener to the nested JTree
   * @param listener The mouse listener to be added
   */
  public void addMouseListenerToTree(MouseListener listener) {
    m_sensorList.addMouseListener(listener);
  }

  /**
   * Sets the local search and storing preferences and reloads them.
   * @param localPrefName The storing preferences
   * @param a_search The new search kriteria
   */
  public void setLocalPref(String localPrefName, UUID[] a_search) {
    m_localPrefName=localPrefName;
    if(a_search == null || a_search.length  == 0)
    a_search=new UUID[]{new UUID("0003",true)};
    m_search=a_search;
    Vector sensors=new Vector();
    try {
      Preferences prefs = Preferences.userNodeForPackage(this.getClass());
      byte[] b = prefs.getByteArray(m_localPrefName, null);
      ObjectInputStream ois = new ObjectInputStream (new ByteArrayInputStream (b));
      sensors = (Vector)ois.readObject();
    } catch (Exception e) { System.err.println("WARNING - No local preferences defined for this protocol!"); }
    initTree();
    for(int i=0;i<sensors.size();i++) {
      try {
        addService((ServiceDescriptor)sensors.elementAt(i));
        }catch(Exception ex) {ex.printStackTrace();}
    }
    if(sensors.size()!=0) expandAll();
  }

  /**
   * Returns the JPanel containg all commands (JButtons)
   * @return
   */
  public JPanel getCommandPanel() {
    return m_commandPanel;
  }

  /**
   * Called, when the cancel button is hit on the ProgressDialog
   */
  
  public void cancel() {
  	if (serviceSearchTransID == -1) m_agent.cancelInquiry(this);
  	else {
  		m_agent.cancelServiceSearch(serviceSearchTransID);
  	}
  	m_dialog.setVisible (false);
  }
  /**
   * Inits the local BT stack
   * @throws Exception
   */
  public void initStack() throws Exception{
    LocalDevice local=LocalDevice.getLocalDevice();
    m_agent=local.getDiscoveryAgent();
  }

  /**
   * Inits the JTree component
   */
  public void initTree() {
    m_top=new DefaultMutableTreeNode("Bluetooth Devices");
    ((DefaultTreeModel)m_sensorList.getModel()).setRoot(m_top);
  }

  // Searches a service by name
  private DefaultMutableTreeNode searchDevice(String name) {
    if(m_top==null) return m_top;
    for(int i=0;i<m_top.getChildCount();i++) {
      DefaultMutableTreeNode dev=(DefaultMutableTreeNode)m_top.getChildAt(i);
      String devStr=(String)dev.getUserObject();
      if(devStr==null) continue;
      if(devStr.equals(name)) return dev;
    }
    return null;

  }

  // Add a remote device to the tree
  private DefaultMutableTreeNode addDevice(String devName) {
    if(devName==null) return null;
    DefaultMutableTreeNode dev=null;
    if((dev=searchDevice(devName))==null) {
      dev=new DefaultMutableTreeNode(devName);
      m_top.add(dev);
      ((DefaultTreeModel)m_sensorList.getModel()).valueForPathChanged(new TreePath(dev.getPath()), dev.getUserObject());
      ((DefaultTreeModel)m_sensorList.getModel()).valueForPathChanged(new TreePath(m_top.getPath()), m_top.getUserObject());
    }
    return dev;
  }

  // Add a service to the tree
  private void addService(ServiceDescriptor serv) {
    DefaultMutableTreeNode toAdd=null;
    if(serv.getRemoteName()!=null)
      toAdd=addDevice(serv.getRemoteName());
    else if(serv.getBTAddress()!=null && serv.getBTAddress().toString()!=null)
      toAdd=addDevice(serv.getBTAddress().toString());
    else
      toAdd=addDevice("Undefined Devices");
    DefaultMutableTreeNode servNode=new DefaultMutableTreeNode(serv);
    toAdd.add(servNode);
  }


  /**
   * Reaction of the class when a new device is discovered
   * @param btDevice The new Remote device
   * @param cod The device class
   */
  public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
    String adr = btDevice.getBluetoothAddress();
    String name=null;
    try {
      name=btDevice.getFriendlyName(false);
    }
    catch(Exception ex) {ex.printStackTrace();}
    if(adr!=null) {
      nameCache.put(adr, (name==null?"Not Found":name));
      m_remote.addElement(btDevice);
      inform("Device: "+name+" found!!");
      try {
		System.out.println ("Device: "+btDevice.getFriendlyName(false)+" found!! major device class " + cod.getMajorDeviceClass() + " minor " + cod.getMinorDeviceClass() + " service " + cod.getServiceClasses());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  }

  /**
   * Reaction of the class when new services are discovered
   * @param transID The SDP transaction ID
   * @param servRecord The new services discovered
   */
  public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
  	//System.out.println ("services Discovered " + servRecord[0]);
    for(int i=0;i<servRecord.length;i++) {
      try {
        RemoteDevice dev=servRecord[i].getHostDevice();
        String name=(String)nameCache.get(dev.getBluetoothAddress());
        ServiceDescriptor myService=new ServiceDescriptor(dev);
        myService.setRemoteName(name);
        myService.parseServiceRecord(servRecord[i], ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
        addService(myService);
        inform("Service: "+myService.getServiceName()+" found!");
      }catch(Exception ex) {ex.printStackTrace();}
    }
  }

  /**
   * Shows an information message in the progress dialog
   * @param message The information message
   */
  public void inform(String message) {
    if(m_dialog!=null) m_dialog.setString(message);
//    System.out.println (message);
  }

  /**
   * Starts the HCI inquiry. After the end of the HCI inquiry, a service search is performed for each found remote device
   */
  public void doInquiry() {

    m_dialog = null;
    serviceSearchTransID = -1;
    
    Runnable r=new Runnable() {

      public void run() {
        if(m_owner instanceof Dialog)
          m_dialog=new ProgressDialog((Dialog)m_owner);
        else
          m_dialog=new ProgressDialog((Frame)m_owner);
        m_dialog.setVisible(true, ServiceFinderPane.this);
      }
    };
    new Thread(r).start();

    while (m_dialog == null) {
      try { synchronized (this) { this.wait(100); } } catch (Exception e) {}
    }

    nameCache=new Hashtable();
    m_remote=new Vector();
    initTree();
    selected=null;
    try {
      m_agent.startInquiry(DiscoveryAgent.GIAC, this);
    }
    catch(Exception ex) {
    		JOptionPane.showMessageDialog(this, "Inquiry failed", "Warning", JOptionPane.ERROR_MESSAGE);
    		m_dialog.setVisible(false);
    		ex.printStackTrace();
    	}
  }

  private void serviceSearch() {
    try {
      int[] attrids=new int[]{0,1,2,3,4,256};
      for(int i=0;i<m_remote.size();i++) {
        RemoteDevice dev=(RemoteDevice)m_remote.elementAt(i);
        try {
        	//System.out.println ("Searching services for " + dev.getBluetoothAddress());
        		serviceSearchTransID = m_agent.searchServices(attrids, m_search, dev, this);
      	//System.out.println ("done starting Searching services for " + dev.getBluetoothAddress());
          synchronized(this) {
            nbOfServiceSearch++;
            if(nbOfServiceSearch==maxServiceSearch) {
              try {
                this.wait();
              }
              catch(Exception ex) {}
              if (serviceSearchTransID == -2) return;
            }
          }
        }catch(BluetoothStateException e) {e.printStackTrace();}
      }
      while(nbOfServiceSearch>0) {
        synchronized(this) {
          nbOfServiceSearch++;
          if(nbOfServiceSearch==maxServiceSearch) {
            try {
              this.wait();
            }
            catch(Exception ex) {ex.printStackTrace();}
            if (serviceSearchTransID == -2) return;
          }
        }
      }
      try {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream (bos);
        Vector v=getAllServices();
        oos.writeObject(v);
        oos.flush();
        oos.close();
        try {
          prefs.putByteArray(m_localPrefName, bos.toByteArray());
        } catch (IllegalArgumentException iae) { }
        bos.close();
      } catch (Exception e) { e.printStackTrace( ); }
      expandAll();
      try {
        m_dialog.setVisible(false);
      }catch(Exception ex) {ex.printStackTrace();}

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Vector getAllServices() {
    Vector v=new Vector();
    if(m_top!=null) {
      for(int i=0;i<m_top.getChildCount();i++) {
        try {
          DefaultMutableTreeNode device=(DefaultMutableTreeNode)m_top.getChildAt(i);
          if(device==null) continue;
          for(int u=0;u<device.getChildCount();u++) {
            DefaultMutableTreeNode service=(DefaultMutableTreeNode)device.getChildAt(u);
            if(service==null) continue;
            ServiceDescriptor desc=(ServiceDescriptor)service.getUserObject();
            if(desc!=null) v.addElement(desc);
          }
          }catch(Exception ex) { ex.printStackTrace(); continue; }
      }
    }
    return v;
  }

  /**
   * Returns the description of the selected service
   * @return The description of the selected service
   */
  public ServiceDescriptor getSelectedService() {return selected;}

  /**
   * Expands all the leafs of the tree in order to make them visible
   */
  public void expandAll() {
      TreeNode root = (TreeNode)m_sensorList.getModel().getRoot();
      expandAll(new TreePath(root));
      m_sensorList.repaint();
  }

  public void actionPerformed (ActionEvent e) {
    if (e.getSource() == m_refresh) doInquiry();
    else if (e.getSource() == m_select) {
      if(selected==null) {
        JOptionPane.showMessageDialog(null, "No service is currently selected!",
                                      "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

    }
  }

  private void expandAll( TreePath parent) {
    TreeNode node = (TreeNode)parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e=node.children(); e.hasMoreElements(); ) {
        TreeNode n = (TreeNode)e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(path);
      }
    }
    m_sensorList.expandPath(parent);
  }

  public void valueChanged(TreeSelectionEvent e) {
    selected=null;
    try {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                    m_sensorList.getLastSelectedPathComponent();
      if (node != null) {
        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof ServiceDescriptor) {
          selected=(ServiceDescriptor)nodeInfo;
        }
      }
   }catch(Exception eSelect) {eSelect.printStackTrace();}
  }

  public void inquiryCompleted(int discType) {
  	inform ("Inquiry completed " + discType);
  	if (discType == DiscoveryListener.INQUIRY_COMPLETED) {
 		synchronized (this) { try { wait (500); } catch (Exception e) {} }
		inform ("starting service search");
  		serviceSearch();
  	}
  }

  public void serviceSearchCompleted(int transID, int respCode) {
  	inform ("Service search completed " + respCode);
  	nbOfServiceSearch--;
    serviceSearchTransID = respCode == DiscoveryListener.SERVICE_SEARCH_TERMINATED ? -2 : -1;
    synchronized(this) {
      this.notifyAll();
    }
  }
}