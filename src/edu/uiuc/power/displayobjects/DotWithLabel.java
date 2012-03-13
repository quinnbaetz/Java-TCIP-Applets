package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class DotWithLabel implements Renderable {
	
	private class LabelForDot extends DataLabel {
		
		@Override
		protected boolean drawBackground() {
			return true;
		}

		@Override
		protected boolean drawBorder() {
			return true;
		}

		@Override
		public DataLabelHorizontalAlignment getHorizontalAlignment() {
			return DataLabelHorizontalAlignment.CENTER;
		}

		@Override
		protected Color getBackgroundColor() {
			return Color.WHITE;
		}

		@Override
		protected Color getBorderColor() {
			return Color.BLACK;
		}

		@Override
		protected double getBorderThickness() {
			return 1.0;
		}

		@Override
		public String getCaption() {
			return text;
		}

		public LabelForDot(Point2D ULCorner, int textsize, Color textcolor,
				double rotationAngle) {
			super(ULCorner, textsize, textcolor, rotationAngle);
			// TODO Auto-generated constructor stub
		}
		
	}

	double coordx, coordy;
	double radius;
	String text;
	LabelForDot myLabel;
	Color dotColor = Color.BLACK;
	
	public void setDotColor(Color dotColor) {
		this.dotColor = dotColor;
	}
	
	public DotWithLabel(double coordx, double coordy, double radius, String text) {
		super();
		this.coordx = coordx;
		this.coordy = coordy;
		this.radius = radius;
		this.text = text;
		if (text.length() != 0)
			myLabel = new LabelForDot(new Point2D.Double(0,0),10,Color.BLACK,0);
		else
			myLabel = null;
	}

	public boolean render(Graphics2D g2d) {
		AffineTransform origXForm = g2d.getTransform();
		Color backupColor = g2d.getColor();
		
		g2d.translate(coordx, coordy);
		g2d.setColor(dotColor);
		g2d.fill(new Ellipse2D.Double(-radius,-radius,2*radius,2*radius));
		
		g2d.translate(0,radius);
		if (myLabel != null)
			myLabel.render(g2d);
		
		g2d.setColor(backupColor);
		g2d.setTransform(origXForm);
		return true;
	}

}
