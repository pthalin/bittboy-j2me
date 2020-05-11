package gnu.x11.event;

import gnu.x11.Display;


/** X gravity notify event. */
public class GravityNotify extends Event {
  public static final int CODE = 24;


  public GravityNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
