package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;
import java.io.IOException;

import edu.uiuc.power.dataobjects.GeneratorData;

public class CoalPlantDisplay extends GenericPlantDisplay {
	int plantNumber;
	
	public CoalPlantDisplay(Point2D ULCorner, int PlantNumber, double deltaPerMouseClick,
			GeneratorData gendata) throws IOException {
		super(ULCorner,deltaPerMouseClick,gendata);
		plantNumber = PlantNumber;
	}

	@Override
	public String getCaption() {
		if (plantNumber >= 1)
			return "Coal " + plantNumber;
		else
			return "Coal";
	}

	@Override
	public String getPathToImage() {
		return "/applet_images/coal_plant.png";
	}
	
	
}
