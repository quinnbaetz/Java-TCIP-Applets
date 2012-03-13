package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.ParseException;

import edu.uiuc.power.dataobjects.WindGeneratorData;

public class WindCurtailedDisplay extends DataLabel {
	WindGeneratorData windData;

	public WindCurtailedDisplay(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, WindGeneratorData windData) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.windData = windData;
		// TODO Auto-generated constructor stub
	}

	public String getCaption() {
		try {
			double curtailVal = windData.getMaxMW() - windData.getMW();
			curtailVal = (curtailVal > 1 ? curtailVal : 0);
			//curtailVal = Math.floor(curtailVal);
			return "Wind output curtailed: " + textFormatter.valueToString(curtailVal) + " MW";
		} catch (ParseException e) {
			return e.toString();
		}
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		// TODO Auto-generated method stub
		return DataLabelHorizontalAlignment.LEFT;
	}
}
