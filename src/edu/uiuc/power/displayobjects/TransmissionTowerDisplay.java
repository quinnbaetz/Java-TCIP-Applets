package edu.uiuc.power.displayobjects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;


public class TransmissionTowerDisplay implements Renderable, ImageObserver {

    BufferedImage transparent;
    Point2D _ULCorner;
    FlowArrows FA = null;
    ArrayList<LineAndDistanceInfoProvider> linesForFA = null;
    ArrayList<SimpleLineDisplayWithDistanceInfo> lines = null;
    
    public TransmissionTowerDisplay(Point2D ULCorner) throws IOException {
    	_ULCorner = ULCorner;
        String path = "/applet_images/transmissiontower.png";
        InputStream is = getClass().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(is);
        transparent = createTransparentImage(image);
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
        g2d.drawImage(transparent, 0, 0, this);
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
