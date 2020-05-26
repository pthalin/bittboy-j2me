package sdljava.event;

public final class SDLEventType {
    public final static int     ACTIVEEVENT     = 1;	/* Application loses/gains visibility */
    public final static int     KEYDOWN         = 2;	/* Keys pressed */
    public final static int     KEYUP           = 3;	/* Keys released */
    public final static int     MOUSEMOTION     = 4;	/* Mouse moved */
    public final static int     MOUSEBUTTONDOWN = 5;      	/* Mouse button pressed */
    public final static int     MOUSEBUTTONUP   = 6;      	/* Mouse button released */
    public final static int     JOYAXISMOTION   = 7;      	/* Joystick axis motion */
    public final static int     JOYBALLMOTION   = 8;      	/* Joystick trackball motion */
    public final static int     JOYHATMOTION    = 9;     	/* Joystick hat position change */
    public final static int     JOYBUTTONDOWN   = 10;	/* Joystick button pressed */
    public final static int     JOYBUTTONUP     = 11;	/* Joystick button released */
    public final static int     QUIT            = 12;       /* User-requested quit */
    public final static int     SYSWMEVENT      = 13;	/* System specific event */
    public final static int     VIDEORESIZE     = 16;	/* User resized video mode */
    public final static int     VIDEOEXPOSE     = 17;	/* Screen needs to be redrawn */
}