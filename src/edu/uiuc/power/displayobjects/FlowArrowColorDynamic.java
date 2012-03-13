package edu.uiuc.power.displayobjects;

import java.awt.Color;

import edu.uiuc.power.dataobjects.BranchData;

public class FlowArrowColorDynamic implements FlowArrowColorProvider {

	static double TOLERANCE = 1e-4;
	
	double warningPercentage, overloadPercentage;
	Color defaultColor, warningColor, overloadColor;	
	
	public FlowArrowColorDynamic(Color defaultColor,
			double warningPercentage, Color warningColor,
			double overloadPercentage, Color overloadColor) {
		this.defaultColor = defaultColor;
		this.warningPercentage = warningPercentage;
		this.warningColor = warningColor;
		this.overloadPercentage = overloadPercentage;
		this.overloadColor = overloadColor;		
	}
		
	public Color getFlowArrowColor(BranchData branch) {
		double x = branch.getDCPercentFlow(); 
		if (x >= (overloadPercentage + TOLERANCE))
			return overloadColor;
		
		if (x >= (warningPercentage + TOLERANCE)) 
			return warningColor;
		
		return defaultColor;	
	}
}
