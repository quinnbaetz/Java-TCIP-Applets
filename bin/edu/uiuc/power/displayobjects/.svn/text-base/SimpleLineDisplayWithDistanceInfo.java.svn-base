package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;
import java.util.Comparator;


public class SimpleLineDisplayWithDistanceInfo extends SimpleLineDisplay implements LineAndDistanceInfoProvider {
		
		private double start_distance;
		private double end_distance;
		
		public double getStartDistance() {
			return start_distance;
		}
		
		public double getEndDistance() {
			return end_distance;
		}
		
		public SimpleLineDisplayWithDistanceInfo(Point2D.Double start_in, Point2D.Double end_in,
				double thickness, double startdistance_in) {
			super(start_in, end_in, thickness);
			start_distance = startdistance_in;
			end_distance = start_distance + getLength();
		}

		public int compare(Object o1, Object o2) {
			SimpleLineDisplayWithDistanceInfo so1 = (SimpleLineDisplayWithDistanceInfo)o1;
			SimpleLineDisplayWithDistanceInfo so2 = (SimpleLineDisplayWithDistanceInfo)o2;			
			if (so1.start_distance > so2.start_distance)
				return 1;
			else if (so1.start_distance < so2.start_distance)
				return -1;
			else
				return 0; 
		}
}
