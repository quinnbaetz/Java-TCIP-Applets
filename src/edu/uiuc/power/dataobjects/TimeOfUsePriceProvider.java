package edu.uiuc.power.dataobjects;

public interface TimeOfUsePriceProvider {
	public double getCurrentRateInDollarsPerKilowattHour(SimulationClock simClock);
	public int getNumOfBreakpoints();
	public double getMaxRateInDollarsPerKilowattHour();
	public String getName();
}
