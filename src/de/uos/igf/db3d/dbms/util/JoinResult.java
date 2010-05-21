/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.util;

/**
 * The class JoinResult models a value object for holding the result pairs of a
 * spatial join query. The first part of the pair always comes from the <code>
 * this</code>
 * object as given through the performed method call.<br>
 * <br>
 * Transient result value class ! <br>
 * <br>
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class JoinResult {

	/** object of <code>this</code> in join query */
	private final Object thisObject;

	/** object of the argument RStar tree in query */
	private final Object argObject;

	/**
	 * Constructor.
	 */
	public JoinResult(Object thisObject, Object argObject) {
		this.thisObject = thisObject;
		this.argObject = argObject;
	}

	/**
	 * Returns the first object of this spatial join result pair.<br>
	 * Returned object is an entry from the <code>this</code> RStar.
	 * 
	 * @return Object - first object.
	 */
	public Object getFirst() {
		return thisObject;
	}

	/**
	 * Returns the second object of this spatial join result pair.<br>
	 * Returned object is an entry from the argument RStar.
	 * 
	 * @return Object - second object.
	 */
	public Object getSecond() {
		return argObject;
	}

}
