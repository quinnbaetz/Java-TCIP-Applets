package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.ParseException;

public class TimeLabel extends DataLabel implements Animatable {

	private double _elapsedTimePerAnimationStep;
	private double _currentTime;
	private boolean _paused;
	
	public TimeLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, double elapsedTimePerAnimationStep,
			double initialTime) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		_elapsedTimePerAnimationStep = elapsedTimePerAnimationStep;
		_currentTime = initialTime;
	}
	
	public void setPaused(boolean paused) {
		_paused = paused;
	}
	
	public boolean getPaused() {
		return _paused;
	}

	public double getElapsedTimerPerAnimationStep() {
		return _elapsedTimePerAnimationStep;
	}
	
	public double getCurrentTime() {
		return _currentTime;
	}
	
	public void resettime() {
		_currentTime = 0;
	}
	
	@Override
	public String getCaption() {
		try {
			return "Time: " + textFormatter.valueToString(getCurrentTime()) + " hr.";
		} catch (ParseException pe) {
			return pe.toString();
		}
	}

	public boolean animationstep() {
		if (!getPaused()) {
			_currentTime += getElapsedTimerPerAnimationStep();
			if (_currentTime >= 24) {
				_currentTime = 24;
				setPaused(true);
			}
		}
		return true;
	}

}
