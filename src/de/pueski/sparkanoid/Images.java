package de.pueski.sparkanoid;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Matthias Pueski (18.08.2010)
 *
 */

public class Images {
	
	public static BufferedImage growPaddleImage;
	public static BufferedImage bonusItemImage;
	
	static {
	
		try {
			growPaddleImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("item_paddle_grow.png"));
			bonusItemImage  = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("item_bonus.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}
