package edu.uiuc.power.displayobjects;

import java.awt.AlphaComposite;
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

import edu.uiuc.power.dataobjects.LoadData;

public class ImageDisplay implements ImageObserver, Renderable {

	public enum ImageDisplayAlignmentVertical {
		CENTER, TOP, BOTTOM
	}
	
	public enum ImageDisplayAlignmentHorizontal {
		CENTER, LEFT, RIGHT
	}	
	
	BufferedImage image;
    BufferedImage transparent;
    boolean imageValid;
    Point2D location;
    ImageDisplayAlignmentVertical vertAlign;
    ImageDisplayAlignmentHorizontal horizAlign;
    private double scale;
    
	public ImageDisplay(Point2D location,
			ImageDisplayAlignmentVertical vertAlign,
			ImageDisplayAlignmentHorizontal horizAlign,
			String loadDescription,
			BufferedImage image,
			double scale) {
		this.vertAlign = vertAlign;
		this.horizAlign = horizAlign;
		this.location = location;
		this.image = image;
		this.imageValid = false;
		this.scale = scale;
	}

	
	private BufferedImage getImage() {
		if ((!imageValid) | (transparent == null)) {
	        //InputStream is = getClass().getResourceAsStream(getImagePath());
	        //System.out.println(getImagePath());
	        //BufferedImage image = ImageIO.read(is);
	        transparent = createTransparentImage(image);
	        if (transparent != null)
	        	imageValid = true;
	        else
	        	imageValid = false;
		}
		return transparent;
	}

	private BufferedImage createTransparentImage(BufferedImage image)
    {
        int w = (int)(image.getWidth()*scale); 
        int h = (int)(image.getHeight()*scale);
        BufferedImage timage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = timage.createGraphics();
        g2.setPaint(new Color(0,0,0,0));
        g2.fillRect(0,0,w,h);
        g2.scale(scale, scale);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g2.drawImage(image, new AffineTransform(1f,0f,0f,1f,0,0), null);
        g2.dispose();
        return timage;
    }
	
	public boolean render(Graphics2D g2d) {
    	AffineTransform origXForm = g2d.getTransform();
    	translateToULCorner(g2d);
    	
		g2d.drawImage(getImage(), 0, 0, this);

		g2d.setTransform(origXForm);
		
		/*
		translateToULCorner(g2d);

		g2d.translate(0,getImage().getHeight());
		
		
		String text = getDescription();
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 14));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(getImage().getWidth()/2-(textwidth/2),textbounds.getHeight());
		
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, 0);
		

		g2d.setTransform(beforeText);
		
		
		g2d.setFont(font);
        g2d.setTransform(origXForm);
        */
        return true;
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	public double getHeight() {
		BufferedImage image = getImage();
		return image.getHeight();
	}

	public double getWidth() {
		BufferedImage image = getImage();
		return image.getWidth();
	}

	public boolean animationstep() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void translateToULCorner(Graphics2D g2d) {
		g2d.translate(location.getX(), location.getY());
		switch (vertAlign) {
			case BOTTOM:
				g2d.translate(0, -getHeight());
				break;
			case CENTER:
				g2d.translate(0, -getHeight()/2);
				break;
			case TOP:
				g2d.translate(0, 0);
				break;
		}
		switch (horizAlign) {
			case LEFT:
				g2d.translate(0, 0);
				break;
			case CENTER:
				g2d.translate(-getWidth()/2,0);
				break;
			case RIGHT:
				g2d.translate(-getWidth(), 0);
				break;
		}
	}
}
