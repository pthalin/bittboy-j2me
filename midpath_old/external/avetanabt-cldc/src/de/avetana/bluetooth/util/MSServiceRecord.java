package de.avetana.bluetooth.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class MSServiceRecord {

	public static byte[] getByteArray(ServiceRecord record) throws IOException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			DataElement base = new DataElement(DataElement.DATSEQ);
			int ids[] = record.getAttributeIDs();
			for (int i = 0; i < ids.length; i++) {
				if (ids[i] == 0)
					continue;
				base.addElement(new DataElement(DataElement.U_INT_2, ids[i]));
				base.addElement(record.getAttributeValue(ids[i]));
			}

			writeElement(base, bos);

			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void writeLong(long l, int size, OutputStream os) throws IOException {
		for (int i = 0; i < size; i++) {
			os.write((int) (l >> (size - 1 << 3)));
			l <<= 8;
		}
	}

	private static void writeBytes(byte[] b, OutputStream os) throws IOException {
		for (int i = 0; i < b.length; i++)
			os.write(b[i]);
	}

	private static int getLength(DataElement e) {
		switch (e.getDataType()) {
		case DataElement.NULL:
			return 1;

		case DataElement.U_INT_1:
			return 2;
		case DataElement.U_INT_2:
			return 3;
		case DataElement.U_INT_4:
			return 5;
		case DataElement.U_INT_8:
			return 9;
		case DataElement.U_INT_16:
			return 17;

		case DataElement.INT_1:
			return 2;
		case DataElement.INT_2:
			return 3;
		case DataElement.INT_4:
			return 5;
		case DataElement.INT_8:
			return 9;
		case DataElement.INT_16:
			return 17;

		case DataElement.UUID:
			return 17;

		case DataElement.STRING:
		case DataElement.URL: {
			byte[] b = ((String) e.getValue()).getBytes();

			if (b.length < 0x100)
				return b.length + 2;
			else if (b.length < 0x10000)
				return b.length + 3;
			else
				return b.length + 5;
		}

		case DataElement.BOOL:
			return 2;

		case DataElement.DATSEQ:
		case DataElement.DATALT: {
			int result = 5;

			for (Enumeration en = (Enumeration) e.getValue(); en.hasMoreElements();)
				result += getLength((DataElement) en.nextElement());

			return result;
		}

		default:
			throw new IllegalArgumentException();
		}
	}

	private static void writeElement(DataElement e, OutputStream bos) throws IOException {
		switch (e.getDataType()) {
		case DataElement.NULL:
			bos.write(0 | 0);
			break;

		case DataElement.U_INT_1:
			bos.write(8 | 0);
			writeLong(e.getLong(), 1, bos);
			break;
		case DataElement.U_INT_2:
			bos.write(8 | 1);
			writeLong(e.getLong(), 2, bos);
			break;
		case DataElement.U_INT_4:
			bos.write(8 | 2);
			writeLong(e.getLong(), 4, bos);
			break;
		case DataElement.U_INT_8:
			bos.write(8 | 3);
			writeBytes((byte[]) e.getValue(), bos);
			break;
		case DataElement.U_INT_16:
			bos.write(8 | 4);
			writeBytes((byte[]) e.getValue(), bos);
			break;

		case DataElement.INT_1:
			bos.write(16 | 0);
			writeLong(e.getLong(), 1, bos);
			break;
		case DataElement.INT_2:
			bos.write(16 | 1);
			writeLong(e.getLong(), 2, bos);
			break;
		case DataElement.INT_4:
			bos.write(16 | 2);
			writeLong(e.getLong(), 4, bos);
			break;
		case DataElement.INT_8:
			bos.write(16 | 3);
			writeLong(e.getLong(), 8, bos);
			break;
		case DataElement.INT_16:
			bos.write(16 | 4);
			writeBytes((byte[]) e.getValue(), bos);
			break;

		case DataElement.UUID:
			bos.write(24 | 4);
			String stringValue = ((UUID) e.getValue()).toString();
			byte[] uuidValue = new byte[16];

			for (int i = 0; i < 16; i++)
				uuidValue[i] = (byte) Integer.parseInt(stringValue.substring(i * 2, i * 2 + 2), 16);

			writeBytes(uuidValue, bos);
			break;

		case DataElement.STRING: {
			byte[] b = ((String) e.getValue()).getBytes();

			if (b.length < 0x100) {
				bos.write(32 | 5);
				writeLong(b.length, 1, bos);
			} else if (b.length < 0x10000) {
				bos.write(32 | 6);
				writeLong(b.length, 2, bos);
			} else {
				bos.write(32 | 7);
				writeLong(b.length, 4, bos);
			}

			writeBytes(b, bos);
			break;
		}

		case DataElement.BOOL:
			bos.write(40 | 0);
			writeLong(e.getBoolean() ? 1 : 0, 1, bos);
			break;

		case DataElement.DATSEQ:
			bos.write(48 | 7);
			writeLong(getLength(e) - 5, 4, bos);

			for (Enumeration en = (Enumeration) e.getValue(); en.hasMoreElements();)
				writeElement((DataElement) en.nextElement(), bos);

			break;
		case DataElement.DATALT:
			bos.write(56 | 7);
			writeLong(getLength(e) - 5, 4, bos);

			for (Enumeration en = (Enumeration) e.getValue(); en.hasMoreElements();)
				writeElement((DataElement) en.nextElement(), bos);

			break;

		case DataElement.URL: {
			byte[] b = ((String) e.getValue()).getBytes();

			if (b.length < 0x100) {
				bos.write(64 | 5);
				writeLong(b.length, 1, bos);
			} else if (b.length < 0x10000) {
				bos.write(64 | 6);
				writeLong(b.length, 2, bos);
			} else {
				bos.write(64 | 7);
				writeLong(b.length, 4, bos);
			}

			writeBytes(b, bos);
			break;
		}

		default:
			throw new IOException();
		}
	}
}
