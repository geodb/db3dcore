/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals that the database is closed.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class DatabaseClosedException extends DBMSException {

	/**
	 * Default Constructor
	 */
	public DatabaseClosedException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DatabaseClosedException with the specified detail
	 * message.
	 * 
	 * @param message
	 *            the error message
	 */
	public DatabaseClosedException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DatabaseClosedException with specified detail message
	 * and nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DatabaseClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DatabaseClosedException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DatabaseClosedException(Throwable cause) {
		super(cause);
	}

}
