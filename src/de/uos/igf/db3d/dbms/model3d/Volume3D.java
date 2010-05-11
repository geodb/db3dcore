/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;

/**
 * Interface for the specifics of a Volume3D.<br>
 * Extends Spatial3D for common spatial methods.
 */
public interface Volume3D extends Spatial3D {

	/**
	 * Returns the volume of this net as the sum of the volumes of its
	 * components.
	 * 
	 * @return double - volume.
	 */
	public double getVolume();

	/**
	 * Returns the boundary area of this net as the sum of the areas of its
	 * components.
	 * 
	 * @return double - boundary area.
	 */
	public double getArea();

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices();

	/**
	 * Returns the number of edges in this net.
	 * 
	 * @return int - number of edges.
	 */
	public int countEdges();

	/**
	 * Returns the number of faces in this net.
	 * 
	 * @return int - number of faces.
	 */
	public int countFaces();

	/**
	 * Returns the number of vertices in the border of his net.
	 * 
	 * @return int - number of vertices in the border.
	 */
	public int countBorderVertices();

	/**
	 * Returns the number of edges in the border of this net.
	 * 
	 * @return int - number of edges in the border.
	 */
	public int countBorderEdges();

	/**
	 * Returns the number of faces in the border of this net.
	 * 
	 * @return int - number of faces in the border.
	 */
	public int countBorderFaces();

	/**
	 * Tests whether this contains the given point geometrically inside.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained inside, false otherwise.
	 */
	public boolean containsInside(Point3D point);

	/**
	 * Tests whether this contains the given segment geometrically inside
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is contained inside, false otherwise.
	 */
	public boolean containsInside(Segment3D seg);

	/**
	 * Tests whether this contains the given triangle geometrically inside.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained inside, false otherwise.
	 */
	public boolean containsInside(Triangle3D triangle);

	/**
	 * Tests whether this contains the given tetrahedron geometrically inside.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsInside(Tetrahedron3D tetra);

}
