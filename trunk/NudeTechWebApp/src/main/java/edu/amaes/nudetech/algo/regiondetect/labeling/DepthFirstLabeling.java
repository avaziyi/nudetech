package edu.amaes.nudetech.algo.regiondetect.labeling;

import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.Stack;

public class DepthFirstLabeling extends FloodFillLabeling {
	
	private static final int LIMIT = Integer.MAX_VALUE;  
	
	public DepthFirstLabeling(ImageProcessor imgProcessor) {
		super(imgProcessor);
	}

	protected void floodFill(int x, int y, int label) {
		//stack contains pixel coordinates
		int max_depth = 0;
		Stack<Point> s = new Stack<Point>();
		s.push(new Point(x,y));
		int ctr = 0; 
		while (!s.isEmpty() && ctr < LIMIT){
			int k = s.size();	// for logging only - remove for efficiency!
			if (k>max_depth)
				max_depth = k;
			Point n = s.pop();
			int u = n.x;
			int v = n.y;
			if ((u>=0) && (u<width) && (v>=0) && (v<height) && isForeground(u,v)) {
				ctr ++;
				setLabel(u, v, label);
				s.push(new Point(u+1,v));
				s.push(new Point(u,v+1));
				s.push(new Point(u,v-1));
				s.push(new Point(u-1,v));
			}
		}
	}

}
