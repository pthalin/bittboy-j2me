/*
 * Created on 08.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PocketTest extends Frame {

	RemoteDevice frd = null;
	boolean inqCompleted = false;
	Button closeBut = new Button("Close");
	Panel discPan, rfcPan, rfsPan, l2cPan, l2sPan, actPanel;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public PocketTest() {
		super();
		this.setLayout(new BorderLayout());
		final Choice ch = new Choice();
		discPan = new DiscoveryPanel();
		rfsPan = new RFServerPanel();
		rfcPan = new RFClientPanel();
		l2cPan = new L2ClientPanel();
		l2sPan = new L2ServerPanel();
		
		ch.add ("Discovery");
		ch.add ("RFComm Client");
		ch.add ("RFComm Server");
		ch.add ("L2CAP Client");
		ch.add ("L2CAP Server");
		
		actPanel = discPan;
		add (discPan, BorderLayout.CENTER);
		add (closeBut, BorderLayout.SOUTH);
		add (ch, BorderLayout.NORTH);

		this.addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {
				System.exit(0);
			}
		});

		closeBut.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				System.exit(0);
			}
		});

		ch.addItemListener (new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (actPanel != null) remove (actPanel);
				int idx = ch.getSelectedIndex();
				if (idx == 0) actPanel = discPan;
				else if (idx == 1) actPanel = rfcPan;
				else if (idx == 2) actPanel = rfsPan;
				else if (idx == 3) actPanel = l2cPan;
				else if (idx == 4) actPanel = l2sPan;
				if (actPanel != null) add (actPanel, BorderLayout.CENTER);
				validate();
				repaint();
			}
		
		});
		
		setSize (Toolkit.getDefaultToolkit().getScreenSize());
		setLocation (0, 0);
		setVisible(true);
		this.setResizable(true);
		

		}

	/**
	 * @param gc
	 */
	
	public static void main (String args[]) {
		new PocketTest();
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */

	class DiscoveryPanel extends Panel implements DiscoveryListener, ActionListener {
		
		List dataList = new List();
		List serviceList = new List();
		Button startInq, startInqLoop, startSS;
		private boolean inquiring = false;
		private int inqCount = 0;
		
		public DiscoveryPanel() {
			setLayout (new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			startInq = new Button ("Start Inquiry");
			startInqLoop = new Button ("Start Inq loop");
			startSS = new Button ("Start ServiceSearch");
			add (startInq, gbc);
			gbc.gridx++; add (startSS, gbc);
			gbc.gridx++; add (startInqLoop, gbc);
			gbc.gridx = 0; gbc. gridy = 1; gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = gbc.weighty = 1;
			add (dataList, gbc);
			gbc.gridy++; add (serviceList, gbc);
			startInq.addActionListener(this);
			startInqLoop.addActionListener(this);
			startSS.addActionListener(this);
		}
		
		public void actionPerformed (ActionEvent e) {
			startInq.setEnabled (false);
			startSS.setEnabled (false);
			if (e.getSource() == startInq) {
				dataList.removeAll();
				try {
					inquiring = true;
					LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry (DiscoveryAgent.GIAC, this);
				} catch (BluetoothStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					dataList.add (e1.getMessage());
				}
			} else if (e.getSource() == startInqLoop && startInqLoop.getLabel().startsWith("Start")) {
				startInqLoop.setLabel("Stop Loop");
				inqCount = 0;
				Runnable r = new Runnable() {
					public void run() {
						while (startInqLoop.getLabel().startsWith("Stop")) {
							dataList.removeAll();
							inqCount++;
							dataList.add("Starting inquiry " + inqCount);
							try {
								inquiring = true;
								LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry (DiscoveryAgent.GIAC, PocketTest.DiscoveryPanel.this);
								while (inquiring) {
									synchronized (PocketTest.DiscoveryPanel.this) { PocketTest.DiscoveryPanel.this.wait (1000); }
								}
							} catch (BluetoothStateException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								dataList.add (e1.getMessage());
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};
				new Thread (r).start();
			} else if (e.getSource() == startInqLoop && startInqLoop.getLabel().startsWith("Stop")) {
				startInqLoop.setLabel("Start Loop");
				try {
					LocalDevice.getLocalDevice().getDiscoveryAgent().cancelInquiry(PocketTest.DiscoveryPanel.this);
				} catch (BluetoothStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if (e.getSource() == startSS) {
				serviceList.removeAll();
				String device = dataList.getSelectedItem();
				device = device.substring(0, device.indexOf(" "));
				try {
					LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(new int[] { 0x100 }, new UUID[] {new UUID(0x100) }, new RemoteDevice (device), this);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					serviceList.add (e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			if (frd == null) frd = btDevice;
			try {
				dataList.add(btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
			} catch (IOException e) {
				dataList.add("Error adding item " + btDevice.getBluetoothAddress());
				e.printStackTrace();
			}

			
		}

		/* (non-Javadoc)
		 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
		 */
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {

			for (int i = 0;i < servRecord.length;i++) {
				try {
					serviceList.add ((String)servRecord[i].getAttributeValue(0x100).getValue());
					serviceList.add (servRecord[i].getConnectionURL (0, false));
					} catch (Exception e) { e.printStackTrace(); }
	 		}

			
		}

		/* (non-Javadoc)
		 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
		 */
		public void serviceSearchCompleted(int transID, int respCode) {
			serviceList.add ("ServiceSearch completed " + respCode);
			startInq.setEnabled(true);
			startSS.setEnabled(true);
		}

		/* (non-Javadoc)
		 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
		 */
		public synchronized void inquiryCompleted(int discType) {
			dataList.add ("Inquiry completed " + discType);
			startInq.setEnabled(true);
			startSS.setEnabled(true);
			inquiring = false;
			notifyAll();
		}
	
	}
	
	class RFServerPanel extends Panel implements ActionListener{
		
		Button offerBut, closeBut, sendBut;
		StreamConnectionNotifier scnot;
		StreamConnection scon;
		InputStream inStream;
		OutputStream outStream;
		List dataList = new List();
		
		public RFServerPanel() {
			setLayout (new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = gbc.gridy = 0;
			gbc.fill = GridBagConstraints.NONE;
			offerBut = new Button ("Offer Service");
			closeBut = new Button ("Close Service");
			sendBut = new Button ("Send Data");
			add (offerBut, gbc);
			gbc.gridx++; add (closeBut, gbc);
			gbc.gridx++; add (sendBut, gbc);
			gbc.gridx = 0; gbc. gridy = 1; gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = gbc.weighty = 1;
			ScrollPane sp = new ScrollPane();
			sp.add (dataList);
			add (sp, gbc);
			sendBut.setEnabled (false);
			closeBut.setEnabled (false);
			offerBut.addActionListener (this);
			closeBut.addActionListener (this);
			sendBut.addActionListener (this);
		}

		public void actionPerformed(final ActionEvent e) {
			new Thread() {
				public void run() {
					if (e.getSource() == offerBut) {
						offerBut.setEnabled(false);
						closeBut.setEnabled(true);
						Runnable r = new Runnable() {
							
						public void run() {

							try {
								dataList.add ("Offering connection through Connector2 " + Connector.class.getName());
								scnot = (StreamConnectionNotifier)Connector.open ("btspp://localhost:00112233445566778899aabbccddeeff;name=ceTestRFCOMM");
								scon = (StreamConnection)scnot.acceptAndOpen();
					
								System.out.println ("Connected");
								
								inStream = scon.openInputStream();
								outStream = scon.openOutputStream();
					
								dataList.add ("Connection connected");
								sendBut.setEnabled(true);
								} catch (Exception e2) { 
									e2.printStackTrace();
									dataList.add (e2.getMessage());
								}

								Runnable r = new Runnable() {
									
								public void run() { 

									try {
										while (inStream != null) {
											int len = 0;
											while (inStream.available() > 0) {
												byte b[] = new byte[1000];
												len += inStream.read (b);
											}
											if (len > 0) {
												if (dataList.getItemCount() > 0 && dataList.getItem(dataList.getItemCount() - 1).startsWith("Read ")) {
													String val = dataList.getItem(dataList.getItemCount() - 1);
													len += Integer.parseInt(val.substring(5, val.indexOf(" bytes")));
													dataList.remove(dataList.getItemCount() - 1);
												} 
												dataList.add ("Read " + len + " bytes");
											}
									//System.out.println ("Data received");
											
											Thread.currentThread().sleep(10);
										}
									} catch (Exception e2) { 
											e2.printStackTrace();
											dataList.add (e2.getMessage());
										}
								}
								};
								new Thread(r).start();

						}
					};
						new Thread(r).start();

					} else if (e.getSource() == closeBut) {
						offerBut.setEnabled (true);
						closeBut.setEnabled (false);
						sendBut.setEnabled (false);
							try {
								if (inStream != null) inStream.close();
								if (outStream != null) outStream.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								dataList.add (e1.getMessage());
							}
							inStream = null;
							outStream = null;
						try { if (scon != null) scon.close();  } catch (Exception e2) {}
						try { if (scnot != null) scnot.close();  } catch (Exception e2) {}
						scon = null;
						scnot = null;
					} else if (e.getSource() == sendBut) {
						if (outStream != null) {
							try {
								outStream.write (new byte[100]);
								dataList.add ("Sent 100 bytes");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								dataList.add (e1.getMessage());
							}
							
						}
					}
				} 
					
				
			}.start();
	
		}
	}
	
	class L2ServerPanel extends Panel implements ActionListener{
		
		Button offerBut;
		Button closeBut, sendBut, pollBut;
		L2CAPConnectionNotifier scnot;
		L2CAPConnection scon;
		List dataList = new List();
		
		public L2ServerPanel() {
			setLayout (new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = gbc.gridy = 0;
			gbc.fill = GridBagConstraints.NONE;
			offerBut = new Button ("Offer");
			closeBut = new Button ("Close");
			sendBut = new Button ("Send");
			pollBut = new Button ("Poll");
			add (offerBut, gbc);
			gbc.gridx++; add (closeBut, gbc);
			gbc.gridx++; add (sendBut, gbc);
			gbc.gridx++; add (pollBut, gbc);
			gbc.gridx = 0; gbc. gridy = 1; gbc.gridwidth = 4;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = gbc.weighty = 1;
			ScrollPane sp = new ScrollPane();
			sp.add (dataList);
			add (sp, gbc);
			sendBut.setEnabled (false);
			pollBut.setEnabled (false);
			closeBut.setEnabled (false);
			offerBut.addActionListener (this);
			closeBut.addActionListener (this);
			sendBut.addActionListener (this);
			pollBut.addActionListener (this);
		}

		public void actionPerformed(ActionEvent e) {
		
			if (e.getSource() == offerBut) {
				offerBut.setEnabled(false);
				closeBut.setEnabled(true);
				Runnable r = new Runnable() {
					
				public void run() {

					try {
						dataList.add ("Offering connection through de.avetana.bluetooth.Connector");
						scnot = (L2CAPConnectionNotifier) Connector.open ("btl2cap://localhost:00112233445566778899aabbccddeeff;name=ceTestL2CAP");
						scon = (L2CAPConnection)scnot.acceptAndOpen();
			
						dataList.add ("Connection connected");
						sendBut.setEnabled(true);
						pollBut.setEnabled(true);
						} catch (Exception e2) { 
							e2.printStackTrace();
							dataList.add (e2.getMessage());
						}

				}
			};
				new Thread(r).start();

			} else if (e.getSource() == closeBut) {
				offerBut.setEnabled (true);
				closeBut.setEnabled (false);
				sendBut.setEnabled (false);
				pollBut.setEnabled (false);
				try { if (scon != null) scon.close();  } catch (Exception e2) {}
				try { if (scnot != null) scnot.close();  } catch (Exception e2) {}
				scon = null;
				scnot = null;
			} else if (e.getSource() == sendBut) {
				if (scon != null) {
					try {
						scon.send (new byte[100]);
						dataList.add ("Sent 100 bytes");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						dataList.add (e1.getMessage());
					}
					
				}
			} else if (e.getSource() == pollBut) {
				if (scon != null) {
					try {
						if (scon.ready()) {
							int len = scon.receive (new byte[500]);
							if (dataList.getItemCount() > 0 && dataList.getItem(dataList.getItemCount() - 1).startsWith("Read ")) {
								String val = dataList.getItem(dataList.getItemCount() - 1);
								len += Integer.parseInt(val.substring(5, val.indexOf(" bytes")));
								dataList.remove(dataList.getItemCount() - 1);
							} 
							dataList.add ("Read " + len + " bytes");
						} else dataList.add ("No data available");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						dataList.add (e1.getMessage());
					}
					
				}
				
			}
		} 
	}

	class RFClientPanel extends Panel implements ActionListener{
		
		Button connectBut, closeBut, sendBut;
		TextField adrField;
		StreamConnection scon;
		InputStream inStream;
		OutputStream outStream;
		List dataList = new List();
		
		public RFClientPanel() {
			setLayout (new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = gbc.gridy = 0;
			gbc.fill = GridBagConstraints.NONE;
			adrField = new TextField (60);
			connectBut = new Button ("Connect");
			closeBut = new Button ("Close");
			sendBut = new Button ("Send Data");
			add (connectBut, gbc);
			gbc.gridx++; add (closeBut, gbc);
			gbc.gridx++; add (sendBut, gbc);
			gbc.gridx = 0; gbc. gridy = 1; gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			add (adrField, gbc);
			gbc.gridy++;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = gbc.weighty = 1;
			ScrollPane sp = new ScrollPane();
			sp.add (dataList);
			add (sp, gbc);
			sendBut.setEnabled (false);
			closeBut.setEnabled (false);
			connectBut.addActionListener (this);
			closeBut.addActionListener (this);
			sendBut.addActionListener (this);
		}

		public void actionPerformed(final ActionEvent e) {
			new Thread() {
				public void run() {
					if (e.getSource() == connectBut) {
						connectBut.setEnabled(false);
						closeBut.setEnabled(true);

						try {
						scon = (StreamConnection)de.avetana.bluetooth.connection.Connector.open (adrField.getText());
						dataList.add ("Connected");
						
						inStream = scon.openInputStream();
						outStream = scon.openOutputStream();
						} catch (Exception e2) {
							e2.printStackTrace();
							dataList.add (e2.getMessage());
						}
						sendBut.setEnabled(true);

						Runnable r = new Runnable() {
							
						public void run() { 

							try {
								while (inStream != null) {
									int len = 0;
									while (inStream.available() > 0) {
										byte b[] = new byte[1000];
										len += inStream.read (b);
									}
									if (len > 0) {
										if (dataList.getItemCount() > 0 && dataList.getItem(dataList.getItemCount() - 1).startsWith("Read ")) {
											String val = dataList.getItem(dataList.getItemCount() - 1);
											len += Integer.parseInt(val.substring(5, val.indexOf(" bytes")));
											dataList.remove(dataList.getItemCount() - 1);
										} 
										dataList.add ("Read " + len + " bytes");
									}
							//System.out.println ("Data received");
									
									Thread.currentThread().sleep(10);
								}
							} catch (Exception e2) { 
									e2.printStackTrace();
									dataList.add (e2.getMessage());
								}
						}
						};
						new Thread(r).start();

					} else if (e.getSource() == closeBut) {
						connectBut.setEnabled (true);
						closeBut.setEnabled (false);
						sendBut.setEnabled (false);
							try {
								if (inStream != null) inStream.close();
								if (outStream != null) outStream.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								dataList.add (e1.getMessage());
							}
							inStream = null;
							outStream = null;
						if (scon != null) try { scon.close(); } catch (IOException  e2) {}
						scon = null;
					} else if (e.getSource() == sendBut) {
						if (outStream != null) {
							try {
								outStream.write (new byte[100]);
								dataList.add ("Sent 100 bytes");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								dataList.add (e1.getMessage());
							}
							
						}
					}
				} 
	
			}.start();
		}
	}

	class L2ClientPanel extends Panel implements ActionListener{
		
		Button connectBut, closeBut, sendBut, pollBut;
		TextField adrField;
		L2CAPConnection scon;
		List dataList = new List();
		
		public L2ClientPanel() {
			setLayout (new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = gbc.gridy = 0;
			gbc.fill = GridBagConstraints.NONE;
			adrField = new TextField (60);
			connectBut = new Button ("Connect");
			closeBut = new Button ("Close");
			sendBut = new Button ("Send");
			pollBut = new Button ("Poll");
			add (connectBut, gbc);
			gbc.gridx++; add (closeBut, gbc);
			gbc.gridx++; add (sendBut, gbc);
			gbc.gridx++; add (pollBut, gbc);
			gbc.gridx = 0; gbc. gridy = 1; gbc.gridwidth = 4;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			add (adrField, gbc);
			gbc.gridy++;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = gbc.weighty = 1;
			ScrollPane sp = new ScrollPane();
			sp.add (dataList);
			add (sp, gbc);
			sendBut.setEnabled (false);
			closeBut.setEnabled (false);
			pollBut.setEnabled (false);
			connectBut.addActionListener (this);
			closeBut.addActionListener (this);
			sendBut.addActionListener (this);
			pollBut.addActionListener (this);
		}

		public void actionPerformed(ActionEvent e) {
		
			if (e.getSource() == connectBut) {
				connectBut.setEnabled(false);
				closeBut.setEnabled(true);

				try {
				scon = (L2CAPConnection)Connector.open (adrField.getText());
				dataList.add ("Connected");
				} catch (Exception e2) {
					e2.printStackTrace();
					dataList.add (e2.getMessage());
				}
				sendBut.setEnabled(true);
				pollBut.setEnabled(true);

			} else if (e.getSource() == closeBut) {
				connectBut.setEnabled (true);
				closeBut.setEnabled (false);
				sendBut.setEnabled (false);
				pollBut.setEnabled (false);
				if (scon != null) try { scon.close(); } catch (IOException e2) {}
				scon = null;
			} else if (e.getSource() == sendBut) {
				if (scon != null) {
					try {
						scon.send (new byte[100]);
						dataList.add ("Sent 100 bytes");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						dataList.add (e1.getMessage());
					}
					
				}
			}  else if (e.getSource() == pollBut) {
				if (scon != null) {
					try {
						if (scon.ready()) {
							int len = scon.receive (new byte[500]);
							if (dataList.getItemCount() > 0 && dataList.getItem(dataList.getItemCount() - 1).startsWith("Read ")) {
								String val = dataList.getItem(dataList.getItemCount() - 1);
								len += Integer.parseInt(val.substring(5, val.indexOf(" bytes")));
								dataList.remove(dataList.getItemCount() - 1);
							} 
							dataList.add ("Read " + len + " bytes");
						} else dataList.add ("No data available");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						dataList.add (e1.getMessage());
					}
					
				}
				
			}
		} 
	}

}
