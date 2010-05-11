/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * ContainmentException signals an exception during an Update operation if an
 * element already exists or is not existent.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class ContainmentException extends UpdateException {

	/**
	 * Default Constructor.
	 */
	public ContainmentException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new ContainmentException with the specified detail message.
	 * 
	 * @param arg0
	 *            the error message
	 */
	public ContainmentException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ContainmentException with the specified nested
	 * Throwable.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 */
	public ContainmentException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ContainmentException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 */
	public ContainmentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
