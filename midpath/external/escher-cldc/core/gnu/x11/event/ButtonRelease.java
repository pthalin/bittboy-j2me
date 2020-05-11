package gnu.x11.event;

import gnu.x11.Display;


/** X button release event. */
public class ButtonRelease extends Input {
  public static final int CODE = 5;


  public ButtonRelease (Display display, byte [] data) {
    super (display, data); 
  }


  public ButtonRelease (Display display) { super (display, CODE); }
}
