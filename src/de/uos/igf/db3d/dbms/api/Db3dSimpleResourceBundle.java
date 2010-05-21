/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This wrapper for the Java API <code>ResourceBundle</code> class adds a
 * default (failure tolerant) behaviour if no resource bundle for a certain
 * locale can be found.
 * 
 * @author Edgar Butwilowski
 */
public class Db3dSimpleResourceBundle {

	private static ResourceBundle resourceBundle;

	static {
		// get locale of system:
		Locale loc = new Locale(System.getProperty("user.language"));
		try {
			resourceBundle = ResourceBundle.getBundle("Resources", loc);
		} catch (MissingResourceException mre) {
			// if no resource for system locale available, try "en" (default):
			loc = new Locale("en");
			resourceBundle = ResourceBundle.getBundle("Resources", loc);
		}
	}

	public static String getString(String key) {
		return resourceBundle.getString(key);
	}

}
