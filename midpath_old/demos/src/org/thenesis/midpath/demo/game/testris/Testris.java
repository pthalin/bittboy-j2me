/*
 * MIDPath - Copyright (C) 2006-2007 Guillaume Legris, Mathieu Legris
 * JNode.org - Copyright (C) 2003-2006 JNode.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */

package org.thenesis.midpath.demo.game.testris;

import java.util.Random;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

/**
 * @author Levente S\u00e1ntha (JNode code)
 * @author Guillaume Legris (port to MIDP2)
 */
public class Testris extends MIDlet {

	private static final int[][][][] BLOCKS = {
			{ { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 1, 1 } }, // * * *
					{ { 1, 0 }, { 0, 1 }, { 1, 1 }, { 1, 2 } }, // *
					{ { 1, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 } }, { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 } } },
			{
					{ { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } }, // * * * *
					{ { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 3 } }, { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } },
					{ { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 3 } } }, { { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 0, 1 } }, // * * *
					{ { 0, 0 }, { 1, 0 }, { 1, 1 }, { 1, 2 } }, // *
					{ { 2, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 } }, { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 1, 2 } } },
			{ { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 2, 1 } }, // * * *
					{ { 1, 0 }, { 1, 1 }, { 0, 2 }, { 1, 2 } }, // *
					{ { 0, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 } }, { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 0, 2 } } },
			{ { { 1, 0 }, { 2, 0 }, { 0, 1 }, { 1, 1 } }, // * *
					{ { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 2 } }, // * *
					{ { 1, 0 }, { 2, 0 }, { 0, 1 }, { 1, 1 } }, { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 2 } } },
			{ { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 2, 1 } }, //   * *
					{ { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 } }, // * *
					{ { 0, 0 }, { 1, 0 }, { 1, 1 }, { 2, 1 } }, { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 } } },
			{ { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, // * *
					{ { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, // * *
					{ { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } } } };

	private static final int[][] DIMS = { { 3, 2 }, { 2, 3 }, { 3, 2 }, { 2, 3 }, { 4, 1 }, { 1, 4 }, { 4, 1 },
			{ 1, 4 }, { 3, 2 }, { 2, 3 }, { 3, 2 }, { 2, 3 }, { 3, 2 }, { 2, 3 }, { 3, 2 }, { 2, 3 }, { 3, 2 },
			{ 2, 3 }, { 3, 2 }, { 2, 3 }, { 3, 2 }, { 2, 3 }, { 3, 2 }, { 2, 3 }, { 2, 2 }, { 2, 2 }, { 2, 2 },
			{ 2, 2 }, };

	private static final int CELL = 10;

	private static final int WIDTH_C = 10;

	private static final int HEIGHT_C = 12;

	private final static Color[] COLORS = { Color.BLACK, Color.YELLOW, Color.RED, Color.CYAN, Color.BLUE,
			Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.DARK_GRAY };

	private int[][] WORLD = new int[WIDTH_C + 2][HEIGHT_C + 2];

	private int next_si = 0;

	private int si = 0;

	private int next_bi = 0;

	private int bi = 0;

	private int x = 1;

	private int y = 1;

	private boolean pause = false;

	private boolean up = true;

	private Thread thread;

	private int score;

	private boolean end = false;

	private Image img;

	//private static final Dimension DIM = new Dimension((WIDTH_C + 2) * CELL, (HEIGHT_C + 5 + 2) * CELL);
	private static final Dimension DIM = new Dimension((WIDTH_C + 2) * CELL, (HEIGHT_C + 2) * CELL);

	private Random si_rnd = new Random();

	private Random bi_rnd = new Random();

	private long delay = 500;

	private Display display;
	GameScreen screen;

	public void startApp() {

		display = Display.getDisplay(this);

		// Initialize
		delay = 500;
		for (int i = 0; i < WIDTH_C + 2; i++) {
			for (int j = 0; j < HEIGHT_C + 2; j++) {
				if (i == 0 || j == 0 || i == WIDTH_C + 1 || j == HEIGHT_C + 1)
					WORLD[i][j] = COLORS.length - 1;
				else
					WORLD[i][j] = 0;
			}
		}

		//System.out.println("W=" + DIM.width + " H=" + DIM.height);

		screen = new GameScreen(false);
		//screen.setFullScreenMode(true);
		newGame();
		display.setCurrent(screen);
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {

	}

	private int darken(int i) {
		int r = i - 64;
		return r < 0 ? 0 : r;
	}

	private int lighten(int i) {
		int r = i + 64;
		return r > 255 ? 255 : r;
	}

	class GameScreen extends Canvas {

		protected GameScreen(boolean arg0) {
			//super(arg0);
		}

		private void paintBox(Graphics g, int i, int j, Color c) {
			Color dc = new Color(darken(c.getRed()), darken(c.getGreen()), darken(c.getBlue()));
			Color lc = new Color(lighten(c.getRed()), lighten(c.getGreen()), lighten(c.getBlue()));
			g.setColor(c.getRGB());
			g.fillRect(i * CELL, j * CELL, CELL - 1, CELL - 1);
			g.setColor(dc.getRGB());
			g.drawLine(i * CELL, (j + 1) * CELL - 1, (i + 1) * CELL - 1, (j + 1) * CELL - 1);
			g.drawLine((i + 1) * CELL - 1, (j + 1) * CELL - 1, (i + 1) * CELL - 1, j * CELL);
			g.setColor(lc.getRGB());
			g.drawLine(i * CELL, (j + 1) * CELL - 1, i * CELL, j * CELL);
			g.drawLine(i * CELL, j * CELL, (i + 1) * CELL - 1, j * CELL);
		}
		
		
		public void paint(Graphics g) {

			if (img == null) {
				// Clean screen
				g.setColor(COLORS[0].getRGB());
				g.fillRect(0, 0, getWidth(), getHeight());
				// Create offscreen buffer
				img = Image.createImage(DIM.width, DIM.height + 4 * HEIGHT_C);
			}
			int offsetX = (getWidth() - img.getWidth()) / 2;
			int offsetY = (getHeight() - img.getHeight()) / 2;
			
			Graphics g2 = img.getGraphics();
			g2.setColor(COLORS[0].getRGB());
			g2.fillRect(0, 0, img.getWidth(), img.getHeight());
			for (int i = 0; i < WIDTH_C + 2; i++) {
				for (int j = 0; j < HEIGHT_C + 2; j++) {
					int ci = WORLD[i][j];
					if (ci > 0)
						paintBox(g2, i, j, COLORS[ci]);
				}
			}
			{
				Color c = COLORS[COLORS.length - 1];
				for (int i = 0; i < WIDTH_C + 2; i++) {
					paintBox(g2, i, HEIGHT_C + 6, c);
				}
				for (int j = 0; j < 4; j++) {
					paintBox(g2, 0, HEIGHT_C + 2 + j, c);
					paintBox(g2, 5, HEIGHT_C + 2 + j, c);
					// paintBox(g2, 6, HEIGHT_C + 2 +j, c );
					paintBox(g2, WIDTH_C + 1, HEIGHT_C + 2 + j, c);
				}
			}
			if (isUp()) {
				int[][] b = BLOCKS[si][bi];
				for (int i = 0; i < b.length; i++) {
					paintBox(g2, x + b[i][0], y + b[i][1], COLORS[si + 1]);
				}
				{
					g2.setColor(Color.WHITE.getRGB());
					g2.drawString("SCORE: " + score, 0, 0, Graphics.TOP | Graphics.LEFT);
					//g2.drawString("SCORE:", CELL + 2, (HEIGHT_C + 4) * CELL - 4, Graphics.TOP | Graphics.LEFT);
					//g2.drawString(String.valueOf(score), 2 * CELL, (HEIGHT_C + 5) * CELL - 4, Graphics.TOP
					//		| Graphics.LEFT);
					b = BLOCKS[next_si][next_bi];
					for (int i = 0; i < b.length; i++) {
						paintBox(g2, 7 + b[i][0], HEIGHT_C + 2 + b[i][1], COLORS[next_si + 1]);
					}
				}
			} else if (end) {
				g2.setColor(Color.BLACK.getRGB());
				g2.fillRect(0, 9 * CELL, img.getWidth() - 1, img.getHeight() - 9 * CELL);
				g2.setColor(Color.WHITE.getRGB());
				g2.drawRect(0, 9 * CELL, img.getWidth() - 1, img.getHeight() - 9 * CELL);
				g2.drawString("GAME OVER!", img.getWidth() / 2, 9 * CELL + (img.getHeight() - 9 * CELL)
						/ 2, Graphics.BOTTOM | Graphics.HCENTER);
				g2.drawString("SCORE: " + score, img.getWidth() / 2, 9 * CELL + (img.getHeight() - 9 * CELL)
						/ 2, Graphics.TOP | Graphics.HCENTER);

				//				g2.setColor(Color.BLACK.getRGB());
				//				g2.fillRect(2 * CELL, 9 * CELL, 8 * CELL, 4 * CELL);
				//				g2.setColor(Color.WHITE.getRGB());
				//				g2.drawRect(2 * CELL, 9 * CELL, 8 * CELL, 4 * CELL);
				//				g2.drawString("GAME OVER! SCORE: " + score, (WIDTH_C - 6) * CELL / 2 + 2, (HEIGHT_C + 2) * CELL / 2,
				//						Graphics.TOP | Graphics.LEFT);
			}

			
			g.drawImage(img, offsetX, offsetY, Graphics.TOP | Graphics.LEFT);
		}

		public void keyPressed(int keyCode) {

			int gameAction = getGameAction(keyCode);

			if (keyCode == Canvas.GAME_A) {
				newGame();
				return;
			}
			//			if (keyCode == KeyEvent.VK_P) {
			//				flipPause();
			//				return;
			//			}

			if (!isUp() || pause)
				return;
			switch (gameAction) {
			case Canvas.UP:
				rot(1);
				break;
			case Canvas.LEFT:
				trans(-1);
				break;
			case Canvas.DOWN:
				rot(3);
				break;
			case Canvas.RIGHT:
				trans(1);
				break;
			case Canvas.FIRE:
				fall();
				break;
			//			case KeyEvent.VK_N:
			//				newGame();
			//				break;
			//			case KeyEvent.VK_P:
			//				flipPause();
			//				break;
			default:
				return;
			}

			repaint();
		}


	}

	private void rot(int i) {
		int t = (bi + i) % 4;
		if (hasRoom(t, x, y)) {
			bi = t;
		}
	}

	private void trans(int i) {
		int t = x + i;
		if (hasRoom(bi, t, y)) {
			x = t;
		}
	}

	private void fall() {
		while (hasRoom(bi, x, y + 1))
			y++;
		//thread.interrupt();
	}

	public void newGame() {
		setUp(false);
		if (thread != null) {
			if (pause) {
				flipPause();
			}
			try {
				thread.join();
			} catch (InterruptedException ignore) {
			}
		}
		for (int i = 0; i < WIDTH_C + 2; i++) {
			for (int j = 0; j < HEIGHT_C + 2; j++) {
				if (i == 0 || j == 0 || i == WIDTH_C + 1 || j == HEIGHT_C + 1)
					WORLD[i][j] = COLORS.length - 1;
				else
					WORLD[i][j] = 0;
			}
		}
		//requestFocus();
		end = false;
		score = 0;
		si = si_rnd.nextInt(7);
		next_si = si_rnd.nextInt(7);
		bi = bi_rnd.nextInt(4);
		next_bi = bi_rnd.nextInt(4);
		x = 1 + bi_rnd.nextInt((WIDTH_C - DIMS[si * 4 + bi][0]));
		y = 0;
		thread = new Thread(new Runnable() {
			public void run() {
				try {
					long before, after, sleep;
					stop: while (isUp()) {
						before = System.currentTimeMillis();
						synchronized (Testris.class) {
							while (pause) {
								try {
									System.out.println("waiting");
									Testris.class.wait();
									System.out.println("back from waiting");
								} catch (InterruptedException ignore) {
								}
								if (!isUp())
									break stop;
							}
						}
						if (hasRoom(bi, x, y + 1)) {
							y++;
							screen.repaint();
						} else {
							newBlock();
							if (!hasRoom(bi, x, y)) {
								setUp(false);
								end = true;
								screen.repaint();
							}
						}
						after = System.currentTimeMillis();
						sleep = delay - (after - before);
						sleep = sleep < 0 ? delay : sleep;
						try {
							Thread.sleep(sleep);
						} catch (InterruptedException ignore) {

						}
					}
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}, "Tetris");
		setUp(true);
		thread.start();
	}

	private void flipPause() {
		synchronized (Testris.class) {
			pause = !pause;
			if (!pause)
				Testris.class.notifyAll();
		}
	}

	private void newBlock() {
		int[][] b = BLOCKS[si][bi];
		for (int i = 0; i < b.length; i++) {
			WORLD[x + b[i][0]][y + b[i][1]] = si + 1;
		}
		for (int i = 1; i < HEIGHT_C + 1; i++) {
			boolean full = true;
			for (int j = 1; j < WIDTH_C + 1; j++) {
				if (WORLD[j][i] == 0) {
					full = false;
					break;
				}
			}
			if (full) {
				int s = WIDTH_C;
				for (int j = 2; j < WIDTH_C + 1; j++) {
					if (WORLD[j - 1][i] != WORLD[j][i]) {
						--s;
					}
				}
				score += s;
			}
			if (full && i > 1) {
				for (int k = 0; k < i - 1; k++) {
					for (int j = 1; j < WIDTH_C + 1; j++) {
						WORLD[j][i - k] = WORLD[j][i - k - 1];
					}
				}
			}
		}
		si = next_si;
		next_si = si_rnd.nextInt(7);
		bi = next_bi;
		next_bi = bi_rnd.nextInt(4);
		x = 1 + bi_rnd.nextInt((WIDTH_C - DIMS[si * 4 + bi][0]));
		y = 1;
	}

	private boolean hasRoom(int bi, int x, int y) {
		boolean hasRoom = true;
		int[][] b = BLOCKS[si][bi];
		for (int i = 0; i < b.length; i++) {
			if (WORLD[x + b[i][0]][y + b[i][1]] != 0) {
				hasRoom = false;
				break;
			}
		}
		return hasRoom;
	}

	private synchronized boolean isUp() {
		return up;
	}

	private synchronized void setUp(boolean up) {
		this.up = up;
	}
}


class Dimension {
	public int width;
	public int height;

	public Dimension(int w, int h) {
		this.width = w;
		this.height = h;
	}

}
