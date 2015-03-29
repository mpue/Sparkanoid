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
package org.pmedv.leveleditor;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.pmedv.core.context.AppContext;
import org.pmedv.leveleditor.beans.RecentFileList;

import de.pueski.sparkanoid.objects.Brick;
import de.pueski.sparkanoid.objects.BrickBean;
import de.pueski.sparkanoid.objects.Level;
import de.pueski.sparkanoid.objects.LevelBean;

public class SparkyUtil {

	/**
	 * Checks if one rectangle collides another diregarding their rotation,
	 * in fact we assume, that they both have a rotation of 0.
	 * 
	 * @param r1 
	 * @param r2
	 * 
	 * @return true if the two guys collide
	 */
	public static boolean collides(Rectangle r1, Rectangle r2) {
		
		if ((r1.getY() +  r1.getHeight()  >= r2.getY() &&           // r1 bottom collides top of the r2
			 r1.getY() <  r2.getY() && 	
			 r1.getX() +  r1.getWidth()  >= r2.getX() && // r1 is between the left and the right 
			 r1.getX() <= r2.getX()+  r2.getWidth()) ) {
			 return true;
		}
		else if ((r1.getY() <= r2.getY() +  r2.getHeight()    && // r1 top collides bottom of the r2
			 r1.getY() +  r1.getHeight() > r2.getY() && 	
			 r1.getX() +  r1.getWidth()   >= r2.getX() && // r1 is between the left and the right 
			 r1.getX() <= r2.getX() +  r2.getWidth())) {
			return true;	
		}
		else if ((r1.getX() +  r1.getWidth()  >= r2.getX() && // r1 collides left side of the r2
			r1.getX() <  r2.getX() && 	
			r1.getY() +  r1.getHeight()  >= r2.getY() && // r1 is between the top and bottom of the r2 
			r1.getY() <= r2.getY()+  r2.getHeight()) ) {
			return true;
		}
		else if ((r1.getX() <= r2.getX() +  r2.getWidth()    && // r1 collides right side of the r2
			r1.getX() +  r1.getWidth() > r2.getX() && 	
			r1.getY() +  r1.getHeight()   >= r2.getY() && // r1 is between the top and bottom of the r2  
			r1.getY() <= r2.getY() +  r2.getHeight())) {
			return true;	
		}
		
		return false;
		
	}
	
	public static void saveLevel(String targetLocation, LevelEditor editor) {
		
		if(!(targetLocation.endsWith(".xml") || targetLocation.endsWith(".XML"))) {
			targetLocation += ".xml";
			
		}
		
		editor.setCurrentFile(targetLocation);		
		File output = new File(targetLocation);		
		LevelBean level = new LevelBean(editor.getLevel().getName());
		
		ArrayList<BrickBean> bricks = new ArrayList<BrickBean>();	
		
		for (Brick brick : editor.getLevel().getBricks()) {
			bricks.add(new BrickBean(brick));
		}
		
		level.setBricks(bricks);
		level.setBackgroundImage(editor.getLevel().getBackgroundImage());
		level.setSongfile(editor.getLevel().getSongfile());
		
		try {
			Marshaller m = (Marshaller)JAXBContext.newInstance(LevelBean.class).createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			m.marshal(level, new FileOutputStream(output));
		}
		catch (PropertyException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}		

	}

	public static void updateRecentFiles(String filename) {

		RecentFileList fileList = null;

		try {

			String inputDir = System.getProperty("user.home") + "/.sparkanoid/";
			String inputFileName = "recentFiles.xml";
			File inputFile = new File(inputDir + inputFileName);

			if (inputFile.exists()) {
				Unmarshaller u = JAXBContext.newInstance(RecentFileList.class).createUnmarshaller();
				fileList = (RecentFileList) u.unmarshal(inputFile);
			}

			if (fileList == null)
				fileList = new RecentFileList();

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}

		if (fileList.getRecentFiles().size() >= 5) {
			fileList.getRecentFiles().remove(0);
		}

		if (!fileList.getRecentFiles().contains(filename))
			fileList.getRecentFiles().add(filename);

		Marshaller m;

		try {
			String outputDir = System.getProperty("user.home") + "/." + AppContext.getApplicationName() + "/";
			String outputFileName = "recentFiles.xml";
			m = JAXBContext.newInstance(RecentFileList.class).createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			File output = new File(outputDir + outputFileName);
			m.marshal(fileList, output);
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static Level loadLevel(File file) throws IllegalArgumentException {

		ArrayList<Brick> bricks = new ArrayList<Brick>();

		int index = 0;

		try {
			Unmarshaller u = (Unmarshaller) JAXBContext.newInstance(LevelBean.class).createUnmarshaller();
			FileInputStream fis = new FileInputStream(file);

			LevelBean level = (LevelBean) u.unmarshal(fis);

			for (BrickBean b : level.getBricks()) {

				Brick brick = new Brick(b);
				brick.setIndex(index);
				bricks.add(brick);
				index++;

			}

			Level _level = new Level();
			_level.setBricks(bricks);

			if (level.getBackgroundImage() != null) {
				_level.setBackgroundImage(level.getBackgroundImage());
			}

			if (level.getSongfile() != null)
				_level.setSongfile(level.getSongfile());

			_level.setBricks(bricks);

			return _level;
		}
		catch (JAXBException e1) {
			e1.printStackTrace();
			throw new IllegalArgumentException("This file seems not to be a valid level file.");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
