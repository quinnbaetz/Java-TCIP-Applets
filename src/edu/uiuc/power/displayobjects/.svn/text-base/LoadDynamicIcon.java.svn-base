package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.LoadDynamicData;

public class LoadDynamicIcon implements Renderable, Animatable, MouseListener, MouseMotionListener, ImageObserver  {
	
	private class LoadDynamicIconTooltip extends MultilineDisplay {

		DecimalFormat decimalFormat = new DecimalFormat("##0");
		NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
		
		public LoadDynamicIconTooltip() {
			super(0, 0);
		}

		@Override
		protected ArrayList<String> getStrings() {
			ArrayList<String> retStrings = new ArrayList<String>();
			try {
				retStrings.add("Demand: " + textFormatter.valueToString(load.getMWSupplied()) + " MW");
				retStrings.add("Peak: " + textFormatter.valueToString(load.getPeakMW()) + " MW");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retStrings;
		}
		
		
	}
	
	private LoadDynamicIconTooltip loadToolTip;

	private LoadDynamicData load;
	private double coordx, coordy;
	private double width,height;
	private double maxPeak;
	private int fontSize;
	
	public LoadDynamicIcon(LoadDynamicData load, double coordx, double coordy, double width, double maxPeak, int fontSize) {
		super();
		loadToolTip = new LoadDynamicIconTooltip(); 
		this.load = load;
		this.coordx = coordx;
		this.coordy = coordy;
		this.width = width;
		this.maxPeak = maxPeak;
		this.fontSize = fontSize;
	}
	
	private double getPeakPercentage() {
		double retVal = (load.getPeakMW()/maxPeak);
		if (retVal > 1) {
			System.out.println("CapPercent > 1");
		}
		return retVal;
	}
	
	private void setPeakPercentage(double peakPercentage) {
		load.setPeakMW(peakPercentage*maxPeak);
	}
	
	private double getLoadPercentage() {
		return (load.getMW()/load.getPeakMW());
	}
	
	AffineTransform renderXForm,tooltipXForm;
	
	Point2D.Double sliderFromPoint,sliderToPoint;
	private double peakSliderX1, peakSliderX2, peakSliderX;
	
	private Font getTextFont() {
		return new Font("Lucida Bright",Font.PLAIN,fontSize);
	}

	private Rectangle2D getTextBounds(Graphics2D g2d, String text) {
		
		Font backupFont = g2d.getFont();
		
		g2d.setFont(getTextFont());
		Font fontNow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textBound = fontNow.getStringBounds(text, frc); 
		g2d.setFont(backupFont);
		
		return textBound;
	}
	
	private String getDemandString() {
		return "Current Demand:";
	}
	
	private String getPeakString() {
		return "Peak Demand:";
	}
	
	private Rectangle2D getDemandTextBounds(Graphics2D g2d) {
		return getTextBounds(g2d,getDemandString());
	}
	
	private Rectangle2D getPeakTextBounds(Graphics2D g2d) {
		return getTextBounds(g2d,getPeakString());
	}
	
