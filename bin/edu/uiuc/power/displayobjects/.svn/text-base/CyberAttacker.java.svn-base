package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class CyberAttacker implements Renderable, ImageObserver {
	
	Point2D ULCorner;
	BufferedImage transparent;
    Image transparentScaled;
	
	public CyberAttacker(Point2D ULCorner, int width, int height) throws IOException {
		this.ULCorner = ULCorner;
		
        String path = "/applet_images/hacker.png";
        InputStream is = getClass().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(is);
        transparent = createScaledTransparentImage(image, width, height);
	}
	
	public Point2D getULCorner() {
		return ULCorner;
	}
	
    private BufferedImage createTransparentImage(BufferedImage image)
    {
        int w = image.getWidth(); 
        int h = image.getHeight();
        BufferedImage timage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = timage.createGraphics();
        g2.setPaint(new Color(0,0,0,0));
        g2.fillRect(0,0,w,h);
        AffineTransform ident = new AffineTransform(1f,0f,0f,1f,0,0);
        
        g2.drawImage(image, new AffineTransform(1f,0f,0f,1f,0,0), null);
        g2.dispose();
        return timage;
    }
    
    private BufferedImage createScaledTransparentImage(BufferedImage image, 
    		int maxwidth, int maxheight)
    {
        double scale = DrawUtilities.getImageScaleToFitInBounds(
        		image.getWidth(), image.getHeight(), maxwidth, maxheight);
        
    	int w = (int)Math.ceil(image.getWidth() * scale);
    	int h = (int)Math.ceil(image.getHeight() * scale);
        
        BufferedImage timage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = timage.createGraphics();
        g2.setPaint(new Color(0,0,0,0));
        g2.fillRect(0,0,w,h);
        AffineTransform scaleXForm = new AffineTransform(1f,0f,0f,1f,0,0);
        scaleXForm.scale(scale, scale);
        
        g2.drawImage(image, scaleXForm, null);
        g2.dispose();
        return timage;
    }
    

	public boolean render(Graphics2D g2d) {
    	AffineTransform origXForm = g2d.getTransform();
    	g2d.translate(getULCorner().getX(), getULCorner().getY());
    	
        g2d.drawImage(transparent, 0, 0, this);
		String text = "Cyber-attacker";
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		double textheight = textbounds.getHeight();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(transparent.getWidth()/2 - textwidth/2,transparent.getHeight() + textheight);
		Color textcolor = new Color(0f,0f,0f,1f);
		g2d.setColor(textcolor);
		g2d.drawString(text, 0, 0);
		g2d.setTransform(beforeText);
		
		int rectwidth;
		int rectXOffset;
		if (textwidth > transparent.getWidth()) {
			rectwidth = (int)Math.ceil(textwidth);
			rectXOffset = -(int)Math.ceil((textwidth - transparent.getWidth())/2);
		} else {
			rectwidth = transparent.getWidth();
			rectXOffset = 0;
		}
		
		g2d.drawRect(rectXOffset - 2, 0, rectwidth + 4, transparent.getHeight() + (int)(1.25*textheight));
		
		g2d.setFont(font);		
		g2d.setTransform(origXForm);
		
		return true;
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}	
}
