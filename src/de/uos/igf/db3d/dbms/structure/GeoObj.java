/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.structure;

import de.uos.igf.db3d.dbms.geom.MBB3D;

/**
 * Interface GeoObj is a common datatype for holding the different geometric
 * object types in the framework. It also describes methods for identifying the
 * different types. The needed constants are defined in the subinterfaces split
 * in simple and complex objects.<br>
 * The only provided methods of geoobjects is getMBB which has to return an MBB
 * in 3D describing the space occupied by the object.
 */
public interface GeoObj {

	/**
	 * Method for identifying the type of object.
	 * 
	 * @return byte - constant for this type.
	 */
	public byte getType();

	/**
	 * Returns the mbb of the geoobject.
	 * 
	 * @return MBB3D of the geoobject.
	 */
	public MBB3D getMBB();

}
