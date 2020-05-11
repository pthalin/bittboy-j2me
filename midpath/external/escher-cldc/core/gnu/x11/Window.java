package gnu.x11;

import gnu.x11.event.Event;
import gnu.x11.event.ClientMessage;


/** X window. */
public class Window extends Drawable {
  /**
   * Predefined windows.
   *
   * <p>All predefined resources are not "properly" initialzied, in the
   * sense that member variable <code>display</code> is <code>null</code>. 
   * That is, they are not connected to any X server, and cannot be used
   * for server interaction (because <code>display == null</code>,
   * resulting in <code>NullPointerException</code>).
   *
   * <p>For special operations like setting selection owner to
   * {@link #NONE}, do:
   *
   * <pre><code>
   *   Window.NONE.display = ...;
   *   Window.NONE.set_selection_owner (...);
   * </code></pre>
   *
   * <p>We could make these predefined resources members of
   * <code>Display</code> class, and could initialize them properly for
   * each <code>Display</code> instance. It would prevent programmers from
   * the mistake described above. However, predefined resources are not
   * designed in the first place for server interaction. For example, you
   * cannot draw a line on <code>Window.POINTER_WINDOW</code>. Second, it
   * is a better classification to put predefined resources in their
   * respective classes. Third, it is cheaper to have predefined resources
   * as <code>static final</code> objects.
   *
   * <p>Note also that, consequently, predefined resources are not interned
   * to <code>Display.resource</code>. For example, <code>Window.intern
   * (this, 0)</code> will not return static variable {@link #NONE}. 
   * Instead it creates a new <code>Window</code> object which sequential
   * intern calls will return. As a side effect, <code>Window</code>
   * equality should not be tested with <code>==</code> operator; it should
   * be tested easily as <code>this_window.id == that_window.id</code>.
   */
  public static final Window NONE = new Window (0);


  public int x, y;
  public Window parent;


  /** Predefined. */
  public Window (int id) { super (id); }

  /** Intern. */
  public Window (Display display, int id) { super (display, id); }


  /**
   * @see #Window(Window, int, int, int, int)
   */
  public Window (Window parent, Rectangle geometry) {
    this (parent, geometry.x, geometry.y, geometry.width, geometry.height);
  }


  /** X window attributes. */
  public static class Attributes extends ValueList {
    public final static Attributes EMPTY = new Attributes ();
    public Attributes () { super (15); }


    /** 
     * @param p possible:
     * {@link Pixmap#NONE} (default),
     * {@link Pixmap#PARENT_RELATIVE} 
     */
    public void set_background (Pixmap p) { set (0, p.id); }


    /** 
     * @see #set_background(int)
     */
    public void set_background (Color c) { set_background (c.pixel); }


    public void set_background (int pixel) { set (1, pixel); }


    /** 
     * @param p possible: {@link Pixmap#COPY_FROM_PARENT} (default)
     */
    public void set_border (Pixmap p) { set (2, p.id); }


    /** 
     * @see #set_border(int)
     */
    public void set_border (Color c) { set_border (c.pixel); }


    public void set_border (int pixel) { set (3, pixel); }

 
    public static final int FORGET = 0;
    public static final int NORTH_WEST = 1;
    public static final int NORTH = 2;
    public static final int NORTH_EAST = 3;
    public static final int WEST = 4;
    public static final int CENTER = 5;
    public static final int EAST = 6;
    public static final int SOUTH_WEST = 7;
    public static final int SOUTH = 8;
    public static final int SOUTH_EAST = 9;
    public static final int STATIC = 10;


    /** 
     * @param i valid:
     * {@link #FORGET} (default),
     * {@link #NORTH_WEST},
     * {@link #NORTH},
     * {@link #NORTH_EAST},
     * {@link #WEST},
     * {@link #CENTER},
     * {@link #EAST},
     * {@link #SOUTH_WEST},
     * {@link #SOUTH},
     * {@link #SOUTH_EAST},
     * {@link #STATIC}
     */
    public void set_win_gravity (int i) { set (5, i); }


    public static final int NOT_USEFUL = 0;
    public static final int WHEN_MAPPED = 1;
    public static final int ALWAYS = 2;


    /**
     * @param i valid:
     * {@link #NOT_USEFUL}(default),
     * {@link #WHEN_MAPPED},
     * {@link #ALWAYS}
     */
    public void set_backing_store (int i) { set (6, i); }


    /** 
     * @param i default: all ones
     */
    public void set_backing_plane (int i) { set (7, i); }


    /** 
     * #set_backing(int)
     */
    public void set_backing (Color c) { set_backing (c.pixel); }


    /** 
     * @param i default: zero
     */
    public void set_backing (int pixel) { set (8, pixel); }


    /** 
     * @param b default: false
     */
    public void set_override_redirect (boolean b) { set (9, b); }


    /** 
     * @param b default: false
     */
    public void set_save_under (boolean b) { set (10, b); }


    /** 
     * @param i default: {}
     */
    public void set_event_mask (int i) { set (11, i); }


    public void add_event_mask (int i) { 
      set_event_mask (event_mask () | i); 
    }


    public int event_mask () { return data [11]; }


    /** 
     * @param i default: {}
     */
    public void set_do_not_propagate_mask (int i) { set (12, i); }


    /** 
     * @param c possible: {@link Colormap#COPY_FROM_PARENT} (default)
     */
    public void set_colormap (Colormap c) { set (13, c.id); }


    /**
     * @param c possible: {@link Cursor#NONE}
     */
    public void set_cursor (Cursor c) { set (14, c.id); }


    public Object clone () {
      Attributes attr = new Attributes ();
      attr.copy (this);
      return attr;
    }
  }


  /**
   * @see #Window(Window, int, int, int, int, int, Window.Attributes)
   */
  public Window (Window parent, Rectangle geometry, int border_width,
    Attributes attr) {
    
    this (parent, geometry.x, geometry.y, geometry.width, geometry.height,
      border_width, attr);
  }


  /**
   * Initialize member fields only without creating object in X server.
   */
  public Window (Window parent, int x, int y, int width, int height) {
    super (parent.display);

    this.parent = parent;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }


  /**
   * @see #create(int, int, int, int, Window.Attributes)
   */
  public Window (Window parent, int x, int y, int width, int height, 
    int border_width, Attributes attr) {
    
    this (parent, x, y, width, height);
    create (border_width, attr);
  }


