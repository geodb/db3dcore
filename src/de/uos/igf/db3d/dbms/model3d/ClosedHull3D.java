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
 * File ClosedHull3D.java - created on 27.01.2004
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;

/**
 * ClosedHull3D models a BoundaryVolume3D object based on closed TriangleNets.
 * This class is based on the TriangleNet3D implementations and adds
 * functionality for volume operations and closed constraints checking.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class ClosedHull3D extends SpatialObject3D implements Hull3D,
		ComplexGeoObj {

	/* the wrapped triangle net */
	private TriangleNet3D tnet;

	/* the wrapped components */
	private ClosedHull3DComp[] components;

	/* closed flag */
	private boolean closed;

	/**
	 * Constructor.
	 * 
	 * @param tnet
	 *            TriangleNet3D to wrap
	 */
	protected ClosedHull3D(TriangleNet3D tnet) {
		this.tnet = tnet;
		this.closed = false;
		buildComponents();
		checkClosure();
	}

	/**
	 * Copy Constructor.
	 * 
	 * @param chull
	 *            ClosedHull3D to copy.
	 */
	public ClosedHull3D(ClosedHull3D chull) {
		this(new TriangleNet3D(chull.getTNet()));
	}

	/**
	 * Tests whether this closed hull object is currently closed.
	 * 
	 * @return boolean - true if this is closed, false otherwise.
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Counts the number of elements in the wrapped triangle net of this.
	 * 
	 * @return int - the number of elements in the wrapped triangle net of this.
	 * @see db3d.dbms.model3d.Hull3D#countElements()
	 */
	public int countElements() {
		return getTNet().countElements();
	}

	/**
	 * Tests if the wrapped triangle net of this contains the given point.
	 * 
	 * @return boolean - true if the given point is contained in the triangle
	 *         net, false otherwise.
	 * @see db3d.dbms.model3d.Hull3D#contains(db3d.dbms.geom.Point3D)
	 */
	public boolean contains(Point3D point) {
		return getTNet().contains(point);
	}

	/**
	 * Tests if the wrapped triangle net of this contains the given segment.
	 * 
	 * @return boolean - true if the given segment is contained in the triangle
	 *         net, false otherwise.
	 * @see db3d.dbms.model3d.Hull3D#contains(db3d.dbms.geom.Segment3D)
	 */
	public boolean contains(Segment3D seg) {
		return getTNet().contains(seg);
	}

	/**
	 * Tests if the wrapped triangle net of this contains the given triangle.
	 * 
	 * @return boolean - true if the given triangle is contained in the triangle
	 *         net, false otherwise.
	 * @see db3d.dbms.model3d.Hull3D#contains(db3d.dbms.geom.Triangle3D)
	 */
	public boolean contains(Triangle3D triangle) {
		return getTNet().contains(triangle);
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * point.
	 * 
	 * @return boolean - true if the given point is strictly contained in the
	 *         triangle net, false otherwise.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Point3D)
	 */
	public boolean containsInside(Point3D point) {

		for (int i = 0; i < this.countComponents(); i++)
			if (this.getComponent(i).containsInside(point))
				return true;
		return false;
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * segment.
	 * 
	 * @return boolean - true if the given segment is strictly contained in the
	 *         triangle net, false otherwise.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Segment3D)
	 */
	public boolean containsInside(Segment3D seg) {

		for (int i = 0; i < this.countComponents(); i++)
			if (this.getComponent(i).containsInside(seg))
				return true;
		return false;
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * tetrahedron.
	 * 
	 * @return boolean - true if the given tetrahedron is strictly contained in
	 *         the triangle net, false otherwise.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Tetrahedron3D)
	 */
	public boolean containsInside(Tetrahedron3D tetra) {

		for (int i = 0; i < this.countComponents(); i++)
			if (this.getComponent(i).containsInside(tetra))
				return true;
		return false;
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * triangle.
	 * 
	 * @return boolean - true if the given triangle is strictly contained in the
	 *         triangle net, false otherwise.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Triangle3D)
	 */
	public boolean containsInside(Triangle3D triangle) {

		for (int i = 0; i < this.countComponents(); i++)
			if (this.getComponent(i).containsInside(triangle))
				return true;
		return false;
	}

	/**
	 * Counts border edges in the wrapped triangle net of this.
	 * 
	 * @return the number of the border edges in the wrapped triangle net of
	 *         this.
	 * @see db3d.dbms.model3d.Volume3D#countBorderEdges()
	 */
	public int countBorderEdges() {
		return getTNet().countEdges();
	}

	/**
	 * Count border faces in the wrapped triangle net of this.
	 * 
	 * @return the number of the border faces in the wrapped triangle net of
	 *         this.
	 * @see db3d.dbms.model3d.Volume3D#countBorderFaces()
	 */
	public int countBorderFaces() {
		// all faces are in the border of the volume
		return getTNet().countFaces();
	}

	/**
	 * Count border vertices in the wrapped triangle net of this.
	 * 
	 * @return the number of the border vertices in the wrapped triangle net of
	 *         this.
	 * @see db3d.dbms.model3d.Volume3D#countBorderVertices()
	 */
	public int countBorderVertices() {
		return getTNet().countVertices();
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param index
	 *            int
	 * @return ClosedHull3DComp
	 */
	public ClosedHull3DComp getComponent(int index) {
		return this.components[index];
	}

	/**
	 * Returns the components.
	 * 
	 * @return ClosedHull3DComp[]
	 */
	public ClosedHull3DComp[] getComponents() {
		return this.components;
	}

	/**
	 * Counts edges in the wrapped triangle net of this.
	 * 
	 * @return the number of edges in the wrapped triangle net of this.
	 * @see db3d.dbms.model3d.Volume3D#countEdges()
	 */
	public int countEdges() {
		return getTNet().countEdges();
	}

	/**
	 * Counts faces in the wrapped triangle net if this.
	 * 
	 * @return the number of faces in the wrapped triangle net of this.
	 * @see db3d.dbms.model3d.Volume3D#countFaces()
	 */
	public int countFaces() {
		return getTNet().countFaces();
	}

	/**
	 * Counts vertices in the wrapped triangle net of this.
	 * 
	 * @return the number of vertices in the wrapped triangle net of this.
	 * @see db3d.dbms.model3d.Volume3D#countVertices()
	 */
	public int countVertices() {
		return getTNet().countVertices();
	}

	/**
	 * Calculates the area of the wrapped triangle net of this.
	 * 
	 * @return double - area of the triangle net.
	 * @see db3d.dbms.model3d.Volume3D#getArea()
	 */
	public double getArea() {
		return getTNet().getArea();
	}

	/**
	 * Calculates the volume of the wrapped triangle net of this.
	 * 
	 * @return double - volume of the triangle net.
	 * @see db3d.dbms.model3d.Volume3D#getVolume()
	 */
	public double getVolume() {

		double volume = 0;
		for (int i = 0; i < countComponents(); i++)
			volume = volume + getComponent(i).getVolume();

		return volume;
	}

	/**
	 * Counts components in the wrapped triangle net of this.
	 * 
	 * @return the number of components in the triangle net.
	 * @see db3d.dbms.model3d.Spatial3D#countComponents()
	 */
	public int countComponents() {
		return getTNet().countComponents();
	}

	/**
	 * Tests if the wrapped triangle net of this intersects with the given line.
	 * 
	 * @param line
	 *            Line3D
	 * @return boolean - true if the triangle net intersects with the given
	 *         line, false otherwise.
	 * @see db3d.dbms.model3d.Spatial3D#intersects(db3d.dbms.geom.Line3D)
	 */
	public boolean intersects(Line3D line) {
		return getTNet().intersects(line);
	}

	/**
	 * Tests if the wrapped triangle net of this intersects with the given
	 * minimum bounding box.
	 * 
	 * @param mbb
	 *            MBB3D
	 * @return boolean - true if the triangle net intersects with the given
	 *         MBB3D, false otherwise.
	 * @see db3d.dbms.model3d.Spatial3D#intersects(db3d.dbms.geom.MBB3D)
	 */
	public boolean intersects(MBB3D mbb) {
		return getTNet().intersects(mbb);
	}

	/**
	 * Tests if the wrapped triangle net of this intersects with the given
	 * plane.
	 * 
	 * @param plane
	 *            Plane3D
	 * @return boolean - true if the triangle net intersects with the given
	 *         plane.
	 * @see db3d.dbms.model3d.Spatial3D#intersects(db3d.dbms.geom.Plane3D)
	 */
	public boolean intersects(Plane3D plane) {
		return getTNet().intersects(plane);
	}

	/**
	 * Returns the data type of this geo-object as a constant number.
	 * 
	 * @return byte - data type of this regarded as a geo-object.
	 * @see db3d.dbms.structure.GeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.CLOSED_HULL_3D;
	}

	/**
	 * Returns the data type of this spatial object as a constant number.
	 * 
	 * @return byte - data type of this regarded as a Spatial3D object.
	 * @see db3d.dbms.model3d.Spatial3D#getSpatial3DType()
	 */
	public byte getSpatial3DType() {
		return Spatial3D.HULL_3D;
	}

	/**
	 * Marks the end of an update. Resets the update flag and begins updating
	 * the net.
	 * 
	 * @see db3d.dbms.model3d.Spatial3D#endUpdate()
	 */
	public void endUpdate() {
		getTNet().endUpdate();
	}

	/**
	 * Updates the MBB of the net. Iterates over all components updating and
	 * union their mbbs. Sets the updated MBB in the abstract SpatialObject.
	 * Updates the index in which the net is.
	 * 
	 * @see db3d.dbms.model3d.SpatialObject3D#updateMBB()
	 */
	protected void updateMBB() {
		getTNet().updateMBB();
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param comp
	 *            ClosedHull3DComp
	 */
	public void addComponent(ClosedHull3DComp comp) {
		getTNet().addComponent(comp.getTNetComp());

		// add also to the wrapped array
		ClosedHull3DComp[] comps = getComponents();
		ClosedHull3DComp[] temp = new ClosedHull3DComp[comps.length + 1];
		for (int i = 0; i < comps.length; i++)
			temp[i] = comps[i];

		temp[comps.length] = comp;
		this.components = temp;
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param comp
	 *            ClosedHull3DComp
	 */
	public void removeComponent(ClosedHull3DComp comp) {
		getTNet().removeComponent(comp.getTNetComp());

		// remove also from wrapped array
		ClosedHull3DComp[] comps = getComponents();
		ClosedHull3DComp[] temp = new ClosedHull3DComp[comps.length - 1];
		int x = 0;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] != comp) {
				temp[x] = comps[i];
				x++;
			}
		}
		this.components = temp;
	}

	/*
	 * Returns the wrapped triangle net of this.
	 * 
	 * @return TriangleNet3D - the wrapped triangle net of this.
	 */
	private TriangleNet3D getTNet() {
		return this.tnet;
	}

	private void buildComponents() {
		this.components = new ClosedHull3DComp[this.countComponents()];
		for (int i = 0; i < this.components.length; i++)
			this.components[i] = new ClosedHull3DComp(this, tnet
					.getComponent(i));
	}

	/*
	 * Tests if all components of this are closed and updates the closed-flag
	 * accordingly.
	 * 
	 * @return boolean - true if all components of this are closed, false
	 * otherwise.
	 */
	private void checkClosure() {

		for (int i = 0; i < this.countComponents(); i++) {
			if (!this.getComponent(i).checkClosed()) {
				this.closed = false;
				return;
			}
		}
		this.closed = true;
	}

	/*
	 * Returns the element with the given id.
	 * 
	 * @param id id of the element
	 * 
	 * @returns SimpleGeoObj - element with the given id.
	 * 
	 * @see de.uos.igf.db3d.dbms.model3d.ComplexGeoObj#getElement(int)
	 */
	public SimpleGeoObj getElement(int id) {
		for (ClosedHull3DComp comp : components) {
			/*
			 * TODO: which is faster:
			 * 
			 * tempComp = comp.getElement(id) and then return tempComp OR
			 * 
			 * this method without direct assignment, but if something was
			 * found, then run the method twice?
			 */

			if (comp.getElement(id) != null)
				return comp.getElement(id);
		}
		return null;
	}
}
