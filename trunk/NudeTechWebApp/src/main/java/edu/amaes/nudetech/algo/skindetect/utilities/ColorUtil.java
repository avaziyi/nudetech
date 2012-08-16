package edu.amaes.nudetech.algo.skindetect.utilities;

import java.awt.Color;

public class ColorUtil {
	
	private static final int RED = 0;
	
	private static final int GREEN = 1;
	
	private static final int BLUE = 2;

	public static int[] getRGBValues(int pixel) {
		int[] rgbValues = new int[3];

		Color color = new Color(pixel);

		rgbValues[RED] = color.getRed();
		rgbValues[GREEN] = color.getGreen();
		rgbValues[BLUE] = color.getBlue();

		return rgbValues;
	}

	public static float[] getNormalizedRGBValues(int pixel) {
		int[] rgbValues = getRGBValues(pixel);

		float[] normalizedRGBValues = new float[3];
		float sum = rgbValues[RED] + rgbValues[GREEN] + rgbValues[BLUE];
		
		normalizedRGBValues[RED] = rgbValues[RED] / sum;
		normalizedRGBValues[GREEN] = rgbValues[GREEN] / sum;
		normalizedRGBValues[BLUE] = rgbValues[BLUE] / sum;

		return normalizedRGBValues;
	}

	public static float[] getHSVValues(int pixel) {

		int[] rgbValues = getRGBValues(pixel);

		float[] hsvValues = new float[3];
		Color.RGBtoHSB(rgbValues[RED], rgbValues[GREEN], rgbValues[BLUE], hsvValues);

		return hsvValues;
	}
}
