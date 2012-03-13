package edu.uiuc.power.dataobjects;

public class GeneratorDataWithExportImportPenaltiesAndEmissions extends
		GeneratorDataWithExportImportPenalties {
	
	double emissionsPerMWh;
	
	public GeneratorDataWithExportImportPenaltiesAndEmissions(NodeData connectNode, double MW, double minMW, double maxMW, double MVar, double minMVar, double maxMVar, boolean connected, double penaltyPerMWh, double emissionsPerMWh) {
		super(connectNode, MW, minMW, maxMW, MVar, minMVar, maxMVar, connected, penaltyPerMWh);
		this.emissionsPerMWh = emissionsPerMWh;
	}

	@Override
	public double getEmissions(double amountOfTimeInHours) {
		return emissionsPerMWh*this.getMW();
	}	
}
