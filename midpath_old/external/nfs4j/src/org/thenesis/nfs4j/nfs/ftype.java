/*
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.thenesis.nfs4j.nfs;
/**
 * Enumeration (collection of constants).
 */
public interface ftype {

    public static final int NFNON = 0;
    public static final int NFREG = 1;
    public static final int NFDIR = 2;
    public static final int NFBLK = 3;
    public static final int NFCHR = 4;
    public static final int NFLNK = 5;
    public static final int NFSOCK = 6;
    public static final int NFBAD = 7;
    public static final int NFFIFO = 8;

}
// End of ftype.java
