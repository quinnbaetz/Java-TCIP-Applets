package edu.uiuc.TCIP.education.lessons.development.appletForDOE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
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
import edu.uiuc.power.displayobjects.PlaceholderImage.PlaceholderType;
import edu.uiuc.power.displayobjects.StorageDisplay.StorageDisplayOrientation;
import edu.uiuc.power.displayobjects.loads.CityDisplay;

public class DOEPanelDraft1 extends JPanel implements ItemListener {
	final int initialLoadPayment = 90;
	final int minutesPerStep = 3;
	
	//WindPlantDisplay winddisplay = null;
	ArrayList<WindPlantDisplay> windPlantDisplays = new ArrayList<WindPlantDisplay>();
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
	
	DOEPanelDraft1 myself;
	
	int animationsStepsPerPlotUpdate = 5;
	int plotUpdateCounter = 0;
	
	TimeLabel tlabel;
	JButton StartSim,StopSim,ResetSim,ResetState,ShowPlotWindow;
	JButton IncludeFixedCosts;
	JButton hideCostsButton;
	JComboBox windLineMaxCombobox;
	
	JComboBox plotBranchBox, plotLoadBox, plotgenBox;
	JSlider windLineMaxSlider, windScaleSlider, loadScaleSlider;
	JLabel windLineMaxLabel, windScaleLabel, loadScaleLabel;
	
	BranchOverloadMonitorParameters overloadMonitorParams;
	JCheckBox branchOverloadMonitorCheckbox;
	
	ArrayList<JSlider> sliderControls = new ArrayList<JSlider>();
	ArrayList<Integer> sliderInitialValues = new ArrayList<Integer>();

	
	static long fps = 20;
	//static long fps = 100;
	static long period = 1000L/fps;
	static int minutesPerAnimationStep = 5;
	boolean toggleRepaint;
	protected long lastRepaintTime = 0;
	protected long previousRepaintTime = 0;
	
	PowerSystem ps;
	BranchData branchWindLocalSubstationToCitySubstation; 
	
	WindGeneratorDataWithLinearCostAndEmissions genWind,genWind2,genWind3,genWind4;
	final double initialWindVariation = 1;
	final double initialWindGen = Math.pow(4.8,3);
	final double maxWindGen = Math.pow(5.8,3);
	ArrayList<WindManager> windManagers = new ArrayList<WindManager>();
	
	LoadData loadResidential,loadCommercial,loadIndustrial;
	SimulationClock simClock;
	
	SimpleDateFormat sdformat = new SimpleDateFormat("H:m");
	
	ArrayList<MWTimeSeries> branchFlowInformation = new ArrayList<MWTimeSeries>();
	ArrayList<MWTimeSeries> generatorInformation = new ArrayList<MWTimeSeries>();
	ArrayList<MWTimeSeries> loadInformation = new ArrayList<MWTimeSeries>();
	
