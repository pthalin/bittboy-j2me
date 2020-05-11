package gnu.x11.event;

import gnu.x11.Display;


/** X selection notify event. */
public class SelectionNotify extends Event {
  public static final int CODE = 31;


  public SelectionNotify (Display display, byte [] data) {
    super (display, data, 8);
  }
}
