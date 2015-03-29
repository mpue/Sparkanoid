package org.pmedv.leveleditor.commands;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.ApplicationPerspective;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.ExitCommand;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.perspectives.AbstractPerspective;
import org.pmedv.core.provider.ApplicationPerspectiveProvider;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.LevelEditor;
import org.springframework.context.ApplicationContext;

public class EditorExitCommand extends ExitCommand {

	private static final Log log = LogFactory.getLog(EditorExitCommand.class);
	
	final ApplicationContext ctx = AppContext.getApplicationContext();
	ArrayList <String> unsavedResources = new ArrayList<String>();
	
	@Override
	public void execute() {
		
		final ApplicationPerspectiveProvider provider = (ApplicationPerspectiveProvider)ctx.getBean("perspectiveProvider"); 
		final ApplicationWindow win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);
		
		for (ApplicationPerspective ap : provider.getPerspectives()) {			

			AbstractPerspective a = (AbstractPerspective)ctx.getBean(ap.getId());
			
			for (int i=0; i < a.getRootWindow().getChildWindowCount();i++) {
				
				if (a.getRootWindow().getChildWindow(i) instanceof TabWindow) {
				
					TabWindow tabWin = (TabWindow) a.getRootWindow().getChildWindow(i);
					
					for (int j = 0;j < tabWin.getChildWindowCount();j++) {
						
						if (tabWin.getChildWindow(j) instanceof View) {
							
							View view = (View)tabWin.getChildWindow(j);
							
							if (view.getComponent() instanceof JScrollPane) {

								final JScrollPane pane = (JScrollPane)view.getComponent();		
								final LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);

								if (editor.getFileState().equals(FileState.DIRTY)) {
									unsavedResources.add(editor.getCurrentFile());
								}
								
							}
							
						}
						
					}
					
				}
				else if (a.getRootWindow().getChildWindow(i) instanceof SplitWindow) {
					
					SplitWindow splitWin = (SplitWindow) a.getRootWindow().getChildWindow(i);
					
					for (int j = 0;j < splitWin.getChildWindowCount();j++) {
					
						if (splitWin.getChildWindow(j) instanceof TabWindow) {
							
							TabWindow tabWin = (TabWindow) splitWin.getChildWindow(j);
							
							for (int k = 0;k < tabWin.getChildWindowCount();k++) {
								
								if (tabWin.getChildWindow(k) instanceof View) {
									
									View view = (View)tabWin.getChildWindow(k);
									
									if (view.getComponent() instanceof JScrollPane) {

										final JScrollPane pane = (JScrollPane)view.getComponent();		
										final LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);

										if (editor.getFileState().equals(FileState.DIRTY)) {
											unsavedResources.add(editor.getCurrentFile());
										}
										
									}
									
								}
								
							}

							
						}
						
					}
						
						
				}
				
			}
			
		}
		
		if (unsavedResources.size() > 0) {
		
			StringBuffer text = new StringBuffer();
			
			text.append("The following resources are unsaved :\n\n ");
			
			for (String resource : unsavedResources) {
				
				text.append(resource);
				text.append("\n");
				
			}
			
			text.append("\nAre you sure you want to close anyway?");

			int result = JOptionPane.showConfirmDialog(win, text.toString(), "Warning", JOptionPane.YES_NO_OPTION);
			
			if (result == JOptionPane.YES_OPTION)		
				super.execute();

			
		}
		else
			super.execute();			
		
	}

}
