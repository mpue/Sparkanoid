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
package org.pmedv.core.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.ApplicationPerspective;
import org.pmedv.core.beans.ApplicationToolbar;
import org.pmedv.core.beans.ApplicationToolbarItem;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.components.CmdJButton;
import org.pmedv.core.context.AppContext;
import org.springframework.context.ApplicationContext;


public class ApplicationToolbarProviderImpl implements ApplicationToolbarProvider {

	private static final Log log = LogFactory.getLog(ApplicationToolbarProviderImpl.class);
	private final ApplicationContext ctx = AppContext.getApplicationContext();
	private JToolBar toolbar;

	@Override
	public JToolBar getToolbar() {
		return toolbar;
	}
	
	public ApplicationToolbarProviderImpl() {
		
		toolbar = new JToolBar();
		toolbar.setFloatable(true);

		try {
			JAXBContext c = JAXBContext.newInstance(ApplicationToolbar.class);
			
			Unmarshaller u = c.createUnmarshaller();
				
			ApplicationToolbar appToolBar  = (ApplicationToolbar)u.unmarshal(getClass().getClassLoader().getResourceAsStream("toolbar.xml"));
			
			for (ApplicationToolbarItem currentItem : appToolBar.getItems()) {
				try {

					if (currentItem.getActionClass() != null) {

						log.info("Mapping action class : "+currentItem.getActionClass());
						
						Class<?> clazz = Class.forName(currentItem.getActionClass());
						
						try {
							Command command = (Command)clazz.newInstance();							
							ImageIcon icon = null;
							String toolTipText = null;
							String text = null;
							
							if (currentItem.getImageIcon() != null) {
								
								InputStream is = getClass().getClassLoader().getResourceAsStream(currentItem.getImageIcon());												 
								icon = new ImageIcon(ImageIO.read(is));
								
								// icon = new ImageIcon(new ClassPathResource(currentItem.getImageIcon()).getFile().getAbsolutePath());
							}
							if (currentItem.getToolTipText() != null) {
								toolTipText = currentItem.getToolTipText();
							}
							if (currentItem.getText() != null) {
								text = currentItem.getText();
							}
							
							CmdJButton button = new CmdJButton("", icon );
							button.setContentAreaFilled(false);
							button.setBorderPainted(false);
							button.setSize(16, 16);
							button.setToolTipText(text);
							button.setCommand(command);
							button.setId("common");
							button.setToolTipText(toolTipText);							
							button.setFocusable(false);
							
							toolbar.add(button);

						} 
						catch (InstantiationException e) {
							log.info("could not instanciate menuitem, skipping.");
						} 
						catch (IllegalAccessException e) {
							log.info("could not access menuitem, skipping.");								
						}
						
					}
					
				} 
				catch (ClassNotFoundException e) {
					log.error("Could not instantiate class " +currentItem.getActionClass()+". Skipping.");
				} 

			}
			
			ApplicationPerspectiveProvider perspectiveProvider = (ApplicationPerspectiveProvider)ctx.getBean(BeanDirectory.PROVIDER_PERSPECTIVE);
			ArrayList<ApplicationPerspective> perspectives = perspectiveProvider.getPerspectives();
			
			for (ApplicationPerspective perspective : perspectives) {

				if (perspective.getToolbarContributions() != null) {

					for (ApplicationToolbarItem toolbarItem : perspective.getToolbarContributions()) {
						
						if (toolbarItem.getActionClass() != null) {

							log.info("Mapping action class : "+toolbarItem.getActionClass());
							
							try {
								Class<?> clazz = Class.forName(toolbarItem.getActionClass());
								
								Command command = (Command)clazz.newInstance();							
								ImageIcon icon = null;
								String toolTipText = null;
								String text = null;
								
								if (toolbarItem.getImageIcon() != null) {									
									InputStream is = getClass().getClassLoader().getResourceAsStream(toolbarItem.getImageIcon());												 
									icon = new ImageIcon(ImageIO.read(is));
								}
								if (toolbarItem.getToolTipText() != null) {
									toolTipText = toolbarItem.getToolTipText();
								}
								if (toolbarItem.getText() != null) {
									text = toolbarItem.getText();
								}
								
								CmdJButton button = new CmdJButton("", icon);
								button.setContentAreaFilled(false);
								button.setBorderPainted(false);
								button.setSize(16, 16);
								button.setToolTipText(text);
								button.setCommand(command);
								button.setId(perspective.getId());
								button.setToolTipText(toolTipText);
								button.setFocusable(false);
								
								toolbar.add(button);
		
							} 
							catch (InstantiationException e) {
								log.info("could not instanciate menuitem, skipping.");
							} 
							catch (IllegalAccessException e) {
								log.info("could not access menuitem, skipping.");								
							} 
							catch (ClassNotFoundException e) {
								log.info("could not find action class, skipping.");
							}
							
						}

					}
					
				}
				
			}
			
		}
		catch (JAXBException e) {
			log.error("could not deserialize menus.");
			throw new RuntimeException("could not deserialize menus.");							
		} 
		catch (IOException e) {
			log.error("could not load menus, exiting.");
			throw new RuntimeException("could not load menus.");							
		}
		
	}


	
}