	private double getTextMargin() {
		return 2.0;
	}
	
	
	public boolean render(Graphics2D g2d) {
		Font backupFont = g2d.getFont();
		AffineTransform backupXForm = g2d.getTransform();
		Color backupColor = g2d.getColor();
		Stroke backupStroke = g2d.getStroke();
		Rectangle2D demandTextBounds = getDemandTextBounds(g2d);
		Rectangle2D peakTextBounds = getPeakTextBounds(g2d);
		
		double textHeight = 2*getTextMargin()+Math.max(demandTextBounds.getHeight(),peakTextBounds.getHeight());
		double textWidth = 2*getTextMargin()+Math.max(demandTextBounds.getWidth(),peakTextBounds.getWidth());
		
		height = 2*textHeight;
		
		g2d.setFont(getTextFont());
		
		g2d.translate(coordx-width/2.0,coordy);
		AffineTransform topVertLeftHoriz = g2d.getTransform();
		tooltipXForm = topVertLeftHoriz;
		Rectangle2D containerBox = new Rectangle2D.Double(0,0,width,height);
		g2d.setColor(Color.WHITE);
		g2d.fill(containerBox);
		g2d.setColor(Color.BLACK);
		g2d.draw(containerBox);

		g2d.translate(0,height/2.0);
		AffineTransform centerVertLeftHoriz = g2d.getTransform();
		g2d.setColor(Color.BLACK);
		g2d.translate(textWidth - getTextMargin() - demandTextBounds.getWidth(),-textHeight/2.0+demandTextBounds.getHeight()/4.0);
		g2d.drawString(getDemandString(),0,0);
		g2d.setTransform(centerVertLeftHoriz);
		g2d.translate(textWidth - getTextMargin() - peakTextBounds.getWidth(),textHeight/2.0+peakTextBounds.getHeight()/4.0);
		g2d.drawString(getPeakString(),0,0);
		
		g2d.setTransform(centerVertLeftHoriz);
		g2d.translate(textWidth, 0);
		AffineTransform centerVertAfterTextHoriz = g2d.getTransform();
		double pastTextWidth = width - textWidth;
		g2d.draw(new Line2D.Double(0, 0, pastTextWidth, 0));
		g2d.draw(new Line2D.Double(0,-height/2.0,0,height/2.0));
		
		double graphAndSliderMargin = 2.0;
		g2d.setTransform(centerVertAfterTextHoriz);
		g2d.translate(0, -textHeight/2.0);
		//g2d.fill(new Rectangle2D.Double(graphAndSliderMargin,-textHeight/2.0+graphAndSliderMargin,100,100));
		AffineTransform centerGraphVertPastTextHoriz = g2d.getTransform();
		double graphLeftX = graphAndSliderMargin;
		double graphMaxWidth = pastTextWidth-2*graphAndSliderMargin; 
		double graphBoxWidth = graphMaxWidth*getPeakPercentage();
		double graphFillWidth = graphMaxWidth*getPeakPercentage()*getLoadPercentage();
		Rectangle2D graphFill = new Rectangle2D.Double(graphLeftX,-textHeight/2.0+graphAndSliderMargin,graphFillWidth,textHeight - 2*graphAndSliderMargin);
		g2d.setColor(Color.GREEN);
		g2d.fill(graphFill);
		g2d.setColor(Color.BLACK);
		g2d.draw(graphFill);
		Rectangle2D graphBox = new Rectangle2D.Double(graphLeftX,-textHeight/2.0+graphAndSliderMargin,graphBoxWidth,textHeight - 2*graphAndSliderMargin);
		g2d.setColor(Color.BLACK);
		g2d.draw(graphBox);
		
		g2d.setTransform(centerVertAfterTextHoriz);
		g2d.translate(0, +textHeight/2.0);
		AffineTransform centerSliderVertPastTextHoriz = g2d.getTransform();
		g2d.setColor(Color.BLACK);
		g2d.draw(new Line2D.Double(graphLeftX,0,graphMaxWidth,0));
		
		double sliderOffset = getPeakPercentage()*graphMaxWidth;
		g2d.translate(sliderOffset+2,0);
		renderXForm = g2d.getTransform();
		peakSliderX1 = graphLeftX;
		peakSliderX2 = graphMaxWidth;
		peakSliderX = sliderOffset+2;
		sliderFromPoint = new Point2D.Double(0,-textHeight/2.0+2*graphAndSliderMargin);
		sliderToPoint = new Point2D.Double(0,textHeight/2.0-2*graphAndSliderMargin);
		g2d.setStroke(new BasicStroke(4.0f));
		g2d.draw(new Line2D.Double(0,-textHeight/2.0+2*graphAndSliderMargin,0,textHeight/2.0-2*graphAndSliderMargin));
		
		/*
		Shape outlineRect = new Rectangle2D.Double(0,0,maxWidth + 5,height + 5); 
		g2d.fill(outlineRect);
		g2d.setPaint(Color.BLACK);
		g2d.draw(outlineRect);
		g2d.translate(2.5,0);
		for (int i = 0; i < stringList.size(); i++) {
			Rectangle2D tBounds = textBoundsList.get(i);
			g2d.translate(0, tBounds.getHeight());
			g2d.drawString(stringList.get(i),0,0);
			g2d.translate(0,tBounds.getHeight()*0.25);
		}
		*/
		g2d.setStroke(backupStroke);
		g2d.setTransform(tooltipXForm);
		if (displayTooltip) {
			drawTooltip(g2d);
		}		
		g2d.setTransform(backupXForm);
		g2d.setFont(backupFont);
		g2d.setColor(backupColor);
		return true;		
	}
	/*
	public boolean renderOLD(Graphics2D g2d) {
		AffineTransform origXForm = g2d.getTransform();
		Stroke origStroke = g2d.getStroke();
		g2d.translate(coordx,coordy-height);
		tooltipXForm = g2d.getTransform();
		g2d.setColor(Color.WHITE);
		Rectangle2D containerRect =new Rectangle2D.Double(0,0,width,height); 
		g2d.fill(containerRect);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3.0f));
		g2d.draw(containerRect);
		g2d.setStroke(origStroke);
		
		AffineTransform beforeIcon = g2d.getTransform();
		double iconWidth = 0.5*width;
		double iconHeight = 0.5*width;
		double yOffset = (height - (0.5*width))/2.0;
		g2d.translate(0,yOffset);
		g2d.drawImage(genIconProvider.getGeneratorIcon(genToGeneratorType.get(gen),(int)iconWidth,(int)iconHeight),0,0,this);
		g2d.setTransform(beforeIcon);
		
		double controlBoxesX = width*0.5;
		
		double usageBoxX = controlBoxesX;
		double usageBoxWidth = (width - controlBoxesX)/2.0;
		g2d.translate(usageBoxX,0);
		g2d.draw(new Rectangle2D.Double(0,0,usageBoxWidth,height));
		
		capacitySliderLineY1 = 0.1*height;
		capacitySliderLineY2 = 0.9*height;
		capacitySliderY = capacitySliderLineY2 - (capacitySliderLineY2 - capacitySliderLineY1)*getPeakPercentage();
		
		double usageGraphContainerY1 = capacitySliderY;
		double usageGraphY2 = 0.9*height;
		double usageGraphWidth = 0.75*usageBoxWidth;
		double usageGraphContainerHeight = usageGraphY2 - usageGraphContainerY1;
		double usageGraphY1 = usageGraphY2 - getLoadPercentage()*(usageGraphContainerHeight);
		double usageGraphHeight = usageGraphY2 - usageGraphY1;
		
		g2d.setColor(Color.GREEN);
		g2d.fill(new Rectangle2D.Double(usageBoxWidth/2.0 - usageGraphWidth/2.0, usageGraphY1,usageGraphWidth,usageGraphHeight));
		g2d.setColor(Color.BLACK);
		g2d.draw(new Rectangle2D.Double(usageBoxWidth/2.0 - usageGraphWidth/2.0, usageGraphContainerY1,usageGraphWidth,usageGraphContainerHeight));
		
		
		double capacityBoxX = (controlBoxesX + width)/2.0;
		double capacityBoxWidth = width - capacityBoxX;
		
		//g2d.translate(capacityBoxX,0);
		g2d.translate(capacityBoxX - usageBoxX,0);
		g2d.draw(new Rectangle2D.Double(0, 0, capacityBoxWidth, height));
		double capacitySliderLineX = (width-capacityBoxX)/2.0;
		g2d.draw(new Line2D.Double(capacitySliderLineX,capacitySliderLineY1,capacitySliderLineX,capacitySliderLineY2));
		
		double capacitySliderWidth = capacityBoxWidth*0.75;
		double capacitySliderX1 = capacitySliderLineX - (capacitySliderWidth/4.0);
		double capacitySliderX2 = capacitySliderLineX + (capacitySliderWidth/4.0);
		
		g2d.translate(0,capacitySliderY);
		renderXForm = g2d.getTransform();
		sliderFromPoint = new Point2D.Double(capacitySliderX1,0);
		sliderToPoint = new Point2D.Double(capacitySliderX2,0);
		g2d.setStroke(new BasicStroke(2.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		g2d.draw(new Line2D.Double(capacitySliderX1,0,capacitySliderX2,0));
		
		g2d.setTransform(tooltipXForm);
		if (displayTooltip) {
			drawTooltip(g2d);
		}

		g2d.setStroke(origStroke);
		g2d.setTransform(origXForm);
		return false;
	}
	*/
	
