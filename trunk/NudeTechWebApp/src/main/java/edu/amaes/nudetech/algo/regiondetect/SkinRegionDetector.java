package edu.amaes.nudetech.algo.regiondetect;

import ij.ImagePlus;

import java.util.List;

import edu.amaes.nudetech.algo.regiondetect.vo.SkinRegion;


public interface SkinRegionDetector {
	
    List<SkinRegion> detectSkinRegions(ImagePlus image);
}
