package gnu.x11;


/** X keyboard and pointer. */
public class Input {
  // KEYBUTMASK - keyboard button mask
  public static final int SHIFT_MASK = 1<<0;
  public static final int LOCK_MASK = 1<<1; // cap lock
  public static final int CONTROL_MASK = 1<<2;
  public static final int MOD1_MASK = 1<<3; // alt key
  public static final int MOD2_MASK = 1<<4; // num lock
  public static final int MOD3_MASK = 1<<5; // menu key
  public static final int MOD4_MASK = 1<<6; // window key
  public static final int MOD5_MASK = 1<<7; // scroll lock
  public static final int BUTTON1_MASK = 1<<8;
  public static final int BUTTON2_MASK = 1<<9;
  public static final int BUTTON3_MASK = 1<<10;
  public static final int BUTTON4_MASK = 1<<11;
  public static final int BUTTON5_MASK = 1<<12;


  // 104 PC keyboard
  public static final int META_MASK = MOD1_MASK;
  public static final int ALT_MASK = MOD3_MASK;
  public static final int SUPER_MASK = MOD4_MASK;


  public static final int BUTTON1 = 1;
  public static final int BUTTON2 = 2;
  public static final int BUTTON3 = 3;
  public static final int BUTTON4 = 4;
  public static final int BUTTON5 = 5;


  public static final int [] LOCK_COMBINATIONS = {
    0, LOCK_MASK, LOCK_MASK|MOD2_MASK, LOCK_MASK|MOD5_MASK, 
    LOCK_MASK|MOD2_MASK|MOD5_MASK, MOD2_MASK, MOD2_MASK|MOD5_MASK,
    MOD5_MASK
  };


  public Display display;
  public int min_keycode, max_keycode, keysyms_per_keycode;
  public int [] keysyms;
  

  public Input (Display display, int min_keycode, int max_keycode) {
    this.display = display;
    this.min_keycode = min_keycode;
    this.max_keycode = max_keycode;
  }


  // opcode 27 - ungrab pointer
  /**
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XUngrabPointer.html">XUngrabPointer</a>
   */
  public void ungrab_pointer (int time) {
    Request request = new Request (display, 27, 2);
    request.write4 (time);
    display.send_request (request);
  }


  // opcode 30 - change active pointer grab
  /**
   * @param cursor possible: {@link Cursor#NONE}
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XChangeActivePointerGrab.html">
   *  XChangeActivePointerGrab</a>
   */
  public void change_active_pointer_grab (int event_mask, 
    Cursor cursor, int time) {

    Request request = new Request (display, 30, 4);
    request.write4 (cursor.id);
    request.write4 (time);
    request.write2 (event_mask);
    display.send_request (request);    
  }


  // opcode 32 - ungrab keyboard
  /**
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XUngrabKeyboard.html">XUngrabKeyboard</a>
   */
  public void ungrab_keyboard (int time) {
    Request request = new Request (display, 32, 2);
    request.write4 (time);
    display.send_request (request);
  }


  public static final int ASYNC_POINTER = 0;
  public static final int SYNC_POINTER = 1;
  public static final int REPLY_POINTER = 2;
  public static final int ASYNC_KEYBOARD = 3;
  public static final int SYNC_KEYBOARD = 4;
  public static final int REPLY_KEYBOARD = 5;
  public static final int ASYNC_BOTH = 6;
  public static final int SYNC_BOTH = 7;


  // opcode 35 - allow events
  /**
   * @param mode valid:
   * {@link #ASYNC_POINTER},
   * {@link #SYNC_POINTER},
   * {@link #REPLY_POINTER},
   * {@link #ASYNC_KEYBOARD},
   * {@link #SYNC_KEYBOARD},
   * {@link #REPLY_KEYBOARD},
   * {@link #ASYNC_BOTH},
   * {@link #SYNC_BOTH}
   *
   * @param time possible: {@link Display#CURRENT_TIME}
   * @see <a href="XAllowEvents.html">XAllowEvents</a>
   */
  public void allow_events (int mode, int time) {
    Request request = new Request (display, 35, mode, 2);
    request.write4 (time);
    display.send_request (request);
  }


  /** Reply of {@link #input_focus()} */
  public static class InputFocusReply extends Data {
    public Display display;

    public InputFocusReply (Display display, Data data) { 
      super (data); 
      this.display = display;
    }
  
  
    /**
     * @return possible:
     * {@link Window#NONE},
     * {@link Window#POINTER_ROOT}
     */
    public int focus_id () { return read4 (8); }
  
  
    public Window focus () { 
      return (Window) Window.intern (this.display, focus_id ()); 
    }
  
  
    /** 
     * @return valid:
     * {@link Window#TO_NONE},
     * {@link Window#TO_POINTER_ROOT},
     * {@link Window#TO_PARENT}
     */
    public int revert_to () { return read1 (1); }
  }
  
  
  // opcode 43 - get input focus
  /**
   * @see <a href="XGetInputFocus.html">XGetInputFocus</a>
   */
  public InputFocusReply input_focus () {
    Request request = new Request (display, 43, 1);
    return new InputFocusReply (display, display.read_reply (request));
  }


