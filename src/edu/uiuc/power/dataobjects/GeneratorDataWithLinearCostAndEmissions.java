package edu.uiuc.power.dataobjects;

public class GeneratorDataWithLinearCostAndEmissions extends GeneratorData implements CostAndEmissionsProvider {

	private double costPerMWh, emissionsPerMWh, fixedCost, fixedEmissions; 
	private boolean includeFixedCostsAndEmissions;
	
	public GeneratorDataWithLinearCostAndEmissions(NodeData connectNode, double MW, 
			double minMW, double maxMW, double MVar, 
			double minMVar, double maxMVar, boolean connected, 
			double fixedCost, double costPerMWh, double fixedEmissions, 
			double emissionsPerMWh, boolean includeFixedCostsAndEmissions) {
		super(connectNode, MW, minMW, maxMW, MVar, minMVar, maxMVar, connected);
		this.fixedCost = fixedCost;
		this.costPerMWh = costPerMWh;
		this.fixedEmissions = fixedEmissions;
		this.emissionsPerMWh = emissionsPerMWh;
		this.includeFixedCostsAndEmissions = includeFixedCostsAndEmissions;
	}

	public double getCost(double amountOfTimeInHours) {
		double thisMW = this.getMW();
//		if (thisMW < this.getMinMW())
//			thisMW = this.getMinMW();
		if (includeFixedCostsAndEmissions)
			return fixedCost + costPerMWh * thisMW * amountOfTimeInHours;
		else
			return costPerMWh * thisMW;
	}
	
	public void setIncludeFixedCostsAndEmissions(boolean includeFixedCostsAndEmissions) {
		this.includeFixedCostsAndEmissions = includeFixedCostsAndEmissions;
	}
	
	public double getEmissions(double amountOfTimeInHours) {
		if (includeFixedCostsAndEmissions)
			return fixedEmissions + emissionsPerMWh * this.getMW() * amountOfTimeInHours;
		else
			return emissionsPerMWh * this.getMW() * amountOfTimeInHours;
	}

	public boolean getIncludeFixedCostsAndEmissions() {
		return includeFixedCostsAndEmissions;
	}

	public double getCostPerMWh() {
		// TODO Auto-generated method stub
		return costPerMWh;
	}

}
