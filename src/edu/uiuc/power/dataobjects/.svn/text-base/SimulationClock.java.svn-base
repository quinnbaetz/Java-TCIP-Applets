package edu.uiuc.power.dataobjects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.uiuc.power.displayobjects.Animatable;

public class SimulationClock implements Animatable {

	static boolean PAUSED = false;
	static boolean RUNNING = true;
	int animationStepsPerClockTick;
	int animationStepCount = 0;
	boolean clockRunning;
	boolean clockPausedForBlackout;
	Calendar currentCalendar = new GregorianCalendar();
	Date startDate;
	int minutesPerAnimationStep;
	Date endDate;
	
	ArrayList<SimulationClockListener> simClockListeners = null;
	
	private enum simClockNotifyType {STARTED, STOPPED};
	
	public SimulationClock(Date startDate, int minutesPerAnimationStep, boolean clockRunning, int animationStepsPerClockTick) {
		this(startDate, null, minutesPerAnimationStep, clockRunning, animationStepsPerClockTick);
	}
	
	public SimulationClock(Date startDate, Date endDate, int minutesPerAnimationStep, boolean clockRunning) {
		this(startDate,endDate,minutesPerAnimationStep,clockRunning,0);
	}
	
	public SimulationClock(Date startDate, int minutesPerAnimationStep, boolean clockRunning) {
		this(startDate,minutesPerAnimationStep,clockRunning,0);
	}
	
	public SimulationClock(Date startDate, Date endDate, int minutesPerAnimationStep, boolean clockRunning, int animationStepsPerClockTick) {
    	this.startDate = startDate;
    	this.endDate = endDate;
    	this.clockRunning = clockRunning;
    	currentCalendar.setTime(startDate);
    	this.animationStepsPerClockTick = animationStepsPerClockTick;
    	this.minutesPerAnimationStep = minutesPerAnimationStep;
    	this.simClockListeners = new ArrayList<SimulationClockListener>();
    	notifyListeners(simClockNotifyType.STARTED);
	}
	
	public void addClockListener(SimulationClockListener listener) {
		simClockListeners.add(listener);
	}
	
	public void removeClockListener(SimulationClockListener listener) {
		simClockListeners.remove(listener);
	}
	
	private void notifyListeners(simClockNotifyType notifyType) {
		switch (notifyType) {
		case STARTED:
			for (int i = 0; i < simClockListeners.size(); i++)
				simClockListeners.get(i).clockStarted(this);
			break;
		case STOPPED:
			for (int i = 0; i < simClockListeners.size(); i++)
				simClockListeners.get(i).clockStopped(this);
			break;
		default:
			break;
		}
	}
	
	public void setPausedForBlackout(boolean paused) {
		this.clockPausedForBlackout = paused;
	}
    
	public int getMinutesPerAnimationStep() {
		return minutesPerAnimationStep;
	}
	    
    public void resetClock() {
    	currentCalendar.setTime(startDate);
    }
    
    public void setClockState(boolean clockRunning) {
    	this.clockRunning = clockRunning;
    	if (clockRunning)
    		notifyListeners(simClockNotifyType.STARTED);
    	else
    		notifyListeners(simClockNotifyType.STOPPED);
    }
    
    public boolean getClockState() {
    	return clockRunning;
    }

	public Calendar getCurrentCalendar() {
    	return currentCalendar;
    }
	
	public boolean animationstep() {
		if (++animationStepCount > animationStepsPerClockTick) {
			animationStepCount = 0;
			if (clockRunning & !clockPausedForBlackout) {
				getCurrentCalendar().add(Calendar.MINUTE, minutesPerAnimationStep);
				if (endDate != null) {
					if (getCurrentCalendar().getTime().compareTo(endDate) > 0) {
						// past the end Date, so stop
						setClockState(false);
					}
				}
				SimpleDateFormat debugDisplay = new SimpleDateFormat("MM/dd/yyyy HH:mm");
				if (false)
					System.out.println(debugDisplay.format(currentCalendar.getTime()));
				//if ((double)(getCurrentCalendar().getTimeInMillis() - startDate.getTime())/1000/60/60/24 >= 1)
				//	clockRunning = false;
				return true;
			} else
				return false;
		} else
			return false;
	}
}
