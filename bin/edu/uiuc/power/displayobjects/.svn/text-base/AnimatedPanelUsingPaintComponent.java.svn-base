package edu.uiuc.power.displayobjects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JPanel;

public class AnimatedPanelUsingPaintComponent extends AnimatedPanel {

	/*
	private ArrayList<Renderable> _middlelayer;

	private ArrayList<Renderable> _toplayer;
	
	private ArrayList<Animatable> _animatables;
	*/
	
	double scaleFactor;
	
	public double getScaleFactor() {
		return scaleFactor;
	}
	
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	
	
	public AnimatedPanelUsingPaintComponent(int desiredWidth, int desiredHeight, long period, int noDelaysPerYield, boolean antiAliasOn) {
		super(0, 0, period, noDelaysPerYield, antiAliasOn);
		scaleFactor = 1.0;
		/*
		_middlelayer = new ArrayList<Renderable>();
		_toplayer = new ArrayList<Renderable>();
		_animatables = new ArrayList<Animatable>();
		*/
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g);
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);	                        	
		
		//g2d.setTransform(new AffineTransform());
		g2d.scale(getScaleFactor(), getScaleFactor());
		ArrayList<Renderable> _bottomlayer = getBottomLayerRenderables();
		for (int i = 0; i < _bottomlayer.size(); i++) {
			_bottomlayer.get(i).render(g2d);
		}
		ArrayList<Renderable> _middlelayer = getMiddleLayerRenderables();
		for (int i = 0; i < _middlelayer.size(); i++) {
			_middlelayer.get(i).render(g2d);
		}
		ArrayList<Renderable> _toplayer = getTopLayerRenderables();
		for (int i = 0; i < _toplayer.size(); i++) {
			_toplayer.get(i).render(g2d);
		}
	}
	/*
	public ArrayList<Renderable> getMiddleLayerRenderables() {
		return _middlelayer;
	}

	public ArrayList<Renderable> getTopLayerRenderables() {
		return _toplayer;
	}
	
	public ArrayList<Animatable> getAnimatables() {
		return _animatables;
	}
	*/

	@Override
	public void startGame() {
		// do nothing
	}

//	@Override
//	public void addNotify() {
//		// do nothing
//	}
}