  public static final int COPY_FROM_PARENT = 0;
  public static final int INPUT_OUTPUT = 1;
  public static final int INPUT_ONLY = 2;


  // opcode 1 - create window
  /**
   * @param depth possible: {@link #COPY_FROM_PARENT}
   
   * @param klass valid:
   * {@link #COPY_FROM_PARENT},
   * {@link #INPUT_OUTPUT},
   * {@link #INPUT_ONLY}
   *
   * @param visual_id possible: {@link #COPY_FROM_PARENT}
   * @see <a href="XCreateWindow.html">XCreateWindow</a>
   */  
  public void create (int border_width, int depth, int klass, int visual_id,
    Attributes attr) {

    Request request = new Request (display, 1, depth, 8+attr.count ());
    request.write4 (id);
    request.write4 (parent.id);
    request.write2 (x);
    request.write2 (y);
    request.write2 (width);
    request.write2 (height);
    request.write2 (border_width);
    request.write2 (klass);
    request.write4 (visual_id);
    request.write4 (attr.bitmask);
    attr.write (request);
    display.send_request (request);
  }


  /**
   * @see #create(int, int, int, int, Window.Attributes)
   */  
  public void create (int border_width, Attributes attr) {
    create (border_width, COPY_FROM_PARENT, COPY_FROM_PARENT,
      COPY_FROM_PARENT, attr);
  }


  /**
   * @see #create(int, Window.Attributes)
   */  
  public void create () {
    create (0, Attributes.EMPTY);
  }


  // opcode 2 - change window attributes
  /**
   * This request will be aggregated.
   *
   * @see <a href="XChangeAttributes.html">
   * XChangeAttributes</a>
   *
   * @see Request.Aggregate aggregation
   */  
  public void change_attributes (Attributes attr) {
    display.send_request (new Request.ValueList (display, 2, 3, id, attr));
  }


  /** Reply of {@link #attributes()}. */
  public static class AttributesReply extends Data {
    public AttributesReply (Data data) { super (data); }
  
  
    public static final int UNMAPPED = 0;
    public static final int UNVIEWABLE = 1;
    public static final int VIEWABLE = 2;
  
  
    /**
     * @return valid:
     * {@link #UNMAPPED},
     * {@link #UNVIEWABLE},
     * {@link #VIEWABLE}
     */
    public int map_state () { return read1 (26); }
  
  
    public boolean override_redirect () { return read_boolean (27); }
  }
  
  
  // opcode 3 - get window attributes
  /**
   * @see <a href="XGetAttributes.html">XGetAttributes</a>
   */
  public AttributesReply attributes () {
    Request request = new Request (display, 3, 2);
    request.write4 (id);
    return new AttributesReply (display.read_reply (request));
  }


  // opcode 4 - destroy window
  /**
   * @see <a href="XDestroyWindow.html">XDestroyWindow</a>
   */
  public void destroy () {
    Request request = new Request (display, 4, 2);
    request.write4 (id);
    display.send_request (request);
  }


  // opcode 5 - destroy subwindows
  /**
   * @see <a href="XDestroySubwindows.html">XDestroySubwindows</a>
   */
  public void destroy_subwindows () {
    Request request = new Request (display, 5, 2);
    request.write4 (id);
    display.send_request (request);
  }  


  public static final int INSERT = 0;
  public static final int DELETE = 1;


  // opcode 6 - change save set
  /**
   * @param mode valid:
   * {@link #INSERT},
   * {@link #DELETE}
   *
   * @see <a href="XChangeSaveSet.html">XChangeSaveSet</a>
   */  
  public void change_save_set (boolean mode) {
    Request request = new Request (display, 6, mode, 2);
    request.write4 (id);
    display.send_request (request);    
  }


  // opcode 7 - reparent window
  /**
   * @see <a href="XReparentWindow.html">XReparentWindow</a>
   */
  public void reparent (Window parent, int x, int y) {
    Request request = new Request (display, 7, 4);
    request.write4 (id);
    request.write4 (parent.id);
    request.write2 (x);
    request.write2 (y);
    display.send_request (request);
  }


  // opcode 8 - map window
  /**
   * @see <a href="XMapWindow.html">XMapWindow</a>
   */
  public void map () {
    Request request = new Request (display, 8, 2);
    request.write4 (id);
    display.send_request (request);
  }


  // opcode 9 - map subwindows
  /**
   * @see <a href="XMapSubwindows.html">XMapSubwindows</a>
   */
  public void map_subwindows () {
    Request request = new Request (display, 9, 2);
    request.write4 (id);
    display.send_request (request);
  }  


  // opcode 10 - unmap window
  /**
   * @see <a href="XUnmapWindow.html">XUnmapWindow</a>
   */
  public void unmap () {
    Request request = new Request (display, 10, 2);
    request.write4 (id);
    display.send_request (request);
  }


  // opcode 11 - unmap subwindows
  /**
   * @see <a href="XUnmapSubwindows.html">XUnmapSubwindows</a>
   */
  public void unmap_subwindows () {
    Request request = new Request (display, 11, 2);
    request.write4 (id);
    display.send_request (request);
  }


  /** X window changes. */
  public static class Changes extends ValueList {
    public Changes () { super (7); } 
    public void x (int i) { set (0, i); }
    public void y (int i) { set (1, i); }
    public void width (int i) { set (2, i); }
    public void height (int i) { set (3, i); }
    public void border_width (int i) { set (4, i); }
    public void sibling_id (int i) { set (5, i); }
    public void sibling (Window window) { sibling_id (window.id); }
  
  
    public static final int ABOVE = 0;
    public static final int BELOW = 1;
    public static final int TOP_IF = 2;
    public static final int BOTTOM_IF = 3;
    public static final int OPPOSITE = 4;
  
  
    /**
     * @param i valid:
     * {@link #ABOVE},
     * {@link #BELOW},
     * {@link #TOP_IF},
     * {@link #BOTTOM_IF},
     * {@link #OPPOSITE}
     */
    public void stack_mode (int i) { set (6, i); }
  
  
    public Object clone () {
      Changes changes = new Changes ();
      changes.copy (this);
      return changes;
    }
  }


  // opcode 12 - configure window
  /**
   * This request will be aggregated.
   *
   * @see <a href="XConfigureWindow.html">XConfigureWindow</a>
   * @see Request.Aggregate aggregation
   */
  public void configure (Changes changes) {
    display.send_request (new Request.ValueList (
      display, 12, 3, id, changes));
  }


