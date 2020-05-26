package gnu.x11.event;

import gnu.x11.Display;


/** X circulate request event. */
public class CirculateRequest extends Event {
  public static final int CODE = 27;


  public CirculateRequest (Display display, byte [] data) {
    super (display, data, 8); 
  }

}
