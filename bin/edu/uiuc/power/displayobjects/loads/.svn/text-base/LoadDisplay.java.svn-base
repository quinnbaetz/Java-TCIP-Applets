package edu.uiuc.power.displayobjects.loads;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.Animatable;
import edu.uiuc.power.displayobjects.Renderable;

public abstract class LoadDisplay implements Renderable, Animatable {

	public enum LoadDisplayAlignmentVertical {
		CENTER, TOP, BOTTOM
	}
	
	public enum LoadDisplayAlignmentHorizontal {
		CENTER, LEFT, RIGHT
	}
	
	LoadData loaddata;
	Point2D location; 
	LoadDisplayAlignmentVertical vertAlign; 
	LoadDisplayAlignmentHorizontal horizAlign;	
	String loadDescription;
	
	public LoadDisplay(LoadData loaddata, Point2D location, 
			LoadDisplayAlignmentVertical vertAlign, 
			LoadDisplayAlignmentHorizontal horizAlign, String loadDescription) {
		this.loaddata = loaddata;
		this.location = location;
		this.vertAlign = vertAlign;
		this.horizAlign = horizAlign;
		this.loadDescription = loadDescription;
	}
	
	public String getDescription() {
		return loadDescription;
	}
	
	public void setDescription(String loadDescription) {
		this.loadDescription = loadDescription;
	}
	
	public abstract boolean render(Graphics2D g2d);
	
	protected void translateToULCorner(Graphics2D g2d) {
		g2d.translate(location.getX(), location.getY());
		switch (vertAlign) {
			case BOTTOM:
				g2d.translate(0, -getHeight());
				break;
			case CENTER:
				g2d.translate(0, -getHeight()/2);
				break;
			case TOP:
				g2d.translate(0, 0);
				break;
		}
		switch (horizAlign) {
			case LEFT:
				g2d.translate(0, 0);
				break;
			case CENTER:
				g2d.translate(-getWidth()/2,0);
				break;
			case RIGHT:
				g2d.translate(-getWidth(), 0);
				break;
		}
	}
	
	public abstract double getHeight();
	
	public LoadDisplayAlignmentHorizontal getHorizAlign() {
		return horizAlign;
	}

	public void setHorizAlign(LoadDisplayAlignmentHorizontal horizAlign) {
		this.horizAlign = horizAlign;
	}

	public LoadData getLoadData() {
		return loaddata;
	}

	public void setLoadData(LoadData loaddata) {
		this.loaddata = loaddata;
	}

	public Point2D getLocation() {
		return location;
	}

	public void setLocation(Point2D location) {
		this.location = location;
	}

	public LoadDisplayAlignmentVertical getVertAlign() {
		return vertAlign;
	}

	public void setVertAlign(LoadDisplayAlignmentVertical vertAlign) {
		this.vertAlign = vertAlign;
	}

	public abstract double getWidth();

	@Override
	public String toString() {
		return getDescription();
	}

}
