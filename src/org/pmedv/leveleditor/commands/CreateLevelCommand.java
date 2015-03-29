package org.pmedv.leveleditor.commands;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.gui.mouse.MouseButtonListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.AbstractOpenEditorCommand;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.core.services.ResourceService;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.LevelEditor;
import org.springframework.context.ApplicationContext;

import de.pueski.sparkanoid.objects.Level;

/**
 * This command opens a level for editing inside a new tab window.
 * 
 * @author pueski
 */
public class CreateLevelCommand extends AbstractOpenEditorCommand implements Command {

	private static final Log	log	= LogFactory.getLog(CreateLevelCommand.class);

	private String				title;

	private View				levelEditorView;

	public CreateLevelCommand() {

		title = "untitled";
	}

	public CreateLevelCommand(String title) {

		this.title = title;
	}

	@Override
	public void execute() {

		final ApplicationContext ctx = AppContext.getApplicationContext();
		final ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor) ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);

		/*
		 * Get the resource service
		 */

		final ResourceService resources = (ResourceService) ctx.getBean(BeanDirectory.SERVICE_RESOURCE);

		/*
		 * The infonode docking framework must be invoked later since swing is
		 * not thread safe.
		 */

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				final LevelEditor editor = new LevelEditor(new Level());
				
				JScrollPane s = new JScrollPane(editor);
				s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				levelEditorView = new View(title, null, s);

				ImageIcon icon = resources.getIcon("icon.document.html");
				levelEditorView.getViewProperties().setIcon(icon);
				editor.setView(levelEditorView);
				editor.setFileState(FileState.NEW_AND_UNSAVED);
				editor.setCurrentFile("untitled");
				
				openEditor(levelEditorView);

				log.info("Opening editor : " + title);

				levelEditorView.addTabMouseButtonListener(new MouseButtonListener() {

					@Override
					public void mouseButtonEvent(MouseEvent arg0) {

						View view = (View) arg0.getSource();
						TabWindow tw = DockingUtil.getTabWindowFor(view);

						advisor.setCurrentEditorArea(tw);

					}

				});

				levelEditorView.addListener(new DockingWindowAdapter() {

					@Override
					public void windowClosing(DockingWindow arg0) throws OperationAbortedException {

						if (editor != null)
						
							if (editor.getFileState().equals(FileState.DIRTY)) {
								int result = JOptionPane.showConfirmDialog((ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION),
										"You did not save your changes, close anyway?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	
								if (result == JOptionPane.NO_OPTION) {
									throw new OperationAbortedException("Aborted.");
								}
							}
						
					}
					
				});


			}
		});

	}

}
