package edu.uiuc.TCIP.education.lessons.development.hackerThePowerGrid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uiuc.power.dataobjects.*;
import edu.uiuc.power.displayobjects.*;
import edu.uiuc.power.displayobjects.loads.CityDisplay;

public class Lesson3PanelOld extends JPanel implements ItemListener, ActionListener {

	AnimatedPanel circuitpanel;
	AnimatedPanel totalloadplotContainer;
	Lesson3PanelOld myself;
	TotalLoadPlot totalloadplot;
	LinearVaryingLoadData loadCommercial, loadIndustrial, loadResidential;
	TimeLabel tlabel;
	JButton StartSim,StopSim,ResetSim,ResetState,ShowPlotWindow;
	BranchOverloadMonitorParameters overloadMonitorParams;
	JCheckBox branchOverloadMonitorCheckbox;
	AnimatedPanel controlAndPlotPanel;
	JFrame controlAndPlotFrame;
	JPanel SimControlPanel;
	boolean totalLoadPlotAdded = false;
	static long fps = 20;
	static long period = 1000000000L/fps;
	static int minutesPerAnimationStep = 1;
	PowerSystem ps;
	
	ArrayList<SwitchDisplayWithDistanceInfo> attackableSwitches = new ArrayList<SwitchDisplayWithDistanceInfo>();
	ArrayList<CyberAttack> cyberattacks = new ArrayList<CyberAttack>();
	HashMap<CyberAttack,SwitchDisplayWithDistanceInfo> cyberAttackToSwitchMap = new HashMap<CyberAttack,SwitchDisplayWithDistanceInfo>();
	
