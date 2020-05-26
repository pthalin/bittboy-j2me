package gnu.x11.extension.render;

import gnu.x11.Atom;
import gnu.x11.Data;
import gnu.x11.Drawable;
import gnu.x11.GC;
import gnu.x11.Pixmap;
import gnu.x11.Rectangle;
import gnu.x11.Request;


/** Picture in RENDER. */
public class Picture extends gnu.x11.Resource {
  /**
   * Predefined picture.
   *
   * @see gnu.x11.Window#NONE
   */
  public static final Picture NONE = new Picture (0);


  public Render render;
  public Drawable drawable;


  /** Predefined. */
  public Picture (int id) { super (id); }


  /** ValueList for {@link Picture}. */
  public static class Attributes extends gnu.x11.ValueList {
    public final static Attributes EMPTY = new Attributes ();
    public Attributes () { super (13); }
    public void set_alpha_x_origin (int i) { set (2, i); }
    public void set_alpha_y_origin (int i) { set (3, i); }
    public void set_clip_x_origin (int i) { set (4, i); }
    public void set_clip_y_origin (int i) { set (5, i); }


    /** 
     * @param b default: false
     */
    public void set_repeat (boolean b) { set (0, b); }

    
    /** 
     * @param p possible:
     * {@link Picture#NONE} (default)
     */
    public void set_alpha_map (Picture p) { set (1, p.id); }


    /** 
     * @param p possible:
     * {@link Pixmap#NONE} (default)
     */
    public void set_clip_mask (Pixmap p) { set (6, p.id); }


    /** 
     * @param b default: true
     */
    public void set_graphics_exposures (boolean b) { set (7, b); }


    /** 
     * @param i valid:
     * {@link gnu.x11.GC.Values#CLIP_BY_CHILDREN} (default),
     * {@link gnu.x11.GC.Values#INCLUDE_INTERIORS}
     */
    public void set_subwindow_mode (int i) { set (8, i); }


    public static final int SHARP = 0;
    public static final int SMOOTH = 1;


    /** 
     * @param i valid:
     * {@link #SHARP},
     * {@link #SMOOTH} (default)
     */
    public void set_poly_edge (int i) { set (9, i); }

    
    public static final int PRECISE = 0;
    public static final int IMPRECISE = 1;

    
    /** 
     * @param i valid:
     * {@link #PRECISE} (default),
     * {@link #IMPRECISE}
     */
    public void set_poly_mode (int i) { set (10, i); }


    /** 
     * @param a possible:
     * {@link Atom#NONE} (default)
     */
    public void set_dither (Atom a) { set (11, a.id); }


    /** 
     * @param b default: false
     */
    public void set_component_alpha (boolean i) { set (12, i); }
  }
  

  /** RENDER picture format. */
  public static class Format extends Data {
    public static final int LENGTH = 28;

    // GLX uses *_BIT too
    public static final int ID_BIT = 1<<0;
    public static final int TYPE_BIT = 1<<1;
    public static final int DEPTH_BIT = 1<<2;
    public static final int RED_BIT = 1<<3;
    public static final int RED_MASK_BIT = 1<<4;
    public static final int GREEN_BIT = 1<<5;
    public static final int GREEN_MASK_BIT = 1<<6;
    public static final int BLUE_BIT = 1<<7;
    public static final int BLUE_MASK_BIT = 1<<8;
    public static final int ALPHA_BIT = 1<<9;
    public static final int ALPHA_MASK_BIT = 1<<10;
    public static final int COLORMAP_BIT = 1<<10;


    public int bitmask;
  
  
    public Format () { super (LENGTH); }


    public Format (Data data, int offset) { 
      super (data, offset); 
    }   
  
  
    /** RENDER direct format. */
    public static class Direct extends Data {
      public static final int LENGTH = 16;
      public static final int TYPE = 1;
    

      public int bitmask;

    
      public Direct (Data data, int offset) { 
        super (data, offset); 
      }
    
    
      //-- reading
    
