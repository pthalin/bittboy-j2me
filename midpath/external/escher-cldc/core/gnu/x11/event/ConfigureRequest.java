package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Rectangle;
import gnu.x11.Window;


/** X configure request event. */
public class ConfigureRequest extends Event {
  public static final int CODE = 23;


  // reading

  public ConfigureRequest (Display display, byte [] data) {
    super (display, data, 8); 
  }

  
  public Window.Changes changes () {
    Window.Changes c = new Window.Changes ();

    c.stack_mode (stack_mode ());
    c.sibling_id (sibling_id ());
    c.x (x ());
    c.y (y ());
    c.width (width ());
    c.height (height ());
    c.border_width (border_width ());

    // since above function calls will change bitmask, 
    // read bitmask last
    c.bitmask = bitmask ();
    return c;
  }


  public int stack_mode () { return read1 (1); }
  public int sibling_id () { return read4 (12); }
  public int x () { return read2 (16); }
  public int y () { return read2 (18); }
  public int width () { return read2 (20); }
  public int height () { return read2 (22); }
  public int border_width () { return read2 (24); }
  public int bitmask () { return read2 (26); }


  public Rectangle rectangle () {
    return new Rectangle (x (), y (), width (), height ());
  }
}
