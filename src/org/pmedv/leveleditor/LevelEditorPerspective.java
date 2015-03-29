package org.pmedv.leveleditor;

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.TabWindow;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.perspectives.AbstractPerspective;

public class LevelEditorPerspective extends AbstractPerspective {

	private static final long serialVersionUID = 1834464939876984405L;

	private static final Log log = LogFactory.getLog(LevelEditorPerspective.class);


	public LevelEditorPerspective() {
		ID = "levelEditorPerspective";
		log.info("Creating perspective : "+ID);		
	}

	@Override
	protected void initializeComponents() {

		setLayout(new BorderLayout());


		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				viewMap = new ViewMap();

				rootWindow = DockingUtil.createRootWindow(viewMap, true);
				rootWindow.getRootWindowProperties().getDockingWindowProperties().setMinimizeEnabled(true);
				rootWindow.getRootWindowProperties().getTabWindowProperties().getMinimizeButtonProperties().setVisible(true);
				rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
				
				DockingWindowsTheme theme = new ShapedGradientDockingTheme();

				rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());

				rootWindow.getWindowProperties().getTabProperties().getHighlightedButtonProperties()
						.getCloseButtonProperties().setVisible(false);
				rootWindow.getWindowProperties().getTabProperties().getNormalButtonProperties()
						.getCloseButtonProperties().setVisible(false);
				rootWindow.getWindowProperties().getTabProperties().getNormalButtonProperties()
				.getMinimizeButtonProperties().setVisible(true);
				
				
				
				editorArea = new TabWindow(new DockingWindow[] {});
				editorArea.getWindowProperties().setCloseEnabled(false);
				
				DockingWindowAdapter dockingAdapter = new DockingWindowAdapter() {

					@Override
					public void windowClosing(DockingWindow window) throws OperationAbortedException {

					}
					
					
					
				};
				
				editorArea.addListener(dockingAdapter);
				setDockingListener(dockingAdapter);
				rootWindow.setWindow(editorArea);
				add(rootWindow, BorderLayout.CENTER);

			}

		});

	}


}
