/*
 * MIDPath - Copyright (C) 2006-2007 Guillaume Legris, Mathieu Legris
 * 
 * com.sixlegs.image.png - Java package to read and display PNG images
 * Copyright (C) 1998-2004 Chris Nokleberg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
/**************************************************************************
* Copyright (c) 2001, 2002, 2003 by Acunia N.V. All rights reserved.      *
*                                                                         *
* This software is copyrighted by and is the sole property of Acunia N.V. *
* and its licensors, if any. All rights, title, ownership, or other       *
* interests in the software remain the property of Acunia N.V. and its    *
* licensors, if any.                                                      *
*                                                                         *
* This software may only be used in accordance with the corresponding     *
* license agreement. Any unauthorized use, duplication, transmission,     *
*  distribution or disclosure of this software is expressly forbidden.    *
*                                                                         *
* This Copyright notice may not be removed or modified without prior      *
* written consent of Acunia N.V.                                          *
*                                                                         *
* Acunia N.V. reserves the right to modify this software without notice.  *
*                                                                         *
*   Acunia N.V.                                                           *
*   Philips-site 5, box 3       info@acunia.com                           *
*   3001 Leuven                 http://www.acunia.com                     *
*   Belgium - EUROPE                                                      *
**************************************************************************/
package org.thenesis.microbackend.ui.image.png;

public class DirectColorModel extends ColorModel {

  private int rmask;
  private int gmask;
  private int bmask;
  private int amask;

  private int rmaskbits;
  private int gmaskbits;
  private int bmaskbits;
  private int amaskbits;

  private int rmaskpos;
  private int gmaskpos;
  private int bmaskpos;
  private int amaskpos;

  public DirectColorModel(int bits, int rmask, int gmask, int bmask) {
    this(bits, rmask, gmask, bmask, 0);
  }
  
  public DirectColorModel(int bits, int rmask, int gmask, int bmask, int amask) {
    super(bits);
    this.rmask = rmask;
    this.gmask = gmask;
    this.bmask = bmask;
    this.amask = amask;

    if(rmask != 0) {
      rmaskpos = getPos(rmask);
      rmaskbits = getBits(rmask, rmaskpos);
    }
    
    if(gmask != 0) {
      gmaskpos = getPos(gmask);
      gmaskbits = getBits(gmask, gmaskpos);
    }
    
    if(bmask != 0) {
      bmaskpos = getPos(bmask);
      bmaskbits = getBits(bmask, bmaskpos);
    }
    
    if(amask != 0) {
      amaskpos = getPos(amask);
      amaskbits = getBits(amask, amaskpos);
    }
    
  }

  private int getPos(int val) {
    int result = 0;
    while((val & 1) == 0) {
      val = val >>> 1;
      result++;
    }
    return result;
  }

  private int getBits(int val, int pos) {
    int result = 0;
    val = val >>> pos;
    while((val & 1) == 1) {
      val = val >>> 1;
      result++;
    }
    return result;
  }
  
  final public int getAlpha(int pixelValue) {
    if(amask == 0) return 255;
    return (((pixelValue & amask) >>> amaskpos) << 8) >>> amaskbits;
  }
  
  final public int getAlphaMask() {
    return amask;
  }
  
  final public int getRed(int pixelValue) {
    if(rmask == 0) return 0;
    return (((pixelValue & rmask) >>> rmaskpos) << 8) >>> rmaskbits;
  }
  
  final public int getRedMask() {
    return rmask;
  }
  
  final public int getGreen(int pixelValue) {
    if(gmask == 0) return 0;
    return (((pixelValue & gmask) >>> gmaskpos) << 8) >>> gmaskbits;
  }

  final public int getGreenMask() {
    return gmask;
  }

  final public int getBlue(int pixelValue) {
    if(bmask == 0) return 0;
    return (((pixelValue & bmask) >>> bmaskpos) << 8) >>> bmaskbits;
  }

  final public int getBlueMask() {
    return bmask;
  }

  public final int getRGB(int pixelValue) {
    return (getAlpha(pixelValue) << 24) | (getRed(pixelValue) << 16) | (getGreen(pixelValue) << 8) | (getBlue(pixelValue));
  }
  
}

