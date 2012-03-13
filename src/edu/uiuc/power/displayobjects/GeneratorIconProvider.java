package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.jfree.util.StringUtils;

import edu.uiuc.power.dataobjects.GeneratorType;

public class GeneratorIconProvider {
	HashMap<GeneratorType,BufferedImage> genTypeToBImage = new HashMap<GeneratorType, BufferedImage>();
	public GeneratorIconProvider() {
		GeneratorType[] genTypes = GeneratorType.values();
		for (int i = 0; i < genTypes.length; i++) {
			GeneratorType gType = genTypes[i];
			String gTypeString = gType.name().toLowerCase();
			String fileName = "/applet_images/" + gTypeString + "_icon.gif";
	        InputStream is = getClass().getResourceAsStream(fileName);
	        if (is != null) {
	        	BufferedImage image;
				try {
					image = ImageIO.read(is);
			        BufferedImage transparent = createTransparentImage(image);
			        genTypeToBImage.put(gType, image);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		}
	}
	
	public Image getGeneratorIcon(GeneratorType gType, int width, int height) {
		Image img = genTypeToBImage.get(gType);
		if (img != null) {
			Image temp1 = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g2 = image.getGraphics();
			g2.setColor(new Color(0,0,0,0));
	        g2.fillRect(0,0,width,height);
			g2.drawImage(temp1, 0, 0, null);
			return image;
		} else
			return null;
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
