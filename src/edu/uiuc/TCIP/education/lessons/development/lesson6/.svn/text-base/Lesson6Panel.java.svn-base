package edu.uiuc.TCIP.education.lessons.development.lesson6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
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
import edu.uiuc.power.dataobjects.PowerSystem.PowerSystemSolutionMethod;
import edu.uiuc.power.displayobjects.*;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;
import edu.uiuc.power.displayobjects.ImageDisplay.ImageDisplayAlignmentVertical;
import edu.uiuc.power.displayobjects.loads.CityDisplay;

public class Lesson6Panel extends JPanel implements ItemListener {
	final int initialLoadPayment = 90;
	final int minutesPerStep = 30;

	GeneratorIconProvider generatorIconProvider = new GeneratorIconProvider();
	
	WindPlantDisplay winddisplay = null;
	CostAndEmissionsTimeSeries costAndEmissionsTimeSeries = null;
	DateFormat dFormat;
	
	//DecimalFormat decFormat = new DecimalFormat("##")
	DecimalFormat decimalFormat = new DecimalFormat("##0");
	NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
	
	StorageDevice genStorage;

	AnimatedPanelUsingPaintComponent circuitpanel;
	
	boolean includeFixedCostsAndEmissions = false;
	
	ArrayList<CostAndEmissionsProvider> gensWithCostsAndEmissions = null;
	ArrayList<CostAndEmissionsOverlay> costOverlays = new ArrayList<CostAndEmissionsOverlay>();
	
	Lesson6Panel myself;
	
	int animationsStepsPerPlotUpdate = 5;
	int plotUpdateCounter = 0;
	
	JButton StartSim,StopSim,ResetSim,ResetState,ShowPlotWindow;
	JButton IncludeFixedCosts;
	JButton hideCostsButton;
	
	JComboBox plotBranchBox, plotLoadBox, plotgenBox;
	JSlider windLineMaxSlider, windScaleSlider, loadScaleSlider;
	JLabel windLineMaxLabel, windScaleLabel, loadScaleLabel;
	
	BranchOverloadMonitorParameters overloadMonitorParams;
	JCheckBox branchOverloadMonitorCheckbox;
	
	ArrayList<JSlider> sliderControls = new ArrayList<JSlider>();
	ArrayList<Integer> sliderInitialValues = new ArrayList<Integer>();

	
	static long fps = 30;
	static long period = 1000L/fps;
	boolean toggleRepaint;
	protected long lastRepaintTime = 0;
	protected long previousRepaintTime = 0;
	
	PowerSystem ps;
	BranchData branchWindLocalSubstationToCitySubstation; 
	
	WindGeneratorDataWithLinearCostAndEmissions genWind;
	final double initialWindVariation = 1;
	final double initialWindGen = Math.pow(4.8,3);
	final double maxWindGen = Math.pow(5.8,3);
	WindManager windGenManager;
	
	LoadData loadResidential,loadCommercial,loadIndustrial;
	SimulationClock simClock;
	
	SimpleDateFormat sdformat = new SimpleDateFormat("H:m");
	
	ArrayList<MWTimeSeries> branchFlowInformation = new ArrayList<MWTimeSeries>();
	ArrayList<MWTimeSeries> generatorInformation = new ArrayList<MWTimeSeries>();
	ArrayList<MWTimeSeries> loadInformation = new ArrayList<MWTimeSeries>();
	
	public Lesson6Panel() {
		initializeDisplay();
	}
	
	boolean animating = true;
	Object animatingLock = new Object();
	
	ArrayList<MWManager> loadManagers;
	
