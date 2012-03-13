package edu.uiuc.TCIP.education.lessons.development.lesson7;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.NumberFormatter;

import edu.uiuc.power.dataobjects.*;
import edu.uiuc.power.dataobjects.PowerSystem.PowerSystemSolutionMethod;
import edu.uiuc.power.displayobjects.*;
import edu.uiuc.power.displayobjects.ImageDisplay.ImageDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.ImageDisplay.ImageDisplayAlignmentVertical;
import edu.uiuc.power.displayobjects.loads.CityDisplay;
import edu.uiuc.power.displayobjects.loads.CityDisplayForTimeVaryingLoad;

public class Lesson7Panel extends JPanel implements ItemListener {
	final int initialLoadPayment = 90;
	
	AnimatedPanelUsingPaintComponent circuitpanel;
	
	boolean includeFixedCostsAndEmissions = true;
	
	ArrayList<CostAndEmissionsProvider> gensWithCostsAndEmissions = null;
	ArrayList<CostAndEmissionsOverlay> costOverlays = new ArrayList<CostAndEmissionsOverlay>();
	
	Lesson4CostAndEmissionsPlotPanel costAndEmissionsPlotPanel = null;
	
	Lesson7Panel myself;
	
	WindGeneratorDataWithLinearCostAndEmissions genWind;

	JPanel SimControlPanel;
	
	JLabel loadPayLabel;
	JSlider loadPaySlider;
	JCheckBox windIsVariableCBox;
	
	JSlider windVariationSlider;
	double initialWindVariation = 10;
	
	int animationsStepsPerPlotUpdate = 5;
	int plotUpdateCounter = 0;
	
	ArrayList<LoadDataWithLinearPayment> loadsWithPayment = new ArrayList<LoadDataWithLinearPayment>();
	
	TimeLabel tlabel;
	TotalCostLabel tcostlabel;
	TotalEmissionsLabel temitlabel;
	JButton StartSim,StopSim,ResetSim,ResetState,ShowPlotWindow;
	JButton IncludeFixedCosts;
	JButton hideCostsButton;
	BranchOverloadMonitorParameters overloadMonitorParams;
	JCheckBox branchOverloadMonitorCheckbox;

	boolean toggleRepaint;

	static long fps = 30;
	static long period = 1000L/fps;
	static int minutesPerAnimationStep = 5;
	PowerSystem ps;
	TransmissionAndDistributionCosts tdcosts;
	TransmissionAndDistributionCostsOverlay tdoverlay;
	protected boolean totalLoadPlotAdded = false;

	protected long lastRepaintTime = 0;

