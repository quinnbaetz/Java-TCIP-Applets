package anl.lpsolver;
/*
 *
 *  Revised by Yu WEI
 *  Summer 2004
 *  email: weiy3@mcmaster.ca
 *
 *  Written by Timothy J. Wisniewski
 *  Summer 1996
 *  email:  tjw@euler.bd.psu.edu
 *
 *  Updated by Joe Czyzyk
 *  September - October 1996
 *
 *  Copyright 2004 Optimization Technology Center
 *
 *	Jan 2009: Modification by Zeb Tate to return true/false if an initial BFS
 *	is obtained/not obtained 
 */

import java.awt.*;
import java.lang.Math;

public class revisedSimplex
{
  public int numVariables;
  public int numConstraints;
  public int numNonbasic;
  public int numConstraintsSoFar = 0;
  public int CurrentStep = 0;	  
  public int NumIterations = 0;
  public int NumArtificials;
  
  private int i,j,k,l;         /* Index Variables */
  
  public float [] reducedCost;
  public float [] cost;
  public float [] x;
  public float [] pi;
  public float [] yB;

  public float MinRatio;
  public int   NumMinRatio;
  
  public Matrix Bt;
  public Matrix B;
  
  /* littleCost is the cost of the BasicVariables */
  public float [] littleCost;
  public float objectiveValue = 0;
  
  public float [][] A;
  public float [] b;
  public int [] constraintType;
  public int [] BasicVariables;
  public int [] NonBasicVariables;
  public int [] varType;
  public float [] colOfA;

  int[] perm;  /* used only in AugmentBasis */
  
  public int EnteringVariable;
  public int LeavingVariable;
  
  public boolean TypeMinimize;
  boolean ArtificialAdded = false;

  public final static  int LessThan    = 0;
  public final static  int GreaterThan = 1;
  public final static  int EqualTo     = 2;

  public final static  int Continue    = 0;
  public final static  int Optimal     = 1;
  public final static  int Feasible    = 2;
  public final static  int Unbounded   = 3;

  public final static  int Regular        = 0;
  public final static  int SlackOrSurplus = 1;
  public final static  int Artificial     = 2;

  public final static  int BasicType      = 1;
  public final static  int NonBasicType   = 2;


  /* variables for two phase part  */
  boolean oldOptType;
  float OriginalCost[];
  
  public revisedSimplex(int nv, int nc)
    {
      numVariables    = nv;
      numConstraints  = nc;
      NumArtificials       = 0;
      reducedCost          = new float [nv + 2*nc];
      cost                 = new float [nv + 2*nc];
      OriginalCost         = new float [nv + 2*nc];
      x                    = new float [nc];
      pi                   = new float [nc];
      yB                   = new float [nc];
      littleCost           = new float [nc]; 
      A        = new float [nc][nv + 3*nc];
      b                    = new float [nc];
      constraintType       = new int [nc];                    
      BasicVariables       = new int [nc];                    
      NonBasicVariables    = new int [nv + 2*nc];
      varType              = new int [nv + 2*nc];          
      colOfA               = new float[nc];
      Bt                   = new Matrix(nc);
      B                    = new Matrix(nc);
    } /* end revisedSimplex procedure */
  
