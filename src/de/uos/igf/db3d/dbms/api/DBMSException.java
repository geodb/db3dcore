/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Root of the exception hierarchy in the DBMS for exceptions regarding the
 * startup of the DBMS.<br>
 * <br>
 * All exceptions in this inheritance hierarchy are fatal exception signaling
 * serious problems in the database system.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class DBMSException extends DB3DException {

	/**
	 * Default Constructor
	 */
	public DBMSException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DBMSException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public DBMSException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DBMSException with specified detail message and nested
	 * Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DBMSException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DBMSException with the specified nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DBMSException(Throwable cause) {
		super(cause);
	}

}
