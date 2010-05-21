/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals that the database is already open.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class DatabaseOpenException extends DBMSException {

	/**
	 * Default Constructor.
	 */
	public DatabaseOpenException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DatabaseOpenException with the specified detail message.
	 * 
	 * @param messages
	 *            the error message
	 */
	public DatabaseOpenException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DatabaseOpenException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DatabaseOpenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DatabaseOpenException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DatabaseOpenException(Throwable cause) {
		super(cause);
	}

}
