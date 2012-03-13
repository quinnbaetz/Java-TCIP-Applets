package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CyberAttack implements Renderable, Animatable {
	Point2D startPoint;
	Point2D endPoint;
	LineDisplay attackVector;
	
    BufferedImage attackImage;
	
	
	double currentDistanceOnLine;
	double totalLineDistance;
	
	int numOfSteps;
	
	ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	private void notifyListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).actionPerformed(new ActionEvent(this,1,"Cyberattack Complete"));
		}
	}
	
	public CyberAttack(Point2D.Double startPoint, Point2D.Double endPoint, int numOfSteps) throws IOException {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.numOfSteps = numOfSteps;
		
        String path = "/applet_images/crossbones.png";
        InputStream is = getClass().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(is);
        attackImage = createTransparentImage(image);
		
		attackVector = new SimpleLineDisplay(startPoint,endPoint,0);
		
		totalLineDistance = DrawUtilities.getDistanceBetweenTwoPoints(startPoint, endPoint);
		currentDistanceOnLine = 0;
	}
	
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	public boolean render(Graphics2D g2d) {
		if (currentDistanceOnLine < totalLineDistance) {
			AffineTransform beforeXForm = g2d.getTransform();
			Point2D.Double pointOnLine = DrawUtilities.getPointAtDistanceOnLine(attackVector, currentDistanceOnLine);
			g2d.translate(pointOnLine.getX(), pointOnLine.getY());
			g2d.translate(-attackImage.getWidth()/2, -attackImage.getHeight()/2);
			g2d.drawImage(attackImage, new AffineTransform(1f,0f,0f,1f,0,0), null);
			g2d.setTransform(beforeXForm);
			return true;
		} else
			return false;
		
	}

	public boolean animationstep() {
		currentDistanceOnLine += attackVector.getLength()/numOfSteps/2.0;
		if (currentDistanceOnLine > totalLineDistance)
			notifyListeners();
		return false;
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
}
