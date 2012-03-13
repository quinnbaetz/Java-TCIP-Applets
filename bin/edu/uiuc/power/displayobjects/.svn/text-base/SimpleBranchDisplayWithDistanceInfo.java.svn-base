package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D.Double;

import edu.uiuc.power.dataobjects.BranchData;

public class SimpleBranchDisplayWithDistanceInfo extends
		SimpleLineDisplayWithDistanceInfo {

	BranchData branch;
	BranchColorProvider colorProvider;
	BranchThicknessProvider thicknessProvider;
	
	public SimpleBranchDisplayWithDistanceInfo(Double start_in, Double end_in,
			BranchThicknessProvider thicknessProvider, 
			double startdistance_in, BranchData branch,
			BranchColorProvider colorProvider) {
		super(start_in, end_in, thicknessProvider.getBranchThickness(branch), startdistance_in);
		this.branch = branch;
		this.colorProvider = colorProvider;
		this.thicknessProvider = thicknessProvider;
	}

	@Override
	protected Color getColor() {
		return colorProvider.getBranchColor(getBranchData());
	}
	
	public BranchData getBranchData() {
		return branch;
	}

	@Override
	public double getThickness() {
		return thicknessProvider.getBranchThickness(getBranchData());
	}
	
}
