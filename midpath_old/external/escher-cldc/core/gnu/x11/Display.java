package gnu.x11;

import gnu.x11.event.Event;
import gnu.x11.extension.BigRequests;
import gnu.x11.extension.ErrorFactory;
import gnu.x11.extension.EventFactory;
import gnu.x11.extension.NotFoundException;
import gnu.x11.extension.XCMisc;

import java.util.Hashtable;

/** X server connection. */
public class Display {
	public static final int CURRENT_TIME = 0;

	public Input input;
	public Connection connection;
	public boolean connected;

	// server information
	public int release_no;
	public String vendor;
	public int maximum_request_length;
	public Screen[] screens;
	public Pixmap.Format[] pixmap_formats;
	public int image_byte_order;
	public int bitmap_format_bit_order;
	public int bitmap_format_scanline_unit;
	public int bitmap_format_scanline_pad;
	public int resource_base;
	public int resource_mask;

	// defaults
	public Color default_black, default_white;
	public Colormap default_colormap;
	public int default_depth;
	public Pixmap.Format default_pixmap_format;
	public Window default_root;
	public Screen default_screen;
	public int default_screen_no;

	/** 
	 * @see Screen#default_gc()
	 */
	public GC default_gc;

	// resources
	public Hashtable resources = new Hashtable(257);
	public int resource_index;
	public Hashtable atom_ids = new Hashtable(257);
	public Hashtable atom_names = new Hashtable(257);

	// xcmisc
	public XCMisc xcmisc;
	public boolean use_xcmisc;
	public int xcmisc_resource_base;
	public int xcmisc_resource_count;

	// extension

	public boolean big_requests_present;
	public int extended_maximum_request_length;

	/**
	 * Major opcodes 128 through 255 are reserved for extensions,
	 * totally 128. 
	 */
	public String[] extension_opcode_strings = new String[128];
	public String[][] extension_minor_opcode_strings = new String[128][];

	/** 
	 * Event codes 64 through 127 are reserved for extensiones, 
	 * totally 64. 
	 */
	public EventFactory[] extension_event_factories = new EventFactory[64];

	/** 
	 * Error codes 128 through 255 are reserved for extensiones,
	 * totally 128. 
	 */
	public ErrorFactory[] extension_error_factories = new ErrorFactory[128];

	/**
	 * #Display(String, int, int)
	 */
	public Display() {
		this("", 0, 0);
	}

	/** X display name. */
	public static class Name {
		public String hostname = "";
		public int display_no, screen_no;

		public Name(String display_name) {
			if (display_name == null)
				return;
			int i = display_name.indexOf(':');

			// case 1: display_name = hostname
			if (i == -1) {
				hostname = display_name;
				return;
			}

			hostname = display_name.substring(0, i);
			int j = display_name.indexOf('.', i);

			if (j == -1) {
				// case 2: display_name = hostname:display_no
				display_no = Integer.parseInt(display_name.substring(i + 1, display_name.length()));
				return;
			}

			// case 3: display_name = hostname:display_no.screen_no
			display_no = Integer.parseInt(display_name.substring(i + 1, j));
			screen_no = Integer.parseInt(display_name.substring(j + 1, display_name.length()));
		}

		public Name(String hostname, int display_no, int screen_no) {
			this.hostname = hostname;
			this.display_no = display_no;
			this.screen_no = screen_no;
		}

		public String toString() {
			return hostname + ":" + display_no + "." + screen_no;
		}
	}

	/**
	 * #Display(String, int, int)
	 */
	public Display(Name name) {
		this(name.hostname, name.display_no, name.screen_no);
	}

	/**
	 * #Display(String, int, int)
	 */
	public Display(String hostname, int display_no) {
		this(hostname, display_no, 0);
	}

