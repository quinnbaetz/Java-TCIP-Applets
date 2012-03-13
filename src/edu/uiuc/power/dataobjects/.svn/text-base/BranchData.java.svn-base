package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

public class BranchData implements Switchable, MWProvider {
	@Override
	public String toString() {
		return _fromNode.getName() + " to " + _toNode.getName();
	}

	double _x;
	double _r;
	
	double _MaxMWFlow;
	
	NodeData _fromNode;
	NodeData _toNode;
	
	boolean _closed;
	
	ArrayList<TopologyChangeListener> topchangelisteners;
	
	public BranchData(NodeData fromNode, NodeData toNode, double X, double R,
			double MaxMWFlow,
			boolean closed) {
		_fromNode = fromNode;
		_toNode = toNode;
		_x = X;
		_r = R;
		_closed = closed;
		_MaxMWFlow = MaxMWFlow;
		topchangelisteners = new ArrayList<TopologyChangeListener>();
		
		fromNode.addAttachedBranch(this);
		toNode.addAttachedBranch(this);
	}
	
	public void unlinkFromNodes() {
		getFromNode().removeAttachedBranch(this);
		getToNode().removeAttachedBranch(this);
	}
	
	public void registerTopologyChangeListener(TopologyChangeListener topchangelistener) {
		topchangelisteners.add(topchangelistener);
	}
	
	public void removeTopologyChangeListener(TopologyChangeListener topchangelistener) {
		topchangelisteners.remove(topchangelistener);
	}
	
	private void notifyTopologyChangeListeners() {
		for (int i = 0; i < topchangelisteners.size(); i++)
			topchangelisteners.get(i).TopologyChanged();
	}
	
	public double getMaxMWFlow() {
		return _MaxMWFlow;
	}
	
	public void setMaxMWFlow(double MaxMWFlow) {
		_MaxMWFlow = MaxMWFlow;
	}
	
	public double getDCPercentFlow() {
		return Math.abs(getDCMWFlow())/getMaxMWFlow();
	}
	
	public boolean getClosed() {
		return _closed;
	}
	
	public void setClosed(boolean closed) {
		if (closed != _closed)
			notifyTopologyChangeListeners();
		_closed = closed;
	}
	
	public double getX() {
		return _x;
	}
	
	public void setX(double x) {
		_x = x;
	}
	
	public double getR() {
		return _r;
	}
	
	public void setR(double r) {
		_r = r;
	}
	
	public NodeData getFromNode() {
		return _fromNode;
	}
	
	public void setFromNode(NodeData fromNode) {
		_fromNode = fromNode;
	}
	
	public NodeData getToNode() {
		return _toNode;
	}
	
	public void setToNode(NodeData toNode) {
		_toNode = toNode;
	}
	
	public double getDCMWFlow() {
		if (getClosed()) {
			if ((!getFromNode().getIslanded()) & (!getToNode().getIslanded())) {
				double flow = (getFromNode().getVAng() - getToNode().getVAng())/getX();
				return flow;
			} else
				return 0;
		} else
			return 0;
		
	}
	
	public double getMW() {
		return getDCMWFlow();
	}
}
