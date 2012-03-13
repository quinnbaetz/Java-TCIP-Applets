package edu.uiuc.TCIP.education.lessons.powerAndEnergyInTheHome;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JApplet;

public class PandEHomeKioskApplet extends JApplet {

	public void init() {
		super.init();
		setBackground(Color.WHITE);
		//getContentPane().setSize(575,450);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new PandEHomePanelForKiosk(),BorderLayout.CENTER);
		getContentPane().doLayout();
	}
	
}
