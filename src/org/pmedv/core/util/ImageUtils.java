package org.pmedv.core.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ImageUtils {
	
	public static BufferedImage createWaterMark(BufferedImage image, String text, int x, int y) {
		
		Graphics2D g2d = (Graphics2D)image.getGraphics();				
		g2d.drawImage(image, 0, 0, null);
		
		// Modify the image and add a watermark
		
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);
		g2d.setComposite(alpha);

        g2d.setColor(Color.black);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        
        g2d.drawString(text,x,y);

        // Free graphic resources and write image to output stream
        
        g2d.dispose();
        
        BufferedImage output = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);        
        g2d.drawImage(output,0,0,null);
        
        return output;
		
	}
	
	public static BufferedImage toBufferedImage(Image image) {

		// Label as ImageObserver
	    Label dummyObserver   = new Label();
	    
	    int width             = image.getWidth(dummyObserver);
	    int height            = image.getHeight(dummyObserver);
	    
	    // Create new image
	    BufferedImage bImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    
	    // Draw image to buffered image
	    bImage.getGraphics().drawImage(image, 0, 0, dummyObserver);
	    
	    return  bImage;
	    
	}

	public static Image toImage(BufferedImage bufferedImage) {
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }


}
