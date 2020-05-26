package gnu.x11.extension;


public interface EventFactory {
  gnu.x11.event.Event build (gnu.x11.Display display, 
    byte [] data, int code); 
}
