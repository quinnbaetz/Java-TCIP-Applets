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

import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class GeneratorMWControl extends DataLabel implements MouseListener {
	
	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.CENTER;
	}

	private GeneratorData _generator;
	private double _deltaPerMouseClick;
	private boolean _arrowsDrawn;
	private double HEIGHT_OFFSET_SCALE = 16.0;
	
	private boolean getArrowsDrawn() {
		return _arrowsDrawn;
	}
	
	private void setArrowsDrawn(boolean arrowsDrawn) {
		_arrowsDrawn = arrowsDrawn;
	}
 
	public GeneratorMWControl(Point2D ULCorner, int textsize, Color textcolor, double rotationAngle,
			GeneratorData generator, double deltaPerMouseClick) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		_generator = generator;
		_deltaPerMouseClick = deltaPerMouseClick;
	}
	
	public GeneratorData getGenerator() {
		return _generator;
	}
	
	AffineTransform renderXForm;
	Point2D ULCornerScreenSpace,ULCornerOfBottomArrow,BRCornerScreenSpace,BRCornerOfTopArrow;
	@Override
	public boolean render(Graphics2D g2d) {
		if (getGenerator().getConnected())
			setTextColor(Color.BLACK);
		else
			setTextColor(Color.LIGHT_GRAY);
		// TODO Auto-generated method stub
		boolean retval = super.render(g2d);
		
		if (getGenerator().getConnected() & (getDeltaPerMouseClick() > 1e-10)) {
	    	AffineTransform origXForm = g2d.getTransform();
	    	g2d.translate(getULCorner().getX(), getULCorner().getY());
	    	g2d.translate(getWidth()/2.0 + getWidth()/20.0,getHeight()/1.5);
	    	renderXForm = g2d.getTransform();
	    	
	    	g2d.setColor(Color.BLACK);
	    	Rectangle2D.Double outlineRect = new Rectangle2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE,getHeight()/2.0,getHeight()/2.1);
	    	g2d.draw(outlineRect);
	    	BRCornerScreenSpace = g2d.getTransform().transform(new Point2D.Double(getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE+getHeight()/2.1), null); 
	    	ULCornerOfBottomArrow = g2d.getTransform().transform(new Point2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE),null);
    		
	    	GeneralPath mypath = new GeneralPath();
	    	mypath.append(new Line2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE,getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE), true);
	    	mypath.append(new Line2D.Double(getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE,getHeight()/4.0,getHeight()/2.1 ), true);
	    	mypath.append(new Line2D.Double(getHeight()/4.0,getHeight()/2.1,0,getHeight()/HEIGHT_OFFSET_SCALE), true);
	    	if ((getGenerator().getMW() - getGenerator().getMinMW()) > (getDeltaPerMouseClick() - 1)) // -1 to handle floating point inaccuracy
	    		g2d.setColor(Color.BLACK);
	    	else
	    		g2d.setColor(new Color(0.8f,0.8f,0.8f));
	    	g2d.fill(mypath);
	    	
	    	g2d.scale(1, -1);

	    	g2d.setColor(Color.BLACK);
	    	g2d.draw(outlineRect);
	    	ULCornerScreenSpace = g2d.getTransform().transform(new Point2D.Double(0,getHeight()/HEIGHT_OFFSET_SCALE+getHeight()/2.1), null); 
	    	BRCornerOfTopArrow = g2d.getTransform().transform(new Point2D.Double(getHeight()/2.0,getHeight()/HEIGHT_OFFSET_SCALE), null);
	    		
	    	if ((getGenerator().getMaxMW() - getGenerator().getMW()) > (getDeltaPerMouseClick() - 1)) // - 1 to handle floating point inaccuracy
	    		g2d.setColor(Color.BLACK);
	    	else
	    		g2d.setColor(new Color(0.8f,0.8f,0.8f));
	    	g2d.fill(mypath);
	    	g2d.setTransform(origXForm);
	    	
	    	setArrowsDrawn(true);
		} else {
			setArrowsDrawn(false);
		}
		
		return retval;
	}	

	@Override
	public String getCaption() {
		
		try {
			if (getGenerator().getConnected())
				return textFormatter.valueToString(getGenerator().getMW()) + " MW";
			else
				//return textFormatter.valueToString(0) + " MW";
				return "Offline";
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
	
	private void changeGenerationAmount(double deltaMWAmount) {
		GeneratorData gen = getGenerator();
		if (!gen.getConnectNode().getIsSlack()) {
			double newMW = gen.getMW() + deltaMWAmount;
			if ((newMW <= gen.getMaxMW()) & (newMW >= gen.getMinMW())) 
				gen.setMW(newMW);
		}
	}
	
	public double getDeltaPerMouseClick() {
		return _deltaPerMouseClick;
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
						changeGenerationAmount(-getDeltaPerMouseClick());	
					} else if ((eY >= (TopYTopArrow - tolerance)) & (eY <= (BottomYTopArrow + tolerance))) {
						changeGenerationAmount(+getDeltaPerMouseClick());
					}
				}
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}

