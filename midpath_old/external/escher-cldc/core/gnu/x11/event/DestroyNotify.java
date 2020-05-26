package gnu.x11.event;

import gnu.x11.Display;


/** X destroy notify event. */
public class DestroyNotify extends Event {
  public static final int CODE = 17;


  public DestroyNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }

}
