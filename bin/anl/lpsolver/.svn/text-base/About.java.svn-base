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

public class About extends JFrame implements ActionListener
{
  SimplexTool outerParent;
  
  JLabel author1;
  JLabel author2;
  JLabel email1;
  JLabel email2;
  JLabel otc;
  JLabel Argonne;
  JLabel NU;
  JButton ok;
  
  public About(SimplexTool target)
    {
      super("About This Applet");
      outerParent = target;
	  Container c = getContentPane();
      c.setLayout(new GridLayout(8,1));
      
      ok = new JButton("Ok");
	  ok.addActionListener(this);

      author1 = new JLabel("Updated by Yu WEI", JLabel.CENTER);
	  email1 = new JLabel("weiy3@mcmaster.ca", JLabel.CENTER);
	  author2 = new JLabel("Written by Timothy J. Wisniewski", JLabel.CENTER);
      email2 = new JLabel("tjw@euler.bd.psu.edu", JLabel.CENTER);
      otc = new JLabel("Optimization Technology Center", JLabel.CENTER);
      Argonne = new JLabel("Argonne National Laboratory", JLabel.CENTER);
      NU = new JLabel("and Northwestern University", JLabel.CENTER);

      
      c.add(otc);
      c.add(Argonne);
      c.add(NU);
      c.add(author1);
	  c.add(email1);
	  c.add(author2);
	  c.add(email2);
      c.add(ok);
      this.pack();
	  
	  //handle window closing event
	  this.addWindowListener(new WindowAdapter() { 
		  public void windowClosing(WindowEvent e) {
			outerParent.about.setEnabled(true);
			dispose();
			System.exit(0);
		  }
		 }
	  );
    } /* end about procedure */
  
  public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == ok) {
		this.hide();
		outerParent.about.setEnabled(true);
	}
  } /* end action procedure */
} /* end About class */
