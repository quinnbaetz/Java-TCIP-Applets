package edu.utoronto.power.lpsolver.test;

import java.io.*;
import java.util.*;

import Jama.*;
import edu.utoronto.power.lpsolver.*;
import edu.utoronto.power.lpsolver.LPSolver.LPSolverVerbosityLevel;

public class RunLPForProblem3 {
  public RunLPForProblem3() {
  }

  static final double BIGVAL = 1e8;
  
  private static void addCostSegmentWithPenalties(LPVariable lpV, double cost, double minVal, double maxVal) {
	  if (minVal != maxVal) {
		  lpV.AddCostSegment(Double.NEGATIVE_INFINITY, minVal, -BIGVAL);
		  lpV.AddCostSegment(minVal, maxVal, cost);
		  lpV.AddCostSegment(maxVal, Double.POSITIVE_INFINITY, BIGVAL);
	  } else {
		  lpV.AddCostSegment(Double.NEGATIVE_INFINITY, minVal, -BIGVAL);
		  lpV.AddCostSegment(maxVal, Double.POSITIVE_INFINITY, BIGVAL);
	  }
  }

  public static void main(String[] args) {
	  
    System.out.println("--------------------");
    System.out.println("Homework 3 Problem 3");
    System.out.println("--------------------");
    ArrayList<LPVariable> LPVariables = new ArrayList<LPVariable>();
    ArrayList<LPConstraint> LPConstraints = new ArrayList<LPConstraint>();
    ArrayList<LPVariable> Basis = new ArrayList<LPVariable>();
    ArrayList<LPVariable> Nonbasis = new ArrayList<LPVariable>();

    try {
    	double pSum = 0;
    	
    	LPVariable Pg1 = new LPVariable("PG1",-200,800);//-Double.MAX_VALUE,Double.MAX_VALUE);
    	//addCostSegmentWithPenalties(Pg1,1500,-200,0);
    	Pg1.AddCostSegment(-200, 800, 1000);
    	LPVariables.add(Pg1);
    	Pg1.SetValue(-200, false);
    	pSum += 200;
		Nonbasis.add(Pg1);
    	
    	LPVariable Pg2 = new LPVariable("PG2",-200,0);//-Double.MAX_VALUE,Double.MAX_VALUE);
    	//addCostSegmentWithPenalties(Pg2,1000,-200,800);
    	LPVariables.add(Pg2);
    	Pg2.AddCostSegment(-200,800,1500);
    	Pg2.SetValue(-200, false);
    	pSum += 200;
		Nonbasis.add(Pg2);
    	
    	LPVariable Pg3 = new LPVariable("PG3",0,1000);//-Double.MAX_VALUE,Double.MAX_VALUE);
    	Pg3.AddCostSegment(0, 1000, 500);
    	//addCostSegmentWithPenalties(Pg3,500,0,1000);
    	LPVariables.add(Pg3);
    	Pg3.SetValue(0, false);
    	pSum += 0;
		Nonbasis.add(Pg3);
    	
    	double BMatrixDoubleArray[][] = new double[3][3];
    	BMatrixDoubleArray[0][0] = 1;
    	BMatrixDoubleArray[0][1] = 0;
    	BMatrixDoubleArray[0][2] = 0;
    	BMatrixDoubleArray[1][0] = 0;
    	BMatrixDoubleArray[1][1] = 20;
    	BMatrixDoubleArray[1][2] = -10;
    	BMatrixDoubleArray[2][0] = 0;
    	BMatrixDoubleArray[2][1] = -10;
    	BMatrixDoubleArray[2][2] = 10;
    	Matrix BMatrix = new Matrix(BMatrixDoubleArray);
    	BMatrix = BMatrix.getMatrix(1, 2, 1, 2);
    	Matrix AMatrix = new Matrix(2,2);
    	AMatrix.set(0, 0, -1);
    	AMatrix.set(0,1,0);
    	AMatrix.set(1,0,1);
    	AMatrix.set(1,1,-1);
    	Matrix SMatrix = new Matrix(2,2);
    	SMatrix.set(0,0,10);
    	SMatrix.set(0,1,0);
    	SMatrix.set(1,0,0);
    	SMatrix.set(1,1,10);
    	Matrix ISFMatrix = SMatrix.times(AMatrix.times(BMatrix.inverse()));
    	double ISF[][] = ISFMatrix.getArray();
    	
		LPConstraint PBalance = new LPConstraint("Power Balance");
		//LPVariable PBalanceSlack = PBalance.DefineRightHandSide(0.0, LPConstraint.EQUAL);
		LPVariable PBalanceSlack = new LPVariable("PBalance Slack", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		addCostSegmentWithPenalties(PBalanceSlack, 0, 0,0);
		LPVariables.add(PBalanceSlack);
		PBalance.AddElement(1, Pg1);
		PBalance.AddElement(1, Pg2);
		PBalance.AddElement(1, Pg3);
		PBalance.AddElement(1, PBalanceSlack);
		LPConstraints.add(PBalance);
		PBalanceSlack.SetValue(pSum,true);
		Basis.add(PBalanceSlack);
		
		LPConstraint Line1 = new LPConstraint("Line 1 Overload");
		Line1.DefineRightHandSide(0.0, LPConstraint.EQUAL);
		LPVariable Line1Slack = new LPVariable("Line 1 Slack",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
		addCostSegmentWithPenalties(Line1Slack, 0.0, -(800-200),-(-800-200));
		LPVariables.add(Line1Slack);
		Line1.AddElement(ISF[0][0], Pg2);
		Line1.AddElement(ISF[0][1], Pg3);
		Line1.AddElement(1.0,Line1Slack);
		LPConstraints.add(Line1);
		Line1Slack.SetValue(-ISF[0][0]*Pg2.GetValue() - ISF[0][1]*Pg3.GetValue() , true);
		Basis.add(Line1Slack);

		LPConstraint Line2 = new LPConstraint("Line 2 Overload");
		Line2.DefineRightHandSide(0.0, LPConstraint.EQUAL);
//		LPVariable Line2Slack = Line2.DefineRightHandSide( -1000 - 0,
//          1000-0, true);
		LPVariable Line2Slack = new LPVariable("Line 2 Slack",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
		addCostSegmentWithPenalties(Line2Slack, 0.0, -(200-0), -(-200-0));
		LPVariables.add(Line2Slack);
		Line2.AddElement(ISF[1][0], Pg2);
		Line2.AddElement(ISF[1][1], Pg3);
		Line2.AddElement(1.0,Line2Slack);
		LPConstraints.add(Line2);
		Line2Slack.SetValue(-ISF[1][0]*Pg2.GetValue() - ISF[1][1]*Pg3.GetValue(), true);
		Basis.add(Line2Slack);

//      Pablo's Values
//      Pg2.SetValue(0.0,false);
//      Pg4.SetValue(2.5,false);
//
//      Line1Slack.SetValue(1.2546,true);
//      Pg1.SetValue(-2.5,true);
//      Line3Slack.SetValue(0.7179,true);
//
//      Nonbasis.add(Pg2);
//      Nonbasis.add(Pg4);
//
//      Basis.add(Pg1);
//      Basis.add(Line1Slack);
//      Basis.add(Line3Slack);

		LPSolver Test = new LPSolver(LPVariables, LPConstraints);
		Matrix lambda = Test.SolveLP(Basis, Nonbasis, 10,LPSolverVerbosityLevel.FULL);

      //Print out the optimal values
      Iterator variter = LPVariables.iterator();
      java.text.DecimalFormat OutputFormatter = new java.text.DecimalFormat(
          "##0.00");
      System.out.println("\nOptimal Redispatch Results:");
      while (variter.hasNext()) {
        LPVariable var = (LPVariable) variter.next();
        System.out.println(var.VariableName + " = " +
                           OutputFormatter.format(var.GetValue()) + " MW");
      }

      //Determine the LMP at each bus

      //Due to the power balance constraint
      double[] AreaConstraint = new double[5];
      for (int k = 0; k < 5; k++) {
        AreaConstraint[k] = -1;
      }

      //Due to the Line 1 constraint
      double[] Line1Constraint = new double[5];
      for (int k = 0; k < 5; k++) {
        Line1Constraint[k] = -ISF[0][k];
      }

      //Due to the Line 3 constraint
      double[] Line3Constraint = new double[5];
      for (int k = 0; k < 5; k++) {
        Line3Constraint[k] = -ISF[2][k];
      }

      double[][] totalmatrix = new double[3][5];
      totalmatrix[0] = AreaConstraint;
      totalmatrix[1] = Line1Constraint;
      totalmatrix[2] = Line3Constraint;
      double[] LMPs = (lambda.times(-1.0).transpose().times(new Matrix(totalmatrix))).
          getColumnPackedCopy();

      //Print the LMPs
      System.out.println("\nLMP Results:");
      for (int k = 0; k < LMPs.length; k++) {
        System.out.println("LMP at bus " + (k + 1) + " is $" + OutputFormatter.format(LMPs[k]/100) + "/MW");
      }
    }
    catch (LPException e) {
      System.out.println("An error occurred when setting up the LP problem.");
      System.out.println(e.getMessage());
    }
    
    /*
    try {
      FileOutputStream out = new FileOutputStream("C:/test",false);
      ObjectOutputStream s = new ObjectOutputStream(out);
      //s.writeObject("Testing");
      s.writeObject(Basis);
      s.flush();
      s.close();
      out.close();


      FileInputStream in = new FileInputStream("C:/test");
      ObjectInputStream sin = new ObjectInputStream(in);
      //String hmm = (String)sin.readObject();
      Object test2=sin.readObject();
      int a = 1;
      a = 2;
    }
    catch (Throwable e) {
      System.out.println(e.getMessage());
      e.printStackTrace(System.out);
    }
	*/

  }
}
