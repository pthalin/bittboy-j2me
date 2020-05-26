package org.thenesis.midpath.test.ui;

import java.io.IOException;

import org.thenesis.microbackend.ui.fb.FBBackend;
import org.thenesis.microbackend.ui.gtk.GTKBackend;
import org.thenesis.microbackend.ui.qt.QTBackend;

public class BackendTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BackendTest testSuite = new BackendTest();
		//testSuite.testGTK();
		try {
			testSuite.testQT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//testSuite.testFB();
	}
	
	public void testGTK() {
		
		int width = 200;
		int height = 200;

		int[] buffer = new int[width * height];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0xFFFF0000;
		}

		GTKBackend test = new GTKBackend(width, height);
		test.open();
		//test.updateARGBPixels(buffer, 50, 50, 50, 50);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 100; i++) {
			test.updateARGBPixels(buffer, i, i, 50, 50);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void testQT() throws IOException {
		
		int width = 200;
		int height = 200;

		int[] buffer = new int[width * height];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0xFFFF0000;
		}

		QTBackend canvas = new QTBackend(width, height);
		canvas.open();
		//test.updateARGBPixels(buffer, 50, 50, 50, 50);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 100; i++) {
			canvas.updateARGBPixels(buffer, i, i, 50, 50);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		canvas.close();

	}
	
	public void testFB() {
		
		int width = 320;
		int height = 200;

		int[] buffer = new int[width * height];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0xFF0000FF;
		}

		//FBCanvas canvas = new FBCanvas("/dev/tty", null, null, "/dev/fb0", width, height);
		FBBackend canvas = new FBBackend(width, height, "/dev/tty", "/dev/input/event1", "/dev/input/event2", "/dev/fb0");
		canvas.open();
		
		canvas.updateARGBPixels(buffer, 0, 0, width, height);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

//		for (int i = 0; i < 100; i++) {
//			canvas.updateARGBPixels(buffer, 0, 0, 50, 50);
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		canvas.close();

	}

}
