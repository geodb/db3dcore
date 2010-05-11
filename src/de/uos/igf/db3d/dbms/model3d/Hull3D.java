/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;

/**
 * Interface for the specifics of a BoundaryVolume3D.<br>
 * Extends Volume3D for methods common to all volumes.
 */
public interface Hull3D extends Volume3D {

	/**
	 * Tests whether this geometrically contains the given point on the surface
	 * of the volume.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Point3D point);

	/**
	 * Tests whether this contains the given segment geometrically on the
	 * surface of the volume.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Segment3D seg);

	/**
	 * Tests whether this contains the given triangle geometrically on the
	 * surface of the volume.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Triangle3D triangle);

}
