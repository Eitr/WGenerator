package net.buddat.wgenerator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.buddat.wgenerator.util.Constants;
import net.buddat.wgenerator.util.ProgressHandler;
import net.buddat.wgenerator.util.SimplexNoise;

/**
 * 16 bit grayscale image. All height values for each point will be between 0.0 and 1.0
 */
public class HeightMap {

	private double[][] heightArray;
	private long noiseSeed;
	private int mapSize;
	private double resolution;
	private int iterations;
	private boolean moreLand;
	private int minimumEdge;
	private int maxHeight;
	private int normalizeRatio;
	private int borderCutoff;
	private double borderNormalize;
	private double singleDirt;

	private BufferedImage heightImage;

	public HeightMap(long seed, int mapSize, double resolution, int iterations, int minimumEdge, int borderWeight, int maxHeight, int normalizeRatio, boolean moreLand) {
		this.noiseSeed = seed;
		this.mapSize = mapSize;
		this.resolution = resolution;
		this.iterations = iterations;
		this.minimumEdge = minimumEdge;
		this.maxHeight = maxHeight;
		this.normalizeRatio = normalizeRatio;
		this.moreLand = moreLand;

		this.heightArray = new double[mapSize][mapSize];
		this.borderCutoff = (int) (mapSize / Math.abs(borderWeight));
		this.borderNormalize = (float) (1.0 / borderCutoff);

		this.singleDirt = 1.0 / maxHeight;
	}

	public HeightMap(BufferedImage heightImage, int mapSize, int maxHeight) {
		this.noiseSeed = 0;
		this.mapSize = mapSize;
		this.resolution = 0;
		this.iterations = 0;
		this.minimumEdge = 0;
		this.maxHeight = maxHeight;
		this.moreLand = false;
		this.heightImage = heightImage;

		this.heightArray = new double[mapSize][mapSize];
		this.borderCutoff = 1;
		this.borderNormalize = (float) (1.0f / borderCutoff);

		this.singleDirt = 1.0 / maxHeight;

		importHeightImage();
	}


