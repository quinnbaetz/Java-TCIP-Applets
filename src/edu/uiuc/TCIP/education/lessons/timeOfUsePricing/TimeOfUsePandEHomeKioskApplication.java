package edu.uiuc.TCIP.education.lessons.timeOfUsePricing;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JFrame;

import sun.nio.ch.SocketOpts.IP.TCP;

public class TimeOfUsePandEHomeKioskApplication {

	public static void main(String[] args) {
		JFrame myframe = new JFrame();
		
		myframe.setTitle("Power and Energy in the Home");
		myframe.add(new TimeOfUsePandEHomePanelForKiosk());
		myframe.pack();
		myframe.setVisible(true);
		myframe.setSize(1010,600);
		myframe.setResizable(false);
		myframe.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
