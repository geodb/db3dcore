/*
 * Sourcecode of the
 *
 * University of Osnabrueck
 * Institute for Geoinformatics and Remote Sensing
 *
 * Copyright (C) Researchgroup Prof. Dr. Martin Breunig
 *
 * File created on 27.01.2004
 */
package de.uos.igf.db3d.dbms.api;

import de.uos.igf.db3d.dbms.api.UpdateException;

/**
 * GeometryException signals an failure during an Update operation which is
 * caused by violation of geometry consistency.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class GeometryException extends UpdateException {

	/*
	 * Default Constructor.
	 */
	public GeometryException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new GeometryException with the specified detail message.
	 * 
	 * @param arg0
	 *            the error message
	 */
	public GeometryException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new GeometryException with the specified nested Throwable.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 */
	public GeometryException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor.<br>
	 * Constructs a new GeometryException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 */
	public GeometryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