	private void importHeightImage() {
		if (heightImage.getHeight() != heightImage.getWidth())
		{
			JOptionPane.showMessageDialog(null, "The map must be square!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (heightImage.getHeight() != mapSize || heightImage.getWidth() != mapSize) {
			JOptionPane.showMessageDialog(null, "The image size does not match your map size! "+heightImage.getHeight(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		long startTime = System.currentTimeMillis();
		try {
            Raster data = heightImage.getData();

            for (int x = 0; x < mapSize; x++) {
                for (int y = 0; y < mapSize; y++) {

                    double[] dArray = null;

                    dArray = data.getPixel(x,y, dArray);

					setHeight(x, y, getHeight(x, y) + (dArray[0] / 65536f), false);
				}
			}

			MainWindow.log("HeightMap Import (" + mapSize + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The map must be 16-bit grayscale.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	void exportHeightImage(String txtName, String fileName) {
		File imageFile = new File("./maps/" + txtName + "/" + fileName);
		BufferedImage bufferedImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_USHORT_GRAY);
		WritableRaster wr = (WritableRaster) bufferedImage.getRaster();

		double[] array = new double[mapSize * mapSize];
		for (int x = 0; x < mapSize; x++) {
			for (int y = 0; y < mapSize; y++) {
				array[x + y * mapSize] = (getHeight(x,y) * 65535);
			}
		}

		wr.setPixels(0, 0, mapSize, mapSize, array);

		bufferedImage.setData(wr);
		try {
			if (!imageFile.exists())
				imageFile.mkdirs();
			ImageIO.write(bufferedImage, "png", imageFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to create heightmap file.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}


	double maxDiff(int x, int y) {
		double neighbours[] = new double[4];
		double currentTile = heightArray[x][y];

		neighbours[0] = heightArray[clamp(x - 1, 0, mapSize - 1)][y];
		neighbours[1] = heightArray[x][clamp(y - 1, 0, mapSize - 1)];
		neighbours[2] = heightArray[clamp(x + 1, 0, mapSize - 1)][y];
		neighbours[3] = heightArray[x][clamp(y + 1, 0, mapSize - 1)];

		double maxDiff = 0.0;
		for (int k = 0; k < 3; k++) {
			double diff = currentTile - neighbours[k];
			if (diff > maxDiff) {
				maxDiff = diff;
			}
		}
		return maxDiff;
	}

	/**
	 *  Generates a full heightmap with the current instance's set values.
	 *  Clamps the heightmap heights for the last iteration only.
	 * @throws InterruptedException 
	 */
	void generateHeights(ProgressHandler progress) throws InterruptedException {
		MainWindow.log("HeightMap seed set to: " + noiseSeed);
		SimplexNoise.genGrad(noiseSeed);

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			int progressValue = (int)((float)i/iterations*99f); 
			long predict = (int)((System.currentTimeMillis()-startTime)/1000.0*(100.0/progressValue-1));
			progress.update(progressValue,progress.getText().substring(0, progress.getText().indexOf("("))+"("+predict+" secs)");

			double iRes = resolution / Math.pow(2, i - 1);
			double str = Math.pow(2, i - 1) * 2.0;
			boolean clamp = (i == iterations - 1);
			
			// Splits the map into equal vertical chunks for multithreading
			int chunkSize = mapSize / Constants.CPU_CORES;
			Thread [] threads = new Thread[Constants.CPU_CORES];
			
			for(int core=0; core<Constants.CPU_CORES; core++) {
				int start = core * chunkSize;
				int end = start + Math.min(chunkSize, mapSize-chunkSize);
				threads[core] = new GenHeightWorker(start,end,iRes,str,clamp);
				threads[core].start();
			}
			
			for(Thread thread : threads) {
				thread.join();
			}
			
		}

		MainWindow.log("HeightMap Generation (" + mapSize + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");

		normalizeHeights();
	}
	
	private class GenHeightWorker extends Thread {
		
		private int start;
		private int end;
		private double iRes;
		private double str;
		private boolean clamp;
		
		public GenHeightWorker(int start, int end, double iRes, double str, boolean clamp) {
			this.start = start;
			this.end = end;
			this.iRes = iRes;
			this.str = str;
			this.clamp = clamp;
		}
		
		public void run() {
			for (int x = start; x < end; x++) {
				for (int y = 0; y < mapSize; y++) {
					setHeight(x, y, getHeight(x,y) + SimplexNoise.noise(x / iRes, y / iRes) / str, clamp);
				}
			}
		}
	}


	private void normalizeHeights() {
		long startTime = System.currentTimeMillis();

		double maxHeight = 0.0f;
		for (int i = 0; i < mapSize; i++) {
			for (int j = 0; j < mapSize; j++) {
				if (getHeight(i, j) > maxHeight) {
					maxHeight = getHeight(i, j);
				}
			}
		}

		double normalize = 1.0f / maxHeight;
		for (int i = 0; i < mapSize; i++) {
			for (int j = 0; j < mapSize; j++) {
				setHeight(i, j, getHeight(i, j) * normalize, false);
			}
		}

		//Converts the bottom half into the bottom 1/3, and the top half into the top 2/3
		double normalizeLow = normalizeRatio/50.0;
		double normalizeHigh = 2.0-normalizeLow;
		for (int i = 0; i < mapSize; i++) {
			for (int j = 0; j < mapSize; j++) {
				if (getHeight(i, j) < 0.5) {
					setHeight(i, j, (getHeight(i, j) * normalizeLow), false);
				} else {
					double newHeight = normalizeLow/2.0 + (getHeight(i, j) - 0.5) * normalizeHigh;
					setHeight(i, j, newHeight, false);
				}
			}
		}

		MainWindow.log("HeightMap Normalization (" + mapSize + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}


	void erode(int iterations, int minSlope, int maxSlope, int sedimentMax, ProgressHandler progress) {
		long startTime = System.currentTimeMillis();

		for (int iter = 0; iter < iterations; iter++) {
			int progressValue = (int)((float)iter/iterations*99f); 
			long predict = (int)((System.currentTimeMillis()-startTime)/1000.0*(100.0/progressValue-1));
			progress.update(progressValue,progress.getText().substring(0, progress.getText().indexOf("("))+"("+predict+" secs)");

			erodeArea(0,0,mapSize,minSlope,maxSlope,sedimentMax);
		}
		MainWindow.log("HeightMap Erosion (" + iterations + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	void erodeArea(int x, int y, int size, int minSlope, int maxSlope, int sedimentMax) {
		for (int i = Math.max(0,x); i < Math.min(mapSize,x+size); i++) {
			for (int j = Math.max(0, y); j < Math.min(mapSize,y+size); j++) {
				double neighbours[] = new double[4];
				double currentTile = heightArray[i][j];

				neighbours[0] = heightArray[clamp(i - 1, 0, mapSize - 1)][j];
				neighbours[1] = heightArray[i][clamp(j - 1, 0, mapSize - 1)];
				neighbours[2] = heightArray[clamp(i + 1, 0, mapSize - 1)][j];
				neighbours[3] = heightArray[i][clamp(j + 1, 0, mapSize - 1)];

				int lowest = 0;
				double maxDiff = 0.0;
				for (int k = 0; k < 3; k++) {
					double diff = currentTile - neighbours[k];
					if (diff > maxDiff) {
						maxDiff = diff;
						lowest = k;
					}
				}

				double sediment = 0.0;
				if (maxDiff > minSlope * singleDirt && maxDiff < maxSlope * singleDirt) {
					sediment = (sedimentMax * singleDirt) * maxDiff;
					currentTile -= sediment;
					neighbours[lowest] += sediment;
				}

				setHeight(i, j, currentTile, false);
				setHeight(clamp(i - 1, 0, mapSize - 1), j, neighbours[0], false);
				setHeight(i, clamp(j - 1, 0, mapSize - 1), neighbours[1], false);
				setHeight(clamp(i + 1, 0, mapSize - 1), j, neighbours[2], false);
				setHeight(i, clamp(j + 1, 0, mapSize - 1), neighbours[3], false);
			}
		}
	}

	double getHeight(int x, int y) {
		return heightArray[x][y];
	}

	/**
	 * @param x Location x
	 * @param y Location y
	 * @param newHeight Height to set the location to
	 * @param clamp Whether to clamp the location's height depending on x/y and the border cutoff (Constants.BORDER_WEIGHT)
	 */
	private void setHeight(int x, int y, double newHeight, boolean clamp) {
		if (newHeight < (moreLand ? -1d : 0))
			newHeight = (moreLand ? -1d : 0);
		if (newHeight > 1d)
			newHeight = 1d;

		heightArray[x][y] = newHeight;

		if (clamp) {
			if (moreLand)
				heightArray[x][y] = (heightArray[x][y] + 1) * 0.5d;

			if (x <= borderCutoff + minimumEdge || y <= borderCutoff + minimumEdge) {
				if (x < y)
					heightArray[x][y] *= Math.max(0, ((Math.min(x, mapSize - y) - minimumEdge)) * borderNormalize);
				else
					heightArray[x][y] *= Math.max(0, ((Math.min(y, mapSize - x) - minimumEdge)) * borderNormalize);
			} else if (mapSize - x <= borderCutoff + minimumEdge || mapSize - y <= borderCutoff + minimumEdge) {
				heightArray[x][y] *= Math.max(0, ((Math.min(mapSize - x, mapSize - y) - minimumEdge)) * borderNormalize);
			}
		}
	}


	int getMaxHeight() {
		return maxHeight;
	}

	int getMapSize() {
		return mapSize;
	}

	double getSingleDirt() {
		return singleDirt;
	}

	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	/** Digs at the (x,y) location until the water depth is met. Each iteration increases the radius of dirt that is dug. */
	void createPond(int ox, int oy, double water, int baseWidth, int slope) {
		if (water <= 0)
			water = 0;
		if (slope <= 0)
			slope = 1;
		int size = baseWidth-1;
		while (getHeight(ox, oy) > water) {
			double dig = slope*singleDirt;
			for (int x = ox-size; x <= ox+size; x++) {
				for (int y = oy-size; y <= oy+size; y++) {
					if (x < 0 || x >= mapSize || y < 0 || y >= mapSize || getHeight(x,y) < water) {
						continue;
					}
					if (Math.sqrt(Math.pow(x-ox, 2)+Math.pow(y-oy, 2)) <= size)
						setHeight(x,y,getHeight(x,y)-dig,false);
				}
			}
			size++;
		}
		for (int i=0; i<size; i++) {
			erodeArea(ox-size,oy-size,size*2+1,0,slope,slope);
		}

	}
}
