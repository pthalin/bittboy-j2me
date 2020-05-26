package gnu.x11.image;

import gnu.x11.Color;
import gnu.x11.Display;
import gnu.x11.Pixmap;


public class ZPixmap extends Image {
  public static final int FORMAT = 2;


  public int image_byte_order, pixel_byte_count;


  public ZPixmap (Display display) {        // for subclass loading
    format = FORMAT;
    pixmap_format = display.default_pixmap_format;
    image_byte_order = display.image_byte_order;
    pixel_byte_count = pixmap_format.bits_per_pixel () / 8;

    if (display.default_depth < 24)
      throw new Error ("Unsupported root depth < 24: " +
        display.default_depth);
  }


  public ZPixmap (Display display, int width, int height, Pixmap.Format format) {
    super (width, height, FORMAT, format);
    image_byte_order = display.image_byte_order;
    pixel_byte_count = pixmap_format.bits_per_pixel () / 8;

    if (display.default_depth < 24)
      throw new Error ("Unsupported root depth < 24: " +
        display.default_depth);
  }


  public void set (int x, int y, Color color) {
    set (x, y, color.pixel);
  }


  public void set (int x, int y, int pixel) {
    int i = y * line_byte_count + pixel_byte_count * x;

    // outside for loop for speed
    if (image_byte_order == LSB_FIRST)
      for (int j=0; j<pixel_byte_count; j++)
	data [i+j] = (byte) (0xff & (pixel >> j*8));

    else			// MSB_FIRST
      for (int j=0; j<pixel_byte_count; j++)
	data [i+j] = (byte) (0xff & (pixel >> (pixel_byte_count-1-j)*8));
  }


  public void set_red (int x, int y, int r) {
    int i = y * line_byte_count + pixel_byte_count * x;
    if (image_byte_order == LSB_FIRST) i += 2;
    data [i] = (byte) r;
  }


  public void set_green (int x, int y, int g) {
    int i = y * line_byte_count + pixel_byte_count * x + 1;
    data [i] = (byte) g;
  }


  public void set_blue (int x, int y, int b) {
    int i = y * line_byte_count + pixel_byte_count * x;
    if (image_byte_order == MSB_FIRST) i += 2;
    data [i] = (byte) b;
  }


  public void set (int x, int y, int r, int g, int b) {
    int i = y * line_byte_count + pixel_byte_count * x;

    // outside for loop for speed
    if (image_byte_order == LSB_FIRST) {
      data [i] = (byte) b;
      data [i+1] = (byte) g;
      data [i+2] = (byte) r;

    } else {			// MSB_FIRST
      data [i] = (byte) r;
      data [i+1] = (byte) g;
      data [i+2] = (byte) b;
    }
  }

  /**
   * Puts the image data into this image. This data must be in the same format
   * as specified in this image.
   *
   * @param image_data the data to set
   */
  public void set_data (int[] image_data) {
    int len = pixel_byte_count * width * height;
    len = Math.min(len, data.length);
    System.arraycopy(image_data, 0, data, 0, len);
  }
}
