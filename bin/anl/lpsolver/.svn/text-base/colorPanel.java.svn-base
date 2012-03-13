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
import javax.swing.*;


public class colorPanel extends JPanel
{
  /*  The width of the text labels is 8 in most cases  */

  JLabel numbers [];
  int rows, cols;
  
  public colorPanel(int rows, Color color)
    {
      super();
      this.rows = rows;
      this.setLayout(new GridLayout(rows,1));
      
      this.setBackground(color);
      
      numbers = new JLabel[rows];
      
      for (int i = 0; i < rows; i++) {
		  numbers[i] = new JLabel("      ");
		  numbers[i].setPreferredSize(new Dimension(60, 20));
		  add(numbers[i]);
      }
    } /* end colorPanel procedure */
} /* end colorPanel class */

