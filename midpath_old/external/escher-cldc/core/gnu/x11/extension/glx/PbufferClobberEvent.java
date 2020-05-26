package gnu.x11.extension.glx;

import gnu.x11.Display;


/** GLX pbuffer clobber event. */
public class PbufferClobberEvent extends gnu.x11.event.Event {
  public static final int code = 0;


  public PbufferClobberEvent (Display display, byte [] data) { 
    super (display, data, 0); 
  }


  public int drawable_id () { return read4 (8); }
  public int buffer_mask () { return read4 (12); }
  public int aux_buffer () { return read2 (16); }
  public int x () { return read2 (18); }
  public int y () { return read2 (20); }
  public int width () { return read2 (22); }
  public int height () { return read2 (24); }
  public int count () { return read2 (26); }


  public final static int DAMAGED = 0x8017;
  public final static int SAVED = 0x8018;


  /** 
   * @return valid:
   * {@link #DAMAGED},
   * {@link #SAVED}
   */
  public int event_type () { return read2 (4); }


  public final static int WINDOW = 0x8019;
  public final static int PBUFFER = 0x801A;


  /** 
   * @return valid:
   * {@link #WINDOW},
   * {@link #PBUFFER}
   */
  public int drawable_type () { return read2 (6); }
}  
