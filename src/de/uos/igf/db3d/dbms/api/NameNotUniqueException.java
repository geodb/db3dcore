/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * NameNotUniqueException signals that a used lookup name somewhere in the DBMS
 * was not unique and so the operation couldn't be done.
 */
public final class NameNotUniqueException extends DataException {

	/**
	 * Default Constructor.
	 */
	public NameNotUniqueException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new NameNotFoundException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public NameNotUniqueException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new NameNotFoundException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public NameNotUniqueException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new NameNotFoundException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public NameNotUniqueException(Throwable cause) {
		super(cause);
	}

}
