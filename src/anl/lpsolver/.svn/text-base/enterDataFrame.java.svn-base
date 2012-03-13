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

public class enterDataFrame extends JFrame implements ActionListener
{
  SimplexTool outerParent;
  revisedSimplex LP;

  JComboBox minmax;
  JPanel  dataPanel;
  JPanel  buttonbar;
  JPanel  messagePanel;

  JPanel objValue = new JPanel();

  JTextField Messages = new JTextField(20);

  int numVariables;
  int numConstraints;

  JButton preprocess = new JButton("Preprocess");
  JButton reset      = new JButton("Reset");
  JButton clear      = new JButton("Clear");

  JLabel nonNegativeLabel;

  numberTextField constraint[][];
  numberTextField objective[];
  numberTextField rhs[];

  Choice rowType[];

  /* layout manager for all containers */
  GridBagLayout gridbag = new GridBagLayout();

  public enterDataFrame(SimplexTool target, String title, int numVariables,
			int numConstraints)
    {
      super(title);
      this.outerParent = target;

	  preprocess.addActionListener(this);
	  reset.addActionListener(this);
	  clear.addActionListener(this);

      int row, col;
      int width;

      this.numVariables   = numVariables;
      this.numConstraints = numConstraints;

      width = 2 * numVariables + 2;

      /* build dataPanel */

      dataPanel = new JPanel();
	  dataPanel.setBackground(Color.white);
      dataPanel.setLayout(gridbag);

      minmax = new JComboBox();
	  minmax.setBackground(Color.white);
      minmax.addItem("Minimize");
      minmax.addItem("Maximize");

      objective = new numberTextField[numVariables]; /* array */

      for (col  = 0; col < numVariables; col++)
	objective[col] = new numberTextField();

      rowType   = new Choice[numConstraints];

      /* add choice of minimization or maximization */

      outerParent.constrain(dataPanel, minmax, 0, 0, width, 1,
			    10, 0, 10, 0);

      /* add fields for variables plus labels */

      for (col = 0; col < numVariables - 1; col++) {
	outerParent.constrain
	  (dataPanel, objective[col],
	   2*col, 1, 1, 1,
	   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	   1.0, 0.0, 0, 0, 0, 0);
	outerParent.constrain
	  (dataPanel, new JLabel((String)"x"+(col+1)+"+",JLabel.LEFT),
	   2*col+1, 1, 1, 1,
	   GridBagConstraints.NONE, GridBagConstraints.WEST,
	   0.0, 0.0, 0, 0, 0, 0);
      }

      col = numVariables - 1;  /* add last element */
      outerParent.constrain
	(dataPanel, objective[col],
	 2*col, 1, 1, 1,
	 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	 1.0, 0.0, 0, 0, 0, 0);

      outerParent.constrain
	(dataPanel, new JLabel((String)"x"+(col+1),JLabel.LEFT),
	 2*col+1, 1, 1, 1,
	 GridBagConstraints.NONE, GridBagConstraints.WEST,
	 0.0, 0.0, 0, 0, 0, 0);

      /* add "subject to:" line */

      outerParent.constrain
	(dataPanel, new JLabel((String)"Subject to:",JLabel.LEFT),
	 0, 2, width, 1,
	 GridBagConstraints.NONE, GridBagConstraints.WEST,
	 0.0, 0.0, 10, 0, 10, 0);

      /* build constraints */

      constraint = new numberTextField[numConstraints][numVariables];
      rhs        = new numberTextField[numConstraints];

      for (row = 0; row < numConstraints; row++) {

	rhs[row] = new numberTextField();

	for (col = 0; col < numVariables - 1; col++) {

	  constraint[row][col] = new numberTextField();

	  outerParent.constrain
	    (dataPanel, constraint[row][col],
	     2*col, row+3, 1, 1,
	     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	     1.0, 0.0, 0, 0, 0, 0);
	  outerParent.constrain
	    (dataPanel, new JLabel((String)"x"+(col+1)+"+",JLabel.LEFT),
	     2*col+1, row+3, 1, 1,
	     GridBagConstraints.NONE, GridBagConstraints.WEST,
	     0.0, 0.0, 0, 0, 0, 0);
	}

	col = numVariables   - 1;  /* add last element */
	constraint[row][col] = new numberTextField();

	outerParent.constrain
	  (dataPanel, constraint[row][col],
	   2*col, row+3, 1, 1,
	   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	   1.0, 0.0, 0, 0, 0, 0);

	outerParent.constrain
	  (dataPanel, new JLabel((String)"x"+(col+1),JLabel.LEFT),
	   2*col+1, row+3, 1, 1,
	   GridBagConstraints.NONE, GridBagConstraints.WEST,
	   0.0, 0.0, 0, 0, 0, 0);

	rowType[row] = new Choice();
	rowType[row].addItem("<=");
	rowType[row].addItem(">=");
	rowType[row].addItem("=");

	outerParent.constrain(dataPanel, rowType[row],
			      2*numVariables, row+3, 1, 1);

	outerParent.constrain
	  (dataPanel, rhs[row],
	   2*numVariables+1, row+3, 1, 1,
	   GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	   1.0, 0.0, 0, 0, 0, 0);

      }  /* end for row */

      nonNegativeLabel = new JLabel("x >= 0");
      nonNegativeLabel.setBackground(Color.white);

      /* build button bar */

      buttonbar = new JPanel();
	  buttonbar.setBackground(Color.white);
      buttonbar.setLayout(gridbag);

      outerParent.constrain
	(buttonbar, preprocess, 0, 0, 1, 1,
	 GridBagConstraints.NONE, GridBagConstraints.CENTER,
	 0.3, 0.0, 0, 0, 0, 0);

      outerParent.constrain
	(buttonbar, reset, 1, 0, 1, 1,
	 GridBagConstraints.NONE, GridBagConstraints.CENTER,
	 0.3, 0.0, 0, 0, 0, 0);

      outerParent.constrain
	(buttonbar, clear, 2, 0, 1, 1,
	 GridBagConstraints.NONE, GridBagConstraints.CENTER,
	 0.3, 0.0, 0, 0, 0, 0);

      /* message panel */

	  Messages.setBackground(Color.white);

      messagePanel = new JPanel();
	  messagePanel.setBackground(Color.white);
      messagePanel.setLayout(gridbag);

      outerParent.constrain
	  (messagePanel, new JLabel("         "), 0, 0, 1, 1,
	   GridBagConstraints.NONE, GridBagConstraints.WEST,
	  1.0, 0.0, 0, 0, 0, 0);
     outerParent.constrain
	(messagePanel, Messages, 1, 0, 1, 1,
	 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	 1.0, 0.0, 0, 0, 0, 0);

      Messages.setEditable(false);

      /* Put it all together */
      Container c = getContentPane();
	  c.setBackground(Color.white);

      c.setLayout(gridbag);

      outerParent.constrain
	(c, dataPanel, 0, 0, 2, 1,
	 GridBagConstraints.BOTH, GridBagConstraints.NORTH,
	 1.0, 1.0, 5, 5, 5, 5);
      outerParent.constrain
	(c, nonNegativeLabel, 0, 1, 1, 1,
	 GridBagConstraints.NONE, GridBagConstraints.WEST,
	 0.0, 0.0, 0, 0, 0, 0);
      outerParent.constrain
	 (c, messagePanel, 1, 1, 2, 1,
	 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
	 1.0, 0.0, 0, 0, 0, 0);
      outerParent.constrain
	(c, buttonbar, 0, 2, 2, 1,
	 GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
	 1.0, 0.0, 10, 0, 10, 0);

	  reset.setEnabled(false);
      LP = target.LP;

	  //handle window closing event
	  this.addWindowListener(new WindowAdapter() { 
		  public void windowClosing(WindowEvent e) {
			outerParent.start.setEnabled(true);
			dispose();
			if (outerParent.activeCruncher != null) 
				outerParent.activeCruncher.dispose();
			System.exit(0);
		  }
		 }
	  );


    } /* end enterDataFrame procedure */

