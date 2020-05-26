package org.thenesis.nfs4j.test;


import java.net.InetAddress;


import org.thenesis.nfs4j.mount.MountProxyStub;
import org.thenesis.nfs4j.mount.dirpath;
import org.thenesis.nfs4j.mount.fhstatus;
import org.thenesis.nfs4j.nfs.NFSProxyStub;
import org.thenesis.nfs4j.nfs.diropargs;
import org.thenesis.nfs4j.nfs.diropres;
import org.thenesis.nfs4j.nfs.entry;
import org.thenesis.nfs4j.nfs.filename;
import org.thenesis.nfs4j.nfs.nfs_fh;
import org.thenesis.nfs4j.nfs.nfs_prot;
import org.thenesis.nfs4j.nfs.nfscookie;
import org.thenesis.nfs4j.nfs.nfsstat;
import org.thenesis.nfs4j.nfs.readargs;
import org.thenesis.nfs4j.nfs.readdirargs;
import org.thenesis.nfs4j.nfs.readdirres;
import org.thenesis.nfs4j.nfs.readres;
import org.thenesis.nfs4j.nfs.statfsres;
import org.thenesis.nfs4j.oncrpc.OncRpcClientAuth;
import org.thenesis.nfs4j.oncrpc.OncRpcClientAuthUnix;
import org.thenesis.nfs4j.oncrpc.OncRpcProtocols;

public class NFSClientTest {

	/*
	 * http://ftp2.de.freebsd.org/pub/misc/www.rootshell.com/hacking/nfsshell.c
	 * http://www.unix.org.ua/orelly/networking_2ndEd/nfs/ch12_04.htm
	 */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NFSClientTest testSuite = new NFSClientTest();
		try {
			testSuite.testMount();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void testMount() throws Exception {
		MountProxyStub stub = new MountProxyStub(InetAddress.getByName("localhost"), 665,
                OncRpcProtocols.ONCRPC_TCP);
//		OncRpcClientAuth auth = new OncRpcClientAuthUnix(
//                "cyrus",
//               1001, 1001);
//		stub.getClient().setAuth(auth);
		
		//mountlist list = stub.MOUNTPROC_DUMP_1();
		//System.out.println("status: " + list.value);
		
		dirpath dirPath = new dirpath();
		dirPath.value = new String("/test");
		fhstatus fhstatus = stub.MOUNTPROC_MNT_1(dirPath);
		System.out.println("mount status: " + fhstatus.fhs_status);
		if (fhstatus.fhs_status == nfsstat.NFS_OK) {
			System.out.println("file handle: " + fhstatus.fhs_fhandle.value);
		}
		
		/* NFS */
		
		NFSProxyStub nfsStub = new NFSProxyStub( InetAddress.getByName("localhost"), 2049,
                OncRpcProtocols.ONCRPC_TCP);
		
		//stub.getClient().setAuth(auth);
		
		// Get filesystem attributes
		nfs_fh fh = new nfs_fh();
		fh.data = fhstatus.fhs_fhandle.value;
		statfsres sfres = nfsStub.NFSPROC_STATFS_2(fh);
		if (sfres.status == nfsstat.NFS_OK) {
			System.out.println("Optimum transfer size : " + sfres.reply.tsize);
			System.out.println("Block size : " + sfres.reply.bsize);
			System.out.println("Free blocks : " + sfres.reply.bfree);
			System.out.println("Available blocks : " + sfres.reply.bavail);
		}
		
		// Read from directory
		readdirargs rdargs = new readdirargs();
		//fh.data[rootDirBytes.length] = '\0';
		nfscookie cookie = new nfscookie();
		cookie.value = new byte[nfs_prot.NFS_COOKIESIZE];
		rdargs.dir = fh;
		rdargs.cookie = cookie;
		rdargs.count = 10000;
		readdirres rdres = nfsStub.NFSPROC_READDIR_2(rdargs);
		System.out.println("status: " + rdres.status);
		
		if (rdres.status == nfsstat.NFS_OK) {
			entry e = rdres.reply.entries;
			System.out.println("entry: " + e.name.value);
			
			while((e = e.nextentry) != null) {
				//System.out.println("filename1: " + e);
				System.out.println("entry: " + e.name.value);
				//e = e.nextentry;
				//Thread.sleep(1000);
			} 
		}
		
		// File lookup
		diropargs doarg = new diropargs();
		doarg.dir = fh;
		doarg.name = new filename();
		doarg.name.value = "file1";
		diropres dores = nfsStub.NFSPROC_LOOKUP_2(doarg);
		nfs_fh fileHandle = null;
		if (dores.status == nfsstat.NFS_OK) {
			fileHandle = dores.diropres.file;
			System.out.println("Lookup  '" +  doarg.name.value + "' : "+ dores.diropres.attributes.size);
		}
		
		
		// Read from file
		readargs rarg = new readargs();
		rarg.file = fileHandle;
		rarg.offset = 0;
		rarg.count = 8192;
		readres rres = nfsStub.NFSPROC_READ_2(rarg);
		if (rres.status == nfsstat.NFS_OK) {
			byte[] data = rres.reply.data;
			System.out.println("data length: " + data.length);
			for (int i = 0; i < data.length; i++) {
				System.out.print((char)data[i]);
			}
			System.out.println();
		}
		
		
		
	}
	
	public void test() throws Exception {
		
		NFSProxyStub stub = new NFSProxyStub( InetAddress.getByName("localhost"), 2049,
                OncRpcProtocols.ONCRPC_TCP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix(
				                                "cyrus",
				                               1001, 1001);
		stub.getClient().setAuth(auth);

//		OncRpcClientAuth auth = new OncRpcClientAuthUnix("marvin@ford.prefect",
//		                                 42, 1001, new int[0]);
		
		//OncRpcClientAuth auth = new OncRpcClientAuthNone();
		stub.getClient().setAuth(auth);
		
			//stub.NFSPROC_NULL_2();
			//stub.NFSPROC_ROOT_2();
		
			String rootDirName = "/test";
			byte[] rootDirBytes = rootDirName.getBytes();
			
			for (int i = 0; i < rootDirBytes.length; i++) {
				System.out.print(Integer.toHexString((int)rootDirBytes[i]));
			}
			
			
			
//			for (int i = 0; i < rootDir.length; i++) {
//				System.out.print(Integer.toHexString(rootDir[i]));
//			}
			System.out.println();
			
			readdirargs rdargs = new readdirargs();
			nfs_fh fh = new nfs_fh();
			fh.data = new byte[nfs_prot.NFS_FHSIZE];
			System.arraycopy(rootDirBytes, 0, fh.data, 0, rootDirBytes.length);
			//fh.data[rootDirBytes.length] = '\0';
			nfscookie cookie = new nfscookie();
			cookie.value = new byte[nfs_prot.NFS_COOKIESIZE];
			rdargs.dir = fh;
			rdargs.cookie = cookie;
			rdargs.count = 1000;
			readdirres rdres = stub.NFSPROC_READDIR_2(rdargs);
			System.out.println("status: " + rdres.status);
			
		
	}
	
	

}
