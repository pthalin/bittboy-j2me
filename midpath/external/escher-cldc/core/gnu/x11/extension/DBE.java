package gnu.x11.extension;

import gnu.x11.Data;
import gnu.x11.Drawable;
import gnu.x11.Enum;
import gnu.x11.Request;
import gnu.x11.Window;


/** 
 * Double Buffer Extension. The specification can be found <a href= 
 * "http://escher.sourceforge.net/etc/specification/dbe-library.ps.gz"
 * >here</a> (<a href=
 * "http://escher.sourceforge.net/etc/specification/dbe-protocol.ps.gz"
 * >protocol</a>).
 * 
 * <p>Check <a href=" "../../../../etc/faq/dbe">this</a> for an insightful
 * discusson on the merit of DBE. 
 */
public class DBE extends Extension implements ErrorFactory {
  public static final String [] MINOR_OPCODE_STRINGS = {
    "GetVersion",               // 0
    "AllocateBackBufferName",   // 1
    "DeallocateBackBufferName", // 2
    "SwapBuffers",              // 3
    "BeginIdiom",               // 4
    "EndIdiom",                 // 5
    "GetVisualInfo"             // 6
  };
     

  public static final int UNDEFINED = 0;
  public static final int BACKGROUND = 1;
  public static final int UNTOUCHED = 2;
  public static final int COPIED = 3;

  public static final int CLIENT_MAJOR_VERSION = 1;
  public static final int CLIENT_MINOR_VERSION = 0;


  public int server_major_version, server_minor_version;


  // dbe opcode 0 - get version
  public DBE (gnu.x11.Display display) throws NotFoundException { 
    super (display, "DOUBLE-BUFFER", MINOR_OPCODE_STRINGS, 1, 0);

    // check version before any other operations
    Request request = new Request (display, major_opcode, 0, 2);
    request.write1 (CLIENT_MAJOR_VERSION);
    request.write1 (CLIENT_MINOR_VERSION);

    Data reply = display.read_reply (request);
    server_major_version = reply.read1 (8);
    server_minor_version = reply.read1 (9);
  }


  // dbe opcode 3 - swap buffers
  /**
   * @param actions array of actions; valid action: 
   * {@link #UNDEFINED}
   * {@link #BACKGROUND}
   * {@link #UNTOUCHED}
   * {@link #COPIED}
   *
   * @see <a href="XdbeSwapBuffers.html">XdbeSwapBuffers</a>
   */  
  public void swap (Window [] windows, int [] actions) {
    int n = windows.length;
    Request request = new Request (this.display, major_opcode, 3, 2+2*n);
    request.write4 (n);
    for (int i=0; i<n; i++) {
      request.write4 (windows [i].id);
      request.write1 (actions [i]);
      request.write3_unused ();
    }
    this.display.send_request (request);
  }


  // dbe opcode 4 - begin idiom
  /**
   * @see <a href="XdbeBeginIdiom.html">XdbeBeginIdiom</a>
   */
  public void begin_idiom () {
    Request request = new Request (this.display, major_opcode, 4, 1);
    this.display.send_request (request);
  }


  // dbe opcode 5 - end idiom
  /**
   * @see <a href="XdbeEndIdiom.html">XdbeEndIdiom</a>
   */
  public void end_idiom () {
    Request request = new Request (this.display, major_opcode, 5, 1);
    this.display.send_request (request);
  }


  /** DBE visual info. */
  public static class VisualInfo extends Data {
    public VisualInfo (Data data, int offset) { super (data, offset); }
    public int visual_id () { return read4 (0); }
    public int depth () { return read1 (4); }
    public int perflevel () { return read1 (5); }


    public String toString () {
      return "#VisualInfo"
        + "\n  visual-id: " + visual_id ()
        + "\n  depth: " + depth ()
        + "\n  performance hint: " + perflevel ();
    }
  }


  /** DBE screen visual info. */
  public static class ScreenVisualInfo extends Data {
    public ScreenVisualInfo (Data data, int offset) { 
      super (data, offset); 
    }


    public int visual_info_count () { return read4 (0); }
    public int length () { return 8 * visual_info_count (); }


