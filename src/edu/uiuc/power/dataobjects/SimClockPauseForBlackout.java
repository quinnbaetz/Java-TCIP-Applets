package edu.uiuc.power.dataobjects;

import javax.swing.JButton;

import edu.uiuc.power.displayobjects.Animatable;

public class SimClockPauseForBlackout implements Animatable {
	
	SimulationClock simClock;
	PowerSystem psystem;
	
	public SimClockPauseForBlackout(SimulationClock simClock,
			PowerSystem psystem) {
		super();
		this.simClock = simClock;
		this.psystem = psystem;
	}

	public boolean animationstep() {
		if (psystem.getSystemBlackout()) {
			simClock.setPausedForBlackout(true);
		} else {
			simClock.setPausedForBlackout(false);
		}
		return true;
	}

}
