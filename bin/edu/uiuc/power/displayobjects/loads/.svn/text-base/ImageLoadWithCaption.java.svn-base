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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import edu.uiuc.power.dataobjects.LoadData;

public class ImageLoadWithCaption extends LoadDisplay implements ImageObserver {
	
	private String imagePath;
    BufferedImage transparent;
    boolean imageValid;

	public ImageLoadWithCaption(LoadData loaddata, Point2D location,
			LoadDisplayAlignmentVertical vertAlign,
			LoadDisplayAlignmentHorizontal horizAlign,
			String loadDescription,
			String imagePath) {
		super(loaddata, location, vertAlign, horizAlign, loadDescription);
		this.imagePath = imagePath;
		this.imageValid = false;
	}
	
	public void setImagePath(String imagePath) {
		if (!this.imagePath.equalsIgnoreCase(imagePath)) {
			this.imagePath = imagePath;
			imageValid = false;
		}
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	private BufferedImage getImage() {
		try {
			if ((!imageValid) | (transparent == null)) {
		        InputStream is = getClass().getClassLoader().getResourceAsStream(getImagePath());
		        //System.out.println(getImagePath());
		        BufferedImage image = ImageIO.read(is);
		        transparent = createTransparentImage(image);
		        imageValid = true;
			}
		} catch (IOException ie) {
    		System.out.println("Unable to read image: " + getImagePath());
			transparent = new BufferedImage(0,0,BufferedImage.TYPE_INT_RGB); 
		}
		return transparent;
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
	
	@Override
	public boolean render(Graphics2D g2d) {
    	AffineTransform origXForm = g2d.getTransform();
    	translateToULCorner(g2d);
    	
		g2d.drawImage(getImage(), 0, 0, this);

		g2d.setTransform(origXForm);
		translateToULCorner(g2d);

		g2d.translate(0,getImage().getHeight());
		
		String text = getDescription();
		
		ArrayList<String> texts = splitString(text, true);

		AffineTransform beforeText = g2d.getTransform();
		
		Font font = g2d.getFont();

		for (int textIdx = 0; textIdx < texts.size(); textIdx++) {
			g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 14));
			text = texts.get(textIdx);
			Font fontnow = g2d.getFont();
			FontRenderContext frc = g2d.getFontRenderContext();
			Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
			double textwidth = textbounds.getWidth();
			g2d.translate(getImage().getWidth()/2-(textwidth/2),textbounds.getHeight());
			g2d.setColor(Color.BLACK);
			g2d.drawString(text, 0, 0);
			g2d.translate(-getImage().getWidth()/2+(textwidth/2),0);
		}
		
		g2d.setTransform(beforeText);
		g2d.setFont(font);
        g2d.setTransform(origXForm);
        
        return true;
	}

	private ArrayList<String> splitString(String text, boolean forceOneString) {
		ArrayList<String> retStrings = new ArrayList<String>();
		
		if (text.length() <= 10 | forceOneString) {
			retStrings.add(text);
		} else {
			// attempt to split string
			String[] splitStrings = text.split("\\s",3);
			String string1;
			String string2;
			if (splitStrings.length == 1) { 
				retStrings.add(splitStrings[0]);
			} else if (splitStrings.length == 2) {
				retStrings.add(splitStrings[0]);
				retStrings.add(splitStrings[1]);
			} else {
				if (splitStrings[0].length() + splitStrings[1].length() + 1 < splitStrings[2].length()) {
					retStrings.add(splitStrings[0] + " " + splitStrings[1]);
					retStrings.add(splitStrings[2]);
				} else {
					retStrings.add(splitStrings[0]);
					retStrings.add(splitStrings[1] + " " + splitStrings[2]);
				}
			}
			//retStrings.addAll(Arrays.asList(text.split("\\s", 2)));
		}
		
		return retStrings;
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getHeight() {
		BufferedImage image = getImage();
		return image.getHeight();
	}

	@Override
	public double getWidth() {
		BufferedImage image = getImage();
		return image.getWidth();
	}

	public boolean animationstep() {
		// TODO Auto-generated method stub
		return false;
	}

}
