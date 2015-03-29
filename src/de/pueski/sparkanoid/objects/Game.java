package de.pueski.sparkanoid.objects;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Game", propOrder = {		
	"level"	
})
public class Game {
	
	private ArrayList<LevelBean> level;

	public Game() {
		level = new ArrayList<LevelBean>();
	}
	
	public Game(ArrayList<LevelBean> level) {
		this.level = level;
	}
	
	/**
	 * @return the level
	 */
	public ArrayList<LevelBean> getLevel() {
		return level;
	}
	
	/**
	 * @param level the level to set
	 */
	public void setLevel(ArrayList<LevelBean> level) {
		this.level = level;
	}

}
