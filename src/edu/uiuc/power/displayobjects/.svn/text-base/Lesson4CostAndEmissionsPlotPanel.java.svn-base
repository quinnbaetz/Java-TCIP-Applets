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
import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.dataobjects.GeneratorDataWithLinearCostAndEmissions;
import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.dataobjects.NodeData;
import edu.uiuc.power.dataobjects.PowerSystem;
import edu.uiuc.power.dataobjects.TransmissionAndDistributionCosts;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
 
public class Lesson4CostAndEmissionsPlotPanel extends JFrame implements Printable, Lesson4TimeProvider,
	TotalCostProvider, TotalEmissionsProvider
{
	Lesson4CostAndEmissionsPlotPanel myself;
	ChartPanel  chartPanel_grandeTaille;
	JFreeChart chart;
	Calendar currentCalendar;
	Date minDate, maxDate;	
	Boolean integrate;
	int _timeoutInMilliseconds;
	PowerSystem _psystem;
	TransmissionAndDistributionCosts tdcosts;
	int _minuteIncrementPerAnimationStep;
	double outputCostStep, outputEmissionStep;
	JPanel controlPanel;
	
	public boolean getIntegrating() {
		synchronized(integrate) {
			return integrate;
		}
	}
	
	public JPanel getControlPanel() {
		return controlPanel;
	}
	
    private static final String TITLE = "Dynamic XY Series Demo";
    TimeSeries totalCostSeries,totalEmissionSeries;

    private void setupOutputSteps() {
		outputCostStep = 0;
		outputEmissionStep = 0;
		
		ArrayList<CostAndEmissionsProvider> costProviders = getAllCostProvidersFromPowerSystem();
		for (int i = 0; i < costProviders.size(); i++) {
			outputCostStep += costProviders.get(i).getCost(1);
			outputEmissionStep += costProviders.get(i).getEmissions(1);
		}
    }
    
    private void updateTitle() {
		DecimalFormat costFormat = new DecimalFormat("$#,##0");
		NumberFormatter costFormatter = new NumberFormatter(costFormat);
		
		DecimalFormat emissionsFormat = new DecimalFormat("#,##0");
		NumberFormatter emissionsFormatter = new NumberFormatter(emissionsFormat);
		try {
			chart.setTitle("Total cost per hour: " + costFormatter.valueToString(outputCostStep) + System.getProperty("line.separator") + 
					"Total emissions per hour: " + emissionsFormatter.valueToString(outputEmissionStep) + " metric tons of CO2");
		} catch (ParseException pe) {
			System.err.println(pe.toString());
		}	
    }
    
    
    public void resetTime() {
 
		_psystem.solve();
		
		setupOutputSteps();
		
		updateTitle();
		
		//System.out.println("total power demand after solve: " + outputstep);

    	totalCostSeries.clear();
    	totalEmissionSeries.clear();
    	
    	chart.fireChartChanged();
    	currentCalendar.setTime(minDate);

	
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
    
    public PowerSystem getPowerSystem() {
    	return _psystem;
    }
    
    public Calendar getCurrentCalendar() {
    	return currentCalendar;
    }

    private ArrayList<CostAndEmissionsProvider> getAllCostProvidersFromPowerSystem() {
    	ArrayList<GeneratorData> allGenData = _psystem.getGenerators();
    	ArrayList<CostAndEmissionsProvider> retdata = new ArrayList<CostAndEmissionsProvider>();
    	for (int i = 0; i < allGenData.size(); i++) {
    		if (allGenData.get(i) instanceof CostAndEmissionsProvider) {
    			retdata.add((CostAndEmissionsProvider)allGenData.get(i));
    		}
    	}
    	ArrayList<LoadData> allLoadData = _psystem.getLoads();
    	for (int i = 0; i < allLoadData.size(); i++) {
    		if (allLoadData.get(i) instanceof CostAndEmissionsProvider) {
    			retdata.add((CostAndEmissionsProvider)allLoadData.get(i));
    		}
    	}
    	retdata.add(tdcosts);
    	return retdata;
    }
    
    XYPlot costPlot;
    
    public Lesson4CostAndEmissionsPlotPanel(String title, PowerSystem psystem,
    		TransmissionAndDistributionCosts tdcosts,
    		int timeoutInMilliseconds,
    		int minuteIncrementPerAnimationStep,
    		double deltaXPerTimeout,
    		double ymin, double ymax)
    {
    	super(title);
    	this.tdcosts = tdcosts;
        myself = this;
        _minuteIncrementPerAnimationStep = minuteIncrementPerAnimationStep;
        _psystem = psystem;
        _timeoutInMilliseconds = timeoutInMilliseconds;
        
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
		minDate = currentCalendar.getTime();
		currentCalendar.add(Calendar.HOUR, 24);
		maxDate = currentCalendar.getTime();
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);

        totalCostSeries = new TimeSeries("Total Cost",Minute.class);
		TimeSeriesCollection totalCostDataset = new TimeSeriesCollection();
		totalCostDataset.addSeries(totalCostSeries);

		totalEmissionSeries = new TimeSeries("Total Emissions",Minute.class);
		TimeSeriesCollection totalEmissionDataset = new TimeSeriesCollection();
		totalEmissionDataset.addSeries(totalEmissionSeries);
		
		totalCostSeries.add(new Minute(minDate),10.0);
		totalCostSeries.add(new Minute(maxDate),20.0);
		totalEmissionSeries.add(new Minute(minDate),100.0);
		totalEmissionSeries.add(new Minute(maxDate),200.0);
        
		DateAxis xaxis = new DateAxis("Time of day");
		xaxis.setMaximumDate(maxDate);
		xaxis.setMinimumDate(minDate);
		DateTickUnit dateTickUnit=new DateTickUnit(DateTickUnit.HOUR,2,new SimpleDateFormat("ha"));
		xaxis.setTickUnit(dateTickUnit);
		
        costPlot = new XYPlot(totalCostDataset,xaxis,new NumberAxis("Cost ($/hr)"),new XYAreaRenderer());
        costPlot.getRangeAxis().setLabelFont(new Font("Lucida Bright", Font.PLAIN, 11));
        costPlot.getRenderer().setSeriesPaint(0, Color.GREEN);
		costPlot.getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)costPlot.getRenderer()).setOutline(true);
		((XYAreaRenderer)costPlot.getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)costPlot.getRenderer()).setOutlineStroke(new BasicStroke(4.0f));
        
        XYPlot emissionsPlot = new XYPlot(totalEmissionDataset,xaxis,new NumberAxis("Emissions (tons/hr)"),new XYAreaRenderer());
        emissionsPlot.getRangeAxis().setLabelFont(new Font("Lucida Bright", Font.PLAIN, 11));
        emissionsPlot.getRenderer().setSeriesPaint(0, Color.GRAY);
        emissionsPlot.getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)emissionsPlot.getRenderer()).setOutline(true);
		((XYAreaRenderer)emissionsPlot.getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)emissionsPlot.getRenderer()).setOutlineStroke(new BasicStroke(4.0f));

        CombinedDomainXYPlot chartXYPlot = new CombinedDomainXYPlot(xaxis);
        chartXYPlot.add(costPlot);
        chartXYPlot.add(emissionsPlot);
        
        Font chartFont = new Font("SansSerif", Font.BOLD, 18);
        chart = new JFreeChart("Time series chart",chartFont,chartXYPlot,true); 
		
		chart.setTitle("Total power demand: 0.00 MW");

		chart.setBackgroundPaint(Color.WHITE);
		
		costPlot.getRenderer().setSeriesPaint(0, Color.YELLOW);
		costPlot.getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)costPlot.getRenderer()).setOutline(true);
		((XYAreaRenderer)costPlot.getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)costPlot.getRenderer()).setOutlineStroke(new BasicStroke(4.0f));
		
        chartPanel_grandeTaille = new ChartPanel(chart);
        chartPanel_grandeTaille.setMouseZoomable(false);
        Dimension grandeTaille = new Dimension(800, 600);
        chartPanel_grandeTaille.setMaximumSize(grandeTaille);
        chartPanel_grandeTaille.setPreferredSize(grandeTaille);
        chartPanel_grandeTaille.setMinimumSize(grandeTaille);
        //setContentPane( chartPanel_grandeTaille);
        getContentPane().add(chartPanel_grandeTaille,BorderLayout.CENTER);
        
        JButton printButton = new JButton("Print Plot");
        printButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			    PrinterJob printJob = PrinterJob.getPrinterJob();
			    printJob.setPrintable(myself);
			    if (printJob.printDialog())
			      try {
			        printJob.print();
			      } catch(PrinterException pe) {
			        System.out.println("Error printing: " + pe);
			      }
			}
        	
        });
        
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(printButton, BorderLayout.PAGE_END);
        getContentPane().add(controlPanel,BorderLayout.PAGE_END);
        //getContentPane().add(printButton,BorderLayout.PAGE_END);
        
        integrate = false;
        
        resetTime();
        
        this.setSize(grandeTaille);
        this.pack();
        this.setVisible(true);
    }
    
    public void updatePlot() {
    	setupOutputSteps();
    	if (integrate) {
			
			getCurrentCalendar().add(Calendar.MINUTE, getMinuteIncrementPerAnimationStep());
			if (currentCalendar.getTime().equals(maxDate)) {
				stopIntegration();
			} else {
				totalCostSeries.addOrUpdate(new Minute(getCurrentCalendar().getTime()),outputCostStep);
				/*
				if (outputCostStep > 0)
		    		costPlot.getRenderer().setSeriesPaint(0, Color.RED);
				else
					costPlot.getRenderer().setSeriesPaint(0, Color.GREEN);
				*/
				totalEmissionSeries.addOrUpdate(new Minute(getCurrentCalendar().getTime()),outputEmissionStep);
			}
    	} else {
    		// Do nothing
    	}
        updateTitle();        	
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return chartPanel_grandeTaille.print(graphics, pageFormat, pageIndex);
	}

	public double getTotalCost() {
		setupOutputSteps();
		return outputCostStep;
	}

	public double getTotalEmissions() {
		setupOutputSteps();
		return outputEmissionStep;
	}
}

