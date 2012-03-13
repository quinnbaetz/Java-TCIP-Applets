package edu.utoronto.power.lpsolver;

import java.io.*;
import java.util.*;

/**
 * <p>Each LPVariable object represents a control variable in the linear program.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Zeb Tate
 * @version 1.0
 */
public class LPVariable
    implements Serializable {
  /**
   * VariableName is the friendly name for the LPVariable used in output
   */
  public String VariableName;

  private double _Value;

  private boolean _CanGoUpOrDown = false;

  private double _CurrentCost;
  private double _CurrentCostUpAndDown[] = new double[2];
  private boolean _AtLowerBound = false;

  private double[] _CurrentBounds = new double[2];
  private double[][] _CurrentBoundsUpAndDown = new double[2][2];

  private ArrayList LPCostCurve = new ArrayList();
  private ArrayList backupCostCurve = null;

  private double LeftBoundary;
  private double RightBoundary;
  private boolean UsePenaltyBoundary;
  private double PenaltyCost;
  
  public void backupCostCurve() {
	  backupCostCurve = new ArrayList(LPCostCurve);
  }
  
  public void clearCostCurve() {
	  LPCostCurve.clear();
  }
  
  public double getLeftBoundary() {
	  return LeftBoundary;
  }
  
  public double getRightBoundary() {
	  return RightBoundary;
  }
  
  public void setToZeroCost() {
	  LPCostCurve.clear();
	  this.AddCostSegment(0, Double.POSITIVE_INFINITY, 0);
  }
  
  public void restoreCostCurve() {
	  if (backupCostCurve != null)
		  LPCostCurve = new ArrayList(backupCostCurve);
  }

  /**
   * LPVariable(String VariableName, double LeftBoundary, double RightBoundary)
   * is used to set up simple bounded LP variables.
   *
   * @param VariableName String with the output name for this variable
   * @param LeftBoundary the leftmost value allowed for this variable
   * @param RightBoundary the rightmost value allowed for this variable
   */
  public LPVariable(String VariableName, double LeftBoundary,
                    double RightBoundary) {
    this.VariableName = VariableName;
    UsePenaltyBoundary = false;
    this.LeftBoundary = LeftBoundary;
    this.RightBoundary = RightBoundary;
  }

  /**
   * Adds a segment of the cost curve for this variable
   *
   * @todo Fix the ability to enable penalty costs for variables running
   *   outside of their boundaries / /* public LPVariable(String VariableName,
    *   double LeftBoundary, double RightBoundary, double PenaltyCost) {
    *   this.VariableName = VariableName; UsePenaltyBoundary = true;
    *   this.LeftBoundary = LeftBoundary; this.RightBoundary = RightBoundary; }
    * @param LeftEndpoint the leftmost value for which this cost segment applies
    * @param RightEndpoint the rightmost value for which this cost segment
    *   applies
    * @param Cost the cost/slope associated with this (linear) segment
    */
   public void AddCostSegment(double LeftEndpoint, double RightEndpoint,
                              double Cost) {
     LPCostSegment addsegment = new LPCostSegment(LeftEndpoint, RightEndpoint,
                                                  Cost);
     LPCostCurve.add(addsegment);
   }
   
   /**
    * Adds a segment of the cost curve for this variable and makes a penalty barrier
    * outside of the given left/right bounds
    *
    *  @param LeftEndpoint the leftmost value for which this cost segment applies
     * @param RightEndpoint the rightmost value for which this cost segment
     *   applies
     * @param costWithinBounds the cost/slope associated with this (linear) segment
     */  
   public void AddCostSegmentWithPenalties(double costWithinBounds, double LeftEndpoint, double RightEndpoint, double penaltyCost) {
	   if (LeftEndpoint != RightEndpoint) {
		   AddCostSegment(Double.NEGATIVE_INFINITY, LeftEndpoint, -penaltyCost);
		   AddCostSegment(LeftEndpoint, RightEndpoint, costWithinBounds);
		   AddCostSegment(RightEndpoint, Double.POSITIVE_INFINITY, penaltyCost);
	   } else {
		   AddCostSegment(Double.NEGATIVE_INFINITY, LeftEndpoint, -penaltyCost);
		   AddCostSegment(RightEndpoint, Double.POSITIVE_INFINITY, penaltyCost);
	  }
   }
   
   public void AddCostSegmentWithPenaltiesIncludeZeroBreakpoint(double costWithinBounds, double LeftEndpoint, double RightEndpoint, double penaltyCost) {
	   
	   if (LeftEndpoint > 0) {
		   AddCostSegment(Double.NEGATIVE_INFINITY, 0,-penaltyCost); 
		   AddCostSegment(0, LeftEndpoint,-penaltyCost);
		   AddCostSegment(LeftEndpoint, RightEndpoint, costWithinBounds);
		   AddCostSegment(RightEndpoint, Double.POSITIVE_INFINITY, penaltyCost);
	   } else if (RightEndpoint < 0) {
		   AddCostSegment(Double.NEGATIVE_INFINITY, LeftEndpoint,-penaltyCost); 
		   AddCostSegment(LeftEndpoint, RightEndpoint, costWithinBounds);
		   AddCostSegment(RightEndpoint, 0, penaltyCost);
		   AddCostSegment(0, Double.POSITIVE_INFINITY, penaltyCost);
	   } else {
		   AddCostSegment(Double.NEGATIVE_INFINITY, LeftEndpoint,-penaltyCost); 
		   AddCostSegment(LeftEndpoint, 0, costWithinBounds);
		   AddCostSegment(0, RightEndpoint, costWithinBounds);
		   AddCostSegment(RightEndpoint, Double.POSITIVE_INFINITY, penaltyCost);
	   }
   }   
   

  /**
   * CanGoUpOrDown checks to see whether this variable can go up or down.
   *
   * @return <p><b>true</b> if the variable can go up or down</p>
   * <p><b>false</b> if the variable can only go in one direction</p>
   */
  public boolean CanGoUpOrDown() {
    return _CanGoUpOrDown;
  }

  /**
   * SetValue sets the current value for the LP variable.
   *
   * @param Value new value of the variable
   * @param BasisVariable <p><b>true</b> if the variable is in the basis.</p>
   *   <p><b>false</b> if the variable isn't in the basis.</p>
   * @throws LPException if a value is specified which is outside the range of
   *   every cost segment, then an exception is thrown indicating this problem.
   */
  public void SetValue(double Value, boolean BasisVariable) throws LPException {
    boolean foundsegment = false;

    if (BasisVariable == false) {
      //We're setting the value of a nonbasis value
      //This means that we should end up on an endpoint.  Force the
      //value to an endpoint if it's not already there.  This situation arrises
      //primarily due to numerical inaccuracies of very small magnitude.  However,
      //in order to adequately handle piecewise cost functions, we must
      //be on the boundary of two pieces simulataneously.

      //We need to handle the variables that have cost functions which are
      //continuous over the entire range of values (-inf,+inf) OR stop after
      //a given lower and upper value (hard limit variables).  This is
      //accomplished by setting _CurrentLeftCost, _CurrentRightCost for
      //those variables which have cost curves on both sides of the set value.
      Iterator costiter = LPCostCurve.iterator();

      boolean foundendpoint = false;
      double tolerance = 1e-5;
      while (costiter.hasNext() & !foundendpoint) {
        //Pick off a cost segment, and see if the value passed in falls within
        //the segment, inclusively with the endpoints of the segment.
        LPCostSegment costseg = (LPCostSegment) costiter.next();
        if ( (Value >= (costseg.LeftEndpoint - tolerance)) & (Value <= (costseg.RightEndpoint + tolerance))) {
          //Found a segment which contains the value passed in.  Let's see
          //if it's closer to the left or right endpoint..
          if ( (Value - costseg.LeftEndpoint) < (costseg.RightEndpoint - Value)) {
            //The value passed in is closer to the left endpoint.  Force the
            //value to the left endpoint.
            Value = costseg.LeftEndpoint;
            foundendpoint = true;
          }
          else {
            //The value passed in is closer to the right endpoint.  Force the
            //value to the right endpoint.
            Value = costseg.RightEndpoint;
            foundendpoint = true;
          }
        }
      }

      _Value = Value;

      //Now, go through the cost segments with the fixed value and try and
      //find one or two segments which contain the given value.  If we find
      //two, that means this variable can go either up or down when it
      //re-enters the basis.  If there's just one segment, we treat it
      //like a normal basis variable..it will only have a single range
      //it's capable of

      //DownSegment refers to the cost curve which applies if
      //this value goes down.  UpSegment refers to the cost curve which
      //applies if this value goes up.
      LPCostSegment DownSegment = null;
      LPCostSegment UpSegment = null;

      costiter = LPCostCurve.iterator();

      while (costiter.hasNext()){// & (DownSegment == null) & (UpSegment == null)) {
        LPCostSegment costseg = (LPCostSegment) costiter.next();
        if ( (Value >= costseg.LeftEndpoint) & (Value <= costseg.RightEndpoint)) {
          foundsegment = true;
          //Found a containing segment.  Figure out if it's the Up- or DownSegment
          if (Value == costseg.LeftEndpoint) {
            //This is the UpSegment
            UpSegment = costseg;
          }
          else {
            //This is the DownSegment
            DownSegment = costseg;
          }
        }
      }

      if ( (UpSegment != null) | (DownSegment != null)) {
        if (UpSegment == null) {
          //There is only the down segment now.  Flag this variable
          //as restricted to going only in one direction.
          _CanGoUpOrDown = false;
          _AtLowerBound = false;

          //Fill in the current cost and current boundary information
          _CurrentCost = DownSegment.Cost;
          _CurrentBounds[0] = DownSegment.LeftEndpoint;
          _CurrentBounds[1] = DownSegment.RightEndpoint;
        }
        else if (DownSegment == null) {
          //There is only the up segment.  Flag this variable as
          //restricted to going only in one direction.
          _CanGoUpOrDown = false;
          _AtLowerBound = true;

          //Fill in the current cost and current boundary information
          _CurrentCost = UpSegment.Cost;
          _CurrentBounds[0] = UpSegment.LeftEndpoint;
          _CurrentBounds[1] = UpSegment.RightEndpoint;
        }
        else {
          //There are two segments.  Fill in CurrentCostUpAndDown and
          //CurrentBoundsUpAndDown
          _CanGoUpOrDown = true;

          _CurrentCostUpAndDown[0] = UpSegment.Cost;
          _CurrentCostUpAndDown[1] = DownSegment.Cost;

          _CurrentBoundsUpAndDown[0][0] = UpSegment.LeftEndpoint;
          _CurrentBoundsUpAndDown[0][1] = UpSegment.RightEndpoint;
          _CurrentBoundsUpAndDown[1][0] = DownSegment.LeftEndpoint;
          _CurrentBoundsUpAndDown[1][1] = DownSegment.RightEndpoint;

          //Set the single-direction cost and bound information to NaN,
          //which will make it quite apparent that a single direction and
          //cost was requested for a variable that can actually go in
          //both directions.
          _CurrentCost = Double.NaN;
          _CurrentBounds[0] = Double.NaN;
          _CurrentBounds[1] = Double.NaN;
        }
      }
    }
    else {
      //A basis variable is having its value set.  This is much easier
      //to handle, as a basis variable must lie within one and only
      //one cost segment.
      Iterator costiter = LPCostCurve.iterator();
      while (costiter.hasNext() & !foundsegment) {
        LPCostSegment costseg = (LPCostSegment) costiter.next();
        if ( (Value >= costseg.LeftEndpoint) & (Value <= costseg.RightEndpoint)) {
          _CurrentCost = costseg.Cost;
          _CurrentBounds[0] = costseg.LeftEndpoint;
          _CurrentBounds[1] = costseg.RightEndpoint;
          foundsegment = true;
          _Value = Value;
        }
      }
    }

    //If foundsegment = false, then we have a problem.  The value passed in
    //was not found in any of the cost segments defined for the variable.
    //Accordingly, we throw an exception.
    if (foundsegment == false) {
      LPException ValueInvalidException = new LPException(VariableName + " = " +
          Value + " is not a valid assignment because " + Value +
          " does not lie within any defined cost segments.");
      _Value = Double.NaN;
      throw ValueInvalidException;
    }
  }

  /**
   * GetValue returns the current value of the variable
   *
   * @return double
   */
  public double GetValue() {
    return _Value;
  }

  /**
   * GetCost returns the current cost associated with a variable that can only
   * go in one direction.  For variables capable of going in two directions,
   * GetCostUpAndDown() should be used instead
   *
   * @return double
   */
  public double GetCost() {
    return _CurrentCost;
  }

  /**
   * GetCostUpAndDown returns two cost values: return[0] is the cost associated
   * with an increase of the variable, and return[1] is the cost associated with
   * a decrease of the variable.  This is primarily used with piecewise cost
   * functions.
   *
   * @return double[]
   */
  public double[] GetCostUpAndDown() {
    return _CurrentCostUpAndDown;
  }

  /**
   * GetBounds returns the left/right bounds for the variable, assuming it can
   * only go in one direction (either up or down exclusively).  For a variable
   * which can go either up or down, use GetBoundsUpAndDown() instead.
   *
   * @return double[]
   */
  public double[] GetBounds() {
    return _CurrentBounds;
  }

  /**
   * GetBoundsUpAndDown returns the bounds on the variable as it moves up or
   * down.  return[0] gives the bounds for the variable as it moves up
   * (return[0][0] is the left bound, return[0][1] is the right bound) and
   * return[1] gives the same information for the variable as it moves down.
   *
   * @return double[][]
   */
  public double[][] GetBoundsUpAndDown() {
    return _CurrentBoundsUpAndDown;
  }

  /**
   * AtLowerBound returns
   *
   * <p><b>true</b> if the variable is currently at a lower bound.</p>
   *
   * <p><b>false</b> if the variable is currently at an upper bound.</p>
   *
   * @return boolean
   */
  public boolean AtLowerBound() {
    return _AtLowerBound;
  }

  private class LPCostSegment
      implements Serializable {
    double LeftEndpoint;
    double RightEndpoint;
    double Cost;

    public LPCostSegment(double LeftEndpoint, double RightEndpoint,
                         double Cost) {
      this.LeftEndpoint = LeftEndpoint;
      this.RightEndpoint = RightEndpoint;
      this.Cost = Cost;
    }
  }
}
