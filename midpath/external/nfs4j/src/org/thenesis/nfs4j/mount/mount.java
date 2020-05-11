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
package org.thenesis.nfs4j.mount;
/**
 * A collection of constants used by the "mount" ONC/RPC program.
 */
public interface mount {
    public static final int MOUNTPROC_EXPORTALL_1 = 6;
    public static final int MOUNTPROC_UMNT_1 = 3;
    public static final int FHSIZE = 32;
    public static final int MOUNTPROC_MNT_1 = 1;
    public static final int MOUNTVERS = 1;
    public static final int MOUNTPROC_UMNTALL_1 = 4;
    public static final int MNTNAMLEN = 255;
    public static final int MOUNTPROC_EXPORT_1 = 5;
    public static final int MOUNTPROC_NULL_1 = 0;
    public static final int MOUNTPROC_DUMP_1 = 2;
    public static final int MOUNTPROG = 100005;
    public static final int MNTPATHLEN = 1024;
}
// End of mount.java
