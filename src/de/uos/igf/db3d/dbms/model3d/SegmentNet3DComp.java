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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import de.uos.igf.db3d.dbms.api.ContainmentException;
import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.api.GeometryException;
import de.uos.igf.db3d.dbms.api.TopologyException;
import de.uos.igf.db3d.dbms.api.UpdateException;
import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.geom.Wireframe3D;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;
import de.uos.igf.db3d.dbms.util.IdentityHashSet;
import de.uos.igf.db3d.dbms.util.RStar;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * SegmentNet3DComp represents a single segment net component. All SegmentElt3D
 * objects in this object belong to one semantic component.<br>
 * For segment nets with several components see @see SegmentNet3D
 */
public class SegmentNet3DComp implements PersistentObject, ComplexGeoObj,
		Serializable {

	/** for serialization - ref to enclosing net skipped */
	private static final ObjectStreamField[] serialPersistentFields;

	static {
		serialPersistentFields = new ObjectStreamField[] {
				new ObjectStreamField("sop", ScalarOperator.class),
				new ObjectStreamField("entry", SegmentElt3D.class),
				new ObjectStreamField("oriented", Boolean.TYPE),
				new ObjectStreamField("connected", Boolean.TYPE),
				new ObjectStreamField("mbb", MBB3D.class),
				new ObjectStreamField("sam", SAM.class),
				new ObjectStreamField("vertices", Integer.TYPE),
				new ObjectStreamField("id", Integer.TYPE) };
	}

	/* id */
	private int id;

	/* ScalarOperator */
	private ScalarOperator sop;

	/* entry element (topological start element) */
	private SegmentElt3D entry;

	/* orientation clean flag */
	private boolean oriented;

	/* connected flag */
	private boolean connected;

	/* MBB of this component */
	private MBB3D mbb;

	/* reference to net */
	private SegmentNet3D net;

	/* spatial tree */
	private SAM sam;

	/* vertices counter */
	private int vertices;

	/**
	 * Constructor.<br>
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	protected SegmentNet3DComp(ScalarOperator sop) {
		this.id = -1;
		this.sop = sop;
		this.oriented = false;
		this.connected = false;
		this.sam = new RStar(MAX_SAM, sop);
	}

	/**
	 * Constructor.<br>
	 * Constructs a SegmentNet3DComp object with the given SegmentElt3D[].<br>
	 * In the given array the neighbourhood topology has not be defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this segment
	 * array.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @param elements
	 *            SegmentElt3D[]
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public SegmentNet3DComp(ScalarOperator sop, SegmentElt3D[] elements) {
		this.id = -1;
		this.sop = sop;
		this.sam = new RStar(MAX_SAM, sop);
		loadSAM(elements);
		// Here an IllegalArgumentException can be thrown.

		this.mbb = this.sam.getMBB();
		// Here an IllegalArgumentException can be thrown.
		this.buildNetTopology(elements);
		this.connected = true;
		this.makeOrientationConsistent();
		this.oriented = true;
		updateEntryElement();
		updateEulerStatistics();
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws DB3DException
	 *             - if the type if the <code>SimpleGeoObj</code> resulting from
	 *             the intersection of this and the given <code>Plane3D</code>
	 *             cannot be identified.
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
	public boolean intersects(Plane3D plane) throws DB3DException { // Dag

		SimpleGeoObj obj = this.getMBB().intersection(plane,
				this.getScalarOperator());
		// Here an IllegalStateException can be thrown. This exception
		// originates in the getPoint(int) method of the class Rectangle3D.

		// Here an IllegalStateException can be thrown signaling problems with
		// the dimension of the wireframe.

		// Here an IllegalStateException can be thrown signaling problems with
		// the index of a point coordinate.
		if (obj == null)
			return false;

		MBB3D intMbb = new MBB3D();

		switch (obj.getType()) {
		case SimpleGeoObj.POINT3D:
			intMbb = ((Point3D) obj).getMBB();
			break;
		case SimpleGeoObj.SEGMENT3D:
			intMbb = ((Segment3D) obj).getMBB();
			break;
		case SimpleGeoObj.TRIANGLE3D:
			intMbb = ((Triangle3D) obj).getMBB();
			break;
		case SimpleGeoObj.WIREFRAME3D:
			intMbb = ((Wireframe3D) obj).getMBB();
			// Here an IllegalArgumentException can be thrown.
			break;
		default:
			throw new DB3DException(Db3dSimpleResourceBundle.getString(
					"db3d.geom.defrmethod"));
		}

		SAM sam = this.getSAM();
		Set segments = sam.intersects(intMbb);

		Iterator it = segments.iterator();
		while (it.hasNext()) {
			SegmentElt3D segment = (SegmentElt3D) it.next();
			if (segment.intersects(plane, this.getScalarOperator()))
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
	public boolean intersects(Line3D line) { // Dag

		SimpleGeoObj obj = this.getMBB().intersection(line,
				this.getScalarOperator());
		MBB3D intMbb = new MBB3D();
		if (obj == null)
			return false;
		if (obj.getType() == SimpleGeoObj.POINT3D)
			intMbb = ((Point3D) obj).getMBB();
		if (obj.getType() == SimpleGeoObj.SEGMENT3D)
			intMbb = ((Segment3D) obj).getMBB();

		SAM sam = this.getSAM();
		Set segments = sam.intersects(intMbb);

		Iterator it = segments.iterator();
		while (it.hasNext()) {
			SegmentElt3D segment = (SegmentElt3D) it.next();
			if (segment.intersects(line, this.getScalarOperator()))
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
	public boolean intersects(MBB3D mbb) { // Dag

		if (!mbb.intersects(this.getMBB(), this.getScalarOperator()))
			return false;

		Set set = this.getSAM().intersects(mbb);
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			if (segelt.intersects(mbb, this.getScalarOperator()))
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
		Set set = this.getSAM().contains(point);
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			if (segelt.contains(point, this.getScalarOperator()))
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
		Set set = this.getSAM().intersects(seg.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			if (segelt.contains(seg, this.getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Returns end element of this - if this is closed, the EndElement will be
	 * the "left" neighbour of the EntryElement.
	 * 
	 * @return end - SegmentElt3D, the end element.
	 */
	public SegmentElt3D getEndElement() {// Dag
		if (this.isClosed())
			return (this.getEntryElement().getNeighbour(1));
		SegmentElt3D current = this.getEntryElement();
		while (current.hasNeighbour(0)) {
			current = current.getNeighbour(0);
		}
		return current;
	}

	/**
	 * Adds the given element to the component.<br>
	 * The given element reference is not valid anymore after insertion.<br>
	 * If you need to hold a reference on the element, update your variable with
	 * the return value element !
	 * 
	 * @param elt
	 *            Segment3D to be added
	 * @return SegmentElt3D - the inserted instance.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SegmentElt3D addElt(Segment3D elt) throws UpdateException { // Dag
		if (this.containsElt(elt))
			throw new ContainmentException("Element already contained !");

		return addElt(elt.getPoint(0), elt.getPoint(1));
	}

	/**
	 * Creates a new element and adds it to the component. One of the points p1
	 * or p2 must be geometry equivalent to an end point of the net.
	 * 
	 * @param p1
	 *            Point3D, start point of the SegmentElt3D that should be
	 *            created
	 * @param p2
	 *            Point3D, end point of the SegmentElt3D that should be created
	 * @return SegmentElt3D - inserted element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SegmentElt3D addElt(Point3D p1, Point3D p2) throws UpdateException { // Dag
		// if the netcomp is empty
		if (this.isEmpty()) {
			SegmentElt3D newElt = new SegmentElt3D(new Point3D(p1),
					new Point3D(p2), sop);
			this.setEntryElement(newElt);
			this.getSAM().insert(newElt);
			// Here an IllegalArgumentException can be thrown.
			this.setOriented(true);
			this.setConnected(true);
			newElt.setID(getNet().nextElementID()); // set id
			return newElt;
		}
		// if the netcomp is already closed
		if (this.isClosed())
			throw new TopologyException("Net component is already closed !");

		ScalarOperator sop = this.getScalarOperator();

		Point3D p_ref = null; // point from the entry or end segment
		Point3D p_nonref = null; // new point
		boolean addAtEntry = false;
		SegmentElt3D entry = this.getEntryElement();
		SegmentElt3D end = this.getEndElement();

		// check where to add
		if (p1.isEqual(entry.getPoint(0), sop)) {
			p_ref = entry.getPoint(0);
			p_nonref = p2;
			addAtEntry = true;
		} else {
			if (p2.isEqual(entry.getPoint(0), sop)) {
				p_ref = entry.getPoint(0);
				p_nonref = p1;
				addAtEntry = true;
			} else { // here the test for end element
				if (p1.isEqual(end.getPoint(1), sop)) {
					p_ref = end.getPoint(1);
					p_nonref = p2;
					addAtEntry = false;
				} else {
					Point3D p = end.getPoint(1);
					if (p2.isEqual(end.getPoint(1), sop)) {
						p_ref = end.getPoint(1);
						p_nonref = p1;
						addAtEntry = false;
					} else { // not at entry or end point
						throw new GeometryException(
								"New Element is disjunct to net component !");
					}
				}
			}
		}

		SegmentElt3D newSeg = null;
		if (addAtEntry)
			newSeg = new SegmentElt3D(p_nonref, p_ref, sop);
		else
			newSeg = new SegmentElt3D(p_ref, p_nonref, sop);

		// test for potential illegal intersections
		Iterator it = this.getSAM().intersects(newSeg.getMBB()).iterator();
		while (it.hasNext()) {
			SegmentElt3D segment = (SegmentElt3D) it.next();

			if (!segment.isEqual(entry, sop) && !segment.isEqual(end, sop)) {

				SimpleGeoObj intersection = segment.intersection(newSeg, sop);

				if (!(intersection instanceof Point3D)) {
					throw new GeometryException(
							"New Element intersects net component !");
				} else {
					boolean closes = false;
					// intersection has to be of type point
					if (addAtEntry) {
						if (p_nonref.isEqual(end.getPoint(1), sop)) {
							closes = true;// closes the segment net comp
						}
					} else {// add at end
						if (p_nonref.isEqual(entry.getPoint(0), sop)) {
							closes = true; // closes the segment net comp
						}
					}
					if (!closes)
						throw new GeometryException(
								"New Element intersects net component !");
				}
			}
		}

		// add newSeg
		if (addAtEntry) {
			SAM sam = this.getSAM();
			sam.insert(newSeg); // insert
			entry.setNeighbour(1, newSeg); // set neighbours
			newSeg.setNeighbour(0, entry);// set neighbours
			newSeg.setID(getNet().nextElementID()); // set id
			this.setEntryElement(newSeg); // update entry element
			return newSeg;
		} else { // addAt end element
			SAM sam = this.getSAM();
			sam.insert(newSeg); // insert
			end.setNeighbour(0, newSeg); // set neighbours
			newSeg.setNeighbour(1, end); // set neighbours
			newSeg.setID(getNet().nextElementID()); // set id
			return newSeg;
		}
	}

	/**
	 * Removes the given element from the component. At the moment only entry
	 * and end element can be removed if the SegmentNet3DComp is NOT closed !
	 * 
	 * @param elt
	 *            Segment3D to be removed
	 * @return SegmentElt3D - removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public SegmentElt3D removeElt(Segment3D elt) throws UpdateException { // Dag

		if (this.isClosed()) { // find element, set entry and remove it
			SegmentElt3D removable = null;
			Set set = this.getSAM().intersects(elt.getMBB());
			Iterator it = set.iterator();
			while (it.hasNext()) {
				SegmentElt3D current = (SegmentElt3D) it.next();
				if (current.isGeometryEquivalent(elt, this.getScalarOperator())) {
					removable = current;
					break;
				}
			}
			if (removable != null) {
				this.setEntryElement(removable.getNeighbour(0));
				this.getSAM().remove(removable);
				// Here an IllegalArgumentException can be thrown.
				return removable;
			}
			// else
			throw new ContainmentException("Element not contained !");
		}

		// check if elt is geometrically equivalent to entry element and handle
		// if
		SegmentElt3D entry = this.getEntryElement();
		if (elt.isGeometryEquivalent(entry, this.getScalarOperator())) {
			if (this.getSAM().getCount() == 1) { // net will be empty after
				// removal
				this.setEntryElement(null);
				this.setOriented(false);
				this.setConnected(false);
			} else
				this.setEntryElement(entry.getNeighbour(0));

			this.getSAM().remove(entry);
			return entry;
		}
		// else case - check if elt is geometrically equivalent to end element
		// and handle if
		SegmentElt3D end = this.getEndElement();
		if (elt.isGeometryEquivalent(end, this.getScalarOperator())) {
			this.getSAM().remove(end);
			return end;
		}

		if (this.containsElt(elt)) // as net is not closed it is an inside
			// element
			throw new TopologyException(
					"Removal of inside elements not possible !");

		// elt is neither entry nor end element so not contained
		throw new ContainmentException("Element not contained !");
	}

	/**
	 * Tests whether an element with the coordinates of the given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElt(Segment3D seg) {
		Set set = this.getSAM().intersects(seg.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			if (segelt.isGeometryEquivalent(seg, getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether an element with the coordinates of the given point is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElt(Point3D point) {
		ScalarOperator sop = this.getScalarOperator();
		Set set = this.getSAM().intersects(point.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			Point3D[] points = segelt.getPoints();
			if (points[0].isEqual(point, sop) || points[1].isEqual(point, sop))
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
	 * Tests if this component is empty.
	 * 
	 * @return boolean - true if empty, false otherwise.
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
	 * Returns the number of vertices in this component.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		return getVertices();
	}

	/**
	 * Returns the number of edges in this component.
	 * 
	 * @return int - number of edges.
	 */
	public int countEdges() {
		return getSAM().getCount();
	}

	/**
	 * Computes and returns the Euler number for this component.
	 * 
	 * @return int - Euler number
	 */
	public int getEuler() { // Dag
		// Euler formula: vertices - edges + faces
		int verticeCount = this.countVertices();
		int edgeCount = this.countElements();
		int faceCount = 0;
		return (verticeCount - edgeCount + faceCount);
	}

	/**
	 * Returns the length of this component.
	 * 
	 * @return double - length.
	 */
	public double getLength() {
		double length = 0;
		Iterator it = this.getElementsViaRecursion().iterator();
		while (it.hasNext())
			length += ((SegmentElt3D) it.next()).getLength();

		return length;
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
	 * Returns the entry element.<br>
	 * Entry element is the topological start element - it has a neighbour at
	 * index 0.
	 * 
	 * @return SegmentElt3D - entry element.
	 */
	public SegmentElt3D getEntryElement() {
		return this.entry;
	}

	/**
	 * Returns the SegmentElt3D objects in a Set.<br>
	 * This method uses the internal SAM of the component to retrieve all
	 * elements. Use this method in the case you also need spatial tests
	 * afterwards.
	 * 
	 * @return Set with SegmentElt3D objects.
	 */
	public Set getElementsViaSAM() {
		return this.sam.getEntries();
	}

	/**
	 * Returns the SegmentElt3D objects in a Set. This method uses a walk over
	 * the neighbours (NOT THE internal SAM) to retrieve all elements. Use this
	 * method only in case you need to process all the elements.
	 * 
	 * @return Set with SegmentElt3D objects.
	 */
	public Set getElementsViaRecursion() {
		Set set = new IdentityHashSet();
		SegmentElt3D current = this.getEntryElement();

		int count = this.countElements();
		if (count == 0) // return set
			return set;

		if (current == null) { // maybe not yet set
			updateEntryElement();
			if (count == 1) {
				set.add(this.getEntryElement());
				return set;
			}
			if (count > 1) {
				updateEntryElement();
				current = this.getEntryElement();
			}
		}

		if (count == 1) {
			set.add(current);
			return set;
		}

		if (this.isClosed()) { // if closed we must check for loop
			SegmentElt3D begin = current;
			do {
				set.add(current);
				current = current.getNeighbour(0);
			} while (current.hasNeighbour(0)
					&& current.getNeighbour(0) != begin);
		} else {
			do {
				set.add(current);
				current = current.getNeighbour(0);
			} while (current.hasNeighbour(0));
		}

		set.add(current); // add the last
		return set;
	}

	/**
	 * Returns an iterator over the elements in this component.<br>
	 * This method walks iteratively of the neighbours of the net. Use this
	 * method in case you expect to process only some objects and can break at a
	 * certain condition.<br>
	 * After break be sure to release the internal resources by calling the
	 * terminate method.
	 * 
	 * @return SegmentElt3DIterator over the elements.
	 */
	public SegmentElt3DIterator getElementsIterator() {
		return new SegmentElt3DIterator(getEntryElement());
	}

	/**
	 * Returns all Point3D objects of this in a Set.
	 * 
	 * @return Set of all Point3D objects of this.
	 */
	public Set getPoints() { // Dag

		Set set = this.getElementsViaRecursion();
		Set pointHS = new IdentityHashSet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D seg = (SegmentElt3D) it.next();
			for (int j = 0; j < 2; j++)
				pointHS.add(seg.getPoint(j));
		}
		return pointHS;
	}

	/**
	 * Tests whether this components element are in a consistent orientation.
	 * 
	 * @return boolean - true if orientation consistent, false otherwise.
	 */
	public boolean isOrientationConsistent() {
		return this.oriented;
	}

	/**
	 * Makes this net component consistent in the orientation of its elements.<br>
	 * Relies on a connected net - run buildNetTopology first if not connected.
	 */
	public void makeOrientationConsistent() {
		// maybe only one segment is in component
		if (countEdges() == 1) {
			this.setOriented(true);
			return;
		}

		SegmentElt3D current;
		SegmentElt3D old = this.getEntryElement();

		if (old == null) { // method called in constructor - there must be first
			// an entry element
			updateEntryElement();
			old = this.getEntryElement();
		}

		int edgeCount = countEdges();
		int counter = 1;

		do {
			current = old.getNeighbour(0);
			if (current != null) {
				Point3D check = current.getPoint(0);
				// check if current is in the right orientation
				if (!check.isEqual(old.getPoint(1), this.getScalarOperator()))
					current.invertOrientation();
				old = current;
			}
			counter++;
		} while (current != null && current.hasNeighbour(0)
				&& counter < edgeCount + 1);

		this.setOriented(true);
	}

	/**
	 * Tests whether the given point is a border vertex.<br>
	 * (point has to be geometrically equivalent to first or last vertex of
	 * this)
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean true if is a border vertex, false otherwise.
	 */
	public boolean isBorderVertex(Point3D point) {
		SegmentElt3D current = this.getEntryElement();
		if (point.isEqual(current.getPoint(0), getScalarOperator()))
			return true;

		current = this.getEndElement();

		if (point.isEqual(current.getPoint(1), getScalarOperator()))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given segment is a border edge.<br>
	 * The given segment is tested to be geometrically equvalent to first or
	 * last element of this.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is a border edge, false otherwise.
	 */
	public boolean isBorderEdge(Segment3D seg) {
		if (seg.isGeometryEquivalent(this.getEntryElement(), this
				.getScalarOperator()))
			return true;
		if (seg.isGeometryEquivalent(this.getEndElement(), this
				.getScalarOperator()))
			return true;
		else
			return false;
	}

	/**
	 * Tests if the component is correctly connected.<br>
	 * 
	 * @return boolean if correctly connected, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Tests if the component is closed.
	 * 
	 * @return boolean - true if closed, false otherwise.
	 */
	public boolean isClosed() { // Dag
		/*
		 * t Test if there is an entry element. If the new object was created
		 * through constructor, there is no entry element.
		 */
		if (this.getEntryElement() == null)
			return false;

		if (!(this.getEntryElement().getNeighbour(1) == null))
			return true;
		else
			return false;
	}

	/**
	 * Returns the type of this as a <code>ComlexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.COMP_SEGMENT_NET_3D;
	}

	/**
	 * Returns the ID of this component if associated to a net.
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
	 *            int id that should be set.
	 */
	protected void setComponentID(int id) {
		this.id = id;
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return SegmentNet3D - enclosing net.
	 */
	public SegmentNet3D getNet() {
		return net;
	}

	/**
	 * Performs a deep copy of this component with all its recursive members.<br>
	 * Only the reference to the enclosing net is not copied and must be set
	 * afterwards.
	 * 
	 * @return SegmentNet3DComp - deep copy.
	 */
	public SegmentNet3DComp serializationCopy() {
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
			return (SegmentNet3DComp) ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the reference to enclosing SegmentNet3D.
	 * 
	 * @param net3D
	 *            enclosing SegmentNet3D
	 */
	protected void setNet(SegmentNet3D net3D) {
		this.net = net3D;
	}

	/*
	 * Sets the oriented flag of the net.
	 * 
	 * @param oriented boolean true if oriented
	 */
	private void setOriented(boolean oriented) {
		this.oriented = oriented;
	}

	/*
	 * Sets the connected flag of the net.
	 * 
	 * @param con boolean true if connected
	 */
	private void setConnected(boolean con) {
		this.connected = con;
	}

	/*
	 * Loads the sam at construction time and counts the vertices.
	 * 
	 * @param element SegmentElt3D to be inserted into the sam
	 * 
	 * @throws IllegalArgumentException if an attempt is made to construct a
	 * MBB3D whose maximum point is not greater than its minimum point.
	 */
	private void loadSAM(SegmentElt3D[] elements) {
		for (int i = 0; i < elements.length; i++)
			this.sam.insert(elements[i]);
	}

	/**
	 * Builds the neighbour topology of the net for the given Segment elements.
	 * 
	 * @param elts
	 *            SegmentElt3D[]
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public void buildNetTopology(SegmentElt3D[] elts) {
		ScalarOperator so = getScalarOperator();

		for (int i = 0; i < elts.length; i++) {
			if (elts[i].isInterior() != true) {
				SAM sa = this.getSAM();
				Set query = sa.intersects(elts[i].getMBB());
				query.remove(elts[i]);

				Point3D po = null;
				Iterator it;

				/*
				 * If neighbours already exist, delete them from the Query-Set,
				 * so that they are not tested.
				 */
				SegmentElt3D te = null;
				te = elts[i].getNeighbour(0);
				if (te != null)
					query.remove(te);
				te = elts[i].getNeighbour(1);
				if (te != null)
					query.remove(te);

				for (int m = 0; m < 2; m++) {
					int p1 = 0;

					/*
					 * Check at what points the neighbour should be:
					 */
					switch (m) {
					case 0:
						p1 = 1;
						break;
					case 1:
						p1 = 0;
					}

					if ((elts[i].getNeighbour(m)) == null) {
						po = elts[i].getPoint(p1);
						it = query.iterator();

						while (it.hasNext()) {
							SegmentElt3D oSE = (SegmentElt3D) it.next();

							for (int j = 0; j < 2; j++) {
								Point3D ot = oSE.getPoint(j);
								if (po.isEqual(ot, so)) {
									// a point is equal
									elts[i].setNeighbour(m, oSE);
									// neighbour registered at this
									int otind; // index for other TriangleElt3D

									if (j == 0)
										/*
										 * is at point 0, therefore is neighbour
										 * 1
										 */
										otind = 1;
									else
										otind = 0;

									oSE.setNeighbour(otind, elts[i]);
									it.remove(); // query.remove(oTE);
									// ConcurrentModificationException
									break;
								}
							}
						}
					}
				}
			}
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

	/*
	 * Returns the vertices counter.
	 * 
	 * @return int - vertices counter.
	 */
	private int getVertices() {
		return vertices;
	}

	/*
	 * Sets the vertices counter to the given value.
	 * 
	 * @param value int to which the vertices counter should be set
	 */
	private void setVertices(int value) {
		this.vertices = value;
	}

	/*
	 * Sets the MBB of this to the given MBB
	 * 
	 * @param mbb MBB to which the MBB of this should be set
	 */
	private void setMBB(MBB3D mbb) {
		this.mbb = mbb;
	}

	/**
	 * Updates the MBB after changes in the net component.
	 * 
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected void updateMBB() {
		setMBB(this.getSAM().getMBB());
	}

	/**
	 * Updates the Entry elements after changes in the net component.
	 */
	protected void updateEntryElement() {
		SAM sam = this.getSAM();
		Set set = sam.getEntries();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			if (!segelt.isInterior()) {
				if (segelt.hasNeighbour(0)) { // neighbour 0 is at point end
					// (Point 1)
					setEntryElement(segelt);
				} else {
					segelt.invertOrientation();
					setEntryElement(segelt);
				}
				break;
			}
		}

		if (this.getEntryElement() == null) { // every element was interior so
			// its a closed comp
			SAM.NNResult[] result = this.getSAM().nearest(1,
					this.getMBB().getPMin());
			setEntryElement((SegmentElt3D) result[0].getObjectRef());
		}
	}

	/*
	 * Sets the entry element to the given SegmentElt3D.
	 * 
	 * @param seg SegmentElt3D to which the entry element should be set.
	 */
	private void setEntryElement(SegmentElt3D seg) {
		this.entry = seg;
	}

	/**
	 * Updates the vertices, edges, faces statistics after changes in the net
	 * component.
	 */
	protected void updateEulerStatistics() {
		SAM sam = this.getSAM();
		Set set = sam.getEntries();
		Set vert = new EquivalentableHashSet((int) (set.size() * 0.5),
				getScalarOperator(), Equivalentable.GEOMETRY_EQUIVALENT);
		Iterator it = set.iterator();
		while (it.hasNext()) {
			SegmentElt3D segelt = (SegmentElt3D) it.next();
			vert.add(segelt.getPoint(0));
			vert.add(segelt.getPoint(1));
		}
		setVertices(vert.size());
	}

	/**
	 * SegmentElt3DIterator - iterator over the elements of the components.<br>
	 * For releasing the resources occupied by this iterator call the terminate
	 * method ASAP.
	 * 
	 * @author Wolfgang Baer
	 */
	public static class SegmentElt3DIterator {
		private SegmentElt3D actual;
		private SegmentElt3D begin;

		private SegmentElt3DIterator(SegmentElt3D element) {
			actual = element;
			begin = element;
		}

		/**
		 * Tests whether there is a next element available.
		 * 
		 * @return boolean - true if.
		 */
		public boolean hasNext() {
			if (actual != null && actual.getNeighbour(0) == begin) // for closed
				// segment
				// nets
				return true;
			else
				return false;
		}

		/**
		 * Returns the next SegmentElt3D if hasNext() returned true.
		 * 
		 * @return SegmentElt3D - the next element.
		 */
		public SegmentElt3D next() {
			SegmentElt3D temp = actual;
			actual = actual.getNeighbour(0);
			return temp;
		}

		/**
		 * Releases immediately all resources held by this iterator.
		 */
		public void terminate() {
		}
	}

	/**
	 * Searches for an element with the given id and returns it. If it was not
	 * found, returns <code>null</code>.
	 * 
	 * @return SimpleGeoObj - element with the given id.
	 */
	public SimpleGeoObj getElement(int id) {

		Iterator<SegmentElt3D> it = getElementsViaRecursion().iterator();
		while (it.hasNext()) {
			SegmentElt3D seg = it.next();
			if (seg.getID() == id)
				return seg;
		}
		return null;
	}

	public int getID() {
		return id;
	}

}
