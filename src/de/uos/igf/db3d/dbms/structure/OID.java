/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.structure;

/**
 * Interface for an Object Identifier OID.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public interface OID {

	/**
	 * Returns the Project ID of the object in the dbms.
	 * 
	 * @return int - the Project ID of the object in the dbms.
	 */
	public int getProject();

	/**
	 * Returns the Space ID of the object in the project.
	 * 
	 * @return int - the Space ID of the object in the project.
	 */
	public int getSpace();

	/**
	 * Returns the Object ID of the object in the space.
	 * 
	 * @return int - the Object ID of the object in the space.
	 */
	public int getObject();

	/**
	 * Tests whether this OID refers to an object in the database or to an
	 * object in workspace as a result of an operation.
	 * 
	 * @return true - if object in DB - so the get methods can be used.
	 */
	public boolean isDBObject();

} // end OID