  public int iterateOneStep()
    {
      switch (CurrentStep) {
      case 0:  /* upate B */
	NumIterations++;
	this.makeBt();
	this.makeB();
	CurrentStep = 1;
	return Continue;

      case 1:  /* update pi*/  
	Bt.solve(pi,littleCost);
	CurrentStep = 2;
	return Continue;
	
      case 2:  /* calculate reduced costs */
	this.calculateReducedCosts();
	CurrentStep = 3;
	return Continue;

      case 3:  /* test for optimality */
	if(this.testForOptimality()) {
	  CurrentStep = 12;
	  return Optimal;
	}
	else {
	  CurrentStep = 4;
	  this.ChooseEnteringVariable();
	  return Continue;
	}

      case 4: /* Get user's input for entering variable */
	CurrentStep = 5;
	return Continue;
	
      case 5:  /* update yB */
	/* make the column of A what we want */
	for (int i = 0; i < numConstraints; i++)
	  colOfA[i] = A[i][NonBasicVariables[EnteringVariable]];
	
	B.solve(yB,colOfA);
	CurrentStep = 6;
	return Continue;
	
      case 6:  /* test for unboundedness */
	if (this.testUnboundedness()) {
	  CurrentStep = 7;
	  return Unbounded; /* The problem is unbounded. */
	}
	else 
	  CurrentStep = 8;
	return Continue;
	
      case 7:  /* This statement is never reached */

      case 8:  /* choose leaving variable */
	chooseLeavingVariable();
	if (NumMinRatio == 1)
	  CurrentStep = 11;
	else /* tie for minimum ratio */
	  CurrentStep = 9;
	return Continue;

      case 9:  /* get user's selection */
	CurrentStep = 10;
	return Continue;

      case 10: /* do nothing -- print leaving variable in tool */
	CurrentStep = 11;
	return Continue;
	
      case 11:  /* Update solution */
	this.updateSolution();
	objectiveValue = this.calculateObjective();
	CurrentStep = 0;  /*  start over  */
	return Continue;  /* We can keep going/! */
      
      case 12:  /* found optimal solution */
	objectiveValue = this.calculateObjective();
	return Optimal;
      }      
      return 4;       /*  this will never happen  */

    } /* end iterateOneStep procedure */
  
  public int iterate()
    {  /* Perform all the steps of one iteration */

      NumIterations++;
      this.makeBt();
      Bt.solve(pi,littleCost);
      this.calculateReducedCosts();
      
      if (!this.testForOptimality()) {
	this.ChooseEnteringVariable();
      }
      else {
	objectiveValue = this.calculateObjective();
	return Optimal;    /* We found the optimal solution!! */
      }
      this.makeB();
      
      /* make the column of A what we want */
      for (int i = 0; i < numConstraints; i++)
	colOfA[i] = A[i][NonBasicVariables[EnteringVariable]];
      
      B.solve(yB,colOfA);    
      
      if (!this.testUnboundedness()) {
	this.chooseLeavingVariable();
	this.updateSolution();
	
	return Continue;  /* We can keep going/! */
      }
      else
	return Unbounded;  /* The problem is unbounded. */
      
    }  /* end iterate procedure */
  
  public float calculateObjective()
    {
      float value = 0;
      
      if (TypeMinimize == true)
	for (int i = 0; i < numConstraints; i++)
	  value += (x[i] * cost[BasicVariables[i]]);
      else
	for (int i = 0; i < numConstraints; i++)
	  value -= (x[i] * cost[BasicVariables[i]]);
      
      return value;
    } /* end calculateObjective procedure */
  
  public void chooseLeavingVariable()
    {
      float Ratio;
      int   minIndex = -1;
      
      NumMinRatio = 0;
      /* MinRatio    = 1000000; */

      for (i = 0; i < numConstraints; i++) {
	if (yB[i] > 0) {
	  Ratio = x[i]/yB[i];
	  if (NumMinRatio == 0) {
	      MinRatio = Ratio;
	      minIndex = i;
	      NumMinRatio = 1;
	  } else if (Ratio < MinRatio) {
	      MinRatio = Ratio;
	      minIndex = i;
	      NumMinRatio = 1;
	  } else if (Ratio == MinRatio)
	      NumMinRatio++;
	}
      }
      
      LeavingVariable = minIndex;

    }  /* end chooseLeavingVariable procedure */
  
    /* SJW 9/14/99: merge the former "updateSolution" with the former
       "changeBasis"; call the merged entity "updateSolution" */

    public void updateSolution()
    {
      int tmp;

      for (i = 0; i < numConstraints; i++)
	x[i] -= (MinRatio*yB[i]);
      x[LeavingVariable] = MinRatio;

      if (varType[BasicVariables[LeavingVariable]] == Artificial)
	  NumArtificials--;
      if(varType[NonBasicVariables[EnteringVariable]] == Artificial) 
	  NumArtificials++;

      tmp = BasicVariables[LeavingVariable];
      BasicVariables[LeavingVariable] = NonBasicVariables[EnteringVariable];
      NonBasicVariables[EnteringVariable] = tmp;
    
    }  /* end updateSolution procedure */
  
