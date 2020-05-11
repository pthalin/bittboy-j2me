package gnu.x11.event;

import gnu.x11.Atom;
import gnu.x11.Display;


/** X client message event. */
public class ClientMessage extends Event {
  public static final int CODE = 33;


  /** Reading. */
  public ClientMessage (Display display, byte [] data) {
    super (display, data, 4); 
  }


  //-- reading

  public int format () { return read1 (1); }
  public int type_id () { return read4 (8); }
  public int wm_data () { return read4 (12); }
  public int wm_time () { return read4 (16); }


  public boolean delete_window () {
    Atom wm_protocols = (Atom) Atom.intern (display, "WM_PROTOCOLS");
    Atom wm_delete_window = (Atom) Atom.intern (display,
      "WM_DELETE_WINDOW");

    return format () == 32
      && type () == wm_protocols
      && wm_data () == wm_delete_window.id;
  }


  public Atom type () { 
    return (Atom) Atom.intern (display, type_id (), true); 
  }


  /** Writing. */
  public ClientMessage (Display display) { super (display, CODE, 4); }

  //-- writing
  public void set_format (int b) { write1 (1, b); }
  public void set_type (Atom atom) { write4 (8, atom.id); }
  public void set_wm_data (int i) { write4 (12, i); }
  public void set_wm_time (int i) { write4 (16, i); } 
}
