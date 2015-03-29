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
package org.pmedv.leveleditor.commands;

import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.LevelEditor;
import org.springframework.context.ApplicationContext;


public class SetImageCommand implements Command {

	@Override
	public void execute() throws Exception {

		ApplicationContext ctx = AppContext.getApplicationContext();		
		ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		final ApplicationWindow win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);

		View  view = (View)advisor.getCurrentEditorArea().getSelectedWindow();				
		JScrollPane pane = (JScrollPane)view.getComponent();		
		LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);
		
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle("Open file");
		fc.setFileFilter(new ImageFilter());

		int result = fc.showOpenDialog(win);

		if (result == JFileChooser.APPROVE_OPTION) {

			if (fc.getSelectedFile() == null)
				return;

			String filename = fc.getSelectedFile().getAbsolutePath();  
			
			/**
			 * Strange stuff here, but it is needed. Since I'll keep the <code>level</code> object serializable
			 * and still want be able to set the background image of a level directly, I need to preserve the name
			 * of the image. This becomes very handy, when I want to execute the level directly from within the editor.
			 * 
			 * (Look at PlaySparkyCommand.)
			 * 
			 */
			
			editor.setBackgroundImage(Toolkit.getDefaultToolkit().getImage(filename));
			editor.getLevel().setBackgroundImage(fc.getSelectedFile().getName());
			
			editor.invalidate();
			editor.repaint();
			
		}
		editor.setFileState(FileState.DIRTY);
		editor.invalidate();
		editor.repaint();
		
	}
	
	private static class ImageFilter extends FileFilter {

		@Override
		public boolean accept(File f) {

			if (f.isDirectory())
				return true;
			if ((f.getName().endsWith(".png") || 
				 f.getName().endsWith(".PNG") ||
				 f.getName().endsWith(".jpg") ||
				 f.getName().endsWith(".JPG"))) 
				return true;
			else
				return false;
		}

		@Override
		public String getDescription() {

			return "Image files only (PNG,JPG)";
		}

	}

}
