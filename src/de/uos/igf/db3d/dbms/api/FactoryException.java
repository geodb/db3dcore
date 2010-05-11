/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Common Exception class signals exceptions and errors in the factory classes
 * inside the DBMS.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class FactoryException extends DBMSException {

	/**
	 * Default Constructor.
	 */
	public FactoryException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new FactoryException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public FactoryException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new FactoryException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public FactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new FactoryException with the specified nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public FactoryException(Throwable cause) {
		super(cause);
	}

}
