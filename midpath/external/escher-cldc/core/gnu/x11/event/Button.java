package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Window;


/** X button event. */
public class Button extends Input {
  public Button (Display display, byte [] data) {
    super (display, data); 
  }


  public int detail () { return read1 (1); }
  public int child_id () { return read4 (16); }
  public Window child () { return (Window) Window.intern (display, child_id ()); }
}
