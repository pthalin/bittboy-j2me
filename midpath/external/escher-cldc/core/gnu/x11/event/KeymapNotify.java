package gnu.x11.event;

import gnu.x11.Display;


/** X keymap notify event. */
public class KeymapNotify extends Event {
  public static final int CODE = 11;


  public KeymapNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }

}
