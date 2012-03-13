package edu.uiuc.power.displayobjects;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import edu.uiuc.power.dataobjects.GeneratorData;
import edu.uiuc.power.dataobjects.WindGeneratorData;


public class WindPlantDisplay implements Renderable, ImageObserver, MouseListener, Animatable {

    BufferedImage baseImage;
    BufferedImage turbineImage;
    Point2D _ULCorner;
    GeneratorMWControl gencontrol;
    WindGeneratorData gendata;
    double deltaPerMouseClick;
    double angle;
    double capacity = -1;
    double scale = 1.0;
    
    public double getScale() {
    	return scale;
    }
    
    public void setScale(double scale) {
    	this.scale = scale;
    }
    
    public void setCapacity(double capacity) {
    	this.capacity = capacity;
    }
    
    public WindPlantDisplay(Point2D ULCorner, double deltaPerMouseClick,
    		WindGeneratorData gendata) throws IOException {
    	gencontrol = null;
    	this.gendata = gendata;
    	this.deltaPerMouseClick = deltaPerMouseClick;
    	_ULCorner = ULCorner;
        String path = "/applet_images/wind base transparent with 200 width.png";
        InputStream is = getClass().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(is);
        baseImage = createTransparentImage(image);
        
        path = "/applet_images/wind turbine transparent with 200 width.png";
        is = getClass().getResourceAsStream(path);
        image = ImageIO.read(is);
        turbineImage = createTransparentImage(image);
        
        angle = 0;
    }

    public String getCaption() {
    	return "Wind Farm";
    }

    public Point2D getULCorner() {
    	return _ULCorner;
    }
    
    public void setULCorner(Point2D ULCorner) {
    	_ULCorner = ULCorner;
    }
    
    public double getCaptionYOffset() {
    	return 400;
    }
    
    public boolean render(Graphics2D g2d) {
    	
    	if (gencontrol == null) {
    		gencontrol = new GeneratorMWControl(new Point2D.Double(0 + (4.0/5.0)*turbineImage.getWidth()/4,0 + (4.0/5.0)*(turbineImage.getHeight()/2.0 + baseImage.getHeight())/2),14,Color.BLACK,0,gendata,deltaPerMouseClick);
    	}
    	
    	AffineTransform origXForm = g2d.getTransform();
	    	g2d.translate(getULCorner().getX(), getULCorner().getY());
	    	g2d.scale(getScale(), getScale());
	    	
	    	gencontrol.render(g2d);
	    	g2d.scale(0.4,0.4);
	    	//g2d.scale(1.0,1.0);
	    	//g2d.scale(4,4);
	    	
	    	AffineTransform beforeBase = g2d.getTransform();
    		for (int i = 0; i < 3; i++) {
    			double angleoffset = 0;
    			switch (i) {
    				case 0:
	    				g2d.translate(120,20);
	    				g2d.scale(0.5, 0.5);
	    				angleoffset = Math.PI/3;
	    				break;
    				case 1:
    					g2d.translate(-60,-10);
    					g2d.scale(0.75,0.75);
    					angleoffset = -Math.PI/3;
    					break;
    				default:
    			}
    			
    			AffineTransform beforeShaft = g2d.getTransform();
    	    	g2d.translate(turbineImage.getWidth()/2.0 - baseImage.getWidth()/2.0,turbineImage.getHeight()/2);
    	        g2d.drawImage(baseImage, 0, 0, this);
    	        g2d.setTransform(beforeShaft);
    	        
    	        g2d.translate(100,turbineImage.getHeight()/2);
    	        g2d.rotate(angle + angleoffset);
    	        g2d.translate(-123,-97);
    	        g2d.drawImage(turbineImage,0,0,this);
    	        g2d.setTransform(beforeBase);
    		}
	        g2d.setTransform(beforeBase);
	        
			String text = getCaption();
			Font font = g2d.getFont();
			g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 22));
			Font fontnow = g2d.getFont();
			FontRenderContext frc = g2d.getFontRenderContext();
			Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
			double textwidth = textbounds.getWidth();
			AffineTransform beforeText = g2d.getTransform();
			g2d.translate(100-(textwidth/2),getCaptionYOffset() + 3*gencontrol.getHeight());
			Color textcolor = new Color(0f,0f,0f,1f);
			g2d.setColor(textcolor);
			g2d.drawString(text, 0, 0);
			g2d.setTransform(beforeText);
			g2d.setFont(font);		
		g2d.setTransform(origXForm);
		
		//gencontrol.render(g2d);
		
        return true;
	}

    private BufferedImage createTransparentImage(BufferedImage image)
    {
        int w = image.getWidth(); 
        int h = image.getHeight();
        BufferedImage timage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = timage.createGraphics();
        g2.setPaint(new Color(0,0,0,0));
        g2.fillRect(0,0,w,h);
        g2.drawImage(image, new AffineTransform(1f,0f,0f,1f,0,0), null);
        g2.dispose();
        return timage;
    }

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		gencontrol.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean animationstep() {
		if ((Math.abs(gendata.getTurbineMW()) > 1e-3) | (capacity > -1)) {
			//angle += (gendata.getMW() / gendata.getMaxMW()) * Math.PI/40;
			if (capacity > -1)
				angle += (gendata.getMaxMW() / capacity) * Math.PI/40;
			else
				angle += (gendata.getTurbineMW() / gendata.getMaxMW()) * Math.PI/40;
			if (angle > 2*Math.PI) {
				angle -= 2*Math.PI;
			} 
			
			if (angle < -2*Math.PI) {
				angle += 2*Math.PI;
			}
			return true;
		} else
			return false;
	}
}
