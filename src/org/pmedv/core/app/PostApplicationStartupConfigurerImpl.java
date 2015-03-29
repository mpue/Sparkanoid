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
package org.pmedv.core.app;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.ApplicationPerspective;
import org.pmedv.core.beans.ApplicationWindowConfiguration;
import org.pmedv.core.commands.OpenPerspectiveCommand;
import org.pmedv.core.components.IMemento;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.perspectives.AbstractPerspective;
import org.pmedv.core.provider.ApplicationPerspectiveProvider;
import org.springframework.context.ApplicationContext;


public class PostApplicationStartupConfigurerImpl implements PostApplicationStartupConfigurer {

	private static final Log log = LogFactory.getLog(PostApplicationStartupConfigurerImpl.class);
	
	private ApplicationPerspectiveProvider perspectiveProvider;
	
	public PostApplicationStartupConfigurerImpl(ApplicationPerspectiveProvider provider) {
		log.info("Application initialized.");
		this.perspectiveProvider = provider;
	}
	
	/**
	 * Loads the perpective which was opened before last application shutdown
	 */
	
	public void restoreLastPerspective() {

		String inputDir = System.getProperty("user.home") + "/."+AppContext.getApplicationName()+"/";
		String inputFileName = "appWindowConfig.xml";			
		
		File output = new File(inputDir+inputFileName);
		
		if (output.exists()) {
			
			try {
				Unmarshaller u = JAXBContext.newInstance(ApplicationWindowConfiguration.class).createUnmarshaller();
				ApplicationWindowConfiguration config = (ApplicationWindowConfiguration)u.unmarshal(output);
				
				log.info("restoring last perspective : "+config.getLastPerspectiveID());
				
				OpenPerspectiveCommand open = new OpenPerspectiveCommand(config.getLastPerspectiveID());
				open.execute();
				
			} 
			catch (JAXBException e) {
				log.info("could not restore ApplicationWindowConfiguration.");				
			}			
			
		}
		else {
			log.info("No ApplicationWindowConfiguration found.");
		}
		
		ApplicationContext ctx = AppContext.getApplicationContext();
		
		for (ApplicationPerspective ap : perspectiveProvider.getPerspectives()) {
			
			AbstractPerspective a = (AbstractPerspective)ctx.getBean(ap.getId());
			
			if (a instanceof IMemento) {

				IMemento m = (IMemento)a;				
				m.loadState();
				
			}
			
		}
		

	}
	
}
