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
 * A collection of constants used by the "nfs_prot" ONC/RPC program.
 */
public interface nfs_prot {
    public static final int NFSPROC_READ_2 = 6;
    public static final int NFSPROC_RMDIR_2 = 15;
    public static final int NFSPROC_RENAME_2 = 11;
    public static final int NFS_PROGRAM = 100003;
    public static final int NFS_FIFO_DEV = -1;
    public static final int NFS_FHSIZE = 32;
    public static final int NFSPROC_SETATTR_2 = 2;
    public static final int NFSPROC_READDIR_2 = 16;
    public static final int NFSPROC_CREATE_2 = 9;
    public static final int NFSPROC_NULL_2 = 0;
    public static final int NFSPROC_LINK_2 = 12;
    public static final int NFSPROC_WRITECACHE_2 = 7;
    public static final int NFSPROC_REMOVE_2 = 10;
    public static final int NFSPROC_LOOKUP_2 = 4;
    public static final int NFSPROC_STATFS_2 = 17;
    public static final int NFSPROC_MKDIR_2 = 14;
    public static final int NFS_MAXDATA = 8192;
    public static final int NFS_VERSION = 2;
    public static final int NFS_MAXPATHLEN = 1024;
    public static final int NFSPROC_GETATTR_2 = 1;
    public static final int NFSPROC_WRITE_2 = 8;
    public static final int NFSPROC_READLINK_2 = 5;
    public static final int NFS_COOKIESIZE = 4;
    public static final int NFS_MAXNAMLEN = 255;
    public static final int NFSPROC_SYMLINK_2 = 13;
    public static final int NFS_PORT = 2049;
    public static final int NFSPROC_ROOT_2 = 3;
}
// End of nfs_prot.java
