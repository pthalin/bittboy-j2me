package gnu.x11.event;

import gnu.x11.Display;


/** X focus out event. */
public class FocusOut extends Event {
  public static final int CODE = 10;


  public FocusOut (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
