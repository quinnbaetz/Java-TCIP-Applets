package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

public class GeneratorData implements MWManageable, MWAndMaxMWProvider {
	double _MW;
	double _MVar;
	
	double _minMW;
	double _maxMW;
	double _minMVar;
	double _maxMVar;
	
	boolean _connected;
	NodeData _connectNode;
	ArrayList<PowerChangeListener> powerchangelisteners;
	
	public GeneratorData(
			NodeData connectNode, 
			double MW, 
			double minMW, double maxMW,
			double MVar,
			double minMVar, double maxMVar,
			boolean connected) {
		_connectNode = connectNode;
		
		_MW = MW;
		_minMW = minMW;
		_maxMW = maxMW;
		
		_MVar = MVar;
		_minMVar = minMVar;
		_maxMVar = maxMVar;
		
		_connected = connected;
		
		_connectNode.addAttachedGenerator(this);
		
		powerchangelisteners = new ArrayList<PowerChangeListener>();
	}
	
	public double getMinMW() {
		return _minMW;
	}
	
	public double getMaxMW() {
		return _maxMW;
	}
	
	public double getMinMVar() {
		return _minMVar;
	}
	
	public double getMaxMVar() {
		return _maxMVar;
	}
	
	public void registerPowerChangeListener(PowerChangeListener listener) {
		if (!powerchangelisteners.contains(listener))
			powerchangelisteners.add(listener);
	}
	
	public void unregisterPowerChangeListener(PowerChangeListener listener) {
		powerchangelisteners.remove(listener);
	}	
	
	public void unregisterAllPowerChangeListeners() {
		powerchangelisteners.clear();
	}
	
	protected void notifyPowerChangeListeners() {
		for (int i = 0; i < powerchangelisteners.size(); i++)
			powerchangelisteners.get(i).PowerChanged();
	}
	
	
	public void unlinkFromNodes() {
		getConnectNode().removeAttachedGenerator(this);
	}
	
	public double getMW() {
		if (getConnected()) {
			if (!getConnectNode().getIslanded()) {
				if (getConnectNode().getIsSlack()) {
						double MWTotal = 0;
						NodeData connectNode = getConnectNode();
						ArrayList<BranchData> connectNodeAttachedBranches = connectNode.getAttachedBranches();
						for (int i = 0; i < connectNodeAttachedBranches.size(); i++) {
							BranchData branch = connectNodeAttachedBranches.get(i);
							if (branch.getClosed()) {
								if (branch.getFromNode() == connectNode)
									MWTotal += (1.0/branch.getX())*(branch.getFromNode().getVAng() - branch.getToNode().getVAng());
								else
									MWTotal += (1.0/branch.getX())*(branch.getToNode().getVAng() - branch.getFromNode().getVAng());
							}
						}	
						if (Math.abs(MWTotal - this.getMinMW()) < 1e-3)
							return this.getMinMW();
						else
							return MWTotal;
				}
				else
					return getMWSetpoint();
			} else 
				return 0;
		} else
			return 0;
	}
	
	public double getMWSetpoint() {
		return _MW;
	}
	
	public void setMW(double MW) {
		_MW = MW;
		notifyPowerChangeListeners();
	}
	
	public double getMVar() {
		return _MVar;
	}
	
	public void setMVar(double MVar) {
		_MVar = MVar;
		notifyPowerChangeListeners();
	}
	
	public boolean getConnected(boolean checkIfIslanded) {
		boolean retval = _connected;
		if (checkIfIslanded) {
			retval = retval & (!getConnectNode().getIslanded()); 
		}
		return retval;
	}
	
	public boolean getConnected() {
		return getConnected(true);
	}
	
	public void setConnected(boolean connected) {
		_connected = connected;
		notifyPowerChangeListeners();
	}
	
	public NodeData getConnectNode() {
		return _connectNode ;
	}
	
	public void setConnectNode(NodeData connectNode) {
		_connectNode = connectNode;
	}


	public void setMaxMW(double _maxmw) {
		if (Math.abs(_maxMW - _maxmw) > 1) {
			_maxMW = _maxmw;
			notifyPowerChangeListeners();
		}
	}
}
