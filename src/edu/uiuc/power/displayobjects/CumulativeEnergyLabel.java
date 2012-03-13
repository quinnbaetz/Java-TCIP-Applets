package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class CumulativeEnergyLabel extends DataLabel {

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.RIGHT;
	}

	String prefix;
	String suffix;
	NumberFormatter textFormatter;
	Lesson1CumulativeEnergyChartPanel cumulativePlot;
	double valueMultiplier;
	public static String newline = System.getProperty("line.separator");
	
	public CumulativeEnergyLabel(Point2D ULCorner, int textsize, Color textcolor, double rotationAngle,
			String prefix, String suffix, double valueMultiplier, Lesson1CumulativeEnergyChartPanel cumulativePlot) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.prefix = prefix;
		this.suffix = suffix;
		this.valueMultiplier = valueMultiplier;
		this.cumulativePlot = cumulativePlot;
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		textFormatter = new NumberFormatter(decimalFormat);
	}

	@Override
	public String getCaption() {
		try {
			return prefix + textFormatter.valueToString(cumulativePlot.getCurrentSum()*valueMultiplier) + suffix;
		} catch (ParseException pe) {
			return "Error reading current energy sum";
		}
	}

}
