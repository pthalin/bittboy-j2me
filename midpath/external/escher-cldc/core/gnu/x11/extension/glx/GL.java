package gnu.x11.extension.glx;

import gnu.x11.Data;
import gnu.x11.Drawable;
import gnu.x11.Enum;
import gnu.x11.Request;


/**
 * GLX rendering context. The specification can be found <a href=
 * "http://escher.sourceforge.net/etc/specification/gl-1.2.1.ps.gz"
 * >here</a> (<a href=
 * "http://escher.sourceforge.net/etc/specification/gl-design.ps.gz"
 * >design</a>).
 *
 * <p>There are a few differences with C binding:
 * <ul>
 * 
 * <li><code>boolean</code> instead of <code>GL_TRUE</code> and <code>GL_FALSE</code>;
 *
 * <li>symbol names starting with numeric character (with <code>GL_</code>
 * removed) is prefixed with <code>X</code> (e.g. <code>GL_2D</code>
 * becomes {@link #X2D});
 *
 * <li>trivial vector wrappers such as <code>glEdgeFlagv</code> and
 * <code>glIndexdv</code> are not implemented.
 *
 * </ul>
 */
public class GL extends gnu.x11.Resource implements GLConstant {
  // 0xffff = 2 bytes = size of `length field'
  private static final int MAX_REQUEST_LENGTH = 0xffff;


  /**
   * Predefined context.
   *
   * @see gnu.x11.Window#NONE
   */
  public static final GL NONE0 = new GL (0);


  public GLX glx;
  public int tag;


  private int render_large_total, render_mode;
  private Request render_request;
  private String version_string_cache;


  /** Predefined. */
  public GL (int id) { super (id); }
  

  // glx opcode 1 - render
  private void begin_render_request (int opcode, int length) {
    if (render_request == null
      || length > render_request.data.length - render_request.index) {

      render_request = begin_command_request (1, MAX_REQUEST_LENGTH);
      render_request.write4 (tag);
    }

    render_request.write2 (length);
    render_request.write2 (opcode);
  }


  // glx opcode 2 - render large (first)
  private void begin_render_large_request (int opcode, int n0, int n1) {
    if (n0+n1 <= MAX_REQUEST_LENGTH) { // non-large
      begin_render_request (opcode, n0+n1);
      return;
    }

    // 16 = size of header of rest render large request
    int max_data_per_request = display.maximum_request_length*4 - 16;

    // 1 = first render large request
    render_large_total = 1 + n1 / max_data_per_request;
    if (n1 % max_data_per_request != 0) render_large_total++;

    // 4 = extra spaces for extended fields
    n0 += 4;

    render_request = begin_command_request (2, 6+Data.unit (n0));
    render_request.write4 (tag);    
    render_request.write2 (1);  // first render large request
    render_request.write2 (render_large_total);
    render_request.write4 (n0);
    render_request.write4 (n0+n1);
    render_request.write4 (opcode);
  }


  // glx opcode 2 - render large (rest)
  private void end_render_large_request (Data data) {
    if (render_large_total == 0) { // non-large
      render_request.write1 (data);
      render_request.write_unused (data.p ());
      return;
    }

    // 16 = size of header of rest render large request
    int max_data_per_request = display.maximum_request_length*4 - 16;
    
    for (int i=2, offset=data.offset; i<=render_large_total; 
         i++, offset+=max_data_per_request) {

      int len = max_data_per_request;
      boolean last = i == render_large_total;
      if (last) len = data.data.length - offset;
        
      Request request = begin_command_request (2, 4+Data.unit(len));
      request.write4 (tag);
      request.write2 (i);
      request.write2 (render_large_total);
      request.write4 (len);
      request.write1 (data.data, offset, len);
      if (last) request.write_unused (data.p ());
      display.send_request (request);
    }

    render_large_total = 0;
  }


  // glx opcode 3 - create context
  /**
   * @see <a href="glXCreateContext.html">glXCreateContext</a>
   */
  public GL (GLX glx, int visual_id, int screen_no,
    GL share_list) {
    
    super (glx.display);
    this.glx = glx;

    Request request = begin_command_request (3, 6);
    request.write4 (id);
    request.write4 (visual_id);
    request.write4 (screen_no);
    request.write4 (share_list.id);
    request.write1 (false);     // is_direct
    display.send_request (request);
  }


  // glx opcode 4 - destroy context  
  /**
   * @see <a href="glXDestroyContext.html">glXDestroyContext</a>
   */
  public void destroy () {
    Request request = begin_command_request (4, 2);
    request.write4 (tag);
    display.send_request (request);
  }


  // glx opcode 5 - make current
  /**
   * @see <a href="glXMakeCurrent.html">glXMakeCurrent</a>
   */
  public void make_current (Drawable drawable) {
    Request request = begin_command_request (5, 4);
    request.write4 (drawable.id);
    request.write4 (id);
    request.write4 (tag);

    Data reply = display.read_reply (request);
    tag = reply.read4 (8);
  }  


  // glx opcode 6 - is direct
  /**
   * @see <a href="glXIsDirect.html">glXIsDirect</a>
   */
  public boolean direct () {
    Request request = begin_command_request (6, 2);
    request.write4 (id);

    Data reply = display.read_reply (request);
    return reply.read_boolean (8);
  }  


  // glx opcode 8 - wait gl
  /**
   * @see <a href="glXWaitGL.html">glXWaitGL</a>
   */
  public void wait_gl () {
    Request request = begin_command_request (8, 2);
    request.write4 (tag);
    display.send_request (request);
  }


  // glx opcode 9 - wait x
  /**
   * @see <a href="glXWaitX.html">glXWaitX</a>
   */
  public void wait_x () {
    Request request = begin_command_request (9, 2);
    request.write4 (tag);
    display.send_request (request);
  }


  // glx opcode 10 - copy context
  /**
   * @see <a href="glXCopyContext.html">glXCopyContext</a>
   */
  public void copy (GL dst, int mask) {
    Request request = begin_command_request (10, 5);
    request.write4 (id);
    request.write4 (dst.id);
    request.write4 (mask);
    request.write4 (tag);
    display.send_request (request);
  }


  // glx opcode 11 - swap buffers
  /**
   * @see <a href="glXSwapBuffers.html">glXSwapBuffers</a>
   */
  public void swap_buffers (Drawable drawable) {
    Request request = begin_command_request (11, 3);
    request.write4 (tag);
    request.write4 (drawable.id);
    display.send_request (request);
  }


  // glx opcode 12 - use x font
  /**
   * @see <a href="glXUseXFont.html">glXUseXFont</a>
   */
  public void use_x_font (gnu.x11.Font font, int first, 
    int count, int base) {

    Request request = begin_command_request (12, 6);
    request.write4 (tag);
    request.write4 (font.id);
    request.write4 (first);
    request.write4 (count);
    request.write4 (base);
    display.send_request (request);
  }


  // glx opcode 101 - new list
  /**
   * @see <a href="glNewList.html">glNewList</a>
   */
  public void new_list (int list, int mode) {
    Request request = begin_single_request (101, 4);
    request.write4 (list);
    request.write4 (mode);
    display.send_request (request);
  }


  // glx opcode 102 - end list
  /**
   * @see <a href="glEndList.html">glEndList</a>
   */
  public void end_list () {
    Request request = begin_single_request (102, 2);
    display.send_request (request);
  }


  // glx opcode 103 - delete lists
  /**
   * @see <a href="glDeleteLists.html">glDeleteLists</a>
   */
  public void delete_lists (int list, int range) {
    Request request = begin_single_request (103, 4);
    request.write4 (list);
    request.write4 (range);
    display.send_request (request);
  }


  // glx opcode 104 - generate lists
  /**
   * @see <a href="glGenLists.html">glGenLists</a>
   */
  public int gen_lists (int range) {
    Request request = begin_single_request (104, 3);
    request.write4 (range);

    Data reply = display.read_reply (request);
    return reply.read4 (8);
  }
  

  // glx opcode 105 - feedback buffer
  /**
   * @see <a href="glFeedbackBuffer.html">glFeedbackBuffer</a>
   */
  public void feedback_buffer (int size, int type) {
    render_mode = FEEDBACK;

    Request request = begin_single_request (105, 4);
    request.write4 (size);    
    request.write4 (type);
    display.send_request (request);
  }


  // glx opcode 106 - selection buffer
  /**
   * @see <a href="glSelectionBuffer.html">glSelectionBuffer</a>
   */
  public void selection_buffer (int size) {
    render_mode = SELECT;

    Request request = begin_single_request (106, 3);
    request.write4 (size);    
    display.send_request (request);
  }


  // glx opcode 107 - render mode
  /**
   * @see <a href="glRenderMode.html">glRenderMode</a>
   */
  public Enum render_mode (int mode) {
    Request request = begin_single_request (107, 3);
    request.write4 (mode);

    if (render_mode == RENDER) {
      display.send_request (request);
      return null;
    }

    Data reply = display.read_reply (request);
    int n = reply.read4 (12);
    render_mode = reply.read4 (16);
    return new Enum (reply, 32, n);
  }


  // glx opcode 108 - finish
  /**
   * @see <a href="glFinish.html">glFinish</a>
   */
  public void finish () {
    Request request = begin_single_request (108, 2);
    display.read_reply (request);
  }


  // glx opcode 109 - pixel storef
  /**
   * @see <a href="glPixelStoref.html">glPixelStoref</a>
   */
  public void pixel_storef (int pname, int param) {
    Request request = begin_single_request (109, 4);
    request.write4 (pname);
    request.write4 (param);
    display.send_request (request);
  }


  // glx opcode 110 - pixel storei
  /**
   * @see <a href="glPixelStorei.html">glPixelStorei</a>
   */
  public void pixel_storei (int pname, int param) {
    Request request = begin_single_request (110, 4);
    request.write4 (pname);
    request.write4 (param);
    display.send_request (request);
  }


