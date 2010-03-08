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
 * File TetrahedronNet3D.java - created on 05.06.2003
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
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * TetrahedronNet3D is the concrete subclass of SpatialObject3D representing a
 * tetrahedron net with several components. All components are referenced as
 * TetrahedronNet3DComp objects.
 */
public class TetrahedronNet3D extends SpatialObject3D implements Solid3D,
		ComplexGeoObj {

	/** components of this */
	protected TetrahedronNet3DComp[] components;

	/**
	 * Constructor.
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	public TetrahedronNet3D(ScalarOperator sop) {
		super();
		this.components = null;
		this.setScalarOperator(sop);
		this.setMBB(null);
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the TetrahedronNetBuilder class.
	 * 
	 * @param components
	 *            TetrahedronNet3DComp[]
	 * @param sop
	 *            ScalarOperator
	 */
	protected TetrahedronNet3D(TetrahedronNet3DComp[] components,
			ScalarOperator sop) {
		super();
		this.components = components;
		this.setScalarOperator(sop);
		updateMBB();
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given TetrahedronNet3D is not copied - so this is a free
	 * TetrahedronNet3D. It is not registered in the Space3D and the
	 * corresponding thematic is gone away !
	 * 
	 * @param net
	 *            TetrahedronNet3D
	 */
	public TetrahedronNet3D(TetrahedronNet3D net) {
		super();
		TetrahedronNet3DComp[] comps = net.getComponents();
		this.components = new TetrahedronNet3DComp[comps.length];
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
	 * Returns the number of elements in the net.
	 * 
	 * @return int - number of elements.
	 */
	public int countElements() {
		int sum = 0;
		for (int i = 0; i < countComponents(); i++)
			sum = sum + getComponent(i).countElements();

		return sum;
	}

	/**
	 * Returns the components of this net.
	 * 
	 * @return TetrahedronNet3DComp[] - array of components.
	 */
	public TetrahedronNet3DComp[] getComponents() {
		// ObjectStore.fetch(this);
		// ObjectStore.fetch(components);
		if (components != null)
			return components;
		else
			return new TetrahedronNet3DComp[0];
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param index
	 *            int index
	 * @return TetrahedronNet3DComp.
	 */
	public TetrahedronNet3DComp getComponent(int index) {
		return this.components[index];
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param comp
	 *            TetrahedronNet3DComp to be added
	 */
	public void addComponent(TetrahedronNet3DComp comp) {
		// set the element ids for this net
		Iterator it = comp.getElementsViaRecursion().iterator();
		while (it.hasNext())
			((TetrahedronElt3D) it.next()).setID(this.nextElementID());

		// set component id
		comp.setComponentID(this.nextComponentID());

		TetrahedronNet3DComp[] comps = getComponents();
		TetrahedronNet3DComp[] temp = new TetrahedronNet3DComp[comps.length + 1];
		for (int i = 0; i < comps.length; i++)
			temp[i] = comps[i];

		temp[comps.length] = comp;
		// set net
		comp.setNet(this);
		setComponents(temp);
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param comp
	 *            TetrahedronNet3DComp to be removed
	 */
	public void removeComponent(TetrahedronNet3DComp comp) {
		TetrahedronNet3DComp[] comps = getComponents();
		TetrahedronNet3DComp[] temp = new TetrahedronNet3DComp[comps.length - 1];
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
	 * Creates and returns a new (empty) component of the net.
	 * 
	 * @return TetrahedronNet3DComp - empty.
	 */
	public TetrahedronNet3DComp createComponent() {
		TetrahedronNet3DComp comp = new TetrahedronNet3DComp(
				getScalarOperator().copy());
		addComponent(comp);
		return comp;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned new net object will have the components of given indexes.<br>
	 * <code>This</code> will contain the remaining components.
	 * 
	 * @param indexes
	 *            int[]
	 * @return TetrahedronNet3D - new TetrahedronNet3D.
	 */
	public TetrahedronNet3D splitTetrahedronNet(int[] indexes,
			ScalarOperator sop) { // Dag
		TetrahedronNet3DComp[] newTetrahedronNetComps = new TetrahedronNet3DComp[indexes.length];

		for (int i = 0; i < indexes.length; i++) {
			newTetrahedronNetComps[i] = this.getComponent(i);
			this.removeComponent(getComponent(i));
		}
		return new TetrahedronNet3D(newTetrahedronNetComps, sop);
	}

	/**
	 * Returns the boundary area of this net as the sum of the areas of its
	 * components.
	 * 
	 * @return double - boundary area.
	 */
	public double getArea() {
		double temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].getArea();

		return temp;
	}

	/**
	 * Returns the volume of this net as the sum of the volumes of its
	 * components.
	 * 
	 * @return double - volume.
	 */
	public double getVolume() {
		double temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].getVolume();

		return temp;
	}

	/**
	 * Computes and returns the Euler number for this net.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() { // Dag
		// Euler formula: vertices - edges + faces
		TetrahedronNet3DComp[] comps = this.getComponents();
		int euler = 0;
		for (int i = 0; i < comps.length; i++)
			euler += comps[i].getEuler();

		return euler;
	}

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countVertices();

		return temp;
	}

	/**
	 * Returns the number of edges in this net.
	 * 
	 * @return int - number of edges.
	 */
	public int countEdges() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countEdges();

		return temp;
	}

	/**
	 * Returns the number of faces in this net.
	 * 
	 * @return int - number of faces.
	 */
	public int countFaces() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countFaces();

		return temp;
	}

	/**
	 * Returns the number of tetrahedrons in this net.
	 * 
	 * @return int - number of tetrahedrons.
	 */
	public int countTetras() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countTetras();

		return temp;
	}

	/**
	 * Returns the number of vertices in the border of his net.
	 * 
	 * @return int - number of vertices in the border.
	 */
	public int countBorderVertices() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countBorderVertices();

		return temp;
	}

	/**
	 * Returns the number of edges in the border of this net.
	 * 
	 * @return int - number of edges in the border.
	 */
	public int countBorderEdges() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countBorderEdges();

		return temp;
	}

	/**
	 * Returns the number of faces in the border of this net.
	 * 
	 * @return int - number of faces in the border.
	 */
	public int countBorderFaces() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countBorderFaces();

		return temp;
	}

	/**
	 * Returns the number of tetrahedrons in the border of this net.
	 * 
	 * @return int - number of tetrahedrons in the border.
	 */
	public int countBorderTetras() {
		int temp = 0;
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp += comps[i].countBorderTetras();

		return temp;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) {
		TetrahedronNet3DComp[] comps = getComponents();
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
	public boolean intersects(Line3D line) {
		TetrahedronNet3DComp[] comps = getComponents();
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
	public boolean intersects(MBB3D mbb) {
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].intersects(mbb))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given point geometrically inside.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsInside(Point3D point) {
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].containsInside(point))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically inside.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsInside(Segment3D seg) {
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].containsInside(seg))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given triangle geometrically inside.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsInside(Triangle3D triangle) {
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].containsInside(triangle))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given tetrahedron geometrically inside.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsInside(Tetrahedron3D tetra) {
		TetrahedronNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].containsInside(tetra))
				return true;
		}
		return false;
	}

	/**
	 * Returns the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.TETRAHEDRON_NET_3D;
	}

	/**
	 * Returns the spatial type (dimension) of this.
	 * 
	 * @return byte - spatial type.
	 * @see db3d.dbms.model3d.Spatial3D#getSpatial3DType()
	 */
	public byte getSpatial3DType() {
		return Spatial3D.SOLID_3D;
	}

	/**
	 * Marks the end of an update.<br>
	 * Resets the update flag and begins updating the net.
	 */
	public void endUpdate() {
		setUpdate(false);

		TetrahedronNet3DComp[] comps = getComponents();
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
	 * Sets the componentes of this net to given components.<br>
	 * Calls updateMBB to update the MBB of this net.
	 * 
	 * @param comps
	 *            TetrahedronNet3DComp[]
	 */
	protected void setComponents(TetrahedronNet3DComp[] comps) {
		this.components = comps;
		updateMBB();
	}

	/**
	 * Updates the MBB of this net.<br>
	 * Iterates over all components updating and union their mbbs.<br>
	 * Sets the updated MBB in the abstract SpatialObject.<br>
	 * Updates the index in which the net is
	 */
	protected void updateMBB() {
		MBB3D neu = null;
		ScalarOperator sop = getScalarOperator();
		TetrahedronNet3DComp[] comps = getComponents();
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
		for (TetrahedronNet3DComp comp : components) {

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
