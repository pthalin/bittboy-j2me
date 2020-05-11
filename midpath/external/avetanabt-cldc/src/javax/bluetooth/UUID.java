/**
 *  (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED.
 *
 * This file is part of the Avetana bluetooth API for Linux.
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.
 *
 * This class was originally part of the JavaBluetooth stack project written by Christian Lorenz.
 * Some features have been added (like the 128 bits UUID).
 *
 * @author Julien Campana
 */

package javax.bluetooth;

/**
 * The <code>UUID</code> class defines universally unique identifiers. These 128-bit unsigned integers are guaranteed
 * to be unique across all time and space. Accordingly, an instance of this class is immutable.
 * The Bluetooth specification provides an algorithm describing how a
 * 16-bit or 32-bit UUID could be promoted to a 128-bit UUID. Accordingly, this class provides an interface that assists
 * applications in creating 16-bit, 32-bit, and 128-bit long UUIDs. The
 * methods supported by this class allow equality testing of two UUID objects. <p> The Bluetooth Assigned Numbers document (<A
 * HREF="http://www.bluetooth.org/assigned-numbers/sdp.htm"> http://www.bluetooth.org/assigned-numbers/sdp.htm</A>)
 * defines a large number of UUIDs for protocols and service classes.
 * The table below provides a short list of the most common UUIDs defined in the Bluetooth Assigned Numbers document. <TABLE>
 * <TR><TH>Name</TH><TH>Value</TH><TH>Size</TH></TR> <TR><TD>Base UUID Value (Used in promoting 16-bit and 32-bit UUIDs to
 * 128-bit UUIDs)</TD><TD>0x0000000000001000800000805F9B34FB</TD> <TD>128-bit</TD></TR>
 * <TR><TD>SDP</TD><TD>0x0001</TD><TD>16-bit</TD></TR> <TR><TD>RFCOMM</TD><TD>0x0003</TD><TD>16-bit</TD></TR>
 * <TR><TD>OBEX</TD><TD>0x0008</TD><TD>16-bit</TD></TR> <TR><TD>HTTP</TD><TD>0x000C</TD><TD>16-bit</TD></TR>
 * <TR><TD>L2CAP</TD><TD>0x0100</TD><TD>16-bit</TD></TR> <TR><TD>BNEP</TD><TD>0x000F</TD><TD>16-bit</TD></TR>
 * <TR><TD>Serial Port</TD><TD>0x1101</TD><TD>16-bit</TD></TR> <TR><TD>ServiceDiscoveryServerServiceClassID</TD><TD>0x1000</TD>
 * <TD>16-bit</TD></TR> <TR><TD>BrowseGroupDescriptorServiceClassID</TD><TD>0x1001</TD> <TD>16-bit</TD></TR>
 * <TR><TD>PublicBrowseGroup</TD><TD>0x1002</TD><TD>16-bit</TD></TR> <TR><TD>OBEX Object Push
 * Profile</TD><TD>0x1105</TD><TD>16-bit</TD></TR> <TR><TD>OBEX File Transfer Profile</TD><TD>0x1106</TD><TD>16-bit</TD></TR>
 * <TR><TD>Personal Area Networking User</TD><TD>0x1115</TD> <TD>16-bit</TD></TR>
 * <TR><TD>Network Access Point</TD><TD>0x1116</TD><TD>16-bit</TD></TR>
 * <TR><TD>Group Network</TD><TD>0x1117</TD><TD>16-bit</TD></TR> </TABLE>
 * @author Christian Lorenz / Julien Campana
 */
