package edu.utoronto.power.lpsolver.test;

import java.io.*;
import java.util.*;

import Jama.*;
import edu.utoronto.power.lpsolver.*;
import edu.utoronto.power.lpsolver.LPSolver.LPSolverVerbosityLevel;

public class CopyOfRunLPForProblem3 {
  public CopyOfRunLPForProblem3() {
  }

  public static void main(String[] args) {

	  
    System.out.println("--------------------");
    System.out.println("Homework 3 Problem 3");
    System.out.println("--------------------");
    ArrayList LPVariables = new ArrayList();
    ArrayList LPConstraints = new ArrayList();
    ArrayList Basis = new ArrayList();
    ArrayList Nonbasis = new ArrayList();
    double[][] ISF = new double[7][5];
    ISF[0][0] = 0.0;
    ISF[0][1] = -0.8364;
    ISF[0][2] = -0.6545;
    ISF[0][3] = -0.6909;
    ISF[0][4] = -0.7879;
    ISF[1][0] = 0.0;
    ISF[1][1] = -0.1636;
    ISF[1][2] = -0.3455;
    ISF[1][3] = -0.3091;
    ISF[1][4] = -0.2121;
    ISF[2][0] = 0.0;
    ISF[2][1] = 0.0909;
    ISF[2][2] = -0.3636;
    ISF[2][3] = -0.2727;
    ISF[2][4] = -0.0303;
    ISF[3][0] = 0.0;
    ISF[3][1] = 0.0485;
    ISF[3][2] = -0.1939;
    ISF[3][3] = -0.2788;
    ISF[3][4] = -0.0606;
    ISF[4][0] = 0.0;
    ISF[4][1] = 0.0242;
    ISF[4][2] = -0.0970;
    ISF[4][3] = -0.1394;
    ISF[4][4] = -0.6970;
    ISF[5][0] = 0.0;
    ISF[5][1] = -0.0727;
    ISF[5][2] = 0.2909;
    ISF[5][3] = -0.5818;
    ISF[5][4] = -0.2424;
    ISF[6][0] = 0.0;
    ISF[6][1] = -0.0242;
    ISF[6][2] = 0.0970;
    ISF[6][3] = 0.1394;
    ISF[6][4] = -0.3030;

    try {

      LPVariable Pg1 = new LPVariable("deltaPg1", -2.7, 7.3);
      Pg1.AddCostSegment( -2.7, 7.3, 1500.0);
      LPVariables.add(Pg1);

      LPVariable Pg2 = new LPVariable("deltaPg2", 0.0, 3.0);
      Pg2.AddCostSegment(0.0, 3.0, 1700.0);
      LPVariables.add(Pg2);

      LPVariable Pg4 = new LPVariable("deltaPg4", 0.0, 2.5);
      Pg4.AddCostSegment(0.0, 2.5, 1900.0);
      LPVariables.add(Pg4);

      LPConstraint PBalance = new LPConstraint("Power Balance");
      PBalance.DefineRightHandSide(0.0, PBalance.EQUAL);
      PBalance.AddElement(1, Pg1);
      PBalance.AddElement(1, Pg2);
      PBalance.AddElement(1, Pg4);
      LPConstraints.add(PBalance);

      LPConstraint Line1 = new LPConstraint("Line 1 Overload");
      LPVariable Line1Slack = Line1.DefineRightHandSide( -1.50 - 1.9727,
          1.500 - 1.9727, true);
      LPVariables.add(Line1Slack);
      Line1.AddElement(ISF[0][1], Pg2);
      Line1.AddElement(ISF[0][3], Pg4);
      LPConstraints.add(Line1);

      LPConstraint Line3 = new LPConstraint("Line 3 Overload");
      LPVariable Line3Slack = Line3.DefineRightHandSide( -0.5 - 0.4639,
          0.500 - 0.4639, true);
      LPVariables.add(Line3Slack);
      Line3.AddElement(ISF[2][1], Pg2);
      Line3.AddElement(ISF[2][3], Pg4);
      LPConstraints.add(Line3);

      Pg1.SetValue(0.0, true);
      Pg2.SetValue(0.0, false);
      Pg4.SetValue(0.0, false);
      Line1Slack.SetValue( -0.4727, true);
      Line3Slack.SetValue(0.0361, true);

      Nonbasis.add(Pg2);
      Nonbasis.add(Pg4);

      Basis.add(Pg1);
      Basis.add(Line1Slack);
      Basis.add(Line3Slack);
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
      Matrix lambda = Test.SolveLP(Basis, Nonbasis, LPSolverVerbosityLevel.FULL);

      //Print out the optimal values
      Iterator variter = LPVariables.iterator();
      java.text.DecimalFormat OutputFormatter = new java.text.DecimalFormat(
          "##0.00");
      System.out.println("\nOptimal Redispatch Results:");
      while (variter.hasNext()) {
        LPVariable var = (LPVariable) variter.next();
        System.out.println(var.VariableName + " = " +
                           OutputFormatter.format(var.GetValue() * 100) + " MW");
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
