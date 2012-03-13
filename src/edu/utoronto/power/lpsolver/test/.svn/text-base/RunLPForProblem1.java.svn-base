package edu.utoronto.power.lpsolver.test;

import java.util.*;

import edu.utoronto.power.lpsolver.*;
import edu.utoronto.power.lpsolver.LPSolver.LPSolverVerbosityLevel;

public class RunLPForProblem1 {
  public RunLPForProblem1() {
  }

  public static void main(String[] args) {
    System.out.println("--------------------");
    System.out.println("Homework 3 Problem 1");
    System.out.println("--------------------");
    ArrayList LPVariables = new ArrayList();
    ArrayList LPConstraints = new ArrayList();
    ArrayList Basis = new ArrayList();
    ArrayList Nonbasis = new ArrayList();

    LPVariable cgrade = new LPVariable("Construction Grade",0.0,Double.POSITIVE_INFINITY);
    cgrade.AddCostSegment(0.0,Double.POSITIVE_INFINITY,-200.0);
    LPVariables.add(cgrade);

    LPVariable fgrade = new LPVariable("Finish Grade",0.0,Double.POSITIVE_INFINITY);
    fgrade.AddCostSegment(0.0,Double.POSITIVE_INFINITY,-250.0);
    LPVariables.add(fgrade);

    LPConstraint c1 = new LPConstraint("Rough Saw Limit");
    LPVariable Slack1 = c1.DefineRightHandSide(10.0, c1.LESSTHANOREQUAL);
    if (Slack1 != null) {
      LPVariables.add(Slack1);
    }
    c1.AddElement(2.0,cgrade);
    c1.AddElement(2.0,fgrade);
    LPConstraints.add(c1);

    LPConstraint c2 = new LPConstraint("Plane Limit");
    LPVariable Slack2 = c2.DefineRightHandSide(16.0, c2.LESSTHANOREQUAL);
    if (Slack2 != null) {
      LPVariables.add(Slack2);
    }
    c2.AddElement(3.0,cgrade);
    c2.AddElement(5.0,fgrade);
    LPConstraints.add(c2);

    try {
      cgrade.SetValue(0.0, false);
      fgrade.SetValue(0.0, false);
      Slack1.SetValue(10.0, true);
      Slack2.SetValue(16.0, true);
    }
    catch (LPException e) {
      System.out.println("Error occurred when setting values.");
      System.out.println(e.getMessage());
    }

    Nonbasis.add(cgrade);
    Nonbasis.add(fgrade);

    Basis.add(Slack1);
    Basis.add(Slack2);

    LPSolver Test = new LPSolver(LPVariables, LPConstraints);

    double lambda[] = Test.SolveLP(Basis, Nonbasis, LPSolverVerbosityLevel.FULL).getColumnPackedCopy();

    double totalcost = 0.0;
    //Print out the optimal values
    Iterator variter = LPVariables.iterator();
    java.text.DecimalFormat OutputFormatter = new java.text.DecimalFormat(
        "##0.00");
    System.out.println("\nOptimal Results:");
    while (variter.hasNext()) {
      LPVariable var = (LPVariable) variter.next();
      if (var.CanGoUpOrDown()) {
        totalcost += var.GetValue()*var.GetCostUpAndDown()[0];
      }
      else {
        totalcost += var.GetValue()*var.GetCost();
      }
      if (var == cgrade | var == fgrade) {
        System.out.println(var.VariableName + " = " +
                           OutputFormatter.format(var.GetValue()) + " thousand board feet/day");
      }
    }

    //Print out the total cost
    System.out.println("\nTotal profit: $" + OutputFormatter.format(-totalcost) + "/day");

    //Print out the sensitivities
    Iterator constraintiter = LPConstraints.iterator();
    System.out.println("\nSensitivity Information:");
    System.out.println(" Increase limit \t    Increase Profit");
    System.out.println("----------------\t----------------------");
    while (constraintiter.hasNext()) {
      LPConstraint constr = (LPConstraint) constraintiter.next();
      int idx = LPConstraints.indexOf(constr);
      System.out.println(constr.ConstraintName + "\t\t $" + OutputFormatter.format(-lambda[idx]) + "/(hr*day)");
    }
  }

}

//    LPVariable x1 = new LPVariable("x1", 0.0, Double.POSITIVE_INFINITY);
//    LPVariable x1 = new LPVariable("x1", 0.0, 4.0);
//    x1.AddCostSegment(0.0, 4.0, -3.0);
//    LPVariables.add(x1);
//
//    LPVariable x2 = new LPVariable("x2", 0.0, 5.0);
//    x2.AddCostSegment(0.0, 5.0, -2.0);
//    LPVariables.add(x2);
//
//    LPVariable x3 = new LPVariable("x3", 0.0, Double.POSITIVE_INFINITY);
//    x3.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 0.0);
//    LPVariables.add(x3);
//
//    LPVariable x4 = new LPVariable("x4", 0.0, Double.POSITIVE_INFINITY);
//    x4.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 0.0);
//    LPVariables.add(x4);
//
//    LPConstraint c1 = new LPConstraint("c1", 10);
//    c1.AddElement(2, x1);
//    c1.AddElement(1, x2);
//    c1.AddElement(1, x3);
//    LPConstraints.add(c1);
//
//    LPConstraint c2 = new LPConstraint("c2", 27);
//    c2.AddElement(5, x1);
//    c2.AddElement(3, x2);
//    c2.AddElement(1, x4);
//    LPConstraints.add(c2);
//
//    x1.SetValue(0);
//    x2.SetValue(0);
//    x3.SetValue(10);
//    x4.SetValue(27);
//
//    Basis.add(x3);
//    Basis.add(x4);
//    Nonbasis.add(x1);
//    Nonbasis.add(x2);
