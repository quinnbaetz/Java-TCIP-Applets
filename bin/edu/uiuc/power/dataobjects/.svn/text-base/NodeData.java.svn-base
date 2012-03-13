package edu.uiuc.power.dataobjects;

import java.util.ArrayList;

public class NodeData {
	private double _vmag;
	private double _vang;
	private boolean _isSlack;
	private boolean _isTempSlack;
	private int _num;
	private int _islandNum;
	private boolean _isCandidateSlack;
	private IslandData myIsland;
	private String name;
	
	private ArrayList<BranchData> _branches;
	private ArrayList<LoadData> _loads;
	private ArrayList<GeneratorData> _gens;
	
	public boolean getIsCandidateSlack() {
		return _isCandidateSlack;
	}
	
	public double peakGen;
	public double peakLoad;
	
	public void setIsCandidateSlack(boolean isCandidateSlack) {
		_isCandidateSlack = isCandidateSlack;
	}
	
	public void unregisterAllLoadsAndGeneratorsWithPowerChangeListener(PowerChangeListener listener) {
		ArrayList<LoadData> loads = getAttachedLoads();
		ArrayList<GeneratorData> gens = getAttachedGenerators();
		
		for (int i = 0; i < loads.size(); i++) {
			loads.get(i).unregisterPowerChangeListener(listener);
		}
		
		for (int i = 0; i < gens.size(); i++) {
			gens.get(i).unregisterPowerChangeListener(listener);
		}
	}
	
	public void unregisterAllLoadsAndGeneratorsFromAllPowerChangeListeners() {
		ArrayList<LoadData> loads = getAttachedLoads();
		ArrayList<GeneratorData> gens = getAttachedGenerators();
		
		for (int i = 0; i < loads.size(); i++) {
			loads.get(i).unregisterAllPowerChangeListeners();
		}
		
		for (int i = 0; i < gens.size(); i++) {
			gens.get(i).unregisterAllPowerChangeListeners();
		}		
	}
	
	public void registerAllLoadsAndGeneratorsWithPowerChangeListener(PowerChangeListener listener) {
		ArrayList<LoadData> loads = getAttachedLoads();
		ArrayList<GeneratorData> gens = getAttachedGenerators();
		
		for (int i = 0; i < loads.size(); i++) {
			loads.get(i).registerPowerChangeListener(listener);
		}
		
		for (int i = 0; i < gens.size(); i++) {
			gens.get(i).registerPowerChangeListener(listener);
		}
	}
	
	public NodeData(int num, double VMag, double VAng, boolean isSlack, boolean isCandidateSlack, String name) {
		_num = num;
		_vmag = VMag;
		_vang = VAng;
		_isSlack = isSlack;
		_isTempSlack = false;
		_isCandidateSlack = isCandidateSlack;
		_islandNum = -1;
		_branches = new ArrayList<BranchData>();
		_loads = new ArrayList<LoadData>();
		_gens = new ArrayList<GeneratorData>();
		this.name = name;
	}
	
	public NodeData(int num, double VMag, double VAng, boolean isSlack, boolean isCandidateSlack) {
		this(num,VMag,VAng,isSlack,isCandidateSlack,"Bus #" + num);
	}
	
	
	public NodeData(int num, double VMag, double VAng, boolean isSlack) {
		this(num,VMag,VAng,isSlack,false);
	}
	
	public NodeData(int num, double VMag, double VAng) {
		this(num,VMag,VAng,false,false);
	}
	
	public boolean getIslanded() {
		if (getIslandNum() == -1)
			return true;
		else
			return false;
	}
	
	public void setIslandData(IslandData islandData) {
		this.myIsland = islandData;
	}
	
	public IslandData getIslandData() {
		return myIsland;
	}
	
	public int getIslandNum() {
		return _islandNum;
	}
	
	public void setIslandNum(int islandNum) {
		_islandNum = islandNum;
	}
	
	public int getNum() {
		return _num;
	}
	
	public void setNum(int num) {
		_num = num;
	}
	
	public double getVMag() {
		return _vmag;
	}
	
	public void setVMag(double vmag) {
		_vmag = vmag;
	}
	
	public double getVAng() {
		return _vang;
	}
	
	public void setVAng(double vang) {
		_vang = vang;
	}
	
	public boolean getIsSlack() {
		return (_isSlack | _isTempSlack);
	}
	
	public boolean getIsSlackByDefinition() {
		return _isSlack;
	}
	
	public void setIsSlack(boolean isSlack) {
		_isSlack = isSlack;
	}
	
	public void setIsTempSlack(boolean isTempSlack) {
		_isTempSlack = isTempSlack;
	}
	
	public void addAttachedBranch(BranchData branch) {
		if (!(_branches.contains(branch))) {
			_branches.add(branch);
		}
	}
	
	public void removeAttachedBranch(BranchData branch) {
		_branches.remove(branch);
	}
	
	public ArrayList<BranchData> getAttachedBranches() {
		return _branches;
	}
	
	public void addAttachedLoad(LoadData load) {
		if (!(_loads.contains(load))) {
			_loads.add(load);
		}
	}
	
	public void removeAttachedLoad(LoadData load) {
		_loads.remove(load);
	}
	
	public ArrayList<LoadData> getAttachedLoads() {
		return _loads;
	}
	
	public double getTotalLoad() {
		double retval = 0;
		
		ArrayList<LoadData> loads = getAttachedLoads();
		for (int i = 0; i < loads.size(); i++) {
			retval += loads.get(i).getMW();
		}
		
		return retval;
	}
	
	public double getTotalPeakLoad() {
		return getTotalLoad();
	}
	
	public double getTotalGenerationCapacity() {
		double retval = 0;

		ArrayList<GeneratorData> gendatas = getAttachedGenerators();
		for (int i = 0; i < gendatas.size(); i++) {
			if (gendatas.get(i).getConnected())
				retval += gendatas.get(i).getMaxMW();
		}

		return retval;		
	}
	
	public void addAttachedGenerator(GeneratorData gen) {
		if (!(_gens.contains(gen))) {
			_gens.add(gen);
		}
	}
	
	public void removeAttachedGenerator(GeneratorData gen) {
		_gens.remove(gen);
	}
	
	public ArrayList<GeneratorData> getAttachedGenerators() {
		return _gens;
	}
	
	public double getMWGenerated() {
		double retval = 0;

		ArrayList<GeneratorData> gendatas = getAttachedGenerators();
		for (int i = 0; i < gendatas.size(); i++) {
			if (gendatas.get(i).getConnected())
				retval += gendatas.get(i).getMW();
		}
		
		ArrayList<LoadData> loaddatas = getAttachedLoads();
		for (int i = 0; i < loaddatas.size(); i++) {
			if (loaddatas.get(i).getConnected()) 
				retval -= loaddatas.get(i).getMW();
		}
		return retval;
	}
	
	public double getMWReserves() {
		double retval = 0;
		boolean foundGeneration = false;
		
		ArrayList<GeneratorData> gendatas = getAttachedGenerators();
		for (int i = 0; i < gendatas.size(); i++) {
			if (gendatas.get(i).getConnected()) {
				foundGeneration = true;
				retval += gendatas.get(i).getMaxMW() - gendatas.get(i).getMW();
			}
		}		
		
		if (foundGeneration)
			return retval;
		else
			return Double.NEGATIVE_INFINITY;
	}

	public String getName() {
		return name;
	}
}
