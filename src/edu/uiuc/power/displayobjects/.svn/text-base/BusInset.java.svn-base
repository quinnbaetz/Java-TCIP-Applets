package edu.uiuc.power.displayobjects;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.dataobjects.GeneratorType;
import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.dataobjects.LoadDynamicData;
import edu.uiuc.power.dataobjects.NodeData;


public class BusInset implements Renderable, MouseMotionListener, MouseListener, Animatable {

	double coordx = 5;
	double coordy = 410;
	double width = 400;
	double height = 150;
	double busWidth = 2.0*width/3.0;
	double busThickness = 20.0;
	int busLabelPointSize = 10;
	
	ArrayList<GeneratorIcon> genIcons;
	
	NodeData currentNode;
	
	HashMap<NodeData,ArrayList<BranchCoordsAndData>> nodeToLinesMap;
	HashMap<NodeData,Point2D.Double> nodeToNodeDisplayMap;
	HashMap<GeneratorData,GeneratorType> genToGeneratorType;
	GeneratorIconProvider genIconProvider;
	
	BranchColorProvider bcProvider;
	BranchThicknessProvider btProvider;
	FlowArrowColorProvider facProvider;	
	
	BusNameDataLabel busNameLabel;
	
	public BusInset(NodeData currentNode,
			HashMap<NodeData, ArrayList<BranchCoordsAndData>> nodeToLinesMap,
			HashMap<NodeData,Point2D.Double> nodeToNodeDisplayMap,
			HashMap<GeneratorData,GeneratorType> genToGeneratorType,
			GeneratorIconProvider genIconProvider,
			BranchColorProvider bcProvider,
			BranchThicknessProvider btProvider,
			FlowArrowColorProvider facProvider) {
		super();
		this.currentNode = currentNode;
		this.nodeToLinesMap = nodeToLinesMap;
		this.nodeToNodeDisplayMap = nodeToNodeDisplayMap;
		this.genToGeneratorType = genToGeneratorType;
		this.genIconProvider = genIconProvider;
		this.bcProvider = bcProvider;
		this.btProvider = btProvider;
		this.facProvider = facProvider;
		this.busNameLabel = new BusNameDataLabel(new Point2D.Double(5,0),busLabelPointSize,Color.BLACK,0);
		genIcons = new ArrayList<GeneratorIcon>();
	}	

	private class BusNameDataLabel extends DataLabel {

		public BusNameDataLabel(Point2D ULCorner, int textsize,
				Color textcolor, double rotationAngle) {
			super(ULCorner, textsize, textcolor, rotationAngle);
		}

		@Override
		public DataLabelHorizontalAlignment getHorizontalAlignment() {
			return DataLabelHorizontalAlignment.LEFT;
		}
		
		

		@Override
		protected boolean drawBackground() {
			return true;
		}

		@Override
		protected Color getBackgroundColor() {
			return Color.WHITE;
		}

		@Override
		protected boolean drawBorder() {
			return true;
		}

		@Override
		protected Color getBorderColor() {
			return Color.BLACK;
		}

		@Override
		public String getCaption() {
			return currentNode.getName();
		}
		
	}
	
	ArrayList<SimpleBranchDisplayWithDistanceInfo> displayLines; 
	ArrayList<FlowArrowsForBranchData> flowArrowList;
	
	LoadDynamicIcon loadIcon;
	
