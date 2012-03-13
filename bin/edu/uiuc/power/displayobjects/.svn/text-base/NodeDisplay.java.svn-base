package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.uiuc.power.dataobjects.NodeData;

public class NodeDisplay implements Renderable, MouseListener {
	private NodeData node;
	private double coordx, coordy;
	private double radius;
	private BusInset bInset;

	public boolean render(Graphics2D g2d) {
		Color backup = g2d.getColor();
		g2d.setColor(Color.BLACK);
		g2d.fill(new Ellipse2D.Double(coordx-radius,coordy-radius,radius*2,radius*2));
		g2d.setColor(backup);
		return true;
	}

	public NodeDisplay(NodeData node, double coordx, double coordy,
			double radius, BusInset bInset) {
		super();
		this.node = node;
		this.coordx = coordx;
		this.coordy = coordy;
		this.radius = radius;
		this.bInset = bInset;
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
		if ((new Point2D.Double(arg0.getX(),arg0.getY())).distance(coordx,coordy) < 5) {
			bInset.setCurrentNode(node);
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
