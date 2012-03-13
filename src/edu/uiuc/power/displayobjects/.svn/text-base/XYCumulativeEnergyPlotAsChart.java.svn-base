package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.uiuc.power.dataobjects.BranchData;

public class XYCumulativeEnergyPlotAsChart implements Renderable, Animatable {
	private static Boolean seriesMutex = new Boolean(true);
	
	JFreeChart chart;
	XYSeries xyseries;
	BranchData _branch;
	double _timeIncrementPerAnimationStep;
	double xMin;
	double xMax;
	double yMin;
	double yMax;
	
	double currenttime = 0;
	double currentoutput = 0;
	double currentsum = 0;
	
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
		plotListener.integrationStopped();
	}
	
	protected synchronized void paintComponent(Graphics g) {

	}
	
	Point2D _ULCorner;
	
	public Point2D getULCorner() {
		return _ULCorner;
	}

	public double getCurrentSum() {
		return currentsum;
	}
	
	XYCumulativeEnergyPlotListener plotListener;
	
	public XYCumulativeEnergyPlotAsChart(Point2D ULCorner, int width, int height, 
			BranchData branch, double timeIncrementPerAnimationStep,
			double xMin, double xMax,
			double yMin, double yMax,
			XYCumulativeEnergyPlotListener plotListener) {
		_ULCorner = ULCorner;
		_width = width;
		_height = height;
		_branch = branch;
		_timeIncrementPerAnimationStep = timeIncrementPerAnimationStep;
		
		this.plotListener = plotListener;
		
		xyseries = new XYSeries("XYGraph");
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(xyseries);
		
		chart = ChartFactory.createXYAreaChart(
				" ", 
				"Time (hours)", 
				"Watts (W)", 
				dataset, 
				org.jfree.chart.plot.PlotOrientation.VERTICAL, 
				false, 
				false, 
				false);
		
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		
		chart.setTitle("Power Consumed Over Time");
		chart.setBackgroundPaint(Color.WHITE);
		Font yLabelsFont = new Font("Lucida Bright", Font.PLAIN, 10);
		Font xLabelsFont = new Font("Lucida Bright", Font.PLAIN, 12);
		chart.getXYPlot().getDomainAxis().setTickLabelFont(xLabelsFont);
		chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Lucida Bright",Font.PLAIN,14));
		chart.getXYPlot().getDomainAxis().setRange(xMin,xMax);
		//((NumberAxis)chart.getXYPlot().getRangeAxis()).setTickUnit(new NumberTickUnit(20.0));
		chart.getXYPlot().getRangeAxis().setRange(yMin,yMax);
		chart.getXYPlot().getRangeAxis().setTickLabelFont(yLabelsFont);
		chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Lucida Bright",Font.PLAIN,14));
		chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.YELLOW);
		chart.getXYPlot().getRenderer().setSeriesItemLabelsVisible(0,true);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutline(true);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutlinePaint(Color.BLACK);
		((XYAreaRenderer)chart.getXYPlot().getRenderer()).setOutlineStroke(new BasicStroke(4.0f));
		
	}
	
	public double getTimeIncrementPerAnimationStep() {
		return _timeIncrementPerAnimationStep;
	}
	
	public void setTimeIncrementPerAnimationStep(double timeIncrementPerAnimationStep) {
		_timeIncrementPerAnimationStep = timeIncrementPerAnimationStep;
	}
	
	public BranchData getBranch() {
		return _branch;
	}
	
	public double getcurrenttime() {
		return currenttime;
	}
	
	public void resettime() {
		synchronized(seriesMutex) {
			xyseries.clear();
			currenttime = 0;
			currentoutput = 0;
			currentsum = 0;
			xyseries.fireSeriesChanged();
			chart.setTitle("Power Consumed Over Time");
			chart.getXYPlot().getDomainAxis().setRange(xMin,xMax);
			//this.repaint();
		}
	}

	public synchronized boolean animationstep() {
		synchronized(seriesMutex) {
			if (getIntegrationStatus() == RUNNING) {
				if (currenttime >= xMax) {
					stopIntegration();
					return false;
				} else {
					double outputstep = getBranch().getDCMWFlow();
					
					double timestep = getTimeIncrementPerAnimationStep();
					
					currentoutput = outputstep;
					
					xyseries.add(currenttime,currentoutput);
					
					currenttime += timestep;
					double timediff = 0;
					if (currenttime >= xMax) {
						timediff = currenttime - xMax;
						currenttime -= timediff;
					}
					
					currentsum += (timestep - timediff) * outputstep;
					
					xyseries.add(currenttime,currentoutput);
					
					//DecimalFormat decimalFormat = new DecimalFormat("##0.00");
					//NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
					
					if (chart.getXYPlot().getDomainAxis().getRange().getUpperBound() < currenttime) {
						chart.getXYPlot().getDomainAxis().setRange(0,currenttime);	
					}
								
					return true;
				}
			} else 
				return false;
		}
	}
	
	int _width,_height;
	
	public int getWidth() {
		return _width;
	}
	
	public int getHeight() {
		return _height;
	}

	public boolean render(Graphics2D g2d) {
		synchronized(seriesMutex) {
			AffineTransform currentXForm = g2d.getTransform();
			g2d.translate(getULCorner().getX(), getULCorner().getY());
			chart.draw(g2d, new Rectangle2D.Double(20,5,getWidth()-40,getHeight()-10));
			g2d.setColor(Color.BLACK);
			g2d.drawRect(3, 3, getWidth()-6, getHeight()-6);
			g2d.setTransform(currentXForm);
			return true;
		}
	}
}
