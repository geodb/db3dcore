/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * Signals the call of a not yet implemented method in the database kernel..<br>
 * Extends NestableRuntimeException from apache.commons for portability reasons.
 */
public class NotYetImplementedException extends NestableRuntimeException {

	/**
	 * Default Constructor.
	 */
	public NotYetImplementedException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new NotYetImplementedException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public NotYetImplementedException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new NotYetImplementedException with specified detail message and nested
	 * Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public NotYetImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new NotYetImplementedException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public NotYetImplementedException(Throwable cause) {
		super(cause);
	}

}
