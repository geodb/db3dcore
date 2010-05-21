/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.api;

import de.uos.igf.db3d.dbms.api.UpdateException;

/**
 * TopologyException signals an failure during an Update operation which is
 * cause through violation of topology consistency.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class TopologyException extends UpdateException {

	/**
	 * Default Constructor.
	 */
	public TopologyException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new TopologyException with the specified detail message.
	 * 
	 * @param arg0
	 *            the error message
	 */
	public TopologyException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TopologyException with the specified nested Throwable.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 */
	public TopologyException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new TopologyException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 */
	public TopologyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
