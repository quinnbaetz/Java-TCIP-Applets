package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class TextLabel extends DataLabel {

	String _labelText;
	boolean vertFlip = true;
	
	public void setVerticalFlip(boolean vertFlip) {
		this.vertFlip = vertFlip;
	}
	
	public TextLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle){
		super( ULCorner,  textsize,  textcolor, rotationAngle);
	}

	public void setLabelText(String labelText){
		_labelText = labelText;
	}
	
	@Override
	protected boolean getVerticalFlip() {
		return vertFlip;
	}
	
	@Override
	public String getCaption() {
		return _labelText;
	}
	
}
