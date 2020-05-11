package gnu.x11;


/** X font. */
public class Font extends Fontable {
  /** 
   * Predefined font.
   *
   * @see Window#NONE
   */
  public static final Font NONE = new Font (0);


  public String name;


  /** Predefined. */
  public Font (int id) {
    super (id);
  }


  // opcode 45 - open font
  /**
   * @see <a href="XLoadFont.html">XLoadFont</a>
   */
  public Font (Display display, String name) {

    super (display);
    this.name = name;

    Request request = new Request (display, 45, 3+Data.unit (name));
    request.write4 (id);
    request.write2 (name.length ());
    request.write2_unused ();
    request.write1 (name);
    display.send_request (request);
  }


  // opcode 46 - close font
  /**
   * @see <a href="XFreeFont.html">XFreeFont</a>
   */
  public void close () {
    Request request = new Request (display, 46, 2);
    request.write4 (id);
    display.send_request (request);
  }


  public String toString () {
    return "#Font: " + name + " " +  super.toString ();
  }
}
