package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.uiuc.power.dataobjects.BranchOverloadMonitor;
import edu.uiuc.power.dataobjects.BranchOverloadMonitorListener;

public class BranchOverloadOutageLabel implements BranchOverloadMonitorListener,
	FadingDataLabelListener {
	
	Point2D ULCorner;
	int textsize;
	Color textcolor, backgroundColor, borderColor;
	double borderThickness, rotationAngle;
	String message;
	int countsToFade, countsWhileFading;
	ArrayList<Renderable> parentRenderablesList;
	ArrayList<Animatable> parentAnimatablesList;
	
	boolean outageLabelCreated;
	FadingDataLabel outageLabel;

	public BranchOverloadOutageLabel(Point2D ULCorner, int textsize,
			Color textcolor, Color backgroundColor, Color borderColor,
			double borderThickness, double rotationAngle, String message,
			int countsToFade, int countsWhileFading,
			ArrayList<Renderable> parentRenderablesList,
			ArrayList<Animatable> parentAnimatablesList) {
		// Store data to create fading labels later
		
		this.ULCorner = ULCorner;
		this.textsize = textsize;
		this.textcolor = textcolor;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
		this.rotationAngle = rotationAngle;
		this.message = message;
		this.countsToFade = countsToFade;
		this.countsWhileFading = countsWhileFading;
		this.parentAnimatablesList = parentAnimatablesList;
		this.parentRenderablesList = parentRenderablesList;
		
		outageLabel = null;
		outageLabelCreated = false;
	}

	public void lineOpenedDueToOverload(BranchOverloadMonitor monitor) {
		if (!outageLabelCreated) {
			outageLabelCreated = true;
			
			outageLabel = new FadingDataLabel(ULCorner,textsize,
					textcolor,backgroundColor,borderColor,
					borderThickness,rotationAngle,
					message, countsToFade, countsWhileFading,
					parentRenderablesList, parentAnimatablesList);
			outageLabel.registerListener(this);
			parentRenderablesList.add(outageLabel);
			parentAnimatablesList.add(outageLabel);
		}
	}

	public void fadedOut(FadingDataLabel label) {
		outageLabel = null;
		outageLabelCreated = false;
	}
}
