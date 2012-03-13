package edu.uiuc.TCIP.education.lessons.powerAndEnergyInTheHome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

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
import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.*;
import edu.uiuc.power.dataobjects.timeOfUseProviders.ConstantPrice;
import edu.uiuc.power.dataobjects.timeOfUseProviders.PricingScheme;
import edu.uiuc.power.displayobjects.*;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;
import edu.uiuc.power.displayobjects.loads.HouseholdAppliances;
import edu.uiuc.power.displayobjects.loads.KitchenAppliances;
import edu.uiuc.power.displayobjects.loads.LoadSelectable;
import edu.uiuc.power.displayobjects.loads.LoadSelectableGaming;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;

public class PandEHomePanelForKiosk extends JPanel implements SimulationClockListener {
	
	ArrayList<LoadSelectable> loadSelectables;
	ArrayList<JComboBox> cboxes = new ArrayList<JComboBox>();
	ArrayList<JLabel> cboxlabels = new ArrayList<JLabel>();
	AnimatedPanelUsingPaintComponent circuitpanel;
	PandEHomePanelForKiosk myself;
	JButton StartSim,StopSim,ResetSim;
	JSlider costSlider;
	JLabel costLabel;
	JPanel costOfElectricityPanel;
	JButton OpenCostSlider;
	int plotUpdateCounter = 0;
	final int animationsStepsPerPlotUpdate = 10;

	PowerSystem ps;
	PricingScheme pScheme = PricingScheme.CONSTANT;
	
