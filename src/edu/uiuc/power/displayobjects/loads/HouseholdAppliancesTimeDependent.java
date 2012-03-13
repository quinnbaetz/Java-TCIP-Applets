package edu.uiuc.power.displayobjects.loads;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JComboBox;

import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.dataobjects.SimulationClock;
import edu.uiuc.power.displayobjects.Animatable;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;
import edu.uiuc.power.displayobjects.loads.LoadDisplay;

public class HouseholdAppliancesTimeDependent extends LoadSelectable {

	public class SolarModuleTimeDependent extends ImageLoadWithCaption {
		double hourSunrise;
		double hourSunset;
		SimulationClock simClock;
		double hoursToFadeInOut;
		double ratedPower;
		
		public SolarModuleTimeDependent(LoadData loaddata, Point2D location,
				LoadDisplayAlignmentVertical vertAlign,
				LoadDisplayAlignmentHorizontal horizAlign,
				String loadDescription, String imagePath,
				double hourSunrise, double hourSunset, double hoursToFadeInOut,
				SimulationClock simClock, double ratedPower) {
			super(loaddata, location, vertAlign, horizAlign, loadDescription, imagePath);
			this.hourSunrise = hourSunrise;
			this.hourSunset = hourSunset;
			this.simClock = simClock;
			this.hoursToFadeInOut = hoursToFadeInOut;
			this.ratedPower = ratedPower;
			// TODO Auto-generated constructor stub
		}
		
		protected double getCurrentOutput() {
			Calendar currentCal = simClock.getCurrentCalendar();
			double currentHour = (double)(currentCal.get(Calendar.HOUR_OF_DAY)) + 1.0/60.0*(currentCal.get(Calendar.MINUTE));
			if (currentHour < hourSunrise)
				return 0;
			else if (currentHour > hourSunset)
				return 0;
			else {
				if (currentHour < (hourSunrise + hoursToFadeInOut)) {
					double inFade = currentHour - hourSunrise;
					double percentFaded = inFade / hoursToFadeInOut;
					return -ratedPower*percentFaded;
				} else if (currentHour > (hourSunset - hoursToFadeInOut)) {
					double inFade = currentHour - (hourSunset - hoursToFadeInOut);
					double percentFaded = inFade / hoursToFadeInOut;
					return -ratedPower*(1.0-percentFaded);
				} else {
					return -ratedPower;
				}
			}
		}

		@Override
		public boolean animationstep() {
			loaddata.setMW(getCurrentOutput());
			return super.animationstep();
		}
	}
	
	public HouseholdAppliancesTimeDependent(LoadData loaddata, Point2D location, LoadDisplayAlignmentVertical vertAlign, LoadDisplayAlignmentHorizontal horizAlign,
			SimulationClock simClock) {
		super(loaddata, location, vertAlign, horizAlign, "");
		
		loadTypes = new ArrayList<LoadDisplay>();
		loadMWs = new ArrayList<Double>();
		
		// 52" Harbor Breeze "Lakeside II", 32 W (Medium speed
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Fan","applet_images/fan.png"));
		loadMWs.add(new Double(32));
		
		// 52" Hunter "Silent Breeze", EStar, 27 W (Medium speed)
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"E-Star Fan","applet_images/fan.png"));
		loadMWs.add(new Double(27));
		
		// Samsung 6000 BTU, AW06NCM7 (non-EStar), 620 W
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Room A/C","applet_images/aircon.png"));
		loadMWs.add(new Double(620));
		
		// Samsung 6000 BTU, AW06ECB7, EStar, 560 W
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"E-Star Room A/C","applet_images/aircon.png"));
		loadMWs.add(new Double(560));
		
		// 40 gallon hot water heater, EE2H40RD045V, 4500 W
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Hot Water Heater","applet_images/hotwater.png"));
		loadMWs.add(new Double(4500));
		
		// Iron - Shark Gentle Glide Precision Iron
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Iron","applet_images/iron.png"));
		loadMWs.add(new Double(1400));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Hair Dryer","applet_images/hairdryer.png"));
		loadMWs.add(new Double(1000));			
		
		// Vacuum Cleaner - Dyson DC15AF - 12 A, 120 V
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Vacuum","applet_images/vacuum.png"));
		loadMWs.add(new Double(120*12));
		
		// GE WJRR417090WW - Non e-star - 120V, 10 A
		loadTypes.add(new ClothesWasherDisplay(loaddata,location,vertAlign,horizAlign));
		loadMWs.add(new Double(120*10));
		
		// EcoSmart GWL15 110-120 V, 7 A
		loadTypes.add(new ClothesWasherDisplay(loaddata,location,vertAlign,horizAlign,"E-Star Washer"));
		loadMWs.add(new Double(120*7));
		
		//Sharp NE-175U1, 175W
		//loadTypes.add(new ImageLoadWithCaption(loaddata, location, vertAlign, horizAlign, "Solar Module", "applet_images/solarmodule.png"));
		//loadMWs.add(new Double(-175));
		loadTypes.add(new SolarModuleTimeDependent(loaddata, location, vertAlign, horizAlign, "Solar Module", "applet_images/solarmodule.png",
				6,18,1,simClock,175));
		loadMWs.add(new Double(0));
		
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"40\" LCD TV","applet_images/monitor.png"));
		loadMWs.add(new Double(214.4));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"42\" Plasma TV","applet_images/monitor.png"));
		loadMWs.add(new Double(357.6));		
		
		loadTypes.add(new IncandescentBulbDisplay(loaddata,location,vertAlign,horizAlign));
		loadMWs.add(new Double(100));
		
		loadTypes.add(new ImageLoadWithCaption(loaddata,location,vertAlign,horizAlign,"Fluorescent Light","applet_images/compact_fluorescent.png"));
		loadMWs.add(new Double(33));		
		
		handleLoadChange("Fan");
		
	}

	@Override
	public String getLabelText() {
		return "Household Appliance";
	}
//
//	@Override
//	public JComboBox createComboBox() {
//		LoadDisplay[] retLoadArrayTemplate = new LoadDisplay[0];
//		LoadDisplay[] loadsArray = loadTypes.toArray(retLoadArrayTemplate);
//		JComboBox retBox = new JComboBox();
//		retBox.add
//		JComboBox retCBox = super.getComboBox();
//		return retCBox;
//	}

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
