package gnu.x11.event;

import gnu.x11.Display;


/** X reparent notify event. */
public class ReparentNotify extends Event {
  public static final int CODE = 21;


  public ReparentNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
