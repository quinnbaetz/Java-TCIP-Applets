package edu.uiuc.power.dataobjects;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.uiuc.power.dataobjects.PowerSystem.PowerSystemSolutionMethod;
import edu.uiuc.power.dataobjects.lp.LPVariableGen;
import edu.utoronto.power.lpsolver.LPConstraint;
import edu.utoronto.power.lpsolver.LPException;
import edu.utoronto.power.lpsolver.LPSolver;
import edu.utoronto.power.lpsolver.LPVariable;
import edu.utoronto.power.lpsolver.LPSolver.LPSolverVerbosityLevel;

import anl.lpsolver.revisedSimplex;

import Jama.Matrix;

class GeneratorComparator implements Comparator<CostAndEmissionsProvider> {

	public int compare(CostAndEmissionsProvider o1,
			CostAndEmissionsProvider o2) {
		return Double.compare(o1.getCostPerMWh(), o2.getCostPerMWh());
		}
}
	

public class IslandData implements PowerChangeListener {
	
	enum SolveResult {
		BLACKOUT,
		SUCCESS
	}
	
	enum GenerationAllocationResult {BLACKOUT, SUCCESS};
	
	private ArrayList<NodeData> nodes;
	
	private Matrix BMatrix;
	private Matrix BMatrixInverse;
	
	private Hashtable<NodeData,Integer> MapBusToRowNum;
	private Hashtable<Integer,NodeData> MapRowNumToBus;
	
	int IslandNum;
	
	boolean solutionIsValid;
	
	public double getMinimumNonSlackGenCost() {
		double minimumGenCost = Double.MAX_VALUE;
		
		for (int i = 0; i < nodes.size(); i++) {
			NodeData nodeCheck = nodes.get(i);
			boolean isCandidateSlack = nodeCheck.getIsCandidateSlack() | nodeCheck.getIsSlack();
			if (!isCandidateSlack) {
				ArrayList<GeneratorData> nodeGens = nodeCheck.getAttachedGenerators();
				for (int j = 0; j < nodeGens.size(); j++) {
					GeneratorData genCheck = nodeGens.get(j);
					if (genCheck instanceof CostAndEmissionsProvider) {
						double costCheck = ((CostAndEmissionsProvider)genCheck).getCost(1);
						if (costCheck < minimumGenCost) 
							minimumGenCost = costCheck;
					}
				}
			} 
		}
		
		return minimumGenCost;
	}
	
	PowerSystem parent;
	
	public IslandData(int IslandNum, PowerSystem parent) {
		this.IslandNum = IslandNum;
		this.parent = parent;
		
		nodes = new ArrayList<NodeData>();
		
		MapBusToRowNum = new Hashtable<NodeData,Integer>();
		MapRowNumToBus = new Hashtable<Integer,NodeData>();
		
		BMatrix = null;
		
		solutionIsValid = false;
	}
	
