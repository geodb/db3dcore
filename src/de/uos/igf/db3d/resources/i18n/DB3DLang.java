/*
 * Sourcecode of the
 *
 * University of Osnabrueck
 * Institute for Geoinformatics and Remote Sensing
 *
 * Copyright (C) Researchgroup Prof. Dr. Martin Breunig
 *
 */
package de.uos.igf.db3d.resources.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uos.igf.db3d.resources.DB3DProperties;

/**
 * The central management class for internationalization resources. You can use
 * this as a static language resource throughout DB3D by simply typing e.g.<br>
 * <br>
 * <code>DB3DLang.getString("db3d.OkKey")</code><br>
 * <br>
 * Exceptions in DB3D should be gracefully caught, like this:
 * <code>catch (Exception e) {
 * 			Db3dLogger.getLogger(CurrClass.class.getName()).log(Level.ERROR,
 * 					DB3DLang.getString("db3d.errorstring"));
 * 		}</code>
 * 
 * The preset or fallback locale is en-US. The method <br>
 * <br>
 * <code>DB3DLang.setFromPropertyFile()</code><br>
 * <br>
 * is invoked once during DB3D server startup (the method loads a locale from
 * the <code>lang</code> and <code>country</code> settings in db3d.properties).<br>
 * You can dynamically change the language of the hole server by setting a new
 * Locale with e.g.<br>
 * <br>
 * <code>DB3DLang.setLocale(new Locale("de","DE"))</code><br>
 * <br>
 * whenever You need to.
 * 
 * @author Edgar Butwilowski
 */
public final class DB3DLang {

	// static use / prohibit instantiation:
	private DB3DLang(){
	}
	
	public final static String qualClassName = DB3DLang.class
			.getCanonicalName().substring(
					0,
					DB3DLang.class.getCanonicalName().length()
							- DB3DLang.class.getSimpleName().length())
			+ "Resources";
	// preset and fallback value is en-US:
	private static ResourceBundle currResourceBundle = ResourceBundle
			.getBundle(qualClassName, new Locale("en", "US"));

	public static boolean setFromPropertyFile() {

		try {
			DB3DLang.setLocale(new Locale(DB3DProperties.getProperty("lang"),
					DB3DProperties.getProperty("country")));
			return true;
		} catch (Exception e) {
			
			// TODO use of DB3DLang.class.getName()?
			
			Logger.getLogger(DB3DLang.class.getName()).log(Level.INFO,
					getString("db3d.info.load_lang"));
			return false;
		}
	}

	/**
	 * With this method You can change the application-wide language and country
	 * preset of GeoDB3D.
	 * 
	 * @param newLocale
	 *            language and country preset for GeoDB3D
	 */
	public static void setLocale(Locale newLocale) {

		// it is possible to realize a connection to the Google Translate
		// Service here...

		String language = newLocale.getLanguage();
		String country = newLocale.getCountry();
		if (newLocale != null && language != null && language.length() != 0
				&& country != null && country.length() != 0)
			currResourceBundle = ResourceBundle.getBundle(qualClassName,
					newLocale);

	}

	public static ResourceBundle getBundle() {
		return currResourceBundle;
	}

	public static String getString(String key) {
		try {
			return currResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			// if the key-resource-pair is not available:
			return "I18N.ERR:" + e.getKey();
		}
	}

}
