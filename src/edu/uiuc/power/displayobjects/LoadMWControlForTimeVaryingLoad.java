package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.uiuc.power.dataobjects.LinearVaryingLoadData;
import edu.uiuc.power.dataobjects.LoadData;

public class LoadMWControlForTimeVaryingLoad extends LoadMWControl {
	
	LinearVaryingLoadData varyingLoad;
	
	public LoadMWControlForTimeVaryingLoad(Point2D ULCorner, int textsize, Color textcolor, double rotationAngle,
			LoadData load, double deltaPerMouseClick, LinearVaryingLoadData varyingLoad) {
		super(ULCorner,textsize,textcolor,rotationAngle,load,deltaPerMouseClick);
		this.varyingLoad = varyingLoad;
	}

	@Override
	protected boolean displayControlArrows() {
		// TODO Auto-generated method stub
		return varyingLoad.getPaused();
	}
	
	
}
