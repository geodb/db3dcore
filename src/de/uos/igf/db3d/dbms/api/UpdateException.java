/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

import org.apache.commons.lang.exception.NestableException;

/**
 * UpdateException is the common exception class for all exceptions which can
 * occur during update operations like add / remove ...
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class UpdateException extends NestableException {

	/**
	 * Default Constructor
	 */
	public UpdateException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new UpdateException with the specified detail message.
	 * 
	 * @param arg0
	 *            the error message
	 */
	public UpdateException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new UpdateException with the specified nested Throwable.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 */
	public UpdateException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new UpdateException with specified detail message and nested
	 * Throwable.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 */
	public UpdateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
