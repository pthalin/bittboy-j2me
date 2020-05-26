/*
 * MIDPath - Copyright (C) 2006 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions. 
 */
package com.sun.midp.l10n;

import java.io.InputStream;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.sun.midp.log.Logging;

abstract class LocalizedStringsBase {
	
	private static Vector list;
	
   String getContent(int index) {
    	
	   String value = null;
	   
    	if (list == null) {
    		list = new Vector();
    		try {
				parseXml(list);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	value = ((Item)list.elementAt(index)).value;
    	
    	if (Logging.TRACE_ENABLED)
    		System.out.println("[DEBUG] LocalizedStringsBase.getContent(): " + index + "  " + ((Item)list.elementAt(index)).value);
    	
    	return value;
    }
    
    public void parseXml(Vector list) throws Exception {
    	
    	KXmlParser parser = new KXmlParser();

		InputStream is = LocalizedStringsBase.class.getResourceAsStream("/com/sun/midp/configuration/l10n/en-US.xml");
		parser.setInput(is, "ISO-8859-1");

		//		parser.relaxed = true;
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "configuration");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "localized_strings");
		//parser.next();

		while (parser.nextTag () != XmlPullParser.END_TAG) {
			parser.require(XmlPullParser.START_TAG, null, "localized_string");
			String key = parser.getAttributeValue(null, "Key");
			String value = parser.getAttributeValue(null, "Value");
			
			Item item = new Item(key, value);
			list.addElement(item);
			
			parser.nextTag ();
			//System.out.println(key + " " + value);
		}
		
		parser.require(XmlPullParser.END_TAG, null, "localized_strings");

	}
    
    public class Item {
    	
    	String key;
    	String value;
    	
    	public Item(String key, String value) {
    		this.key = key;
    		this.value = value;
    	}
    	
    }
    
}
