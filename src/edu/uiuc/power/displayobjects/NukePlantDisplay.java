package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;
import java.io.IOException;

import edu.uiuc.power.dataobjects.GeneratorData;

public class NukePlantDisplay extends GenericPlantDisplay {
	public NukePlantDisplay(Point2D ULCorner, double deltaPerMouseClick,
			GeneratorData gendata) throws IOException {
		super(ULCorner, deltaPerMouseClick, gendata);
	}

	@Override
	public String getCaption() {
		return "Nuclear";
	}

	@Override
	public String getPathToImage() {
		return "/applet_images/nuke_plant.png";
	}

	@Override
	public double getCaptionYOffset() {
		return 220;
	}
	
	
}
