package edu.uiuc.power.displayobjects.loads;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComboBox;

import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.Animatable;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;
import edu.uiuc.power.displayobjects.loads.LoadDisplay;

public class KitchenAppliances extends LoadSelectable {

	public KitchenAppliances(LoadData loaddata, Point2D location, LoadDisplayAlignmentVertical vertAlign, LoadDisplayAlignmentHorizontal horizAlign) {
		super(loaddata, location, vertAlign, horizAlign, "");
		
		loadTypes = new ArrayList<LoadDisplay>();
		loadMWs = new ArrayList<Double>();
		
		// Whirlpool, 120 V, 9.6 A
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Dishwasher","applet_images/dishwasher.png"));
		loadMWs.add(new Double(120*9.6));
		
		// Maytag, 120 V, 9A
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"E-Star Dishwasher","applet_images/dishwasher.png"));
		loadMWs.add(new Double(120*9));
		
		// KitchenAid 25.1 ft^3, KBFL25ETSS00, 115 V, 7.9 A
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Fridge","applet_images/fridge.png"));
		//loadMWs.add(new Double(115*7.9));
		loadMWs.add(new Double(300)); // Changed to 300 based on data from website Jana sent me the link to 
		
		// KitchenAid 25 ft^3, KSRL25FRST04, 115 V, 6.5 A
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"E-Star Fridge","applet_images/fridge.png"));
		//loadMWs.add(new Double(115*6.5));
		loadMWs.add(new Double(300*(6.5/7.9))); // Changed to 300 using same ratio as KitchenAid estar based on data from website Jana sent me the link to 

		// sharp 1200 w output, 1700 w input, 2.0 ft^3
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Microwave","applet_images/microwave.png"));
		loadMWs.add(new Double(1700));
		
		// Oster 4-slice toaster
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Toaster","applet_images/toaster.png"));
		loadMWs.add(new Double(1500));
		
		loadTypes.add(new IncandescentBulbDisplay(loaddata,location,vertAlign,horizAlign));
		loadMWs.add(new Double(100));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Fluorescent Light","applet_images/compact_fluorescent.png"));
		loadMWs.add(new Double(33));		
		
		handleLoadChange("Dishwasher");
	}

	@Override
	public String getLabelText() {
		return "Kitchen Appliance";
	}
	
	public double getDemand() {
		return loaddata.getMW();
	}

	public String getUnits() {
		return "W";
	}

	public double getWattMultiplier() {
		return 1.0;
	}}
