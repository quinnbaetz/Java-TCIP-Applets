package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.text.DateFormatter;

import edu.uiuc.power.dataobjects.SimulationClock;

public class SimClockDisplay extends DataLabel {

	SimulationClock simClock;
	boolean displayDay;
	
	public SimClockDisplay(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, SimulationClock simClock) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.simClock = simClock;
		this.displayDay = false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.CENTER;
	}

	@Override
	public String getCaption() {
		Calendar simCalendar = simClock.getCurrentCalendar();
		DateFormatter dFormat = new DateFormatter(new SimpleDateFormat("h:mm a"));
		String suffix = "";
		/*
		if (!simClock.getClockState()) {
			suffix = " (paused)";
		}
		*/
		try {
			if (displayDay)
				return "Current Time: Day " + simCalendar.get(Calendar.DAY_OF_YEAR) +", " + dFormat.valueToString(simCalendar.getTime()) + suffix;
			else
				return "Current Time: " + dFormat.valueToString(simCalendar.getTime()) + suffix;
		} catch (ParseException e) {
			return e.toString();
		}
	}
	
	
	public void setDisplayDay(boolean displayDay) {
		this.displayDay = displayDay;
	}

}
