package gnu.x11.event;

import gnu.x11.Display;


/** X circulate notify event. */
public class CirculateNotify extends Event {
  public static final int CODE = 26;


  public CirculateNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }

}
