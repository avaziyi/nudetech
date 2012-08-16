package edu.amaes.nudetech.algo.regiondetect.labeling;

import ij.process.ImageProcessor;

public class RecursiveLabeling extends FloodFillLabeling {

	public RecursiveLabeling(ImageProcessor imgProcessor) {
		super(imgProcessor);
	}

	public void floodFill(int u, int v, int label) {
		if ((u>=0) && (u<width) && (v>=0) && (v<height) && isForeground(u,v)) {
			setLabel(u, v, label);
			floodFill(u+1,v,label);
			floodFill(u,v+1,label);
			floodFill(u,v-1,label);
			floodFill(u-1,v,label);
		}
	}
}
