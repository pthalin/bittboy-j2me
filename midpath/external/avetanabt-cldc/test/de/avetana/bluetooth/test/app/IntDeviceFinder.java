package de.avetana.bluetooth.test.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
 * <b>Description: </b><br>A Dialog performing an HCI inquiry and storing the result of this inquire in a
 * a JList swing component.
 * </b><br>
 */

public class IntDeviceFinder extends JDialog implements ActionListener{

  private DeviceFinderPane m_pane;
  private boolean m_start=false;
  private JButton close;

  public IntDeviceFinder(Frame owner, boolean start) throws HeadlessException, Exception {
    super(owner, "Device Finder",true);
    try {
      m_start=start;
      jbInit();
      pack();
      setLocationRelativeTo(null);
    }catch(Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
      setVisible(false);
   }
 }

 public void actionPerformed(ActionEvent e) {
   if (e.getSource() == close) setVisible(false);
  }

 public void jbInit() throws Exception {
   close = new JButton("Close");
   m_pane=new DeviceFinderPane(this);
   JPanel command=m_pane.getCommandPanel();
   command.add(close);
   Container c=this.getContentPane();
   c.setLayout(new BorderLayout());
   c.add(m_pane, BorderLayout.CENTER);
   close.addActionListener(this);
   if(m_start) m_pane.doInquiry();
  }
 
}