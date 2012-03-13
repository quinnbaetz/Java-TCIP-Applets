package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.BranchOverloadMonitor;
import edu.uiuc.power.dataobjects.BranchOverloadMonitorParameters;
import edu.uiuc.power.dataobjects.Switchable;

public class DrawUtilities {
	
	public static double getImageScaleToFitInBounds(double imageWidth, double imageHeight,
			double boundWidth, double boundHeight) {
		double afWidth = imageWidth;
		double afHeight = imageHeight;
		double tWidth = boundWidth;
		double tHeight = boundHeight;
		
		double afScale;
		
		if ((afWidth > tWidth) | (afHeight > tHeight)) {
			if ((afWidth/tWidth) > (afHeight/tHeight)) {
				afScale = tWidth/afWidth; 
			} else {
				afScale = tHeight/afHeight;
			}
		} else {
			if ((afWidth/tWidth) > (afHeight/tHeight)) {
				afScale = tWidth/afWidth; 
			} else {
				afScale = tHeight/afHeight;
			}
		}		
		
		return afScale;
	}
	
	public static ArrayList<SimpleLineDisplayWithDistanceInfo> createLinesFromPointList(
			ArrayList<Point2D.Double> points, double startDistance, 
			double thickness) {
		
		ArrayList<SimpleLineDisplayWithDistanceInfo> retlines = new ArrayList<SimpleLineDisplayWithDistanceInfo>();
		
		for (int i = 0; i < (points.size() - 1); i++) {
			if (i != 0) 
				retlines.add(new SimpleLineDisplayWithDistanceInfo(points.get(i),points.get(i+1),thickness,retlines.get(i-1).getEndDistance()));
			else
				retlines.add(new SimpleLineDisplayWithDistanceInfo(points.get(i),points.get(i+1),thickness,startDistance));
		}
		
		return retlines;
	}
	
	public static ArrayList<LineAndDistanceInfoProvider> addLineWithSwitchToAnimatedPanel(
			ArrayList<Point2D.Double> points, double startDistance,
			BranchThicknessProvider branchThicknessProvider,
			double switchthickness,
			int switchStartIndex, double offsetAnglePerAnimationStep,
			BranchData branch, BranchColorProvider branchColorProvider,
			AnimatedPanel panelToAddTo,
			boolean reverseSwitchDirection,
			BranchOverloadMonitorParameters monitorParams,
			boolean monitorLine) {
		ArrayList<LineAndDistanceInfoProvider> lines = new ArrayList<LineAndDistanceInfoProvider>();
		double currentEndDistance = startDistance;
		boolean switchPointsAreValid = false;
		Point2D.Double switchPointFrom = null;
		Point2D.Double switchPointTo = null;
		for (int i = 0; i < (points.size() - 1); i++) {
			if (i == switchStartIndex) {
				SwitchDisplayWithDistanceInfo myswitch = null;
				switchPointFrom = points.get(i);
				switchPointTo = points.get(i+1);
				switchPointsAreValid = true;
				if (reverseSwitchDirection)
					myswitch = new SwitchDisplayWithDistanceInfo(branch,points.get(i),points.get(i+1),switchthickness,offsetAnglePerAnimationStep,true,currentEndDistance);
				else 
					myswitch = new SwitchDisplayWithDistanceInfo(branch,points.get(i),points.get(i+1),switchthickness,offsetAnglePerAnimationStep,false,currentEndDistance);
				currentEndDistance = myswitch.getEndDistance();
				panelToAddTo.getMiddleLayerRenderables().add(myswitch);
				panelToAddTo.getAnimatables().add(myswitch);
				panelToAddTo.addMouseListener(myswitch);
				lines.add(myswitch);
			} 
			else {
				SimpleBranchDisplayWithDistanceInfo line = new SimpleBranchDisplayWithDistanceInfo(points.get(i),points.get(i+1),branchThicknessProvider,currentEndDistance,branch,branchColorProvider);
				currentEndDistance = line.getEndDistance();
				panelToAddTo.getMiddleLayerRenderables().add(line);
				lines.add(line);
			}
		}
		if (monitorLine) {
			BranchOverloadMonitor branchOverloadMonitor = new BranchOverloadMonitor(branch,monitorParams);
			if (switchPointsAreValid) {
				Point2D.Double midPointTimesTwo = addPoints(switchPointFrom,switchPointTo);
				Point2D.Double midPoint = scalePoint(midPointTimesTwo,0.5);
				BranchOverloadOutageLabel branchOverloadOutageLabel = new BranchOverloadOutageLabel(midPoint,
						14,Color.WHITE,Color.BLACK,Color.RED,2.0,0,"Outage due to overload",25,25,panelToAddTo.getTopLayerRenderables(),panelToAddTo.getAnimatables());
				branchOverloadMonitor.registerListener(branchOverloadOutageLabel);
			}
			panelToAddTo.getAnimatables().add(branchOverloadMonitor);
		}
		
		return lines;
		
	}
	