  public static final int RAISE_LOWEST = 0;
  public static final int LOWER_HIGHEST = 1;


  // opcode 13 - circulate window
  /**
   * @param direction valid:
   * {@link #RAISE_LOWEST},
   * {@link #LOWER_HIGHEST}
   * 
   * @see <a href="XCirculateSubwindows.html">XCirculateSubwindows</a>
   */
  public void circulate_window (int direction) {
    Request request = new Request (display, 13, direction, 2);
    request.write4 (id);
    display.send_request (request);
  }


  /** Reply of {@link #geometry()}. */
  public static class GeometryReply extends Data {
    public Display display;

    public GeometryReply (Display display, Data data) { 
      super (data);
      this.display = display;
    }


    public int depth () { return read1 (1); }
    public int root_id () { return read4 (8); }
    public Window root () { return (Window) intern (display, read4 (8)); }
    public int x () { return read2 (12); }
    public int y () { return read2 (14); }
    public int width () { return read2 (16); }
    public int height () { return read2 (18); }
    public int border_width () { return read2 (20); }
  }
  
  
  // opcode 14 - get geometry
  /**
   * @see <a href="XGetGeometry.html">XGetGeometry</a>
   */
  public GeometryReply geometry () {
    Request request = new Request (display, 14, 2);
    request.write4 (id);

    Data reply = display.read_reply (request);
    GeometryReply gr = new GeometryReply (display, reply);

    // update our variables
    x = gr.x ();
    y = gr.y ();
    width = gr.width ();
    height = gr.height ();

    return gr;
  }


  /** Reply of {@link #tree()}. */
  public static class TreeReply extends Data {
    public Display display;

    public TreeReply (Display display, Data data) { 
      super (data); 
      this.display = display;
    }


    public int root_id () { return read4 (8); }
    public int parent_id () { return read4 (12); }
    public int children_count () { return read2 (16); }


    public Window root () {
      return (Window) intern (display, root_id ());
    }


    /**
     * @return possible: {@link #NONE}
     */
    public Window parent () {
      return (Window) intern (display, parent_id ());
    }


    /**
     * @return valid:
     * {@link Enum#next()} of type {@link Window},
     * {@link Enum#next4()}
     */
    public Enum children () {
      return new Enum (this, 32, children_count ()) {
        public Object next () {
          return intern (display, next4 ());
        }
      };
    }
  }


  // opcode 15 - query tree
  /**
   * @see <a href="XQueryTree.html">XQueryTree</a>
   */
  public TreeReply tree () {
    Request request = new Request (display, 15, 2);
    request.write4 (id);
    return new TreeReply (display, display.read_reply (request));
  }


  public static final int REPLACE = 0;
  public static final int PREPEND = 1;
  public static final int APPEND = 2;


  // opcode 18 - change property
  /**
   * Extra parameters (offset and data_format) are used to support Data
   * class as parameter for writing. See set_wm_normal_hints ().
   *
   * @param mode valid:
   * {@link #REPLACE},
   * {@link #PREPEND},
   * {@link #APPEND}
   * 
   * @param format: valid:
   * <code>8</code>,
   * <code>16</code>,
   * <code>32</code>
   *
   * @param data_format: valid:
   * <code>8</code>,
   * <code>16</code>,
   * <code>32</code>
   * 
   * @see <a href="XChangeProperty.html">XChangeProperty</a>
   */ 
  public void change_property (int mode, int n, Atom property, Atom type,
    int format, Object data, int offset, int data_format) {

    Request request = new Request (display, 18, mode, 6+Data.unit (n*format/8));
    request.write4 (id);
    request.write4 (property.id);
    request.write4 (type.id);
    request.write1 (format);
    request.write3_unused ();
    request.write4 (n);		// data length in format unit

    // data
    switch (data_format) {
    case 8: request.write1 ((byte []) data, offset); break;    
    case 16: request.write2 ((int []) data, offset); break;
    case 32: request.write4 ((int []) data, offset); break;
    }
    display.send_request (request);
  }


  // opcode 19 - delete property
  /**
   * @see <a href="XDeleteProperty.html">XDeleteProperty</a>
   */
  public void delete_property (Atom property) {
    Request request = new Request (display, 19, 3);
    request.write4 (id);
    request.write4 (property.id);
    display.send_request (request);
  }


  /** Reply of {@link #property(boolean, Atom, Atom, int, int)}. */
  public static class PropertyReply extends Data {
    public PropertyReply (Data data) { super (data); }
    public int format () { return read1 (1); }
    public int type_id () { return read4 (8); }
    public int byte_after () { return read4 (12); }
    public int length () { return read4 (16); }
  
  
    public Enum items () {
      return new Enum (this, 32, length ());
    }
  }
  
  
  // opcode 20 - get property
  /**
   * @see <a href="XGetWindowProperty.html">XGetWindowProperty</a>
   */
  public PropertyReply property (boolean delete, Atom property, Atom type, 
    int offset, int length) {
    
    Request request = new Request (display, 20, delete, 6);
    request.write4 (id);
    request.write4 (property.id);
    request.write4 (type.id);
    request.write4 (offset);
    request.write4 (length);
    return new PropertyReply (display.read_reply (request));
  }


  // opcode 21 - list properties
  /**
   * @return valid:
   * {@link Enum#next()} of type {@link Atom},
   * {@link Enum#next4()}
   *
   * @see <a href="XRotateWindowProperties.html">
   * XRotateWindowProperties</a>
   */
  public Enum properties () {
    Request request = new Request (display, 21, 2);
    request.write4 (id);

    Data reply = display.read_reply (request);
    return new Enum (reply, 32, reply.read2 (8)) {
      public Object next () {
        return Atom.intern (display, next4 ());
      }
    };
  }


  // opcode 22 - set selection owner
  /**
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XSetSelectionOwner.html">XSetSelectionOwner</a>
   */
  public void set_selection_owner (Atom selection, int time) {
    Request request = new Request (display, 22, 4);
    request.write4 (id);
    request.write4 (selection.id);
    request.write4 (time);
    display.send_request (request);
  }


  // opcode 24 - convert selection
  /**
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XConvertSelection.html">XConvertSelection</a>
   */
  public void convert_selection (Atom selection, Atom target, 
    Atom property, int time) {

    Request request = new Request (display, 24, 6);
    request.write4 (id);
    request.write4 (selection.id);
    request.write4 (target.id);
    request.write4 (property.id);
    request.write4 (time);
    display.send_request (request);
  }


