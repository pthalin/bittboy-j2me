package gnu.x11.event;

import gnu.x11.Display;


/** X selection request event. */
public class SelectionRequest extends Event {
  public static final int CODE = 30;


  public SelectionRequest (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
