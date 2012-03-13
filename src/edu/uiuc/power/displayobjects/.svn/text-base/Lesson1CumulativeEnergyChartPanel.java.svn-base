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
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.*;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.StringUtils;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.NodeData;

import java.util.Random;
 
public class Lesson1CumulativeEnergyChartPanel extends JFrame implements Printable
{
	
	Lesson1CumulativeEnergyChartPanel myself;
	ChartPanel  chartPanel_grandeTaille;
	private double maxTime;
	public JFreeChart chart = null;
	
    private static final String TITLE = "Dynamic XY Series Demo";
    LocalXYDataset xydataset;

    public boolean getIntegrationStatus() {
    	return xydataset.getIntegrationStatus();
    }
    
    public double getCurrentSum() {
    	return xydataset.getCurrentSum();
    }
    
    public double getCurrentTime() {
    	return xydataset.getCurrentTime();
    };
    
    public void startIntegration() {
    	xydataset.startIntegration();
    }
    
    public void stopIntegration() {
    	xydataset.stopIntegration();
    }
    
    public void resetTime() {
    	xydataset.resetTime();
    };
    
    public void updatePlot() {
    	xydataset.updateDataSet();
    }
    
    public Lesson1CumulativeEnergyChartPanel(String title, BranchData branch,
    		int timeoutInMilliseconds,
    		double maxTime,
    		double deltaXPerTimeout,
    		double xmin, double xmax,
    		double ymin, double ymax,
    		XYCumulativeEnergyPlotListener listener)
    {
        super(title);
        this.maxTime = maxTime;
        myself = this;
        xydataset = new LocalXYDataset(branch, deltaXPerTimeout, listener, timeoutInMilliseconds, maxTime, this);
        
        chart = ChartFactory.createXYAreaChart(
				" ", 
				"Time (hours)", 
				"Watts (W)", 
				xydataset, 
				org.jfree.chart.plot.PlotOrientation.VERTICAL, 
				false, 
				false,
				false);
        //chart.setNotify(false);
        
        //xydataset.removeChangeListener(chart.getXYPlot());
		
		chart.setTitle("Power Consumed Over Time");
		chart.setBackgroundPaint(Color.WHITE);
		Font yLabelsFont = new Font("Lucida Bright", Font.PLAIN, 10);
		Font xLabelsFont = new Font("Lucida Bright", Font.PLAIN, 12);
		//chart.getXYPlot().getDomainAxis().setRange(xmin,xmax);
		chart.getXYPlot().getDomainAxis().setRange(new Range(xmin,xmax),true,true);
		chart.getXYPlot().getDomainAxis().setTickLabelFont(xLabelsFont);
		chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Lucida Bright",Font.PLAIN,14));
		//((NumberAxis)chart.getXYPlot().getRangeAxis()).setTickUnit(new NumberTickUnit(20.0));
		chart.getXYPlot().getRangeAxis().setRange(ymin,ymax);
		chart.getXYPlot().getRangeAxis().setTickLabelFont(yLabelsFont);
		chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Lucida Bright",Font.PLAIN,14));
		chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.YELLOW);
		chart.getXYPlot().getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutline(true);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutlineStroke(new BasicStroke(4.0f));
		chart.setAntiAlias(false);
		
        chartPanel_grandeTaille = new ChartPanel(chart);
        
