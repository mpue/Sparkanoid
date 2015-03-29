package org.pmedv.leveleditor.commands;

import javax.swing.undo.UndoManager;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.springframework.context.ApplicationContext;


public class UndoCommand implements Command {

	@Override
	public void execute() throws Exception {
		
		ApplicationContext ctx = AppContext.getApplicationContext();		
		ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		
		UndoManager undoManager = (UndoManager)ctx.getBean("undoManager");
		undoManager.undo();
		
	}

}
