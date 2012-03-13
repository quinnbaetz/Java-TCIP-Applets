package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;

public interface LineDisplay {
	public Point2D.Double getFromPoint();
	public Point2D.Double getToPoint();
	public double getLength();
}
