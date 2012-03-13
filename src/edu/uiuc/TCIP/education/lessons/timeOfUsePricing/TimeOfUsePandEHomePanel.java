package edu.uiuc.TCIP.education.lessons.timeOfUsePricing;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;

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
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

import java.util.Hashtable;

import edu.uiuc.power.dataobjects.*;
import edu.uiuc.power.dataobjects.timeOfUseProviders.ConstantPrice;
import edu.uiuc.power.dataobjects.timeOfUseProviders.HourlyPrice;
import edu.uiuc.power.dataobjects.timeOfUseProviders.PricingScheme;
import edu.uiuc.power.displayobjects.*;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;
import edu.uiuc.power.displayobjects.loads.HouseholdAppliances;
import edu.uiuc.power.displayobjects.loads.HouseholdAppliancesTimeDependent;
import edu.uiuc.power.displayobjects.loads.KitchenAppliances;
import edu.uiuc.power.displayobjects.loads.LoadSelectable;
import edu.uiuc.power.displayobjects.loads.LoadSelectableGaming;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;

public class TimeOfUsePandEHomePanel extends JPanel implements XYCumulativeEnergyPlotListener, ComponentListener {
	
	final static String SHOW_PRICE_SELECTOR = "Show constant price selector";
	final static String HIDE_PRICE_SELECTOR = "Hide constant price selector";
	
	ArrayList<LoadSelectable> loadSelectables;
	ArrayList<JComboBox> cboxes = new ArrayList<JComboBox>();
	ArrayList<JLabel> cboxlabels = new ArrayList<JLabel>();
	AnimatedPanelUsingPaintComponent circuitpanel;
	TimeOfUsePandEHomePanel myself;
	JButton StartSim,StopSim,ResetSim;
	JSlider costSlider;
	JLabel costLabel;
	JPanel costOfElectricityPanel;
	JButton OpenCostSlider;
	int plotUpdateCounter = 0;
	PricingScheme pScheme = PricingScheme.AMEREN_SPRING;
	final int animationsStepsPerPlotUpdate = 10;
	
	PowerSystem ps;
	
	BranchData branchTowerToCB;
	JCheckBox plotOnTopCkBox;
	// Set up display elements
	static long fps = 30;
	static long period = 1000L/fps;
	boolean toggleRepaint;
	long previousRepaintTime = 0;
	long lastRepaintTime = 0;
	double maxTime = 99.0 + 59.0/60.0;
	
	// new clock stuff for time of use pricing
	private SimulationClock simClock;
	private int minutesPerStep = 1;
	SimpleDateFormat sdformat = new SimpleDateFormat("D:H:m");
	private CostAndDemandTimeSeries costAndDemandTimeSeries;
	private TimeOfUsePriceOnelinePlot timeOfUsePlot;
	private TimeOfUsePriceProvider tPriceProvider;

	// new plots
	private TimeSeriesPlot tplotDemand = null; 
	private TimeSeriesPlot tplotCost = null;


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
	
	private void propagateNewTPriceProvider() {
        costAndDemandTimeSeries.setTimeOfUsePriceProvider(tPriceProvider);
        timeOfUsePlot.setTimeOfUsePriceProvider(tPriceProvider);
        
        if (pScheme == PricingScheme.CONSTANT) {
        	OpenCostSlider.setEnabled(true);
        } else {
        	OpenCostSlider.setEnabled(false);
        	OpenCostSlider.setText(SHOW_PRICE_SELECTOR);
        	costSlider.setVisible(false);
        }
	}
	
	private class PricingSchemeComboBoxListener implements ActionListener {
		public void updateAll(JComboBox cb) {
            pScheme = (PricingScheme)cb.getSelectedItem();
            tPriceProvider = pScheme.getTimeOfUsePriceProvider(simClock,(double)(costSlider.getValue())/1000.0);
            propagateNewTPriceProvider();
		}
		
