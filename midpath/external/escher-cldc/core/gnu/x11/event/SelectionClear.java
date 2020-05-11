package gnu.x11.event;

import gnu.x11.Display;


/** X selection clear event. */
public class SelectionClear extends Event {
  public static final int CODE = 29;


  public SelectionClear (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
