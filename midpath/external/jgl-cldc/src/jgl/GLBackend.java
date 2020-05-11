package jgl;

public interface GLBackend {

	/**
	 * Updates pixels of the backend.
	 * 
	 * @param w the width of the rectangle of pixels
	 * @param h the height of the rectangle of pixels
	 * @param pix an array of pixels
	 * @param off the offset into the array of where to store the first pixel
	 * @param scan the distance from one row of pixels to the next in the array
	 */
	public void updatePixels(int w, int h, int[] pix, int off, int scan);
	
	/**
	 * Paint pixels on the screen.
	 */
	public void sync();
	
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	
	public int[] getColorBuffer(int size);
	public float[] getDepthBuffer(int size);
	public int[] getStencilBuffer(int size);
	

}
