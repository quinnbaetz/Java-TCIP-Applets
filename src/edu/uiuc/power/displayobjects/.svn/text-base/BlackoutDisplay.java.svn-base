package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import edu.uiuc.power.dataobjects.PowerSystem;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class BlackoutDisplay implements Renderable {
	
	class BlackoutLabel extends DataLabel {

		public BlackoutLabel(Point2D.Double textBoxLocation) {
			super(textBoxLocation, 40, Color.WHITE, 0);
		}

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
		protected Color getBorderColor() {
			return Color.WHITE;
		}

		@Override
		public String getCaption() {
			return "Blackout (Time Paused)";
		}

		@Override
		protected Color getBackgroundColor() {
			return Color.BLACK;
		}

		@Override
		protected double getBorderThickness() {
			return 10;
		}
		
		
		
	}
	
	BlackoutLabel mylabel;
	
	PowerSystem psystem;
	
	public BlackoutDisplay(PowerSystem psystem, Point2D.Double textBoxLocation) {
		super();
		this.psystem = psystem;
		mylabel = new BlackoutLabel(textBoxLocation);
	}
	
	public BlackoutDisplay(PowerSystem psystem) {
		this(psystem,new Point2D.Double(300,450));		
	}

	public boolean render(Graphics2D g2d) {
		if (psystem.getSystemBlackout()) {
			Color backup = g2d.getColor();
			g2d.setColor(new Color(0,0,0,150));
			g2d.fillRect(0, 0, 5000, 5000);
			g2d.setColor(backup);
			mylabel.render(g2d);
			return true;
		} else 
			return false;
	}
}
