package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

public class FadingDataLabel extends DataLabel implements Animatable {
	
	String message;
	int countsToFade;
	int currentCountToFade;
	
	int countsWhileFading;
	int currentCountWhileFading;
	boolean fading;
	
	Color backgroundColor, borderColor;
	
	double borderThickness;
	
	ArrayList<Renderable> parentRenderablesList;
	ArrayList<Animatable> parentAnimatablesList;
	
	ArrayList<FadingDataLabelListener> listeners;
	
	public void registerListener(FadingDataLabelListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(FadingDataLabelListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners() {
		Iterator<FadingDataLabelListener> listenersIter = listeners.iterator();
		while (listenersIter.hasNext()) {
			FadingDataLabelListener listener = listenersIter.next();
			listener.fadedOut(this);
		}
	}
	
	public FadingDataLabel(Point2D ULCorner,
			int textsize, Color textcolor,
			Color backgroundColor,
			Color borderColor,
			double borderThickness,
			double rotationAngle,			
			String message, 
			int countsToFade, 
			int countsWhileFading,
			ArrayList<Renderable> parentRenderablesList,
			ArrayList<Animatable> parentAnimatablesList
		) {
		super(ULCorner,textsize,textcolor,rotationAngle);
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
		this.message = message;
		this.countsToFade = countsToFade;
		this.countsWhileFading = countsWhileFading;
		this.parentRenderablesList = parentRenderablesList;
		this.parentAnimatablesList = parentAnimatablesList;
		currentCountToFade = 0;
		currentCountWhileFading = 0;
		fading = false;
		listeners = new ArrayList<FadingDataLabelListener>();
	}
	
	

	@Override
	public String getCaption() {
		return message;
	}

	public Color getTextColor() {
		return getColorWithFade(super.getTextColor());
	}



	public boolean animationstep() {
		if (!fading)
			currentCountToFade++;
		if (currentCountToFade > countsWhileFading)
			fading = true;

		if (fading)
			currentCountWhileFading++;
		if (currentCountWhileFading > countsWhileFading) {
			parentRenderablesList.remove(this);
			parentAnimatablesList.remove(this);
			notifyListeners();
		}

		return true;
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
	protected Color getBackgroundColor() {
		return getColorWithFade(backgroundColor);
	}



	private Color getColorWithFade(Color sourceColor) {
		int alphaVal = 255;
		if (fading) {
			double alphascale = ((double)countsWhileFading - (double)currentCountWhileFading)/(double)countsWhileFading;
			alphaVal = (int)(255*alphascale);
		}
		
		Color drawMessageColor = new Color(sourceColor.getRed(),
				sourceColor.getBlue(),
				sourceColor.getGreen(),
				alphaVal);
		
		return drawMessageColor;
	}
	
	@Override
	protected Color getBorderColor() {
		return getColorWithFade(borderColor);
	}



	@Override
	protected double getBorderThickness() {
		return borderThickness;
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.CENTER;
	}

}
