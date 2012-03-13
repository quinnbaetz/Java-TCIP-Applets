package edu.uiuc.power.displayobjects.loads;

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

import edu.uiuc.power.dataobjects.LoadData;
import edu.uiuc.power.displayobjects.DrawUtilities;
import edu.uiuc.power.displayobjects.GeneratorMWControl;
import edu.uiuc.power.displayobjects.LoadMWControl;
import edu.uiuc.power.displayobjects.LoadMWLabel;
import edu.uiuc.power.displayobjects.Renderable;


public class CityDisplay implements Renderable, ImageObserver, MouseListener {

    BufferedImage transparent;
    Point2D _ULCorner;
    String _caption;
    //LoadMWLabel _loadlabel;
    LoadMWControl loadcontrol;
    LoadData _loaddata;
    BufferedImage angryface;
	double afScale = 1.0;
	double deltaPerMouseClick;
	
	public enum CityType {RESIDENTIAL, COMMERCIAL, INDUSTRIAL};
	
	public enum LabelLocation {ABOVE, BELOW};
	
	double scale = 1.0;
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	public double getScale() {
		return scale;
	}
	
	private LabelLocation getLabelLocation() {
		return LabelLocation.BELOW; 
	}
	
	public CityDisplay(Point2D ULCorner, CityType ctype,
    		String caption,
    		LoadData loadCity) throws IOException {
		this(ULCorner,ctype,caption,loadCity,50.0);
	}

    public CityDisplay(Point2D ULCorner, CityType ctype,
    		String caption,
    		LoadData loadCity,
    		double deltaPerMouseClick) throws IOException {
    	this.deltaPerMouseClick = deltaPerMouseClick;
    	_loaddata = loadCity;
    	_ULCorner = ULCorner;
        
    	String path = null;
    	switch (ctype) {
    		case COMMERCIAL:
    			path = "applet_images/commercial_city.png";
    			break;
    		case INDUSTRIAL:
    			path = "applet_images/industrial_city.png";
    			break;
    		case RESIDENTIAL:
    			path = "applet_images/residential_city.png";
    			break;
			default:
				path = "applet_images/commercial_city.png";
    	}
    	
    	InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        BufferedImage image = ImageIO.read(is);
        transparent = createTransparentImage(image);
        
        is = getClass().getClassLoader().getResourceAsStream("applet_images/face_angry.png");
        image = ImageIO.read(is);
        angryface = createTransparentImage(image);
        
        /*
		double afWidth = angryface.getWidth();
		double afHeight = angryface.getHeight();
		double tWidth = transparent.getWidth();
		double tHeight = transparent.getHeight();
		
		if ((afWidth > tWidth) | (afHeight > tHeight)) {
			if ((afWidth/tWidth) > (afHeight/tHeight)) {
				afScale = tWidth/afWidth; 
			} else {
				afScale = tHeight/afHeight;
			}
		} else {
			if ((afWidth/tWidth) > (afHeight/tHeight)) {
				afScale = tWidth/afWidth; 
			} else {
				afScale = tHeight/afHeight;
			}
		}
		*/
        afScale = DrawUtilities.getImageScaleToFitInBounds(
        		angryface.getWidth(), angryface.getHeight(), 
        		transparent.getWidth(), transparent.getHeight());
		        
        
        _caption = caption;
        //_loadlabel = new LoadMWLabel(new Point2D.Double(transparent.getWidth()/2.0,0),12,Color.BLACK,0,loadCity);
        loadcontrol = null;
    }
    
    public LoadData getLoadData() {
    	return _loaddata;
    }
    
    private String getCaption() {
    	return _caption;
    }

    public Point2D getULCorner() {
    	return _ULCorner;
    }
    
    public void setULCorner(Point2D ULCorner) {
    	_ULCorner = ULCorner;
    }
    
    /*
    public LoadMWLabel getLoadLabel() {
    	return _loadlabel;
    }
    
    public void setLoadLabel(LoadMWLabel loadlabel) {
    	_loadlabel = loadlabel;
    }
    */
    
    public LoadMWControl getLoadControl() {
    	return loadcontrol;
    }
    
    protected LoadMWControl createLoadMWControl() {
    	switch (getLabelLocation()) {
    		case ABOVE:
    	    	return new LoadMWControl(new Point2D.Double(0 + transparent.getWidth()/2,0-16),14,Color.BLACK,0,getLoadData(),deltaPerMouseClick);
    		case BELOW:
    		default:
    	    	return new LoadMWControl(new Point2D.Double(0 + transparent.getWidth()/2,0 + transparent.getHeight()),14,Color.BLACK,0,getLoadData(),deltaPerMouseClick);
    	}
    }
    
    public boolean render(Graphics2D g2d) {
    	if (loadcontrol == null) 
        	loadcontrol = createLoadMWControl();
    	AffineTransform origXForm = g2d.getTransform();
    	g2d.translate(getULCorner().getX(), getULCorner().getY());
    	g2d.scale(getScale(), getScale());
		getLoadControl().render(g2d);
    	g2d.drawImage(transparent, 0, 0, this);
    	
		Font font = g2d.getFont();
		g2d.setFont(new Font("Lucida Bright", Font.PLAIN, 12));
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();

		if (!(getLoadData().getConnected())) {
    		// Draw something over it.
    		Rectangle2D.Double blackoutRect = new Rectangle2D.Double(0,0,transparent.getWidth(),transparent.getHeight());
    		Color colorOrig = g2d.getColor();
    		g2d.setColor(new Color(0,0,0,0.5f));
    		g2d.fill(blackoutRect);
    		String blackoutText = "Blackout!";
    		Rectangle2D blackoutTextBounds = fontnow.getStringBounds(blackoutText, frc);
    		AffineTransform beforeBlackoutText = g2d.getTransform();
    		g2d.translate((transparent.getWidth()/2)-(blackoutTextBounds.getWidth()/2),transparent.getHeight()/2 + blackoutTextBounds.getHeight()/2);
    		g2d.setColor(Color.WHITE);
    		//g2d.drawString(blackoutText, 0, 0);
    		g2d.setTransform(beforeBlackoutText);
    		
    		g2d.translate(transparent.getWidth()/2 , transparent.getHeight()/2);
    		g2d.scale(afScale, afScale);
    		g2d.translate(-angryface.getWidth()/2.0, -angryface.getHeight()/2.0);
    		g2d.drawImage(angryface, 0, 0, this);
    		
    		g2d.setTransform(beforeBlackoutText);
    		
    		g2d.setColor(colorOrig);
    	}
        
		String text = getCaption();
		Rectangle2D textbounds = fontnow.getStringBounds(text, frc);
		double textwidth = textbounds.getWidth();
		AffineTransform beforeText = g2d.getTransform();
		g2d.translate(0,loadcontrol.getHeight());
		g2d.translate((transparent.getWidth()/2)-(textwidth/2),transparent.getHeight() + textbounds.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, 0);
		
		g2d.setTransform(beforeText);
	
		//g2d.translate(0,transparent.getHeight() + textbounds.getHeight());
		//getLoadLabel().render(g2d);
        g2d.setTransform(origXForm);

	
	
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

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		getLoadControl().mousePressed(arg0);		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
