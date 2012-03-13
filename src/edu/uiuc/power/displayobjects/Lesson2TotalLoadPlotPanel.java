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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.*;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.StringUtils;
import org.jfree.data.DomainOrder;

import edu.uiuc.power.dataobjects.BranchData;
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
 
public class Lesson2TotalLoadPlotPanel extends JFrame implements Printable
{
	Lesson2TotalLoadPlotPanel myself;
	ChartPanel  chartPanel_grandeTaille;
	JFreeChart chart;
	Calendar currentCalendar;
	Date minDate, maxDate;	
	Boolean integrate;
	int _timeoutInMilliseconds;
	PowerSystem _psystem;
	int _minuteIncrementPerAnimationStep;
	double outputstep;
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
    TimeSeries totalLoadSeries;

    public void resetTime() {
 
		outputstep = 0;
		_psystem.solve();
		ArrayList<LoadData> loads = getAllLoadsFromPowerSystem();
		for (int i = 0; i < loads.size(); i++) {
			outputstep += loads.get(i).getMWSupplied();
		}
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
		try {
			chart.setTitle("Total power demand: " + textFormatter.valueToString(outputstep) + " MW");
		} catch (ParseException pe) {
			System.err.println(pe.toString());
		}	
		
		//System.out.println("total power demand after solve: " + outputstep);

    	totalLoadSeries.clear();
    	
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
    
    private ArrayList<LoadData> getAllLoadsFromPowerSystem() {
    	return _psystem.getLoads();
    }
    
    public Lesson2TotalLoadPlotPanel(String title, PowerSystem psystem,
    		int timeoutInMilliseconds,
    		int minuteIncrementPerAnimationStep,
    		double deltaXPerTimeout,
    		double ymin, double ymax)
    {
        super(title);
        _minuteIncrementPerAnimationStep = minuteIncrementPerAnimationStep;
        _psystem = psystem;
        _timeoutInMilliseconds = timeoutInMilliseconds;
        GregorianCalendar minCal;
        
        
        totalLoadSeries = new TimeSeries("Total Load",Minute.class);
		minCal = new GregorianCalendar(2006, 12, 10, 0, 0);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		minCal = new GregorianCalendar(2006, 12, 10, 0, 0);
		minDate = minCal.getTime();
		minCal.add(Calendar.HOUR, 24);
		maxDate = minCal.getTime();
		dataset.addSeries(totalLoadSeries);
		
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
		
        myself = this;
        
		chart = ChartFactory.createTimeSeriesChart(
				"Time series chart",
				"Time of day", 
				"Megawatts (MW)", 
				dataset, 
				false,
				false,
				false);

		chart.setTitle("Total power demand: 0.00 MW");
		chart.getXYPlot().setRenderer(new XYAreaRenderer());

		chart.setBackgroundPaint(Color.WHITE);
		
		chart.getXYPlot().getRangeAxis().setRange(ymin,ymax);
		
		DateAxis xaxis = new DateAxis("Time of day");
		xaxis.setMaximumDate(maxDate);
		xaxis.setMinimumDate(minDate);
		DateTickUnit dateTickUnit=new DateTickUnit(DateTickUnit.HOUR,2,new SimpleDateFormat("ha"));
		xaxis.setTickUnit(dateTickUnit);
		chart.getXYPlot().setDomainAxis(xaxis);
		
		chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.YELLOW);
		chart.getXYPlot().getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutline(true);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutlineStroke(new BasicStroke(4.0f));
		
        chartPanel_grandeTaille = new ChartPanel(chart);
        chartPanel_grandeTaille.setMouseZoomable(false);
        Dimension grandeTaille = new Dimension(640, 480);
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
        // A thread to update the model every 2 seconds
        /*
        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    synchronized (this)
                    {
                            try {
								wait(_timeoutInMilliseconds);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                    }
                    
                    synchronized(integrate) {
                    	if (integrate) {
                    		
                			if (currentCalendar.getTime().equals(maxDate)) {
                				stopIntegration();                    		
                			}
                    		
    	    				outputstep = 0;
    	    				ArrayList<LoadData> loads = getAllLoadsFromPowerSystem();
    	    				for (int i = 0; i < loads.size(); i++) {
    	    					outputstep += loads.get(i).getMWSupplied();
    	    				}
    	    				SwingUtilities.invokeLater(new Runnable() {
							
								public void run() {
		    	    				totalLoadSeries.addOrUpdate(new Minute(getCurrentCalendar().getTime()),outputstep);
								}
    	    					
    	    				});
    	    				
    	    				//totalLoadSeries.add(new Minute(getCurrentCalendar().getTime()),outputstep);
    	    				getCurrentCalendar().add(Calendar.MINUTE, getMinuteIncrementPerAnimationStep());
    	    				
    	    				//totalLoadSeries.add(currenttime,currentoutput);
    	    				DecimalFormat decimalFormat = new DecimalFormat("##0.00");
    	    				NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
    	    				try {
    	    					chart.setTitle("Total power demand: " + textFormatter.valueToString(outputstep) + " MW");
    	    				} catch (ParseException pe) {
    	    					System.err.println(pe.toString());
    	    				}
                    	} else {
                    		// Do nothing
                    	}
                    }
                }
            }
        };//.start();
        */
    }
    
    public void updatePlot() {
        synchronized(integrate) {
        	if (integrate) {
        		
    			if (currentCalendar.getTime().equals(maxDate)) {
    				stopIntegration();                    		
    			}
        		
				outputstep = 0;
				ArrayList<LoadData> loads = getAllLoadsFromPowerSystem();
				for (int i = 0; i < loads.size(); i++) {
					outputstep += loads.get(i).getMWSupplied();
				}
				SwingUtilities.invokeLater(new Runnable() {
				
					public void run() {
	    				totalLoadSeries.addOrUpdate(new Minute(getCurrentCalendar().getTime()),outputstep);
					}
					
				});
				
				//totalLoadSeries.add(new Minute(getCurrentCalendar().getTime()),outputstep);
				getCurrentCalendar().add(Calendar.MINUTE, getMinuteIncrementPerAnimationStep());
				
				//totalLoadSeries.add(currenttime,currentoutput);
				DecimalFormat decimalFormat = new DecimalFormat("##0.00");
				NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
				try {
					chart.setTitle("Total power demand: " + textFormatter.valueToString(outputstep) + " MW");
				} catch (ParseException pe) {
					System.err.println(pe.toString());
				}
        	} else {
        		// Do nothing
        	}
        }    	
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return chartPanel_grandeTaille.print(graphics, pageFormat, pageIndex);
	}
}

