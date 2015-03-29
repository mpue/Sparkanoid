package de.pueski.sparkanoid;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Polygon;
import paulscode.sound.SoundSystemConfig;
import de.pueski.sparkanoid.objects.Ball;
import de.pueski.sparkanoid.objects.BonusItem;
import de.pueski.sparkanoid.objects.Brick;
import de.pueski.sparkanoid.objects.BrickBean;
import de.pueski.sparkanoid.objects.DropItem;
import de.pueski.sparkanoid.objects.EnlargeItem;
import de.pueski.sparkanoid.objects.Game;
import de.pueski.sparkanoid.objects.ImageBody;
import de.pueski.sparkanoid.objects.LevelBean;
import de.pueski.sparkanoid.objects.Paddle;

public class Sparkanoid extends AbstractGame {

	private static final long	serialVersionUID	= 2910308975498494303L;

	public Sparkanoid(String title) {
		super();
	}

	private float	panelXLoc;
	
	private final Color transBack = new Color(50,50,50,180);

	protected static final int INITIAL_BALL_Y = HEIGHT - 60;	
	protected static final int NUMBALLS = 5;
	protected static final int PADDLE_OFFSET = 50;
	
	protected int numBallsLeft = NUMBALLS;
	protected int score;	
	protected int numDestroyableBricks = 0;	
	
	protected Game game;
	protected LevelBean currentLevel;
	
	protected int levelIndex = 0;
	
	private boolean customGame = false;
	
	protected final List<Brick>	bricks	= Collections.synchronizedList(new ArrayList<Brick>());

	protected Ball				ball;
	protected Paddle			paddle;
	
	private final Font smallFont = new Font("Sansserif", Font.PLAIN, 12);
	private final Font bigFont   = new Font("Sansserif", Font.BOLD, 24);

	private final URL zapURL      = getClass().getClassLoader().getResource("zap.wav");
	private final URL blipURL     = getClass().getClassLoader().getResource("blip.wav");
	private final URL itemURL     = getClass().getClassLoader().getResource("item.wav");
	private final URL registerURL = getClass().getClassLoader().getResource("register.wav");
	
	public Sparkanoid(Game game , String title) {
		customGame = true;		
		loadGame(game);
	}
	
	protected void init(final World world) {

		gameState = GameState.WELCOMESCREEN;

		if (!customGame)		
			loadGame("game.xml");	
		
		createBall();
		createPaddle();
		createBorders();
		hookListeners();

		levelIndex = 0;
		currentLevel = game.getLevel().get(levelIndex);

		if (currentLevel.getBackgroundImage() != null)
			setBackgroundImage(currentLevel.getBackgroundImage());
		else
			setBackgroundImage("room.png");
		
		loadBricks(currentLevel);
		calculateDestroyableBricks();

		if (soundSystem != null)		
			playLevelMusic();
				
	}
	
	private void dropEnlargeItem(Body body) {
		
		EnlargeItem ei = new EnlargeItem("EnlargeItem",new Box(100,25),15f);
		
		ei.setPosition(body.getPosition().getX(), body.getPosition().getY()+30);		
		ei.setRestitution(1.0f);
		
		for (int i = 0; i < world.getBodies().size();i++) {
			
			if (!world.getBodies().get(i).getName().startsWith("Paddle")) {
				ei.addExcludedBody(world.getBodies().get(i));
			}
			
		}
		
		world.add(ei);
		
	}