      public int red () { return read2 (0); }
      public int red_mask () { return read2 (2); }
      public int green () { return read2 (4); }
      public int green_mask () { return read2 (6); }
      public int blue () { return read2 (8); }
      public int blue_mask () { return read2 (10); }
      public int alpha () { return read2 (12); }
      public int alpha_mask () { return read2 (14); }
    
    
      //-- writing

      public void set_red (int i) { 
        write2 (0, i); 
        set (RED_BIT); 
      }


      public void set_red_mask (int i) {
        write2 (2, i); 
        set (RED_MASK_BIT); 
      }


      public void set_green (int i) {
        write2 (4, i); 
        set (GREEN_BIT); 
      }


      public void set_green_mask (int i) {
        write2 (6, i); 
        set (GREEN_MASK_BIT);
      }


      public void set_blue (int i) {
        write2 (8, i); 
        set (BLUE_BIT); 
      }


      public void set_blue_mask (int i) {
        write2 (10, i); 
        set (BLUE_MASK_BIT); 
      }


      public void set_alpha (int i) {
        write2 (12, i); 
        set (ALPHA_BIT); 
      }


      public void set_alpha_mask (int i) {
        write2 (14, i); 
        set (ALPHA_MASK_BIT); 
      }
    
    
      public String toString () {
        return "#Direct"
          + "\n  red: " + red ()
          + "\n  red-mask: " + red_mask ()
          + "\n  green: " + green ()
          + "\n  green-mask: " + green_mask ()
          + "\n  blue: " + blue ()
          + "\n  blue-mask: " + blue_mask ()
          + "\n  alpha: " + alpha ()
          + "\n  alpha-mask: " + alpha_mask ();
      }


      private void set (int mask) { bitmask |= mask; }
    }


    //-- reading
  
    public int id () { return read4 (0); }
    public int depth () { return read1 (5); }
    public int colormap_id () { return read4 (24); }
  
    
    /** 
     * @return valid:
     * {@link Direct#TYPE}
     */
    public int type () { 
      return read1 (4); 
    }
  
  
    public Direct direct_format () {
      return new Direct (this, 8);
    }
  
  
    //-- writing

    public void clear () {
      bitmask = 0; 
      direct_format ().bitmask = 0;
    }


    public void set_id (int i) {
      write4 (0, i); 
      set (ID_BIT); 
    }


    public void set_depth (int i) {
      write1 (5, i); 
      set (DEPTH_BIT); 
    }
  
    
    /**
     * @param i valid:
     * {@link Direct#TYPE}
     */
    public void set_type (int i) {
      write1 (4, i); 
      set (TYPE_BIT); 
    }
  
  
    public boolean match (Format template) {
      if ((template.bitmask & ID_BIT) != 0 
        && template.id () != id ()) return false;
      if ((template.bitmask & TYPE_BIT) != 0 
        && template.type () != type ()) return false;
      if ((template.bitmask & DEPTH_BIT) != 0 
        && template.depth () != depth ()) return false;
      if ((template.bitmask & COLORMAP_BIT) != 0 
        && template.colormap_id () != colormap_id ()) return false;
  
      
      Direct df0 = direct_format ();
      Direct df1 = template.direct_format ();
  
      if ((df1.bitmask & RED_BIT) != 0
        && df0.red () != df1.red ()) return false;
      if ((df1.bitmask & RED_MASK_BIT) != 0
        && df0.red_mask () != df1.red_mask ()) return false;
      if ((df1.bitmask & GREEN_BIT) != 0 
        && df0.green () != df1.green ()) return false;
      if ((df1.bitmask & GREEN_MASK_BIT) != 0 &&
        df0.green_mask () != df1.green_mask ()) return false;
      if ((df1.bitmask & BLUE_BIT) != 0 &&
        df0.blue () != df1.blue ()) return false;
      if ((df1.bitmask & BLUE_MASK_BIT) != 0 &&
        df0.blue_mask () != df1.blue_mask ()) return false;
      if ((df1.bitmask & ALPHA_BIT) != 0 &&
        df0.alpha () != df1.alpha ()) return false;
      if ((df1.bitmask & ALPHA_MASK_BIT) != 0 &&
        df0.alpha_mask () != df1.alpha_mask ()) return false;
  
      return true;
    }
    
  
    public static final String [] TYPE_STRINGS = {"indexed", "direct"};
  
  
    public String toString () {
      return "#Format"
        + "\n  id: " + id ()
        + "\n  type: " + TYPE_STRINGS [type ()]
        + "\n  depth: " + depth ()
        + "\n#Format: " + direct_format ()
        + "\n  colormap-id: " + colormap_id ()
        + "\n  bitmask: " + bitmask;
    }


