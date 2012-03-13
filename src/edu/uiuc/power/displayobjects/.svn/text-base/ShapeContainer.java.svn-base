package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;

public class ShapeContainer implements Renderable {
	private Stroke strokeInfo;
	private Color strokeColor;
	private ArrayList<Shape> shapesToDraw = new ArrayList<Shape>();
	
	public ShapeContainer(Stroke strokeInfo, Color strokeColor) {
		this.strokeInfo = strokeInfo;
		this.strokeColor = strokeColor;
	}
	
	public boolean render(Graphics2D g2d) {
		Stroke backupStroke = g2d.getStroke();
		Color backupColor = g2d.getColor();
			g2d.setColor(strokeColor);
			for (int i = 0; i < shapesToDraw.size(); i++) {
				g2d.setStroke(strokeInfo);
				g2d.draw(shapesToDraw.get(i));
			}
		g2d.setStroke(backupStroke);
		g2d.setColor(backupColor);
		return true;
	}

	public void addShape(Shape shapeToAdd) {
		shapesToDraw.add(shapeToAdd);
	}
}
