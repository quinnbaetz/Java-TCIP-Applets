package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class TimeSeriesPlotPanel extends JPanel {
	
	TimeSeries plotTimeSeries;
	DateAxis xaxis;
	XYPlot timePlot;
	JFreeChart chart;
	ChartPanel chartPanel;
	
	public void setFillColor(Color color) {
		timePlot.getRenderer().setSeriesPaint(0, color);
	}
	
	public TimeSeriesPlotPanel (String title, 
    		double ymin, double ymax,
    		int xsize, int ysize, 
    		TimeSeries plotTimeSeries,
    		String yaxisLabel) {
		super();
        TimeSeriesCollection totalDataSet = new TimeSeriesCollection();
        this.plotTimeSeries = plotTimeSeries;
		totalDataSet.addSeries(plotTimeSeries);

		xaxis = new DateAxis("Time of day");
		xaxis.setDateFormatOverride(new SimpleDateFormat("h:mm a"));
		NumberAxis yaxis = new NumberAxis(yaxisLabel);
		yaxis.setAutoRange(true);
        timePlot = new XYPlot(totalDataSet,xaxis,yaxis,new XYAreaRenderer());
        timePlot.getRangeAxis().setLabelFont(new Font("Lucida Bright", Font.PLAIN, 11));
        timePlot.getRenderer().setSeriesPaint(0, Color.GREEN);
		timePlot.getRenderer().setSeriesItemLabelsVisible(0,true);
		LegendItemCollection lic = new LegendItemCollection();
		//timePlot.getRenderer().getL
		((XYAreaRenderer)timePlot.getRenderer()).setOutline(true);
		((XYAreaRenderer)timePlot.getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)timePlot.getRenderer()).setOutlineStroke(new BasicStroke(1.0f));
        
        Font chartFont = new Font("SansSerif", Font.BOLD, 18);
        chart = new JFreeChart("Time series chart",chartFont,timePlot,true); 
        chart.removeLegend();
		chart.setTitle(title);

		chart.setBackgroundPaint(Color.WHITE);
		
		chartPanel = new ChartPanel(chart);
		chartPanel.setMouseZoomable(false);
       // Dimension chartPanelDimensions = new Dimension(xsize, ysize);
       //chartPanel.setMaximumSize(chartPanelDimensions);
       // chartPanel.setPreferredSize(chartPanelDimensions);
       // chartPanel.setMinimumSize(chartPanelDimensions);
        
        JButton printButton = new JButton("Print Plot");
        printButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			    PrinterJob printJob = PrinterJob.getPrinterJob();
			    printJob.setPrintable(chartPanel);
			    if (printJob.printDialog())
			      try {
			        printJob.print();
			      } catch(PrinterException pe) {
			        System.out.println("Error printing: " + pe);
			      }
			}
        	
        });
        
        this.setLayout(new BorderLayout());
        //this.add(printButton,BorderLayout.SOUTH);
        this.add(chartPanel,BorderLayout.CENTER);
        //chartAndControlPanel.setSize(chartPanelDimensions);
        this.doLayout();
        //this.setVisible(true);
	}
}