		public void actionPerformed(ActionEvent e) {
			updateAll((JComboBox)e.getSource());
		}
	}
	
	
	static String[] timesOntarioSummer = {"00:00","07:00","11:00","17:00","21:00"};
	static double[] pricesOntarioSummer = {0.042,0.076,0.091,0.076,0.042};
	static String[] timesOntarioWeekendHolidays = {"00:00"};
	static double[] pricesOntarioWeekendHolidays = {0.042};
	static String[] timesOntarioWinter = {"00:00","07:00","11:00","17:00","21:00"};
	static double[] pricesOntarioWinter = {0.042,0.091,0.076,0.091,0.042};
	static String[] timesAmeren = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
	static double[] pricesAmerenWinter = {0.023,0.022,0.021,0.021,0.02,0.021,0.023,0.023,0.025,0.026,0.027,0.025,0.024,0.024,0.024,0.026,0.047,0.064,0.054,0.045,0.04,0.028,0.023,0.021}; // Jan 6 2008
	static double[] pricesAmerenSpring = {0.019,0.018,0.018,0.018,0.022,0.037,0.049,0.063,0.071,0.077,0.081,0.081,0.084,0.084,0.081,0.081,0.068,0.06,0.048,0.065,0.078,0.039,0.026,0.022}; // May 6 2008
	static double[] pricesAmerenSummer = {0.0245,0.02221,0.02161,0.02123,0.02147,0.02365,0.02971,0.03619,0.04208,0.05384,0.06233,0.0672,0.0752,0.0799,0.09054,0.09771,0.09114,0.07913,0.07196,0.06543,0.06755,0.0527,0.03813,0.03161}; // August 3 2010
	static double[] pricesAmerenFall =  {0.018,0.014,0.012,0.011,0.013,0.012,0.014,0.023,0.029,0.035,0.043,0.045,0.045,0.046,0.045,0.049,0.051,0.045,0.034,0.041,0.04,0.026,0.02,0.018}; // Sept 6 2008
	private JComboBox costplanComboBox;
	private int initialConstantPrice = 4; // cents per kWh
	
	public TimeOfUsePandEHomePanel() {
		myself = this;
	
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.addComponentListener(this);
		
		setLayout(new BorderLayout());
		this.setSize(700,600);

		// Initialize clock for the simulator
		Date startDate = null;
		try {
			startDate = sdformat.parse("1:16:0");
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		simClock = new SimulationClock(startDate,minutesPerStep ,true);
		simClock.setClockState(true);
		
		//ConstantPrice priceProvider = new ConstantPrice(0.1);
		tPriceProvider = pScheme.getTimeOfUsePriceProvider(simClock);
		costAndDemandTimeSeries = new CostAndDemandTimeSeries(simClock,"Total cost","Total demand",tPriceProvider);		
		
		setupPowerSystem();

		timeOfUsePlot = new TimeOfUsePriceOnelinePlot(tPriceProvider,10,475,400,125,simClock);
		circuitpanel.getMiddleLayerRenderables().add(timeOfUsePlot);
		
		// add mouse coordinates
//		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,0),12,Color.BLACK,0);
//		circuitpanel.getTopLayerRenderables().add(mclabel);
//		circuitpanel.addMouseMotionListener(mclabel);
		
		costAndDemandTimeSeries.addDemandProvider(new DemandProviderFromLineFlow(branchTowerToCB,"W",1.0));
		
		// add the clock so it gets animated
		circuitpanel.getAnimatables().add(simClock);
		circuitpanel.getAnimatables().add(costAndDemandTimeSeries);
		

		SimClockDisplay clockDisplay = new SimClockDisplay(new Point2D.Double(350,5),12,Color.RED,0,simClock);
		clockDisplay.setDisplayDay(true);
		circuitpanel.getTopLayerRenderables().add(clockDisplay); 
		
		Dimension circuitPanelDimension = new Dimension(600,600);
		circuitpanel.setPreferredSize(circuitPanelDimension);
		circuitpanel.setBackground(Color.WHITE);
		add(circuitpanel,BorderLayout.CENTER);

		
		JPanel cboxHolder = new JPanel();
		cboxHolder.setLayout(null);
		cboxHolder.setPreferredSize(new Dimension(135,500));
		for (int i = 0; i < loadSelectables.size(); i++) {
			JComboBox cbox = loadSelectables.get(i).getComboBox();
			cboxes.add(cbox);
			Dimension size = cbox.getPreferredSize();
			cbox.setSize(size);
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
		
		
		OpenCostSlider = new JButton(SHOW_PRICE_SELECTOR);
		OpenCostSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		OpenCostSlider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (OpenCostSlider.getText().compareTo(SHOW_PRICE_SELECTOR) == 0) {
					costSlider.setVisible(true);	
					OpenCostSlider.setText(HIDE_PRICE_SELECTOR);
				}
				else {
					costSlider.setVisible(false);
					OpenCostSlider.setText(SHOW_PRICE_SELECTOR);
				}
			}
		});

