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

public class problemDimensionWindow extends JFrame implements ActionListener
{
  JTextField numConstraints;
  JTextField numVariables;
  JButton ok;
  
  JPanel top;
  JPanel buttonPanel;

  JLabel reminderCon;
  JLabel reminderVar;
  
  SimplexTool outerParent;

  GridBagLayout gridbag;
  
  public problemDimensionWindow(SimplexTool target, String title)
    {
      super(title);
      this.outerParent = target;

	  Container c = getContentPane();
	  c.setBackground(Color.white);

      /* layout for each container */
      gridbag = new GridBagLayout();

      ok = new JButton("Continue");
	  ok.addActionListener(this);

      buttonPanel = new JPanel();
	  buttonPanel.setBackground(Color.white);
      buttonPanel.setLayout(gridbag);

      outerParent.constrain(buttonPanel, ok, 0, 0, 1, 1, 
		GridBagConstraints.NONE, GridBagConstraints.CENTER,
		0.3, 0.0, 5, 0, 5, 0);
	      
      numConstraints = new JTextField(3);
      numVariables   = new JTextField(3);
      
      numConstraints.getPreferredSize();
      numVariables.getPreferredSize();
      
      reminderCon = new JLabel("Between 2 and 7:");
	  reminderCon.setOpaque(true);
	  reminderCon.setBackground(Color.white);
      reminderVar = new JLabel("Between 2 and 7:");
	  reminderVar.setOpaque(true);
	  reminderVar.setBackground(Color.white);
      
      top = new JPanel();
	  top.setBackground(Color.white);

      top.setLayout(gridbag);

      outerParent.constrain(top,new JLabel("Number of Constraints:"),0,0,1,1,0,0,5,0);
      outerParent.constrain(top,reminderCon,0,1,1,1);
      outerParent.constrain(top,numConstraints,1,1,1,1);
      
      outerParent.constrain(top,new JLabel("Number of Variables:"),0,2,1,1,0,0,5,0);
      outerParent.constrain(top,reminderVar,0,3,1,1);
      outerParent.constrain(top,numVariables,1,3,1,1);

      /* Put it all together */ 

      c.setLayout(new GridBagLayout());

      outerParent.constrain(c,top,0,0,1,1,
			    GridBagConstraints.BOTH, GridBagConstraints.NORTH,
			    0.0, 0.0, 10, 5, 10, 5);
      outerParent.constrain(c,buttonPanel,0,1,1,1,
			    GridBagConstraints.NONE, GridBagConstraints.CENTER,
			    0.0, 0.0, 10, 5, 10, 5);

	  //handle window closing event
	  this.addWindowListener(new WindowAdapter() { 
		  public void windowClosing(WindowEvent e) {
			outerParent.start.setEnabled(true);
			dispose();
			System.exit(0);
		  }
		 }
	  );

    } /* end problemDimensionWindow procedure */
  
  public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == ok)
		/* when continue button is pushed */
		outerParent.update(this);
    } /* end action procedure */
} /* end problemDimensionWindow class */

