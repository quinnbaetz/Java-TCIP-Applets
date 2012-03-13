package edu.uiuc.TCIP.education.lessons.powerAndEnergyInTheHome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uiuc.power.dataobjects.*;
import edu.uiuc.power.displayobjects.*;
import edu.uiuc.power.displayobjects.loads.HouseholdAppliances;
import edu.uiuc.power.displayobjects.loads.KitchenAppliances;
import edu.uiuc.power.displayobjects.loads.LoadSelectable;
import edu.uiuc.power.displayobjects.loads.LoadSelectableGaming;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;

public class PandEHomePanel extends JPanel implements XYCumulativeEnergyPlotListener, ComponentListener {
	
	ArrayList<LoadSelectable> loadSelectables;
	ArrayList<JComboBox> cboxes = new ArrayList<JComboBox>();
	ArrayList<JLabel> cboxlabels = new ArrayList<JLabel>();
	AnimatedPanelUsingPaintComponent circuitpanel;
	PandEHomePanel myself;
	JButton StartSim,StopSim,ResetSim;
	JSlider costSlider;
	JLabel costLabel;
	JPanel costOfElectricityPanel;
	JButton OpenCostSlider;
	int plotUpdateCounter = 0;
	final int animationsStepsPerPlotUpdate = 10;
	//Lesson1CumulativeEnergyChartPanel plotFrame = null;
	Lesson1PlotPanel plotFrame = null;
	//Lesson2TotalLoadPlotPanel plotLoad;
	
	PowerSystem ps;
	
