package com.sun.midp.events;

public interface EventMapper {
	/**
     * Return the key code that corresponds to the specified game
     * action on the device.  gameAction must be a defined game action
     * (Canvas.UP, Canvas.DOWN, Canvas.FIRE, etc.)
     * <B>Post-conditions:</B><BR> The key code of the key that
     * corresponds to the specified action is returned.  The return
     * value will be 0 if the game action is invalid or not supported
     * by the device.     
     *
     * @param gameAction The game action to obtain the key code for.
     *
     * @return the key code.
     */
    public int getKeyCode(int gameAction);

    /**
     * Returns the game action associated with the given key code on
     * the device.  keyCode must refer to a key that is mapped as a
     * game key on the device. The game action of the key is returned.
     * The return value will be 0 if the key is not mapped to 
     * a game action, or it will be -1 if the keycode is invalid.
     *
     * @param keyCode the key code
     *
     * @return the corresponding game action 
     *         (UP, DOWN, LEFT, RIGHT, FIRE, etc.)
     */
    public int getGameAction(int keyCode);

    /**
     * Returns <code>0</code> if keyCode is not a system key.  
     * Otherwise, returns one of the EventConstants.SYSTEM_KEY_ constants.
     *
     * @param keyCode get the system equivalent key.
     *
     * @return translated system key or zero if it is not a system key.
     */
    public int getSystemKey(int keyCode);

    /**
     * Gets an informative key string for a key. The string returned
     * should resemble the text physically printed on the key. For
     * example, on a device with function keys F1 through F4, calling
     * this method on the keycode for the F1 key will return the
     * string "F1". A typical use for this string will be to compose
     * help text such as "Press F1 to proceed."
     *
     * <p>There is no direct mapping from game actions to key
     * names. To get the string name for a game action, the
     * application must call
     *
     * <p><code>getKeyName(getKeyCode(GAME_A))</code>
     *
     * @param keyCode the key code being requested
     *
     * @return a string name for the key, or <code>null</code> if no name 
     *         is available
     */
    public String getKeyName(int keyCode);
}
