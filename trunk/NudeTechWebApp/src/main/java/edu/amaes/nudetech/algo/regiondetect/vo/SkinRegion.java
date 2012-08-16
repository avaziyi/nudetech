package edu.amaes.nudetech.algo.regiondetect.vo;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class SkinRegion {

	private int regionLabel;
	private int numberOfPixels = 0;
	private double xCenter = Double.NaN;
	private double yCenter = Double.NaN;
	private int leftMostPixel = Integer.MAX_VALUE;
	private int rightMostPixel = -1;
	private int topMostPixel = Integer.MAX_VALUE;
	private int bottomMostPixel = -1;

	private int xSum = 0;
	private int ySum = 0;
	private int x2Sum = 0;
	private int y2Sum = 0;

	public SkinRegion(int label) {
		this.regionLabel = label;
	}

	public int getRegionLabel() {
		return this.regionLabel;
	}

	public int getNumberOfPixels() {
		return this.numberOfPixels;
	}

	public int getLeftMostPixel() {
		return leftMostPixel;
	}

	public int getRightMostPixel() {
		return rightMostPixel;
	}

	public int getTopMostPixel() {
		return topMostPixel;
	}

	public int getBottomMostPixel() {
		return bottomMostPixel;
	}

	public Rectangle getBoundingBox() {
		if (leftMostPixel == Integer.MAX_VALUE) {
			return null;
		} else {
			return new Rectangle(leftMostPixel, topMostPixel, rightMostPixel
					- leftMostPixel + 1, bottomMostPixel - topMostPixel + 1);
		}
	}

	public Point2D.Double getCenter() {
		if (Double.isNaN(xCenter)) {
			return null;
		} else {
			return new Point2D.Double(xCenter, yCenter);
		}
	}

	/**
	 * Use this method to add a single pixel to this region. Updates summation
	 * and boundary variables used to calculate various region statistics.
	 * 
	 * @param pixelX
	 * @param pixelY
	 */
	public void addPixel(int pixelX, int pixelY) {
		numberOfPixels = numberOfPixels + 1;
		xSum = xSum + pixelX;
		ySum = ySum + pixelY;
		x2Sum = x2Sum + pixelX * pixelX;
		y2Sum = y2Sum + pixelY * pixelY;

		if (pixelX < leftMostPixel) {
			leftMostPixel = pixelX;
		}
		if (pixelY < topMostPixel) {
			topMostPixel = pixelY;
		}
		if (pixelX > rightMostPixel) {
			rightMostPixel = pixelX;
		}
		if (pixelY > bottomMostPixel) {
			bottomMostPixel = pixelY;
		}
	}

	public void updateRegionStatistics() {
		if (numberOfPixels > 0) {
			xCenter = (double) xSum / numberOfPixels;
			yCenter = (double) ySum / numberOfPixels;
		}
	}

	public String toString() {
		return "Region: " + regionLabel + " / Pixels: " + numberOfPixels
				+ " / Bounding Box: (" + leftMostPixel + "," + topMostPixel
				+ "," + rightMostPixel + "," + bottomMostPixel + ")"
				+ " / Center: (" + truncateValue(xCenter, 2) + ","
				+ truncateValue(yCenter, 2) + ")";
	}

	private String truncateValue(double d, int precision) {
		double m = Math.pow(10, precision);
		long k = Math.round(d * m);
		return String.valueOf(k / m);
	}
}
