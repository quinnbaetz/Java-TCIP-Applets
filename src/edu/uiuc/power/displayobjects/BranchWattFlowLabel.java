package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class BranchWattFlowLabel extends DataLabel {
	
	private BranchData _branch;

	public BranchWattFlowLabel(Point2D ULCorner, int textsize, Color textcolor, double rotationAngle, BranchData branch) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		_branch = branch;
		this.setHorizontalAlignment(DataLabelHorizontalAlignment.LEFT);
	}

	public BranchData getBranch() {
		return _branch;
	}
	
	@Override
	public String getCaption() {
		try {
			String flowText = "Current power consumed: " + textFormatter.valueToString(getBranch().getDCMWFlow()) + " W";
			//String percentText = textFormatter.valueToString(Math.abs(getBranch().getDCPercentFlow()*100)) + "%";
			return flowText;
			//return flowText + " (" + percentText + ")"; 
		} catch (ParseException pe) {
			return pe.toString();
		}			
	}
}
