package edu.uiuc.power.displayobjects;

import java.awt.Font;
import java.awt.geom.Point2D;

import edu.uiuc.power.dataobjects.CostAndEmissionsProvider;

public class TransmissionAndDistributionCostsOverlay extends
		CostAndEmissionsOverlay {
	
	public TransmissionAndDistributionCostsOverlay(Point2D centerLocation,
			CostAndEmissionsProvider costprovider) {
		super(centerLocation, costprovider, true);
	}
	
	@Override
	protected String getCaptionLine1() {
		return "Transmission and Distribution";		
	}
	@Override
	protected String getCaptionLine2() {
		return "Costs: $" + convertValueToString(Math.abs(costprovider.getCost(1.0)),true) + "/hr";		
	}
	@Override
	protected boolean isCost() {
		return true;
	}

	@Override
	protected int getFontSize() {
		// TODO Auto-generated method stub
		return 12;
	}

//	@Override
//	protected int getFontStyle() {
//		// TODO Auto-generated method stub
//		return Font.BOLD;
//	}

}
