package edu.uiuc.power.dataobjects;

public class DemandProviderFromLineFlow implements DemandProvider {

	BranchData bdata;
	String units;
	double wattMultiplier;
	
	public DemandProviderFromLineFlow(BranchData bdata, String units,
			double wattMultiplier) {
		super();
		this.bdata = bdata;
		this.units = units;
		this.wattMultiplier = wattMultiplier;
	}

	public double getDemand() {
		return bdata.getMW();
	}

	public String getUnits() {
		return units;
	}

	public double getWattMultiplier() {
		return wattMultiplier;
	}

}
