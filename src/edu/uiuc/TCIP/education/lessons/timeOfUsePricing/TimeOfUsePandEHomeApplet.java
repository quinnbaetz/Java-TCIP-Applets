package edu.uiuc.TCIP.education.lessons.timeOfUsePricing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JApplet;

public class TimeOfUsePandEHomeApplet extends JApplet {

	public void init() {
		super.init();
		setBackground(Color.WHITE);
		//getContentPane().setSize(575,450);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new TimeOfUsePandEHomePanel(),BorderLayout.CENTER);
	}
	
}
