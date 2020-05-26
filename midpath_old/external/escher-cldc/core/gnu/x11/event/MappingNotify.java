package gnu.x11.event;

import gnu.x11.Display;


/** X mapping notify event. */
public class MappingNotify extends Event {
  public static final int CODE = 34;


  public MappingNotify (Display display, byte [] data) {
    super (display, data, 0); 
  }


  public void set_window (int i) {} // no window
  public int window_id () { return 0; } // no window
}
