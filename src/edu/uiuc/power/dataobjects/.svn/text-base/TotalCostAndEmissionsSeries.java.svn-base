package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

public class TotalCostAndEmissionsSeries {
	SimulationClock simClock;
	ArrayList<CostAndEmissionsProvider> providers;
	TimeSeries costTseries;
	TimeSeries emissionsTseries;
	String costDescriptor;
	String emissionsDescriptor;
	
	public TotalCostAndEmissionsSeries(SimulationClock simClock, ArrayList<CostAndEmissionsProvider> providers, String CostSeriesTitle, String EmissionsSeriesTitle) {
		this.simClock = simClock;
		this.providers = providers;
		this.costDescriptor = CostSeriesTitle;
		this.emissionsDescriptor = EmissionsSeriesTitle;
		costTseries = new TimeSeries(CostSeriesTitle,Minute.class);
		emissionsTseries = new TimeSeries(EmissionsSeriesTitle,Minute.class);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return costDescriptor + " / " + emissionsDescriptor;
	}

	public boolean animationstep() {
		double outputCostStep = 0;
		double outputEmissionStep = 0;
		
		for (int i = 0; i < providers.size(); i++) {
			outputCostStep += providers.get(i).getCost(1);
			outputEmissionStep += providers.get(i).getEmissions(1);
		}		
		costTseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), outputCostStep);
		emissionsTseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), outputEmissionStep);
		return true;
	}
	
	public TimeSeries getCostTimeSeries() {
		return costTseries;
	}
	
	public TimeSeries getEmissionsTimeSeries() {
		return emissionsTseries;
	}

}
