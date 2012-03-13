package edu.uiuc.power.dataobjects;

public class LoadDynamicData extends LoadData {

	double peakMW;
	
	public LoadDynamicData(NodeData connectNode, double MW, double MVar,
			boolean connected) {
		super(connectNode, MW, MVar, connected);
		peakMW = connectNode.peakLoad/2.0;
		// TODO Auto-generated constructor stub
	}

	public double getPeakMW() {
		return peakMW;
	}
	
	public void setPeakMW(double peakMW) {
		this.peakMW = peakMW;
		setMW(peakMW);
	}
}
