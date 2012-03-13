package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;


import edu.uiuc.power.dataobjects.CostAndEmissionsProvider;
import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.dataobjects.GeneratorDataWithExportImportPenalties;

public class CostAndEmissionsOverlay implements Animatable, Renderable {
	
	private Point2D centerLocation;
	protected CostAndEmissionsProvider costprovider;
	private boolean visible = true;
	private boolean displayEmissions = true;
	
	public CostAndEmissionsOverlay(Point2D centerLocation, CostAndEmissionsProvider costprovider, boolean displayEmissions) {
		this.centerLocation = centerLocation;
		this.costprovider = costprovider;
		this.displayEmissions = displayEmissions;
	}
	
	public CostAndEmissionsOverlay(Point2D centerLocation, CostAndEmissionsProvider costprovider) {
		this(centerLocation, costprovider, true); // default is to display the emissions field
	}
	
	public void setVisible(boolean visibility) {
		this.visible = visibility;
	}
	
	public boolean animationstep() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected String convertValueToString(double value, boolean isCost) {
		DecimalFormat decimalFormatCost = new DecimalFormat("##,##0");
		NumberFormatter textFormatterCost = new NumberFormatter(decimalFormatCost);
		
		DecimalFormat decimalFormatEmissions = new DecimalFormat("##0.0");
		NumberFormatter textFormatterEmissions = new NumberFormatter(decimalFormatEmissions);
		
		String retString = "ERROR!";
		try {
			if (isCost)
				retString = textFormatterCost.valueToString(value);
			else
				retString = textFormatterEmissions.valueToString(value);
		} catch (ParseException e) {
			retString = "UNABLE TO PARSE VALUE";
		}
		return retString;
	}

	protected String getCaptionLine1() {
		String prefixString;
		if (isCost())
			prefixString = "Cost";
		else
			prefixString = "Payment";
		return prefixString + ": $" + convertValueToString(Math.abs(costprovider.getCost(1.0)),true) + "/hr";
	}
	
	protected boolean isCost() {
		if (costprovider instanceof GeneratorData)
			if (costprovider.getCost(1) >= 0)
				return true;
			else
				return false;
		else
			return false;
	}
	
	private Color getCostColor() {
		return new Color(200,0,0);
	}
	
	private Color getPaymentColor() {
		return new Color(0,100,0);
	}
	
	protected String getCaptionLine2() {
		return "Emissions: " + convertValueToString(Math.abs(costprovider.getEmissions(1.0)),false) + " tons/hr";
	}
	
	protected int getFontSize() {
		return 11;
	}
	
	protected int getFontStyle() {
		return Font.PLAIN;
	}

	public boolean render(Graphics2D g2d) {
		if (visible) {
			
			Font backupFont = g2d.getFont();
			AffineTransform backupXForm = g2d.getTransform();
			Color backupColor = g2d.getColor();
				
				String text1 = getCaptionLine1();
				String text2 = getCaptionLine2();
				
				g2d.setFont(new Font("Lucida Bright", getFontStyle(), getFontSize()));
				Font fontnow = g2d.getFont();
				FontRenderContext frc = g2d.getFontRenderContext();
				Rectangle2D textbounds1 = fontnow.getStringBounds(text1, frc);
				Rectangle2D textbounds2 = fontnow.getStringBounds(text2, frc);
			
				if (displayEmissions) {
					double maxwidth = Math.max(textbounds1.getWidth(),textbounds2.getWidth());
					double vertBuffer = 0.25*textbounds1.getHeight();
					double height = textbounds1.getHeight() + textbounds2.getHeight() + vertBuffer;
					
					g2d.translate(centerLocation.getX(), centerLocation.getY());
					g2d.setPaint(Color.WHITE);
					Shape outlineRect = new Rectangle2D.Double(-maxwidth/2 - vertBuffer,-height/2.0,maxwidth + vertBuffer*2,height+vertBuffer); 
					g2d.fill(outlineRect);
					g2d.setPaint(Color.BLACK);
					g2d.draw(outlineRect);
					
					if (isCost()) 
						g2d.setPaint(getCostColor());
					else
						g2d.setPaint(getPaymentColor());
					g2d.drawString(text1,(float)(-textbounds1.getWidth()/2.0),(float)(-vertBuffer/2.0));
					
					if (isCost())
						g2d.setPaint(getCostColor());
					else
						g2d.setPaint(getPaymentColor());
					g2d.drawString(text2,(float)(-textbounds2.getWidth()/2.0),(float)(+vertBuffer/2.0 + textbounds2.getHeight()));
				} else {
					double maxwidth = textbounds1.getWidth();
					double vertBuffer = 0.25*textbounds1.getHeight();
					double height = textbounds1.getHeight();
					
					g2d.translate(centerLocation.getX(), centerLocation.getY());
					g2d.setPaint(Color.WHITE);
					Shape outlineRect = new Rectangle2D.Double(-maxwidth/2 - vertBuffer,-(height + vertBuffer)/2.0,maxwidth + vertBuffer*2,(height+vertBuffer)); 
					g2d.fill(outlineRect);
					g2d.setPaint(Color.BLACK);
					g2d.draw(outlineRect);
					
					if (isCost())
						g2d.setPaint(getCostColor());
					else
						g2d.setPaint(getPaymentColor());
					g2d.drawString(text1,(float)(-textbounds1.getWidth()/2.0),(float)(height/2.0 - vertBuffer/2.0));
				}
				
			g2d.setTransform(backupXForm);
			g2d.setFont(backupFont);
			g2d.setColor(backupColor);
			return true;
		}
		else
			return false;
	}

}
