package gnu.x11.event;

import gnu.x11.Display;


/** X resize request event. */
public class ResizeRequest extends Event {
  public static final int CODE = 25;


  public ResizeRequest (Display display, byte [] data) {
    super (display, data, 8); 
  }


  //-- reading

  public int width () { return read2 (8); }
  public int height () { return read2 (10); }

  /**
   * Returns the window ID of the resize request.
   *
   * @return the window ID of the resize request
   */
  public int window_id () {
    return read4 (4);
  }
}