    private void set (int mask) { bitmask |= mask; }
  }


  // render opcode 4 - create picture
  /**
   * @see <a href="XRenderCreatePicture.html">XRenderCreatePicture</a>
   * @see Render#create_picture(Drawable, Picture.Format, 
   * Picture.Attributes)
   */
  public Picture (Render render, Drawable drawable, Format format, 
    Attributes attr) {
    
    super (render.display);
    this.render = render;

    Request request = new Request (display, render.major_opcode, 4,
      5+attr.count ());
    request.write4 (id);
    request.write4 (drawable.id);
    request.write4 (format.id ());
    request.write4 (attr.bitmask);
    attr.write (request);
    display.send_request (request);
  }


  // render opcode 5 - change picture
  /**
   * @see <a href="XRenderChangePicture.html">XRenderChangePicture</a>
   */
  public void change (Attributes attr) {
    Request request = new Request (display, render.major_opcode, 5,
      5+attr.count ());
    request.write4 (id);
    request.write4 (attr.bitmask);
    attr.write (request);
    display.send_request (request);    
  }


  // render opcode 6 - set picture clip rectangles
  /**
   * @see <a href="XRenderSetPictureClipRectangles.html">
   * XRenderSetPictureClipRectangles</a>
   */
  public void set_clip_rectangles (int x_origin, int y_origin,
    Rectangle [] rectangles) {

    Request request = new Request (display, render.major_opcode, 7,
      3+2*rectangles.length);
    request.write4 (id);

    for (int i=0; i<rectangles.length; i++) {
      request.write2 (rectangles [i].x);
      request.write2 (rectangles [i].y);
      request.write2 (rectangles [i].width);
      request.write2 (rectangles [i].height);
    }
    display.send_request (request);
  }


  // render opcode 7 - free picture
  /**
   * @see <a href="XRenderFreePicture.html">XRenderFreePicture</a>
   */
  public void free () {
    Request request = new Request (display, render.major_opcode, 7, 2);
    request.write4 (id);
    display.send_request (request);
  }


  // render opcode 8 - scale
  public void scale (int color_scale, int alpha_scale, 
    Picture src, int src_x, int src_y, 
    int dst_x, int dst_y, int width, int height) {

    Request request = new Request (display, render.major_opcode, 9, 8);
    request.write4 (src.id);
    request.write4 (id);
    request.write4 (color_scale);
    request.write4 (alpha_scale);
    request.write2 (src_x);
    request.write2 (src_y);
    request.write2 (dst_x);
    request.write2 (dst_y);
    request.write2 (width);
    request.write2 (height);
    display.send_request (request);
  }


  // render opcode 26 - fill rectangles
  /**
   * @see <a href="XRenderFillRectangle.html">XRenderFillRectangle</a>
   */
  public void fill_rectangle (int op, Color color, int x, int y, 
    int width, int height) {

    Request request = new Request (display, render.major_opcode, 26, 7);
    request.write1 (op);
    request.write3_unused ();
    request.write4 (id);
    request.write2 (x);
    request.write2 (y);
    request.write2 (width);
    request.write2 (height);
    request.write2 (color.red);
    request.write2 (color.green);
    request.write2 (color.blue);
    request.write2 (color.alpha);
    display.send_request (request);
  }
}