	public DOEPanelDraft1() {
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
	
	private void setWindMWManagers() {
		WindManager windGenManager = null;
		
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
		windGenManager.setScaleAllPoints(1.0);
		circuitpanel.getAnimatables().add(windGenManager);
		windGenManager.animationstep();
		maxWindTimeSeries = new MaxMWTimeSeries(simClock,genWind,"Maximum wind output available");
		windManagers.add(windGenManager);
		
		windGenManager = new WindManager(genWind2,simClock,true);
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
		windGenManager.setScaleAllPoints(1.0);
		circuitpanel.getAnimatables().add(windGenManager);
		windGenManager.animationstep();
		maxWindTimeSeries = new MaxMWTimeSeries(simClock,genWind,"Maximum wind output available");
		windManagers.add(windGenManager);
		
		windGenManager = new WindManager(genWind3,simClock,true);
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
		windGenManager.setScaleAllPoints(1.0);
		circuitpanel.getAnimatables().add(windGenManager);
		windGenManager.animationstep();
		maxWindTimeSeries = new MaxMWTimeSeries(simClock,genWind,"Maximum wind output available");
		windManagers.add(windGenManager);		
		
		windGenManager = new WindManager(genWind4,simClock,true);
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
		windGenManager.setScaleAllPoints(1.0);
		circuitpanel.getAnimatables().add(windGenManager);
		windGenManager.animationstep();
		maxWindTimeSeries = new MaxMWTimeSeries(simClock,genWind,"Maximum wind output available");
		windManagers.add(windGenManager);			
	}
	
	private void initializeDisplay() {
		myself = this;

		// Create the main display panel
		circuitpanel = new AnimatedPanelUsingPaintComponent(800,800,period,16,true);
		
		// Initialize clock for the simulator
		Date startDate = null;
		try {
			startDate = sdformat.parse("0:0");
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		simClock = new SimulationClock(startDate,minutesPerStep,true);
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
		
		setupLoadManagersAndTimeSeries();
		setWindMWManagers();
		circuitpanel.getAnimatables().add(maxWindTimeSeries);
		
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
				genStorage.setAnimating(true);
			}
			
		});
		timeControls.add(StartSim);
		StopSim = new JButton("Pause Time");
		StopSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simClock.setClockState(false);
				StopSim.setEnabled(false);
				StartSim.setEnabled(true);
				genStorage.setAnimating(false);
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
//			genStorage.setAnimating(true);			
		} else {
			StopSim.setEnabled(false);
			StartSim.setEnabled(true);
//			genStorage.setAnimating(false);
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
		
		JPanel windLineMaxControl = new JPanel(new BorderLayout());
		ArrayList<BranchData> allBranches = ps.getBranches();
		windLineMaxCombobox = new JComboBox(allBranches.toArray());		
		windLineMaxCombobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BranchData currentBranch = (BranchData)(windLineMaxCombobox.getSelectedItem());
				windLineMaxSlider.setValue((int)Math.floor(currentBranch.getMaxMWFlow()));
				//windLineMaxLabel.setText("<html><center>Capacity of the transmission line <br> " + currentBranch.toString() + windLineMaxSlider.getValue() + " MW</center></html>");				
			}
		});
		windLineMaxControl.setBorder(BorderFactory.createEtchedBorder());
		windLineMaxControl.setBackground(Color.WHITE);
		int windLineMaxinitialValue = 125;
		windLineMaxSlider = new JSlider(1,3000,windLineMaxinitialValue);
		windLineMaxSlider.setBackground(Color.WHITE);
		sliderControls.add(windLineMaxSlider);
		sliderInitialValues.add(windLineMaxinitialValue);
		windLineMaxSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				BranchData currentBranch = (BranchData)(windLineMaxCombobox.getSelectedItem());
				currentBranch.setMaxMWFlow(windLineMaxSlider.getValue());
				windLineMaxLabel.setText("<html><center>---Adjust transmission line capacity---<br> " + currentBranch.toString() + ": " + windLineMaxSlider.getValue() + " MW</center></html>");
			}
		});
		windLineMaxLabel = new JLabel("");
		windLineMaxLabel.setHorizontalAlignment(JLabel.CENTER);
		windLineMaxLabel.setBackground(Color.WHITE);
		windLineMaxSlider.setValueIsAdjusting(true);
		windLineMaxSlider.setValueIsAdjusting(false);
		
		windLineMaxControl.add(windLineMaxSlider, BorderLayout.CENTER);
		windLineMaxControl.add(windLineMaxLabel,BorderLayout.NORTH);
		windLineMaxControl.add(windLineMaxCombobox,BorderLayout.SOUTH);
		
		
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
				for (int i = 0; i < windManagers.size(); i++) {
					WindManager windGenManager = windManagers.get(i);
					windGenManager.setScaleAllPoints(windScaleSlider.getValue());
				}
				try {
					windScaleLabel.setText("<html><center>Wind farm <br> capacity: " + 
							textFormatter.valueToString(windScaleSlider.getValue()*maxWindGen) + " MW</center></html>");
					for (int i = 0; i < windPlantDisplays.size(); i++)
						windPlantDisplays.get(i).setCapacity(windScaleSlider.getValue()*maxWindGen);
//					winddisplay.setCapacity(windScaleSlider.getValue()*maxWindGen);
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
	}


	private void setupPowerSystem() {
		// Create the power system
		ps = new PowerSystem(PowerSystemSolutionMethod.ANL_OPF);
		addUSAGraphic();
		try {
			readBusDataFromFile();
			readLineDataFromFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create loads
		MWTimeSeries loadTimeSeries = null;
		
		loadResidential = new LoadData(ps.findNodeByNumber(3),100.0,0,true);
		loadTimeSeries = new MWTimeSeries(simClock,loadResidential,"Residential load");
		circuitpanel.getAnimatables().add(loadTimeSeries);
		loadInformation.add(loadTimeSeries);
		ps.addLoad(loadResidential);
		
		loadCommercial = new LoadData(ps.findNodeByNumber(4),100.0,0,true);
		loadTimeSeries = new MWTimeSeries(simClock,loadCommercial,"Commercial load");
		circuitpanel.getAnimatables().add(loadTimeSeries);
		loadInformation.add(loadTimeSeries);
		ps.addLoad(loadCommercial);
		
		loadIndustrial = new LoadData(ps.findNodeByNumber(5),100.0,0,true);
		loadTimeSeries = new MWTimeSeries(simClock,loadIndustrial,"Industrial load");
		circuitpanel.getAnimatables().add(loadTimeSeries);
		loadInformation.add(loadTimeSeries);
		ps.addLoad(loadIndustrial);

		// create gens
		MWTimeSeries genTimeSeries = null;
		gensWithCostsAndEmissions = new ArrayList<CostAndEmissionsProvider>();
		
//		NodeData windStorageNode = new NodeData(15,1,0,false,true,"Wind / storage interchange");
//		ps.addNode(windStorageNode);
//		BranchData windStorageNodeToNode1 = new BranchData(ps.findNodeByNumber(15),ps.findNodeByNumber(1),0.01,0,1e5,true);
//		ps.addBranch(windStorageNodeToNode1);
//		
//		NodeData windNode = new NodeData(16,1,0,false,true,"Northwest wind generation");
//		ps.addNode(windNode);
//		BranchData windNodeToWindStorageNode = new BranchData(ps.findNodeByNumber(16),ps.findNodeByNumber(15),0.01,0,1e5,true);
//		ps.addBranch(windNodeToWindStorageNode);
//		
//		NodeData storageNode = new NodeData(17,1,0,false,true,"Storage");
//		ps.addNode(storageNode);
//		BranchData storageToWindStorageNode = new BranchData(ps.findNodeByNumber(17),ps.findNodeByNumber(15),0.01,0,1e5,true);
//		ps.addBranch(storageToWindStorageNode);
		
		genWind = new WindGeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(16),initialWindGen,0,210,initialWindVariation,true,200.0,1e-5,0,0,includeFixedCostsAndEmissions);
		genWind.setAnimating(false);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genWind);
		genTimeSeries = new MWTimeSeries(simClock,genWind,"Northwest wind generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genWind);
		ps.addGenerator(genWind);		
		
		genWind2 = new WindGeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(12),initialWindGen,0,210,initialWindVariation,true,200.0,2e-5,0,0,includeFixedCostsAndEmissions);
		genWind2.setAnimating(false);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genWind2);
		genTimeSeries = new MWTimeSeries(simClock,genWind2,"Central plains wind generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genWind2);
		ps.addGenerator(genWind2);		

		genWind3 = new WindGeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(7),initialWindGen,0,210,initialWindVariation,true,200.0,3e-5,0,0,includeFixedCostsAndEmissions);
		genWind3.setAnimating(false);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genWind3);
		genTimeSeries = new MWTimeSeries(simClock,genWind3,"North Atlantic wind generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genWind3);
		ps.addGenerator(genWind3);		
		
		genWind4 = new WindGeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(10),initialWindGen,0,210,initialWindVariation,true,200.0,0,0,0,includeFixedCostsAndEmissions);
		genWind4.setAnimating(false);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genWind4);
		genTimeSeries = new MWTimeSeries(simClock,genWind3,"South Atlantic wind generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genWind4);
		ps.addGenerator(genWind4);			
		
		GeneratorDataWithLinearCostAndEmissions genCoal = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(14),600,0,2000,0,0,9999,true,2000,20.0,0,1.0,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genCoal);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genCoal);
		genTimeSeries = new MWTimeSeries(simClock,genCoal,"Coal generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		ps.addGenerator(genCoal);
		
		GeneratorDataWithLinearCostAndEmissions genGas = new GeneratorDataWithLinearCostAndEmissions(ps.findNodeByNumber(13),200,0,2000,0,0,9999,true,700.0,70.0,0,0.5,includeFixedCostsAndEmissions);
		gensWithCostsAndEmissions.add(genGas);
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genGas);
		genTimeSeries = new MWTimeSeries(simClock,genGas,"Natural gas generation");
		circuitpanel.getAnimatables().add(genTimeSeries);
		generatorInformation.add(genTimeSeries);
		ps.addGenerator(genGas);		
		
		genStorage = new StorageDevice(ps.findNodeByNumber(15),0,0,0,0,0,0,true,genWind,ps.findBranch(1, 2),simClock,1000,100);
		genTimeSeries = new MWTimeSeries(simClock,genStorage,"Storage generation");
		costAndEmissionsTimeSeries.addCostAndEmissionsProvider(genStorage);
		circuitpanel.getAnimatables().add(genTimeSeries);
		circuitpanel.getAnimatables().add(genStorage);
		generatorInformation.add(genTimeSeries);
		gensWithCostsAndEmissions.add(genStorage);
		ps.addGenerator(genStorage);		
		
		// Wind plant displays
		try {
			WindPlantDisplay winddisplay = new WindPlantDisplay(new Point2D.Double(420,5),0.0,genWind);
			windPlantDisplays.add(winddisplay);
			winddisplay.setScale(0.75);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay);
			circuitpanel.getAnimatables().add(winddisplay);
			//circuitpanel.addMouseListener(winddisplay);
			CostAndEmissionsOverlay windoverlay = new CostAndEmissionsOverlay(new Point2D.Double(358,80-46),genWind);
			costOverlays.add(windoverlay);
			circuitpanel.getTopLayerRenderables().add(windoverlay);
			WindMaxDisplay windMaxLabel = new WindMaxDisplay(new Point2D.Double(488,43),10,Color.BLACK,0,genWind);
			circuitpanel.getMiddleLayerRenderables().add(windMaxLabel);
			WindCurtailedDisplay windCurtailedLabel = new WindCurtailedDisplay(new Point2D.Double(488,43+15),10,Color.BLACK,0,genWind);
			circuitpanel.getMiddleLayerRenderables().add(windCurtailedLabel);			
		} catch (IOException ie) {
			System.out.println(ie);
		}			
		try {
			WindPlantDisplay winddisplay2 = new WindPlantDisplay(new Point2D.Double(434,220),0.0,genWind2);
			windPlantDisplays.add(winddisplay2);
			winddisplay2.setScale(0.75);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay2);
			circuitpanel.getAnimatables().add(winddisplay2);
			//circuitpanel.addMouseListener(winddisplay);
			CostAndEmissionsOverlay windoverlay2 = new CostAndEmissionsOverlay(new Point2D.Double(350,267),genWind2);
			costOverlays.add(windoverlay2);
			circuitpanel.getTopLayerRenderables().add(windoverlay2);
			WindMaxDisplay windMaxLabel = new WindMaxDisplay(new Point2D.Double(399,363),10,Color.BLACK,0,genWind2);
			circuitpanel.getMiddleLayerRenderables().add(windMaxLabel);
			WindCurtailedDisplay windCurtailedLabel = new WindCurtailedDisplay(new Point2D.Double(399,378),10,Color.BLACK,0,genWind2);
			circuitpanel.getMiddleLayerRenderables().add(windCurtailedLabel);				
		} catch (IOException ie) {
			System.out.println(ie);
		}
		try {
			WindPlantDisplay winddisplay3 = new WindPlantDisplay(new Point2D.Double(840,40),0.0,genWind3);
			windPlantDisplays.add(winddisplay3);
			winddisplay3.setScale(0.75);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay3);
			circuitpanel.getAnimatables().add(winddisplay3);
			//circuitpanel.addMouseListener(winddisplay);
			CostAndEmissionsOverlay windoverlay3 = new CostAndEmissionsOverlay(new Point2D.Double(902,20),genWind3);
			costOverlays.add(windoverlay3);
			circuitpanel.getTopLayerRenderables().add(windoverlay3);
			WindMaxDisplay windMaxLabel = new WindMaxDisplay(new Point2D.Double(800,179),10,Color.BLACK,0,genWind3);
			circuitpanel.getMiddleLayerRenderables().add(windMaxLabel);
			WindCurtailedDisplay windCurtailedLabel = new WindCurtailedDisplay(new Point2D.Double(800,179+15),10,Color.BLACK,0,genWind3);
			circuitpanel.getMiddleLayerRenderables().add(windCurtailedLabel);			
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		try {
			WindPlantDisplay winddisplay4 = new WindPlantDisplay(new Point2D.Double(840,280),0.0,genWind4);
			windPlantDisplays.add(winddisplay4);
			winddisplay4.setScale(0.75);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay4);
			circuitpanel.getAnimatables().add(winddisplay4);
			//circuitpanel.addMouseListener(winddisplay);
			CostAndEmissionsOverlay windoverlay4 = new CostAndEmissionsOverlay(new Point2D.Double(940,327),genWind4);
			costOverlays.add(windoverlay4);
			circuitpanel.getTopLayerRenderables().add(windoverlay4);
			WindMaxDisplay windMaxLabel = new WindMaxDisplay(new Point2D.Double(783,423),10,Color.BLACK,0,genWind4);
			circuitpanel.getMiddleLayerRenderables().add(windMaxLabel);
			WindCurtailedDisplay windCurtailedLabel = new WindCurtailedDisplay(new Point2D.Double(783,423+15),10,Color.BLACK,0,genWind4);
			circuitpanel.getMiddleLayerRenderables().add(windCurtailedLabel);				
		} catch (IOException ie) {
			System.out.println(ie);
		}			
		
		// Coal plant display
		try {
			CoalPlantDisplay coaldisplay = new CoalPlantDisplay(new Point2D.Double(500,160),0,0.0,genCoal);
			coaldisplay.setScale(0.75);
			circuitpanel.getMiddleLayerRenderables().add(coaldisplay);
			circuitpanel.addMouseListener(coaldisplay);
			CostAndEmissionsOverlay coaloverlay = new CostAndEmissionsOverlay(new Point2D.Double(450,191),genCoal);
			costOverlays.add(coaloverlay);
			circuitpanel.getTopLayerRenderables().add(coaloverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Gas plant display
		try {
		GasPlantDisplay gasdisplay = new GasPlantDisplay(new Point2D.Double(780,220),0.0,genGas);
		gasdisplay.setScale(0.75);
		circuitpanel.getMiddleLayerRenderables().add(gasdisplay);
		circuitpanel.addMouseListener(gasdisplay);
		CostAndEmissionsOverlay gasoverlay = new CostAndEmissionsOverlay(new Point2D.Double(901,246),genGas);
		costOverlays.add(gasoverlay);
		circuitpanel.getTopLayerRenderables().add(gasoverlay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		// Residential load
		try {
			CityDisplay residentialLoad = new CityDisplay(new Point2D.Double(640,170),CityDisplay.CityType.RESIDENTIAL,"Residential",loadResidential,0);
			residentialLoad.setScale(0.75);
			DotWithLabel cLoadDot = new DotWithLabel(682,270,2.0,"");
			cLoadDot.setDotColor(Color.ORANGE);
			circuitpanel.getTopLayerRenderables().add(cLoadDot);
			circuitpanel.getBottomLayerRenderables().add(residentialLoad);
			circuitpanel.addMouseListener(residentialLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		// Commercial load
		try {
			CityDisplay commercetonLoad = new CityDisplay(new Point2D.Double(640,252),CityDisplay.CityType.COMMERCIAL,"Commercial",loadCommercial,0);
			commercetonLoad.setScale(0.75);
			DotWithLabel cLoadDot = new DotWithLabel(682,200,2.0,"");
			cLoadDot.setDotColor(Color.ORANGE);
			circuitpanel.getTopLayerRenderables().add(cLoadDot);
			circuitpanel.getBottomLayerRenderables().add(commercetonLoad);
			circuitpanel.addMouseListener(commercetonLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		// Industrial load
		try {
			CityDisplay industrialLoad = new CityDisplay(new Point2D.Double(640,330),CityDisplay.CityType.INDUSTRIAL,"Industrial",loadIndustrial,0);
			industrialLoad.setScale(0.75);
			DotWithLabel cLoadDot = new DotWithLabel(682,340,2.0,"");
			cLoadDot.setDotColor(Color.ORANGE);
			circuitpanel.getTopLayerRenderables().add(cLoadDot);			
			circuitpanel.getBottomLayerRenderables().add(industrialLoad);
			circuitpanel.addMouseListener(industrialLoad);
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}			
		
		StorageDisplay storagedisplay = new StorageDisplay(genStorage,StorageDisplay.Alignment.LEFT,StorageDisplay.Alignment.TOP,
				new Point2D.Double(338,82),StorageDisplayOrientation.HORIZONTAL);
		storagedisplay.setScale(0.75);
		storagedisplay.setBatteryHeight(75);
		circuitpanel.getMiddleLayerRenderables().add(storagedisplay);		
		
		circuitpanel.getMiddleLayerRenderables().add(new StoredEnergyLabel(new Point2D.Double(328,64),10,Color.BLACK,0,genStorage));
		
		ps.solve();

//		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,0),12,Color.BLACK,0);
//		circuitpanel.getMiddleLayerRenderables().add(mclabel);
//		circuitpanel.addMouseMotionListener(mclabel);
		
		circuitpanel.getTopLayerRenderables().add(new BlackoutDisplay(ps));		
		
		circuitpanel.getAnimatables().add(ps);
		
		try {
			double[] biomassXs = {732};
			double[] biomassYs = {71};
			circuitpanel.getMiddleLayerRenderables().add(new PlaceholderImage(biomassXs,biomassYs,PlaceholderType.BIOMASS));
			
			double[] oceanXs = {65,779,42};
			double[] oceanYs = {27,456,143};
			circuitpanel.getMiddleLayerRenderables().add(new PlaceholderImage(oceanXs,oceanYs,PlaceholderType.OCEAN));
			
			double[] solarXs = {312,211};
			double[] solarYs = {306,278};
			circuitpanel.getBottomLayerRenderables().add(new PlaceholderImage(solarXs,solarYs,PlaceholderType.SOLAR));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		circuitpanel.getTopLayerRenderables().add(
				new SimClockDisplay(new Point2D.Double(223,173),12,Color.BLACK,0,simClock));
		
		
		TextLabel tLabel = new 	TextLabel(new Point2D.Double(81,411), 24, Color.BLACK,
				0);
		tLabel.setVerticalFlip(false);
		tLabel.setLabelText("DRAFT VERSION");
		circuitpanel.getMiddleLayerRenderables().add(tLabel);
	}
	
	BranchColorProvider branchColorProvider = new BranchColorDynamic(Color.BLACK,0.99,Color.ORANGE,1.01,Color.RED);
	BranchThicknessProvider branchThicknessProvider = new BranchThicknessDynamic(1.0,0.99,2.0,1.0,3.0);
	FlowArrowColorProvider flowArrowColorProvider = new FlowArrowColorDynamic(new Color(0,255,0,255/2),0.85,new Color(255,128,64,255/2),1.0,new Color(255,0,0,255/2));
	
	private void readBusDataFromFile() throws FileNotFoundException {
		InputStream is = getClass().getResourceAsStream("/dataFiles/DOE_Draft_Buses.csv");
	    Scanner scanner = new Scanner(is);
	    String currentDelims = ",";
	    ArrayList<String> result = new ArrayList<String>();
	    try {
	      //first use a Scanner to get each line
	    	int lineIdx = 0;
	      while ( scanner.hasNextLine() ){
	        String lineToProcess = scanner.nextLine();
//	        System.out.println("Line " + lineIdx++);
	        result.clear();
		    StringTokenizer parser = new StringTokenizer(
		      lineToProcess,
		      currentDelims);	        
		    String token = null;
		    while ( parser.hasMoreTokens() ) {
				token = parser.nextToken(currentDelims);
				result.add(token);
//				System.out.println(token);
	      	}
		    NodeData nodeToAdd = new NodeData(Integer.parseInt(result.get(0)),1.0,0.0,false,true,result.get(3));
		    if (Boolean.parseBoolean(result.get(4))) {
		    	DotWithLabel dLabel = new DotWithLabel(Double.parseDouble(result.get(1)),Double.parseDouble(result.get(2)),3.0,nodeToAdd.getName());
		    	circuitpanel.getTopLayerRenderables().add(dLabel);
		    }
		    ps.addNode(nodeToAdd);
	      }
	    }
	    finally {
	      //ensure the underlying stream is always closed
	      scanner.close();
	    }
	}	
	
	
	
	private void readLineDataFromFile() throws FileNotFoundException {
		InputStream is = getClass().getResourceAsStream("/dataFiles/DOE_Draft_Lines.csv");
	    Scanner scanner = new Scanner(is);
	    String currentDelims = ",";
	    ArrayList<String> result = new ArrayList<String>();
	    try {
	      //first use a Scanner to get each line
	    	int lineIdx = 0;
	      while ( scanner.hasNextLine() ){
	        String lineToProcess = scanner.nextLine();
//	        System.out.println("Line " + lineIdx++);
	        result.clear();
		    StringTokenizer parser = new StringTokenizer(
		      lineToProcess,
		      currentDelims);	        
		    String token = null;
		    while ( parser.hasMoreTokens() ) {
				token = parser.nextToken(currentDelims);
				result.add(token);
//				System.out.println(token);
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
		    	MWTimeSeries branchTimeSeries = new MWTimeSeries(simClock,branchToAdd,bus1.getName() + " to " + bus2.getName());
				branchFlowInformation.add(branchTimeSeries);
				circuitpanel.getAnimatables().add(branchTimeSeries);		    	
		    	ps.addBranch(branchToAdd);
			    int numCoords = Integer.parseInt(result.get(coordOffset));
			    int switchIdx = Integer.parseInt(result.get(coordOffset + 1));
			    ArrayList<Point2D.Double> coordList = new ArrayList<Point2D.Double>();
			    for (int coordIdx = 0; coordIdx < numCoords; coordIdx++) {
//			    	double xcoord = 1016*Double.parseDouble(result.get(coordOffset+1+coordIdx*2));
			    	double xcoord = Double.parseDouble(result.get(coordOffset+2+coordIdx*2));
//			    	double ycoord = 654*(1-Double.parseDouble(result.get(coordOffset+1+coordIdx*2 + 1)));
			    	double ycoord = Double.parseDouble(result.get(coordOffset+2+coordIdx*2 + 1));
			    	coordList.add(new Point2D.Double(xcoord,ycoord));
			    }
			    
//			    if (!(nodeToLinesMap.containsKey(bus1))) 
//			    	nodeToLinesMap.put(bus1, new ArrayList<BranchCoordsAndData>());
//		    	nodeToLinesMap.get(bus1).add(new BranchCoordsAndData(coordList,branchToAdd));
//		    	
//			    if (!(nodeToLinesMap.containsKey(bus2)))
//			    	nodeToLinesMap.put(bus2, new ArrayList<BranchCoordsAndData>());
//		    	nodeToLinesMap.get(bus2).add(new BranchCoordsAndData(coordList,branchToAdd));

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
		    	ArrayList<LineAndDistanceInfoProvider> lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(coordList, 
		    			0, branchThicknessProvider, 2.0, switchIdx, Math.PI/20, 
						branchToAdd, branchColorProvider, circuitpanel, true, 
						overloadMonitorParams);			    
//			    	ArrayList<LineAndDistanceInfoProvider> lines = DrawUtilities.addLinesToAnimatedPanel(coordList, 
//						0, branchThicknessProvider,  
//						branchToAdd, branchColorProvider, circuitpanel);
				    FlowArrowsForBranchData fA = new FlowArrowsForBranchData(branchToAdd,lines,0.02,40.0,10.0,flowArrowColorProvider,1000);
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
	

	private void addUSAGraphic(){
        InputStream is = getClass().getResourceAsStream("usa_outline_map.gif");
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
