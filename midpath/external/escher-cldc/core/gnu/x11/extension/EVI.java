package gnu.x11.extension;

import gnu.x11.Data;
import gnu.x11.Display;
import gnu.x11.Enum;
import gnu.x11.Request;
import gnu.x11.Visual;


/**
 * Extended Visual Information Extension. The specification can be found <a href=
 * "http://escher.sourceforge.net/etc/specification/evi.ps.gz">here</a>.
 */
public class EVI extends Extension {
  public static final String [] MINOR_OPCODE_STRINGS = {
    "GetVersion",               // 0
    "GetVisualInfo"             // 1
  };


  public static final int CLIENT_MAJOR_VERSION = 1;
  public static final int CLIENT_MINOR_VERSION = 0;


  public int server_major_version, server_minor_version;


  // evi opcode 0 - get version
  /**
   * @see <a href="XeviQueryVersion.html">XeviQueryVersion</a>
   */
  public EVI (Display display) throws NotFoundException { 
    super (display, "Extended-Visual-Information", MINOR_OPCODE_STRINGS); 

    // check version before any other operations
    /* Note that the specification says the request includes a major and a
     * minor version, but most implementation (xfree86 3.3/4.0) does not. 
     * Which one is bugged?
     */
    Request request = new Request (display, major_opcode, 0, 1);
    Data reply = display.read_reply (request);
    server_major_version = reply.read2 (8);
    server_minor_version = reply.read2 (10);
  }


  /** EVI visual info. */
  public static class VisualInfo extends gnu.x11.Data {
    public Display display;


    public VisualInfo (Display display, Data data, int offset) { 
      super (data, offset); 
      this.display = display;
    }
  
  
    public int core_visual_id () { return read4 (0); }
    public int screen_id () { return read1 (4); }
    public int level () { return read1 (5); }
    public int trasparency_type () { return read1 (6); }
    public int trasparency_value () { return read4 (8); }
    public int min_hw_colormaps () { return read1 (12); }
    public int max_hw_colormaps () { return read1 (13); }
    public int colormap_conflict_count () { return read2 (14); }
  }


  /** Reply of {@link #visual_info(Visual[])} */
  public static class VisualInfoReply extends Data {
    public Display display;

    public VisualInfoReply (Display display, Data data) { 
      super (data); 
      this.display = display;
    }


    public int info_count () { return read4 (8); }
    public int conflicts_count () { return read4 (12); }
  
  
    /**
     * @return valid: {@link Enum#next()} of type
     * {@link EVI.VisualInfo}
     */
    public Enum items () {
      return new Enum (this, 32, info_count ()) {
        public Object next () {
          // blackdown JDK 1.1.8 fails to compile the following line
          VisualInfo vi = new VisualInfo (display, this, this.offset);
          inc (16);
          return vi;
        }
      };
    }
  }


  // evi opcode 1 - get visual info
  /**
   * @see <a href="XeviGetVisualInfo.html">XeviGetVisualInfo</a>
   */
  public VisualInfoReply visual_info (Visual [] visuals) {
    Request request = new Request (display, major_opcode, 1, 1);
    request.write4 (visuals.length);

    for (int i=0; i<visuals.length; i++)
      request.write4 (visuals [i].id ());

    return new VisualInfoReply (display, display.read_reply (request));
  }


  public String more_string () {
    return "\n  client-version: " 
      + CLIENT_MAJOR_VERSION + "." + CLIENT_MINOR_VERSION
      + "\n  server-version: "
      + server_major_version + "." + server_minor_version;
  }
}