    public void ChooseEnteringVariable()
    {
      int minIndex = 0;
      float minValue = 100000;

      /*  Actually any variable with a negative reduced cost will work.
       *  We choose the variable with lowest reduced cost to enter.
       *  (The user can choose a different entering variable.)
       */
      
      for (i = 0; i < numNonbasic;i++)
	if (reducedCost[i] < 0 && reducedCost[i] < minValue) {
	  minIndex = i;
	  minValue = reducedCost[i];
	}
      
      EnteringVariable = minIndex;
    } /* end pickEnteringVariable procedure */
  
  public boolean testUnboundedness()
    {
      boolean isUnbounded = true;
      
      /* Problem is unbounded if yB > 0 in all elements */

      for (i = 0; i < numConstraints; i++)
	if (yB[i] > 0) {
	  isUnbounded = false;
	  break;
	}
      return isUnbounded;
    } /* end testUnboundedness procedure */
  
  public void calculateReducedCosts()
    {
      for (i = 0; i < numNonbasic; i++)	{
	for (j = 0; j < numConstraints; j++)
	  colOfA[j] = A[j][NonBasicVariables[i]];
	
	reducedCost[i] = cost[NonBasicVariables[i]] - 
	                 this.Dot(pi,colOfA,numConstraints);
      }
    } /* end calculateReducedCosts procedure */
  
  public boolean testForOptimality()
    {
      boolean isOptimal = true;
      
      for (int i = 0; i < numNonbasic; i++)
	if (reducedCost[i] < 0) {
	  isOptimal = false;
	  return isOptimal;  /* false */
	}
      
      return isOptimal;      /* true */
    } /* end testForOptimality procedure */
  
  private void makeBt()
    {
      for (i = 0; i < numConstraints; i++) {
	littleCost[i] = cost[BasicVariables[i]];
	
	for (j = 0; j < numConstraints; j++)
	  Bt.A[i][j] = A[j][BasicVariables[i]];
      }
    } /* end makeBt procedure */
  
  private void makeB()
    {
      for (i = 0; i < numConstraints; i++)
	for (j = 0; j < numConstraints; j++)
	  B.A[i][j] = A[i][BasicVariables[j]];
    } /* end makeB procedure */
  
  public void addConstraint(float [] coefficients, float rhs, int type)
    {
      for (i = 0; i < numVariables; i++) {
	A[numConstraintsSoFar][i] = coefficients[i];
      }
      x[numConstraintsSoFar] = rhs;
      b[numConstraintsSoFar] = rhs;
      
      constraintType[numConstraintsSoFar] = type;
      numConstraintsSoFar++;
    } /* end addConstraint procedure */
  
  public void specifyObjective(float [] coefficients, boolean type)
    {
      for (i = 0; i < numVariables; i++)
	cost[i] = coefficients[i];
      
      TypeMinimize = type;
    } /* end specifyObjective procedure */
  
