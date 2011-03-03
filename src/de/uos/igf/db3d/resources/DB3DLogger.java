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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.uos.igf.db3d.resources.i18n.DB3DLang;

/**
 * A preconfigured logging object...
 * 
 * @author Edgar Butwilowski
 */
public final class DB3DLogger {

	public static Logger logger = Logger.getLogger("", DB3DLang.qualClassName
			+ "_" + DB3DLang.getBundle().getLocale());
	public final static String logFolderString = "logs/";

	/*
	 * Factory method returns a preconfigured logging object. The logging
	 * messages go to logging files in the <code>logs/</code> folder in the app
	 * directory. Through the parameter <code>useStandardHandler</code> it is
	 * possible to define whether the standard logging handler configuration of
	 * Java should also be used.
	 * 
	 * @return preconfigured or standard configured logging object, never
	 * <tt>null</tt>.
	 */
	static {

		try {

			File logFolder = new File(logFolderString);

			if (!logFolder.exists()) {
				logFolder.mkdir();
			}

			// file handler for the logging with up to 50,000 bytes per
			// file, file name pattern db3d[NUMBER].log, and number
			// counting +1 every step:
			Handler fh = new FileHandler(logFolder + "/db3d%g.log", 50000, 1,
					true);
			fh.setFormatter(new SimpleFormatter());

			String useStandardHandlerString = DB3DProperties
					.getProperty("db3d.logging.use_standard_handler");
			// use_standard_handler defaults to true:
			boolean useStandardHandler = true;
			if ("false".equals(useStandardHandlerString)) {
				useStandardHandler = false;
			}
			if (!useStandardHandler) {
				for (Handler handler : logger.getHandlers()) {
					logger.removeHandler(handler);
				}
			}

			logger.addHandler(fh);
			logger.setLevel(Level.FINEST);

		} catch (Exception ex) {
			new Exception("Logging in logs/db3d%g.log file not possible.", ex)
					.printStackTrace();
		}

	}

	/**
	 * Reactivates output to the console.
	 */
	public static void reactivateConsoleOutput() {
		boolean noConsoleOutput = true;
		Handler[] handlersOfDb3d = logger.getHandlers();
		for (Handler handlerOfDb3d : handlersOfDb3d) {
			if (handlerOfDb3d instanceof ConsoleHandler)
				noConsoleOutput = false;
		}
		if (noConsoleOutput) {
			logger.addHandler(new ConsoleHandler());
		}
	}

	/**
	 * Oppresses all output to the console.
	 */
	public static void setQuiet() {
		Handler[] handlersOfDb3d = logger.getHandlers();
		for (Handler handlerOfDb3d : handlersOfDb3d) {
			if (handlerOfDb3d instanceof ConsoleHandler)
				logger.removeHandler(handlerOfDb3d);
		}
	}

}