	protected long previousRepaintTime = 0;
	SimulationClock simClock;
	WindManager windGenManager;
	SimpleDateFormat sdformat = new SimpleDateFormat("H:m");
	final double initialWindGen = Math.pow(4.8,3);
	final double maxWindGen = Math.pow(5.8,3);	
	private void setWindMWManager() {
		windGenManager = new WindManager(genWind,simClock,true);
		try {
			windGenManager.addPoint(sdformat.parse("00:00"), Math.pow(4.8,3));
			windGenManager.addPoint(sdformat.parse("06:00"), Math.pow(4.75,3));
			windGenManager.addPoint(sdformat.parse("12:00"), Math.pow(5.5,3));
			windGenManager.addPoint(sdformat.parse("16:00"), maxWindGen);
			windGenManager.addPoint(sdformat.parse("17:00"), Math.pow(5.75,3));
			windGenManager.addPoint(sdformat.parse("21:00"), Math.pow(4.75,3));
			windGenManager.addPoint(sdformat.parse("24:00"), Math.pow(4.8,3));
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		windGenManager.setScaleAllPoints(4.0);
		circuitpanel.getAnimatables().add(windGenManager);
		windGenManager.animationstep();
//		maxWindTimeSeries = new MaxMWTimeSeries(simClock,genWind,"Maximum wind output available");
	}
	private void initializeDisplay() {
		myself = this;
		// Create the main display panel
		circuitpanel = new AnimatedPanelUsingPaintComponent(800,810,period,16,true);
		// Initialize clock for the simulator
		Date startDate = null;
		try {
			startDate = sdformat.parse("6:0");
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		final int minutesPerStep = 5;
		simClock = new SimulationClock(startDate,minutesPerStep,true,10);
		simClock.setClockState(true);		
		// add the clock so it gets animated

		circuitpanel.getAnimatables().add(simClock);
		overloadMonitorParams = new BranchOverloadMonitorParameters(true,150);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setupPowerSystem();
		setWindMWManager();		
		ps.saveState();
		
		circuitpanel.setBackground(Color.WHITE);
		add(circuitpanel,BorderLayout.CENTER);
		
		JPanel mainFrameControlPanel = new JPanel();
		mainFrameControlPanel.setLayout(new FlowLayout());
		mainFrameControlPanel.setBackground(new Color(255,255,255,0));
		mainFrameControlPanel.setOpaque(false);
		
		branchOverloadMonitorCheckbox = new JCheckBox("Monitor branch overloads");
		branchOverloadMonitorCheckbox.setSelected(overloadMonitorParams.getStatus());
		branchOverloadMonitorCheckbox.addItemListener(this);
		branchOverloadMonitorCheckbox.setBackground(Color.WHITE);

		
		costAndEmissionsPlotPanel = new Lesson4CostAndEmissionsPlotPanel("Applet 3: Generator costs and emissions",ps,tdcosts,50,minutesPerAnimationStep,1.0/100.0,0,3000);
		costAndEmissionsPlotPanel.setSize(800,400);
		costAndEmissionsPlotPanel.setVisible(false);
				
		//mainFrameControlPanel.add(branchOverloadMonitorCheckbox,BorderLayout.WEST);

		// Set up simulation start/stop/reset buttons on SimControlPanel
		SimControlPanel = new JPanel();
		StartSim = new JButton("Start Time");
		StartSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StartSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				costAndEmissionsPlotPanel.startIntegration();
				StartSim.setEnabled(false);
				StopSim.setEnabled(true);
				tlabel.setPaused(false);
				genWind.setAnimating(true);
				//loadCommercial.setPaused(false);
				//loadIndustrial.setPaused(false);
				//loadResidential.setPaused(false);				
			}			
		});
		StopSim = new JButton("Pause Time");
		StopSim.setEnabled(false);
		StopSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		StopSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (StopSim.getText().equalsIgnoreCase("Pause Time")) {
					costAndEmissionsPlotPanel.stopIntegration();
					//StartSim.setEnabled(true);
					//StopSim.setEnabled(false);
					tlabel.setPaused(true);
					genWind.setAnimating(false);
					//loadCommercial.setPaused(true);
					//loadIndustrial.setPaused(true);
					//loadResidential.setPaused(true);
					StopSim.setText("Resume Time");
				} else {
					costAndEmissionsPlotPanel.startIntegration();
					tlabel.setPaused(false);
					genWind.setAnimating(true);
					StopSim.setText("Pause Time");
					
				}
			}
		});
		ResetSim = new JButton("Reset Time");
		ResetSim.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		ResetSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tlabel.resettime();
				//loadCommercial.resettime();
				//loadResidential.resettime();
				//loadIndustrial.resettime();
				costAndEmissionsPlotPanel.resetTime();
				if (!costAndEmissionsPlotPanel.getIntegrating()) {
					if (StopSim.getText().equalsIgnoreCase("Pause Time")) 
						costAndEmissionsPlotPanel.startIntegration();
				}
			}
		});
		
		SimControlPanel.setLayout(new FlowLayout());
		//SimControlPanel.add(StartSim);
		//SimControlPanel.add(IncludeFixedCosts);
		//SimControlPanel.add(new JLabel("|"));		
		SimControlPanel.add(StopSim);
		SimControlPanel.add(ResetSim);
		SimControlPanel.add(new JLabel("|"));
		SimControlPanel.setBackground(Color.WHITE);
		/*
		mainFrameControlPanel.add(new JLabel("|"),BorderLayout.CENTER);
		mainFrameControlPanel.add(StartSim,BorderLayout.CENTER);
		mainFrameControlPanel.add(StopSim,BorderLayout.CENTER);
		mainFrameControlPanel.add(ResetSim,BorderLayout.CENTER);
		mainFrameControlPanel.add(new JLabel("|"),BorderLayout.CENTER);
		plotLoad = new Lesson2TotalLoadPlotPanel("Lesson 2: Total Load Plot",ps,
				50,
				minutesPerAnimationStep,
				1.0/100.0,
				0,3000);
		plotLoad.setSize(700,300);
		plotLoad.setVisible(false);
		
		//plotLoad.getContentPane().add(SimControlPanel,BorderLayout.PAGE_START);
		//plotLoad.getControlPanel().add(SimControlPanel,BorderLayout.CENTER);
		//add(SimControlPanel,BorderLayout.PAGE_START);
		
		
		mainFrameControlPanel.add(ShowPlotWindow,BorderLayout.EAST);
		mainFrameControlPanel.add(new JLabel("|"),BorderLayout.CENTER);
		*/
		
		ShowPlotWindow = new JButton("Show Plot");
		ShowPlotWindow.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		ShowPlotWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				costAndEmissionsPlotPanel.setVisible(true);
			}
		});
		SimControlPanel.add(ShowPlotWindow);
		SimControlPanel.add(new JLabel("|"));
		
		JButton PrintCircuit = new JButton("Print System");
		PrintCircuit.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		PrintCircuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintUtilities.printComponent(myself);
			}
		});
		SimControlPanel.add(PrintCircuit);
		SimControlPanel.add(new JLabel("|"));
		mainFrameControlPanel.add(SimControlPanel);
		
		ResetState = new JButton("Reset System");
		ResetState.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		ResetState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//plotLoad.stopIntegration();
				//StartSim.setEnabled(true);
				//StopSim.setEnabled(false);
				//loadCommercial.setPaused(true);
				//loadIndustrial.setPaused(true);
				//loadResidential.setPaused(true);
				
				ps.restoreState();
				loadPaySlider.setValue(initialLoadPayment);
				windIsVariableCBox.setSelected(true);
				//tlabel.resettime();
				//loadCommercial.resettime();
				//loadResidential.resettime();
				//loadIndustrial.resettime();

				//plotLoad.resetTime();
				
				/*
				if (totalloadplot.getIntegrationStatus() == totalloadplot.STOPPED)
					StartSim.setEnabled(true);
					*/
			}
		});
		mainFrameControlPanel.add(ResetState);
		
		windIsVariableCBox = new JCheckBox("Variable wind",true);
		windIsVariableCBox.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		windIsVariableCBox.setBackground(Color.WHITE);
		windIsVariableCBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					genWind.setVariation(initialWindVariation);	
				else
					genWind.setVariation(0);
			}
		});
		mainFrameControlPanel.add(new JLabel("|"));
		mainFrameControlPanel.add(windIsVariableCBox);
		/*
		windVariationSlider = new JSlider(0,100,1);
		windVariationSlider.setMajorTickSpacing(10);
		windVariationSlider.setMinorTickSpacing(1);
		windVariationSlider.setValue((int)(200.0*genWind.getVariation()));
		windVariationSlider.setBackground(new Color(255,255,255,0));
		//windVariationSlider.setPaintTicks(true);
		//windVariationSlider.setPaintLabels(true);
		windVariationSlider.setBackground(Color.WHITE);
		windVariationSlider.setFont(new Font("Lucida Bright", Font.PLAIN, 8));
		JPanel windVariationPanel  = new JPanel();
		windVariationPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		windVariationPanel.setBackground(new Color(255,255,255,0));
		windVariationPanel.setOpaque(false);
		windVariationPanel.add(new JLabel("Wind variation: "));
		windVariationPanel.add(new JLabel("None"));
		windVariationPanel.add(windVariationSlider);
		windVariationPanel.add(new JLabel("High"));

		windVariationSlider.addChangeListener(new ChangeListener()  {

			public void stateChanged(ChangeEvent arg0) {
				genWind.setVariation((double)windVariationSlider.getValue()/200.0);
			}
		
		});
		*/
		//mainFrameControlPanel.add(windVariationPanel);
		//mainFrameControlPanel.add(SimControlPanel,BorderLayout.CENTER);

		add(mainFrameControlPanel,BorderLayout.PAGE_END);
		//add(windVariationPanel,BorderLayout.PAGE_END);

		// start with it stopped
		/*
		plotLoad.stopIntegration();
		StartSim.setEnabled(true);
		StopSim.setEnabled(false);
		*/
		tlabel.setPaused(true);
		genWind.setAnimating(false);
		
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
	                        	ps.solve();
	                        	if (plotUpdateCounter++ > animationsStepsPerPlotUpdate) {
	                        		plotUpdateCounter = 0;
		                        	costAndEmissionsPlotPanel.updatePlot();
	                        	}
	                        	ArrayList<Animatable> animatables = circuitpanel.getAnimatables();
	                        	for (int i = 0; i < animatables.size(); i++) {
	                        		animatables.get(i).animationstep();
	                        	}
	                        	
	                        	//plotLoad.updatePlot();

	                        	if (toggleRepaint) {
	                        		myself.repaint();
	                        		//plotLoad.repaint();
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
		
		final String hideCostsHideText = "<html><center>Hide Costs/Payments/Emissions</center></html>";
		final String hideCostsUnhideText = "<html><center>Show Costs/Payments/Emissions</center></html>";
		
		hideCostsButton = new JButton(hideCostsHideText);
		hideCostsButton.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		hideCostsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hideCostsButton.getText().equalsIgnoreCase(hideCostsHideText)) {
					for (int i = 0; i < costOverlays.size(); i++)
						costOverlays.get(i).setVisible(false);
					tdoverlay.setVisible(false);
					hideCostsButton.setText(hideCostsUnhideText);
				} else {
					for (int i = 0; i < costOverlays.size(); i++)
						costOverlays.get(i).setVisible(true);
					tdoverlay.setVisible(true);
					hideCostsButton.setText(hideCostsHideText);
				}
			}
		});
		
		/*
		IncludeFixedCosts = new JButton("Include Fixed Costs");
		if (includeFixedCostsAndEmissions)
			IncludeFixedCosts.setText("Ignore Fixed Costs");
		else
			IncludeFixedCosts.setText("Include Fixed Costs");
		
		IncludeFixedCosts.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		IncludeFixedCosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				includeFixedCostsAndEmissions = !includeFixedCostsAndEmissions;
				tdcosts.setIncludeFixedCostsAndEmissions(includeFixedCostsAndEmissions);
				for (int i = 0; i < gensWithCostsAndEmissions.size(); i++) {
					CostAndEmissionsProvider tempGen = gensWithCostsAndEmissions.get(i);
					tempGen.setIncludeFixedCostsAndEmissions(includeFixedCostsAndEmissions);
				}
				if (includeFixedCostsAndEmissions)
					IncludeFixedCosts.setText("Ignore Fixed Costs");
				else
					IncludeFixedCosts.setText("Include Fixed Costs");				
			}
		});
		*/
		loadPayLabel = new JLabel("Load Payment");
		loadPayLabel.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		loadPayLabel.setHorizontalAlignment(JLabel.CENTER);
		
		int maxPayment = 200;
		loadPaySlider = new JSlider(0,maxPayment,0);
		loadPaySlider.setFont(new Font("Lucida Bright", Font.PLAIN, 10));
		loadPaySlider.setMajorTickSpacing(maxPayment/5);
		loadPaySlider.setMinorTickSpacing(maxPayment/10);
		loadPaySlider.setPaintTicks(true);
		loadPaySlider.setPaintLabels(true);
		loadPaySlider.setBackground(Color.WHITE);
		loadPaySlider.addChangeListener(new ChangeListener()  {

			public void stateChanged(ChangeEvent arg0) {
				DecimalFormat costFormat = new DecimalFormat("$#,##0");
				NumberFormatter costFormatter = new NumberFormatter(costFormat);
				try {
					loadPayLabel.setText("Load Payment: " + costFormatter.valueToString(loadPaySlider.getValue()) + "/MWh");
				} catch (ParseException pe) {
					loadPayLabel.setText("Error parsing load payment amount");
				}
				
				for (int i = 0; i < loadsWithPayment.size(); i++) {
					loadsWithPayment.get(i).setPaymentPerMWh(loadPaySlider.getValue());
				}
			}
		
		});
		loadPaySlider.setValue(initialLoadPayment);
		
		JLabel costControlLabel = new JLabel();
		costControlLabel.setText("Cost Controls");
		costControlLabel.setHorizontalAlignment(JLabel.CENTER);
		costControlLabel.setFont(new Font("Lucida Bright", Font.BOLD, 14));
		
		circuitpanel.setLayout(null);
		
		circuitpanel.add(costControlLabel);
		costControlLabel.setBounds(580,0,220,25);

		circuitpanel.add(hideCostsButton);
		hideCostsButton.setMargin(new Insets(0,0,0,0));
		hideCostsButton.setBounds(580,25,220,20);
		
		hideCostsButton.doClick();
		
		/*
		circuitpanel.add(IncludeFixedCosts);
		IncludeFixedCosts.setBounds(580,45,220,20);
		*/
		
		circuitpanel.add(loadPayLabel);
		loadPayLabel.setBounds(580,45,220,20);
		
		circuitpanel.add(loadPaySlider);
		loadPaySlider.setBounds(580,65,200,45);
		
		ShapeContainer linesInURight = new ShapeContainer(new BasicStroke((float) 1.0), Color.BLACK);
		linesInURight.addShape(new Rectangle2D.Double(579,0,220,110));
		//linesInURight.addShape(new Line2D.Double(579,-1,579,130));
		//linesInURight.addShape(new Line2D.Double(579,130,800,130));
		
		circuitpanel.getTopLayerRenderables().add(linesInURight);
		tcostlabel = new TotalCostLabel(new Point2D.Double(295.,535.),12,Color.BLACK,0,costAndEmissionsPlotPanel);
		circuitpanel.getTopLayerRenderables().add(tcostlabel);
		temitlabel = new TotalEmissionsLabel(new Point2D.Double(305.,535.),12,Color.BLACK,0,costAndEmissionsPlotPanel);
		circuitpanel.getTopLayerRenderables().add(temitlabel);
	}
	
	public Lesson7Panel() {
		initializeDisplay();
	}
	
	private void addUSAGraphic(){
        InputStream is = getClass().getResourceAsStream("usaMap.gif");
        //System.out.println(getImagePath());
        try {
			BufferedImage image = ImageIO.read(is);
	        //transparent = createTransparentImage(image);
			ImageDisplay id = new ImageDisplay(new Point2D.Double(0,0),ImageDisplay.ImageDisplayAlignmentVertical.TOP,ImageDisplay.ImageDisplayAlignmentHorizontal.LEFT,"",image,1);
			circuitpanel.getBottomLayerRenderables().add(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupPowerSystem() {
		
		// Set up display elements
		
		
		//circuitpanel = new AnimatedPanelUsingPaintComponent(800,810,period,16,true);	
		addUSAGraphic();
		
		// Create nodes
		//NodeData nodeExternalSystem = new NodeData(0,1.0,0,true);
		//NodeData nodeExternalSystem2 = new NodeData(999,1.0,0,false,true);
		NodeData nodeOne = new NodeData(1,1.0,0,false);
		NodeData nodeTwo = new NodeData(2,1.0,0,false);
		NodeData nodeThree = new NodeData(3,1.0,0,false);
		NodeData nodeFour = new NodeData(4,1.0,0,false);
		NodeData nodeFive = new NodeData(5,1.0,0,false);
		NodeData nodeFifteen = new NodeData(15,1.0,0,false,true);
		NodeData nodeSix = new NodeData(6,1.0,0,false);
		NodeData nodeEight = new NodeData(8,1.0,0,false,true);
		NodeData nodeNine = new NodeData(9,1.0,0,false,true);
		NodeData nodeTen = new NodeData(10,1.0,0,false,true);
		NodeData nodeTwelve = new NodeData(12,1.0,0,false);
		NodeData nodeThirteen = new NodeData(13,1.0,0,false);
		NodeData nodeFourteen = new NodeData(14,1.0,0,false);
		NodeData nodeSixteen = new NodeData(16,1.0,0,false,true);
		
		// Create lines
//		BranchData branchExternalSystemToOne = new BranchData(nodeExternalSystem,nodeOne,0.01,0,1500.0,true);
		BranchData branchOneToTwo = new BranchData(nodeOne,nodeTwo,0.01,0,1500.0,true);
		BranchData branchTwoToThree = new BranchData(nodeTwo,nodeThree,0.01,0,1500.0,true);
		BranchData branchTwoToFour = new BranchData(nodeTwo,nodeFour,0.01,0,1500.0,true);
		BranchData branchOneToFive = new BranchData(nodeOne,nodeFive,0.01,0,1500.0,true);
		BranchData branchFiveToFifteen = new BranchData(nodeFive,nodeFifteen,0.01,0,1500.0,true);
		BranchData branchFiveToSix = new BranchData(nodeFive,nodeSix,0.01,0,1500.0,true);
		BranchData branchFourToSix = new BranchData(nodeFour,nodeSix,0.01,0,1500.0,true);
		BranchData branchTwoToEight = new BranchData(nodeTwo,nodeEight,0.01,0,1500.0,true);
		BranchData branchThreeToNine = new BranchData(nodeThree,nodeNine,0.01,0,1500.0,true);
		//BranchData branchThreeToFourteen = new BranchData(nodeThree,nodeFourteen,0.01,0,1500.0,true);
		BranchData branchThreeToFourteen = new BranchData(nodeThree,nodeFourteen,0.01,0,1000.0,true); // to Industryville
		//BranchData branchSixToThirteen = new BranchData(nodeSix,nodeThirteen,0.01,0,1500.0,true);
		BranchData branchSixToThirteen = new BranchData(nodeSix,nodeThirteen,0.01,0,2000.0,true); // to Residenceburg
		BranchData branchSixToSixteen = new BranchData(nodeSix,nodeSixteen,0.01,0,1500.0,true); // to wind generator
//		BranchData branchSixToExternalSystemTwo = new BranchData(nodeSix,nodeExternalSystem2,0.01,0,1500.0,true);
		//BranchData branchFiveToTwelve = new BranchData(nodeFive,nodeTwelve,0.01,0,1500.0,true);
		BranchData branchFiveToTwelve = new BranchData(nodeFive,nodeTwelve,0.01,0,1000.0,true); // to Commerceton
		BranchData branchFourToTen = new BranchData(nodeFour,nodeTen,0.01,0,1500.0,true);
		
		// Create loads
		
		LoadDataWithLinearPayment loadCommercial = new LoadDataWithLinearPayment(nodeTwelve,850.0,0,true,initialLoadPayment);
		loadsWithPayment.add(loadCommercial);
		LoadDataWithLinearPayment loadIndustrial = new LoadDataWithLinearPayment(nodeFourteen,850.0,0,true,initialLoadPayment);
		loadsWithPayment.add(loadIndustrial);
		LoadDataWithLinearPayment loadResidential = new LoadDataWithLinearPayment(nodeThirteen,1700.0,0,true,initialLoadPayment);
		loadsWithPayment.add(loadResidential);
		
		gensWithCostsAndEmissions = new ArrayList<CostAndEmissionsProvider>();
		
		// Create gens
		//GeneratorDataWithLinearCostAndEmissions
		GeneratorDataWithLinearCostAndEmissions genHydro = new GeneratorDataWithLinearCostAndEmissions(nodeEight,1000,0,1000,0,0,9999,true,1500,3.0,0,0,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genHydro);
		//GeneratorData genHydro = new GeneratorData(nodeEight,1000,500,1000,0,0,9999,true);
//		GeneratorDataWithExportImportPenaltiesAndEmissions genSlack = new GeneratorDataWithExportImportPenaltiesAndEmissions(nodeExternalSystem,0,-10000,10000,0,0,9999,true,30.0,0.6);
//		gensWithCostsAndEmissions.add(genSlack);
//		GeneratorDataWithExportImportPenaltiesAndEmissions genExternal2 = new GeneratorDataWithExportImportPenaltiesAndEmissions(nodeExternalSystem2,0,-10000,10000,0,0,9999,true,30.0,0.6);
//		gensWithCostsAndEmissions.add(genExternal2);
		GeneratorDataWithLinearCostAndEmissions genCoal = new GeneratorDataWithLinearCostAndEmissions(nodeNine,600,0,700,0,0,9999,true,2000,20.0,0,1.0,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genCoal);
		GeneratorDataWithLinearCostAndEmissions genGas = new GeneratorDataWithLinearCostAndEmissions(nodeFifteen,200,0,500,0,0,9999,true,700.0,70.0,0,0.5,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genGas);
		GeneratorDataWithLinearCostAndEmissions genNuke = new GeneratorDataWithLinearCostAndEmissions(nodeTen,900,0,900,0,0,9999,true,6500.0,6.0,0,0,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genNuke);
		//GeneratorData genWind = new GeneratorData(nodeSixteen,54,54,54,0,0,9999,true);
		genWind = new WindGeneratorDataWithLinearCostAndEmissions(nodeSixteen,initialWindGen,0,210,initialWindVariation,true,200.0,0,0,0,includeFixedCostsAndEmissions);
		circuitpanel.getAnimatables().add(genWind);
		gensWithCostsAndEmissions.add(genWind);
		
		tdcosts = new TransmissionAndDistributionCosts(25000,includeFixedCostsAndEmissions);
		
		// Register with power system
		ps = new PowerSystem(PowerSystemSolutionMethod.ZEB_OPF);
		
//		ps.addNode(nodeExternalSystem);
//		ps.addNode(nodeExternalSystem2);
		ps.addNode(nodeOne);
		ps.addNode(nodeTwo);
		ps.addNode(nodeThree);
		ps.addNode(nodeFour);
		ps.addNode(nodeFive);
		ps.addNode(nodeFifteen);
		ps.addNode(nodeSix);
		ps.addNode(nodeEight);
		ps.addNode(nodeNine);
		ps.addNode(nodeTen);
		ps.addNode(nodeTwelve);
		ps.addNode(nodeThirteen);
		ps.addNode(nodeFourteen);
		ps.addNode(nodeSixteen);
		
		
//		ps.addBranch(branchExternalSystemToOne);
		ps.addBranch(branchOneToTwo);
		ps.addBranch(branchOneToFive);
		ps.addBranch(branchTwoToThree);
		ps.addBranch(branchTwoToFour);
		ps.addBranch(branchFiveToFifteen);
		ps.addBranch(branchFiveToSix);
		ps.addBranch(branchFourToSix);
		ps.addBranch(branchTwoToEight);
		ps.addBranch(branchThreeToNine);
		ps.addBranch(branchThreeToFourteen);
		ps.addBranch(branchSixToThirteen);
		ps.addBranch(branchSixToSixteen);
//		ps.addBranch(branchSixToExternalSystemTwo);
		ps.addBranch(branchFiveToTwelve);
		ps.addBranch(branchFourToTen);
		
		ps.addLoad(loadCommercial);
		ps.addLoad(loadIndustrial);
		ps.addLoad(loadResidential);
		
		ps.addGenerator(genHydro);
//		ps.addGenerator(genSlack);
		ps.addGenerator(genCoal);
		ps.addGenerator(genGas);
		ps.addGenerator(genNuke);
		ps.addGenerator(genWind);
//		ps.addGenerator(genExternal2);
		
		ps.solve();
		
		ArrayList<LineAndDistanceInfoProvider> lines; 
		FlowArrowsForBranchData fA;
		
		BranchColorProvider branchColorProvider = new BranchColorDynamic(Color.BLACK,0.85,Color.ORANGE,1.0,Color.RED);
		BranchThicknessProvider branchThicknessProvider = new BranchThicknessDynamic(1.0,0.85,2.0,1.0,3.0);
		FlowArrowColorProvider flowArrowColorProvider = new FlowArrowColorDynamic(new Color(0,255,0,255/2),0.85,new Color(255,128,64,255/2),1.0,new Color(255,0,0,255/2));
		
		double switchthickness = 5.0;
		
//		// Line External System to One
//		ArrayList<Point2D.Double> pointsExternalToOne = new ArrayList<Point2D.Double>();
//		pointsExternalToOne.add(new Point2D.Double(50,40));
//		pointsExternalToOne.add(new Point2D.Double(150,40));
//		pointsExternalToOne.add(new Point2D.Double(200,40));
//		pointsExternalToOne.add(new Point2D.Double(350,40));
//		pointsExternalToOne.add(new Point2D.Double(350,100));
//		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsExternalToOne, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchExternalSystemToOne, branchColorProvider, circuitpanel, overloadMonitorParams);
//		fA = new FlowArrowsForBranchData(branchExternalSystemToOne,lines,0.05,20.0,10.0,flowArrowColorProvider);
//		circuitpanel.getMiddleLayerRenderables().add(fA);
//		circuitpanel.getAnimatables().add(fA);
		
		// Line One to Two
		ArrayList<Point2D.Double> pointsOneToTwo = new ArrayList<Point2D.Double>();
		pointsOneToTwo.add(new Point2D.Double(325,100));
		pointsOneToTwo.add(new Point2D.Double(325,150));
		pointsOneToTwo.add(new Point2D.Double(200,150));
		pointsOneToTwo.add(new Point2D.Double(150,150));
		pointsOneToTwo.add(new Point2D.Double(125,150));
		pointsOneToTwo.add(new Point2D.Double(125,200));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsOneToTwo, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchOneToTwo, branchColorProvider, circuitpanel,true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchOneToTwo,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		
		// Line One to Five
		ArrayList<Point2D.Double> pointsOneToFive = new ArrayList<Point2D.Double>();
		pointsOneToFive.add(new Point2D.Double(375,100));
		pointsOneToFive.add(new Point2D.Double(375,150));
		pointsOneToFive.add(new Point2D.Double(400,150));
		pointsOneToFive.add(new Point2D.Double(450,150));
		pointsOneToFive.add(new Point2D.Double(475,150));
		pointsOneToFive.add(new Point2D.Double(475,200));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsOneToFive, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchOneToFive, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchOneToFive,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);

		// Line Two to Three
		ArrayList<Point2D.Double> pointsTwoToThree = new ArrayList<Point2D.Double>();
		pointsTwoToThree.add(new Point2D.Double(100,200));
		pointsTwoToThree.add(new Point2D.Double(100,250));
		pointsTwoToThree.add(new Point2D.Double(90,250));
		pointsTwoToThree.add(new Point2D.Double(40,250));
		pointsTwoToThree.add(new Point2D.Double(30,250));
		pointsTwoToThree.add(new Point2D.Double(30,300));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsTwoToThree, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchTwoToThree, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchTwoToThree,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		
		// Line Two to Eight
		ArrayList<Point2D.Double> pointsTwoToEight = new ArrayList<Point2D.Double>();
		pointsTwoToEight.add(new Point2D.Double(150,200));
		pointsTwoToEight.add(new Point2D.Double(200,200));
		pointsTwoToEight.add(new Point2D.Double(250,200));
		pointsTwoToEight.add(new Point2D.Double(275,200));
		pointsTwoToEight.add(new Point2D.Double(275,225));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsTwoToEight, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchTwoToEight, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchTwoToEight,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		
		// Line Three to Nine
		ArrayList<Point2D.Double> pointsThreeToNine = new ArrayList<Point2D.Double>();
		pointsThreeToNine.add(new Point2D.Double(50,350));
		pointsThreeToNine.add(new Point2D.Double(50,360));
		pointsThreeToNine.add(new Point2D.Double(50,410));
		pointsThreeToNine.add(new Point2D.Double(75,500));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsThreeToNine, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchThreeToNine, branchColorProvider, circuitpanel, true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchThreeToNine,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		
		// Line Three to Fourteen
		ArrayList<Point2D.Double> pointsThreeToFourteen = new ArrayList<Point2D.Double>();
		pointsThreeToFourteen.add(new Point2D.Double(100,350));
		pointsThreeToFourteen.add(new Point2D.Double(170,360));
		pointsThreeToFourteen.add(new Point2D.Double(170,375));
		pointsThreeToFourteen.add(new Point2D.Double(170,415));
		pointsThreeToFourteen.add(new Point2D.Double(170,450));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsThreeToFourteen, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchThreeToFourteen, branchColorProvider, circuitpanel,true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchThreeToFourteen,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		
		// Line Two to Four
		ArrayList<Point2D.Double> pointsTwoToFour = new ArrayList<Point2D.Double>();
		pointsTwoToFour.add(new Point2D.Double(125,200));
		pointsTwoToFour.add(new Point2D.Double(150,300));
		pointsTwoToFour.add(new Point2D.Double(175,300));
		pointsTwoToFour.add(new Point2D.Double(225,300));
		pointsTwoToFour.add(new Point2D.Double(250,300));
		pointsTwoToFour.add(new Point2D.Double(250,390));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsTwoToFour, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchTwoToFour, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchTwoToFour,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);			
		
		// Line Four to Ten
		ArrayList<Point2D.Double> pointsFourToTen = new ArrayList<Point2D.Double>();
		pointsFourToTen.add(new Point2D.Double(250,390));
		pointsFourToTen.add(new Point2D.Double(250,465));
		pointsFourToTen.add(new Point2D.Double(400,465));
		pointsFourToTen.add(new Point2D.Double(450,465));
		pointsFourToTen.add(new Point2D.Double(510,465));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFourToTen, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchFourToTen, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchFourToTen,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);			
		
		// Line Five To Twelve
		ArrayList<Point2D.Double> pointsFiveToTwelve = new ArrayList<Point2D.Double>();
		pointsFiveToTwelve.add(new Point2D.Double(480,200));
		pointsFiveToTwelve.add(new Point2D.Double(560,200));
		pointsFiveToTwelve.add(new Point2D.Double(560,150));
		pointsFiveToTwelve.add(new Point2D.Double(560,100));
		pointsFiveToTwelve.add(new Point2D.Double(560,50));
		pointsFiveToTwelve.add(new Point2D.Double(475,50));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFiveToTwelve, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchFiveToTwelve, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchFiveToTwelve,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);	
		
		// Line Five To Fifteen
		ArrayList<Point2D.Double> pointsFiveToFifteen = new ArrayList<Point2D.Double>();
		pointsFiveToFifteen.add(new Point2D.Double(470,200));
		SimpleLineDisplay tempLineFiveToFifteen = new SimpleLineDisplay(pointsFiveToFifteen.get(0),new Point2D.Double(370,300),1.0);
		pointsFiveToFifteen.add(DrawUtilities.getPointAtDistanceOnLine(tempLineFiveToFifteen, tempLineFiveToFifteen.getLength()/3));
		pointsFiveToFifteen.add(DrawUtilities.getPointAtDistanceOnLine(tempLineFiveToFifteen, 0.75*tempLineFiveToFifteen.getLength()));
		pointsFiveToFifteen.add(tempLineFiveToFifteen.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFiveToFifteen, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchFiveToFifteen, branchColorProvider, circuitpanel, true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchFiveToFifteen,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);	
		
		// Line Five To Six
		ArrayList<Point2D.Double> pointsFiveToSix = new ArrayList<Point2D.Double>();
		pointsFiveToSix.add(new Point2D.Double(475,205));
		pointsFiveToSix.add(new Point2D.Double(475,235));
		pointsFiveToSix.add(new Point2D.Double(475,265));
		pointsFiveToSix.add(new Point2D.Double(475,325));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFiveToSix, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchFiveToSix, branchColorProvider, circuitpanel,true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchFiveToSix,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);	
		
		// Line Six To Thirteen
		ArrayList<Point2D.Double> pointsSixToThirteen = new ArrayList<Point2D.Double>();
		pointsSixToThirteen.add(new Point2D.Double(480,325));
		pointsSixToThirteen.add(new Point2D.Double(500,325));
		pointsSixToThirteen.add(new Point2D.Double(500,290));
		pointsSixToThirteen.add(new Point2D.Double(580,290));
		pointsSixToThirteen.add(new Point2D.Double(580,280));
		pointsSixToThirteen.add(new Point2D.Double(580,230));
		pointsSixToThirteen.add(new Point2D.Double(580,170));
		pointsSixToThirteen.add(new Point2D.Double(610,170));
		
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsSixToThirteen, 0, branchThicknessProvider, switchthickness, 4, Math.PI/20, branchSixToThirteen, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchSixToThirteen,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);	
		
		// Line Six To Sixteen
		ArrayList<Point2D.Double> pointsSixToSixteen = new ArrayList<Point2D.Double>();
		pointsSixToSixteen.add(new Point2D.Double(480,325));
		pointsSixToSixteen.add(new Point2D.Double(480,400));
		pointsSixToSixteen.add(new Point2D.Double(500,400));
		pointsSixToSixteen.add(new Point2D.Double(550,400));
		pointsSixToSixteen.add(new Point2D.Double(625,400));
		pointsSixToSixteen.add(new Point2D.Double(690,473));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsSixToSixteen, 0, branchThicknessProvider, switchthickness, 2, Math.PI/20, branchSixToSixteen, branchColorProvider, circuitpanel,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchSixToSixteen,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);	
		
