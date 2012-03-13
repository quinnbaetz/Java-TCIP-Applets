package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.*;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.StringUtils;
import org.jfree.data.DomainOrder;
import org.jfree.data.RangeType;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.CostAndEmissionsProvider;
import edu.uiuc.power.dataobjects.CumulativeTimeProvider;
import edu.uiuc.power.dataobjects.EnergyUsageProvider;
import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.dataobjects.GeneratorDataWithLinearCostAndEmissions;
import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.dataobjects.NodeData;
import edu.uiuc.power.dataobjects.PowerSystem;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
 
public class Lesson1PlotPanelForKiosk extends JPanel implements Printable, EnergyUsageProvider, CumulativeTimeProvider
{
	Lesson1PlotPanelForKiosk myself;
	ChartPanel chartpanel;
	JFreeChart chart;
	XYCumulativeEnergyPlotListener stopListener;

	Calendar currentCalendar;
	Date minDate,maxDate,originalAxisMinDate,originalAxisMaxDate;
	DateAxis xaxis;
	boolean xaxisUsingAutoRange;
	
	Boolean integrate;
	BranchData branch;
	
	int _minuteIncrementPerAnimationStep;
	double outputCostStep, outputEmissionStep;
	JPanel controlPanel;
	
	DateFormatter dformatter = new DateFormatter(new SimpleDateFormat("HH:mm"));
	
	public boolean getIntegrating() {
		synchronized(integrate) {
			return integrate;
		}
	}
	
	double cumulativeEnergyUsage = 0;
	
	public JPanel getControlPanel() {
		return controlPanel;
	}
	
    private static final String TITLE = "Dynamic XY Series Demo";
    TimeSeries EnergySeries;
    
    public void resetTime() {
    	EnergySeries.clear();
    	cumulativeEnergyUsage = 0;
    	currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
    };
    
    public void startIntegration() {
    	synchronized(integrate) {
    		integrate = true;
    	}
    }
    
    public void stopIntegration() {
    	synchronized(integrate) {
    		integrate = false;
    	}
    }
    
    public int getMinuteIncrementPerAnimationStep() {
    	return _minuteIncrementPerAnimationStep;
    }
    
    public Calendar getCurrentCalendar() {
    	return currentCalendar;
    }

    XYPlot energyPlot;
    
    public Lesson1PlotPanelForKiosk(String title, BranchData branch,
    		int minuteIncrementPerAnimationStep,
    		XYCumulativeEnergyPlotListener stopListener,
    		double ymin, double ymax)
    {
    	super();
    	this.stopListener = stopListener;
        myself = this;
        _minuteIncrementPerAnimationStep = minuteIncrementPerAnimationStep;
        this.branch = branch;
        this.setLayout(new BorderLayout());
        
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
		minDate = currentCalendar.getTime();
		currentCalendar.add(Calendar.MINUTE,-1);
		originalAxisMinDate = currentCalendar.getTime();
		currentCalendar.add(Calendar.MINUTE,+1); // back to midnight
		currentCalendar.add(Calendar.HOUR,6);
		currentCalendar.add(Calendar.MINUTE,30); // original x-axis is (midnight - 1 minute):(6am + 30 minutes)
		originalAxisMaxDate = currentCalendar.getTime();
		currentCalendar.add(Calendar.HOUR, 18);
		currentCalendar.add(Calendar.MINUTE, -1); // stop at 23:59
		maxDate = currentCalendar.getTime();
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);

        EnergySeries = new TimeSeries("Energy consumed",Minute.class);
		TimeSeriesCollection totalEnergyDataset = new TimeSeriesCollection();
		totalEnergyDataset.addSeries(EnergySeries);

		//EnergySeries.add(new Minute(minDate),0.0);
		//EnergySeries.add(new Minute(maxDate),0.0);
		
		/*
		xaxis = new DateAxis("Time of day");
		xaxis.setMaximumDate(maxDate);
		xaxis.setMinimumDate(minDate);
		DateTickUnit dateTickUnit=new DateTickUnit(DateTickUnit.MINUTE,30,new SimpleDateFormat("HH:mm"));
		xaxis.setTickUnit(dateTickUnit);
		xaxis.setDateFormatOverride(new SimpleDateFormat("h:mm a"));		
		//xaxis.setAutoRange(true);
		xaxis.setRange(originalAxisMinDate,originalAxisMaxDate);
		xaxisUsingAutoRange = true;
		*/
		xaxis = new DateAxis("Time of day");
		xaxis.setRange(originalAxisMinDate,originalAxisMaxDate);
		xaxis.setDateFormatOverride(new SimpleDateFormat("h:mm a"));		
		
		NumberAxis yaxis = new NumberAxis("Watts (W)");
		yaxis.setRange(ymin,ymax);
		
		energyPlot = new XYPlot(totalEnergyDataset,xaxis,yaxis,new XYAreaRenderer());
		energyPlot.getRangeAxis().setLabelFont(new Font("Lucida Bright", Font.PLAIN, 11));
		energyPlot.getRenderer().setSeriesPaint(0, Color.YELLOW);
		energyPlot.getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)energyPlot.getRenderer()).setOutline(true);
		energyPlot.getRenderer().setSeriesOutlinePaint(0, Color.BLACK);
		energyPlot.getRenderer().setSeriesOutlineStroke(0, new BasicStroke(2.0f));

        Font chartFont = new Font("SansSerif", Font.BOLD, 18);
        chart = new JFreeChart("Time series chart",chartFont,energyPlot,true); 
		
        chart.removeLegend();
		chart.setTitle("Power Consumption");

		chart.setBackgroundPaint(Color.WHITE);
		
        chartpanel = new ChartPanel(chart);
        chartpanel.setMouseZoomable(false);
        //setContentPane( chartPanel_grandeTaille);
        this.add(chartpanel,BorderLayout.CENTER);
        this.setBackground(Color.WHITE);
        integrate = false;
        
        resetTime();
        
        //this.setSize(grandeTaille);
        this.doLayout();
        //this.setVisible(true);
    }
    
    public void updatePlot() {
    	if (integrate) {
    		if (!xaxisUsingAutoRange && (currentCalendar.getTime().after(originalAxisMaxDate))) {
    			xaxis.setAutoRange(true);
    			xaxis.setAutoTickUnitSelection(true);
    			xaxisUsingAutoRange = true;
    		}
    		
			Calendar tempCalendar = (Calendar)getCurrentCalendar().clone();
			tempCalendar.add(Calendar.MINUTE, getMinuteIncrementPerAnimationStep());
			if (tempCalendar.getTime().after(maxDate)) {
				stopIntegration();
				stopListener.integrationStopped();
			} else {
				cumulativeEnergyUsage += (getMinuteIncrementPerAnimationStep()/60.0) *  branch.getDCMWFlow();
				EnergySeries.addOrUpdate(new Minute(getCurrentCalendar().getTime()),branch.getDCMWFlow());
				currentCalendar.add(Calendar.MINUTE, getMinuteIncrementPerAnimationStep());
			}
    	} else {
    		// Do nothing
    	}
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return chartpanel.print(graphics, pageFormat, pageIndex);
	}

	public double getCurrentEnergyUsage() {
		return cumulativeEnergyUsage;
	}

	public String getCumulativeTime() {
		try {
			return dformatter.valueToString(currentCalendar.getTime());
		} catch (ParseException pe) {
			return "UNABLE TO PARSE DATE!";
		}
	}
}