	/**
	 * Sets up a display using a connection over the specified
	 * <code>socket</code>. This should be used when there is a need to use
	 * non-TCP sockets, like connecting to an X server via Unix domain sockets.
	 * You need to provide an implementation for this kind of socket though.
	 *
	 * @param socket the socket to use for that connection
	 * @param hostname the hostname to connect to
	 * @param display_no the display number
	 * @param screen_no the screen number
	 */
//	public Display(Socket socket, String hostname, int display_no, int screen_no) {
//		default_screen_no = screen_no;
//		connection = new Connection(this, socket, hostname, display_no);
//		init();
//	}

	/**
	 * @see <a href="XOpenDisplay.html">XOpenDisplay</a>
	 */
	public Display(String hostname, int display_no, int screen_no) {
		default_screen_no = screen_no;
		connection = new Connection(this, hostname, display_no);
		init();
	}

	private void init() {

		String auth_name = "";
		String auth_data = "";

		Request request = new Request(this, 'B', // java = MSB
				3 + Data.unit(auth_name) + Data.unit(auth_data));
		request.index = 2; // connection setup request hack
		request.write2(11); // major version
		request.write2(0); // minor version
		request.write2(auth_name.length());
		request.write2(auth_data.length());
		request.write1(auth_name);
		request.write1(auth_data);

		init_server_info(read_reply(request));
		init_defaults();
		init_big_request_extension();

		// authorization protocol
		//		    XAuthority xauth = get_authority ();
		//		    //System.err.println("xauth: " + xauth);
		//		    byte[] auth_name = xauth.protocol_name;
		//		    byte[] auth_data = xauth.protocol_data;
		//   
		//    Request request = new Request (this, 'B', // java = MSB
		//      3 + Data.unit (auth_name) + Data.unit (auth_data));
		//    request.index = 2;// connection setup request hack
		//    request.write2 (11);// major version
		//    request.write2 (0);// minor version
		//    request.write2 (auth_name.length);
		//    request.write2 (auth_data.length);
		//    request.write2 (0); // 2 bytes must be skipped.
		//    request.write1 (auth_name);
		//    request.pad (auth_name.length);
		//    request.write1 (auth_data);
		//    request.pad( auth_data.length);
		//
		//    init_server_info (read_reply (request));
		//    init_defaults ();
		//    init_big_request_extension ();
	}

	// opcode 23 - get selection owner
	/**
	 * @see <a href="XGetSelectionOwner.html">XGetSelectionOwner</a>
	 */
	public Window selection_owner(Atom selection) {
		Request request = new Request(this, 23, 2);
		request.write4(selection.id);

		Data reply = read_reply(request);
		return (Window) Window.intern(this, reply.read4(8));
	}

	// opcode 36 - grab server
	public synchronized void grab_server() {
		Request request = new Request(this, 36, 1);
		send_request(request);
	}

	// opcode 37 - ungrab server
	public void ungrab_server() {
		Request request = new Request(this, 37, 1);
		send_request(request);
	}

	// opcode 49 - list fonts
	/**
	 * @return valid:
	 * {@link Enum#next()} of type {@link Font},
	 * {@link Enum#next_string()}
	 * 
	 * @see <a href="XListFonts.html">XListFonts</a>
	 */
	public Enum fonts(String pattern, int max_name_count) {
		Request request = new Request(this, 49, 2 + Data.unit(pattern));
		request.write2(max_name_count);
		request.write2(pattern.length());
		request.write1(pattern);

		Data reply = read_reply(request);
		return new Enum(reply, 32, reply.read2(8)) {
			public Object next() {
				return new Font(Display.this, next_string());
			}
		};
	}

	// opcode 50 - list fonts with info
	/**
	 * @see <a href="XListFontsWithInfo.html">XListFontsWithInfo</a>
	 */
	public Data fonts_with_info(String pattern, int max_name_count) {

		Request request = new Request(this, 50, 2 + Data.unit(pattern));
		request.write2(max_name_count);
		request.write2(pattern.length());
		request.write1(pattern);

		// TODO deal with multiple replies
		return read_reply(request);
	}

