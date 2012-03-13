package edu.uiuc.power.displayobjects;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.text.NumberFormatter;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.CostAndDemandTimeSeries;
import edu.uiuc.power.dataobjects.EnergyUsageProvider;

public class MeterDisplayForTimeOfUseEnergyAndCost extends MeterDisplay {

	NumberFormatter textFormatter;
	JSlider costSlider;
	CostAndDemandTimeSeries cdTimeSeries;
	
	public MeterDisplayForTimeOfUseEnergyAndCost(Point2D ULPos, CostAndDemandTimeSeries cdTimeSeries, JSlider costSlider) {
		super(ULPos);
		this.cdTimeSeries = cdTimeSeries;
		this.costSlider = costSlider;
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		textFormatter = new NumberFormatter(decimalFormat);		
	}

	protected String getMessageTextBottom() {
		try {
			return "Bill: $" + textFormatter.valueToString(cdTimeSeries.getRunningCostTotal()) + "";
		} catch (ParseException e) {
			return "Error reading current energy sum";
		}
	}

	@Override
	protected String getMessageTextTop() {
		try {
			return textFormatter.valueToString(cdTimeSeries.getRunningEnergyTotal()) + " kWh";
		} catch (ParseException e) {
			return "Error reading current energy sum";
		}
	}

	@Override
	public double getOffsetPerAnimationStep() {
		double offsetPerAnimStep = 0.05*cdTimeSeries.getDemandInW();
		return offsetPerAnimStep;
	}

	
}
