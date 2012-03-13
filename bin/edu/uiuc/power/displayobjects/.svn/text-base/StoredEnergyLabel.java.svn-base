package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.ParseException;

import edu.uiuc.power.dataobjects.StorageDevice;

public class StoredEnergyLabel extends DataLabel {

	StorageDevice storageDevice;
	
	public StoredEnergyLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle, StorageDevice storageDevice) {
		super(ULCorner, textsize, textcolor, rotationAngle);
		this.storageDevice = storageDevice;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCaption() {
		try {
			return textFormatter.valueToString(storageDevice.getStoredEnergy()) + " MWh stored";
		} catch (ParseException e) {
			return e.toString();
		}
	}

	@Override
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return DataLabelHorizontalAlignment.LEFT;
	}

}
