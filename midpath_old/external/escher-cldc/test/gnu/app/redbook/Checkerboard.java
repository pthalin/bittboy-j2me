package gnu.app.redbook;

import gnu.x11.Data;


class Checkerboard {
  public static Data PIXELS, RED_PIXELS, RGB_PIXELS, SUB_PIXELS;

  public static final int SIZE = 64;
  public static final int SUB_SIZE = 16;


  static {
    byte [] [] [] pixels;


    pixels = new byte [SIZE] [SIZE] [4];

    for (int i=0; i<SIZE; i++)
      for (int j=0; j<SIZE; j++) {
        boolean b1 = (i & 0x8) == 0;
        boolean b2 = (j & 0x8) == 0;
        int c = (b1 && !b2) || (!b1 && b2) ? 255 : 0;

        pixels [i] [j] [0] = (byte) c;
        pixels [i] [j] [1] = (byte) c;
        pixels [i] [j] [2] = (byte) c;
        pixels [i] [j] [3] = (byte) 255;
      }
    
    PIXELS = new Data (pixels);


    pixels = new byte [SIZE] [SIZE] [4];

    for (int i=0; i<SIZE; i++)
      for (int j=0; j<SIZE; j++) {
        boolean b1 = (i & 0x8) == 0;
        boolean b2 = (j & 0x8) == 0;
        int c = (b1 && !b2) || (!b1 && b2) ? 255 : 0;

        pixels [i] [j] [0] = (byte) c;
        pixels [i] [j] [1] = (byte) 0;
        pixels [i] [j] [2] = (byte) 0;
        pixels [i] [j] [3] = (byte) 255;
      }
    
    RED_PIXELS = new Data (pixels);


    pixels = new byte [SIZE] [SIZE] [3];

    for (int i=0; i<SIZE; i++)
      for (int j=0; j<SIZE; j++) {
        boolean b1 = (i & 0x8) == 0;
        boolean b2 = (j & 0x8) == 0;
        int c = (b1 && !b2) || (!b1 && b2) ? 255 : 0;

        pixels [i] [j] [0] = (byte) c;
        pixels [i] [j] [1] = (byte) c;
        pixels [i] [j] [2] = (byte) c;
      }

    RGB_PIXELS = new Data (pixels);


    pixels = new byte [SUB_SIZE] [SUB_SIZE] [4];

    for (int i=0; i<SUB_SIZE; i++)
      for (int j=0; j<SUB_SIZE; j++) {
        boolean b1 = (i & 0x4) == 0;
        boolean b2 = (j & 0x4) == 0;
        int c = (b1 && !b2) || (!b1 && b2) ? 255 : 0;

        pixels [i] [j] [0] = (byte) c;
        pixels [i] [j] [1] = (byte) 0;
        pixels [i] [j] [2] = (byte) 0;
        pixels [i] [j] [3] = (byte) 255;
      }

    SUB_PIXELS = new Data (pixels);
  }
}    
