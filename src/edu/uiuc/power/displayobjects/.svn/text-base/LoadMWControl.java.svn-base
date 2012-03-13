package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;

import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class LoadMWControl extends DataLabel implements MouseListener {
	
	private double MAX_MW = 10000.0;
	private double MIN_MW = 0.0;
	
	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.CENTER;
	}

	private LoadData _load;
	private double deltaPerMouseClick;
	private boolean _arrowsDrawn;
	private double HEIGHT_OFFSET_SCALE = 16.0;
	
	private boolean getArrowsDrawn() {
		return _arrowsDrawn;
	}
	
	private void setArrowsDrawn(boolean arrowsDrawn) {
		_arrowsDrawn = arrowsDrawn;
	}
 
	public LoadMWControl(Point2D ULCorner, int textsize, Color textcolor, double rotationAngle,
			LoadData load, double deltaPerMouseClick) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		_load = load;
		this.deltaPerMouseClick = deltaPerMouseClick;
	}
	
	public LoadData getLoad() {
		return _load;
	}
	
	protected boolean displayControlArrows() {
		return true;
	}

	AffineTransform renderXForm;
	Point2D ULCornerScreenSpace,ULCornerOfBottomArrow,BRCornerScreenSpace,BRCornerOfTopArrow;
	@Override
	public boolean render(Graphics2D g2d) {
		if (getLoad().getConnected())
			setTextColor(Color.BLACK);
		else
			setTextColor(Color.LIGHT_GRAY);
		// TODO Auto-generated method stub
		boolean retval = super.render(g2d);
		
		if (getLoad().getConnected()) {
			if (displayControlArrows() & (deltaPerMouseClick > 1e-8)) {
		    	AffineTransform origXForm = g2d.getTransform();
		    	g2d.translate(getULCorner().getX(), getULCorner().getY());
		    	g2d.translate(getWidth()/2.0 + getWidth()/20.0,getHeight()/1.5);
		    	this.renderXForm = g2d.getTransform();
		    	g2d.setColor(Color.BLACK);
		    	Rectangle2D.Double outlineRect = new Rectangle2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE,getHeight()/2.0,getHeight()/2.1);
		    	g2d.draw(outlineRect);
		    	BRCornerScreenSpace = g2d.getTransform().transform(new Point2D.Double(getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE+getHeight()/2.1), null); 
		    	ULCornerOfBottomArrow = g2d.getTransform().transform(new Point2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE),null);

		    	GeneralPath mypath = new GeneralPath();
		    	mypath.append(new Line2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE,getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE), true);
		    	mypath.append(new Line2D.Double(getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE,getHeight()/4.0,getHeight()/2.1 ), true);
		    	mypath.append(new Line2D.Double(getHeight()/4.0,getHeight()/2.1,0,getHeight()/HEIGHT_OFFSET_SCALE), true);
		    	
		    	if ((getLoad().getMW() - MIN_MW) > (getDeltaPerMouseClick() - 0.1)) // -1 to handle floating point inaccuracy
		    		g2d.setColor(Color.BLACK);
		    	else
		    		g2d.setColor(new Color(0.8f,0.8f,0.8f));
		    	g2d.fill(mypath);
		    	
		    	g2d.scale(1, -1);

		    	g2d.setColor(Color.BLACK);
		    	g2d.draw(outlineRect);
		    	ULCornerScreenSpace = g2d.getTransform().transform(new Point2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE+getHeight()/2.1), null); 
		    	BRCornerOfTopArrow = g2d.getTransform().transform(new Point2D.Double(getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE), null);
		    	
		    	if ((MAX_MW - getLoad().getMW()) > (getDeltaPerMouseClick() - 0.1)) // - 1 to handle floating point inaccuracy
		    		g2d.setColor(Color.BLACK);
		    	else
		    		g2d.setColor(new Color(0.8f,0.8f,0.8f));
		    	g2d.fill(mypath);
		    	g2d.setTransform(origXForm);
		    	
		    	setArrowsDrawn(true);
			} else {
				setArrowsDrawn(false);
			}
		} else {
			setArrowsDrawn(false);
		}
		return retval;
	}	

	@Override
	public String getCaption() {
		
		try {
			if (getLoad().getConnected())
				return textFormatter.valueToString(getLoad().getMW()) + " MW";
			else
				//return textFormatter.valueToString(0) + " MW";
				return "Blackout";
		} catch (ParseException pe) {
			return pe.toString();
		}	
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
	
	private void changeLoadAmount(double deltaMWAmount) {
		LoadData load = getLoad();
		double newMW = load.getMW() + deltaMWAmount;
		if ((newMW <= MAX_MW) & (newMW >= MIN_MW)) 
			load.setMW(newMW);
	}
	
	/*
	private void changeGenerationAmount(double deltaMWAmount) {
		GeneratorData gen = getGenerator();
		if (!gen.getConnectNode().getIsSlack()) {
			double newMW = gen.getMW() + deltaMWAmount;
			if ((newMW <= gen.getMaxMW()) & (newMW >= gen.getMinMW())) 
				gen.setMW(newMW);
		}
	}
	*/
	
	public double getDeltaPerMouseClick() {
		return deltaPerMouseClick;
	}

	public void mousePressed(MouseEvent e) {
		if (getArrowsDrawn()) {
			if (renderXForm != null) {
				double LeftX = ULCornerScreenSpace.getX();
				double RightX = BRCornerScreenSpace.getX();
				
				double TopYBottomArrow = ULCornerOfBottomArrow.getY();
				double BottomYBottomArrow = BRCornerScreenSpace.getY();
				double BottomYTopArrow = BRCornerOfTopArrow.getY();
				double TopYTopArrow = ULCornerScreenSpace.getY();
				int eX = e.getX();
				int eY = e.getY();
				
				double tolerance = 0.1;
				
				if ((eX >= (LeftX - tolerance)) & (eX <= (RightX + tolerance))) {
					if ((eY >= (TopYBottomArrow - tolerance)) & (eY <= (BottomYBottomArrow + tolerance))) {
						changeLoadAmount(-getDeltaPerMouseClick());	
					} else if ((eY >= (TopYTopArrow - tolerance)) & (eY <= (BottomYTopArrow + tolerance))) {
						changeLoadAmount(+getDeltaPerMouseClick());
					}
				}
				
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}

