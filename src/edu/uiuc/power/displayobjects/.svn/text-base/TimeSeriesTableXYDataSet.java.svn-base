package edu.uiuc.power.displayobjects;

import java.util.Locale;
import java.util.TimeZone;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;

public class TimeSeriesTableXYDataSet extends TimeTableXYDataset {
	TimeSeriesCollection tcCollection;
	public void setTimeSeriesCollection(TimeSeriesCollection tcCollection) {
		this.tcCollection = tcCollection;
	}
	@Override
	public void addChangeListener(DatasetChangeListener listener) {
		// TODO Auto-generated method stub
		tcCollection.addChangeListener(listener);
	}
	@Override
	public void removeChangeListener(DatasetChangeListener listener) {
		// TODO Auto-generated method stub
		tcCollection.removeChangeListener(listener);
	}
	public TimeSeriesTableXYDataSet() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TimeSeriesTableXYDataSet(TimeZone zone, Locale locale) {
		super(zone, locale);
		// TODO Auto-generated constructor stub
	}
	public TimeSeriesTableXYDataSet(TimeZone zone) {
		super(zone);
		// TODO Auto-generated constructor stub
	}
	@Override
	public double getYValue(int series, int item) {
		return tcCollection.getYValue(series, item);
	}
	@Override
	public double getXValue(int series, int item) {
		return tcCollection.getXValue(series, item);
	}
	@Override
	public int getItemCount(int series) {
		return tcCollection.getItemCount(series);
	}
	
	
}
