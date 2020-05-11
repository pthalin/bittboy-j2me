/*
 * Created on 22.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import java.io.*;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.obex.*;



/**
 * @author gmelin
 *
 * 
 */
public class OBEXSendTest implements DiscoveryListener {

	private boolean finishedSearching = false;
	private ServiceRecord rec = null;
	private String bta;
	
	public OBEXSendTest(String bta, String file[], int channel) throws Exception {
        LocalDevice myLocalDevice = LocalDevice.getLocalDevice();
        this.bta = bta;
                
        String url = "";
        
        if (channel == 0) {
	        finishedSearching = false;
	        
	        DiscoveryAgent myDiscoveryAgent = myLocalDevice.getDiscoveryAgent();
	        int trans = myDiscoveryAgent.searchServices(null, new UUID[] { new UUID (0x1105) }, new RemoteDevice (bta), this);
	        System.out.println ("Started service search on " + bta + " transaction " + trans);
	        
	        while (!finishedSearching) {
	        		synchronized (this) {
	        			wait (100);
	        		}
	        }
	        	        
	        if (rec == null) throw new Exception ("No OBEX_OBJECT_PUSH Service found on device " + bta);
	        url = rec.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
        } else url = "btgoep://" + bta + ":" + channel;
        
//        while (true) {
        System.out.println ("Connecting to " + url + " on " + bta);
        ClientSession conn = (ClientSession) Connector 
              .open(url);
        HeaderSet header = conn.createHeaderSet();
        HeaderSet response = conn.connect(header);
        System.out.println("connected");
        for (int i = 0;i < file.length;i++) {
        	File sendFile = new File (file[i]);
            header = conn.createHeaderSet();
            header.setHeader(HeaderSet.NAME, sendFile.getName());
            
            String type = null;
            
            if (file[i].endsWith(".jpg")) type = "image/jpeg";
            else if (file[i].endsWith(".gif")) type = "image/gif";
            else if (file[i].endsWith(".jar")) type = "application/x-java-archive";
            else if (file[i].endsWith(".vcf")) type = "text/x-vcard";
            else if (file[i].endsWith(".midi") || file[i].endsWith(".mid")) type = "audio/x-midi";
            
            if (type != null) header.setHeader(HeaderSet.TYPE, type);
            header.setHeader(HeaderSet.LENGTH, new Long (sendFile.length()));

            Operation op = conn.put(header);

            OutputStream os = op.openOutputStream();
            InputStream is = new FileInputStream (sendFile);
            
            byte[] b = new byte[400];
            int r;
            long ts = System.currentTimeMillis();
            long tot = 0;
            while ((r = is.read(b)) > 0) {
            		os.write(b, 0, r);
            		tot += r;
            		System.out.println("Transmission speed " + ((double)tot / (double)(System.currentTimeMillis() - ts)) + "kb/sec");
            }
            is.close();
            os.close();
            op.close();
        	
        }
        
        conn.disconnect(conn.createHeaderSet());
        conn.close();
        System.out.println ("Connection terminated " + url);
//        }        
	}
	
	/**
	 * start this program with two parameters
	 * param 1 is the BT-Address of the device to send to
	 * param 2 is the full path of the file to send
	 */
	 
	public static void main (final String[] args) throws Exception {
		
		
		//THIS IS ONLY USED TO INITIALIZE THE STACK WHEN THREADS ARE STARTED
		LocalDevice.getLocalDevice();
		
		String files[] = new String[args.length - 1];
		System.arraycopy(args, 1, files, 0, files.length);
		
		new OBEXSendTest (args[0], files, 0);
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		rec = servRecord[0];
		System.out.println ("Service discovered on " + bta + " trans " + transID);
		//System.out.println (rec);
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public void serviceSearchCompleted(int transID, int respCode) {
		finishedSearching = true;
		System.out.println ("Service search completed " + bta + " trans " + transID + " respCode " + respCode);
		
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public void inquiryCompleted(int discType) {
		// TODO Auto-generated method stub
		
	}
}