		chartPanel_grandeTaille.setMouseZoomable(false);
        Dimension grandeTaille = new Dimension(160, 120);
        chartPanel_grandeTaille.setMaximumSize(grandeTaille);
        chartPanel_grandeTaille.setPreferredSize(grandeTaille);
        chartPanel_grandeTaille.setMinimumSize(grandeTaille);
        //setContentPane( chartPanel_grandeTaille);
        getContentPane().add(chartPanel_grandeTaille,BorderLayout.CENTER);
        JPopupMenu chartPopup = chartPanel_grandeTaille.getPopupMenu();
        MenuElement[] chartPopupElements = chartPopup.getSubElements();
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
        getContentPane().add(printButton,BorderLayout.PAGE_END);
    }
    
    private static JFreeChart createChart(XYDataset xydataset)
    {
        JFreeChart jfreechart = ChartFactory.createXYLineChart(TITLE, "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);
        return jfreechart;
    }
    
    private static class LocalXYDataset extends AbstractXYDataset
    {
        double[] xValues_ = new double[0];
        double[] yValues_ = new double[0];
        
        int fireEventChangeCounter = 0;
        final int eventChangeCounterMax = 50; 
        BranchData _branch;
        boolean fireDataStateChangedToggle = true;

        double _maxTime;
        double currentTime = 0;
        double currentSum = 0;
        int _timeoutInMilliseconds = 100;
        XYCumulativeEnergyPlotListener _listener;

        Lesson1CumulativeEnergyChartPanel _chart;
        
        double _deltaXPerTimeout;
        
        Boolean integrate = new Boolean(false);
        
        public double getCurrentSum() {
        	return currentSum;
        }
        
        public boolean getIntegrationStatus() {
        	boolean retval = false;
        	synchronized(integrate) {
        		retval = integrate;
        	}
        	return retval;
        }
        
        public double getCurrentTime() {
        	return currentTime;
        }
        
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
        
        public void resetTime() {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                	currentTime = 0;
                	currentSum = 0;
                	xValues_ = new double[0];
                	yValues_ = new double[0];
                	_chart.chart.getXYPlot().getDomainAxis().setRange(0.0,6.0);
                    // So now we tell all the listeners that the
                    // model has bee updated.
                    fireDatasetChanged();
                }
            });
        }
        
        private LocalXYDataset(BranchData branch, double deltaXPerTimeout, 
        		XYCumulativeEnergyPlotListener listener, int timeoutInMilliseconds,
        		double maxTime, Lesson1CumulativeEnergyChartPanel chart)
        {
        	_chart = chart;
        	_deltaXPerTimeout = deltaXPerTimeout;
        	_branch = branch; 
        	_listener = listener;
        	_maxTime = maxTime;
        	_timeoutInMilliseconds = timeoutInMilliseconds;
        }
        
        public void updateDataSet() {
            synchronized(integrate) {
            	if (integrate) {
                    // Compute the new set of data points\
                    final double[] newXValues;
                    final double[] newYValues;
                    
					double outputstep = _branch.getDCMWFlow();
					
					double timestep = _deltaXPerTimeout;
					
					double currentoutput = outputstep;
					
					int newsize = xValues_.length + 2;
                    
					newXValues = new double[newsize];
					System.arraycopy(xValues_, 0, newXValues, 0, xValues_.length);
					newYValues = new double[newsize];
					System.arraycopy(yValues_, 0, newYValues, 0, yValues_.length);

					currentTime += timestep;
					/*
					if (_chart.chart != null) {
    					if (currentTime >= 6.0/1.1) {
    						_chart.chart.getXYPlot().getDomainAxis().setAutoRange(true);
    						_chart.chart.getXYPlot().getDomainAxis().setUpperMargin(0.1);
    					} else {
    						_chart.chart.getXYPlot().getDomainAxis().setRange(0.0,6.0);
    					}
					}
					*/
            		
					if (currentTime > (_maxTime + timestep/2)) {
						integrate = false;
						currentTime -= timestep;
						_listener.integrationStopped();
					} else {
    					currentSum += outputstep*timestep;
    					if (newsize < 3) {
    						newXValues[newXValues.length - 2] = 0;
    						newYValues[newYValues.length - 2] = outputstep;
    					} else {
        					newXValues[newXValues.length - 2] = newXValues[newXValues.length - 3];
        					newYValues[newYValues.length - 2] = outputstep;
    					}
    					
                        newXValues[newXValues.length - 1] = currentTime;
                        newYValues[newYValues.length - 1] = currentoutput;
                        // We have to actually update the model values in the event thread
                        // to make sure that they are consistent. This is a quick operation
                        // because all we are doing is updating the references to the data arrays.
                        if (true) { //(fireEventChangeCounter++ > eventChangeCounterMax) { //; (fireDataStateChangedToggle) {
                        	/*
                        	SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                            */
                                    xValues_ = newXValues;
                                    yValues_ = newYValues;
                                    
                                    // So now we tell all the listeners that the
                                    // model has bee updated.
                                    fireDatasetChanged();
                            /*
                                }
                                
                            });
                            */
                            //fireDataStateChangedToggle = false;
                        } else {
                        	System.out.println("waiting...");
                        }
					}
					
            	} else {
            		// Do nothing
            	}
            }        	
        }
        
        public DomainOrder getDomainOrder()
        {
            return DomainOrder.ASCENDING;
        }
        
        public double getXValue(int series, int item)
        {
            return xValues_[item];
        }
        
        public double getYValue(int series, int item)
        {
            return yValues_[item];
        }
        
        public int getSeriesCount()
        {
            return 1;
        }
        
        public Comparable getSeriesKey(int series)
        {
            return "Dynamic values";
        }
        
        public Number getX(int series, int item)
        {
            return new Double(getXValue(series, item));
        }
        
        public Number getY(int series, int item)
        {
            return new Double(getYValue(series, item));
        }
        
        public int getItemCount(int series)
        {
            return xValues_.length;
        }
    }

    public ChartPanel getChartPanel() {
    	return chartPanel_grandeTaille;
    }
    
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return chartPanel_grandeTaille.print(graphics, pageFormat, pageIndex);
	}
}

