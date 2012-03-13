package edu.uiuc.power.displayobjects;

import java.awt.Color;

import edu.uiuc.power.dataobjects.BranchData;

public class BranchColorStatic implements BranchColorProvider {

	Color staticColor;
	
	public BranchColorStatic (Color staticColor) {
		this.staticColor = staticColor;
	}
	
	public Color getBranchColor(BranchData branch) {
		return staticColor;
	}

}