  // opcode 25 - send event
  /**
   * @see <a href="XSendEvent.html">XSendEvent</a>
   */
  public void send_event (boolean propagate, int event_mask, 
    Event event) {

    Request request = new Request (display, 25, propagate, 11);
    request.write4 (id);
    request.write4 (event_mask);
    request.write1 (event.data);
    display.send_request (request);
  }


  public static final int SYNCHRONOUS = 0;
  public static final int ASYNCHRONOUS = 1;


  public static final int SUCCESS = 0;
  public static final int ALREADY_GRABBED = 1;
  public static final int INVALID_TIME = 2;
  public static final int NOT_VIEWABLE = 3;
  public static final int FROZEN = 4;


  // opcode 26 - grab pointer
  /**
   * @param pointer_mode valid:
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param keyboard_mode valid:
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param confine_to possible: {@link #NONE}
   * @param cursor possible: {@link Cursor#NONE}
   * @param time possible: {@link Display#CURRENT_TIME}
   *
   * @return valid:
   * {@link #SUCCESS},
   * {@link #ALREADY_GRABBED},
   * {@link #FROZEN},
   * {@link #INVALID_TIME},
   * {@link #NOT_VIEWABLE}
   *
   * @see <a href="XGrabPointer.html">XGrabPointer</a>
   */
  public int grab_pointer (boolean owner_events, int event_mask, 
    int pointer_mode, int keyboard_mode, Window confine_to, Cursor cursor,
    int time) { 

    Request request = new Request (display, 26, owner_events, 6);
    request.write4 (id);
    request.write2 (event_mask);
    request.write1 (pointer_mode);
    request.write1 (keyboard_mode);
    request.write4 (confine_to.id);
    request.write4 (cursor.id);
    request.write4 (time);
    return display.read_reply (request).read1 (1);  
  }


  public static final int ANY_BUTTON = 0;
  public static final int ANY_MODIFIER = 0x8000;


  // opcode 28 - grab button
  /**
   * @param button possible: {@link #ANY_BUTTON}
   * @param modifiers possible: {@link #ANY_MODIFIER}
   * @param pointer_mode valid:
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param keyboard_mode valid:
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param confine_to possible: {@link #NONE}
   * @param cursor possible: {@link Cursor#NONE}
   * @see <a href="XGrabButton.html">XGrabButton</a>
   */
  public void grab_button (int button, int modifiers, boolean owner_events,
    int event_mask, int pointer_mode, int keyboard_mode, Window confine_to,
    Cursor cursor) { 
    
    Request request = new Request (display, 28, owner_events, 6);
    request.write4 (id);
    request.write2 (event_mask);
    request.write1 (pointer_mode);
    request.write1 (keyboard_mode);
    request.write4 (confine_to.id);
    request.write4 (cursor.id);
    request.write1 (button);
    request.write1_unused ();
    request.write2 (modifiers);
    display.send_request (request);
  }


  // opcode 29 - ungrab button
  /**
   * @param button possible: {@link #ANY_BUTTON}
   * @param modifiers possible: {@link #ANY_MODIFIER}
   * @see <a href="XUngrabButton.html">XUngrabButton</a>
   */
  public void ungrab_button (int button, int modifiers) {
    Request request = new Request (display, 29, button, 3);
    request.write4 (id);
    request.write2 (modifiers);
    display.send_request (request);
  }
  

  // opcode 31 - grab keyboard
  /**
   * @param pointer_mode valid:
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param keyboard_mode valid: 
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param time possible: {@link Display#CURRENT_TIME}
   *
   * @return valid:
   * {@link #SUCCESS},
   * {@link #ALREADY_GRABBED},
   * {@link #FROZEN},
   * {@link #INVALID_TIME},
   * {@link #NOT_VIEWABLE}
   *
   * @see <a href="XGrabKeyboard.html">XGrabKeyboard</a>
   */
  public int grab_keyboard  (boolean owner_events, int pointer_mode, 
    int keyboard_mode, int time) { 

    Request request = new Request (display, 31, owner_events, 4);
    request.write4 (id);
    request.write4 (time);
    request.write1 (pointer_mode);
    request.write1 (keyboard_mode);
    return display.read_reply (request).read1 (1);
  }

  
  // opcode 33 - grab key
  /**
   * @param modifiers possible: {@link #ANY_MODIFIER}
   * @param pointer_mode valid: 
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @param keyboard_mode valid:
   * {@link #SYNCHRONOUS},
   * {@link #ASYNCHRONOUS}
   * 
   * @see <a href="XGrabKey.html">XGrabKey</a>
   */
  public void grab_key (int keysym, int modifiers, boolean owner_events, 
    int pointer_mode, int keyboard_mode) {

    int keycode = display.input.keysym_to_keycode (keysym);

    Request request = new Request (display, 33, owner_events, 4);
    request.write4 (id);
    request.write2 (modifiers);
    request.write1 (keycode);
    request.write1 (pointer_mode);
    request.write1 (keyboard_mode);
    display.send_request (request);
  }


  public static final int ANY_KEY = 0;  


  // opcode 34 - ungrab key
  /**
   * @param key possible: {@link #ANY_KEY}
   * @param modifiers possible: {@link #ANY_MODIFIER}
   * @see <a href="XUngrabKey.html">XUngrabKey</a>
   */
  public void ungrab_key (int keysym, int modifiers) {
    int keycode = keysym == 0 ? 0 
      : display.input.keysym_to_keycode (keysym);

    Request request = new Request (display, 34, keycode, 3);
    request.write4 (id);
    request.write2 (modifiers);
    display.send_request (request);
  }


  /** Reply of {@link #pointer()}. */
  public static class PointerReply extends Data {
    public Display display;

    public PointerReply (Display display, Data data) {
      super (data);
      this.display = display;
    }
  
  
    public int child_id () { return read4 (12); }
    public int root_x () { return read2 (16);}
    public int root_y () { return read2 (18);}
  
  
    public Window child () { 
      return (Window) intern (display, child_id ()); 
    }
  
  
    public Point root_position () { 
      return new Point (root_x (), root_y ()); 
    } 
  }
  
  
  // opcode 38 - query pointer
  /**
   * @see <a href="XQueryPointer.html">XQueryPointer</a>
   */
  public PointerReply pointer () {
    Request request = new Request (display, 38, 2);
    request.write4 (id);	       
    return new PointerReply (display, display.read_reply (request));
  }

  
  /** Reply of {@link #motion_events(int, int)}. */
  public static class MotionEventsReply extends Data {
    public MotionEventsReply (Data data) { super (data); }
  }


