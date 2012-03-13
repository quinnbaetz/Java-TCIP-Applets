package edu.uiuc.power.dataobjects.timeOfUseProviders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeSet;

import edu.uiuc.power.dataobjects.MWManageable;
import edu.uiuc.power.dataobjects.SimulationClock;
import edu.uiuc.power.dataobjects.TimeOfUsePriceProvider;
import edu.uiuc.power.displayobjects.Animatable;

public class HourlyPrice implements TimeOfUsePriceProvider {

	TreeSet<PriceTimePoint> timePoints = new TreeSet<PriceTimePoint>();
	SimulationClock simClock;
	private double maxPrice = Double.NEGATIVE_INFINITY;
	double maxMW;
	String name;
	
	class PriceTimePoint implements Comparable {
		Date time;
		double price;
		
		public PriceTimePoint(Date time, double priceInDollarsPerKWH) {
			this.time = time;
			this.price = priceInDollarsPerKWH;
		}

		public int compareTo(Object arg0) throws ClassCastException {
			if (arg0 instanceof PriceTimePoint)
				return time.compareTo(((PriceTimePoint)arg0).time);
			else if (arg0 instanceof Date) {
				return time.compareTo((Date)arg0);
			} else 
				throw new ClassCastException("Not a PriceTimePoint or Date");
		}
	}
	
	public HourlyPrice(SimulationClock simClock, String name) {
		this.simClock = simClock;
		this.name = name;
	}
	
	private double getCurrentPrice(Date currentTimeToCheck) {
		if (timePoints.size() > 0) {
			Date currentTime;

			Calendar currentCal = new GregorianCalendar();
			currentCal.setTime(currentTimeToCheck);
			Calendar firstCal = new GregorianCalendar();
			firstCal.setTime(timePoints.first().time);
			currentCal.set(firstCal.get(Calendar.YEAR), firstCal.get(Calendar.MONTH), firstCal.get(Calendar.DAY_OF_MONTH));
			currentTime = currentCal.getTime();
			
			Iterator<PriceTimePoint> tpIter = timePoints.iterator();
			PriceTimePoint beforePoint = null;
			PriceTimePoint afterPoint = null;
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
					return timePoints.first().price;
				} else {
					return beforePoint.price;
				}
			} else {
				// currentTime was after all times in the series
				return timePoints.last().price;
			}
		} else {
			// no points so error
			System.out.println("NO TIMEPOINTS");
			return 0;
		}
	}
	
	public double getMaxPrice() {
		return maxPrice;
	}
	
	public void addPoint(Date time, double priceInDollarsPerKWH) {
		timePoints.add(new PriceTimePoint(time,priceInDollarsPerKWH));
		if (priceInDollarsPerKWH > maxPrice)
			maxPrice = priceInDollarsPerKWH;
	}

	public void addPoints(Date[] times, double[] pricesInDollarsPerKWH) {
		for (int i = 0; i < times.length; i++)
			addPoint(times[i],pricesInDollarsPerKWH[i]);
	}
	
	public void addPointsTimeDataAndPriceStrings(String[] timeStringsInHHMMFormat, double prices[]) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
		for (int i = 0; i < timeStringsInHHMMFormat.length; i++) {
			addPoint(sdf.parse(timeStringsInHHMMFormat[i]),prices[i]);
		}
	}
	
	public double getCurrentRateInDollarsPerKilowattHour(
			SimulationClock simClock) {
		// TODO Auto-generated method stub
		return getCurrentPrice(simClock.getCurrentCalendar().getTime());
	}

	public int getNumOfBreakpoints() {
		// TODO Auto-generated method stub
		return timePoints.size();
	}

	public double getMaxRateInDollarsPerKilowattHour() {
		// TODO Auto-generated method stub
		return getMaxPrice();
	}

	public String getName() {
		return name;
	}

}