	BranchData branchTowerToCB;
	JCheckBox plotOnTopCkBox;
	// Set up display elements
	static long fps = 30;
	static long period = 1000L/fps;
	boolean toggleRepaint;
	long previousRepaintTime = 0;
	long lastRepaintTime = 0;
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		System.out.println("in setsize");
	}
	
	private void propagateNewTPriceProvider() {
        costAndDemandTimeSeries.setTimeOfUsePriceProvider(tPriceProvider, true);
	}
	
	SimulationClock simClock;
	DecimalFormat dformat = new DecimalFormat("0.0");
	SimpleDateFormat sdformat = new SimpleDateFormat("D:H:m");
	
	static int minutesPerStep = 1;
	private int initialConstantPrice = 10; // cents per kWh
	static int maxConstantPrice = 20; // cents per kWh
	private TimeOfUsePriceProvider tPriceProvider;
	CostAndDemandTimeSeries costAndDemandTimeSeries = null;
	
	public PandEHomePanelForKiosk() {
		myself = this;
	
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		// Initialize clock for the simulator
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = sdformat.parse("1:0:0");
			endDate = sdformat.parse("3:23:58");
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		simClock = new SimulationClock(startDate,endDate,minutesPerStep,false);
		simClock.addClockListener(this);
		
		tPriceProvider = pScheme.getTimeOfUsePriceProvider(simClock, (double)initialConstantPrice/1000.0);
		costAndDemandTimeSeries = new CostAndDemandTimeSeries(simClock,"Total cost","Total demand",tPriceProvider);

		setupPowerSystem();
		
		costAndDemandTimeSeries.addDemandProvider(new DemandProviderFromLineFlow(branchTowerToCB,"W",1.0));
		
		// add the clock so it gets animated
		circuitpanel.getAnimatables().add(simClock);
		circuitpanel.getAnimatables().add(costAndDemandTimeSeries);

		SimClockDisplay clockDisplay = new SimClockDisplay(new Point2D.Double(350,5),12,Color.RED,0,simClock);
		clockDisplay.setDisplayDay(true);
		circuitpanel.getTopLayerRenderables().add(clockDisplay); 
		
		
		Dimension circuitPanelDimension = new Dimension(590,487);
		circuitpanel.setMinimumSize(circuitPanelDimension);
		circuitpanel.setBackground(Color.WHITE);
		GridBagConstraints gbConstraints = new GridBagConstraints();
		gbConstraints.fill = GridBagConstraints.NONE;
		gbConstraints.gridx = 0;
		gbConstraints.gridy = 0;
		gbConstraints.anchor = GridBagConstraints.NORTH;
		gbConstraints.weightx = 0;//0.2;
		gbConstraints.weighty = 0.1;
		gbConstraints.insets = new Insets(1,1,1,1);
		this.add(circuitpanel,gbConstraints);
		
		JPanel cboxHolder = new JPanel();
		cboxHolder.setLayout(null);
		cboxHolder.setMinimumSize(new Dimension(140,500));
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
		gbConstraints = new GridBagConstraints();
		gbConstraints.fill = GridBagConstraints.NONE;
		gbConstraints.anchor = GridBagConstraints.NORTH;
		gbConstraints.gridx = 1;
		gbConstraints.gridy = 0;
		gbConstraints.insets = new Insets(1,1,1,1);
		this.add(cboxHolder,gbConstraints);		

		int plotHolderWidth = 1005 - 600 - 135;
		TimeSeriesPlotPanel plotPanel = new TimeSeriesPlotPanel("Power consumption",0,1000,plotHolderWidth,300,costAndDemandTimeSeries.getDemandTimeSeries(),"Watts (W)");
		plotPanel.setFillColor(Color.YELLOW);
		plotPanel.setBorder(BorderFactory.createEtchedBorder());
		JPanel plotHolder = new JPanel();
		plotHolder.setLayout(new BorderLayout());
		plotHolder.add(plotPanel,BorderLayout.CENTER);
		plotHolder.doLayout();
		gbConstraints = new GridBagConstraints();
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.gridx = 2;
		gbConstraints.gridy = 0;
		gbConstraints.gridheight = 2;
		gbConstraints.weightx = 1;
		gbConstraints.weighty = 1;
		gbConstraints.insets = new Insets(1,1,1,1);
		this.add(plotHolder,gbConstraints);		
		
		costSlider = new JSlider(1,200,initialConstantPrice*10);
		costSlider.setMajorTickSpacing(10);
		costSlider.setMinorTickSpacing(10);
		costSlider.setPaintTicks(true);
		costSlider.setPaintLabels(true);
		costSlider.setBackground(Color.WHITE);
		costSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		Hashtable<Integer, Component> labels = new Hashtable<Integer, Component>();
		NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.#"));
		int tickIdx = 1;
		while (tickIdx <= 200) {
			try {
				JLabel tickLabel = new JLabel(nf.valueToString(((double)tickIdx-1)/10));
				tickLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
				tickLabel.setBackground(Color.WHITE);
				labels.put(tickIdx, tickLabel);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (tickIdx == 1)
				tickIdx = 21;//(int)(maxConstantPrice/10);
			else
				tickIdx += 20;//(int)(maxConstantPrice/10);
		}
		costSlider.setLabelTable(labels);		
		costLabel = new JLabel("Cost of electricity: " + dformat.format(costSlider.getValue()/10.0) + " cents per kWh");
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
					//costLabel = new JLabel("Cost of electricity: " + costSlider.getValue()/10.0 + " cents per kWh");
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
				tPriceProvider = pScheme.getTimeOfUsePriceProvider(simClock,(double)(costSlider.getValue())/1000.0);
				costLabel.setText("Cost of electricity: " + dformat.format(costSlider.getValue()/10.0) + " cents per kWh");
				propagateNewTPriceProvider();
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
				
		MeterDisplayForTimeOfUseEnergyAndCost energyMeter = new MeterDisplayForTimeOfUseEnergyAndCost(new Point2D.Double(175,225),costAndDemandTimeSeries,costSlider);
		circuitpanel.getTopLayerRenderables().add(energyMeter);
		circuitpanel.getAnimatables().add(energyMeter);
		
		BranchWattFlowLabel powerConsumedLabel = new BranchWattFlowLabel(new Point2D.Double(350,25),12,new Color(0,100,0),0,branchTowerToCB);
		powerConsumedLabel.setHorizontalAlignment(DataLabelHorizontalAlignment.CENTER);
		circuitpanel.getMiddleLayerRenderables().add(powerConsumedLabel);
		
		//CumulativeEnergyLabel cEnergyLabel = new CumulativeEnergyLabel(new Point2D.Double(500,500),16,Color.BLACK,0,"Total energy consumed: "," kWh",0.001,plotFrame);
		//circuitpanel.getMiddleLayerRenderables().add(cEnergyLabel);
		
		//MeterDisplayForEnergyUsage energyMeter = new MeterDisplayForEnergyUsage(new Point2D.Double(175,205),plotPanel,branchTowerToCB,costSlider);
		//circuitpanel.getTopLayerRenderables().add(energyMeter);
		//circuitpanel.getAnimatables().add(energyMeter);
		
		//CumulativeTimeLabel cTimeLabel = new CumulativeTimeLabel(new Point2D.Double(115,430),16,Color.RED,0,plotPanel);
		//circuitpanel.getMiddleLayerRenderables().add(cTimeLabel);
		
		StartSim = new JButton("Start Time");
		StartSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StartSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(true);
			}			
		});
		
		StopSim = new JButton("Pause Time");
		StopSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StopSim.setEnabled(false);
		StopSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(false);
			}
		});
		
		ResetSim = new JButton("Reset Time");
		ResetSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		ResetSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simClock.resetClock();
				costAndDemandTimeSeries.clearAllTimeSeries();
			}
		});
		
		JButton PrintCircuit = new JButton("Print");
		PrintCircuit.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		PrintCircuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintUtilities.printComponent(myself);
			}
		});
		
		JPanel SimControlPanel = new JPanel();
		SimControlPanel.setLayout(new FlowLayout());
		SimControlPanel.add(StartSim);
		SimControlPanel.add(StopSim);
		SimControlPanel.add(ResetSim);
		SimControlPanel.setBackground(Color.WHITE);
		SimControlPanel.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel mainFrameControlPanel = new JPanel();
		mainFrameControlPanel.setLayout(new BorderLayout());
		mainFrameControlPanel.add(SimControlPanel,BorderLayout.NORTH);
		mainFrameControlPanel.add(costOfElectricityPanel,BorderLayout.CENTER);
		gbConstraints = new GridBagConstraints();
		gbConstraints.gridx = 0;
		gbConstraints.gridy = 1;
		gbConstraints.gridwidth = 2;
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.insets = new Insets(1,1,1,1);
		this.add(mainFrameControlPanel,gbConstraints);		
		//StartSim.doClick();

		simClock.setClockState(true);
	
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

	public void clockStarted(SimulationClock simClock) {
		StartSim.setEnabled(false);
		StopSim.setEnabled(true);
	}

	public void clockStopped(SimulationClock simClock) {
		StartSim.setEnabled(true);
		StopSim.setEnabled(false);
	}
}
