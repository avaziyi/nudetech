package edu.amaes.nudetech.algo.regiondetect.labeling;

import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.LinkedList;

public class BreadthFirstLabeling extends FloodFillLabeling {

	private static final int LIMIT = Integer.MAX_VALUE;

	public BreadthFirstLabeling(ImageProcessor imgProcessor) {
		super(imgProcessor);
	}

	protected void floodFill(int x, int y, int label) {
		int maxDepth = 0;
		LinkedList<Point> queue = new LinkedList<Point>();
		int counter = 0;
		queue.addFirst(new Point(x, y));
		while (!queue.isEmpty() && counter < LIMIT) {
			
			int k = queue.size(); // for logging only - remove for efficiency!
			if (k > maxDepth)
				maxDepth = k;
			
			Point point = queue.removeLast();
			
			int u = point.x;
			int v = point.y;
			
			if ((u >= 0) && (u < width) && (v >= 0) && (v < height)
					&& isForeground(u, v)) {
				counter++;
				setLabel(u, v, label);
				queue.addFirst(new Point(u + 1, v));
				queue.addFirst(new Point(u, v + 1));
				queue.addFirst(new Point(u, v - 1));
				queue.addFirst(new Point(u - 1, v));
			}
		}
	}

}
