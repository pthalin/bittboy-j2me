package gnu.x11.event;

import gnu.x11.Display;


/** X motion notify event. */
public class MotionNotify extends Input {
  public static final int CODE = 6;


  public MotionNotify (Display display, byte [] data) {
    super (display, data); 
  }

}
