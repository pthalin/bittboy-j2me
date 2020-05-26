package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Rectangle;


/** X configure notify event. */
public class ConfigureNotify extends Event {
  public static final int CODE = 22;

  
  /** Reading. */
  public ConfigureNotify (Display display, byte [] data) {
    super (display, data, 8); 
  }


  //-- reading

  public int event_id () { return read4 (4); }
  public int above_sibling_id () { return read4 (12); }
  public int x () { return read2 (16); }
  public int y () { return read2 (18); }
  public int width () { return read2 (20); }
  public int height () { return read2 (22); }


  public Rectangle rectangle () {
    return new Rectangle (x (), y (), width (), height ());
  }


  /** Writing. */
  public ConfigureNotify (Display display) { super (display, CODE, 8); }

  //-- writing
  public void set_event_id (int i) { write4 (4, i); }
  public void set_above_sibling_id (int i) { write4 (12, i); }
  public void set_x (int i) { write2 (16, i); }
  public void set_y (int i) { write2 (18, i); }
  public void set_width (int i) { write2 (20, i); }
  public void set_height (int i) { write2 (22, i); }
  public void set_border_width (int i) { write2 (24, i); }
  public void set_override_redirect (boolean b) { write1 (26, b); }
}
