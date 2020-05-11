package gnu.x11.event;

import gnu.x11.Display;
import gnu.x11.Window;


/** X event. */
public class Event extends gnu.x11.Data {
  public static final int NO_EVENT_MASK = 0;
  public static final int KEY_PRESS_MASK = 1<<0;
  public static final int KEY_RELEASE_MASK = 1<<1;
  public static final int BUTTON_PRESS_MASK = 1<<2;
  public static final int BUTTON_RELEASE_MASK = 1<<3;
  public static final int ENTER_WINDOW_MASK = 1<<4;
  public static final int LEAVE_WINDOW_MASK = 1<<5;
  public static final int POINTER_MOTION_MASK = 1<<6;
  public static final int POINTER_MOTION_HINT_MASK = 1<<7;
  public static final int BUTTON1_MOTION_MASK = 1<<8;
  public static final int BUTTON2_MOTION_MASK = 1<<9;
  public static final int BUTTON3_MOTION_MASK = 1<<10;
  public static final int BUTTON4_MOTION_MASK = 1<<11;
  public static final int BUTTON5_MOTION_MASK = 1<<12;
  public static final int BUTTON_MOTION_MASK = 1<<13;
  public static final int KEYMAP_STATE_MASK = 1<<14;
  public static final int EXPOSURE_MASK = 1<<15;
  public static final int VISIBILITY_CHANGE_MASK = 1<<16;
  public static final int STRUCTURE_NOTIFY_MASK = 1<<17;
  public static final int RESIZE_REDIRECT_MASK = 1<<18;
  public static final int SUBSTRUCTURE_NOTIFY_MASK = 1<<19;
  public static final int SUBSTRUCTURE_REDIRECT_MASK = 1<<20;
  public static final int FOCUS_CHANGE_MASK = 1<<21;
  public static final int PROPERTY_CHANGE_MASK = 1<<22;
  public static final int COLORMAP_CHANGE_MASK = 1<<23;
  public static final int OWNER_GRAB_BUTTON_MASK = 1<<24;
  public static final int LAST_MASK_INDEX = 24;


  public Display display;
  public boolean synthetic;
  public int window_offset;


  /** Writing. */
  public Event (Display display, int code, int window_offset) {
    this.display = display;
    this.window_offset = window_offset;

    data = new byte [32];
    data [0] = (byte) code;
  }


  /** Reading. */
  public Event (Display display, byte [] data, int window_offset) {
    super (data);
    this.display = display;
    this.window_offset = window_offset;

    synthetic = (this.data [0] & 0x80) != 0;
    this.data [0] &= 0x7f;
  }


  public int code () { return read1 (0); }
  public int seq_no () { return read2 (2); }
  public int time () { return read4 (4); }
  public int window_id () { return read4 (window_offset); }
  public void set_window (Window window) { set_window (window.id); }
  public void set_window (int i) { write4 (window_offset, i); }


  public String toString () {
    String class_name = "#" + getClass ().getName ();
    String synthetic0 = synthetic ? " (synthetic) " : " ";
    return class_name + " " + code () + synthetic0 + window ();
  }


  public Window window () {
    return (Window) Window.intern (display, window_id ());
  }
}
