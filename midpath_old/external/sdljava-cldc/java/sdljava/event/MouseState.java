package sdljava.event;

/**
 * The current state of the mouse.  The x and y position as well as the button state.
 *
 * @author  Ivan Z. Ganza
 * @version $Id: MouseState.java,v 1.1 2004/12/27 06:23:49 ivan_ganza Exp $
 */
public class MouseState {
	int x, y;
	MouseButtonState buttonState;

	/**
	 * Creates a new <code>MouseState</code> instance.
	 *
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param buttons an <code>int</code> value
	 */
	public MouseState(int x, int y, int buttons) {
		this.x = x;
		this.y = y;
		buttonState = MouseButtonState.get(buttons);
	}

	/**
	 * Gets the value of x
	 *
	 * @return the value of x
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Gets the value of y
	 *
	 * @return the value of y
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Gets the value of buttonState
	 *
	 * @return the value of buttonState
	 */
	public MouseButtonState getButtonState() {
		return this.buttonState;
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("MouseState[").append("x=").append(x).append(", y=").append(y).append(", ").append(buttonState)
				.append("]");

		return buf.toString();
	}
}