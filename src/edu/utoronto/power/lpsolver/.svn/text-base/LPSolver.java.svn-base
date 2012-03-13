package edu.utoronto.power.lpsolver;

import java.util.*;

import Jama.*;

/**
 * <p>This class is responsible for the actual solution of the
 * linear program, given a set of variables and constraints.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Zeb Tate
 * @version 1.0
 */
public class LPSolver {
  private ArrayList<LPVariable> LPVariables;
  private ArrayList<LPConstraint> LPConstraints;
  
  public enum LPSolverVerbosityLevel {
	  NONE, FULL, ITERATIONCOUNT;
  }

  /**
   * The constructor should be passed in the set
   * of LPVariables and LPConstraints.  Each of
   * these is an ArrayList consisting of
   * LPVariable objects and LPConstraint objects,
   * respectively.
   *
   * @param LPVariables ArrayList containing all LPVariable objects involved in
   *    this particular linear program.
   * @param LPConstraints ArrayList containing all LPConstraint objects
   *    involved in this particular linear program.
   */
  public LPSolver(ArrayList<LPVariable> LPVariables, ArrayList<LPConstraint> LPConstraints) {
    //Initialize LPVariables & LPConstraints with the given inputs
    this.LPVariables = LPVariables;
    this.LPConstraints = LPConstraints;
  }

  /**
   * @see SolveLP(ArrayList Basis, ArrayList Nonbasis, int maxiterations, boolean VERBOSE)
   */
  public Matrix SolveLP(ArrayList<LPVariable> Basis, ArrayList<LPVariable> Nonbasis) {
    //Convenience method.  Assumes default verbosity (false) and
    //max iteration count (100)
    return SolveLP(Basis, Nonbasis, 100, LPSolverVerbosityLevel.NONE);
  }

  /**
   * @see SolveLP(ArrayList Basis, ArrayList Nonbasis, int maxiterations, boolean VERBOSE)
   */
  public Matrix SolveLP(ArrayList<LPVariable> Basis, ArrayList<LPVariable> Nonbasis, int maxiterations) {
    //Convenience method.  Assumes default verbosity (false).
    return SolveLP(Basis, Nonbasis, maxiterations, LPSolverVerbosityLevel.NONE);
  }
  
//  public ArrayList<Object> findInitialBFS() {
//	  ArrayList<Object> results = new ArrayList<Object>();
//	  int numArtificials = LPConstraints.size();
//	  ArrayList<LPVariable> artificials = new ArrayList<LPVariable>();
//	  ArrayList<LPVariable> Nonbasis = new ArrayList<LPVariable>();
//	  ArrayList<LPVariable> Basis = new ArrayList<LPVariable>();
//	  
//	  // clear old cost info from variables
//	  for (int i = 0; i < LPVariables.size(); i++) {
//		  LPVariable lpvar = ((LPVariable)LPVariables.get(i));
//		  lpvar.backupCostCurve();
//		  lpvar.setToZeroCost();
//		  try {
//			  lpvar.SetValue(0.0, false);
//			  Nonbasis.add(lpvar);
//		  } catch (LPException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		  }
//		  
//	  }
//	  
//	  for (int i = 0; i < numArtificials; i++) {
//		  LPConstraint lpc = (LPConstraint)LPConstraints.get(i);
//		  double RHS = lpc.getRHS();
//		  int cType = lpc.getConstraintType();
//		  LPVariable artificial = new LPVariable("artificial " + i,0,Double.POSITIVE_INFINITY);
//		  artificial.AddCostSegment(0, Double.POSITIVE_INFINITY, 1);
//		  artificials.add(artificial);
//		  LPVariables.add(artificial);
//		  Basis.add(artificial);
//		  try {
//			  if (RHS < 0) {
//				lpc.AddElement(-1.0, artificial);
//				artificial.SetValue(-RHS, true);
//			  } 
//			  else {
//				  lpc.AddElement(+1.0, artificial);
//				  artificial.SetValue(+RHS, true);
//			  }
//		  } catch (LPException e) {
//			  // TODO Auto-generated catch block
//			  e.printStackTrace();
//		  }
//	  }
//	  
//	  SolveLP(Basis, Nonbasis, 100, true);
//	  
//	  return results;
//  }

