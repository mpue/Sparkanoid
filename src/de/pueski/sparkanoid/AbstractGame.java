package de.pueski.sparkanoid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.net.URL;

import net.phys2d.math.MathUtil;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.AngleJoint;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.DistanceJoint;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.Joint;
import net.phys2d.raw.SlideJoint;
import net.phys2d.raw.SpringJoint;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public abstract class AbstractGame extends Canvas {

	private static final long	serialVersionUID	= 6792271009025315961L;

	protected final World world = new World(new Vector2f(0.0f, 10.0f), 10, new QuadSpaceStrategy(20,5));
	
	protected static final int WIDTH  = 750;
	protected static final int HEIGHT = 600;

	private BufferStrategy strategy;

	protected boolean needsReset;

	protected GameState gameState;
	
	protected boolean running = true;	

	protected boolean pause = false;
	protected boolean soundMute     = false;	
	protected boolean developerMode = false;
	
	protected static final float DEFAULT_VOLUME = 1.00f;

	protected Image backgroundImage;
	
	protected float target = 1000 / 50.0f;
	protected float frameAverage = target;
	protected long  lastFrame = System.currentTimeMillis();
	protected float yield = 5000f;
	protected float damping = 0.1f;		
	protected long  renderTime = 0;
	protected long  logicTime = 0;
	
	protected Image logo;

	protected SoundSystem soundSystem;
		
	public AbstractGame() {
	}
	
	public void keyHit(char c) {
		
		if (c == 'd') {
			developerMode = !developerMode;
		}
		if (c == 'p') {			
			pause = !pause;
		}
		if (c == 'm') {			
			soundMute = !soundMute;
			toggleMusic(soundMute);
			
		}
		if (c == KeyEvent.VK_ESCAPE) {
			needsReset = true;
		}		
		if (c == 'q') {
			shutdown();
		}
		if (c == KeyEvent.VK_SPACE) {
			startGame();
		}

	}
		
	/**
	 * Starts the sound system
	 */
	private void initSoundSystem() {
		
        try {
        	
			SoundSystemConfig.addLibrary( LibraryJavaSound.class );
	        SoundSystemConfig.setCodec( "wav", CodecWav.class );
	        SoundSystemConfig.setCodec( "ogg", CodecJOgg.class ); 
	        
        }
		catch (SoundSystemException e) {
			e.printStackTrace();
		}
		
		try {
			soundSystem = new SoundSystem( LibraryJavaSound.class );
		}
		catch (SoundSystemException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Initialize the GUI 
	 */
	private void initGUI() {
		
		loadImages();

		setIgnoreRepaint(true);
		setSize(WIDTH,HEIGHT);
		setVisible(true);
		createBufferStrategy(2);		
		strategy = getBufferStrategy();
		
	}
	
	/**
	 * Start the simulation running
	 */
	public void start() {
		
		initGUI();
		init();
		
		invalidate();
		repaint();
		
		while (running) {
			
			while (pause) {
				Thread.yield();
			}
			
			// adaptive timing loop from Master Onyx
			
			long timeNow = System.currentTimeMillis();
			frameAverage = (frameAverage * 10 + (timeNow - lastFrame)) / 11;
			lastFrame = timeNow;
			
			yield += yield*((target/frameAverage)-1)*damping+0.05f;

			for(int i = 0;i < yield;i++) {
				Thread.yield();
			}

			long beforeRender = System.currentTimeMillis();
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			
			g.drawImage(backgroundImage, 0, 0, this);
			
			draw(g);
			renderGUI(g);
			
			g.dispose();
			strategy.show();
			renderTime = System.currentTimeMillis() - beforeRender;
			
			// update data model

			long beforeLogic = System.currentTimeMillis();
			
			for (int i=0;i<5;i++) {
				world.step();
			}
			
			logicTime = System.currentTimeMillis() - beforeLogic;
			
			if (needsReset) {
				soundSystem.cleanup();
				world.clear();
				init();
				needsReset = false;
				frameAverage = target;
				yield = 10000f;
			}


		}

	}
	
 
	
	/**
	 * Draw a body 
	 * 
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 */
	protected void drawBody(Graphics2D g, Body body) {
		if (body.getShape() instanceof Box) {
			drawBoxBody(g,body,(Box) body.getShape());
		}
		if (body.getShape() instanceof Circle) {
			drawCircleBody(g,body,(Circle) body.getShape());
		}
		if (body.getShape() instanceof Line) {
			drawLineBody(g,body,(Line) body.getShape());
		}
		if (body.getShape() instanceof Polygon) {
			drawPolygonBody(g,body,(Polygon) body.getShape());
		}
	}
	
	/**
	 * Draw a polygon into the world
	 * 
	 * @param g The graphics to draw the poly onto
	 * @param body The body describing the poly's position
	 * @param poly The poly to be drawn
	 */
	protected void drawPolygonBody(Graphics2D g, Body body, Polygon poly) {

		ROVector2f[] verts = poly.getVertices(body.getPosition(), body.getRotation());
		
		if (developerMode) {
		
			g.setColor(Color.WHITE);
	
			for ( int i = 0, j = verts.length-1; i < verts.length; j = i, i++ ) {			
				g.drawLine(
						(int) (0.5f + verts[i].getX()),
						(int) (0.5f + verts[i].getY()), 
						(int) (0.5f + verts[j].getX()),
						(int) (0.5f + verts[j].getY()));
			}
		}
		
		
	}

	/**
	 * Draw a line into the world
	 * 
	 * @param g The graphics to draw the line onto
	 * @param body The body describing the line's position
	 * @param line The line to be drawn
	 */
	protected void drawLineBody(Graphics2D g, Body body, Line line) {
		
		g.setColor(Color.WHITE);

		Vector2f[] verts = line.getVertices(body.getPosition(), body.getRotation());
		
		g.drawLine(
				(int) verts[0].getX(),
				(int) verts[0].getY(), 
				(int) verts[1].getX(),
				(int) verts[1].getY());
	}
	
	/**
	 * Draw a circle in the world
	 * 
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 * @param circle The shape to be drawn
	 */
	protected void drawCircleBody(Graphics2D g, Body body, Circle circle) {
		
		g.setColor(Color.WHITE);

		float x = body.getPosition().getX();
		float y = body.getPosition().getY();

		float r = circle.getRadius()+1;
		float rot = body.getRotation();
		float xo = (float) (Math.cos(rot) * r);
		float yo = (float) (Math.sin(rot) * r);
				
		if (developerMode) {			
			g.drawOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
			g.drawLine((int) x,(int) y,(int) (x+xo),(int) (y+yo));			
		}

	}
	
	/**
	 * Draw a box in the world
	 * 
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 * @param box The shape to be drawn
	 */
	protected void drawBoxBody(Graphics2D g, Body body, Box box) {
		
		Vector2f[] pts = box.getPoints(body.getPosition(), body.getRotation());
		
		Vector2f v1 = pts[0];
		Vector2f v2 = pts[1];
		Vector2f v3 = pts[2];		
		Vector2f v4 = pts[3];
		
		if (developerMode) {

			g.setColor(Color.WHITE);
			
			g.drawLine((int) v1.x,(int) v1.y,(int) v2.x,(int) v2.y);
			g.drawLine((int) v2.x,(int) v2.y,(int) v3.x,(int) v3.y);
			g.drawLine((int) v3.x,(int) v3.y,(int) v4.x,(int) v4.y);
			g.drawLine((int) v4.x,(int) v4.y,(int) v1.x,(int) v1.y);
			
		}
	
	}

	/**
	 * Draw a joint 
	 * 
	 * @param g The graphics contact on which to draw
	 * @param j The joint to be drawn
	 */
	public void drawJoint(Graphics2D g, Joint j) {
		
		if (j instanceof FixedJoint) {
			FixedJoint joint = (FixedJoint) j;
			
			g.setColor(Color.red);
			float x1 = joint.getBody1().getPosition().getX();
			float x2 = joint.getBody2().getPosition().getX();
			float y1 = joint.getBody1().getPosition().getY();
			float y2 = joint.getBody2().getPosition().getY();
			
			g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
		}
		if(j instanceof SlideJoint){
			
			SlideJoint joint = (SlideJoint) j;
			
			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();
	
			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());
	
			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1,joint.getAnchor1());
			p1.add(x1);
	
			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2,joint.getAnchor2());
			p2.add(x2);
			
			Vector2f im = new Vector2f(p2);
			im.sub(p1);
			im.normalise();
		
			g.setColor(Color.red);
			g.drawLine((int)p1.x,(int)p1.y,(int)(p1.x+im.x*joint.getMinDistance()),(int)(p1.y+im.y*joint.getMinDistance()));
			g.setColor(Color.blue);
			g.drawLine((int)(p1.x+im.x*joint.getMinDistance()),(int)(p1.y+im.y*joint.getMinDistance()),(int)(p1.x+im.x*joint.getMaxDistance()),(int)(p1.y+im.y*joint.getMaxDistance()));
		}
		if(j instanceof AngleJoint){
			AngleJoint angleJoint = (AngleJoint)j;
			Body b1 = angleJoint.getBody1();
			Body b2 = angleJoint.getBody2();
			float RA = j.getBody1().getRotation() + angleJoint.getRotateA();
			float RB = j.getBody1().getRotation() + angleJoint.getRotateB();
			
			Vector2f VA = new Vector2f((float) Math.cos(RA), (float) Math.sin(RA));
			Vector2f VB = new Vector2f((float) Math.cos(RB), (float) Math.sin(RB));
			
			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());
			
			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1,angleJoint.getAnchor1());
			p1.add(x1);
	
			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2,angleJoint.getAnchor2());
			p2.add(x2);
			
			g.setColor(Color.red);
			g.drawLine((int)p1.x,(int)p1.y,(int)(p1.x+VA.x*20),(int)(p1.y+VA.y*20));
			g.drawLine((int)p1.x,(int)p1.y,(int)(p1.x+VB.x*20),(int)(p1.y+VB.y*20));
		}
		if (j instanceof BasicJoint) {
			BasicJoint joint = (BasicJoint) j;
			
			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();
	
			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());
	
			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1,joint.getLocalAnchor1());
			p1.add(x1);
	
			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2,joint.getLocalAnchor2());
			p2.add(x2);
	
			g.setColor(Color.red);
			g.drawLine((int) x1.getX(), (int) x1.getY(), (int) p1.x, (int) p1.y);
			g.drawLine((int) p1.x, (int) p1.y, (int) x2.getX(), (int) x2.getY());
			g.drawLine((int) x2.getX(), (int) x2.getY(), (int) p2.x, (int) p2.y);
			g.drawLine((int) p2.x, (int) p2.y, (int) x1.getX(), (int) x1.getY());
		}
		if(j instanceof DistanceJoint){
			DistanceJoint joint = (DistanceJoint) j;
			
			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();
	
			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());
	
			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1,joint.getAnchor1());
			p1.add(x1);
	
			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2,joint.getAnchor2());
			p2.add(x2);
			
			g.setColor(Color.red);
			g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.x, (int) p2.y);
		}
		if (j instanceof SpringJoint) {
			SpringJoint joint = (SpringJoint) j;
			
			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();
	
			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());
	
			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1,joint.getLocalAnchor1());
			p1.add(x1);
	
			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2,joint.getLocalAnchor2());
			p2.add(x2);
			
			g.setColor(Color.red);
			g.drawLine((int) x1.getX(), (int) x1.getY(), (int) p1.x, (int) p1.y);
			g.drawLine((int) p1.x, (int) p1.y, (int) p2.getX(), (int) p2.getY());
			g.drawLine((int) p2.getX(), (int) p2.getY(), (int) x2.getX(), (int) x2.getY());
		}
	}
	
	
	/**
	 * Draw the whole simulation
	 * 
	 * @param g The graphics context on which to draw
	 */
	protected void draw(Graphics2D g) {
			
		BodyList bodies = world.getBodies();
		
		for (int i=0;i<bodies.size();i++) {
			Body body = bodies.get(i);			
			drawBody(g, body);
		}
		
	}

	/**
	 * Sets the background image of this canvas
	 * 
	 * @param imageLocation
	 */
	protected void setBackgroundImage(String imageLocation) {
		URL u = Thread.currentThread().getContextClassLoader().getResource(imageLocation);		
		this.backgroundImage = Toolkit.getDefaultToolkit().getImage(u);
	}
	
	public final void init() {
		world.clear();
		world.setGravity(0,0);		 
		initSoundSystem();		
		init(world);
	}

	/**
	 * Should be implemented by the game, add the whole stuff to the world
	 * 
	 * @param world The world in which the simulation is going to run
	 */
	protected abstract void init(World world);

	/**
	 * Renders the graphical user interface on top of everything else
	 * 
	 * @param g
	 */
	protected abstract void renderGUI(Graphics2D g);

	/**
	 * Loads the static images needed by the game
	 */
	protected abstract void loadImages();
	
	protected abstract void shutdown();

	protected abstract void toggleMusic(boolean mute);
	
	protected abstract void startGame();
}
