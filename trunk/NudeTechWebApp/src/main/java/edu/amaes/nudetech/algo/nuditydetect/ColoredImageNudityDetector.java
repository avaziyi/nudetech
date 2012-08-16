package edu.amaes.nudetech.algo.nuditydetect;

import ij.ImagePlus;

import java.awt.Image;
import java.util.Collections;
import java.util.List;

import edu.amaes.nudetech.algo.regiondetect.BinaryImageSkinRegionDetector;
import edu.amaes.nudetech.algo.regiondetect.SkinRegionDetector;
import edu.amaes.nudetech.algo.regiondetect.utilities.SkinRegionComparator;
import edu.amaes.nudetech.algo.regiondetect.vo.SkinRegion;
import edu.amaes.nudetech.algo.skindetect.BinaryImageSkinDetector;
import edu.amaes.nudetech.algo.skindetect.SkinDetector;
import edu.amaes.nudetech.algo.skindetect.vo.SkinDetectedImage;

public class ColoredImageNudityDetector implements ImageNudityDetector {

	private static final int PIXEL_COUNT_THRESHOLD = 10;

	private SkinDetector skinDetector;

	private SkinRegionDetector skinRegionDetector;

	public boolean isImageNude(Image inputImage) {
		ImagePlus image = new ImagePlus(inputImage.toString(), inputImage);

		skinDetector = new BinaryImageSkinDetector();
		SkinDetectedImage skinDetectedImage = skinDetector.detectSkin(image);

		skinRegionDetector = new BinaryImageSkinRegionDetector();
		List<SkinRegion> skinRegions = skinRegionDetector
				.detectSkinRegions(skinDetectedImage.getImage());

		filterOutSmallRegions(skinRegions);

		Collections.sort(skinRegions, new SkinRegionComparator());

		int skinRegionsCount = skinRegions.size();
		int totalPixels = image.getWidth() * image.getHeight();
		int skinPixelCount = skinDetectedImage.getSkinPixelCount();

		if (skinRegionsCount < 3) {
			return false;
		}

		double skinPixelPercentage = (skinPixelCount / (totalPixels * 1.0)) * 100;
		if (skinPixelPercentage < 15) {
			return false;
		}

		SkinRegion largestSkinRegion = skinRegions.get(0);
		SkinRegion secondLargestSkinRegion = skinRegions.get(1);
		SkinRegion thirdLargestSkinRegion = skinRegions.get(2);

		double skinPixelPercentLargest = (largestSkinRegion.getNumberOfPixels() / (skinPixelCount * 1.0)) * 100;
		double skinPixelPercent2ndLargest = (secondLargestSkinRegion
				.getNumberOfPixels() / (skinPixelCount * 1.0)) * 100;
		double skinPixelPercent3rdLargest = (thirdLargestSkinRegion
				.getNumberOfPixels() / (skinPixelCount * 1.0)) * 100;

		if (skinPixelPercentLargest < 35 && skinPixelPercent2ndLargest < 30
				&& skinPixelPercent3rdLargest < 30) {
			return false;
		}
		
		if (skinPixelPercentLargest < 45) {
			return false;
		}
		
		if (skinPixelCount < (0.3 * totalPixels)) {
			return false;
		}
		
		if (skinRegionsCount > 60) {
			return false;
		}

		return true;
	}

	private void filterOutSmallRegions(List<SkinRegion> skinRegions) {
		SkinRegion[] regionsArray = new SkinRegion[skinRegions.size()];

		regionsArray = skinRegions.toArray(regionsArray);

		for (SkinRegion skinRegion : regionsArray) {
			if (skinRegion.getNumberOfPixels() < PIXEL_COUNT_THRESHOLD) {
				skinRegions.remove(skinRegion);
			}
		}
	}

}
