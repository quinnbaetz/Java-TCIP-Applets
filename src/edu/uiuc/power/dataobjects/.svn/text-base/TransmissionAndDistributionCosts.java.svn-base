package edu.uiuc.power.dataobjects;

public class TransmissionAndDistributionCosts implements
		CostAndEmissionsProvider {

	double fixedCostPerHour;
	boolean includeFixedCostsAndEmissions;
	
	public TransmissionAndDistributionCosts(double fixedCostPerHour,
			boolean includeFixedCostsAndEmissions) {
		super();
		this.fixedCostPerHour = fixedCostPerHour;
		this.includeFixedCostsAndEmissions = includeFixedCostsAndEmissions;
	}

	public double getCost(double amountOfTimeInHours) {
		if (includeFixedCostsAndEmissions)
			return fixedCostPerHour;
		else 
			return 0;
	}

	public double getEmissions(double amountOfTimeInHours) {
		return 0;
	}

	public boolean getIncludeFixedCostsAndEmissions() {
		return includeFixedCostsAndEmissions;
	}

	public void setIncludeFixedCostsAndEmissions(
			boolean includeFixedCostsAndEmissions) {
		this.includeFixedCostsAndEmissions = includeFixedCostsAndEmissions;
	}

	public double getCostPerMWh() {
		// TODO Auto-generated method stub
		return fixedCostPerHour;
	}

}