    public Enum visual_info () {
      return new Enum (ScreenVisualInfo.this, offset+4,
        visual_info_count ()) {

        public Object next () {
          VisualInfo visual_info = new VisualInfo (this, 0);
          inc (8);
          return visual_info;
        }
      };
    }


    public String toString () {
      return "#ScreenVisualInfo"
        + visual_info ().to_string (Enum.NEXT, "\n  ** visual info **\n");
    }
  }


  public static final Drawable [] EMPTY = new Drawable [0];


  // dbe opcode 6 - get visual info
  /**
   * Get information about which visuals support double buffering. There
   * seems to be <a href= "../../../../etc/dbe-bug">bug</a> in all servers
   * based on X Consortium sample implementation up to R6.5 (including
   * XFree86 4.0.1 or earlier): the reply length of the reply is incorrect
   * , causing a "read timed out" error.
   *
   * @param screen_specifiers valid: {@link #EMPTY}
   * @see <a href="XdbeGetVisualInfo.html">XdbeGetVisualInfo</a>
   */
  public Enum visual_info (Drawable [] screen_specifiers) {
    Request request = new Request (display, major_opcode, 6, 
      2+screen_specifiers.length);

    request.write4 (screen_specifiers.length);
    for (int i=0; i<screen_specifiers.length; i++)
      request.write4 (screen_specifiers [i].id);

    Data reply = display.read_reply (request);
    return new Enum (reply, 32, reply.read4 (8)) {
      public Object next () {
        ScreenVisualInfo svi = new ScreenVisualInfo (this, 0);
        inc (svi.length ());
        return svi;
      }
    };
  }


  public class BackBuffer extends Drawable {
    public Window window;


    // dbe opcode 1 - allocate back buffer name
    /**
     * @param swap_action_hint valid: 
     * {@link #UNDEFINED}
     * {@link #BACKGROUND}
     * {@link #UNTOUCHED}
     * {@link #COPIED}
     */
    public BackBuffer (Window window, int swap_action_hint) {
      super (window.display);
      this.window = window;
      width = window.width;
      height = window.height;
      
      Request request = new Request (this.display, major_opcode, 1, 4);
      request.write4 (window.id);
      request.write4 (id);
      request.write1 (swap_action_hint);
      this.display.send_request (request);
    }


    // dbe opcode 2 - deallocate back buffer name
    /**
     * @see <a href="XdbeDeallocateBackBufferName.html">
     * XdbeDeallocateBackBufferName</a>
     */
    public void deallocate () {
      Request request = new Request (this.display, major_opcode, 2, 2);
      request.write4 (id);
      this.display.send_request (request);
    }


    // dbe opcode 7 - get back buffer attributes
    /**
     * @see <a href="XdbeGetBackBufferAttributes.html">
     * XdbeGetBackBufferAttributes</a>
     */
    public Window attributes () {
      Request request = new Request (this.display, major_opcode, 7, 2);
      request.write4 (id);

      Data reply = this.display.read_reply (request);
      return (Window) Window.intern (this.display, reply.read4 (8));
    }


    /**
     * DBE#swap(Window, int)
     */
    public void swap (int action) {
      DBE.this.swap (window, action);
    }
  }


  // javadoc bug? should be BackBuffer#BackBuffer(Window, int)
  /**
   * @see <a href="XdbeAllocateBackBufferName.html">
   * XdbeAllocateBackBufferName</a>
   *
   * @see BackBuffer
   */
  public BackBuffer allocate (Window window, int swap_action_hint) {
    return new BackBuffer (window, swap_action_hint);
  }

  
  public static final String ERROR_STRING
    = "BAD_DBE_BUFFER: parameter not a DBE back buffer";


  public gnu.x11.Error build (gnu.x11.Display display, Data data, 
    int code, int seq_no, int bad, int minor_opcode, int major_opcode) {

    return new gnu.x11.Error (display, ERROR_STRING, code, seq_no, bad, 
      minor_opcode, major_opcode); 
  }


  /** 
   * @see #swap(Window[], int[])
   */
  public void swap (Window window, int action) {
    swap (new Window [] {window}, new int [] {action});
  }


  public String more_string () {
    return "\n  client-version: " 
      + CLIENT_MAJOR_VERSION + "." + CLIENT_MINOR_VERSION
      + "\n  server-version: "
      + server_major_version + "." + server_minor_version;
  }
}
