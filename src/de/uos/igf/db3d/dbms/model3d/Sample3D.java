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
 * File Sample3D.java - created on 05.06.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Point3D;

/**
 * Interface for the specifics of a Sample3D.<br>
 * Extends Spatial3D for common spatial methods.
 */
public interface Sample3D extends Spatial3D {

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
	public boolean contains(Point3D point);

}
