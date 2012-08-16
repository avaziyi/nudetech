package edu.amaes.nudetech.algo.skindetect.classifiers;

import edu.amaes.nudetech.algo.skindetect.utilities.ColorUtil;

public class HSVClassifier implements SkinColorClassifier {
	
	private static final int HUE = 0;
	
	private static final int SATURATION = 1;

	/* (non-Javadoc)
	 * @see edu.amaes.nudetech.algo.skindetect.SkinColorClassifier#isSkinPixel(int)
	 */
	public boolean isSkinPixel(int pixel) {
		float[] hsvValues = ColorUtil.getHSVValues(pixel);
		
		return (hsvValues[HUE] >= 0 
				&& hsvValues[HUE] <= 50 
				&& hsvValues[SATURATION] >= 0.23 
				&& hsvValues[SATURATION] <= 0.68);
	}
}
