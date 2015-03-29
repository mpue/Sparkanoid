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

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Shape;

public class Paddle extends StaticBody implements ImageBody {

	private BufferedImage	image;

	private float			width;
	private float			height;

	public Paddle(Shape shape, float width, float height) {
		super(shape);
		this.width = width;
		this.height = height;
	}

	public Paddle(String name, Shape shape, float width, float height) {
		super(name, shape);
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(int x, int y, Graphics g) {
		g.drawImage(image, x, y, null);
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {

		this.image = image;
	}

	@Override
	public int getXLoc() {

		return Math.round(this.position.x);
	}

	@Override
	public int getYLoc() {

		return Math.round(this.position.y);
	}

	/**
	 * @return the width
	 */
	public float getWidth() {

		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {

		this.width = width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {

		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {

		this.height = height;
	}

}
