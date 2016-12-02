import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Window {

	private static final boolean OVERLAPS = true;
	private static final boolean DIPSPAY_DAKOTA_SPACESHIP = true;

	int x = 30, y = 30;
	boolean[][] data = new boolean[x][y];
	boolean[][] data_TEMP = new boolean[x][y];
	int greyShade;
	int width = data.length;
	int height = data[0].length;
	int[] flattenedData = new int[width * height * 3];
	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	JLabel jLabel;
	JPanel jPanel;
	JFrame r;

	@SuppressWarnings("deprecation")
	public Window() {

		fillImage();
		jLabel = new JLabel(new ImageIcon(img));
		jLabel.setPreferredSize(new Dimension(100, 100));
		jPanel = new JPanel();
		jPanel.add(jLabel);
		r = new JFrame();
		r.add(jPanel);
		r.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		r.pack();
		r.show();
	}

	public void display() {

		int ind = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (data[i][j])
					greyShade = 255;
				else
					greyShade = 0;

				flattenedData[ind + j * 3] = greyShade;
				flattenedData[ind + j * 3 + 1] = greyShade;
				flattenedData[ind + j * 3 + 2] = greyShade;

			}
			ind += height * 3;
		}

		img.getRaster().setPixels(0, 0, data.length, data[0].length, flattenedData);
		jLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_FAST)));
		r.update(r.getGraphics());
	}

	private void fillImage() {

		if (DIPSPAY_DAKOTA_SPACESHIP) {
			for (int i = 0; i < getDakota().length; i++) {
				System.arraycopy(getDakota()[i], 0, data[i + data.length / 2], data[i].length / 2, getDakota()[0].length);
			}
		} else {
			for (int i = 0; i < getSpaceShip().length; i++) {
				System.arraycopy(getSpaceShip()[i], 0, data[i + data.length / 2], data[i].length / 2, getSpaceShip()[0].length);
			}
		}
	}

	public synchronized void doLife() {
		for ( int i=0; i<data.length; i++){
			System.arraycopy(data[i], 0, data_TEMP[i], 0, data.length);
		}
		
		for (int y = 0; y < data[0].length; y++) {
			for (int x = 0; x < data.length; x++) {
				data[x][y] = isSurvive(x, y);
			}
		}
	}

	public synchronized boolean isSurvive(int x, int y) {
		boolean live = data[x][y];
		int n = sumNeigh(x, y);
		if (live) {
			if (n >= 2 && n <= 3)
				return true;
			return false;
		} else {
			if (n == 3)
				return true;
			return false;
		}

	}

	public synchronized int sumNeigh(int x, int y) {
		int neigh = 0;
		int xi, yj;
		if (OVERLAPS) {
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					xi = i;
					yj = j;
					if (i == y && j == x)
						continue;
					if (i < 0)
						xi = data_TEMP[0].length - 1;
					else if (i >= data_TEMP[0].length)
						xi = 0;

					if (j < 0)
						yj = data_TEMP.length - 1;
					else if (j >= data_TEMP.length)
						yj = 0;

					try {
						if (data_TEMP[yj][xi] == true)
							neigh++;
					} catch (Exception e) {
					}
				}
			}
		} else {
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					if (i == y && j == x)
						continue;
					try {
						if (data_TEMP[j][i] == true)
							neigh++;
					} catch (Exception e) {
					}
				}
			}
		}
		return neigh;
	}

    private boolean[][] getDakota() {
		boolean[][] dakota = new boolean[4][5];

		dakota[0][0] = false;
		dakota[0][1] = true;
		dakota[0][2] = false;
		dakota[0][3] = false;
		dakota[0][4] = true;

		dakota[1][0] = true;
		dakota[1][1] = false;
		dakota[1][2] = false;
		dakota[1][3] = false;
		dakota[1][4] = false;

		dakota[2][0] = true;
		dakota[2][1] = false;
		dakota[2][2] = false;
		dakota[2][3] = false;
		dakota[2][4] = true;

		dakota[3][0] = true;
		dakota[3][1] = true;
		dakota[3][2] = true;
		dakota[3][3] = true;
		dakota[3][4] = false;

		return dakota;
    }

	private boolean[][] getSpaceShip() {
		boolean[][] spaceShip = new boolean[3][3];

		spaceShip[0][0] = true;
		spaceShip[0][1] = true;
		spaceShip[0][2] = true;
		spaceShip[1][0] = true;
		spaceShip[1][1] = false;
		spaceShip[1][2] = false;
		spaceShip[2][0] = false;
		spaceShip[2][1] = true;
		spaceShip[2][2] = false;

		return spaceShip;
	}

	public void displayInConsole() {
		for (int x = 0; x < data[0].length; x++) {
			for (int y = 0; y < data.length; y++) {
				if (data[x][y])
					System.out.print('#');
				else {
					System.out.print((char) 127);
				}
			}
			System.out.println();
		}
	}

	public void displayDakotaInConsole() {
		for (int x = 0; x < getDakota().length; x++) {
			for (int y = 0; y < getDakota()[0].length; y++) {
				if (getDakota()[x][y])
					System.out.print('#');
				else {
					System.out.print((char) 127);
				}
			}
			System.out.println();
		}
	}
}
