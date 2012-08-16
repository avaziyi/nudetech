package edu.amaes.nudetech.algo.skindetect;

import ij.ImagePlus;
import edu.amaes.nudetech.algo.skindetect.vo.SkinDetectedImage;

public interface SkinDetector {

	SkinDetectedImage detectSkin(ImagePlus inputImage);
}
