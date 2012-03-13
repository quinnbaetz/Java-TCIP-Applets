package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class SimpleLineDisplay implements Renderable, LineDisplay { 
	private Point2D.Double from;
	private Point2D.Double to;
	private double _thickness;
	
	public Point2D.Double getFromPoint() {
		return from;
	}
	
	public Point2D.Double getToPoint() {
		return to;
	}
	
	public SimpleLineDisplay(Point2D.Double start_in, Point2D.Double end_in, 
			double thickness) {
		from = start_in;
		to = end_in;
		_thickness = thickness;
	}
	
	public double getLength() {
		return DrawUtilities.getDistanceBetweenTwoPoints(from, to);
	}
	
	public double getThickness() {
		return _thickness;
	}
	
	public void setThickness(double thickness) {
		_thickness = thickness;
	}
	
	protected Color getColor() {
		return Color.BLACK;
	}

	public boolean render(Graphics2D g2d) {
		Color backupColor = g2d.getColor();
		g2d.setColor(getColor());
		Stroke origStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke((float)getThickness()));
		g2d.draw(new Line2D.Double(from, to));
		g2d.setStroke(origStroke);
		g2d.setColor(backupColor);
		return true;
	}
	
	
}
