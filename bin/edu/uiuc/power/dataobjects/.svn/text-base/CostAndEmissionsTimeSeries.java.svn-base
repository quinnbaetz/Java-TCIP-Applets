package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import edu.uiuc.power.displayobjects.Animatable;

public class CostAndEmissionsTimeSeries implements Animatable {
	SimulationClock simClock;
	ArrayList<CostAndEmissionsProvider> costAndEmissionsProviders = new ArrayList<CostAndEmissionsProvider>();
	TimeSeries costTseries, emissionsTseries;
	String costSeriesTitle, emissionsSeriesTitle;
	int countToUpdate;
	int countsPerUpdate;
	
	public void addCostAndEmissionsProvider(CostAndEmissionsProvider cep) {
		costAndEmissionsProviders.add(cep);
	}
	
	public CostAndEmissionsTimeSeries(SimulationClock simClock, String costSeriesTitle, String emissionsSeriesTitle, int countsPerUpdate) {
		this.simClock = simClock;
		this.emissionsSeriesTitle = emissionsSeriesTitle;
		this.costSeriesTitle = costSeriesTitle;
		costTseries = new TimeSeries(costSeriesTitle,Minute.class);
		emissionsTseries = new TimeSeries(emissionsSeriesTitle,Minute.class);
		countToUpdate = countsPerUpdate;
		this.countsPerUpdate = countsPerUpdate;
	}
	
	public CostAndEmissionsTimeSeries(SimulationClock simClock, String costSeriesTitle, String emissionsSeriesTitle) {
		this(simClock,costSeriesTitle,emissionsSeriesTitle,5);
	}

	public boolean animationstep() {
		if (simClock.getClockState()) {
			if (--countToUpdate == 0) {
				double costSum = 0;
				double emissionsSum = 0;
				for (int i = 0; i < costAndEmissionsProviders.size(); i++) {
					costSum += costAndEmissionsProviders.get(i).getCost(1);
					emissionsSum += costAndEmissionsProviders.get(i).getEmissions(1); 
				}
				costTseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), costSum);
				emissionsTseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), emissionsSum);
				countToUpdate = countsPerUpdate;
				return true;
			} else
				return false;
		} else
			return false;
	}
	
	public TimeSeries getCostTimeSeries() {
		return costTseries;
	}
	
	public TimeSeries getEmissionsTimeSeries() {
		return emissionsTseries;
	}	
}
