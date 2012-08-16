package edu.amaes.nudetech.algo.video.frameextract.utilities;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class VideoExtractProperties {
	private static final String BUNDLE_NAME = "video_extraction";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private VideoExtractProperties() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
