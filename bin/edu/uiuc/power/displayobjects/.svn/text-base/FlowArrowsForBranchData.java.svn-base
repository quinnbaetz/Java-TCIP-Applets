package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import edu.uiuc.power.dataobjects.BranchData;

public class FlowArrowsForBranchData extends FlowArrows {
	
	private BranchData _branch;
	private FlowArrowColorProvider colorProvider;
	private double absoluteArrowRef;
	boolean absoluteArrowRefProvided = false;
	public FlowArrowsForBranchData(BranchData branch, ArrayList<LineAndDistanceInfoProvider> lines, double OffsetStepPerAnimationStep, double triangleLength,
			double distanceBetweenTriangles, FlowArrowColorProvider colorProvider, double absoluteArrowRef) {
		super(lines,OffsetStepPerAnimationStep,triangleLength, distanceBetweenTriangles);
		_branch = branch;
		this.colorProvider = colorProvider;
		this.absoluteArrowRef = absoluteArrowRef;
		if (absoluteArrowRef > 0) 
			absoluteArrowRefProvided = true;
		else
			absoluteArrowRefProvided = false;
	}

	public FlowArrowsForBranchData(BranchData branch, ArrayList<LineAndDistanceInfoProvider> lines, double OffsetStepPerAnimationStep, double triangleLength,
			double distanceBetweenTriangles, FlowArrowColorProvider colorProvider) {
		this(branch,lines,OffsetStepPerAnimationStep,triangleLength,distanceBetweenTriangles,colorProvider,-1.0);
	}
	
	public BranchData getBranchData() {
		return _branch;
	}

	public double getTriangleLength() {
		//ezplot('(1.125*x)/(x+0.125)',[0,1])
		double x = -1;
		if (absoluteArrowRefProvided) {
			x = Math.abs(getBranchData().getMW())/absoluteArrowRef;
		} else
			x = getBranchData().getDCPercentFlow();
		
		x = Math.sqrt(x);
		if ((x < 0.05) & (x >= 1e-5))
			x = 0.05;
		else if (x > 1) 
			x = 1;
		//double coeff = 1.125*x/(x + 0.125);
		double coeff = x;
		
		return coeff*super.getTriangleLength();
	}

	public boolean render(Graphics2D g2d) {
		if (getBranchData().getDCPercentFlow() > 1e-5) 
			return super.render(g2d);
		else
			return true;
	}

	@Override
	public boolean getReverseDirection() {
		if (getBranchData().getDCMWFlow() >= 0)
			return false;
		else
			return true;
		
	}

	@Override
	protected Color getFillColor() {
		return colorProvider.getFlowArrowColor(getBranchData());
	}
	
}
