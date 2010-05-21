/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Root of the exception hierarchy in the DBMS for exceptions regarding the data
 * stored in the DBMS.<br>
 * <br>
 * All exceptions in this inheritance hierarchy are non-fatal exceptions
 * signaling data integrity problems in the database system.
 */
public class DataException extends DB3DException {

	/**
	 * Default Constructor
	 */
	public DataException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DataException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public DataException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DataException with specified detail message and nested
	 * Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DataException with the specified nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DataException(Throwable cause) {
		super(cause);
	}

}
