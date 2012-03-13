package edu.uiuc.power.dataobjects.timeOfUseProviders;

import java.text.ParseException;

import edu.uiuc.power.dataobjects.SimulationClock;
import edu.uiuc.power.dataobjects.TimeOfUsePriceProvider;

public enum PricingScheme {
	ONTARIO_SUMMER,ONTARIO_WINTER,//ONTARIO_WEEKENDHOLIDAY,
	AMEREN_SUMMER,AMEREN_FALL,AMEREN_WINTER,AMEREN_SPRING,
	CONSTANT;

	@Override
	public String toString() {
		switch (this) {
		case ONTARIO_SUMMER:
			return "Three-Tiered Plan - Summer";
		//case ONTARIO_WEEKENDHOLIDAY:
		//	return "Three-Tiered Plan - Weekends and Holidays";
		case ONTARIO_WINTER:
			return "Three-Tiered Plan - Winter";
		case AMEREN_WINTER:
			return "Hourly Plan - Winter";
		case AMEREN_SUMMER:
			return "Hourly Plan - Summer";
		case AMEREN_FALL:
			return "Hourly Plan - Fall";
		case AMEREN_SPRING:
			return "Hourly Plan - Spring";
		case CONSTANT:
			return "Constant Price";
		default:
			return "Unknown Pricing Scheme: " + super.toString();
		}
	}
	
	public TimeOfUsePriceProvider getTimeOfUsePriceProvider(SimulationClock simClock) {
		return getTimeOfUsePriceProvider(simClock,1.0);
	}
	
	public TimeOfUsePriceProvider getTimeOfUsePriceProvider(SimulationClock simClock, double constantPrice) {
		HourlyPrice hprice;
		switch (this) {
		case CONSTANT:
			return new ConstantPrice(constantPrice);
		case ONTARIO_SUMMER:
			hprice = new HourlyPrice(simClock, this.toString());
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesOntarioSummer, pricesOntarioSummer);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		/*case ONTARIO_WEEKENDHOLIDAY:
			hprice = new HourlyPrice(simClock);
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesOntarioWeekendHolidays, pricesOntarioWeekendHolidays);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		*/
		case ONTARIO_WINTER:
			hprice = new HourlyPrice(simClock, this.toString());
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesOntarioWinter, pricesOntarioWinter);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case AMEREN_WINTER:
			hprice = new HourlyPrice(simClock, this.toString());
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesAmeren, pricesAmerenWinter);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case AMEREN_SUMMER:
			hprice = new HourlyPrice(simClock, this.toString());
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesAmeren, pricesAmerenSummer);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;			
			
		case AMEREN_FALL:
			hprice = new HourlyPrice(simClock, this.toString());
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesAmeren, pricesAmerenFall);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case AMEREN_SPRING:
			hprice = new HourlyPrice(simClock, this.toString());
			try {
				hprice.addPointsTimeDataAndPriceStrings(timesAmeren, pricesAmerenSpring);
				return hprice;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;				
		default:
			System.out.println("Invalid season: " + this);
		}
		return null;
	}
	
	static String[] timesOntarioSummer = {"00:00","07:00","11:00","17:00","21:00"};
	static double[] pricesOntarioSummer = {0.042,0.076,0.091,0.076,0.042};
	static String[] timesOntarioWeekendHolidays = {"00:00"};
	static double[] pricesOntarioWeekendHolidays = {0.042};
	static String[] timesOntarioWinter = {"00:00","07:00","11:00","17:00","21:00"};
	static double[] pricesOntarioWinter = {0.042,0.091,0.076,0.091,0.042};
	static String[] timesAmeren = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
	static double[] pricesAmerenWinter = {0.023,0.022,0.021,0.021,0.02,0.021,0.023,0.023,0.025,0.026,0.027,0.025,0.024,0.024,0.024,0.026,0.047,0.064,0.054,0.045,0.04,0.028,0.023,0.021}; // Jan 6 2008
	static double[] pricesAmerenSpring = {0.019,0.018,0.018,0.018,0.022,0.037,0.049,0.063,0.071,0.077,0.081,0.081,0.084,0.084,0.081,0.081,0.068,0.06,0.048,0.065,0.078,0.039,0.026,0.022}; // May 6 2008
	static double[] pricesAmerenSummer = {0.0245,0.02221,0.02161,0.02123,0.02147,0.02365,0.02971,0.03619,0.04208,0.05384,0.06233,0.0672,0.0752,0.0799,0.09054,0.09771,0.09114,0.07913,0.07196,0.06543,0.06755,0.0527,0.03813,0.03161}; // August 3 2010
	static double[] pricesAmerenFall =  {0.018,0.014,0.012,0.011,0.013,0.012,0.014,0.023,0.029,0.035,0.043,0.045,0.045,0.046,0.045,0.049,0.051,0.045,0.034,0.041,0.04,0.026,0.02,0.018}; // Sept 6 2008

}
