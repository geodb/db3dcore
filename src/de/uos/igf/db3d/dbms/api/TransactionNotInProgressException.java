/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals that a Transaction is not in progress where one is needed.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class TransactionNotInProgressException extends
		TransactionException {

	/**
	 * Default Constructor
	 */
	public TransactionNotInProgressException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new TransactionNotInProgressException with the specified
	 * detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public TransactionNotInProgressException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TransactionNotInProgressException with specified detail
	 * message and nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public TransactionNotInProgressException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TransactionNotInProgressException with the specified
	 * nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public TransactionNotInProgressException(Throwable cause) {
		super(cause);
	}

}
