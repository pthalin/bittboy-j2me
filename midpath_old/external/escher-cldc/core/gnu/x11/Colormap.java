package gnu.x11;


/** X colormap. */
public class Colormap extends Resource {
  /** 
   * Predefined colormap.
   *
   * @see Window#NONE
   */
  public static final Colormap COPY_FROM_PARENT = new Colormap (0);


  /** Predefined. */
  public Colormap (int id) {
    super (id);
  }


  /** Create. */
  public Colormap (Display display) {
    super (display);
  }


  /** Intern. */
  public Colormap (Display display, int id) {
    super (display, id);
  }


  public static final int NONE = 0;
  public static final int ALL = 1;


  // opcode 78 - create colormap
  /**
   * @param alloc valid:
   * {@link #NONE},
   * {@link #ALL}
   *
   * @see <a href="XCreateColormap.html">XCreateColormap</a>
   */   
  public Colormap (Window window, int visual_id, boolean alloc_all) {
    super (window.display);

    Request request = new Request (display, 78, alloc_all, 4);
    request.write4 (id);
    request.write4 (window.id);
    request.write4 (visual_id);
    display.send_request (request);
  }


  // opcode 79 - free colormap
  /**
   * @see <a href="XFreeColormap.html">XFreeColormap</a>
   */
  public void free () {
    Request request = new Request (display, 79, 2);
    request.write4 (id);
    display.send_request (request);
  }


  // opcode 80 - copy colormap and free
  public Colormap copy_and_free (int new_id) {
    Colormap new_map = new Colormap (display, new_id);
    
    Request request = new Request (display, 80, 3);
    request.write4 (new_id);
    request.write4 (id);
    display.send_request (request);
    return new_map;
  }


  // opcode 81 - install colormap
  /**
   * @see <a href="XInstallColormap.html">XInstallColormap</a>
   */
  public void install () {
    Request request = new Request (display, 81, 2);
    request.write4 (id);
    display.send_request (request);
  }


  // opcode 82 - uninstall colormap
  /**
   * @see <a href="XUninstallColormap.html">XUninstallColormap</a>
   */
  public void uninstall () {
    Request request = new Request (display, 82, 2);
    request.write4 (id);
    display.send_request (request);
  }


  public static Object intern (Display display, int id) {
    Object value = display.resources.get (new Integer (id));
    if (value != null) return value;
    return new Colormap (display, id);
  }


  // opcode 84 - alloc color
  /**
   * @see <a href="XAllocColor.html">XAllocColor</a>
   */
  public Color alloc_color (int red, int green, int blue) {
    Request request = new Request (display, 84, 4);
    request.write4 (id);
    request.write2 (red);
    request.write2 (green);
    request.write2 (blue);

    Data reply = display.read_reply (request);
    Color color = new Color (reply.read4 (16));
    color.exact = new RGB (reply.read2 (8), reply.read2 (10), reply.read2 (12));
    return color;
  }

  
  // opcode 85 - alloc named color
  /**
   * @see <a href="XAllocNamedColor.html">XAllocNamedColor</a>
   */  
  public Color alloc_named_color (String name) {
    Request request = new Request (display, 85, 3+Data.unit (name));
    request.write4 (id);
    request.write2 (name.length ());
    request.write2_unused ();
    request.write1 (name);

    Data reply = display.read_reply (request);
    Color color = new Color (reply.read4 (8));
    color.name = name;
    color.exact = new RGB (reply.read2 (12), reply.read2 (14), 
      reply.read2 (16));
    color.visual = new RGB (reply.read2 (18), reply.read2 (20), 
      reply.read2 (22));
    return color;
  }


  /** Reply of {@link #alloc_color_cells(boolean, int, int)}. */
  public static class ColorCellsReply extends Data {
    public ColorCellsReply (Data data) { super (data); }
  }


  // opcode 86 - alloc color cells
  /**
   * @see <a href="XAllocColorCells.html">XAllocColorCells</a>
   */
  public ColorCellsReply alloc_color_cells (boolean contiguous, 
    int color_count, int plane_count) {

    Request request = new Request (display, 86, contiguous, 3);
    request.write4 (id);
    request.write2 (color_count);
    request.write2 (plane_count);
    return new ColorCellsReply (display.read_reply (request));
  }


