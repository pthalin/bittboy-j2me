package de.avetana.bluetooth.test.app;

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
* Java representation of a Bluetooth device address.
*
* A Bluetooth device address is comprised of six pairs of hex digits,
* for example <code>00:12:34:56:78:9A</code>.<br>
*
* This class is based upon the <code>bdaddr_t</code> type, as defined in <code>bluetooth.h</code>
* of the BlueZ libraries.
*
* @author Edward Kay, ed.kay@appliancestudio.com
* @author Julien Campana for a few static methods
*/

public class BTAddress
{
        /**
         * The address is stored as six 8-bit numbers.
         */
        public short[] addr_arr = new short[6];

        /**
         * Default constructor
         */
        public BTAddress()
        {
        }
        
        protected BTAddress (BTAddress a2) {
        		addr_arr = a2.addr_arr;
        }

        /**
         * Creates a <code>BTAddress</code> object from the address <code>addr_str</code>. The address string should be in the form "<code>00:12:34:56:78:9A</code>".
         *
         * @param addr_str <code>String</code> representation of the Bluetooth device
         *      address.
         * @exception BTAddressFormatException If the String is not a parsable
         *     Bluetooth address.
         */
        public BTAddress(String addr_str) throws BTAddressFormatException
        {
                this.setValue(addr_str);
        }

        /**
         * Set the Bluetooth device address. Valid string format is "<code>00:12:34:56:78:AB</code>", that is 6, colon separated pairs of hex digits.
         *
         * @param addr_str <code>String</code> representation of the Bluetooth device
         *      address.
         * @exception BTAddressFormatException If the String is not a parsable
         *     Bluetooth address.
         */
        public void setValue(String addr_str) throws BTAddressFormatException
        {
                // Parse the given string into a BTAddress object
                // Valid string format is "00:12:34:56:78:AB"
                // i.e. 6, colon separated pairs of hex digits.
				int pos = -1;
				while ((pos = addr_str.indexOf("-")) != -1) {
					addr_str = addr_str.substring(0, pos) + ":" + addr_str.substring (pos + 1);
				}

				int count = 0;
				pos = -1;
				while ((pos = addr_str.indexOf(":", pos + 1)) != -1) count++;
				
                if (count != 5)
                        throw new BTAddressFormatException("Bad number of tokens");

                int i = 0, lastPos = 0;
                short s;
                String str = new String();
                pos = -1;
                while ((pos = addr_str.indexOf(":", pos + 1)) != -1)
                {
                        str = addr_str.substring (lastPos, pos);
                        lastPos = pos + 1;
                        // Try and parse the (hex) token to a short
                        try
                        {
                                s = Short.parseShort(str, 16);
                        }
                        catch (NumberFormatException nfe)
                        {
                                String msg = "NumberFormatExpection occurred whilst trying to parseShort from token " + str + ".";
                                throw new BTAddressFormatException(msg);
                        }

                        // If successful, set that part of the addr_arr array
                        // and get ready for the next token.
                        addr_arr[i] = s;
                        i++;
                }
                try { 
                	addr_arr[5] = Short.parseShort(addr_str.substring(addr_str.lastIndexOf(':') + 1), 16);
                }
                catch (NumberFormatException nfe)
                {
                        String msg = "NumberFormatExpection occurred whilst trying to parseShort from token " + str + ".";
                        throw new BTAddressFormatException(msg);
                }

                	return;
        }

        /**
         * Returns a String representation of the Bluetooth device address in the
         * form "<code>00:12:34:56:78:9A</code>".
         *
         * @return A String representation of the Bluetooth device address.
         */
        public String toString() {
          return toStringSep(true);
        }

        public String toStringSep(boolean with) {
          StringBuffer buf = with?new StringBuffer(17):new StringBuffer(12);

          String tmp = new String();
          for (int i=0; i<6; i++)
          {
            tmp = Integer.toHexString(addr_arr[i]).toUpperCase();
            // Pad single digits with a leading 0
            if (addr_arr[i] < 16)
              buf.append("0");
            buf.append(tmp);
            if (i < 5 && with)
              buf.append(":");
          }
          return buf.toString();
        }

        public static String transform(String original) throws Exception {
          if(original.length()==17) return original;
          if(original.length()!=12) throw new Exception();
          StringBuffer buf=new StringBuffer(17);
          char[] chArray=original.toCharArray();
          for(int i=0;i<12;i=i+2) {
            buf.append(chArray[i]);
            buf.append(chArray[i+1]);
            if(i!=10) buf.append(':');
          }
          return buf.toString();
        }

        public static BTAddress parseString(String original) throws Exception {
          if(original.length()==17) return new BTAddress(original);
          else return new BTAddress(transform(original));
        }

        /**
         * Compares two BTAddress objects to see if they represent the same Bluetooth device address.
         *
         * @param compare The BTAddress object to compare to this.
         * @return True if the Bluetooth device addresses are equal, otherwise false.
         */
        public boolean equals(BTAddress compare)
        {
                for (int i=0; i<6; i++)
                        if (this.addr_arr[i] != compare.addr_arr[i])
                                return false;

                return true;
        }
        
}
