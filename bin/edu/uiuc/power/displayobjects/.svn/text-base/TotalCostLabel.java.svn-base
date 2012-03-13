package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.CumulativeTimeProvider;

public class TotalCostLabel extends DataLabel {

//	@Override
//	protected boolean drawBorder() {
//		return true;
//	}

	@Override
	protected Color getBorderColor() {
		return Color.BLACK;
	}

	TotalCostProvider costprovider;
	DecimalFormat costFormat = new DecimalFormat("$#,##0");
	NumberFormatter costFormatter = new NumberFormatter(costFormat);
	
	public TotalCostLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, TotalCostProvider costprovider) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.costprovider = costprovider;
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.RIGHT;
	}

	@Override
	public String getCaption() {
		try {
			return "Total Costs: " + costFormatter.valueToString(costprovider.getTotalCost()) + "/hr";
		} 
		catch (ParseException pe) {
			System.out.println(pe.toString());
			return "";
		}
	}

	@Override
	protected int getFontStyle() {
		return Font.BOLD;
	}

}
