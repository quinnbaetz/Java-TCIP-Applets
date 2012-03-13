package edu.uiuc.power.displayobjects;

import java.awt.*;
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


public class SubstationDisplay implements Renderable, ImageObserver {

    BufferedImage transparent;
    Point2D _ULCorner;
    String substationName;
    
    public SubstationDisplay(Point2D ULCorner, int SubstationNumber) throws IOException {
    	this(ULCorner,Integer.toString(SubstationNumber));
    }
    
    public SubstationDisplay(Point2D ULCorner, String SubstationName) throws IOException {
    	_ULCorner = ULCorner;
        String path = "/applet_images/substation.png";
        InputStream is = getClass().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(is);
        transparent = createTransparentImage(image);
        this.substationName = SubstationName;
    }

    public Point2D getULCorner() {
    	return _ULCorner;
    }
    
    public void setULCorner(Point2D ULCorner) {
    	_ULCorner = ULCorner;
    }
    
    public boolean render(Graphics2D g2d) {
    	AffineTransform origXForm = g2d.getTransform();
    	g2d.translate(getULCorner().getX(), getULCorner().getY());
    	g2d.scale(1,1);
        g2d.drawImage(transparent, 0, 0, this);
        
		String text = "Substation";
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.BOLD, 14));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(50-(textwidth/2),12);
		Color textcolor = new Color(1f,1f,1f,1f);
		g2d.setColor(textcolor);
		g2d.drawString(text, 0, 0);
		
		
		g2d.setTransform(beforeText);
		g2d.translate(0,12 + textbounds.getHeight());
		text = substationName;
		textbounds = fontnow.getStringBounds(text, frc);
		textwidth = textbounds.getWidth();
		g2d.translate(50-(textwidth/2), 0);
		g2d.drawString(text,0,0);
		
		g2d.setTransform(beforeText);
		g2d.setFont(font);		
		
        g2d.setTransform(origXForm);
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
}
