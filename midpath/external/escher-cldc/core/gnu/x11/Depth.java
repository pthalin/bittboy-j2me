package gnu.x11;


/** X depth. */
public class Depth extends Data {
  public Depth (Data data, int offset) { super (data, offset); }
  public int depth () { return read1 (0); }
  public int visual_count () { return read2 (2); }
  public int length () { return 8 + visual_count ()*24; }


  /**
   * @return valid:
   * {@link Enum#elt(int)} of type {@link Visual},
   * {@link Enum#next()} of type {@link Visual}
   */
  public Enum visuals () {
    return new Enum (this, 8, visual_count ()) {
      public Object elt (int i) {
        int offset = start_offset + i*24;
        return new Visual (this, offset);
      }


      public Object next () {
        Visual visual = new Visual (this, 0);
        inc (24);
        return visual;
      }     
    };
  }


  public String toString () {
    return "#Depth"
      + "\n  depth: " + depth ()
      + "\n  visual-count: " + visual_count ()
      + visuals ().to_string (Enum.NEXT, "\n#Depth: ");
  }
}
