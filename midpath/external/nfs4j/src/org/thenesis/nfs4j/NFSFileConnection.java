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
package org.thenesis.nfs4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;

import org.thenesis.nfs4j.mount.MountProxyStub;
import org.thenesis.nfs4j.mount.dirpath;
import org.thenesis.nfs4j.mount.fhstatus;
import org.thenesis.nfs4j.nfs.NFSProxyStub;
import org.thenesis.nfs4j.nfs.attrstat;
import org.thenesis.nfs4j.nfs.createargs;
import org.thenesis.nfs4j.nfs.diropargs;
import org.thenesis.nfs4j.nfs.diropokres;
import org.thenesis.nfs4j.nfs.diropres;
import org.thenesis.nfs4j.nfs.entry;
import org.thenesis.nfs4j.nfs.filename;
import org.thenesis.nfs4j.nfs.ftype;
import org.thenesis.nfs4j.nfs.nfs_fh;
import org.thenesis.nfs4j.nfs.nfs_prot;
import org.thenesis.nfs4j.nfs.nfscookie;
import org.thenesis.nfs4j.nfs.nfsstat;
import org.thenesis.nfs4j.nfs.nfstime;
import org.thenesis.nfs4j.nfs.readargs;
import org.thenesis.nfs4j.nfs.readdirargs;
import org.thenesis.nfs4j.nfs.readdirres;
import org.thenesis.nfs4j.nfs.readres;
import org.thenesis.nfs4j.nfs.renameargs;
import org.thenesis.nfs4j.nfs.sattr;
import org.thenesis.nfs4j.nfs.statfsokres;
import org.thenesis.nfs4j.nfs.statfsres;
import org.thenesis.nfs4j.nfs.writeargs;
import org.thenesis.nfs4j.oncrpc.OncRpcException;
import org.thenesis.nfs4j.oncrpc.OncRpcPortmapClient;
import org.thenesis.nfs4j.oncrpc.OncRpcProtocols;

public class NFSFileConnection {

	public static final int USER_OTHER = 0;
	public static final int USER_GROUP = 1;
	public static final int USER_OWNER = 2;

	public static final int BIT_EXEC_OTHER = 0;
	public static final int BIT_WRITE_OTHER = 1;
	public static final int BIT_READ_OTHER = 2;
	public static final int BIT_EXEC_GROUP = 3;
	public static final int BIT_WRITE_GROUP = 4;
	public static final int BIT_READ_GROUP = 5;
	public static final int BIT_EXEC_OWNER = 6;
	public static final int BIT_WRITE_OWNER = 7;
	public static final int BIT_READ_OWNER = 8;
	public static final int BIT_DIRECTORY = 14;
	public static final int BIT_REGULAR_FILE = 15;

	private boolean portmapActivated = true;
	private static final int PROGRAM_NUMBER_NFS = 100003;
	private static final int PROGRAM_NUMBER_MOUNTD = 100005;
	private int nfsPort = 2049;
	private int mountdPort = 616;

	/* User infos */
	private int uid = -1;
	private int gid = -1;
	private int fileAccessMode = 0;
	private int directoryAccessMode = 0;

	/* Read/Write access */
	private int filePosition = 0;
	private int available = 0;
	private byte[] singleByteArray = new byte[1];
	private NFSInputStream nfsInputStream = new NFSInputStream();
	private NFSOutputStream nfsOutputStream = new NFSOutputStream();
	private readargs rarg = new readargs();
	private writeargs warg = new writeargs();
	private nfs_fh currentHandle;

	/* Mount */
	MountProxyStub mountClient;
	String mountPointName;
	nfs_fh mountPointHandle = new nfs_fh();
	boolean mounted = false;

	NFSProxyStub nfsClient;
	private int defaultTransferSize;

	private String fileName;
	private String[] pathElements;

	public NFSFileConnection() {
	}

