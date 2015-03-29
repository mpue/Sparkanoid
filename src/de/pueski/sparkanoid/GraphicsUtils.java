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
package de.pueski.sparkanoid;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class GraphicsUtils {

	public static void drawCenteredString(String s, int w, int h, Graphics g) {

		FontMetrics fm = g.getFontMetrics();
		int x = (w - fm.stringWidth(s)) / 2;
		int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
		g.drawString(s, x, y);
	}

	public static BufferedImage rotateImage(BufferedImage inputImage, double angle) {

		int X, Y = 0;

		if ((angle == 0) || (angle == 180) || (angle == 360)) {
			X = inputImage.getWidth(null);
			Y = inputImage.getHeight(null);
		}
		else {
			X = inputImage.getHeight(null);
			Y = inputImage.getWidth(null);
		}

		BufferedImage sourceBI = new BufferedImage(X, Y, BufferedImage.TYPE_INT_RGB);

		AffineTransform at = new AffineTransform();

		// rotate around image center
		at.rotate(Math.toRadians(angle), (sourceBI.getWidth() / 2), (sourceBI.getHeight() / 2));

		/*
		 * translate to make sure the rotation doesn't cut off any image data
		 */
		AffineTransform translationTransform;
		translationTransform = findTranslation(at, sourceBI, angle);
		at.preConcatenate(translationTransform);

		Graphics2D g = (Graphics2D) sourceBI.getGraphics();
		g.setTransform(at);
		g.drawImage(inputImage, 0, 0, null);

		return sourceBI;
	}

	/*
	 * find proper translations to keep rotated image correctly displayed
	 */
	private static AffineTransform findTranslation(AffineTransform at, BufferedImage bi, double angle) {
		Point2D p2din, p2dout;
		double ytrans, xtrans = 0.0;

		AffineTransform tat = new AffineTransform();

		if (angle == 180) {
			p2din = new Point2D.Double(0, bi.getHeight());
		}
		else {
			p2din = new Point2D.Double(0.0, 0.0);
		}

		p2dout = at.transform(p2din, null);

		if (angle == 270) {
			xtrans = p2dout.getX();
			ytrans = xtrans;
		}
		else {
			ytrans = p2dout.getY();
			xtrans = ytrans;
		}

		// System.out.println("X: "+ (xtrans) +" Y: "+ (ytrans));

		tat.translate(-xtrans, -ytrans);

		return tat;
	}

	public static BufferedImage toBufferedImage(Image image, ImageObserver observer) {

		int width = image.getWidth(observer);
		int height = image.getHeight(observer);

		// Create new image
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Draw image to buffered image
		bImage.getGraphics().drawImage(image, 0, 0, observer);

		return bImage;

	}

	public static BufferedImage toBufferedImage(Image image, int width, int height) {

		// Create new image
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Draw image to buffered image
		bImage.getGraphics().drawImage(image, 0, 0, null);

		return bImage;

	}

	
	public static Image toImage(BufferedImage bufferedImage) {
		return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
	}

}
