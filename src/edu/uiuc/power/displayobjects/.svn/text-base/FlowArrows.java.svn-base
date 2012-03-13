package edu.uiuc.power.displayobjects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import edu.uiuc.power.dataobjects.Switchable;

public class FlowArrows implements Animatable, Renderable {

	ArrayList<LineAndDistanceInfoProvider> _lines;
	double _OffsetStepPerAnimationStep;
	Switchable _switchableObject;
	double _trianglelength, _distanceBetweenTriangles;
	
	double offset = 0;
	
	public boolean getReverseDirection() {
		return false;
	}
	
	public FlowArrows(ArrayList<LineAndDistanceInfoProvider> lines, double OffsetStepPerAnimationStep, 
			double triangleLength, double distanceBetweenTriangles) {
		_lines = lines;
		_OffsetStepPerAnimationStep = OffsetStepPerAnimationStep;
		_switchableObject = null;
		_trianglelength = triangleLength;
		_distanceBetweenTriangles = distanceBetweenTriangles;
	}
	
	public double getOffsetStepPerAnimationStep() {
		return _OffsetStepPerAnimationStep;
	}
	
	public void setSwitchableObject(Switchable switchableObject) {
		_switchableObject = switchableObject;
	}
	
	public Switchable getSwitchableObject() {
		return _switchableObject;
	}
	
	public boolean getSwitchStatus() {
		Switchable switchObj = getSwitchableObject();
		if (switchObj != null)
			return switchObj.getClosed();
		else
			return true;
	}
	
	public ArrayList<LineAndDistanceInfoProvider> getLines() {
		return _lines;
	}
	
	public boolean animationstep() {
		if (getSwitchStatus()) {
			if (getReverseDirection())
				offset -= getOffsetStepPerAnimationStep();
			else
				offset += getOffsetStepPerAnimationStep();
			
			if (offset > 1.0)
				offset -= 1.0;
			if (offset < 0.0)
				offset += 1.0;
			
			return true;
		} else
			return false;
	}
	
	public double getTriangleLength() {
		return _trianglelength;
	}
	
	public double getDistanceBetweenTriangles() {
		return _distanceBetweenTriangles;
	}
	
	protected Color getFillColor() {
		return new Color(0f,1f,0f,0.5f);
	}
	
	protected Color getOutlineColor() {
		return new Color(0f,0f,0f,0.75f);
	}
	
	public boolean render(Graphics2D g2d) {
		AffineTransform currentxform = g2d.getTransform();
		Color backupColor = g2d.getColor();
		
		if (getSwitchStatus()) {
			// Calculate the total length
			ArrayList<LineAndDistanceInfoProvider> lines = getLines();
			double totallength = lines.get(lines.size() - 1).getEndDistance();
			double triangleLength = getTriangleLength();
			double distanceBetween = getDistanceBetweenTriangles();
			double tPlusD = triangleLength + distanceBetween;
			long numoftriangles = Math.round(Math.floor(totallength/(triangleLength+distanceBetween)));
			
			ArrayList<LineAndDistanceInfoProvider> linesForOffsets = getLines();
			
			for (int i = 0; i < numoftriangles; i++) {
				double dol = i*tPlusD + offset*tPlusD;
				
				Color fillcolor = getFillColor();
				Color outlinecolor = getOutlineColor();
				
				if ((dol >= distanceBetween) & (dol <= tPlusD)) {
					double firstbaselength = dol - distanceBetween;
					Point2D.Double tip = DrawUtilities.getPointAtDistanceOnLines(dol-distanceBetween,linesForOffsets);
					System.out.flush();
					if (getReverseDirection())
						DrawUtilities.drawFlowArrowWithBaseAndTip(g2d,tip,lines.get(0).getFromPoint(),firstbaselength,
								fillcolor,outlinecolor);
					else 	
						DrawUtilities.drawFlowArrowWithBaseAndTip(g2d,lines.get(0).getFromPoint(),tip,firstbaselength,
							fillcolor,outlinecolor);
				}

				Point2D.Double baseCenter = DrawUtilities.getPointAtDistanceOnLines(dol,linesForOffsets);
				Point2D.Double tip = DrawUtilities.getPointAtDistanceOnLines(dol + triangleLength,linesForOffsets);
				if ((tip != null) & (baseCenter != null))
					if (getReverseDirection()) 
						DrawUtilities.drawFlowArrowWithBaseAndTip(g2d, tip, baseCenter, triangleLength, fillcolor, outlinecolor);
					else
						DrawUtilities.drawFlowArrowWithBaseAndTip(g2d, baseCenter, tip, triangleLength, fillcolor, outlinecolor);
			}
			
			
		}
		g2d.setTransform(currentxform);
		g2d.setColor(backupColor);
		return true;
	}
	
	
}
