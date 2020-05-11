package gnu.x11.event;

import gnu.x11.Atom;
import gnu.x11.Display;


/** X property notify event. */
public class PropertyNotify extends Event {
  public static final int CODE = 28;


  public PropertyNotify (Display display, byte [] data) {
    super (display, data, 4); 
  }


  public Atom atom (Display display) { 
    return (Atom) Atom.intern (display, read4 (8), true);
  }
}
