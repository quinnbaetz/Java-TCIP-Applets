package edu.uiuc.power.displayobjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JComboBox;

public class AnimatedPanel extends JPanel implements Runnable {

	private ArrayList<Renderable> _middlelayer;

	private ArrayList<Renderable> _toplayer;
	
	private ArrayList<Renderable> _bottomlayer;

	private ArrayList<Animatable> _animatables;

	private volatile boolean running = false; // used to stop the animation thread

	private volatile boolean isPaused = false;

	private long period;

	private int noDelaysPerYield;

	private int _desiredWidth, _desiredHeight;

	private Thread animator;

	private boolean _antiAliasEnabled;

	private static int MAX_FRAME_SKIPS = 5;
	
	private boolean doPaintScreen;

	private boolean getAntiAliasEnabled() {
		return _antiAliasEnabled;
	}
	
	private void setPaintScreen(boolean value) {
		doPaintScreen = value;
	}
	
	private boolean getPaintScreen() {
		return doPaintScreen;
	}

	private void setAntiAliasEnabled(boolean antiAliasEnabled) {
		_antiAliasEnabled = antiAliasEnabled;
	}

	public AnimatedPanel(int desiredWidth, int desiredHeight, long period,
			int noDelaysPerYield, boolean antiAliasOn) {
		_desiredWidth = desiredWidth;
		_desiredHeight = desiredHeight;
		this.period = period;
		running = false;
		this.noDelaysPerYield = noDelaysPerYield;
		_antiAliasEnabled = antiAliasOn;
		
		doPaintScreen = false;

		_middlelayer = new ArrayList<Renderable>();
		_toplayer = new ArrayList<Renderable>();
		_bottomlayer = new ArrayList<Renderable>();
		_animatables = new ArrayList<Animatable>();

	}

	public void addNotify() {
		super.addNotify();
		setPaintScreen(true);
		startGame();
	}

	public void startGame() {
		if ((animator == null) || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	public ArrayList<Renderable> getMiddleLayerRenderables() {
		return _middlelayer;
	}

	public ArrayList<Renderable> getTopLayerRenderables() {
		return _toplayer;
	}
	
	public ArrayList<Renderable> getBottomLayerRenderables() {
		return _bottomlayer;
	}

	public ArrayList<Animatable> getAnimatables() {
		return _animatables;
	}

	public void run()
	/* The frames of the animation are drawn inside the while loop. */
	{
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		Graphics g;

		beforeTime = System.nanoTime();

		running = true;

		while (running) {
			panelAnimation();
			if (getPaintScreen()) {
				panelRender(); // render the game to a buffer
				paintScreen(); // draw the buffer on-screen
			}

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= noDelaysPerYield) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				panelAnimation(); // update state but don't render
				skips++;
			}
		}

	} // end of run()

	private void panelAnimation() {
		ArrayList<Animatable> animatables = getAnimatables();
		for (int i = 0; i < animatables.size(); i++) {
			animatables.get(i).animationstep();
		}
	}

	protected void panelRender() {
		if (dbImage == null) {
			dbImage = createImage(getDesiredWidth(), getDesiredHeight());
			if (dbImage == null) {
				System.out.println("dbimage is null");
				return;
			} else {
				dbg = dbImage.getGraphics();
			}
		}


		if (getAntiAliasEnabled())
			((Graphics2D) dbg).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		else
			((Graphics2D) dbg).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);

		ArrayList<Renderable> bottomLayerRenderables = getBottomLayerRenderables();
		ArrayList<Renderable> middleLayerRenderables = getMiddleLayerRenderables();
		ArrayList<Renderable> topLayerRenderables = getTopLayerRenderables();

		Graphics2D dbg2 = (Graphics2D) dbg;
		dbg2.setTransform(new AffineTransform());
		// clear the background
		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, getDesiredWidth(), getDesiredHeight());
		performInitialTransform(dbg2);

		for (int i = 0; i < bottomLayerRenderables.size(); i++)
			bottomLayerRenderables.get(i).render((Graphics2D) dbg);

		for (int i = 0; i < middleLayerRenderables.size(); i++)
			middleLayerRenderables.get(i).render((Graphics2D) dbg);

		for (int i = 0; i < topLayerRenderables.size(); i++)
			topLayerRenderables.get(i).render((Graphics2D) dbg);
	}

	protected void performInitialTransform(Graphics2D g2d) {
	}

	private int getDesiredWidth() {
		return _desiredWidth;
	}

	private int getDesiredHeight() {
		return _desiredHeight;
	}

	protected Graphics dbg;

	protected Image dbImage = null;

	private void paintScreen()
	// use active rendering to put the buffered image on-screen
	{
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		} catch (Exception e) // quite commonly seen at applet destruction
		{
			System.out.println("Graphics error: " + e);
		}
	} // end of paintScreen()
	  

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
	}
}
