package edu.uiuc.power.dataobjects;

public class WindGeneratorDataWithLinearCostAndEmissions extends
		WindGeneratorData implements CostAndEmissionsProvider {

	private double costPerMWh, emissionsPerMWh, fixedCost, fixedEmissions;
	private boolean includeFixedCostsAndEmissions = false;
	
	public WindGeneratorDataWithLinearCostAndEmissions(NodeData connectNode,
			double initialMW, double minMW, double maxMW, double variation,
			boolean connected, double fixedCost, double costPerMWh, 
			double fixedEmissions, double emissionsPerMWh,
			boolean includeFixedCostsAndEmissions) {
		super(connectNode, initialMW, minMW, maxMW, variation, connected);
		this.includeFixedCostsAndEmissions = includeFixedCostsAndEmissions;
		this.fixedCost = fixedCost;
		this.costPerMWh = costPerMWh;
		this.fixedEmissions = fixedEmissions;
		this.emissionsPerMWh = emissionsPerMWh;
		// TODO Auto-generated constructor stub
	}

	public double getCost(double amountOfTimeInHours) {
		if (includeFixedCostsAndEmissions)
			return fixedCost + costPerMWh * this.getMW();
		else
			return costPerMWh * this.getMW();
	}
	
	public double getEmissions(double amountOfEmissionsInHours) {
		if (includeFixedCostsAndEmissions)
			return fixedEmissions + emissionsPerMWh * this.getMW();
		else
			return emissionsPerMWh * this.getMW();
	}

	public void setIncludeFixedCostsAndEmissions(boolean includeFixedCostsAndEmissions) {
		this.includeFixedCostsAndEmissions = includeFixedCostsAndEmissions;
	}

	public boolean getIncludeFixedCostsAndEmissions() {
		return includeFixedCostsAndEmissions;
	}

	public double getCostPerMWh() {
		// TODO Auto-generated method stub
		return costPerMWh;
	}
}
