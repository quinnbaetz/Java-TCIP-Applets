package edu.uiuc.power.dataobjects.timeOfUseProviders;

import edu.uiuc.power.dataobjects.SimulationClock;
import edu.uiuc.power.dataobjects.TimeOfUsePriceProvider;

public class ConstantPrice implements TimeOfUsePriceProvider {

	double constantPrice;
	
	public double getCurrentRateInDollarsPerKilowattHour(SimulationClock simClock) {
		// TODO Auto-generated method stub
		return constantPrice;
	}

	public ConstantPrice(double constantPrice) {
		super();
		this.constantPrice = constantPrice;
	}

	public int getNumOfBreakpoints() {
		return 1;
	}

	public double getMaxRateInDollarsPerKilowattHour() {
		// TODO Auto-generated method stub
		return constantPrice;
	}

	public String getName() {
		return "Constant Price";
	}

}
