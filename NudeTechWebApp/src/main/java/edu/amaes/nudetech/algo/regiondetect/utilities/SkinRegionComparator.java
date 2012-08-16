package edu.amaes.nudetech.algo.regiondetect.utilities;

import java.util.Comparator;

import edu.amaes.nudetech.algo.regiondetect.vo.SkinRegion;

public class SkinRegionComparator implements Comparator<SkinRegion> {

	public int compare(SkinRegion skinRegion1, SkinRegion skinRegion2) {
		
		if (skinRegion1 == null || skinRegion2 == null) {
			throw new IllegalArgumentException("SkinRegion cannot be null");
		}
		
		int skinRegion1Pixels = skinRegion1.getNumberOfPixels();
		int skinRegion2Pixels = skinRegion2.getNumberOfPixels();
		
		if(skinRegion1Pixels > skinRegion2Pixels)
            return -1;
        else if(skinRegion1Pixels < skinRegion2Pixels)
            return 1;
        else
            return 0;
	}
}
