package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class CircuitBreakerBoxDisplay implements Renderable {
	
	Point2D.Double _ulpos;
	double _height;
	double _width;
	
	public CircuitBreakerBoxDisplay(Point2D.Double ulpos,
			double height, double width) {
		_height = height;
		_width = width;
		_ulpos = ulpos;
	}

	public Point2D.Double getULPos() {
		return _ulpos;
	}
	
	public double getheight() {
		return _height;
	}
	
	public double getwidth() {
		return _width;
	}
	
	public boolean render(Graphics2D g2d) {
		AffineTransform currentxform = g2d.getTransform();
		Paint currentpaint = g2d.getPaint();
		
		g2d.translate(getULPos().getX(), getULPos().getY());
		g2d.scale(getwidth()/100.0, getheight()/150.0);
		
		RoundRectangle2D outerRect = new RoundRectangle2D.Double(0,0,100,150,10.0,10.0);
		g2d.setPaint(new Color(200,200,200));
		g2d.fill(outerRect);
		g2d.setPaint(Color.BLACK);
		g2d.draw(outerRect);
		
		RoundRectangle2D innerRect = new RoundRectangle2D.Double(15,15,70,120,10.0,10.0);
		g2d.setPaint(new Color(200,200,200));
		g2d.fill(innerRect);
		g2d.setPaint(Color.BLACK);
		g2d.draw(innerRect);
		
		Rectangle2D latch = new Rectangle2D.Double(60,70,20,10);
		g2d.setPaint(Color.BLACK);
		g2d.fill(latch);
		
		g2d.setTransform(currentxform);
		g2d.translate(getULPos().getX(), getULPos().getY());
		
		String text = "Circuit breaker box";
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate((getwidth()/2)-(textwidth/2),getheight()+textbounds.getHeight());
		g2d.setColor(Color.BLACK);
		//g2d.scale(100.0/getwidth(), 150.0/getheight());
		g2d.drawString(text, 0, 0);
		g2d.setTransform(beforeText);
		g2d.setFont(font);		
		
		g2d.setPaint(currentpaint);
		g2d.setTransform(currentxform);
		return true;
	}

}
