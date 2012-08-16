package edu.amaes.nudetech;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.amaes.nudetech.algo.nuditydetect.ColoredImageNudityDetector;
import edu.amaes.nudetech.algo.nuditydetect.ImageNudityDetector;
import edu.amaes.nudetech.algo.video.frameextract.VideoFrameExtractor;
import edu.amaes.nudetech.algo.video.frameextract.VideoFrameExtractorImpl;

public class App {

	public static void main(String[] args) {
		App app = new App();
		app.detectNudity();
	}

	public void detectNudity() {

		try {
			VideoFrameExtractor frameExtractor = new VideoFrameExtractorImpl();
			frameExtractor.extractFrames("C:/samples/nudeart.flv", "C:/samples/nudeart/");
			
			
			File folder = new File("C:/samples/nudeart/");
			File[] files = folder.listFiles();
			
			for (File file: files) {
				Image inputImage = ImageIO.read(file);
				
                                ImageNudityDetector nudityDetector = new ColoredImageNudityDetector();  
				boolean isNude = nudityDetector.isImageNude(inputImage);
				
				System.out.println("file: " + file.getName() + ", isNude: " + isNude);
			}
			
		} catch (IOException e) {

		}

	}
}
