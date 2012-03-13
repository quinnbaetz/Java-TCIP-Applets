package edu.uiuc.power.dataobjects;

public class GeneratorDataWithExportImportPenalties extends GeneratorData implements CostAndEmissionsProvider {

	private double penaltyPerMWh; 
	
	public GeneratorDataWithExportImportPenalties(NodeData connectNode, double MW, double minMW, double maxMW, double MVar, double minMVar, double maxMVar, boolean connected, double penaltyPerMWh) {
		super(connectNode, MW, minMW, maxMW, MVar, minMVar, maxMVar, connected);
		this.penaltyPerMWh = penaltyPerMWh;
	}

	public double getCost(double amountOfTimeInHours) {
		if (this.getMW() > 0) {
			//System.out.println("cost > 0: " + (penaltyPerMWh * this.getMW() * amountOfTimeInHours));
			return penaltyPerMWh * this.getMW() * amountOfTimeInHours;
		} else {
			//System.out.println("cost < 0: " + (penaltyPerMWh * this.getMW() * amountOfTimeInHours));
			return penaltyPerMWh/2 * this.getMW() * amountOfTimeInHours;
		}
	}
	
	public void setIncludeFixedCostsAndEmissions(boolean includeFixedCostsAndEmissions) {
		// do nothing
	}
	
	public double getEmissions(double amountOfTimeInHours) {
		return 0.0;
	}

	public boolean getIncludeFixedCostsAndEmissions() {
		return false;
	}

	public double getCostPerMWh() {
		// TODO Auto-generated method stub
		return penaltyPerMWh;
	}
}
