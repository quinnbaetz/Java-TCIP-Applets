package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Connector implements Renderable {

	Point2D _CenterLocation;
	double _width, _height;
	Color _outlineColorConnected, _fillColorConnected, 
		_outlineColorDisconnected, _fillColorDisconnected;
	protected AffineTransform renderxform;
	
	public Connector(Point2D CenterLocation, double width, double height,
			Color outlineColorConnected, Color fillColorConnected,
			Color outlineColorDisconnected, Color fillColorDisconnected) {
		_outlineColorConnected = outlineColorConnected;
		_fillColorConnected = fillColorConnected;
		_outlineColorDisconnected = outlineColorDisconnected;
		_fillColorDisconnected = fillColorDisconnected;
		_CenterLocation = CenterLocation;
		_width = width;
		_height = height;
	}
	
	public boolean getConnected() {
		return true;
	}
	
	public Point2D getCenterLocation() {
		return _CenterLocation;
	}
	
	public double getWidth() {
		return _width;
	}
	
	public double getHeight() {
		return _height;
	}
	
	public Color getOutlineColorConnected() {
		return _outlineColorConnected;
	}
	
	public Color getFillColorConnected() {
		return _fillColorConnected;
	}
	
	public Color getOutlineColorDisconnected() {
		return _outlineColorDisconnected;
	}
	
	public Color getFillColorDisconnected() {
		return _fillColorDisconnected;
	}
	
	public boolean render(Graphics2D g2d) {
		AffineTransform beforeXForm = g2d.getTransform();
		
		g2d.translate(getCenterLocation().getX(), getCenterLocation().getY());
		
		renderxform = g2d.getTransform();
		
		//g2d.setTransform(renderxform);
		
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(0.1f));
		
		Rectangle2D connector = new Rectangle2D.Double(-getWidth()/2,-getHeight()/2,getWidth(),getHeight()); 
		if (getConnected()) {
			g2d.setColor(getFillColorConnected());
			g2d.fill(connector);
			g2d.setColor(getOutlineColorConnected());
			g2d.draw(connector);
		} 
		else {
			g2d.setColor(getFillColorDisconnected());
			g2d.fill(connector);
			g2d.setColor(getOutlineColorDisconnected());
			g2d.draw(connector);
		}
		
		g2d.setStroke(oldStroke);
		g2d.setTransform(beforeXForm);
		return true;
	}

}
