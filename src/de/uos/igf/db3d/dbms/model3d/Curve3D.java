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
 * File Curve3D.java - created on 05.06.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.Segment3D;

/**
 * Interface for the specifics of a Curve3D.<br>
 * Extends Spatial3D for common spatial methods.
 */
public interface Curve3D extends Spatial3D {

	/**
	 * Returns the length of this net as the sum of the length of its
	 * components.
	 * 
	 * @return double - length of this net.
	 */
	public double getLength();

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices in this net.
	 */
	public int countVertices();

	/**
	 * Returns the number of edges in this net.
	 * 
	 * @return int - number of edges in this net.
	 */
	public int countEdges();

	/**
	 * Tests whether this geometrically contains the given point.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Point3D point);

	/**
	 * Tests whether this contains the given segment geometrically.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Segment3D seg);
}
