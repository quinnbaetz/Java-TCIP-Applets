package edu.uiuc.power.displayobjects.loads;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;

import edu.uiuc.power.dataobjects.LinearVaryingLoadData;
import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.LoadMWControl;
import edu.uiuc.power.displayobjects.LoadMWControlForTimeVaryingLoad;

public class CityDisplayForTimeVaryingLoad extends CityDisplay {
	LinearVaryingLoadData varyingLoad;
	
    public CityDisplayForTimeVaryingLoad(Point2D ULCorner, CityType ctype,
    		String caption,
    		LinearVaryingLoadData varyingLoad) throws IOException {
    	super(ULCorner,ctype,caption,varyingLoad);
    	this.varyingLoad = varyingLoad;
    }

	@Override
	protected LoadMWControl createLoadMWControl() {
		// TODO Auto-generated method stub
		return new LoadMWControlForTimeVaryingLoad(new Point2D.Double( transparent.getWidth()/2,transparent.getHeight()),14,Color.BLACK,0,getLoadData(),50.0,varyingLoad);
	}
}
