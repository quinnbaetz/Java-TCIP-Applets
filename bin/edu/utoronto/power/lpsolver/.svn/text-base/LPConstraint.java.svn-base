package edu.utoronto.power.lpsolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
 * <p>This class defines a constraint on the solution of the LP.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Zeb Tate
 * @version 1.0
 */
public class LPConstraint
    implements Serializable {
  /**
   * ConstraintName is the friendly name used for displaying the constraint in
   * output.
   */
  public String ConstraintName;
  private int ConstraintType;
  private double RightHandSide;
  private ArrayList ConstraintElements = new ArrayList();

  /**
   * EQUAL denotes a constraint that is an equality constraint. (Used when calling DefineRightHandSide)
   *
   * @see DefineRightHandSide(double RightHandSide, int ConstraintType)
   */
  public final static int EQUAL = 1;

  /**
   * LESSTHANOREQUAL denotes a constraint that is a less than or equal to
   * constraint. (Used when calling DefineRightHandSide)
   *
   * @see DefineRightHandSide(double RightHandSide, int ConstraintType)
   */
  public final static int LESSTHANOREQUAL = 2;

  /**
   * LESSTHAN denotes a constraint that is a less than constraint. (Used when calling DefineRightHandSide)
   *
   * @see DefineRightHandSide(double RightHandSide, int ConstraintType)
   */
  public final static int LESSTHAN = 3;

  /**
   * GREATERTHANOREQUAL denotes a constraint that is a greater than or equal to
   * constraint. (Used when calling DefineRightHandSide)
   *
   * @see DefineRightHandSide(double RightHandSide, int ConstraintType)
   */
  public final static int GREATERTHANOREQUAL = 4;

  /**
   * GREATERTHAN denotes a constraint that is a greater than
   * constraint. (Used when calling DefineRightHandSide)
   *
   * @see DefineRightHandSide(double RightHandSide, int ConstraintType)
   */
  public final static int GREATERTHAN = 5;

  /**
   * The constructor only needs to be passed in the name of the constraint.  The
   * remainder of the setup is accomplished with DefineRightHandSide and AddElement.
   *
   * @param ConstraintName String that will be the displayed name for the
   *   constraint (i.e. "Power Balance")
   */
  public LPConstraint(String ConstraintName) {
    this.ConstraintName = ConstraintName;
  }

  /**
   * <p>DefineRightHandSide(double LeftBound, double RightBound, boolean Range)
   * defines a constraint which is of a range type.  This means the function
   * evaluated is restricted on the left by LeftBound and on the right by
   * RightBound.  One example where this is used is power line constraints, which
   * typically can go from -Limit to +Limit.</p>
   *
   * @param LeftBound double
   * @param RightBound double
   * @param Range boolean
   * @return Slack LPVariable created for the constraint.
   */
  public LPVariable DefineRightHandSide(double LeftBound, double RightBound,
                                        boolean Range) {
    this.RightHandSide = RightBound;
    LPVariable MySlack = new LPVariable(ConstraintName + " Slack", 0.0,
                                        RightBound - LeftBound);
    MySlack.AddCostSegment(0.0, RightBound - LeftBound, 0.0);
    MySlack.AddCostSegment(Double.NEGATIVE_INFINITY, 0.0, -1e10);
    MySlack.AddCostSegment(0, Double.POSITIVE_INFINITY, 1e10);
    AddElement(1.0, MySlack);
    return MySlack;
  }

  /**
   * DefineRightHandSide(double RightHandSide, int ConstraintType) is used to
   * define equality and one-sided inequality constraints.
   *
   * @param RightHandSide double that denotes the value to the right of the
   *   equal or inequality sign for this constraint, i.e. the "b" value in Ax =
   *   b.
   * @param ConstraintType integer that denotes what type of constraint is
   *   needed here.
   * @see EQUAL
   * @see LESSTHANOREQUAL
   * @see LESSTHAN
   * @see GREATERTHANOREQUAL
   * @see GREATERTHAN
   * @return LPVariable that was created as the slack variable for this
   *   constraint.  If no slack variable was created, this returns null.
   */
  public LPVariable DefineRightHandSide(double RightHandSide,
                                        int ConstraintType) {
    this.RightHandSide = RightHandSide;
    LPVariable MySlack = null;
    this.ConstraintType = ConstraintType;
    switch (ConstraintType) {
      case LESSTHANOREQUAL:
        MySlack = new LPVariable(ConstraintName + " Slack", 0.0,
                                 Double.POSITIVE_INFINITY);
        MySlack.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 0.0);
        AddElement(1.0, MySlack);
        break;
      case LESSTHAN:
        MySlack = new LPVariable(ConstraintName + " Slack", Double.MIN_VALUE,
                                 Double.POSITIVE_INFINITY);
        MySlack.AddCostSegment(Double.MIN_VALUE, Double.POSITIVE_INFINITY, 0.0);
        AddElement(1.0, MySlack);
        break;
      case GREATERTHANOREQUAL:
        MySlack = new LPVariable(ConstraintName + " Slack", 0.0,
                                 Double.POSITIVE_INFINITY);
        MySlack.AddCostSegment(0.0, Double.POSITIVE_INFINITY, 0.0);
        AddElement( -1.0, MySlack);
        break;
      case GREATERTHAN:
        MySlack = new LPVariable(ConstraintName + " Slack", Double.MIN_VALUE,
                                 Double.POSITIVE_INFINITY);
        AddElement( -1.0, MySlack);
        MySlack.AddCostSegment(Double.MIN_VALUE, Double.POSITIVE_INFINITY, 0.0);
        break;
    }
    return MySlack;
  }

  /**
   * AddElement adds a term in the constraint to the left hand of the equal
   * sign; it consists of a multiplier/LPVariable pair.
   *
   * @param multiplier double that is the coefficient of the LPVariable for
   *   this constraint
   * @param variable LPVariable that corresponds to the multiplier within this
   *   constraint.
   */
  public void AddElement(double multiplier, LPVariable variable) {
    LPConstraintElement addelement = new LPConstraintElement(multiplier,
        variable);
    ConstraintElements.add(addelement);
  }
  
  public void RemoveElement(LPVariable variable) {
    Iterator elementiter = ConstraintElements.iterator();
    LPConstraintElement removeElement = null;
    while (elementiter.hasNext()) {
      LPConstraintElement checkelement = (LPConstraintElement) elementiter.next();
      if (checkelement.variable == variable) {
        removeElement = checkelement;
      }
    }
    if (removeElement != null)
    	ConstraintElements.remove(removeElement);
  }

  /**
   * LookUpCoefficient looks up an LPVariable to see if it is represented in the
   * constraint.  It returns either a zero if the variable is not found or the
   * multiplier originally assigned using the AddElement method.
   *
   * @param checkvar LPVariable to look up in this constraint.
   * @return double that is the coefficient for the variable queried.
   */
  public double LookUpCoefficient(LPVariable checkvar) {
    Iterator elementiter = ConstraintElements.iterator();
    double returnvalue = 0;
    while (elementiter.hasNext()) {
      LPConstraintElement checkelement = (LPConstraintElement) elementiter.next();
      if (checkelement.variable == checkvar) {
        returnvalue = checkelement.multiplier;
      }
    }
    return returnvalue;
  }

  private class LPConstraintElement
      implements Serializable {
    double multiplier;
    LPVariable variable;

    public LPConstraintElement(double multiplier, LPVariable variable) {
      this.multiplier = multiplier;
      this.variable = variable;
    }
  }
  
  public double getRHS() {
	  return RightHandSide;
  }

  public int getConstraintType() {
	  return ConstraintType;
  }
}
