package edu.uiuc.power.displayobjects.loads;

import java.util.ArrayList;

public class ImageCaptionMWDescription {

	private String imagePath;
	private String caption;
	private double MW;
	
	private ImageCaptionMWDescription() {
		
	}
	
	public double getMW() {
		return MW;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	@Override
	public String toString() {
		return caption;
	}

	public static ImageCaptionMWDescription getFridgeDescription() {
		ImageCaptionMWDescription retval = new ImageCaptionMWDescription();
		retval.imagePath = "applet_images/fridge.png";
		retval.caption = "Fridge";
		retval.MW = 700.0;
		return retval;
	}
	
	public static ImageCaptionMWDescription getDryerDescription() {
		ImageCaptionMWDescription retval = new ImageCaptionMWDescription();
		retval.imagePath = "applet_images/dryer.png";
		retval.caption = "Clothes Dryer";
		retval.MW = 2500.0;
		return retval;
	}
	
	public static ArrayList<ImageCaptionMWDescription> getAllPossibleLoadDescriptions() {
		ArrayList<ImageCaptionMWDescription> retval = new ArrayList<ImageCaptionMWDescription>();
		retval.add(getFridgeDescription());
		retval.add(getDryerDescription());
		return retval;
	}
}
