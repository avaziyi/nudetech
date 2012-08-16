package edu.amaes.nudetech.algo.skindetect.classifiers;

import edu.amaes.nudetech.algo.skindetect.utilities.ColorUtil;

public class RGBClassifier implements SkinColorClassifier {
	
	private static final int RED = 0;
	
	private static final int GREEN = 1;
	
	private static final int BLUE = 2;

	/*
	 * (non-Javadoc)
	 * @see edu.amaes.nudetech.algo.skindetect.classifiers.SkinColorClassifier#isSkinPixel(int)
	 */
	public boolean isSkinPixel(int pixel) {
		int[] rgbValues = ColorUtil.getRGBValues(pixel);

		return ((rgbValues[RED] > 95)
				&& (rgbValues[GREEN] > 40)
				&& (rgbValues[BLUE] > 20)
				&& ((Math.max(rgbValues[RED],
						Math.max(rgbValues[GREEN], rgbValues[BLUE])) - Math.min(
						rgbValues[RED],
						Math.min(rgbValues[GREEN], rgbValues[BLUE]))) > 15)
				&& (Math.abs(rgbValues[RED] - rgbValues[GREEN]) > 15)
				&& (rgbValues[RED] > rgbValues[GREEN]) && (rgbValues[RED] > rgbValues[BLUE]));
	}
}
