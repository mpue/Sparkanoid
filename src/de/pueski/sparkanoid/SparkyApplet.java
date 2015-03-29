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
package de.pueski.sparkanoid;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JApplet;


public class SparkyApplet extends JApplet {
	
	private static final long	serialVersionUID	= 420122100448680981L;

	private Sparkanoid sparky;
	
	@Override
	public void init() {		
		
		super.init();
		
		setLayout(new BorderLayout());		
		sparky = new Sparkanoid("Sparky");				
		sparky.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				sparky.keyHit(e.getKeyChar());
			}			
		});
		
		setSize(new Dimension(750,600));
		add(sparky, BorderLayout.CENTER);
		setVisible(true);		
		sparky.start();

		
	}
	
	@Override
	public void destroy() {
		super.destroy();
		sparky.soundSystem.cleanup();
		System.exit(0);
	}

	
}
