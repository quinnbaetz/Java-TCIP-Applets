package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.CumulativeTimeProvider;

public class CumulativeTimeLabel extends DataLabel {

	CumulativeTimeProvider xyplot;
	NumberFormatter minuteFormatter;
	
	public CumulativeTimeLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, CumulativeTimeProvider xyplot) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.xyplot = xyplot;
		DecimalFormat decimalFormat = new DecimalFormat("00");
		minuteFormatter = new NumberFormatter(decimalFormat);
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.LEFT;
	}

	@Override
	public String getCaption() {
		return "Elapsed time (HH:MM): " + xyplot.getCumulativeTime();
	}

}
