package gnu.x11.event;

import gnu.x11.Display;


/** X map request event. */
public class MapRequest extends Event {
  public static final int CODE = 20;


  public MapRequest (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
