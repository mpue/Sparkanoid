package org.pmedv.leveleditor;

import org.pmedv.core.app.AbstractApplication;

/**
 * This is the main application entry point.
 * 
 * @author pueski
 *
 */
public class Application extends AbstractApplication {

	protected Application(String name) {
		super(name);
	}

	public static void main(String[] args) {
			
		Application app = new Application("SparkanoidV2"); 		
		
	}
	
	
}
