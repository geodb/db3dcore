/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.api;

import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;

/**
 * Interface GeoObj is a common datatype for holding the different geometric
 * object types in the framework. It also describes methods for identifying the
 * different types. The needed constants are defined in the subinterfaces split
 * in simple and complex objects.<br>
 * The only provided methods of geoobjects is getMBB which has to return an MBB
 * in 3D describing the space occupied by the object.
 */
public interface Spatial3D extends Spatial {

	/** defines the maximum entries per node in the SAM */
	public static final short MAX_SAM = 8;

	/**
	 * Returns the mbb of the geoobject.
	 * 
	 * @return MBB3D of the geoobject.
	 */
	public MBB3D getMBB();

}