	public void setCurrentNode(NodeData currentNode) {
		this.currentNode = currentNode;
		Point2D.Double nodeCoord = nodeToNodeDisplayMap.get(currentNode);
		double nodeDisplayX = nodeCoord.x;
		double nodeDisplayY = nodeCoord.y;
		
		genIcons.clear();
		ArrayList<GeneratorData> gens = currentNode.getAttachedGenerators();
		double numGens = gens.size();
		double interIconSpace = 5;
		double iconWidth = (busWidth - interIconSpace*(numGens - 1))/numGens;
		if (iconWidth > (2*height/3) - (busThickness/2.0) - busLabelPointSize) {
			iconWidth = (2*height/3) - (busThickness/2.0) - busLabelPointSize;
		} 
		double totalIconsWidth = iconWidth*numGens + interIconSpace*(numGens-1);
		double firstIconLocationX = -totalIconsWidth/2;
		for (int i = gens.size() - 1; i > -1; i--) {
			GeneratorIcon genIcon = new GeneratorIcon(gens.get(i),firstIconLocationX + (iconWidth + 5)*i,-busThickness/2,iconWidth,currentNode.peakGen,
					genIconProvider,genToGeneratorType);
			genIcons.add(genIcon);
		}
		
		ArrayList<LoadData> loads = currentNode.getAttachedLoads();
		if (loads.size() > 0) {
			LoadData oldLoad = loads.get(0);
			if (oldLoad instanceof LoadDynamicData) {
				System.out.println("BusInset: LoadDynamicData found");
				loadIcon = new LoadDynamicIcon((LoadDynamicData)oldLoad,0,busThickness/2,busWidth,currentNode.peakLoad,11);
			} else {
				getCurrentNode().removeAttachedLoad(oldLoad);
				LoadDynamicData lDyn = new LoadDynamicData(getCurrentNode(),oldLoad.getMW(),oldLoad.getMVar(),oldLoad.getConnected());
				loadIcon = new LoadDynamicIcon(lDyn,0,busThickness/2,busWidth,10000.0,11);
			}
		} else
			loadIcon = null;
		
		displayLines = new ArrayList<SimpleBranchDisplayWithDistanceInfo>();
		flowArrowList = new ArrayList<FlowArrowsForBranchData>();
		
		ArrayList<BranchCoordsAndData> lines = nodeToLinesMap.get(getCurrentNode());
		if (lines != null) {
			for (int i = 0; i < lines.size(); i++) {
				ArrayList<Point2D.Double> lineCoords = lines.get(i).getCoords();
				int j = 0;
				boolean endPointAtNode = false;
				boolean pointsTooCloseLastIteration = false;
				while (j < (lineCoords.size() - 1) & !(endPointAtNode)) {
					Point2D.Double fromPointOrig = lineCoords.get(j);
					Point2D.Double toPointOrig = lineCoords.get(j+1);
					double scaleOut = 20;
					Point2D.Double fromPoint = null;
					Point2D.Double toPoint = null; 
					
					if (!((nodeCoord.distance(fromPointOrig) < 5) & (nodeCoord.distance(toPointOrig) < 5))) {

						if (nodeCoord.distance(fromPointOrig) < 5) { 
							fromPoint = new Point2D.Double(0,0);
							if (j == 0 | pointsTooCloseLastIteration)
								endPointAtNode = true;
						}
						else 
							fromPoint = new Point2D.Double(scaleOut*(fromPointOrig.x-nodeDisplayX),scaleOut*(fromPointOrig.y-nodeDisplayY));

						if (nodeCoord.distance(toPointOrig) < 5) { 
							toPoint = new Point2D.Double(0,0);
							endPointAtNode = true;
						}
						else 
							toPoint = new Point2D.Double(scaleOut*(toPointOrig.x-nodeDisplayX),scaleOut*(toPointOrig.y-nodeDisplayY));

						
						if (endPointAtNode) {
							SimpleBranchDisplayWithDistanceInfo sbranch = new SimpleBranchDisplayWithDistanceInfo(fromPoint,toPoint,
									btProvider,0,lines.get(i).getBranchData(),bcProvider);
							displayLines.add(sbranch);
							ArrayList<LineAndDistanceInfoProvider> singleLineList = new ArrayList<LineAndDistanceInfoProvider>();
							singleLineList.add(sbranch);
							FlowArrowsForBranchData fArrows = new FlowArrowsForBranchData(lines.get(i).getBranchData(),singleLineList,
									0.06,40.0,5.0,facProvider);
							flowArrowList.add(fArrows);
						}
						pointsTooCloseLastIteration = false;
					} else
						pointsTooCloseLastIteration = true;
					
					j++;
				}
			}		
		}
	}
	
	public NodeData getCurrentNode() {
		return currentNode;
	}
	