  /**
   * @see SolveLP(ArrayList Basis, ArrayList Nonbasis, int maxiterations, boolean VERBOSE)
   */
  public Matrix SolveLP(ArrayList<LPVariable> Basis, ArrayList<LPVariable> Nonbasis, LPSolverVerbosityLevel verbosity) {
    //Convenience method.  Assumes default max iteration count (100)
    return SolveLP(Basis, Nonbasis, 100, verbosity);
  }

  /**
   * SolveLP is called after construction of the LP object to initiate solution
   * of the LP.  Currently, the Basis and Nonbasis must contain appropriate
   * values, i.e. variables with values that constitute a basic feasible
   * solution.  The other SolveLP methods are simply convenience methods
   * that assume default values for maxiterations (100) and/or VERBOSE (false).
   *
   * @param Basis ArrayList containing the LPVariable objects that are in the
   *   basis at the beginning of the LP solution process.
   * @param Nonbasis ArrayList containing the LPVariable objects that are
   *   <b>not</b> in the basis at the beginning of the LP solution process.
   * @param maxiterations The maximum number of iterations allowed before the
   *   LP is considered to have stalled.
   * @param VERBOSE A boolean value indicating whether verbose debugging
   *   information should be printed as the solution progresses.
   * @return Matrix lambda - the lambda Matrix indicates the sensitivity of the
   *   cost function to a relaxation of the constraints
   */
  public Matrix SolveLP(ArrayList<LPVariable> Basis, ArrayList<LPVariable> Nonbasis, int maxiterations,
		  LPSolverVerbosityLevel verbosity) {

    java.text.DecimalFormat OutputFormatter = new java.text.DecimalFormat(
        "##0.00"); //Used for formatting output in a good way

    int iterationcount = 1;
    Matrix lambda = null;
    //Declare lambda outside of the main loop so it can be returned to
    //the calling object

    boolean done = false;
    while (!done & (iterationcount < maxiterations)) {
      if ((verbosity == LPSolverVerbosityLevel.ITERATIONCOUNT) | (verbosity == LPSolverVerbosityLevel.FULL)) {
        System.out.println("-------------------");
        System.out.println("---Iteration " + iterationcount + "-----");
        System.out.println("-------------------");
      }

      //Build the basis variable cost matrix.  This is done
      //after each pivot, which is not the best
      //way to do it.  Only the basis cost for
      //the variable recently brought into the basis needs changing.
      Iterator<LPVariable> variableiter = Basis.iterator();

      Matrix BasisCost = new Matrix(1, LPConstraints.size());
      int BasisVariableIdx = 0;
      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        System.out.println("\nCurrent Basis Values:");
      }
      while (variableiter.hasNext()) {
        LPVariable Variable = (LPVariable) variableiter.next();
        if ((verbosity == LPSolverVerbosityLevel.FULL)) {
          //Print out the basis value
          System.out.println(Variable.VariableName + "\t=\t" +
                             OutputFormatter.format(Variable.GetValue()));
        }
        BasisCost.set(0, BasisVariableIdx, Variable.GetCost());
        BasisVariableIdx++;
      }

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        //print out the basis cost matrix
        System.out.println("\nCurrent Basis costs:");
        double[] basiscostview = BasisCost.getColumnPackedCopy();
        for (int k = 0; k < basiscostview.length; k++) {
          System.out.println( ( (LPVariable) Basis.get(k)).VariableName +
                             "\t=\t" +
                             OutputFormatter.format(basiscostview[k]));
        }
      }

      //Build the nonbasis variable cost matrices.  This is done
      //after each pivot, which is not the best way to do it.
      //Only the basis variable which has been brought out of
      //the basis needs to be updated in the nonbasis cost vector.
      Matrix NonbasisCostUp = new Matrix(1,
                                         LPVariables.size() -
                                         LPConstraints.size());