	// opcode 51 - set font path
	/**
	 * @see <a href="XSetFontPath.html">XSetFontPath</a>
	 */
	public void set_font_path(int count, String path) {
		Request request = new Request(this, 51, 2 + Data.unit(path));
		request.write2(count);
		request.write2_unused();
		request.write1(path);
		send_request(request);
	}

	// opcode 52 - get font path
	/**
	 * @return valid: {@link Enum#next_string()}
	 * @see <a href="XGetFontPath.html">XGetFontPath</a>
	 */
	public Enum font_path() {
		Request request = new Request(this, 52, 1);

		Data reply = read_reply(request);
		return new Enum(reply, 32, reply.read2(8));
	}

	/** Reply of {@link #extension(String)}. */
	public static class ExtensionReply extends Data {
		public ExtensionReply(Data data) {
			super(data);
		}

		public boolean present() {
			return read_boolean(8);
		}

		public int major_opcode() {
			return read1(9);
		}

		public int first_event() {
			return read1(10);
		}

		public int first_error() {
			return read1(11);
		}
	}

	// opcode 98 - query extension
	/**
	 * @see <a href="XQueryExtension.html">XQueryExtension</a>
	 */
	public ExtensionReply extension(String name) {
		Request request = new Request(this, 98, 2 + Data.unit(name));
		request.write2(name.length());
		request.write2_unused();
		request.write1(name);
		return new ExtensionReply(read_reply(request));
	}

	// opcode 99 - list extensions
	/**
	 * @return valid: {@link Enum#next_string()}
	 * @see <a href="XListExtensions.html">XListExtensions</a>
	 */
	public Enum extensions() {
		Request request = new Request(this, 99, 1);

		Data reply = read_reply(request);
		return new Enum(reply, 32, reply.read1(1));
	}

	// opcode 104 - bell
	/**
	 * @see <a href="XBell.html">XBell</a>
	 */
	public void bell(int percent) {
		Request request = new Request(this, 104, percent, 1);
		send_request(request);
	}

	public static final int NO = 0;
	public static final int YES = 1;
	public static final int DEFAULT = 2;

	public static final String[] SCREEN_SAVER_STRINGS = { "no", "yes", "default" };

	// opcode 107 - set screen saver
	/**
	 * @param prefer_blanking valid:
	 * {@link #NO},
	 * {@link #YES},
	 * {@link #DEFAULT}
	 * 
	 * @param allow_exposures valid: 
	 * {@link #NO},
	 * {@link #YES},
	 * {@link #DEFAULT}
	 * 
	 * @see <a href="XSetScreenSaver.html">XSetScreenSaver</a>
	 */
	public void set_screen_saver(int timeout, int interval, int prefer_blanking, int allow_exposures) {
		Request request = new Request(this, 107, 3);
		request.write2(timeout);
		request.write2(interval);
		request.write1(prefer_blanking);
		request.write1(allow_exposures);
		send_request(request);
	}

	/** Reply of {@link #screen_saver()}. */
	public static class ScreenSaverReply extends Data {
		public ScreenSaverReply(Data data) {
			super(data);
		}

		public int timeout() {
			return read2(8);
		}

		public int interval() {
			return read2(10);
		}

		/**
		 * @return valid: 
		 * {@link Display#NO},
		 * {@link Display#YES}
		 */
		public int prefer_blanking() {
			return read1(12);
		}

		/**
		 * @return valid:
		 * {@link Display#NO},
		 * {@link Display#YES}
		 */
		public int allow_exposures() {
			return read1(13);
		}

		public String toString() {
			return "#ScreenSaverReply" + "\n  timeout: " + timeout() + "\n  interval: " + interval()
					+ "\n  prefer-blanking: " + Display.SCREEN_SAVER_STRINGS[prefer_blanking()]
					+ "\n  allow-exposures: " + Display.SCREEN_SAVER_STRINGS[allow_exposures()];
		}
	}

