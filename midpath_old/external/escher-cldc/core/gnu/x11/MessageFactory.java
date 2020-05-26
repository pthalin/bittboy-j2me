package gnu.x11;

import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.CirculateNotify;
import gnu.x11.event.CirculateRequest;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.ColormapNotify;
import gnu.x11.event.ConfigureNotify;
import gnu.x11.event.ConfigureRequest;
import gnu.x11.event.CreateNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.EnterNotify;
import gnu.x11.event.Event;
import gnu.x11.event.Expose;
import gnu.x11.event.FocusIn;
import gnu.x11.event.FocusOut;
import gnu.x11.event.GraphicsExpose;
import gnu.x11.event.GravityNotify;
import gnu.x11.event.KeyPress;
import gnu.x11.event.KeyRelease;
import gnu.x11.event.KeymapNotify;
import gnu.x11.event.LeaveNotify;
import gnu.x11.event.MapNotify;
import gnu.x11.event.MapRequest;
import gnu.x11.event.MappingNotify;
import gnu.x11.event.MotionNotify;
import gnu.x11.event.NoExposure;
import gnu.x11.event.PropertyNotify;
import gnu.x11.event.ReparentNotify;
import gnu.x11.event.ResizeRequest;
import gnu.x11.event.SelectionClear;
import gnu.x11.event.SelectionNotify;
import gnu.x11.event.SelectionRequest;
import gnu.x11.event.UnmapNotify;
import gnu.x11.event.VisibilityNotify;

import java.io.IOException;

/**
 * Build X Error, X Event, and X Reply from input stream.
 *
 * Although both are incoming packets from X server to us, Reply and Event
 * (and Error) are quite different in nature and thus treated very
 * differently in this libraray.
 *
 * Event packets are of fixed length and can be easily constructed. 
 * However, events are exposed to the users of this library. Therefore, the
 * interface to events of different types should be as accessible as
 * possible. This leads us to dynamically construct events of specific and
 * correct event types so that users can simply cast the return it and use
 * it. Moreover, almost all events contain id to other objects such as
 * Window and Atom, it makes sense to keep an {@link Display} reference in
 * event so that those objects can be intern easily later.
 *
 * Another complication for event are extension events. Extension events
 * are registered during querying and setting up extension. The information
 * will be stored in the array {@link Display#extension_event_factories}. 
 * When an extension event arrive, a particular factory in the array will
 * be called to construct and return an extension event object.
 *
 * Reply, on the other hand, are of various length and the reply length
 * field must be inspected to construct packets correctly. However, reply
 * packets are used soely inside this library. Therefore, only a minimum
 * interface is supported as developers of this libraray should be familiar
 * with details of the protocol and reply packets. If the packets are to be
 * exposed to users of this library, clean interfaces such as {@link
 * Display.ExtensionReply} and {@link Fontable.FontReply} should be provided.
 */
public class MessageFactory {
	public static Error build_connection_error(Display display) throws IOException {

		java.io.DataInputStream din = display.connection.din;
		int msg_len = din.readUnsignedByte();
		int major_version = din.readUnsignedShort();
		int minor_version = din.readUnsignedShort();
		int total_len = 4 * din.readUnsignedShort();
		byte[] buffer = new byte[total_len];
		din.readFully(buffer);
		String reason = new String(buffer, 0, msg_len);

		return new Error("X server connection error" + "\n  protocol-major-version: " + major_version
				+ "\n  protocol-minor-version " + minor_version + "\n  reason: " + reason);
	}

	public static Error build_error(Display display) throws IOException {
		if (!display.connected)
			return build_connection_error(display);

		// treat it as X Event 0
		Event data = build_event(display, 0);
		int code = data.read1(1);
		int seq_no = data.read2(2);
		int bad = data.read4(4);
		int minor_opcode = data.read2(8);
		int major_opcode = data.read1(10);

		if (code >= 128 && code <= 255)
			return build_extension_error(display, data, code, seq_no, bad, minor_opcode, major_opcode);

		return new Error(display, Error.ERROR_STRINGS[code], code, seq_no, bad, minor_opcode, major_opcode);
	}

	public static Event build_event(Display display, int code) throws IOException {

		// 32 = fixed length of event
		byte[] data = new byte[32];

		// keep code in [0]
		data[0] = (byte) code;
		display.connection.din.readFully(data, 1, 31);

		code &= 0x7f; // remove synthetic mask

		if (code >= 64 && code <= 127)
			return build_extension_event(display, data, code);
		else
			return build_core_event(display, data, code);
	}

