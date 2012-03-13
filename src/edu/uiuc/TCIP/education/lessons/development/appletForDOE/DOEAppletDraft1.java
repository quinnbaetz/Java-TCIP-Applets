package edu.uiuc.TCIP.education.lessons.development.appletForDOE;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class DOEAppletDraft1 extends JApplet {

	public void init() {
		super.init();
		setBackground(Color.WHITE);
		getContentPane().setSize(600,800);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new DOEPanelDraft1(),BorderLayout.CENTER);
	}
	
}
