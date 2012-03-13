package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MultilineDisplay implements Renderable {
	double coordX;
	double coordY;
	
	public MultilineDisplay(double coordX, double coordY) {
		super();
		this.coordX = coordX;
		this.coordY = coordY;
	}

	protected ArrayList<String> getStrings() {
		ArrayList<String> retvals = new ArrayList<String>();
		retvals.add("Test1");
		retvals.add("Test2");
		retvals.add("Test3");
		return retvals;
	}
	
	protected int getFontSize() {
		return 11;
	}
	
	protected int getFontStyle() {
		return Font.PLAIN;
	}	
	
	
	public boolean render(Graphics2D g2d) {
		ArrayList<String> stringList = getStrings();
		Font backupFont = g2d.getFont();
		AffineTransform backupXForm = g2d.getTransform();
		Color backupColor = g2d.getColor();
			
		g2d.setFont(new Font("Lucida Bright", getFontStyle(), getFontSize()));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		ArrayList<Rectangle2D> textBoundsList = new ArrayList<Rectangle2D>();
		
		double maxWidth = 0;
		double height = 0;
		double vertBuffer = -1;
		for (int i = 0; i < stringList.size(); i++) {
			Rectangle2D textBound = fontnow.getStringBounds(stringList.get(i), frc);
			textBoundsList.add(textBound);
			maxWidth = Math.max(maxWidth,textBound.getWidth());
			height += textBound.getHeight();
			if (i < stringList.size() - 1) 
				height += textBound.getHeight()*0.25;
		}
		
		g2d.translate(coordX,-height);
		g2d.setPaint(Color.WHITE);
		
		Shape outlineRect = new Rectangle2D.Double(0,0,maxWidth + 5,height + 5); 
		g2d.fill(outlineRect);
		g2d.setPaint(Color.BLACK);
		g2d.draw(outlineRect);
		g2d.translate(2.5,0);
		for (int i = 0; i < stringList.size(); i++) {
			Rectangle2D tBounds = textBoundsList.get(i);
			g2d.translate(0, tBounds.getHeight());
			g2d.drawString(stringList.get(i),0,0);
			g2d.translate(0,tBounds.getHeight()*0.25);
		}
		g2d.setTransform(backupXForm);
		g2d.setFont(backupFont);
		g2d.setColor(backupColor);
		return true;
	}	

}
