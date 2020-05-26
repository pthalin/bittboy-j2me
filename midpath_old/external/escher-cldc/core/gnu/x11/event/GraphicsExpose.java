package gnu.x11.event;

import gnu.x11.Display;


/** X graphics expose event. */
public class GraphicsExpose extends Event {
  public static final int CODE = 13;


  public GraphicsExpose (Display display, byte [] data) {
    super (display, data, 8); 
  }
}
