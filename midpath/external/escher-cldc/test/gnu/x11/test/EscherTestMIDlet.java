package gnu.x11.test;


import gnu.app.glxdemo.RotateBox;
import gnu.x11.extension.NotFoundException;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class EscherTestMIDlet extends MIDlet {

	private Display display;

	//@Override
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	//@Override
	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	//@Override
	protected void startApp() throws MIDletStateChangeException {
		System.out.println("[DEBUG] TestMidlet.startApp(): 1");
		display = Display.getDisplay(this);
		System.out.println("[DEBUG] TestMidlet.startApp(): 2");
		try {
			testEscher();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testEscher() throws NotFoundException {
		
		String[] args = new String[] { "" };
		
		// Standard tests
		//new Polygon(args).exec();
		new ZPixmap (args).exec ();
		
		// OpenGL tests
		//new RotateBox (args).exec ();
		
	}

}
