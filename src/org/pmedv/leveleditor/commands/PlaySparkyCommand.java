package org.pmedv.leveleditor.commands;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
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
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.core.services.ResourceService;
import org.pmedv.leveleditor.LevelEditor;
import org.springframework.context.ApplicationContext;

import de.pueski.sparkanoid.Sparkanoid;
import de.pueski.sparkanoid.objects.Brick;
import de.pueski.sparkanoid.objects.BrickBean;
import de.pueski.sparkanoid.objects.Game;
import de.pueski.sparkanoid.objects.LevelBean;

/**
 * This command opens a preview of the game with a new sparkanoid instance
 * 
 * @author Matthias Pueski
 */
public class PlaySparkyCommand extends AbstractOpenEditorCommand implements Command {

	private static final Log	log	= LogFactory.getLog(PlaySparkyCommand.class);

	private String				title;

	private View				sparkyView;

	public PlaySparkyCommand() {

		title = "Sparkanoid";
	}

	public PlaySparkyCommand(String title) {

		this.title = title;
	}

	@Override
	public final void execute() {

		final ApplicationContext ctx = AppContext.getApplicationContext();
		final ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor) ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);

		/*
		 * Get the resource service
		 */

		final ResourceService resources = (ResourceService) ctx.getBean(BeanDirectory.SERVICE_RESOURCE);
		
		final View  view = (View)advisor.getCurrentEditorArea().getSelectedWindow();
		
		final Game game = new Game();
		
		if (view != null) {

			final JScrollPane pane = (JScrollPane)view.getComponent();		
			final LevelEditor editor = (LevelEditor)pane.getViewport().getComponent(0);			
			final LevelBean level = new LevelBean();
			
			level.setName("level1");
			level.setBackgroundImage(editor.getLevel().getBackgroundImage());
			level.setSongfile(editor.getLevel().getSongfile());
			
			for (Brick brick : editor.getLevel().getBricks()) {		
				level.getBricks().add(new BrickBean(brick));			
			}
			
			game.getLevel().add(level);
			
		}
		

		
		/*
		 * The infonode docking framework must be invoked later since swing is
		 * not thread safe.
		 */

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				final Sparkanoid sparky;
				
				if (game.getLevel().size() > 0)				
					sparky = new Sparkanoid(game,"Sparkanoid");
				else
					sparky = new Sparkanoid("Sparkanoid");
					
				sparkyView = new View(title, null, sparky);

				ImageIcon icon = resources.getIcon("icon.document.html");
				sparkyView.getViewProperties().setIcon(icon);
				
				sparky.addKeyListener(new KeyAdapter() {
					public void keyTyped(KeyEvent e) {
						sparky.keyHit(e.getKeyChar());
					}			
				});

				
				openEditor(sparkyView);
				
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						sparky.start();						
					}
					
				});
				
				t.start();		
				
				sparkyView.addTabMouseButtonListener(new MouseButtonListener() {

					@Override
					public void mouseButtonEvent(MouseEvent arg0) {

						View view = (View) arg0.getSource();
						TabWindow tw = DockingUtil.getTabWindowFor(view);

						advisor.setCurrentEditorArea(tw);

					}

				});
				
				sparkyView.addListener(new DockingWindowAdapter() {

					@Override
					public void windowClosing(DockingWindow arg0) throws OperationAbortedException {
						sparky.stop();
					}
					
				});

			}
		});

	}

}
