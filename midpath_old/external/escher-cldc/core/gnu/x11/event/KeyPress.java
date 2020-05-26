package gnu.x11.event;

import gnu.x11.Display;


/** X key press event. */
public class KeyPress extends Input {
  public static final int CODE = 2;


  // reading
  public KeyPress (Display display, byte [] data) {
    super (display, data);
  }


  // writing
  public KeyPress (Display display) { super (display, CODE); }
}