	public boolean render(Graphics2D g2d) {
		Color backupColor = g2d.getColor();
		Stroke backupStroke = g2d.getStroke();
		AffineTransform backupXForm = g2d.getTransform();
		Point2D.Double nodeCoord = nodeToNodeDisplayMap.get(getCurrentNode());
		
		
		double highlightWidth = width/8.0;
		double highlightHeight = height/8.0;
		g2d.translate(nodeCoord.x, nodeCoord.y);
		Rectangle2D.Double nodeHighlightRect = new Rectangle2D.Double(-highlightWidth/2,
				-highlightHeight/2,highlightWidth,highlightHeight);
		g2d.setColor(Color.GREEN);
		g2d.setStroke(new BasicStroke(5.0f));
		g2d.draw(nodeHighlightRect);
		
		g2d.setTransform(backupXForm);
		
		g2d.translate(coordx, coordy);
		//busNameLabel.render(g2d);
		Rectangle2D insetBox = new Rectangle2D.Double(0,0,width,height);
		g2d.setColor(Color.WHITE);
		g2d.fill(insetBox);

		g2d.setColor(backupColor);
		g2d.setStroke(backupStroke);
		
		// Draw lines

		g2d.setClip(new Rectangle2D.Double(0,0,width,height));
		g2d.translate(width/2, 2*height/3);
		double nodeDisplayX = nodeCoord.x;
		double nodeDisplayY = nodeCoord.y;

		if (displayLines != null) {
			for (int i = 0; i < displayLines.size(); i++) {
				displayLines.get(i).render(g2d);
			}
			for (int i = 0; i < flowArrowList.size(); i++) {
				flowArrowList.get(i).render(g2d);
			}
		}
		
		g2d.setClip(null);
		g2d.setStroke(new BasicStroke((float)busThickness,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.BLACK);
		Point2D.Double busStartPoint = new Point2D.Double(-busWidth/2,0);
		Point2D.Double busEndPoint = new Point2D.Double(+busWidth/2,0);
		g2d.draw(new Line2D.Double(busStartPoint,busEndPoint));
		g2d.setStroke(backupStroke);
		AffineTransform genIconXForm = g2d.getTransform();
		
		g2d.setTransform(backupXForm);
		g2d.setStroke(new BasicStroke(5.0f));
		g2d.translate(coordx, coordy);
		//busNameLabel.render(g2d);
		g2d.setColor(Color.GREEN);
		g2d.draw(insetBox);
		g2d.setColor(backupColor);
		g2d.setStroke(backupStroke);
		
		g2d.setTransform(genIconXForm);
		for (int i = 0; i < genIcons.size(); i++) {
			genIcons.get(i).render(g2d);
		}
		if (loadIcon != null) {
			loadIcon.render(g2d);
		}
		g2d.setColor(backupColor);
		g2d.setTransform(backupXForm);
		g2d.translate(coordx, coordy);
		busNameLabel.render(g2d);

		
		g2d.setTransform(backupXForm);
		g2d.setStroke(backupStroke);
		
		return true;
	}

	public void mouseDragged(MouseEvent arg0) {
		for (int i = 0; i < genIcons.size(); i++) {
			genIcons.get(i).mouseDragged(arg0);
		}
		if (loadIcon != null)
			loadIcon.mouseDragged(arg0);
	}

	public void mouseMoved(MouseEvent arg0) {
		for (int i = 0; i < genIcons.size(); i++) {
			genIcons.get(i).mouseMoved(arg0);
		}
		if (loadIcon != null)
			loadIcon.mouseMoved(arg0);
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

	public void mousePressed(MouseEvent arg0) {
		for (int i = 0; i < genIcons.size(); i++) {
			genIcons.get(i).mousePressed(arg0);
		}
		if (loadIcon != null)
			loadIcon.mousePressed(arg0);
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean animationstep() {
		if (flowArrowList != null) {
			for (int i = 0; i < flowArrowList.size(); i++) {
				flowArrowList.get(i).animationstep();
			}
		}
		return true;
	}
	
}
