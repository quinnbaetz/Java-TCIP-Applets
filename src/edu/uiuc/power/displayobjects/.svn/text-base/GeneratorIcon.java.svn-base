package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

import edu.uiuc.power.dataobjects.BranchData;
import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.dataobjects.GeneratorDataWithLinearCostAndEmissions;
import edu.uiuc.power.dataobjects.GeneratorType;
import edu.uiuc.power.dataobjects.Switchable;
import edu.uiuc.power.dataobjects.WindGeneratorData;
import edu.uiuc.power.dataobjects.WindGeneratorDataWithLinearCostAndEmissions;

public class GeneratorIcon implements Renderable, Animatable, MouseListener, MouseMotionListener, ImageObserver  {
	
	private class GeneratorIconTooltip extends MultilineDisplay {

		DecimalFormat decimalFormat = new DecimalFormat("##0");
		NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
		
		public GeneratorIconTooltip() {
			super(0, 0);
		}

		@Override
		protected ArrayList<String> getStrings() {
			ArrayList<String> retStrings = new ArrayList<String>();
			try {
				retStrings.add("Output: " + textFormatter.valueToString(gen.getMW()) + " MW");
				retStrings.add("Capacity: " + textFormatter.valueToString(gen.getMaxMW()) + " MW");
				if (gen instanceof GeneratorDataWithLinearCostAndEmissions) {
					GeneratorDataWithLinearCostAndEmissions genCostEmit = (GeneratorDataWithLinearCostAndEmissions)gen;
					retStrings.add("Cost: $" + textFormatter.valueToString(genCostEmit.getCost(1)) + "/hr");
					String emitString = textFormatter.valueToString(genCostEmit.getEmissions(1));
					retStrings.add("Emissions: " + emitString + " " + (emitString.compareTo("1") == 0 ? "ton" : "tons")+ " CO2/hr");
				} else if (gen instanceof WindGeneratorData) {
					WindGeneratorData windData = (WindGeneratorData)gen;
					double curtailVal = windData.getMaxMW() - windData.getMW();
					curtailVal = (curtailVal > 0 ? curtailVal : 0);
					retStrings.add("Curtailed: " + textFormatter.valueToString(curtailVal) + " MW");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return retStrings;
		}
		
		
	}

	private GeneratorData gen;
	private double coordx, coordy;
	private double width, height;
	private GeneratorIconProvider genIconProvider;
	HashMap<GeneratorData,GeneratorType> genToGeneratorType;
	private GeneratorIconTooltip genToolTip;
	
	public GeneratorIcon(GeneratorData gen, double coordx, double coordy, double width, double maxCapacity, GeneratorIconProvider genIconProvider,
			HashMap<GeneratorData,GeneratorType> genToGeneratorType) {
		super();
		genToolTip = new GeneratorIconTooltip(); 
		this.gen = gen;
		this.coordx = coordx;
		this.coordy = coordy;
		this.width = width;
		this.height = width;
		this.genIconProvider = genIconProvider;
		this.genToGeneratorType = genToGeneratorType;
		this.maxCapacity = maxCapacity;
		
	}
	
	private double maxCapacity;
	
	private double getCapacityPercentage() {
		double retVal = (gen.getMaxMW())/maxCapacity;
		if (retVal > 1) {
			System.out.println("CapPercent > 1");
		}
		return retVal;
	}
	
	private void setCapacityPercentage(double capacityPercentage) {
		gen.setMaxMW(capacityPercentage*maxCapacity);
	}
	
	private double getUsagePercentage() {
		return (gen.getMW()/gen.getMaxMW());
	}
	
	AffineTransform renderXForm,tooltipXForm;
	
	Point2D.Double sliderFromPoint,sliderToPoint;
	private double capacitySliderLineY1, capacitySliderLineY2, capacitySliderY; 
	
	public boolean render(Graphics2D g2d) {
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
		capacitySliderY = capacitySliderLineY2 - (capacitySliderLineY2 - capacitySliderLineY1)*getCapacityPercentage();
		
		double usageGraphContainerY1 = capacitySliderY;
		double usageGraphY2 = 0.9*height;
		double usageGraphWidth = 0.75*usageBoxWidth;
		double usageGraphContainerHeight = usageGraphY2 - usageGraphContainerY1;
		double usageGraphY1 = usageGraphY2 - getUsagePercentage()*(usageGraphContainerHeight);
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

	private void drawTooltip(Graphics2D g2d) {
		g2d.translate(displayTooltipLocation.x, displayTooltipLocation.y);
		genToolTip.render(g2d);
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
			/*
			System.out.println("  from (x,y) = (" + sliderFromDevSpace.getX() + "," + 
					sliderFromDevSpace.getY() + "), to (x,y) = (" + sliderToDevSpace.getX() + 
					"," + sliderToDevSpace.getY() + ")");
					*/
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
				//System.out.println("Dragpoint = (" + dragPoint.getX() + "," + dragPoint.getY() + ")");
				double yOffset = dragPoint.getY();
				double dragSliderY = capacitySliderY + yOffset; 
				//capacitySliderLineY1, capacitySliderLineY2, capacitySliderY
				if (dragSliderY < capacitySliderLineY1)
					dragSliderY = capacitySliderLineY1;
				if (dragSliderY > capacitySliderLineY2)
					dragSliderY = capacitySliderLineY2;
				
				double newCapPercentage =(capacitySliderLineY2 - dragSliderY)/(capacitySliderLineY2 - capacitySliderLineY1); 
				setCapacityPercentage(newCapPercentage);
				
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