  public boolean preprocess(int numberOfVariables, int numberOfConstraints)
    { 
      int   LastCol, NextNonBasic;
      int[] ConstraintVariable = new int[numberOfConstraints];

      int   slack;
      int   surplus;
      
      oldOptType = TypeMinimize;
      LastCol    = numberOfVariables;

      if (TypeMinimize == false)      /* switch sign for maximize */
	for (i = 0; i < LastCol; i++)
	  cost[i] *= -1;

      for (i = 0; i < LastCol; i++)
	NonBasicVariables[i] = i;

      NextNonBasic = LastCol;
      
      /*  Add slacks and surplus first, this is slower but it makes 
       *  displaying a lot easier for two phase problems. 
       *  Keep track of which variable was added for each constraint.
       */
      
      for (i = 0; i < numberOfConstraints; i++)
	switch (constraintType[i]) {
	case LessThan:
	  A[i][LastCol] = 1; 
	  cost[LastCol] = 0;
	  varType[LastCol] = SlackOrSurplus;
	  ConstraintVariable[i] = LastCol;
	  LastCol++;
	  break;

	case GreaterThan:
	  A[i][LastCol] = -1;
	  cost[LastCol] =  0;
	  varType[LastCol] = SlackOrSurplus;
	  ConstraintVariable[i] = LastCol;
	  LastCol++;
	  break;

	case EqualTo:  /* do nothing */
	} /* end switch */
      
      /* Add artificial variables if necessary, Assign basis */

      for (i = 0; i < numberOfConstraints; i++)
	switch(constraintType[i]) {
	case GreaterThan:
	  if (b[i] > 0) {
	    /* Add artificial variable, make basic */
	    A[i][LastCol]     = 1;  
	    x[i]              = b[i];
	    varType[LastCol]  = Artificial;
	    ArtificialAdded   = true;
	    BasicVariables[i] = LastCol;
	    /* surplus not basic */
	    surplus           = ConstraintVariable[i];
	    NonBasicVariables[NextNonBasic] = surplus;
	    /* increment counter */
	    NextNonBasic++;
	    LastCol++;
	    NumArtificials++;
	  } else {
	    /* make surplus variable basic */
	    BasicVariables[i] = ConstraintVariable[i];
	    x[i]              = -b[i];
	  }
	  break;

	case EqualTo:  /* add artificial variable, make basic */
	  if (b[i] >= 0) {
	    A[i][LastCol] = 1;  
	    x[i]          = b[i];
	  } else {  /* b[i] < 0 */
	    A[i][LastCol] = -1;
	    x[i]          = -b[i];
	  }

	  varType[LastCol]  = Artificial;
	  ArtificialAdded   = true;
	  BasicVariables[i] = LastCol;
	  LastCol++;
	  NumArtificials++;
	  break;

	case LessThan:  
	  if (b[i] >= 0) {
	    /* make slack variable basic */
	    BasicVariables[i] = ConstraintVariable[i];
	    x[i] = b[i];
	  } else { /* b[i] < 0 */
	    /* add artificial variable, make basic */
	    A[i][LastCol]        = -1;  
	    x[i]                 = -b[i];
	    varType[LastCol]     = Artificial;
	    ArtificialAdded      = true;
	    BasicVariables[i]    = LastCol;
	    /* slack not basic */
	    slack             = ConstraintVariable[i];
	    NonBasicVariables[NextNonBasic] = slack;
	    /* increment counter */
	    NextNonBasic++;
	    LastCol++;
	    NumArtificials++;
	  }
	  break;
	}
      
      numNonbasic  = LastCol - numConstraints;
      numVariables = LastCol;
      
      if (NumArtificials > 0)
	this.SetCostForPhaseOne();

      return true;
    } /* end preprocess procedure */
  
  /* SetCostForPhaseOne sets feasibility cost function.
   * Run getRidOfArtificials after the problem has been solved.
   */
  
