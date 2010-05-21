/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;

//import com.odi.ObjectStore;

import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * PointNet3D is the concrete subclass of SpatialObject3D representing a point
 * net with several components. All components are referenced as PointNet3DComp
 * objects.
 */
public class PointNet3D extends SpatialObject3D implements Sample3D,
		ComplexGeoObj {

	/* components of this */
	private PointNet3DComp[] components;

	/**
	 * Constructor.
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	public PointNet3D(ScalarOperator sop) {
		super();
		this.components = null;
		this.setMBB(null);
		this.setScalarOperator(sop);
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the PointNetBuilder class.
	 * 
	 * @param components
	 *            PointNet3DComp[]
	 * @param sop
	 *            ScalarOperator
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected PointNet3D(PointNet3DComp[] components, ScalarOperator sop) {
		super();
		this.components = components;
		this.setScalarOperator(sop);
		updateMBB();
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given PointNet3D is not copied - so this is a free PointNet3D. It is not
	 * registered in the Space3D and the corresponding thematic is gone away ! <br>
	 * 
	 * @param net
	 *            PointNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public PointNet3D(PointNet3D net) {
		super();
		PointNet3DComp[] comps = net.getComponents();
		this.components = new PointNet3DComp[comps.length];
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
	 * @return PointNet3DComp[] - array of components.
	 */
	public PointNet3DComp[] getComponents() {
		// ObjectStore.fetch(this);
		// ObjectStore.fetch(components);
		return this.components;
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param index
	 *            int index
	 * @return PointNet3DComp with the given index.
	 */
	public PointNet3DComp getComponent(int index) {
		return this.components[index];
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param comp
	 *            PointNet3DComp to be added
	 */
	public void addComponent(PointNet3DComp comp) {
		// set the element ids for this net
		Iterator it = comp.getElementsViaSAM().iterator();
		while (it.hasNext())
			((PointElt3D) it.next()).setID(this.nextElementID());

		// set component id
		comp.setComponentID(this.nextComponentID());

		// copy component
		PointNet3DComp[] comps = getComponents();
		int compsLength = 0;
		if (comps != null)
			compsLength = comps.length;
		PointNet3DComp[] temp = new PointNet3DComp[compsLength + 1];
		for (int i = 0; i < compsLength; i++)
			temp[i] = comps[i];

		temp[compsLength] = comp;
		// set net
		comp.setNet(this);
		setComponents(temp);
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param comp
	 *            PointNet3DComp to be removed
	 */
	public void removeComponent(PointNet3DComp comp) {
		PointNet3DComp[] comps = getComponents();
		PointNet3DComp[] temp = new PointNet3DComp[comps.length - 1];
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
	 * @return PointNet3DComp - newly added component.
	 */
	public PointNet3DComp createComponent() {
		PointNet3DComp comp = new PointNet3DComp(getScalarOperator().copy());
		addComponent(comp);
		return comp;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned net will have the components with given indexes.<br>
	 * The <code>this</code> net will have the remaining components.
	 * 
	 * @param indexes
	 *            int[]
	 * @return PointNet3D - new PointNet3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public PointNet3D splitPointNet(int[] indexes) { // Dag
		PointNet3DComp[] newPointNetComps = new PointNet3DComp[indexes.length];

		for (int i = 0; i < indexes.length; i++) {
			newPointNetComps[i] = this.getComponent(i);
			this.removeComponent(getComponent(i));
		}
		return new PointNet3D(newPointNetComps, getScalarOperator());
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Computes and returns the Euler number for this net.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() { // Dag
		// Euler formular: vertices - edges + faces

		// number of PointElt3D elements = vertices = Euler
		return this.countVertices();
	}

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		int count = 0;
		PointNet3DComp[] comps = this.getComponents();
		for (int i = 0; i < comps.length; i++)
			count += comps[i].countVertices();

		return count;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) {
		PointNet3DComp[] comps = getComponents();
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
		PointNet3DComp[] comps = getComponents();
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
		PointNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].intersects(mbb))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this geometrically contains the given point.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Point3D point) {
		PointNet3DComp[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].contains(point))
				return true;
		}
		return false;
	}

	/**
	 * Returns the type of this as as <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.POINT_NET_3D;
	}

	/**
	 * Returns the spatial type (the dimension) of this.
	 * 
	 * @return byte - spatial type.
	 * @see db3d.dbms.model3d.Spatial3D#getSpatial3DType()
	 */
	public byte getSpatial3DType() {
		return Spatial3D.SAMPLE_3D;
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

		PointNet3DComp[] comps = getComponents();
		if (comps != null && comps[0] != null) {
			for (int i = 0; i < comps.length; i++) {
				comps[i].updateEulerStatistics();
				comps[i].updateEntryElement();
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
	 *            PointNet3DComp[]
	 */
	protected void setComponents(PointNet3DComp[] comps) {
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
		PointNet3DComp[] comps = getComponents();
		if (comps != null && comps[0] != null) {
			neu = comps[0].getMBB();
			for (int i = 1; i < comps.length; i++)
				neu = neu.union(comps[i].getMBB(), sop);
		}

		/*
		 * Update the index if sam exists - means if object is registered in
		 * space.
		 */
		SAM sam = getSAM();
		if (sam != null) { // must be first removed and afterward the new mbb
			// must be
			sam.remove(this); // set before reinsertion
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
		for (PointNet3DComp comp : components) {

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
