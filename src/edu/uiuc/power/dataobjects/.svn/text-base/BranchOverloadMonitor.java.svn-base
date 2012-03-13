package edu.uiuc.power.dataobjects;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uiuc.power.displayobjects.Animatable;

public class BranchOverloadMonitor implements Animatable {
	
	BranchData branch;
	int currentCount;
	ArrayList<BranchOverloadMonitorListener> listeners;
	BranchOverloadMonitorParameters monitorParams;
	
	public BranchData getBranch() {
		return branch;
	}
	
	public BranchOverloadMonitor(BranchData branch, BranchOverloadMonitorParameters monitorParams) {
		this.monitorParams = monitorParams;
		this.branch = branch;
		currentCount = 0;
		this.listeners = new ArrayList<BranchOverloadMonitorListener>();
	}
	
	public void registerListener(BranchOverloadMonitorListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(BranchOverloadMonitorListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListenersOfLineOpening() {
		Iterator<BranchOverloadMonitorListener> listenersIter = listeners.iterator();
		while(listenersIter.hasNext()) {
			BranchOverloadMonitorListener listener = listenersIter.next();
			listener.lineOpenedDueToOverload(this);
		}
	}

	public boolean animationstep() {
		if (monitorParams.getStatus()) {
			if (branch.getDCPercentFlow() > (1.0 + 1e-4))
				currentCount++;
			else
				currentCount = 0;
				
			if (currentCount > monitorParams.getNumOfStepsBeforeOutage()) {
				//System.out.println("line " + branch.getFromNode().getNum() + " to " + branch.getToNode().getNum() + " outaged due to overload");
				branch.setClosed(false);
				notifyListenersOfLineOpening();
			}
			
			return false;
		} else {
			currentCount = 0;
			return false;
		}
	}
}