  // glx opcode 112 - get booleanv
  /**
   * @see <a href="glGetBooleanv.html">glGetBooleanv</a>
   */
  public Enum booleanv (int pname) {
    Request request = begin_single_request (112, 3);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 113 - get clip plane
  /**
   * @see <a href="glGetClipPlane.html">glGetClipPlane</a>
   */
  public double [] clip_plane (int plane) {
    Request request = begin_single_request (113, 3);
    request.write4 (plane);

    Data reply = display.read_reply (request);
    int len = reply.read4 (4);
    if (len == 0) return null;

    double [] equation = new double [4];
    equation [0] = reply.read_double (32);
    equation [1] = reply.read_double (40);
    equation [2] = reply.read_double (48);
    equation [3] = reply.read_double (56);
    return equation;
  }


  // glx opcode 114 - get doublev
  /**
   * @see <a href="glGetDoublev.html">glGetDoublev</a>
   */
  public Enum doublev (int pname) {
    Request request = begin_single_request (114, 3);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 115 - get error
  /**
   * @return valid:
   * @see #error_string()
   * @see <a href="glGetError.html">glGetError</a>
   */
  public int error () {
    Request request = begin_single_request (115, 2);
    Data reply = display.read_reply (request);
    return reply.read4 (8);
  }


  // glx opcode 116 - get floatv
  /**
   * @see <a href="glGetFloatv.html">glGetFloatv</a>
   */
  public Enum floatv (int pname) {
    Request request = begin_single_request (116, 3);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 117 - get integerv
  /**
   * @see <a href="glGetIntegerv.html">glGetIntegerv</a>
   */
  public Enum integerv (int pname) {
    Request request = begin_single_request (117, 3);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 118 - get lightfv
  /**
   * @see <a href="glGetLightfv.html">glGetLightfv</a>
   */
  public Enum lightfv (int light, int pname) {
    Request request = begin_single_request (118, 4);
    request.write4 (light);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 119 - get lightiv
  /**
   * @see <a href="glGetLightiv.html">glGetLightiv</a>
   */
  public Enum lightiv (int light, int pname) {
    Request request = begin_single_request (119, 4);
    request.write4 (light);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 120 - get mapdv
  /**
   * @see <a href="glGetMapdv.html">glGetMapdv</a>
   */
  public Enum mapdv (int target, int query) {
    Request request = begin_single_request (120, 4);
    request.write4 (target);
    request.write4 (query);
    return read_enum (request);
  }


  // glx opcode 121 - get mapfv
  /**
   * @see <a href="glGetMapfv.html">glGetMapfv</a>
   */
  public Enum mapfv (int target, int query) {
    Request request = begin_single_request (121, 4);
    request.write4 (target);
    request.write4 (query);
    return read_enum (request);
  }


  // glx opcode 122 - get mapiv
  /**
   * @see <a href="glGetMapiv.html">glGetMapiv</a>
   */
  public Enum mapiv (int target, int query) {
    Request request = begin_single_request (122, 4);
    request.write4 (target);
    request.write4 (query);
    return read_enum (request);
  }


  // glx opcode 123 - get materialfv
  /**
   * @see <a href="glGetMaterialfv.html">glGetMaterialfv</a>
   */
  public Enum materialfv (int face, int pname) {
    Request request = begin_single_request (123, 4);
    request.write4 (face);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 124 - get materialiv
  /**
   * @see <a href="glGetMaterialiv.html">glGetMaterialiv</a>
   */
  public Enum materialiv (int face, int pname) {
    Request request = begin_single_request (124, 4);
    request.write4 (face);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 125 - get pixel mapfv
  /**
   * @see <a href="glGetPixelMapfv.html">
   * glGetPixelMapfv</a>
   */
  public Enum pixel_mapfv (int map) {
    Request request = begin_single_request (125, 3);
    request.write4 (map);
    return read_enum (request);
  }


  // glx opcode 126 - get pixel mapiv
  /**
   * @see <a href="glGetPixelMapiv.html">
   * glGetPixelMapiv</a>
   */
  public Enum pixel_mapiv (int map) {
    Request request = begin_single_request (126, 3);
    request.write4 (map);
    return read_enum (request);
  }


  // glx opcode 127 - get pixel mapusv
  /**
   * @see <a href="glGetPixelMapusv.html">
   * glGetPixelMapusv</a>
   */
  public Enum pixel_mapusv (int map) {
    Request request = begin_single_request (127, 3);
    request.write4 (map);
    return read_enum (request);
  }


  // glx opcode 129 - get string
  /**
   * @see <a href="glGetString.html">glGetString</a>
   */
  public String string (int name) {
    if (name == VERSION && version_string_cache != null)
      return version_string_cache;

    Request request = begin_single_request (129, 3);
    request.write4 (name);
    
    Data reply = display.read_reply (request);
    int len = reply.read4 (12);
    String s = reply.read_string (32, len-1);

    if (name == VERSION) version_string_cache = s;
    return s;
  }


  // glx opcode 130 - get tex envfv
  /**
   * @see <a href="glGetTexEnvfv.html">glGetTexEnvfv</a>
   */
  public Enum tex_envfv (int target, int pname) {
    Request request = begin_single_request (130, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 131 - get tex enviv
  /**
   * @see <a href="glGetTexEnviv.html">glGetTexEnviv</a>
   */
  public Enum tex_enviv (int target, int pname) {
    Request request = begin_single_request (131, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 132 - get tex gendv
  /**
   * @see <a href="glGetTexGendv.html">glGetTexGendv</a>
   */
  public Enum tex_gendv (int target, int pname) {
    Request request = begin_single_request (132, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 133 - get tex genfv
  /**
   * @see <a href="glGetTexGenfv.html">glGetTexGenfv</a>
   */
  public Enum tex_genfv (int target, int pname) {
    Request request = begin_single_request (133, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 134 - get tex geniv
  /**
   * @see <a href="glGetTexGeniv.html">glGetTexGeniv</a>
   */
  public Enum tex_geniv (int target, int pname) {
    Request request = begin_single_request (134, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 136 - get tex parameterfv
  /**
   * @see <a href="glGetTexParameterfv.html">glGetTexParameterfv</a>
   */
  public Enum tex_parameterfv (int target, int pname) {
    Request request = begin_single_request (136, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 137 - get tex parameteriv
  /**
   * @see <a href="glGetTexParameteriv.html">glGetTexParameteriv</a>
   */
  public Enum tex_parameteriv (int target, int pname) {
    Request request = begin_single_request (137, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 138 - get tex level parameterfv
  /**
   * @see <a href="glGetTexLevelParameterfv.html">
   * glGetTexLevelParameterfv</a>
   */
  public Enum tex_level_parameterfv (int target, int level, int pname) {
    Request request = begin_single_request (138, 5);
    request.write4 (target);
    request.write4 (level);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 139 - get tex level parameteriv
  /**
   * @see <a href="glGetTexLevelParameteriv.html">
   * glGetTexLevelParameteriv</a>
   */
  public Enum tex_level_parameteriv (int target, int level, int pname) {
    Request request = begin_single_request (139, 5);
    request.write4 (target);
    request.write4 (level);
    request.write4 (pname);
    return read_enum (request);
  }

  
  // glx opcode 141 - is list
  /**
   * @see <a href="glXIsList.html">glXIsList</a>
   */
  public boolean list (int list) {
    Request request = begin_command_request (141, 3);
    request.write4 (list);

    Data reply = display.read_reply (request);
    return reply.read4_boolean (8);
  }


  // glx opcode 142 - flush
  /**
   * @see <a href="glFlush.html">glFlush</a>
   */
  public void flush () {
    Request request = begin_single_request (142, 2);
    display.send_request (request);
  }


  /** Reply of {@link #textures_resident(int[])}. */
  public static class TexturesResidentReply extends Data {
    public int count;


    public TexturesResidentReply (Data data, int count) { 
      super (data); 
      this.count = count;
    }


    public boolean all_resident () { return read4_boolean (8); }

    
   /**
     */
    public Enum residences () {
      return new Enum (this, 32, count);
    }      
  }


  // glx opcode 143 - are textures resident
  /**
   * @see <a href="glAreTexturesResident.html">glAreTexturesResident</a>
   */
  public TexturesResidentReply textures_resident (int [] textures) {
    int n = textures.length;
    Request request = begin_single_request (143, 3+n);
    request.write4 (n);
    request.write4 (textures);

    Data reply = display.read_reply (request);
    return new TexturesResidentReply (reply, n);
  }


  // glx opcode 144 - delete textures
  /**
   * @see <a href="glDeleteTextures.html">glDeleteTextures</a>
   */
  public void delete_textures (int [] textures) {
    int n = textures.length;
    Request request = begin_single_request (144, 3+n);
    request.write4 (n);
    request.write4 (textures);
    display.send_request (request);
  }


  // glx opcode 145 - generate textures
  /**
   * @see <a href="glGenTextures.html">glGenTextures</a>
   */
  public Enum gen_textures (int n) {
    Request request = begin_single_request (145, 3);
    request.write4 (n);

    Data reply = display.read_reply (request);
    return new Enum (reply, 32, n);
  }


  // glx opcode 146 - is texture
  /**
   * @see <a href="glXIsTexture.html">glXIsTexture</a>
   */
  public boolean texture (int texture) {
    Request request = begin_command_request (146, 3);
    request.write4 (texture);

    Data reply = display.read_reply (request);
    return reply.read4_boolean (8);
  }


  // glx opcode 148 - get color table parameterfv
  /**
   * @see <a href="glGetColorTableParameterfv.html">
   * glGetColorTableParameterfv</a>
   */
  public Enum color_table_parameterfv (int target, int pname) {
    Request request = begin_single_request (148, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 149 - get color table parameteriv
  /**
   * @see <a href="glGetColorTableParameteriv.html">
   * glGetColorTableParameteriv</a>
   */
  public Enum color_table_parameteriv (int target, int pname) {
    Request request = begin_single_request (149, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 151 - get convolution parameterfv
  /**
   * @see <a href="glGetConvolutionParameterfv.html">
   * glGetConvolutionParameterfv</a>
   */
  public Enum convolution_parameterfv (int target, int pname) {
    Request request = begin_single_request (151, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 152 - get convolution parameteriv
  /**
   * @see <a href="glGetConvolutionParameteriv.html">
   * glGetConvolutionParameteriv</a>
   */
  public Enum convolution_parameteriv (int target, int pname) {
    Request request = begin_single_request (152, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 155 - get histogram parameterfv
  /**
   * @see <a href="glGetHistogramParameterfv.html">
   * glGetHistogramParameterfv</a>
   */
  public Enum histogram_parameterfv (int target, int pname) {
    Request request = begin_single_request (155, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 156 - get histogram parameteriv
  /**
   * @see <a href="glGetHistogramParameteriv.html">
   * glGetHistogramParameteriv</a>
   */
  public Enum histogram_parameteriv (int target, int pname) {
    Request request = begin_single_request (156, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 158 - get minmax parameterfv
  /**
   * @see <a href="glGetMinmaxParameterfv.html">
   * glGetMinmaxParameterfv</a>
   */
  public Enum minmax_parameterfv (int target, int pname) {
    Request request = begin_single_request (158, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx opcode 159 - get minmax parameteriv
  /**
   * @see <a href="glGetMinmaxParameteriv.html">
   * glGetMinmaxParameteriv</a>
   */
  public Enum minmax_parameteriv (int target, int pname) {
    Request request = begin_single_request (159, 4);
    request.write4 (target);
    request.write4 (pname);
    return read_enum (request);
  }


  // glx render opcode 1 - call list
  /**
   * @see <a href="glCallList.html">glCallList</a>
   */
  public void call_list (int list) {
    begin_render_request (1, 8);
    render_request.write4 (list);
  }
    

  // glx render opcode 2 - call lists
  /**
   * @see <a href="glCallLists.html">glCallLists</a>
   */
  public void call_lists (int type, Object lists) {
    Data data;

    switch (type) {
    case BYTE:                  // fall through
    case UNSIGNED_BYTE: 
      data = new Data ((byte []) lists);
      break;

    case SHORT:                 // fall through
    case UNSIGNED_SHORT:        // fall through
    case X2_BYTES: 
      data = new Data ((int []) lists, 2);
      break;

    case INT:                   // fall through
    case UNSIGNED_INT:          // fall through
    case X4_BYTES: 
      data = new Data ((int []) lists, 4);
      break;

    case FLOAT: 
      data = new Data ((float []) lists);
      break;

    default:
      return;
    }

    begin_render_large_request (2, 12, data.np ());
    render_request.write4 (data.n ());
    render_request.write4 (type);
    end_render_large_request (data);
  }


  // glx render opcode 3 - list base
  /**
   * @see <a href="glListBase.html">glListBase</a>
   */
  public void list_base (int base) {
    begin_render_request (3, 8);
    render_request.write4 (base);
  }


  // glx render opcode 4 - begin
  /**
   * @see <a href="glBegin.html">glBegin</a>
   */
  public void begin (int mode) {
    begin_render_request (4, 8);
    render_request.write4 (mode);    
  }


  // glx render opcode 5 - bitmap
  /**
   * @see <a href="glBitmap.html">glBitmap</a>
   */
  public void bitmap (int width, int height, float xorig, float yorig,
    float xmove, float ymove, byte [] bitmap) {
    
    Data data = new Data (bitmap);

    begin_render_large_request (5, 48, data.np ());
    render_request.write1_unused ();
    render_request.write1 (false); // java = msb = !lsb_first
    render_request.write2_unused ();

    // FIXME work with other cases??
    render_request.write4 (0);  // row len
    render_request.write4 (0);  // skip rows
    render_request.write4 (0);  // skip pixels
    render_request.write4 (1);  // alignment

    render_request.write4 (width);
    render_request.write4 (height);
    render_request.write4 (xorig);
    render_request.write4 (yorig);
    render_request.write4 (xmove);
    render_request.write4 (ymove);
    end_render_large_request (data);
  }


  // glx render opcode 6 - color3bv
  /**
   * @see <a href="glColor3b.html">glColor3b</a>
   */
  public void color3b (boolean red, boolean green, boolean blue) {
    begin_render_request (6, 8);
    render_request.write1 (red);    
    render_request.write1 (green);    
    render_request.write1 (blue);
    render_request.write1_unused ();
  }


  // glx render opcode 7 - color3dv
  /**
   * @see <a href="glColor3d.html">glColor3d</a>
   */
  public void color3d (double red, double green, double blue) {
    begin_render_request (7, 28);
    render_request.write8 (red);    
    render_request.write8 (green);    
    render_request.write8 (blue);
  }


  // glx render opcode 8 - color3fv
  /**
   * @see <a href="glColor3f.html">glColor3f</a>
   */
  public void color3f (float red, float green, float blue) {
    begin_render_request (8, 16);
    render_request.write4 (red);    
    render_request.write4 (green);    
    render_request.write4 (blue);
  }


  // glx render opcode 9 - color3iv
  /**
   * @see <a href="glColor3i.html">glColor3i</a>
   */
  public void color3i (int red, int green, int blue) {
    begin_render_request (9, 16);
    render_request.write4 (red);    
    render_request.write4 (green);    
    render_request.write4 (blue);
  }


  // glx render opcode 10 - color3sv
  /**
   * @see <a href="glColor3s.html">glColor3s</a>
   */
  public void color3s (int red, int green, int blue) {
    begin_render_request (10, 12);
    render_request.write2 (red);    
    render_request.write2 (green);    
    render_request.write2 (blue);
    render_request.write2_unused ();
  }


  // glx render opcode 11 - color3ubv
  /**
   * @see <a href="glColor3ub.html">glColor3ub</a>
   */
  public void color3ub (byte red, byte green, byte blue) {
    begin_render_request (11, 8);
    render_request.write1 (red);
    render_request.write1 (green);
    render_request.write1 (blue);
    render_request.write1_unused ();
  }


  // glx render opcode 12 - color3uiv
  /**
   * @see <a href="glColor3ui.html">glColor3ui</a>
   */
  public void color3ui (int red, int green, int blue) {
    begin_render_request (12, 16);
    render_request.write4 (red);    
    render_request.write4 (green);    
    render_request.write4 (blue);
  }


  // glx render opcode 13 - color3usv
  /**
   * @see <a href=5.html">glColor3us</a>
   */
  public void color3us (int red, int green, int blue) {
    begin_render_request (13, 12);
    render_request.write2 (red);    
    render_request.write2 (green);    
    render_request.write2 (blue);
    render_request.write2_unused ();
  }


  // glx render opcode 14 - color4bv
  /**
   * @see <a href="glColor4b.html">glColor4b</a>
   */
  public void color4b (boolean red, boolean green, 
    boolean blue, boolean alpha) {

    begin_render_request (14, 8);
    render_request.write1 (red);    
    render_request.write1 (green);    
    render_request.write1 (blue);
    render_request.write1 (alpha);
  }


  // glx render opcode 15 - color4dv
  /**
   * @see <a href="glColor4d.html">glColor4d</a>
   */
  public void color4d (double red, double green, 
    double blue, double alpha) {

    begin_render_request (15, 36);
    render_request.write8 (red);    
    render_request.write8 (green);    
    render_request.write8 (blue);
    render_request.write8 (alpha);
  }


  // glx render opcode 16 - color4fv
  /**
   * @see <a href="glColor4f.html">glColor4f</a>
   */
  public void color4f (float red, float green, float blue, float alpha) {
    begin_render_request (16, 20);
    render_request.write4 (red);    
    render_request.write4 (green);    
    render_request.write4 (blue);
    render_request.write4 (alpha);
  }


  // glx render opcode 17 - color4iv
  /**
   * @see <a href="glColor4i.html">glColor4i</a>
   */
  public void color4i (int red, int green, int blue, int alpha) {
    begin_render_request (17, 20);
    render_request.write4 (red);    
    render_request.write4 (green);    
    render_request.write4 (blue);
    render_request.write4 (alpha);
  }


  // glx render opcode 18 - color4sv
  /**
   * @see <a href="glColor4s.html">glColor4s</a>
   */
  public void color4s (int red, int green, int blue, int alpha) {
    begin_render_request (18, 12);
    render_request.write2 (red);    
    render_request.write2 (green);    
    render_request.write2 (blue);
    render_request.write2 (alpha);
  }


  // glx render opcode 19 - color4ubv
  /**
   * @see <a href="glColor4ub.html">glColor4ub</a>
   */
  public void color4ub (boolean red, boolean green, 
    boolean blue, boolean alpha) {

    begin_render_request (19, 8);
    render_request.write1 (red);    
    render_request.write1 (green);    
    render_request.write1 (blue);
    render_request.write1 (alpha);
  }


  // glx render opcode 20 - color4uiv
  /**
   * @see <a href="glColor4ui.html">glColor4ui</a>
   */
  public void color4ui (int red, int green, int blue, int alpha) {
    begin_render_request (20, 20);
    render_request.write4 (red);    
    render_request.write4 (green);    
    render_request.write4 (blue);
    render_request.write4 (alpha);
  }


  // glx render opcode 21 - color4usv
  /**
   * @see <a href=5.html">glColor4us</a>
   */
  public void color4us (int red, int green, int blue, int alpha) {
    begin_render_request (21, 12);
    render_request.write2 (red);    
    render_request.write2 (green);    
    render_request.write2 (blue);
    render_request.write2 (alpha);
  }


  // glx render opcode 22 - edge flagv
  /**
   * @see <a href="glEdgeFlag.html">glEdgeFlag</a>
   */
  public void edge_flag (boolean flag) {
    begin_render_request (22, 8);
    render_request.write1 (flag);
    render_request.write3_unused ();
  }


  // glx render opcode 23 - end
  /**
   * @see <a href="glEnd.html">glEnd</a>
   */
  public void end () {
    begin_render_request (23, 4);
  }


  // glx render opcode 24 - indexdv
  /**
   * @see <a href="glIndexd.html">glIndexd</a>
   */
  public void indexd (double c) {
    begin_render_request (24, 12);
    render_request.write8 (c);
  }


  // glx render opcode 25 - indexfv
  /**
   * @see <a href="glIndexf.html">glIndexf</a>
   */
  public void indexf (float c) {
    begin_render_request (25, 8);
    render_request.write4 (c);
  }


  // glx render opcode 26 - indexiv
  /**
   * @see <a href="glIndexi.html">glIndexi</a>
   */
  public void indexi (int c) {
    begin_render_request (26, 8);
    render_request.write4 (c);
  }


  // glx render opcode 27 - indexsv
  /**
   * @see <a href="glIndexs.html">glIndexs</a>
   */
  public void indexs (int c) {
    begin_render_request (27, 8);
    render_request.write2 (c);
    render_request.write2_unused ();
  }


  // glx render opcode 28 - normal3bv
  /**
   * @see <a href="glNormal3b.html">glNormal3b</a>
   */
  public void normal3b (boolean x, boolean y, boolean z) {
    begin_render_request (28, 8);
    render_request.write1 (x);    
    render_request.write1 (y);    
    render_request.write1 (z);
    render_request.write1_unused ();
  }


  // glx render opcode 29 - normal3dv
  /**
   * @see <a href="glNormal3d.html">glNormal3d</a>
   */
  public void normal3d (double x, double y, double z) {
    begin_render_request (29, 28);
    render_request.write8 (x);
    render_request.write8 (y);
    render_request.write8 (z);
  } 


  // glx render opcode 30 - normal3fv
  /**
   * @see <a href="glNormal3f.html">glNormal3f</a>
   */
  public void normal3f (float x, float y, float z) {
    begin_render_request (30, 16);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 31 - normal3iv
  /**
   * @see <a href="glNormal3i.html">glNormal3i</a>
   */
  public void normal3i (int x, int y, int z) {
    begin_render_request (31, 16);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 32 - normal3sv
  /**
   * @see <a href="glNormal3s.html">glNormal3s</a>
   */
  public void normal3s (int x, int y, int z) {
    begin_render_request (32, 12);
    render_request.write2 (x);
    render_request.write2 (y);
    render_request.write2 (z);
    render_request.write2_unused ();
  } 


  // glx render opcode 33 - raster pos2dv
  /**
   * @see <a href="glRasterPos2d.html">glRasterPos2d</a>
   */
  public void raster_pos2d (double x, double y) {
    begin_render_request (33, 20);
    render_request.write8 (x);
    render_request.write8 (y);
  } 


  // glx render opcode 34 - raster pos2fv
  /**
   * @see <a href="glRasterPos2f.html">glRasterPos2f</a>
   */
  public void raster_pos2f (float x, float y) {
    begin_render_request (34, 12);
    render_request.write4 (x);
    render_request.write4 (y);
  } 


  // glx render opcode 35 - raster pos2iv
  /**
   * @see <a href="glRasterPos2i.html">glRasterPos2i</a>
   */
  public void raster_pos2i (int x, int y) {
    begin_render_request (35, 12);
    render_request.write4 (x);
    render_request.write4 (y);
  } 


  // glx render opcode 36 - raster pos2sv
  /**
   * @see <a href="glRasterPos2s.html">glRasterPos2s</a>
   */
  public void raster_pos2s (int x, int y) {
    begin_render_request (36, 8);
    render_request.write2 (x);
    render_request.write2 (y);
  } 


  // glx render opcode 37 - raster pos3dv
  /**
   * @see <a href="glRasterPos3d.html">glRasterPos3d</a>
   */
  public void raster_pos3d (double x, double y, double z) {
    begin_render_request (37, 28);
    render_request.write8 (x);
    render_request.write8 (y);
    render_request.write8 (z);
  } 


  // glx render opcode 38 - raster pos3fv
  /**
   * @see <a href="glRasterPos3f.html">glRasterPos3f</a>
   */
  public void raster_pos3f (float x, float y, float z) {
    begin_render_request (38, 16);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 39 - raster pos3iv
  /**
   * @see <a href="glRasterPos3i.html">glRasterPos3i</a>
   */
  public void raster_pos3i (int x, int y, int z) {
    begin_render_request (39, 16);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 40 - raster pos3sv
  /**
   * @see <a href="glRasterPos3s.html">glRasterPos3s</a>
   */
  public void raster_pos3s (int x, int y, int z) {
    begin_render_request (40, 12);
    render_request.write2 (x);
    render_request.write2 (y);
    render_request.write2 (z);
    render_request.write2_unused ();
  } 


  // glx render opcode 41 - raster pos4dv
  /**
   * @see <a href="glRasterPos4d.html">glRasterPos4d</a>
   */
  public void raster_pos4d (double x, double y, double z, double w) {
    begin_render_request (41, 36);
    render_request.write8 (x);
    render_request.write8 (y);
    render_request.write8 (z);
    render_request.write8 (w);
  } 


  // glx render opcode 42 - raster pos4fv
  /**
   * @see <a href="glRasterPos4f.html">glRasterPos4f</a>
   */
  public void raster_pos4f (float x, float y, float z, float w) {
    begin_render_request (42, 20);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
    render_request.write4 (w);
  } 


  // glx render opcode 43 - raster pos4iv
  /**
   * @see <a href="glRasterPos4i.html">glRasterPos4i</a>
   */
  public void raster_pos4i (int x, int y, int z, int w) {
    begin_render_request (43, 20);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
    render_request.write4 (w);
  } 


  // glx render opcode 44 - raster pos4sv
  /**
   * @see <a href="glRasterPos4s.html">glRasterPos4s</a>
   */
  public void raster_pos4s (int x, int y, int z, int w) {
    begin_render_request (44, 12);
    render_request.write2 (x);
    render_request.write2 (y);
    render_request.write2 (z);
    render_request.write2 (w);
  } 


  // glx render opcode 45 - rectdv
  /**
   * @see <a href="glRectd.html">glRectd</a>
   */
  public void rectd (double x1, double x2, double y1, double y2) {
    begin_render_request (45, 36);
    render_request.write8 (x1);
    render_request.write8 (x2);
    render_request.write8 (y1);
    render_request.write8 (y2);
  } 


  // glx render opcode 46 - rectfv
  /**
   * @see <a href="glRectf.html">glRectf</a>
   */
  public void rectf (float x1, float x2, float y1, float y2) {
    begin_render_request (46, 20);
    render_request.write4 (x1);
    render_request.write4 (x2);
    render_request.write4 (y1);
    render_request.write4 (y2);
  } 


  // glx render opcode 47 - rectiv
  /**
   * @see <a href="glRecti.html">glRecti</a>
   */
  public void recti (int x1, int x2, int y1, int y2) {
    begin_render_request (47, 20);
    render_request.write4 (x1);
    render_request.write4 (x2);
    render_request.write4 (y1);
    render_request.write4 (y2);
  } 


  // glx render opcode 48 - rectsv
  /**
   * @see <a href="glRects.html">glRects</a>
   */
  public void rects (int x1, int x2, int y1, int y2) {
    begin_render_request (48, 12);
    render_request.write2 (x1);
    render_request.write2 (x2);
    render_request.write2 (y1);
    render_request.write2 (y2);
  } 


  // glx render opcode 49 - texture coord1dv
  /**
   * @see <a href="glTexCoord1d.html">glTexCoord1d</a>
   */
  public void tex_coord1d (double s) {
    begin_render_request (49, 12);
    render_request.write8 (s);
  } 


  // glx render opcode 50 - texture coord1fv
  /**
   * @see <a href="glTexCoord1f.html">glTexCoord1f</a>
   */
  public void tex_coord1f (float s) {
    begin_render_request (50, 8);
    render_request.write4 (s);
  } 


  // glx render opcode 51 - texture coord1iv
  /**
   * @see <a href="glTexCoord1i.html">glTexCoord1i</a>
   */
  public void tex_coord1i (int s) {
    begin_render_request (51, 8);
    render_request.write4 (s);
  } 


  // glx render opcode 52 - texture coord1sv
  /**
   * @see <a href="glTexCoord1f.html">glTexCoord1f</a>
   */
  public void tex_coord1s (int s) {
    begin_render_request (52, 8);
    render_request.write2 (s);
    render_request.write2_unused ();
  } 


  // glx render opcode 53 - texture coord2dv
  /**
   * @see <a href="glTexCoord2d.html">glTexCoord2d</a>
   */
  public void tex_coord2d (double s, double t) {
    begin_render_request (53, 20);
    render_request.write8 (s);
    render_request.write8 (t);
  } 


  // glx render opcode 54 - texture coord2fv
  /**
   * @see <a href="glTexCoord2f.html">glTexCoord2f</a>
   */
  public void tex_coord2f (float s, float t) {
    begin_render_request (54, 12);
    render_request.write4 (s);
    render_request.write4 (t);
  } 


  // glx render opcode 55 - texture coord2iv
  /**
   * @see <a href="glTexCoord2i.html">glTexCoord2i</a>
   */
  public void tex_coord2i (int s, int t) {
    begin_render_request (55, 12);
    render_request.write4 (s);
    render_request.write4 (t);
  } 


  // glx render opcode 56 - texture coord2sv
  /**
   * @see <a href="glTexCoord2f.html">glTexCoord2f</a>
   */
  public void tex_coord2s (int s, int t) {
    begin_render_request (56, 8);
    render_request.write2 (s);
    render_request.write2 (t);
  } 


  // glx render opcode 57 - texture coord3dv
  /**
   * @see <a href="glTexCoord3d.html">glTexCoord3d</a>
   */
  public void tex_coord3d (double s, double t, double r) {
    begin_render_request (57, 28);
    render_request.write8 (s);
    render_request.write8 (t);
    render_request.write8 (r);
  } 


  // glx render opcode 58 - texture coord3fv
  /**
   * @see <a href="glTexCoord3f.html">glTexCoord3f</a>
   */
  public void tex_coord3f (float s, float t, float r) {
    begin_render_request (58, 16);
    render_request.write4 (s);
    render_request.write4 (t);
    render_request.write4 (r);
  } 


  // glx render opcode 59 - texture coord3iv
  /**
   * @see <a href="glTexCoord3i.html">glTexCoord3i</a>
   */
  public void tex_coord3i (int s, int t, int r) {
    begin_render_request (59, 16);
    render_request.write4 (s);
    render_request.write4 (t);
    render_request.write4 (r);
  } 


  // glx render opcode 60 - texture coord3sv
  /**
   * @see <a href="glTexCoord3f.html">glTexCoord3f</a>
   */
  public void tex_coord3s (int s, int t, int r) {
    begin_render_request (60, 12);
    render_request.write2 (s);
    render_request.write2 (t);
    render_request.write2 (r);
    render_request.write2_unused ();
  } 


  // glx render opcode 61 - texture coord4dv
  /**
   * @see <a href="glTexCoord4d.html">glTexCoord4d</a>
   */
  public void tex_coord4d (double s, double t, double r, double q) {
    begin_render_request (61, 36);
    render_request.write8 (s);
    render_request.write8 (t);
    render_request.write8 (r);
    render_request.write8 (q);
  } 


  // glx render opcode 62 - texture coord4fv
  /**
   * @see <a href="glTexCoord4f.html">glTexCoord4f</a>
   */
  public void tex_coord4f (float s, float t, float r, float q) {
    begin_render_request (62, 20);
    render_request.write4 (s);
    render_request.write4 (t);
    render_request.write4 (r);
    render_request.write4 (q);
  } 


  // glx render opcode 63 - texture coord4iv
  /**
   * @see <a href="glTexCoord4i.html">glTexCoord4i</a>
   */
  public void tex_coord4i (int s, int t, int r, int q) {
    begin_render_request (63, 20);
    render_request.write4 (s);
    render_request.write4 (t);
    render_request.write4 (r);
    render_request.write4 (q);
  } 


  // glx render opcode 64 - texture coord4sv
  /**
   * @see <a href="glTexCoord4f.html">glTexCoord4f</a>
   */
  public void tex_coord4s (int s, int t, int r, int q) {
    begin_render_request (64, 12);
    render_request.write2 (s);
    render_request.write2 (t);
    render_request.write2 (r);
    render_request.write2 (q);
  } 


  // glx render opcode 65 - vertex2dv
  /**
   * @see <a href="glVertex2d.html">glVertex2d</a>
   */
  public void vertex2d (double x, double y) {
    begin_render_request (65, 20);
    render_request.write8 (x);    
    render_request.write8 (y);    
  }


  // glx render opcode 66 - vertex2fv
  /**
   * @see <a href="glVertex2f.html">glVertex2f</a>
   */
  public void vertex2f (float x, float y) {
    begin_render_request (66, 12);
    render_request.write4 (x);    
    render_request.write4 (y);    
  }


  // glx render opcode 67 - vertex2iv
  /**
   * @see <a href="glVertex2i.html">glVertex2i</a>
   */
  public void vertex2i (int x, int y) {
    begin_render_request (67, 12);
    render_request.write4 (x);    
    render_request.write4 (y);    
  }


  // glx render opcode 68 - vertex2sv
  /**
   * @see <a href="glVertex2s.html">glVertex2s</a>
   */
  public void vertex2s (int x, int y) {
    begin_render_request (68, 8);
    render_request.write2 (x);    
    render_request.write2 (y);    
  }


  // glx render opcode 69  - vertex3dv
  /**
   * @see <a href="glVertex3d.html">glVertex3d</a>
   */
  public void vertex3d (double x, double y, double z) {
    begin_render_request (69, 28);
    render_request.write8 (x);    
    render_request.write8 (y);    
    render_request.write8 (z);
  }


  // glx render opcode 70 - vertex3fv
  /**
   * @see <a href="glVertex3f.html">glVertex3f</a>
   */
  public void vertex3f (float x, float y, float z) {
    begin_render_request (70, 16);
    render_request.write4 (x);    
    render_request.write4 (y);    
    render_request.write4 (z);
  }


  // glx render opcode 71 - vertex3iv
  /**
   * @see <a href="glVertex3i.html">glVertex3i</a>
   */
  public void vertex3i (int x, int y, int z) {
    begin_render_request (71, 16);
    render_request.write4 (x);    
    render_request.write4 (y);    
    render_request.write4 (z);
  }


  // glx render opcode 72 - vertex3sv
  /**
   * @see <a href="glVertex3s.html">glVertex3s</a>
   */
  public void vertex3s (int x, int y, int z) {
    begin_render_request (72, 12);
    render_request.write2 (x);    
    render_request.write2 (y);    
    render_request.write2 (z);
    render_request.write2_unused ();
  }


  // glx render opcode 73  - vertex4dv
  /**
   * @see <a href="glVertex4d.html">glVertex4d</a>
   */
  public void vertex4d (double x, double y, double z, double w) {
    begin_render_request (73, 36);
    render_request.write8 (x);    
    render_request.write8 (y);    
    render_request.write8 (z);
    render_request.write8 (w);
  }


  // glx render opcode 74 - vertex4fv
  /**
   * @see <a href="glVertex4f.html">glVertex4f</a>
   */
  public void vertex4f (float x, float y, float z, float w) {
    begin_render_request (74, 20);
    render_request.write4 (x);    
    render_request.write4 (y);    
    render_request.write4 (z);
    render_request.write4 (w);
  }


  // glx render opcode 75 - vertex4iv
  /**
   * @see <a href="glVertex4i.html">glVertex4i</a>
   */
  public void vertex4i (int x, int y, int z, int w) {
    begin_render_request (75, 20);
    render_request.write4 (x);    
    render_request.write4 (y);    
    render_request.write4 (z);
    render_request.write4 (w);
  }


  // glx render opcode 76 - vertex4sv
  /**
   * @see <a href="glVertex4s.html">glVertex4s</a>
   */
  public void vertex4s (int x, int y, int z, int w) {
    begin_render_request (76, 12);
    render_request.write2 (x);    
    render_request.write2 (y);    
    render_request.write2 (z);
    render_request.write2 (w);
  }


  // glx render opcode 77 - clip plane
  /**
   * @see <a href="glClipPlane.html">glClipPlane</a>
   */
  public void clip_plane (int plane, double [] equation) {
    begin_render_request (77, 40);
    render_request.write8 (equation [0]);
    render_request.write8 (equation [1]);
    render_request.write8 (equation [2]);
    render_request.write8 (equation [3]);
    render_request.write4 (plane);
  }


  // glx render opcode 78 - color material
  /**
   * @see <a href="glColorMaterial.html">glColorMaterial</a>
   */
  public void color_material (int face, int mode) {
    begin_render_request (78, 12);
    render_request.write4 (face);
    render_request.write4 (mode);
  }


  // glx render opcode 79 - cull face
  /**
   * @see <a href="glCullFace.html">glCullFace</a>
   */
  public void cull_face (int mode) {
    begin_render_request (79, 8);
    render_request.write4 (mode);
  }


  // glx render opcode 80 - fogf
  /**
   * @see <a href="glFogf.html">glFogf</a>
   */
  public void fogf (int pname, float param) {
    begin_render_request (80, 12);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 81 - fogfv
  /**
   * @see <a href="glFogfv.html">glFogfv</a>
   */
  public void fogfv (int pname, float [] params) {
    int n = 0;

    switch (pname) {
    case FOG_MODE:              // fall through
    case FOG_DENSITY:           // fall through
    case FOG_START:             // fall through
    case FOG_END:               // fall through
    case FOG_INDEX: n = 1; break;      
    case FOG_COLOR: n = 4; break;
    }
    
    begin_render_request (81, 8+4*n);
    render_request.write4 (pname);    
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 82 - fogi
  /**
   * @see <a href="glFogi.html">glFogi</a>
   */
  public void fogi (int pname, int param) {
    begin_render_request (82, 12);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 83 - fogiv
  /**
   * @see <a href="glFogiv.html">glFogiv</a>
   */
  public void fogiv (int pname, int [] params) {
    int n = 0;

    switch (pname) {
    case FOG_MODE:              // fall through
    case FOG_DENSITY:           // fall through
    case FOG_START:             // fall through
    case FOG_END:               // fall through
    case FOG_INDEX: n = 1; break;
    case FOG_COLOR: n = 4; break;
    }
    
    begin_render_request (83, 8+4*n);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  /**
   * @see <a href="glFrontFace.html">glFrontFace</a>
   */
  public void front_face (int mode) {
    begin_render_request (84, 8);
    render_request.write4 (mode);
  }


  // glx render opcode 85 - hint
  /**
   * @see <a href="glHint.html">glHint</a>
   */
  public void hint (int target, int mode) {
    begin_render_request (85, 12);
    render_request.write4 (target);
    render_request.write4 (mode);
  }


  // glx render opcode 86 - lightf
  /**
   * @see <a href="glLightf.html">glLightf</a>
   */
  public void lightf (int light, int pname, float param) {
    begin_render_request (86, 16);
    render_request.write4 (light);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 87 - lightfv
  /**
   * @see <a href="glLightfv.html">glLightfv</a>
   */
  public void lightfv (int light, int pname, float [] params) {
    int n = 0;

    switch (pname) {
    case SPOT_EXPONENT:         // fall through
    case SPOT_CUTOFF:           // fall through
    case CONSTANT_ATTENUATION:  // fall through
    case LINEAR_ATTENUATION:    // fall through
    case QUADRATIC_ATTENUATION: n = 1; break;
    case SPOT_DIRECTION: n = 3; break;
    case AMBIENT:               // fall through
    case DIFFUSE:               // fall through
    case SPECULAR:              // fall through
    case POSITION: n = 4; break;
    }
    
    begin_render_request (87, 12+4*n);
    render_request.write4 (light);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 88 - lighti
  /**
   * @see <a href="glLighti.html">glLighti</a>
   */
  public void lighti (int light, int pname, int param) {
    begin_render_request (88, 16);
    render_request.write4 (light);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 89 - lightiv
  /**
   * @see <a href="glLightiv.html">glLightiv</a>
   */
  public void lightiv (int light, int pname, int [] params) {
    int n = 0;

    switch (pname) {
    case SPOT_EXPONENT:         // fall through
    case SPOT_CUTOFF:           // fall through
    case CONSTANT_ATTENUATION:  // fall through
    case LINEAR_ATTENUATION:    // fall through
    case QUADRATIC_ATTENUATION: n = 1; break;
    case SPOT_DIRECTION: n = 3; break;
    case AMBIENT:               // fall through
    case DIFFUSE:               // fall through
    case SPECULAR:              // fall through
    case POSITION: n = 4; break;
    }
    
    begin_render_request (89, 12+4*n);
    render_request.write4 (light);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 90 - light modelf
  /**
   * @see <a href="glLightModelf.html">glLightModelf</a>
   */
  public void light_modelf (int pname, float param) {
    begin_render_request (90, 12);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 91 - light modelfv
  /**
   * @see <a href="glLightModelfv.html">glLightModelfv</a>
   */
  public void light_modelfv (int pname, float [] params) {
    int n = 0;

    switch (pname) {
    case LIGHT_MODEL_COLOR_CONTROL: // fall through
    case LIGHT_MODEL_LOCAL_VIEWER: // fall through
    case LIGHT_MODEL_TWO_SIDE: n = 1; break;
    case LIGHT_MODEL_AMBIENT: n = 4; break;
    }

    begin_render_request (91, 8+4*n);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 92 - light modeli
  /**
   * @see <a href="glLightModeli.html">glLightModeli</a>
   */
  public void light_modeli (int pname, int param) {
    begin_render_request (92, 12);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 93 - light modeliv
  /**
   * @see <a href="glLightModeliv.html">glLightModeliv</a>
   */
  public void light_modeliv (int pname, int [] params) {
    int n = 0;

    switch (pname) {
    case LIGHT_MODEL_COLOR_CONTROL: // fall through
    case LIGHT_MODEL_LOCAL_VIEWER: // fall through
    case LIGHT_MODEL_TWO_SIDE: n = 1; break;
    case LIGHT_MODEL_AMBIENT: n = 4; break;
    }

    begin_render_request (93, 8+4*n);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 94 - line stipple
  /**
   * @see <a href="glLineStipple.html">glLineStipple</a>
   */
  public void line_stipple (int factor, int pattern) {
    begin_render_request (94, 12);
    render_request.write4 (factor);
    render_request.write2 (pattern);
    render_request.write2_unused ();
  }


  // glx render opcode 95 - line width
  /**
   * @see <a href="glLineWidth.html">glLineWidth</a>
   */
  public void line_width (float width) {
    begin_render_request (95, 8);
    render_request.write4 (width);
  }


  // glx render opcode 96 - materialf
  /**
   * @see <a href="glMaterialf.html">glMaterialf</a>
   */
  public void materialf (int face, int pname, float param) {
    begin_render_request (96, 16);
    render_request.write4 (face);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 97 - materialfv
  /**
   * @see <a href="glMaterialfv.html">glMaterialfv</a>
   */
  public void materialfv (int face, int pname, float [] params) {
    int n = 0;

    switch (pname) {
    case SHININESS: n = 1; break;
    case COLOR_INDEXES: n = 3; break;
    case AMBIENT:               // fall through
    case DIFFUSE:               // fall through
    case SPECULAR:              // fall through
    case EMISSION:              // fall through
    case AMBIENT_AND_DIFFUSE: n = 4; break;
    }

    begin_render_request (97, 12+4*n);
    render_request.write4 (face);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 98 - materiali
  /**
   * @see <a href="glMateriali.html">glMateriali</a>
   */
  public void materiali (int face, int pname, int param) {
    begin_render_request (98, 16);
    render_request.write4 (face);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 99 - materialiv
  /**
   * @see <a href="glMaterialiv.html">glMaterialiv</a>
   */
  public void materialiv (int face, int pname, int [] params) {
    int n = 0;

    switch (pname) {
    case SHININESS: n = 1; break;
    case COLOR_INDEXES: n = 3; break;
    case AMBIENT:               // fall through
    case DIFFUSE:               // fall through
    case SPECULAR:              // fall through
    case EMISSION:              // fall through
    case AMBIENT_AND_DIFFUSE: n = 4; break;
    }

    begin_render_request (99, 12+4*n);
    render_request.write4 (face);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 100 - point size
  /**
   * @see <a href="glPointSize.html">glPointSize</a>
   */
  public void point_size (float size) {
    begin_render_request (100, 8);
    render_request.write4 (size);
  }


  // glx render opcode 101 - polygon mode
  /**
   * @see <a href="glPolygonMode.html">glPolygonMode</a>
   */
  public void polygon_mode (int face, int mode) {
    begin_render_request (101, 12);
    render_request.write4 (face);
    render_request.write4 (mode);
  }



  // glx render opcode 102 - polygon stipple
  /**
   * @see <a href="glPolygonMode.html">glPolygonStipple</a>
   */
  public void polygon_stipple (byte [] mask) {    
    Data data = new Data (mask);

    begin_render_large_request (102, 24, data.np ());
    render_request.write1 (0);  // swap bytes
    render_request.write1 (false); // java = msb = !lsb_first
    render_request.write2_unused ();

    // FIXME work with other cases??
    render_request.write4 (0);  // row len
    render_request.write4 (0);  // skip rows
    render_request.write4 (0);  // skip pixels
    render_request.write4 (1);  // alignment

    end_render_large_request (data);
  }


  // glx render opcode 103 - scissor
  /**
   * @see <a href="glScissor.html">glScissor</a>
   */
  public void scissor (int x, int y, int width, int height) {
    begin_render_request (103, 20);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
  } 

  
  // glx render opcode 104 - shade model
  /**
   * @see <a href="glShadeModel.html">glShadeModel</a>
   */
  public void shade_model (int mode) {
    begin_render_request (104, 8);
    render_request.write4 (mode);
  }


  // glx render opcode 105 - texture parameterf
  /**
   * @see <a href="glTexParameterf.html">glTexParameterf</a>
   */
  public void tex_parameterf (int target, int pname, float param) {
    begin_render_request (105, 16);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 106 - texture parameterfv
  /**
   * @see <a href="glTexParameterfv.html">glTexParameterfv</a>
   */
  public void tex_parameterfv (int target, int pname, 
    float [] params) {

    int n = 0;
    
    switch (pname) {    
    case TEXTURE_MIN_FILTER:    // fall through
    case TEXTURE_MAG_FILTER:    // fall through
    case TEXTURE_WRAP_S:        // fall through
    case TEXTURE_WRAP_T:        // fall through
    case TEXTURE_PRIORITY: n = 1; break;
    case TEXTURE_BORDER_COLOR: n = 4; break;
    }

    begin_render_request (106, 12+4*n);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 107 - texture parameteri
  /**
   * @see <a href="glTexParameteri.html">glTexParameteri</a>
   */
  public void tex_parameteri (int target, int pname, int param) {
    begin_render_request (107, 16);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 108 - texture parameteriv
  /**
   * @see <a href="glTexParameteriv.html">glTexParameteriv</a>
   */
  public void tex_parameteriv (int target, int pname, 
    float [] params) {

    int n = 0;
    
    switch (pname) {    
    case TEXTURE_MIN_FILTER:    // fall through
    case TEXTURE_MAG_FILTER:    // fall through
    case TEXTURE_WRAP_S:        // fall through
    case TEXTURE_WRAP_T:        // fall through
    case TEXTURE_PRIORITY: n = 1; break;
    case TEXTURE_BORDER_COLOR: n = 4; break;
    }

    begin_render_request (108, 12+4*n);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 109 - tex image 1d
  /**
   * @see <a href="glTexImage1d.html">glTexImage1D</a>
   */
  public void tex_image_1d (int target, int level, int internal_format,
    int width, int border, int format, int type, Data pixels) {   

    begin_render_large_request (109, 56, pixels.np ());
    render_request.write1 (0);  // swap bytes
    render_request.write1 (false); // java = msb = !lsb_first
    render_request.write2_unused ();

    // FIXME GL_ABGR_EXT?

    // FIXME work with other cases??
    render_request.write4 (0);  // row len
    render_request.write4 (0);  // skip rows
    render_request.write4 (0);  // skip pixels
    render_request.write4 (1);  // alignment

    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (internal_format);
    render_request.write4 (width);
    render_request.write4_unused ();
    render_request.write4 (border);
    render_request.write4 (format);
    render_request.write4 (type);
    end_render_large_request (pixels);
  }


  // glx render opcode 110 - tex image 2d
  /**
   * @see <a href="glTexImage2d.html">glTexImage2D</a>
   */
  public void tex_image_2d (int target, int level, int internal_format,
    int width, int height, int border, int format, int type, 
    Data pixels) {   

    begin_render_large_request (110, 56, pixels.np ());
    render_request.write1 (0);  // swap bytes
    render_request.write1 (false); // java = msb = !lsb_first
    render_request.write2_unused ();

    // FIXME GL_ABGR_EXT?

    // FIXME work with other cases??
    render_request.write4 (0);  // row len
    render_request.write4 (0);  // skip rows
    render_request.write4 (0);  // skip pixels
    render_request.write4 (1);  // alignment

    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (internal_format);
    render_request.write4 (width);
    render_request.write4 (height);
    render_request.write4 (border);
    render_request.write4 (format);
    render_request.write4 (type);
    end_render_large_request (pixels);
  }


  // glx render opcode 111 - texture envf
  /**
   * @see <a href="glTexEnvf.html">glTexEnvf</a>
   */
  public void tex_envf (int target, int pname, float param) {
    begin_render_request (111, 16);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 112 - texture envfv
  /**
   * @see <a href="glTexEnvfv.html">glTexEnvfv</a>
   */
  public void tex_envfv (int target, int pname, float [] params) {
    int n = 0;

    switch (pname) {
    case TEXTURE_ENV_MODE: n = 1; break;
    case TEXTURE_ENV_COLOR: n = 4; break;
    }

    begin_render_request (112, 12+4*n);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 113 - texture envi
  /**
   * @see <a href="glTexEnvi.html">glTexEnvi</a>
   */
  public void tex_envi (int target, int pname, int param) {
    begin_render_request (113, 16);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 114 - texture enviv
  /**
   * @see <a href="glTexEnviv.html">glTexEnviv</a>
   */
  public void tex_enviv (int target, int pname, int [] params) {
    int n = 0;

    switch (pname) {
    case TEXTURE_ENV_MODE: n = 1; break;
    case TEXTURE_ENV_COLOR: n = 4; break;
    }

    begin_render_request (114, 12+4*n);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 115 - texture gend
  /**
   * @see <a href="glTexGend.html">glTexGend</a>
   */
  public void tex_gend (int coord, int pname, double param) {
    begin_render_request (115, 20);
    render_request.write4 (coord);
    render_request.write4 (pname);
    render_request.write8 (param);
  }


  // glx render opcode 116 - texture gendv
  /**
   * @see <a href="glTexGendv.html">glTexGendv</a>
   */
  public void tex_gendv (int coord, int pname, double [] params) {
    int n = 0;

    switch (pname) {
    case TEXTURE_GEN_MODE: n = 1; break;
    case OBJECT_PLANE:        // fall through
    case EYE_PLANE: n = 4; break;
    }

    begin_render_request (116, 12+8*n);
    render_request.write4 (coord);
    render_request.write4 (pname);
    render_request.write8 (params, 0, n);
  }


  // glx render opcode 117 - texture genf
  /**
   * @see <a href="glTexGenf.html">glTexGenf</a>
   */
  public void tex_genf (int coord, int pname, float param) {
    begin_render_request (117, 16);
    render_request.write4 (coord);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 118 - texture genfv
  /**
   * @see <a href="glTexGenfv.html">glTexGenfv</a>
   */
  public void tex_genfv (int coord, int pname, float [] params) {
    int n = 0;

    switch (pname) {
    case TEXTURE_GEN_MODE: n = 1; break;
    case OBJECT_PLANE:        // fall through
    case EYE_PLANE: n = 4; break;
    }

    begin_render_request (118, 12+4*n);
    render_request.write4 (coord);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 119 - texture geni
  /**
   * @see <a href="glTexGeni.html">glTexGeni</a>
   */
  public void tex_geni (int coord, int pname, int param) {
    begin_render_request (119, 16);
    render_request.write4 (coord);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 120 - texture geniv
  /**
   * @see <a href="glTexGeniv.html">glTexGeniv</a>
   */
  public void tex_geniv (int coord, int pname, int [] params) {
    int n = 0;

    switch (pname) {
    case TEXTURE_GEN_MODE: n = 1; break;
    case OBJECT_PLANE:          // fall through
    case EYE_PLANE: n = 4; break;
    }

    begin_render_request (120, 12+4*n);
    render_request.write4 (coord);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 121 - init names
  /**
   * @see <a href="glInitNames.html">glInitNames</a>
   */
  public void init_names () {
    begin_render_request (121, 4);
  }


  // glx render opcode 122 - load name
  /**
   * @see <a href="glLoadName.html">glLoadName</a>
   */
  public void load_name (int name) {
    begin_render_request (122, 8);
    render_request.write4 (name);
  }


  // glx render opcode 123 - pass through
  /**
   * @see <a href="glPassThrough.html">glPassThrough</a>
   */
  public void pass_through  (float token) {
    begin_render_request (123, 8);
    render_request.write4 (token);
  }


  // glx render opcode 124 - pop name
  /**
   * @see <a href="glPopName.html">glPopName</a>
   */
  public void pop_name () {
    begin_render_request (124, 4);
  }


  // glx render opcode 125 - push name
  /**
   * @see <a href="glPushName.html">glPushName</a>
   */
  public void push_name (int name) {
    begin_render_request (125, 8);
    render_request.write4 (name);
  }


  // glx render opcode 126 - draw buffer
  /**
   * @see <a href="glClear.html">glDrawBuffer</a>
   */
  public void draw_buffer (int mode) {
    begin_render_request (126, 8);
    render_request.write4 (mode);    
  }


  // glx render opcode 127 - clear
  /**
   * @see <a href="glClear.html">glClear</a>
   */
  public void clear (int mask) {
    begin_render_request (127, 8);
    render_request.write4 (mask);    
  }


  // glx render opcode 128 - clear accum
  /**
   * @see <a href="glClearAccum.html">glClearAccum</a>
   */
  public void clear_accum (float red, float green, 
    float blue, float alpha) {

    begin_render_request (128, 20);
    render_request.write4 (red);    
    render_request.write4 (green);
    render_request.write4 (blue);
    render_request.write4 (alpha);
  }


  // glx render opcode 129 - clear index
  /**
   * @see <a href="glClearIndex.html">glClearIndex</a>
   */
  public void clear_index (float c) {
    begin_render_request (129, 8);
    render_request.write4 (c);
  }


  // glx render opcode 130 - clear color
  /**
   * @see <a href="glClearColor.html">glClearColor</a>
   */
  public void clear_color (float red, float green, 
    float blue, float alpha) {

    begin_render_request (130, 20);
    render_request.write4 (red);    
    render_request.write4 (green);
    render_request.write4 (blue);
    render_request.write4 (alpha);
  }


  // glx render opcode 131 - clear stencil
  /**
   * @see <a href="glClearStencil.html">glClearStencil</a>
   */
  public void clear_stencil (int s) {
    begin_render_request (131, 8);
    render_request.write4 (s);
  }


  // glx render opcode 132 - clear depth
  /**
   * @see <a href="glClearDepth.html">glClearDepth</a>
   */
  public void clear_depth (double depth) {
    begin_render_request (132, 12);
    render_request.write8 (depth);
  }


  // glx render opcode 133 - stencil mask
  /**
   * @see <a href="glStencilMask.html">glStencilMask</a>
   */
  public void stencil_mask (int mask) {
    begin_render_request (133, 8);
    render_request.write4 (mask);
  }


  // glx render opcode 134 - color mask
  /**
   * @see <a href="glColorMask.html">glColorMask</a>
   */
  public void color_mask (boolean red, boolean green, 
    boolean blue, boolean alpha) {

    begin_render_request (134, 8);
    render_request.write1 (red);    
    render_request.write1 (green);    
    render_request.write1 (blue);
    render_request.write1 (alpha);
  }


  // glx render opcode 135 - depth mask
  /**
   * @see <a href="glDepthMask.html">glDepthMask</a>
   */
  public void depth_mask (boolean flag) {
    begin_render_request (135, 8);
    render_request.write1 (flag);
    render_request.write3_unused ();
  }


  // glx render opcode 136 - index mask
  /**
   * @see <a href="glIndexMask.html">glIndexMask</a>
   */
  public void index_mask (int mask) {
    begin_render_request (136, 8);
    render_request.write4 (mask);
  }


  // glx render opcode 137 - accum
  /**
   * @see <a href="glAccum.html">glAccum</a>
   */
  public void accum (int op, float value) {
    begin_render_request (137, 12);
    render_request.write4 (op);
    render_request.write4 (value);
  }
  


  // glx render opcode 138 - disable
  /**
   * @see <a href="glDisable.html">glDisable</a>
   */
  public void disable (int capability) {
    begin_render_request (138, 8);
    render_request.write4 (capability);
  }


  // glx render opcode 139 - enable
  /**
   * @see <a href="glEnable.html">glEnable</a>
   */
  public void enable (int capability) {
    begin_render_request (139, 8);
    render_request.write4 (capability);
  }


  // glx render opcode 141 - pop attrib
  /**
   * @see <a href="glPopAttrib.html">glPopAttrib</a>
   */
  public void pop_attrib () {
    begin_render_request (141, 4);
  }


  // glx render opcode 142 - push attrib
  /**
   * @see <a href="glPushAttrib.html">glPushAttrib</a>
   */
  public void push_attrib (int mask) {
    begin_render_request (142, 8);
    render_request.write4 (mask);
  }


  // glx render opcode 143 - map1d
  /**
   * @see <a href="glMap1d.html">glMap1d</a>
   */
  public void map1d (int target, double u1, double u2, int stride, 
    int order, double [] points) {

    int k = 0;

    switch (target) {
    case MAP1_INDEX:            // fall through
    case MAP1_TEXTURE_COORD_1: k = 1; break;
    case MAP1_TEXTURE_COORD_2: k = 2; break;
    case MAP1_NORMAL:           // fall through
    case MAP1_TEXTURE_COORD_3:  // fall through
    case MAP1_VERTEX_3: k = 3; break;
    case MAP1_COLOR_4:          // fall through
    case MAP1_TEXTURE_COORD_4:  // fall through
    case MAP1_VERTEX_4: k = 4; break;
    }


    int n = order * k * 8;

    Data data = new Data (n);
    for (int i=0; i<order; i++)
      for (int j=0; j<k; j++)
        data.write8 (points [i*stride + j]);

    begin_render_large_request (143, 28, n);
    render_request.write8 (u1);
    render_request.write8 (u2);
    render_request.write4 (target);
    render_request.write4 (order);
    end_render_large_request (data);
  }


  // glx render opcode 144 - map1f
  /**
   * @see <a href="glMap1f.html">glMap1f</a>
   */
  public void map1f (int target, float u1, float u2, int stride, 
    int order, float [] points) {

    int k = 0;

    switch (target) {
    case MAP1_INDEX:            // fall through
    case MAP1_TEXTURE_COORD_1: k = 1; break;
    case MAP1_TEXTURE_COORD_2: k = 2; break;
    case MAP1_NORMAL:           // fall through
    case MAP1_TEXTURE_COORD_3:  // fall through
    case MAP1_VERTEX_3: k = 3; break;
    case MAP1_COLOR_4:          // fall through
    case MAP1_TEXTURE_COORD_4:  // fall through
    case MAP1_VERTEX_4: k = 4; break;
    }


    int n = order * k * 4;

    Data data = new Data (n);
    for (int i=0; i<order; i++)
      for (int j=0; j<k; j++)
        data.write4 (points [i*stride + j]);

    begin_render_large_request (144, 20, n);
    render_request.write4 (target);
    render_request.write4 (u1);
    render_request.write4 (u2);
    render_request.write4 (order);
    end_render_large_request (data);
  }


  // glx render opcode 145 - map2d
  /**
   * @see <a href="glMap2d.html">glMap2d</a>
   */
  public void map2d (int target, double u1, double u2, 
    int ustride, int uorder, double v1, double v2,
    int vstride, int vorder, double [] points) {

    int k = 0;

    switch (target) {
    case MAP2_INDEX:            // fall through
    case MAP2_TEXTURE_COORD_1: k = 1; break;
    case MAP2_TEXTURE_COORD_2: k = 2; break;
    case MAP2_NORMAL:           // fall through
    case MAP2_TEXTURE_COORD_3:  // fall through
    case MAP2_VERTEX_3: k = 3; break;
    case MAP2_COLOR_4:          // fall through
    case MAP2_TEXTURE_COORD_4:  // fall through
    case MAP2_VERTEX_4: k = 4; break;
    }

    int n = vorder * uorder * k * 8;

    Data data = new Data (n);
    for (int i=0; i<vorder; i++)
      for (int j=0; j<uorder; j++)
        for (int m=0; m<k; m++)
          data.write8 (points [i*ustride + j*vstride + m]);

    begin_render_large_request (145, 48, n);
    render_request.write8 (u1);
    render_request.write8 (u2);
    render_request.write8 (v1);
    render_request.write8 (v2);
    render_request.write4 (target);
    render_request.write4 (uorder);
    render_request.write4 (vorder);
    end_render_large_request (data);
  }


  // glx render opcode 146 - map2f
  /**
   * @see <a href="glMap2f.html">glMap2f</a>
   */
  public void map2f (int target, float u1, float u2, 
    int ustride, int uorder, float v1, float v2,
    int vstride, int vorder, float [] points) {

    int k = 0;

    switch (target) {
    case MAP2_INDEX:            // fall through
    case MAP2_TEXTURE_COORD_1: k = 1; break;
    case MAP2_TEXTURE_COORD_2: k = 2; break;
    case MAP2_NORMAL:           // fall through
    case MAP2_TEXTURE_COORD_3:  // fall through
    case MAP2_VERTEX_3: k = 3; break;
    case MAP2_COLOR_4:          // fall through
    case MAP2_TEXTURE_COORD_4:  // fall through
    case MAP2_VERTEX_4: k = 4; break;
    }

    int n = vorder * uorder * k * 4;

    Data data = new Data (n);
    for (int i=0; i<vorder; i++)
      for (int j=0; j<uorder; j++)
        for (int m=0; m<k; m++)
          data.write4 (points [i*ustride + j*vstride + m]);

    begin_render_large_request (146, 32, n);
    render_request.write4 (target);
    render_request.write4 (u1);
    render_request.write4 (u2);
    render_request.write4 (uorder);
    render_request.write4 (v1);
    render_request.write4 (v2);
    render_request.write4 (vorder);
    end_render_large_request (data);
  }


  // glx render opcode 147 - map grid1d
  /**
   * @see <a href="glMapGrid1d.html">glMapGrid1d</a>
   */
  public void map_grid1d (int un, double u1, double u2) {
    begin_render_request (147, 24);
    render_request.write8 (u1);
    render_request.write8 (u2);
    render_request.write4 (un);
  }


  // glx render opcode 148 - map grid1f
  /**
   * @see <a href="glMapGrid1f.html">glMapGrid1f</a>
   */
  public void map_grid1f (int un, float u1, float u2) {
    begin_render_request (148, 16);
    render_request.write4 (un);
    render_request.write4 (u1);
    render_request.write4 (u2);
  }


  // glx render opcode 149 - map grid2d
  /**
   * @see <a href="glMapGrid2d.html">glMapGrid2d</a>
   */
  public void map_grid2d (int un, double u1, double u2, 
    int vn, double v1, double v2) {

    begin_render_request (149, 44);
    render_request.write8 (u1);
    render_request.write8 (u2);
    render_request.write8 (v1);
    render_request.write8 (v2);
    render_request.write4 (un);
    render_request.write4 (vn);
  }


  // glx render opcode 150 - map grid2f
  /**
   * @see <a href="glMapGrid2f.html">glMapGrid2f</a>
   */
  public void map_grid2f (int un, float u1, float u2, 
    int vn, float v1, float v2) {

    begin_render_request (150, 28);
    render_request.write4 (un);
    render_request.write4 (u1);
    render_request.write4 (u2);
    render_request.write4 (vn);
    render_request.write4 (v1);
    render_request.write4 (v2);
  }


  // glx render opcode 151 - eval coord1dv
  /**
   * @see <a href="glEvalCoord1d.html">glEvalCoord1d</a>
   */
  public void eval_coord1d (double u) {
    begin_render_request (151, 12);
    render_request.write8 (u);
  }


  // glx render opcode 152 - eval coord1df
  /**
   * @see <a href="glEvalCoord1f.html">glEvalCoord1f</a>
   */
  public void eval_coord1f (float u) {
    begin_render_request (152, 8);
    render_request.write4 (u);
  }


  // glx render opcode 153 - eval coord2dv
  /**
   * @see <a href="glEvalCoord2d.html">glEvalCoord2d</a>
   */
  public void eval_coord2d (double u, double v) {
    begin_render_request (153, 20);
    render_request.write8 (u);
    render_request.write8 (v);
  }


  // glx render opcode 154 - eval coord2df
  /**
   * @see <a href="glEvalCoord2f.html">glEvalCoord2f</a>
   */
  public void eval_coord2f (float u, float v) {
    begin_render_request (154, 12);
    render_request.write4 (u);
    render_request.write4 (v);
  }


  // glx render opcode 155 - eval mesh1
  /**
   * @see <a href="glEvalMesh1.html">glEvalMesh1</a>
   */
  public void eval_mesh1 (int mode, int i1, int i2) {
    begin_render_request (155, 16);
    render_request.write4 (mode);
    render_request.write4 (i1);
    render_request.write4 (i2);
  }


  // glx render opcode 156 - eval point1
  /**
   * @see <a href="glEvalPoint1.html">glEvalPoint1</a>
   */
  public void eval_point1 (int i) {
    begin_render_request (156, 8);
    render_request.write4 (i);
  }


  // glx render opcode 157 - eval mesh2
  /**
   * @see <a href="glEvalMesh2.html">glEvalMesh2</a>
   */
  public void eval_mesh2 (int mode, int i1, int i2, int j1, int j2) {
    begin_render_request (157, 24);
    render_request.write4 (mode);
    render_request.write4 (i1);
    render_request.write4 (i2);
    render_request.write4 (j1);
    render_request.write4 (j2);
  }


  // glx render opcode 158 - eval point2
  /**
   * @see <a href="glEvalPoint2.html">glEvalPoint2</a>
   */
  public void eval_point2 (int i, int j) {
    begin_render_request (158, 12);
    render_request.write4 (i);
    render_request.write4 (j);
  }


  // glx render opcode 159 - alpha function
  /**
   * @see <a href="glAlphaFunc.html">glAlphaFunc</a>
   */
  public void alpha_func (int func, int ref) {
    begin_render_request (159, 12);
    render_request.write4 (func);
    render_request.write4 (ref);
  }


  // glx render opcode 160 - blend function
  /**
   * @see <a href="glAlphaFunc.html">glBlendFunc</a>
   */
  public void blend_func (int sfactor, int dfactor) {
    begin_render_request (160, 12);
    render_request.write4 (sfactor);
    render_request.write4 (dfactor);
  }


  // glx render opcode 161 - logic op
  /**
   * @see <a href="glLogicOp.html">glLogicOp</a>
   */
  public void logic_op (int opcode) {
    begin_render_request (161, 8);
    render_request.write4 (opcode);
  }


  // glx render opcode 162 - stencil function
  /**
   * @see <a href="glStencilFunc.html">glStencilFunc</a>
   */
  public void stencil_func (int func, int ref, int mask) {
    begin_render_request (162, 16);
    render_request.write4 (func);
    render_request.write4 (ref);
    render_request.write4 (mask);
  }


  // glx render opcode 163 - stencil op
  /**
   * @see <a href="glStencilOp.html">glStencilOp</a>
   */
  public void stencil_op (int fail, int zfail, int zpass) {
    begin_render_request (163, 16);
    render_request.write4 (fail);
    render_request.write4 (zfail);
    render_request.write4 (zpass);
  }


  // glx render opcode 164 - depth function
  /**
   * @see <a href="glDepthFunc.html">glDepthFunc</a>
   */
  public void depth_func (int func) {
    begin_render_request (164, 8);
    render_request.write4 (func);
  }


  // glx render opcode 165 - pixel zoom
  /**
   * @see <a href="glPixelZoom.html">glPixelZoom</a>
   */
  public void pixel_zoom (float xfactor, float yfactor) {
    begin_render_request (165, 12);
    render_request.write4 (xfactor);
    render_request.write4 (yfactor);
  }


  // glx render opcode 166 - pixel transferf
  /**
   * @see <a href="glPixelTransferf.html">glPixelTransferf</a>
   */
  public void pixel_transferf (int pname, float param) {
    begin_render_request (166, 12);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 167 - pixel transferi
  /**
   * @see <a href="glPixelTransferi.html">glPixelTransferi</a>
   */
  public void pixel_transferi (int pname, int param) {
    begin_render_request (167, 12);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 171 - read buffer
  /**
   * @see <a href="glReadBuffer.html">glReadBuffer</a>
   */
  public void read_buffer (int mode) {
    begin_render_request (171, 8);
    render_request.write4 (mode);    
  }


  // glx render opcode 172 - copy pixels
  /**
   * @see <a href="glCopyPixels.html">glCopyPixels</a>
   */
  public void copy_pixels (int x, int y, 
    int width, int height, int type) {

    begin_render_request (172, 24);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
    render_request.write4 (type);
  } 


  // glx render opcode 173 - draw pixels
  /**
   * @see <a href="glDrawPixels.html">glDrawPixels</a>
   */
  public void draw_pixels (int width, int height, int format, int type,
    Data pixels) {
    
    begin_render_large_request (173, 40, pixels.np ());
    render_request.write1 (0);  // swap bytes
    render_request.write1 (false); // java = msb = !lsb_first
    render_request.write2_unused ();

    // FIXME work with other cases??
    render_request.write4 (0);  // row len
    render_request.write4 (0);  // skip rows
    render_request.write4 (0);  // skip pixels
    render_request.write4 (1);  // alignment

    render_request.write4 (width);
    render_request.write4 (height);
    render_request.write4 (format);
    render_request.write4 (type);
    end_render_large_request (pixels);
  }


  // glx render opcode 174 - depth range
  /**
   * @see <a href="glDepthRange.html">glDepthRange</a>
   */
  public void depth_range (double near, double far) {
    begin_render_request (174, 20);
    render_request.write8 (near);
    render_request.write8 (far);
  }


  // glx render opcode 175 - frustum
  /**
   * @see <a href="glFrustum.html">glFrustum</a>
   */
  public void frustum (double left, double right, double bottom, 
    double top, double near, double far) {

    begin_render_request (175, 52);
    render_request.write8 (left);
    render_request.write8 (right);
    render_request.write8 (bottom);
    render_request.write8 (top);
    render_request.write8 (near);
    render_request.write8 (far);
  }


  // glx render opcode 176 - load identity
  /**
   * @see <a href="glLoadIdentity.html">glLoadIdentity</a>
   */
  public void load_identity () {
    begin_render_request (176, 4);
  }


  // glx render opcode 177 - load matrixf
  /**
   * @see <a href="glLoadMatrixf.html">glLoadMatrixf</a>
   */
  public void load_matrixf (float [] matrix) {
    begin_render_request (177, 68);
    render_request.write4 (matrix, 0, 16);
  }


  // glx render opcode 178 - load matrixd
  /**
   * @see <a href="glLoadMatrixd.html">glLoadMatrixd</a>
   */
  public void load_matrixd (double [] matrix) {
    begin_render_request (178, 132);
    render_request.write8 (matrix, 0, 16);
  }


  // glx render opcode 179 - matrix mode
  /**
   * @see <a href="glMatrixMode.html">glMatrixMode</a>
   */
  public void matrix_mode (int mode) {
    begin_render_request (179, 8);
    render_request.write4 (mode);
  }


  // glx render opcode 180 - mult matrixf
  /**
   * @see <a href="glMultMatrixf.html">glMultMatrixf</a>
   */
  public void mult_matrixf (float [] matrix) {
    begin_render_request (180, 68);
    render_request.write4 (matrix, 0, 16);
  } 


  // glx render opcode 181 - mult matrixd
  /**
   * @see <a href="glMultMatrixd.html">glMultMatrixd</a>
   */
  public void mult_matrixd (double [] matrix) {
    begin_render_request (181, 132);
    render_request.write8 (matrix, 0, 16);
  } 

  
  // glx render opcode 182 - ortho
  /**
   * @see <a href="glOrtho.html">glOrtho</a>
   */
  public void ortho (double left, double right, 
    double bottom, double top, double near, double far) {

    begin_render_request (182, 52);
    render_request.write8 (left);
    render_request.write8 (right);
    render_request.write8 (bottom);
    render_request.write8 (top);
    render_request.write8 (near);
    render_request.write8 (far);
  }


  // glx render opcode 183 - pop matrix
  /**
   * @see <a href="glPopMatrix.html">glPopMatrix</a>
   */
  public void pop_matrix () {
    begin_render_request (183, 4);
  }


  // glx render opcode 184 - push matrix
  /**
   * @see <a href="glPushMatrix.html">glPushMatrix</a>
   */
  public void push_matrix () {
    begin_render_request (184, 4);
  }


  // glx render opcode 185 - rotated
  /**
   * @see <a href="glRotated.html">glRotated</a>
   */
  public void rotated (double angle, double x, double y, double z) {
    begin_render_request (185, 36);
    render_request.write8 (angle);
    render_request.write8 (x);
    render_request.write8 (y);
    render_request.write8 (z);
  } 


  // glx render opcode 186 - rotatef
  /**
   * @see <a href="glRotatef.html">glRotatef</a>
   */
  public void rotatef (float angle, float x, float y, float z) {
    begin_render_request (186, 20);
    render_request.write4 (angle);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 187 - scaled
  /**
   * @see <a href="glScaled.html">glScaled</a>
   */
  public void scaled (double x, double y, double z) {
    begin_render_request (187, 28);
    render_request.write8 (x);
    render_request.write8 (y);
    render_request.write8 (z);
  } 


  // glx render opcode 188 - scalef
  /**
   * @see <a href="glScalef.html">glScalef</a>
   */
  public void scalef (float x, float y, float z) {
    begin_render_request (188, 16);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 189 - translated
  /**
   * @see <a href="glTranslated.html">glTranslated</a>
   */
  public void translated (double x, double y, double z) {
    begin_render_request (189, 28);
    render_request.write8 (x);
    render_request.write8 (y);
    render_request.write8 (z);
  } 


  // glx render opcode 190 - translatef
  /**
   * @see <a href="glTranslatef.html">glTranslatef</a>
   */
  public void translatef (float x, float y, float z) {
    begin_render_request (190, 16);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (z);
  } 


  // glx render opcode 191 - viewport
  /**
   * @see <a href="glViewport.html">glViewport</a>
   */
  public void viewport (int x, int y, int width, int height) {
    begin_render_request (191, 20);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
  } 


  // glx render opcode 193 - draw arrays
  /**
   * @see <a href="glDrawArrays.html">glDrawArrays</a>
   */
  public void draw_arrays (int mode) {
    begin_render_request (193, 16);
    render_request.write4 (mode);
  }



  // glx render opcode 194 - indexubv
  /**
   * @see <a href="glIndexub.html">glIndexub</a>
   */
  public void indexub (boolean c) {
    begin_render_request (194, 8);
    render_request.write1 (c);
    render_request.write3_unused ();
  }


  // glx render opcode 196 - copy color sub table
  /**
   * @see <a href="glCopyColorSubTable.html">glCopyColorSubTable</a>
   */
  public void copy_color_sub_table (int target, int start, 
    int x, int y, int width) {

    begin_render_request (196, 24);
    render_request.write4 (target);
    render_request.write4 (start);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
  } 


  // glx render opcode 197 - active texture arb
  /**
   * @see <a href="glActiveTextureARB.html">glActiveTextureARB</a>
   */
  public void active_texture_arb (int texture) {
    begin_render_request (197, 8);
    render_request.write4 (texture);
  }
  
  
  // glx render opcode 198 - multi-texture coord1dv arb
  /**
   * @see <a href="glMultiTexCoord1dARB.html">glMultiTexCoord1dARB</a>
   */
  public void multi_tex_coord1d_arb (int target, double s) {
    begin_render_request (198, 16);
    render_request.write8 (s);
    render_request.write4 (target);
  } 


  // glx render opcode 199 - multi-texture coord1fv arb
  /**
   * @see <a href="glMultiTexCoord1fARB.html">glMultiTexCoord1fARB</a>
   */
  public void multi_tex_coord1f_arb (int target, float s) {
    begin_render_request (199, 12);
    render_request.write4 (target);
    render_request.write4 (s);
  } 


  // glx render opcode 200 - multi-texture coord1iv arb
  /**
   * @see <a href="glMultiTexCoord1iARB.html">glMultiTexCoord1iARB</a>
   */
  public void multi_tex_coord1i_arb (int target, int s) {
    begin_render_request (200, 12);
    render_request.write4 (target);
    render_request.write4 (s);
  } 


  // glx render opcode 201 - multi-texture coord1sv arb
  /**
   * @see <a href="glMultiTexCoord1fARB.html">glMultiTexCoord1fARB</a>
   */
  public void multi_tex_coord1s_arb (int target, int s) {
    begin_render_request (201, 12);
    render_request.write4 (target);
    render_request.write2 (s);
    render_request.write2_unused ();
  } 


  // glx render opcode 202 - multi-texture coord2dv arb
  /**
   * @see <a href="glMultiTexCoord2dARB.html">glMultiTexCoord2dARB</a>
   */
  public void multi_tex_coord2d_arb (int target, double s, double t) {
    begin_render_request (202, 24);
    render_request.write8 (s);
    render_request.write8 (t);
    render_request.write4 (target);
  } 


  // glx render opcode 203 - multi-texture coord2fv arb
  /**
   * @see <a href="glMultiTexCoord2fARB.html">glMultiTexCoord2fARB</a>
   */
  public void multi_tex_coord2f_arb (int target, float s, float t) {
    begin_render_request (203, 16);
    render_request.write4 (target);
    render_request.write4 (s);
    render_request.write4 (t);
  } 


  // glx render opcode 204 - multi-texture coord2iv arb
  /**
   * @see <a href="glMultiTexCoord2iARB.html">glMultiTexCoord2iARB</a>
   */
  public void multi_tex_coord2i_arb (int target, int s, int t) {
    begin_render_request (204, 16);
    render_request.write4 (target);
    render_request.write4 (s);
    render_request.write4 (t);
  } 


  // glx render opcode 205 - multi-texture coord2sv arb
  /**
   * @see <a href="glMultiTexCoord2fARB.html">glMultiTexCoord2fARB</a>
   */
  public void multi_tex_coord2s_arb (int target, int s, int t) {
    begin_render_request (205, 12);
    render_request.write4 (target);
    render_request.write2 (s);
    render_request.write2 (t);
  } 


  // glx render opcode 206 - multi-texture coord3dv arb
  /**
   * @see <a href="glMultiTexCoord3dARB.html">glMultiTexCoord3dARB</a>
   */
  public void multi_tex_coord3d_arb (int target, double s, double t, double r) {
    begin_render_request (206, 32);
    render_request.write8 (s);
    render_request.write8 (t);
    render_request.write8 (r);
    render_request.write4 (target);
  } 


  // glx render opcode 207 - multi-texture coord3fv arb
  /**
   * @see <a href="glMultiTexCoord3fARB.html">glMultiTexCoord3fARB</a>
   */
  public void multi_tex_coord3f_arb (int target, float s, float t, float r) {
    begin_render_request (207, 20);
    render_request.write4 (target);
    render_request.write4 (s);
    render_request.write4 (r);
    render_request.write4 (t);
  } 


  // glx render opcode 208 - multi-texture coord3iv arb
  /**
   * @see <a href="glMultiTexCoord3iARB.html">glMultiTexCoord3iARB</a>
   */
  public void multi_tex_coord3i_arb (int target, int s, int t, int r) {
    begin_render_request (208, 20);
    render_request.write4 (target);
    render_request.write4 (s);
    render_request.write4 (r);
    render_request.write4 (t);
  } 


  // glx render opcode 209 - multi-texture coord3sv arb
  /**
   * @see <a href="glMultiTexCoord3fARB.html">glMultiTexCoord3fARB</a>
   */
  public void multi_tex_coord3s_arb (int target, int s, int t, int r) {
    begin_render_request (209, 16);
    render_request.write4 (target);
    render_request.write2 (s);
    render_request.write2 (r);
    render_request.write2 (t);
    render_request.write2_unused ();
  } 


  // glx render opcode 210 - multi-texture coord4dv arb
  /**
   * @see <a href="glMultiTexCoord4dARB.html">glMultiTexCoord4dARB</a>
   */
  public void multi_tex_coord4d_arb (int target, double s, double t, 
    double r,double q) {

    begin_render_request (210, 40);
    render_request.write8 (s);
    render_request.write8 (t);
    render_request.write8 (r);
    render_request.write8 (q);
    render_request.write4 (target);
  } 


  // glx render opcode 211 - multi-texture coord4fv arb
  /**
   * @see <a href="glMultiTexCoord4fARB.html">glMultiTexCoord4fARB</a>
   */
  public void multi_tex_coord4f_arb (int target, float s, float t, 
    float r, float q) {

    begin_render_request (211, 24);
    render_request.write4 (target);
    render_request.write4 (s);
    render_request.write4 (t);
    render_request.write4 (r);
    render_request.write4 (q);
  } 


  // glx render opcode 212 - multi-texture coord4iv arb
  /**
   * @see <a href="glMultiTexCoord4iARB.html">glMultiTexCoord4iARB</a>
   */
  public void multi_tex_coord4i_arb (int target, int s, int t, 
    int r, int q) {

    begin_render_request (212, 24);
    render_request.write4 (target);
    render_request.write4 (s);
    render_request.write4 (t);
    render_request.write4 (r);
    render_request.write4 (q);
  } 


  // glx render opcode 213 - multi-texture coord4sv arb
  /**
   * @see <a href="glMultiTexCoord4fARB.html">glMultiTexCoord4fARB</a>
   */
  public void multi_tex_coord4s_arb (int target, int s, int t, 
    int r, int q) {

    begin_render_request (213, 16);
    render_request.write4 (target);
    render_request.write2 (s);
    render_request.write2 (t);
    render_request.write2 (r);
    render_request.write2 (q);
  } 


  // glx render opcode 2054 - color table parameterfv
  /**
   * @see <a href="glColorTableParameterf.html">
   * glColorTableParameterf</a>
   */
  public void color_table_parameterfv (int target, int pname, 
    float [] params) {

    int n = 0;

    switch (pname) {
    case COLOR_TABLE_SCALE:     // fall through
    case COLOR_TABLE_BIAS: n = 4; break;
    }

    begin_render_request (2054, 12+4*n);      
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }



  // glx render opcode 2055 - color table parameteriv
  /**
   * @see <a href="glColorTableParameteri.html">
   * glColorTableParameterf</a>
   */
  public void color_table_parameteriv (int target, int pname, 
    int [] params) {

    int n = 0;

    switch (pname) {
    case COLOR_TABLE_SCALE:     // fall through
    case COLOR_TABLE_BIAS: n = 4; break;
    }

    begin_render_request (2055, 12+4*n);      
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 2056 - copy color table
  /**
   * @see <a href="glCopyColorTable.html">glCopyColorTable</a>
   */
  public void copy_color_table (int target, int internal_format, 
    int x, int y, int width) {

    begin_render_request (2056, 24);
    render_request.write4 (target);
    render_request.write4 (internal_format);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
  } 


  // glx render opcode 4096 - blend color
  /**
   * @see <a href="glBlendColor.html">glBlendColor</a>
   */
  public void blend_color (float red, float green, 
    float blue, float alpha) {

    begin_render_request (4096, 20);
    render_request.write4 (red);
    render_request.write4 (green);    
    render_request.write4 (blue);
    render_request.write4 (alpha);
  }


  // glx render opcode 4097 - blend equation
  /**
   * @see <a href="glBlendEquation.html">glBlendEquation</a>
   */
  public void blend_equation (int mode) {
    begin_render_request (4097, 8);
    render_request.write4 (mode);
  }


  // glx render opcode 4098 - polygon offset
  /**
   * @see <a href="glPolygonOffset.html">glPolygonOffset</a>
   */
  public void polygon_offset (float factor, float units) {
    // TODO 1.3: opcode = 192
    begin_render_request (4098, 12);
    render_request.write4 (factor);
    render_request.write4 (units);
  }


  // glx render opcode 4100 - tex subimage 2d
  /**
   * @see <a href="glTexSubimage2d.html">glTexSubimage2D</a>
   */
  public void tex_subimage_2d (int target, int level, 
    int xoffset, int yoffset, int width, int height,
    int format, int type, Data pixels) {

    begin_render_large_request (4100, 60, pixels.np ());
    render_request.write1 (0);  // swap bytes
    render_request.write1 (false); // java = msb = !lsb_first
    render_request.write2_unused ();

    // FIXME work with other cases??
    render_request.write4 (0);  // row len
    render_request.write4 (0);  // skip rows
    render_request.write4 (0);  // skip pixels
    render_request.write4 (1);  // alignment

    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (xoffset);
    render_request.write4 (yoffset);
    render_request.write4 (width);
    render_request.write4 (height);
    render_request.write4 (format);
    render_request.write4 (type);
    render_request.write4_unused ();
    end_render_large_request (pixels);
  }


  // glx render opcode 4103 - convolution parameterf
  /**
   * @see <a href="glConvolutionParameterf.html">
   * glConvolutionParameterf</a>
   */
  public void convolution_parameterf (int target, 
    int pname, float param) {

    begin_render_request (4103, 16);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 4104 - convolution parameterfv
  /**
   * @see <a href="glConvolutionParameterf.html">
   * glConvolutionParameterf</a>
   */
  public void convolution_parameterf (int target, int pname, 
    float [] params) {

    int n = 0;

    switch (pname) {
    case CONVOLUTION_BORDER_COLOR: // fall through
    case CONVOLUTION_FORMAT:    // fall through
    case CONVOLUTION_WIDTH:   // fall through
    case CONVOLUTION_HEIGHT:    // fall through
    case MAX_CONVOLUTION_WIDTH: // fall through
    case MAX_CONVOLUTION_HEIGHT: n = 1; break;
    case CONVOLUTION_FILTER_SCALE: // fall through
    case CONVOLUTION_FILTER_BIAS: n = 4; break;
    }

    begin_render_request (4104, 12+4*n);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 4105 - convolution parameteri
  /**
   * @see <a href="glConvolutionParameteri.html">
   * glConvolutionParameteri</a>
   */
  public void convolution_parameteri (int target, 
    int pname, int param) {

    begin_render_request (4105, 16);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (param);
  }


  // glx render opcode 4106 - convolution parameteriv
  /**
   * @see <a href="glConvolutionParameteri.html">
   * glConvolutionParameteri</a>
   */
  public void convolution_parameteri (int target, int pname, 
    int [] params) {

    int n = 0;

    switch (pname) {
    case CONVOLUTION_BORDER_COLOR: // fall through
    case CONVOLUTION_FORMAT:    // fall through
    case CONVOLUTION_WIDTH:   // fall through
    case CONVOLUTION_HEIGHT:    // fall through
    case MAX_CONVOLUTION_WIDTH: // fall through
    case MAX_CONVOLUTION_HEIGHT: n = 1; break;
    case CONVOLUTION_FILTER_SCALE: // fall through
    case CONVOLUTION_FILTER_BIAS: n = 4; break;
    }

    begin_render_request (4106, 12+4*n);
    render_request.write4 (target);
    render_request.write4 (pname);
    render_request.write4 (params, 0, n);
  }


  // glx render opcode 4107 - copy convolution filter1d
  /**
   * @see <a href="glCopyConvolutionFilter1d.html">
   * glCopyConvolutionFilter1d</a>
   */
  public void copy_convolution_filter1d (int target, int internal_format, 
    int x, int y, int width) {

    begin_render_request (4107, 24);
    render_request.write4 (target);
    render_request.write4 (internal_format);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
  }


  // glx render opcode 4108 - copy convolution filter2d
  /**
   * @see <a href="glCopyConvolutionFilter1d.html">
   * glCopyConvolutionFilter1d</a>
   */
  public void copy_convolution_filter2d (int target, int internal_format, 
    int x, int y, int width, int height) {

    begin_render_request (4108, 28);
    render_request.write4 (target);
    render_request.write4 (internal_format);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
  }


  // glx render opcode 4110 - histogram
  /**
   * @see <a href="glHistogram.html">
   * glHistogram</a>
   */
  public void histogram (int target, int width, int internal_format, 
    boolean sink) {

    begin_render_request (4110, 20);
    render_request.write4 (target);
    render_request.write4 (width);
    render_request.write4 (internal_format);
    render_request.write1 (sink);
    render_request.write3_unused ();
  }


  // glx render opcode 4111 - minmax
  /**
   * @see <a href="glMinmax.html">
   * glMinmax</a>
   */
  public void minmax (int target, int internal_format, boolean sink) {
    begin_render_request (4111, 16);
    render_request.write4 (target);
    render_request.write4 (internal_format);
    render_request.write1 (sink);
    render_request.write3_unused ();
  }


  // glx render opcode 4112 - reset histogram
  /**
   * @see <a href="glResetHistogram.html">glResetHistogram</a>
   */
  public void reset_histogram (int target) {
    begin_render_request (4112, 8);
    render_request.write4 (target);
  }


  // glx render opcode 4113 - reset minmax
  /**
   * @see <a href="glResetMinmax.html">glResetMinmax</a>
   */
  public void reset_minmax (int target) {
    begin_render_request (4113, 8);
    render_request.write4 (target);
  }


  // glx render opcode 4117 - bind texture
  /**
   * @see <a href="glBindTexture.html">glBindTexture</a>
   */
  public void bind_texture (int target, int texture) {
    begin_render_request (4117, 12);
    render_request.write4 (target);
    render_request.write4 (texture);
  }


  // glx render opcode 4118 - prioritize textures
  /**
   * @see <a href="glPrioritizeTextures.html">glPrioritizeTextures</a>
   */
  public void prioritize_textures (int [] textures, float [] priorities) {
    begin_render_request (4118, 8+textures.length*2*8);
    render_request.write4 (textures);
    render_request.write4 (priorities);
  }


  // glx render opcode 4119 - copy texture image 1d
  /**
   * @see <a href="glCopyTexImage1D.html">glCopyTexImage1D</a>
   */
  public void copy_texture_image_1d (int target, int level, 
    int internal_format, int x, int y, int width, int border) {

    begin_render_request (4119, 32);
    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (internal_format);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (border);
  } 


  // glx render opcode 4120 - copy texture image 2d
  /**
   * @see <a href="glCopyTexImage2D.html">glCopyTexImage2D</a>
   */
  public void copy_texture_image_2d (int target, int level, 
    int internal_format, int x, int y, int width, int height, int border) {

    begin_render_request (4120, 36);
    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (internal_format);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
    render_request.write4 (border);
  } 


  // glx render opcode 4121 - copy texture sub image 1d
  /**
   * @see <a href="glCopyTexSubImage1D.html">glCopyTexSubImage1D</a>
   */
  public void copy_texture_sub_image_1d (int target, int level, 
    int xoffset, int x, int y, int width) {

    begin_render_request (4121, 28);
    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (xoffset);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
  } 


  // glx render opcode 4122 - copy texture sub image 2d
  /**
   * @see <a href="glCopyTexSubImage2D.html">glCopyTexSubImage2D</a>
   */
  public void copy_texture_sub_image_2d (int target, int level, 
    int xoffset, int yoffset, int x, int y, int width, int height) {

    begin_render_request (4122, 36);
    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (xoffset);
    render_request.write4 (yoffset);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
  } 


  // glx render opcode 4123 - copy texture sub image3d
  /**
   * @see <a href="glCopyTexSubImage3D.html">glCopyTexSubImage3D</a>
   */
  public void copy_texture_sub_image3d (int target, int level, 
    int xoffset, int yoffset, int zoffset,
    int x, int y, int width, int height) {

    begin_render_request (4123, 40);
    render_request.write4 (target);
    render_request.write4 (level);
    render_request.write4 (xoffset);
    render_request.write4 (yoffset);
    render_request.write4 (zoffset);
    render_request.write4 (x);
    render_request.write4 (y);
    render_request.write4 (width);
    render_request.write4 (height);
  } 


  /**
   * Enable or disable server-side GL capability.
   *
   * @see #enable(int)
   * @see #disable(int)
   */
  public void capability (int capability, boolean enable) {
    if (enable) enable (capability);
    else disable (capability);
  }


  /**
   * @see #color3d(double, double, double)
   * @see <a href="GLColor3dv.html">GLColor3dv</a>
   */
  public void color3dv (double [] v) {
    color3d (v [0], v [1], v [2]);
  }


  /**
   * @see #color3f(float, float, float)
   * @see <a href="GLColor3fv.html">GLColor3fv</a>
   */
  public void color3fv (float [] v) {
    color3f (v [0], v [1], v [2]);
  }


  /**
   * @see #color3i(int, int, int)
   * @see <a href="GLColor3iv.html">GLColor3iv</a>
   */
  public void color3iv (int [] v) {
    color3i (v [0], v [1], v [2]);
  }


  /**
   * @see #color3s(int, int, int)
   * @see <a href="GLColor3sv.html">GLColor3sv</a>
   */
  public void color3sv (int [] v) {
    color3s (v [0], v [1], v [2]);
  }


  /**
   * @see #color3ub(byte, byte, byte)
   * @see <a href="GLColor3ubv.html">GLColor3ubv</a>
   */
  public void color3ubv (byte [] v) {
    color3ub (v [0], v [1], v [2]);
  }


  /**
   * @see #color3ui(int, int, int)
   * @see <a href="GLColor3uiv.html">GLColor3uiv</a>
   */
  public void color3uiv (int [] v) {
    color3ui (v [0], v [1], v [2]);
  }


  /**
   * @see #color3us(int, int, int)
   * @see <a href="GLColor3usv.html">GLColor3usv</a>
   */
  public void color3usv (int [] v) {
    color3s (v [0], v [1], v [2]);
  }


  /**
   * @see #color4d(double, double, double, double)
   * @see <a href="GLColor4d.html">GLColor4d</a>
   */
  public void color4dv (double [] v) {
    color4d (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #color4f(float, float, float, float)
   * @see <a href="GLColor4f.html">GLColor4f</a>
   */
  public void color4fv (float [] v) {
    color4f (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #color4i(int, int, int, int)
   * @see <a href="GLColor4i.html">GLColor4i</a>
   */
  public void color4iv (int [] v) {
    color4i (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #color4s(int, int, int, int)
   * @see <a href="GLColor4s.html">GLColor4s</a>
   */
  public void color4sv (int [] v) {
    color4s (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #color4ui(int, int, int, int)
   * @see <a href="GLColor4ui.html">GLColor4ui</a>
   */
  public void color4uiv (int [] v) {
    color4ui (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #color4us(int, int, int, int)
   * @see <a href="GLColor4us.html">GLColor4us</a>
   */
  public void color4usv (int [] v) {
    color4us (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #error()
   */ 
  public String error_string () {
    switch (error ()) {
    case NO_ERROR: return "no-error";
    case INVALID_ENUM: return "invalid-enum";
    case INVALID_VALUE: return "invalid-value";
    case INVALID_OPERATION: return "invalid-operation";
    case STACK_OVERFLOW: return "stack-overflow";
    case STACK_UNDERFLOW: return "stack-underflow";
    case OUT_OF_MEMORY: return "out-of-memory";
    default: return null;
    }
  }


  /**
   * @see #multi_tex_coord1d_arb(int, double)
   * @see <a href="GLMultiTexCoord1dvARB.html">
   * GLMultiTexCoord1dvARB</a>
   */
  public void multi_tex_coord1dv_arb (int target, double [] v) {
    multi_tex_coord1d_arb (target, v [0]);
  }


  /**
   * @see #multi_tex_coord1f_arb(int, float)
   * @see <a href="GLMultiTexCoord1fvARB.html">
   * GLMultiTexCoord1fvARB</a>
   */
  public void multi_tex_coord1fv_arb (int target, float [] v) {
    multi_tex_coord1f_arb (target, v [0]);
  }


  /**
   * @see #multi_tex_coord1i_arb(int, int)
   * @see <a href="GLMultiTexCoord1ivARB.html">
   * GLMultiTexCoord1ivARB</a>
   */
  public void multi_tex_coord1iv_arb (int target, int [] v) {
    multi_tex_coord1i_arb (target, v [0]);
  }


  /**
   * @see #multi_tex_coord1s_arb(int, int)
   * @see <a href="GLMultiTexCoord1svARB.html">
   * GLMultiTexCoord1svARB</a>
   */
  public void multi_tex_coord1sv_arb (int target, int [] v) {
    multi_tex_coord1s_arb (target, v [0]);
  }

  /**
   * @see #multi_tex_coord2d_arb(int, double, double)
   * @see <a href="GLMultiTexCoord2dvARB.html">
   * GLMultiTexCoord2dvARB</a>
   */
  public void multi_tex_coord2dv (int target, double [] v) {
    multi_tex_coord2d_arb (target, v [0], v [1]);
  }


  /**
   * @see #multi_tex_coord2f_arb(int, float, float)
   * @see <a href="GLMultiTexCoord2fvARB.html">
   * GLMultiTexCoord2fvARB</a>
   */
  public void multi_tex_coord2fv_arb (int target, float [] v) {
    multi_tex_coord2f_arb (target, v [0], v [1]);
  }


  /**
   * @see #multi_tex_coord2i_arb(int, int, int)
   * @see <a href="GLMultiTexCoord2ivARB.html">
   * GLMultiTexCoord2ivARB</a>
   */
  public void multi_tex_coord2iv_arb (int target, int [] v) {
    multi_tex_coord2i_arb (target, v [0], v [1]);
  }


  /**
   * @see #multi_tex_coord2s_arb(int, int, int)
   * @see <a href="GLMultiTexCoord2svARB.html">
   * GLMultiTexCoord2svARB</a>
   */
  public void multi_tex_coord2sv_arb (int target, int [] v) {
    multi_tex_coord2s_arb (target, v [0], v [1]);
  }


  /**
   * @see #multi_tex_coord3d_arb(int, double, double, double)
   * @see <a href="GLMultiTexCoord3dvARB.html">
   * GLMultiTexCoord3dvARB</a>
   */
  public void multi_tex_coord3dv_arb (int target, double [] v) {
    multi_tex_coord3d_arb (target, v [0], v [1], v [2]);
  }


  /**
   * @see #multi_tex_coord3f_arb(int, float, float, float)
   * @see <a href="GLMultiTexCoord3fvARB.html">
   * GLMultiTexCoord3fvARB</a>
   */
  public void multi_tex_coord3fv_arb (int target, float [] v) {
    multi_tex_coord3f_arb (target, v [0], v [1], v [2]);
  }


  /**
   * @see #multi_tex_coord3i_arb(int, int, int, int)
   * @see <a href="GLMultiTexCoord3ivARB.html">
   * GLMultiTexCoord3ivARB</a>
   */
  public void multi_tex_coord3iv_arb (int target, int [] v) {
    multi_tex_coord3i_arb (target, v [0], v [1], v [2]);
  }


  /**
   * @see #multi_tex_coord3s_arb(int, int, int, int)
   * @see <a href="GLMultiTexCoord3svARB.html">
   * GLMultiTexCoord3svARB</a>
   */
  public void multi_tex_coord3sv_arb (int target, int [] v) {
    multi_tex_coord3s_arb (target, v [0], v [1], v [2]);
  }


  /**
   * @see #multi_tex_coord4d_arb(int, double, double, double, double)
   * @see <a href="GLMultiTexCoord4dARB.html">
   * GLMultiTexCoord4dARB</a>
   */
  public void multi_tex_coord4dv_arb (int target, double [] v) {
    multi_tex_coord4d_arb (target, v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #multi_tex_coord4f_arb(int, float, float, float, float)
   * @see <a href="GLMultiTexCoord4fARB.html">
   * GLMultiTexCoord4fARB</a>
   */
  public void multi_tex_coord4fv_arb (int target, float [] v) {
    multi_tex_coord4f_arb (target, v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #multi_tex_coord4i_arb(int, int, int, int, int)
   * @see <a href="GLMultiTexCoord4iARB.html">
   * GLMultiTexCoord4iARB</a>
   */
  public void multi_tex_coord4iv_arb (int target, int [] v) {
    multi_tex_coord4i_arb (target, v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #multi_tex_coord4s_arb(int, int, int, int, int)
   * @see <a href="GLMultiTexCoord4sARB.html">
   * GLMultiTexCoord4sARB</a>
   */
  public void multi_tex_coord4sv_arb (int target, int [] v) {
    multi_tex_coord4s_arb (target, v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #normal3b(boolean, boolean, boolean)
   * @see <a href="GLNormal3bv.html">GLNormal3bv</a>
   */
  public void normal3bv (boolean [] v) {
    normal3b (v [0], v [1], v [2]);
  }


  /**
   * @see #normal3d(double, double, double)
   * @see <a href="GLNormal3dv.html">GLNormal3dv</a>
   */
  public void normal3dv (double [] v) {
    normal3d (v [0], v [1], v [2]);
  }


  /**
   * @see #normal3f(float, float, float)
   * @see <a href="GLNormal3fv.html">GLNormal3fv</a>
   */
  public void normal3fv (float [] v) {
    normal3f (v [0], v [1], v [2]);
  }


  /**
   * @see #normal3i(int, int, int)
   * @see <a href="GLNormal3iv.html">GLNormal3iv</a>
   */
  public void normal3iv (int [] v) {
    normal3i (v [0], v [1], v [2]);
  }


  /**
   * @see #normal3s(int, int, int)
   * @see <a href="GLNormal3sv.html">GLNormal3sv</a>
   */
  public void normal3sv (int [] v) {
    normal3s (v [0], v [1], v [2]);
  }


  /**
   * @see #raster_pos2d(double, double)
   * @see <a href="GLRasterPos2dv.html">GLRasterPos2dv</a>
   */
  public void raster_pos2dv (double [] v) {
    raster_pos2d (v [0], v [1]);
  }


  /**
   * @see #raster_pos2f(float, float)
   * @see <a href="GLRasterPos2fv.html">GLRasterPos2fv</a>
   */
  public void raster_pos2fv (float [] v) {
    raster_pos2f (v [0], v [1]);
  }


  /**
   * @see #raster_pos2i(int, int)
   * @see <a href="GLRasterPos2iv.html">GLRasterPos2iv</a>
   */
  public void raster_pos2iv (int [] v) {
    raster_pos2i (v [0], v [1]);
  }


  /**
   * @see #raster_pos2s(int, int)
   * @see <a href="GLRasterPos2sv.html">GLRasterPos2sv</a>
   */
  public void raster_pos2sv (int [] v) {
    raster_pos2s (v [0], v [1]);
  }


  /**
   * @see #raster_pos3d(double, double, double)
   * @see <a href="GLRasterPos3dv.html">GLRasterPos3dv</a>
   */
  public void raster_pos3dv (double [] v) {
    raster_pos3d (v [0], v [1], v [2]);
  }


  /**
   * @see #raster_pos3f(float, float, float)
   * @see <a href="GLRasterPos3fv.html">GLRasterPos3fv</a>
   */
  public void raster_pos3fv (float [] v) {
    raster_pos3f (v [0], v [1], v [2]);
  }


  /**
   * @see #raster_pos3i(int, int, int)
   * @see <a href="GLRasterPos3iv.html">GLRasterPos3iv</a>
   */
  public void raster_pos3iv (int [] v) {
    raster_pos3i (v [0], v [1], v [2]);
  }


  /**
   * @see #raster_pos3s(int, int, int)
   * @see <a href="GLRasterPos3sv.html">GLRasterPos3sv</a>
   */
  public void raster_pos3sv (int [] v) {
    raster_pos3s (v [0], v [1], v [2]);
  }


  /**
   * @see #raster_pos4d(double, double, double, double)
   * @see <a href="GLRasterPos4d.html">GLRasterPos4d</a>
   */
  public void raster_pos4dv (double [] v) {
    raster_pos4d (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #raster_pos4f(float, float, float, float)
   * @see <a href="GLRasterPos4f.html">GLRasterPos4f</a>
   */
  public void raster_pos4fv (float [] v) {
    raster_pos4f (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #raster_pos4i(int, int, int, int)
   * @see <a href="GLRasterPos4i.html">GLRasterPos4i</a>
   */
  public void raster_pos4iv (int [] v) {
    raster_pos4i (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #raster_pos4s(int, int, int, int)
   * @see <a href="GLRasterPos4s.html">GLRasterPos4s</a>
   */
  public void raster_pos4sv (int [] v) {
    raster_pos4s (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #rectd(double, double, double, double)
   * @see <a href="glRectdv.html">glRectdv</a>
   */
  public void rectdv (double [] v1, double [] v2) {
    rectd (v1 [0], v1 [1], v2 [0], v2 [1]);
  }


  /**
   * @see #rectf(float, float, float, float)
   * @see <a href="glRectfv.html">glRectfv</a>
   */
  public void rectfv (float [] v1, float [] v2) {
    rectf (v1 [0], v1 [1], v2 [0], v2 [1]);
  }


  /**
   * @see #recti(int, int, int, int)
   * @see <a href="glRectiv.html">glRectiv</a>
   */
  public void rectiv (int [] v1, int [] v2) {
    recti (v1 [0], v1 [1], v2 [0], v2 [1]);
  }


  /**
   * @see #rects(int, int, int, int)
   * @see <a href="glRectsv.html">glRectsv</a>
   */
  public void rectsv (int [] v1, int [] v2) {
    rects (v1 [0], v1 [1], v2 [0], v2 [1]);
  }


  /**
   * @see #vertex2d(double, double)
   * @see <a href="GLVertex2dv.html">GLVertex2dv</a>
   */
  public void vertex2dv (double [] v) {
    vertex2d (v [0], v [1]);
  }


  /**
   * @see #vertex2f(float, float)
   * @see <a href="GLVertex2fv.html">GLVertex2fv</a>
   */
  public void vertex2fv (float [] v) {
    vertex2f (v [0], v [1]);
  }


  /**
   * @see #vertex2i(int, int)
   * @see <a href="GLVertex2iv.html">GLVertex2iv</a>
   */
  public void vertex2iv (int [] v) {
    vertex2i (v [0], v [1]);
  }


  /**
   * @see #vertex2s(int, int)
   * @see <a href="GLVertex2sv.html">GLVertex2sv</a>
   */
  public void vertex2sv (int [] v) {
    vertex2s (v [0], v [1]);
  }


  /**
   * @see #vertex3d(double, double, double)
   * @see <a href="GLVertex3dv.html">GLVertex3dv</a>
   */
  public void vertex3dv (double [] v) {
    vertex3d (v [0], v [1], v [2]);
  }


  /**
   * @see #vertex3f(float, float, float)
   * @see <a href="GLVertex3fv.html">GLVertex3fv</a>
   */
  public void vertex3fv (float [] v) {
    vertex3f (v [0], v [1], v [2]);
  }


  /**
   * @see #vertex3i(int, int, int)
   * @see <a href="GLVertex3iv.html">GLVertex3iv</a>
   */
  public void vertex3iv (int [] v) {
    vertex3i (v [0], v [1], v [2]);
  }


  /**
   * @see #vertex3s(int, int, int)
   * @see <a href="GLVertex3sv.html">GLVertex3sv</a>
   */
  public void vertex3sv (int [] v) {
    vertex3s (v [0], v [1], v [2]);
  }


  /**
   * @see #vertex4d(double, double, double, double)
   * @see <a href="GLVertex4d.html">GLVertex4d</a>
   */
  public void vertex4dv (double [] v) {
    vertex4d (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #vertex4f(float, float, float, float)
   * @see <a href="GLVertex4f.html">GLVertex4f</a>
   */
  public void vertex4fv (float [] v) {
    vertex4f (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #vertex4i(int, int, int, int)
   * @see <a href="GLVertex4i.html">GLVertex4i</a>
   */
  public void vertex4iv (int [] v) {
    vertex4i (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #vertex4s(int, int, int, int)
   * @see <a href="GLVertex4s.html">GLVertex4s</a>
   */
  public void vertex4sv (int [] v) {
    vertex4s (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #tex_coord1d(double)
   * @see <a href="GLTexCoord1dv.html">GLTexCoord1dv</a>
   */
  public void tex_coord1dv (double [] v) {
    tex_coord1d (v [0]);
  }


  /**
   * @see #tex_coord1f(float)
   * @see <a href="GLTexCoord1fv.html">GLTexCoord1fv</a>
   */
  public void tex_coord1fv (float [] v) {
    tex_coord1f (v [0]);
  }


  /**
   * @see #tex_coord1i(int)
   * @see <a href="GLTexCoord1iv.html">GLTexCoord1iv</a>
   */
  public void tex_coord1iv (int [] v) {
    tex_coord1i (v [0]);
  }


  /**
   * @see #tex_coord1s(int)
   * @see <a href="GLTexCoord1sv.html">GLTexCoord1sv</a>
   */
  public void tex_coord1sv (int [] v) {
    tex_coord1s (v [0]);
  }

  /**
   * @see #tex_coord2d(double, double)
   * @see <a href="GLTexCoord2dv.html">GLTexCoord2dv</a>
   */
  public void tex_coord2dv (double [] v) {
    tex_coord2d (v [0], v [1]);
  }


  /**
   * @see #tex_coord2f(float, float)
   * @see <a href="GLTexCoord2fv.html">GLTexCoord2fv</a>
   */
  public void tex_coord2fv (float [] v) {
    tex_coord2f (v [0], v [1]);
  }


  /**
   * @see #tex_coord2i(int, int)
   * @see <a href="GLTexCoord2iv.html">GLTexCoord2iv</a>
   */
  public void tex_coord2iv (int [] v) {
    tex_coord2i (v [0], v [1]);
  }


  /**
   * @see #tex_coord2s(int, int)
   * @see <a href="GLTexCoord2sv.html">GLTexCoord2sv</a>
   */
  public void tex_coord2sv (int [] v) {
    tex_coord2s (v [0], v [1]);
  }


  /**
   * @see #tex_coord3d(double, double, double)
   * @see <a href="GLTexCoord3dv.html">GLTexCoord3dv</a>
   */
  public void tex_coord3dv (double [] v) {
    tex_coord3d (v [0], v [1], v [2]);
  }


  /**
   * @see #tex_coord3f(float, float, float)
   * @see <a href="GLTexCoord3fv.html">GLTexCoord3fv</a>
   */
  public void tex_coord3fv (float [] v) {
    tex_coord3f (v [0], v [1], v [2]);
  }


  /**
   * @see #tex_coord3i(int, int, int)
   * @see <a href="GLTexCoord3iv.html">GLTexCoord3iv</a>
   */
  public void tex_coord3iv (int [] v) {
    tex_coord3i (v [0], v [1], v [2]);
  }


  /**
   * @see #tex_coord3s(int, int, int)
   * @see <a href="GLTexCoord3sv.html">GLTexCoord3sv</a>
   */
  public void tex_coord3sv (int [] v) {
    tex_coord3s (v [0], v [1], v [2]);
  }


  /**
   * @see #tex_coord4d(double, double, double, double)
   * @see <a href="GLTexCoord4d.html">GLTexCoord4d</a>
   */
  public void tex_coord4dv (double [] v) {
    tex_coord4d (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #tex_coord4f(float, float, float, float)
   * @see <a href="GLTexCoord4f.html">GLTexCoord4f</a>
   */
  public void tex_coord4fv (float [] v) {
    tex_coord4f (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #tex_coord4i(int, int, int, int)
   * @see <a href="GLTexCoord4i.html">GLTexCoord4i</a>
   */
  public void tex_coord4iv (int [] v) {
    tex_coord4i (v [0], v [1], v [2], v [3]);
  }


  /**
   * @see #tex_coord4s(int, int, int, int)
   * @see <a href="GLTexCoord4s.html">GLTexCoord4s</a>
   */
  public void tex_coord4sv (int [] v) {
    tex_coord4s (v [0], v [1], v [2], v [3]);
  }


  private Request begin_command_request (int opcode, int unit) {
    flush_render_request ();
    return new Request (display, glx.major_opcode, opcode, unit);
  }


  private Request begin_single_request (int opcode, int unit) {
    Request request = begin_command_request (opcode, unit);
    request.write4 (tag);
    return request;
  }


  private void flush_render_request () {
    if (render_request == null) return;

    render_request.length = Data.len (render_request.index);
    render_request.write2 (2, render_request.length/4);
    display.send_request (render_request);
    render_request = null;
  }


  private Enum read_enum (Request request) {
    Data reply = display.read_reply (request);
    int n = reply.read4 (12);
    
    if (n == 0) return null;
    if (n == 1) return new Enum (reply, 16, 1);
    return new Enum (reply, 32, n);
  }


  public boolean support (int major, int minor) {
    String version_all = string (VERSION);
    int to = version_all.indexOf (' ');
    if (to == -1) to = version_all.length ();
    String version_number = version_all.substring (0, to);
    
    String [] versions = gnu.util.Misc.tokenize (version_number, ".");
    if (versions.length < 2) return false; // invalid format

    int major0 = Integer.parseInt (versions [0]);
    int minor0 = Integer.parseInt (versions [1]);

    return major0 == major && minor0 >= minor;
  }


  public String toString () {
    return "#GL"
      + "\n  vendor: " + string (VENDOR)
      + "\n  renderer: " + string (RENDERER)
      + "\n  version: " + string (VERSION)
      + "\n  extensions: " + string (EXTENSIONS);
  }
}
