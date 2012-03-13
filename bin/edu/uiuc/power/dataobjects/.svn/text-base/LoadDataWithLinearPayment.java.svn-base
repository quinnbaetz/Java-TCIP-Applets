package edu.uiuc.power.dataobjects;

public class LoadDataWithLinearPayment extends LoadData implements CostAndEmissionsProvider {

	private double paymentPerMWh;
	
	public LoadDataWithLinearPayment(NodeData connectNode, double MW, double MVar,
			boolean connected, double paymentPerMWh) {
		super(connectNode, MW, MVar, connected);
		this.paymentPerMWh = paymentPerMWh;
	}

	public double getCost(double amountOfTimeInHours) {
		return -amountOfTimeInHours*getMWSupplied()*paymentPerMWh;
	}

	public double getEmissions(double amountOfTimeInHours) {
		return 0;
	}

	public boolean getIncludeFixedCostsAndEmissions() {
		return false;
	}
	
	public double getPaymentPerMWh() {
		return paymentPerMWh;
	}
	
	public void setPaymentPerMWh(double paymentPerMWh) {
		this.paymentPerMWh = paymentPerMWh;
	}

	public void setIncludeFixedCostsAndEmissions(boolean includeFixedCostsAndEmissions) {
		// don't use fixed stuff in load cost model
	}

	public double getCostPerMWh() {
		// TODO Auto-generated method stub
		return -paymentPerMWh;
	}
}
