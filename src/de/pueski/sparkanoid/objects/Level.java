package de.pueski.sparkanoid.objects;

import java.util.ArrayList;

public class Level {

	private ArrayList<Brick> bricks;
	private String name;
	private String songfile;
	private String backgroundImage;

	public Level() {
		this("unnamed");
	}
	
	public Level(String name) {
		
		this.name = name;
		bricks = new ArrayList<Brick>();
		
	}
	
	/**
	 * @return the bricks
	 */
	public ArrayList<Brick> getBricks() {
		return bricks;
	}
	
	/**
	 * @param bricks the bricks to set
	 */
	public void setBricks(ArrayList<Brick> bricks) {
		this.bricks = bricks;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the songfile
	 */
	public String getSongfile() {
		return songfile;
	}

	
	/**
	 * @param songfile the songfile to set
	 */
	public void setSongfile(String songfile) {
		this.songfile = songfile;
	}

	/**
	 * @return the backgroundImage
	 */
	public String getBackgroundImage() {
	
		return backgroundImage;
	}

	
	/**
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(String backgroundImage) {
	
		this.backgroundImage = backgroundImage;
	}

	
}
