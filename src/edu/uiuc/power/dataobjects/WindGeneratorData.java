package edu.uiuc.power.dataobjects;

import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;

import edu.uiuc.power.displayobjects.Animatable;

public class WindGeneratorData extends GeneratorData implements Animatable {
	
	private class WindTurbine {
		double currentMW;
		Random randomGen;
		double variation;
		double minMW, maxMW;
		private boolean animating;
		
		public WindTurbine(double initialMW, double minMW, double maxMW, double variation) {
			this.variation = variation;
			this.randomGen = new Random();
			this.currentMW = initialMW;
			this.minMW = minMW;
			this.maxMW = maxMW;
		}
		
		public void setAnimating(boolean animating) {
			this.animating = animating;
		}
		
		public double getMW() {
			return currentMW;
		}
		
		public void setMW(double MW) {
			currentMW = MW;
		}
		
		public boolean animationstep() {
			if (animating) {
				currentMW += randomGen.nextGaussian()*variation;
				if (currentMW <= minMW)
					currentMW = minMW;
				if (currentMW >= maxMW)
					currentMW = maxMW;
				setMW(currentMW);
				//System.out.println("currentMW = " + currentMW);
				return true;
			} else 
				return false;
		}
		
		public void setVariation(double variation) {
			this.variation = variation;
		}
		
		public double getVariation() {
			return variation;
		}
	}
	
	WindTurbine turbine;
	
	public void setVariation(double variation) {
		turbine.setVariation(variation);
	}
	
	public void setAnimating(boolean animating) {
		turbine.setAnimating(animating);
	}
	
	@Override
	public void setMW(double MW) {
		super.setMW(MW);
		turbine.setMW(MW);
	}

	public double getVariation() {
		return turbine.getVariation();
	}
	
	public double getTurbineMW() {
		return turbine.getMW();
	}
	
	public void setTurbineMW(double MW) {
		turbine.setMW(MW);
	}
	
	public WindGeneratorData(NodeData connectNode, double initialMW, double minMW, double maxMW, double variation,
			boolean connected) {
		super(connectNode, initialMW, minMW, maxMW, 0, 0, 0, connected);
		turbine = new WindTurbine(initialMW, minMW, maxMW, variation);
	}

	public boolean animationstep() {
		// TODO Auto-generated method stub
		turbine.animationstep();
		if (getConnected()) {
			setMW(turbine.getMW());
			return true;
		} else
			return false;
	}

	@Override
	public double getMW() {
		return super.getMW();
	}
}
