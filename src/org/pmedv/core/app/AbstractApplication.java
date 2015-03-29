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

import java.awt.EventQueue;
import java.io.File;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.core.util.CheckEnv;
import org.pmedv.core.util.FileUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This is the abstract base mother of the application which provides some basic
 * functionality and invokes the application window.
 * 
 * @author Matthias Pueski
 * 
 */
public abstract class AbstractApplication {

	private static final Log log = LogFactory.getLog(AbstractApplication.class);

	protected ApplicationContext ctx;
	protected ApplicationWindow win;

	private Properties properties;
	private String currentDir;
	private SplashScreen splashScreen;

	protected AbstractApplication(String name) {
		
		
		/**
		 * Initialize spring application context.
		 */

		try {
			properties = CheckEnv.getEnvVars();
		} catch (Throwable e) {
			properties = new Properties();
		}

		currentDir = new File(System.getProperty("user.home")).getAbsolutePath();

		String datadir = currentDir + "/."+name;

		File f = new File(datadir);

		if (!f.exists()) {
			log.info("Creating directory " + datadir);
			FileUtils.makeDirectory(datadir);
		}

		AppContext.setApplicationName(name);
		
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		// displaySplashScreen(ctx);

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				/**
				 * Invoke the application window
				 */

				win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);
				win.setVisible(true);

				/**
				 * Configure window after startup
				 */

				PostApplicationStartupConfigurer configurer = (PostApplicationStartupConfigurer) ctx.getBean("configurer");
				configurer.restoreLastPerspective();
				// destroySplashScreen();

			}
		});

	}

	private void displaySplashScreen(BeanFactory beanFactory) {
		try {

			this.splashScreen = (SplashScreen) ctx.getBean(BeanDirectory.SPLASH_SCREEN);
			log.info("Displaying application splash screen...");

		} catch (Exception e) {
			log.warn("Unable to load and display startup splash screen.", e);
		}
	}

	private void destroySplashScreen() {
		if (splashScreen != null) {
			log.debug("Closing splash screen...");
			new SplashScreenCloser(splashScreen);
		}
	}

	/**
	 * Closes the splash screen in the event dispatching (GUI) thread.
	 * 
	 * @author Keith Donald
	 * @see SplashScreen
	 */
	public static class SplashScreenCloser {

		/**
		 * Closes the currently-displayed, non-null splash screen.
		 * 
		 * @param splashScreen
		 */
		public SplashScreenCloser(final SplashScreen splashScreen) {

			/**
			 * Removes the splash screen.
			 * 
			 * Invoke this <code> Runnable </code> using <code>
             * EventQueue.invokeLater </code>
			 * , in order to remove the splash screen in a thread-safe manner.
			 */
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					splashScreen.dispose();
				}
			});
		}
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the currentDir
	 */
	public String getCurrentDir() {
		return currentDir;
	}

	/**
	 * @param currentDir
	 *            the currentDir to set
	 */
	public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}

	
}
