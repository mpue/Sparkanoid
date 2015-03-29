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

import java.util.Stack;

import javax.swing.JScrollPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.LevelEditor;
import org.springframework.context.ApplicationContext;

import de.pueski.sparkanoid.objects.Brick;


public class DeleteBrickCommand extends AbstractUndoableEdit implements Command {

	private Stack<Brick> deletedBricks;
	
	private int numDeletedBricks;
	
	public DeleteBrickCommand() {
		deletedBricks = new Stack<Brick>();
	}
	
	@Override
	public void execute() throws Exception {

		ApplicationContext ctx = AppContext.getApplicationContext();		
		ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		
		UndoManager undoManager = (UndoManager)ctx.getBean("undoManager");
		
		View  view = (View)advisor.getCurrentEditorArea().getSelectedWindow();				
		JScrollPane pane = (JScrollPane)view.getComponent();		
		LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);

		Brick selectedBrick = editor.getSelectedBrick();
		
		if (selectedBrick != null) {
			deletedBricks.push(selectedBrick);			
			editor.getLevel().getBricks().remove(selectedBrick);
			editor.setSelectedBrick(null);
			undoManager.addEdit(this);
			numDeletedBricks = 1;
		}

		if (editor.getSelectedBricks().size() > 0) {
			
			numDeletedBricks = 0;
			
			for (Brick brick : editor.getSelectedBricks()) {
				deletedBricks.push(brick);
				editor.getLevel().getBricks().remove(brick);
				numDeletedBricks++;				
			}
			undoManager.addEdit(this);
		}

		editor.setFileState(FileState.DIRTY);
		editor.invalidate();
		editor.repaint();
		
	}

	@Override
	public void undo() throws CannotUndoException {

		ApplicationContext ctx = AppContext.getApplicationContext();		
		ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		
		UndoManager undoManager = (UndoManager)ctx.getBean("undoManager");
		
		View  view = (View)advisor.getCurrentEditorArea().getSelectedWindow();				
		JScrollPane pane = (JScrollPane)view.getComponent();		
		LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);

		for (int i=0; i < numDeletedBricks;i++) {
			editor.getLevel().getBricks().add(deletedBricks.pop());			
		}

		editor.invalidate();
		editor.repaint();
		
	}
	
	@Override
	public void redo() throws CannotRedoException {
		System.out.println("redo");
	}

}
