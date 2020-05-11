package gnu.x11.extension.glx;

import gnu.x11.Request;


/** GLX pixmap. */
public class Pixmap extends gnu.x11.Resource {
  public GLX glx;


  // glx opcode 5 - create glx pixmap
  /**
   * @see <a href="glXCreateGLXPixmap.html">glXCreateGLXPixmap</a>
   */
  public Pixmap (GLX glx, int screen_no, gnu.x11.Visual visual, 
    gnu.x11.Pixmap pixmap) {

    super (glx.display);

    Request request = new Request (display, glx.major_opcode, 5, 5);
    request.write4 (screen_no);
    request.write4 (visual.id ());
    request.write4 (pixmap.id);
    request.write4 (id);
    display.send_request (request);
  } 


  // glx opcode 6 - destroy glx pixmap
  /**
   * @see <a href="glXDestroyContext.html">glXDestroyContext</a>
   */
  public void destroy () {
    Request request = new Request (display, glx.major_opcode, 6, 2);
    request.write4 (id);
    display.send_request (request);
  }    
}
