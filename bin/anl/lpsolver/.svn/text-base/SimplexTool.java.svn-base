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

public class SimplexTool extends JApplet implements ActionListener
{
  int numConstraints; 
  int numVariables;
  
  revisedSimplex LP;
  problemDimensionWindow DimensionWindow;
  enterDataFrame dataFrame; 
  
  numberCrunchingFrame activeCruncher, oldCruncher;
  
  About aboutWindow;
  
  float coefficients[] = new float[33];

  int SolveStatus;

  JButton start = new JButton("New Problem");
  JButton end   = new JButton("Quit");
  JButton about = new JButton("About");

  JPanel buttonPane = new JPanel(new FlowLayout()); 

  public void init()
    {
      super.init();
      buttonPane.setBackground(Color.white);
              
      start.setBackground(Color.lightGray);
      end.setBackground(Color.lightGray);
      about.setBackground(Color.lightGray);

	  start.addActionListener(this);
	  end.addActionListener(this);
	  about.addActionListener(this);
      
      buttonPane.add(start, BorderLayout.WEST);
      buttonPane.add(end, BorderLayout.CENTER);
      buttonPane.add(about, BorderLayout.EAST);

	  this.getContentPane().add(buttonPane);

      aboutWindow = new About(this);
    } /* end init procedure */
  
  public void actionPerformed(ActionEvent e)
    {

	if (e.getSource() == start) {
	  if (dataFrame != null)      dataFrame.dispose();
	  if (activeCruncher != null) activeCruncher.dispose();
	  if (LP != null) LP.reset(numVariables, numConstraints);
	  
	  DimensionWindow = new problemDimensionWindow(this,"Enter the Sizes");
	  DimensionWindow.pack();
	  start.setEnabled(false);
	  DimensionWindow.show();
	}
	
	if (e.getSource() == end) {
	  if (DimensionWindow != null) DimensionWindow.dispose();
	  if (dataFrame != null)       dataFrame.dispose();
	  if (activeCruncher != null)  activeCruncher.dispose();
	  
	  if (aboutWindow != null) aboutWindow.dispose();
	  if (!start.isEnabled())  start.setEnabled(true);
	  if (!about.isEnabled())  about.setEnabled(true);
	}

	if (e.getSource() == about) {
	  about.setEnabled(false);
	  if (!aboutWindow.isShowing()) 
		  aboutWindow.show();
	} 

    } /* end action procedure */
    
  public void update(problemDimensionWindow InputWindow)
    {
      try {
	numVariables = 
	  Integer.valueOf(InputWindow.numVariables.getText()).intValue();
	numConstraints = 
	  Integer.valueOf(InputWindow.numConstraints.getText()).intValue();
	
	if (numVariables >= 2 && numConstraints >= 2 && 
	    numVariables <= 7 && numConstraints <= 7) {
	  InputWindow.dispose();
	  LP        = new revisedSimplex(numVariables, numConstraints);
	  dataFrame = new enterDataFrame(this, "Enter Your Linear Program!",
					 numVariables, numConstraints);
	  dataFrame.Messages.setText("Press Preprocess to Begin.");
	  dataFrame.pack();
	  dataFrame.show();
	  start.setEnabled(true);
	}
	else {
	  if (numVariables > 7 || numVariables < 2)
	    InputWindow.reminderVar.setBackground(Color.red);
	  else 
	    InputWindow.reminderVar.setBackground(Color.lightGray);

	  if (numConstraints > 7 || numConstraints < 2)
	    InputWindow.reminderCon.setBackground(Color.red);
	  else
	    InputWindow.reminderCon.setBackground(Color.lightGray);
	}
      }
      catch (NumberFormatException e) {
      }
    } /* end update procedure */

  public void update(numberCrunchingFrame LPWindow, boolean OneStepOnly)
    {
      if (OneStepOnly) {
		SolveStatus = LP.iterateOneStep();
		activeCruncher.updatePanelsForOneStep();
	
	if (LP.CurrentStep == 0)
	  LPWindow.iterate.enable();
      }
      else {  /* perform one complete iteration */
		activeCruncher.Messages.setText("Keep Iterating");
		SolveStatus = LP.iterate();
		activeCruncher.updateAllPanels();
      }

      /* Check return status and take action if necessary */
      
      if (SolveStatus == LP.Unbounded) {
		activeCruncher.Messages.setText("The problem is unbounded.");
		LPWindow.iterate.setEnabled(false);
		LPWindow.step.setEnabled(false);
		start.setEnabled(true);

      } else if (SolveStatus == LP.Optimal) {
		if (LP.ArtificialAdded == false) {
			activeCruncher.Messages.setText("You've Done It!\nYou've Solved It!!!");
		  dataFrame.Messages.setText("");
		  LPWindow.updateAllPanels();
		  LPWindow.iterate.setEnabled(false);
		  LPWindow.step.setEnabled(false);
		  start.setEnabled(true);
	} else {  /* artificial variables were added (in phase I) */
	  /* if it passes this if test, then the problem is feasible */

	  /*** ADD CODE HERE TO TEST WHEN ARTIFICIALS ARE STILL IN BASIS ***/

	  if (LP.calculateObjective() == 0) {

	    if (LP.NumArtificials > 0) {
	      System.out.print  ("Even though 0 objective, there are");
	      System.out.println(" still artificials in basis.");
	    }
	    
	    System.out.println("Calling getRidOfArtificials");
	    LP.getRidOfArtificials();
	    System.out.println("Called  getRidOfArtificials");

	    activeCruncher.Messages.setText
	      ("Artificial Variables Eliminated\nGetting New Window");

	    oldCruncher    = activeCruncher;
	    activeCruncher = new numberCrunchingFrame(this, "Phase 2",LP);
	    oldCruncher.dispose();
	    
	    activeCruncher.Messages.setText
	      ("Continuing With Original Problem\n(Phase 2)");
	    dataFrame.Messages.setText("In phase 2");
	  } else {
	    activeCruncher.Messages.setText("The Problem is infeasible.");
	    LPWindow.iterate.setEnabled(false);
	    LPWindow.step.setEnabled(false);
	    start.setEnabled(true);
	  }
	}

      } 
    }   /* end update procedure */

