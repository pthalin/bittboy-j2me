package gnu.x11;

import gnu.x11.image.Image;


/** X drawable. */
public abstract class Drawable extends Resource {
  public int width, height;


  /** Predefined. */
  public Drawable (int id) {
    super (id);
  }


  /** Create. */
  public Drawable (Display display) {
    super (display);
  }


  /** Intern. */
  public Drawable (Display display, int id) {
    super (display, id);
  }


  // opcode 62 - copy area
  /**
   * @see <a href="XCopyArea.html">XCopyArea</a>
   */
  public void copy_area (Drawable src, GC gc, int src_x, int src_y, 
    int width, int height, int dst_x, int dst_y) {

    Request request = new Request (display, 62, 7);
    request.write4 (src.id);
    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (src_x);
    request.write2 (src_y);
    request.write2 (dst_x);
    request.write2 (dst_y);
    request.write2 (width);
    request.write2 (height);
    display.send_request (request);    
  }


  // opcode 63 - copy plane
  /**
   * @see <a href="XCopyPlane.html">XCopyPlane</a>
   */
  public void copy_plane (Drawable src, GC gc, int src_x, int src_y, 
    int dst_x, int dst_y, int width, int height, int bit_plane) {
    
    Request request = new Request (display, 63, 8);
    request.write4 (src.id);
    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (src_x);
    request.write2 (src_y);
    request.write2 (dst_x);
    request.write2 (dst_y);
    request.write2 (width);
    request.write2 (height);
    request.write4 (bit_plane);
    display.send_request (request);
  }
    

  public static final int ORIGIN = 0;
  public static final int PREVIOUS = 1;


  // opcode 64 and 65 - poly point/line
  /**
   * This request will be aggregated.
   * 
   * @param coordinate_mode valid:
   * {@link #ORIGIN},
   * {@link #PREVIOUS}
   *
   * @param join if join, draw line; otherwise, draw point
   * 
   * @see <a href="XDrawPoints.html">XDrawPoints</a>
   * @see <a href="XDrawLines.html">XDrawLines</a>
   * @see Request.Aggregate aggregation
   */
  public void poly_dot (GC gc, Point [] points, 
    int coordinate_mode, boolean join) {

    display.send_request (new Request.Poly.Dot (this, gc, points, 
      coordinate_mode, join));
  }


  // opcode 66 - poly segment
  /**
   * This request will be aggregated.
   *
   * @see <a href="XDrawSegments.html">XDrawSegments</a>
   * @see Request.Aggregate aggregation
   */
  public void poly_segment (GC gc, Segment [] segments) {
    display.send_request (new Request.Poly.Segment (this, gc, segments));
  }


  // opcode 67 and 70 - poly (fill) rectangle
  /**
   * This request will be aggregated.
   *
   * @see <a href="XDrawRectangles.html">XDrawRectangles</a>
   * @see <a href="XFillRectangles.html">XFillRectangles</a>
   * @see Request.Aggregate aggregation
   */
  public void poly_rectangle (GC gc, Rectangle [] rectangles, 
    boolean fill) {

    display.send_request (new Request.Poly.Rectangle (
      this, gc, rectangles, fill)); 
  }


  // opcode 68 and 71 - poly (fill) arc
  /**
   * This request will be aggregated.
   *
   * @see <a href="XDrawArcs.html">XDrawArcs</a>
   * @see <a href="XFillArcs.html">XFillArcs</a>
   * @see Request.Aggregate aggregation
   */
  public void poly_arc (GC gc, Arc [] arcs, boolean fill) {
    display.send_request (new Request.Poly.Arc (
      this, gc, arcs, fill)); 
  }


  public static final int COMPLEX = 0;
  public static final int NONCONVEX = 1;
  public static final int CONVEX = 2;


  // opcode 69 - fill poly
  /**
   * This request will be aggregated.
   * 
   * @param shape valid:
   * {@link #COMPLEX},
   * {@link #NONCONVEX},
   * {@link #CONVEX}
   * 
   * @param coordinate_mode valid:
   * {@link #ORIGIN},
   * {@link #PREVIOUS}
   * 
   * @see <a href="XFillPolygon.html">XFillPolygon</a>
   * @see Request.Aggregate aggregation
   */
  public void fill_poly (GC gc, Point [] points, int shape, 
    int coordinate_mode) {

    display.send_request (new Request.Poly.Fill (
      this, gc, points, shape, coordinate_mode));
  }


