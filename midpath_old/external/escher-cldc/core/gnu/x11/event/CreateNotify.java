package gnu.x11.event;

import gnu.x11.Display;


/** X create notify event. */
public class CreateNotify extends Event {
  public static final int CODE = 16;


  public CreateNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }

}