	// opcode 108 - get screen saver
	/**
	 * @see <a href="XGetScreenSaver.html">XGetScreenSaver</a>
	 */
	public ScreenSaverReply screen_saver() {
		Request request = new Request(this, 108, 1);
		return new ScreenSaverReply(read_reply(request));
	}

	public static final int INSERT = 0;
	public static final int DELETE = 1;

	// opcode 109 - change hosts
	/**
	 * @param mode valid:
	 * {@link #INSERT},
	 * {@link #DELETE}
	 * 
	 * @see <a href="XAddHost.html">XAddHost</a>
	 * @see <a href="XRemoveHost.html">XRemoveHost</a>
	 */
	public void change_hosts(int mode, int family, byte[] host) {
		Request request = new Request(this, 109, mode, 2 + Data.unit(host));

		request.write1(family);
		request.write1_unused();
		request.write2(host.length);
		request.write1(host);
		send_request(request);
	}

	/** Reply of {@link #hosts()}. */
	public static class HostsReply extends Data {
		public HostsReply(Data data) {
			super(data);
		}

		public boolean mode() {
			return read_boolean(1);
		}
	}

	// opcode 110 - list hosts
	/**
	 * @see <a href="XListHosts.html">XListHosts</a>
	 */
	public HostsReply hosts() {
		Request request = new Request(this, 110, 1);
		return new HostsReply(read_reply(request));
	}

	public static final int ENABLE = 0;
	public static final int DISABLE = 1;

	// opcode 111 - set access control
	/**
	 * @param mode valid:
	 * {@link #ENABLE},
	 * {@link #DISABLE}
	 * 
	 * @see <a href="XSetAccessControl.html">XSetAccessControl</a>
	 */
	public void set_access_control(int mode) {
		Request request = new Request(this, 111, mode, 1);
		send_request(request);
	}

	// opcode 113 - kill client
	/**
	 * @see <a href="XKillClient.html">XKillClient</a>
	 */
	public void kill_client(Resource resource) {
		Request request = new Request(this, 113, 2);
		request.write4(resource.id);
		send_request(request);
	}

	public static final int DESTROY = 0;
	public static final int RETAIN_PERMANENT = 1;
	public static final int RETAIN_TEMPORARY = 2;

	// opcode 112 - set close down mode
	/**
	 * @param mode valid:
	 * {@link #DESTROY},
	 * {@link #RETAIN_PERMANENT},
	 * {@link #RETAIN_TEMPORARY}
	 * 
	 * @see <a href="XSetCloseDownMode.html">XSetCloseDownMode</a>
	 */
	public void set_close_down_mode(int mode) {
		Request request = new Request(this, 112, mode, 1);
		send_request(request);
	}

	public static final int ACTIVATE = 0;
	public static final int RESET = 1;

	// opcode 115 - force screen saver
	/**
	 * @param mode valid:
	 * {@link #ACTIVATE},
	 * {@link #RESET}
	 * 
	 * @see <a href="XForceScreenSaver.html">XForceScreenSaver</a>
	 */
	public void force_screen_saver(int mode) {
		Request request = new Request(this, 115, mode, 1);
		send_request(request);
	}