  // opcode 87 - alloc color planes
  /**
   * @see <a href="XAllocColorPlanes.html">XAllocColorPlanes</a>
   */
  public Data alloc_planes (boolean contiguous, int color_count, 
    int red_count, int green_count, int blue_count) {

    Request request = new Request (display, 87, contiguous, 4);
    request.write4 (id);
    request.write2 (color_count);
    request.write2 (red_count);
    request.write2 (green_count);
    request.write2 (blue_count);
    return display.read_reply (request);
  }


  // opcode 88 - free colors
  /**
   * @see <a href="XFreeColors.html">XFreeColors</a>
   */
  public void free_colors (int [] pixels, int plane_mask) {
    Request request = new Request (display, 88, 3+pixels.length);
    request.write4 (id);
    request.write4 (plane_mask);

    for (int i=0; i<pixels.length; i++)
      request.write4 (pixels [i]);

    display.send_request (request);
  }


  // opcode 89 - store colors
  /**
   * @see <a href="XStoreColors.html">XStoreColors</a>
   */
  public void store_colors (int [] pixels, boolean [] do_reds, 
    boolean [] do_greens, boolean [] do_blues, RGB [] rgbs) {

    Request request = new Request (display, 89, 2+3*pixels.length);
    request.write4 (id);

    for (int i=0; i<pixels.length; i++) {
      request.write4 (pixels [i]);
      request.write2 (rgbs [i].red);
      request.write2 (rgbs [i].green);
      request.write2 (rgbs [i].blue);

      int do_color = 0;
      if (do_reds [i]) do_color |= 0x01;
      if (do_greens [i]) do_color |= 0x02;
      if (do_blues [i]) do_color |= 0x04;

      request.write1 (do_color);
      request.write1_unused ();
    }

    display.send_request (request);
  }


  // opcode 90 - store named color
  /**
   * @see <a href="XStoreNamedColor.html">XStoreNamedColor</a>
   */
  public void store_named_color (int pixel, String name, boolean do_reds, 
    boolean do_greens, boolean do_blues) {

    int do_color = 0;
    if (do_reds) do_color |= 0x01;
    if (do_greens) do_color |= 0x02;
    if (do_blues) do_color |= 0x04;

    Request request = new Request (display, 90, do_color, 4+Data.unit (name));
    request.write4 (id);
    request.write4 (pixel);
    request.write2 (name.length ());
    request.write1 (name);
    display.send_request (request);
  }

    
  // opcode 91 - query colors
  /**
   * @return valid: {@link Enum#next()} of type {@link RGB}
   * @see <a href="XQueryColors.html">XQueryColors</a>
   */
  public Enum colors (int [] pixels) {
    Request request = new Request (display, 91, 2+pixels.length);
    request.write4 (id);

    for (int i=0; i<pixels.length; i++)
      request.write4 (pixels [i]);

    Data reply = display.read_reply (request);

    return new Enum (reply, 32, reply.read2 (8)) {
      public Object next () {
        int r = read2 (offset);
        int g = read2 (offset+2);
        int b = read2 (offset+4);
        RGB rgb = new RGB (r, g, b);

        inc (4);
        return rgb;
      }
    };
  }


  // opcode 92 - lookup color
  /**
   * @see <a href="XLookupColor.html">XLookupColor</a>
   */
  public Color lookup_color (String name) {
    Request request = new Request (display, 92, 3+Data.unit (name));
    request.write4 (id);
    request.write2 (name.length ());
    request.write2_unused ();
    request.write1 (name);

    Data reply = display.read_reply (request);
    Color color = new Color (0);
    color.name = name;
    color.exact = new RGB (reply.read2 (8), reply.read2 (10), reply.read2 (12));
    color.visual = new RGB (reply.read2 (14), reply.read2 (16), 
      reply.read2 (18));
    return color;
  }


  /**
   * @see #alloc_color(int, int, int)
   */
  public Color alloc_color (RGB rgb) {
    return alloc_color (rgb.red, rgb.green, rgb.blue);
  }

  
  /**
   * @see #alloc_color(RGB)
   */
  public Color alloc_color8 (int red8, int green8, int blue8) {
    return alloc_color (RGB.rgb8 (red8, green8, blue8));
  }


  /**
   * @see #alloc_color(int, int, int)
   */
  public Color alloc_random_color (java.util.Random random) {
    return alloc_color (random.nextInt () & 0xffff,
      random.nextInt () & 0xffff,
      random.nextInt () & 0xffff);
  }


  /**
   * @see #alloc_color(int, int, int)
   */
  public Color alloc_random_rainbow_color (java.util.Random random) {
    int hue = random.nextInt (360);
    return alloc_color (RGB.hsv (hue, 1, 1));
  }
}
