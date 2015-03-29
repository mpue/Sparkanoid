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
package org.pmedv.core.services;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceServiceImpl implements ResourceService {

	private static ResourceBundle myResourceBundle;

	public ResourceServiceImpl() {
		myResourceBundle = ResourceBundle.getBundle("MessageResources", Locale.getDefault());
	}

	public String getResourceByKey(String key) {

		String message;

		try {
			message = myResourceBundle.getString(key);
		}
		catch (MissingResourceException m) {
			message = key;
		}
		return message;
	}

	public ImageIcon getIcon(String key) {

		ImageIcon icon = null;

		try {

			String res = getResourceByKey(key);

			InputStream is = getClass().getClassLoader().getResourceAsStream(res);
			
			if (is != null)			
				icon = new ImageIcon(ImageIO.read(is));

			// icon = new ImageIcon(new
			// ClassPathResource(getResourceByKey(key)).getFile().getAbsolutePath());

			if (icon == null)
				return new ImageIcon();
			else
				return icon;

		}
		catch (IOException e) {
			return new ImageIcon();
		}

	}

	@Override
	public ImageIcon getIcon(String key, Object... params) {

		ImageIcon icon = null;

		try {

			String res = getResourceByKey(key);

			res = MessageFormat.format(res, params);

			InputStream is = getClass().getClassLoader().getResourceAsStream(res);
			icon = new ImageIcon(ImageIO.read(is));

			if (icon == null)
				return new ImageIcon();
			else
				return icon;

		}
		catch (IOException e) {
			return new ImageIcon();
		}
	}

	@Override
	public String getResourceByKey(String key, Object... params) {
		
		String message;

		try {
			message = myResourceBundle.getString(key);

			message = MessageFormat.format(message, params);

		}
		catch (MissingResourceException m) {
			message = key;
		}
		return message;
	}

}
