package gnu.x11.event;

import gnu.x11.Display;


/** X map notify event. */
public class MapNotify extends Event {
  public static final int CODE = 19;


  public MapNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
