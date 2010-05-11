/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import org.apache.commons.lang.exception.NestableRuntimeException;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;

/**
 * Signals an exception during validation of a SimpleGeoObj object.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class ValidationException extends NestableRuntimeException {

	/* the SimpleGeoObj instance with caused this ValidationException */
	private SimpleGeoObj execptionCause;

	/**
	 * Default Constructor.
	 */
	public ValidationException() {
		super();
	}

	/**
	 * Constructor. <br>
	 * Constructs a new ValidationException with the specified detail message.
	 * 
	 * @param arg0
	 *            the error message
	 */
	public ValidationException(String arg0) {
		super(arg0);
		this.execptionCause = null;
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ValidationException with specified detail message and a
	 * SimpleGeoObj.
	 * 
	 * @param arg0
	 *            the error message
	 * @param geo
	 *            object that caused the exception
	 */
	public ValidationException(String arg0, SimpleGeoObj geo) {
		super(arg0);
		this.execptionCause = geo;
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ValidationException with a nested Throwable.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 */
	public ValidationException(Throwable arg0) {
		super(arg0);
		this.execptionCause = null;
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ValidationException with specified detail message and a
	 * SimpleGeoObj.
	 * 
	 * @param arg0
	 *            the exception or error that caused this exception to be thrown
	 * @param geo
	 *            object that caused the exception
	 */
	public ValidationException(Throwable arg0, SimpleGeoObj geo) {
		super(arg0);
		this.execptionCause = geo;
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ValidationException with specified detail message, a
	 * nested Throwable and a SimpleGeoObj.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 * @param geo
	 *            object that caused the exception
	 */
	public ValidationException(String arg0, Throwable arg1, SimpleGeoObj geo) {
		super(arg0, arg1);
		this.execptionCause = geo;
	}

	/**
	 * Constructor.<br>
	 * Constructs a new ValidationException with specified detail message and
	 * nested Throwable.
	 * 
	 * @param arg0
	 *            the error message
	 * @param arg1
	 *            the exception or error that caused this exception to be thrown
	 */
	public ValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.execptionCause = null;
	}

	/**
	 * Returns the error message. Overloaded to include the causing SimpleGeoObj
	 * instance.
	 * 
	 * @return String - the error message.
	 */
	public String getMessage() {
		if (super.getMessage() != null) {
			if (execptionCause != null)
				return super.getMessage() + "\n "
						+ Db3dSimpleResourceBundle.getString("db3d.geom.cause")
						+ ":\n" + execptionCause.toString();
			return super.getMessage();
		}
		return null;
	}

}
