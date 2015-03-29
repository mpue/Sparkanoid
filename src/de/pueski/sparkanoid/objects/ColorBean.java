package de.pueski.sparkanoid.objects;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Color", propOrder = { "red", "green", "blue" })
public class ColorBean {

	/**
	 * @param blue the blue to set
	 */
	public void setBlue(int blue) {
		this.blue = blue;
	}

	private int red;
	private int green;
	private int blue;

	public ColorBean() {

	}

	public ColorBean(Color color) {

		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();

	}

	public ColorBean(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
	}

	/**
	 * @return the red
	 */
	public int getRed() {
		return red;
	}

	/**
	 * @param red the red to set
	 */
	public void setRed(int red) {
		this.red = red;
	}

	/**
	 * @return the green
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * @param green the green to set
	 */
	public void setGreen(int green) {
		this.green = green;
	}

	/**
	 * @return the blue
	 */
	public int getBlue() {
		return blue;
	}

}
