package edu.uiuc.power.dataobjects;

public class LinearVaryingLoadPoint {
	private double _MW;
	private double _MVar;
	private double _timeValue;
	
	public LinearVaryingLoadPoint(double MW, double MVar, double timeValue) {
		_MW = MW;
		_MVar = MVar;
		_timeValue = timeValue;
	}
	
	public void scaleMW(double scaleValue) {
		_MW *= scaleValue;
	}
	
	public double getMW() {
		return _MW;
	}
	
	public double getMVar() {
		return _MVar;
	}
	
	public double getTimeValue() {
		return _timeValue;
	}
}