  // opcode 44 - query keymap
  /**
   * @return valid: {@link Enum#next1()}
   * @see <a href="XQueryKeymap.html">XQueryKeymap</a>
   */
  public Enum keymap () {
    Request request = new Request (display, 44, 1);
    return new Enum (display.read_reply (request), 8, 32);
  }


  // opcode 100 - change keyboard mapping  
  /**
   * @see <a href="XChangeKeyboardMapping.html">XChangeKeyboardMapping</a>
   */
  public void change_keyboard_mapping (int first_keycode, 
    int keysyms_per_keycode, int [] keysyms) {
    
    int keycode_count = keysyms.length / keysyms_per_keycode;

    Request request = new Request (display, 100, keycode_count, 2+keysyms.length);
    request.write1 (first_keycode);
    request.write1 (keysyms_per_keycode);
    request.write2_unused ();

    for (int i=0; i<keysyms.length; i++)
      request.write4 (keysyms [i]);

    display.send_request (request);
  }


  // opcode 101 - get keyboard mapping  
  /**
   * @see <a href="XGetKeyboardMapping.html">XGetKeyboardMapping</a>
   */
  public void keyboard_mapping () {
    int keysym_count = max_keycode - min_keycode + 1;

    Request request = new Request (display, 101, 2);
    request.write1 (min_keycode);
    request.write1 (keysym_count);
    Data reply = display.read_reply (request);

    keysyms_per_keycode = reply.read1 (1);
    keysyms = new int [keysym_count * keysyms_per_keycode];

    for (int i=0; i < keysym_count * keysyms_per_keycode; i++)
      keysyms [i] = reply.read4 (32 + 4 * i);
  }


  /** Reply of {@link #keyboard_control()} */
  public static class KeyboardControlReply extends Data {
    public KeyboardControlReply (Data data) { super (data); }
    public int led_mask () { return read4 (8); }
    public int key_click_percent () { return read1 (12); }
    public int bell_percent () { return read1 (13); }
    public int bell_pitch () { return read2 (14); }
    public int bell_duration () { return read2 (16); }
  
    
    /**
     * @return valid:
     * {@link Input.KeyboardControl#OFF},
     * {@link Input.KeyboardControl#ON}
     */
    public int global_auto_repeat () { return read1 (1); }
  
  
    /**
     * @return valid: {@link Enum#next1()}
     */
    public Enum auto_repeats () {
      return new Enum (this, 20, 32);
    }
  
  
    public String toString () {
      return "#KeyboardControlReply"
        + "\n  global-auto-repeat: " 
        + KeyboardControl.GLOBAL_AUTO_REPEAT_STRINGS [global_auto_repeat ()]
        + "\n  led-mask: " + Integer.toBinaryString (led_mask ())
        + "\n  key-click-percent: " + key_click_percent ()
        + "\n  bell-percent: " + bell_percent ()
        + "\n  bell-pitch: " + bell_pitch ()
        + "\n  bell-duration: " + bell_duration ();
    }
  }
  
  
  /** X keyboard control. */
  public static class KeyboardControl extends ValueList { // TODO
    public KeyboardControl () { super (8); }
  
  
    public static final int OFF = 0;
    public static final int ON = 1;
  
  
    public static final String [] GLOBAL_AUTO_REPEAT_STRINGS
      = {"off", "on"};
  }


  // opcode 102 - change keyboard control
  /**
   * @see <a href="XChangeKeyboardControl.html">XChangeKeyboardControl</a>
   */
  public void change_keyboard_control (KeyboardControl control) {
    display.send_request (new Request.ValueList (
      display, 102, 2, 0, control));
  }


  // opcode 103 - get keyboard control
  /**
   * @see <a href="XGetKeyboardControl.html">XGetKeyboardControl</a>
   */
  public KeyboardControlReply keyboard_control () {
    Request request = new Request (display, 103, 1);    
    return new KeyboardControlReply (display.read_reply (request));
  }


  // opcode 105 - change pointer control
  /**
   * @see <a href="XChangePointerControl.html">XChangePointerControl</a>
   */
  public void change_pointer_control (boolean do_accel, 
    boolean do_threshold, int accel_numerator, int accel_denominator,
    int threshold) {

    Request request = new Request (display, 105, 3);
    request.write2 (accel_numerator);
    request.write2 (accel_denominator);
    request.write2 (threshold);
    request.write1 (do_accel);
    request.write1 (do_threshold);
    display.send_request (request);
  }