public class UUID {
	private String uuidString;
	private byte[] uuidBytes;
	private long uuidLong;
	public static final String baseUUID = "0000000000001000800000805F9B34FB";
	public static final UUID NULL_UUID = new UUID(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
	private static byte[] bUUIDBytes;

	public UUID() {

	}

	/**
	 * Christian Lorenz: Added this for use with DataElement implementation.
	 * @param uuidBytes
	 */
	public UUID(byte[] uuidBytes) {
		this.uuidBytes = uuidBytes;
		if (uuidBytes.length == 4 || uuidBytes.length == 16) {
			this.uuidLong = (long) (((long) uuidBytes[0] & 0xff) << 24 | ((long) uuidBytes[1] & 0xff) << 16
					| ((long) uuidBytes[2] & 0xff) << 8 | ((long) uuidBytes[3] & 0xff));
		} else if (uuidBytes.length == 2) {
			this.uuidLong = (long) (((long) uuidBytes[0] & 0xff) << 8 | ((long) uuidBytes[1] & 0xff));
		} else
			throw new IllegalArgumentException("uuidValue is not in range.");

	}

	/**
	 * Creates a <code>UUID</code> object from <code>long</code> value <code>uuidValue</code>. A UUID
	 * is defined as an unsigned integer whose value can range from
	 * [0 to 2<sup>128</sup>-1]. However, this constructor allows only
	 * those values that are in the range of [0 to 2<sup>32</sup> -1].
	 * Negative values and values in the range of [2<sup>32</sup>, 2<sup>63</sup> -1] are not
	 * allowed and will cause an <code>IllegalArgumentException</code> to be thrown.
	 * @param uuidValue the 16-bit or 32-bit value of the UUID
	 * @exception IllegalArgumentException if <code>uuidValue</code> is not in the range [0, 2<sup>32</sup> -1]
	 */
	public UUID(long uuidValue) throws IllegalArgumentException {
		if (uuidValue < 0)
			throw new IllegalArgumentException("UUID must be in the range [0, 2puis32]!");
		uuidLong = uuidValue;
		if (uuidValue > 0xFFFF) {
			uuidBytes = new byte[4];
			uuidBytes[0] = (byte) ((uuidValue >> 24) & 0xff);
			uuidBytes[1] = (byte) ((uuidValue >> 16) & 0xff);
			uuidBytes[2] = (byte) ((uuidValue >> 8) & 0xff);
			uuidBytes[3] = (byte) (uuidValue & 0xff);
		} else {
			uuidBytes = new byte[2];
			uuidBytes[0] = (byte) ((uuidValue >> 8) & 0xff);
			uuidBytes[1] = (byte) (uuidValue & 0xff);
		}
		//TODO throw new IllegalArgumentException("uuidValue is not in range.");
	}

	private static byte[] getBaseUUIDAsBytes() {
		if (bUUIDBytes == null) {
			bUUIDBytes = new byte[16];
			char[] toCharr = baseUUID.toCharArray();
			for (int i = 0; i < bUUIDBytes.length; i++) {
				try {
					char[] tmp = new char[2];
					tmp[0] = toCharr[i * 2];
					tmp[1] = toCharr[(i + 1) * 2 - 1];
					bUUIDBytes[i] = (byte) Short.parseShort(new String(tmp), 16);
				} catch (Exception ex) {
					throw new IllegalArgumentException();
				}
			}
		}
		return bUUIDBytes;
	}

	/*  End of the constructor method   */

	/**
	 * Creates a <code>UUID</code> object from the string provided.  The
	 * characters in the string must be from the hexadecimal set [0-9,
	 * a-f, A-F].  It is important to note that the prefix "0x" generally
	 * used for hex representation of numbers is not allowed. If the
	 * string does not have characters from the hexadecimal set, an
	 * exception will be thrown. The string length has to be positive
	 * and less than or equal to 32. A string length that exceeds 32 is
	 * illegal and will cause an exception. Finally, a <code>null</code> input
	 * is also considered illegal and causes an exception. <P>
	 * If <code>shortUUID</code> is <code>true</code>, <code>uuidValue</code>
	 * represents a 16-bit or 32-bit UUID.  If <code>uuidValue</code> is in
	 * the range 0x0000 to 0xFFFF then this constructor will create a 16-bit UUID.  If <code>uuidValue</code> is in the range
	 * 0x000010000 to 0xFFFFFFFF, then this constructor will create
	 * a 32-bit UUID.  Therefore, <code>uuidValue</code> may only be 8 characters long. <P>
	 * On the other hand, if <code>shortUUID</code> is <code>false</code>, then
	 * <code>uuidValue</code> represents a 128-bit UUID.  Therefore, <code>uuidValue</code> may only be 32 character long
	 * @param uuidValue the string representation of a 16-bit, 32-bit or 128-bit UUID
	 * @param shortUUID indicates the size of the UUID to be constructed; <code>true</code> is used to indicate short UUIDs,
	 * i.e. either 16-bit or 32-bit; <code>false</code> indicates an 128-bit UUID
	 * @exception NumberFormatException if <code>uuidValue</code>
	 * has characters that are not defined in the hexadecimal set [0-9, a-f, A-F]
	 * @exception IllegalArgumentException if <code>uuidValue</code>
	 * length is zero; if <code>shortUUID</code> is <code>true</code>
	 * and <code>uuidValue</code>'s length is  greater than 8; if <code>shortUUID</code> is <code>false</code> and
	 * <code>uuidValue</code>'s length is greater than 32
	 * @exception NullPointerException if <code>uuidValue</code> is <code>null</code>
	 */
	public UUID(String uuidValue, boolean shortUUID) throws NullPointerException, IllegalArgumentException {
		if (uuidValue == null)
			throw new NullPointerException("uuidValue is null.");
		if (shortUUID) {
			uuidLong = Long.parseLong(uuidValue, 16); //also throws a number format exception
			uuidBytes = new byte[4];
			uuidBytes[0] = (byte) ((uuidLong >> 24) & 0xff);
			uuidBytes[1] = (byte) ((uuidLong >> 16) & 0xff);
			uuidBytes[2] = (byte) ((uuidLong >> 8) & 0xff);
			uuidBytes[3] = (byte) (uuidLong & 0xff);
		} else {
			if (uuidValue.startsWith("0x"))
				uuidValue = uuidValue.substring(2);
			if (uuidValue.length() != 32)
				throw new IllegalArgumentException("A 128-bits UUID must be a 32 character long String!!");
			uuidLong = Long.parseLong(uuidValue.substring(0, 8), 16);
			uuidBytes = new byte[16];
			char[] toCharr = uuidValue.toCharArray();
			for (int i = 0; i < uuidBytes.length; i++) {
				try {
					char[] tmp = new char[2];
					tmp[0] = toCharr[i * 2];
					tmp[1] = toCharr[(i + 1) * 2 - 1];
					uuidBytes[i] = (byte) Short.parseShort(new String(tmp), 16);
				} catch (Exception ex) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/*  End of the constructor method   */

	/**
	 * Returns the string representation of the 128-bit UUID object. The string being returned represents a UUID
	 * that contains characters from the hexadecimal set, [0-9, A-F]. It does not include the prefix "0x" that is generally
	 * used for hex representation of numbers. The return value will never be <code>null</code>.
	 * @return the string representation of the UUID
	 */
	public String toString() {
		if (uuidString == null || uuidString.equals("")) {
			uuidString = "";
			byte[] b = null;
			b = convert32to128(this).toByteArray();
			for (int i = 0; i < b.length; i++) {
				String tmp = Integer.toHexString((int) (b[i] & 0xff));
				while (tmp.length() != 2)
					tmp = "0" + tmp;
				uuidString += tmp;
			}
			uuidString = uuidString.toUpperCase();
		}
		return uuidString;
	}

	public String to32bitsString() {
		byte b[];
		if (uuidBytes.length == 4 || uuidBytes.length == 2)
			b = uuidBytes;
		else {
			b = new byte[4];
			System.arraycopy(uuidBytes, 4, b, 0, 4);
		}
		String myStr = "";
		for (int i = 0; i < b.length; i++) {
			String tmp = Integer.toHexString((int) (b[i] & 0xff));
			while (tmp.length() != 2)
				tmp = "0" + tmp;
			myStr += tmp;
		}
		return myStr;
	}

	public static UUID convert32to128(UUID uuid) {
		byte[] b = uuid.toByteArray();
		UUID retour = new UUID(b);
		if (b.length == 16)
			return retour;
		retour.uuidBytes = new byte[16];
		System.arraycopy(getBaseUUIDAsBytes(), 0, retour.uuidBytes, 0, 16);
		retour.uuidString = "";
		retour.uuidLong = 0;
		int decal = b.length == 2 ? 2 : 0;
		for (int i = 0; i < b.length; i++) {
			retour.uuidBytes[i + decal] += b[i];
		}
		return retour;
	}

	public static UUID convert16to32(UUID uuid) throws Exception {
		byte b[] = uuid.toByteArray();
		if (b.length != 2)
			throw new Exception("Not a 16-bits UUID!");
		UUID retour = new UUID();
		retour.uuidBytes = new byte[] { 0, 0, 0, 0 };
		for (int i = 2; i < 4; i++)
			retour.uuidBytes[i] += b[i - 2];
		retour.uuidLong = uuid.uuidLong;
		retour.uuidString = null;
		return retour;

	}

	/*  End of the method toString  */

	/** Christian Lorenz: Added this for use with DataElement implementation. */
	public byte[] toByteArray128() throws Exception { //TODO handle uuids constructed from strings
		if (uuidBytes.length == 2)
			return convert32to128(convert16to32(this)).toByteArray();
		else if (uuidBytes.length == 4)
			return convert32to128(this).toByteArray();
		else
			return uuidBytes;
	}

	public byte[] toByteArray() {
		return uuidBytes;
	}

	/** Christian Lorenz: Added this for use with equals(). */
	public long toLong() { //TODO handle uuids constructed from strings
		return uuidLong;
	}

	/**
	 * Determines if two <code>UUID</code>s are equal.  They are equal
	 * if their 128 bit values are the same. This method will return <code>false</code> if <code>value</code> is
	 * <code>null</code> or is not a <code>UUID</code> object.
	 * @param value the object to compare to
	 * @return <code>true</code> if the 128 bit values of the two objects are equal, otherwise <code>false</code>
	 */
	public boolean equals(Object value) {
		if (!(value instanceof UUID))
			return false;
		byte b1[] = convert32to128(this).toByteArray();
		byte b2[] = convert32to128(((UUID) value)).toByteArray();
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i])
				return false;
		}
		return true;
	}

	public boolean equals(UUID other) {
		try {
			byte[] b1 = other.toByteArray128();
			byte[] b2 = this.toByteArray128();
			for (int i = 0; i < b1.length; i++)
				if (b1[i] != b2[i])
					return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/* End of the method equals */

	/**
	 * Computes the hash code for this object. This method retains the same semantic contract as defined in
	 * the class <code>java.lang.Object</code> while overriding the implementation.
	 * @return the hash code for this object
	 */
	public int hashCode() {
		return (int) uuidLong;
	}

	/*  End of the method hashCode  */
}

/*  End of the class UUID definition. */