	public static ArrayList<LineAndDistanceInfoProvider> addLinesToAnimatedPanel(
			ArrayList<Point2D.Double> points, double startDistance,
			BranchThicknessProvider thicknessProvider, 
			BranchData branch, BranchColorProvider branchColorProvider,
			AnimatedPanel panelToAddTo) {
		ArrayList<LineAndDistanceInfoProvider> lines = new ArrayList<LineAndDistanceInfoProvider>();
		double currentEndDistance = startDistance;
		for (int i = 0; i < (points.size() - 1); i++) {
			SimpleBranchDisplayWithDistanceInfo line = new SimpleBranchDisplayWithDistanceInfo(points.get(i),points.get(i+1),thicknessProvider,currentEndDistance,branch,branchColorProvider);
			currentEndDistance = line.getEndDistance();
			panelToAddTo.getMiddleLayerRenderables().add(line);
			lines.add(line);
		}
		return lines;
	}	
	
	public static ArrayList<LineAndDistanceInfoProvider> addLineWithSwitchToAnimatedPanel(
			ArrayList<Point2D.Double> points, double startDistance,
			BranchThicknessProvider thicknessProvider, 
			double switchthickness,			
			int switchStartIndex, double offsetAnglePerAnimationStep,
			BranchData branch, BranchColorProvider branchColorProvider,
			AnimatedPanel panelToAddTo) {
		return addLineWithSwitchToAnimatedPanel(points, startDistance, thicknessProvider, switchthickness, switchStartIndex, offsetAnglePerAnimationStep, branch, branchColorProvider, panelToAddTo, false, null, false);
	}
	
	public static ArrayList<LineAndDistanceInfoProvider> addLineWithSwitchToAnimatedPanel(
			ArrayList<Point2D.Double> points, double startDistance,
			BranchThicknessProvider thicknessProvider,
			double switchthickness,
			int switchStartIndex, double offsetAnglePerAnimationStep,
			BranchData branch, BranchColorProvider branchColorProvider,
			AnimatedPanel panelToAddTo,
			boolean reverseSwitchDirection) {
		return addLineWithSwitchToAnimatedPanel(points, startDistance, thicknessProvider, switchthickness, switchStartIndex, offsetAnglePerAnimationStep, branch, branchColorProvider, panelToAddTo, reverseSwitchDirection, null, false);
	}	
	
	public static ArrayList<LineAndDistanceInfoProvider> addLineWithSwitchToAnimatedPanel(
			ArrayList<Point2D.Double> points, double startDistance,
			BranchThicknessProvider thicknessProvider,
			double switchthickness,
			int switchStartIndex, double offsetAnglePerAnimationStep,
			BranchData branch, BranchColorProvider branchColorProvider,
			AnimatedPanel panelToAddTo,
			boolean reverseSwitchDirection,
			BranchOverloadMonitorParameters monitorParams) {
		return addLineWithSwitchToAnimatedPanel(points, startDistance, thicknessProvider, switchthickness, switchStartIndex, offsetAnglePerAnimationStep, branch, branchColorProvider, panelToAddTo, reverseSwitchDirection, monitorParams, true);
	}		
	
	public static ArrayList<LineAndDistanceInfoProvider> addLineWithSwitchToAnimatedPanel(
			ArrayList<Point2D.Double> points, double startDistance,
			BranchThicknessProvider thicknessProvider, 
			double switchthickness,			
			int switchStartIndex, double offsetAnglePerAnimationStep,
			BranchData branch, BranchColorProvider branchColorProvider,
			AnimatedPanel panelToAddTo,
			BranchOverloadMonitorParameters monitorParams) {
		return addLineWithSwitchToAnimatedPanel(points, startDistance, thicknessProvider, switchthickness, switchStartIndex, offsetAnglePerAnimationStep, branch, branchColorProvider, panelToAddTo, false, monitorParams, true);
	}		
			
			
	public static double getDistanceBetweenTwoPoints(Point2D.Double from, Point2D.Double to) {
		double retval = 0;
		
		retval += (from.getX() - to.getX())*(from.getX() - to.getX());
		retval += (from.getY() - to.getY())*(from.getY() - to.getY());
		retval = Math.sqrt(retval);
		
		return retval;			
	}
	
	public static Point2D.Double scalePoint(Point2D.Double point, double scale) {
		return new Point2D.Double(scale*point.x,scale*point.y);
	}
	
