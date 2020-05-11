package gnu.x11.event;

import gnu.x11.Display;


/** X enter notify event. */
public class EnterNotify extends Input {
  public static final int CODE = 7;


  public EnterNotify (Display display, byte [] data) {
    super (display, data);
  }
}
