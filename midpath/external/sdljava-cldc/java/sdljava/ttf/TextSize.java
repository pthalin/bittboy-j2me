package sdljava.ttf;

public class TextSize {

	int width;
	int height;

	public TextSize() {
	}

	public TextSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Gets the value of width
	 *
	 * @return the value of width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Sets the value of width
	 *
	 * @param argWidth Value to assign to this.width
	 */
	public void setWidth(int argWidth) {
		this.width = argWidth;
	}

	/**
	 * Gets the value of height
	 *
	 * @return the value of height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Sets the value of height
	 *
	 * @param argHeight Value to assign to this.height
	 */
	public void setHeight(int argHeight) {
		this.height = argHeight;
	}

	public String toString() {
		return "TextSize[width=" + width + ", height=" + height + "]";
	}
}