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


public class GenericPlantDisplay implements Renderable, ImageObserver, MouseListener {

    BufferedImage transparent;
    Point2D _ULCorner;
    GeneratorMWControl gencontrol;
    GeneratorData gendata;
    double deltaPerMouseClick;
    double scale = 1.0;
    
    public void setScale(double scale) {
    	this.scale = scale;
    }
    
    public double getScale() {
    	return scale;
    }
    
    public GenericPlantDisplay(Point2D ULCorner, double deltaPerMouseClick,
    		GeneratorData gendata) throws IOException {
    	gencontrol = null;
    	this.gendata = gendata;
    	this.deltaPerMouseClick = deltaPerMouseClick;
    	_ULCorner = ULCorner;
        InputStream is = getClass().getResourceAsStream(getPathToImage());
        BufferedImage image = ImageIO.read(is);
        transparent = createTransparentImage(image);
    }
    
    public String getPathToImage() {
    	return "/applet_images/coal_plant.png";
    }
    
    public String getCaption() {
    	return "Generic Plant";
    }

    public Point2D getULCorner() {
    	return _ULCorner;
    }
    
    public void setULCorner(Point2D ULCorner) {
    	_ULCorner = ULCorner;
    }
    
    public double getCaptionYOffset() {
    	return 100;
    }
    
    public boolean render(Graphics2D g2d) {
    	
    	AffineTransform origXForm = g2d.getTransform();
    	
    	
    	if (gencontrol == null) {
    		gencontrol = new GeneratorMWControl(new Point2D.Double(0 + transparent.getWidth()/2,0 + transparent.getHeight()),14,Color.BLACK,0,gendata,deltaPerMouseClick);
    	}
    	g2d.translate(getULCorner().getX(), getULCorner().getY());
    	g2d.scale(getScale(), getScale());
    	gencontrol.render(g2d);
    	g2d.drawImage(transparent, 0, 0, this);
        
		String text = getCaption();
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 11));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate((transparent.getWidth()/2)-(textwidth/2),transparent.getHeight() + 1.65*gencontrol.getHeight());
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
}