      Matrix NonbasisCostDown = new Matrix(1,
                                           LPVariables.size() -
                                           LPConstraints.size());
      int NonbasisVariableIdx = 0;

      //CanGoUp and CanGoDown keep track of whether or not a nonbasis variable
      //can go up or down.  This is created while building the cost vector
      //for the nonbasis variables, and is eventually passed in to the
      //new basis variable selection method to select the most cost-effective
      //variable to bring into the basis.
      boolean CanGoUp[] = new boolean[LPVariables.size() - LPConstraints.size()];
      boolean CanGoDown[] = new boolean[LPVariables.size() - LPConstraints.size()];

      variableiter = Nonbasis.iterator();
      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        System.out.println("\nCurrent Nonbasis Values:");
      }
      while (variableiter.hasNext()) {
        LPVariable Variable = (LPVariable) variableiter.next();
        if ((verbosity == LPSolverVerbosityLevel.FULL)) {
          //Print out the nonbasis value
          System.out.println(Variable.VariableName + "\t=\t" +
                             OutputFormatter.format(Variable.GetValue()));
        }
        if (Variable.CanGoUpOrDown() == true) {
          //This variable can go either way, so fill in values
          //in NonbasisCostUp and NonbasisCostDown
          CanGoUp[NonbasisVariableIdx] = true;
          CanGoDown[NonbasisVariableIdx] = true;
          NonbasisCostUp.set(0, NonbasisVariableIdx,
                             Variable.GetCostUpAndDown()[0]);
          NonbasisCostDown.set(0, NonbasisVariableIdx,
                               Variable.GetCostUpAndDown()[1]);
          NonbasisVariableIdx++;
        }
        else if (Variable.AtLowerBound() == true) {
          //This variable can only go one way, and it's at its lower
          //bound, so put the cost into the NonbasisCostUp vector
          CanGoUp[NonbasisVariableIdx] = true;
          CanGoDown[NonbasisVariableIdx] = false;
          NonbasisCostUp.set(0, NonbasisVariableIdx, Variable.GetCost());
          NonbasisCostDown.set(0, NonbasisVariableIdx, 0);
          NonbasisVariableIdx++;
        }
        else {
          //This variable can only go one way, and it's at its upper
          //bound, so put the cost into the NonbasicCostDown vector
          CanGoUp[NonbasisVariableIdx] = false;
          CanGoDown[NonbasisVariableIdx] = true;
          NonbasisCostUp.set(0, NonbasisVariableIdx, 0);
          NonbasisCostDown.set(0, NonbasisVariableIdx, Variable.GetCost());
          NonbasisVariableIdx++;
        }
      }

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        //print out the nonbasiscost matrix for those going up
        double[] nonbasiscostview = NonbasisCostUp.getColumnPackedCopy();
        boolean titledisplayed = false;
        for (int k = 0; k < nonbasiscostview.length; k++) {
          if (CanGoUp[k] == true) {
            if (titledisplayed == false) {
              System.out.println(
                  "\nCurrent Nonbasis costs for increasing variables:");
              titledisplayed = true;
            }
            System.out.println( ( (LPVariable) Nonbasis.get(k)).VariableName +
                               "\t=\t" +
                               OutputFormatter.format(nonbasiscostview[k]));
          }
        }
      }

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        //print out the nonbasiscost matrix for those going down
        double[] nonbasiscostview = NonbasisCostDown.getColumnPackedCopy();
        boolean titledisplayed = false;
        for (int k = 0; k < nonbasiscostview.length; k++) {
          if (CanGoDown[k] == true) {
            if (titledisplayed == false) {
              System.out.println(
                  "\nCurrent Nonbasis costs for decreasing variables:");
              titledisplayed = true;
            }
            System.out.println( ( (LPVariable) Nonbasis.get(k)).VariableName +
                               "\t" +
                               OutputFormatter.format(nonbasiscostview[k]));
          }
        }
      }

      //Build Ab and Anbasis.  Once again, there's alot of unnecessary
      //work being done here.  Ab and Anbasis really only need to have
      //the columns relevent to the previous pivot.
      Matrix Abasis = new Matrix(LPConstraints.size(), Basis.size());
      Matrix Anbasis = new Matrix(LPConstraints.size(), Nonbasis.size());
      Iterator<LPConstraint> constraintiter = LPConstraints.iterator();
      int ConstraintIdx = 0;
      while (constraintiter.hasNext()) {
        //Outer loop iterates through each constraint
        BasisVariableIdx = 0;
        NonbasisVariableIdx = 0;
        Iterator<LPVariable> variter = Basis.iterator();
        LPConstraint constraint = (LPConstraint) constraintiter.next();
        while (variter.hasNext()) {
          //This inner loop iterates through each variable in the
          //basis to put it into Ab.  It asks each constraint if
          //the given variable is represented in the constraint.  If
          //so, the variable's coefficient is put into the correct row
          //of Ab.
          double coeff = constraint.LookUpCoefficient( (LPVariable) variter.
              next());
          Abasis.set(ConstraintIdx, BasisVariableIdx, coeff);
          BasisVariableIdx++;
        }
        variter = Nonbasis.iterator();
        while (variter.hasNext()) {
          //This inner loop iterates through each variable not in the
          //basis to put it into An.  Works identically to the basis variable
          //while loop immediately above.
          double coeff = constraint.LookUpCoefficient( (LPVariable) variter.
              next());
          Anbasis.set(ConstraintIdx, NonbasisVariableIdx, coeff);
          NonbasisVariableIdx++;
        }
        //Go to the next row of Abasis/Anbasis
        ConstraintIdx++;
      }

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        //Print out the Abasis matrix
        double[][] AbasisArray = Abasis.getArray();
        System.out.println("\nAbasis");
        for (int k = 0; k < AbasisArray.length; k++) {
          String RowDisplay = "";
          for (int j = 0; j < AbasisArray[k].length; j++) {
            RowDisplay += OutputFormatter.format(AbasisArray[k][j]);
            if (j < (AbasisArray[k].length - 1)) {
              RowDisplay += "\t";
            }
          }
          System.out.println( ( (LPConstraint) LPConstraints.get(k)).
                             ConstraintName + "\t:\t" + RowDisplay);
        }
      }

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        //Print out the Anbasis matrix
        double[][] AnonbasisArray = Anbasis.getArray();
        System.out.println("\nAnonbasis");
        for (int k = 0; k < AnonbasisArray.length; k++) {
          String RowDisplay = "";
          for (int j = 0; j < AnonbasisArray[k].length; j++) {
            RowDisplay += OutputFormatter.format(AnonbasisArray[k][j]);
            if (j < (AnonbasisArray[k].length - 1)) {
              RowDisplay += "\t";
            }
          }
          System.out.println( ( (LPConstraint) LPConstraints.get(k)).
                             ConstraintName + "\t:\t" + RowDisplay);
        }
      }

      //Determine lambda
      lambda = Abasis.solveTranspose(BasisCost);

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        //Print out all the lambda values.
        System.out.println("\nLambda Values:");
        double lambdaview[];
        lambdaview = lambda.getColumnPackedCopy();
        for (int k = 0; k < lambdaview.length; k++) {
          LPConstraint constraintview = (LPConstraint) LPConstraints.get(k);
          System.out.println("lambda[" + constraintview.ConstraintName + "] = " +
                             OutputFormatter.format(lambdaview[k]));
        }
      }

      //costsensup and costsensdown represent the reduced cost coefficients
      //for those variables going up and down, respectively.  For those
      //variables on a border point between two piecewise cost segments,
      //there will be entries in both costsensup and costsensdown.
      Matrix costsensup = NonbasisCostUp.minus(lambda.transpose().times(Anbasis));
      Matrix costsensdown = NonbasisCostDown.minus(lambda.transpose().times(
          Anbasis));

      if ((verbosity == LPSolverVerbosityLevel.FULL)) {
        boolean titledisplayed = false;
        //Print out the reduced cost sensitivities for those
        //variables which can be increased when brought into
        //the basis.
        double costsensviewup[] = costsensup.getColumnPackedCopy();
        for (int indx = 0; indx < costsensviewup.length; indx++) {
          if (CanGoUp[indx] == true) {
            if (titledisplayed == false) {
              System.out.println("\nRelative cost sensitivities for " +
                                 "increasing variables:");
              titledisplayed = true;
            }
            LPVariable vardisp = (LPVariable) Nonbasis.get(indx);
            System.out.println("CostSensUp[" + vardisp.VariableName + "] = " +
                               OutputFormatter.format(costsensviewup[indx]));
          }
        }

        //Print out the reduced cost sensitivities for those
        //variables which can be increased when brought into
        //the basis.
        titledisplayed = false;
        double costsensviewdown[] = costsensdown.getColumnPackedCopy();
        for (int indx = 0; indx < costsensviewdown.length; indx++) {
          if (CanGoDown[indx] == true) {
            if (titledisplayed == false) {
              System.out.println("\nRelative cost sensitivities for " +
                                 "decreasing variables:");
              titledisplayed = true;
            }
            LPVariable vardisp = (LPVariable) Nonbasis.get(indx);
            System.out.println("CostSensDown[" + vardisp.VariableName + "] = " +
                               OutputFormatter.format(costsensviewdown[indx]));
          }
        }
      }

      //Indexanddelta contains at position [0] the index into
      //the Nonbasis variables indicating the one to be brought
      //into the basis.  At position [1] is stored the delta value, either
      //+1 or -1 corresponding to an increase or decrease, respectively.
      int indexanddelta[] = findBestNewBasisVariable(costsensup.
          getColumnPackedCopy(), costsensdown.getColumnPackedCopy(),
          CanGoUp, CanGoDown);
      int delta = indexanddelta[1];

      if (indexanddelta[0] == -1) {
        //if index was set to -1 within the findBestNewBasisVariable function,
        //it means no advantage will be obtained by bringing any nonbasis
        //variable into the basis.  So, exit if this happens.
        done = true;
        if ((verbosity == LPSolverVerbosityLevel.FULL)) {
          System.out.println("\n------------------------------------------");
          System.out.println("No decrease in cost with further pivoting.");
          System.out.println("Exiting iteration loop.");
          System.out.println("------------------------------------------");
        }
      }
      else {
        //A suitable nonbasis candidate variable was found to attempt
        //to bring into the basis.  Pick it out of the Nonbasis ArrayList
        //and assign it to NewBasisVariable.
        LPVariable NewBasisVariable = (LPVariable) Nonbasis.get(
            indexanddelta[0]);

        if ((verbosity == LPSolverVerbosityLevel.FULL)) {
          System.out.println("\nCandidate chosen for entering the basis: " +
                             NewBasisVariable.VariableName);
        }

        //Grab the column of A that corresponds to this nonbasis variable
        //that will go into the basis.
        Matrix AColForNewBasisVariable = Anbasis.getMatrix(0,
            LPConstraints.size() - 1, indexanddelta[0], indexanddelta[0]);

        //Solve for d, which indicates the sensitivity of the old basis
        //variables to the introduction of the new basis variable.
        //A positive d value indicates that if the new basis variable
        //will be increased, the old basis variable will be decreased.
        double d[] = Abasis.solve(AColForNewBasisVariable).getColumnPackedCopy();

        //tpossible stores the possible magnitude of change in the
        //nonbasis variable as it's brought into the basis.
        double tpossible[] = new double[Basis.size() + 1];

        for (int k = 0; k < (tpossible.length - 1); k++) {
          //tpossible[0]-tpossible[tpossible.length-1] are filled
          //by checking the effect of pushing the basis values towards
          //their endpoints.

          LPVariable checkbasisvar = (LPVariable) Basis.get(k);

          // Xb[k] will not change after the new variable is brought in, but don't let it be the constraining factor
          if (d[k] == 0) {
        	  tpossible[k] = Double.POSITIVE_INFINITY; 
          }
          if (delta == -1) {
            //delta = -1.  Nonbasis variable would like to be decreased
            if (d[k] > 0) {
              //Xb will be increased
              tpossible[k] = (checkbasisvar.GetBounds()[1] -
                              checkbasisvar.GetValue()) / d[k];
            }
            else if (d[k] < 0) {
              //Xb will be decreased
              tpossible[k] = (checkbasisvar.GetValue() -
                              checkbasisvar.GetBounds()[0]) / -d[k];
            } 
          }
          else {
            //delta = 1.  Nonbasis variable would like to be increased
            if (d[k] > 0) {
              //Xb will be decreased
              tpossible[k] = (checkbasisvar.GetValue() -
                              checkbasisvar.GetBounds()[0]) / d[k];
            }
            else if (d[k] < 0) {
              //Xb will be increased
              tpossible[k] = (checkbasisvar.GetBounds()[1] -
                              checkbasisvar.GetValue()) / -d[k];
            } 
          }
        }

        //The last entry in tpossible corresponds to moving the nonbasis
        //variable completely from one bound to the other (either up or
        //down).  In the case of this being the selected value of t, the
        //candidate nonbasis variable never actually enters the basis--it
        //just moves from one bound to the other.
    	if (NewBasisVariable.CanGoUpOrDown()) {
    		if (delta == -1) {
    			// Trying to go down
    			tpossible[tpossible.length-1] = NewBasisVariable.GetBoundsUpAndDown()[1][1] - NewBasisVariable.GetBoundsUpAndDown()[1][0]; 
    		} else {
    			// Trying to go up
    			tpossible[tpossible.length-1] = NewBasisVariable.GetBoundsUpAndDown()[0][1] - NewBasisVariable.GetBoundsUpAndDown()[0][0];
    		}
    	} else {
			if (delta == -1) {
				if (!(NewBasisVariable.AtLowerBound())) {
					// can move down
					tpossible[tpossible.length-1] = NewBasisVariable.GetBounds()[1]-NewBasisVariable.GetBounds()[0];
				} else
					// can't move down (at lower bound)
					tpossible[tpossible.length-1] = 0;
			} else {
				if (NewBasisVariable.AtLowerBound()) {
					// move up
					tpossible[tpossible.length-1] = NewBasisVariable.GetBounds()[1]-NewBasisVariable.GetBounds()[0];
				} else {
					// can't move up (at upper bound)
					tpossible[tpossible.length-1] = 0;
				}
			}
    	}
    	
    	// print out t possible values
    	if ((verbosity == LPSolverVerbosityLevel.FULL)) {
    		System.out.println("tPossible vector:");
    		for (int k = 0; k < Basis.size(); k++) {
	            System.out.println( ( (LPVariable) Basis.get(k)).
	                    VariableName + "\t:\t" + tpossible[k]);
    		}
    		System.out.println(NewBasisVariable.VariableName + "\t:\t" + tpossible[tpossible.length - 1]);
    		System.out.println();
    	}

        //Get the best increment/decrement for the new basis variable.
        //tandindex[0] contains the amount the new nonbasis variable
        //will be moved.  tandindex[1] contains either an index
        //into the Basis which indicates the variable that's leaving
        //the basis, or it is equal to (tpossible.length - 1), which indicates
        //that the candidate nonbasis variable will just move from one
        //bound to the other without entering the basis.
        double tandindex[] = findMin(tpossible);
        if ((verbosity == LPSolverVerbosityLevel.FULL)) {
          System.out.println("\nT selected: " +
                             OutputFormatter.format(tandindex[0]));
          System.out.println("delta = " + indexanddelta[1]);
        }

        //Update basis variable values
        try {
          if ((verbosity == LPSolverVerbosityLevel.FULL)) {
            System.out.println("\nBasis variable value changes:");
          }
          for (int k = 0; k < Basis.size(); k++) {
            if (tandindex[1] != k) {
              //For the basis variable which is leaving, don't update yet.
              //We need to determine whether or not the basis variable
              //should be sent to the nonbasis or kept in the basis.  This
              //is crucial due to differences in the SetValue method based
              //on whether or not the variable being set is in the basis
              //or nonbasis set.  Note that if tandindex[1] is greater
              //than the number of terms in the basis, it means
              //the nonbasis variable will never enter the basis and
              //every basis element will stay in the basis.
              LPVariable working = (LPVariable) Basis.get(k);
              if ((verbosity == LPSolverVerbosityLevel.FULL)) {
                //Print out exactly what's happenning to each basis variable.
                System.out.println("Variable " + working.VariableName + " = " +
                                   OutputFormatter.format(working.GetValue()) +
                                   " - " + delta + "*" +
                                   OutputFormatter.format(d[k]) + "*" +
                                   OutputFormatter.format(tandindex[0]) + " = " +
                                   OutputFormatter.format(working.GetValue() -
                    delta * d[k] * tandindex[0]));
              }
              //Set the new value of each basis element.
              working.SetValue(working.GetValue() - delta * d[k] * tandindex[0], true);
            }
          }
        }
        catch (LPException e) {
          System.out.println(
              "Error occurred while attempting to update basis values.");
          System.out.println(e.getMessage());
        }

        if (tandindex[1] == (tpossible.length - 1)) {
          //NewBasisVariable stays out of the basis; it just changes from its
          //upper limit to its lower limit (or vice versa) and the basis variables
          //are updated
          if ((verbosity == LPSolverVerbosityLevel.FULL)) {
            //Notification that the nonbasis variable doesn't actually enter
            //the basis.
            System.out.println("\nVariable " + NewBasisVariable.GetValue() +
                               " is set to enter " +
                               "the basis but instead moves straight from one bound to another.");
          }
          try {
            //Update the nonbasis variable that was going to enter the basis.
            NewBasisVariable.SetValue(NewBasisVariable.GetValue() +
                                      delta * tandindex[0], false);

          }
          catch (LPException e) {
            System.out.println(
                "Error occurred while attempting to update basis values.");
            System.out.println(e.getMessage());
          }
        }
        else {
          try {
            //Update the value of the basis variable that is leaving
            int basisidx = (int) tandindex[1];
            LPVariable working = (LPVariable) Basis.get(basisidx);
            if ((verbosity == LPSolverVerbosityLevel.FULL)) {
              System.out.println("Variable " + working.VariableName + " = " +
                                 OutputFormatter.format(working.GetValue()) +
                                 " - " + delta + "*" +
                                 OutputFormatter.format(d[basisidx]) + "*" +
                                 OutputFormatter.format(tandindex[0]) +
                                 " = " +
                                 (working.GetValue() -
                                  delta * d[basisidx] * tandindex[0]));
            }
            //Note here that SetValue has false for the BasisVariable argument,
            //which instructs the variable to use the nonbasis form of the
            //SetValue method.
            working.SetValue(working.GetValue() -
                             delta * d[basisidx] * tandindex[0], false);

            //Grab the variable that's leaving the basis out of the Basis
            //ArrayList based on the tandindex[1] value.
            LPVariable NewNonbasisVariable = (LPVariable) Basis.get(basisidx);

            if ((verbosity == LPSolverVerbosityLevel.FULL)) {
              System.out.println("\nExiting basis: " +
                                 NewNonbasisVariable.VariableName);
            }

            //Exchange the variables in the Basis and Nonbasis ArrayList.
            Basis.set( (int) tandindex[1], NewBasisVariable);
            Nonbasis.set( (int) indexanddelta[0], NewNonbasisVariable);

            //Update the value for the new basis variable
            NewBasisVariable.SetValue(NewBasisVariable.GetValue() +
                                      delta * tandindex[0], true);
          }

          catch (LPException e) {
            System.out.println(
                "Error occurred while attempting to update values for " +
                "new basis variable and old basis variable");
            System.out.println(e.getMessage());
          }

        }
      }
      iterationcount++;
    }

