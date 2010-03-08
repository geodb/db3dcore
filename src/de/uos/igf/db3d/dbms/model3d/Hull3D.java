/*
 * Source Code of the Research Project
 * "Development of Component-Software for the Internet-Based
 * Access to Geo-Database Services"
 *
 * University of Osnabrueck
 * Research Center for Geoinformatics and Remote Sensing
 *
 * Copyright (C) 2002-2005 Research Group Prof. Dr. Martin Breunig
 *
 * File Hull3D.java - created on 04.08.2003
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
