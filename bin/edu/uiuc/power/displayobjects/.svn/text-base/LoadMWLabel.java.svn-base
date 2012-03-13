package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.text.ParseException;

import edu.uiuc.power.dataobjects.LoadData;

public class LoadMWLabel extends DataLabel {

	private LoadData _loaddata;
	
	public LoadMWLabel(Point2D ULCorner, int textsize, Color textcolor, double rotationAngle,
			LoadData loadData) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		_loaddata = loadData;
	}

	@Override
	protected int getFontStyle() {
		if (!getLoadData().getConnected())
			return Font.BOLD;
		else
			return super.getFontStyle();
	}

	public LoadData getLoadData() {
		return _loaddata;
	}
	
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.CENTER;
	}

	@Override
	public String getCaption() {
		if (getLoadData().getConnected()) {
			try {
				return textFormatter.valueToString(getLoadData().getMWSupplied()) + " MW";
			} catch (ParseException pe) {
				return pe.toString();
			}
		} else {
			return "BLACKOUT";
		}
	}
	
}