  // opcode 72 - put image
  public void put_small_image (GC gc, Image image, int y1, int y2, 
    int x, int y) {

    int offset = image.line_byte_count * y1;
    int length = image.line_byte_count * (y2 - y1);

    Request request = new Request (display, 72, image.format,
      6+Data.unit (length));

    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (image.width);
    request.write2 (y2 - y1);
    request.write2 (x);
    request.write2 (y);
    request.write1 (image.left_pad);
    request.write1 (image.pixmap_format.depth ());
    request.write2_unused ();
    request.write1 (image.data, offset, length);
    display.send_request (request);    
  }


  // opcode 73 - get image TODO
  /**
   * @see <a href="XGetImage.html">XGetImage</a>
   */
  public Data image (int x, int y, int width, int height, 
    int plane_mask, int format) {

    Request request = new Request (display, 73, format, 5);
    request.write4 (id);
    request.write2 (x);
    request.write2 (y);
    request.write2 (width);
    request.write2 (height);
    request.write2 (plane_mask);
    
    return display.read_reply (request);
  }


  // opcode 74 - poly text8
  /**
   * @see <a href="XDrawText.html">XDrawText</a>
   */
  public void poly_text (GC gc, int x, int y, Text [] texts) {
    Request request = new Request (display, 74, 
      4+Data.unit (length (texts, 8)));

    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (x);
    request.write2 (y);

    for (int i=0; i<texts.length; i++) {
      if (texts [i].font != null) {
	request.write1 (255);	// font-shift indicator
	request.write4 (texts [i].font.id); // java = MSB
      }

      request.write1 (texts [i].s.length ());
      request.write1 (texts [i].delta);
      request.write1 (texts [i].s);
    }

    display.send_request (request);
  }


  // opcode 75 - poly text16
  /**
   * @see <a href="XDrawText16.html">XDrawText16</a>
   */
  public void poly_text16 (GC gc, int x, int y, Text [] texts) {
    Request request = new Request (display, 75, 
      4+Data.unit (length (texts, 16)));

    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (x);
    request.write2 (y);


    for (int i=0; i<texts.length; i++) {
      if (texts [i].font != null) {
	request.write1 (255);	// font-shift indicator
	request.write4 (texts [i].font.id); // java = MSB
      }

      String s = texts [i].s;

      if (s.charAt (0) > 128) { // non-ascii
	request.write1 (s.length ()/2);
	request.write1 (texts [i].delta);
	request.write1 (s);

      } else {		// ascii
	request.write1 (s.length ());
	request.write1 (texts [i].delta);
	request.write2 (s);
      }
    }

    display.send_request (request);
  }



  // opcode 76 - image text8
  /**
   * @see <a href="XDrawImageString.html">XDrawImageString</a>
   */
  public void image_text (GC gc, int x, int y, String s) {
    Request request = new Request (display, 76, s.length (), 
      4+Data.unit (s));

    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (x);
    request.write2 (y);
    request.write1 (s);
    display.send_request (request);
  }


  // opcode 77 - image text16
  /**
   * @see <a href="XDrawImageString16.html">XDrawImageString16</a>
   */
  public void image_text16 (GC gc, int x, int y, String s) {
    Request request = new Request (display, 77, s.length ()/2, 
      4+Data.unit (s));

    request.write4 (id);
    request.write4 (gc.id);
    request.write2 (x);
    request.write2 (y);
    request.write1 (s);
    display.send_request (request);
  }


  public static final int CURSOR = 0;
  public static final int TILE = 1;
  public static final int STIPPLE = 2;


  // opcode 97 - query best size
  /**
   * @param klass valid:
   * {@link #CURSOR},
   * {@link #TILE},
   * {@link #STIPPLE}
   * 
   * @see <a href="XQueryBestSize.html">XQueryBestSize</a>
   */
  public Size best_size (int klass, int width, int height) {
    Request request = new Request (display, 97, 3);
    request.write4 (id);
    request.write2 (width);
    request.write2 (height);

    Data reply = display.read_reply (request);
    return new Size (reply.read2 (8), reply.read2 (10));
  }


  /**
   * @see #poly_arc(GC, Arc[], boolean)
   */
  public void arc (GC gc, Arc arc, boolean fill) {
    poly_arc (gc, new Arc [] {arc}, fill);
  }


