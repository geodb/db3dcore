/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals exceptions with transactions in the dbms.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class TransactionException extends DB3DRuntimeException {

	/**
	 * Default Constructor.
	 */
	public TransactionException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new TransactionException with the specified detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public TransactionException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TransactionException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TransactionException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public TransactionException(Throwable cause) {
		super(cause);
	}

}