		costSlider = new JSlider(1,100,initialConstantPrice*10);
		costSlider.setMajorTickSpacing(1);
		costSlider.setMinorTickSpacing(1);
		costSlider.setPaintTicks(true);
		costSlider.setPaintLabels(true);
		costSlider.setBackground(Color.WHITE);
		costSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		Hashtable<Integer, Component> labels = new Hashtable<Integer, Component>();
		NumberFormatter nf = new NumberFormatter(new DecimalFormat("0.#"));
		int tickIdx = 1;
		while (tickIdx < 100) {
			try {
				JLabel tickLabel = new JLabel(nf.valueToString(((double)tickIdx)/10));
				tickLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
				tickLabel.setBackground(Color.WHITE);
				labels.put(tickIdx, tickLabel);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (tickIdx == 1)
				tickIdx = 10;
			else
				tickIdx += 10;
		}
		costSlider.setLabelTable(labels);
		
		//costLabel = new JLabel("Cost of electricity: " + costSlider.getValue() + " cents per kWh");
		costLabel = new JLabel("Select pricing plan:");// + costSlider.getValue() + " cents per kWh");
		costLabel.setBackground(Color.WHITE);
		costLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		costLabel.setHorizontalAlignment(JLabel.CENTER);
		
		costplanComboBox = new JComboBox(PricingScheme.values());
		costplanComboBox.setSelectedItem(pScheme);
		PricingSchemeComboBoxListener costplanComboBoxListener = new PricingSchemeComboBoxListener();
		costplanComboBox.addActionListener(costplanComboBoxListener);
		costplanComboBoxListener.updateAll(costplanComboBox);
		costplanComboBox.setBackground(Color.WHITE);
		costplanComboBox.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		
		costSlider.addChangeListener(new ChangeListener()  {

			public void stateChanged(ChangeEvent arg0) {
				tPriceProvider = pScheme.getTimeOfUsePriceProvider(simClock,(double)(costSlider.getValue())/1000.0);
				propagateNewTPriceProvider();
//				if (costSlider.getValue() == 1)
//					costLabel.setText("Cost of electricity: " + costSlider.getValue() + " cent per kWh");
//				else
//					costLabel.setText("Cost of electricity: " + costSlider.getValue() + " cents per kWh");
			}
		
		});	
		
	
		costOfElectricityPanel = new JPanel();
		costOfElectricityPanel.setBackground(Color.WHITE);
		costOfElectricityPanel.setLayout(new BorderLayout());
		JPanel costDisplayAndButtonPanel = new JPanel();
		costDisplayAndButtonPanel.setBackground(Color.WHITE);
		costDisplayAndButtonPanel.setLayout(new FlowLayout());
		costDisplayAndButtonPanel.add(costLabel,BorderLayout.CENTER);
		costDisplayAndButtonPanel.add(costplanComboBox,BorderLayout.CENTER);
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
		
		JPanel SimControlPanel = new JPanel();
		StartSim = new JButton("Start Time");
		StartSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StartSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(true);
				StartSim.setEnabled(false);
				StopSim.setEnabled(true);
			}			
		});
		
		StopSim = new JButton("Pause Time");
		StopSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StopSim.setEnabled(false);
		StopSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(false);
				StartSim.setEnabled(true);
				StopSim.setEnabled(false);
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
		
		//plotFrame = new JFrame("Plot window");

		
		JButton openCostPlotButton = new JButton("Show cost plot");
		openCostPlotButton.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		openCostPlotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tplotCost == null)
					tplotCost = new TimeSeriesPlot("Costs",0,1000,400,300,costAndDemandTimeSeries.getCostTimeSeries(),"¢/hr");
				tplotCost.setVisible(true);
			}
		});
		JButton openDemandPlotButton = new JButton("Show demand plot");
		openDemandPlotButton.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		openDemandPlotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tplotDemand == null)
					tplotDemand = new TimeSeriesPlot("Demand",0,1000,400,300,costAndDemandTimeSeries.getDemandTimeSeries(),"W");
				tplotDemand.setVisible(true);
			}
		});
		
		JButton PrintCircuit = new JButton("Print");
		PrintCircuit.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		PrintCircuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintUtilities.printComponent(myself);
			}
		});
		
		// First row of buttons
		SimControlPanel.setLayout(new FlowLayout());
		SimControlPanel.add(StartSim);
		SimControlPanel.add(StopSim);
		SimControlPanel.add(ResetSim);
		SimControlPanel.add(new JLabel("-"));
		SimControlPanel.add(openCostPlotButton);
		SimControlPanel.add(openDemandPlotButton);
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
		LoadSelectable loadSelectableMiddle = new HouseholdAppliancesTimeDependent(loadMiddle,new Point2D.Double(525,225),LoadDisplayAlignmentVertical.CENTER,LoadDisplayAlignmentHorizontal.CENTER,simClock);
			//new HouseholdAppliances(loadMiddle,new Point2D.Double(525,225),LoadDisplayAlignmentVertical.CENTER,LoadDisplayAlignmentHorizontal.CENTER);
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
	
	public void setupStartStopTimeSliders() {
		TimeRangeModel tmodel = new TimeRangeModel();
		
		JSlider startTimeSlider = new JSlider(tmodel);
		startTimeSlider.setSnapToTicks(true);
		startTimeSlider.setMajorTickSpacing(4);
		startTimeSlider.setMinorTickSpacing(1);
		startTimeSlider.setPaintTicks(true);
		startTimeSlider.setPaintLabels(true);
		startTimeSlider.setBackground(Color.WHITE);
		startTimeSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));		
		Hashtable<Integer, Component> startStopLabels = new Hashtable<Integer, Component>();
		DateFormatter startStopDateFormatter = new DateFormatter(new SimpleDateFormat("ha"));
		int startStopTickIdx = 0;
		GregorianCalendar startStopDateLabelCalender = new GregorianCalendar();
		startStopDateLabelCalender.set(Calendar.HOUR_OF_DAY, 0);
		startStopDateLabelCalender.set(Calendar.MINUTE, 0);
		while (startStopTickIdx <= 48) {
			try {
				JLabel tickLabel = new JLabel(startStopDateFormatter.valueToString(startStopDateLabelCalender.getTime()));
				tickLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
				tickLabel.setBackground(Color.WHITE);
				startStopLabels.put(startStopTickIdx, tickLabel);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			startStopTickIdx+=4;
			startStopDateLabelCalender.add(Calendar.MINUTE, 30*4);
		}
		startTimeSlider.setLabelTable(startStopLabels);	
		JLabel startstopTimeLabel = new JLabel("Start Time: xx:xx PM, Stop Time: xx:xx PM");
		startstopTimeLabel.setBackground(Color.WHITE);
		startstopTimeLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		JPanel startTimePanel = new JPanel(new BorderLayout());
		startTimePanel.setBackground(Color.WHITE);
		startTimePanel.add(startstopTimeLabel,BorderLayout.NORTH);
		startTimePanel.add(startTimeSlider,BorderLayout.SOUTH);

		JSlider stopTimeSlider = new JSlider(tmodel.getExtentEditor());
		stopTimeSlider.setPaintTrack(false);
		stopTimeSlider.setMajorTickSpacing(4);
		stopTimeSlider.setMinorTickSpacing(1);
		stopTimeSlider.setPaintTicks(true);
		stopTimeSlider.setPaintLabels(true);
		stopTimeSlider.setBackground(Color.WHITE);
		stopTimeSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 12));		
		stopTimeSlider.setLabelTable(startStopLabels);	
		JPanel startStopPanel = new JPanel();
		startStopPanel.setLayout(new BorderLayout());
		startStopPanel.setBackground(Color.WHITE);
		startStopPanel.setVisible(true);
		startStopPanel.add(startTimePanel,BorderLayout.NORTH);
		startStopPanel.add(stopTimeSlider,BorderLayout.SOUTH);		
	}
}

