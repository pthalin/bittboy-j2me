package gnu.x11.extension;

import gnu.x11.Data;
import gnu.x11.Request;


/**
 * Display Power Management Signaling Extension. The specification can be
 * found <a href=
 * "http://escher.sourceforge.net/etc/specification/dpms-library.ps.gz"
 * >here</a> (<a href=
 * "http://escher.sourceforge.net/etc/specification/dpms-protocol.ps.gz"
 * >protocol</a>).
 * 
 */
public class DPMS extends Extension {
  public static final String [] MINOR_OPCODE_STRINGS = {
    "GetVersion",               // 0
    "Capable",                  // 1
    "GetTimeouts",              // 2
    "SetTimeouts",              // 3
    "Enable",                   // 4
    "Disable",                  // 5
    "ForceLevel",               // 6
    "Info"                      // 7
  };


  public static final int CLIENT_MAJOR_VERSION = 1;
  public static final int CLIENT_MINOR_VERSION = 1;


  public int server_major_version, server_minor_version;


  // dpms opcode 0 - get version
  /**
   * @see <a href="DPMSQueryExtension.html">DPMSQueryExtension</a>
   */
  public DPMS (gnu.x11.Display display) throws NotFoundException {  
    super (display, "DPMS", MINOR_OPCODE_STRINGS); 

    // check version before any other operations
    Request request = new Request (display, major_opcode, 0, 2);
    request.write2 (CLIENT_MAJOR_VERSION);
    request.write2 (CLIENT_MINOR_VERSION);

    Data reply = display.read_reply (request);
    server_major_version = reply.read2 (8);
    server_minor_version = reply.read2 (10);
  }
  
  
  // dpms opcode 1 - capable
  /**
   * Determine whether or not the currently running server's devices are
   * capable of DPMS operations. There is a <a href=
   * "../../../../etc/dpms-bug">bug</a> in all servers based on X
   * Consortium sample implementation up to R6.5 (including XFree86 4.0.1
   * or earlier): the sequence number of the reply is incorrect, causing a
   * "reply out of order" error.
   *
   * @see <a href="DPMSCapable.html">DPMSCapable</a>
   */
  public boolean capable () {
    Request request = new Request (display, major_opcode, 1, 1);
    return display.read_reply (request).read_boolean (8);
  }

  
  /** Reply of {@link #timeouts()} */
  public static class TimeoutsReply extends Data {
    public TimeoutsReply (Data data) { super (data); }
    public int standby () { return read2 (8); }
    public int suspend () { return read2 (10); }
    public int off () { return read2 (12); }
  
  
    public String toString () {
      return "#TimeoutsReply"
        + "\n  standby: " + standby ()
        + "\n  suspend: " + suspend ()
        + "\n  off: " + off ();
    }
  }
  
  
  // dpms opcode 2 - get timeouts
  /**
   * @see <a href="DPMSGetTimeouts.html">DPMSGetTimeouts</a>
   */
  public TimeoutsReply timeouts () {
    Request request = new Request (display, major_opcode, 2, 1);
    return new TimeoutsReply (display.read_reply (request));
  }


  // dpms opcode 3 - set timeouts
  /**
   * @see <a href="DPMSSetTimeouts.html">DPMSSetTimeouts</a>
   */
  public void set_timeouts (int standby, int suspend, int off) {
    Request request = new Request (display, major_opcode, 3, 3);
    request.write2 (standby);
    request.write2 (suspend);
    request.write2 (off);
    display.send_request (request);
  }


  // dpms opcode 4 - enable
  /**
   * @see <a href="DPMSEnable.html">DPMSEnable</a>
   */
  public void enable () {
    Request request = new Request (display, major_opcode, 4, 1);
    display.send_request (request);
  }


  // dpms opcode 5 - disable
  /**
   * @see <a href="DPMSDisable.html">DPMSDisable</a>
   */
  public void disable () {
    Request request = new Request (display, major_opcode, 5, 1);
    display.send_request (request);
  }


  public static final int ON = 0;
  public static final int STAND_BY = 1;
  public static final int SUSPEND = 2;
  public static final int OFF = 3;


  public static final String [] LEVEL_STRINGS
    = {"on", "stand-by", "suspend", "off"};


  // dpms opcode 6 - force level
  /**
   * @param level valid:
   * {@link #ON},
   * {@link #STAND_BY},
   * {@link #SUSPEND},
   * {@link #OFF}
   *
   * @see <a href="DPMSForceLevel.html">DPMSForceLevel</a>
   */
  public void force_level (int level) {
    Request request = new Request (display, major_opcode, 6, 2);
    request.write2 (level);
    display.send_request (request);    
  }


  /** Reply of {@link #info()} */
  public static class InfoReply extends Data {
    public InfoReply (Data data) { super (data); }  
    public boolean state () { return read_boolean (10); }
  
  
    /**
     * @return valid:
     * {@link #ON},
     * {@link #STAND_BY},
     * {@link #SUSPEND},
     * {@link #OFF}
     */
    public int level () { return read2 (8); }
    
  
    public String toString () {
      return "#InfoReply"
        + "\n  state: " + state ()
        + "\n  level: " + LEVEL_STRINGS [level ()];
    }
  }


  // dpms opcode 7 - info
  /**
   * @see <a href="DPMSInfo.html">DPMSInfo</a>
   */
  public InfoReply info () {
    Request request = new Request (display, major_opcode, 7, 1);
    return new InfoReply (display.read_reply (request));
  }


  public String more_string () {
    return "\n  client-version: " 
      + CLIENT_MAJOR_VERSION + "." + CLIENT_MINOR_VERSION
      + "\n  server-version: "
      + server_major_version + "." + server_minor_version;
  }
}
