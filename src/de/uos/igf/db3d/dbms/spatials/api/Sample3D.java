/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.api;

import de.uos.igf.db3d.dbms.spatials.standard3d.Point3DElement;

/**
 * Interface for the specifics of a Sample3D.<br>
 * Extends Spatial3D for common spatial methods.
 */
public interface Sample3D extends Net3D {

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices();

	/**
	 * Tests whether this geometrically contains the given point.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Point3DElement point);

}
