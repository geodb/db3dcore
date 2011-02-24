/*
 * Sourcecode of the
 *
 * University of Osnabrueck
 * Institute for Geoinformatics and Remote Sensing
 *
 * Copyright (C) Researchgroup Prof. Dr. Martin Breunig
 *
 */
package de.uos.igf.db3d.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A preconfigured properties object... <br>
 * Easy to use:<br>
 * <code>
 * DB3DProperties.getProperty("mypropertyname");
 * DB3DProperties.setProperty("mypropertyname", "42");
 * DB3DProperties.store();
 * </code>
 * 
 * @author Edgar Butwilowski
 */
public final class DB3DProperties {

	private final static Properties db3dProperties = new Properties();
	private static File propertiesFile;

	static {

		/*
		 * CAUTION!: do not use logging in this static constructor, since the
		 * logging architecture of Db3dLogger itself depends on an object of
		 * this class. Simply use printStackTrace() method instead.
		 */

		try {

//			String appFolderString = DB3DProperties.class.getResource(
//					DB3DProperties.class.getSimpleName() + ".class").toString();
//			
//			System.out.println(":::::::::::::::::::::::::::::::::: "+appFolderString);
//
//			/*
//			 * cut
//			 * "file:/.../DB3DoDB/bin/de/uos/igf/db3d/resources/Db3dProperties.class"
//			 * to ".../DB3DoDB/":
//			 */
//			appFolderString = appFolderString.substring(5, appFolderString
//					.length()
//					- (DB3DProperties.class.getCanonicalName().length() + 10));
//
//			String propertiesFolderString = appFolderString + "properties";
			
			String propertiesFolderString = "etc/";
			
			File propertiesFolder = new File(propertiesFolderString);

			if (!propertiesFolder.exists()) {
				propertiesFolder.mkdir();
			}

			propertiesFile = new File(propertiesFolderString
					+ "/db3d.properties");

			if (!propertiesFile.exists()) {
				propertiesFile.createNewFile();
			}

			if (propertiesFile.exists()) {
				db3dProperties.load(new FileInputStream(propertiesFile));
			}

		} catch (Exception ex) {
			// Db3dLogger can not be used here, since the logger needs the
			// Db3dProperties, so:
			System.out.println("DB3DProperties: Can not find db3d.properties file "
					+ "in properties/ folder of the server.");
		}

	}

	// prohibit instantiation:
	private DB3DProperties() {
	}

	public static void store(String comments) throws IOException {
		if (propertiesFile != null && propertiesFile.exists()) {
			try {
				FileOutputStream fw = new FileOutputStream(propertiesFile);
				db3dProperties.store(fw, comments);
			} catch (IOException e) {
				IOException ioe = new IOException(
						"Could not write to the DB3D properties file.");
				ioe.initCause(e);
				throw ioe;
			}
		}
	}

	public static void store() throws IOException {
		store(null);
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns
	 * <code>null</code> if the property is not found.
	 * 
	 * @param key
	 *            the property key.
	 * @return the value in this property list with the specified key value.
	 * @see #setProperty
	 * @see #defaults
	 */
	public static String getProperty(String key) {
		return db3dProperties.getProperty(key);
	}

	/**
	 * Calls the <tt>Hashtable</tt> method <code>put</code>z. Provided for
	 * parallelism with the <tt>getProperty</tt> method. Enforces use of strings
	 * for property keys and values. The value returned is the result of the
	 * <tt>Hashtable</tt> call to <code>put</code>.
	 * 
	 * @param key
	 *            the key to be placed into this property list.
	 * @param value
	 *            the value corresponding to <tt>key</tt>.
	 * @return the previous value of the specified key in this property list, or
	 *         <code>null</code> if it did not have one.
	 * @see #getProperty
	 * @since 1.2
	 */
	public static Object setProperty(String key, String value) {
		return db3dProperties.setProperty(key, value);
	}

}