	public int allocate_id(Object object) {
		/* From XC-MISC extension specification:
		 * 
		 * When an X client connects to an X server, it receives a fixed range
		 * of resource IDs to use to identify the client's resources inside the
		 * X server. Xlib hands these out sequentially as needed. When it
		 * overruns the end of the range, an IDChoice protocol error results. 
		 * Long running clients, or clients that use resource IDs at a rapid
		 * rate, may encounter this circumstance. When it happens, there are
		 * usually many resource IDs available, but Xlib doesn't know about
		 * them.
		 *
		 * One approach to solving this problem would be to have Xlib notice
		 * when a resource is freed and recycle its ID for future use. This
		 * strategy runs into difficulties because sometimes freeing one
		 * resource causes others to be freed (for example, when a window is
		 * destroyed, so are its children). To do a complete job, Xlib would
		 * have to maintain a large amount of state that currently resides only
		 * in the server (the entire window tree in the above example). Even if
		 * a less comprehensive strategy was adopted, such as recycling only
		 * those IDs that Xlib can identify without maintaining additional
		 * state, the additional bookkeeping at resource creation and
		 * destruction time would likely introduce unacceptable overhead.
		 *
		 * To avoid the problems listed above, the server's complete knowledge
		 * of all resource IDs in use by a client is leveraged. This extension
		 * provides two ways for Xlib to query the server for available
		 * resource IDs. Xlib can use these extension requests behind the
		 * scenes when it has exhausted its current pool of resource IDs.
		 */

		/* If XC-MISC is present, we use it. Otherwise, we fall back to
		 * allocate X resource ID sequentially to the end without recycling ID
		 * (just as xlib does).
		 *
		 * Sample values:
		 *   resource base: 0x04000000 or 00000100000000000000000000000000b
		 *   resource mask: 0x003FFFFF or 00000000001111111111111111111111b
		 */

		if (!use_xcmisc)
			// check if basic allocation fails
			use_xcmisc = (resource_index + 1 & ~resource_mask) != 0;

		if (!use_xcmisc) {
			int id = resource_index++ | resource_base;
			resources.put(new Integer(id), object);
			return id;
		}

		if (xcmisc == null)
			try {
				xcmisc = new XCMisc(this);
			} catch (NotFoundException e) {
				throw new RuntimeException("Failed to allocate new resource id");
			}

		if (xcmisc_resource_count == 0) {
			// first time, or used up
			gnu.x11.extension.XCMisc.XIDRangeReply rr = xcmisc.xid_range();
			xcmisc_resource_base = rr.start_id();
			xcmisc_resource_count = rr.count();
		}

		// give out in descending order
		xcmisc_resource_count--;
		return xcmisc_resource_base + xcmisc_resource_count;
	}

	/**
	 * @see <a href="XCloseDisplay.html">XCloseDisplay</a>
	 */
	public void close() {
		connection.close();
	}

	/**
	 * Force a round-trip request to flush errors in server. The name
	 * <code>check_error</code> is used instead of <code>sync</code> to
	 * distinguish the function of synchronous send mode and that of force
	 * round-trip send mode.
	 * 
	 * @see <a href="XSync.html">XSync</a>
	 */
	public void check_error() {
		connection.check_error();
	}

	/**
	 * @see <a href="XFlush.html">XFlush</a>
	 */
	public void flush() {
		connection.flush();
	}

	public void init_big_request_extension() {
		/* From Big Requests extension specification:
		 *
		 * It is desirable for core Xlib, and other extensions, to use this
		 * extension internally when necessary. It is also desirable to make
		 * the use of this extension as transparent as possible to the X
		 * client. For example, if enabling of the extension were delayed until
		 * the first time it was needed, an application that used XNextRequest
		 * to determine the sequence number of a request would no longer get
		 * the correct sequence number. As such, XOpenDisplay will determine if
		 * the extension is supported by the server and, if it is, enable
		 * extended-length encodings.
		 *
		 * The core Xlib functions XDrawLines, XDrawArcs, XFillPolygon,
		 * XChangeProperty, XSetClipRectangles, and XSetRegion are required to
		 * use extended-length encodings when necessary, if supported by the
		 * server. Use of extended-length encodings in other core Xlib
		 * functions (XDrawPoints, XDrawRectangles, XDrawSegments, XFillArcs,
		 * XFillRectangles, XPutImage) is permitted but not required; an Xlib
		 * implementation may choose to split the data across multiple smaller
		 * requests instead.
		 */
		try {
			BigRequests big = new BigRequests(this);
			big_requests_present = true;
			extended_maximum_request_length = big.enable();
		} catch (NotFoundException e) {
			big_requests_present = false;
		}
	}

