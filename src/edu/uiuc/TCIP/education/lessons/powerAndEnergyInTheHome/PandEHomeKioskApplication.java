package edu.uiuc.TCIP.education.lessons.powerAndEnergyInTheHome;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JFrame;

import edu.uiuc.TCIP.education.lessons.timeOfUsePricing.TimeOfUsePandEHomePanelForKiosk;

import sun.nio.ch.SocketOpts.IP.TCP;

public class PandEHomeKioskApplication {

	public static void main(String[] args) {
		JFrame myframe = new JFrame();
		myframe.setTitle("Power and Energy in the Home");
		myframe.add(new PandEHomePanelForKiosk());
		myframe.pack();
		myframe.setVisible(true);
		myframe.setSize(1005,600);
		myframe.setResizable(true);
		myframe.setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}

}
