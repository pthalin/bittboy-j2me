package gnu.x11.event;

import gnu.x11.Display;


/** X no exposure event. */
public class NoExposure extends Event {
  public static final int CODE = 14;


  public NoExposure (Display display, byte [] data) {
    super (display, data, 4); 
  }
}
