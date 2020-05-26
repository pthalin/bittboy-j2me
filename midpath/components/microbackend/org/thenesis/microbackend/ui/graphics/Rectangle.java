/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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
 */
package org.thenesis.microbackend.ui.graphics;

class Rectangle {
	
	public int xmin, ymin, xmax, ymax, w, h;
	
	public void set(int x0, int y0, int x1, int y1) {
		xmin=x0; ymin=y0; xmax=x1; ymax=y1; 
		w = xmax - xmin;
		h = ymax - ymin;
	}
	
	public void set(final Point p0, final Point p1) {
		xmin=p0.x; ymin=p0.y; xmax=p1.x; ymax=p1.y;
	}
	
	public void copy(final Rectangle r) {
		xmin=r.xmin; ymin=r.ymin; xmax=r.xmax; ymax=r.ymax;
	}
	
	public boolean check() {
		return ((xmin>xmax) || (ymin>ymax));
	}
	
	public void reorder() {
		int t;
		if (xmin>xmax) { t=xmin; xmin=xmax; xmax=t; }
		if (ymin>ymax) { t=ymin; ymin=ymax; ymax=t; }		
	}
	
	public void translate(int dx, int dy) {
		xmin+=dx; ymin+=dy; xmax+=dx; ymax+=dy;
	}
	
	public void merge(int x, int y) {
		if (x<xmin) xmin=x;
		if (x>xmax) xmax=x;
		if (y<ymin) ymin=y;
		if (y>ymax) ymax=y;
	}
	
	public void merge(final Point p) {
		if (p.x<xmin) xmin=p.x;
		if (p.x>xmax) xmax=p.x;
		if (p.y<ymin) ymin=p.y;
		if (p.y>ymax) ymax=p.y;		
	}
	
	public void merge(final Rectangle r) {
		if (r.xmin<xmin) xmin=r.xmin;
		if (r.xmax>xmax) xmax=r.xmax;
		if (r.ymin<ymin) ymin=r.ymin;
		if (r.ymax>ymax) ymax=r.ymax;		
	}
	
	public void intersect(final Rectangle r) {
		if (r.xmin>xmin) xmin=r.xmin;
		if (r.ymin>ymin) ymin=r.ymin;
		if (r.xmax<xmax) xmax=r.xmax;
		if (r.ymax>ymax) ymax=r.ymax;
	}
	
	public boolean isInside(int x, int y) {
		return (x>=xmin) && (x<=xmax) && (y>=ymin) && (y<=ymax);
	}
	
	public String toString() {
		return "xmin=" + xmin + " xmax=" + xmax + " ymin=" + ymin + " ymax=" + ymax;
	}
}
