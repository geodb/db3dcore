/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

import org.apache.commons.lang.exception.NestableException;

/**
 * Root class of the exception hierarchy in the DB3D DBMS.<br>
 * Extends NestableException from apache.commons for portability reasons.
 */
public class DB3DException extends NestableException {

	/**
	 * Default Constructor
	 */
	public DB3DException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DB3DException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public DB3DException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DB3DException with specified detail message and nested
	 * Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DB3DException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DB3DException with the specified nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DB3DException(Throwable cause) {
		super(cause);
	}

}
