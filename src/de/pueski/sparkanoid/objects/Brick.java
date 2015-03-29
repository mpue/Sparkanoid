/**

	Sparkanoid
	Written and maintained by Matthias Pueski 
	
	Copyright (c) 2009 Matthias Pueski
	
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 */
package de.pueski.sparkanoid.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Shape;

public class Brick extends StaticBody implements ImageBody {

	private int hitCount;
	private int index;

	private static final Shape shape = new Box(Brick.WIDTH, Brick.HEIGHT);

	public final static int DEFAULT_SCORE = 1000;

	private int score = DEFAULT_SCORE;

	int colorIndex = 0;

	boolean destroyable = true;

	private static final int BLUE   = 0;
	private static final int RED    = 1;
	private static final int GREEN  = 2;
	private static final int YELLOW = 3;
	private static final int CYAN   = 4;
	private static final int ORANGE = 5;

	public static final int HEIGHT = 25;
	public static final int WIDTH = 100;

	private static final BufferedImage[] brickImages = new BufferedImage[6];

	private Color color;

	private String dropItemClass;
	
	static {

		URL u = null;

		u = Thread.currentThread().getContextClassLoader().getResource("brick_blue.png");

		try {
			brickImages[BLUE] = ImageIO.read(u);
			u = Thread.currentThread().getContextClassLoader().getResource("brick_red.png");
			brickImages[RED] = ImageIO.read(u);
			u = Thread.currentThread().getContextClassLoader().getResource("brick_green.png");
			brickImages[GREEN] = ImageIO.read(u);
			u = Thread.currentThread().getContextClassLoader().getResource("brick_yellow.png");
			brickImages[YELLOW] = ImageIO.read(u);
			u = Thread.currentThread().getContextClassLoader().getResource("brick_cyan.png");
			brickImages[CYAN] = ImageIO.read(u);
			u = Thread.currentThread().getContextClassLoader().getResource("brick_orange.png");
			brickImages[ORANGE] = ImageIO.read(u);

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public Brick() {
		this(0, 0, Color.BLUE, 1, true);
		this.score = DEFAULT_SCORE;
		this.name = "Brick";
	}

	public Brick(BrickBean brickBean) {
		this(brickBean.getXLoc(), brickBean.getYLoc(), new Color(brickBean.getColor().getRed(), brickBean.getColor().getGreen(),
				brickBean.getColor().getBlue()), brickBean.getHitCount(), brickBean.isDestroyable(), brickBean.getScore(),
				brickBean.getDropItemClass());
		this.name = "Brick";
	}

	public Brick(int x, int y, Color color) {
		this(x, y, color, 1, true);
		this.score = DEFAULT_SCORE;
		this.name = "Brick";
	}

	public Brick(int x, int y, Color color, int hitCount, boolean destroyable) {
		super(shape);
		this.position.x = x;
		this.position.y = y;
		this.hitCount = hitCount;
		this.color = color;
		this.destroyable = destroyable;
		this.score = DEFAULT_SCORE;
		this.name = "Brick";

	}

	public Brick(int x, int y, Color color, int hitCount, boolean destroyable, String dropItemClass) {
		super(shape);
		this.position.x = x;
		this.position.y = y;
		this.hitCount = hitCount;
		this.color = color;
		this.destroyable = destroyable;
		this.score = DEFAULT_SCORE;
		this.name = "Brick";
		this.dropItemClass = dropItemClass;
	}

	public Brick(int x, int y, Color color, int hitCount, boolean destroyable, int score, String dropItemClass) {
		super(shape);
		this.position.x = x;
		this.position.y = y;
		this.hitCount = hitCount;
		this.color = color;
		this.destroyable = destroyable;
		this.score = score;
		this.name = "Brick";
		this.dropItemClass = dropItemClass;
	}

	/**
	 * @return the xLoc
	 */
	public int getXLoc() {

		return Math.round(this.position.x);
	}

	/**
	 * @param loc
	 *            the xLoc to set
	 */
	public void setXLoc(int loc) {
		this.position.x = loc;
	}

	/**
	 * @return the yLoc
	 */
	public int getYLoc() {

		return Math.round(this.position.y);
	}

	/**
	 * @param loc
	 *            the yLoc to set
	 */
	public void setYLoc(int loc) {
		this.position.y = loc;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the hitCount
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 * @param hitCount
	 *            the hitCount to set
	 */
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if ((obj != null) && (obj.getClass().equals(this.getClass()))) {

			Brick brick = (Brick) obj;

			if (this.getXLoc() == brick.getXLoc() && this.getYLoc() == brick.getYLoc() && this.getIndex() == brick.getIndex())
				return true;

		}
		return false;
	}

	/**
	 * @return the destroyable
	 */
	public boolean isDestroyable() {
		return destroyable;
	}

	/**
	 * @param destroyable
	 *            the destroyable to set
	 */
	public void setDestroyable(boolean destroyable) {
		this.destroyable = destroyable;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		Brick b = new Brick(getXLoc(), getYLoc(), getColor(), getHitCount(), isDestroyable(), dropItemClass);

		return b;

	}

	/**
	 * @return the score
	 */
	public int getScore() {

		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(int score) {

		this.score = score;
	}

	/**
	 * Checks if a given point with the coordinates (x,y) is iside the bouding
	 * rectangle of this brick
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @return
	 */
	public boolean isInside(int x, int y) {
		return x >= getXLoc() && x <= getXLoc() + Brick.WIDTH && y >= getYLoc() && y <= getYLoc() + Brick.HEIGHT ? true : false;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void draw(int x, int y, Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g2.setRenderingHints(rh);

		g2.setColor(Color.WHITE);

		if (color.equals(Color.BLUE))
			colorIndex = BLUE;
		else if (color.equals(Color.RED))
			colorIndex = RED;
		else if (color.equals(Color.YELLOW))
			colorIndex = YELLOW;
		else if (color.equals(Color.GREEN))
			colorIndex = GREEN;
		else if (color.equals(Color.ORANGE))
			colorIndex = ORANGE;
		else if (color.equals(Color.CYAN))
			colorIndex = CYAN;

		
		g2.drawImage(brickImages[colorIndex], x,y, null);

	}

	/**
	 * @return the dropItemClass
	 */
	public String getDropItemClass() {
		return dropItemClass;
	}

	/**
	 * @param dropItemClass the dropItemClass to set
	 */
	public void setDropItemClass(String dropItemClass) {
		this.dropItemClass = dropItemClass;
	}

}