  public boolean ReadDataAndPreprocess(int numVariables, int numConstraints) 
    {
      /* Specify the objective function */
      
      float rhs;

      LP.reset(numVariables, numConstraints);  /* reset LP class for reading */
      
      for (int i = 0; i < numVariables; i++) {
	if (dataFrame.objective[i].isNumber())
	  coefficients[i] = dataFrame.objective[i].getFloat();
	else {
	  /* error in reading data, notify user */
	  dataFrame.Messages.setText("Error: Objective x"+(i+1));
	  return false;
	}
      }
      
      if (dataFrame.minmax.getSelectedIndex() == 0)
	LP.specifyObjective(coefficients, true);  /* minimize */
      else
	LP.specifyObjective(coefficients, false); /* maximize */

	/* Let the SimplexTool Class know about the constraints */

      for (int i = 0; i < numConstraints; i++) {
	/*LP.showInfo();*/
	for(int j = 0; j < numVariables; j++) {
	  if (dataFrame.constraint[i][j].isNumber())
	    coefficients[j] = dataFrame.constraint[i][j].getFloat();
	  else {
	    dataFrame.Messages.setText
	      ("Error: Constraint "+(i+1)+" in x"+(j+1));
	    return false;
	  }
	} /* end for j */
	      
	if (dataFrame.rhs[i].isNumber())
	  rhs  = dataFrame.rhs[i].getFloat();
	else {
	  dataFrame.Messages.setText("Error: Constraint "+(i+1)+ "RHS");
	  return false;
	}
	
	LP.addConstraint(coefficients, rhs, 
			 dataFrame.rowType[i].getSelectedIndex());
      } /* end for i loop */

      /* Now that we have the data, perform preprocessing */
	  
      LP.preprocess(numVariables, numConstraints);
	  
      dataFrame.setEditable(false);
      dataFrame.Messages.setText("Starting Simplex Window");
      
      /*if (LP.artificialPresent) {*/
      if (LP.NumArtificials > 0) {
	activeCruncher = new numberCrunchingFrame(this, "Phase 1",LP);
	dataFrame.Messages.setText("Starting phase 1");
      }
      else {
	activeCruncher = new numberCrunchingFrame(this, "Phase 2",LP);
	dataFrame.Messages.setText("Starting phase 2");
      }
      
      return true;  /* successful completion */
    } /* End ReadDataAndPreprocess */

  public void constrain(Container container, Component component, 
			int grid_x, int grid_y, int grid_width, 
			int grid_height,
			int fill, int anchor, double weight_x, double weight_y,
			int top, int left, int bottom, int right)
    {
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = grid_x; c.gridy = grid_y;
      c.gridwidth = grid_width; c.gridheight = grid_height;
      c.fill = fill; c.anchor = anchor;
      c.weightx = weight_x; c.weighty = weight_y;
      if (top+bottom+left+right > 0)
	c.insets = new Insets(top, left, bottom, right);
      
      ((GridBagLayout)container.getLayout()).setConstraints(component, c);
      container.add(component);
    } /* end constrain procedure */
  
  public void constrain(Container container, Component component, 
			int grid_x, int grid_y, int grid_width, 
			int grid_height) {
    constrain(container, component, grid_x, grid_y, 
              grid_width, grid_height, GridBagConstraints.NONE, 
              GridBagConstraints.NORTHWEST, 0.0, 0.0, 0, 0, 0, 0);
  } /* end constrain procedure */

 public void constrain(Container container, Component component, 
                  int grid_x, int grid_y, int grid_width, int grid_height,
                  int top, int left, int bottom, int right) 
   {
     constrain(container, component, grid_x, grid_y, 
	       grid_width, grid_height, GridBagConstraints.NONE, 
	       GridBagConstraints.NORTHWEST, 
	       0.0, 0.0, top, left, bottom, right);
   } /* end constrain procedure */
}  /* end SimplexTool class */
