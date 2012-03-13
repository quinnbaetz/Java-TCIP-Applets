package edu.uiuc.power.displayobjects;

import edu.uiuc.power.dataobjects.BranchData;

public class BranchThicknessDynamic implements BranchThicknessProvider {

	double defaultThickness, warningThickness, overloadThickness;
	double warningPercentage, overloadPercentage;
	
	public BranchThicknessDynamic(double defaultThickness, 
			double warningPercentage, double warningThickness, 
			double overloadPercentage, double overloadThickness) {
		this.defaultThickness = defaultThickness;
		this.warningPercentage = warningPercentage;
		this.warningThickness = warningThickness;
		this.overloadPercentage = overloadPercentage;
		this.overloadThickness = overloadThickness;
	}
	
	public double getBranchThickness(BranchData branch) {
		double x = branch.getDCPercentFlow();
		if (x >= overloadPercentage)
			return overloadThickness;
		if (x >= warningPercentage)
			return warningThickness;
		
		return defaultThickness;	
	}
}