//		// Line Six To External System 2
//		ArrayList<Point2D.Double> pointsSixToExternalSystem2 = new ArrayList<Point2D.Double>();
//		pointsSixToExternalSystem2.add(new Point2D.Double(480,325));
//		pointsSixToExternalSystem2.add(new Point2D.Double(535,350));
//		pointsSixToExternalSystem2.add(new Point2D.Double(585,350));
//		pointsSixToExternalSystem2.add(new Point2D.Double(625,350));
//		pointsSixToExternalSystem2.add(new Point2D.Double(625,290));
//		pointsSixToExternalSystem2.add(new Point2D.Double(665,290));
//		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsSixToExternalSystem2, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchSixToExternalSystemTwo, branchColorProvider, circuitpanel,true,overloadMonitorParams);
//		fA = new FlowArrowsForBranchData(branchSixToExternalSystemTwo,lines,0.05,20.0,10.0,flowArrowColorProvider);
//		circuitpanel.getMiddleLayerRenderables().add(fA);
//		circuitpanel.getAnimatables().add(fA);			
		
		// Line Four To Six
		ArrayList<Point2D.Double> pointsFourToSix = new ArrayList<Point2D.Double>();
		pointsFourToSix.add(new Point2D.Double(255,390));
		pointsFourToSix.add(new Point2D.Double(321,415));
		pointsFourToSix.add(new Point2D.Double(375,415));
		pointsFourToSix.add(new Point2D.Double(400,415));
		pointsFourToSix.add(new Point2D.Double(470,325));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFourToSix, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchFourToSix, branchColorProvider, circuitpanel, true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchFourToSix,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		
