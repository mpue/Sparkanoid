package de.pueski.sparkanoid.objects;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Level", propOrder = {		
	"name",	
    "bricks",
    "songfile",
    "backgroundImage"
})
public class LevelBean {

	private ArrayList<BrickBean> bricks;
	private String name;
	private String songfile;
	private String backgroundImage;

	public LevelBean() {
		this("unnamed");
	}
	
	public LevelBean(String name) {
		
		this.name = name;
		bricks = new ArrayList<BrickBean>();
		
	}
	
	/**
	 * @return the bricks
	 */
	public ArrayList<BrickBean> getBricks() {
		return bricks;
	}
	
	/**
	 * @param bricks the bricks to set
	 */
	public void setBricks(ArrayList<BrickBean> bricks) {
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
