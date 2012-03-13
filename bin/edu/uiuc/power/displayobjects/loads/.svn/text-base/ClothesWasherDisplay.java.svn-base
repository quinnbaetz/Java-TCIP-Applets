package edu.uiuc.power.displayobjects.loads;

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
import edu.uiuc.power.displayobjects.Animatable;
import edu.uiuc.power.displayobjects.Renderable;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentHorizontal;
import edu.uiuc.power.displayobjects.loads.LoadDisplay.LoadDisplayAlignmentVertical;

public class ClothesWasherDisplay extends LoadDisplay implements Animatable, ImageObserver {

	BufferedImage dryerImage;
	BufferedImage tumblerImage;
	double currentRotation;
	boolean fallingDown;
	
	
	public ClothesWasherDisplay(LoadData loaddata, Point2D location, LoadDisplayAlignmentVertical vertAlign, LoadDisplayAlignmentHorizontal horizAlign) {
		this(loaddata, location, vertAlign, horizAlign,"Clothes Washer");
	}
	
	public ClothesWasherDisplay(LoadData loaddata, Point2D location, LoadDisplayAlignmentVertical vertAlign, LoadDisplayAlignmentHorizontal horizAlign,
			String name) {
		super(loaddata, location, vertAlign, horizAlign, name);
		
		currentRotation = 0;
		fallingDown = loaddata.getConnected();
		
		ClassLoader cloader = this.getClass().getClassLoader();
		System.out.println(cloader);
		InputStream is = cloader.getResourceAsStream("applet_images/dryer.png");
		BufferedImage image;
		try {
			image = ImageIO.read(is);
			dryerImage = createTransparentImage(image);		
		} catch (IOException e) {
			image = null;
		}

        InputStream is2 = getClass().getClassLoader().getResourceAsStream("applet_images/dryer_tumbler.png");
        BufferedImage image2;
		try {
			image2 = ImageIO.read(is2);
	        tumblerImage = createTransparentImage(image2);		
		} catch (IOException e) {
			image = null;
		}
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
    	translateToULCorner(g2d);

		g2d.drawImage(dryerImage, 0, 0, this);
		
		AffineTransform beforeTumbler = g2d.getTransform();
		g2d.translate(14, 41);
		g2d.translate(tumblerImage.getWidth()/2, tumblerImage.getHeight()/2);
		g2d.rotate(currentRotation);
		g2d.translate(-tumblerImage.getWidth()/2, -tumblerImage.getHeight()/2);
		g2d.drawImage(tumblerImage,0,0,this);
		g2d.setTransform(beforeTumbler);

		String text = getDescription();
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 14));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(dryerImage.getWidth()/2-(textwidth/2),dryerImage.getHeight()+textbounds.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, 0);
		g2d.setTransform(beforeText);
		g2d.setFont(font);
        g2d.setTransform(origXForm);
        
        return true;
	}

	public boolean animationstep() {
		if (currentRotation < 0) {
			currentRotation = 0;
		}
		if (getLoadData().getConnected()) {
			currentRotation += Math.PI/20;
			if (currentRotation >= 2*Math.PI)
				currentRotation -= 2*Math.PI;
			return true;
		} else {
			if (currentRotation > 1e-4) {
				if (currentRotation > Math.PI) {
					currentRotation += Math.PI/10;
					if (currentRotation >= 2*Math.PI) {
						currentRotation -= 2*Math.PI;
					}
				} else {
					currentRotation -= Math.PI/10;
					if (currentRotation < 0) 
						currentRotation = 0;
				}
				return true;
			} else {
				currentRotation = 0;
				return false;
			}
		}
	}

	@Override
	public double getHeight() {
		return dryerImage.getHeight();
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return dryerImage.getWidth();
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

}
