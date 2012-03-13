package edu.uiuc.power.dataobjects;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import edu.uiuc.power.displayobjects.Animatable;

public class MaxMWTimeSeries implements Animatable {
	SimulationClock simClock;
	MWAndMaxMWProvider maxMWProvider;
	TimeSeries tseries;
	String descriptor;
	int countToUpdate;
	int countsPerUpdate;
	
	public MaxMWTimeSeries(SimulationClock simClock, MWAndMaxMWProvider maxMWProvider, String seriesTitle, int countsPerUpdate) {
		this.simClock = simClock;
		this.maxMWProvider = maxMWProvider;
		descriptor = seriesTitle;
		tseries = new TimeSeries(seriesTitle,Minute.class);
		countToUpdate = countsPerUpdate;
		this.countsPerUpdate = countsPerUpdate;
	}
	
	public MaxMWTimeSeries(SimulationClock simClock, MWAndMaxMWProvider maxMWProvider, String seriesTitle) {
		this(simClock,maxMWProvider,seriesTitle,5);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return descriptor;
	}

	public boolean animationstep() {
		if (simClock.getClockState()) {
			if (--countToUpdate == 0) {
				tseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), maxMWProvider.getMaxMW());
				//tseries.addOrUpdate(new Minute(), 10.0);
				//System.out.println("TSeries Item Count: " + tseries.getItemCount());
				countToUpdate = countsPerUpdate;
				return true;
			} else
				return false;
		} else
			return false;
	}
	
	public TimeSeries getTimeSeries() {
		return tseries;
	}
}