	BranchData branchTowerToCB;
	JCheckBox plotOnTopCkBox;
	// Set up display elements
	long fps,period;
	boolean toggleRepaint;
	long previousRepaintTime = 0;
	long lastRepaintTime = 0;
	double maxTime = 99.0 + 59.0/60.0;
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		System.out.println("in setsize");
	}

	public void integrationStopped() {
		/*
		if (plotFrame.getCurrentTime() < maxTime)
			StartSim.setEnabled(true);
		*/
		StopSim.setEnabled(false);
	}
	
	public PandEHomePanel() {
		myself = this;
	
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.addComponentListener(this);
		
		//fps = 80;
		fps = 60;
		period = 1000L/fps;	
		
		setLayout(new BorderLayout());
		//setBackground(Color.RED);
		this.setSize(700,600);
		setupPowerSystem();
		Dimension circuitPanelDimension = new Dimension(600,600);
		//circuitpanel.setSize(circuitPanelDimension);
		circuitpanel.setPreferredSize(circuitPanelDimension);
		//circuitpanel.setMaximumSize(circuitPanelDimension);
		//circuitpanel.setMinimumSize(circuitPanelDimension);
		circuitpanel.setBackground(Color.WHITE);
		add(circuitpanel,BorderLayout.CENTER);
		toggleRepaint = true;
		new Thread(){
				public void run() {
					while (true) {
						

						synchronized(this) {
							try {
								long diffTimeInMilliseconds = (lastRepaintTime - previousRepaintTime)/1000000; 
								if (diffTimeInMilliseconds > period*4) {
									//System.out.println("short wait");
									wait(period/2);
								}
								else
									wait(period);
							} catch (InterruptedException e) {
								// ok..do nothing
							}
						}
	                    SwingUtilities.invokeLater(new Runnable()
	                    {
	                        public void run()
	                        {
	                        	if ((plotUpdateCounter++ > animationsStepsPerPlotUpdate) & (plotFrame != null)) {
	                        		plotUpdateCounter = 0;
                        			plotFrame.updatePlot();
	                        	}
	                        	ArrayList<Animatable> animatables = circuitpanel.getAnimatables();
	                        	for (int i = 0; i < animatables.size(); i++) {
	                        		animatables.get(i).animationstep();
	                        	}
	                        		
	                        	if (toggleRepaint) {
	                        		repaint();
	                        		previousRepaintTime = lastRepaintTime;
	                        		lastRepaintTime = System.nanoTime();
	                        		toggleRepaint = false;
	                        	} else
	                        		toggleRepaint = true;
	                        }
	                    });
					}
				}
		}.start();
		
		JPanel cboxHolder = new JPanel();
		cboxHolder.setLayout(null);
		cboxHolder.setPreferredSize(new Dimension(135,500));
		for (int i = 0; i < loadSelectables.size(); i++) {
			JComboBox cbox = loadSelectables.get(i).getComboBox();
			cboxes.add(cbox);
			Dimension size = cbox.getPreferredSize();
			cbox.setSize(size);
			//(70,225,380)
			cbox.setBounds(0,cbox.getLocation().y,cbox.getBounds().width,cbox.getBounds().height);
			
			JLabel cboxLabel = loadSelectables.get(i).getLabel();
			cboxlabels.add(cboxLabel);
			cboxLabel.setSize(cboxLabel.getPreferredSize());
			cboxLabel.setBounds(0,cboxLabel.getLocation().y,cboxLabel.getBounds().width,cboxLabel.getBounds().height);
			cboxHolder.add(cboxLabel);
			cboxHolder.add(cbox);
		}
		cboxHolder.setBackground(Color.WHITE);
		add(cboxHolder,BorderLayout.LINE_END);
		this.doLayout();
		
		
		costSlider = new JSlider(0,30,10);
		costSlider.setMajorTickSpacing(10);
		costSlider.setMinorTickSpacing(1);
		costSlider.setPaintTicks(true);
		costSlider.setPaintLabels(true);
		costSlider.setBackground(Color.WHITE);
		costSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		
		costLabel = new JLabel("Cost of electricity: " + costSlider.getValue() + " cents per kWh");
		costLabel.setBackground(Color.WHITE);
		costLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		costLabel.setHorizontalAlignment(JLabel.CENTER);
		
		OpenCostSlider = new JButton("Show cost selector");
		OpenCostSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		OpenCostSlider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (OpenCostSlider.getText().compareTo("Show cost selector") == 0) {
					costSlider.setVisible(true);	
					OpenCostSlider.setText("Hide cost selector");
					//myself.doLayout();
				}
				else {
					costSlider.setVisible(false);
					OpenCostSlider.setText("Show cost selector");
				}
			}
		});
		
		costSlider.addChangeListener(new ChangeListener()  {

			public void stateChanged(ChangeEvent arg0) {
				if (costSlider.getValue() == 1)
					costLabel.setText("Cost of electricity: " + costSlider.getValue() + " cent per kWh");
				else
					costLabel.setText("Cost of electricity: " + costSlider.getValue() + " cents per kWh");
			}
		
		});		
		
		
		costOfElectricityPanel = new JPanel();
		costOfElectricityPanel.setBackground(Color.WHITE);
		costOfElectricityPanel.setLayout(new BorderLayout());
		JPanel costDisplayAndButtonPanel = new JPanel();
		costDisplayAndButtonPanel.setBackground(Color.WHITE);
		costDisplayAndButtonPanel.setLayout(new FlowLayout());
		costDisplayAndButtonPanel.add(costLabel,BorderLayout.CENTER);
		costDisplayAndButtonPanel.add(OpenCostSlider,BorderLayout.CENTER);
		costOfElectricityPanel.add(costDisplayAndButtonPanel,BorderLayout.CENTER);
		costOfElectricityPanel.add(costSlider,BorderLayout.PAGE_END);
		costSlider.setVisible(false);
		costOfElectricityPanel.setBorder(BorderFactory.createEtchedBorder());
		costOfElectricityPanel.setVisible(true);
				
		
		/*
		plotFrame = new Lesson1CumulativeEnergyChartPanel("Lesson 1: Plot of Power Consumed Over Time",branchTowerToCB,
				100,
				maxTime,
				1.0/60.0,
				0,6,
				-200,7000,
				this);
				*/
		plotFrame = new Lesson1PlotPanel("Lesson 1",branchTowerToCB,1,this,-200,7000);
		plotFrame.setLocation(this.getLocation().x + 450,this.getLocation().y + 450);
		plotFrame.setPreferredSize(new Dimension(575,275));
		plotFrame.setSize(0,0);
		plotFrame.setVisible(true);
		plotFrame.setVisible(false);
		plotFrame.setSize(575,275);
		//plotFrame.setAlwaysOnTop(true);
		
		
		
		//CumulativeEnergyLabel cEnergyLabel = new CumulativeEnergyLabel(new Point2D.Double(500,500),16,Color.BLACK,0,"Total energy consumed: "," kWh",0.001,plotFrame);
		//circuitpanel.getMiddleLayerRenderables().add(cEnergyLabel);
		
		MeterDisplayForEnergyUsage energyMeter = new MeterDisplayForEnergyUsage(new Point2D.Double(175,225),plotFrame,branchTowerToCB,costSlider);
		circuitpanel.getTopLayerRenderables().add(energyMeter);
		circuitpanel.getAnimatables().add(energyMeter);
		
		CumulativeTimeLabel cTimeLabel = new CumulativeTimeLabel(new Point2D.Double(115,440),16,Color.RED,0,plotFrame);
		circuitpanel.getMiddleLayerRenderables().add(cTimeLabel);
		
		BranchWattFlowLabel powerConsumedLabel = new BranchWattFlowLabel(new Point2D.Double(115,460),16,new Color(0,100,0),0,branchTowerToCB);
		circuitpanel.getMiddleLayerRenderables().add(powerConsumedLabel);
		
		JPanel SimControlPanel = new JPanel();
		StartSim = new JButton("Start Time");
		StartSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StartSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				plotFrame.startIntegration();
				StartSim.setEnabled(false);
				StopSim.setEnabled(true);
				//plotLoad.startIntegration();
			}			
		});
		
		StopSim = new JButton("Pause Time");
		StopSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StopSim.setEnabled(false);
		StopSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				plotFrame.stopIntegration();
				StartSim.setEnabled(true);
				StopSim.setEnabled(false);
			}
		});
		
		ResetSim = new JButton("Reset Time");
		ResetSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		ResetSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotFrame.resetTime();
				if (!plotFrame.getIntegrating())
					StartSim.setEnabled(true);
				
			}
		});
		
		//plotFrame = new JFrame("Plot window");



		JButton OpenPlotFrame = new JButton("Show plot");
		OpenPlotFrame.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		OpenPlotFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotFrame.setVisible(true);
			}
		});
		
		JButton PrintCircuit = new JButton("Print");
		PrintCircuit.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		PrintCircuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintUtilities.printComponent(myself);
			}
		});
		
		JPanel openPlotFramePanel = new JPanel();
		openPlotFramePanel.add(OpenPlotFrame);
		openPlotFramePanel.setBackground(Color.WHITE);

		
		SimControlPanel.setLayout(new FlowLayout());
		SimControlPanel.add(StartSim);
		SimControlPanel.add(StopSim);
		SimControlPanel.add(ResetSim);
		SimControlPanel.add(new JLabel("-"));
		SimControlPanel.add(OpenPlotFrame);
		//SimControlPanel.add(plotOnTopCkBox);
		//SimControlPanel.add(new JLabel("-"));
		//SimControlPanel.add(OpenCostSlider);
		SimControlPanel.add(new JLabel("-"));
		SimControlPanel.add(PrintCircuit);
		SimControlPanel.setBackground(Color.WHITE);
		SimControlPanel.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel mainFrameControlPanel = new JPanel();
		mainFrameControlPanel.setLayout(new BorderLayout());
		mainFrameControlPanel.add(costOfElectricityPanel,BorderLayout.PAGE_END);
		mainFrameControlPanel.add(SimControlPanel,BorderLayout.PAGE_START);
		add(mainFrameControlPanel,BorderLayout.PAGE_END);
		StartSim.doClick();
	}

	private void setupPowerSystem() {
		// Create nodes
		NodeData nodeTower = new NodeData(1,1.0,0,true);
		NodeData nodeCircuitBreaker = new NodeData(2,1.0,0,false);
		NodeData nodeTopLoad = new NodeData(3,1.0,0,false);
		NodeData nodeBottomLoad = new NodeData(4,1.0,0,false);
		NodeData nodeMiddleLoad = new NodeData(5,1.0,0,false);
		
		// Create lines
		branchTowerToCB = new BranchData(nodeTower,nodeCircuitBreaker,0.01,0,10500.0,true);
		BranchData branchCBToTopLoad = new BranchData(nodeCircuitBreaker,nodeTopLoad, 0.01,0,1800,true);
		BranchData branchCBToBottomLoad = new BranchData(nodeCircuitBreaker,nodeBottomLoad,0.01,0,1800,true);
		BranchData branchCBToMiddleLoad = new BranchData(nodeCircuitBreaker,nodeMiddleLoad,0.01,0,1800,true);
		
		// Create loads
		LoadData loadTop = new LoadData(nodeTopLoad,700,0,true);
		LoadData loadBottom = new LoadData(nodeBottomLoad,200,0,true);
		LoadData loadMiddle = new LoadData(nodeMiddleLoad,2500,0,true);
		
		// Register with power system
		ps = new PowerSystem();
		
		ps.addNode(nodeTower);
		ps.addNode(nodeCircuitBreaker);
		ps.addNode(nodeTopLoad);
		ps.addNode(nodeBottomLoad);
		ps.addNode(nodeMiddleLoad);
		
		ps.addBranch(branchTowerToCB);
		ps.addBranch(branchCBToBottomLoad);
		ps.addBranch(branchCBToTopLoad);
		ps.addBranch(branchCBToMiddleLoad);
		
		ps.addLoad(loadTop);
		ps.addLoad(loadBottom);
		ps.addLoad(loadMiddle);
		
		ps.solve();

		
		// Set up selectable loads
		LoadSelectable loadSelectableTop = new KitchenAppliances(loadTop,new Point2D.Double(525,70),LoadDisplayAlignmentVertical.CENTER,LoadDisplayAlignmentHorizontal.CENTER);
		LoadSelectable loadSelectableMiddle = new HouseholdAppliances(loadMiddle,new Point2D.Double(525,225),LoadDisplayAlignmentVertical.CENTER,LoadDisplayAlignmentHorizontal.CENTER);
		LoadSelectable loadSelectableBottom = new LoadSelectableGaming(loadBottom,new Point2D.Double(525,380),LoadDisplayAlignmentVertical.CENTER,LoadDisplayAlignmentHorizontal.CENTER);
		loadSelectables = new ArrayList<LoadSelectable>();
		loadSelectables.add(loadSelectableTop);
		loadSelectables.add(loadSelectableMiddle);
		loadSelectables.add(loadSelectableBottom);
		
		// Create circuit panel
		//circuitpanel = new AnimatedPanelForLesson1(300,550,period,0,true,loadSelectables);
		circuitpanel = new AnimatedPanelUsingPaintComponent(300,550,period,0,true);//,loadSelectables);
		
		// Create various branch displays
		ArrayList<LineAndDistanceInfoProvider> lines; 
		FlowArrowsForBranchData fA;
		BranchColorProvider branchColorProvider = new BranchColorStatic(Color.BLACK);
		BranchThicknessProvider branchThicknessProvider = new BranchThicknessStatic(1.0);
		FlowArrowColorProvider flowArrowColorProvider = new FlowArrowColorStatic(new Color(0,255,0,255/2));
		
		Point2D.Double CBCenter = new Point2D.Double(340,275);
		
		// Line CB to Top Load
		ArrayList<Point2D.Double> pointsCBToLoadTop = new ArrayList<Point2D.Double>();
		pointsCBToLoadTop.add(CBCenter);
		pointsCBToLoadTop.add(new Point2D.Double(CBCenter.getX(),70));
		pointsCBToLoadTop.add(new Point2D.Double(425,70));
		pointsCBToLoadTop.add(new Point2D.Double(475,70));
		pointsCBToLoadTop.add(new Point2D.Double(525,70));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsCBToLoadTop, 0, branchThicknessProvider, 3.0, 2, Math.PI/20, branchCBToTopLoad, branchColorProvider, circuitpanel,false);
		
		fA = new FlowArrowsForBranchData(branchCBToTopLoad,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		
		// Line CB to Middle Load
		ArrayList<Point2D.Double> pointsCBToLoadMiddle = new ArrayList<Point2D.Double>();
		pointsCBToLoadMiddle.add(CBCenter);
		pointsCBToLoadMiddle.add(new Point2D.Double(425,225));
		pointsCBToLoadMiddle.add(new Point2D.Double(475,225));
		pointsCBToLoadMiddle.add(new Point2D.Double(525,225));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsCBToLoadMiddle, 0, branchThicknessProvider, 3.0, 1, Math.PI/20, branchCBToMiddleLoad, branchColorProvider, circuitpanel,false);
		fA = new FlowArrowsForBranchData(branchCBToMiddleLoad,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		
		// Line CB to Bottom Load
		Point2D.Double bottomLoadPoint = new Point2D.Double(525,380);
		ArrayList<Point2D.Double> pointsCBToLoadBottom = new ArrayList<Point2D.Double>();
		pointsCBToLoadBottom.add(CBCenter);
		SimpleLineDisplayWithDistanceInfo lineCBToLoadBottomtemp = 
			new SimpleLineDisplayWithDistanceInfo(CBCenter,
					bottomLoadPoint,1.0,0);			
		double distlineCBToLoadBottomtemp = lineCBToLoadBottomtemp.getEndDistance();
		pointsCBToLoadBottom.add(DrawUtilities.getPointAtDistanceOnLine(lineCBToLoadBottomtemp, (distlineCBToLoadBottomtemp / 2.0) - 25));
		pointsCBToLoadBottom.add(DrawUtilities.getPointAtDistanceOnLine(lineCBToLoadBottomtemp, (distlineCBToLoadBottomtemp / 2.0) + 25));
		pointsCBToLoadBottom.add(bottomLoadPoint);
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsCBToLoadBottom, 0, branchThicknessProvider, 3.0, 1, Math.PI/20, branchCBToBottomLoad, branchColorProvider, circuitpanel,false);
		fA = new FlowArrowsForBranchData(branchCBToBottomLoad,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);	
		
		// Line Tower to CB
		ArrayList<Point2D.Double> pointsTowerToCB = new ArrayList<Point2D.Double>();
		
		SimpleLineDisplayWithDistanceInfo lineTowerToCBtemp = 
			new SimpleLineDisplayWithDistanceInfo(new Point2D.Double(110,105),
					new Point2D.Double(150,275),1.0,0);		
		double distanceOfTempLine = lineTowerToCBtemp.getEndDistance();
		pointsTowerToCB.add(lineTowerToCBtemp.getFromPoint());		
		pointsTowerToCB.add(DrawUtilities.getPointAtDistanceOnLine(lineTowerToCBtemp, 0.5*distanceOfTempLine));
		pointsTowerToCB.add(DrawUtilities.getPointAtDistanceOnLine(lineTowerToCBtemp, 0.75*distanceOfTempLine));
		pointsTowerToCB.add(lineTowerToCBtemp.getToPoint());
		pointsTowerToCB.add(CBCenter);
		
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsTowerToCB, 0, branchThicknessProvider, 3.0, 1, Math.PI/20, branchTowerToCB, branchColorProvider, circuitpanel,false);
		fA = new FlowArrowsForBranchData(branchTowerToCB,lines,0.05,2*20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);			
		
		//MeterDisplayForBranch meterdisp = new MeterDisplayForBranch(new Point2D.Double(175,225),branchTowerToCB);
		//circuitpanel.getMiddleLayerRenderables().add(meterdisp);
		//circuitpanel.getAnimatables().add(meterdisp);
		
		TransmissionTowerDisplay towerdisp = null;
		try {
			towerdisp = new TransmissionTowerDisplay(new Point2D.Double(-60,50));
			circuitpanel.getMiddleLayerRenderables().add(towerdisp);
		} catch (IOException ie) {
			System.out.println(ie);
		}	

		circuitpanel.getMiddleLayerRenderables().add(loadSelectableTop);
		circuitpanel.getAnimatables().add(loadSelectableTop);
		circuitpanel.getMiddleLayerRenderables().add(loadSelectableMiddle);
		circuitpanel.getAnimatables().add(loadSelectableMiddle);
		circuitpanel.getMiddleLayerRenderables().add(loadSelectableBottom);
		circuitpanel.getAnimatables().add(loadSelectableBottom);
		
		{
			// set up line leading to tower from the left
	    	int numofsegments = 8;
	    	Point2D.Double start = new Point2D.Double(-60.0, 100.0);
	    	Point2D.Double controlPoint1 = new Point2D.Double(10.0,130.0);
	    	Point2D.Double controlPoint2 = new Point2D.Double(40.0,130.0);
	    	Point2D.Double end = new Point2D.Double(110.0,100.0);
			
	    	ArrayList<SimpleLineDisplayWithDistanceInfo> TTowerlines = DrawUtilities.getLineSegmentsCorrespondingToCubicCurve(start, controlPoint1, controlPoint2, end, numofsegments, 1.0);
	    	ArrayList<LineAndDistanceInfoProvider> TTowerlinesForFA = new ArrayList<LineAndDistanceInfoProvider>();
	    	for (int i = 0; i < TTowerlines.size(); i++) {
	    		TTowerlinesForFA.add(TTowerlines.get(i));
	    		circuitpanel.getMiddleLayerRenderables().add(TTowerlines.get(i));
	    	}
	    	
	    	FlowArrowsForBranchData TTowerFA = new FlowArrowsForBranchData(branchTowerToCB,TTowerlinesForFA,0.05,2*20.0,10.0,flowArrowColorProvider);
	    	circuitpanel.getMiddleLayerRenderables().add(TTowerFA);
	    	circuitpanel.getAnimatables().add(TTowerFA);
		}		
		double cbWidth = 100*0.75;
		double cbHeight = 150*0.75;
		CircuitBreakerBoxDisplay cbdisplay = new CircuitBreakerBoxDisplay(new Point2D.Double(CBCenter.getX()-cbWidth/2.0,CBCenter.getY()-cbHeight/2),cbHeight,cbWidth);
		circuitpanel.getMiddleLayerRenderables().add(cbdisplay);

		/*
		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,0),12,Color.BLACK,0);
		circuitpanel.getMiddleLayerRenderables().add(mclabel);
		circuitpanel.addMouseMotionListener(mclabel);
		*/
		
		circuitpanel.getAnimatables().add(ps);
		
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		final double circuitPanelAspectRatio = 574.0/595.0;
		// from 720x600, get 574 x 595
		// so comboboxes take 720-564 = 156 pixels
		Component c = e.getComponent();
		//System.out.println("component resized, width = " + c.getSize().width + ", height = " + c.getSize().height);
		double currentWidth = c.getSize().width;
		double currentHeight = c.getSize().height;
		double circuitPanelWidth = currentWidth - 156;
		double circuitPanelHeight = currentHeight - 5;
		
		double scaleFactor = 1;
		
		if (circuitPanelWidth < circuitPanelHeight*circuitPanelAspectRatio) {
			scaleFactor = circuitPanelWidth/574.0;
		} else {
			scaleFactor = circuitPanelHeight/595.0;
		}
		
		if (circuitpanel != null) {
			circuitpanel.setScaleFactor(scaleFactor);
			
			//(70,225,380)
			for (int i = 0; i < cboxes.size(); i++) {
				JComboBox cbox = cboxes.get(i);
				JLabel cboxlabel = cboxlabels.get(i);
				double origY = 0;
				switch (i) {
					case 0:
						origY = 70;
						break;
					case 1:
						origY = 225;
						break;
					case 2:
						origY = 380;
						break;
				}
				cbox.setBounds(0,(int)(scaleFactor*origY) - 10,cbox.getBounds().width,cbox.getBounds().height);
				cboxlabel.setBounds(0,(int)(scaleFactor*origY) - 30,cboxlabel.getBounds().width,cboxlabel.getBounds().height);
			}
		}

	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
