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

import javax.swing.JScrollPane;

import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.dialogs.AbstractNiceDialog;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.leveleditor.LevelEditor;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.dialogs.BrickDialog;
import org.springframework.context.ApplicationContext;


public class EditBrickCommand implements Command {

	@Override
	public void execute() throws Exception {

		ApplicationContext ctx = AppContext.getApplicationContext();		
		ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		
		View  view = (View)advisor.getCurrentEditorArea().getSelectedWindow();				
		JScrollPane pane = (JScrollPane)view.getComponent();		
		LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);
		
		if (editor.getSelectedBrick() != null) {

			final BrickDialog dialog = new BrickDialog(editor.getSelectedBrick());
			dialog.setVisible(true);
			
			if (dialog.getResult() == AbstractNiceDialog.OPTION_CANCEL)
				return;
			
			editor.setFileState(FileState.DIRTY);
			editor.invalidate();
			editor.repaint();
			
		}
		
		
	}

}
