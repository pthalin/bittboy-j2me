package de.avetana.bluetooth.test.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
* A dialog showing the progression of an operation.
* @author Julien Campana
*/

public class ProgressDialog extends JDialog {

   private JProgressBar jpb;
   private JLabel label;
   private JButton cancel=new JButton("Cancel");
   private Window m_owner;
   private Cancelable cListener = null;

   public ProgressDialog (Frame owner) {
     super(owner, "Status", true);
     init(owner);
   }

   public ProgressDialog (Dialog owner) {
     super(owner, "Status", true);
     init(owner);
   }

   public void init(Window owner) {
     m_owner=owner;
     Container cont=getContentPane();
     cont.setLayout(new BorderLayout());
     jpb=new JProgressBar();
     jpb.setIndeterminate(true);
     label=new JLabel("Searching ...");
     cont.add(label, BorderLayout.NORTH);
     cont.add(jpb, BorderLayout.CENTER);
     cancel.addActionListener(new ActionListener () {
     	public void actionPerformed (ActionEvent e) {
     		if (cListener != null) cListener.cancel();
     	}
     });
     cont.add(cancel, BorderLayout.SOUTH);
     pack();
     int height=(int)this.getPreferredSize().getHeight();
     this.setSize(300,height);
     this.setLocationRelativeTo(m_owner);
   }

   public void setString(String message) {
     label.setText(message);
   }
   
   public void setVisible (boolean stat, Cancelable cListener) {
   	this.cListener = cListener;
   	super.setVisible (stat);
   }
}