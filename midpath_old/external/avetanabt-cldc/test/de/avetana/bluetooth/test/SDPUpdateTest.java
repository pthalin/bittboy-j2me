/*
 * Created on 01.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import de.avetana.bluetooth.sdp.RemoteServiceRecord;
import de.avetana.bluetooth.util.MSServiceRecord;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SDPUpdateTest {

	private Connection streamConNot;
	private boolean accepting = false;
	
	public SDPUpdateTest() throws Exception {
		System.out.println ("Registering Service");
		streamConNot = (Connection)Connector.open("btgoep://localhost:" + new UUID (0x456) + ";name=Testname");

		ServiceRecord srec = LocalDevice.getLocalDevice().getRecord(streamConNot);
		
			byte[] raw = MSServiceRecord.getByteArray (srec);
			for (int j = 0; j < raw.length;j++) {
				System.out.print (" " + Integer.toHexString(raw[j] & 0xff));
			}
			System.out.println();

			ServiceRecord srs2 = RemoteServiceRecord.createServiceRecord("000000000000", new byte[0][0], new int[] { 1,2,3,4,5,6,7,256 }, raw);
			System.out.println (srs2);

		System.out.println ("Press a key to update service name... "  + srec);

		BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
		br.readLine();
		//srec.setAttributeValue(0x100, new DataElement (DataElement.STRING, "UpdatedName2"));
		DataElement testEl = new DataElement (DataElement.DATSEQ);
		testEl.addElement(new DataElement (DataElement.U_INT_2, 0x1234));
		testEl.addElement(new DataElement (DataElement.UUID, new UUID (0x1234)));
		srec.setAttributeValue(0x230, testEl);
		srec.setAttributeValue(0x201, new DataElement (DataElement.U_INT_1, 1));
		srec.setAttributeValue(0x202, new DataElement (DataElement.U_INT_2, 2));
		srec.setAttributeValue(0x203, new DataElement (DataElement.U_INT_4, 4));
		srec.setAttributeValue(0x204, new DataElement (DataElement.U_INT_8, new byte[] { 1,2,3,4,5,6,7,8 }));
		srec.setAttributeValue(0x205, new DataElement (DataElement.U_INT_16, new byte[] { 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16 }));
		srec.setAttributeValue(0x211, new DataElement (DataElement.INT_1, 11));
		srec.setAttributeValue(0x303, new DataElement (DataElement.U_INT_2, 256));
		srec.setAttributeValue(0x213, new DataElement (DataElement.INT_4, 44));
		srec.setAttributeValue(0x214, new DataElement (DataElement.INT_8, new byte[] { 11,22,33,44,55,66,77,88 }));
		srec.setAttributeValue(0x215, new DataElement (DataElement.INT_16,new byte[] { 11,22,33,44,55,66,77,88,19,101,111,112,113,114,115,116 }));
		srec.setAttributeValue(0x100, new DataElement (DataElement.STRING, "updated"));
//		srec.setAttributeValue(0x216, new DataElement (DataElement.STRING, "xxx"));

		 
        final short ATTR_SVCINFO_TTL = 0x008;
        srec.setAttributeValue(ATTR_SVCINFO_TTL, 
                new DataElement(DataElement.U_INT_1, 123)); // Seconds

		
		//srec.setAttributeValue(0x217, new DataElement (true));
		//srec.setAttributeValue(0x218, new DataElement (false));
		srec.setAttributeValue(0x217, new DataElement (DataElement.URL, "http://Test"));
	    
		//DataElement testEl = new DataElement (DataElement.U_INT_2, 0x1234);
		//testEl.addElement();
		LocalDevice.getLocalDevice().updateRecord(srec);
		System.out.println ("Press a key to remove service");
		br.readLine();
		streamConNot.close();
		while (accepting) { synchronized (this) { wait (1000); } }
		System.exit(0);

	}
	
	public static void main(String[] args) throws Exception {
		new SDPUpdateTest();
	}
}
