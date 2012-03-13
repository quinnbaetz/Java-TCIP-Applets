package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.CumulativeTimeProvider;

public class TotalEmissionsLabel extends DataLabel {

//	@Override
//	protected boolean drawBorder() {
//		return true;
//	}

	@Override
	protected Color getBorderColor() {
		return Color.BLACK;
	}
	
	@Override
	protected int getFontStyle() {
		return Font.BOLD;
	}	

	TotalEmissionsProvider emissionsprovider;
	DecimalFormat emissionsFormat = new DecimalFormat("#,##0");
	NumberFormatter emissionsFormatter = new NumberFormatter(emissionsFormat);
	
	public TotalEmissionsLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, TotalEmissionsProvider emissionsprovider) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.emissionsprovider = emissionsprovider;
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.LEFT;
	}

	@Override
	public String getCaption() {
		try {
			return "Total Emissions: " + emissionsFormatter.valueToString(emissionsprovider.getTotalEmissions()) + "/hr";
		} 
		catch (ParseException pe) {
			System.out.println(pe.toString());
			return "";
		}
	}

}