	private void dropBonusItem(Body body) {
		
		BonusItem bi = new BonusItem("BonusItem",new Box(100,25),15f);
		bi.setScore(500);
		bi.setPosition(body.getPosition().getX(), body.getPosition().getY()+30);		
		bi.setRestitution(1.0f);
		
		for (int i = 0; i < world.getBodies().size();i++) {
			
			if (!world.getBodies().get(i).getName().startsWith("Paddle")) {
				bi.addExcludedBody(world.getBodies().get(i));
			}
			
		}
		
		world.add(bi);
		
	}
	
	
	private void hookListeners() {

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {

				if (e.getX() < paddle.getWidth() / 2)
					panelXLoc = paddle.getWidth() / 2;
				else if (e.getX() + paddle.getWidth() / 2 > WIDTH)
					panelXLoc = WIDTH - paddle.getWidth() / 2;
				else
					panelXLoc = e.getX();

				paddle.setPosition(panelXLoc - paddle.getWidth() / 2, paddle.getPosition().getY());

				if (!gameState.equals(GameState.RUNNING)) {
					ball.setPosition(panelXLoc, INITIAL_BALL_Y);
				}

			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (gameState.equals(GameState.WON)) {
					score = 0;
					gameState = GameState.WELCOMESCREEN;
				}
				else if (gameState.equals(GameState.GAME_OVER)) {
					score = 0;
					gameState = GameState.WELCOMESCREEN;
				}
				else if (gameState.equals(GameState.WELCOMESCREEN)) {
					gameState = GameState.LEVEL_START;
				}
				else if (gameState.equals(GameState.LEVEL_START)) {

					startGame();

				}
				
			}
			
		});

		world.addListener(new CollisionListener() {

			@Override
			public void collisionOccured(CollisionEvent event) {

				if (event.getBodyA().getName().equals("leftBorder")  ||
					event.getBodyB().getName().equals("leftBorder")  ||
				    event.getBodyA().getName().equals("rightBorder") || 
				    event.getBodyB().getName().equals("rightBorder") || 
				    event.getBodyA().getName().equals("topBorder")  ||
					event.getBodyB().getName().equals("topBorder")) {
					playBlipSound();
				}
				else if (event.getBodyA().getName().equals("Brick")) {

					playBounceSound();
					
					Brick brick = (Brick) event.getBodyA().getUserData();
					
					if (brick.isDestroyable()) {
						
						if (brick.getHitCount() > 1) {
							brick.setHitCount(brick.getHitCount() - 1);
						}
						else {
							world.remove(event.getBodyA());
							numDestroyableBricks--;
							score = score + brick.getScore();
							
							if (brick.getDropItemClass() != null) {
							
								if (brick.getDropItemClass().equals(EnlargeItem.class.getName())) {
									dropEnlargeItem(event.getBodyA());
								}
								else if (brick.getDropItemClass().equals(BonusItem.class.getName())) {
									dropBonusItem(event.getBodyA());
								}
								
							}

						}
						
					}

				}
				else if (event.getBodyB().getName().equals("Brick")) {

					playBounceSound();
					
					Brick brick = (Brick) event.getBodyB().getUserData();
					
					if (brick.isDestroyable()) {
						
						if (brick.getHitCount() > 1) {
							brick.setHitCount(brick.getHitCount() - 1);
						}
						else {
							world.remove(event.getBodyB());
							numDestroyableBricks--;
							score = score + brick.getScore();
							
							if (brick.getDropItemClass() != null) {

								if (brick.getDropItemClass().equals(EnlargeItem.class.getName())) {
									dropEnlargeItem(event.getBodyA());
								}
								else if (brick.getDropItemClass().equals(BonusItem.class.getName())) {
									dropBonusItem(event.getBodyA());
								}
								
							}

						}
						
					}

				}
				else if (event.getBodyA().getName().equals("bottomBorder") && event.getBodyB().getName().equals("Ball")) {

					if (numBallsLeft > 0) {
						numBallsLeft--;
						gameState = GameState.LEVEL_START;
					}
					else {
						numBallsLeft = NUMBALLS;
						loadBricks(currentLevel);
						gameState = GameState.GAME_OVER;
					}

					world.remove(ball);
					createBall();
					resetPaddleAndBall();

				}
				else if (event.getBodyB().getName().equals("bottomBorder") && event.getBodyA().getName().equals("Ball")) {

					if (numBallsLeft > 0) {
						numBallsLeft--;
						gameState = GameState.LEVEL_START;
					}
					else {
						numBallsLeft = NUMBALLS;
						loadBricks(currentLevel);
						gameState = GameState.GAME_OVER;
					}

					world.remove(ball);
					createBall();
					resetPaddleAndBall();

				}
				else if (event.getBodyA().getName().equals("Paddle") && event.getBodyB() instanceof DropItem) {

					if (event.getBodyB() instanceof BonusItem) {						
						BonusItem item = (BonusItem) event.getBodyB();						
						score +=  item.getScore();
						playBonusSound();
					}
					else if (event.getBodyB() instanceof EnlargeItem) {
						System.out.println("nlarge");
						world.remove(paddle);						
						createBigPaddle(paddle.getXLoc());
						playItemSound();
					}
					else {
						playItemSound();	
					}
					
					world.remove(event.getBodyB());					
				}
				else if (event.getBodyB().getName().equals("Paddle") &&  event.getBodyA() instanceof DropItem) {
					
					
					if (event.getBodyA() instanceof BonusItem) {						
						BonusItem item = (BonusItem) event.getBodyA();						
						score +=  item.getScore();
						playBonusSound();
					}
					else if (event.getBodyA() instanceof EnlargeItem) {
						System.out.println("nlarge");
						world.remove(paddle);
						createBigPaddle(paddle.getXLoc());
						playItemSound();
					}
					else {
						playItemSound();
					}
					
					world.remove(event.getBodyA());
				}
				else if (event.getBodyA().getName().equals("bottomBorder") && event.getBodyB() instanceof DropItem) {
					world.remove(event.getBodyB());
				}
				else if (event.getBodyB().getName().equals("bottomBorder") &&  event.getBodyA() instanceof DropItem) {
					world.remove(event.getBodyA());
				}

				
				if (event.getBodyA().getName().equals("Paddle") || event.getBodyB().getName().equals("Paddle") ) {
					playBlipSound();
				}
				
				

				if (numDestroyableBricks == 0) {													
					nextLevel();
				}

			}

		});
	}
	
	public void playLevelMusic() {

		Thread audioThread = new Thread(new Runnable() {

			@Override
			public void run() {

				if (currentLevel.getSongfile() != null) {
				
					URL myURL = soundSystem.getClass().getClassLoader().getResource(currentLevel.getSongfile());
	
					soundSystem.newStreamingSource(true, "OGG Music", myURL, currentLevel.getSongfile(), true, 0, 0, 0,
							SoundSystemConfig.ATTENUATION_NONE, 0);
					soundSystem.setVolume("OGG Music", DEFAULT_VOLUME);
					
					soundSystem.play("OGG Music");
	
					while (gameState.equals(GameState.RUNNING)) {
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {
						}
					}
				}

			}
			
		});
		
		if (soundSystem != null)		
			audioThread.start();

		
	}

	protected void createPaddle() {

		Vector2f v1 = new Vector2f(0.0f, 15.0f);
		Vector2f v2 = new Vector2f(10.0f, 5.0f);
		Vector2f v3 = new Vector2f(40.0f, 0.0f);
		Vector2f v4 = new Vector2f(60.0f, 0.0f);
		Vector2f v5 = new Vector2f(90.0f, 5.0f);
		Vector2f v6 = new Vector2f(100.0f, 15.0f);
		Vector2f v7 = new Vector2f(100.0f, 50.0f);		
		Vector2f v8 = new Vector2f(0.0f, 50.0f);

		Vector2f[] vertices = new Vector2f[8];

		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		vertices[3] = v4;
		vertices[4] = v5;
		vertices[5] = v6;
		vertices[6] = v7;
		vertices[7] = v8;

		ConvexPolygon p1 = new ConvexPolygon(vertices);

		paddle = new Paddle("Paddle", p1, 100.0f, 25.0f);
		
		BufferedImage paddleImage;
		
		URL u = Thread.currentThread().getContextClassLoader().getResource("paddle.png");		
		
		try {
			paddleImage = ImageIO.read(u);
			paddle.setImage(paddleImage);
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
				
		paddle.setPosition(0, HEIGHT - PADDLE_OFFSET);
		paddle.setRestitution(1.1f);

		world.add(paddle);
	}
	
	protected void createSmallPaddle() {

		Vector2f v1 = new Vector2f(0.0f, 15.0f);
		Vector2f v2 = new Vector2f(7.5f, 5.0f);
		Vector2f v3 = new Vector2f(30.0f, 0.0f);
		Vector2f v4 = new Vector2f(45.0f, 0.0f);
		Vector2f v5 = new Vector2f(67.5f, 5.0f);
		Vector2f v6 = new Vector2f(100.0f, 15.0f);
		Vector2f v7 = new Vector2f(100.0f, 50.0f);		
		Vector2f v8 = new Vector2f(0.0f, 50.0f);

		Vector2f[] vertices = new Vector2f[8];

		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		vertices[3] = v4;
		vertices[4] = v5;
		vertices[5] = v6;
		vertices[6] = v7;
		vertices[7] = v8;

		ConvexPolygon p1 = new ConvexPolygon(vertices);

		paddle = new Paddle("Paddle", p1, 75f,25f);
		
		BufferedImage paddleImage;
		
		URL u = Thread.currentThread().getContextClassLoader().getResource("paddle_small.png");		
		
		try {
			paddleImage = ImageIO.read(u);
			paddle.setImage(paddleImage);
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
				
		paddle.setPosition(0, HEIGHT - PADDLE_OFFSET);
		paddle.setRestitution(1.1f);

		world.add(paddle);
	}	
	
	protected void createBigPaddle(int xLoc) {

		Vector2f v1 = new Vector2f(0.0f, 15.0f);
		Vector2f v2 = new Vector2f(10.0f, 5.0f);
		Vector2f v3 = new Vector2f(40.0f, 0.0f);
		Vector2f v4 = new Vector2f(100.0f, 0.0f);
		Vector2f v5 = new Vector2f(140.0f, 5.0f);
		Vector2f v6 = new Vector2f(150.0f, 15.0f);
		Vector2f v7 = new Vector2f(150.0f, 50.0f);		
		Vector2f v8 = new Vector2f(0.0f, 50.0f);

		Vector2f[] vertices = new Vector2f[8];

		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;
		vertices[3] = v4;
		vertices[4] = v5;
		vertices[5] = v6;
		vertices[6] = v7;
		vertices[7] = v8;

		ConvexPolygon p1 = new ConvexPolygon(vertices);

		paddle = new Paddle("Paddle", p1,10f,25f);
		
		BufferedImage paddleImage;
		
		URL u = Thread.currentThread().getContextClassLoader().getResource("paddle_big.png");		
		
		try {
			paddleImage = ImageIO.read(u);
			paddle.setImage(paddleImage);
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
				
		paddle.setPosition(xLoc, HEIGHT - PADDLE_OFFSET);
		paddle.setRestitution(1.1f);

		world.add(paddle);
	}	

	protected void createBorders() {

		final Body leftBorder = new StaticBody("leftBorder", new Box(5, HEIGHT));
		leftBorder.setPosition(5, HEIGHT / 2);
		leftBorder.setRestitution(0.7f);
		leftBorder.setDamping(2.0f);
		world.add(leftBorder);

		final Body rightBorder = new StaticBody("rightBorder", new Box(5, HEIGHT));
		rightBorder.setPosition(WIDTH - 5, HEIGHT / 2 + 5);
		rightBorder.setRestitution(0.7f);
		world.add(rightBorder);

		final Body topBorder = new StaticBody("topBorder", new Box(WIDTH, 5));
		topBorder.setPosition(WIDTH / 2, 30.0f);
		topBorder.setRestitution(0.5f);
		world.add(topBorder);

		final Body bottomBorder = new StaticBody("bottomBorder", new Box(WIDTH, 5));
		bottomBorder.setPosition(WIDTH / 2, HEIGHT - 5);
		bottomBorder.setRestitution(0.7f);
		world.add(bottomBorder);
		
	}
	
	protected void createBall() {

		ball = new Ball();
		ball.setPosition(WIDTH / 2, INITIAL_BALL_Y);
		ball.setRestitution(1.3f);
		
		URL u = Thread.currentThread().getContextClassLoader().getResource("ball.png");
		
		try {
			BufferedImage ballImage = ImageIO.read(u);
			ball.setImage(ballImage);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		
		world.add(ball);

	}

	protected void resetPaddleAndBall() {

		ball.setForce(0, 0);
		world.setGravity(0, 0);
		ball.setPosition(WIDTH / 2, INITIAL_BALL_Y);
		paddle.setPosition(WIDTH / 2 - 50, HEIGHT - 50);

	}

	protected void calculateDestroyableBricks() {
		
		for (int i = 0; i < world.getBodies().size();i++) {
			
			if (world.getBodies().get(i).getUserData() instanceof Brick) {
				
				Brick brick = (Brick) world.getBodies().get(i).getUserData();
				
				if (brick.isDestroyable()) {
					numDestroyableBricks++;
				}
				
			}
			
			
		}
		
	}

	protected void loadBricks(LevelBean currLevel) {
		
		System.out.println("Loading bricks for level "+currLevel.getName());

		try {
			
			LevelBean level = null;
			
			if (!customGame) {
			
				Unmarshaller u = (Unmarshaller) JAXBContext.newInstance(LevelBean.class).createUnmarshaller();
				InputStream is = getClass().getClassLoader().getResourceAsStream(currLevel.getName());
				level = (LevelBean) u.unmarshal(is);
			}
			else {
				level = game.getLevel().get(0);
			}

			for (BrickBean b : level.getBricks()) {

				Body brick = new Brick(b);
				brick.setPosition(b.getXLoc() + 50, b.getYLoc()+15);
				brick.setRotation(0.02f);
				brick.setRestitution(0.5f);
				brick.setUserData(new Brick(b));

				world.add(brick);
			}
			
		}
		catch (JAXBException e1) {
			e1.printStackTrace();
		}

	}
	
	protected void nextLevel() {
		
		gameState = GameState.LEVEL_START;
		
		if (levelIndex < game.getLevel().size() - 1) {

			if (soundSystem != null)
				soundSystem.stop("OGG Music");
			
			levelIndex++;
			gameState = GameState.LEVEL_START;
			
			playLevelMusic();
		}
		
		else { // all levels passed
			
			gameState = GameState.WON;					
			levelIndex = 0;			
		}

		currentLevel = game.getLevel().get(levelIndex);					

		if (currentLevel.getBackgroundImage() != null)
			setBackgroundImage(currentLevel.getBackgroundImage());
		else
			setBackgroundImage("room.png");
		
		world.clear();
		createBorders();
		createBall();
		createPaddle();
		resetPaddleAndBall();
		
		numBallsLeft = NUMBALLS;					
		
		loadBricks(currentLevel);
		calculateDestroyableBricks();
		
	}	
	
	
	public void renderGUI(Graphics2D g) {

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(rh);
		
		g2.setFont(bigFont);
		g2.setColor(Color.WHITE);
		
		if (pause) {

			g.setColor(transBack);			
			g.fillRect(0,0,WIDTH, HEIGHT);
			
			g.setColor(Color.WHITE);
			
			String message = "Paused";			
			GraphicsUtils.drawCenteredString(message, WIDTH, HEIGHT, g);
			
		}
		
		String scoreString = "Score : "+score;
		String ballString =  "Balls : "+numBallsLeft;
		
		g2.drawString(scoreString, 10, HEIGHT-25);
		g2.drawString(ballString , WIDTH-110, HEIGHT-25);
		
		if (gameState.equals(GameState.WELCOMESCREEN)) {
			
			g2.setColor(transBack);			
			g2.fillRect(0,0,WIDTH, HEIGHT);
			
			g2.setColor(Color.WHITE);
			
			String message = "Hit space or click mouse to start!";			
			GraphicsUtils.drawCenteredString(message, WIDTH, HEIGHT, g);
			
			g2.drawImage(logo,WIDTH/2-180, HEIGHT/2-100, this);
		}
		else if (gameState.equals(GameState.WON)) {
			
			g2.setColor(transBack);			
			g2.fillRect(0,0,WIDTH, HEIGHT);
			
			g2.setColor(Color.WHITE);
			
			String message = "You won!"; 
			String scoreMessage = "Your score is "+score+".";
			
			GraphicsUtils.drawCenteredString(message, WIDTH, HEIGHT-50, g2);
			GraphicsUtils.drawCenteredString(scoreMessage, WIDTH, HEIGHT, g2);
			
		}

		else if (gameState.equals(GameState.GAME_OVER)) {
			
			g2.setColor(transBack);			
			g2.fillRect(0,0,WIDTH, HEIGHT);			
			g2.setColor(Color.WHITE);
			
			String message = "Game Over!";
			g2.drawString(message, WIDTH/2-60, HEIGHT/2);

		}		

		g2.setColor(Color.WHITE);

		if (developerMode) {
			
			g2.setFont(smallFont);
			
			g2.drawString("FAv: "+frameAverage,10,50);
			g2.drawString("FPS: "+(int) (1000 / frameAverage),10,70);
			g2.drawString("Yield: "+yield,10,90);
			g2.drawString("Arbiters: "+world.getArbiters().size(),10,110);
			g2.drawString("Bodies: "+world.getBodies().size(),10,130);
			g2.drawString("R: "+renderTime,10,150);
			g2.drawString("L: "+logicTime,10,170);
			g2.drawString("Energy: "+world.getTotalEnergy(),10,190);				
			g2.drawString("State : "+gameState,10,210);
			g2.drawString("Num balls : "+numBallsLeft,10,230);
			g2.drawString("Bricks left : "+numDestroyableBricks,10,250);
		}
		
	}
	
	@Override
	protected void drawCircleBody(Graphics2D g, Body body, Circle circle) {

		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		
		if (body instanceof ImageBody) {
			
			ImageBody ib = (ImageBody)body;			
			ib.draw(Math.round(x-10.0f), Math.round(y-10.0f), g);
			
		}
		
		super.drawCircleBody(g, body, circle);
	}
	
	@Override
	protected void drawBoxBody(Graphics2D g, Body body, Box box) {

		Vector2f[] pts = box.getPoints(body.getPosition(), body.getRotation());
		
		Vector2f v1 = pts[0];
		
		try{
			ImageBody ib = (ImageBody)body;
			ib.draw(Math.round(v1.x), Math.round(v1.y), g);			
		}
		catch (ClassCastException c) {
			
			
		}
		super.drawBoxBody(g, body, box);
	}
	
	@Override
	protected void drawPolygonBody(Graphics2D g, Body body, Polygon poly) {
	
		ROVector2f[] verts = poly.getVertices(body.getPosition(), body.getRotation());

		try{
			ImageBody ib = (ImageBody)body;
			ib.draw(Math.round(verts[0].getX()), Math.round(verts[0].getY()-12), g);		
		}
		catch (ClassCastException c) {
			
		}
		
		super.drawPolygonBody(g, body, poly);
	}
	
	public void loadImages()  {
		
		URL logoURL = Thread.currentThread().getContextClassLoader().getResource("sparklogo.png");
		logo = Toolkit.getDefaultToolkit().getImage(logoURL);
		
	}
	
	protected void loadGame(String name) {
		
		try {
			Unmarshaller u = (Unmarshaller)JAXBContext.newInstance(Game.class).createUnmarshaller();			
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);			
			game = (Game)u.unmarshal(is); 			
			currentLevel = game.getLevel().get(levelIndex);
			
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

	protected void loadGame(Game game) {
		
		this.game = game; 			
		currentLevel = game.getLevel().get(levelIndex);
			
	}

	
    protected void playBounceSound() {
    	
    	if (soundSystem != null)
    	
	    	soundSystem.quickPlay( false, zapURL, "zap.wav",false, 0, 0, 0,
	                SoundSystemConfig.ATTENUATION_ROLLOFF,
	                SoundSystemConfig.getDefaultRolloff() );
    	
	}
	
    protected void playBlipSound() {
    	
    	if (soundSystem != null)
    	
	    	soundSystem.quickPlay( false, blipURL, "blip.wav",false, 0, 0, 0,
	                SoundSystemConfig.ATTENUATION_ROLLOFF,
	                SoundSystemConfig.getDefaultRolloff() );
    	
	}

    protected void playItemSound() {
    	
    	if (soundSystem != null)
    	
	    	soundSystem.quickPlay( false, itemURL, "item.wav",false, 0, 0, 0,
	                SoundSystemConfig.ATTENUATION_ROLLOFF,
	                SoundSystemConfig.getDefaultRolloff() );
    	
	}

    protected void playBonusSound() {
    	
    	if (soundSystem != null)
    	
	    	soundSystem.quickPlay( false, registerURL, "register.wav",false, 0, 0, 0,
	                SoundSystemConfig.ATTENUATION_ROLLOFF,
	                SoundSystemConfig.getDefaultRolloff() );
    	
	}
    
    public void stop() {    	
    	running = false;
    	soundSystem.cleanup();
    	
    }
    
    @Override
    protected void shutdown() {
    	stop();
    	System.exit(0);
    	
    }
    
    public static BufferedImage rotateImage(BufferedImage inputImage, double angle) {

		int x, y = 0;

		if ((angle == 0) || (angle == 180) || (angle == 360)) {
			x = inputImage.getWidth(null);
			y = inputImage.getHeight(null);
		} else {
			x = inputImage.getHeight(null);
			y = inputImage.getWidth(null);
		}

		AffineTransform at = new AffineTransform();

		// rotate around image center
		at.rotate(Math.toRadians(angle), (inputImage.getWidth() / 2), (inputImage.getHeight() / 2));

		BufferedImageOp bio;
		bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		return bio.filter(inputImage, null);
	}

	@Override
	protected void toggleMusic(boolean mute) {
		
		if (mute)
			soundSystem.setVolume("OGG Music", 0.0f);
		else
			soundSystem.setVolume("OGG Music", 1.0f);
		
	}

	@Override
	protected void startGame() {
		
		if (gameState.equals(GameState.RUNNING))
			return;
		
		gameState = GameState.RUNNING;
		world.setGravity(0, 10);
		ball.setForce(0, -10000);		
	}


}
