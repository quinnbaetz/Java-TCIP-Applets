package edu.uiuc.power.dataobjects;

import edu.uiuc.power.displayobjects.Animatable;

public class StorageDevice extends GeneratorData implements Animatable, 
	CostAndEmissionsProvider {

	WindGeneratorData windGen;
	BranchData branch;
	SimulationClock clock;
	double currentStoredEnergy;
	double energyCapacityInMWh;
	double capacityInMW;
	boolean animating = true;
	
	public void setStoredEnergy(double value) {
		currentStoredEnergy = value;
	}
	
	public double getEnergyCapacity() {
		return energyCapacityInMWh;
	}
	
	public void setAnimating(boolean animating) {
		this.animating = animating;
	}
	
	public double getStoredEnergy() {
		return currentStoredEnergy;
	}
	
	public double getMaxMW() {
		if (windGen.getConnectNode().getIslandNum() == this.getConnectNode().getIslandNum()) {
			double windMax = windGen.getMaxMW();
			double totalLoad = 0;
			if (this.getConnectNode().getIslandData() != null)
				totalLoad = this.getConnectNode().getIslandData().getTotalLoad();
			double lineMax = branch.getMaxMWFlow();
			
			double storeMax = Double.NEGATIVE_INFINITY;
			if (windMax > lineMax) {
				if (totalLoad > lineMax) {
					storeMax = windMax - lineMax;
				} else {
					storeMax = windMax - totalLoad;
				}
			} else {
				if (totalLoad > lineMax) {
					// generate
				} else {
					if (totalLoad > windMax) {
						// generate
					} else {
						storeMax = windMax - totalLoad;
					}
				}
			}
			
			double retval = 0;
			
			if (storeMax > 0) {
				if (currentStoredEnergy < energyCapacityInMWh) {
					retval = (capacityInMW < storeMax ? -capacityInMW : -storeMax);	
				} else 
					retval = 0;
			} else {
				double energyLimit = currentStoredEnergy / ((double)(clock.minutesPerAnimationStep)/60.0);
				retval = (energyLimit < capacityInMW ? energyLimit : capacityInMW);
			}
			return retval;
			
//			
//			if (surplusWindDueToLine > 0) {
//				if (currentStoredEnergy < energyCapacityInMWh) {
//					retval = (capacityInMW < surplusWindDueToLine ? -capacityInMW : -surplusWindDueToLine);	
//				} else 
//					retval = 0;
//			} else {
//				double energyLimit = currentStoredEnergy / ((double)(clock.minutesPerAnimationStep)/60.0);
//				retval = (energyLimit < capacityInMW ? energyLimit : capacityInMW);
//			}
//			return retval;
		} else {
			double energyLimit = currentStoredEnergy / ((double)(clock.minutesPerAnimationStep)/60.0);
			return (energyLimit < capacityInMW ? energyLimit : capacityInMW);
		}
	}
	
	public boolean treatAsGen() {
		if (getMaxMW() > 0)
			return true;
		else
			return false;
	}
	
	public StorageDevice(NodeData connectNode, double MW, double minMW,
			double maxMW, double MVar, double minMVar, double maxMVar,
			boolean connected, WindGeneratorData windGen, BranchData branch,
			SimulationClock clock, double energyCapacityInMWh, double capacityInMW) {
		super(connectNode, MW, minMW, maxMW, MVar, minMVar, maxMVar, connected);
		this.windGen = windGen;
		this.branch = branch;
		this.clock = clock;
		this.energyCapacityInMWh = energyCapacityInMWh;
		currentStoredEnergy = 0.0;
		this.capacityInMW = capacityInMW;
	}

	public boolean animationstep() {
		if (animating) {
			if (this.getMW() < 0) {
				double newstoreVal = currentStoredEnergy - this.getMW()*((double)clock.getMinutesPerAnimationStep())/60.0; 
				currentStoredEnergy = (newstoreVal > energyCapacityInMWh ? energyCapacityInMWh : newstoreVal);
				return true;
			} else if (this.getMW() > 0) {
				double newstoreVal = currentStoredEnergy - this.getMW()*((double)clock.getMinutesPerAnimationStep())/60.0; 
				currentStoredEnergy = (newstoreVal > 0 ? newstoreVal : 0);
				return true;
			} else
				return false;
		} else 
			return false;
	}

	public boolean haveCapacity() {
		if (energyCapacityInMWh < currentStoredEnergy)
			return true;
		else
			return false;
	}

	public double getCost(double amountOfTimeInHours) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	double loadCost = 0;
	
	public void setLoadCost(double loadCost) {
		this.loadCost = loadCost;
	}

	public double getCostPerMWh() {
		if (treatAsGen()) 
			return Double.MIN_VALUE;
		else
			return 10;//Double.MIN_VALUE;
	}

	public double getEmissions(double amountOfTimeInHours) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getIncludeFixedCostsAndEmissions() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setIncludeFixedCostsAndEmissions(
			boolean includeFixedCostsAndEmissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMW() {
		// TODO Auto-generated method stub
		double superVal = super.getMW();
		return superVal;
	}
}
