package gnu.x11;


/** Iteration over an array of byte data. */
public class Enum extends Data {
  public int count;
  public int start_offset;
  public int start_count;


  public Enum (Data data, int offset, int count) {
    super (data, offset);    
    this.count = count;

    // save these for later reset
    start_offset = this.offset;
    start_count = count;
  }


  public boolean more () { return count > 0; }
  public Object elt (int i) { return null; }


  public void inc (int i) {
    count--;
    offset += i;
  }


  /**
   * Next object. Reminder: it is important to call {@link #inc(int)} in
   * overrided <code>next()</code>. See source code of {@link
   * Colormap#colors(int[])}.
   */
  public Object next () { 
    inc (0);
    return null; 
  }


  public int next1 () { 
    int value = read1 (0);
    inc (1);
    return value;
  }


  public int next2 () { 
    int value = read1 (2);
    inc (2);
    return value;
  }


  public int next4 () { 
    int value = read4 (0);
    inc (4);
    return value;
  }


  public long next8 () { 
    long value = read8 (0);
    inc (8);
    return value;
  }


  public boolean next_boolean () {
    boolean value = read_boolean (0);
    inc (1);
    return value;
  }


  public double next_double () {
    double value = read_double (0);
    inc (8);
    return value;
  }


  public double [] next_doublev (int len) {
    double [] retval = new double [len];
    for (int i=0; i<len; i++)
      retval [i] = next_double ();

    return retval;
  }


  public float next_float () {
    float value = read_float (0);
    inc (4);
    return value;
  }


  public int next_integer () {
    return next4 ();
  }


  public int [] next_integerv (int len) {
    int [] retval = new int [len];
    for (int i=0; i<len; i++)
      retval [i] = next_integer ();

    return retval;
  }


  public String next_string () {
    int len = read1 (0);
    String s = read_string (1, len);

    // 1 byte for null in c-string
    inc (len+1);
    return s;
  }


  public void reset () {
    offset = start_offset;
    count = start_count;
  }


  /**
   * @see #to_string(int, String)  
   */
  public String to_string (int kind) {
    return to_string (kind, "\n");
  }


  public static final int NEXT = 0;
  public static final int NEXT1 = 1;
  public static final int NEXT2 = 2;
  public static final int NEXT4 = 3;
  public static final int NEXT8 = 4;
  public static final int NEXT_BOOLEAN = 5;
  public static final int NEXT_DOUBLE = 6;
  public static final int NEXT_FLOAT = 7;
  public static final int NEXT_INTEGER = NEXT4;
  public static final int NEXT_STRING = 8;


  /**
   * @param kind valid: 
   * {@link #NEXT},
   * {@link #NEXT1},
   * {@link #NEXT2},
   * {@link #NEXT4},
   * {@link #NEXT8},
   * {@link #NEXT_BOOLEAN},
   * {@link #NEXT_DOUBLE},
   * {@link #NEXT_FLOAT},
   * {@link #NEXT_STRING}
   */
  public String to_string (int kind, String prefix) {
    StringBuffer sb = new StringBuffer ();

    // save states
    int saved_count = count;
    int saved_offset = offset;

    while (more ()) {
      sb.append (prefix);

      switch (kind) {
      case NEXT1: sb.append (next1 ()); break;
      case NEXT2: sb.append (next2 ()); break;
      case NEXT4: sb.append (next4 ()); break;
      case NEXT8: sb.append (next8 ()); break;
      case NEXT_BOOLEAN: sb.append (next_boolean ()); break;
      case NEXT_DOUBLE: sb.append (next_double ()); break;
      case NEXT_FLOAT: sb.append (next_float ()); break;
      case NEXT_STRING: sb.append (next_string ()); break;
      default: sb.append (next ()); break;
      }
    }

    // restore states
    count = saved_count;
    offset = saved_offset;

    return sb.toString ();
  }
}
