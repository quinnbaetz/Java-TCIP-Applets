package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.PageAttributes.OriginType;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Timer;

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.Switchable;

public class SwitchDisplay extends SimpleLineDisplay implements Animatable, Switchable, 
	MouseListener {

	double _switchangle; // radians

	double _switchlength;

	Switchable _switchableObject;
	
	double _OffsetAnglePerAnimationStep;
	
	AffineTransform currentxform;
	AffineTransform renderxform;
	Point2D.Double lineFrom;
	Point2D.Double lineTo;
	
	boolean currentDisplayClosed;
	
	boolean _openInOppositeDirection = false;
	
	private boolean getOpenInOppositeDirection() {
		return _openInOppositeDirection;
	}
	
	public SwitchDisplay(Switchable switchableObject, Point2D.Double from, Point2D.Double to,
			double thickness, 
			double offsetAnglePerAnimationStep) {
		this(switchableObject,from,to,thickness,offsetAnglePerAnimationStep,false);
	}
	
	public SwitchDisplay(Switchable switchableObject, Point2D.Double from, Point2D.Double to,
			double thickness,
			double offsetAnglePerAnimationStep,
			boolean openInOppositeDirection) {
		super(from,to,thickness);
		_openInOppositeDirection = openInOppositeDirection;
		currentxform = null;
		renderxform = null;
		lineFrom = null;
		lineTo = null;
		
		_OffsetAnglePerAnimationStep = offsetAnglePerAnimationStep;
		_switchableObject = switchableObject;
		if (_switchableObject.getClosed()) {
			_switchangle = 0;
			currentDisplayClosed = true;
		}
		else {
			currentDisplayClosed = false;
			if (getOpenInOppositeDirection()) 
				_switchangle = - Math.PI / 4;
			else
				_switchangle = Math.PI / 4;
		}
	}
	
	public double getOffsetAnglePerAnimationStep() {
		return _OffsetAnglePerAnimationStep;
	}
	
	public void setOffsetAnglePerAnimationStep(double offsetAnglePerAnimationStep) {
		_OffsetAnglePerAnimationStep = offsetAnglePerAnimationStep;
	}

	public boolean render(Graphics2D g) {
		// Backup transform
		currentxform = g.getTransform();
		Color currentcolor = g.getColor();
		Stroke currentstroke = g.getStroke();

		g.translate(getFromPoint().getX(), getFromPoint().getY());
		//g.setColor(Color.RED);
		g.setColor(Color.BLUE);

		g.rotate(-getSwitchAngle());
		
		renderxform = g.getTransform();
		
		lineFrom = new Point2D.Double(0,0);
		lineTo = new Point2D.Double(getToPoint().x - getFromPoint().x,getToPoint().y - getFromPoint().y);
		
		Line2D.Double linetodraw = new Line2D.Double(lineFrom,lineTo);
		g.setStroke(new BasicStroke((float)getThickness()));
		g.draw(linetodraw);
		
		g.translate(lineTo.getX(),lineTo.getY());
		g.setColor(Color.BLACK);
		g.fill(new Ellipse2D.Double(-5,-5,10,10));

		g.setTransform(currentxform);
		g.setColor(currentcolor);
		g.setStroke(currentstroke);
		return true;
	}
	
	public double getLength() {
		return DrawUtilities.getDistanceBetweenTwoPoints(getFromPoint(), getToPoint());
	}

	public double getSwitchAngle() {
		return _switchangle;
	}

	public void setSwitchAngle(double switchangle) {
		_switchangle = switchangle;
	}
	
	public Switchable getSwitch() {
		return _switchableObject;
	}
	
	public void setSwitch(Switchable switchableObject) {
		_switchableObject = switchableObject;
	}
	
	public void toggleSwitch() {
		if (getCurrentAnimStatus() == NOT_OPENING_OR_CLOSING) {
			if (getClosed() == true)
				open();
			else
				close();
		} else if (getCurrentAnimStatus() == OPENING) {
			close();
		} else if (getCurrentAnimStatus() == CLOSING) {
			open();
		}
	}
	
	public void setSwitchStatus(boolean status) {
		if (getClosed() != status)
			toggleSwitch();
	}
	

	int OPENING = 0;
	int CLOSING = 1;
	int NOT_OPENING_OR_CLOSING = 2;
	
	int currentAnimStatus = NOT_OPENING_OR_CLOSING;
	
	private int getCurrentAnimStatus() {
		return currentAnimStatus; 
	}
	
	private void setCurrentAnimStatus(int value) {
		currentAnimStatus = value;
	}
	
	
	public void open() {
		setClosed(false);
		setCurrentAnimStatus(OPENING);
	}

	public void close() {
		setCurrentAnimStatus(CLOSING);
	}

	public boolean animationstep() {
		boolean retval = false;
		if (getCurrentAnimStatus() == OPENING) {
			if (!getOpenInOppositeDirection()) {
				if (getSwitchAngle() < Math.PI / 4) {
					setSwitchAngle(getSwitchAngle() + getOffsetAnglePerAnimationStep());
					retval = true;
				} else {
					setCurrentAnimStatus(NOT_OPENING_OR_CLOSING);
					currentDisplayClosed = false;
				}
			} else {
				if (getSwitchAngle() > - Math.PI / 4) {
					setSwitchAngle(getSwitchAngle() - getOffsetAnglePerAnimationStep());
					retval = true;
				} else {
					currentDisplayClosed = false;
					setCurrentAnimStatus(NOT_OPENING_OR_CLOSING);
				}
			}
		} else 
		if (getCurrentAnimStatus() == CLOSING) {
			if (!getOpenInOppositeDirection()) {
				if (getSwitchAngle() > 0 ) {
					if ((getSwitchAngle() - getOffsetAnglePerAnimationStep()) < 0) 
						setSwitchAngle(0);
					else
						setSwitchAngle(getSwitchAngle() - getOffsetAnglePerAnimationStep());
					retval = true;
				} else {
					setCurrentAnimStatus(NOT_OPENING_OR_CLOSING);
					setClosed(true);
					currentDisplayClosed = true;
				}				
			} else {
				if (getSwitchAngle() < 0 ) {
					if ((getSwitchAngle() + getOffsetAnglePerAnimationStep()) > 0) 
						setSwitchAngle(0);
					else
						setSwitchAngle(getSwitchAngle() + getOffsetAnglePerAnimationStep());
					retval = true;
				} else {
					setCurrentAnimStatus(NOT_OPENING_OR_CLOSING);
					setClosed(true);
					currentDisplayClosed = true;
				}				
			}
		} else {
			if (currentDisplayClosed != getClosed()) {
				if (getClosed())
					close();
				else
					open();
			}
		}

		return retval;
	}

	public boolean getClosed() {
		return getSwitch().getClosed();
	}

	public void setClosed(boolean closed) {
		getSwitch().setClosed(closed);
	}
	
	public Rectangle2D getBounds() {
		Rectangle2D.Double retrect = new Rectangle2D.Double();
		retrect.x = getFromPoint().getX();
		return null;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		if (!(renderxform == null)) {
			Point offset = ((AnimatedPanel)e.getSource()).getLocation();
			Point2D lineFromDevSpace = null;
			lineFromDevSpace = renderxform.transform(lineFrom, lineFromDevSpace);
			Point2D lineToDevSpace = null;
			lineToDevSpace = renderxform.transform(lineTo, lineToDevSpace);
			Switchable switchable = getSwitch();
			BranchData branch = (BranchData)switchable;
			/*
			System.out.println("To node num: " + branch.getToNode().getNum());
			
			System.out.println("  from (x,y) = (" + lineFromDevSpace.getX() + "," + 
					lineFromDevSpace.getY() + "), to (x,y) = (" + lineToDevSpace.getX() + 
					"," + lineToDevSpace.getY() + ")");
			*/
			double distToClickPoint = DrawUtilities.distanceFromLineToPoint(lineFromDevSpace, lineToDevSpace, new Point2D.Double(e.getX() + offset.x,e.getY() + offset.y));
			/*
			System.out.println("  clickpoint = (" + e.getPoint().x + "," + e.getPoint().y + "), distance to clickpoint = " + 
					distToClickPoint);
			Point2D clickPointDevSpace = null;
			Point2D.Double clickPoint = new Point2D.Double(e.getX(),e.getY());
			clickPointDevSpace = currentxform.transform(clickPoint, clickPointDevSpace);
			
			double distanceToClickPoint = DrawUtilities.distanceFromLineToPoint(lineFromDevSpace, lineToDevSpace, clickPointDevSpace);
			System.out.println("  clickPointDevSpace = (" + clickPointDevSpace.getX() + "," + clickPointDevSpace.getY() + ")");
			
			System.out.println("  clickpoint = (" + clickPointDevSpace.getX() + "," + clickPointDevSpace.getY() + "), distance to clickpoint = " + 
					distToClickPoint);
			*/
			
			if (distToClickPoint < 20) {
				toggleSwitch();
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