  /** Reply of {@link #pointer_control()}. */
  public static class PointerControlReply extends Data {
    public PointerControlReply (Data data) { super (data); }
    public int acceleration_numerator () { return read2 (8); }
    public int acceleration_denumerator () { return read2 (10); }
    public int threshold () { return read2 (12); }
  
  
    public String toString () {
      return "#PointerControlReply"
        + "\n  acceleration: " 
        + acceleration_numerator () + "/" + acceleration_denumerator ()
        + "\n  threshold: " + threshold ();
    }
  }
  
  
  // opcode 106 - get pointer control
  /**
   * @see <a href="XGetPointerControl.html">XGetPointerControl</a>
   */
  public PointerControlReply pointer_control () {
    Request request = new Request (display, 106, 1);    
    return new PointerControlReply (display.read_reply (request));
  }


  public static final int SUCCESS = 0;
  public static final int BUSY = 1;


  // opcode 116 - set pointer mapping
  /**
   * @return valid:
   * {@link #SUCCESS},
   * {@link #BUSY}
   *
   * @see <a href="XSetPointerMapping.html">XSetPointerMapping</a>
   */
  public int set_pointer_mapping (byte [] map) {
    Request request = new Request (display, 116, map.length, 2+Data.unit (map));
    request.write1 (map);
    return display.read_reply (request).read1 (1);
  }


  // opcode 117 - get pointer mapping
  /**
   * @see <a href="XGetPointerMapping.html">XGetPointerMapping</a>
   */
  public Data pointer_mapping () {
    Request request = new Request (display, 117, 1);
    return display.read_reply (request);
  }


  public static final int FAILED = 2;


  // opcode 118 - set modifier mapping
  /**
   * @return valid:
   * {@link #SUCCESS},
   * {@link #BUSY},
   * {@link #FAILED}
   *
   * @see <a href="XSetModifierMapping.html">XSetModifierMapping</a>
   */
  public int set_modifier_mapping (int keycodes_per_modifier, 
    byte [] keycodes) {

    Request request = new Request (display, 118, keycodes_per_modifier, 
      1+Data.unit (keycodes));
    
    request.write1 (keycodes);
    return display.read_reply (request).read1 (1);
  }


  // opcode 119 - get modifier mapping
  /**
   * @return valid: {@link Enum#next1()}
   * @see <a href="XModifierKeymap.html">XModifierKeymap</a>
   */
  public Enum modifier_mapping () {
    Request request = new Request (display, 119, 1);

    Data reply = display.read_reply (request);
    return new Enum (reply, 32, reply.read1 (1));
  }


  public static final String [] KEYBUT_STRINGS = {
    "shift", "lock", "control", "mod1", "mod2", "mod3", "mod4", "mod5",
    "button1", "button2", "button3", "button4", "button5"
  };


  public static void dump_keybut_mask (int m) {
    for (int i=0; i<KEYBUT_STRINGS.length; i++)
      if (((m & 0x1fff) & 1 << i) != 0)
	System.out.print (KEYBUT_STRINGS [i] + " ");
  }

  /**
   * Maps a keycode to a keysym.
   *
   * @param keycode the keycode
   * @param keystate the modifiers
   *
   * @return the keysym for the specified key code and modifier mask
   */
  public int keycode_to_keysym (int keycode, int keystate) {
    return keycode_to_keysym(keycode, keystate, false);
  }

  /**
   * Maps a keycode to a keysym. When <code>ignore_modifiers</code> is
   * <code>true</code> then this returns the plain keysymbol, independent
   * of the modifiers. Otherwise it returns the real symbol.
   *
   * @param keycode the keycode
   * @param keystate the modifiers
   * @param ignore_modifiers <code>true</code> for returning plain
   *        keysyms, <code>false</code> for taking the modifiers
   *        into account
   *
   * @return the keysym for the specified key code and modifier mask
   */
  public int keycode_to_keysym (int keycode, int keystate,
                                boolean ignore_modifiers) {
    if (keycode > max_keycode) 
      throw new java.lang.Error ("Invalid keycode: " + keycode);

    int keysym = 0;
    int keysym_no = 0;

    // TODO: Maybe add handling of other modifiers.
    if (! ignore_modifiers) {
      if ((keystate & SHIFT_MASK) != 0)
        keysym_no = 1;
      else if ((keystate & MOD5_MASK) != 0) // Alt Gr
        keysym_no = 2; // TODO: 4 seems also valid.
    }

    int index = (keycode - min_keycode) * keysyms_per_keycode + keysym_no;
    keysym = keysyms[index];
    return keysym;
  }


  public int keysym_to_keycode (int keysym) {
    // linear lookup - expensive?
    //for (int i=0; i<keysyms.length; i++)
    // FIXME, hacked to do it in reverse order for solaris

    for (int i=keysyms.length-1; i>=0; i--)
      if (keysyms [i] == keysym)
	return i + min_keycode;
    
    throw new java.lang.Error ("Invalid keysym: " + keysym);	
  }


  /** 
   * Input#ungrab_keyboard(int)
   */
  public void ungrab_keyboard () {
    ungrab_keyboard (Display.CURRENT_TIME);
  }
}
