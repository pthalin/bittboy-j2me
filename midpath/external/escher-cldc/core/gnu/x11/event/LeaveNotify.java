package gnu.x11.event;

import gnu.x11.Display;


/** X leave notify event. */
public class LeaveNotify extends Input {
  public static final int CODE = 8;


  public LeaveNotify (Display display, byte [] data) {
    super (display, data);
  }
}
