/*
 * Created on 01.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import javax.bluetooth.*;
import javax.microedition.io.*;

import de.avetana.bluetooth.sdp.RemoteServiceRecord;
import de.avetana.bluetooth.util.MSServiceRecord;

import java.io.*;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SDPUpdateTest2 {

	private Connection streamConNot;
	private boolean accepting = false;
	private UUID l2cap_protocol = new UUID (0x0100);
	private UUID hid_protocol = new UUID (0x0011);
	private int hid_control_psm   = 0x0011;
	private int hid_interrupt_psm = 0x0013;
	
	public SDPUpdateTest2() throws Exception {
		System.out.println ("Registering Service");



		LocalDevice local = LocalDevice.getLocalDevice();
		local.setDiscoverable(DiscoveryAgent.GIAC);
		StringBuffer url = new StringBuffer ();
		url.append("btl2cap").append("://");
		url.append("localhost").append(":");
		url.append(new UUID (0x1124));
		L2CAPConnectionNotifier server = (L2CAPConnectionNotifier) Connector.open(url.toString());
		ServiceRecord rec = local.getRecord( server );

		//ProtocolDescriptor List
		/*
		{
			DataElement e = new DataElement( DataElement.DATSEQ );
				DataElement e1 = new DataElement( DataElement.DATSEQ );
				e1.addElement( new DataElement( DataElement.UUID, l2cap_protocol ) );
				e1.addElement( new DataElement( DataElement.U_INT_2, hid_control_psm ) );

				DataElement e2 = new DataElement( DataElement.DATSEQ );
				e2.addElement( new DataElement( DataElement.UUID, hid_protocol ) );
			e.addElement( e1 );
			e.addElement( e2 );
			rec.setAttributeValue( 0x0004, e );
		}
		*/
		//LanguageBaseAttributeIDList
		// language : ENglish 
		// encoding : UTF-8
		// location : primary language text are located @0x100
		{
			DataElement e = new DataElement( DataElement.DATSEQ );
			e.addElement( new DataElement( DataElement.U_INT_2, 0x656E ) );
			e.addElement( new DataElement( DataElement.U_INT_2, 106 ) );
			e.addElement( new DataElement( DataElement.U_INT_2, 0x0100 ) );
			rec.setAttributeValue( 0x0006, e );
		}

		// ServiceAvailability
		// fully available
		rec.setAttributeValue( 0x0008, new DataElement( DataElement.U_INT_1, 0xFF ) );

		//BluetoothProfileDescriptorList
		{
			DataElement e = new DataElement( DataElement.DATSEQ );
			e.addElement( new DataElement( DataElement.UUID, hid_protocol ) );
			e.addElement( new DataElement( DataElement.U_INT_2, 0x0100 ) );
			rec.setAttributeValue( 0x0009, e );
		}
		
		//AdditionalProtocolDescriptorList
		{
			DataElement e = new DataElement( DataElement.DATSEQ );
			e.addElement( new DataElement( DataElement.UUID, l2cap_protocol ) );
			e.addElement( new DataElement( DataElement.U_INT_2, hid_interrupt_psm ) );
			e.addElement( new DataElement( DataElement.UUID, hid_protocol ) );
			rec.setAttributeValue( 0x000D, e );
		}
		
		//ServiceName
		rec.setAttributeValue( 0x0100+0x0000, new DataElement( DataElement.STRING, "HID_Debug" ) );

		//ServiceDescription
		rec.setAttributeValue( 0x0100+0x0001, new DataElement( DataElement.STRING, "HID service for debugger" ) );

		//ProviderName
		rec.setAttributeValue( 0x0100+0x0002, new DataElement( DataElement.STRING, "Michel.Hassenforder@uha.fr" ) );

		//*************** HID SPECIFIC SERVICE RECORD ATTRIBUTES *****************

		//HIDDeviceReleaseNumber
		rec.setAttributeValue( 0x0200, new DataElement( DataElement.U_INT_2, 0x0100 ) );

		//HIDParserVersion
		rec.setAttributeValue( 0x0201, new DataElement( DataElement.U_INT_2, 0x0111 ) );

		//HIDDeviceSubclass
		// mouse device
		rec.setAttributeValue( 0x0202, new DataElement( DataElement.U_INT_1, 0x80 ) );

		//HIDCountryCode
		rec.setAttributeValue( 0x0203, new DataElement( DataElement.U_INT_1, 0x00 ) );

		//HIDVirtualCable
		rec.setAttributeValue( 0x0204, new DataElement( true ) );

		//HIDReconnectInitiate
		rec.setAttributeValue( 0x0205, new DataElement( false ) );

		/*
		//HIDDescriptorList
		{
			DataElement e = new DataElement( DataElement.DATSEQ );
			e.addElement( new DataElement( DataElement.U_INT_1, 0x22 ) );
			int [] ReportDescriptor = {
						0x05, 0x01,
						0x09, 0x02,
						0xA1, 0x01,
						0x09, 0x01,
						0xA1, 0x00,
						0x05, 0x01,
						0x09, 0x30,
						0x09, 0x31,
						0x15, 0x81,
						0x25, 0x7F,
						0x75, 0x08,
						0x95, 0x02,
						0x81, 0x06,
						0xC0,
						0x05, 0x09,
						0x19, 0x01,
						0x29, 0x03,
						0x15, 0x00,
						0x25, 0x01,
						0x95, 0x03,
						0x75, 0x01,
						0x81, 0x02,
						0x95, 0x01,
						0x75, 0x05,
						0x81, 0x03,
						0xC0
			};
			e.addElement( new DataElement( DataElement.STRING, toString (ReportDescriptor) ) );
			rec.setAttributeValue( 0x0206, e );
		}
		*/
		
		//HIDLangIDBaseList
		// language : English
		// offset   : 0x100 (same as for universal attributes)
		{ 
			DataElement e = new DataElement( DataElement.DATSEQ );
			e.addElement( new DataElement( DataElement.U_INT_2, 0x0409 ) );
			e.addElement( new DataElement( DataElement.U_INT_2, 0x0100 ) );
			rec.setAttributeValue( 0x0207, e );
		}
		
		//HIDSDPDisable
		rec.setAttributeValue( 0x0208, new DataElement( false ) );

		//HIDBatteryPower
		rec.setAttributeValue( 0x0209, new DataElement( false ) );

		//HIDRemoteWake
		rec.setAttributeValue( 0x020A, new DataElement( false ) );
		
		//HIDProfileVersion
		rec.setAttributeValue( 0x020B, new DataElement( DataElement.U_INT_2, 0x0100 ) );

		//HIDSupervisionTimeout
		rec.setAttributeValue( 0x020C, new DataElement( DataElement.U_INT_2, 0x1f40 ) );

		//HIDNormallyConnectable
//		rec.setAttributeValue( 0x020D, new DataElement( true ) );

		//HIDBootDevice
		rec.setAttributeValue( 0x020E, new DataElement( true ) );
		int ids[] = rec.getAttributeIDs();
		for (int i = 0;i < ids.length;i++)
			System.out.println("Updateing id " + ids[i]);
		LocalDevice.getLocalDevice().updateRecord(rec);
		System.out.println ("Press a key to remove service");
		BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
		br.readLine();
		server.close();
		System.exit(0);

	}
	
	public static void main(String[] args) throws Exception {
		new SDPUpdateTest2();
	}
}