  public void SetCostForPhaseOne()
    {
      float newCoefficients [] = new float [numVariables];
      
      /* Set the coefficients of the objective */
      for (int i = 0; i < numVariables; i++) {
	OriginalCost[i] = cost[i];
	
	if (varType[i] == Artificial)
	  newCoefficients[i] = 1;
	else
	  newCoefficients[i] = 0;
      }
      this.specifyObjective(newCoefficients, true); /* minimize */
    } /* end SetCostForPhaseOne procedure */
  
  
  public boolean getRidOfArtificials()
    {
	  boolean result = true;
      int LastBasic        = 0;
      int LastNonBasic     = 0;
      int CountArtificials = 0;
      int ArtificialsInBasis;
      int[]   BasisType  = new int [numVariables];
      float[] TempX      = new float [numVariables];

      int i;

      /* Build index of variable type */

      for (i = 0; i < numNonbasic; i++)
	BasisType[NonBasicVariables[i]] = NonBasicType;

      for (i = 0; i < numConstraints; i++) {
	BasisType[BasicVariables[i]] = BasicType;
	TempX[BasicVariables[i]] = x[i];
      }

      /*  Move real BasicVariables to the beginning of the matrix.
       *  Artificials are to the right.  They will be swapped out
       *  by a call to AugmentBasis.
       */

      for (i = 0; i < numVariables; i++)  
	if (varType[i] != Artificial) {
	  switch (BasisType[i]) {
	  case BasicType:
	    BasicVariables[LastBasic] = i;
	    x[LastBasic] = TempX[i];
	    LastBasic++;
	    break;
	  case NonBasicType:
	    NonBasicVariables[LastNonBasic] = i;
	    LastNonBasic++;
	    break;
	  default:
	    System.out.println("Error:Variable must be of Basic or NonBasic type");
	  }
	}
	else CountArtificials++;

      ArtificialsInBasis = 0;

      for (i = 0; i < numVariables; i++)  
	if (varType[i] == Artificial) {
	  switch (BasisType[i]) {
	  case BasicType:
	    ArtificialsInBasis++;
	    BasicVariables[LastBasic] = i;
	    x[LastBasic] = TempX[i];
	    LastBasic++;
	    break;
	  case NonBasicType:
	    NonBasicVariables[LastNonBasic] = i;
	    LastNonBasic++;
	    break;
	  default:
	    System.out.println("Error:Variable must be of Basic or NonBasic type");
	  }
	}

      /* test for artificial variables in basis */

      if (ArtificialsInBasis > 0) {
	//System.out.println("CALLING AugmentBasis!!");
	AugmentBasis(numConstraints-ArtificialsInBasis);

	/* rebuild index, BasicVariables, NonBasicVariables, and X */

	for (i = 0; i < numConstraints; i++) {
	  BasicVariables[i] = perm[i];
	}

	for (i = 0; i < numVariables; i++)
	  BasisType[i] = NonBasicType;  /* default */

	for (i = 0; i < numConstraints; i++)
	  BasisType[BasicVariables[i]] = BasicType;

	LastBasic = 0; LastNonBasic = 0;

	for (i = 0; i < numVariables; i++)  
	  switch (BasisType[i]) {
	  case BasicType:
	    if (varType[i] == Artificial) {
	    	result = false;
	      //System.out.println("Error in GetRidOfArtificials:");
	      //System.out.println(" There are still artificials after AugmentBasis");
	    }
	    BasicVariables[LastBasic] = i;
	    x[LastBasic] = TempX[i];
	    LastBasic++;
	    break;
	  case NonBasicType:
	    NonBasicVariables[LastNonBasic] = i;
	    LastNonBasic++;
	    break;
	  default:
	    System.out.println("Error:Variable must be of Basic or NonBasic type");
	  }
      }
      
      ArtificialAdded = false;  /* The problem can now be treated
				   as in phase II */
      
      this.specifyObjective(OriginalCost, oldOptType);
      
      numNonbasic  -= CountArtificials;
      numVariables -= CountArtificials;
      
      /* reset TypeMinimize  */
      this.TypeMinimize = oldOptType;

      CurrentStep = 0;
      
      return result;

    } /* end getRidOfArtificials procedure */
  
  public float Dot (float []row, float []col, int  size) 
    {
      float result = 0;
      for (int i = 0; i < size; i++)
	result += row[i] * col[i];
      return result;
    }   /* end Dot procedure */
  
