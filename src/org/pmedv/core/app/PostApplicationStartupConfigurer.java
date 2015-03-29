package org.pmedv.core.app;

/**
 * The <code>PostApplicationStartupConfigurer</code> restores the application 
 * state after application startup and does all neccesary work to be done
 * after the application has been started. 
 * 
 * @author Matthias Pueski
 *
 */
public interface PostApplicationStartupConfigurer {
	
	/**
	 * restore the last opened perspective
	 */
	public void restoreLastPerspective();

}
