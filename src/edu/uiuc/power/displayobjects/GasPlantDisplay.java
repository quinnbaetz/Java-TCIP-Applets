package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;
import java.io.IOException;

import edu.uiuc.power.dataobjects.GeneratorData;

public class GasPlantDisplay extends GenericPlantDisplay {
	public GasPlantDisplay(Point2D ULCorner, double deltaPerMouseClick,
			GeneratorData gendata) throws IOException {
		super(ULCorner,deltaPerMouseClick,gendata);
	}

	@Override
	public String getCaption() {
		return "Natural Gas";
	}

	@Override
	public String getPathToImage() {
		return "/applet_images/coal_plant.png";
	}
	
	
}
