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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.LevelEditor;
import org.pmedv.leveleditor.SparkyUtil;
import org.springframework.context.ApplicationContext;

public class SaveLevelCommand implements Command {

	@Override
	public void execute() throws Exception {

		final ApplicationContext ctx = AppContext.getApplicationContext();		
		final ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		final ApplicationWindow win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);
		
		final View  view = (View)advisor.getCurrentEditorArea().getSelectedWindow();				
		final JScrollPane pane = (JScrollPane)view.getComponent();		
		final LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);

		if (editor.getCurrentFile().equals("untitled")) {

			final JFileChooser fc = new JFileChooser(".");
			
			fc.setDialogTitle("Select file to save");
			fc.setApproveButtonText("Save");
			
			int result = fc.showOpenDialog(win);

			if (result == JFileChooser.APPROVE_OPTION) {

				if (fc.getSelectedFile() == null)
					return;

				final File selectedFile = fc.getSelectedFile();
				
				if (selectedFile != null) {						
					SparkyUtil.saveLevel(selectedFile.getAbsolutePath(),editor);						
				}
				
				editor.getView().getViewProperties().setTitle(selectedFile.getAbsolutePath());				
				editor.setFileState(FileState.SAVED);	
			}

			
		}
		if  (editor.getFileState().equals(FileState.DIRTY)) {
			SparkyUtil.saveLevel(editor.getCurrentFile(), editor);
			editor.setFileState(FileState.SAVED);
			SparkyUtil.updateRecentFiles(editor.getCurrentFile());
		}
		
		
	}

	
}
