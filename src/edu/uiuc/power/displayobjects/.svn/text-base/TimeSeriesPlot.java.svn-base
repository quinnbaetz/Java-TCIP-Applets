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
import org.jfree.data.time.RegularTimePeriod;
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
import edu.uiuc.power.dataobjects.MWTimeSeries;
import edu.uiuc.power.dataobjects.NodeData;
import edu.uiuc.power.dataobjects.PowerSystem;
import edu.uiuc.power.dataobjects.TransmissionAndDistributionCosts;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
 
public class TimeSeriesPlot extends JFrame implements Printable, Animatable
{
	TimeSeriesPlot myself;
	ChartPanel  chartPanel_grandeTaille;
	JFreeChart chart;
	Date minDate, maxDate;	
	JPanel controlPanel;
	
	public JPanel getControlPanel() {
		return controlPanel;
	}
	
    XYPlot timePlot;
    DateAxis xaxis;
    TimeSeries plotTimeSeries;
    
    public TimeSeriesPlot(String title, 
    		double ymin, double ymax,
    		int xsize, int ysize, 
    		TimeSeries plotTimeSeries,
    		String yaxisLabel)
    {
    	super(title);
        myself = this;
        
        TimeSeriesCollection totalDataSet = new TimeSeriesCollection();
        this.plotTimeSeries = plotTimeSeries;
		totalDataSet.addSeries(plotTimeSeries);

		xaxis = new DateAxis("Time of day");
		//xaxis.setMaximumDate(maxDate);
		//xaxis.setMinimumDate(minDate);
		//DateTickUnit dateTickUnit=new DateTickUnit(DateTickUnit.HOUR,2,new SimpleDateFormat("ha"));
		//xaxis.setTickUnit(dateTickUnit);
		xaxis.setDateFormatOverride(new SimpleDateFormat("h:mm a"));
		NumberAxis yaxis = new NumberAxis(yaxisLabel);
		yaxis.setAutoRange(true);
		//yaxis.setLowerBound(ymin);
		//yaxis.setUpperBound(ymax);
        timePlot = new XYPlot(totalDataSet,xaxis,yaxis,new XYAreaRenderer());
        timePlot.getRangeAxis().setLabelFont(new Font("Lucida Bright", Font.PLAIN, 11));
        timePlot.getRenderer().setSeriesPaint(0, Color.GREEN);
		timePlot.getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)timePlot.getRenderer()).setOutline(true);
		((XYAreaRenderer)timePlot.getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)timePlot.getRenderer()).setOutlineStroke(new BasicStroke(1.0f));
        
        Font chartFont = new Font("SansSerif", Font.BOLD, 18);
        chart = new JFreeChart("Time series chart",chartFont,timePlot,true); 
		
		chart.setTitle(title);

		chart.setBackgroundPaint(Color.WHITE);
		
        chartPanel_grandeTaille = new ChartPanel(chart);
        chartPanel_grandeTaille.setMouseZoomable(false);
        Dimension grandeTaille = new Dimension(xsize, ysize);
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
        
        this.setSize(grandeTaille);
        this.pack();
        this.setVisible(true);
    }    

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return chartPanel_grandeTaille.print(graphics, pageFormat, pageIndex);
	}

	public boolean animationstep() {
		return false;
	}
}

