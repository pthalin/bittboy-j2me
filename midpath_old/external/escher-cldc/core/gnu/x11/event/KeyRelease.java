package gnu.x11.event;

import gnu.x11.Display;


/** X key release event. */
public class KeyRelease extends Input {
  public static final int CODE = 3;


  public KeyRelease (Display display, byte [] data) {
    super (display, data);
  }

}