	private void setupLoadManagersAndTimeSeries() {
		loadManagers = new ArrayList<MWManager>();
		
		MWManager loadManagerRes = new MWManager(loadResidential,simClock,true);
		try {
			loadManagerRes.addPoint(sdformat.parse("00:00"), 20);
			loadManagerRes.addPoint(sdformat.parse("06:00"), 20);
			loadManagerRes.addPoint(sdformat.parse("07:00"), 50);
			loadManagerRes.addPoint(sdformat.parse("10:00"), 50);
			loadManagerRes.addPoint(sdformat.parse("11:00"), 20);
			loadManagerRes.addPoint(sdformat.parse("16:00"), 20);
			loadManagerRes.addPoint(sdformat.parse("17:00"), 75);
			loadManagerRes.addPoint(sdformat.parse("20:00"), 75);
			loadManagerRes.addPoint(sdformat.parse("21:00"), 50);
			loadManagerRes.addPoint(sdformat.parse("22:00"), 20);
			loadManagerRes.addPoint(sdformat.parse("24:00"), 20);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		loadManagerRes.setScaleAllPoints(3.0);
		loadManagerRes.animationstep();
		circuitpanel.getAnimatables().add(loadManagerRes);
		loadManagers.add(loadManagerRes);
		
		MWManager loadManagerCom = new MWManager(loadCommercial,simClock,true);
		try {
			loadManagerCom.addPoint(sdformat.parse("00:00"), 20);
			loadManagerCom.addPoint(sdformat.parse("06:00"), 75);
			loadManagerCom.addPoint(sdformat.parse("18:00"), 75);
			loadManagerCom.addPoint(sdformat.parse("24:00"), 20);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		loadManagerCom.setScaleAllPoints(3.0);
		loadManagerCom.animationstep();
		circuitpanel.getAnimatables().add(loadManagerCom);
		loadManagers.add(loadManagerCom);
		
		MWManager loadManagerInd = new MWManager(loadIndustrial,simClock,true);
		try {
			loadManagerInd.addPoint(sdformat.parse("00:00"), 20);
			loadManagerInd.addPoint(sdformat.parse("06:00"), 75);
			loadManagerInd.addPoint(sdformat.parse("08:00"), 100);
			loadManagerInd.addPoint(sdformat.parse("16:00"), 100);
			loadManagerInd.addPoint(sdformat.parse("18:00"), 75);
			//loadManager.addPoint(sdformat.parse("21:00"), 5);
			loadManagerInd.addPoint(sdformat.parse("24:00"), 20);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		loadManagerInd.setScaleAllPoints(3.0);
		loadManagerInd.animationstep();
		circuitpanel.getAnimatables().add(loadManagerInd);
		loadManagers.add(loadManagerInd);
	}
	
	MaxMWTimeSeries maxWindTimeSeries = null;
	
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
		maxWindTimeSeries = new MaxMWTimeSeries(simClock,genWind,"Maximum wind output available");
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
		simClock = new SimulationClock(startDate,minutesPerStep,true,10);
		simClock.setClockState(true);

		costAndEmissionsTimeSeries = new CostAndEmissionsTimeSeries(simClock,"System costs","System emissions");
		
		// setup basic overload trip settings
		overloadMonitorParams = new BranchOverloadMonitorParameters(false,150);
		
		// setup container
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		
		// setup the power system data (nodes, branches, generators) & oneline diagram
		setupPowerSystem();
		
		// add the costAndEmissions tseries so it gets animated
		circuitpanel.getAnimatables().add(costAndEmissionsTimeSeries);

		// add the clock so it gets animated
		circuitpanel.getAnimatables().add(simClock);
		
		
		SimClockPauseForBlackout blackoutPauser = new SimClockPauseForBlackout(simClock,ps);
		circuitpanel.getAnimatables().add(blackoutPauser);
		
		//setupLoadManagersAndTimeSeries();
//		setWindMWManager();
//		circuitpanel.getAnimatables().add(maxWindTimeSeries);
		
		//createTestCircuitPanel();
		//ps.saveState();
		
		circuitpanel.setBackground(Color.WHITE);
		add(circuitpanel,BorderLayout.CENTER);
		JPanel timeControls = new JPanel();
		timeControls.setBackground(Color.WHITE);
		StartSim = new JButton("Resume Time");
		StartSim.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(true);
				StartSim.setEnabled(false);
				StopSim.setEnabled(true);
				//genStorage.setAnimating(true);
			}
			
		});
		timeControls.add(StartSim);
		StopSim = new JButton("Pause Time");
		StopSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(false);
				StopSim.setEnabled(false);
				StartSim.setEnabled(true);
				//genStorage.setAnimating(false);
			}
		});
		timeControls.add(StopSim);
		JButton ResetTimeButton = new JButton("Reset Time");
		ResetTimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.restoreState(PowerSystem.restoreSettings.LOAD_MW);
				ps.restoreState(PowerSystem.restoreSettings.GEN_MW);
				simClock.setClockState(false);
				simClock.resetClock();
				for (int i = 0; i < loadManagers.size(); i++)
					loadManagers.get(i).animationstep();
				for (int i = 0; i < generatorInformation.size(); i++) {
					generatorInformation.get(i).getTimeSeries().clear();
				}
				for (int i = 0; i < loadInformation.size(); i++) {
					loadInformation.get(i).getTimeSeries().clear();
				}
				for (int i = 0; i < branchFlowInformation.size(); i++) {
					branchFlowInformation.get(i).getTimeSeries().clear();
				}
				
				costAndEmissionsTimeSeries.getCostTimeSeries().clear();
				costAndEmissionsTimeSeries.getEmissionsTimeSeries().clear();
				maxWindTimeSeries.getTimeSeries().clear();
				
				genStorage.setStoredEnergy(0);
				genWind.setMaxMW(windScaleSlider.getValue()*initialWindGen);
				StopSim.setEnabled(true);
				StartSim.setEnabled(false);
				ps.solve();
				simClock.setClockState(true);
				genStorage.setAnimating(true);
			}
		});
		timeControls.add(ResetTimeButton);
		
		JButton resetButton = new JButton("Reset Entire Simulation");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ps.restoreState();
				simClock.setClockState(false);
				simClock.resetClock();
				for (int i = 0; i < loadManagers.size(); i++)
					loadManagers.get(i).animationstep();
				for (int i = 0; i < generatorInformation.size(); i++) {
					generatorInformation.get(i).getTimeSeries().clear();
				}
				for (int i = 0; i < loadInformation.size(); i++) {
					loadInformation.get(i).getTimeSeries().clear();
				}
				for (int i = 0; i < branchFlowInformation.size(); i++) {
					branchFlowInformation.get(i).getTimeSeries().clear();
				}
				costAndEmissionsTimeSeries.getCostTimeSeries().clear();
				costAndEmissionsTimeSeries.getEmissionsTimeSeries().clear();
				maxWindTimeSeries.getTimeSeries().clear();
				genStorage.setStoredEnergy(0);
				for (int i = 0; i < sliderControls.size(); i++) {
					sliderControls.get(i).setValue(sliderInitialValues.get(i));
				}
				genWind.setMaxMW(initialWindGen);
				StopSim.setEnabled(true);
				StartSim.setEnabled(false);
				ps.solve();
				simClock.setClockState(true);
				genStorage.setAnimating(true);
			}
		});
		timeControls.add(new JLabel("|"));
		timeControls.add(resetButton);
		
		if (simClock.getClockState()) {
			StopSim.setEnabled(true);
			StartSim.setEnabled(false);
			//genStorage.setAnimating(true);			
		} else {
			StopSim.setEnabled(false);
			StartSim.setEnabled(true);
			//genStorage.setAnimating(false);
		}
		
		JPanel simControls = new JPanel();
		simControls.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		simControls.add(timeControls,c);
		
		add(simControls,BorderLayout.PAGE_START);
		/*
		JPanel windLineMaxControl = new JPanel(new BorderLayout());
		windLineMaxControl.setBorder(BorderFactory.createEtchedBorder());
		windLineMaxControl.setBackground(Color.WHITE);
		int windLineMaxinitialValue = 125;
		windLineMaxSlider = new JSlider(1,1000,windLineMaxinitialValue);
		windLineMaxSlider.setBackground(Color.WHITE);
		sliderControls.add(windLineMaxSlider);
		sliderInitialValues.add(windLineMaxinitialValue);
		windLineMaxSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				branchWindLocalSubstationToCitySubstation.setMaxMWFlow(windLineMaxSlider.getValue());
				windLineMaxLabel.setText("<html><center>Capacity of the transmission line <br> from substation 1 to 2: " + windLineMaxSlider.getValue() + " MW</center></html>");
			}
		});
		windLineMaxLabel = new JLabel("");
		windLineMaxLabel.setHorizontalAlignment(JLabel.CENTER);
		windLineMaxLabel.setBackground(Color.WHITE);
		windLineMaxSlider.setValueIsAdjusting(true);
		windLineMaxSlider.setValueIsAdjusting(false);
		windLineMaxControl.add(windLineMaxSlider, BorderLayout.PAGE_START);
		windLineMaxControl.add(windLineMaxLabel,BorderLayout.PAGE_END);
		
		
		JPanel windScaleControl = new JPanel(new BorderLayout());
		windScaleControl.setBorder(BorderFactory.createEtchedBorder());
		windScaleControl.setBackground(Color.WHITE);
		int windScaleInitialValue = 1;
		windScaleSlider = new JSlider(1,20,windScaleInitialValue);
		windScaleSlider.setBackground(Color.WHITE);
		sliderControls.add(windScaleSlider);
		sliderInitialValues.add(windScaleInitialValue);
		windScaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				windGenManager.setScaleAllPoints(windScaleSlider.getValue());
				try {
					windScaleLabel.setText("<html><center>Wind farm <br> capacity: " + 
							textFormatter.valueToString(windScaleSlider.getValue()*maxWindGen) + " MW</center></html>");
					winddisplay.setCapacity(windScaleSlider.getValue()*maxWindGen);
				} catch (ParseException e1) {
					windScaleLabel.setText("<html><center>Wind farm <br> capacity: ??? MW</center></html>");
					
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		windScaleLabel = new JLabel("");
		windScaleLabel.setHorizontalAlignment(JLabel.CENTER);
		windScaleLabel.setBackground(Color.WHITE);
		windScaleSlider.setValueIsAdjusting(true);
		windScaleSlider.setValueIsAdjusting(false);
		windScaleControl.add(windScaleSlider,BorderLayout.PAGE_START);
		windScaleControl.add(windScaleLabel,BorderLayout.PAGE_END);
		
		
		JPanel loadScaleControl = new JPanel(new BorderLayout());
		loadScaleControl.setBorder(BorderFactory.createEtchedBorder());
		loadScaleControl.setBackground(Color.WHITE);
		int loadScaleInitialValue = 3;
		loadScaleSlider = new JSlider(1,20,3);
		loadScaleSlider.setBackground(Color.WHITE);
		sliderControls.add(loadScaleSlider);
		sliderInitialValues.add(loadScaleInitialValue);
		loadScaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double maxLoad = 0;
				for (int i = 0; i < loadManagers.size(); i++) {
					maxLoad += loadManagers.get(i).getMaxMW();
					loadManagers.get(i).setScaleAllPoints(loadScaleSlider.getValue());
				}
				try {
					loadScaleLabel.setText("<html><center>Peak community <br> power demand: " + 
							textFormatter.valueToString(loadScaleSlider.getValue()*maxLoad) + " MW</center></html>");
				} catch (ParseException e1) {
					e1.printStackTrace();
					loadScaleLabel.setText("<html><center>Peak community <br> power demand: ??? MW</center></html>");
				}
			}
		});
		loadScaleLabel = new JLabel("");
		loadScaleLabel.setBackground(Color.WHITE);
		loadScaleLabel.setHorizontalAlignment(JLabel.CENTER);
		loadScaleControl.add(loadScaleSlider,BorderLayout.PAGE_START);
		loadScaleControl.add(loadScaleLabel,BorderLayout.PAGE_END);
		loadScaleSlider.setValueIsAdjusting(true);
		loadScaleSlider.setValueIsAdjusting(false);
		
		JPanel sliderControls = new JPanel(new GridBagLayout());
		sliderControls.setBackground(Color.WHITE);
		GridBagConstraints sliderConstraints = new GridBagConstraints();
		sliderConstraints.fill = GridBagConstraints.HORIZONTAL;
		sliderConstraints.weightx = 1.0;
		sliderConstraints.gridy = 0;
		
		sliderConstraints.gridx = 0;
		sliderControls.add(loadScaleControl,sliderConstraints);
		
		sliderConstraints.gridx = 1;
		sliderControls.add(windScaleControl,sliderConstraints);
		
		sliderConstraints.gridx = 2;
		sliderControls.add(windLineMaxControl, sliderConstraints);
		
		JPanel plotSelectors = new JPanel();
		plotSelectors.setBackground(Color.WHITE);
		plotSelectors.setBorder(BorderFactory.createEtchedBorder());
		JPanel branchPlotSelector = new JPanel();
			branchPlotSelector.setBackground(Color.WHITE);
			branchPlotSelector.setLayout(new BorderLayout());
			JLabel plotBranchLabel = new JLabel("Plot branch flow");
			plotBranchLabel.setHorizontalAlignment(JLabel.CENTER);
			branchPlotSelector.add(plotBranchLabel,BorderLayout.PAGE_START);
			plotBranchBox = new JComboBox(branchFlowInformation.toArray());
			branchPlotSelector.add(plotBranchBox,BorderLayout.CENTER);
			JButton plotBranchButton = new JButton("Plot branch flow");
			plotBranchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MWTimeSeries mwtoplot = (MWTimeSeries)plotBranchBox.getSelectedItem();
					TimeSeriesPlot tplot = new TimeSeriesPlot(mwtoplot.toString(),0,1000.0,300,200,mwtoplot.getTimeSeries(),"MW");
					//circuitpanel.getAnimatables().add(tplot);
					tplot.setVisible(true);					
				}
			});
			branchPlotSelector.add(plotBranchButton,BorderLayout.PAGE_END);
		JPanel loadPlotSelector = new JPanel();
			loadPlotSelector.setBackground(Color.WHITE);
			loadPlotSelector.setLayout(new BorderLayout());
			JLabel plotLoadLabel = new JLabel("Plot load demand");
			plotLoadLabel.setHorizontalAlignment(JLabel.CENTER);
			loadPlotSelector.add(plotLoadLabel,BorderLayout.PAGE_START);
			plotLoadBox = new JComboBox(loadInformation.toArray());
			loadPlotSelector.add(plotLoadBox,BorderLayout.CENTER);
			JButton plotLoadButton = new JButton("Plot load");
			plotLoadButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					MWTimeSeries mwtoplot = (MWTimeSeries)plotLoadBox.getSelectedItem();
					TimeSeriesPlot tplot = new TimeSeriesPlot(mwtoplot.toString(),0,1000.0,300,200,mwtoplot.getTimeSeries(),"MW");
					//circuitpanel.getAnimatables().add(tplot);
					tplot.setVisible(true);
				}
			});
			loadPlotSelector.add(plotLoadButton,BorderLayout.PAGE_END);
		JPanel genPlotSelector = new JPanel();
			genPlotSelector.setBackground(Color.WHITE);
			genPlotSelector.setLayout(new BorderLayout());
			JLabel plotgenLabel = new JLabel("Plot generation");
			plotgenLabel.setHorizontalAlignment(JLabel.CENTER);
			genPlotSelector.add(plotgenLabel,BorderLayout.PAGE_START);
			plotgenBox = new JComboBox(generatorInformation.toArray());
			genPlotSelector.add(plotgenBox,BorderLayout.CENTER);
			JButton plotgenButton = new JButton("Plot gen");
			plotgenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					MWTimeSeries mwtoplot = (MWTimeSeries)plotgenBox.getSelectedItem();
					TimeSeriesPlot tplot = new TimeSeriesPlot(mwtoplot.toString(),0,1000.0,300,200,mwtoplot.getTimeSeries(),"MW");
					//circuitpanel.getAnimatables().add(tplot);
					tplot.setVisible(true);
				}
			});
			genPlotSelector.add(plotgenButton,BorderLayout.PAGE_END);

		plotSelectors.add(branchPlotSelector);
		plotSelectors.add(loadPlotSelector);
		plotSelectors.add(genPlotSelector);
		
		JPanel plotCostAndEmissions = new JPanel(new FlowLayout());
		plotCostAndEmissions.setBorder(BorderFactory.createEtchedBorder());
		plotCostAndEmissions.setBackground(Color.WHITE);
		JButton plotCostsButton = new JButton("Plot system costs");
		plotCostsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TimeSeriesPlot tplot = new TimeSeriesPlot("System costs",0,1000,300,200,costAndEmissionsTimeSeries.getCostTimeSeries(),"$/hr");
				tplot.setVisible(true);
			}
		});
		JButton plotEmissionsButton = new JButton("Plot system emissions");
		plotEmissionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TimeSeriesPlot tplot = new TimeSeriesPlot("System emissions",0,1000,300,200,costAndEmissionsTimeSeries.getEmissionsTimeSeries(),"tons C02/hr");
				tplot.setVisible(true);
			}
		});
		JButton plotMaxWindButton = new JButton("Plot maximum wind power available");
		plotMaxWindButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TimeSeriesPlot tplot = new TimeSeriesPlot("Maximum wind output available",0,1000,300,200,maxWindTimeSeries.getTimeSeries(),"MW");
				tplot.setVisible(true);
			}
		});
		plotCostAndEmissions.add(plotCostsButton);
		plotCostAndEmissions.add(plotEmissionsButton);
		plotCostAndEmissions.add(plotMaxWindButton);

		JPanel bottomSection = new JPanel(new BorderLayout());
		bottomSection.setBackground(Color.WHITE);
		bottomSection.add(sliderControls,BorderLayout.NORTH);
		bottomSection.add(plotCostAndEmissions,BorderLayout.CENTER);
		bottomSection.add(plotSelectors,BorderLayout.SOUTH);
		
		add(bottomSection,BorderLayout.SOUTH);
		
		TimeSeriesPlot tplot = new TimeSeriesPlot("Maximum wind output available",0,1000,300,200,maxWindTimeSeries.getTimeSeries(),"MW");
		tplot.dispose();
		*/
		ps.solve();
		ps.saveState();
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
	                        		myself.repaint();
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
//		try {
//			readBusDataFromFile("busData.txt");
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.exit(0);
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
	
	HashMap<NodeData,ArrayList<BranchCoordsAndData>> nodeToLinesMap = new HashMap<NodeData, ArrayList<BranchCoordsAndData>>(); 	
	HashMap<NodeData,Point2D.Double> nodeToNodeDisplayMap = new HashMap<NodeData,Point2D.Double>();
	
	BranchColorProvider branchColorProvider = new BranchColorDynamic(Color.BLACK,0.99,Color.ORANGE,1.01,Color.RED);
	BranchThicknessProvider branchThicknessProvider = new BranchThicknessDynamic(1.0,0.99,2.0,1.0,3.0);
	FlowArrowColorProvider flowArrowColorProvider = new FlowArrowColorDynamic(new Color(0,255,0,255/2),0.85,new Color(255,128,64,255/2),1.0,new Color(255,0,0,255/2));
	
	private void readLineDataFromFile() throws FileNotFoundException {
		InputStream is = getClass().getResourceAsStream("/dataFiles/transmissionLines.csv");
	    Scanner scanner = new Scanner(is);
	    String currentDelims = ",";
	    ArrayList<String> result = new ArrayList<String>();
	    try {
	      //first use a Scanner to get each line
	    	int lineIdx = 0;
	      while ( scanner.hasNextLine() ){
	        String lineToProcess = scanner.nextLine();
	        System.out.println("Line " + lineIdx++);
	        result.clear();
		    StringTokenizer parser = new StringTokenizer(
		      lineToProcess,
		      currentDelims);	        
		    String token = null;
		    while ( parser.hasMoreTokens() ) {
				token = parser.nextToken(currentDelims);
				result.add(token);
				System.out.println(token);
	      	}
		    int bus1Int = Integer.parseInt(result.get(0));
		    NodeData bus1 = ps.findNodeByNumber(bus1Int);
		    int bus2Int = Integer.parseInt(result.get(1));
		    NodeData bus2 = ps.findNodeByNumber(bus2Int);
		    double rVal = Double.parseDouble(result.get(2));
		    double xVal = Double.parseDouble(result.get(3));
		    double capacity = Double.parseDouble(result.get(4));
		    int coordOffset = 5;
		    
		    if ((bus1 == null) | (bus2 == null)) {
		    	System.out.println("Unable to find buses " + bus1Int + " and " + bus2Int);
		    } else {
		    	BranchData branchToAdd = new BranchData(bus1,bus2,xVal,rVal,capacity,true);
		    	ps.addBranch(branchToAdd);
			    int numCoords = Integer.parseInt(result.get(coordOffset));
			    ArrayList<Point2D.Double> coordList = new ArrayList<Point2D.Double>();
			    for (int coordIdx = 0; coordIdx < numCoords; coordIdx++) {
			    	double xcoord = 1016*Double.parseDouble(result.get(coordOffset+1+coordIdx*2));
			    	double ycoord = 654*(1-Double.parseDouble(result.get(coordOffset+1+coordIdx*2 + 1)));
			    	coordList.add(new Point2D.Double(xcoord,ycoord));
			    }
			    
			    if (!(nodeToLinesMap.containsKey(bus1))) 
			    	nodeToLinesMap.put(bus1, new ArrayList<BranchCoordsAndData>());
		    	nodeToLinesMap.get(bus1).add(new BranchCoordsAndData(coordList,branchToAdd));
		    	
			    if (!(nodeToLinesMap.containsKey(bus2)))
			    	nodeToLinesMap.put(bus2, new ArrayList<BranchCoordsAndData>());
		    	nodeToLinesMap.get(bus2).add(new BranchCoordsAndData(coordList,branchToAdd));

		    	/*if ((bus1Int == 22620) & (bus2Int == 22674)) {
			    	ArrayList<LineAndDistanceInfoProvider> lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(coordList, 
							0, branchThicknessProvider, 2.0, 1, Math.PI/20, 
							branchToAdd, branchColorProvider, circuitpanel, true, 
							overloadMonitorParams);
			    	FlowArrowsForBranchData fA = new FlowArrowsForBranchData(branchToAdd,lines,0.05,5.0,10.0,flowArrowColorProvider);
					circuitpanel.getMiddleLayerRenderables().add(fA);
					circuitpanel.getAnimatables().add(fA);		
					//circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(413,180),12,new Color(0f,0f,0f,1f),0,branchToAdd));		
								    	
			    } else {
			    */
			    	ArrayList<LineAndDistanceInfoProvider> lines = DrawUtilities.addLinesToAnimatedPanel(coordList, 
						0, branchThicknessProvider,  
						branchToAdd, branchColorProvider, circuitpanel);
				    FlowArrowsForBranchData fA = new FlowArrowsForBranchData(branchToAdd,lines,0.02,40.0,10.0,flowArrowColorProvider,10000);
					circuitpanel.getMiddleLayerRenderables().add(fA);
					circuitpanel.getAnimatables().add(fA);		
			    //}
			    
		    }
		    
	      }
	    }
	    finally {
	      //ensure the underlying stream is always closed
	      scanner.close();
	    }
	    addUSAGraphic();
	}
	
	
	private void readBusDataFromFile() throws FileNotFoundException {
		InputStream is = getClass().getResourceAsStream("/dataFiles/buses.csv");
	    Scanner scanner = new Scanner(is);
	    String currentDelims = ",";
	    ArrayList<String> result = new ArrayList<String>();
	    try {
	      //first use a Scanner to get each line
	    	int lineIdx = 0;
	      while ( scanner.hasNextLine() ){
	        String lineToProcess = scanner.nextLine();
	        System.out.println("Line " + lineIdx++);
	        result.clear();
		    StringTokenizer parser = new StringTokenizer(
		      lineToProcess,
		      currentDelims);	        
		    String token = null;
		    while ( parser.hasMoreTokens() ) {
				token = parser.nextToken(currentDelims);
				result.add(token);
				System.out.println(token);
	      	}
		    NodeData nodeToAdd = new NodeData(Integer.parseInt(result.get(0)),Double.parseDouble(result.get(1)),Double.parseDouble(result.get(2)),Boolean.parseBoolean(result.get(3)),Boolean.parseBoolean(result.get(4)),result.get(5));
		    double nodeXCoord = 1016*Double.parseDouble(result.get(6));
		    double nodeYCoord = 654*(1-Double.parseDouble(result.get(7)));
		    NodeDisplay nDisplay = new NodeDisplay(nodeToAdd,nodeXCoord,nodeYCoord,5.0,busInset);
		    nodeToNodeDisplayMap.put(nodeToAdd, new Point2D.Double(nodeXCoord,nodeYCoord));
		    circuitpanel.getTopLayerRenderables().add(nDisplay);
		    circuitpanel.addMouseListener(nDisplay);
		    
		    ps.addNode(nodeToAdd);
		    
		    double loadPeakMW = Double.parseDouble(result.get(8));
		    nodeToAdd.peakLoad = 2*loadPeakMW;
			LoadDynamicData loadDynamic = new LoadDynamicData(nodeToAdd,loadPeakMW,0,true);
			ps.addLoad(loadDynamic);
			
		    double genPeakMW = Double.parseDouble(result.get(9));
		    
		    double costPerMWh = 0;
		    double emissionsPerMWh = 0;
		    
		    GeneratorType gType = null;
		    
		    double totalGenPercent = 0; 
		    
		    for (int genIdx = 0; genIdx < 5; genIdx++) {
		    	totalGenPercent += Double.parseDouble(result.get(10+genIdx));
		    }
		    
		    double maxGen = Double.NEGATIVE_INFINITY;
		    
		    for (int genIdx = 0; genIdx < 5; genIdx++) {
		    	double genPercent = Double.parseDouble(result.get(10+genIdx))/totalGenPercent;
		    	if (genPercent > 1e-5) {
			    	double genCapacity = genPercent * genPeakMW;
			    	maxGen = (genCapacity > maxGen ? genCapacity : maxGen);
			    	switch (genIdx) {
			    		case 0:
			    			// coal
			    			gType = GeneratorType.COAL;
			    			costPerMWh = 20.0;
			    			emissionsPerMWh = 1.0;
			    			break;
			    		case 1:
			    			// natural gas
			    			gType = GeneratorType.GAS;
			    			costPerMWh = 70.0;
			    			emissionsPerMWh = 0.5;
			    			break;
			    		case 2:
			    			// nuclear,hydro,wind,biomass
			    			gType = GeneratorType.NUCLEAR;
			    			costPerMWh = 6.0;
			    			emissionsPerMWh = 0;
			    			break;
			    		case 3:
			    			// hydro
			    			gType = GeneratorType.HYDRO;
			    			costPerMWh = 3.0;
			    			emissionsPerMWh = 0;
			    			break;
			    		case 4:
			    			// wind
			    			gType = GeneratorType.WIND;
			    			costPerMWh = 0.0;
			    			emissionsPerMWh = 0;
			    			break;
			    		case 5:
			    			// biomass
			    			gType = GeneratorType.BIOMASS;
			    			costPerMWh = 13.0;
			    			emissionsPerMWh = 0;
			    			break;
			    	}
					GeneratorDataWithLinearCostAndEmissions genToAdd = new GeneratorDataWithLinearCostAndEmissions(nodeToAdd,genCapacity,0,genCapacity,0,0,9999,
							true,2000,costPerMWh,0,1.0,includeFixedCostsAndEmissions);
					gensWithCostsAndEmissions.add(genToAdd);
					ps.addGenerator(genToAdd);
					genToGeneratorType.put(genToAdd, gType);		    	
		    	}
		    }
		    nodeToAdd.peakGen = maxGen;
	      }
	    }
	    finally {
	      //ensure the underlying stream is always closed
	      scanner.close();
	    }
	
	}
	
	BusInset busInset;
	HashMap<GeneratorData,GeneratorType> genToGeneratorType = new HashMap<GeneratorData, GeneratorType>();
	
	private void setupPowerSystem() {
		gensWithCostsAndEmissions = new ArrayList<CostAndEmissionsProvider>();
		busInset = new BusInset(null,nodeToLinesMap,nodeToNodeDisplayMap, 
				genToGeneratorType, generatorIconProvider,branchColorProvider,branchThicknessProvider,flowArrowColorProvider);
		
		// Create the power system
		ps = new PowerSystem(PowerSystemSolutionMethod.ZEB_OPF);
		try {
			readBusDataFromFile();
			readLineDataFromFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		GeneratorDataWithLinearCostAndEmissions genCoal = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(22674),600,0,1000,0,0,9999,true,2000,20.0,0,1.0,includeFixedCostsAndEmissions);
//		gensWithCostsAndEmissions.add(genCoal);
//		ps.addGenerator(genCoal);
//		genToGeneratorType.put(genCoal, GeneratorType.COAL);
//		
//		GeneratorDataWithLinearCostAndEmissions genGas = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(22674),200,0,1000,0,0,9999,true,700.0,70.0,0,0.5,includeFixedCostsAndEmissions);
//		gensWithCostsAndEmissions.add(genGas);
//		ps.addGenerator(genGas);
//		genToGeneratorType.put(genGas, GeneratorType.GAS);
		
//		genWind = new WindGeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(22674),initialWindGen,0,210,initialWindVariation,true,200.0,0,0,0,includeFixedCostsAndEmissions);
//		genWind.setAnimating(false);
//		genToGeneratorType.put(genWind, GeneratorType.WIND);
//		gensWithCostsAndEmissions.add(genWind);
//		ps.addGenerator(genWind);		
		
//		GeneratorDataWithLinearCostAndEmissions genNuke = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(22620),900,0,900,0,0,9999,true,6500.0,6.0,0,0,includeFixedCostsAndEmissions);
//		gensWithCostsAndEmissions.add(genNuke);
//		genToGeneratorType.put(genNuke, GeneratorType.NUCLEAR);
//		ps.addGenerator(genNuke);
//		
//		GeneratorDataWithLinearCostAndEmissions genHydro = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(22620),1000,500,1000,0,0,9999,true,1500,3.0,0,0,includeFixedCostsAndEmissions);
//		gensWithCostsAndEmissions.add(genHydro);
//		genToGeneratorType.put(genHydro, GeneratorType.HYDRO);
//		ps.addGenerator(genHydro);
//		
//		GeneratorDataWithLinearCostAndEmissions genSolar = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(22620),0,0,50,0,0,9999,true,0.0,1e-5,0,0.0,includeFixedCostsAndEmissions);
//		gensWithCostsAndEmissions.add(genSolar);
//		genToGeneratorType.put(genSolar, GeneratorType.SOLAR);
//		ps.addGenerator(genSolar);
		
		//busInset = new BusInset(ps.findNodeByNumber(22674),nodeToLinesMap,nodeToNodeDisplayMap);
		
		busInset.setCurrentNode(ps.findNodeByNumber(22620));
		circuitpanel.getTopLayerRenderables().add(busInset);
		circuitpanel.addMouseListener(busInset);
		circuitpanel.addMouseMotionListener(busInset);
		circuitpanel.getAnimatables().add(busInset);
		ps.solve();
		circuitpanel.getAnimatables().add(ps);
		
		
		/*
		// Create nodes & register with power system
		NodeData nodeWindGenerator = new NodeData(1,1.0,0,false,true);
		ps.addNode(nodeWindGenerator);
		NodeData nodeSubstation2 = new NodeData(2,1.0,0,false);
		ps.addNode(nodeSubstation2);
		NodeData nodeCityResidential = new NodeData(3,1.0,0,false);
		ps.addNode(nodeCityResidential);
		NodeData nodeCityIndustrial = new NodeData(4,1.0,0,false);
		ps.addNode(nodeCityIndustrial);
		NodeData nodeCityCommercial = new NodeData(5,1.0,0,false);
		ps.addNode(nodeCityCommercial);
		NodeData nodeCoalGenerator = new NodeData(7,1.0,0,false,true);
		ps.addNode(nodeCoalGenerator);
		NodeData nodeSubstation3 = new NodeData(8,1.0,0,false);
		ps.addNode(nodeSubstation3);
		NodeData nodeGasGenerator = new NodeData(11,1.0,0,false,true);
		ps.addNode(nodeGasGenerator);
		NodeData nodeWindStorage = new NodeData(9,1.0,0,false);
		ps.addNode(nodeWindStorage);
		NodeData nodeSubstation1 = new NodeData(0,1.0,0,false);
		ps.addNode(nodeSubstation1);
		
		// Create lines & register with power system
		MWTimeSeries branchTimeSeries = null;
		
		BranchData branchWindGeneratorToLocalSubstation = new BranchData(nodeWindGenerator,nodeSubstation1,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchWindGeneratorToLocalSubstation,"Wind Generator to Substation 1");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);
		ps.addBranch(branchWindGeneratorToLocalSubstation);

		BranchData branchWindStorageToLocalSubstation = new BranchData(nodeWindStorage,nodeSubstation1,0.01,0,1500.0,false);
		branchTimeSeries = new MWTimeSeries(simClock,branchWindStorageToLocalSubstation,"Storage to Substation 1");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);
		ps.addBranch(branchWindStorageToLocalSubstation);
		
		branchWindLocalSubstationToCitySubstation = new BranchData(nodeSubstation1,nodeSubstation2,0.01,0,125.0,false);
		branchTimeSeries = new MWTimeSeries(simClock,branchWindLocalSubstationToCitySubstation,"Substation 1 to Substation 2");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);
		ps.addBranch(branchWindLocalSubstationToCitySubstation);
		
		BranchData branchWindToResidential = new BranchData(nodeSubstation2,nodeCityResidential,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchWindToResidential,"Substation 2 to Residential");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);
		ps.addBranch(branchWindToResidential);
		
		BranchData branchWindToCommercial = new BranchData(nodeSubstation2,nodeCityCommercial,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchWindToCommercial,"Substation 2 to Commercial");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);
		ps.addBranch(branchWindToCommercial);

		BranchData branchWindToIndustrial = new BranchData(nodeSubstation2,nodeCityIndustrial,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchWindToIndustrial,"Substation 2 to Industrial");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);		
		ps.addBranch(branchWindToIndustrial);
		
		BranchData branchCoalGeneratorToSubstation = new BranchData(nodeCoalGenerator,nodeSubstation3,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchCoalGeneratorToSubstation,"Coal Generator to Substation 3");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);
		ps.addBranch(branchCoalGeneratorToSubstation);
		
		
		BranchData branchGasToSubstation = new BranchData(nodeGasGenerator,nodeSubstation3,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchGasToSubstation,"Natural Gas Generator to Substation 3");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);		
		ps.addBranch(branchGasToSubstation);
		
		BranchData branchCoalToResidential = new BranchData(nodeSubstation3,nodeCityResidential,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchCoalToResidential,"Substation 3 to Residential");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);		
		ps.addBranch(branchCoalToResidential);
		
		BranchData branchCoalToIndustrial = new BranchData(nodeSubstation3,nodeCityIndustrial,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchCoalToIndustrial,"Substation 3 to Industrial");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);		
		ps.addBranch(branchCoalToIndustrial);
		
		BranchData branchCoalToCommercial = new BranchData(nodeSubstation3,nodeCityCommercial,0.01,0,1500.0,true);
		branchTimeSeries = new MWTimeSeries(simClock,branchCoalToCommercial,"Substation 3 to Commercial");
		branchFlowInformation.add(branchTimeSeries);
		circuitpanel.getAnimatables().add(branchTimeSeries);		
		ps.addBranch(branchCoalToCommercial);

		
		// Create loads
		MWTimeSeries loadTimeSeries = null;
		
		loadResidential = new LoadData(nodeCityResidential,100.0,0,true);
		loadTimeSeries = new MWTimeSeries(simClock,loadResidential,"Residential load");
		circuitpanel.getAnimatables().add(loadTimeSeries);
		loadInformation.add(loadTimeSeries);
		ps.addLoad(loadResidential);
		
		loadCommercial = new LoadData(nodeCityCommercial,100.0,0,true);
		loadTimeSeries = new MWTimeSeries(simClock,loadCommercial,"Commercial load");
		circuitpanel.getAnimatables().add(loadTimeSeries);
		loadInformation.add(loadTimeSeries);
		ps.addLoad(loadCommercial);
		
		loadIndustrial = new LoadData(nodeCityIndustrial,100.0,0,true);
		loadTimeSeries = new MWTimeSeries(simClock,loadIndustrial,"Industrial load");
		circuitpanel.getAnimatables().add(loadTimeSeries);
		loadInformation.add(loadTimeSeries);
		ps.addLoad(loadIndustrial);
		
		// Create gens
		MWTimeSeries genTimeSeries = null;
		
		gensWithCostsAndEmissions = new ArrayList<CostAndEmissionsProvider>();
		GeneratorDataWithLinearCostAndEmissions genCoal = new GeneratorDataWithLinearCostAndEmissions(nodeCoalGenerator,600,0,2000,0,0,9999,true,2000,20.0,0,1.0,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genCoal);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genCoal);
		genTimeSeries = new MWTimeSeries(simClock,genCoal,"Coal generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		ps.addGenerator(genCoal);
		
		GeneratorDataWithLinearCostAndEmissions genGas = new GeneratorDataWithLinearCostAndEmissions(nodeGasGenerator,200,0,2000,0,0,9999,true,700.0,70.0,0,0.5,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genGas);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genGas);
		genTimeSeries = new MWTimeSeries(simClock,genGas,"Natural gas generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		ps.addGenerator(genGas);
		
		genWind = new WindGeneratorDataWithLinearCostAndEmissions(nodeWindGenerator,initialWindGen,0,210,initialWindVariation,true,200.0,0,0,0,includeFixedCostsAndEmissions);
		genWind.setAnimating(false);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genWind);
		genTimeSeries = new MWTimeSeries(simClock,genWind,"Wind generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genWind);
		ps.addGenerator(genWind);
		
		genStorage = new StorageDevice(nodeWindStorage,0,0,0,0,0,0,true,genWind,branchWindLocalSubstationToCitySubstation,simClock,1000,100);
		genTimeSeries = new MWTimeSeries(simClock,genStorage,"Storage generation");
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genStorage);
		circuitpanel.getAnimatables().add(genTimeSeries);
		circuitpanel.getAnimatables().add(genStorage);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genStorage);
		ps.addGenerator(genStorage);
		
		ps.solve();
		*/
		/*
		ArrayList<LineAndDistanceInfoProvider> lines; 
		FlowArrowsForBranchData fA;
		SimpleLineDisplay line;
		
		BranchColorProvider branchColorProvider = new BranchColorDynamic(Color.BLACK,0.85,Color.ORANGE,1.0,Color.RED);
		BranchThicknessProvider branchThicknessProvider = new BranchThicknessDynamic(1.0,0.85,2.0,1.0,3.0);
		FlowArrowColorProvider flowArrowColorProvider = new FlowArrowColorDynamic(new Color(0,255,0,255/2),0.85,new Color(255,128,64,255/2),1.0,new Color(255,0,0,255/2));
		
		
		double switchthickness = 5.0;

		// Line Coal Generator to Fossil Substation & flow label
		ArrayList<Point2D.Double> pointsCoalGenToCoalSubstation = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(710,350),new Point2D.Double(710,212),1);
		pointsCoalGenToCoalSubstation.add(line.getFromPoint());
		pointsCoalGenToCoalSubstation.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsCoalGenToCoalSubstation.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsCoalGenToCoalSubstation.add(line.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsCoalGenToCoalSubstation, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchCoalGeneratorToSubstation, branchColorProvider, circuitpanel, true,
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchCoalGeneratorToSubstation,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		//circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(640,260),12,new Color(0f,0f,0f,1f),0,branchCoalGeneratorToSubstation));
		
		// Line Gas Generator to Fossil Substation & flow label
		ArrayList<Point2D.Double> pointsGasGenToFossilSubstation = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(710,112.5),new Point2D.Double(765,160),1);
		pointsGasGenToFossilSubstation.add(line.getFromPoint());
		pointsGasGenToFossilSubstation.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsGasGenToFossilSubstation.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsGasGenToFossilSubstation.add(line.getToPoint());
		pointsGasGenToFossilSubstation.add(new Point2D.Double(710,212));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsGasGenToFossilSubstation, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchGasToSubstation, branchColorProvider, circuitpanel, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchGasToSubstation,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		//circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(660,170),12,new Color(0f,0f,0f,1f),0,branchGasToSubstation));
		
		// Line Wind Generator to Wind Substation & flow label
		ArrayList<Point2D.Double> pointsWindGenToWindSubstation = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(89,281),new Point2D.Double(89,150),1);
		pointsWindGenToWindSubstation.add(line.getFromPoint());
		pointsWindGenToWindSubstation.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsWindGenToWindSubstation.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsWindGenToWindSubstation.add(line.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsWindGenToWindSubstation, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchWindGeneratorToLocalSubstation, branchColorProvider, circuitpanel, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchWindGeneratorToLocalSubstation,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(100,200),12,new Color(0f,0f,0f,1f),0,branchWindGeneratorToLocalSubstation));		
		
		// Line storage to Wind Substation & flow label
		ArrayList<Point2D.Double> pointsWindStorageToWindSubstation = new ArrayList<Point2D.Double>();
		pointsWindStorageToWindSubstation.add(new Point2D.Double(210,25));
		pointsWindStorageToWindSubstation.add(new Point2D.Double(85,25));
		pointsWindStorageToWindSubstation.add(new Point2D.Double(85,55));
		pointsWindStorageToWindSubstation.add(new Point2D.Double(85,95));
		pointsWindStorageToWindSubstation.add(new Point2D.Double(85,140));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsWindStorageToWindSubstation, 
				0, branchThicknessProvider, switchthickness, 2, Math.PI/20, 
				branchWindStorageToLocalSubstation, branchColorProvider, circuitpanel, true,
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchWindStorageToLocalSubstation,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(100,75),12,new Color(0f,0f,0f,1f),0,branchWindStorageToLocalSubstation));		
		
		// Line substation 2 to Residential load
		ArrayList<Point2D.Double> pointsWindStationToResidential = new ArrayList<Point2D.Double>();
		pointsWindStationToResidential.add(new Point2D.Double(351,212.5));
		pointsWindStationToResidential.add(new Point2D.Double(419,212.5));
		pointsWindStationToResidential.add(new Point2D.Double(457,212.5));
		pointsWindStationToResidential.add(new Point2D.Double(525,212.5));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsWindStationToResidential, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchWindToResidential, branchColorProvider, circuitpanel, true, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchWindToResidential,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(413,180),12,new Color(0f,0f,0f,1f),0,branchWindToResidential));		
		
		// Line substation 3 to Residential load
		ArrayList<Point2D.Double> pointsFossilStationToResidential = new ArrayList<Point2D.Double>();
		pointsFossilStationToResidential.add(new Point2D.Double(710,212.5));
		pointsFossilStationToResidential.add(new Point2D.Double(631,212.5));
		pointsFossilStationToResidential.add(new Point2D.Double(592,212.5));
		pointsFossilStationToResidential.add(new Point2D.Double(525,212.5));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFossilStationToResidential, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchCoalToResidential, branchColorProvider, circuitpanel, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchCoalToResidential,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(585,180),12,new Color(0f,0f,0f,1f),0,branchCoalToResidential));		
		
		// Line Substation 3 to Industrial load & flow label
		ArrayList<Point2D.Double> pointsFossilSubstationToIndustrial = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(710,212.5),new Point2D.Double(525,365),1);
		pointsFossilSubstationToIndustrial.add(line.getFromPoint());
		pointsFossilSubstationToIndustrial.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsFossilSubstationToIndustrial.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsFossilSubstationToIndustrial.add(line.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsFossilSubstationToIndustrial, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchCoalToIndustrial, branchColorProvider, circuitpanel, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchCoalToIndustrial,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(585,280),12,new Color(0f,0f,0f,1f),-Math.PI/5,branchCoalToIndustrial));
		
		// Line Substation 3 to Commercial load & flow label
		ArrayList<Point2D.Double> pointsSub3ToCommercial = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(710,212.5),new Point2D.Double(525,55),1);
		pointsSub3ToCommercial.add(line.getFromPoint());
		pointsSub3ToCommercial.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsSub3ToCommercial.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsSub3ToCommercial.add(line.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsSub3ToCommercial, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchCoalToCommercial, branchColorProvider, circuitpanel, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchCoalToCommercial,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(615,105),12,new Color(0f,0f,0f,1f),Math.PI/5,branchCoalToCommercial));

		
		// Line Substation 2 to Industrial load & flow label
		ArrayList<Point2D.Double> pointsWindSubstationToIndustrial = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(351,212.5),new Point2D.Double(525,365),1);
		pointsWindSubstationToIndustrial.add(line.getFromPoint());
		pointsWindSubstationToIndustrial.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsWindSubstationToIndustrial.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsWindSubstationToIndustrial.add(line.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsWindSubstationToIndustrial, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchWindToIndustrial, branchColorProvider, circuitpanel, 
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchWindToIndustrial,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(399,273),12,new Color(0f,0f,0f,1f),Math.PI/4,branchWindToIndustrial));

		// Line Substation 2 to Commercial load & flow label
		ArrayList<Point2D.Double> pointsWindSubstationToCommercial = new ArrayList<Point2D.Double>();
		line = new SimpleLineDisplay(new Point2D.Double(351,212.5),new Point2D.Double(525,55),1);
		pointsWindSubstationToCommercial.add(line.getFromPoint());
		pointsWindSubstationToCommercial.add(DrawUtilities.getPointAtDistanceOnLine(line,line.getLength()/3));
		pointsWindSubstationToCommercial.add(DrawUtilities.getPointAtDistanceOnLine(line,2*line.getLength()/3));
		pointsWindSubstationToCommercial.add(line.getToPoint());
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsWindSubstationToCommercial, 
				0, branchThicknessProvider, switchthickness, 1, Math.PI/20, 
				branchWindToCommercial, branchColorProvider, circuitpanel, true,
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchWindToCommercial,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(396,140),12,new Color(0f,0f,0f,1f),-Math.PI/4,branchWindToCommercial));
		
		// Line Substation 1 to Substation 2
		ArrayList<Point2D.Double> pointsSub0ToSub1 = new ArrayList<Point2D.Double>();
		pointsSub0ToSub1.add(new Point2D.Double(85,140));
		pointsSub0ToSub1.add(new Point2D.Double(175,140));
		pointsSub0ToSub1.add(new Point2D.Double(175,212));
		pointsSub0ToSub1.add(new Point2D.Double(190,212));
		pointsSub0ToSub1.add(new Point2D.Double(230,212));
		pointsSub0ToSub1.add(new Point2D.Double(351,212));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsSub0ToSub1, 
				0, branchThicknessProvider, switchthickness, 3, Math.PI/20, 
				branchWindLocalSubstationToCitySubstation, branchColorProvider, circuitpanel, true,
				overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchWindLocalSubstationToCitySubstation,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);		
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(195,141),12,new Color(0f,0f,0f,1f),0,branchWindLocalSubstationToCitySubstation));
		*/
		
		// Coal plant display
		/*
		try {
			CoalPlantDisplay coaldisplay = new CoalPlantDisplay(new Point2D.Double(650,300),0,0.0,genCoal);
			circuitpanel.getMiddleLayerRenderables().add(coaldisplay);
			circuitpanel.addMouseListener(coaldisplay);
			CostAndEmissionsOverlay coaloverlay = new CostAndEmissionsOverlay(new Point2D.Double(700,450),genCoal);
			costOverlays.add(coaloverlay);
			circuitpanel.getTopLayerRenderables().add(coaloverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}
		*/
		
		/*
		GeneratorIcon coalDisplay = new GeneratorIcon(genCoal, 650, 300, 50);
		circuitpanel.getMiddleLayerRenderables().add(coalDisplay);
		circuitpanel.addMouseListener(coalDisplay);
		circuitpanel.addMouseMotionListener(coalDisplay);
		
		// Gas plant display
		try {
		GasPlantDisplay gasdisplay = new GasPlantDisplay(new Point2D.Double(650,40),0.0,genGas);
		circuitpanel.getMiddleLayerRenderables().add(gasdisplay);
		circuitpanel.addMouseListener(gasdisplay);
		CostAndEmissionsOverlay gasoverlay = new CostAndEmissionsOverlay(new Point2D.Double(700,20),genGas);
		costOverlays.add(gasoverlay);
		circuitpanel.getTopLayerRenderables().add(gasoverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Wind plant display
		try {
			winddisplay = new WindPlantDisplay(new Point2D.Double(50,240),0.0,genWind);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay);
			circuitpanel.getAnimatables().add(winddisplay);
			circuitpanel.addMouseListener(winddisplay);
			CostAndEmissionsOverlay windoverlay = new CostAndEmissionsOverlay(new Point2D.Double(100,450),genWind);
			costOverlays.add(windoverlay);
			circuitpanel.getTopLayerRenderables().add(windoverlay);						
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		// Storage display
//		try {
			//SubstationDisplay storagedisplay = new SubstationDisplay(new Point2D.Double(160,0),"Storage");
			//circuitpanel.getMiddleLayerRenderables().add(storagedisplay);
			StorageDisplay storagedisplay = new StorageDisplay(genStorage,StorageDisplay.Alignment.LEFT,StorageDisplay.Alignment.TOP,new Point2D.Double(170,0));
			circuitpanel.getMiddleLayerRenderables().add(storagedisplay);
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
		
		// Commercial load
		try {
			CityDisplay commercetonLoad = new CityDisplay(new Point2D.Double(475,25),CityDisplay.CityType.COMMERCIAL,"Commercial",loadCommercial,0);
			circuitpanel.getBottomLayerRenderables().add(commercetonLoad);
			circuitpanel.addMouseListener(commercetonLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		
		// Residential load
		try {
			CityDisplay residentialLoad = new CityDisplay(new Point2D.Double(475,175),CityDisplay.CityType.RESIDENTIAL,"Residential",loadResidential,0);
			circuitpanel.getBottomLayerRenderables().add(residentialLoad);
			circuitpanel.addMouseListener(residentialLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		// Industrial load
		try {
			CityDisplay industrialLoad = new CityDisplay(new Point2D.Double(475,329),CityDisplay.CityType.INDUSTRIAL,"Industrial",loadIndustrial,0);
			circuitpanel.getBottomLayerRenderables().add(industrialLoad);
			circuitpanel.addMouseListener(industrialLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}			
		
		// Substation 3
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(660,212 - 25.0),3));
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		// Substation 2
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(305,212 - 25.0),2));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Substation 1
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(39,120.0),1));
		} catch (IOException ie) {
			System.out.println(ie);
		}			
		
		circuitpanel.getMiddleLayerRenderables().add(new StoredEnergyLabel(new Point2D.Double(230,56),12,Color.BLACK,0,genStorage));
		*/
		/*
		totalloadplot = new TotalLoadPlot(
				new Point2D.Double(10,10),
				700,200,
				ps,
				minutesPerAnimationStep,
				0,24,
				0,3000);
		*/
		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,20),12,Color.BLACK,0);
		circuitpanel.getTopLayerRenderables().add(mclabel);
		circuitpanel.addMouseMotionListener(mclabel);
		//MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,0),12,Color.BLACK,0);
		//circuitpanel.getMiddleLayerRenderables().add(mclabel);
		//circuitpanel.addMouseMotionListener(mclabel);
		/*
		WindMaxDisplay windMaxLabel = new WindMaxDisplay(new Point2D.Double(144,384),12,Color.BLACK,0,genWind);
		circuitpanel.getMiddleLayerRenderables().add(windMaxLabel);
		WindCurtailedDisplay windCurtailedLabel = new WindCurtailedDisplay(new Point2D.Double(144,404),12,Color.BLACK,0,genWind);
		circuitpanel.getMiddleLayerRenderables().add(windCurtailedLabel);
*/
		circuitpanel.getTopLayerRenderables().add(new BlackoutDisplay(ps,new Point2D.Double(555,250)));		
/*	
		tlabel = new TimeLabel(new Point2D.Double(0,0),12,Color.BLACK,0,minutesPerAnimationStep,0);
		//circuitpanel.getRenderables().add(tlabel);
		//circuitpanel.getAnimatables().add(tlabel);
		circuitpanel.getAnimatables().add(ps);
		*/
		circuitpanel.getTopLayerRenderables().add(
				new SimClockDisplay(new Point2D.Double(350,5),12,Color.BLACK,0,simClock)); 

		// Wind plant at node sixteen
//		try {
//			WindPlantDisplay winddisplay = new WindPlantDisplay(new Point2D.Double(650,340),0.0,genWind);
//			circuitpanel.getMiddleLayerRenderables().add(winddisplay);
//			circuitpanel.getAnimatables().add(winddisplay);
//			circuitpanel.addMouseListener(winddisplay);
//			CostAndEmissionsOverlay windoverlay = new CostAndEmissionsOverlay(new Point2D.Double(689,462),genWind);
//			costOverlays.add(windoverlay);
//			circuitpanel.getTopLayerRenderables().add(windoverlay);						
//		} catch (IOException ie) {
//			System.out.println(ie);
//		}
		
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
