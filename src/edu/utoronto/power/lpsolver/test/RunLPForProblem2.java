package edu.utoronto.power.lpsolver.test;

import java.util.*;

import edu.utoronto.power.lpsolver.*;

public class RunLPForProblem2 {
  public RunLPForProblem2() {
  }

  public static void main(String[] args) {
    System.out.println("--------------------");
    System.out.println("Homework 3 Problem 2");
    System.out.println("--------------------");
    ArrayList LPVariables = new ArrayList();
    ArrayList LPConstraints = new ArrayList();
    ArrayList Basis = new ArrayList();
    ArrayList Nonbasis = new ArrayList();

    LPVariable Pg1 = new LPVariable("Pg1", 0.0, 300.0);
    Pg1.AddCostSegment(0.0, 300.0, 15.0);
    LPVariables.add(Pg1);

    LPVariable Pg2 = new LPVariable("Pg2", 0.0, 150.0);
    Pg2.AddCostSegment(0.0, 150.0, 20.0);
    LPVariables.add(Pg2);

    LPVariable Pg3 = new LPVariable("Pg3", 0.0, Double.POSITIVE_INFINITY);
    Pg3.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 25.0);
    LPVariables.add(Pg3);

    LPVariable Pg4 = new LPVariable("Pg4", 0.0, Double.POSITIVE_INFINITY);
    Pg4.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 23.0);
    LPVariables.add(Pg4);

//    LPVariable S1 = new LPVariable("S1", 0.0, Double.POSITIVE_INFINITY);
//    S1.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 0.0);
//    LPVariables.add(S1);

//    LPConstraint c1 = new LPConstraint("c1", 10);
//    c1.AddElement(2, x1);
//    c1.AddElement(1, x2);
//    c1.AddElement(1, x3);
//    LPConstraints.add(c1);
    LPConstraint c1 = new LPConstraint("Line Limit");
    LPVariable Slack1 = c1.DefineRightHandSide(100.0, c1.LESSTHANOREQUAL);
    if (Slack1 != null) {
      LPVariables.add(Slack1);
    }
    c1.AddElement(0.5, Pg1);
    c1.AddElement(0.3, Pg2);
    c1.AddElement( -0.2, Pg3);
    //c1.AddElement(1, S1);
    LPConstraints.add(c1);

    //LPConstraint c2 = new LPConstraint("Power balance", 700.0);
    LPConstraint c2 = new LPConstraint("Power balance");
    c1.DefineRightHandSide(700.0, c2.EQUAL);
    c2.AddElement(1.0, Pg1);
    c2.AddElement(1.0, Pg2);
    c2.AddElement(1.0, Pg3);
    c2.AddElement(1.0, Pg4);
    LPConstraints.add(c2);

    try {
      Pg1.SetValue(0.0, false);
      Pg2.SetValue(0.0, false);
      Pg3.SetValue(0.0, false);
      Pg4.SetValue(700.0, true);
      Slack1.SetValue(100.0, true);
    }
    catch (LPException e) {
      System.out.println("Error occurred when setting values.");
      System.out.println(e.getMessage());
    }

    Nonbasis.add(Pg1);
    Nonbasis.add(Pg2);
    Nonbasis.add(Pg3);

    Basis.add(Pg4);
    Basis.add(Slack1);

    LPSolver Test = new LPSolver(LPVariables, LPConstraints);
    Test.SolveLP(Basis, Nonbasis);

    //Print out the optimal values
    Iterator variter = LPVariables.iterator();
    java.text.DecimalFormat OutputFormatter = new java.text.DecimalFormat(
        "##0.00");
    System.out.println("\nOptimal Results:");
    double totalcost = 0.0;
    while (variter.hasNext()) {
      LPVariable var = (LPVariable) variter.next();
      System.out.println(var.VariableName + " = " +
                         OutputFormatter.format(var.GetValue()));
      totalcost +=var.GetCost()*var.GetValue();
    }

    System.out.println("Total objective evaluation: " + OutputFormatter.format(totalcost));

  }

}
