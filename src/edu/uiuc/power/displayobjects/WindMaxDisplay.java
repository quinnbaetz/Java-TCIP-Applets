package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.ParseException;

import edu.uiuc.power.dataobjects.WindGeneratorData;

public class WindMaxDisplay extends DataLabel {
	WindGeneratorData windData;

	public WindMaxDisplay(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, WindGeneratorData windData) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.windData = windData;
		// TODO Auto-generated constructor stub
	}

	public String getCaption() {
		try {
			return "Maximum wind output available: " + textFormatter.valueToString(windData.getMaxMW()) + " MW";
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
