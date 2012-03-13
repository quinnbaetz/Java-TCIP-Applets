package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;

import edu.uiuc.power.dataobjects.Switchable;

public class SwitchDisplayWithDistanceInfo extends SwitchDisplay implements LineAndDistanceInfoProvider {
	
	private double start_distance;
	private double end_distance;
	
	public SwitchDisplayWithDistanceInfo(Switchable switchableObject, Point2D.Double from, Point2D.Double to,
			double thickness, double offsetAnglePerAnimationStep, boolean openInOppositeDirection, double startdistance_in) {
		super(switchableObject, from, to, thickness, offsetAnglePerAnimationStep,openInOppositeDirection);
		start_distance = startdistance_in;
		end_distance = start_distance + getLength();
	}
	
	public SwitchDisplayWithDistanceInfo(Switchable switchableObject, Point2D.Double from, Point2D.Double to,
			double thickness, double offsetAnglePerAnimationStep, double startdistance_in) {
		super(switchableObject, from, to, thickness, offsetAnglePerAnimationStep);
		start_distance = startdistance_in;
		end_distance = start_distance + getLength();
	}

	public int compare(Object o1, Object o2) {
		SwitchDisplayWithDistanceInfo so1 = (SwitchDisplayWithDistanceInfo)o1;
		SwitchDisplayWithDistanceInfo so2 = (SwitchDisplayWithDistanceInfo)o2;			
		if (so1.start_distance > so2.start_distance)
			return 1;
		else if (so1.start_distance < so2.start_distance)
			return -1;
		else
			return 0; 
	}

	public double getEndDistance() {
		return end_distance;
	}

	public double getStartDistance() {
		return start_distance;
	}
}