	public void cleanUpListeners() {
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).unregisterAllLoadsAndGeneratorsWithPowerChangeListener(this);
		}
	}
	
	public void splitPowerBetweenSlacks() {
		
		ArrayList<NodeData> extraSlackNodes = new ArrayList<NodeData>();
		NodeData slackNode = null;
		double slackExportedPower = 0.0;
		for (int i = 0; i < nodes.size(); i++) {
			NodeData node = nodes.get(i);
			
			if (node.getIsSlack())
				slackNode = node;
			else if (node.getIsCandidateSlack()) {
				extraSlackNodes.add(node);
				ArrayList<GeneratorData> extraSlackNodeGens = node.getAttachedGenerators();
				for (int j = 0; j < extraSlackNodeGens.size(); j++) {
					GeneratorData extraSlackNodeGen = extraSlackNodeGens.get(j);
					slackExportedPower += extraSlackNodeGen.getMW();
				}
			}
		}
		
		//double slackExportedPower = 0;
		
		if (slackNode != null) {
			ArrayList<BranchData> slackBranches = slackNode.getAttachedBranches();
			for (int i = 0; i < slackBranches.size(); i++) {
				BranchData slackBranch = slackBranches.get(i);
				if (slackBranch.getClosed()) {
					if (slackBranch.getFromNode() == slackNode)
						slackExportedPower += slackBranch.getDCMWFlow();
					else 
						slackExportedPower -= slackBranch.getDCMWFlow();
				}
			}
		}
		
		//System.out.println("slackExportedPower for island " + this.IslandNum + " = " + slackExportedPower);
		
		double eachSlackShouldContribute = slackExportedPower/(extraSlackNodes.size() + 1);
		
		//System.out.println("eachSlackShouldContribute for island " + this.IslandNum + " = " + eachSlackShouldContribute);
		
		// Redistribute power between slack nodes
		for (int i = 0; i < extraSlackNodes.size(); i++) {
			NodeData extraSlackNode = extraSlackNodes.get(i);
			ArrayList<GeneratorData> extraSlackNodeGens = extraSlackNode.getAttachedGenerators();
			for (int j = 0; j < extraSlackNodeGens.size(); j++) {
				GeneratorData extraSlackNodeGen = extraSlackNodeGens.get(j);
				
				extraSlackNodeGen.setMW((slackExportedPower/(extraSlackNodes.size() + 1))/extraSlackNodeGens.size());
			}
		}
		
		
		//System.out.println("extraSlackNodes.size() for island " + this.IslandNum + " = " + extraSlackNodes.size());
		
		// figure out how much the slack is generating
	}
	
	Matrix ISF = null;
	Matrix SusceptanceMatrix = null;
	Matrix IncidenceMatrix = null;
	
	ArrayList<BranchData> allLines = null;
	HashSet<BranchData> lineSet = null;
	HashMap<BranchData,Integer> branchToISFRowNum = null;
	ArrayList<GeneratorData> genSet = null;
	
	static final double PBalancePenalty = 1e10;
	static final double LBalancePenalty = 1e8;

	
	public class LPLineConstraintsAndVariablesContainer {
		ArrayList<LPConstraint> lineConstraints = new ArrayList<LPConstraint>();
		ArrayList<LPVariable> lineVariables = new ArrayList<LPVariable>();
		double[] lineConstraintInitialSlackValue = new double[lineSet.size()];
		
		public void setAllBranchFlowSlackVariables() {
			for (int i = 0; i < lineVariables.size(); i++) {
				try {
					lineVariables.get(i).SetValue(lineConstraintInitialSlackValue[i],true);
				} catch (LPException e) {
					System.out.println("Error setting value for variable " + lineVariables.get(i).VariableName + ": " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}
	
	public class LPPowerBalanceConstraintAndGenVariablesContainer {
		LPConstraint powerBalanceConstraint;
		LPVariable powerBalanceVariable;
		
		ArrayList<LPVariableGen> genVariables = new ArrayList<LPVariableGen>();
		ArrayList<GeneratorData> generators = new ArrayList<GeneratorData>();
		
		public LPPowerBalanceConstraintAndGenVariablesContainer() {
			powerBalanceConstraint = new LPConstraint("Power balance constraint");
			powerBalanceConstraint.DefineRightHandSide(0.0, LPConstraint.EQUAL);
			powerBalanceVariable = new LPVariable("Power balance variable",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
			powerBalanceVariable.AddCostSegmentWithPenalties(0, 0, 0,PBalancePenalty);
			powerBalanceConstraint.AddElement(1.0, powerBalanceVariable);
		}
		
		public void allocateDeltaGeneration() {
			for (int i = 0; i < generators.size(); i++) {
				generators.get(i).setMW(generators.get(i).getMW() + genVariables.get(i).GetValue());
			}
		}
	}
	
	public GenerationAllocationResult solveUsingZebLP() {
		
		boolean includeBranchConstraints = true;
		
		if (nodes.size() > 1) {
			
			// First, solve the case since we're optimizing the deltas from the current state
			Matrix PMatrix = BuildPMatrix();
			Matrix Angles = BMatrixInverse.times(PMatrix);
			DistributeSolvedAngles(Angles);
			
			// If necessary, build lineSet, which also builds the incidence and susceptance matrices
			if (allLines == null) {
				buildLineSetAndISFMatrix();
			}
			
			// If necessary, build genSet
			if (genSet == null) {
				buildGenSet();
			}
			
			createLineSet(10.0);
			
			// initialize list of constraints & variables
			ArrayList<LPConstraint> lpConstraints = new ArrayList<LPConstraint>();
			ArrayList<LPVariable> lpVariables = new ArrayList<LPVariable>();
			
			// initialize data container for line constraint information
			double[][] lineConstraintParams = new double[lineSet.size()][3];	// [][0] = initial value for BFS, [][1] = left bound of slack var, [][2] = right bound of slack var
			LPLineConstraintsAndVariablesContainer lineLPContainer = fillLineConstraintParams(lineConstraintParams);		

			// build genConstraintParams & add generator offsets to initial slack value for each line constraint
			// also, add generation effects on the line flow constraints
			double[][] genConstraintParams = new double[genSet.size()][3]; 			// [][0] = initial value, [][1] = min gen, [][2] = max gen
			LPPowerBalanceConstraintAndGenVariablesContainer genLPContainer = fillGenConstraintParams(genConstraintParams,lineLPContainer);

			
			// set all the branch flow slack variables so that we start at a basic feasible solution
			lineLPContainer.setAllBranchFlowSlackVariables();

			// aggregate power balance & line constraints
			ArrayList<LPConstraint> aggregateConstraints = new ArrayList<LPConstraint>();
			aggregateConstraints.add(genLPContainer.powerBalanceConstraint);
			if (includeBranchConstraints)
				aggregateConstraints.addAll(lineLPContainer.lineConstraints);
			
			// aggregate generator & slack variables
			ArrayList<LPVariable> aggregateVariables = new ArrayList<LPVariable>();
			aggregateVariables.add(genLPContainer.powerBalanceVariable);
			aggregateVariables.addAll(genLPContainer.genVariables);
			if (includeBranchConstraints)
				aggregateVariables.addAll(lineLPContainer.lineVariables);
			// Initialize the LPSolver
			LPSolver lpSolver= new LPSolver(aggregateVariables, aggregateConstraints);

			ArrayList<LPVariable> basisVariables = new ArrayList<LPVariable>();
			basisVariables.add(genLPContainer.powerBalanceVariable);
			if (includeBranchConstraints)
				basisVariables.addAll(lineLPContainer.lineVariables);
			
			ArrayList<LPVariable> nonBasisVariables = new ArrayList<LPVariable>();
			nonBasisVariables.addAll(genLPContainer.genVariables);
			
			Matrix lambda = lpSolver.SolveLP(basisVariables, nonBasisVariables, 1000,LPSolverVerbosityLevel.ITERATIONCOUNT);
			if (lambda == null)
				return GenerationAllocationResult.BLACKOUT;
			else {
				genLPContainer.allocateDeltaGeneration();
				// First, solve the case since we're optimizing the deltas from the current state
				PMatrix = BuildPMatrix();
				Angles = BMatrixInverse.times(PMatrix);
				DistributeSolvedAngles(Angles);
				
				return GenerationAllocationResult.SUCCESS;
			}
				
			
		} else
			return GenerationAllocationResult.SUCCESS;
	}
	
	private LPPowerBalanceConstraintAndGenVariablesContainer fillGenConstraintParams(double[][] genConstraintParams, 
			LPLineConstraintsAndVariablesContainer lineLPContainer) {
		LPPowerBalanceConstraintAndGenVariablesContainer retVals = new LPPowerBalanceConstraintAndGenVariablesContainer();
		LPConstraint pBalanceCon = retVals.powerBalanceConstraint;
		LPVariable pBalanceVar = retVals.powerBalanceVariable; 
		double initialPMismatch = 0;
		for (int gIdx = 0; gIdx < genSet.size(); gIdx++) {
			GeneratorData gen = genSet.get(gIdx);
			double currentMW = gen.getMW();
			double deltaMWLowerBound = gen.getMinMW() - currentMW;
			double deltaMWUpperBound = gen.getMaxMW() - currentMW;
			if ((deltaMWUpperBound - deltaMWLowerBound) > 1e-4) {
				genConstraintParams[gIdx][0] = deltaMWLowerBound;
				genConstraintParams[gIdx][1] = deltaMWLowerBound;
				genConstraintParams[gIdx][2] = deltaMWUpperBound;
				
				LPVariableGen genVar = new LPVariableGen("Generator at " + gen.getConnectNode().getName(),deltaMWLowerBound,deltaMWUpperBound,gen);
				if (gen instanceof CostAndEmissionsProvider) {
					CostAndEmissionsProvider cep = (CostAndEmissionsProvider)gen;
					genVar.AddCostSegment(deltaMWLowerBound, deltaMWUpperBound, cep.getCostPerMWh());
				}
				retVals.genVariables.add(genVar);
				retVals.generators.add(gen);
				try {
					genVar.SetValue(deltaMWLowerBound, false);
					initialPMismatch -= deltaMWLowerBound;
				} catch (LPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				pBalanceCon.AddElement(1.0, genVar);
				
				int genNodeRowNum = getNodeRowNum(gen.getConnectNode());
				if (genNodeRowNum > -1) { // nonslack
					for (int lIdx = 0; lIdx < lineSet.size(); lIdx++) {
						lineLPContainer.lineConstraints.get(lIdx).AddElement(ISF.get(lIdx,genNodeRowNum), genVar);
						lineLPContainer.lineConstraintInitialSlackValue[lIdx] -= genConstraintParams[gIdx][0]*ISF.get(lIdx,genNodeRowNum);
					}
				}
			} else {
				gen.setMW(gen.getMW() + deltaMWLowerBound);
			}
		}
		try {
			pBalanceVar.SetValue(initialPMismatch, true);
		} catch (LPException e) {
			System.out.println("Unable to set value of power balance: " + e.toString());
		}
		return retVals;
	}
	
	private void createLineSet(double thresholdPercentage) {
		HashSet<BranchData> closeToViolationBranches = new HashSet<BranchData>();
		Iterator<BranchData> biter = allLines.iterator();
		while (biter.hasNext()) {
			BranchData b = biter.next();
			double overFlow = Math.abs(b.getMW()) - b.getMaxMWFlow();
			if (overFlow > -thresholdPercentage*b.getMaxMWFlow()) {
				closeToViolationBranches.add(b);
			}
		}
		lineSet = new HashSet<BranchData>(closeToViolationBranches);
	}	
	
	private LPLineConstraintsAndVariablesContainer fillLineConstraintParams(double[][] lineConstraintParams) {
		LPLineConstraintsAndVariablesContainer retVal = new LPLineConstraintsAndVariablesContainer();
		
		ArrayList<LPVariable> lpVars = retVal.lineVariables;
		ArrayList<LPConstraint> lpCons = retVal.lineConstraints;
		
		Iterator<BranchData> bIter = lineSet.iterator();
		
		while (bIter.hasNext()) {
			BranchData branch = bIter.next();
			double currentMW = branch.getMW();
			double maxMW = branch.getMaxMWFlow();
			double slackLowerBound = -maxMW + currentMW;
			double slackUpperBound = maxMW + currentMW;
			
			// add LP constraint & variable
			LPConstraint branchFlowConstraint = new LPConstraint("Line " + branch.getFromNode().getName() + " to " + branch.getToNode().getName() + " delta flow");
			branchFlowConstraint.DefineRightHandSide(0.0, LPConstraint.EQUAL);
			lpCons.add(branchFlowConstraint);
			
			LPVariable branchFlowSlackVariable = new LPVariable("Line " + branch.getFromNode().getName() + " to " + branch.getToNode().getName() + " delta flow slack",
					Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
			branchFlowConstraint.AddElement(1.0, branchFlowSlackVariable);
			branchFlowSlackVariable.AddCostSegmentWithPenalties(0, slackLowerBound, slackUpperBound,LBalancePenalty);
			lpVars.add(branchFlowSlackVariable);
			
//			lineConstraintParams[branchToISFRowNum.get(branch)][0] = 0;
//			lineConstraintParams[branchToISFRowNum.get(branch)][1] = slackLowerBound;
//			lineConstraintParams[branchToISFRowNum.get(branch)][2] = slackUpperBound;
		}
		return retVal;
	}

	private void buildGenSet() {
		genSet = new ArrayList<GeneratorData>();
		for (int i = 0; i < nodes.size(); i++) {
			genSet.addAll(nodes.get(i).getAttachedGenerators());
		}
	}
	
	
	
	private void buildLineSetAndISFMatrix() {
		HashSet<BranchData> lineSetTemp = new HashSet<BranchData>();
		for (int i = 0; i < nodes.size(); i++) {
			lineSetTemp.addAll(nodes.get(i).getAttachedBranches());
		}
		allLines = new ArrayList<BranchData>(lineSetTemp);
		//lineSet = new ArrayList<BranchData>(lineSetTemp);
		
		SusceptanceMatrix = new Matrix(allLines.size(),allLines.size());
		IncidenceMatrix = new Matrix(allLines.size(),nodes.size() - 1); // -1 because slack bus is not given a column
		//BMatrixInverse already built before getting to here
		
		branchToISFRowNum = new HashMap<BranchData, Integer>();
		for (int bIdx = 0; bIdx < allLines.size(); bIdx++) {
			BranchData branch = allLines.get(bIdx);
			branchToISFRowNum.put(branch, new Integer(bIdx));
			SusceptanceMatrix.set(bIdx, bIdx, 1/branch.getX());
			int fromBusIdx = getNodeRowNum(branch.getFromNode());
			int toBusIdx = getNodeRowNum(branch.getToNode());
			if (fromBusIdx >= 0)
				IncidenceMatrix.set(bIdx, fromBusIdx, 1);
			if (toBusIdx >= 0)
				IncidenceMatrix.set(bIdx, toBusIdx, -1);
		}
		
		ISF = SusceptanceMatrix.times(IncidenceMatrix.times(BMatrixInverse));		
	}	
	
	public SolveResult solve(PowerSystemSolutionMethod solveMethod) {
		if (!solutionIsValid) {
			if (BMatrix == null) {
				BuildBMatrix();
			}
			GenerationAllocationResult allocationResult;
			switch (solveMethod) {
				case ZEB_OPF:
					allocationResult = solveUsingZebLP();
					if (allocationResult == GenerationAllocationResult.BLACKOUT) {
						//System.out.println("Blackout in island " + IslandNum);
						solutionIsValid = false;
						return SolveResult.BLACKOUT;
					} else if (allocationResult == GenerationAllocationResult.SUCCESS) {
						//System.out.println("allocationResult in island " + IslandNum + " = " + allocationResult);
						solutionIsValid = true;
						return SolveResult.SUCCESS;
					} else
						return SolveResult.BLACKOUT;					
				case ANL_OPF:
//					long startTime = System.nanoTime();
					System.out.flush();
					allocationResult = solveOPF();
//					long endTime = System.nanoTime();
//					long elapsedNanoseconds = endTime - startTime;
//					System.out.println("Took " + (double)elapsedNanoseconds/(double)1e9 + " seconds to solve OPF");
//					System.out.flush();

					if (allocationResult == GenerationAllocationResult.BLACKOUT) {
						//System.out.println("Blackout in island " + IslandNum);
						solutionIsValid = false;
						return SolveResult.BLACKOUT;
					} else if (allocationResult == GenerationAllocationResult.SUCCESS) {
						//System.out.println("allocationResult in island " + IslandNum + " = " + allocationResult);
						solutionIsValid = true;
						return SolveResult.SUCCESS;
					} else
						return SolveResult.BLACKOUT;
				case NONOPF:
					//BMatrix.print(new DecimalFormat(), 10);
					
					Matrix PMatrix = BuildPMatrix();
					
					Matrix Angles = BMatrixInverse.times(PMatrix);
					
					DistributeSolvedAngles(Angles);
					
					splitPowerBetweenSlacks();
					
					PMatrix = BuildPMatrix();
					
					Angles = BMatrixInverse.times(PMatrix);
					
					DistributeSolvedAngles(Angles);
					
					solutionIsValid = true;
					
					return SolveResult.SUCCESS;					
					
			}
		} else {
			return SolveResult.SUCCESS;
		}
		return SolveResult.SUCCESS;
	}
	
	public void addNode(NodeData node) {
		if (!nodes.contains(node))
			nodes.add(node);
	}
	
	public void removeNode(NodeData node) {
		nodes.remove(node);
	}
	
	public ArrayList<NodeData> getNodes() {
		return nodes;
	}
	
	public double getTotalLoad() {
		double totalLoad = 0.0;
		for (int i = 0; i < nodes.size(); i++) {
			totalLoad += nodes.get(i).getTotalLoad();
		}
		return totalLoad;
	}
	
	private void DistributeSolvedAngles(Matrix Angles) {
		for (int i = 0; i < nodes.size(); i++) {
			NodeData node = nodes.get(i);
			int rownum = getNodeRowNum(node);
			if (rownum >= 0)
				node.setVAng(Angles.get(rownum, 0));
		}
	}
	
	private int getNodeRowNum(NodeData node) {
		Integer rownumObj = MapBusToRowNum.get(node);
		if (rownumObj != null) {
			return rownumObj.intValue();
		} else
			return -1;
	}
	
	private int getNodeIdxFromRowNum(Integer rowNum) {
		NodeData node = MapRowNumToBus.get(rowNum);
		return nodes.indexOf(node);
	}
	
	private Matrix BuildPMatrix() {
		Matrix PMatrix = new Matrix(BMatrix.getRowDimension(),1);

		// Handle all loads and generators
		for (int i = 0; i < nodes.size(); i++) {
			NodeData node = nodes.get(i);
			int rownum = getNodeRowNum(node);
			if (rownum >= 0) {
				PMatrix.set(rownum, 0, node.getMWGenerated());
			}
		}
		
		return PMatrix;
	}
	
	private void BuildBMatrix() {
		BMatrix = new Matrix(nodes.size()-1,nodes.size()-1);
		
		int hashTableRowNum = 0;
		
		// Build up the MapBusToRowNum hashtable
		for (int i = 0; i < nodes.size(); i++) {
			if (!(nodes.get(i).getIsSlack())) {
				int currentRowNum = hashTableRowNum++;
				MapBusToRowNum.put(nodes.get(i),new Integer(currentRowNum));
				MapRowNumToBus.put(new Integer(currentRowNum),nodes.get(i));
			} 
		}
		
		// Fill in the BMatrix
		for (int i = 0; i < nodes.size(); i++) {
			NodeData rowNode = nodes.get(i);
			int rowNum = getNodeRowNum(rowNode);
			if (rowNum >= 0) {
				ArrayList<BranchData> branches = rowNode.getAttachedBranches();
				for (int j = 0; j < branches.size(); j++) {
					BranchData branch = branches.get(j);
					if (branch.getClosed()) {
						double bfromto = - 1/branch.getX();
						
						// set the self term
						BMatrix.set(rowNum, 
								rowNum, 
								BMatrix.get(rowNum, rowNum) - bfromto 
								);

						int colNum = 0;
						if (branch.getFromNode() == rowNode)
							colNum = getNodeRowNum(branch.getToNode());
						else
							colNum = getNodeRowNum(branch.getFromNode());
						
						if (colNum >= 0)
							BMatrix.set(rowNum, 
									colNum, 
									BMatrix.get(rowNum, colNum) + bfromto 
									);							
					}
				}
			}
		}
		
		BMatrixInverse = BMatrix.inverse();
		
	}
	
	private void printCosts(ArrayList<CostAndEmissionsProvider> gens) {
		for (int i = 0; i < gens.size(); i++) {
			System.out.println(gens.get(i).getCostPerMWh());
		}
	}
	
	private class ThetaLPVariablesAndConstraintNumber {
		public int indexplus;
		public int indexminus;
		public int constraintNum;
		public float[] constraintCoeffs;
		public NodeData node;
		public float RHS = 0;
		public Hashtable<Integer,Integer> genVariableNums = new Hashtable<Integer, Integer>(); // maps the generator index into node.getGens to the LP variable number for this generator's output
		public Hashtable<Integer,Integer> genMaxGenConstraintNums = new Hashtable<Integer, Integer>(); // maps the generator index into node.getGens to the LP capacity constraint for this generator
		public Hashtable<Integer, float[]> genMaxGenConstraintCoeffs = new Hashtable<Integer,float[]>(); // maps the generator index into node.getGens to this generator's capacity
		public Hashtable<Integer,Float> genMaxGen = new Hashtable<Integer,Float>();
		public Hashtable<Integer,Float> genCosts = new Hashtable<Integer,Float>();
	}
	
	private class LPVariableInformation {
		protected String LPVariableDescriptor;
		protected int LPVariableType;
		protected Object LPVariableObject;
		
		final public static int thetaPlus = 0;
		final public static int thetaMinus = 1;
		final public static int generation = 2;
		final public static int storageAsLoad = 3;
		
		public LPVariableInformation(String variableDescriptor,
				int variableType, Object variableObject) {
			super();
			LPVariableDescriptor = variableDescriptor;
			LPVariableType = variableType;
			LPVariableObject = variableObject;
		}
	}
	
	private HashSet<BranchData> checkBranchFlows(HashSet<BranchData> allBranches, double thresholdPercentage) {
		HashSet<BranchData> closeToViolationBranches = new HashSet<BranchData>();
		Iterator<BranchData> biter = allBranches.iterator();
		while (biter.hasNext()) {
			BranchData b = biter.next();
			double overFlow = Math.abs(b.getMW()) - b.getMaxMWFlow();
			if (overFlow > -thresholdPercentage*b.getMaxMWFlow()) {
				closeToViolationBranches.add(b);
			}
		}
		return closeToViolationBranches;
	}
	
	private HashSet<GeneratorData> checkGenerators(ArrayList<GeneratorData> allGenerators, double thresholdPercentage) {
		HashSet<GeneratorData> closeToViolationGenerators = new HashSet<GeneratorData>();
		Iterator<GeneratorData> giter = allGenerators.iterator();
		while (giter.hasNext()) {
			GeneratorData g = giter.next();
			if (!(g instanceof StorageDevice)) {
				double overFlow = Math.abs(g.getMW()) - g.getMaxMW();
				if (overFlow > -thresholdPercentage*g.getMaxMW()) {
					closeToViolationGenerators.add(g);
				}
			}
		}
		return closeToViolationGenerators;
	}	
	
	private GenerationAllocationResult solveOPF() {
		boolean enableGenerationLimits = true;
		boolean enableLineFlowLimits = true;
		boolean usePartialFlowChecks = true;
		
		boolean branchConstraintsSatisfied = false;
		boolean genConstraintsSatisfied = false;
		int iterationCount = 0;
		int maxIterations = 10;
		
		ArrayList<GeneratorData> allGenerators = new ArrayList<GeneratorData>();
		
		HashSet<BranchData> allBranches = new HashSet<BranchData>();
		for (int i = 0; i < nodes.size(); i++) {
			allBranches.addAll(nodes.get(i).getAttachedBranches());
		}
		HashSet<BranchData> violatingBranches = new HashSet<BranchData>();
		HashSet<BranchData> hasViolatedBeforeBranches = new HashSet<BranchData>();
		
		HashSet<GeneratorData> violatingGens = new HashSet<GeneratorData>();
		HashSet<GeneratorData> hasViolatedBeforeGens = new HashSet<GeneratorData>();
		
		while (((!branchConstraintsSatisfied & enableLineFlowLimits) | (!genConstraintsSatisfied & enableGenerationLimits)) & (iterationCount < maxIterations)) {
			
			if (nodes.size() > 1) {
				int lpVariableCount = 0;
				int lpConstraintCount = 0;
				
				ArrayList<StorageDevice> storageDevices = new ArrayList<StorageDevice>();
				
				// Store a ThetaLPVariablesAndConstraintNumber structure for each node
				Hashtable<Integer,ThetaLPVariablesAndConstraintNumber> nodeIdxToVariableIdxs = new Hashtable<Integer, ThetaLPVariablesAndConstraintNumber>();
				
				// set up the slack constraint info
				float slackRHS = 0;
				float[] slackCoeffs = null;
				int slackConstraintIdx = lpConstraintCount++; // slackConstraintIdx is never used, but need to make sure and leave room for the constraint
				
				// Store a description for each LP variable
				Hashtable<Integer,LPVariableInformation> lpVariableDescriptors = new Hashtable<Integer, LPVariableInformation>();
				
				Hashtable<Integer,Float> slackGenCosts = new Hashtable<Integer, Float>();
				Hashtable<Integer,Integer> slackGenVariableIdxs = new Hashtable<Integer, Integer>(); // maps the generator index into slackBus.getGens() to the LP Variable number
				Hashtable<Integer,Integer> slackGenMaxGenConstraintNums = new Hashtable<Integer,Integer>(); // maps the generator index into slackBus.getGens() to the LP generator capacity constraint
				Hashtable<Integer,Float> slackGenMaxGens = new Hashtable<Integer, Float>(); // maps the generator index into slackBus.getGens() to the capacity of the generator 
				ArrayList<ThetaLPVariablesAndConstraintNumber> lpConstraintsTheta = new ArrayList<ThetaLPVariablesAndConstraintNumber>();
				
				Hashtable<StorageDevice,Integer> storageAsLoadToVariableIdx = new Hashtable<StorageDevice, Integer>();
				Hashtable<StorageDevice,Integer> storageAsLoadToMaxConstraintNum = new Hashtable<StorageDevice, Integer>();
				Hashtable<StorageDevice,Float> storageAsLoadToMaxDemand = new Hashtable<StorageDevice, Float>();
				
				float totalLoad = 0;
				
				for (int i = 0; i < nodes.size(); i++) {
					totalLoad += nodes.get(i).getTotalLoad(); // add node load to total load for power balance constraint
					if (getNodeRowNum(nodes.get(i)) >= 0) {
						// non slack
						
						// bookkeeping for angle at bus (angle = x(indexplus) - x(indexminus))
						ThetaLPVariablesAndConstraintNumber storeVals = new ThetaLPVariablesAndConstraintNumber();
						storeVals.node = nodes.get(i);
						storeVals.indexplus = lpVariableCount++;
						storeVals.indexminus = lpVariableCount++;
						lpVariableDescriptors.put((lpVariableCount-2), 
								new LPVariableInformation("Theta " + nodes.get(i).getNum() + " plus",LPVariableInformation.thetaPlus,nodes.get(i)));
						lpVariableDescriptors.put((lpVariableCount-1), 
								new LPVariableInformation("Theta " + nodes.get(i).getNum() + " minus",LPVariableInformation.thetaMinus,nodes.get(i)));
						
						// add the power balance constraint for this bus
						storeVals.constraintNum = lpConstraintCount++;
						lpConstraintsTheta.add(storeVals);
						nodeIdxToVariableIdxs.put(new Integer(i), storeVals); // map index into nodes to ThetaLP.. structure corresponding to node
						
						// load all generators from node
						ArrayList<GeneratorData> gens = nodes.get(i).getAttachedGenerators();
						
						for (int j = 0; j < gens.size(); j++) {
							if (gens.get(j).getConnected()) {
								if (!(gens.get(j) instanceof StorageDevice)) {
									// if not a storage device, then store bookkeeping information
									storeVals.genVariableNums.put(new Integer(j), new Integer(lpVariableCount++)); // store lp variable num for this generator
									lpVariableDescriptors.put((lpVariableCount-1), 
											new LPVariableInformation("Bus " + nodes.get(i).getNum() + 
													" generator " + j,LPVariableInformation.generation,gens.get(j))); // store description of lp variable corresponding to this gen 
									if (enableGenerationLimits) {
										// if generator limits enabled, set up max gen constraint
										if (gens.get(j) instanceof CostAndEmissionsProvider) {
											CostAndEmissionsProvider gCost = (CostAndEmissionsProvider)gens.get(j);
											if (gCost.getCostPerMWh() < 1e-3 | violatingGens.contains(gens.get(j)) | !usePartialFlowChecks) {
												storeVals.genMaxGenConstraintNums.put(new Integer(j),new Integer(lpConstraintCount++)); // store max generation LP constraint number
												storeVals.genMaxGen.put(new Integer(j), new Float(gens.get(j).getMaxMW())); // store maximum generation value
											} 
										} else if (violatingGens.contains(gens.get(j)) | !usePartialFlowChecks) {
											storeVals.genMaxGenConstraintNums.put(new Integer(j),new Integer(lpConstraintCount++)); // store max generation LP constraint number
											storeVals.genMaxGen.put(new Integer(j), new Float(gens.get(j).getMaxMW())); // store maximum generation value
										}
									}
									storeVals.genCosts.put(new Integer(j), (float)((CostAndEmissionsProvider)gens.get(j)).getCostPerMWh()); // store cost information for this gen
									allGenerators.add(gens.get(j)); // add to set of all generators
								} else {
									// handled separately from normal generators
									StorageDevice sdevice = (StorageDevice)gens.get(j);
									if (sdevice.treatAsGen()) {
										storeVals.genVariableNums.put(new Integer(j), new Integer(lpVariableCount++));
										lpVariableDescriptors.put((lpVariableCount-1), 
											new LPVariableInformation("Bus " + nodes.get(i).getNum() + " generator " + j, LPVariableInformation.generation,sdevice));
										if (enableGenerationLimits) {
											storeVals.genMaxGenConstraintNums.put(new Integer(j), new Integer(lpConstraintCount++));
											storeVals.genMaxGen.put(new Integer(j), new Float(sdevice.getMaxMW()));
										}
										storeVals.genCosts.put(new Integer(j),  (float)((CostAndEmissionsProvider)sdevice).getCostPerMWh());
										allGenerators.add(sdevice);
									} else {
										storageAsLoadToVariableIdx.put(sdevice, lpVariableCount++);
										lpVariableDescriptors.put(lpVariableCount -1, 
												new LPVariableInformation("Bus " + nodes.get(i).getNum() + " storage as load " + j, LPVariableInformation.storageAsLoad,sdevice));
										if (enableGenerationLimits) {
											storageAsLoadToMaxDemand.put(sdevice, new Float(-sdevice.getMaxMW()));
											storageAsLoadToMaxConstraintNum.put(sdevice, lpConstraintCount++);
										}
									}
									storageDevices.add(sdevice);
								}
							}
						}
					} else {
						// slack node
						ArrayList<GeneratorData> gens = nodes.get(i).getAttachedGenerators();
						for (int j = 0; j < gens.size(); j++) {
							if (gens.get(j).getConnected()) {
								if (!(gens.get(j) instanceof StorageDevice)) {
									slackGenVariableIdxs.put(new Integer(j), new Integer(lpVariableCount++));
									lpVariableDescriptors.put((lpVariableCount-1), 
											new LPVariableInformation("Bus " + nodes.get(i).getNum() + " (slack bus) generator " + j,LPVariableInformation.generation,gens.get(j)));
									if (enableGenerationLimits) {
										if (violatingGens.contains(gens.get(j))) {
											slackGenMaxGenConstraintNums.put(new Integer(j), new Integer(lpConstraintCount++));
											slackGenMaxGens.put(new Integer(j), new Float(gens.get(j).getMaxMW()));
										}
									}
									slackGenCosts.put(new Integer(j), (float)((CostAndEmissionsProvider)gens.get(j)).getCostPerMWh());
									allGenerators.add(gens.get(j));
								} else {
									StorageDevice sdevice = (StorageDevice)gens.get(j);
									if (sdevice.treatAsGen()) {
										slackGenVariableIdxs.put(new Integer(j), new Integer(lpVariableCount++));
										lpVariableDescriptors.put((lpVariableCount-1), 
											new LPVariableInformation("Bus " + nodes.get(i).getNum() + " (slack bus) generator " + j, LPVariableInformation.generation,sdevice));
										if (enableGenerationLimits) {
											slackGenMaxGenConstraintNums.put(new Integer(j), new Integer(lpConstraintCount++));
											slackGenMaxGens.put(new Integer(j), new Float(sdevice.getMaxMW()));
										}
										slackGenCosts.put(new Integer(j), (float)((CostAndEmissionsProvider)sdevice).getCostPerMWh());
									} else {
										storageAsLoadToVariableIdx.put(sdevice, lpVariableCount++);
										lpVariableDescriptors.put(lpVariableCount -1, 
												new LPVariableInformation("Bus " + nodes.get(i).getNum() + " storage as load " + j, LPVariableInformation.storageAsLoad,sdevice));
										if (enableGenerationLimits) {
											storageAsLoadToMaxDemand.put(sdevice, new Float(-sdevice.getMaxMW()));
											storageAsLoadToMaxConstraintNum.put(sdevice, lpConstraintCount++);
										}									
									}
									storageDevices.add((StorageDevice)gens.get(j));
								}
							}
						}
					}
				}
				
				float[] costcoeffs = new float[lpVariableCount]; // cost vector (costcoeffs.*genVals = totalCost)
				for (int i = 0; i < nodes.size(); i++) {
					ThetaLPVariablesAndConstraintNumber stored = nodeIdxToVariableIdxs.get(new Integer(i));
					if (stored != null) {
						// non slack

						// setup power balance constraint at this node
						stored.constraintCoeffs = new float[lpVariableCount];
						stored.constraintCoeffs[nodeIdxToVariableIdxs.get(new Integer(i)).indexplus] = -1;
						stored.constraintCoeffs[nodeIdxToVariableIdxs.get(new Integer(i)).indexminus] = +1;
						
						// add entries to costcoeffs for all gens at this node
						ArrayList<GeneratorData> gens = nodes.get(i).getAttachedGenerators();
						for (int j = 0; j < gens.size(); j++) {
							if (gens.get(j).getConnected()) {
								if (!(gens.get(j) instanceof StorageDevice)) {
									costcoeffs[stored.genVariableNums.get(new Integer(j))] = stored.genCosts.get(new Integer(j));
								} else {
									StorageDevice sdevice = (StorageDevice)gens.get(j);
									if (sdevice.treatAsGen()) {
										costcoeffs[stored.genVariableNums.get(new Integer(j))] = stored.genCosts.get(new Integer(j));
									} else {
										costcoeffs[storageAsLoadToVariableIdx.get(sdevice)] = -(float)sdevice.getCostPerMWh();
									}
								}
							} 
						}
					} else {
						// total Pgen = total Pload
						// sumSlackGens + sumOtherGens = sumLoad
						// slackCoeffs.*genVals = totalLoad
						slackCoeffs = new float[lpVariableCount];
						ArrayList<GeneratorData> gens = nodes.get(i).getAttachedGenerators();
						for (int j = 0; j < gens.size(); j++) {
							if (gens.get(j).getConnected()) {
								if (!(gens.get(j) instanceof StorageDevice)) {
									costcoeffs[slackGenVariableIdxs.get(new Integer(j))] = slackGenCosts.get(new Integer(j));
									slackCoeffs[slackGenVariableIdxs.get(new Integer(j))] = 1.0f;
								} else {
									StorageDevice sdevice = (StorageDevice)gens.get(j);
									if (sdevice.treatAsGen()) {
										costcoeffs[slackGenVariableIdxs.get(new Integer(j))] = slackGenCosts.get(new Integer(j));
										slackCoeffs[slackGenVariableIdxs.get(new Integer(j))] = 1.0f;
									} else {
										costcoeffs[storageAsLoadToVariableIdx.get(sdevice)] = -0.001f;
										slackCoeffs[storageAsLoadToVariableIdx.get(sdevice)] = -1.0f;
									}
								}
							}
						}
						
					}
				}
				
				// fill in slackCoeffs & the power balance constraint at each node 
				for (int i = 0; i < nodes.size(); i++) {
					NodeData rowNode = nodes.get(i);
					int rowNum = getNodeRowNum(rowNode);
					if (rowNum >= 0) {
						ArrayList<GeneratorData> gendatas = rowNode.getAttachedGenerators();
						
						for (int j = 0; j < gendatas.size(); j++) {
							GeneratorData genData = gendatas.get(j);
							if (genData.getConnected()) {
								if (!(gendatas.get(j) instanceof StorageDevice)) {
									// get lp variable index
									int lpvaridx = nodeIdxToVariableIdxs.get(new Integer(i)).genVariableNums.get(new Integer(j));
									// put gen in slack constraint
									slackCoeffs[lpvaridx] = 1.0f;
									// add term to each P balance equation at each node this node is connected to
									for (int k = 0; k < nodes.size(); k++) {
										ThetaLPVariablesAndConstraintNumber stored = nodeIdxToVariableIdxs.get(new Integer(k));
										if (stored != null) {
											stored.constraintCoeffs[lpvaridx] = (float)BMatrixInverse.get(getNodeRowNum(nodes.get(k)), rowNum);
										} 
									}
								} else {
									StorageDevice sdevice = (StorageDevice)gendatas.get(j);
									if (sdevice.treatAsGen()) {
										// get lp variable index
										int lpvaridx = nodeIdxToVariableIdxs.get(new Integer(i)).genVariableNums.get(new Integer(j));
										// put gen in slack constraint
										slackCoeffs[lpvaridx] = 1.0f;
										// add term to each theta equality constraint
										for (int k = 0; k < nodes.size(); k++) {
											ThetaLPVariablesAndConstraintNumber stored = nodeIdxToVariableIdxs.get(new Integer(k));
											if (stored != null) {
												stored.constraintCoeffs[lpvaridx] = (float)BMatrixInverse.get(getNodeRowNum(nodes.get(k)), rowNum);
											} 
										}									
									} else {
										// get lp variable index
										int lpvaridx = storageAsLoadToVariableIdx.get(sdevice);
										// put gen in slack constraint
										slackCoeffs[lpvaridx] = -1.0f;
										// add term to each theta equality constraint
										for (int k = 0; k < nodes.size(); k++) {
											ThetaLPVariablesAndConstraintNumber stored = nodeIdxToVariableIdxs.get(new Integer(k));
											if (stored != null) {
												stored.constraintCoeffs[lpvaridx] = -(float)BMatrixInverse.get(getNodeRowNum(nodes.get(k)), rowNum);
											} 
										}										
									}
								}
							}
						}	
						
						if (rowNode.getTotalLoad() != 0) {
							// add load to RHS of each theta equality constraint
							slackRHS += rowNode.getTotalLoad();
							for (int k = 0; k < nodes.size(); k++) {
								ThetaLPVariablesAndConstraintNumber stored = nodeIdxToVariableIdxs.get(new Integer(k));
								if (stored != null) {
									stored.RHS += (float)rowNode.getTotalLoad()*(float)BMatrixInverse.get(getNodeRowNum(nodes.get(k)), rowNum);
								}
							}
						}
					} 
				}
				
				ArrayList<float[]> branchLimitCoefficients = new ArrayList<float[]>();
				ArrayList<Float> branchLimits = new ArrayList<Float>();

//				if (iterationCount > 0) {
					if (enableLineFlowLimits) {
						//Iterator<BranchData> biter = allBranches.iterator();
						Iterator<BranchData> biter = violatingBranches.iterator();
						while (biter.hasNext()) {
							BranchData b = biter.next();
							if (b.getClosed()) {
								float[] bcoeff = new float[lpVariableCount];
								int fidx = nodes.indexOf(b.getFromNode());
								int tidx = nodes.indexOf(b.getToNode());
								if (fidx >= 0) {
									ThetaLPVariablesAndConstraintNumber ftlp = nodeIdxToVariableIdxs.get(fidx);
									if (ftlp != null) {
										float BVal = (float)(1.0/b.getX());
										bcoeff[ftlp.indexplus] = +BVal;
										bcoeff[ftlp.indexminus] = -BVal;
									}
								}
								if (tidx >= 0) {
									ThetaLPVariablesAndConstraintNumber ttlp = nodeIdxToVariableIdxs.get(new Integer(tidx));
									if (ttlp != null) {
										float BVal = (float)(1.0/b.getX()); 
										bcoeff[ttlp.indexplus] = -BVal;
										bcoeff[ttlp.indexminus] = BVal;
									}
								}
								branchLimitCoefficients.add(bcoeff);
								branchLimits.add(new Float(b.getMaxMWFlow()));
								lpConstraintCount += 2;
							}
						}
					}
//				}

				
				boolean printVerboseLP = false;
				revisedSimplex LP = new revisedSimplex(lpVariableCount,lpConstraintCount);
				LP.specifyObjective(costcoeffs, true);
				if (printVerboseLP) {
					printArray("costcoeffs",costcoeffs);
				}
				
				// add line constraints
				if (iterationCount > 0) {
					if (enableLineFlowLimits) {
						for (int i = 0; i < branchLimitCoefficients.size(); i++) {
							LP.addConstraint(branchLimitCoefficients.get(i), branchLimits.get(i), LP.LessThan);
							LP.addConstraint(branchLimitCoefficients.get(i), -branchLimits.get(i), LP.GreaterThan);
						}
					}
				}

				// slack bus gen = load constraint
				LP.addConstraint(slackCoeffs, slackRHS, LP.EqualTo);
				
				// add gen max constraint for slack bus gens
				if (enableGenerationLimits) {
					Enumeration<Integer> slackGenIdxs = slackGenMaxGens.keys();
					while (slackGenIdxs.hasMoreElements()) {
						Integer slackGenIdx = slackGenIdxs.nextElement();
						float[] genMaxConstraint = new float[lpVariableCount];
						genMaxConstraint[slackGenVariableIdxs.get(slackGenIdx)] = 1.0f;
						LP.addConstraint(genMaxConstraint, slackGenMaxGens.get(slackGenIdx), LP.LessThan);
					}
					
//					Enumeration<Integer> slackGenIdxs = slackGenVariableIdxs.keys();
//					while (slackGenIdxs.hasMoreElements()) {
//						Integer slackGenIdx = slackGenIdxs.nextElement();
//						float[] genMaxConstraint = new float[lpVariableCount];
//						genMaxConstraint[slackGenVariableIdxs.get(slackGenIdx)] = 1.0f;
//						LP.addConstraint(genMaxConstraint, slackGenMaxGens.get(slackGenIdx), LP.LessThan);
//					}
				}

				// add theta constraints, gen max constraints for all other buses
				for (int i = 0; i < lpConstraintsTheta.size(); i++) {
					ThetaLPVariablesAndConstraintNumber tlp = lpConstraintsTheta.get(i);
					LP.addConstraint(tlp.constraintCoeffs, tlp.RHS, LP.EqualTo);
					
					if (enableGenerationLimits) {
						Enumeration<Integer> genNums = tlp.genMaxGenConstraintNums.keys();
						while (genNums.hasMoreElements()) {
							float[] genMaxConstraint = new float[lpVariableCount]; 
							Integer genIdx = genNums.nextElement();
							genMaxConstraint[tlp.genVariableNums.get(genIdx)] = 1.0f;
							LP.addConstraint(genMaxConstraint, tlp.genMaxGen.get(genIdx), LP.LessThan);
						}
						
//						
//						Enumeration<Integer> genNums = tlp.genVariableNums.keys();
//						while (genNums.hasMoreElements()) {
//							float[] genMaxConstraint = new float[lpVariableCount]; 
//							Integer genIdx = genNums.nextElement();
//							genMaxConstraint[tlp.genVariableNums.get(genIdx)] = 1.0f;
//							LP.addConstraint(genMaxConstraint, tlp.genMaxGen.get(genIdx), LP.LessThan);
//						}
					}
				}
				
				// add storageAsLoad constraint
				if (enableGenerationLimits) {
					Enumeration<StorageDevice> sdevices = storageAsLoadToVariableIdx.keys(); 
					while (sdevices.hasMoreElements()) {
						StorageDevice sdevice = sdevices.nextElement();
						float[] sdeviceMaxConstraint = new float[lpVariableCount];
						sdeviceMaxConstraint[storageAsLoadToVariableIdx.get(sdevice)] = 1.0f;
						LP.addConstraint(sdeviceMaxConstraint, storageAsLoadToMaxDemand.get(sdevice), LP.LessThan);
					}
				}
				
				LP.preprocess(lpVariableCount, lpConstraintCount);
				//LP.showInfo();
				LP.solveLP();
				//LP.showInfo();
				boolean BFSfound = LP.getRidOfArtificials();
				if (!BFSfound) {
					// set storage to zero
					for (int i = 0; i < storageDevices.size(); i++)
						storageDevices.get(i).setMW(0);
					for (int i = 0; i < nodes.size(); i++)
						nodes.get(i).setVAng(0);
					return GenerationAllocationResult.BLACKOUT;
				}
				LP.solveLP();
				//LP.showInfo();
				//System.out.println("Basic variables and values");
				
				float[] lpVariableVals = new float[lpVariableCount];
				Arrays.fill(lpVariableVals,0.0f);
				for (int i = 0; i < LP.BasicVariables.length; i++) {
					if (LP.BasicVariables[i] < lpVariableVals.length) {
						lpVariableVals[LP.BasicVariables[i]] = LP.x[i];
					}
				}
				
				// assign zero to all angles, generators
				for (int i = 0; i < nodes.size(); i++) {
					nodes.get(i).setVAng(0.0);
					ArrayList<GeneratorData> gens = nodes.get(i).getAttachedGenerators();
					for (int j = 0; j < gens.size(); j++) {
						gens.get(j).setMW(0.0);
					}
				}
				
				// assign LP results to buses
				for (int i = 0; i < lpVariableVals.length; i++) {
					LPVariableInformation lpInfo = lpVariableDescriptors.get(i);
					if (lpInfo != null) {
						NodeData node;
						GeneratorData gen;
						switch (lpInfo.LPVariableType) {
							case LPVariableInformation.thetaPlus:
								node = (NodeData)lpInfo.LPVariableObject;
								node.setVAng(node.getVAng() + lpVariableVals[i]);
								break;
							case LPVariableInformation.thetaMinus:
								node = (NodeData)lpInfo.LPVariableObject;
								node.setVAng(node.getVAng() - lpVariableVals[i]);
								break;
							case LPVariableInformation.generation:
								gen = (GeneratorData)lpInfo.LPVariableObject;
								gen.setMW(lpVariableVals[i]);
								break;
							case LPVariableInformation.storageAsLoad:
								StorageDevice sdevice = (StorageDevice)lpInfo.LPVariableObject;
								sdevice.setMW(-lpVariableVals[i]);
								break;
						}
					}
				}
				
				double threshold = -1e-3;
				violatingBranches = checkBranchFlows(allBranches, threshold);
				violatingGens = checkGenerators(allGenerators,threshold);
				//System.out.println("Branches within " + (threshold*100) + "% of limit: " + violatingBranches.size());
				if (violatingBranches.size() == 0) {
					branchConstraintsSatisfied = true;
//					System.out.println("branch Constraints Satisfied");
				}
				else {
					branchConstraintsSatisfied = false;
					if (iterationCount > 10)
						System.out.println("branch Constraints Violated");
				}

				if (violatingGens.size() == 0) {
					genConstraintsSatisfied = true;
//					System.out.println("gen Constraints Satisfied");
				}
				else {
					genConstraintsSatisfied = false;
					if (iterationCount > 10)
						System.out.println("gen Constraints violated");
				}
				
				if (violatingGens.size() > 0 | violatingBranches.size() > 0) {
					if (iterationCount > 10)
						System.out.println("FORCING RESOLUTION, ITERATION COUNT = " + iterationCount);
					//System.out.println("LP.BT.A.size = " + LP.Bt.size);
					Iterator<BranchData> biter = violatingBranches.iterator();
					while (biter.hasNext()) {
						BranchData b = biter.next();
//						System.out.println("  " + b.getFromNode().getNum() + "<->" + b.getToNode().getNum());
						hasViolatedBeforeBranches.add(b);
					}
					violatingBranches = checkBranchFlows(allBranches, 0.25);
					//System.out.println("Branches within 50% of limit: " + violatingBranches.size());
					biter = violatingBranches.iterator();
					while (biter.hasNext()) {
						BranchData b = biter.next();
						//System.out.println("  " + b.getFromNode().getNum() + "<->" + b.getToNode().getNum());
					}
					violatingBranches.addAll(hasViolatedBeforeBranches);
					

//					System.out.println("Gens within " + (threshold*100) + "% of limit: " + violatingGens.size());
					
					Iterator<GeneratorData> giter = violatingGens.iterator();
					while (giter.hasNext()) {
						GeneratorData g = giter.next();
//						System.out.println("  " + g.getConnectNode().getName());
						hasViolatedBeforeGens.add(g);
					}
					violatingGens = checkGenerators(allGenerators,0.25);
//					System.out.println("Gens within 25% of limit: " + violatingGens.size());
					giter = violatingGens.iterator();
					while (giter.hasNext()) {
						GeneratorData g = giter.next();
//						System.out.println("  " + g.getConnectNode().getName());
					}
					violatingGens.addAll(hasViolatedBeforeGens);
				} else {
//					System.out.println("SOLUTION OBTAINED, ITERATION COUNT = " + iterationCount);
//					System.out.println();
				}
				
				boolean printResults = false;
				if (printResults) {
					System.out.println("");
					for (int i = 0; i < nodes.size(); i++) {
						System.out.println("Node #" + nodes.get(i).getNum());
						NodeData node = nodes.get(i);
						ArrayList<GeneratorData> gens = node.getAttachedGenerators();
						for (int j = 0; j < gens.size(); j++) {
							System.out.println(" Gen " + j + ", MW = " + gens.get(j).getMW());
						}
					}
				}
				iterationCount++;
				//return GenerationAllocationResult.SUCCESS;
			} else {
				return GenerationAllocationResult.SUCCESS;
			}			
		}
		
		if ((!branchConstraintsSatisfied & enableLineFlowLimits) | (!genConstraintsSatisfied & enableGenerationLimits))
			return GenerationAllocationResult.BLACKOUT;
		else
			return GenerationAllocationResult.SUCCESS;
	}
	
	private void printArray(String description, float[] array) {
		String buffer = description + ",";
		for (int i = 0; i < array.length; i++) {
			buffer = buffer + array[i]; 
			if (i < (array.length - 1))
				buffer = buffer + ",";
		}
		System.out.println(buffer);
	}
	
	private int power(int base, int exponent) {
		int result = 1;
		for (int i = 1; i <= exponent; i++) {
			result *= base;
		}
		return result;
	} 
	  
	public void PowerChanged() {
		solutionIsValid = false;
	}
}
