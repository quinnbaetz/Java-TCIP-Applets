package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;

public class BasicTextLabel extends DataLabel {

	String text;
	DataLabelHorizontalAlignment halignment; 
	DataLabelVerticalAlignment valignment;
	
	public BasicTextLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, String text, DataLabelHorizontalAlignment halignment) {
		this(ULCorner, textsize, textcolor, rotationAngle,text,halignment,DataLabelVerticalAlignment.TOP);
	}
	
	public BasicTextLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, String text, DataLabelHorizontalAlignment halignment,
			DataLabelVerticalAlignment valignment) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.text = text;
		this.halignment = halignment;
		this.valignment = valignment;
	}	

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return halignment;
	}
	
	public void setCaption(String text) {
		this.text = text;
	}

	@Override
	public String getCaption() {
		return text;
	}

	@Override
	public DataLabelVerticalAlignment getVerticalAlignment() {
		return valignment;
	}

	@Override
	protected boolean useTextLayoutToDetermineTextDimensions() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
