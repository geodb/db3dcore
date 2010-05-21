/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.api.ContainmentException;
import de.uos.igf.db3d.dbms.api.UpdateException;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.RStar;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * PointNet3DComp represents a single point net component. All PointElt3D
 * objects in this object belong to one semantic component.<br>
 * For point nets with several components see @see PointNet3D. Serialization
 * (reference to enclosing net skipped) - WBaer 06082003
 */
public class PointNet3DComp implements PersistentObject, ComplexGeoObj,
		Serializable {

	/* for serialization - ref to enclosing net skipped */
	private static final ObjectStreamField[] serialPersistentFields;

	static {
		serialPersistentFields = new ObjectStreamField[] {
				new ObjectStreamField("sop", ScalarOperator.class),
				new ObjectStreamField("mbb", MBB3D.class),
				new ObjectStreamField("sam", SAM.class),
				new ObjectStreamField("id", Integer.TYPE) };
	}

	/* id - if not set its -1 */
	private int id;

	/* ScalarOperator */
	private ScalarOperator sop;

	/* MBB of this component */
	private MBB3D mbb;

	/* reference to net */
	private PointNet3D net;

	/* spatial tree */
	private SAM sam;

	/**
	 * Constructor.<br>
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	public PointNet3DComp(ScalarOperator sop) {
		this.id = -1;
		this.sop = sop;
		this.sam = new RStar(MAX_SAM, sop);
	}

	/**
	 * Constructor.<br>
	 * Constructs a PointNet3DComp object with the given PointElt3D[].<br>
	 * In the given array the neighbourhood topology has not been defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this triangle
	 * array.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @param elements
	 *            PointElt3D[]
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected PointNet3DComp(PointElt3D[] elements, ScalarOperator sop) {
		this.id = -1;
		this.sop = sop;
		this.sam = new RStar(MAX_SAM, sop);
		loadSAM(elements);
		// Here an IllegalArgumentException can be thrown.
		this.mbb = sam.getMBB();
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) { // Dag
		Set set = this.getElementsViaSAM();
		PointElt3D[] points = (PointElt3D[]) set.toArray(new PointElt3D[set
				.size()]);
		for (int i = 0; i < points.length; i++)
			if (points[i].intersects(plane, getScalarOperator()))
				return true;

		return false;
	}

	/**
	 * Test whether this intersects with given line.
	 * 
	 * @param line
	 *            Line3D to be tested
	 * @return boolean - true if intersects, false itherwise.
	 */
	public boolean intersects(Line3D line) { // Dag
		Set set = this.getElementsViaSAM();
		PointElt3D[] points = (PointElt3D[]) set.toArray(new PointElt3D[set
				.size()]);
		for (int i = 0; i < points.length; i++)
			if (points[i].intersects(line, getScalarOperator()))
				return true;

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
		if (!mbb.intersects(this.getMBB(), this.getScalarOperator()))
			return false;

		Set set = this.getSAM().intersects(mbb);
		if (set.isEmpty())
			return false;

		return true;
	}

	/**
	 * Tests whether this geometrically contains the given point.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is cointained, false otherwise.
	 */
	public boolean contains(Point3D point) {
		return containsElt(point);
	}

	/**
	 * Creates a PointElt3D from given point and adds it to the component.
	 * 
	 * @param point
	 *            Point3D
	 * @return PointElt3D - the inserted instance.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public PointElt3D addElt(Point3D point) throws UpdateException {
		PointElt3D elt = new PointElt3D(point);
		if (this.containsElt(elt))
			throw new ContainmentException("Element already contained !");

		this.getSAM().insert(elt);
		// Here an IllegalArgumentException can be thrown.
		elt.setID(getNet().nextElementID()); // set id
		return elt;
	}

	/**
	 * Removes the given element from the component.
	 * 
	 * @param elt
	 *            Point3D to be removed
	 * @return PointElt3D - the removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public PointElt3D removeElt(Point3D elt) throws UpdateException {
		// find element
		PointElt3D removable = null;
		Set set = this.getSAM().intersects(elt.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			PointElt3D current = (PointElt3D) it.next();
			if (current.isGeometryEquivalent(elt, this.getScalarOperator())) {
				removable = current;
				break;
			}
		}
		if (removable != null) {

			/*
			 * Working with the retrieved removable element implies that the
			 * remove method which is currently based on equals comparison
			 * removes the element correctly.
			 */
			this.getSAM().remove(removable);
			// Here an IllegalArgumentException can be thrown.
			return removable;
		}
		// else
		throw new ContainmentException("Element not contained !");
	}

	/**
	 * Tests whether a element with the coordinates of given point is contained
	 * in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * In the case of a point net this test is equal to a geometric test of
	 * containment !<br>
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean containsElt(Point3D point) {
		Set set = this.getSAM().intersects(point.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			PointElt3D pointelt = (PointElt3D) it.next();
			if (pointelt.isGeometryEquivalent(point, getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Returns the number of elements in the net.
	 * 
	 * @return int - number of elements.
	 */
	public int countElements() {
		return getSAM().getCount();
	}

	/**
	 * Returns the number of vertices in this component.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		return getSAM().getCount();
	}

	/**
	 * Tests if this component is empty.
	 * 
	 * @return boolean - true if is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return getSAM().getCount() == 0;
	}

	/**
	 * Returns the mbb of this.
	 * 
	 * @return MBB3D.
	 */
	public MBB3D getMBB() {
		return this.mbb;
	}

	/**
	 * Computes and returns the Euler number for this component.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() {
		/*
		 * Euler formula: vertices - edges + faces number of PointElt3D elements
		 * = vertices = Euler
		 */
		return (this.countVertices());
	}

	/**
	 * Returns the ScalarOperator of this.
	 * 
	 * @return ScalarOperator.
	 */
	public ScalarOperator getScalarOperator() {
		return this.sop;
	}

	/**
	 * Returns the PointElt3D objects in a Set.
	 * 
	 * @return Set with PointElt3D objects.
	 */
	public Set getElementsViaSAM() {
		return this.sam.getEntries();
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return PointNet3D - the enclosing net.
	 */
	public PointNet3D getNet() {
		return net;
	}

	/**
	 * Performs a deep copy of this component with all its recursive members.<br>
	 * Only the reference to the enclosing net is not copied and must be set
	 * afterwards.
	 * 
	 * @return PointNet3DComp - deep copy of this.
	 */
	public PointNet3DComp serializationCopy() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(this);
			os.flush();
			ObjectInputStream is = new ObjectInputStream(
					new ByteArrayInputStream(out.toByteArray()));
			Object ret = is.readObject();
			is.close();
			os.reset();
			os.close();
			return (PointNet3DComp) ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the internal spatial access method object (SAM) to the elements
	 * of the component.
	 * 
	 * @return SAM.
	 */
	public SAM getSAM() {
		return this.sam;
	}

	/**
	 * Returns the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.COMP_POINT_NET_3D;
	}

	/**
	 * Returns the ID of this component if associated with a net.
	 * 
	 * @return int - id.
	 */
	public int getComponentID() {
		return id;
	}

	/**
	 * Sets the component ID.<br>
	 * Called if the component is added to a net.
	 * 
	 * @param id
	 *            int id
	 */
	protected void setComponentID(int id) {
		this.id = id;
	}

	/**
	 * Sets the reference to enclosing PointNet3D.
	 * 
	 * @param net3D
	 *            PointNet3D
	 */
	protected void setNet(PointNet3D net3D) {
		this.net = net3D;
	}

	/**
	 * Updates the vertices, edges, faces statistics after changes in the net
	 * component.
	 */
	protected void updateEulerStatistics() {
		// in the pointnetcomp do nothing
	}

	/**
	 * Updates the MBB after changes in the net component.
	 * 
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected void updateMBB() {
		setMBB(getSAM().getMBB());
	}

	/**
	 * Updates the Entry elements after changes in the net component.
	 */
	protected void updateEntryElement() {
		// in the pointnetcomp do nothing
	}

	/*
	 * Inserts <code>PointElt3D</code> elements into the spatial access method
	 * of this.
	 * 
	 * @param elements PointElt3D elements to be inserted
	 * 
	 * @throws IllegalArgumentException if an attempt is made to construct a
	 * MBB3D whose maximum point is not greater than its minimum point.
	 */
	private void loadSAM(PointElt3D[] elements) {
		for (int i = 0; i < elements.length; i++)
			this.sam.insert(elements[i]);
	}

	/*
	 * Sets the MBB of this to the given MBB.
	 * 
	 * @param mbb MBB
	 */
	private void setMBB(MBB3D mbb) {
		this.mbb = mbb;
	}

	/**
	 * Returns the element of this with the given id.
	 * 
	 * @param id
	 *            int id of the element to be searched.
	 */
	public SimpleGeoObj getElement(int id) {

		Iterator<PointElt3D> it = getElementsViaSAM().iterator();
		while (it.hasNext()) {
			PointElt3D p = it.next();
			if (p.getID() == id)
				return p;
		}
		return null;
	}
}
