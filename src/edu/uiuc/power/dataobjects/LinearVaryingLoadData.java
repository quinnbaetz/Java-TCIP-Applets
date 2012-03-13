package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

import edu.uiuc.power.displayobjects.Animatable;

public class LinearVaryingLoadData extends LoadData implements Animatable {

	private ArrayList<LinearVaryingLoadPoint> _loadpoints;
	private double _timeLeft;
	private double _slopeMW;
	private double _slopeMVar;
	private int currentPoint;
	private boolean endOfList;
	private double _timeElapsedPerAnimationStep;
	
	public LinearVaryingLoadData(NodeData connectNode, 
			boolean connected,
			ArrayList<LinearVaryingLoadPoint> loadpoints,
			double timeElapsedPerAnimationStep) {
		super(connectNode,0,0,connected);
		_timeElapsedPerAnimationStep = timeElapsedPerAnimationStep;
		_loadpoints = loadpoints;
		resettime();
	}
	
	private boolean _paused;
	
	public void scaleAllPoints(double scaleValue) {
		ArrayList<LinearVaryingLoadPoint> loadpoints = getLoadPoints();
		for (int i = 0; i < loadpoints.size(); i++) {
			loadpoints.get(i).scaleMW(scaleValue);
		}
		resettime();
	}
	
	public void setPaused(boolean paused) {
		_paused = paused;
	}
	
	public boolean getPaused() {
		return _paused;
	}
	
	private void setSlopeMW(double slopeMW) {
		_slopeMW = slopeMW;
	}
	
	private double getSlopeMW() {
		return _slopeMW;
	}
	
	private void setSlopeMVar(double slopeMVar) {
		_slopeMVar = slopeMVar;
	}
	
	private double getSlopeMVar() {
		return _slopeMVar;
	}
	
	public double getTimeElapsedPerAnimationStep() {
		return _timeElapsedPerAnimationStep;
	}
	
	public ArrayList<LinearVaryingLoadPoint> getLoadPoints() {
		return _loadpoints;
	}
	
	private void setTimeLeft(double timeAmount) {
		_timeLeft = timeAmount;
	}
	
	private double getTimeLeft() {
		return _timeLeft;
	}
	
	public void resettime() {
		currentPoint = -1;
		if (getLoadPoints().size() > 0)
			endOfList = false;
		loadNextPoint();		
	}
	
	private void loadNextPoint() {
		if (!endOfList) {
			if ((currentPoint + 1) < getLoadPoints().size() ) {
				currentPoint++;
				if ((currentPoint + 1) < getLoadPoints().size()) {
					// Set up slope if necessary
					LinearVaryingLoadPoint pointA = getLoadPoints().get(currentPoint);
					LinearVaryingLoadPoint pointB = getLoadPoints().get(currentPoint + 1);
					double slopeMW = (pointB.getMW() - pointA.getMW())/(pointB.getTimeValue() - pointA.getTimeValue());
					setSlopeMW(slopeMW);
					double slopeMVar = (pointB.getMVar() - pointA.getMVar())/(pointB.getTimeValue() - pointA.getTimeValue());
					setSlopeMVar(slopeMVar);
					setMW(pointA.getMW());
					setMVar(pointA.getMVar());
					setTimeLeft(pointB.getTimeValue() - pointA.getTimeValue());
					endOfList = false;
				} else {
					// Last value, so hold constant
					LinearVaryingLoadPoint point = getLoadPoints().get(currentPoint);
					setMW(point.getMW());
					setSlopeMW(0);
					setMVar(point.getMVar());
					setSlopeMVar(0);
					endOfList = true;
				}
			} else {
				endOfList = true;
			}
		}
	}

	public boolean animationstep() {
		if (getPaused())
			return false; // do nothing
		else {
			if (getTimeLeft() > 0) {
				setTimeLeft(getTimeLeft() - getTimeElapsedPerAnimationStep());
				double negativeOffset = 0;
				if (getTimeLeft() < 0) 
					negativeOffset = -getTimeLeft();
				setMW(getMW() + getSlopeMW()*getTimeElapsedPerAnimationStep() - getSlopeMW()*negativeOffset);
				setMVar(getMVar() + getSlopeMVar()*getTimeElapsedPerAnimationStep() - getSlopeMVar()*negativeOffset);
				return false;
			} else {
				loadNextPoint();		
				return true;
			}
		}
	}

}
