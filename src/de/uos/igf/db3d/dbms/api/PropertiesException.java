/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals problems encountered during property processing during the
 * instantiation of a correct ConnectionFactory.
 * 
 * @author Wolfgang B�r / University of Osnabrueck
 */
public final class PropertiesException extends DBMSException {

	/**
	 * Default Constructor.
	 */
	public PropertiesException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new PropertiesException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public PropertiesException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new PropertiesException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public PropertiesException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new PropertiesException with the specified nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public PropertiesException(Throwable cause) {
		super(cause);
	}

}
