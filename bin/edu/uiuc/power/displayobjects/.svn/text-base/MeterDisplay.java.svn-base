package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

public class MeterDisplay implements Animatable, Renderable {

	Point2D _ULPos;
	
	double _offset;
	
	double dialradius = 50;
	double hashradius = 40;
	double meterCircleRadius = 70;
	double anglePerHash = Math.PI/4;
	double _offsetPerAnimationStep = 0.1;
	
	public MeterDisplay(Point2D ULPos) {
		_ULPos = ULPos;
		_offset = 0;
	}
	
	public double getOffsetPerAnimationStep() {
		return _offsetPerAnimationStep;
	}
	
	public void setOffsetPerAnimationStep(double offsetPerAnimationStep) {
		_offsetPerAnimationStep = offsetPerAnimationStep;
	}
	
	public double getOffset() {
		return _offset;
	}
	
	public void setOffset(double offset) {
		_offset = offset % 1.0;
	}
	
	public Point2D getULPos() {
		return _ULPos;
	}
	
	public void setULPos(Point2D ULPos) {
		_ULPos = ULPos;
	}
	
	public boolean animationstep() {
		setOffset(getOffset() - getOffsetPerAnimationStep());
		return true;
	}
	
	protected String getMessageTextTop() {
		return "";
	}
	
	protected String getMessageTextBottom() {
		return "";
	}

	public boolean render(Graphics2D g2d) {
		
		Color origColor = g2d.getColor();
		AffineTransform origXForm = g2d.getTransform();
		Shape origClip = g2d.getClip();
		
		g2d.translate(getULPos().getX(),getULPos().getY());
		g2d.scale(0.75, 0.75);
		
		g2d.setColor(new Color(0.9f,0.9f,0.9f));
		g2d.fill(new Ellipse2D.Double(0,0,meterCircleRadius*2,meterCircleRadius*2));
		
		String text = getMessageTextTop();
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 18));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(meterCircleRadius-(textwidth/2), meterCircleRadius*0.75);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, 0);
		g2d.setTransform(beforeText);
		
		text = getMessageTextBottom();
		textbounds = fontnow.getStringBounds(text, frc);
		textwidth = textbounds.getWidth();
		beforeText = g2d.getTransform();
		g2d.translate(meterCircleRadius-(textwidth/2), meterCircleRadius*1.5);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, 0);
		
		g2d.setTransform(beforeText);
		g2d.setFont(font);
		
		g2d.setColor(Color.BLACK);
		g2d.draw(new Ellipse2D.Double(0,0,meterCircleRadius*2,meterCircleRadius*2));
		
		g2d.translate(meterCircleRadius-dialradius,meterCircleRadius-(dialradius*0.25));
		g2d.scale(1,0.25);
		
		g2d.setColor(Color.BLACK);
		g2d.fill(new Rectangle2D.Double(-dialradius*0.1,dialradius*0.75,2*dialradius + dialradius*0.2,dialradius*0.5));
		
		g2d.clip(new Rectangle2D.Double(0,dialradius,2*dialradius,dialradius*1.1));
		
		// draw dial
		g2d.setColor(Color.WHITE);
		g2d.fill(new java.awt.geom.Ellipse2D.Double(0,0,dialradius*2,dialradius*2));

		g2d.setColor(Color.BLACK);
		g2d.draw(new java.awt.geom.Ellipse2D.Double(0,0,dialradius*2,dialradius*2));
		
		long numOfHashes = Math.round(Math.floor(Math.PI*2/anglePerHash));
		
		// draw hash marks
		g2d.translate(dialradius, dialradius);
		for (int i = 0; i < numOfHashes; i++) {
			double angle = i*anglePerHash + getOffset()*(anglePerHash);
			Point2D.Double fromPoint = new Point2D.Double(hashradius*Math.cos(angle),hashradius*Math.sin(angle));
			Point2D.Double toPoint = new Point2D.Double(dialradius*Math.cos(angle),dialradius*Math.sin(angle));
			g2d.draw(new java.awt.geom.Line2D.Double(fromPoint,toPoint));
		}
		
		g2d.setColor(origColor);
		g2d.setTransform(origXForm);
		g2d.setClip(origClip);
		
		
		return true;
	}

}
