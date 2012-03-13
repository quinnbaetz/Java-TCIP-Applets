package anl.lpsolver;
/*
 *
 *  Revised by Yu WEI
 *  Summer 2004
 *  email: weiy3@mcmaster.ca
 *
 *  Written by Timothy J. Wisniewski
 *  Summer 1996
 *  email:  tjw@euler.bd.psu.edu
 *
 *  Updated by Joe Czyzyk
 *  September - October 1996
 *
 *  Copyright 2004 Optimization Technology Center
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class numberCrunchingFrame extends JFrame implements ActionListener
{
  SimplexTool outerParent;
  revisedSimplex LP;   /* pointer to LP corresponding to problem in frame */

  JPanel ObjPanel;
  JPanel UserObjFunc;
  JPanel ObjFunc;
  JLabel VarLabelObj;

  JPanel Apanel;

  JPanel ReducedCostPanel;
  JLabel ReducedCostLabel = new JLabel("The Reduced Costs");
  JLabel RedCosts[];
  CheckboxGroup usersChoice;
  Checkbox whichVariable[];

  JPanel Bpanel;
  colorPanel Acoefficients[];
  JLabel Bcoefficients[][];

  JPanel ObjValuePanel;
  
  float coefficients[];
  
  JPanel MessagePanel;
  JTextArea Messages;
  
  JPanel legend;
  JLabel basic      = new JLabel(" Basic Variables ");
  JLabel slack      = new JLabel(" Slack/Surplus Variable ");
  JLabel artificial = new JLabel(" Artificial Variable ");
  JLabel entering   = new JLabel(" Entering Variable ");
  JLabel leaving    = new JLabel(" Leaving Variable ");
  
  JPanel otherStuff;
  colorPanel yB;
  colorPanel pi;
  colorPanel x;
  JLabel      Value;
  colorPanel littleCost;
  colorPanel rhs;

  JPanel  ButtonPanel;
  JButton step    = new JButton("Next Operation");
  JButton iterate = new JButton("Do A Full Iterate");
  JButton quit    = new JButton("Quit");

  public numberCrunchingFrame(SimplexTool target, String title, 
			      revisedSimplex LPpointer)
    {

      super(title);
	  
	  Container c = getContentPane();
	  c.setBackground(Color.white);

	  basic.setOpaque(true);
	  slack.setOpaque(true);
	  artificial.setOpaque(true);
	  entering.setOpaque(true);
	  leaving.setOpaque(true);

      this.outerParent = target;
      c.setLayout(new GridBagLayout());
      
	  step.addActionListener(this);
	  iterate.addActionListener(this);
	  quit.addActionListener(this);

      LP = LPpointer;
      
      coefficients = new float[LP.numVariables];
      
      this.makePanels();

      outerParent.constrain(c, ObjPanel,     0,0,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, Apanel,       0,1,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, ReducedCostPanel, 0,2,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, otherStuff,   0,3,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, ObjValuePanel,0,4,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, MessagePanel, 0,5,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, ButtonPanel,  0,6,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      outerParent.constrain(c, legend,       0,7,1,1, 
			    GridBagConstraints.HORIZONTAL, 
			    GridBagConstraints.NORTHWEST,
			    1.0, 1.0, 5,5,5,5);
      this.pack();
      this.show();
    } /* end numberCrunchingFrame procedure */
  
  void makePanels()
    {
      int i;

      /* Make panel for objective functions */
      /*    user-defined objective function */

      JLabel varLabelObj[] = new JLabel[LP.numVariables];

      ObjPanel = new JPanel();
	  ObjPanel.setBackground(Color.white);
      ObjPanel.setLayout(new GridLayout(4,1));

      if (LP.oldOptType == true)
	ObjPanel.add(new JLabel("Your Objective: Minimize"));
      else
	ObjPanel.add(new JLabel("Your Objective: Maximize"));

      UserObjFunc = new JPanel();
	  UserObjFunc.setBackground(Color.white);
      UserObjFunc.setLayout(new FlowLayout(FlowLayout.LEFT));

      for (i = 0; i < outerParent.numVariables; i++) {
	
	if (outerParent.dataFrame.objective[i].getFloat() < 0.0 || (i == 0))
	  UserObjFunc.add
	    (new JLabel(" "+ outerParent.dataFrame.objective[i].getFloat()
		       +" x"+(i+1)));
	else
	  UserObjFunc.add
	    (new JLabel("+ "+ outerParent.dataFrame.objective[i].getFloat()
		       +" x"+(i+1)));
      }

      ObjPanel.add(UserObjFunc);

      /*    preprocessed objective function */

      ObjPanel.add(new JLabel("Preprocessed Objective: Minimize"));

      ObjFunc = new JPanel();
	  ObjFunc.setBackground(Color.white);
      ObjFunc.setLayout(new FlowLayout(FlowLayout.LEFT));

      varLabelObj = new JLabel[LP.numVariables];

      for (i = 0; i < LP.numVariables; i++) {
	varLabelObj[i] = new JLabel();
	if (LP.cost[i] < 0.0 || (i == 0))
	  varLabelObj[i].setText(" "+Float.toString(LP.cost[i])+
				 " x"+(i+1));
	else
	  varLabelObj[i].setText("+ "+Float.toString(LP.cost[i])+
				 " x"+(i+1));

	varLabelObj[i].setOpaque(true);

	if (LP.varType[i] == LP.Regular) 
	  varLabelObj[i].setBackground(Color.lightGray);
	else if (LP.varType[i] == LP.SlackOrSurplus) 
	
	  varLabelObj[i].setBackground(Color.cyan); 
	else if (LP.varType[i] == LP.Artificial)
	  varLabelObj[i].setBackground(Color.orange);

	
	ObjFunc.add(varLabelObj[i]);
      }

      ObjPanel.add(ObjFunc);

      /* create panel for coefficient matrix */

      Apanel = new JPanel();
	  Apanel.setBackground(Color.white);
      Apanel.setLayout(new GridBagLayout());
      
      Acoefficients  = new colorPanel[LP.numVariables];
      outerParent.constrain(Apanel,new JLabel("Constraint Matrix:"), 0, 0,
			    LP.numVariables, 1, 0, 0, 5, 0);
            
      for (i = 0; i < LP.numVariables; i++) {
		  Acoefficients[i] = new colorPanel(LP.numConstraints, Color.lightGray);
		  
		  for (int j = 0; j < LP.numConstraints; j++)
			  Acoefficients[i].numbers[j].setText(Float.toString
					      (LP.A[j][i])+"     ");
		  outerParent.constrain(Apanel, Acoefficients[i], i, 1, 1, 1, 5, 0, 0, 0);
      }
      
      for (i = 0; i < LP.numConstraints; i++) {
		  Acoefficients[LP.BasicVariables[i]].setBackground(Color.red);
	  }

      for (i = 0; i < LP.numNonbasic; i++) {
		  Acoefficients[LP.NonBasicVariables[i]].setBackground(Color.lightGray);
      }


      rhs = new colorPanel(LP.numConstraints, Color.white);
      
      for (i = 0;i < LP.numConstraints; i++)
		  rhs.numbers[i].setText(Float.toString(LP.b[i]));
	  
	  outerParent.constrain(Apanel, new JLabel("RHS"), 
			    LP.numVariables + 1, 0, 1, 1,
			    0, 5, 10, 0);
      outerParent.constrain(Apanel, rhs, LP.numVariables + 1, 1, 1, 4,
			    5, 10, 0, 0);

      /* add reduced cost buttons to panel */
      
      ReducedCostPanel = new JPanel();
	  ReducedCostPanel.setBackground(Color.white);
      ReducedCostPanel.setLayout(new GridBagLayout());

      outerParent.constrain(ReducedCostPanel, ReducedCostLabel, 
			    0, 0, LP.numVariables,1, 5,5,5,5);

      usersChoice   = new CheckboxGroup();
      whichVariable = new Checkbox[LP.numVariables];
      RedCosts      = new JLabel[LP.numVariables];

      for (i = 0; i < LP.numVariables; i++) {
	RedCosts[i] = new JLabel("             ");
	RedCosts[i].setPreferredSize(new Dimension(60,20));
	whichVariable[i] = new Checkbox((String)"x"+(i+1),usersChoice,false);
	whichVariable[i].setEnabled(false);
	outerParent.constrain(ReducedCostPanel,RedCosts[i], i,1,1,1,
			      GridBagConstraints.HORIZONTAL,
			      GridBagConstraints.WEST,
			      1.0,1.0, 0,0,0,0);

	outerParent.constrain(ReducedCostPanel,whichVariable[i], i,2,1,1,
			      GridBagConstraints.HORIZONTAL,
			      GridBagConstraints.WEST,
			      1.0,1.0, 0,0,0,0);
      }

      /* Add other items on JPanel */
      
      otherStuff = new JPanel();
	  otherStuff.setBackground(Color.white);
      otherStuff.setLayout(new GridBagLayout());
	  JLabel xlabel = new JLabel("x");
	  xlabel.setPreferredSize(new Dimension(100,10));
      outerParent.constrain(otherStuff, xlabel, 0,0,1,1, 0,0,0,2);
      x = new colorPanel(LP.numConstraints, Color.magenta);
      
      for (i = 0; i < LP.numConstraints; i++)
		  x.numbers[i].setText("x"+(LP.BasicVariables[i]+1)+" = "+ Float.toString(LP.x[i]) +"      ");
       
	  outerParent.constrain(otherStuff, x, 0,1,1,1, 
			    GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.WEST,
			    1.0,1.0, 0,0,0,2);
      
      outerParent.constrain(otherStuff, new JLabel("cB"), 1,0,1,1, 0,2,0,2);
      littleCost = new colorPanel(LP.numConstraints, Color.green);
      for (i = 0; i < LP.numConstraints; i++)
		  littleCost.numbers[i].setText(" "+Float.toString (LP.cost[LP.BasicVariables[i]])+"      ");
      
	  outerParent.constrain(otherStuff, littleCost, 1,1,1,1,
 			    GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.WEST,
			    1.0,1.0, 0,2,0,2);
      
      outerParent.constrain(otherStuff,new JLabel("yB"),2,0,1,1, 
			    GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.WEST,
			    1.0,1.0, 0,2,0,2);
      yB = new colorPanel(LP.numConstraints, Color.white);
      for (i = 0; i < LP.numConstraints; i++)
		  yB.numbers[i].setText("      ");
	  
	  outerParent.constrain(otherStuff,yB,2,1,1,1, 
			    GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.WEST,
			    1.0,1.0, 0,2,0,2);
      
      outerParent.constrain(otherStuff,new JLabel("pi"),3,0,1,1, 0,2,0,2);
      pi = new colorPanel(LP.numConstraints, Color.lightGray);
      for (i = 0; i < LP.numConstraints; i++)
		  pi.numbers[i].setText("      ");
	  
	  outerParent.constrain(otherStuff,pi,3,1,1,1, 
			    GridBagConstraints.HORIZONTAL,
			    GridBagConstraints.WEST,
			    1.0,1.0, 0,2,0,2);
      
      Bpanel = new JPanel();
	  Bpanel.setBackground(Color.white);
      Bpanel.setLayout(new GridBagLayout());

      Bcoefficients = new JLabel[LP.numConstraints][LP.numConstraints];

      outerParent.constrain(Bpanel, new JLabel("The B matrix."),
			    0, 0, LP.numConstraints, 1);
      
      for (i = 0; i < LP.numConstraints; i++) {
		  Bcoefficients[i] = new JLabel[LP.numConstraints];
		  for (int j = 0; j < LP.numConstraints; j++) {
			Bcoefficients[i][j] = new JLabel("      ");
			Bcoefficients[i][j].setPreferredSize(new Dimension(60, 20));

			Bcoefficients[i][j].setOpaque(true);
			Bcoefficients[i][j].setBackground(Color.red);
		  
		    outerParent.constrain(Bpanel, Bcoefficients[i][j], j, i+1, 1, 1);
		  }
      }

      outerParent.constrain(otherStuff, Bpanel, 4,0,1,4, 0,10,0,0);

      /* create objective value panel */

      ObjValuePanel = new JPanel();
	  ObjValuePanel.setBackground(Color.white);
      Value    = new JLabel();
      Value.setText("                       ");

      ObjValuePanel.add(new JLabel("Current Objective Value:"));
      ObjValuePanel.add(Value);

      /* create message panel */
      
      MessagePanel = new JPanel();
	  MessagePanel.setBackground(Color.white);
      Messages     = new JTextArea(3,20);
	  Messages.setBackground(Color.lightGray);
      Messages.setEditable(false);
      
      /*if (outerParent.LP.artificialPresent == true)*/
      if (outerParent.LP.NumArtificials > 0)
		Messages.setText("Artificial Variable Added\nUsing Two Phase Method.");
      else
		Messages.setText("Ready!");
      
      MessagePanel.add(new JLabel("Messages:"));
      MessagePanel.add(Messages);
      
      legend = new JPanel();
	  legend.setBackground(Color.white);
      legend.setLayout(new GridLayout(2,3));
      basic.setBackground(Color.red);
      slack.setBackground(Color.cyan);
      artificial.setBackground(Color.orange);
      entering.setBackground(Color.yellow);
      leaving.setBackground(Color.white);
      
      legend.add(new JLabel("Color Legend"));
      legend.add(basic);
      legend.add(slack);
      legend.add(artificial);
      legend.add(entering);
      legend.add(leaving);

      ButtonPanel = new JPanel();
	  ButtonPanel.setBackground(Color.white);
      ButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

      ButtonPanel.add(step);
      ButtonPanel.add(iterate);
      ButtonPanel.add(quit);
    } /* end makePanels procedure */

  public void actionPerformed(ActionEvent e)
   {
      boolean OneStep = true;
      boolean OneIter = false;
	  
	  if (e.getSource() == step) {
		  iterate.setEnabled(false);
		  outerParent.update(this, OneStep);
	  }
	  
	  if (e.getSource() == iterate) {
		  outerParent.update(this, OneIter);
	  }

	if (e.getSource() == quit) {
	  
	  if (!outerParent.start.isEnabled()) outerParent.start.setEnabled(true);
	  if (!outerParent.about.isEnabled()) outerParent.about.setEnabled(true);
	  
	  outerParent.activeCruncher.dispose();
	  
	}
  
  } /* end action procedure */
  
  public void updatePanelsForOneStep()
    {
      int CurrentStep = LP.CurrentStep;
      int EnteringVariable;
      int index;
      int i;
      
      switch (CurrentStep) {
      case 1: 	/* update B */
		for (i = 0; i < LP.numConstraints; i++)
		  for (int j = 0; j < LP.numConstraints; j++)
			Bcoefficients[j][i].setText
			(Float.toString(LP.A[j][LP.BasicVariables[i]]));
		outerParent.activeCruncher.Messages.setText("Made B.");
		break;
	  
      case 2:	/* update pi*/  
		for (i = 0; i < LP.numConstraints; i++)
		  pi.numbers[i].setText(" "+Float.toString(LP.pi[i]));
		  outerParent.activeCruncher.Messages.setText
			  ("Calculated B^T pi = cB.");
		break;
	  
      case 3:	/* update reduced costs */
		for (i = 0; i < LP.numNonbasic; i++) {
		  RedCosts[LP.NonBasicVariables[i]].setText
			(Float.toString(LP.reducedCost[i]));
		}

		for (i = 0; i < LP.numConstraints; i++) {
		  RedCosts[LP.BasicVariables[i]].setText("Basic");
		}

		outerParent.activeCruncher.Messages.setText("Calculated reduced costs.\nTest for Optimality.");
		break;
      
      case 4:	/* select minimum reduced cost for entering variable 
		 *  (heuristic) 
		 */
	
		outerParent.activeCruncher.Messages.setText("Not Optimal!\nPick entering Variable");
	    
	/* Turn on check boxes for choosing entering variable */

		for (i = 0; i < LP.numNonbasic; i++)
		  if (LP.reducedCost[i] < 0)
			outerParent.activeCruncher.whichVariable[LP.NonBasicVariables[i]].setEnabled(true);

		outerParent.activeCruncher.whichVariable[LP.NonBasicVariables[LP.EnteringVariable]].setState(true);
	    
		for (i = 0; i < LP.numConstraints; i++) {
		  /*  Pop the checkboxes back up before turning them off  */
		  outerParent.activeCruncher.whichVariable[LP.BasicVariables[i]].setState(false);
			
		  outerParent.activeCruncher.whichVariable[LP.BasicVariables[i]].setEnabled(false);
		}
		break;
	  
      case 5:
		for (i = 0; i < LP.numNonbasic; i++) {
		  if (outerParent.activeCruncher.whichVariable[LP.NonBasicVariables[i]].getState() == true)
			LP.EnteringVariable = i;
			
		  outerParent.activeCruncher.whichVariable[LP.NonBasicVariables[i]].setState(false);
			
		  outerParent.activeCruncher.whichVariable[LP.NonBasicVariables[i]].setEnabled(false);
		}

		outerParent.activeCruncher.Messages.setText
		  ("The entering Variable is x" + 
		   Integer.toString(LP.NonBasicVariables[LP.EnteringVariable]+1));

		Acoefficients[LP.NonBasicVariables[LP.EnteringVariable]].setBackground(Color.yellow);
		break;
	  
      case 6:	/* update yB */
		for (i = 0; i < LP.numConstraints; i++)
		  yB.numbers[i].setText(" "+Float.toString(LP.yB[i]));
		outerParent.activeCruncher.Messages.setText
		  ("Calculate Search Direction (yB).\nTest for Unboundedness");
		break;
	  
      case 7:  /* test for unboundedness */
		outerParent.activeCruncher.Messages.setText
		  ("The Problem is Unbounded");
		break;
		  
      case 8: /* report unboundedness status */
		outerParent.activeCruncher.Messages.setText
		  ("The Problem is Not Unbounded.\nWork continues.");
		break;
	
      case 9: /* more than 1 variable has minimum ratio */
	/* change label on reduced cost buttons */

		ReducedCostLabel.setText("Min Ratio");

		/* put up minimum ratio */

		for (i = 0; i < LP.numConstraints; i++) {
		  index = LP.BasicVariables[i];

		  if (LP.yB[i] > 0 && LP.x[i]/LP.yB[i] == LP.MinRatio) {  
			/* has min ratio */
			outerParent.activeCruncher.whichVariable[index].setEnabled(true);
			RedCosts[index].setText(Float.toString(LP.MinRatio));
		  } else {  /* not minimum */
			outerParent.activeCruncher.whichVariable[index].setEnabled(false);
			RedCosts[index].setText("   ");
		  }
		}

		/* set first variable with min ratio to exit */

		index = LP.BasicVariables[LP.LeavingVariable];
		outerParent.activeCruncher.whichVariable[index].setState(true);

		  /* turn off check boxes and blank labels */

		for (i = 0; i < LP.numNonbasic; i++) {
		  index = LP.NonBasicVariables[i];
		  outerParent.activeCruncher.whichVariable[index].setState(false);
		  outerParent.activeCruncher.whichVariable[index].setEnabled(false);
		  RedCosts[index].setText("   ");
		}

		outerParent.activeCruncher.Messages.setText
		  ("More than one variable has minimum ratio.\nSelect leaving variable.");
		break;

      case 10: /* get user's choice */

		for (i = 0; i < LP.numConstraints; i++) {
		  if (outerParent.activeCruncher.whichVariable[LP.BasicVariables[i]].getState() == true)
			LP.LeavingVariable = i;
			
		  outerParent.activeCruncher.whichVariable[LP.BasicVariables[i]].setState(false);
			
		  outerParent.activeCruncher.whichVariable[LP.BasicVariables[i]].setEnabled(false);
		}

		outerParent.activeCruncher.Messages.setText
		  ("The leaving Variable is x" + 
		   Integer.toString(LP.BasicVariables[LP.LeavingVariable]+1));

		/* reset reduced cost label */

		ReducedCostLabel.setText("The Reduced Costs");

		for (i = 0; i < LP.numVariables; i++)
		  RedCosts[i].setText("   ");
		  
		break;

      case 11:

		outerParent.activeCruncher.Messages.setText
		  ("The Min Ratio Test Indicates x"+
		   Integer.toString(LP.BasicVariables[LP.LeavingVariable] + 1)+
		   "\nshould leave the basis.");

		Acoefficients[LP.BasicVariables[LP.LeavingVariable]].setBackground(Color.white);
		break;
	  
      case 12: /* report optimal status */
		outerParent.activeCruncher.Messages.setText
		  ("Solution found.\nFound X and objective value.");
		break;
	  
      case 0: /* update basis and objective value */
		for (i = 0; i < LP.numConstraints; i++) {
		  Acoefficients[LP.BasicVariables[i]].setBackground(Color.red);
		}
			
		for (i = 0; i < LP.numNonbasic; i++) {
		  Acoefficients[LP.NonBasicVariables[i]].setBackground
			(Color.lightGray);
		}
			
		Value.setText(Float.toString(LP.calculateObjective()));
			
		for (i = 0; i < LP.numConstraints; i++)
		  x.numbers[i].setText((String)"x"+(LP.BasicVariables[i]+1)
					   +"= "+Float.toString(LP.x[i]));
			
		for (i = 0; i < LP.numConstraints; i++)
		  littleCost.numbers[i].setText
			(" "+Float.toString (LP.cost[LP.BasicVariables[i]]));
			
		outerParent.activeCruncher.Messages.setText
		  ("Update Basis, X,\nand Objective Value.");
			
		break;
      }
    } /* end updatePanelsForOneStep procedure */
  
  public void updateAllPanels()
    {
      int i;

      for (i = 0; i < LP.numNonbasic; i++)
	RedCosts[LP.NonBasicVariables[i]].setText
	  (Float.toString(LP.reducedCost[i]));
      
      for (i = 0; i < LP.numConstraints; i++)
	RedCosts[LP.BasicVariables[i]].setText("Basic");
      
      if (outerParent.SolveStatus != 1) {
	RedCosts[LP.BasicVariables[LP.LeavingVariable]].setText
	  (Float.toString(LP.reducedCost[LP.EnteringVariable]));
	  
	RedCosts[LP.NonBasicVariables[LP.EnteringVariable]].setText("        ");
      }
      
      for (i = 0; i < LP.numConstraints; i++)
	x.numbers[i].setText((String)"x"+(LP.BasicVariables[i]+1)+"= "+
			     Float.toString(LP.x[i]));
      
      for (i = 0; i < LP.numConstraints; i++)
	pi.numbers[i].setText(" "+Float.toString(LP.pi[i]));
      
      for (i = 0; i < LP.numConstraints; i++)
	littleCost.numbers[i].setText
	  (" "+Float.toString(LP.cost[LP.BasicVariables[i]]));
      
      for (i = 0; i < LP.numConstraints; i++)
	yB.numbers[i].setText(" "+Float.toString(LP.yB[i]));
      
      for (i = 0; i < LP.numConstraints; i++)
	for (int j = 0; j < LP.numConstraints; j++)
	  Bcoefficients[j][i].setText
	    (Float.toString(LP.A[j][LP.BasicVariables[i]]));
      
      Value.setText
	(" "+Float.toString(LP.calculateObjective()));

      for (i = 0; i < LP.numConstraints; i++) {
	Acoefficients[LP.BasicVariables[i]].setBackground(Color.red);
      }
      
      for (i = 0; i < LP.numNonbasic; i++) {
	Acoefficients[LP.NonBasicVariables[i]].setBackground
	  (Color.lightGray);
      }
    } /* end updateAllPanels procedure */
} /* end numberCrunchingFrame */

