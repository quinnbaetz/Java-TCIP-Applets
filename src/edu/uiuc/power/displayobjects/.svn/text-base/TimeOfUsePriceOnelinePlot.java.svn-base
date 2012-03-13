package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.SimulationClock;
import edu.uiuc.power.dataobjects.TimeOfUsePriceProvider;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelVerticalAlignment;

public class TimeOfUsePriceOnelinePlot implements Renderable {

	private TimeOfUsePriceProvider tProvider;
	private double x,y,height,width;
	SimpleDateFormat sdformat = new SimpleDateFormat("d:H:m");
	SimulationClock simClock;
	double minCost,maxCost;
	int initialDayOfYear = 0;
	private BasicTextLabel xaxisDayLabel;
	
	public TimeOfUsePriceOnelinePlot(TimeOfUsePriceProvider tProvider, double x,
			double y, double width, double height, SimulationClock simClock) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.tProvider = tProvider;
		this.simClock = simClock;
		this.initialDayOfYear = simClock.getCurrentCalendar().get(Calendar.DAY_OF_YEAR);
		try {
			createPlotPoints(true);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setTimeOfUsePriceProvider(TimeOfUsePriceProvider tProvider) {
		this.tProvider = tProvider;
		try {
			if (tProvider.getNumOfBreakpoints() > 6)
				createPlotPoints(false);
			else
				createPlotPoints(true);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createPlotPoints(boolean useCostChangeTimesForXTicks) throws ParseException {
		SimulationClock simClock = new SimulationClock(sdformat.parse("1:00:00"), 1, true);
		maxCost = Double.NEGATIVE_INFINITY;
		minCost = 0;
		Calendar cal = simClock.getCurrentCalendar();
		double initialCost = tProvider.getCurrentRateInDollarsPerKilowattHour(simClock);
		maxCost = initialCost;
		double previousCost = initialCost;
		
		TreeMap<Integer, Double> costBreakpoints = new TreeMap<Integer, Double>();
		costBreakpoints.put(0, initialCost);
		
		while (cal.get(Calendar.DATE) < 2) { // only need to go through one day
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int minuteOfDay = hour*60+minute;
			double currentCost = tProvider.getCurrentRateInDollarsPerKilowattHour(simClock);
			if (currentCost != previousCost) {
				costBreakpoints.put(minuteOfDay, currentCost);
				if (currentCost > maxCost)
					maxCost = currentCost;
				if (currentCost < minCost)
					minCost = currentCost;
				previousCost = currentCost;
			}
			
			simClock.animationstep();
			cal = simClock.getCurrentCalendar();
		}

		// should be at midnight on the next day
		costBreakpoints.put(24*60, tProvider.getCurrentRateInDollarsPerKilowattHour(simClock));
		
		// initialize x/yaxislabels & ticks arrays
		yaxislabels = new ArrayList<BasicTextLabel>();
		yticks = new ArrayList<Line2D.Double>();
		xaxislabels = new ArrayList<BasicTextLabel>();
		xticks = new ArrayList<Line2D.Double>();
		
		// set parameters
		int yaxisLabelFontSize = 12;
		int yaxisTitleFontSize = 12;
		int xaxisLabelFontSize = 12;
		int numYTicks;
		NumberFormatter nf = null;
		double sampleTick;
		if (costBreakpoints.size() == 2) {
			numYTicks = 2;
			nf = new NumberFormatter(new DecimalFormat("0.0"));
			sampleTick = Math.round(((double)maxCost)/100.0) + 0.1;
		}
		else {
			numYTicks = 4;
			nf = new NumberFormatter(new DecimalFormat("0"));
			sampleTick = Math.round(((double)maxCost)/100.0);
		}
		if (minCost < 0)
			sampleTick *= -1;
		
		int numXTicks = 6;
		double yaxisTickWidth = 0.02*width;
		double xaxisTickHeight = 0.1*height;
		
		// Find out what the maximum width will be; this should correspond to the maximum price throughout the day since
		BasicTextLabel newLabel = new BasicTextLabel(new Point2D.Double(0,0.1*height),yaxisLabelFontSize,new Color(0,0,255/2),0,
				nf.valueToString(tProvider.getMaxRateInDollarsPerKilowattHour()*100),
				DataLabelHorizontalAlignment.RIGHT,DataLabelVerticalAlignment.CENTER); // create a temporary label corresponding to tick labels
		BufferedImage off_Image = 
			new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB); // a place to render the temporary label
		newLabel.render((Graphics2D) off_Image.getGraphics()); // render, which will store the width and height of the temporary label
		yaxisTextWidth = newLabel.getWidth(); // grab maximum width of tick labels
		double yaxisTextHeight = newLabel.getHeight();
		newLabel = new BasicTextLabel(new Point2D.Double(0,0.1*height),yaxisTitleFontSize,new Color(0,0,255/2),0,
				"Cost in ¢/kWh",DataLabelHorizontalAlignment.RIGHT,
				DataLabelVerticalAlignment.CENTER); // create a temporary label corresponding to the axis title
		newLabel.render((Graphics2D) off_Image.getGraphics());
		yaxisTitleWidth = newLabel.getHeight(); // grab width of title (which is the height, since the title is rotated 90 degrees)
		double yaxisTitleHeight = newLabel.getWidth();
		yaxisWidth = yaxisTextWidth*1.5 + yaxisTitleWidth*1.5 + yaxisTickWidth/2; 	// the amount of space to leave to the left of the y-axis 
																					// for the ticks, the tick labels, and the axis title
		
		newLabel = new BasicTextLabel(new Point2D.Double(0,0.1*height),yaxisLabelFontSize,new Color(0,0,255/2),0,
				nf.valueToString(maxCost*100),
				DataLabelHorizontalAlignment.RIGHT,DataLabelVerticalAlignment.CENTER); // create a temporary label corresponding to xtick labels
		newLabel.render((Graphics2D) off_Image.getGraphics());
		double xaxisTickLabelHeight = newLabel.getHeight();
		xaxisHeight = xaxisTickHeight/2 + 3.25*xaxisTickLabelHeight;

		// If the title doesn't fit, rescale the font
		while (yaxisTitleHeight > (height - xaxisHeight)) {
			yaxisTitleFontSize -= 1;
			newLabel = new BasicTextLabel(new Point2D.Double(0,0.1*height),yaxisTitleFontSize,new Color(0,0,255/2),0,
					"Cost in ¢/kWh",DataLabelHorizontalAlignment.RIGHT,
					DataLabelVerticalAlignment.CENTER); // create a temporary label corresponding to the axis title
			newLabel.render((Graphics2D) off_Image.getGraphics());
			yaxisTitleWidth = newLabel.getHeight(); // grab width of title (which is the height, since the title is rotated 90 degrees)
			yaxisTitleHeight = newLabel.getWidth();
		}
		
		// generate all the ticks and tick labels
		if (minCost < 0) {
			double costTickGap = (maxCost - minCost)/(numYTicks);
			double ycoordOfZeroTick = getCoordinates(0,0.0,minCost,maxCost,width,(height-xaxisHeight)*scaleOfHeightForNegativeCost,0).getY();
			double ycoordTickGap = getCoordinates(0,0.0,minCost,maxCost,width,(height-xaxisHeight)*scaleOfHeightForNegativeCost,0).distance(
					getCoordinates(0,costTickGap,minCost,maxCost,width,(height-xaxisHeight)*scaleOfHeightForNegativeCost,0));
			double ticksFitBelowZero = Math.floor(((height-xaxisHeight)*scaleOfHeightForNegativeCost - ycoordOfZeroTick) / ycoordTickGap);
			double tickYCoord = ycoordOfZeroTick + ticksFitBelowZero * ycoordTickGap;
			System.out.println("0 = " + getYValFromCoordinate(ycoordOfZeroTick, minCost,maxCost,(height-xaxisHeight)*scaleOfHeightForNegativeCost));
			DataLabelVerticalAlignment labelAlignment;
			if (tickYCoord + yaxisTextHeight/2.0 > (height-xaxisHeight)*scaleOfHeightForNegativeCost)
				labelAlignment = DataLabelVerticalAlignment.BOTTOM;
			else
				labelAlignment = DataLabelVerticalAlignment.CENTER;
			while (tickYCoord > 0.05*(height - xaxisHeight)) {
				double yval = 100*getYValFromCoordinate(tickYCoord, minCost,maxCost,(height-xaxisHeight)*scaleOfHeightForNegativeCost);
				double tickXTo = yaxisWidth + yaxisTickWidth/2.0;
				if (Math.abs(yval) <= 0.1) {
					yval = 1e-10;
					tickXTo = width;
				}
				yaxislabels.add(new BasicTextLabel(new Point2D.Double(yaxisWidth-yaxisTickWidth/2-yaxisTextWidth*0.5,tickYCoord),yaxisLabelFontSize,costPlotColor,0,
						nf.valueToString(yval),DataLabelHorizontalAlignment.RIGHT,labelAlignment));
				yticks.add(new Line2D.Double(new Point2D.Double(yaxisWidth-yaxisTickWidth/2.0,tickYCoord),new Point2D.Double(tickXTo,tickYCoord)));
				labelAlignment = DataLabelVerticalAlignment.CENTER;
				tickYCoord -= ycoordTickGap;
			}
		} else {
			for (int tickIdx = 0; tickIdx < numYTicks; tickIdx++) {
				double scaleY = ((double)tickIdx/((double)numYTicks-1));
				double tickHeight = (height-xaxisHeight)*(1 - 0.9*scaleY);
				DataLabelVerticalAlignment labelAlignment;
				if (tickIdx == 0)
					labelAlignment = DataLabelVerticalAlignment.BOTTOM;
				else
					labelAlignment = DataLabelVerticalAlignment.CENTER;
						
				double tickXTo = yaxisWidth+yaxisTickWidth/2;
				double valueToConvertToString = minCost*100 + (maxCost-minCost)*100*scaleY;
				if ((valueToConvertToString > -0.1) & (valueToConvertToString < 0.1))
					valueToConvertToString = Double.MIN_VALUE;
				
				yaxislabels.add(new BasicTextLabel(new Point2D.Double(yaxisWidth-yaxisTickWidth/2-yaxisTextWidth*0.5,tickHeight),yaxisLabelFontSize,costPlotColor,0,
						nf.valueToString(valueToConvertToString),DataLabelHorizontalAlignment.RIGHT,labelAlignment));
				yticks.add(new Line2D.Double(new Point2D.Double(yaxisWidth-yaxisTickWidth/2,tickHeight),new Point2D.Double(tickXTo,tickHeight)));
			}
		}
		
		// add in the axis title
		yaxislabels.add(new BasicTextLabel(new Point2D.Double(0.1*yaxisTitleWidth,0.5*(height-xaxisHeight)),yaxisTitleFontSize,costPlotColor,-Math.PI/2,
				"Cost in ¢/kWh",DataLabelHorizontalAlignment.CENTER,DataLabelVerticalAlignment.TOP));
		
		
		// Do the same thing for the x-axis
		xticks = new ArrayList<Line2D.Double>();
		SimpleDateFormat xtickLabelFormat = new SimpleDateFormat("ha");
		GregorianCalendar xTickCalendar = null;
		if (useCostChangeTimesForXTicks) {
			Set<Integer> costBreakPointMinutes = costBreakpoints.keySet();
			Iterator<Integer> costBreakPointMinuteIterator = costBreakPointMinutes.iterator();
			while (costBreakPointMinuteIterator.hasNext()) {
				int costBreakPointMinute = costBreakPointMinuteIterator.next();
				int costBreakPointHour = (int)Math.round((double)costBreakPointMinute/(60.0));
				double scaleMaxTime = ((double)costBreakPointHour)/24.0;
				double tickXLocation = (width - yaxisWidth)*(scaleMaxTime) + yaxisWidth;
				xTickCalendar = new GregorianCalendar(0,0,0,costBreakPointHour,0);
				DataLabelHorizontalAlignment tickLabelAlignment = null;
				if (costBreakPointMinuteIterator.hasNext())
					tickLabelAlignment = DataLabelHorizontalAlignment.CENTER;
				else
					tickLabelAlignment = DataLabelHorizontalAlignment.RIGHT;
				
				xaxislabels.add(new BasicTextLabel(new Point2D.Double(tickXLocation,height - xaxisHeight + 0.75*xaxisTickLabelHeight),xaxisLabelFontSize,Color.BLACK,0,
					xtickLabelFormat.format(xTickCalendar.getTime()),tickLabelAlignment,DataLabelVerticalAlignment.TOP));
					
				xticks.add(new Line2D.Double(new Point2D.Double(tickXLocation,height - xaxisHeight - xaxisTickHeight/2),
						new Point2D.Double(tickXLocation,height- xaxisHeight + xaxisTickHeight/2)));
			}
		} else {
			for (int tickIdx = 0; tickIdx < numXTicks; tickIdx++) {
				double scaleMaxTime = ((double)tickIdx/((double)numXTicks - 1));
				double tickXLocation = (width - yaxisWidth)*(scaleMaxTime) + yaxisWidth;
				int tickHour = (int)Math.round(scaleMaxTime*24.0);
				xTickCalendar = new GregorianCalendar(0,0,0,tickHour,0);
				DataLabelHorizontalAlignment tickLabelAlignment = null;
				if (tickIdx < numXTicks - 1)
					tickLabelAlignment = DataLabelHorizontalAlignment.CENTER;
				else
					tickLabelAlignment = DataLabelHorizontalAlignment.RIGHT;
				xaxislabels.add(new BasicTextLabel(new Point2D.Double(tickXLocation,height - xaxisHeight + 0.75*xaxisTickLabelHeight),xaxisLabelFontSize,Color.BLACK,0,
					xtickLabelFormat.format(xTickCalendar.getTime()),tickLabelAlignment,DataLabelVerticalAlignment.TOP));
				xticks.add(new Line2D.Double(new Point2D.Double(tickXLocation,height - xaxisHeight - xaxisTickHeight/2),
						new Point2D.Double(tickXLocation,height - xaxisHeight + xaxisTickHeight/2)));
			}			
		}
		
		xaxisDayLabel = new BasicTextLabel(new Point2D.Double(width/10,height - 0.25*xaxisTickLabelHeight),xaxisLabelFontSize,Color.BLACK,0,
				"Day 9",DataLabelHorizontalAlignment.LEFT,DataLabelVerticalAlignment.BOTTOM);
		
		Set<Entry<Integer,Double>> costSet = costBreakpoints.entrySet();
		Entry<Integer,Double>[] entries = costSet.toArray((Entry<Integer,Double>[])(new Entry[0]));
		costLines = new ArrayList<SimpleLineDisplayWithDistanceInfo>(); 
		SimpleLineDisplayWithDistanceInfo prevLine = null;
		SimpleLineDisplayWithDistanceInfo addLine = null;
		double costLineThickness = 1.0;
		for (int entryIdx = 0; entryIdx < (entries.length - 1); entryIdx++) {
			Entry<Integer,Double> ent1 = entries[entryIdx];
			Entry<Integer,Double> ent2 = entries[entryIdx+1];
			//Point2D.Double coordFrom = getCoordinates(ent1.getKey(), ent1.getValue(), maxCost,1,1);
			Point2D.Double coordFrom = null;
			Point2D.Double coordTo = null;
			if (minCost < 0) {
				coordFrom = getCoordinates(ent1.getKey(), ent1.getValue(), minCost,maxCost,width-yaxisWidth,
						(height-xaxisHeight)*scaleOfHeightForNegativeCost, yaxisWidth);
				coordTo = getCoordinates(ent2.getKey(), ent1.getValue(), minCost,maxCost,width-yaxisWidth,
						(height-xaxisHeight)*scaleOfHeightForNegativeCost, yaxisWidth);
			} else {
				coordFrom = getCoordinates(ent1.getKey(), ent1.getValue(), minCost,maxCost,width-yaxisWidth,height-xaxisHeight, yaxisWidth);
				coordTo = getCoordinates(ent2.getKey(), ent1.getValue(), minCost,maxCost,width-yaxisWidth,height-xaxisHeight, yaxisWidth);
			}
			if (entryIdx == 0) {
				addLine = new SimpleLineDisplayWithDistanceInfo(coordFrom,coordTo,costLineThickness,0);
			}
			else {
				addLine = new SimpleLineDisplayWithDistanceInfo(coordFrom,coordTo,costLineThickness,prevLine.getEndDistance());
			}
			costLines.add(addLine);
			Point2D.Double coordNext;
			if (minCost < 0)
				coordNext = getCoordinates(ent2.getKey(),ent2.getValue(),minCost,maxCost,width-yaxisWidth,
						(height-xaxisHeight)*scaleOfHeightForNegativeCost, yaxisWidth);
			else
				coordNext = getCoordinates(ent2.getKey(),ent2.getValue(),minCost,maxCost,width-yaxisWidth,
						height-xaxisHeight, yaxisWidth);
			addLine = new SimpleLineDisplayWithDistanceInfo(coordTo,coordNext,costLineThickness,addLine.getEndDistance());
			costLines.add(addLine);
			if (entryIdx == (entries.length - 2)) {
				
			}
			prevLine = addLine;
		}
	}
	
	private Point2D.Double getCoordinates(int minuteOfDay, double yValue, double minYValue, double maxYValue, double plotWidth, double plotHeight, double xOffset) {
		double yCoord = plotHeight*(1-0.9*(yValue - minYValue)/(maxYValue - minYValue));//plotHeight + ((0.1*plotHeight - plotHeight)/(maxYValue-minYValue))*(yValue - minYValue);
		return new Point2D.Double(xOffset + plotWidth*minuteOfDay/(24.0*60.0),yCoord);
	}
	
	private double getYValFromCoordinate(double yCoord, double minYValue, double maxYValue, double plotHeight) {
		double retVal = -(yCoord/plotHeight - 1)*(maxYValue - minYValue)/0.9+minYValue;
		return retVal;
	}
	
	private ArrayList<BasicTextLabel> xaxislabels,yaxislabels;
	private ArrayList<Line2D.Double> xticks,yticks;
	private double yaxisWidth,yaxisTitleWidth,yaxisTextWidth,xaxisHeight;
	ArrayList<SimpleLineDisplayWithDistanceInfo> costLines;
	
	Color costPlotColor = Color.black;//new Color(0,255/2,0);
	SimpleDateFormat sdFormatForXAxisLabel = new SimpleDateFormat("hh:mma");
	double scaleOfHeightForNegativeCost = 0.9;
	
	
	public boolean render(Graphics2D g2d) {
		Stroke backupStroke = g2d.getStroke();
		Color backupColor = g2d.getColor();
		AffineTransform backupXForm = g2d.getTransform();
		g2d.translate(x, y-height);
		Rectangle2D.Double bgRectEntire = new Rectangle2D.Double(0,0,width,height);
		Rectangle2D.Double bgRectPlot = new Rectangle2D.Double(yaxisWidth,0,width-yaxisWidth,height-xaxisHeight); 
		g2d.setColor(Color.WHITE);
		g2d.fill(bgRectEntire);
		//g2d.fill(bgRectPlot);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke((float)1.0));
		g2d.draw(bgRectEntire);
		g2d.translate(+width/2,+height/2);
		g2d.scale((width-10.0)/width, (height-10.0)/height);
		g2d.translate(-width/2,-height/2);
		g2d.draw(bgRectPlot);
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < costLines.size(); i++) {
			costLines.get(i).render(g2d);
		}
		for (int i = 0; i < yticks.size(); i++)
			g2d.draw(yticks.get(i));		
		for (int i = 0; i < xticks.size(); i++)
			g2d.draw(xticks.get(i));
		g2d.setStroke(new BasicStroke((float)3.0,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND));
		g2d.setColor(costPlotColor);
		for (int i = 0; i < yaxislabels.size(); i++)
			yaxislabels.get(i).render(g2d);
		for (int i = 0; i < xaxislabels.size(); i++)
			xaxislabels.get(i).render(g2d);

		// place time dot
		int minuteOfTime = simClock.getCurrentCalendar().get(Calendar.HOUR_OF_DAY)*60 + simClock.getCurrentCalendar().get(Calendar.MINUTE);
		int dayNumber =  simClock.getCurrentCalendar().get(Calendar.DAY_OF_YEAR) - initialDayOfYear + 1;
		double currentCost = tProvider.getCurrentRateInDollarsPerKilowattHour(simClock);
		NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.0#"));
		try {
			xaxisDayLabel.setCaption("Current Time: Day " + dayNumber + ", " + 
					sdFormatForXAxisLabel.format(simClock.getCurrentCalendar().getTime()) + 
					"  Current Price: " + nf.valueToString(currentCost*100) + "¢/kWh");
		} catch (ParseException e) {
			xaxisDayLabel.setCaption("Current Time: Day " + dayNumber + ", " + 
					sdFormatForXAxisLabel.format(simClock.getCurrentCalendar().getTime()) + 
					"  Current Price: ??? ¢/kWh");			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xaxisDayLabel.render(g2d);
		Point2D.Double timeDotLocation;
		if (minCost < 0)
			timeDotLocation = getCoordinates(minuteOfTime, currentCost, minCost,maxCost,width-yaxisWidth,(height-xaxisHeight)*scaleOfHeightForNegativeCost, yaxisWidth);
		else
			timeDotLocation = getCoordinates(minuteOfTime, currentCost, minCost,maxCost,width-yaxisWidth,height-xaxisHeight, yaxisWidth);
		double timeDotRadius = 4.0;
		Ellipse2D.Double timeDot = new Ellipse2D.Double(timeDotLocation.getX()-timeDotRadius,timeDotLocation.getY()-timeDotRadius,
				2*timeDotRadius,2*timeDotRadius);
		g2d.setColor(Color.RED);
		g2d.fill(timeDot);
		g2d.setColor(Color.BLACK);
		g2d.draw(timeDot);
		
		g2d.setTransform(backupXForm);
		g2d.setStroke(backupStroke);
		g2d.setColor(backupColor);
		return true;
	}

}
