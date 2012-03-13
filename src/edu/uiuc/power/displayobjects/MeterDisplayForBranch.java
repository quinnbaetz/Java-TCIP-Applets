package edu.uiuc.power.displayobjects;

import java.awt.Graphics2D;
import java.awt.geom.*;
import java.text.DecimalFormat;

import edu.uiuc.power.dataobjects.BranchData;

public class MeterDisplayForBranch extends MeterDisplay {
	
	private BranchData _bdata;
	
	public MeterDisplayForBranch(Point2D ULPos, BranchData bdata ) {
		super(ULPos);
		_bdata = bdata;
	}

	@Override
	protected String getMessageTextTop() {
		DecimalFormat df = new DecimalFormat("0");
		return df.format(getBranchData().getDCMWFlow()) + " W";
	}

	@Override
	protected String getMessageTextBottom() {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(getBranchData().getDCMWFlow()/1000.0) + " kW";
	}

	public BranchData getBranchData() {
		return _bdata;
	}
	
	@Override
	public double getOffsetPerAnimationStep() {
		double offsetPerAnimStep = 0.05*getBranchData().getDCPercentFlow();
		return offsetPerAnimStep;
	}

}
