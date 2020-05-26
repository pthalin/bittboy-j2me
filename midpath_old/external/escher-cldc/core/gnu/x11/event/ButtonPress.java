package gnu.x11.event;

import gnu.x11.Display;


/** X button press event. */
public class ButtonPress extends Input {
  public static final int CODE = 4;


  public ButtonPress (Display display, byte [] data) {
    super (display, data); 
  }


  public ButtonPress (Display display) { super (display, CODE); }
}
