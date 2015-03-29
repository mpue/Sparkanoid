/*
 * Created by JFormDesigner on Sun Feb 21 15:06:14 CET 2010
 */

package org.pmedv.leveleditor.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.pmedv.core.util.PluginClassDiscovery;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Matthias Pueski
 */


public class BrickPanel extends JPanel {

	private static final long	serialVersionUID	= -6759415266321209196L;

	public BrickPanel() {
		
		initComponents();
		
		colorCombo.setRenderer(new ColorRenderer());
		
		colorCombo.addItem(Color.BLUE);
		colorCombo.addItem(Color.RED);
		colorCombo.addItem(Color.YELLOW);
		colorCombo.addItem(Color.GREEN);
		colorCombo.addItem(Color.CYAN);
		colorCombo.addItem(Color.ORANGE);
		
		Map<String, Set<Class<?>>> classMap = new HashMap<String, Set<Class<?>>>();
		Set<String> interfaceFilter = new HashSet<String>();
		Set<String> packageFilter   = new HashSet<String>();
		
		dropItemComboBox.setRenderer(new ItemRenderer());

		dropItemComboBox.addItem("none");
		
		interfaceFilter.add("de.pueski.sparkanoid.objects.IDropItem");
		packageFilter.add("de.pueski.sparkanoid.objects");

		try {
			classMap = PluginClassDiscovery.findClasses(this.getClass().getClassLoader(), interfaceFilter, packageFilter, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		for (Iterator<Set<Class<?>>> classSetIterator = classMap.values().iterator();classSetIterator.hasNext(); ) {
			
			Set<Class<?>> currentSet = (Set<Class<?>>)classSetIterator.next();
			
			for (Iterator <Class<?>> classIterator = currentSet.iterator();classIterator.hasNext();) {
				Class<?> currentClass = (Class<?>) classIterator.next();				
				dropItemComboBox.addItem(currentClass);
				
			}
		}
		
		destroyableCheckbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hitCountSpinner.setEnabled(destroyableCheckbox.isSelected());
			}
			
		});
		
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		colorLabel = new JLabel();
		colorCombo = new JComboBox();
		scoreLabel = new JLabel();
		scoreSpinner = new JSpinner();
		hitCountLabel = new JLabel();
		hitCountSpinner = new JSpinner();
		destroyableLabel = new JLabel();
		destroyableCheckbox = new JCheckBox();
		dropItemLabel = new JLabel();
		dropItemComboBox = new JComboBox();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setLayout(new FormLayout(
			"2*($lcgap), 35dlu, 3*($lcgap, default:grow), 2*($lcgap)",
			"2*($lgap), 3*(default, $lgap), $lgap"));

		//---- colorLabel ----
		colorLabel.setText("Color");
		add(colorLabel, cc.xy(3, 3));
		add(colorCombo, cc.xy(5, 3));

		//---- scoreLabel ----
		scoreLabel.setText("Score");
		add(scoreLabel, cc.xy(7, 3));
		add(scoreSpinner, cc.xy(9, 3));

		//---- hitCountLabel ----
		hitCountLabel.setText("Hit count");
		add(hitCountLabel, cc.xy(3, 5));
		add(hitCountSpinner, cc.xy(5, 5));

		//---- destroyableLabel ----
		destroyableLabel.setText("Destroyable");
		add(destroyableLabel, cc.xy(7, 5));
		add(destroyableCheckbox, cc.xy(9, 5));

		//---- dropItemLabel ----
		dropItemLabel.setText("Drops item");
		add(dropItemLabel, cc.xy(3, 7));
		add(dropItemComboBox, cc.xywh(5, 7, 5, 1));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel colorLabel;
	private JComboBox colorCombo;
	private JLabel scoreLabel;
	private JSpinner scoreSpinner;
	private JLabel hitCountLabel;
	private JSpinner hitCountSpinner;
	private JLabel destroyableLabel;
	private JCheckBox destroyableCheckbox;
	private JLabel dropItemLabel;
	private JComboBox dropItemComboBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	/**
	 * @return the colorCombo
	 */
	public JComboBox getColorCombo() {
	
		return colorCombo;
	}

	
	/**
	 * @return the scoreSpinner
	 */
	public JSpinner getScoreSpinner() {
	
		return scoreSpinner;
	}

	
	/**
	 * @return the hitCountSpinner
	 */
	public JSpinner getHitCountSpinner() {
	
		return hitCountSpinner;
	}

	
	/**
	 * @return the destroyableCheckbox
	 */
	public JCheckBox getDestroyableCheckbox() {
	
		return destroyableCheckbox;
	}
	
	/**
	 * @return the dropItemComboBox
	 */
	public JComboBox getDropItemComboBox() {
		return dropItemComboBox;
	}

	
	private static class ColorRenderer extends  DefaultListCellRenderer {

		private static final long serialVersionUID = -3314160707000461070L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			ColorRenderer renderer = (ColorRenderer)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if (value instanceof Color) {
				
				Color color = (Color)value;
				
				if (color.equals(Color.BLUE)) {
					renderer.setText("Blue");
				}
				else if (color.equals(Color.GREEN)) {
					renderer.setText("Green");
				}
				else if (color.equals(Color.RED)) {
					renderer.setText("Red");
				}
				else if (color.equals(Color.YELLOW)) {
					renderer.setText("Yellow");
				}
				else if (color.equals(Color.CYAN)) {
					renderer.setText("Cyan");
				}
				else if (color.equals(Color.ORANGE)) {
					renderer.setText("Orange");
				}
					
			}
			
			return renderer;

		}
		
	}	

	private static class ItemRenderer extends  DefaultListCellRenderer {

		private static final long serialVersionUID = -3314160707000461070L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			ItemRenderer renderer = (ItemRenderer)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if (value instanceof Class<?>) {
				
				Class<?> c = (Class<?>)value;				
				renderer.setText(c.getSimpleName());
					
			}
			else
				renderer.setText("none");
			
			return renderer;

		}
		
	}	
	
	
}
