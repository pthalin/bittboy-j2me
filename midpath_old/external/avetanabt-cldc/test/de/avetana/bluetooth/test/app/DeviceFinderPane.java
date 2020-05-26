package de.avetana.bluetooth.test.app;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
 * <b>Description: </b><br>A JPanel performing an HCI inquiry and storing the result of this inquire in a
 * a JList swing component.
 * </b><br>
 */

public class DeviceFinderPane extends JPanel implements ActionListener, DiscoveryListener, Cancelable{

  private JList m_deviceList;
  private DiscoveryAgent m_agent;
  private int maxServiceSearch=1;
  private int nbOfServiceSearch=0;
  private ProgressDialog m_dialog;
  private Window m_owner;
  private Hashtable nameCache = new Hashtable();
  private Vector m_remote=new Vector();
  public JButton m_refresh, m_name;
  private JPanel m_commandPanel;
  private DefaultListModel myListModel;

  public DeviceFinderPane(Window owner) throws Exception{
    super ();
    m_owner=owner;
    myListModel = new DefaultListModel() {
      public int getSize() { return m_remote.size(); }
      public Object getElementAt(int index) {
        if(index == -1 || m_remote.size() <= index) return null;
        RemoteDevice dev=(RemoteDevice)m_remote.elementAt(index);
        return dev.toStringWithName();
      }
      public Object elementAt(int index) {
        return getElementAt(index);
      }
    };

    m_deviceList=new JList(myListModel);
    initStack();
    setLayout(new BorderLayout());
    JScrollPane myPane=new JScrollPane(m_deviceList);
    add(myPane, BorderLayout.CENTER);

    m_commandPanel = new JPanel ();

    m_refresh = new JButton ("Refresh");
    m_name= new JButton ("Get names");
    m_name.setEnabled(false);
    m_commandPanel.add(m_refresh);
    m_commandPanel.add(m_name);
 //   if (System.getProperty("os.name").equalsIgnoreCase("mac os x")) butPan.add(macScan);
 //   c.add(butPan, BorderLayout.SOUTH);
    m_refresh.addActionListener(this);
    m_name.addActionListener(this);
    add(m_commandPanel, BorderLayout.SOUTH);
  }

  public JPanel getCommandPanel() {
    return m_commandPanel;
  }

  public void initStack() throws Exception{
    LocalDevice local=LocalDevice.getLocalDevice();
    m_agent=local.getDiscoveryAgent();
  }

  public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
    String addr=null, name=null;
    try {
      addr=btDevice.getBluetoothAddress();
      name=btDevice.getFriendlyName(false);
      inform("Device: "+btDevice.getFriendlyName(false)+" found!!");
      System.out.println ("Device: "+btDevice.getFriendlyName(false)+" found!! major device class " + cod.getMajorDeviceClass() + " minor " + cod.getMinorDeviceClass() + " service " + cod.getServiceClasses());
     }
    catch(Exception ex) {ex.printStackTrace();}
    if(addr!=null) {
      nameCache.put(addr, (name==null?"Not Found":name));
      m_remote.addElement(btDevice);
    }
  }
  
  public void cancel() {
  	m_agent.cancelInquiry (this);
  	m_dialog.setVisible (false);
  }

  public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
  }

  public void inform(String message) {
    if(m_dialog!=null) m_dialog.setString(message);
  }

  public void showDialog() {
    Runnable r=new Runnable() {
      public void run() {
        if(m_owner instanceof Dialog)
          m_dialog=new ProgressDialog((Dialog)m_owner);
        else
          m_dialog=new ProgressDialog((Frame)m_owner);
        m_dialog.setVisible(true, DeviceFinderPane.this);
      }
    };
    new Thread(r).start();
  }

  public void doInquiry() {
    showDialog();
    nameCache=new Hashtable();
    m_remote=new Vector();
    try {
      m_agent.startInquiry(DiscoveryAgent.GIAC, this);
    }
    catch(Exception ex) {ex.printStackTrace();}
  }

  public void actionPerformed (ActionEvent e) {
    if (e.getSource() == m_refresh) doInquiry();
    else if (e.getSource() == m_name) {
      Cursor cur=this.getCursor();
      this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      for(int i=0;i<m_remote.size();i++) {
        try {
          RemoteDevice dev=(RemoteDevice)m_remote.elementAt(i);
          dev.getFriendlyName(true);
          }catch(Exception ex) {ex.printStackTrace();}
      }
      myListModel.removeAllElements();
      for(int i=0;i<m_remote.size();i++) {
        myListModel.addElement(m_remote.elementAt(i));
      }
      this.setCursor(cur);
      m_deviceList=new JList(myListModel);
    }
  }

  public void inquiryCompleted(int discType) {
    m_deviceList.setListData(m_remote);
    if (m_dialog != null) m_dialog.setVisible(false);
  }

  public void serviceSearchCompleted(int transID, int respCode) {
  }
  }
