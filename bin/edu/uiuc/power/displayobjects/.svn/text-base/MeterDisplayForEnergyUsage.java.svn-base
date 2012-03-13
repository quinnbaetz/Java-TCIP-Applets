package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JSlider;
import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.EnergyUsageProvider;

public class MeterDisplayForEnergyUsage extends MeterDisplay {

	EnergyUsageProvider chartPanel;
	NumberFormatter textFormatter;
	BranchData bdata;
	JSlider costSlider;
	
	public MeterDisplayForEnergyUsage(Point2D ULPos, EnergyUsageProvider chartPanel,
			BranchData bdata, JSlider costSlider) {
		super(ULPos);
		this.bdata = bdata;
		this.chartPanel = chartPanel;
		this.costSlider = costSlider;
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		textFormatter = new NumberFormatter(decimalFormat);		
	}

	protected String getMessageTextBottom() {
		try {
			return "Bill: $" + textFormatter.valueToString((chartPanel.getCurrentEnergyUsage() / 1000.0)*((double)(costSlider.getValue()))/100.0) + "";
		} catch (ParseException e) {
			return "Error reading current energy sum";
		}
	}

	@Override
	protected String getMessageTextTop() {
		try {
			return textFormatter.valueToString(chartPanel.getCurrentEnergyUsage() / 1000.0) + " kWh";
		} catch (ParseException e) {
			return "Error reading current energy sum";
		}
	}

	@Override
	public double getOffsetPerAnimationStep() {
		double offsetPerAnimStep = 0.05*bdata.getDCPercentFlow();
		return offsetPerAnimStep;
	}

	
}