	public static Event build_core_event(Display display, byte[] data, int code) {

		switch (code) {
		case 0: // 0 = Error
			return new Event(display, data, 0);

		case KeyPress.CODE: // 2
			return new KeyPress(display, data);

		case KeyRelease.CODE: // 3
			return new KeyRelease(display, data);

		case ButtonPress.CODE: // 4
			return new ButtonPress(display, data);

		case ButtonRelease.CODE: // 5
			return new ButtonRelease(display, data);

		case MotionNotify.CODE: // 6
			return new MotionNotify(display, data);

		case EnterNotify.CODE: // 7
			return new EnterNotify(display, data);

		case LeaveNotify.CODE: // 8
			return new LeaveNotify(display, data);

		case FocusIn.CODE: // 9
			return new FocusIn(display, data);

		case FocusOut.CODE: // 10
			return new FocusOut(display, data);

		case KeymapNotify.CODE: // 11
			return new KeymapNotify(display, data);

		case Expose.CODE: // 12
			return new Expose(display, data);

		case GraphicsExpose.CODE: // 13
			return new GraphicsExpose(display, data);

		case NoExposure.CODE: // 14
			return new NoExposure(display, data);

		case VisibilityNotify.CODE: // 15
			return new VisibilityNotify(display, data);

		case CreateNotify.CODE: // 16
			return new CreateNotify(display, data);

		case DestroyNotify.CODE: // 17
			return new DestroyNotify(display, data);

		case UnmapNotify.CODE: // 18
			return new UnmapNotify(display, data);

		case MapNotify.CODE: // 19
			return new MapNotify(display, data);

		case MapRequest.CODE: // 20
			return new MapRequest(display, data);

		case ReparentNotify.CODE: // 21
			return new ReparentNotify(display, data);

		case ConfigureNotify.CODE: // 22
			return new ConfigureNotify(display, data);

		case ConfigureRequest.CODE: // 23
			return new ConfigureRequest(display, data);

		case GravityNotify.CODE: // 24
			return new GravityNotify(display, data);

		case ResizeRequest.CODE: // 25
			return new ResizeRequest(display, data);

		case CirculateNotify.CODE: // 26
			return new CirculateNotify(display, data);

		case CirculateRequest.CODE: // 27
			return new CirculateRequest(display, data);

		case PropertyNotify.CODE: // 28
			return new PropertyNotify(display, data);

		case SelectionClear.CODE: // 29
			return new SelectionClear(display, data);

		case SelectionRequest.CODE: // 30
			return new SelectionRequest(display, data);

		case SelectionNotify.CODE: // 31
			return new SelectionNotify(display, data);

		case ColormapNotify.CODE: // 32
			return new ColormapNotify(display, data);

		case ClientMessage.CODE: // 33
			return new ClientMessage(display, data);

		case MappingNotify.CODE: // 34
			return new MappingNotify(display, data);

		default:
			throw new java.lang.Error("Unsupported core event: " + code);
		}
	}

	public static Error build_extension_error(Display display, Data data, int code, int seq_no, int bad,
			int minor_opcode, int major_opcode) {

		gnu.x11.extension.ErrorFactory factory = display.extension_error_factories[code - 128];

		if (factory == null)
			throw new java.lang.Error("Unsupported extension error: " + code);

		return factory.build(display, data, code, seq_no, bad, minor_opcode, major_opcode);
	}

	public static Event build_extension_event(Display display, byte[] data, int code) {

		gnu.x11.extension.EventFactory factory = display.extension_event_factories[code - 64];

		if (factory == null)
			throw new java.lang.Error("Unsupported extension event: " + code);

		return factory.build(display, data, code);
	}

	public static Data build(Display display) throws IOException {

		// FIXME
		//display.connection.socket.setSoTimeout (0);

		int code = display.connection.din.read();

		/* Set the timeout to 10 seconds, a very safe bet to avoid premature
		 * timeout in slow networks. Setting a timeout avoid being locked
		 * when reading a reply or an event of a shorter length than
		 * expected, due to bugs in X server. For instance, the reply of
		 * {@link DBE#visual_info(Drawable[])} has a wrong reply length field
		 * in XFree86 4.0.1 or eariler servers.
		 *
		 * Note that timeout is reset to 0 (no timeout) before reading the
		 * first byte of the message because a blocking read of the next event
		 * or a blocking read of the expected reply from a slow X server should
		 * not be timed out.
		 */

		// FIXME
		//display.connection.streamConnection.setSoTimeout (10000);

		if (code == 0) // X Error
			throw build_error(display);

		else if (code == 1) // X Reply
			return build_reply(display);

		else
			// X Event
			return build_event(display, code);
	}

	public static Data build_reply(Display display) throws IOException {
		// 8 = fixed length of basic connection reply
		// 32 = fixed length of basic other reply
		int basic = display.connected ? 32 : 8;

		byte[] b = new byte[8];
		b[0] = 1; // reply code

		display.connection.din.readFully(b, 1, 7); // reply length

		int extra = 4 * (display.connected ? ((b[4] & 0xff) << 24) // general reply
				| ((b[5] & 0xff) << 16) | ((b[6] & 0xff) << 8) | (b[7] & 0xff) : ((b[6] & 0xff) << 8) | (b[7] & 0xff)); // connection reply

		Data reply = new Data();
		reply.data = new byte[basic + extra];
		System.arraycopy(b, 0, reply.data, 0, 8);
		display.connection.din.readFully(reply.data, 8, basic - 8 + extra);

		return reply;
	}
}
