/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * Root class of the runtime exception hierarchie in the DB3D DBMS.<br>
 * Extends NestableException from apache.commons for portability reasons.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class DB3DRuntimeException extends NestableRuntimeException {

	/**
	 * Default Constructor.
	 */
	public DB3DRuntimeException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DB3DRuntimeException with the specified detail message.
	 * 
	 * @param arg0
	 *            the error message
	 */
	public DB3DRuntimeException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DB3DRuntimeException with the specified nested
	 * Throwable.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 */
	public DB3DRuntimeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DB3DRuntimeException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 */
	public DB3DRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
