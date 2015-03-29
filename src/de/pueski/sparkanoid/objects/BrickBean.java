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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "Brick", propOrder = { "XLoc", "YLoc", "color", "hitCount","destroyable","score","dropItemClass" })
public class BrickBean {

	private int xLoc;
	private int yLoc;
	private ColorBean color;
	private int hitCount;
	private boolean destroyable;
	private int score;
	private String dropItemClass;
	
	public BrickBean() {
	}

	public BrickBean(Brick brick) {

		this.xLoc = brick.getXLoc();
		this.yLoc = brick.getYLoc();
		this.color = new ColorBean(brick.getColor());
		this.hitCount = brick.getHitCount();
		this.destroyable = brick.isDestroyable();
		this.score = brick.getScore();
		this.dropItemClass = brick.getDropItemClass();
		
	}
	
	public BrickBean(int x, int y, ColorBean color, int hitCount, boolean destroyable, int score) {

		this.xLoc = x;
		this.yLoc = y;
		this.color = color;
		this.hitCount = hitCount;
		this.destroyable = destroyable;
		this.score = score;
		
	}

	
	public BrickBean(int x, int y, ColorBean color, int hitCount) {

		this.xLoc = x;
		this.yLoc = y;
		this.color = color;
		this.hitCount = hitCount;
		this.destroyable = true;
		this.score = Brick.DEFAULT_SCORE;

	}

	public BrickBean(int x, int y, Color color) {

		this.xLoc = x;
		this.yLoc = y;
		this.color = new ColorBean(color);
		this.hitCount = -1;
		this.destroyable = true;
		this.score = Brick.DEFAULT_SCORE;
	}

	public BrickBean(int x, int y, Color color, int hitCount) {

		this.xLoc = x;
		this.yLoc = y;
		this.color = new ColorBean(color);
		this.hitCount = hitCount;
		this.destroyable = true;
		this.score = Brick.DEFAULT_SCORE;
	}

	
	/**
	 * @return the xLoc
	 */
	public int getXLoc() {

		return xLoc;
	}

	/**
	 * @param loc the xLoc to set
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
	 * @param loc the yLoc to set
	 */
	public void setYLoc(int loc) {

		yLoc = loc;

	}

	/**
	 * @return the color
	 */
	public ColorBean getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(ColorBean color) {
		this.color = color;
	}

	/**
	 * @return the hitCount
	 */
	public int getHitCount() {
		return hitCount;
	}

	
	/**
	 * @param hitCount the hitCount to set
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
			BrickBean brick = (BrickBean) obj;
			if (this.getXLoc() == brick.getXLoc() && this.getYLoc() == brick.getYLoc())
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
	 * @param destroyable the destroyable to set
	 */
	public void setDestroyable(boolean destroyable) {
		this.destroyable = destroyable;
	}	
	
	/**
	 * @return the score
	 */
	public int getScore() {
	
		return score;
	}

	
	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
	
		this.score = score;
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
