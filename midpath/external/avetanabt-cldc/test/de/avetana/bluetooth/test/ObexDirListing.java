/**
 * Created on 11.01.2005
 *
 * Example program to get the root directory listing from 
 * an OBEX FileTransfer Service
 * 
 * @author Ruediger Mosig
 */

package de.avetana.bluetooth.test;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;

import de.avetana.bluetooth.connection.Connector;

public class ObexDirListing {

   public ObexDirListing() {
      super();
   }

   public static void main(String args[]) {
      ObexDirListing myDebugger = new ObexDirListing();
      myDebugger.obexS55();
   }

   public void obexS55() {
      try {
         LocalDevice myLocalDevice = LocalDevice.getLocalDevice();
         DiscoveryAgent myDiscoveryAgent = myLocalDevice.getDiscoveryAgent();
 //       p910 000E0799107C:3
//		s55 0001E304A323:0005
         // 6600 000E6D7057F9:10
         //Win  0003C934DA55:2
         
         //Change the URL to reflect the OBEX File Transfer service on your target device.
         
         ClientSession conn = (ClientSession) Connector 
               .open("btgoep://0003C934DA55:2;authenticate=false;encrypt=false;master=false");
         HeaderSet header = conn.createHeaderSet();
         byte[] uuidBrowsing = hexToByte("f9ec7bc4953c11d2984e525400dc9e09");
         header.setHeader(HeaderSet.TARGET, uuidBrowsing);
         HeaderSet response = conn.connect(header);
         System.out.println("connected");
         header = conn.createHeaderSet();
         header.setHeader(HeaderSet.NAME, ""); // empty => root folder
         HeaderSet result = conn.setPath(header, false, false);
         System.out.println("set to root folder");
         header = conn.createHeaderSet();
         // null-terminating character \u0000 does not harm S55 communication
         header.setHeader(HeaderSet.TYPE, "x-obex/folder-listing\u0000");
         Operation op = conn.get(header);

         // Read data into a String and close the InputStream
         InputStream input = op.openInputStream();
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         int data = input.read();
         while (data != -1) {
            out.write(data);
            data = input.read();
         }
         input.close();
         String xmlString = out.toString();
         System.out.println("Obex XML String:\n" + xmlString);
         conn.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private byte[] hexToByte(String hexStr) {
      byte[] bts = new byte[hexStr.length() / 2];
      for (int i = 0; i < bts.length; i++) {
         bts[i] = (byte) Integer.parseInt(
               hexStr.substring(2 * i, 2 * i + 2), 16);
      }
      return bts;
   }

}