package gnu.x11.extension.glx;

import gnu.x11.Data;


/** GLX visual configuration. */ 
public class VisualConfig extends Data {
  public static final int VISUAL_BIT = 1<<0;
  public static final int CLASS_BIT = 1<<1;
  public static final int RGBA_BIT = 1<<2;
  public static final int RED_SIZE_BIT = 1<<3;
  public static final int GREEN_SIZE_BIT = 1<<4;
  public static final int BLUE_SIZE_BIT = 1<<5;
  public static final int ALPHA_SIZE_BIT = 1<<6;
  public static final int ACCUM_RED_SIZE_BIT = 1<<7;
  public static final int ACCUM_GREEN_SIZE_BIT = 1<<8;
  public static final int ACCUM_BLUE_SIZE_BIT = 1<<9;
  public static final int ACCUM_ALPHA_SIZE_BIT = 1<<10;
  public static final int DOUBLE_BUFFER_BIT = 1<<11;
  public static final int STERO_BIT = 1<<12;
  public static final int BUFFER_SIZE_BIT = 1<<13;
  public static final int DEPTH_SIZE_BIT = 1<<14;
  public static final int STENCIL_SIZE_BIT = 1<<15;
  public static final int AUX_BUFFERS_BIT = 1<<16;
  public static final int LEVEL_BIT = 1<<17;


  public int bitmask;
  public int count;


  /** Writing. */
  public VisualConfig () { super (18*4); }


  /** Reading. */
  public VisualConfig (Data data, int offset, int count) { 
    super (data, offset);
    this.count = count;
  }    


  //-- reading

  public int visual_id () { return read4 (0); }
  public int clazz () { return read4 (4); }
  public boolean rgba  () { return read4_boolean (8); }
  public int red_size () { return read4 (12); }
  public int green_size () { return read4 (16); }
  public int blue_size () { return read4 (20); }
  public int alpha_size () { return read4 (24); }
  public int accum_red_size () { return read4 (28); }
  public int accum_green_size () { return read4 (32); }
  public int accum_blue_size () { return read4 (36); }
  public int accum_alpha_size () { return read4 (40); }
  public boolean double_buffer  () { return read4_boolean (44); }
  public boolean stero  () { return read4_boolean (48); }
  public int buffer_size () { return read4 (52); }
  public int depth_size () { return read4 (56); }
  public int stencil_size () { return read4 (60); }
  public int aux_buffers () { return read4 (64); }
  public int level () { return read4 (68); }


  public int more_property_type (int i) { return read4 (18*4 + i*8); }
  public int more_property_value (int i) { return read4 (18*4 + i*8 + 4); }


  //-- writing

  public void set_visual_id (int i) { write4 (0, i); set (0); }
  public void set_clazz (int i) { write4 (4, i); set (1); }
  public void set_rgba () { set (2); }
  public void set_red_size (int i) { write4 (12, i); set (3); }
  public void set_green_size (int i) { write4 (16, i); set (4); }
  public void set_blue_size (int i) { write4 (20, i); set (5); }
  public void set_alpha_size (int i) { write4 (24, i); set (6); }
  public void set_accum_red_size (int i) { write4 (28, i); set (7); }
  public void set_accum_green_size (int i) { write4 (32, i); set (8); }
  public void set_accum_blue_size (int i) { write4 (36, i); set (9); }
  public void set_accum_alpha_size (int i) { write4 (40, i); set (10); }
  public void set_double_buffer () { set (11); }
  public void set_stero () { set (12); }
  public void set_buffer_size (int i) { write4 (52, i); set (13); }
  public void set_depth_size (int i) { write4 (56, i); set (14); }
  public void set_stencil_size (int i) { write4 (60, i); set (15); }
  public void set_aux_buffers (int i) { write4 (64, i); set (16); }
  public void set_level (int i) { write4 (68, i); set (17); }


  public void clear () { bitmask = 0; }
  public int length () { return 4 * count; }


  public void set_accum_rgb_size (int i) {
    set_accum_red_size (i);
    set_accum_green_size (i);
    set_accum_blue_size (i);
  }


  public boolean match (VisualConfig template) { // TODO
    if ((template.bitmask & VISUAL_BIT) != 0 // exact
      && template.visual_id () != visual_id ()) return false;

    if ((template.bitmask & CLASS_BIT) != 0 // exact
      && template.clazz () != clazz ()) return false;

    if ((template.bitmask & RGBA_BIT) != 0 // exact
      && !rgba ()) return false;

    if ((template.bitmask & RED_SIZE_BIT) != 0 // larger
      && template.red_size () > red_size ()) return false;

    if ((template.bitmask & GREEN_SIZE_BIT) != 0 // larger
      && template.green_size () > green_size ()) return false;

    if ((template.bitmask & BLUE_SIZE_BIT) != 0 // larger
      && template.blue_size () > blue_size ()) return false;

    if ((template.bitmask & ALPHA_SIZE_BIT) != 0 // larger
      && template.alpha_size () > alpha_size ()) return false;

    if ((template.bitmask & ACCUM_RED_SIZE_BIT) != 0 // larger
      && template.accum_red_size () > accum_red_size ()) return false;

    if ((template.bitmask & ACCUM_GREEN_SIZE_BIT) != 0 // larger
      && template.accum_green_size () > accum_green_size ()) return false;

    if ((template.bitmask & ACCUM_BLUE_SIZE_BIT) != 0 // larger
      && template.accum_blue_size () > accum_blue_size ()) return false;

    if ((template.bitmask & ACCUM_ALPHA_SIZE_BIT) != 0 // larger
      && template.accum_alpha_size () > accum_alpha_size ()) return false;

    if ((template.bitmask & DOUBLE_BUFFER_BIT) != 0 // exact
      && !double_buffer ()) return false;

    if ((template.bitmask & STERO_BIT) != 0 // exact
      && !stero ()) return false;

    if ((template.bitmask & BUFFER_SIZE_BIT) != 0 // larger
      && template.buffer_size () > buffer_size ()) return false;

    if ((template.bitmask & DEPTH_SIZE_BIT) != 0 // larger
      && template.depth_size () > depth_size ()) return false;

    if ((template.bitmask & STENCIL_SIZE_BIT) != 0 // larger
      && template.stencil_size () > stencil_size ()) return false;

    if ((template.bitmask & AUX_BUFFERS_BIT) != 0 // larger
      && template.aux_buffers () > aux_buffers ()) return false;

    if ((template.bitmask & LEVEL_BIT) != 0 // exact
      && template.level () == level ()) return false;

    return true;
  }  

      
  public String toString () {
    return "#VisualConfig"
      + "\n  visual-id: " + visual_id ()
      + "\n  class: " + clazz ()
      + "\n  rgba: " + rgba ()
      + "\n  red-size: " + red_size ()
      + "\n  green-size: " + green_size ()
      + "\n  blue-size: " + blue_size ()
      + "\n  alpha-size: " + alpha_size ()
      + "\n  accum-red-size: " + accum_red_size ()
      + "\n  accum-green-size: " + accum_green_size ()
      + "\n  accum-blue-size: " + accum_blue_size ()
      + "\n  accum-alpha-size: " + accum_alpha_size ()
      + "\n  double-buffer: " + double_buffer ()
      + "\n  stero: " + stero ()
      + "\n  buffer-size: " + buffer_size ()
      + "\n  depth-size: " + depth_size ()
      + "\n  stencil-size: " + stencil_size ()
      + "\n  aux-buffers: " + aux_buffers ()
      + "\n  level: " + level ()
      + "\n  property-count: " + count;
  }     


  private void set (int i) { bitmask |= 1<<i; }
}


  
