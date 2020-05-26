package gnu.x11;


/** X visual. */
public class Visual extends Data {
  public static final Visual COPY_FROM_PARENT = new Visual (4);


  public Visual (int length) { super (length); }
  public Visual (Data data, int offset) { super (data, offset); }
  public int id () { return read4 (0); }
  public int bits_per_rgb_value () { return read1 (5); }
  public int colormap_entries () { return read2 (6); }
  public int red_mask () { return read4 (8); }
  public int green_mask () { return read4 (12); }
  public int blue_mask () { return read4 (16); }


  public static final int STATIC_GRAY = 0;
  public static final int GRAY_SCALE = 1;
  public static final int STATIC_COLOR = 2;
  public static final int PSEUDO_COLOR = 3;
  public static final int TRUE_COLOR = 4;
  public static final int DIRECT_COLOR = 5;


  public static final String [] CLASS_STRINGS = {
    "static-gray", "gray-scale", "static-color", "pseudo-color", 
    "true-color", "direct-color" 
  };


  /**
   * @return valid:
   * {@link #STATIC_GRAY}
   * {@link #STATIC_COLOR}
   * {@link #TRUE_COLOR}
   * {@link #GRAY_SCALE}
   * {@link #PSEUDO_COLOR}
   * {@link #DIRECT_COLOR}
   */
  public int klass () { return read1 (4); }


  public String toString () {
    return "#Visual"
      + "\n  id: " + id ()
      + "\n  class: " + CLASS_STRINGS [klass ()]
      + "\n  bits-per-rgb-value: " + bits_per_rgb_value ()
      + "\n  colormap-entries: " + colormap_entries ()
      + "\n  red-mask: 0x" + Integer.toHexString (red_mask ())
      + "\n  green-mask: 0x " + Integer.toHexString (green_mask ())
      + "\n  blue-mask: 0x" + Integer.toHexString (blue_mask ());
  }
}
