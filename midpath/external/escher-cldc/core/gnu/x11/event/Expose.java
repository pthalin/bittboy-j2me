package gnu.x11.event;

import gnu.x11.Display;


/** X expose event. */
public class Expose extends Event {
  public static final int CODE = 12;


  public Expose (Display display, byte [] data) {
    super (display, data, 4); 
  }


  public int x () { return read2 (8); }
  public int y () { return read2 (10); }
  public int width () { return read2 (12); }
  public int height () { return read2 (14); }
  public int count () { return read2 (16); }
}