  void reset()
    {
      this.setEditable(true);

      LP.reset(numVariables, numConstraints);
    }

  public void setEditable(boolean status)
    {
      int row, col;

      if (status)
	minmax.setEnabled(true);
      else
	minmax.setEnabled(false);

      for (col = 0; col < numVariables; col++)
	objective[col].setEditable(status);

      for (row = 0; row < numConstraints; row++)
	for (col = 0; col < numVariables; col++)
	  constraint[row][col].setEditable(status);
	
	for (row = 0; row < numConstraints; row++)
	  rhs[row].setEditable(status);

    } /* end setEditable procedure */

  public void clear()
    {
      int row, col;

      for (col = 0; col < numVariables; col++)
	objective[col].setText("");

      for (row = 0; row < numConstraints; row++)
	for (col = 0; col < numVariables; col++)
	  constraint[row][col].setText("");

      for (row = 0; row < numConstraints; row++)
	rhs[row].setText("");

      for (row = 0; row < numConstraints; row++)
	rowType[row].select(0);

      minmax.setSelectedIndex(0);

    } /* end clear procedure */

  public void actionPerformed(ActionEvent e)
    {

	if (e.getSource() == preprocess) {

	  if(outerParent.ReadDataAndPreprocess
	     (outerParent.numVariables, outerParent.numConstraints)) {
	    preprocess.setEnabled(false);
	    reset.setEnabled(true);
	    clear.setEnabled(false);
		outerParent.activeCruncher.step.requestFocus();
	  }

	  /*  ReadDataAndPreprocess created the number crunching window.
	   *  Control now goes to that window.
	   */
	}

	if (e.getSource() ==  reset) {

	  outerParent.LP.reset(outerParent.numVariables,
			       outerParent.numConstraints);

	  if (outerParent.activeCruncher != null)
	    outerParent.activeCruncher.dispose();

	  if (outerParent.activeCruncher.iterate.isEnabled())
	    outerParent.activeCruncher.iterate.setEnabled(false);

	  if (outerParent.activeCruncher.step.isEnabled())
	    outerParent.activeCruncher.step.setEnabled(false);

	  outerParent.dataFrame.preprocess.setEnabled(true);
	  outerParent.dataFrame.clear.setEnabled(true);
	  outerParent.dataFrame.reset.setEnabled(false);

	  outerParent.dataFrame.reset();
	  outerParent.dataFrame.Messages.setText("Press Preprocess to Begin.");
	}

	if (e.getSource() ==  clear) {
	  clear();
	}

    } /* end action procedure */

} /* end enterDataFrame class */

