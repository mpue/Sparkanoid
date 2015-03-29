package org.pmedv.leveleditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import org.pmedv.core.dialogs.AbstractNiceDialog;
import org.pmedv.leveleditor.panels.BrickPanel;

import de.pueski.sparkanoid.objects.Brick;

/**
 * The dialog for host data entry
 * 
 * @author Matthias Pueski
 *
 */
public class BrickDialog extends AbstractNiceDialog {

	private static final long serialVersionUID = 4399624580838621949L;
	
	private static final String title = "Brick";
	private static final String subTitle = "Add or modify a brick";
	private static final ImageIcon icon = resources.getIcon("icon.dialog.computer");
	
	private BrickPanel brickPanel;	
	private Brick brick;

	public BrickDialog(Object object) {
		
		super(title, subTitle, icon, true, false, true,
				true, win, object);
		
	}

	@Override
	protected void initializeComponents() {
		
		setBounds(new Rectangle(new Dimension(500, 250)));
		setSize(new Dimension(500, 250));
		
		brickPanel = new BrickPanel();
		
		getContentPanel().add(brickPanel, BorderLayout.CENTER);
	
		getOkButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);				
				
				if (brick != null) {
					brick.setHitCount((Integer)brickPanel.getHitCountSpinner().getValue());
					brick.setDestroyable(brickPanel.getDestroyableCheckbox().isSelected());
					brick.setColor((Color)brickPanel.getColorCombo().getSelectedItem());			
					brick.setScore((Integer)brickPanel.getScoreSpinner().getValue());
					
					if (!(brickPanel.getDropItemComboBox().getSelectedItem() instanceof String)) {
						Class<?> c = (Class<?>)brickPanel.getDropItemComboBox().getSelectedItem();						
						brick.setDropItemClass(c.getName());						
					}
					
				}
				
				result = OPTION_OK;
			}
			
		});
		
		getCancelButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				
				result = OPTION_CANCEL;
			}
			
		});
		
		if (getUserObject() instanceof Brick) {
			
			brick = (Brick)getUserObject();

			brickPanel.getHitCountSpinner().setValue(brick.getHitCount());
			brickPanel.getDestroyableCheckbox().setSelected(brick.isDestroyable());
			brickPanel.getColorCombo().setSelectedItem(brick.getColor());
			brickPanel.getScoreSpinner().setValue(brick.getScore());
			brickPanel.getHitCountSpinner().setEnabled(brick.isDestroyable());	
			
			for (int i=0; i < brickPanel.getDropItemComboBox().getItemCount();i++) {
				
				if (!(brickPanel.getDropItemComboBox().getItemAt(i) instanceof String)) {
					Class<?> c = (Class<?>)brickPanel.getDropItemComboBox().getItemAt(i);
					
					if (c.getName().equals(brick.getDropItemClass())) {
						brickPanel.getDropItemComboBox().setSelectedItem(c);
					}
					
				}
					
					
				
			}

		}
		
	}

	public Brick getBrick() {
		return brick;
	}
	
	
}
