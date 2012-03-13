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

public class LoadSelectableGaming extends LoadSelectable {
	
	public LoadSelectableGaming(LoadData loaddata, Point2D location, LoadDisplayAlignmentVertical vertAlign, LoadDisplayAlignmentHorizontal horizAlign) {
		super(loaddata, location, vertAlign, horizAlign, "");
		loadTypes = new ArrayList<LoadDisplay>();
		loadMWs = new ArrayList<Double>();
		
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"PC","applet_images/pc.png"));
		loadMWs.add(new Double(156.6));

		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"XBOX 360","applet_images/xbox360.png"));
		loadMWs.add(new Double(176.54));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Playstation 3","applet_images/ps3.png"));
		loadMWs.add(new Double(185.9));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Wii","applet_images/wii.png"));
		loadMWs.add(new Double(16.8));
		
		//Power usage of monitors: http://reviews.cnet.com/4520-6475_7-6400401-3.html?tag=txt
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"40\" LCD TV","applet_images/monitor.png"));
		loadMWs.add(new Double(214.4));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"42\" Plasma TV","applet_images/monitor.png"));
		loadMWs.add(new Double(357.6));

		handleLoadChange("PC");

	}
	
	@Override
	public String getLabelText() {
		return "Entertainment";
	}
	public double getDemand() {
		return loaddata.getMW();
	}

	public String getUnits() {
		return "W";
	}

	public double getWattMultiplier() {
		return 1.0;
	}	
}