	public void init_defaults() {
		default_screen = screens[default_screen_no];
		default_root = default_screen.root(); // before init default_gc
		default_depth = default_screen.root_depth();
		default_colormap = default_screen.default_colormap();
		default_gc = default_screen.default_gc();
		default_black = new Color(default_screen.black_pixel());
		default_white = new Color(default_screen.white_pixel());

		for (int i = pixmap_formats.length - 1; i >= 0; i--)
			if (pixmap_formats[i].depth() == default_depth) {
				default_pixmap_format = pixmap_formats[i];
				break;
			}
	}

	public void init_server_info(Data reply) {
		connected = true;
		release_no = reply.read4(8);
		resource_base = reply.read4(12);
		resource_mask = reply.read4(16);
		int vendor_length = reply.read2(24);
		extended_maximum_request_length = maximum_request_length = reply.read2(26);
		int screen_count = reply.read1(28);
		int pixmap_format_count = reply.read1(29);

		image_byte_order = reply.read1(30);
		bitmap_format_bit_order = reply.read1(31);
		bitmap_format_scanline_unit = reply.read1(32);
		bitmap_format_scanline_pad = reply.read1(33);

		int min_keycode = reply.read1(34);
		int max_keycode = reply.read1(35);
		input = new Input(this, min_keycode, max_keycode);
		input.keyboard_mapping();

		vendor = reply.read_string(40, vendor_length);

		// pixmap formats
		pixmap_formats = new Pixmap.Format[pixmap_format_count];
		int pixmap_formats_offset = 40 + Data.len(vendor_length);
		for (int i = 0; i < pixmap_format_count; i++)
			pixmap_formats[i] = new Pixmap.Format(reply, pixmap_formats_offset + i * 8);

		// screens

		if (default_screen_no < 0 || default_screen_no >= screen_count)
			throw new RuntimeException("Invalid screen number (screen-count " + screen_count + "): "
					+ default_screen_no);

		screens = new Screen[screen_count];
		int screen_offset = pixmap_formats_offset + 8 * pixmap_format_count;
		for (int i = 0; i < screen_count; i++) {
			screens[i] = new Screen(this, reply, screen_offset);
			screen_offset += screens[i].length();
		}
	}

	public Event next_event() {
		return connection.read_event(true, true);
	}

	public Data read_reply(Request request) {
		return connection.read_reply(request);
	}

	public int send_request(Request request) {
		return connection.send(request, false);
	}

	public String toString() {
		return "#Display" + "\n  default-screen-number: " + default_screen_no + "\n  vendor: " + vendor
				+ "\n  release-number: " + release_no + "\n  maximum-request-length: " + maximum_request_length + "\n"
				+ connection;
	}

	/**
	 * Fetches the XAuthority that matches this display.
	 *
	 * @return the XAuthority that matches this display
	 */
//	private XAuthority get_authority() {
//
//		XAuthority[] auths = XAuthority.get_authorities();
//
//		// Fetch hostname.
//		String hostname = connection.hostname;
//		if (hostname == null || hostname.equals("") || hostname.equals("localhost")) {
//			// Translate localhost hostnames to the real hostname of this host.
//			try {
//				InetAddress local = InetAddress.getLocalHost();
//				hostname = local.getHostName();
//			} catch (UnknownHostException ex) {
//			}
//		}
//
//		// Fetch display no.
//		String display_no = String.valueOf(connection.display_no);
//
//		// Find the XAuthority that matches the hostname and display no.
//		XAuthority found = null;
//		for (int i = 0; i < auths.length; i++) {
//			XAuthority auth = auths[i];
//			// FIXME: Maybe add handling of IP addresses here.
//			if (auth.hostname != null && auth.hostname.equals(hostname) && auth.display.equals(display_no)) {
//				found = auth;
//				break;
//			}
//		}
//		return found;
//	}
}
