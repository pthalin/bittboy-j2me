package gnu.x11.event;

import gnu.x11.Display;


/** X colormap notify event. */
public class ColormapNotify extends Event {
  public static final int CODE = 32;


  public ColormapNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }

}
