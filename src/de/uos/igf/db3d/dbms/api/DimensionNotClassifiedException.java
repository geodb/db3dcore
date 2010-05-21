/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

/**
 * DimensionNotClassifiedException
 */
public final class DimensionNotClassifiedException extends DataException {

	/**
	 * Default Constructor.
	 */
	public DimensionNotClassifiedException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new DimensionNotClassifiedException with the specified
	 * detail message.
	 * 
	 * @param message
	 *            the error message
	 */
	public DimensionNotClassifiedException(String message) {
		super(message);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DimensionNotClassifiedException with specified detail
	 * message and nested Throwable.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DimensionNotClassifiedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new DimensionNotClassifiedException with the specified
	 * nested Throwable.
	 * 
	 * @param cause
	 *            the exception or error that caused this exception to be thrown
	 */
	public DimensionNotClassifiedException(Throwable cause) {
		super(cause);
	}

}
