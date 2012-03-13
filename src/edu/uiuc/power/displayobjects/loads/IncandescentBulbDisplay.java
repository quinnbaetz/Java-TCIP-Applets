package edu.uiuc.power.displayobjects.loads;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.Renderable;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;

public class IncandescentBulbDisplay extends LoadDisplay {
	
	public IncandescentBulbDisplay(LoadData loaddata, Point2D location, LoadDisplayAlignmentVertical vertAlign, LoadDisplayAlignmentHorizontal horizAlign) {
		super(loaddata, location, vertAlign, horizAlign, "Incandescent Light");
	}

	public boolean render(Graphics2D g2d) {
		// Backup transform
		AffineTransform currentxform = g2d.getTransform();
		Color currentcolor = g2d.getColor();
		
		//g2d.translate(getULCorner().getX(),getULCorner().getY());
		this.translateToULCorner(g2d);
		
		// Drawn width = 100, drawn height = 175; scale accordingly
		
		g2d.scale(getWidth()/101, getHeight()/175);
		
		Ellipse2D.Double circ = new Ellipse2D.Double(0,0,100,100);
		Rectangle2D.Double rect = new Rectangle2D.Double(25,75,50,50);
		Area isect = new Area(circ);
		isect.add(new Area(rect));
		if (getLoadData().getConnected()) 
			g2d.setColor(Color.YELLOW);
		else
			g2d.setColor(Color.WHITE);
		
		g2d.fill(isect);
		g2d.setColor(Color.BLACK);
		g2d.draw(isect);
		
		// Draw spiral for filament
		AffineTransform beforeSpiral = g2d.getTransform();
		int numofloops = 3;
		double filamentwidth = 50;
		double smallLoopPercent = 0.3;
		double biglooplength = filamentwidth/(numofloops*(1-smallLoopPercent)+1);
		double filamentheight = biglooplength;
		
		g2d.translate(100/2-(filamentwidth/2),50);
		
		// Draw lines out of filament to base
		Point2D.Double filamentfrom,filamentto;
		filamentfrom = new Point2D.Double(0.0,0.0);
		filamentto = new Point2D.Double(filamentwidth/2 - filamentwidth/4,65);
		//filamentto = new Point2D.Double(filamentwidth/2,65);

		g2d.draw(new CubicCurve2D.Double(
				filamentfrom.getX(), 
				filamentfrom.getY(), 
				filamentfrom.getX() + filamentwidth/4, 
				filamentfrom.getY() + (filamentto.getY() - filamentfrom.getY())/4, 
				filamentfrom.getX() + filamentwidth/3, 
				filamentto.getY() - (filamentto.getY() - filamentfrom.getY())/4, 
				filamentto.getX(), 
				filamentto.getY())); 			
		
		filamentfrom = new Point2D.Double(filamentwidth,0.0);
		filamentto = new Point2D.Double(filamentwidth - filamentwidth/4,65);
		//filamentto = new Point2D.Double(filamentwidth/2,65);
		g2d.draw(new CubicCurve2D.Double(
				filamentfrom.getX(), 
				filamentfrom.getY(), 
				filamentfrom.getX() - filamentwidth/4, 
				filamentfrom.getY() + (filamentto.getY() - filamentfrom.getY())/4, 
				filamentfrom.getX() - filamentwidth/3, 
				filamentto.getY() - (filamentto.getY() - filamentfrom.getY())/4, 
				filamentto.getX(), 
				filamentto.getY())); 			
		for (int j = 0; j < (numofloops + 1); j++) {
			for (int i = 0; i < 2; i++) {
				if ((i % 2) == 0) {
					Arc2D.Double arc = new Arc2D.Double(
							new Rectangle2D.Double(
									0,
									-filamentheight/2,
									biglooplength,
									filamentheight)
							,0,180,Arc2D.OPEN);
					g2d.draw(arc);
				} else {
					if (j != numofloops) {
						Arc2D.Double arc = new Arc2D.Double(
								new Rectangle2D.Double(
										biglooplength*(1-smallLoopPercent),
										-filamentheight/2,
										smallLoopPercent*biglooplength,
										filamentheight),
								180,180,Arc2D.OPEN);
						g2d.draw(arc);
					}
				}
			}
			g2d.translate(biglooplength*(1-smallLoopPercent), 0);
		}
		
		g2d.setTransform(beforeSpiral);

		// Draw bottom rectangle
		g2d.draw(new Rectangle2D.Double(25,125,50,25));
		
		//g2d.translate(25, 175);
		g2d.setTransform(currentxform);
		this.translateToULCorner(g2d);//g2d.translate(getULCorner().getX(), getULCorner().getY());
		
		String text = "Incandescent Light";
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 14));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(getWidth()/2-textwidth/2,getHeight());
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, 0);
		
		g2d.translate(textwidth/2, 0);
		
		textbounds = fontnow.getStringBounds("Light Bulb", frc);
		textwidth = textbounds.getWidth();
		g2d.translate(-textwidth/2, textbounds.getHeight());
		//g2d.drawString("Light Bulb", 0, 0);
		
		
		g2d.setTransform(beforeText);
		g2d.setFont(font);
		
		//g2d.drawString("Light bulb", 0, 0);

		g2d.setTransform(currentxform);
		g2d.setColor(currentcolor);
		return true;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 131.25;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 75;
	}

	public boolean animationstep() {
		// TODO Auto-generated method stub
		return false;
	}
}