	public static ArrayList<SimpleLineDisplayWithDistanceInfo> getLineSegmentsCorrespondingToCubicCurve(
			Point2D.Double start,
			Point2D.Double controlPoint1,
			Point2D.Double controlPoint2,
			Point2D.Double end,
			int numofsegments,
			double thickness)
	{
		ArrayList<SimpleLineDisplayWithDistanceInfo> lines = new ArrayList<SimpleLineDisplayWithDistanceInfo>();
		
		// Algorithm taken from http://www.timotheegroleau.com/Flash/articles/cubic_bezier_in_flash.htm
		// want B(u) = alpha*u^3 + beta*u^2 + delta*u + P0
		// where P0 = start, P1 = cp1, P2 = cp2, P3 = end
		// u goes from 0 to 1
		
		// delta = 3*(P1 - P0)
		Point2D.Double delta = scalePoint(getDifferenceAMinusB(controlPoint1, start),3);
		
		// beta = 3*(P2 - P1) - delta
		Point2D.Double beta = scalePoint(getDifferenceAMinusB(controlPoint2, controlPoint1),3);
		beta = getDifferenceAMinusB(beta, delta);
		
		// alpha = P3 - P0 - delta - beta
		Point2D.Double alpha = getDifferenceAMinusB(end, start);
		alpha = getDifferenceAMinusB(alpha, delta);
		alpha = getDifferenceAMinusB(alpha, beta);
		
		double upersegment = 1.0/(double)numofsegments;
		
		Point2D.Double from = null;
		Point2D.Double to = start;
		double currentdistance = 0;
		for (int i = 0; i < numofsegments; i++) {
			from = to;
			double uval = (i + 1)*upersegment;
			to = addPoints(scalePoint(alpha, uval*uval*uval),
					scalePoint(beta,uval*uval));
			to = addPoints(to,scalePoint(delta,uval));
			to = addPoints(to,start);
			lines.add(new SimpleLineDisplayWithDistanceInfo(from,to,thickness,currentdistance));
			currentdistance += from.distance(to);
		}
		return lines;
	}
	public static void drawFlowArrowWithBaseAndRotation(Graphics2D g, Point2D.Double baseCenter,
			double altitude, double angleInRads, double baselength,
			Color fillcolor, Color outlinecolor) {
		AffineTransform backupxform = g.getTransform();
		Color backupcolor = g.getColor();
		
		g.translate(baseCenter.getX(),baseCenter.getY());
		g.rotate(angleInRads);
		Point2D.Double triPoint1 = new Point2D.Double(-baselength/2,0);
		Point2D.Double triPoint2 = new Point2D.Double(0,-altitude);
		Point2D.Double triPoint3 = new Point2D.Double(+baselength/2,0);
		Point2D.Double indentPoint = new Point2D.Double(0,-altitude/4);
		
		GeneralPath mypath = new GeneralPath();
		mypath.append(new Line2D.Double(triPoint1,triPoint2), false);
		mypath.append(new Line2D.Double(triPoint2,triPoint3), true);
		mypath.append(new Line2D.Double(triPoint3,indentPoint), true);
		mypath.append(new Line2D.Double(indentPoint,triPoint1), true);
		g.setColor(fillcolor);
		g.fill(mypath);
		g.setColor(outlinecolor);
		g.draw(mypath);
		
		g.setColor(backupcolor);
		g.setTransform(backupxform);
	}
	
	public static void drawFlowArrowWithBaseAndTip(Graphics2D g, Point2D.Double baseCenter,
			Point2D.Double tip, double baselength, Color fillcolor, Color outlinecolor) {
		// Convert it to a flowarrow with base and rotation
		Point2D.Double tipMinusBase = getDifferenceAMinusB(tip, baseCenter);
		double angleInRads = Math.atan2(tipMinusBase.y, tipMinusBase.x);
		angleInRads += Math.PI/2;
		double altitude = baseCenter.distance(tip);
		drawFlowArrowWithBaseAndRotation(g, baseCenter, altitude, angleInRads, baselength, fillcolor, outlinecolor);
	}
	
	
	
	public static double getTriangleDrawAngleOnLine(LineDisplay line) {
		double angle = Math.atan2(line.getToPoint().getY() - line.getFromPoint().getY(),
				line.getToPoint().getX() - line.getFromPoint().getX());
		angle += Math.PI/2;
		if (angle > 2*Math.PI)
			angle -= 2*Math.PI;
		return angle;
	}
	
	public static Point2D.Double getPointAtDistanceOnLine(LineDisplay line, double distance) {
		double totalLineLength = line.getLength();
		double percentDist = distance/totalLineLength;
		double xVal = (1-percentDist)*line.getFromPoint().getX() + percentDist*line.getToPoint().getX();
		double yVal = (1-percentDist)*line.getFromPoint().getY() + percentDist*line.getToPoint().getY();
		return new Point2D.Double(xVal,yVal);
	}
	