  // opcode 39 - get motion events
  /**
   * @param start possible: {@link Display#CURRENT_TIME}
   * @param stop possible: {@link Display#CURRENT_TIME}
   * @see <a href="XGetMotionEvents.html">XGetMotionEvents</a>
   */
  public MotionEventsReply motion_events (int start, int stop) {
    Request request = new Request (display, 39, 4);
    request.write4 (id);
    request.write4 (start);
    request.write4 (stop);
    return new MotionEventsReply (display.read_reply (request));
  }


  
  /** Reply of {@link #translate_coordinates(Window, int, int)}. */
  public static class CoordinateReply extends Data {
    public CoordinateReply (Data data) { super (data); }
  }
  

  // opcode 40 - translate coordinates
  /**
   * @see <a href="XTranslateCoordinates.html">XTranslateCoordinates</a>
   */
  public CoordinateReply translate_coordinates (Window src, 
    int src_x, int src_y) {

    Request request = new Request (display, 40, 4);
    request.write4 (src.id);
    request.write4 (id);
    request.write2 (src_x);
    request.write2 (src_y);
    return new CoordinateReply (display.read_reply (request));
  }


  // opcode 41 - warp pointer
  /**
   * @param src possible: {@link #NONE}
   * @see <a href="XWarpPointer.html">XWarpPointer</a>
   */
  public void warp_pointer (Window src, int src_x, int src_y, 
    int src_width, int src_height, int dest_x, int dest_y) {

    Request request = new Request (display, 41, 6);
    request.write4 (src.id);
    request.write4 (id);
    request.write2 (src_x);
    request.write2 (src_y);
    request.write2 (src_width);
    request.write2 (src_height);
    request.write2 (dest_x);
    request.write2 (dest_y);
    display.send_request (request);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_background(Color)
   */
  public void set_background (Color c) {
    Attributes attr = new Attributes ();
    attr.set_background (c);
    change_attributes (attr);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_background(int)
   */
  public void set_background (int pixel) {
    Attributes attr = new Attributes ();
    attr.set_background (pixel);
    change_attributes (attr);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_background(Pixmap)
   */
  public void set_background (Pixmap p) {
    Attributes attr = new Attributes ();
    attr.set_background (p);
    change_attributes (attr);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_border(Color)
   */
  public void set_border (Color c) {
    Attributes attr = new Attributes ();
    attr.set_border (c);
    change_attributes (attr);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_border(int)
   */
  public void set_border (int pixel) {
    Attributes attr = new Attributes ();
    attr.set_border (pixel);
    change_attributes (attr);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_border(Pixmap)
   */
  public void set_border (Pixmap p) {
    Attributes attr = new Attributes ();
    attr.set_border (p);
    change_attributes (attr);
  }


  /**
   * @see #change_attributes(Window.Attributes)
   * @see Attributes#set_colormap(Colormap)
   */
  public void set_colormap (Colormap cmap) {
    Attributes attr = new Attributes ();
    attr.set_colormap (cmap);
    change_attributes (attr);
  }


  public static final Window POINTER_ROOT = new Window (1);


  public static final int TO_NONE = 0;
  public static final int TO_POINTER_ROOT = 1;
  public static final int TO_PARENT = 2;


  // opcode 42 - set input focus
  /**
   * @param mode valid:
   * {@link #TO_NONE},
   * {@link #TO_POINTER_ROOT},
   * {@link #TO_PARENT}
   * 
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XSetInputFocus.html">XSetInputFocus</a>
   */
  public void set_input_focus (int revert_to, int time) {
    Request request = new Request (display, 42, revert_to, 3);
    request.write4 (id);
    request.write4 (time);
    display.send_request (request);
  }


  // opcode 61 - clear area
  /**
   * @see <a href="XClearArea.html">XClearArea</a>
   */
  public void clear_area (int x, int y, int width, int height, 
    boolean exposures) {

    Request request = new Request (display, 61, exposures, 4);
    request.write4 (id);
    request.write2 (x);
    request.write2 (y);
    request.write2 (width);
    request.write2 (height);
    display.send_request (request);
  }


  // opcode 83 - list installed colormaps
  /**
   * @return valid:
   * {@link Enum#next()} of type {@link Colormap},
   * {@link Enum#next4()}
   *
   * @see <a href="XListInstalledColormaps.html">
   * XListInstalledColormaps</a>
   */
  public Enum installed_colormaps () {
    Request request = new Request (display, 83, 2);
    request.write4 (id);

    Data reply = display.read_reply (request);
    return new Enum (reply, 32, reply.read2 (8)) {
      public Object next () {
        return Colormap.intern (display, next4 ());
      }
    };
  }


  // opcode 114 - rotate properties
  /**
   * @see <a href="XRotateWindowProperties.html">
   * XRotateWindowProperties</a>
   */
  public void rotate_properties (Atom [] properties, int delta) {
    Request request = new Request (display, 114, 3+properties.length);
    request.write4 (id);
    request.write2 (properties.length);
    request.write2 (delta);
    
    for (int i=0; i<properties.length; i++)
      request.write4 (properties [i].id);

    display.send_request (request);
  }


  /**
   * @see #change_property(int, int, Atom, Atom, int, Object, int, int)
   */
  public void change_property (Atom property, Atom type, int data) {   
    change_property (REPLACE, 1, property, type, 32, 
      new int [] {data}, 0, 32); 
  }


  /**
   * @see <a href="XClearWindow.html">XClearWindow</a>
   * @see #clear_area(int, int, int, int, boolean)
   */
  public void clear (boolean exposures) {
    clear_area (0, 0, width, height, exposures);
  }

 
  public void delete () {
    if (!(wm_protocol ("WM_DELETE_WINDOW"))) return;

    ClientMessage event = new ClientMessage (display);
    Atom wm_protocols = (Atom) Atom.intern (display, "WM_PROTOCOLS");
    Atom wm_delete_window = (Atom) Atom.intern (display,
      "WM_DELETE_WINDOW"); 

    event.set_format (32);
    event.set_window (this);
    event.set_type (wm_protocols);
    event.set_wm_data (wm_delete_window.id);
    event.set_wm_time (display.CURRENT_TIME);
    send_event (false, Event.NO_EVENT_MASK, event);
  }


  /** 
   * @see #configure(Window.Changes)
   */
  public void flip () {
    Changes changes = new Changes ();
    changes.stack_mode (Changes.OPPOSITE);
    configure (changes);
  }


  /** 
   * Grab button ignoring caps lock (LOCK), num lock (MOD2), and scroll
   * lock (MOD5).
   *
   * @see #grab_button(int, int, boolean, int, int, int, Window, Cursor)
   */
  public void grab_button_ignore_locks (int button, int modifiers, 
    boolean owner_events, int event_mask, int pointer_mode, 
    int keyboard_mode, Window confine_to, Cursor cursor) {

    // Are there a portable way to do this?
    // Sawfish and Icewm use the same technique as well.
    // TODO highly inefficient (many X requests)
    for (int i=0; i<Input.LOCK_COMBINATIONS.length; i++)
      grab_button (button, modifiers | Input.LOCK_COMBINATIONS [i],
        owner_events, event_mask, pointer_mode, keyboard_mode, confine_to,
        cursor); 
  }


  /** 
   * Grab key ignoring caps lock (LOCK), num lock (MOD2), and scroll lock
   * (MOD5).
   *
   * @see #grab_key(int, int, boolean, int, int)
   *
   * @see #grab_button_ignore_locks(int, int, boolean, int, int, int,
   * Window, Cursor)
   */
  public void grab_key_ignore_locks (int keysym, int modifiers,
    boolean owner_events, int pointer_mode, int keyboard_mode) {

    for (int i=0; i<Input.LOCK_COMBINATIONS.length; i++)
      grab_key (keysym, modifiers | Input.LOCK_COMBINATIONS [i],
        owner_events, pointer_mode, keyboard_mode);
  }


  /**
   * @see <a href="XIconifyWindow.html">XIconifyWindow</a>
   * @see <a href="icccm.html#4.1.4">ICCCM Section 4.1.4</a>
   * @see #send_event(boolean, int, Event)
   */
  public void iconify () {
    Atom wm_change_state = (Atom) Atom.intern (display, "WM_CHANGE_STATE");

    ClientMessage event = new ClientMessage (display);
    event.set_format (32);
    event.set_window (this);
    event.set_type (wm_change_state);
    event.set_wm_data (WMHints.ICONIC);
    send_event (false, Event.SUBSTRUCTURE_REDIRECT_MASK
      | Event.SUBSTRUCTURE_NOTIFY_MASK, event); 
  }



  public static Object intern (Display display, int id) {
    Object value = display.resources.get (new Integer (id));
    if (value != null) return value;
    return new Window (display, id);
  }


  /** 
   * @see <a href="XLowerWindow.html">XLowerWindow</a>
   * @see #configure(Window.Changes)
   */
  public void lower () {
    Changes changes = new Changes ();
    changes.stack_mode (Changes.BELOW);
    configure (changes);
  }


  /*
   * @see <a href="XMoveWindow.html">XMoveWindow</a>
   * @see #configure(Window.Changes)
   */
  public void move () {
    Changes changes = new Changes ();
    changes.x (x);
    changes.y (y);
    configure (changes);
  }


  /**
   * @see <a href="XMoveWindow.html">XMoveWindow</a>
   * @see #configure(Window.Changes)
   */
  public void move (int x, int y) {
    if (this.x == x && this.y == y) return;

    this.x = x;
    this.y = y;
    move ();
  }


  /** 
   * @see <a href="XMoveResizeWindow.html">XMoveResizeWindow</a>
   * @see #configure(Window.Changes)
   */
  public void move_resize () {
    move ();
    resize ();
  }


  /** 
   * @see <a href="XMoveResizeWindow.html">XMoveResizeWindow</a>
   * @see #configure(Window.Changes)
   */
  public void move_resize (int x, int y, int width, int height) {
    move (x, y);
    resize (width, height);
  }


  /** 
   * @see <a href="XMoveResizeWindow.html">XMoveResizeWindow</a>
   * @see #configure(Window.Changes)
   */
  public void move_resize (Rectangle rectangle) {
    move_resize (rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }


  /**
   * @see Display#kill_client(Resource)
   */
  public void kill () {
    display.kill_client (this);
  }


  /** 
   * @see #configure(Window.Changes)
   */
  public void raise () {
    Changes changes = new Changes ();
    changes.stack_mode (Changes.ABOVE);
    configure (changes);
  }


  public Rectangle rectangle () {
    return new Rectangle (x, y, width, height);
  }


  /**
   * @see <a href="XResizeWindow.html">XResizeWindow</a>
   * @see #configure(Window.Changes)
   */
  public void resize () {
    Changes changes = new Changes ();

    // width/height == 0 causes BAD_VALUE Error
    if (width != 0) changes.width (width);
    if (height != 0) changes.height (height);
    if (changes.bitmask != 0) configure (changes);
  }


  /** 
   * @see <a href="XResizeWindow.html">XResizeWindow</a>
   * @see #configure(Window.Changes)
   */
  public void resize (int width, int height) {
    if (this.width == width && this.height == height) return;

    this.width = width;
    this.height = height;
    resize ();
  }


  public boolean resized (Rectangle r) {
    return r.width != width || r.height != height;
  }


  public Screen screen () {
    for (int i=0; i<display.screens.length; i++) {
      Screen screen = display.screens [i];
      if (screen.root_id () == id) return screen;
    }

    return null;
  }



  /**
   * @see <a href="XSelectInput.html">XSelectInput</a>
   * @see #change_attributes(Window.Attributes)
   */
  public void select_input (int event_mask) {
    Attributes attr = new Attributes ();
    attr.set_event_mask (event_mask);
    change_attributes (attr);
  }


  /**
   * @see #set_input_focus(int, int)
   */
  public void set_input_focus () {
    set_input_focus (TO_POINTER_ROOT, display.CURRENT_TIME);
  }

  
  public void set_geometry_cache (Rectangle r) {
    x = r.x;
    y = r.y;
    width = r.width;
    height = r.height;
  }


  /**
   * A standard way to set wm class hint and name in Java.
   *
   * @see #set_wm_class_hint(String, String)
   * @see #set_wm_name(String)
   */
  public void set_wm (Object app, String topic) {
    String res_class = app.getClass ().getName ();
    set_wm_class_hint (topic, res_class);
    set_wm_name (topic + " - " + res_class);
  }    


  /** X window manager class hint. */
  public static class WMClassHint {
    public String res;
    public int middle;
  
  
    public WMClassHint (Data data) { 
      int len = data.read4 (16)-1;
      res = data.read_string (32, len);
      middle = res.indexOf (0);
    }
  
  
    public boolean class_equals (String res_class) {
      if (res_class == null) return false;
      return res.endsWith (res_class);
    }
  
  
    public boolean class_equals (WMClassHint hint) {
      if (hint == null) return false;
      return class_equals (hint.res_class ());
    }
  
  
    public boolean equals (WMClassHint hint) {
      if (hint == null) return false;
      return res.equals (hint.res);
    }
  
  
    public boolean equals (String res_name, String res_class) {
      if (res_name == null || res_class == null) return false;
      if (res_name.length () + res_class.length () != res.length ()-1)
        return false;
      
      String res0 = res_name + "\0" + res_class;
      return res.equals (res0);
    }
  
  
    public String res_name () {
      return res.substring (0, middle);
    }
  
  
    public String res_class () {
      return res.substring (middle+1, res.length ());
    }
  
  
    public String toString () {
      return "[" + res_name () + " " + res_class () + "]";
    }
  }


  /**
   * @see #set_wm_class_hint(String, String)
   */
  public void set_wm_class_hint (WMClassHint class_hint) {    
    set_wm_class_hint (class_hint.res_name (), class_hint.res_class ());
  }


  /**
   * @see <a href="XSetClassHint.html">XSetClassHint</a>
   * @see #change_property(int, int, Atom, Atom, int, Object, int, int)
   */
  public void set_wm_class_hint (String res_name, String res_class) {
    String wm_class = res_name + '\0' + res_class + '\0';

    change_property (REPLACE, wm_class.length (), Atom.WM_CLASS,
      Atom.STRING, 8, wm_class.getBytes (), 0, 8);
  }


  /** X window manager hints. */
  public static class WMHints extends Data {
    public WMHints (Data data) { super (data); }
  
  
    public static final int INPUT_HINT_MASK = 1<<0;
    public static final int STATE_HINT_MASK = 1<<1;
    public static final int ICON_PIXMAP_HINT_MASK = 1<<2;
    public static final int ICON_WINDOW_HINT_MASK = 1<<3;
    public static final int ICON_POSITION_HINT_MASK = 1<<4;
    public static final int ICON_MASK_HINT_MASK = 1<<5;
    public static final int WINDOW_GROUP_HINT_MASK = 1<<6;
    public static final int URGENCY_HINT_MASK = 1<<8;
  
  
    /**
     * @return valid:
     * {@link #INPUT_HINT_MASK},
     * {@link #STATE_HINT_MASK},
     * {@link #ICON_PIXMAP_HINT_MASK},
     * {@link #ICON_WINDOW_HINT_MASK},
     * {@link #ICON_POSITION_HINT_MASK},
     * {@link #ICON_MASK_HINT_MASK},
     * {@link #WINDOW_GROUP_HINT_MASK},
     * {@link #URGENCY_HINT_MASK}
     */
    public int flags () { return read4 (32); }
  
  
    public final static int WITHDRAWN = 0;
    public final static int NORMAL = 1;
    public final static int ICONIC = 3;
  
  
    /**
     * @return valid:
     * {@link #WITHDRAWN},
     * {@link #NORMAL},
     * {@link #ICONIC}
     */
    public int initial_state () { return read4 (40); }
  }


  /**
   * @see <a href="XSetWMHints.html">XSetWMHints</a>
   * @see #change_property(int, int, Atom, Atom, int, Object, int, int)
   */
  public void set_wm_hints (WMHints wm_hints) {
    change_property (REPLACE, 8, Atom.WM_HINTS, Atom.WM_HINTS,
      8, wm_hints.data, 32, 8);
  }

   
  /** X window manager size hints. */
  public static class WMSizeHints extends Data {
    public WMSizeHints (Data data) { super (data); }
  
  
    // reading
  
    public final static int USPOSITION_MASK = 1<<0;
    public final static int USSIZE_MASK = 1<<1;
    public final static int PPOSITION_MASK = 1<<2;
    public final static int PSIZE_MASK = 1<<3;
    public final static int PMIN_SIZE_MASK = 1<<4;
    public final static int PMAX_SIZE_MASK = 1<<5;
    public final static int PRESIZE_INC_MASK = 1<<6;
    public final static int PASPECT_MASK = 1<<7;
    public final static int PBASE_SIZE_MASK = 1<<8;
    public final static int PWIN_GRAVITY_MASK = 1<<9;
  
  
    /**
     * @return valid: 
     * {@link #USPOSITION_MASK},
     * {@link #USSIZE_MASK},
     * {@link #PPOSITION_MASK},
     * {@link #PSIZE_MASK},
     * {@link #PMIN_SIZE_MASK},
     * {@link #PMAX_SIZE_MASK},
     * {@link #PRESIZE_INC_MASK},
     * {@link #PASPECT_MASK},
     * {@link #PBASE_SIZE_MASK},
     * {@link #PWIN_GRAVITY_MASK}
     */
    public int flags () { return read4 (32); }
  
  
    // skip 4 paddings in July 27, 1988 draft of icccm? 
    // but apps still use it, and how otherwise can we get these info?
    public boolean user_position () { return (flags () & USPOSITION_MASK) != 0; }
    public boolean user_size () { return (flags () & USSIZE_MASK) != 0; }
    public boolean program_position () { return (flags () & PPOSITION_MASK) != 0; }
    public boolean program_size () { return (flags () & PSIZE_MASK) != 0; }
  
    public int x () { return read4 (36); }
    public int y () { return read4 (40); }
    public int width () { return read4 (44); }
    public int height () { return read4 (48); }
    public int min_width () { return read4 (52); }
    public int min_height () { return read4 (56); }
  
  
    // writing
  
    public void x (int i) { write4 (36, i); }
  }


  /**
   * @see <a href="XSetWMNormalHints.html">XSetWMNormalHints</a>
   * @see #change_property(int, int, Atom, Atom, int, Object, int, int)
   */
  public void set_wm_normal_hints (WMSizeHints size_hints) {
    change_property (REPLACE, 18, Atom.WM_NORMAL_HINTS,
      Atom.WM_SIZE_HINTS, 32, size_hints.data, 32, 8);
  }


  /**
   * @see <a href="XSetWMName.html">XSetWMName</a>
   * @see #change_property(int, int, Atom, Atom, int, Object, int, int)
   */
  public void set_wm_name (String wm_name) {
    change_property (REPLACE, wm_name.length (), Atom.WM_NAME,
      Atom.STRING, 8, wm_name.getBytes (), 0, 8); // support other types?
  }


  /** 
   * @see #set_wm_protocol(String)
   */
  public void set_wm_delete_window () {
    set_wm_protocol ("WM_DELETE_WINDOW");
  }


  /** 
   * @see #change_property(Atom, Atom, int)
   */
  public void set_wm_protocol (String name) {
    Atom wm_protocols = (Atom) Atom.intern (display, "WM_PROTOCOLS");
    Atom protocol =  (Atom) Atom.intern (display, name);

    change_property (wm_protocols, Atom.ATOM, protocol.id);
  }
  

  /** 
   * @see #set_wm_state(int, Window)
   */
  public void set_wm_state (int state) {
    set_wm_state (state, NONE);
  }


  /** X window manager state. */
  public static class WMState extends Data {
    public Display display;

    public WMState (Display display, Data data) { 
      super (data); 
      this.display = display;
    }
  
  
    public final static int WITHDRAWN = 0;
    public final static int NORMAL = 1;
    public final static int ICONIC = 3;
  
  
    /**
     * @return valid:
     * {@link #WITHDRAWN},
     * {@link #NORMAL},
     * {@link #ICONIC}
     */
    public int state () { return read4 (32); }
  
  
    public int icon_id () { return read4 (36); }
    public Window icon () { return (Window) intern (display, icon_id ()); }
  }
  
  
  /** 
   * @see #set_wm_state(int, Window)
   */
  public void set_wm_state (WMState state) {
    set_wm_state (state.state (), state.icon ());
  }


  /** 
   * @see #change_property(int, int, Atom, Atom, int, Object, int, int)
   */
  public void set_wm_state (int state, Window icon) {
    Atom wm_state = (Atom) Atom.intern (display, "WM_STATE");
    int [] data = {state, icon.id};

    change_property (REPLACE, 2, wm_state, wm_state, 32, data, 0, 32);
  }


  public String toString () {
    return "#Window " + id 
      + " " + (new Rectangle (x, y, width, height)).spec ();
  }

  
  public static final int MAX_WM_LENGTH = 1000;


  /**
   * @see <a href="XGetClassHint.html">XGetClassHint</a>
   * @see #property(boolean, Atom, Atom, int, int)
   */
  public WMClassHint wm_class_hint () {
    PropertyReply pi = property (false, Atom.WM_CLASS, 
      Atom.STRING, 0, MAX_WM_LENGTH); // support other types?

    if (pi.format () != 8 || pi.type_id () != Atom.STRING.id)
      return null;

    return new WMClassHint (pi);
  }


  /**
   * @see <a href="XGetWMHints.html">XGetWMHints</a>
   * @see #property(boolean, Atom, Atom, int, int)
   */
  public WMHints wm_hints () {
    PropertyReply pi = property (false, Atom.WM_HINTS, Atom.WM_HINTS, 0, 8);
    
    if (pi.format () != 32 || pi.type_id () != Atom.WM_HINTS.id
      || pi.length () != 8) return null;

    return new WMHints (pi);
  }


  /**
   * @see <a href="XGetWMName.html">XGetWMName</a>
   * @see #property(boolean, Atom, Atom, int, int)
   */
  public String wm_name () {
    PropertyReply pi = property (false, Atom.WM_NAME, 
      Atom.STRING, 0, MAX_WM_LENGTH); // support other types?

    if (pi.format () != 8 || pi.type_id () != Atom.STRING.id) 
      return null;

    return pi.read_string (32, pi.length ());
  }


  /**
   * @see <a href="XGetWMNormalHints.html">XGetWMNormalHints</a>
   * @see #property(boolean, Atom, Atom, int, int)
   */
  public WMSizeHints wm_normal_hints () {
    PropertyReply pi = property (false, Atom.WM_NORMAL_HINTS,
      Atom.WM_SIZE_HINTS, 0, 18);

    if (pi.format () != 32 
      || pi.type_id () != Atom.WM_SIZE_HINTS.id
      || pi.length () != 18) return null;
   
    return new WMSizeHints (pi);
  }


  /**
   * @see #wm_protocols()
   */
  public boolean wm_protocol (String name) {
    Atom protocol = (Atom) Atom.intern (display, name);
    Enum list = wm_protocols ();

    while (list.more ())
      if (list.next4 () == protocol.id) return true;

    return false;
  }
      

  /** 
   * @return valid:
   * {@link Enum#next()} of type {@link Atom},
   * {@link Enum#next4()}
   *
   * @see #property(boolean, Atom, Atom, int, int)
   */
  public Enum wm_protocols () {
    Atom wm_protocols = (Atom) Atom.intern (display, "WM_PROTOCOLS");
    PropertyReply pi = property (false, wm_protocols, Atom.ATOM, 0,
      MAX_WM_LENGTH/4);

    if (pi.byte_after () != 0)
      throw new RuntimeException ("Number of WM protocol exceeds " +
	MAX_WM_LENGTH/4); 

    return new Enum (pi, 32, pi.length ()) {
      public Object next () {
        return Atom.intern (display, next4 ());
      }
    };
  }


  /** 
   * @see #property(boolean, Atom, Atom, int, int)
   */
  public WMState wm_state () {
    Atom wm_state = (Atom) Atom.intern (display, "WM_STATE");    
    PropertyReply pi = property (false, wm_state, wm_state, 0, 2);

    if (pi.format () != 32 
      || pi.type_id () != wm_state.id
      || pi.length () != 2) return null;

    return new WMState (display, pi);
  }


  /**
   * @see #warp_pointer(Window, int, int, int, int, int, int)
   */
  public void warp_pointer (int x, int y) {
    warp_pointer (NONE, 0, 0, 0, 0, x, y);
  }


  /**
   * @see #warp_pointer(int, int)
   */
  public void warp_pointer (Point position) {
    warp_pointer (position.x, position.y);
  }
}