//		// Substation at node 1
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(300,85),1));
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
//		
//		// Substation at node 2
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(75,180),2));
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
//		
//		// Substation at node 3
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(10,300),3));
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}		
//		
//		// Substation at node 5
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(425,175),5));
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}		
//
//		// Substation at node 6
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(425,300),6));
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
//		
//		// Substation at node 4
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(200,365),4));
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
		
//		// External connection at node ExternalSystem
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new ExternalSystemDisplay(new Point2D.Double(20,20)));
//			CostAndEmissionsOverlay slackoverlay = new CostAndEmissionsOverlay(new Point2D.Double(76,72),genSlack);
//			costOverlays.add(slackoverlay);
//			circuitpanel.getTopLayerRenderables().add(slackoverlay);
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
		
//		// External connection at node ExternalSystem2
//		try {
//			circuitpanel.getMiddleLayerRenderables().add(new ExternalSystemDisplay(new Point2D.Double(650,255)));
//			CostAndEmissionsOverlay ext2overlay = new CostAndEmissionsOverlay(new Point2D.Double(689,304),genExternal2);
//			costOverlays.add(ext2overlay);
//			circuitpanel.getTopLayerRenderables().add(ext2overlay);
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
		
		double genScale = 0.75;
		// Coal plant at node 9
		try {
			CoalPlantDisplay coaldisplay = new CoalPlantDisplay(new Point2D.Double(0,425),0,0.0,genCoal);
			coaldisplay.setScale(genScale);
			circuitpanel.getMiddleLayerRenderables().add(coaldisplay);
			circuitpanel.addMouseListener(coaldisplay);
			CostAndEmissionsOverlay coaloverlay = new CostAndEmissionsOverlay(new Point2D.Double(76,490),genCoal);
			costOverlays.add(coaloverlay);
			circuitpanel.getTopLayerRenderables().add(coaloverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Hydro plant at node 8
		try {
			HydroelectricDisplay hydrodisplay = new HydroelectricDisplay(new Point2D.Double(240,175),0.0,genHydro);
			hydrodisplay.setScale(genScale);
			circuitpanel.getMiddleLayerRenderables().add(hydrodisplay);
			circuitpanel.addMouseListener(hydrodisplay);
			CostAndEmissionsOverlay hydrooverlay = new CostAndEmissionsOverlay(new Point2D.Double(294,252),genHydro);
			costOverlays.add(hydrooverlay);
			circuitpanel.getTopLayerRenderables().add(hydrooverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		

		
		// Nuke plant at node 10
		try {
			NukePlantDisplay nukedisplay = new NukePlantDisplay(new Point2D.Double(460,420),0.0,genNuke);
			nukedisplay.setScale(genScale);
			circuitpanel.getMiddleLayerRenderables().add(nukedisplay);
			circuitpanel.addMouseListener(nukedisplay);
			CostAndEmissionsOverlay nukeoverlay = new CostAndEmissionsOverlay(new Point2D.Double(513,496),genNuke);
			costOverlays.add(nukeoverlay);
			circuitpanel.getTopLayerRenderables().add(nukeoverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		// Gas plant at node 6
		try {
			GasPlantDisplay gasdisplay = new GasPlantDisplay(new Point2D.Double(300,260),0.0,genGas);
			gasdisplay.setScale(genScale);
			circuitpanel.getMiddleLayerRenderables().add(gasdisplay);
			circuitpanel.addMouseListener(gasdisplay);
			CostAndEmissionsOverlay gasoverlay = new CostAndEmissionsOverlay(new Point2D.Double(351,339),genGas);
			costOverlays.add(gasoverlay);
			circuitpanel.getTopLayerRenderables().add(gasoverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		

//		// Branch External to One flow label 
//		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(221,52),10,new Color(0f,0f,0f,1f),0,branchExternalSystemToOne));

		// Branch One to Two flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(200,125),10,new Color(0f,0f,0f,1f),0,branchOneToTwo));

		// Branch One to Five flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(375,155),10,new Color(0f,0f,0f,1f),0,branchOneToFive));
		
		// Branch Two to Eight flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(190,210),10,new Color(0f,0f,0f,1f),0,branchTwoToEight));
		
		// Branch Two to Three flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(10,225),10,new Color(0f,0f,0f,1f),0,branchTwoToThree));

		// Branch Two to Four flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(159,310),10,new Color(0f,0f,0f,1f),0,branchTwoToFour));
		
		// Branch Three to Nine flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(70,405),10,new Color(0f,0f,0f,1f),-Math.PI/2,branchThreeToNine));		

		// Branch Three to Fourteen flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(107,357),10,new Color(0f,0f,0f,1f),Math.PI/20,branchThreeToFourteen));		

		// Branch Five to Fifteen flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(404,276),10,new Color(0f,0f,0f,1f),-Math.PI/4,branchFiveToFifteen));		

		// Branch Five to Twelve flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(526,25),10,new Color(0f,0f,0f,1f),0,branchFiveToTwelve));		

		// Branch Six to Thirteen flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(601,282),10,new Color(0f,0f,0f,1f),-Math.PI/2,branchSixToThirteen));		

		// Branch Five to Six flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(487,240),10,new Color(0f,0f,0f,1f),0,branchFiveToSix));		

		// Branch Six to Sixteen flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(600,380),10,new Color(0f,0f,0f,1f),0,branchSixToSixteen));		

//		// Branch Six to ExternalSystem2 flow label 
//		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(535,325),10,new Color(0f,0f,0f,1f),0,branchSixToExternalSystemTwo));		
		
		// Branch Four to Six flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(415,415),10,new Color(0f,0f,0f,1f),-Math.PI/3.5,branchFourToSix));		

		// Branch Four to Ten flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(280,475),10,new Color(0f,0f,0f,1f),0,branchFourToTen));		
		
		// Commercial city at node 12
		try {
			CityDisplay commercetonLoad = new CityDisplay(new Point2D.Double(415,25),CityDisplay.CityType.COMMERCIAL,"Commerceton",loadCommercial);
			circuitpanel.getMiddleLayerRenderables().add(commercetonLoad);
			CostAndEmissionsOverlay commercetonOverlay = new CostAndEmissionsOverlay(new Point2D.Double(465,57),loadCommercial,false);
			costOverlays.add(commercetonOverlay);
			circuitpanel.getMiddleLayerRenderables().add(commercetonOverlay);
			circuitpanel.addMouseListener(commercetonLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		
		// Residential city at node 13
		try {
			CityDisplay residentialLoad = new CityDisplay(new Point2D.Double(603,135),CityDisplay.CityType.RESIDENTIAL,"Residenceburg",loadResidential);
			circuitpanel.getMiddleLayerRenderables().add(residentialLoad);
			CostAndEmissionsOverlay residentialOverlay = new CostAndEmissionsOverlay(new Point2D.Double(651,167),loadResidential,false);
			costOverlays.add(residentialOverlay);
			circuitpanel.getMiddleLayerRenderables().add(residentialOverlay);
			circuitpanel.addMouseListener(residentialLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		// Industrial city at node 13
		try {
			CityDisplay industrialLoad = new CityDisplay(new Point2D.Double(126,429),CityDisplay.CityType.INDUSTRIAL,"Industryville",loadIndustrial);
			circuitpanel.getMiddleLayerRenderables().add(industrialLoad);
			CostAndEmissionsOverlay industrialOverlay = new CostAndEmissionsOverlay(new Point2D.Double(172,446),loadIndustrial,false);
			costOverlays.add(industrialOverlay);
			circuitpanel.getMiddleLayerRenderables().add(industrialOverlay);
			circuitpanel.addMouseListener(industrialLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}		
		
		/*
		totalloadplot = new TotalLoadPlot(
				new Point2D.Double(10,10),
				700,200,
				ps,
				minutesPerAnimationStep,
				0,24,
				0,3000);
		*/
		/*
		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,20),12,Color.BLACK,0);
		circuitpanel.getTopLayerRenderables().add(mclabel);
		circuitpanel.addMouseMotionListener(mclabel);
		*/
		/*
		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,0),12,Color.BLACK,0);
		circuitpanel.getMiddleLayerRenderables().add(mclabel);
		circuitpanel.addMouseMotionListener(mclabel);
		*/
				
		
		tlabel = new TimeLabel(new Point2D.Double(0,0),12,Color.BLACK,0,minutesPerAnimationStep,0);
		//circuitpanel.getRenderables().add(tlabel);
		//circuitpanel.getAnimatables().add(tlabel);
		circuitpanel.getAnimatables().add(ps);
		
		// Wind plant at node sixteen
		try {
			WindPlantDisplay winddisplay = new WindPlantDisplay(new Point2D.Double(650,340),0.0,genWind);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay);
			circuitpanel.getAnimatables().add(winddisplay);
			circuitpanel.addMouseListener(winddisplay);
			CostAndEmissionsOverlay windoverlay = new CostAndEmissionsOverlay(new Point2D.Double(689,462),genWind);
			costOverlays.add(windoverlay);
			circuitpanel.getTopLayerRenderables().add(windoverlay);						
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// t&d costs
		tdoverlay = new TransmissionAndDistributionCostsOverlay(new Point2D.Double(335,510),tdcosts);
		circuitpanel.getTopLayerRenderables().add(tdoverlay);
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == branchOverloadMonitorCheckbox) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				overloadMonitorParams.setStatus(true);
			else
				overloadMonitorParams.setStatus(false);
		}
	}
}
