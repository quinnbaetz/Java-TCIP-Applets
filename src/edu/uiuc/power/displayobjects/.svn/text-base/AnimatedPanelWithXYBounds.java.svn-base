package edu.uiuc.power.displayobjects;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class AnimatedPanelWithXYBounds extends AnimatedPanel {

	private Point2D _ULCorner, _BRCorner;
	
	public AnimatedPanelWithXYBounds(
			int desiredWidth, 
			int desiredHeight, 
			long period, 
			int noDelaysPerYield, 
			boolean antiAliasOn,
			Point2D ULCorner,
			Point2D BRCorner
			) {
		super(desiredWidth, desiredHeight, period, noDelaysPerYield, antiAliasOn);
		_ULCorner = ULCorner;
		_BRCorner = BRCorner;
	}
	
	public Point2D getULCorner() {
		return _ULCorner;
	}
	
	public Point2D getBRCorner() {
		return _BRCorner;
	}

	public double getDisplayAreaWidth() {
		return this.getBRCorner().getX()-this.getULCorner().getX();
	}
	
	public double getDisplayAreaHeight() {
		return this.getBRCorner().getY()-this.getULCorner().getY();
	}
	
	@Override
	protected void performInitialTransform(Graphics2D g2d) {
		super.performInitialTransform(g2d);
		g2d.scale(this.getWidth()/getDisplayAreaWidth(), this.getHeight()/getDisplayAreaHeight());
		g2d.translate(-this.getULCorner().getX(),-this.getULCorner().getY());
		
		g2d.translate(getDisplayAreaWidth()/2,getDisplayAreaHeight()/2);
		g2d.scale(1,-1);
		g2d.translate(-getDisplayAreaWidth()/2,-getDisplayAreaHeight()/2);
		
		//g2d.scale(1,-1);		
	}

}
