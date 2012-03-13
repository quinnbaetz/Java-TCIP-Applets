package edu.uiuc.power.dataobjects;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.uiuc.power.dataobjects.IslandData.SolveResult;
import edu.uiuc.power.displayobjects.Animatable;

import Jama.Matrix;



public class PowerSystem implements TopologyChangeListener, Animatable {
	public enum PowerSystemSolutionMethod {
		NONOPF,ANL_OPF,ZEB_OPF;
	}
	private ArrayList<NodeData> nodes;
	private ArrayList<LoadData> loads;
	private ArrayList<BranchData> branches;
	private ArrayList<GeneratorData> generators;
	private ArrayList<IslandData> islands;
	private PowerSystemSolutionMethod solveMethod;

	private ArrayList<Boolean> branchClosedBackup = new ArrayList<Boolean>();
	private ArrayList<Double> generatorsMWBackup = new ArrayList<Double>();
	private ArrayList<Double> loadsMWBackup = new ArrayList<Double>();

	private boolean islandsValid;
	
	private boolean systemBlackout;
	
	public boolean getSystemBlackout() {
		return systemBlackout;
	}
	
	public NodeData findNodeByNumber(int nodeID) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getNum() == nodeID)
				return nodes.get(i);
		}
		return null;
	}
	
	public double getMinCostForSameIsland(GeneratorData genInIsland) {
		double retval = 0;
			
		int islNum = genInIsland.getConnectNode().getIslandNum();
		
		IslandData foundIsland = null;
		boolean foundIslandFlag = false;
		int islNumCheck = 0;
		
		while ((islNumCheck < islands.size()) & (!foundIslandFlag)) {
			if (islands.get(islNumCheck).IslandNum == islNum) {
				foundIsland = islands.get(islNumCheck);
				return foundIsland.getMinimumNonSlackGenCost();
			}
			else
				islNumCheck++;
		}
		return -1;
	}
	
	public void saveState() {
		for (int i = 0; i < branches.size(); i++) {
			branchClosedBackup.add(i,branches.get(i).getClosed());
		}
		for (int j = 0; j < generators.size(); j++) {
			generatorsMWBackup.add(j,generators.get(j).getMWSetpoint());
		}
		for (int k = 0; k < loads.size(); k++) {
			loadsMWBackup.add(k,loads.get(k).getMWSupplied());
		}
	}
	
	public enum restoreSettings {BRANCH_STATUS, GEN_MW, LOAD_MW};
	
	public void restoreState() {
		restoreSettings[] allRestoreOptions = restoreSettings.values();
		for (int i = 0; i < allRestoreOptions.length; i++)
			restoreState(allRestoreOptions[i]);
	}
	
	public void restoreState(restoreSettings whatToRestore) {
		switch (whatToRestore) {
			case BRANCH_STATUS:
				for (int i = 0; i < branches.size(); i++) {
					branches.get(i).setClosed(branchClosedBackup.get(i));
				}
				break;
			case GEN_MW:
				for (int j = 0; j < generators.size(); j++) {
					generators.get(j).setMW(generatorsMWBackup.get(j));
				}
				break;
			case LOAD_MW:
				for (int k = 0; k < loads.size(); k++) {
					loads.get(k).setMW(loadsMWBackup.get(k));
				}
		}
	}
	public PowerSystem() {
		this(PowerSystemSolutionMethod.NONOPF);
	}
	
	public PowerSystem(PowerSystemSolutionMethod solveMethod) {
		nodes = new ArrayList<NodeData>();
		loads = new ArrayList<LoadData>();
		branches = new ArrayList<BranchData>();
		generators = new ArrayList<GeneratorData>();
		islandsValid = false;
		this.solveMethod = solveMethod;
	}
	
	public ArrayList<NodeData> getNodes() {
		return nodes;
	}
	
	public ArrayList<LoadData> getLoads() {
		return loads;
	}
	
	public ArrayList<GeneratorData> getGenerators() {
		return generators;
	}
	
	public ArrayList<BranchData> getBranches() {
		return branches;
	}
	
	public BranchData findBranch(int fromBusNum, int toBusNum) {
		for (int i = 0; i < branches.size(); i++) {
			BranchData branch = branches.get(i);
			int bFromBus = branch.getFromNode().getNum();
			int bToBus = branch.getToNode().getNum();
			if (((bFromBus == fromBusNum) & (bToBus == toBusNum)) | ((bFromBus == toBusNum) & (bToBus == fromBusNum))) {
				return branch;
			}
		}
		return null;
	}
	
	private ArrayList<IslandData> buildIslands() {
		if (islands != null) {
			for (int i = 0; i < islands.size(); i++) {
				islands.get(i).cleanUpListeners();
			}
		}
		
		ArrayList<IslandData> islands = new ArrayList<IslandData>();
		
		// First, set all nodes to not have an island.  Also, 
		// detect the slack bus while we're at it
		ArrayList<NodeData> nodes = getNodes();
		NodeData slackNode = null;
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setIslandNum(-1);
			nodes.get(i).setIslandData(null);
			nodes.get(i).setIsTempSlack(false);
			if (nodes.get(i).getIsSlackByDefinition())
				slackNode = nodes.get(i);
		}
		
		int islandNum = 0;
		int nodesTagged = 0;
		
		boolean doneIslandCreation = false;
		
		while (!doneIslandCreation) {
			boolean foundASlackNode = false;
			
			if (slackNode == null) {
				int i = 0;
				while ((i < nodes.size()) & (!foundASlackNode)) {
					/*
					// Find a node without an island that
					// has generation
					NodeData node = nodes.get(i);
					if (node.getIslandNum() == -1) {
						ArrayList<GeneratorData> nodeGens = node.getAttachedGenerators();
						//System.out.println("node " + node.getNum() + " gens.size = " + nodeGens.size() );
						for (int j = 0; j < nodeGens.size(); j++) {
							if (nodeGens.get(j).getConnected(false)) {
								slackNode = node;
								//node.setIsTempSlack(true);
								//node.setVAng(0);
								foundASlackNode = true;
							}
						}
					}
					i++;
					*/
					// Find a node that is a candidate slack node
					NodeData node = nodes.get(i);
					if (node.getIslandNum() == -1) {
						if (node.getIsCandidateSlack() & (node.getAttachedGenerators().size() > 0)) {
							slackNode = node;
							foundASlackNode = true;
						}
					}
					i++;
				}
			} else {
				foundASlackNode = true;
			}
				
			if (foundASlackNode) {
				islandNum++;
				IslandData island = new IslandData(islandNum,this);
				islands.add(island);
				
				NodeData currentSlackNode = slackNode;
				
				double totalIslandLoad = 0;
				double totalIslandGenCapacity = 0;
				
				LinkedList<NodeData> nodesVisited = new LinkedList<NodeData>();
				slackNode.setIslandNum(islandNum);
				slackNode.setIslandData(island);
				nodesVisited.add(slackNode);
				slackNode = null;
				while (nodesVisited.size() > 0) {
					NodeData node = nodesVisited.removeFirst();
					
					totalIslandLoad += node.getTotalLoad();
					totalIslandGenCapacity += node.getTotalGenerationCapacity();
					
					island.addNode(node);
					node.registerAllLoadsAndGeneratorsWithPowerChangeListener(island);
					nodesTagged++;
					ArrayList<BranchData> attachedBranches = node.getAttachedBranches();
					for (int j = 0; j < attachedBranches.size(); j++) {
						if (attachedBranches.get(j).getClosed()) {
							NodeData toAdd = null;
							if (attachedBranches.get(j).getFromNode() == node)
								toAdd = attachedBranches.get(j).getToNode();
							else 
								toAdd = attachedBranches.get(j).getFromNode();

							if (toAdd.getIslandNum() == -1) {
								toAdd.setIslandNum(islandNum);
								toAdd.setIslandData(island);
								nodesVisited.add(toAdd);
							}
						}
					}
				}
				
				/*
				System.out.println("island " + island.IslandNum + " has " +
						totalIslandLoad + " MW load and " 
						+ totalIslandGenCapacity + " MW generation capacity.");
				*/	
				
				currentSlackNode.setIsTempSlack(true);
				currentSlackNode.setVAng(0);
				if (nodesTagged == nodes.size())
					doneIslandCreation = true;
			} else {
				doneIslandCreation = true;
			}
		}
		return islands;
	}
	
	public void solve() {
		if (!islandsValid) {
			islands = buildIslands();
			islandsValid = true;
		}
		
		systemBlackout = false;
		
		for (int i = 0; i < islands.size(); i++) {
			
			IslandData island = islands.get(i);
			/*
			System.out.println("islands[" + i + "] node count = " + island.getNodes().size());
			for (int j = 0; j < island.getNodes().size(); j++) {
				System.out.println(island.getNodes().get(j).getNum());
			}
			*/
			
			SolveResult sresult = island.solve(solveMethod);
			if (sresult != SolveResult.SUCCESS) {
				//System.out.println("BLACKOUT");
				systemBlackout = true;
			}
		}
	}
	
	public void addNode(NodeData node) {
		if (!nodes.contains(node))
			nodes.add(node);
		TopologyChanged();
	}
	
	public void removeNode(NodeData node) {
		nodes.remove(node);
		TopologyChanged();
	}
	
	public void addBranch(BranchData branch) {
		if (!branches.contains(branch))
			branches.add(branch);
		branch.registerTopologyChangeListener(this);
		
		TopologyChanged();
	}
	
	public void removeBranch(BranchData branch) {
		branches.remove(branch);
		branch.removeTopologyChangeListener(this);
		branch.unlinkFromNodes();
		TopologyChanged();
	}
	
	public void addLoad(LoadData load) {
		if (!loads.contains(load))
			loads.add(load);
	}
	
	public void removeLoad(LoadData load) {
		loads.remove(load);
		load.unlinkFromNodes();
	}
	
	public void addGenerator(GeneratorData gen) {
		if (!generators.contains(gen))
			generators.add(gen);
	}
	
	public void removeGenerator(GeneratorData gen) {
		generators.remove(gen);
		gen.unlinkFromNodes();
	}
	
	public void TopologyChanged() {
		islandsValid = false;
	}

	int blackoutWaitCounter = 0;
	int blackoutCounterMax = 5;
	
	public boolean animationstep() {
		if (getSystemBlackout()) {
			if (blackoutWaitCounter++ > blackoutCounterMax) {
				solve();
				blackoutWaitCounter = 0;
				return true;
			} else {
				return false;
			}
		} else {
			blackoutWaitCounter = 0;
			solve();
			return true;
		}
		
	}	
	
}
