/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;

//import com.odi.ObjectStore;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * SegmentNet3D is the subclass of SpatialObject3D representing a segment net
 * with several components. All components are referenced as SegmentNet3DComp
 * objects.
 */
public class SegmentNet3D extends SpatialObject3D implements Curve3D,
		ComplexGeoObj {

	/* components of this */
	private SegmentNet3DComp[] components;

	/**
	 * Constructor.
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	public SegmentNet3D(ScalarOperator sop) {
		super();
		this.components = null;
		this.setScalarOperator(sop);
		this.setMBB(null);
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the SegmentNetBuilder class.
	 * 
	 * @param components
	 *            SegmentNet3DComp[]
	 * @param sop
	 *            ScalarOperator
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected SegmentNet3D(SegmentNet3DComp[] components, ScalarOperator sop) {
		super();
		this.components = components;
		this.setScalarOperator(sop);
		updateMBB();
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given SegmentNet3D is not copied - so this is a free SegmentNet3D. It is
	 * not registered in the Space3D and the corresponding thematic is gone
	 * away!
	 * 
	 * @param net
	 *            SegmentNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public SegmentNet3D(SegmentNet3D net) {
		super();
		SegmentNet3DComp[] comps = net.getComponents();
		this.components = new SegmentNet3DComp[comps.length];
		for (int i = 0; i < comps.length; i++) {
			this.components[i] = comps[i].serializationCopy();
			this.components[i].setNet(this);
		}
		this.setScalarOperator(net.getScalarOperator().copy());
		updateMBB();
		// Here an IllegalArgumentException can be thrown.
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
	 * @return SegmentNet3DComp[] - array of components.
	 */
	public SegmentNet3DComp[] getComponents() {
		// ObjectStore.fetch(this);
		// ObjectStore.fetch(components);
		if (components != null)
			return components;
		else {
			components = new SegmentNet3DComp[0];
			return components;
		}
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param index
	 *            int index
	 * @return SegmentNet3DComp or <code>null</code>, if no component exists at
	 *         index position.
	 */
	public SegmentNet3DComp getComponent(int index) {
		if (components == null)
			components = new SegmentNet3DComp[0];
		if (index < components.length)
			return components[index];
		else
			return null;
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param comp
	 *            SegmentNet3DComp to be added
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(SegmentNet3DComp comp) {
		// set the element ids for this net
		Iterator it = comp.getElementsViaRecursion().iterator();
		while (it.hasNext())
			((SegmentElt3D) it.next()).setID(this.nextElementID());

		// set component id
		comp.setComponentID(this.nextComponentID());

		SegmentNet3DComp[] comps = getComponents();
		SegmentNet3DComp[] temp = new SegmentNet3DComp[comps.length + 1];
		for (int i = 0; i < comps.length; i++)
			temp[i] = comps[i];

		temp[comps.length] = comp;
		// set net
		comp.setNet(this);
		setComponents(temp);
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param comp
	 *            SegmentNet3DComp to be removed
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void removeComponent(SegmentNet3DComp comp) {
		SegmentNet3DComp[] comps = getComponents();
		SegmentNet3DComp[] temp = new SegmentNet3DComp[comps.length - 1];
		int x = 0;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] != comp) {
				temp[x] = comps[i];
				x++;
			}
		}
		setComponents(temp);
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Creates and returns a new component of the net.
	 * 
	 * @return SegmentNet3DComp that has been created.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public SegmentNet3DComp createComponent() {
		SegmentNet3DComp comp = new SegmentNet3DComp(getScalarOperator().copy());
		addComponent(comp);
		return comp;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned net will have the components with the given indexes.<br>
	 * The <code>this</code> net will have the remaining components.
	 * 
	 * @param indexes
	 *            int[]
	 * @return SegmentNet3D - new SegmentNet3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public SegmentNet3D splitSegmentNet(int[] indexes, ScalarOperator sop) {// Dag
		SegmentNet3DComp[] newSegmentNetComps = new SegmentNet3DComp[indexes.length];

		for (int i = 0; i < indexes.length; i++) {
			newSegmentNetComps[i] = this.getComponent(i);
			this.removeComponent(getComponent(i));
			// Here an IllegalArgumentException can be thrown.
		}

		return new SegmentNet3D(newSegmentNetComps, sop);
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Computes and returns the Euler number for this net.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() {
		// Dag
		// Euler formula: vertices - edges + faces
		SegmentNet3DComp[] comps = this.getComponents();
		int euler = 0;

		for (int i = 0; i < comps.length; i++)
			euler += comps[i].getEuler();

		return euler;
	}

	/**
	 * Returns the length of this net as the sum of the length of its
	 * components.
	 * 
	 * @return double - length of the net.
	 */
	public double getLength() {
		double temp = 0;
		SegmentNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].getLength();

		return temp;
	}

	/**
	 * Returns the number of elements in this net.
	 * 
	 * @return int number of elements.
	 */
	public int countElements() {
		int temp = 0;
		SegmentNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countElements();

		return temp;
	}

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		int temp = 0;
		SegmentNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countVertices();

		return temp;
	}

	/**
	 * Returns the number of edges in this net.
	 * 
	 * @return int - number of edges.
	 */
	public int countEdges() {
		int temp = 0;
		SegmentNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
			temp = temp + comps[i].countEdges();

		return temp;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws DB3DException
	 *             - if the type if the <code>SimpleGeoObj</code> resulting from
	 *             the intersection of the components of this and the given
	 *             <code>Plane3D</code> cannot be identified.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - if the index of a Point3D in a Rectangle3D is not in the
	 *             interval [0, 3]. This exception originates in the getPoint
	 *             (int index) method of the class Rectangle3D called by this
	 *             method.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Plane3D plane) throws DB3DException {
		SegmentNet3DComp[] comps = getComponents();
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
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Line3D line) {
		SegmentNet3DComp[] comps = getComponents();
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
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(MBB3D mbb) {
		SegmentNet3DComp[] comps = getComponents();
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
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean contains(Point3D point) {
		SegmentNet3DComp[] comps = getComponents();
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
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean contains(Segment3D seg) {
		SegmentNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].contains(seg))
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
	 * Returns the type of this as as <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * 
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.SEGMENT_NET_3D;
	}

	/**
	 * Returns the spatial type (dimension) of this.
	 * 
	 * @return byte - spatial type.
	 * @see db3d.dbms.model3d.Spatial3D#getSpatial3DType()
	 */
	public byte getSpatial3DType() {
		return Spatial3D.CURVE_3D;
	}

	/**
	 * Marks the end of an update.<br>
	 * Resets the update flag and begins updating the net.
	 * 
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void endUpdate() {
		setUpdate(false);

		SegmentNet3DComp[] comps = getComponents();
		if (comps != null && comps[0] != null) {
			for (int i = 0; i < comps.length; i++) {
				comps[i].updateEulerStatistics();
				// comps[i].updateEntryElement(); add and remove keeps track of
				// entry element
				comps[i].updateMBB();
				// Here an IllegalArgumentException can be thrown.
			}
		}
		this.updateMBB();
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Sets the components of this net to given components.
	 * 
	 * @param comps
	 *            SegmentNet3DComp[]
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected void setComponents(SegmentNet3DComp[] comps) {
		this.components = comps;
		updateMBB();
	}

	/**
	 * Updates the MBB of this net.<br>
	 * Iterates over all components updating and union their mbbs.<br>
	 * Sets the updated MBB in the abstract SpatialObject.<br>
	 * Updates the index in which the net is.
	 * 
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected void updateMBB() {
		MBB3D neu = null;
		ScalarOperator sop = getScalarOperator();
		SegmentNet3DComp[] comps = getComponents();
		if (comps != null && comps[0] != null) {
			neu = comps[0].getMBB();
			for (int i = 1; i < comps.length; i++)
				neu = neu.union(comps[i].getMBB(), sop);
		}

		/*
		 * update the index if sam exists - means if object is registered in
		 * space
		 */
		SAM sam = getSAM();
		if (sam != null) {
			sam.remove(this);
			// Here an IllegalArgumentException can be thrown.
			setMBB(neu);
			sam.insert(this);
			// Here an IllegalArgumentException can be thrown.
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
		for (SegmentNet3DComp comp : components) {

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