	public void mount(String mountPointName) throws IOException {
		this.mountPointName = mountPointName;

		try {

			if (portmapActivated) {
				OncRpcPortmapClient portmapClient = new OncRpcPortmapClient(InetAddress.getByName("localhost"),
						OncRpcProtocols.ONCRPC_TCP);
				mountdPort = portmapClient.getPort(PROGRAM_NUMBER_MOUNTD, 2, OncRpcProtocols.ONCRPC_TCP);
				nfsPort = portmapClient.getPort(PROGRAM_NUMBER_NFS, 2, OncRpcProtocols.ONCRPC_TCP);
				portmapClient.close();
			}
			/* Start mount client */

			mountClient = new MountProxyStub(InetAddress.getByName("localhost"), mountdPort, OncRpcProtocols.ONCRPC_TCP);
			//		OncRpcClientAuth auth = new OncRpcClientAuthUnix(
			//                "cyrus",
			//               1001, 1001);
			//		stub.getClient().setAuth(auth);

			//mountlist list = stub.MOUNTPROC_DUMP_1();
			//System.out.println("status: " + list.value);

			dirpath dirPath = new dirpath();
			dirPath.value = mountPointName;
			fhstatus fhstatus = mountClient.MOUNTPROC_MNT_1(dirPath);
			//System.out.println("mount status: " + fhstatus.fhs_status);
			if (fhstatus.fhs_status == nfsstat.NFS_OK) {
				//System.out.println("file handle: " + fhstatus.fhs_fhandle.value);
				mountPointHandle.data = fhstatus.fhs_fhandle.value;
				mounted = true;
			} else {
				throw new IOException();
			}

			/* Start NFS client */
			nfsClient = new NFSProxyStub(InetAddress.getByName("localhost"), nfsPort, OncRpcProtocols.ONCRPC_TCP);
			//stub.getClient().setAuth(auth);

			// Get attributes of the file system
			statfsokres attributes = getFileSystemAttributes();
			if (attributes != null) {
				defaultTransferSize = attributes.tsize > 0 ? attributes.tsize : 1000;
			}

			// Set default access mode for created file/directory
			fileAccessMode = setRights(0, USER_OWNER, true, true, false);
			directoryAccessMode = setRights(0, USER_OWNER, true, true, true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Can't mount : " + mountPointName + ": " + e);
		}

	}

	public void connect(String fileName) {
		this.fileName = fileName;
		parse(fileName);
	}

	public void close() {

		try {
			nfsClient.close();
			mountClient.close();
		} catch (OncRpcException e) {
			// Do nothing
		}

	}

	private statfsokres getFileSystemAttributes() throws Exception {

		// Get filesystem attributes
		statfsres sfres = nfsClient.NFSPROC_STATFS_2(mountPointHandle);
		if (sfres.status == nfsstat.NFS_OK) {
			/*System.out.println("Optimum transfer size : " + sfres.reply.tsize);
			 System.out.println("Block size : " + sfres.reply.bsize);
			 System.out.println("Total blocks : " + sfres.reply.blocks);
			 System.out.println("Free blocks : " + sfres.reply.bfree);
			 System.out.println("Available blocks : " + sfres.reply.bavail);*/
			return sfres.reply;
		}
		return null;

	}

	private diropokres getFileAttributes() throws IOException {
		return getFileAttributes(pathElements.length);
	}

	private diropokres getFileAttributes(int elements) throws IOException {

		nfs_fh fileHandle = mountPointHandle;
		diropokres dres = null;

		for (int i = 0; i < elements; i++) {

			dres = getFileAttributes(fileHandle, pathElements[i]);
			//System.out.println("pathElements[" + i + "]: name=" + pathElements[i] + " id=" + dres.attributes.fileid);

			if (dres != null) {
				fileHandle = dres.file;
			} else {
				return null;
			}
		}

		return dres;
	}

	private diropokres getFileAttributes(nfs_fh dirHandle, String localFileName) throws IOException {
		// File lookup
		diropargs doarg = new diropargs();
		doarg.dir = dirHandle;
		doarg.name = new filename();
		doarg.name.value = localFileName;
		diropres dores;
		try {
			dores = nfsClient.NFSPROC_LOOKUP_2(doarg);
			if (dores.status == nfsstat.NFS_OK) {
				//System.out.println("Lookup  '" +  doarg.name.value + "' : "+ dores.diropres.attributes.size);
				return dores.diropres;
			}
		} catch (OncRpcException e) {
			throw new IOException(e.toString());
		}

		return null;
	}

	public long availableSize() {
		try {
			statfsokres attributes = getFileSystemAttributes();
			if (attributes != null) {
				return attributes.bavail * attributes.bsize;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public long totalSize() {
		try {
			statfsokres attributes = getFileSystemAttributes();
			if (attributes != null) {
				return attributes.blocks * attributes.bsize;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public long usedSize() {
		try {
			statfsokres attributes = getFileSystemAttributes();
			if (attributes != null) {
				return (attributes.blocks - attributes.bavail) * attributes.bsize;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public boolean canRead() {

		// FIXME
		return true;

		//		try {
		//			diropokres dres = getFileAttributes();
		//			if (dres != null) {
		//				if (dres.attributes.type == ftype.NFDIR) {
		//					if ((dres.attributes.uid == uid) & isBitSet(dres.attributes.mode, BIT_EXEC_OWNER)) {
		//						return true;
		//					}
		//				} else {
		//					if (isBitSet(dres.attributes.mode, BIT_READ_OWNER)
		//							|| isBitSet(dres.attributes.mode, BIT_READ_GROUP)) {
		//						return true;
		//					}
		//				}
		//
		//			}
		//
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		//		return false;

	}

	public boolean canWrite() {

		// FIXME
		return true;

		//		try {
		//			diropokres dres = getFileAttributes();
		//			if ((dres != null)
		//					&& (isBitSet(dres.attributes.mode, BIT_WRITE_OWNER) || isBitSet(dres.attributes.mode,
		//							BIT_WRITE_GROUP))) {
		//				return true;
		//			}
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		//		return false;

	}

	public long fileSize() throws IOException {

		diropokres dres = null;
		try {
			dres = getFileAttributes();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		if (dres == null) {
			return -1;
		} else if (dres.attributes.type == ftype.NFDIR) {
			throw new IOException("Can't get the file size of a directory");
		} else {
			return dres.attributes.size;
		}

	}

	public long lastModified() {

		try {
			diropokres dres = getFileAttributes();
			return (long) dres.attributes.mtime.useconds;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public boolean isDirectory() {
		try {
			diropokres dres = getFileAttributes();
			if ((dres != null) && (dres.attributes.type == ftype.NFDIR)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean exists() {
		try {
			diropokres dres = getFileAttributes();
			if (dres != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	

	public void mkdir() throws java.io.IOException {

		normalizeDirectoryURL(true);

		if (exists()) {
			throw new IOException("Can't create directory : a file/directory with the same name already exists");
		}

		mkdir(getNameInternal());

	}

	private diropokres mkdir(String dirName) throws java.io.IOException {

		System.out.println("mkdir: " + dirName);

		normalizeDirectoryURL(true);

		// Check if the file is into an existing directory
		diropokres dres = null;
		try {
			dres = getFileAttributes(pathElements.length - 2);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		// No directory found 
		if (dres == null || (dres.attributes.type != ftype.NFDIR)) {
			throw new IOException("Can't create directory: no parent directory found");
		}

		diropargs doargs = new diropargs();
		doargs.dir = dres.file;
		doargs.name = new filename();
		doargs.name.value = dirName;

		sattr attributes = new sattr();
		attributes.uid = uid;
		attributes.gid = gid;
		attributes.mode = setBit(directoryAccessMode, BIT_DIRECTORY, true);
		attributes.size = -1;
		attributes.atime = new nfstime();
		attributes.atime.seconds = -1;
		attributes.atime.useconds = -1;
		attributes.mtime = new nfstime();
		attributes.mtime.seconds = -1;
		attributes.mtime.useconds = -1;

		createargs carg = new createargs();
		carg.where = doargs;
		carg.attributes = attributes;

		diropres dores = null;
		try {
			dores = nfsClient.NFSPROC_MKDIR_2(carg);
		} catch (OncRpcException e) {
			throw new IOException("Can't create directory");
		}

		if (dores.status != nfsstat.NFS_OK) {
			throw new IOException("Can't create directory: error while creating the directory: " + dores.status);
		}
		return dores.diropres;
	}

	public void create() throws java.io.IOException {

		if (fileName.endsWith("/") || exists()) {
			throw new IOException("Can't create file : already exists or is a directory");
		}

		// Check if the file is into an existing directory
		diropokres dres = null;
		try {
			dres = getFileAttributes(pathElements.length - 1);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		// No directory found 
		if (dres == null || (dres.attributes.type != ftype.NFDIR)) {
			throw new IOException("Can't create file: no parent directory found");
		}

		diropargs doargs = new diropargs();
		doargs.dir = dres.file;
		doargs.name = new filename();
		doargs.name.value = pathElements[pathElements.length - 1];

		sattr attributes = new sattr();
		attributes.uid = uid;
		attributes.gid = gid;
		attributes.mode = setBit(fileAccessMode, BIT_REGULAR_FILE, true);
		attributes.size = -1;
		attributes.atime = new nfstime();
		attributes.atime.seconds = -1;
		attributes.atime.useconds = -1;
		attributes.mtime = new nfstime();
		attributes.mtime.seconds = -1;
		attributes.mtime.useconds = -1;

		createargs carg = new createargs();
		carg.where = doargs;
		carg.attributes = attributes;

		diropres dores = null;
		try {
			dores = nfsClient.NFSPROC_CREATE_2(carg);
		} catch (OncRpcException e) {
			throw new IOException("Can't create file");
		}

		if (dores.status != nfsstat.NFS_OK) {
			throw new IOException("Can't create file: error while creating the file: " + dores.status);
		}

	}

	public void delete() throws java.io.IOException {

		/* normalize URL without trailing slash if it's a directory */
		normalizeDirectoryURL(false);

		// Check if the file/directory is into an existing directory
		diropokres dres = null;
		try {
			dres = getFileAttributes(pathElements.length - 1);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		// No directory found 
		if (dres == null) {
			throw new IOException("Can't delete directory: no parent directory found");
		}

		// Remove directory or file
		diropargs doargs = new diropargs();
		doargs.dir = dres.file;
		doargs.name = new filename();
		String name = pathElements[pathElements.length - 1];

		if (dres.attributes.type == ftype.NFDIR) {
			removeDirectory(dres.file, name);
		} else {
			removeFile(dres.file, name);
		}

	}

	private void removeFile(nfs_fh dir, String name) throws IOException {
		// Remove directory or file
		diropargs doargs = new diropargs();
		doargs.dir = dir;
		doargs.name = new filename();
		doargs.name.value = name;
		try {
			int resp = nfsClient.NFSPROC_REMOVE_2(doargs);
			if (resp != nfsstat.NFS_OK) {
				throw new IOException("Can't delete file");
			}
		} catch (OncRpcException e) {
			throw new IOException("Can't delete file " + e.toString());
		}
	}

	private void removeDirectory(nfs_fh dir, String name) throws IOException {
		// Remove directory or file
		diropargs doargs = new diropargs();
		doargs.dir = dir;
		doargs.name = new filename();
		doargs.name.value = name;
		try {
			int resp = nfsClient.NFSPROC_RMDIR_2(doargs);
			if (resp != nfsstat.NFS_OK) {
				throw new IOException("Can't delete directory: directory may be not empty");
			}
		} catch (OncRpcException e) {
			throw new IOException("Can't delete directory " + e.toString());
		}
	}

	public void rename(java.lang.String newName) throws java.io.IOException {

		diropokres dres = null;
		try {
			dres = getFileAttributes();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		// No directory found 
		if (dres == null) {
			throw new IOException("Can't rename non-existing file/directory");
		}

		// Rename directory or file
		if (dres.attributes.type == ftype.NFDIR) {

			// Create dir 
			diropokres destDir = mkdir(newName);

			// move all files into it
			Enumeration e = list();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();

				diropargs fromArgs = new diropargs();
				fromArgs.dir = dres.file;
				fromArgs.name = new filename();
				fromArgs.name.value = name;
				diropargs toArgs = new diropargs();
				toArgs.dir = destDir.file;
				toArgs.name = new filename();
				toArgs.name.value = name;
				renameargs renameArgs = new renameargs();
				renameArgs.from = fromArgs;
				renameArgs.to = toArgs;
				try {
					int resp = nfsClient.NFSPROC_RENAME_2(renameArgs);
					if (resp != nfsstat.NFS_OK) {
						throw new IOException("Can't rename file");
					}
				} catch (OncRpcException ex) {
					throw new IOException("Can't rename file: " + ex.toString());
				}
			}

			// Remove current dir
			delete();
			
		} else {
			diropargs fromArgs = new diropargs();
			fromArgs.dir = dres.file;
			fromArgs.name = new filename();
			fromArgs.name.value = getNameInternal();
			diropargs toArgs = new diropargs();
			toArgs.dir = dres.file;
			toArgs.name = new filename();
			toArgs.name.value = newName;
			renameargs renameArgs = new renameargs();
			renameArgs.from = fromArgs;
			renameArgs.to = toArgs;
			try {
				int resp = nfsClient.NFSPROC_RENAME_2(renameArgs);
				if (resp != nfsstat.NFS_OK) {
					throw new IOException("Can't rename file");
				}
			} catch (OncRpcException e) {
				throw new IOException("Can't rename file: " + e.toString());
			}
			
		}
		
		// Update state to the new file/directory
		setFileConnection(newName);

	}

	public void setFileConnection(String fileName) throws java.io.IOException {
	
		String[] pathElementsCopy = new String[pathElements.length];
		System.arraycopy(pathElements, 0, pathElementsCopy, 0, pathElements.length);
		
		//printPathElements(pathElements);
		
		if (!isDirectory()) {
			throw new IOException("Current connection must be a directory");
		}
		
		if (fileName.equals("..")) {
			String[] newPathElements = new String[pathElements.length - 1];
			
			System.arraycopy(pathElements, 0, newPathElements, 0, pathElements.length - 1);
			if (pathElements[pathElements.length - 1].equals("")) {
				newPathElements[newPathElements.length - 1] = "";
			} else if (pathElements.length >= 2) {
				newPathElements[newPathElements.length - 1] = pathElements[pathElements.length - 2];
			} else if (newPathElements.length == 0){
				newPathElements = new String[1];
				newPathElements[0] = "";
			}
			pathElements = newPathElements;
			//printPathElements(pathElements);
			return;
		}
		
		if (!pathElements[pathElements.length - 1].equals("")) {
			pathElements[pathElements.length - 1] = fileName;
		} else if (pathElements.length >= 2) {
			pathElements[pathElements.length - 2] = fileName;
		}
		
		if (!exists()) {
			// Restore
			pathElements = pathElementsCopy;
			throw new IllegalArgumentException("file doesn't yet exist");
		}
		
		// Reset write/read stream
		currentHandle = null;
	}
	
	private void printPathElements(String[] elements) {
		
		System.out.print("Path: ");
		for (int i = 0; i < elements.length; i++) {
			System.out.print("'" + elements[i] + "' ");
		}
		System.out.println();
		
	}

	public void setUserInfos(int uid, int gid, int fileAccessMode, int directoryAccessMode) {
		this.uid = uid;
		this.gid = gid;
		this.fileAccessMode = (fileAccessMode & 0x1FF); // Remove high bits
		this.directoryAccessMode = (directoryAccessMode & 0x1FF); // Remove high bits
	}

	private boolean isBitSet(int value, int bit) {
		int mask = 1 << bit;
		return ((value & mask) == mask) ? true : false;
	}

	private int setBit(int val, int bit, boolean set) {
		int mask = 1 << bit;

		if (set) {
			return (val | mask);
		} else {
			return (val & (~mask));
		}
	}

	public int setRights(int attributes, int user, boolean read, boolean write, boolean execute) {

		if (user == USER_OWNER) {
			attributes = setBit(attributes, BIT_READ_OWNER, read);
			attributes = setBit(attributes, BIT_WRITE_OWNER, write);
			attributes = setBit(attributes, BIT_EXEC_OWNER, execute);
		} else if (user == USER_GROUP) {
			attributes = setBit(attributes, BIT_READ_GROUP, read);
			attributes = setBit(attributes, BIT_WRITE_GROUP, write);
			attributes = setBit(attributes, BIT_EXEC_GROUP, execute);
		} else if (user == USER_OTHER) {
			attributes = setBit(attributes, BIT_READ_OTHER, read);
			attributes = setBit(attributes, BIT_WRITE_OTHER, write);
			attributes = setBit(attributes, BIT_EXEC_OTHER, execute);
		}

		return attributes;
	}

	public String getName() {

		String internalName = getNameInternal();

		if (fileName.endsWith("/")) {
			return internalName + "/";
		} else {
			return internalName;
		}
	}

	public String getPath() {

		//TODO
		return null;

		//		StringBuffer buffer = new StringBuffer();
		//		buffer.append('/');
		//		for (int i = 0; i < pathElements.length - 1; i++) {
		//			buffer.append(pathElements[i]);
		//		}
		//		buffer.append('/');
		//		return buffer.toString();
	}
	
	private void normalizeDirectoryURL(boolean trailingSlash) {

		if (trailingSlash) {
			// Normalize (add a trailing "/" if needed)
			if (!fileName.endsWith("/")) {
				fileName += "/";
				parse(fileName);
			}
		} else {
			if (fileName.endsWith("/")) {
				fileName = fileName.substring(0, fileName.length() - 1);
				parse(fileName);
			}
		}
	}

	/**
	 * Returns file/directory name without trailing slash
	 * @return
	 */
	private String getNameInternal() {

		if ((!pathElements[pathElements.length - 1].equals("")) || (pathElements.length < 2)) {
			return pathElements[pathElements.length - 1];
		} else {
			return pathElements[pathElements.length - 2];
		}
	}

	public String getParentDirectory() {
		if ((!pathElements[pathElements.length - 1].equals("")) || (pathElements.length < 3)) {
			return pathElements[pathElements.length - 2];
		} else {
			return pathElements[pathElements.length - 3];
		}
	}

	public Enumeration list() throws java.io.IOException {

		Vector fileList = new Vector();

		// Check if current URI points to an existing directory
		diropokres dres = null;
		try {
			dres = getFileAttributes();
			//System.out.println("list in "  + dres.attributes.fileid);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		if (dres == null) {
			throw new IOException("Directory doesn't exist");
		} else if (dres.attributes.type != ftype.NFDIR) {
			throw new IOException("Not a directory");
		}

		// Read from directory
		readdirargs rdargs = new readdirargs();
		nfscookie cookie = new nfscookie();
		cookie.value = new byte[nfs_prot.NFS_COOKIESIZE];
		rdargs.dir = dres.file;
		rdargs.cookie = cookie;
		rdargs.count = defaultTransferSize;

		try {
			readdirres readResult;
			entry lastValidEntry = null;
			do {

				readResult = nfsClient.NFSPROC_READDIR_2(rdargs);
				System.out.println("NFSPROC_READDIR_2 status: " + readResult.status);

				if (readResult.status == nfsstat.NFS_OK) {
					entry e = readResult.reply.entries;
					System.out.println("entry: " + e.name.value);

					while ((e = e.nextentry) != null) {
						lastValidEntry = e;
						//System.out.println("filename1: " + e);
						//System.out.println("entry: " + e.name.value);
						if ((!e.name.value.equals("..")) && (!e.name.value.equals("."))) {
							diropokres dokres = getFileAttributes(dres.file, e.name.value);
							if (dokres != null) {
								if (dokres.attributes.type == ftype.NFDIR) {
									fileList.add(e.name.value + "/");
								} else {
									fileList.add(e.name.value);
								}
							}

						}
					}
				} else {
					throw new IOException("Can't list directory");
				}

				if (lastValidEntry != null) {
					rdargs.cookie = lastValidEntry.cookie;
				}

			} while (!readResult.reply.eof);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		return fileList.elements();

	}

	private void ensureFileAccess() throws IOException {
		// Get the file handle if not done yet
		if (currentHandle == null) {
			diropokres dres = getFileAttributes();
			if ((dres == null) || (dres.attributes.type == ftype.NFDIR)) {
				throw new IOException("can't open input/output stream: ");
			} else {
				currentHandle = dres.file;
			}
		}
	}

	/**
	 * Reads up to len bytes of data from this file into an array of bytes. 
	 * This method blocks until at least one byte of input is available.
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public int read(byte[] b, int off, int len) throws IOException {

		ensureFileAccess();

		rarg.file = currentHandle;
		rarg.offset = filePosition;
		rarg.count = len;

		readres rres = null;
		try {
			rres = nfsClient.NFSPROC_READ_2(rarg);
		} catch (OncRpcException e) {
			throw new IOException(e.toString());
		}

		if (rres.status == nfsstat.NFS_OK) {
			byte[] data = rres.reply.data;
			System.arraycopy(data, 0, b, off, data.length);
			filePosition += data.length;
			available = rres.reply.attributes.size - filePosition;
			return data.length;
		} else {
			throw new IOException("Can't read file");
		}

	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public int read() throws IOException {
		int len = read(singleByteArray, 0, 1);
		if (len == -1) {
			return -1;
		} else {
			return singleByteArray[0];
		}
	}

	/**
	 * 
	 * @param b
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	public void write(byte[] b, int off, int len) throws IOException {

		ensureFileAccess();

		warg.file = currentHandle;
		warg.offset = (int) filePosition;
		warg.data = new byte[len];
		System.arraycopy(b, off, warg.data, 0, len);

		attrstat rstat = null;
		try {
			rstat = nfsClient.NFSPROC_WRITE_2(warg);
		} catch (OncRpcException e) {
			throw new IOException(e.toString());
		}

		if (rstat.status == nfsstat.NFS_OK) {
			filePosition += len;
			available = rstat.attributes.size - filePosition;
		} else {
			throw new IOException("Can't write file");
		}

	}

	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	public void write(int b) throws IOException {
		singleByteArray[0] = (byte) b;
		write(singleByteArray, 0, 1);
	}

	public void seek(long pos) throws IOException {
		filePosition = (int) pos;
	}

	/**
	 * Returns the number of bytes that can be read from this input stream without blocking.
	 * @return
	 */
	public int available() {
		return available;
	}

	public InputStream getInputStream() {
		return nfsInputStream;
	}

	public OutputStream getOutputStream() {
		return nfsOutputStream;
	}

	private void parse(String name) {

		/* Valid URLs: 
		 * scheme://<host>/<root>/<directory>/<filename.extension> 
		 * scheme://<host>/<root>/<directory>/<filename> 
		 * scheme://<host>/<root>/<directory>/
		 */

		if (!name.startsWith("//")) {
			throw new IllegalArgumentException("Missing protocol separator");
		}

		Vector v = new Vector();
		int lastIndex = 2;
		int index;

		// Get host
		index = name.indexOf('/', lastIndex);
		if (index == -1)
			throw new IllegalArgumentException("Missing protocol separator");
		String host = name.substring(lastIndex, index);
		lastIndex = index + 1;
		System.out.println("host: " + host);

		// Get root
		index = name.indexOf('/', lastIndex);
		if (index == -1)
			throw new IllegalArgumentException("Missing protocol separator");
		String root = name.substring(lastIndex, index);
		lastIndex = index + 1;
		System.out.println("root: " + root);

		//		// If no path is given, the current directory is the root directory
		//		if (name.indexOf('/', lastIndex) == -1) {
		//			pathElements = new String[1];
		//			pathElements[0] = new String("");
		//			return;
		//		}

		// Get path
		while ((index = name.indexOf('/', lastIndex)) != -1) {
			//System.out.println(name.substring(lastIndex, index));
			//System.out.println(lastIndex + " " + index);
			v.add(name.substring(lastIndex, index));
			lastIndex = index + 1;
		}

		String lastElement = name.substring(lastIndex, name.length());
		v.add(lastElement);

		pathElements = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			pathElements[i] = (String) v.get(i);
		}

	}

	public String[] getPathElements() {
		return pathElements;
	}

	class NFSInputStream extends InputStream {

		public int read() throws IOException {
			return NFSFileConnection.this.read();
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return NFSFileConnection.this.read(b, off, len);
		}

		public int read(byte[] b) throws IOException {
			return NFSFileConnection.this.read(b);
		}

	}

	class NFSOutputStream extends OutputStream {

		public void write(byte[] b, int off, int len) throws IOException {
			NFSFileConnection.this.write(b, off, len);
		}

		public void write(byte[] b) throws IOException {
			NFSFileConnection.this.write(b);
		}

		public void write(int b) throws IOException {
			NFSFileConnection.this.write(b);
		}

	}

}
