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

public class numberTextField extends JTextField
{
  private Float tmp;
    
  public numberTextField()
    {
      super(3);
    }
  
  /* this function tests input to see if it is a number.
     It allows for no input to be a Zero */
  
  public boolean isNumber()
    {
      try {
	tmp = new Float(Float.valueOf(this.getText()).floatValue());
      }
      catch (NumberFormatException e) {
	if (this.getText().length() > 0)
	  return false;
	else
	  tmp = new Float(0);
      }
      return true;
    } /* end isNumber procedure */
  
  public float getFloat()
    {
      if(tmp != null) {
	this.isNumber();
	return tmp.floatValue();
      }
      else 
	return tmp.floatValue();
    } /* end getFloat */
} /* end numberTextField class */

