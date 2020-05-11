package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Window;


/** X input-related event. */
public abstract class Input extends Event {
  /** Reading. */
  public Input (Display display, byte [] data) {
    super (display, data, 12); 
  }


  //-- reading

  public int detail () { return read1 (1); }
  public int root_id () { return read4 (8); }
  public int child_id () { return read4 (16); }
  public int root_x () { return read2 (20); }
  public int root_y () { return read2 (22); }
  public int event_x () { return read2 (24); }
  public int event_y () { return read2 (26); }
  public int state () { return read2 (28); }
  public boolean same_screen () { return read_boolean (30); }


  public Window root () { 
    return (Window) Window.intern (display, root_id ()); 
  }


  public Window child () { 
    return (Window) Window.intern (display, child_id ()); 
  }


  /** Writing. */
  public Input (Display display, int CODE) { 
    super (display, CODE, 12); 
  }


  //-- writing

  public void set_detail (int i) { write1 (1, i); }
  public void set_root (Window w) { set_root_id (w.id); }
  public void set_root_id (int i) { write4 (8, i); }
  public void set_child (Window w) { set_child_id (w.id); }
  public void set_child_id (int i) { write4 (16, i); }
  public void set_root_x (int i) { write2 (20, i); }
  public void set_root_y (int i) { write2 (22, i); }
  public void set_event_x (int i) { write2 (24, i); }
  public void set_event_y (int i) { write2 (26, i); }
  public void set_state (int i) { write2 (28, i); }
  public void set_same_screen (boolean b) { write1 (30, b); }
}
