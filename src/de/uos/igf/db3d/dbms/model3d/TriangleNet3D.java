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
 * File TriangleNet3D.java - created on 05.06.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;

//import com.odi.ObjectStore;

import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * TriangleNet3D is the concrete subclass of SpatialObject3D representing a
 * triangle net with several components. All components are referenced as
 * TriangleNet3DComp objects.
 */
public class TriangleNet3D extends SpatialObject3D implements Surface3D,
		ComplexGeoObj {

	/** components of this */
	protected TriangleNet3DComp[] components;

	/**
	 * Constructor.<br>
	 * 
	 * @param sop
	 *            ScalarOperator needed for validation
	 */
	public TriangleNet3D(ScalarOperator sop) {
		components = null;
		setScalarOperator(sop);
		setMBB(null);
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the TriangleNetBuilder class.
	 * 
	 * @param components
	 *            TriangleNet3DComp[]
	 * @param sop
	 *            ScalarOperator needed for validation
	 */
	protected TriangleNet3D(TriangleNet3DComp[] components, ScalarOperator sop) {
		super();
		this.components = components;
		this.setScalarOperator(sop);
		updateMBB();
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given TriangleNet3D is not copied - so this is a free TriangleNet3D. It
	 * is not registered in the Space3D and the corresponding thematic is gone
	 * away !
	 * 
	 * @param net
	 *            TriangleNet3D
	 */
	public TriangleNet3D(TriangleNet3D net) {
		super();
		TriangleNet3DComp[] comps = net.getComponents();
		this.components = new TriangleNet3DComp[comps.length];
		for (int i = 0; i < comps.length; i++) {
			this.components[i] = comps[i].serializationCopy();
			this.components[i].setNet(this);
		}
		this.setScalarOperator(net.getScalarOperator().copy());
		updateMBB();
	}

	/**
	 * Returns the number of components in the net.
	 * 
	 * @return int - number of components.
	 */
	public int countComponents() {
		return getComponents().length;
	}

	/**
	 * Returns the components of this net.
	 * 
	 * @return TriangleNet3DComp[] - array of components.
	 */
	public TriangleNet3DComp[] getComponents() {

		// TODO Fix this stuff!

		// ObjectStore.fetch(this);
		// ObjectStore.fetch(components);
		return this.components;
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param index
	 *            int
	 * @return TriangleNet3DComp.
	 */
	public TriangleNet3DComp getComponent(int index) {
		return this.components[index];
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param comp
	 *            TriangleNet3DComp to be added
	 */
	public void addComponent(TriangleNet3DComp comp) {
		// set the element ids for this net
		Iterator it = comp.getElementsViaRecursion().iterator();
		while (it.hasNext())
			((TriangleElt3D) it.next()).setID(this.nextElementID());

		// set component id
		int id = this.nextComponentID();
		comp.setComponentID(id);

		TriangleNet3DComp[] comps = getComponents();

		TriangleNet3DComp[] temp;
		if (comps == null) {
			temp = new TriangleNet3DComp[1];
			temp[0] = comp;
		} else {
			temp = new TriangleNet3DComp[comps.length + 1];
			for (int i = 0; i < comps.length; i++)
				temp[i] = comps[i];
			temp[comps.length] = comp;
		}

		// set net
		comp.setNet(this);
		setComponents(temp);
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param comp
	 *            TriangleNet3DComp to be removed
	 */
	public void removeComponent(TriangleNet3DComp comp) {
		TriangleNet3DComp[] comps = getComponents();
		TriangleNet3DComp[] temp = new TriangleNet3DComp[comps.length - 1];
		int x = 0;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] != comp) {
				temp[x] = comps[i];
				x++;
			}
		}
		setComponents(temp);
	}

	/**
	 * Creates and returns a new component of the net.
	 * 
	 * @return TriangleNet3DComp that has been created.
	 */
	public TriangleNet3DComp createComponent() {
		TriangleNet3DComp comp = new TriangleNet3DComp(getScalarOperator()
				.copy());
		addComponent(comp);
		return comp;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned net will have the components with given indexes.<br>
	 * <code>This</code> will afterwards contain the remaining components.
	 * 
	 * @param indexes
	 *            int[]
	 * @return TriangleNet3D - new TriangleNet3D.
	 */
	public TriangleNet3D splitTriangleNet(int[] indexes, ScalarOperator sop) { // Dag
		TriangleNet3DComp[] newTriangleNetComps = new TriangleNet3DComp[indexes.length];

		for (int i = 0; i < indexes.length; i++) {
			newTriangleNetComps[i] = this.getComponent(i);
			this.removeComponent(getComponent(i));
		}
		return new TriangleNet3D(newTriangleNetComps, sop);
	}

	/**
	 * Computes and returns the Euler number for this net.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() { // Dag
		// Euler formula: vertices - edges + faces
		TriangleNet3DComp[] comps = this.getComponents();
		int euler = 0;
		for (int i = 0; i < comps.length; i++)
			euler += comps[i].getEuler();

		return euler;
	}

	/**
	 * Returns the area of this net as the sum of the areas of its components.
	 * 
	 * @return double - area.
	 */
	public double getArea() { // Dag
		double temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].getArea();

		return temp;
	}

	/**
	 * Returns the number of elements in the net
	 * 
	 * @return int - number of elements.
	 */
	public int countElements() {
		int temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countElements();

		return temp;
	}

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() { // Dag
		int temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countVertices();

		return temp;
	}

	/**
	 * Returns the number of edges in this net.
	 * 
	 * @return int - number of edges.
	 */
	public int countEdges() { // Dag
		int temp = 0;
		// TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < components.length; i++)
			temp = temp + components[i].countEdges();

		return temp;
	}

	/**
	 * Returns the number of faces in this net.
	 * 
	 * @return int - number of faces.
	 */
	public int countFaces() { // Dag
		int temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countElements();

		return temp;
	}

	/**
	 * Returns the number of vertices in the border of his net.
	 * 
	 * @return int - number of vertices in the border.
	 */
	public int countBorderVertices() { // Dag
		int temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countBorderVertices();

		return temp;
	}

	/**
	 * Returns the number of edges in the border of this net.
	 * 
	 * @return int - number of edges in the border.
	 */
	public int countBorderEdges() { // Dag
		int temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countBorderEdges();

		return temp;
	}

	/**
	 * Returns the number of faces in the border of this net.
	 * 
	 * @return int - number of faces in the border.
	 */
	public int countBorderFaces() { // Dag
		int temp = 0;
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countBorderFaces();

		return temp;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) { // Dag
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].intersects(plane))
				return true;
		}
		return false;
	}

	/**
	 * Test whether this intersects with given line.
	 * 
	 * @param line
	 *            Line3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Line3D line) { // Dag
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].intersects(line))
				return true;
		}
		return false;
	}

	/**
	 * Test whether this intersects with given bounding box.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(MBB3D mbb) { // Dag
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].intersects(mbb))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given point geometrically.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Point3D point) { // Dag

		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].contains(point))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Segment3D seg) { // Dag

		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].contains(seg))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given triangle geometrically.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Triangle3D triangle) { // Dag
		TriangleNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].contains(triangle))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether all components in the net are orientation consistent.
	 * 
	 * @return boolean - true if orientation consistent, false otherwise.
	 */
	public boolean isOrientationConsistent() {
		for (int i = 0; i < components.length; i++)
			if (!components[i].isOrientationConsistent())
				return false;

		return true;
	}

	/**
	 * Returns the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.TRIANGLE_NET_3D;
	}

	/**
	 * Returns the spatial type (dimension) of this.
	 * 
	 * @return byte - spatial type.
	 * @see db3d.dbms.model3d.Spatial3D#getSpatial3DType()
	 */
	public byte getSpatial3DType() {
		return Spatial3D.SURFACE_3D;
	}

	/**
	 * Marks the end of an update.<br>
	 * Resets the update flag and begins updating the net.
	 */
	public void endUpdate() {
		setUpdate(false);

		TriangleNet3DComp[] comps = getComponents();
		if (comps != null && comps[0] != null) {
			for (int i = 0; i < comps.length; i++) {
				comps[i].updateEulerStatistics();
				comps[i].updateEntryElement();
				comps[i].updateMBB();
			}
		}
		this.updateMBB();
	}

	/**
	 * Sets the components of this net to given components.
	 * 
	 * @param comps
	 *            TriangleNet3DComp[]
	 */
	protected void setComponents(TriangleNet3DComp[] comps) {
		this.components = comps;
		updateMBB();
	}

	/**
	 * Updates the MBB of this net.<br>
	 * Iterates over all components updating and union their mbbs.<br>
	 * Sets the updated MBB in the abstract SpatialObject.<br>
	 * Updates the index in which the net is.
	 */
	protected void updateMBB() {
		MBB3D neu = null;
		ScalarOperator sop = getScalarOperator();
		TriangleNet3DComp[] comps = getComponents();
		if (comps != null && comps[0] != null) {
			neu = comps[0].getMBB();
			for (int i = 1; i < comps.length; i++)
				neu = neu.union(comps[i].getMBB(), sop);
		}

		// udpate the index if sam exists - means if object is registered in
		// space
		SAM sam = getSAM();
		if (sam != null) {
			sam.remove(this);
			setMBB(neu);
			sam.insert(this);
		} else {
			// set the SpatialObject mbb
			setMBB(neu);
		}
	}

	/**
	 * Searches for an element with the given id in the components of this and
	 * returns it. If it was not found, returns <code>null</code>.
	 * 
	 * @return SimpleGeoObj - element with the given id.
	 */
	public SimpleGeoObj getElement(int id) {
		for (TriangleNet3DComp comp : components) {

			/*
			 * TODO: which is faster: tempComp = comp.getElement(id) and then
			 * return tempComp or this method without direct assignment but if
			 * run it twice if an element was found?
			 */
			if (comp.getElement(id) != null)
				return comp.getElement(id);
		}
		return null;
	}
}
