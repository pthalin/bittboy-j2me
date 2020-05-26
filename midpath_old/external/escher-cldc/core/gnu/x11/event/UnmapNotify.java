package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Window;


/** X unmap notify event. */
public class UnmapNotify extends Event {
  public static final int CODE = 18;


  /** Reading. */
  public UnmapNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }


  //-- reading
  public boolean from_configure () { return read_boolean (12); }


  /** Writing. */
  public UnmapNotify (Display display) { super (display, CODE, 8); }

  //-- writing
  public void set_event (Window w) { write4 (4, w.id); }
  public void set_window (Window w) { write4 (8, w.id); }
  public void set_from_configure (boolean b) { write1 (12, b); }
}
