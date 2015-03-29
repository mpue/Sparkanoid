package org.pmedv.leveleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.components.CmdJMenuItem;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.services.ResourceService;
import org.pmedv.leveleditor.commands.DeleteBrickCommand;
import org.pmedv.leveleditor.commands.EditBrickCommand;
import org.pmedv.leveleditor.commands.ToggleGridCommand;
import org.springframework.context.ApplicationContext;

import de.pueski.sparkanoid.objects.Brick;
import de.pueski.sparkanoid.objects.Level;

@SuppressWarnings("unused")
public class LevelEditor extends JPanel {

	private static final long	serialVersionUID	= -6034450048001248857L;

	private static final int	EDITOR_WIDTH		= 750;
	private static final int	EDITOR_HEIGHT		= 600;

	private ApplicationContext	ctx					= AppContext.getApplicationContext();
	private ApplicationWindow	win					= (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);
	private ResourceService		resources			= (ResourceService) ctx.getBean(BeanDirectory.SERVICE_RESOURCE);

	private Level level;

	private JPopupMenu			popupMenu;

	int							currentX			= 0;
	int							currentY			= 0;

	int							offsetX				= 0;
	int							offsetY				= 0;

	private int					xgrid				= 25;
	private int					ygrid				= 25;

	boolean						snapToGrid			= true;
	boolean						gridVisible			= true;

	private boolean				button1Pressed		= false;
	private boolean				button2Pressed		= false;
	private boolean				button3Pressed		= false;

	private int					dragStartX;
	private int					dragStartY;

	private int					dragStopX;
	private int					dragStopY;

	boolean						refreshed			= false;

	private Brick				currentMovingBrick;
	private Brick				selectedBrick;

	private Image				backgroundImage;
	private String 				backgroundImageName;
	
	protected boolean			ctrlPressed			= false;

	private ArrayList<Brick>	selectedBricks		= new ArrayList<Brick>();

	private DeleteBrickCommand	deleteBrickCommand;
	private EditBrickCommand	editBrickCommand;
	private ToggleGridCommand   toggleGridCommand;

	private EditorState			state;	
	private FileState           fileState;
	private String 				currentFile;

	protected Rectangle			selectionBorder		= new Rectangle();

	private View view;