	public static Point2D.Double getPointAtDistanceOnLines(double dol, ArrayList<LineAndDistanceInfoProvider> lines) {
		Point2D.Double retPoint = null;
		boolean foundLine = false;
		for (int j = 0; j < lines.size(); j++) {
			LineAndDistanceInfoProvider line = lines.get(j);
			if (line.getStartDistance() <= dol & line.getEndDistance() >= dol) {
				foundLine = true;
				retPoint = DrawUtilities.getPointAtDistanceOnLine(line, dol - line.getStartDistance());				
			}
		}
		if (foundLine)
			return retPoint;
		else
			return null;
	}	
	
	// based on: http://geometryalgorithms.com/Archive/algorithm_0106/algorithm_0106.htm
	
	public static double distanceFromLineToLine(Point2D line1From, Point2D line1To, Point2D line2From, Point2D line2To) {
		double ux = line1To.getX() - line1From.getX();
		double uy = line1To.getY() - line1From.getY();
		
		double vx = line2To.getX() - line2From.getX();
		double vy = line2To.getY() - line2From.getY();
		
		double wx = line1From.getX() - line2From.getX();
		double wy = line1From.getY() - line2From.getY();
		
		double a = ux*ux + uy*uy;
		double b = ux*vx + uy*vy;
		double c = vx*vx + vy*vy;
		double d = ux*wx + uy*wy;
		double e = vx*wx + vy*wy;
		double D = a*c - b*b;
		double sc = D;
		double sN = D;
		double sD = D;
		double tc = D;
		double tN = D;
		double tD = D;
		
		double SMALL_NUM = 1e-20;
		
		if (D < SMALL_NUM) {
			sN = 0.0;
			sD = 1.0;
			tN = e;
			tD = c;
		} else {
			sN = (b*e - c*d);
			tN = (a*e - b*d);
			if (sN < 0.0) {
				sN = 0.0;
				tN = e;
				tD = c;
			} else if (sN > sD) {
				sN = sD;
				tN = e + b;
				tD = c;
			} 
		}
		
		if (tN < 0.0) {
			tN = 0.0;
			if (-d < 0.0)
				sN = 0.0;
			else if (-d > a) 
				sN = sD;
			else {
				sN = -d;
				sD = a;
			}
		} else if (tN > tD) {
			tN = tD;
			if ((-d + b) < 0.0)
				sN = 0;
			else if ((-d + b) > a)
				sN = sD;
			else {
				sN = (-d + b);
				sD = a;
			}
		}
		
		sc = (Math.abs(sN) < SMALL_NUM ? 0.0 : sN / sD);
		tc = (Math.abs(tN) < SMALL_NUM ? 0.0 : tN / tD);
		
		double dPx = wx + (sc * ux) - (tc * vx);
		double dPy = wy + (sc * uy) - (tc * vy);
		
		return Math.sqrt(dPx*dPx + dPy*dPy);
	}

	
	public static double distanceFromLineToPoint(Point2D lineFrom, Point2D lineTo, Point2D checkPoint) {
		/*
		 *  
		 *  Algorithm taken from http://softsurfer.com/Archive/algorithm_0102/algorithm_0102.htm
			distance( Point P, Segment P0:P1 )
			{
			      v = P1 - P0
			      w = P - P0
			      if ( (c1 = w·v) <= 0 )
			            return d(P, P0)
			      if ( (c2 = v·v) <= c1 )
			            return d(P, P1)
			      b = c1 / c2
			      Pb = P0 + bv
			      return d(P, Pb)
			}
		 */
		
		double distance = 0;

		Point2D p0 = lineFrom;
		Point2D p1 = lineTo;

		Point2D p = checkPoint;
		
		double vx = p1.getX() - p0.getX();
		double vy = p1.getY() - p0.getY();
		
		double wx = p.getX() - p0.getX();
		double wy = p.getY() - p0.getY();
		
		double c1 = vx*wx + vy*wy;
		double c2 = vx*vx + vy*vy;
		
		if (c1 <= 0) {
			distance = p.distance(p0);
		} else if (c2 <= c1) {
			distance = p.distance(p1);
		} else {
			double b = c1 / c2;
			Point2D pb = new Point2D.Double(p0.getX() + b*vx,p0.getY() + b*vy);
			
			distance = p.distance(pb);
		}
			
		return distance;
	}
	
	public static Point2D.Double getDifferenceAMinusB(Point2D.Double A, Point2D.Double B) {
		
		try {
			Point2D.Double retPoint = new Point2D.Double(A.x - B.x,A.y - B.y);
			return retPoint;
		} catch (NullPointerException npe) {
			System.out.println("null pointer exception");
		}
		return null;
	}
	
	public static Point2D.Double addPoints(Point2D.Double A, Point2D.Double B) {
		return new Point2D.Double(A.x + B.x, A.y + B.y);
	}
}
