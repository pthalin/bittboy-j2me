package gnu.x11.event;

import gnu.x11.Display;


/** X focus in event. */
public class FocusIn extends Event {
  public static final int CODE = 9;


  public FocusIn (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
