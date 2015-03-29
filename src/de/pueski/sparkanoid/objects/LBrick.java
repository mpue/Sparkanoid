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

public class LBrick extends StaticBody implements ImageBody {

	private int xLoc;
	private int yLoc;
	private int hitCount;
	private int index;

	private static final Shape shape = new Box(LBrick.WIDTH, LBrick.HEIGHT);


	int colorIndex = 0;

	boolean destroyable = false;

	public static final int HEIGHT = 75;
	public static final int WIDTH = 75;

	private static BufferedImage image;

	private Color color;

	static {

		URL u = null;

		u = Thread.currentThread().getContextClassLoader().getResource("sp_brick_l.png");
		try {
			image = ImageIO.read(u);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public LBrick() {
		this(0, 0, Color.BLUE, 1, true);
		this.name = "LBrick";
	}

	public LBrick(BrickBean brickBean) {
		this(brickBean.getXLoc(), brickBean.getYLoc(), new Color(brickBean.getColor().getRed(), brickBean.getColor().getGreen(),
				brickBean.getColor().getBlue()), brickBean.getHitCount(), brickBean.isDestroyable(), brickBean.getScore());
		this.name = "LBrick";
	}

	public LBrick(int x, int y, Color color) {
		this(x, y, color, 1, true);
		this.name = "Brick";
	}

	public LBrick(int x, int y, Color color, int hitCount, boolean destroyable) {
		super(shape);
		this.xLoc = x;
		this.yLoc = y;
		this.hitCount = hitCount;
		this.color = color;
		this.destroyable = destroyable;
		this.name = "Brick";

	}

	public LBrick(int x, int y, Color color, int hitCount, boolean destroyable, int score) {
		super(shape);
		this.xLoc = x;
		this.yLoc = y;
		this.hitCount = hitCount;
		this.color = color;
		this.destroyable = destroyable;
		this.name = "Brick";
	}

	/**
	 * @return the xLoc
	 */
	public int getXLoc() {

		return xLoc;
	}

	/**
	 * @param loc
	 *            the xLoc to set
	 */
	public void setXLoc(int loc) {
		xLoc = loc;
	}

	/**
	 * @return the yLoc
	 */
	public int getYLoc() {

		return yLoc;
	}

	/**
	 * @param loc
	 *            the yLoc to set
	 */
	public void setYLoc(int loc) {
		yLoc = loc;
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

			LBrick brick = (LBrick) obj;

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

		LBrick b = new LBrick(getXLoc(), getYLoc(), getColor(), getHitCount(), isDestroyable());

		return b;

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
		return x >= getXLoc() && x <= getXLoc() + LBrick.WIDTH && y >= getYLoc() && y <= getYLoc() + LBrick.HEIGHT ? true : false;
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
		g2.drawImage(image, xLoc, yLoc, null);

	}

}
