package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class PlaceholderImage implements Renderable, ImageObserver {
	
	public enum PlaceholderType {
		BIOMASS, SOLAR, OCEAN;
	}

	BufferedImage transparent;
	double[] coordxs, coordys;
	
	public PlaceholderImage(double[] coordxs, double[] coordys, PlaceholderType pType) throws IOException {
		this.coordxs = coordxs;
		this.coordys = coordys;
		
		String path = null;
		switch(pType) {
		case BIOMASS:
			path = "/applet_images/biomass_power_placeholder.gif";
			break;
		case SOLAR:
			path = "/applet_images/solar_power_placeholder.gif";
			break;
		case OCEAN:
			path = "/applet_images/ocean_power_placeholder.gif";
			break;
		default:
			path = "/applet_images/biomass_power_placeholder.gif";
			break;
		}
		InputStream is = getClass().getResourceAsStream(path);
		BufferedImage image = ImageIO.read(is);
		transparent = createTransparentImage(image);
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


	public boolean render(Graphics2D g2d) {
		AffineTransform origXForm = g2d.getTransform();
		for (int i = 0; i < coordxs.length; i++) {
			g2d.translate(coordxs[i], coordys[i]);
			g2d.fill(new Rectangle2D.Double(0-1,0-1,transparent.getWidth()+2,transparent.getHeight()+2));
			g2d.drawImage(transparent, 0, 0, this);
			g2d.setTransform(origXForm);
		}
		return true;
	}


	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}

}
