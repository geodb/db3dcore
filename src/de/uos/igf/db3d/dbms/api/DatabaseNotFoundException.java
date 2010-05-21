/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals that the database is not found by the system. <br>
 * <br>
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class DatabaseNotFoundException extends DBMSException {

	/**
	 * Default Construtor
	 */
	public DatabaseNotFoundException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DatabaseNotFoundException with the specified detail
	 * message.
	 * 
	 * @param message
	 *            the error message
	 */
	public DatabaseNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DatabaseNotFoundException with specified detail message
	 * and nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DatabaseNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DatabaseNotFoundException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DatabaseNotFoundException(Throwable cause) {
		super(cause);
	}
}
