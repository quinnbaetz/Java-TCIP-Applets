package edu.uiuc.power.displayobjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.text.NumberFormatter;

public class DataLabel implements Renderable {
	
	private Point2D ulCorner;
	private int textsize;
	private Color textcolor;
	private double rotangle;
	private AffineTransform _renderxform;
	private double height;
	private double width;
	private DecimalFormat decimalFormat;
	protected NumberFormatter textFormatter;
	DataLabelHorizontalAlignment horizAlignment;
	DataLabelVerticalAlignment vertAlignment;
	
	public enum DataLabelHorizontalAlignment {
		CENTER, LEFT, RIGHT;
	}

	public enum DataLabelVerticalAlignment {
		CENTER, TOP, BOTTOM;
	}	
	
	protected boolean drawBackground() {
		return false;
	}
	
	protected Color getBackgroundColor() {
		return Color.RED;
	}
	
	protected int getFontStyle() {
		return Font.PLAIN;
	}
	
	protected boolean drawBorder() {
		return false;
	}
	
	protected Color getBorderColor() {
		return Color.BLUE;
	}
	
	protected double getBorderThickness() {
		return 1.0;
	}
	
	public DataLabel(Point2D ULCorner, int textsize, Color textcolor,
			double rotationAngle) {
		ulCorner = ULCorner;
		this.textsize = textsize;
		rotangle = rotationAngle;
		//decimalFormat = new DecimalFormat("##0.00");
		decimalFormat = new DecimalFormat("##0");
		textFormatter = new NumberFormatter(decimalFormat);
		vertAlignment = DataLabelVerticalAlignment.TOP;
		horizAlignment = DataLabelHorizontalAlignment.LEFT;
		this.textcolor = textcolor;
	}
	
	public AffineTransform getRenderTransform() {
		return _renderxform;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getRotationAngle() {
		return rotangle;
	}
	
	public Color getTextColor() {
		return textcolor;
	}
	
	public void setTextColor(Color textcolor) {
		this.textcolor = textcolor;
	}
	
	public int getTextSize() {
		return textsize;
	}
	
	public Point2D getULCorner() {
		return ulCorner;
	}
	
	public String getCaption() {
		return "Label";
	}
	
	protected boolean getVerticalFlip() {
		return false;
	}
	
	public DataLabelHorizontalAlignment getHorizontalAlignment() {
		return horizAlignment;
	}
	
	public void setHorizontalAlignment(DataLabelHorizontalAlignment horizAlignment) {
		this.horizAlignment = horizAlignment;
	}
	
	public DataLabelVerticalAlignment getVerticalAlignment() {
		return vertAlignment;
	}
	
	public void setVerticalAlignment(DataLabelVerticalAlignment vertAlignment) {
		this.vertAlignment = vertAlignment;
	}
	
	protected boolean useTextLayoutToDetermineTextDimensions() {
		return false;
	}
	
	public boolean render(Graphics2D g2d) {
    	AffineTransform origXForm = g2d.getTransform();
    	g2d.translate(getULCorner().getX(), getULCorner().getY());
        
		String text = getCaption();
		Font origfont = g2d.getFont();
		Font thisfont = new Font("Lucida Bright", getFontStyle(), getTextSize()); 
		g2d.setFont(thisfont);
		Font fontnow = g2d.getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D textbounds = null;
		if (useTextLayoutToDetermineTextDimensions()) {
			TextLayout layout = new TextLayout(text,thisfont,frc);
			BufferedImage bi = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
			layout.draw((Graphics2D) bi.getGraphics(), 0, 0);
			textbounds = layout.getBounds();
		} else
			textbounds = fontnow.getStringBounds(text,frc);

		g2d.rotate(getRotationAngle());
		
		if (getHorizontalAlignment() == DataLabelHorizontalAlignment.CENTER)
			g2d.translate(-textbounds.getWidth()/2.0, 0);
		else if (getHorizontalAlignment() == DataLabelHorizontalAlignment.LEFT)
			g2d.translate(0,0);
		else if (getHorizontalAlignment() == DataLabelHorizontalAlignment.RIGHT)
			g2d.translate(-textbounds.getWidth(),0);
		
		if (getVerticalFlip())
			g2d.scale(1.0,-1.0); // Made this a function call because it was breaking my educational applets

		if (getVerticalAlignment() == DataLabelVerticalAlignment.TOP) 
			g2d.translate(0,textbounds.getHeight());
		else if (getVerticalAlignment() == DataLabelVerticalAlignment.BOTTOM)
			g2d.translate(0,0);
		else if (getVerticalAlignment() == DataLabelVerticalAlignment.CENTER)
			g2d.translate(0,textbounds.getHeight()/2.0);
			
		
		_renderxform = g2d.getTransform();
		height = textbounds.getHeight();
		width = textbounds.getWidth();
		
		if (drawBackground() | drawBorder()) {
			Rectangle2D backgroundShape = new Rectangle2D.Double(width - 1.1*width,-height,1.2*width,1.25*height); 
			if (drawBackground()) {
				g2d.setColor(getBackgroundColor());
				g2d.fill(backgroundShape);
			}
			if (drawBorder()) {
				g2d.setColor(getBorderColor());
				Stroke backupStroke = g2d.getStroke();
				g2d.setStroke(new BasicStroke((float)getBorderThickness()));
				g2d.draw(backgroundShape);
				g2d.setStroke(backupStroke);
			}
			
		}
		
		g2d.setColor(getTextColor());
		g2d.drawString(text, 0, 0);
		
		g2d.setFont(origfont);		
		
        g2d.setTransform(origXForm);
        return true;
	}
	
	
}
