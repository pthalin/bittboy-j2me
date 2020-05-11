package gnu.x11.event;

import gnu.x11.Display;


/** X visibility notify event. */
public class VisibilityNotify extends Event {
  public static final int CODE = 15;


  public VisibilityNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