	private void drawTooltip(Graphics2D g2d) {
		g2d.translate(displayTooltipLocation.x, displayTooltipLocation.y);
		loadToolTip.render(g2d);
	}
	
	public boolean animationstep() {
		// TODO Auto-generated method stub
		return false;
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	boolean canDrag = false;
	int dragFromX, dragFromY;
	
	public void mousePressed(MouseEvent e) {
		if (!(renderXForm == null)) {
			Point offset = ((AnimatedPanel)e.getSource()).getLocation();
			Point2D sliderFromDevSpace = null;
			sliderFromDevSpace = renderXForm.transform(sliderFromPoint, sliderFromDevSpace);
			Point2D sliderToDevSpace = null;
			sliderToDevSpace = renderXForm.transform(sliderToPoint, sliderToDevSpace);
			//System.out.println("To node num: " + branch.getToNode().getNum());
			System.out.println("  from (x,y) = (" + sliderFromDevSpace.getX() + "," + 
					sliderFromDevSpace.getY() + "), to (x,y) = (" + sliderToDevSpace.getX() + 
					"," + sliderToDevSpace.getY() + ")");
			double distToClickPoint = DrawUtilities.distanceFromLineToPoint(sliderFromDevSpace, sliderToDevSpace, 
					new Point2D.Double(e.getX() + offset.x,e.getY() + offset.y));
			
			if (distToClickPoint < 5) {
				canDrag = true;
				dragFromX = e.getX();
				dragFromY = e.getY();
				System.out.println("CLICK!!!");
			} else
				canDrag = false;
		}		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent e) {
		if (canDrag) {
			Point offset = ((AnimatedPanel)e.getSource()).getLocation();
			Point2D dragPoint = null;
			try {
				dragPoint = renderXForm.createInverse().transform(new Point2D.Double(e.getX() + offset.x,e.getY() + offset.y),dragPoint);
				System.out.println("Dragpoint = (" + dragPoint.getX() + "," + dragPoint.getY() + ")");
				double xOffset = dragPoint.getX();
				double dragSliderX = peakSliderX + xOffset; 
				System.out.println("DragSliderX = " + dragSliderX);
				//capacitySliderLineY1, capacitySliderLineY2, capacitySliderY
				if (dragSliderX < peakSliderX1)
					dragSliderX = peakSliderX1;
				if (dragSliderX > peakSliderX2)
					dragSliderX = peakSliderX2;
				
				double newPeakPercentage =(dragSliderX - peakSliderX1)/(peakSliderX2 - peakSliderX1);
				System.out.println("new capacity = " + maxPeak*newPeakPercentage);
				setPeakPercentage(newPeakPercentage);
				
			} catch (NoninvertibleTransformException e1) {
				// TODO Auto-generated catch block
				System.out.println("Non-invertible transform");
				e1.printStackTrace();
			} 
		}
	}
	
	boolean displayTooltip = false;
	Point2D.Double displayTooltipLocation = null;
	
	public void mouseMoved(MouseEvent e) {
		if (tooltipXForm != null) {
			displayTooltip = false;
			Point offset = ((AnimatedPanel)e.getSource()).getLocation();
			Point2D dragPoint = null;
			try {
				dragPoint = tooltipXForm.createInverse().transform(new Point2D.Double(e.getX() + offset.x,e.getY() + offset.y),dragPoint);
				//System.out.println("Dragpoint = (" + dragPoint.getX() + "," + dragPoint.getY() + ")");
				//capacitySliderLineY1, capacitySliderLineY2, capacitySliderY
				if ((dragPoint.getY() > 0) & (dragPoint.getY() < height)) {
					//System.out.println("Y success");
					if ((dragPoint.getX() > 0) & (dragPoint.getX() < width)) {
						//System.out.println("X success");
						displayTooltip = true;
						displayTooltipLocation = new Point2D.Double(dragPoint.getX(),dragPoint.getY());
					}
					
				}
				
			} catch (NoninvertibleTransformException e1) {
				// TODO Auto-generated catch block
				System.out.println("Non-invertible transform");
				e1.printStackTrace();
			} 
		}
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}

}