  /**
   * @see #arc(GC, Arc, boolean)
   */
  public void arc (GC gc, int x, int y, int width, int height, 
    int angle1, int angle2, boolean fill) {
    
    arc (gc, new Arc (x, y, width, height, angle1, angle2), fill);
  }


  /**
   * @see #copy_area(Drawable, GC, int, int, int, int, int, int)
   */
  public void copy_area (Drawable src, GC gc) {
    copy_area (src, gc, 0, 0, src.width, src.height, 0, 0);
  }


  /** 
   * @see #line(GC, int, int, int, int)
   */
  public void horizontal (GC gc, int x1, int x2, int y) {
    line (gc, x1, y, x2, y);
  }
  

  /** 
   * @see #segment(GC, Segment)
   */
  public void line (GC gc, int x1, int y1, int x2, int y2) {
    segment (gc, x1, y1, x2, y2);
  }


  /**
   * @see #poly_dot(GC, Point[], int, boolean)
   */
  public void point (GC gc, Point point) {
    poly_dot (gc, new Point [] {point}, ORIGIN, false);
  }


  /**
   * @see #point(GC, Point)
   */
  public void point (GC gc, int x, int y) {
    point (gc, new Point (x, y));
  }


  /**
   * @see Drawable#poly_dot(GC, Point[], int, boolean)
   */
  public void poly_line (GC gc, Point [] points, int coordinate_mode) {
    poly_dot (gc, points, coordinate_mode, true);
  }


  /**
   * @see Drawable#poly_dot(GC, Point[], int, boolean)
   */
  public void poly_point (GC gc, Point [] points, int coordinate_mode) {
    poly_dot (gc, points, coordinate_mode, false);
  }

  
  /**
   * @see <a href="XPutImage.html">XPutImage</a>
   * @see #put_small_image(GC, Image, int, int, int, int)
   */
  public void put_image (GC gc, Image image, int x, int y) {
    int max_data_byte = 4 * display.extended_maximum_request_length - 24;
    int request_height = max_data_byte / image.line_byte_count;
    int rem = image.height % request_height;
    int request_count = image.height / request_height + (rem == 0 ? 0 : 1);

    for (int i=0; i<request_count; i++)
      put_small_image (gc, image, i*request_height,
	Math.min (image.height, (i+1)*request_height), x, y+i*request_height);
  }


  /** 
   * @see #poly_rectangle(GC, Rectangle[], boolean)
   */
  public void rectangle (GC gc, Rectangle rectangle, boolean fill) {
    poly_rectangle (gc, new Rectangle [] {rectangle}, fill);
  }


  /** 
   * @see #rectangle(GC, Rectangle, boolean)
   */      
  public void rectangle (GC gc, int x, int y, int width, 
    int height, boolean fill) {

    rectangle (gc, new Rectangle (x, y, width, height), fill);
  }
      

  /** 
   * @see #rectangle(GC, int, int, int, int, boolean)
   */      
  public void rectangle_clear (GC gc) {
    rectangle (gc, 0, 0, width, height, true);
  }


  /** 
   * @see #line(GC, int, int, int, int)
   */
  public void vertical (GC gc, int x, int y1, int y2) {
    line (gc, x, y1, x, y2);
  }


  /**
   * @see #poly_segment(GC, Segment[])
   */
  public void segment (GC gc, Segment segment) {
    poly_segment (gc, new Segment [] {segment});
  }


  /**
   * @see #segment(GC, Segment)
   */
  public void segment (GC gc, int x1, int y1, int x2, int y2) {
    segment (gc, new Segment (x1, y1, x2, y2));
  }


  /**
   * @see #poly_text(GC, int, int, Text[])
   */
  public void text (GC gc, int x, int y, String s, int delta, Font font) {
    poly_text (gc, x, y, new Text [] {new Text (s, delta, font)});
  }


  /**
   * @see #text(GC, int, int, String, int, Font)
   */
  public void text (GC gc, int x, int y, String s) {
    poly_text (gc, x, y, new Text [] {new Text (s, 0, null)});
  }


  private int length (Text [] texts, int bit) {
    int n = 0;
    for (int i=0; i<texts.length; i++) n += texts [i].length (bit);

    return n;
  }
}

