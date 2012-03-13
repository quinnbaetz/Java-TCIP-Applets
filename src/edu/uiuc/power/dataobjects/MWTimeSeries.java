package edu.uiuc.power.dataobjects;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import edu.uiuc.power.displayobjects.Animatable;

public class MWTimeSeries implements Animatable {
	SimulationClock simClock;
	MWProvider myMWProvider;
	TimeSeries tseries;
	String descriptor;
	int countToUpdate;
	int countsPerUpdate;
	
	public MWTimeSeries(SimulationClock simClock, MWProvider provider, String seriesTitle, int countsPerUpdate) {
		this.simClock = simClock;
		myMWProvider = provider;
		descriptor = seriesTitle;
		tseries = new TimeSeries(seriesTitle,Minute.class);
		countToUpdate = countsPerUpdate;
		this.countsPerUpdate = countsPerUpdate;
	}
	
	public MWTimeSeries(SimulationClock simClock, MWProvider provider, String seriesTitle) {
		this(simClock,provider,seriesTitle,5);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return descriptor;
	}

	public boolean animationstep() {
		if (simClock.getClockState()) {
			if (--countToUpdate == 0) {
				tseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), myMWProvider.getMW());
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
