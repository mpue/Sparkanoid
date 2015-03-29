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
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.gui.mouse.MouseButtonListener;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.AbstractOpenEditorCommand;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.core.services.ResourceService;
import org.pmedv.leveleditor.FileState;
import org.pmedv.leveleditor.LevelEditor;
import org.pmedv.leveleditor.SparkyUtil;
import org.springframework.context.ApplicationContext;

import de.pueski.sparkanoid.objects.Level;

public class OpenLevelCommand extends AbstractOpenEditorCommand implements Command {

	private String	title;
	private View	levelEditorView;
	private String	filename;

	public OpenLevelCommand() {

		title = "Level Editor";
	}

	public OpenLevelCommand(String title) {

		this.title = title;
	}

	public OpenLevelCommand(String title, String filename) {

		this.title = title;
		this.filename = filename;

	}

	@Override
	public void execute() {

		final ApplicationContext ctx = AppContext.getApplicationContext();
		final ApplicationWindow win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);
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

				if (filename == null) {

					JFileChooser fc = new JFileChooser(".");
					fc.setDialogTitle("Open file");
					fc.setFileFilter(new XMLFilter());

					int result = fc.showOpenDialog(win);

					if (result == JFileChooser.APPROVE_OPTION) {

						if (fc.getSelectedFile() == null)
							return;

						filename = fc.getSelectedFile().getAbsolutePath();

					}
					else {
						return;
					}

				}

				Level level = null;

				try {
					level = SparkyUtil.loadLevel(new File(filename));

					if (level == null)
						throw new IllegalArgumentException("Could not load level");

				}
				catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(win, i.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					filename = null;
					return;
				}

				final LevelEditor editor = new LevelEditor(level);

				final JScrollPane s = new JScrollPane(editor);
				s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				levelEditorView = new View(title, null, s);

				final ImageIcon icon = resources.getIcon("icon.document.html");
				levelEditorView.getViewProperties().setIcon(icon);
				levelEditorView.getViewProperties().setTitle(filename);

				if (level.getBackgroundImage() != null) {
					URL bgImageURL = Thread.currentThread().getContextClassLoader().getResource(level.getBackgroundImage());
					editor.setBackgroundImage(Toolkit.getDefaultToolkit().getImage(bgImageURL));
				}
				
				editor.setFileState(FileState.OPENED);
				editor.setCurrentFile(filename);
				editor.setView(levelEditorView);
				editor.invalidate();
				editor.repaint();

				SparkyUtil.updateRecentFiles(filename);
				openEditor(levelEditorView);

				// filename = null;

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

						if (editor.getFileState().equals(FileState.DIRTY)) {
							int result = JOptionPane.showConfirmDialog((ApplicationWindow) ctx
									.getBean(BeanDirectory.WINDOW_APPLICATION), "You did not save your changes, close anyway?",
									"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

							if (result == JOptionPane.NO_OPTION) {
								throw new OperationAbortedException("Aborted.");
							}
						}

					}

				});

			}

		});

	}



	private static class XMLFilter extends FileFilter {

		@Override
		public boolean accept(File f) {

			if (f.isDirectory())
				return true;
			if ((f.getName().endsWith(".xml")) || f.getName().endsWith(".XML"))
				return true;
			else
				return false;
		}

		@Override
		public String getDescription() {

			return "XML files only (*.xml *.XML)";
		}

	}

}