  public void showInfo()
    {
      for (int j = 0; j < numVariables; j++)
	System.out.println("cost["+j+"]:"+cost[j]);
      for (int i = 0; i < numConstraints; i++)
	for (int j = 0; j < numVariables; j++)
	  System.out.println("A["+i+"]["+j+"]:"+A[i][j]);
      
      System.out.println("LeavingVariable:"+LeavingVariable);
      
      System.out.println("EnteringVariable:"+EnteringVariable);
      
      for (int i = 0; i < numConstraints; i++) 
	System.out.println("littleCost["+i+"]:"+littleCost[i]);
      
      
      System.out.println("MinRatio:"+MinRatio);
      
      for (int i = 0; i < numConstraints; i++) 
	System.out.println("pi["+i+"]:"+pi[i]);
      
      for (int i = 0; i < numConstraints; i++) 
	System.out.println("yB["+i+"]:"+yB[i]);
      
      
      for(int i = 0; i < numConstraints; i++)
	System.out.println("BasicVariables["+i+"]:"+BasicVariables[i]);
      
      for(int i = 0; i < numNonbasic; i++)
	System.out.println("NonBasicVariables["+i+"]:" + 
			   NonBasicVariables[i]);
      
      for(int i = 0; i < numConstraints; i++)
	System.out.println("x["+i+"]:"+x[i]);
      
      for (int i = 0; i < numNonbasic; i++)
	System.out.println("reducedCost["+i+"]:"+reducedCost[i]);
      
      System.out.println("objectiveValue:"+objectiveValue);

      for (int i = 0; i < numVariables; i++)
	System.out.println("OriginalCost["+i+"]:" + 
			   OriginalCost[i]);

      System.out.println("TypeMinimize"+ TypeMinimize);
      System.out.println("oldOptType"+ oldOptType);
    }  /* end showInfo procedure */
  
  public int solveLP()
    {
      int status = 0;
      
      while (status == 0)
	status = this.iterate();
      
      return status;
    } /* end solveLP procedure */
  
  public void reset(int numberOfVariables, int numberOfConstraints)
    {
      for (int i = 0; i < numConstraints; i++)
	for (int j = 0; j < numVariables; j++)
	  A[i][j] = 0;
      
      numConstraintsSoFar = 0;
      numVariables = numberOfVariables;
      objectiveValue = 0;
      CurrentStep = 0;
      ArtificialAdded = false;
      NumArtificials = 0;
    } /* end reset procedure */
  
