package edu.uiuc.power.dataobjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeSet;

import edu.uiuc.power.displayobjects.Animatable;

public class WindManager implements Animatable {

	WindGeneratorData manageMW;
	TreeSet<MWTimePoint> timePoints = new TreeSet<MWTimePoint>();
	boolean adjustDayToMatchFirstPoint;
	SimulationClock simClock;
	private double scaleAllPoints = 1.0;
	
	class MWTimePoint implements Comparable {
		Date time;
		double MW;
		
		public MWTimePoint(Date time, double MW) {
			this.time = time;
			this.MW = MW;
		}

		public int compareTo(Object arg0) throws ClassCastException {
			if (arg0 instanceof MWTimePoint)
				return time.compareTo(((MWTimePoint)arg0).time);
			else if (arg0 instanceof Date) {
				return time.compareTo((Date)arg0);
			} else 
				throw new ClassCastException("Not a GenerationTimePoint or Date");
		}
	}
	
	public WindManager(WindGeneratorData manageMW, SimulationClock simClock, boolean adjustDayToMatchFirstPoint) {
		this.manageMW = manageMW;
		this.simClock = simClock;
		this.adjustDayToMatchFirstPoint = adjustDayToMatchFirstPoint;
	}
	
	private double getCurrentMW(Date currentTimeToCheck) {
		if (timePoints.size() > 0) {
			Date currentTime;
			if (adjustDayToMatchFirstPoint) {
				Calendar currentCal = new GregorianCalendar();
				currentCal.setTime(currentTimeToCheck);
				Calendar firstCal = new GregorianCalendar();
				firstCal.setTime(timePoints.first().time);
				currentCal.set(firstCal.get(Calendar.YEAR), firstCal.get(Calendar.MONTH), firstCal.get(Calendar.DAY_OF_MONTH));
				currentTime = currentCal.getTime();
			} else {
				currentTime = currentTimeToCheck;
			}
			
			Iterator<MWTimePoint> tpIter = timePoints.iterator();
			MWTimePoint beforePoint = null;
			MWTimePoint afterPoint = null;
			boolean foundBoundPoints = false;
			while (tpIter.hasNext() & !foundBoundPoints) {
				afterPoint = tpIter.next();
				if (afterPoint.compareTo(currentTime) < 0)
					beforePoint = afterPoint;
				else 
					foundBoundPoints = true;
			}
			
			if (foundBoundPoints == true) {
				if (beforePoint == null) {
					// The first point was later than currentTime
					return timePoints.first().MW;
				} else {
					long beforeAfterGap = afterPoint.time.getTime() - beforePoint.time.getTime();
					long beforeCurrentGap = currentTime.getTime() - beforePoint.time.getTime();
					double percentBetweenBeforeAndAfter =  (double)beforeCurrentGap/(double)beforeAfterGap;
					return beforePoint.MW + (afterPoint.MW - beforePoint.MW)*percentBetweenBeforeAndAfter;
				}
			} else {
				// currentTime was after all times in the series
				return timePoints.last().MW;
			}
		} else {
			// no points so error
			System.out.println("NO TIMEPOINTS");
			return 0;
		}
	}
	
	public double getMaxMW() {
		double retVal = Double.NEGATIVE_INFINITY;
		Iterator<MWTimePoint> tpIter = timePoints.iterator();
		while(tpIter.hasNext()) {
			MWTimePoint tp = tpIter.next();
			if (tp.MW > retVal)
				retVal = tp.MW;
		}
		return retVal*scaleAllPoints;
	}
	
	public void addPoint(Date time, double MW) {
		timePoints.add(new MWTimePoint(time,MW));
	}

	public double getCurrentMWSetting() {
		return scaleAllPoints*getCurrentMW(simClock.getCurrentCalendar().getTime());
	}
	
	public boolean animationstep() {
		manageMW.setMaxMW(scaleAllPoints*getCurrentMW(simClock.getCurrentCalendar().getTime()));
		//System.out.println(manageMW.getMW());
		return true;
	}
	
	public void setScaleAllPoints(double scaleAllPoints) {
		this.scaleAllPoints = scaleAllPoints;
	}

}