//    if (VERBOSE == true) {
//      //Print out all the values when we're done.
//      Iterator variter = LPVariables.iterator();
//      double totalcost = 0;
//      while (variter.hasNext()) {
//        LPVariable displayvar = (LPVariable) variter.next();
//        System.out.println(displayvar.VariableName + " = " +
//                           OutputFormatter.format(displayvar.GetValue()));
//        totalcost += displayvar.GetCost() * displayvar.GetValue();
//      }
//      System.out.println("Total objective evaluation: " + OutputFormatter.format(totalcost));
//    }

    //Return lambda, the sensitivity of the objective to the right hand side
    //of the constraint equations.
    if (iterationcount == maxiterations)
    	return null;
    else
    	return lambda;
  }

  private double[] findMin(double[] evalarray) {
    //This function is quite simple..it iterates through a single-dimensional
    //double array and finds the smallest value.  Perhaps some native
    //Java method can be used, but I'm unable to find it.

    double minvalue = evalarray[0];
    double minindex = 0;
    for (int i = 0; i < evalarray.length; i++) {
      minvalue = java.lang.Math.min(minvalue, evalarray[i]);
      if (minvalue == evalarray[i]) {
        minindex = i;
      }
    }
    double returnval[] = {
        minvalue, minindex};
    return returnval;
  }

  private int[] findBestNewBasisVariable(double[] costsensup,
                                         double[] costsensdown,
                                         boolean[] CanGoUp,
                                         boolean[] CanGoDown) {
    //this function iterates through all the nonbasis variables
    //and finds the one that should be put into the basis.  Maximum
    //sensitivity of the cost function (in the negative direction)
    //to a change in value is the criterion used.
    int index = 0;
    int delta = 1;

    double minvalueup = 0;
    int indexup = -1;
    for (int idx = 0; idx < costsensup.length; idx++) {
      //For variables going up, we want the one
      //with the greatest negative cost sensitivity, so
      //check each one against the old minimum (stored in minvalueup)
      //and if it has a greater negative value, update minvalueup
      //and indexup.
      if ( (costsensup[idx] < minvalueup) & (CanGoUp[idx] == true)) {
        minvalueup = costsensup[idx];
        indexup = idx;
      }
    }

    double minvaluedown = 0;
    int indexdown = -1;
    for (int idx = 0; idx < costsensdown.length; idx++) {
      //For variables going down, we want the one
      //with the greatest positive cost sensitivity, so
      //check each one against the old minimum (stored in minvaluedown)
      //and if it has a greater positive value, update minvaluedown
      //and indexup.  Also, negate the value so it can be checked
      //for nonnegativity later in determining whether or not to stop
      //the LP.
      if ( ( -costsensdown[idx] < minvaluedown) & (CanGoDown[idx] == true)) {
        minvaluedown = -costsensdown[idx];
        indexdown = idx;
      }
    }

    if ( (minvalueup >= 0) & (minvaluedown >= 0)) {
      //Introducing any nonbasis variables into the basis would give
      //no decrease in the objective.  Send back an index of -1, which
      //signals that the LP should stop.
      index = -1;
    }
    else if (minvalueup < minvaluedown) {
      //It's best to push one of the nonbasis variables up
      index = indexup;
      delta = 1;
    }
    else if (minvaluedown < minvalueup) {
      //It's best to push one of the nonbasis variables down
      index = indexdown;
      delta = -1;
    }
    else {
      //Pushing the nonbasis up or down is equally good.  I'm not
      //sure if this is even possible.  Arbitrarily choose to push
      //the variable up in this case.
      index = indexup;
      delta = 1;
    }
    int returnval[] = {
        index, delta};
    return returnval;
  }
}
