package edu.amaes.nudetech.algo.regiondetect;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.List;

import edu.amaes.nudetech.algo.regiondetect.labeling.BreadthFirstLabeling;
import edu.amaes.nudetech.algo.regiondetect.labeling.RegionLabeling;
import edu.amaes.nudetech.algo.regiondetect.vo.SkinRegion;

public class BinaryImageSkinRegionDetector implements SkinRegionDetector {

	private RegionLabeling labeling;
	
	/*
	 * (non-Javadoc)
	 * @see edu.amaes.nudetech.algo.regiondetect.SkinRegionDetector#detectSkinRegions(ij.ImagePlus)
	 */
	public List<SkinRegion> detectSkinRegions(ImagePlus image) {
    	ImageProcessor imgProcessor = image.getProcessor();
    	
        labeling =  new BreadthFirstLabeling(imgProcessor);
        
        return labeling.getRegions();
    }
}
