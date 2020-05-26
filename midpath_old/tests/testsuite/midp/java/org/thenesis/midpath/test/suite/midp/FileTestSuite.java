package org.thenesis.midpath.test.suite.midp;

import java.io.IOException;

import org.thenesis.midpath.io.backend.cldc.File;
import org.thenesis.midpath.io.backend.cldc.RandomAccessFile;
import org.thenesis.midpath.test.suite.AbstractTestSuite;

public class FileTestSuite extends AbstractTestSuite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FileTestSuite testSuite = new FileTestSuite("FileTest");
		try {
			testSuite.testFile();
			testSuite.testDir();
			testSuite.testList();
			testSuite.testRandomAccess();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public FileTestSuite(String className) {
		super(className);
	}

	public void testFile() throws IOException {

		checkPoint("File");
		verbose("== File operation tests ==");

		File file = new File("test");
		boolean fileExists = file.exists();

		if (!fileExists) {
			debug("File doesn't exist. Creating it...");
			check(file.createNewFile(), true, "createNewFile");
		} else {
			debug("File already exists.");
			check(file.createNewFile(), false, "createNewFile");
		}

		check(file.canRead(), "canRead");
		check(file.canWrite(), "canWrite");
		check(file.isFile(), "isFile");
		check(!file.isHidden(), "isHidden()");
		check(file.length(), 0, "length");
		check(file.getName(), "test", "getName");
		debug("Absolute path: " + file.getAbsolutePath());
		debug("Canonical path: " + file.getCanonicalPath());
		debug("Parent: " + file.getParent());

		File file2 = new File("test2");
		check(file.renameTo(file2), "renameTo");
		check(file.exists(), false);
		check(file2.exists(), true);

		check(file2.delete(), true, "delete");
		check(file2.exists(), false);

	}

	public void testDir() throws IOException {

		checkPoint("Dir");
		verbose("==Directory operation tests==");

		File file = new File("testDir");
		boolean fileExists = file.exists();

		if (!fileExists) {
			debug("File doesn't exist. Creating it...");
			check(file.mkdir(), true, "mkdir");
		} else {
			debug("File already exists.");
			check(file.mkdir(), false, "mkdir");
		}
		check(file.exists(), true, "mkdir");

		check(file.canRead(), true, "canRead");
		check(file.canWrite(), true, "canWrite");
		check(file.isDirectory(), "isDirectory");
		check(!file.isHidden(), "isHidden()");
		check(file.getName(), "testDir", "getName");

		File file2 = new File("testDir2");
		check(file.renameTo(file2), "renameTo");
		check(file.exists(), false);
		check(file2.exists(), true);

		check(file2.delete(), true, "delete");
		check(file2.exists(), false);

	}

	public void testList() throws IOException {

		checkPoint("List files");
		verbose("==File listing tests==");

		File[] files = File.listRoots();
		debug("First root: " + files[0]);
		check(files.length != 0, "listRoots");

		File file = new File("/tmp/");
		String[] list = file.list();
		check(list.length != 0, "list()");
		debug("First file in the  list: " + list[0]);
		File[] fileList = file.listFiles();
		check(fileList.length != 0, "listFiles()");
		check(fileList[0].exists());
		debug("First file in the  list: " + fileList[0].getName());

	}
	
	public void testRandomAccess() throws IOException {
		
		checkPoint("Random access (Write/Read)");
		verbose("==File random access tests==");
		
		RandomAccessFile raf = new RandomAccessFile("raTest", "rw");
		
		byte[] buf = { 56, 34, 26 };
		raf.write((byte)22);
		raf.write(buf);
		raf.close();
		
		raf = new RandomAccessFile("raTest", "rw");
		check(raf.read(), 22);
		raf.read(buf);
		check(buf[0], 56);
		check(buf[1], 34);
		check(buf[2], 26);
		raf.close();
		
		raf = new RandomAccessFile("raTest", "rw");
		raf.seek(2);
		check(raf.read(), 34, "seek");
		
		raf.seek(1);
		raf.skipBytes(2);
		check(raf.read(), 26, "skipBytes");
		
		check(raf.length(), 4, "length");
		
		raf.setLength(3);
		check(raf.length(), 3, "setLength");
		
	
	}
	
	

}