	private void initializeDisplay() {

		myself = this;
		overloadMonitorParams = new BranchOverloadMonitorParameters(true,50);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setupPowerSystem();
		
		ps.saveState();
		
		add(circuitpanel,BorderLayout.CENTER);
		
		JPanel mainFrameControlPanel = new JPanel();
		mainFrameControlPanel.setLayout(new FlowLayout());
		mainFrameControlPanel.setBackground(Color.WHITE);
		
		branchOverloadMonitorCheckbox = new JCheckBox("Monitor branch overloads");
		branchOverloadMonitorCheckbox.setSelected(overloadMonitorParams.getStatus());
		branchOverloadMonitorCheckbox.addItemListener(this);
		branchOverloadMonitorCheckbox.setBackground(Color.WHITE);

		mainFrameControlPanel.add(branchOverloadMonitorCheckbox,BorderLayout.WEST);

		// Set up simulation start/stop/reset buttons on SimControlPanel
		SimControlPanel = new JPanel();
		StartSim = new JButton("Start Time");
		StartSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				totalloadplot.startIntegration();
				StartSim.setEnabled(false);
				StopSim.setEnabled(true);
				tlabel.setPaused(false);
				loadCommercial.setPaused(false);
				loadIndustrial.setPaused(false);
				loadResidential.setPaused(false);				
			}			
		});
		StopSim = new JButton("Pause Time");
		StopSim.setEnabled(false);
		StopSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				totalloadplot.stopIntegration();
				StartSim.setEnabled(true);
				StopSim.setEnabled(false);
				tlabel.setPaused(true);
				loadCommercial.setPaused(true);
				loadIndustrial.setPaused(true);
				loadResidential.setPaused(true);				
			}
		});
		ResetSim = new JButton("Reset Time");
		ResetSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				totalloadplot.resettime();
				tlabel.resettime();
				loadCommercial.resettime();
				loadResidential.resettime();
				loadIndustrial.resettime();
				if (totalloadplot.getIntegrationStatus() == totalloadplot.STOPPED)
					StartSim.setEnabled(true);
			}
		});
		SimControlPanel.setLayout(new FlowLayout());
		SimControlPanel.add(StartSim);
		SimControlPanel.add(StopSim);
		SimControlPanel.add(ResetSim);
		SimControlPanel.setBackground(Color.WHITE);

		totalloadplotContainer = new AnimatedPanel(725,310,period,16,true);
		controlAndPlotFrame = new JFrame("Plot and time controls");
		controlAndPlotFrame.getContentPane().setBackground(Color.WHITE);
		controlAndPlotFrame.setSize(725,310);
		controlAndPlotFrame.setLayout(new BorderLayout());
		controlAndPlotFrame.setBackground(Color.WHITE);
		controlAndPlotFrame.setResizable(false);
		totalloadplotContainer.getTopLayerRenderables().add(totalloadplot);
		totalloadplotContainer.getAnimatables().add(totalloadplot);
		totalloadplotContainer.startGame();
		controlAndPlotFrame.add(totalloadplotContainer,BorderLayout.CENTER);
		controlAndPlotFrame.add(SimControlPanel,BorderLayout.SOUTH);
		controlAndPlotFrame.setVisible(false);
		
		ShowPlotWindow = new JButton("Open Plot and Time Controls Window");
		ShowPlotWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controlAndPlotFrame.setVisible(true);
			}
		});
		
		mainFrameControlPanel.add(ShowPlotWindow,BorderLayout.EAST);

		ResetState = new JButton("Reset Simulation");
		ResetState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				totalloadplot.stopIntegration();
				StartSim.setEnabled(true);
				StopSim.setEnabled(false);
				loadCommercial.setPaused(true);
				loadIndustrial.setPaused(true);
				loadResidential.setPaused(true);
				
				ps.restoreState();

				totalloadplot.resettime();
				tlabel.resettime();
				loadCommercial.resettime();
				loadResidential.resettime();
				loadIndustrial.resettime();
			}
		});
		mainFrameControlPanel.add(ResetState,BorderLayout.CENTER);
		
		JButton AttackButton = new JButton("Cyber-Attack!");
		AttackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//HashMap<CyberAttack,SwitchDisplayWithDistanceInfo> cyberAttackToSwitchMap
				
				int arrayIdx = (int)(Math.floor(Math.random()*(attackableSwitches.size() - 1e-10)));
				
				CyberAttack newattack;
				try {
					SwitchDisplayWithDistanceInfo attackSwitch = attackableSwitches.get(arrayIdx);
					double halfSwitchLength = attackSwitch.getLength()/2;
					Point2D.Double toPoint = DrawUtilities.getPointAtDistanceOnLine(attackSwitch, halfSwitchLength);
					
					newattack = new CyberAttack(new Point2D.Double(736,59),
							toPoint,50);
					
					cyberAttackToSwitchMap.put(newattack, attackSwitch);
					newattack.addActionListener(myself);
					circuitpanel.getTopLayerRenderables().add(newattack);
					circuitpanel.getAnimatables().add(newattack);
					cyberattacks.add(newattack);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});		
		mainFrameControlPanel.add(AttackButton,BorderLayout.EAST);
		
		add(mainFrameControlPanel,BorderLayout.SOUTH);
		// start with it stopped
		totalloadplot.stopIntegration();
		StartSim.setEnabled(true);
		StopSim.setEnabled(false);
		tlabel.setPaused(true);
		loadCommercial.setPaused(true);
		loadIndustrial.setPaused(true);
		loadResidential.setPaused(true);		
	}
	
	public Lesson3PanelOld() {
		initializeDisplay();
	}

	private void setupPowerSystem() {
		
		
		// Set up display elements
		
		
		circuitpanel = new AnimatedPanel(800,810,period,16,true);	
		
		// Create nodes
		NodeData nodeExternalSystem = new NodeData(0,1.0,0,true);
		NodeData nodeExternalSystem2 = new NodeData(999,1.0,0,false,true);
		NodeData nodeOne = new NodeData(1,1.0,0,false);
		NodeData nodeTwo = new NodeData(2,1.0,0,false);
		NodeData nodeThree = new NodeData(3,1.0,0,false);
		NodeData nodeFour = new NodeData(4,1.0,0,false);
		NodeData nodeFive = new NodeData(5,1.0,0,false);
		NodeData nodeFifteen = new NodeData(15,1.0,0,false);
		NodeData nodeSix = new NodeData(6,1.0,0,false);
		NodeData nodeEight = new NodeData(8,1.0,0,false);
		NodeData nodeNine = new NodeData(9,1.0,0,false);
		NodeData nodeTen = new NodeData(10,1.0,0,false);
		NodeData nodeTwelve = new NodeData(12,1.0,0,false);
		NodeData nodeThirteen = new NodeData(13,1.0,0,false);
		NodeData nodeFourteen = new NodeData(14,1.0,0,false);
		NodeData nodeSixteen = new NodeData(16,1.0,0,false);
		
		// Create lines
		BranchData branchExternalSystemToOne = new BranchData(nodeExternalSystem,nodeOne,0.01,0,1500.0,true);
		BranchData branchOneToTwo = new BranchData(nodeOne,nodeTwo,0.01,0,1500.0,true);
		BranchData branchTwoToThree = new BranchData(nodeTwo,nodeThree,0.01,0,1500.0,true);
		BranchData branchTwoToFour = new BranchData(nodeTwo,nodeFour,0.01,0,1500.0,true);
		BranchData branchOneToFive = new BranchData(nodeOne,nodeFive,0.01,0,1500.0,true);
		BranchData branchFiveToFifteen = new BranchData(nodeFive,nodeFifteen,0.01,0,1500.0,true);
		BranchData branchFiveToSix = new BranchData(nodeFive,nodeSix,0.01,0,1500.0,true);
		BranchData branchFourToSix = new BranchData(nodeFour,nodeSix,0.01,0,1500.0,true);
		BranchData branchTwoToEight = new BranchData(nodeTwo,nodeEight,0.01,0,1500.0,true);
		BranchData branchThreeToNine = new BranchData(nodeThree,nodeNine,0.01,0,1500.0,true);
		BranchData branchThreeToFourteen = new BranchData(nodeThree,nodeFourteen,0.01,0,1500.0,true);
		BranchData branchSixToThirteen = new BranchData(nodeSix,nodeThirteen,0.01,0,1500.0,true);
		BranchData branchSixToSixteen = new BranchData(nodeSix,nodeSixteen,0.01,0,1500.0,true);
		BranchData branchSixToExternalSystemTwo = new BranchData(nodeSix,nodeExternalSystem2,0.01,0,1500.0,true);
		BranchData branchFiveToTwelve = new BranchData(nodeFive,nodeTwelve,0.01,0,1500.0,true);
		BranchData branchFourToTen = new BranchData(nodeFour,nodeTen,0.01,0,1500.0,false);
		
		// Create loads
		ArrayList<LinearVaryingLoadPoint> loadCommercialPoints = new ArrayList<LinearVaryingLoadPoint>();
		loadCommercialPoints.add(new LinearVaryingLoadPoint(300,0,0));
		loadCommercialPoints.add(new LinearVaryingLoadPoint(300,0,7));
		loadCommercialPoints.add(new LinearVaryingLoadPoint(900,0,10));
		loadCommercialPoints.add(new LinearVaryingLoadPoint(900,0,14));
		loadCommercialPoints.add(new LinearVaryingLoadPoint(300,0,17));
		loadCommercialPoints.add(new LinearVaryingLoadPoint(300,0,23)); 
		loadCommercial = new LinearVaryingLoadData(nodeTwelve,true,loadCommercialPoints,minutesPerAnimationStep/60.0);
		circuitpanel.getAnimatables().add(loadCommercial);
		
		ArrayList<LinearVaryingLoadPoint> loadIndustrialPoints = new ArrayList<LinearVaryingLoadPoint>();
		loadIndustrialPoints.add(new LinearVaryingLoadPoint(100,0,0));
		loadIndustrialPoints.add(new LinearVaryingLoadPoint(100,0,6));
		loadIndustrialPoints.add(new LinearVaryingLoadPoint(1200,0,9));
		loadIndustrialPoints.add(new LinearVaryingLoadPoint(1200,0,15));
		loadIndustrialPoints.add(new LinearVaryingLoadPoint(100,0,18));
		loadIndustrialPoints.add(new LinearVaryingLoadPoint(100,0,23));
		loadIndustrial = new LinearVaryingLoadData(nodeFourteen,true,loadIndustrialPoints,minutesPerAnimationStep/60.0);
		circuitpanel.getAnimatables().add(loadIndustrial);
		//LoadData loadIndustrial = new LoadData(nodeFourteen,2000,0,true);
		
		ArrayList<LinearVaryingLoadPoint> loadResidentialPoints = new ArrayList<LinearVaryingLoadPoint>();
		loadResidentialPoints.add(new LinearVaryingLoadPoint(500,0,0));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(600,0,4));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(500,0,7));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(800,0,11));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(800,0,13));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(500,0,17));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(600,0,20));
		loadResidentialPoints.add(new LinearVaryingLoadPoint(500,0,23));
		loadResidential = new LinearVaryingLoadData(nodeThirteen,true,loadResidentialPoints,minutesPerAnimationStep/60.0);
		circuitpanel.getAnimatables().add(loadResidential);
		//LoadData loadResidential = new LoadData(nodeThirteen,800,0,true);
		
		// Create gens
		GeneratorData genHydro = new GeneratorData(nodeEight,1000,1000,1000,0,0,9999,true);
		GeneratorData genSlack = new GeneratorData(nodeExternalSystem,0,-10000,10000,0,0,9999,true);
		GeneratorData genExternal2 = new GeneratorData(nodeExternalSystem2,0,-10000,10000,0,0,9999,true);
		GeneratorData genCoal = new GeneratorData(nodeNine,600,300,700,0,0,9999,true);
		GeneratorData genGas = new GeneratorData(nodeFifteen,200,0,500,0,0,9999,true);
		GeneratorData genNuke = new GeneratorData(nodeTen,900,900,900,0,0,9999,true);
		GeneratorData genWind = new GeneratorData(nodeSixteen,54,54,54,0,0,9999,true);
		
		// Register with power system
		ps = new PowerSystem();
		
		ps.addNode(nodeExternalSystem);
		ps.addNode(nodeExternalSystem2);
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
		
		
		ps.addBranch(branchExternalSystemToOne);
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
		ps.addBranch(branchSixToExternalSystemTwo);
		ps.addBranch(branchFiveToTwelve);
		ps.addBranch(branchFourToTen);
		
		ps.addLoad(loadCommercial);
		ps.addLoad(loadIndustrial);
		ps.addLoad(loadResidential);
		
		ps.addGenerator(genHydro);
		ps.addGenerator(genSlack);
		ps.addGenerator(genCoal);
		ps.addGenerator(genGas);
		ps.addGenerator(genNuke);
		ps.addGenerator(genWind);
		ps.addGenerator(genExternal2);
		
		ps.solve();
		
		ArrayList<LineAndDistanceInfoProvider> lines; 
		FlowArrowsForBranchData fA;
		
		BranchColorProvider branchColorProvider = new BranchColorDynamic(Color.BLACK,0.85,Color.ORANGE,1.0,Color.RED);
		BranchThicknessProvider branchThicknessProvider = new BranchThicknessDynamic(1.0,0.85,2.0,1.0,3.0);
		FlowArrowColorProvider flowArrowColorProvider = new FlowArrowColorDynamic(new Color(0,255,0,255/2),0.85,new Color(255,128,64,255/2),1.0,new Color(255,0,0,255/2));
		
		double switchthickness = 5.0;
		
		// Line External System to One
		ArrayList<Point2D.Double> pointsExternalToOne = new ArrayList<Point2D.Double>();
		pointsExternalToOne.add(new Point2D.Double(50,40));
		pointsExternalToOne.add(new Point2D.Double(150,40));
		pointsExternalToOne.add(new Point2D.Double(200,40));
		pointsExternalToOne.add(new Point2D.Double(350,40));
		pointsExternalToOne.add(new Point2D.Double(350,100));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsExternalToOne, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchExternalSystemToOne, branchColorProvider, circuitpanel, overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchExternalSystemToOne,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
		// Line Six To External System 2
		ArrayList<Point2D.Double> pointsSixToExternalSystem2 = new ArrayList<Point2D.Double>();
		pointsSixToExternalSystem2.add(new Point2D.Double(480,325));
		pointsSixToExternalSystem2.add(new Point2D.Double(535,350));
		pointsSixToExternalSystem2.add(new Point2D.Double(585,350));
		pointsSixToExternalSystem2.add(new Point2D.Double(625,350));
		pointsSixToExternalSystem2.add(new Point2D.Double(625,290));
		pointsSixToExternalSystem2.add(new Point2D.Double(665,290));
		lines = DrawUtilities.addLineWithSwitchToAnimatedPanel(pointsSixToExternalSystem2, 0, branchThicknessProvider, switchthickness, 1, Math.PI/20, branchSixToExternalSystemTwo, branchColorProvider, circuitpanel,true,overloadMonitorParams);
		fA = new FlowArrowsForBranchData(branchSixToExternalSystemTwo,lines,0.05,20.0,10.0,flowArrowColorProvider);
		circuitpanel.getMiddleLayerRenderables().add(fA);
		circuitpanel.getAnimatables().add(fA);			
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
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
		addSwitchToAttackableSwitchesFromArrayList(lines);
		
		// Substation at node 1
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(300,85),1));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Substation at node 2
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(75,180),2));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Substation at node 3
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(10,300),3));
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		// Substation at node 5
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(425,175),5));
		} catch (IOException ie) {
			System.out.println(ie);
		}		

		// Substation at node 6
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(425,300),6));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Substation at node 4
		try {
			circuitpanel.getMiddleLayerRenderables().add(new SubstationDisplay(new Point2D.Double(200,365),4));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// External connection at node ExternalSystem
		try {
			circuitpanel.getMiddleLayerRenderables().add(new ExternalSystemDisplay(new Point2D.Double(20,20)));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// External connection at node ExternalSystem2
		try {
			circuitpanel.getMiddleLayerRenderables().add(new ExternalSystemDisplay(new Point2D.Double(650,255)));
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Coal plant at node 9
		try {
			CoalPlantDisplay coaldisplay = new CoalPlantDisplay(new Point2D.Double(0,425),0,100.0,genCoal);
			circuitpanel.getMiddleLayerRenderables().add(coaldisplay);
			circuitpanel.addMouseListener(coaldisplay);
		} catch (IOException ie) {
			System.out.println(ie);
		}
		
		// Hydro plant at node 8
		try {
			HydroelectricDisplay hydrodisplay = new HydroelectricDisplay(new Point2D.Double(240,175),100.0,genHydro);
			circuitpanel.getMiddleLayerRenderables().add(hydrodisplay);
			circuitpanel.addMouseListener(hydrodisplay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		

		
		// Nuke plant at node 10
		try {
			NukePlantDisplay nukedisplay = new NukePlantDisplay(new Point2D.Double(460,420),100.0,genNuke);
			circuitpanel.getMiddleLayerRenderables().add(nukedisplay);
			circuitpanel.addMouseListener(nukedisplay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		
		
		// Gas plant at node 6
		try {
			GasPlantDisplay gasdisplay = new GasPlantDisplay(new Point2D.Double(300,260),100.0,genGas);
			circuitpanel.getMiddleLayerRenderables().add(gasdisplay);
			circuitpanel.addMouseListener(gasdisplay);
		} catch (IOException ie) {
			System.out.println(ie);
		}		

		// Branch External to One flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(221,52),10,new Color(0f,0f,0f,1f),0,branchExternalSystemToOne));

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

		// Branch Six to ExternalSystem2 flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(535,325),10,new Color(0f,0f,0f,1f),0,branchSixToExternalSystemTwo));		
		
		// Branch Four to Six flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(415,415),10,new Color(0f,0f,0f,1f),-Math.PI/3.5,branchFourToSix));		

		// Branch Four to Ten flow label 
		circuitpanel.getMiddleLayerRenderables().add(new BranchMWFlowLabel(new Point2D.Double(280,475),10,new Color(0f,0f,0f,1f),0,branchFourToTen));		
		
		// Commercial city at node 12
		try {
			circuitpanel.getMiddleLayerRenderables().add(new CityDisplay(new Point2D.Double(415,25),CityDisplay.CityType.COMMERCIAL,"Commerceton",loadCommercial));
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		
		// Residential city at node 13
		try {
			circuitpanel.getMiddleLayerRenderables().add(new CityDisplay(new Point2D.Double(603,135),CityDisplay.CityType.RESIDENTIAL,"Residenceburg",loadResidential));
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}
		
		// Industrial city at node 13
		try {
			circuitpanel.getMiddleLayerRenderables().add(new CityDisplay(new Point2D.Double(126,429),CityDisplay.CityType.INDUSTRIAL,"Industryville",loadIndustrial));
		} catch (IOException ie) {
			System.err.println(ie.toString());
		}		
		
		totalloadplot = new TotalLoadPlot(
				new Point2D.Double(10,10),
				700,200,
				ps,
				minutesPerAnimationStep,
				0,24,
				0,3000);
		
		/*
		MouseCoordinateLabel mclabel = new MouseCoordinateLabel(new Point2D.Double(0,20),12,Color.BLACK,0);
		circuitpanel.getTopLayerRenderables().add(mclabel);
		circuitpanel.addMouseMotionListener(mclabel);
		*/
		
		tlabel = new TimeLabel(new Point2D.Double(0,0),12,Color.BLACK,0,minutesPerAnimationStep,0);
		//circuitpanel.getRenderables().add(tlabel);
		//circuitpanel.getAnimatables().add(tlabel);
		circuitpanel.getAnimatables().add(ps);
		
		// Wind plant at node sixteen
		/*
		try {
			WindPlantDisplay winddisplay = new WindPlantDisplay(new Point2D.Double(650,340),100.0,genWind);
			circuitpanel.getMiddleLayerRenderables().add(winddisplay);
			circuitpanel.getAnimatables().add(winddisplay);
			circuitpanel.addMouseListener(winddisplay);
		} catch (IOException ie) {
			System.out.println(ie);
		}
		*/
		
		// Hacker
		try {
			CyberAttacker attacker = new CyberAttacker(new Point2D.Double(700,0),
					100,100);
			circuitpanel.getMiddleLayerRenderables().add(attacker);
		} catch (IOException ie) {
			System.out.println(ie);
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
	
	private SwitchDisplayWithDistanceInfo extractSwitchFromLineArraylist(ArrayList<LineAndDistanceInfoProvider> lines) {
		SwitchDisplayWithDistanceInfo retSwitch = null;
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).getClass() == SwitchDisplayWithDistanceInfo.class) {
				//System.out.println("found switch at index " + i);
				retSwitch = (SwitchDisplayWithDistanceInfo)(lines.get(i));
			}
		}
		
		return retSwitch;
	}
	
	private void addSwitchToAttackableSwitchesFromArrayList(ArrayList<LineAndDistanceInfoProvider> lines) {
		SwitchDisplayWithDistanceInfo attackSwitch = extractSwitchFromLineArraylist(lines);
		if (attackSwitch != null) {
			attackableSwitches.add(attackSwitch);
		}
	}
	

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Cyberattack Complete") {
			int i = 0;
			boolean foundAttack = false;
			while ((i < cyberattacks.size()) & (!foundAttack)) {
				if (cyberattacks.get(i) == e.getSource()) {
					//System.out.println("removing cyberattack at index " + i);
					cyberattacks.remove(i);
					circuitpanel.getTopLayerRenderables().remove(e.getSource());
					circuitpanel.getAnimatables().remove(e.getSource());
					SwitchDisplayWithDistanceInfo attackSwitch = cyberAttackToSwitchMap.get(e.getSource());
					attackSwitch.toggleSwitch();
					//ps.solve();
					//cyberAttackToBranchDataMap.get(e.getSource()).setClosed(false);
					foundAttack = true;
				}
				i++;
			}
		}
	}
}
