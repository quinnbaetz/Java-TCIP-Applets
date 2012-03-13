package edu.uiuc.power.dataobjects;

import java.util.ArrayList;
import java.util.Date;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import edu.uiuc.power.displayobjects.Animatable;

public class CostAndDemandTimeSeries implements Animatable {
	SimulationClock simClock;
	ArrayList<DemandProvider> demandProviders = new ArrayList<DemandProvider>();
	TimeOfUsePriceProvider timeOfUsePriceProvider;
	
	TimeSeries costTseries,demandTseries;
	String costSeriesTitle,demandSeriesTitle;
	int countToUpdate;
	int countsPerUpdate;

	double runningCostTotal = 0;
	double runningEnergyTotal = 0;
	
	
	public void addDemandProvider(DemandProvider dp) {
		demandProviders.add(dp);
	}
	
	public double getDemandInW() {
		double totalDemand = 0;
		for (int i = 0; i < demandProviders.size(); i++)
			totalDemand += demandProviders.get(i).getDemand()*demandProviders.get(i).getWattMultiplier();
		return totalDemand;
	}
	
	public CostAndDemandTimeSeries(SimulationClock simClock, String costSeriesTitle, String demandSeriesTitle, 
			TimeOfUsePriceProvider timeOfUsePriceProvider, int countsPerUpdate) {
		this.timeOfUsePriceProvider = timeOfUsePriceProvider;
		this.simClock = simClock;
		this.costSeriesTitle = costSeriesTitle;
		costTseries = new TimeSeries(costSeriesTitle,Minute.class);
		this.demandSeriesTitle = demandSeriesTitle;
		demandTseries = new TimeSeries(demandSeriesTitle,Minute.class);
		countToUpdate = countsPerUpdate;
		this.countsPerUpdate = countsPerUpdate;
	}
	
	public CostAndDemandTimeSeries(SimulationClock simClock, String costSeriesTitle, String emissionsSeriesTitle,
			TimeOfUsePriceProvider timeOfUsePriceProvider) {
		this(simClock,costSeriesTitle,emissionsSeriesTitle,timeOfUsePriceProvider,5);
	}

	Date lastDate = null;
	
	public boolean animationstep() {
		if (simClock.getClockState()) {
			if (--countToUpdate == 0) {
				double demandSumInW = getDemandInW();
				double demandSumInkW = demandSumInW / 1000.0;
				double currentPricePerKWHour = timeOfUsePriceProvider.getCurrentRateInDollarsPerKilowattHour(simClock);
				double currentPricePerKWMinute = currentPricePerKWHour/60.0;
				double costSum = currentPricePerKWHour*demandSumInkW*100;
				
				if (lastDate != null) {
					long timeDiffInMillis = simClock.getCurrentCalendar().getTime().getTime() - lastDate.getTime(); 
					double timeDiffInHours = (double)(timeDiffInMillis)/(3600.0*1000.0);
					runningCostTotal += timeDiffInHours*costSum/100.0;
					runningEnergyTotal += demandSumInkW*(timeDiffInHours);
				}
				lastDate = simClock.getCurrentCalendar().getTime();
				
				
				costTseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), costSum);
				demandTseries.addOrUpdate(new Minute(simClock.getCurrentCalendar().getTime()), demandSumInW);
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
	
	public TimeSeries getDemandTimeSeries() {
		return demandTseries;
	}
	
	public void clearAllTimeSeries() {
		costTseries.clear();
		demandTseries.clear();
		runningCostTotal = 0;
		runningEnergyTotal = 0;
		lastDate = null;
	}
	
	public void setTimeOfUsePriceProvider(TimeOfUsePriceProvider tProvider, boolean retroactive) {
		this.timeOfUsePriceProvider = tProvider;
		if (retroactive && tProvider.getName().equalsIgnoreCase("Constant Price")) {
			TimeSeries demandTimeSeries = getDemandTimeSeries();
			costTseries.clear();
			for (int i = 0; i < demandTimeSeries.getItemCount(); i++) {
				 TimeSeriesDataItem dataItem = demandTimeSeries.getDataItem(i);
				 costTseries.add(dataItem.getPeriod(),dataItem.getValue().doubleValue()/1000.0*100*tProvider.getMaxRateInDollarsPerKilowattHour());
				 //System.out.println("costTseries point i added, " + dataItem.getPeriod() + ", " + dataItem.getValue().doubleValue()*tProvider.getMaxRateInDollarsPerKilowattHour());
			}
			runningCostTotal = runningEnergyTotal*tProvider.getMaxRateInDollarsPerKilowattHour(); 			
		}
	}
	
	public void setTimeOfUsePriceProvider(TimeOfUsePriceProvider tProvider) {
		setTimeOfUsePriceProvider(tProvider,false);
	}

	public double getRunningCostTotal() {
		return runningCostTotal;
	}

	public double getRunningEnergyTotal() {
		return runningEnergyTotal;
	}
}
