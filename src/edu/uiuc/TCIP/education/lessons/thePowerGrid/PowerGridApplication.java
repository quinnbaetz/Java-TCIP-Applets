package edu.uiuc.TCIP.education.lessons.thePowerGrid;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import javax.swing.JFrame;

public class PowerGridApplication {

	public static void main(String[] args) {
		JFrame myframe = new JFrame();
		myframe.setTitle("The Power Grid");
		myframe.setSize(800,625);
		myframe.add(new PowerGridPanel());
		myframe.setVisible(true);
		myframe.setResizable(false);
		myframe.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