	@SuppressWarnings("serial")
	public LevelEditor(Level level) {

		super();

		this.level = level;
		
		state = EditorState.NOTHING_SELECTED;

		editBrickCommand   = new EditBrickCommand();
		deleteBrickCommand = new DeleteBrickCommand();
		toggleGridCommand  = new ToggleGridCommand();

		Dimension size = new Dimension(EDITOR_WIDTH, EDITOR_HEIGHT);
		setBounds(new Rectangle(size));
		setPreferredSize(size);

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {

				handleMouseDragged(e);
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				handleMousePressed(e);

			}

			@Override
			public void mouseReleased(MouseEvent e) {

				handleMouseReleased(e);
			}

		});

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "toggleGrid");
		
		getActionMap().put("delete", new AbstractAction() {

			public void actionPerformed(ActionEvent event) {

				try {
					deleteBrickCommand.execute();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		getActionMap().put("toggleGrid", new AbstractAction() {

			public void actionPerformed(ActionEvent event) {

				try {
					toggleGridCommand.execute();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		
		hookContextMenu();
	}

	private void handleMouseDragged(MouseEvent e) {

		if (currentMovingBrick != null) {
			
			setFileState(FileState.DIRTY);

			if (ctrlPressed && selectedBricks.size() < 1) {

				ctrlPressed = false;

				if (selectedBrick != null) {

					Brick newBrick;

					try {

						newBrick = (Brick) selectedBrick.clone();

						level.getBricks().add(newBrick);
						currentMovingBrick = newBrick;
						selectedBrick = currentMovingBrick;
						
						setFileState(FileState.DIRTY);
						
					}
					catch (CloneNotSupportedException e1) {
						System.out.println("could not clone.");
					}

				}

			}

			int newX = e.getX() - offsetX;
			int newY = e.getY() - offsetY;

			if (snapToGrid) {

				if (newX % xgrid >= 5)
					newX = newX + (xgrid - (newX % xgrid));

				if (newY % ygrid >= 5)
					newY = newY + (ygrid - (newY % ygrid));

			}

			if (newX + Brick.WIDTH > EDITOR_WIDTH)
				newX = EDITOR_WIDTH - Brick.WIDTH;
			else if (newX < 0)
				newX = 0;

			if (newY + Brick.HEIGHT > EDITOR_HEIGHT) {
				newY = EDITOR_HEIGHT - Brick.HEIGHT;
			}
			else if (newY < 0)
				newY = 0;

			int deltaX = newX  - currentMovingBrick.getXLoc();
			int deltaY = newY  - currentMovingBrick.getYLoc();
			
			currentMovingBrick.setXLoc(newX);
			currentMovingBrick.setYLoc(newY);
			
			if (selectedBricks.size() > 1) {
				
				if (ctrlPressed) {
					
					ctrlPressed = false;
					
					ArrayList<Brick> clonedSelection = new ArrayList<Brick>();

					for (Brick selected : selectedBricks) {

						try {
							
							Brick newBrick = (Brick)selected.clone();							
							
							int currentLastIndex = 0;
							
							for (Brick brick : level.getBricks()) {
								
								if (brick.getIndex() > currentLastIndex)
									currentLastIndex = brick.getIndex();
								
							}
							
							/**
							 * Increment by one, since we need the first free index.
							 */
							
							currentLastIndex++;
							newBrick.setIndex(currentLastIndex);
							
							clonedSelection.add(newBrick);
							level.getBricks().add(newBrick);
							
							if (selected.equals(currentMovingBrick)) {
								currentMovingBrick = newBrick;
							}
							
						}
						catch (CloneNotSupportedException e1) {
							e1.printStackTrace();
						}
						
					}

					selectedBricks = clonedSelection;				

				}
				
				invalidate();
				repaint();
				
				for (Brick selected : selectedBricks) {
					
					if (!selected.equals(currentMovingBrick)) {					
						selected.setXLoc(selected.getXLoc()+ deltaX);
						selected.setYLoc(selected.getYLoc()+ deltaY);
					}
					
				}
				
			}			

		}
		else {

			if (selectedBrick == null) {

				state = EditorState.DRAGGING_NEW_SELECTION;

				dragStopX = e.getX();
				dragStopY = e.getY();

			}

			if (!state.equals(EditorState.DRAGGING_NEW_SELECTION)) {

				for (Brick brick : level.getBricks()) {

					if (e.getX() >= brick.getXLoc() && e.getX() <= brick.getXLoc() + Brick.WIDTH && e.getY() >= brick.getYLoc()
							&& e.getY() <= brick.getYLoc() + Brick.HEIGHT) {

						if (button1Pressed) {

							currentMovingBrick = brick;

							currentX = e.getX();
							currentY = e.getY();

							offsetX = currentX - currentMovingBrick.getXLoc();
							offsetY = currentY - currentMovingBrick.getYLoc();

						}

					}

				}
			}
			else {

				for (Brick brick : level.getBricks()) {

					if (SparkyUtil.collides(selectionBorder, new Rectangle(brick.getXLoc(), brick.getYLoc(), Brick.WIDTH,
							Brick.HEIGHT))) {
						
						if(!selectedBricks.contains(brick))						
							selectedBricks.add(brick);

					}
					else {

						if(selectedBricks.contains(brick))						
							selectedBricks.remove(brick);

					}
					
				}

			}

		}

		invalidate();
		repaint();

	}

	private void handleMousePressed(MouseEvent e) {

		if (e.isPopupTrigger()) {
			handleContextClick(e);
		}

		if (e.getClickCount() == 2) {
			try {
				editBrickCommand.execute();
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		if (e.isControlDown()) {
			ctrlPressed = true;
		}
		else {
			ctrlPressed = false;
		}

		if (e.getButton() == 1) {

			button1Pressed = true;
			selectedBrick = null;
			
			for (Brick brick : level.getBricks()) {
				
				if (brick.isInside(e.getX(), e.getY())) {

					selectedBrick = brick;
					invalidate();
					repaint();

				}

			}

			if (selectedBrick == null) {

				selectedBricks.clear();
				selectionBorder.setLocation(0, 0);
				selectionBorder.setSize(0, 0);
				
				dragStartX = e.getX();
				dragStartY = e.getY();

			}

		}
		else if (e.getButton() == 2) {
			button2Pressed = true;
		}
		else if (e.getButton() == 3) {
			button3Pressed = true;
		}

		invalidate();
		repaint();

	}

	private void handleMouseReleased(MouseEvent e) {

		if (selectedBrick != null)
			state = EditorState.SINGLE_BRICK_SELECTED;
		else
			state = EditorState.NOTHING_SELECTED;

		if (e.isPopupTrigger()) {
			handleContextClick(e);
		}

		if (e.getButton() == 1) {
			button1Pressed = false;
			currentMovingBrick = null;
		}
		else if (e.getButton() == 2) {
			button2Pressed = false;
		}
		else if (e.getButton() == 3) {
			button3Pressed = false;
		}

		invalidate();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {

		g.clearRect(0, 0, getWidth(), getHeight());

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(rh);

		if (backgroundImage != null)
			g2.drawImage(backgroundImage, 0, 0, this);

		if (gridVisible)
			drawGrid(g);

		if (level.getBricks() != null) {

			for (Brick brick : level.getBricks()) {

				brick.draw(brick.getXLoc(), brick.getYLoc(),g);
			
				if (selectedBricks.contains(brick)) {
				
					g2.setColor(Color.GREEN);

					Rectangle r = new Rectangle(brick.getXLoc() - 5, brick.getYLoc() - 5, Brick.WIDTH + 10, Brick.HEIGHT + 10);
					g2.draw(r);

				}

			}

			if (selectedBrick != null) {

				g2.setColor(Color.GREEN);

				Rectangle r = new Rectangle(selectedBrick.getXLoc() - 5, selectedBrick.getYLoc() - 5, Brick.WIDTH + 10,
						Brick.HEIGHT + 10);
				g2.draw(r);

			}

		}

		if (state.equals(EditorState.DRAGGING_NEW_SELECTION) && button1Pressed) {

			g2.setColor(Color.GREEN);

			if (dragStopX < dragStartX && dragStopY > dragStartY) {
				selectionBorder.setSize(dragStartX - dragStopX, dragStopY - dragStartY);
				selectionBorder.setLocation(dragStopX, dragStartY);
			}
			else if (dragStopY < dragStartY && dragStopX > dragStartX) {
				selectionBorder.setSize(dragStopX - dragStartX, dragStartY - dragStopY);
				selectionBorder.setLocation(dragStartX, dragStopY);
			}
			else if (dragStopX < dragStartX && dragStopY < dragStartY) {
				selectionBorder.setSize(dragStartX - dragStopX, dragStartY - dragStopY);
				selectionBorder.setLocation(dragStopX, dragStopY);
			}
			else {
				selectionBorder.setSize(dragStopX - dragStartX, dragStopY - dragStartY);
				selectionBorder.setLocation(dragStartX, dragStartY);
			}

			g2.draw(selectionBorder);
		}

		g2.setColor(Color.RED);

		Rectangle border = new Rectangle(0, 0, EDITOR_WIDTH, EDITOR_HEIGHT);
		g2.draw(border);

	}

	private void drawGrid(Graphics g) {

		g.setColor(Color.LIGHT_GRAY);

		for (int i = xgrid; i < EDITOR_WIDTH; i += xgrid)
			g.drawLine(i, 0, i, EDITOR_HEIGHT);

		for (int i = ygrid; i < EDITOR_HEIGHT; i += ygrid)
			g.drawLine(0, i, EDITOR_WIDTH, i);

	}

	private void hookContextMenu() {

		ImageIcon editIcon = resources.getIcon("icon.popupmenu.edit");
		ImageIcon deleteIcon = resources.getIcon("icon.popupmenu.delete");

		popupMenu = new JPopupMenu();

		CmdJMenuItem deleteItem = new CmdJMenuItem("Delete", deleteIcon, deleteBrickCommand, "D", "Deletes a brick", win);
		CmdJMenuItem editItem = new CmdJMenuItem("Edit", editIcon, editBrickCommand, "B", "Adds a new brick.", win);

		popupMenu.add(deleteItem);
		popupMenu.add(editItem);
	}

	private void handleContextClick(MouseEvent e) {

		if (e.isPopupTrigger()) {
			if (selectedBrick != null) {
				popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}

	}
	/**
	 * @return the snapToGrid
	 */
	public boolean isSnapToGrid() {

		return snapToGrid;
	}

	/**
	 * @param snapToGrid the snapToGrid to set
	 */
	public void setSnapToGrid(boolean snapToGrid) {

		this.snapToGrid = snapToGrid;
	}

	/**
	 * @return the selectedBrick
	 */
	public Brick getSelectedBrick() {

		return selectedBrick;
	}

	public void setSelectedBrick(Brick brick) {

		selectedBrick = brick;
	}

	/**
	 * @return the gridVisible
	 */
	public boolean isGridVisible() {

		return gridVisible;
	}

	/**
	 * @param gridVisible the gridVisible to set
	 */
	public void setGridVisible(boolean gridVisible) {

		this.gridVisible = gridVisible;
	}

	/**
	 * @return the selectedBricks
	 */
	public ArrayList<Brick> getSelectedBricks() {
	
		return selectedBricks;
	}

	
	/**
	 * @param selectedBricks the selectedBricks to set
	 */
	public void setSelectedBricks(ArrayList<Brick> selectedBricks) {
	
		this.selectedBricks = selectedBricks;
	}


	
	/**
	 * @return the fileState
	 */
	public FileState getFileState() {
		return fileState;
	}

	
	/**
	 * @param fileState the fileState to set
	 */
	public void setFileState(FileState fileState) {
		
		if (view != null && currentFile != null) {
		
			if (fileState.equals(FileState.DIRTY)) {
				view.getViewProperties().setTitle(currentFile+"*");
			}
			else {
				view.getViewProperties().setTitle(currentFile);
			}
		}
		else if (view != null && currentFile == null) {
			
			if (fileState.equals(FileState.DIRTY)) {
				fileState = FileState.NEW_AND_UNSAVED;
				view.getViewProperties().setTitle("untitled*");
			}
			else {
				view.getViewProperties().setTitle("untitled");
			}
			
		}
		
		this.fileState = fileState;
	}
	
	/**
	 * @return the currentFile
	 */
	public String getCurrentFile() {
		return currentFile;
	}

	
	/**
	 * @param currentFile the currentFile to set
	 */
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}
	
	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	
	/**
	 * @param view the view to set
	 */
	public void setView(View view) {
		this.view = view;
	}


	/**
	 * @return the backgroundImage
	 */
	public Image getBackgroundImage() {
	
		return backgroundImage;
	}

	/**
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(Image backgroundImage) {

		this.backgroundImage = backgroundImage;
	}

	/**
	 * @return the level
	 */
	public Level getLevel() {
	
		return level;
	}

	
	/**
	 * @param level the level to set
	 */
	public void setLevel(Level level) {
	
		this.level = level;
	}

}
