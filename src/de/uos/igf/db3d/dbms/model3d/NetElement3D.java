/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Point3D;

/**
 * Interface defining common methods for all net element objects in 3D.
 */
public interface NetElement3D {

	/**
	 * Returns the identifier of this.
	 * 
	 * @return int - id.
	 */
	public int getID();

	/**
	 * Sets the id to given value
	 * 
	 * @param id
	 *            int identifier of this
	 */
	public void setID(int id);

	/**
	 * Tests if this has a neighbour element at given index
	 * 
	 * @param index
	 *            int index
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasNeighbour(int index);

	/**
	 * Tests if this has neighbour elements at all.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasNeighbours();

	/**
	 * Tests if this is an interior element - it has two neighbours.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean isInterior();

	/**
	 * Returns the type of this.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType();

	/**
	 * Returns the Point3D for given index
	 * 
	 * @param index
	 *             int
	 * @return Point3D
	 */
	public Point3D getPoint(int index);

	/**
	 * Returns the geometry of the NetElement3D as a newly created array !.<br>
	 * Array gets invalid if a setPointX() method is called !
	 * 
	 * @return Point3D[] - array of Point3D objects.
	 */
	public Point3D[] getPoints();

}
