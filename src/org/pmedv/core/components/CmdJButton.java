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
package org.pmedv.core.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;

import org.pmedv.core.commands.Command;
import org.pmedv.core.commands.CommandHolder;



/**
 * This class represents a basic JButton with an associated command
 * 
 * @author mpue 19.12.2006
 *
 */

public class CmdJButton extends JButton implements CommandHolder,MouseListener {

	private static final long serialVersionUID = 1L;
	
	protected Command menuCommand;
	protected JFrame frame;
	protected String id;
	
	Border rb = BorderFactory.createRaisedBevelBorder(),
    	   lb = BorderFactory.createLoweredBevelBorder();
	
	public CmdJButton(String name, Icon icon,JFrame frm) {
		super(name,icon);
		frame = frm;
		this.addActionListener(new CmdActionListener());
		this.addMouseListener(this);
	}
	
	public CmdJButton(Icon icon,String tooltipText, Command command) {
		super(icon);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setSize(16, 16);
		this.setToolTipText(tooltipText);
		this.setCommand(command);
		this.addActionListener(new CmdActionListener());
		this.addMouseListener(this);		
	}
	
	public CmdJButton(String name, Icon icon) {
		super(name,icon);
		frame = null;
		this.addActionListener(new CmdActionListener());
		this.addMouseListener(this);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);		
	}
	
	public Command getCommand() {
		return  menuCommand;
	}

	public void setCommand(Command cmd) {
		menuCommand = cmd;
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		setBorderPainted(true);
	}

	public void mouseExited(MouseEvent e) {
		setBorderPainted(false);
	}

	public void mousePressed(MouseEvent e) {		
	}

	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
