package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Arc2D.Double;

import edu.uiuc.power.dataobjects.StorageDevice;
import edu.uiuc.power.displayobjects.DataLabel.DataLabelHorizontalAlignment;

public class StorageDisplay implements Renderable, Animatable {
	
	public enum StorageDisplayOrientation {
		VERTICAL, HORIZONTAL;
	}
	
	class StorageDataLabel extends DataLabel {

		public StorageDataLabel(Point2D ULCorner, int textsize,
				Color textcolor, double rotationAngle) {
			super(ULCorner, textsize, textcolor, rotationAngle);
		}

		@Override
		public DataLabelHorizontalAlignment getHorizontalAlignment() {
			return DataLabelHorizontalAlignment.CENTER;
		}

		@Override
		public String getCaption() {
			return "Storage Device";
		} 
		
		
	}
	
	public enum Alignment {TOP, BOTTOM, CENTER, LEFT, RIGHT};

	StorageDevice sdevice;
	Alignment halign,valign;
	Point2D coords;
	StorageDataLabel mylabel;
	double batteryHeight = 100;
	double batteryWidth = 50;
	double scale = 1.0;
	double rotation = 0.0;
	double hoffset = 0.0;
	
	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setBatteryHeight(double batteryHeight) {
		this.batteryHeight = batteryHeight;
	}
	
	public StorageDisplay(StorageDevice sdevice, Alignment halign, Alignment valign, Point2D coords) {
		this(sdevice,halign,valign,coords,StorageDisplayOrientation.VERTICAL);
	}
	
	public StorageDisplay(StorageDevice sdevice, Alignment halign, Alignment valign, Point2D coords, StorageDisplayOrientation orientation) {
		super();
		this.sdevice = sdevice;
		this.halign = halign;
		this.valign = valign;
		this.coords = coords;
		
		switch (orientation) {
			case HORIZONTAL:
				this.mylabel = new StorageDataLabel(new Point2D.Double(0 + batteryHeight/2,0 + batteryWidth),11,Color.BLACK,0);
				rotation = Math.PI/2;
				hoffset = batteryHeight - batteryWidth/4;
				break;
			case VERTICAL:
				this.mylabel = new StorageDataLabel(new Point2D.Double(0 + batteryWidth/2,0 + batteryHeight),11,Color.BLACK,0);
				hoffset = 0;
				rotation = 0;
				break;
		}
	}	

	public boolean render(Graphics2D g2d) {
		AffineTransform origXForm = g2d.getTransform();
		Color origColor = g2d.getColor();
		
		g2d.translate(coords.getX(), coords.getY());
		g2d.scale(scale, scale);
		mylabel.render(g2d);
		g2d.translate(hoffset, 0);
		g2d.rotate(rotation);
		// define some constants
		double ellipseHeight = batteryWidth/2;
		double sideLength = batteryHeight - ellipseHeight;
		
		Ellipse2D batteryEllipse = new Ellipse2D.Double(0,0,batteryWidth,ellipseHeight);
		
		g2d.setColor(Color.WHITE);
		g2d.fill(batteryEllipse);
		g2d.fill(new Rectangle2D.Double(0,ellipseHeight/2,batteryWidth,sideLength));
		g2d.fill(new Ellipse2D.Double(0,sideLength,batteryWidth,ellipseHeight));

		// draw bottom cap
		g2d.translate(0, sideLength);
		g2d.setColor(Color.BLACK);
		g2d.draw(batteryEllipse);
		
		if (sdevice.getStoredEnergy() > 0) {
			
		}
		double percentFull = Math.min(Math.max(sdevice.getStoredEnergy() / sdevice.getEnergyCapacity(),0.0),1.0);
		double ascentHeight = percentFull*sideLength;
		
		// fill the translucent part
		g2d.setColor(new Color(0f,1.0f,0f,0.75f));
		//g2d.fill(batteryEllipse);
		g2d.translate(0, -ascentHeight);
		
		Arc2D ellipseArc = new Arc2D.Double();
		ellipseArc.setArc(new Rectangle2D.Double(0,ascentHeight,batteryWidth,ellipseHeight), 180, 180, Arc2D.OPEN);
		
		GeneralPath gpath = new GeneralPath();
		gpath.moveTo((float)0f,(float)ellipseHeight/2);
		
		//gpath.moveTo((float)batteryWidth,(float)ellipseHeight/2);
		gpath.lineTo((float)0f, (float)(ascentHeight + ellipseHeight/2));
		gpath.append(ellipseArc, true);
		//gpath.lineTo((float)batteryWidth,(float)(ascentHeight + ellipseHeight/2));
		gpath.lineTo((float)batteryWidth,(float)ellipseHeight/2);
		ellipseArc.setArc(new Rectangle2D.Double(0,0,batteryWidth,ellipseHeight),360,-180,Arc2D.OPEN);
		gpath.append(ellipseArc,true);
		//gpath.lineTo(0f,(float)ellipseHeight/2);
		gpath.closePath();
		
		Paint greentowhite;
		greentowhite = new GradientPaint(0,0,new Color(0f,1f,0f,0.75f),(float)(3*batteryWidth/4),0,new Color(1f,1f,1f,1f),true);
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(greentowhite);
		g2d.fill(gpath);
		g2d.setPaint(oldPaint);
		

		g2d.setColor(new Color(0.1f,1.0f,0.1f,0.75f));
		//RadialGradientPaint  radialPaint = new RadialGradientPaint(new Rectangle2D.Double(0,0,batteryWidth,ellipseHeight),dist,colors,CycleMethod.NO_CYCLE);
		//radialPaint = new RadialGradientPaint((float)batteryWidth/2,(float)ellipseHeight/2,(float)batteryWidth,(float)(4*batteryWidth/5),(float)ellipseHeight,dist,colors,CycleMethod.NO_CYCLE);
		g2d.fill(batteryEllipse);
		g2d.setColor(new Color(0f,0f,0f,0.25f));
		g2d.draw(batteryEllipse);
			
		
		// draw top cap
		g2d.setTransform(origXForm);
		g2d.translate(coords.getX(), coords.getY());
		g2d.scale(scale, scale);
		g2d.translate(hoffset,0);
		g2d.rotate(rotation);
		g2d.setColor(Color.BLACK);
		g2d.draw(batteryEllipse);

		// draw sides
		g2d.translate(0, ellipseHeight/2);
		g2d.setColor(Color.BLACK);
		g2d.draw(new Line2D.Double(0,0,0,sideLength));
		g2d.draw(new Line2D.Double(batteryWidth,0,batteryWidth,sideLength));

		g2d.setTransform(origXForm);
		g2d.setColor(origColor);
		
//		mylabel.render(g2d);
		
		return true;
	}

	public boolean animationstep() {
		// TODO Auto-generated method stub
		return false;
	}

}
