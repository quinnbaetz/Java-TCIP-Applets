package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

public class LoadData implements MWManageable {
	double _MW;
	double _MVar;
	boolean _connected;
	NodeData _connectNode;
	ArrayList<PowerChangeListener> powerchangelisteners;
	
	public LoadData(NodeData connectNode, 
			double MW, double MVar, boolean connected)  {
		_connectNode = connectNode;
		_MW = MW;
		_MVar = MVar;
		_connected = connected;
		powerchangelisteners = new ArrayList<PowerChangeListener>();
		connectNode.addAttachedLoad(this);
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
	
	private void notifyPowerChangeListeners() {
		for (int i = 0; i < powerchangelisteners.size(); i++)
			powerchangelisteners.get(i).PowerChanged();
	}	
	
	public void unlinkFromNodes() {
		getConnectNode().removeAttachedLoad(this);
	}
	
	public double getMW() {
		return _MW;
	}
	
	public double getMWSupplied() {
		if (!getConnectNode().getIslanded()) {
			return _MW;
		} else
			return 0;
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
	
	public boolean getConnected() {
		return _connected & (!getConnectNode().getIslanded());
	}
	
	public void setConnected(boolean connected) {
		_connected = connected;
		notifyPowerChangeListeners();
	}
	
	public NodeData getConnectNode() {
		return _connectNode;
	}
	
	public void setConnectNode(NodeData connectNode) {
		_connectNode = connectNode;
	}
}
