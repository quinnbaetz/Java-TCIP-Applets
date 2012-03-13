package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

public class MouseCoordinateLabel extends DataLabel implements java.awt.event.MouseMotionListener {

	public MouseCoordinateLabel(Point2D ULCorner, int textsize,
			Color textcolor, double rotationAngle) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		// TODO Auto-generated constructor stub
	}
	
	double currentX;
	double currentY;

	@Override
	public String getCaption() {
		// TODO Auto-generated method stub
		return "(" + currentX + "," + currentY + ")";
	}

	
	
	@Override
	protected boolean drawBackground() {
		return true;
	}





	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent e) {
		currentX = e.getX();
		currentY = e.getY();
	}
}
