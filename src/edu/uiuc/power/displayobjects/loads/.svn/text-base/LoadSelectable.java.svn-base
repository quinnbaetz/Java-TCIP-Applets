package edu.uiuc.power.displayobjects.loads;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import edu.uiuc.power.dataobjects.DemandProvider;
import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;

public abstract class LoadSelectable extends LoadDisplay implements ActionListener, DemandProvider {

	LoadDisplay currentLoadDisplay;
	protected ArrayList<LoadDisplay> loadTypes;
	protected ArrayList<Double> loadMWs;

	public LoadSelectable(LoadData loaddata, Point2D location,
			LoadDisplayAlignmentVertical vertAlign,
			LoadDisplayAlignmentHorizontal horizAlign, String loadDescription) {
		super(loaddata, location, vertAlign, horizAlign, loadDescription);
	}

	@Override
	public String getDescription() {
		if (currentLoadDisplay != null)
			return currentLoadDisplay.getDescription();
		else
			return "";
	}

	@Override
	public void setDescription(String loadDescription) {
	}
	
	public JComboBox createComboBox() {
		JComboBox retBox = new JComboBox(loadTypes.toArray());
		return retBox;
	}

	public JComboBox getComboBox() {
		JComboBox newBox = createComboBox();
		newBox.addActionListener(this);
		newBox.setSize(110,20);
		newBox.setBounds((int)getLocation().getX()+50,(int)(getLocation().getY()) - 10,110,20);
		//newBox.setLightWeightPopupEnabled(false);
		return newBox;
	}
	
	public abstract String getLabelText();
	
	public JLabel getLabel() {
		JLabel newLabel = new JLabel(getLabelText());
		newLabel.setSize(110,20);
		newLabel.setBounds((int)getLocation().getX()+50,(int)(getLocation().getY()) - 30,110,20);
		return newLabel;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean render(Graphics2D g2d) {
		if (currentLoadDisplay == null)
			return false;
		else
			return currentLoadDisplay.render(g2d);
	}
	
	protected void handleLoadChange(LoadDisplay loadDisp) {
		for (int i = 0; i < loadTypes.size(); i++) {
			if (loadTypes.get(i).equals(loadDisp)) {
				currentLoadDisplay = loadDisp;
				getLoadData().setMW(loadMWs.get(i).doubleValue());
			}
		}
	}

	protected void handleLoadChange(String newLoadDescription) {
	    for (int i = 0; i < loadTypes.size(); i++) {
	    	LoadDisplay checkLoad = loadTypes.get(i);
	    	if (checkLoad.getDescription().equalsIgnoreCase(newLoadDescription)) {
	    		currentLoadDisplay = checkLoad;
	    		getLoadData().setMW(loadMWs.get(i).doubleValue());
	    	}
	    }
	}

	public void actionPerformed(ActionEvent e) {
	    JComboBox cb = (JComboBox)e.getSource();
	    LoadDisplay loadDisp = (LoadDisplay)cb.getSelectedItem();
	    //handleLoadChange(loadDisp.getDescription());
	    handleLoadChange(loadDisp);
	}

	public boolean animationstep() {
		return currentLoadDisplay.animationstep();
	}

}