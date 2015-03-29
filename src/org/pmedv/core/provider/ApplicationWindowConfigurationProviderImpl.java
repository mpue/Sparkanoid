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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.ApplicationWindowConfiguration;

public class ApplicationWindowConfigurationProviderImpl implements ApplicationWindowConfigurationProvider {

	private static final Log log = LogFactory.getLog(ApplicationWindowConfigurationProviderImpl.class);

	private ApplicationWindowConfiguration config;

	public ApplicationWindowConfiguration getConfig() {
		return config;
	}

	public ApplicationWindowConfigurationProviderImpl() {

		try {
			JAXBContext c = JAXBContext.newInstance(ApplicationWindowConfiguration.class);
			Unmarshaller u = c.createUnmarshaller();

			ApplicationWindowConfiguration appWindowConfig = (ApplicationWindowConfiguration) u
					.unmarshal(getClass().getClassLoader().getResourceAsStream("appWindowConfig.xml"));

			config = appWindowConfig;
		}
		catch (JAXBException e) {
			log.error("could not deserialize application window configuration.");
			throw new RuntimeException("could not deserialize application window configuration.");
		}
		catch (Exception e) {
			log.error("could not load application window configuration.");
			throw new RuntimeException("could not load application window configuration.");
		}
	}

}
