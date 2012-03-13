package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

//Import the JFreeChart classes
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.*;
import org.jfree.data.general.*;
import org.jfree.data.time.*;
import org.jfree.ui.*;


import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.dataobjects.PowerSystem;

public class TotalLoadPlot implements Renderable, Animatable {
	/*
	@Override
	protected void paintComponent(Graphics g) {
		chart.draw((Graphics2D)g, new Rectangle2D.Double(20,5,getWidth()-40,getHeight()-10));
	}
	*/

	JFreeChart chart;
	TimeSeries totalLoadSeries;
	PowerSystem _psystem;
	int _minuteIncrementPerAnimationStep;
	
	Calendar currentCalendar;
	Date maxDate;
	double currentoutput = 0;
	
	public int RUNNING = 0;
	public int STOPPED = 1;
	
	int integrationStatus = STOPPED;
	
	public int getIntegrationStatus() {
		return integrationStatus;
	}
	
	private void setIntegrationStatus(int value) {
		integrationStatus = value;
	}
	
	public void startIntegration() {
		integrationStatus = RUNNING;
	}
	
	public void stopIntegration() {
		integrationStatus = STOPPED;
	}
	
	Point2D _ULCorner;
	
	public Point2D getULCorner() {
		return _ULCorner;
	}

	public TotalLoadPlot(Point2D ULCorner, int width, int height, 
			PowerSystem psystem, int minuteIncrementPerAnimationStep,
			double xMin, double xMax,
			double yMin, double yMax) {
		_ULCorner = ULCorner;
		_width = width;
		_height = height;
		_psystem = psystem;
		_minuteIncrementPerAnimationStep = minuteIncrementPerAnimationStep;
		GregorianCalendar minCal;
		Date minDate;
		
		totalLoadSeries = new TimeSeries("Total Load",Minute.class);
		minCal = new GregorianCalendar(2006, 12, 10, 0, 0);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		minCal = new GregorianCalendar(2006, 12, 10, 0, 0);
		minDate = minCal.getTime();
		minCal.add(Calendar.HOUR, 24);
		maxDate = minCal.getTime();
		
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
		
		dataset.addSeries(totalLoadSeries);
		//XYSeriesCollection dataset = new XYSeriesCollection();
		//dataset.addSeries(xyseries);
		
		chart = ChartFactory.createTimeSeriesChart(
				"Time series chart",
				"Time of day", 
				"Megawatts (MW)", 
				dataset, 
				false,
				false,
				false);
		
		chart.getXYPlot().setRenderer(new XYAreaRenderer());
		chart.setTitle("Total power demand: 0.00 MW");
		chart.getXYPlot().setRenderer(new XYAreaRenderer());

		chart.setBackgroundPaint(Color.WHITE);
		
		chart.getXYPlot().getRangeAxis().setRange(yMin,yMax);
		
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
		
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
	}
	
	public int getMinuteIncrementPerAnimationStep() {
		return _minuteIncrementPerAnimationStep;
	}
	
	public void setMinuteIncrementPerAnimationStep(int minuteIncrementPerAnimationStep) {
		_minuteIncrementPerAnimationStep = minuteIncrementPerAnimationStep;
	}
	
	public PowerSystem getPowerSystem() {
		return _psystem;
	}
	
	public ArrayList<LoadData> getAllLoadsFromPowerSystem() {
		return getPowerSystem().getLoads();
	}
	
	public Calendar getCurrentCalendar() {
		return currentCalendar;
	}
	
	public void resettime() {
		totalLoadSeries.clear();
		currentCalendar = new GregorianCalendar(2006, 12, 10, 0, 0);
		currentoutput = 0;
		totalLoadSeries.fireSeriesChanged();
		chart.setTitle("Total power demand: 0.00 MW");
		//chart.getXYPlot().getDomainAxis().setRange(0,24);
		//this.repaint();
	}

	public synchronized boolean animationstep() {
		if (getIntegrationStatus() == RUNNING) {
			if (currentCalendar.getTime().equals(maxDate)) {//currentCalendar.get(Calendar.HOUR) > 24) {
				stopIntegration();
				return false;
			} else {
				double outputstep = 0;
				ArrayList<LoadData> loads = getAllLoadsFromPowerSystem();
				for (int i = 0; i < loads.size(); i++) {
					outputstep += loads.get(i).getMWSupplied();
				}
				
				currentoutput = outputstep;
				
				totalLoadSeries.add(new Minute(getCurrentCalendar().getTime()),currentoutput);
				
				getCurrentCalendar().add(Calendar.MINUTE, getMinuteIncrementPerAnimationStep());
				
				//totalLoadSeries.add(currenttime,currentoutput);
				DecimalFormat decimalFormat = new DecimalFormat("##0.00");
				NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
				try {
					chart.setTitle("Total power demand: " + textFormatter.valueToString(currentoutput) + " MW");
				} catch (ParseException pe) {
					System.err.println(pe.toString());
				}		
				/*
				if (chart.getXYPlot().getDomainAxis().getRange().getUpperBound() < currenttime) {
					chart.getXYPlot().getDomainAxis().setRange(0,currenttime);	
				}
				*/
				
				return true;
			}
		} else 
			return false;
		
	}
	
	int _width,_height;
	
	public int getWidth() {
		return _width;
	}
	
	public int getHeight() {
		return _height;
	}

	public boolean render(Graphics2D g2d) {
		AffineTransform currentXForm = g2d.getTransform();
		g2d.translate(getULCorner().getX(), getULCorner().getY());
		try {
			chart.draw(g2d, new Rectangle2D.Double(20,5,getWidth()-40,getHeight()-10));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g2d.setColor(Color.BLACK);
		g2d.drawRect(3, 3, getWidth()-6, getHeight()-6);
		g2d.setTransform(currentXForm);
		return true;
	}
}
