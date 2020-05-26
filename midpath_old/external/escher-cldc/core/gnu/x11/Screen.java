package gnu.x11;


/** X Screen. */
public class Screen extends Data {
  public Display display;


  public Screen (Display display, Data data, int offset) { 
    super (data, offset); 
    this.display = display;
  } 


  public int root_id () { return read4 (0); }
  public int default_colormap_id () { return read4 (4); }
  public int white_pixel () { return read4 (8); }
  public int black_pixel () { return read4 (12); }
  public int width () { return read2 (20); }
  public int height () { return read2 (22); }
  public int width_mm () { return read2 (24); }
  public int height_mm () { return read2 (26); }
  public int min_installed_maps () { return read2 (28); }
  public int max_installed_maps () { return read2 (30); }
  public int root_visual_id () { return read4 (32); }
  public boolean save_unders () { return read_boolean (37); }
  public int root_depth () { return read1 (38); }
  public int allowed_depth_count () { return read1 (39); }
  public Color white () { return new Color (white_pixel ()); }
  public Color black () { return new Color (black_pixel ()); }


  public static final int NEVER = 0;
  public static final int WHEN_MAPPED = 1;
  public static final int ALWAYS = 2;


  public static final String [] BACKING_STORES_STRINGS = {
    "never", "when-mapped", "always"
  };


  /**
   * @return valid:
   * {@link #NEVER}
   * {@link #WHEN_MAPPED}
   * {@link #ALWAYS}
   */
  public int backing_stores () { return read1 (36); }


  public Colormap default_colormap () { 
    return new Colormap (display, default_colormap_id ());
  }


  private GC default_gc_cache;


  /** Shared, read-only resource in general. */
  public GC default_gc () {
    if (default_gc_cache == null) {
      GC.Values gv = new GC.Values ();
      gv.set_foreground (black_pixel ());
      gv.set_background (white_pixel ());

      default_gc_cache = new GC (display, gv);
    }

    return default_gc_cache;
  }


  /**
   * @return valid:
   * {@link Enum#elt(int)},
   * {@link Enum#next()} of type {@link Depth}
   */
  public Enum depths () {
    return new Enum (this, 40, allowed_depth_count ()) {
      public Object elt (int i) {
        // probe i-th element linearly
        int saved_offset = this.offset;
        this.offset = start_offset;

        for (int j=0; j<i; j++)
          this.offset += 8 + this.read2 (2)*24;
              
        Depth depth = new Depth (this, 0);
        this.offset = saved_offset;
        return depth;
      }


      public Object next () {
        Depth depth = new Depth (this, 0);
        inc (depth.length ());
        return depth;
      }
    };
  }


  public int length () {
    int pos = 40;

    for (int i=0; i<allowed_depth_count (); i++) {
      int visual_count = read2 (pos + 2);
      pos += 8 + visual_count*24;
    }

    return pos;
  }


  public Window root () { 
    return (Window) Window.intern (display, root_id ());
  }


  public String toString () { 
    return "#Screen"
      + "\n  root-id: " + root_id ()
      + "\n  default-colormap-id: " + default_colormap_id ()
      + "\n  white-pixel: " + white_pixel ()
      + "\n  black-pixel: " + black_pixel ()
      + "\n  width: " + width ()
      + "\n  height: " + height ()
      + "\n  width-mm: " + width_mm ()
      + "\n  height-mm: " + height_mm ()
      + "\n  min-installed-maps: " + min_installed_maps ()
      + "\n  max-installed-maps: " + max_installed_maps ()
      + "\n  root-visual-id: " + root_visual_id ()
      + "\n  backing-stores: " + BACKING_STORES_STRINGS [backing_stores ()]
      + "\n  save-unders: " + save_unders ()
      + "\n  root-depth: " + root_depth ()
      + "\n  allowed-depth-count: " + allowed_depth_count ()
      + depths ().to_string (Enum.NEXT, "\n#Screen: ");
  }
}
