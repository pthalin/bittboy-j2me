package sdljava.video;
/**
 *  sdljava - a java binding to the SDL API
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */



/**
 * Holds gamma translation look table values.
 *
 * @author Ivan Z. Ganza
 * @version $Id: GammaTable.java,v 1.4 2005/09/03 19:24:03 ivan_ganza Exp $
 */
public class GammaTable {
	
	int[] redTable;
	int[] greenTable;
	int[] blueTable;
	
	public GammaTable(int[] red, int[] green, int[] blue) {
		redTable   = red;
		greenTable = green;
		blueTable  = blue;
	}
	
	int[] getRedTable() {
		return redTable;
	}
	
	int[] getGreenTable() {
		return greenTable;
	}
	
	int[] getBlueTable() {
		return blueTable;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("GammaTable[redTable=");
		
//		for (int i = 0; i < redTable.length; i++) {
//		buf.append("(").append(i).append("=").append(redTable[i]).append(")");
//		}
//		
//		buf.append(", greenTable=");
//		for (int i = 0; i < greenTable.length; i++) {
//		buf.append("(").append(i).append("=").append(greenTable[i]).append(")");
//		}
//		
//		buf.append(", blueTable=");
//		for (int i = 0; i < blueTable.length; i++) {
//		buf.append("(").append(i).append("=").append(blueTable[i]).append(")");
//		}
		
		buf.append("]");
		
		return buf.toString();
	}
}