/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * Signals that a Transaction is already in progress.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class TransactionInProgressException extends TransactionException {

	/**
	 * Default Constructor.
	 */
	public TransactionInProgressException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new TransactionInProgressException with the specified detail
	 * message.
	 * 
	 * @param message
	 *            the error message
	 */
	public TransactionInProgressException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TransactionInProgressException with specified detail
	 * message and nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public TransactionInProgressException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TransactionInProgressException with the specified nested
	 * Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public TransactionInProgressException(Throwable cause) {
		super(cause);
	}

}