  public int AugmentBasis(int BasisSize)
    {
      /* on input:
       *  BasisSize = number of elements in the partial basis
       *  BasicVariables = the current "partial" basis in its locations 
       *                                            0,1,...BasisSize-1.
       *          Assumes that the column indices are selected 
       *          from [0,1,2,...cols-1].
       *
       * on output: 
       *  perm = in locations BasisSize, +1,...,rows-1 contains the
       *        additional indices needed to form a full basis.  
       */

      int i, j, k, ihi, ilo, itemp;
      float[][] BN;
      float vnorm, v1, v2, dtemp;
      float[] tvec;
      
      int   rows, cols;

      /* Make local copies of variables */

      rows = numConstraints;
      cols = numVariables;

      /* check that input is OK */
      
      if (rows > cols) {
	System.out.println("Odd-shaped constraint matrix:");
	System.out.println("rows = "+rows+" cols = "+cols);
	return 1;
      }
      
      if (BasisSize > rows) {
	System.out.println("Too many elements in the basis!");
	System.out.println("BasisSize = "+BasisSize+" rows = "+rows);
	return 1;
      }
      if (BasisSize < 0 || rows <= 0 || cols <= 0) {
	System.out.println("Something is rotten in the State of Denmark");
	return 1;
      }

      /* check that the elements of the current BasicVariables make sense */
      
      for (i = 0; i < BasisSize; i++) {
	if (BasicVariables[i] < 0 || BasicVariables[i] >= cols) {
	  System.out.println("Basis element["+i+"]= "+BasicVariables[i]+" out of range");
	  return 1;
	}
      }
      
      if (BasisSize == rows) {
	System.out.println("Basis already has the right number of elements");
	return 0;
      }
      /*      
      System.out.println("The current basis has "+BasisSize+" columns.");
      System.out.println("It needs "+(rows-BasisSize)+" more.");
      */

      /* On with the job. First allocate temporary data structures */
      perm = new int[cols]; 
      BN   = new float[rows][cols];
      
      /* transfer columns of A to BN, with the current basis in the first
	 BasisSize locations and the remaining columns in the last cols-BasisSize
	 locations. Keep track of the permutations in perm */
      
      ilo = 0;  
      ihi = cols - 1;

      for (i = 0; i < cols; i++) {
	if (inbasis(i, BasisSize, BasicVariables)) {
	  for (j = 0; j < rows; j++) 
	    BN[j][ilo] = A[j][i]; 
	  perm[ilo] = i; 
	  ilo++;
	} else {
	  for (j = 0; j < rows; j++) 
	    BN[j][ihi] = A[j][i]; 
	  perm[ihi] = i; 
	  ihi--;
	}
      }
      
      tvec = new float[rows];
      
      /* now do BasisSize stages of the QR factorization */
      
      for (i = 0; i < BasisSize; i++) {
	
	/* find norm of the present column and calculate Householder vector */

	vnorm = 0;
	for (j = i; j < rows; j++) 
	  vnorm += BN[j][i] * BN[j][i];
	vnorm = (float) Math.sqrt(vnorm);

	for (j = i; j < rows; j++) 
	  tvec[j] = BN[j][i];

	if (tvec[i] < 0) {
	  BN[i][i] = vnorm; 
	  tvec[i] -= vnorm;
	} else {
	  BN[i][i] = -vnorm; 
	  tvec[i] += vnorm;
	}
	
	/* blank out present column */
	for (j = i+1; j < rows; j++) 
	  BN[j][i] = 0;
	
	/* apply the Householder to the remaining columns */
	v1 = 0;
	for (j = i; j < rows; j++) 
	  v1 += tvec[j]*tvec[j];

	v1 = 2 / v1;
	
	for (k = i+1; k < cols; k++) {
	  v2 = 0;
	  for (j = i; j < rows; j++) 
	    v2 += tvec[j]*BN[j][k];
	  v2 *= v1;
	  for (j = i; j < rows; j++) 
	    BN[j][k] -= v2 * tvec[j];
	}
      }
      
      /* next phase - find another m-BasisSize columns by choosing the largest
	 pivot in each row */
      
      for (i = BasisSize; i < rows; i++) {
	
	/* identify the column with largest pivot in row i */
	v1 = Math.abs(BN[i][i]); 
	ihi = i;
	for (j = i+1; j < cols; j++) {
	  if (Math.abs(BN[i][j]) > v1) {
	    v1 = Math.abs(BN[i][j]); 
	    ihi = j;
	  }
	}
	/* swap column ihi with column i */
	itemp     = perm[i]; 
	perm[i]   = perm[ihi]; 
	perm[ihi] = itemp;
	
	for (j = 0; j < rows; j++) {
	  dtemp      = BN[j][i]; 
	  BN[j][i]   = BN[j][ihi]; 
	  BN[j][ihi] = dtemp;
	}
	
	/* if we're not at stage "rows" yet, proceed with Householder */

	if (i < rows-1) {
	  /* now construct the Householder vector from column i as before */ 

	  vnorm = 0;
	  for (j = i; j < rows; j++) 
	    vnorm += BN[j][i] * BN[j][i];

	  vnorm = (float) Math.sqrt(vnorm);

	  for (j = i; j < rows; j++) 
	    tvec[j] = BN[j][i];

	  if (tvec[i] < 0) {
	    BN[i][i] = vnorm; 
	    tvec[i] -= vnorm;
	  } else {
	    BN[i][i] = -vnorm; 
	    tvec[i] += vnorm;
	  }
	  
	  /* blank out present column */
	  for (j = i+1; j < rows; j++) 
	    BN[j][i] = 0;
	  
	  /* apply the Householder to the remaining columns */
	  v1 = 0;
	  for (j = i; j < rows; j++) 
	    v1 += tvec[j] * tvec[j];
	  v1 = 2 / v1;
	  
	  for (k = i+1; k < cols; k++) {
	    v2 = 0;
	    for (j = i; j < rows; j++) 
	      v2 += tvec[j] * BN[j][k];
	    v2 *= v1;
	    for (j = i; j < rows; j++) 
	      BN[j][k] -= v2 * tvec[j];
	  }
	}
      }
      
      /* elements BasisSize, BasisSize+1, ... "rows"-1 of perm should 
       * now contain the columns required for a full basis; 
       */
      
      return 0;
    }
  
  boolean inbasis(int i, int BasisSize, int[] basis)
  {
    int j;
    for (j = 0; j < BasisSize; j++) {
      if (basis[j] == i) 
	return true;
    }
    return false;
  }
  
} /* end revisedSimplex class */
