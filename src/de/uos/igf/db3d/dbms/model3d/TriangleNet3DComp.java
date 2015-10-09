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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.api.ContainmentException;
import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.api.GeometryException;
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
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;
import de.uos.igf.db3d.dbms.util.FlagMap;
import de.uos.igf.db3d.dbms.util.IdentityHashSet;
import de.uos.igf.db3d.dbms.util.RStar;
import de.uos.igf.db3d.dbms.util.SAM;
import de.uos.igf.db3d.dbms.util.SAM.NNResult;
import de.uos.igf.db3d.resources.DB3DLogger;

/**
 * TriangleNet3DComp represents a single triangle net component. All
 * TriangleElt3D objects in this object belong to one semantic component.<br>
 * For triangle nets with several components see @see TriangleNet3D.
 */
public class TriangleNet3DComp implements PersistentObject, ComplexGeoObj,
		Serializable {

	/* serial version */
	private static final long serialVersionUID = 5361954449792026181L;

	/* for serialization - ref to enclosing net skipped */
	private static final ObjectStreamField[] serialPersistentFields;

	static {
		serialPersistentFields = new ObjectStreamField[] {
				new ObjectStreamField("sop", ScalarOperator.class),
				new ObjectStreamField("entry", TriangleElt3D.class),
				new ObjectStreamField("oriented", Boolean.TYPE),
				new ObjectStreamField("connected", Boolean.TYPE),
				new ObjectStreamField("mbb", MBB3D.class),
				new ObjectStreamField("closed", Boolean.TYPE),
				new ObjectStreamField("sam", SAM.class),
				new ObjectStreamField("vertices", Integer.TYPE),
				new ObjectStreamField("edges", Integer.TYPE),
				new ObjectStreamField("id", Integer.TYPE) };
	}

	/* id */
	private int id;

	/* ScalarOperator */
	private ScalarOperator sop;

	/* entry element */
	private TriangleElt3D entry;

	/* orientation clean flag */
	private boolean oriented;

	/* connected flag */
	private boolean connected;

	/* MBB of this component */
	private MBB3D mbb;

	/* reference to net */
	private TriangleNet3D net;

	/* spatial tree */
	private SAM sam;

	/* vertices counter */
	private int vertices;

	/* edges counter */
	private int edges;

	/**
	 * Constructor.<br>
	 * 
	 * @param sop
	 *            ScalarOperator needed for validation
	 */
	protected TriangleNet3DComp(ScalarOperator sop) {
		this.id = -1;
		this.sop = sop;
		this.oriented = false;
		this.connected = false;
		this.sam = new RStar(MAX_SAM, sop);
	}

	/**
	 * Constructor.<br>
	 * Constructs a TriangleNet3DComp object with the given TriangleElt3D[].<br>
	 * In the given array the neighbourhood topology has not be defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this triangle
	 * array.
	 * 
	 * @param sop
	 *            ScalarOperator needed for validation
	 * @param elements
	 *            TriangleElt3D[]
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public TriangleNet3DComp(ScalarOperator sop, TriangleElt3D[] elements) {
		this.id = -1;
		this.sop = sop;
		this.sam = new RStar(MAX_SAM, sop);
		// MBB3D XXMBB = elements[0].getMBB();
		// for( int i=1; i < elements.length; i++) {
		// XXMBB = XXMBB.union(elements[i].getMBB(), sop);
		// }
		//
		// this.sam = new Octree((short)10, XXMBB, sop);
		DB3DLogger.logger.log(Level.FINEST, "Insert data into SAM");
		double time = System.currentTimeMillis();
		loadSAM(elements);
		DB3DLogger.logger.log(Level.FINEST,
				"took " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		// Here an IllegalArgumentException can be thrown.
		this.mbb = sam.getMBB();
		// Here an IllegalArgumentException can be thrown.
		DB3DLogger.logger.log(Level.FINEST, "Build Topology");
		this.buildNetTopology(elements);
		DB3DLogger.logger.log(Level.FINEST,
				"took " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		this.connected = true;
		DB3DLogger.logger.log(Level.FINEST, "Update Entry element");
		updateEntryElement();
		DB3DLogger.logger.log(Level.FINEST,
				"took " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		DB3DLogger.logger.log(Level.FINEST, "Update Euler statistics");
		updateEulerStatistics();
		DB3DLogger.logger.log(Level.FINEST,
				"took " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		DB3DLogger.logger.log(Level.FINEST, "Make orientation consistent");
		this.makeOrientationConsistent(sop);
		DB3DLogger.logger.log(Level.FINEST,
				"took " + (System.currentTimeMillis() - time));
	}

	/**
	 * Adds the given element to the component. If you need to hold a reference
	 * on the element update your variable with the return value element !
	 * 
	 * @param elt
	 *            Triangle3D
	 * @return TriangleElt3D - the inserted instance, <code>null</code> if not
	 *         inserted.
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
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt3D addElt(Triangle3D elt) throws UpdateException {
		TriangleElt3D element = new TriangleElt3D(elt);

		if (this.isEmpty()) { // simplest case
			this.setEntryElement(element);
			this.setMBB(element.getMBB());
			this.setOriented(true);
			this.setConnected(true);
			this.getSAM().insert(element);
			// Here an IllegalArgumentException can be thrown.
			return element;
		}

		if (this.contains(elt))
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			throw new ContainmentException("Element already contained !");

		Set<Equivalentable> triangles = this.getSAM().intersects(elt.getMBB());
		Iterator<Equivalentable> it = triangles.iterator();

		SpatialObject3D.HoldNeighbourStructure[] hns = new SpatialObject3D.HoldNeighbourStructure[3];
		int neighbourCounter = 0;

		while (it.hasNext()) { // test for intersections
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			SimpleGeoObj sgo = element.intersection(triElt,
					this.getScalarOperator());
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.

			if (sgo != null) {
				switch (sgo.getType()) {
				case SimpleGeoObj.TRIANGLE3D: // fall through
				case SimpleGeoObj.WIREFRAME3D:
					throw new GeometryException(
							"New Element intersects net component !");

				case SimpleGeoObj.POINT3D:
					Point3D p = (Point3D) sgo;
					if (!(element.hasCorner(p, this.getScalarOperator()) || triElt
							.hasCorner(p, this.getScalarOperator())))
						throw new GeometryException(
								"New Element intersects net component !");

					break;
				case SimpleGeoObj.SEGMENT3D:
					Segment3D seg = (Segment3D) sgo;
					int index0 = element.getSegmentIndex(seg,
							this.getScalarOperator());
					if (index0 != -1) {
						int index1 = triElt.getSegmentIndex(seg,
								this.getScalarOperator());
						if (index1 != -1) {
							hns[neighbourCounter] = new SpatialObject3D.HoldNeighbourStructure(
									element, index0, triElt, index1);
							neighbourCounter++;
						} else
							throw new GeometryException(
									"New Element intersects net component !"); // seg
						// is not edge of triElt

					} else
						throw new GeometryException(
								"New Element intersects net component !"); // seg
					// is not edge of this

				}
			}
		}

		if (neighbourCounter > 0) {
			for (int i = 0; i < neighbourCounter; i++) {
				// set neighborly relations
				((TriangleElt3D) hns[i].getObject(0)).setNeighbour(
						hns[i].getIndex(0),
						((TriangleElt3D) hns[i].getObject(1)));
				((TriangleElt3D) hns[i].getObject(1)).setNeighbour(
						hns[i].getIndex(1),
						((TriangleElt3D) hns[i].getObject(0)));
			}
			// add element to SAM
			this.getSAM().insert(element);
			return element;
		} else
			throw new GeometryException(
					"New Element intersects net component !");
	}

	/**
	 * Removes the given element from the component.<br>
	 * Assumes that an element of geometric equality exists in this.
	 * 
	 * @param elt
	 *            Triangle3D
	 * @return TriangleElt3D - removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public TriangleElt3D removeElt(Triangle3D elt) throws UpdateException { // Dag

		// find element and set removable
		TriangleElt3D removable = null;
		Set<Equivalentable> set = this.getSAM().intersects(elt.getMBB());
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D current = (TriangleElt3D) it.next();
			if (current.isGeometryEquivalent(elt, this.getScalarOperator())) {
				removable = current;
				break;
			}
		}

		if (removable != null) {
			if (!removable.hasNeighbours()) {
				// element is single
				this.setEntryElement(null);
				this.setOriented(false);
				this.setConnected(false);
			} else {
				TriangleElt3D[] neighbour = removable.getNeighbours();

				switch (neighbour.length) {
				case 1: // only one neighbour -> smooth removal
					neighbour[0].setNeighbourNull(removable,
							this.getScalarOperator());
					break;

				case 2:
					// set new (potential) neighbourhood first
					int index0 = neighbour[0].setNeighbourNull(removable,
							this.getScalarOperator());
					int index1 = neighbour[1].setNeighbourNull(removable,
							this.getScalarOperator());
					// check if net will still be connected

					if (!isConnectedWith(neighbour[0], neighbour[1])) {
						// reverse settings and return null for
						// "illegal removal operation"
						neighbour[0].setNeighbour(index0, removable);
						neighbour[1].setNeighbour(index1, removable);
					}
					break;

				case 3:
					// set new (potential) neighbourhood first
					index0 = neighbour[0].setNeighbourNull(removable,
							this.getScalarOperator());
					index1 = neighbour[1].setNeighbourNull(removable,
							this.getScalarOperator());
					int index2 = neighbour[2].setNeighbourNull(removable,
							this.getScalarOperator());
					// check if net will still be connected
					for (int i = 0; i < 2; i++) {
						if (!isConnectedWith(neighbour[i], neighbour[(i + 1)])) {
							// reverse settings and return null for
							// "illegal removal operation"
							neighbour[0].setNeighbour(index0, removable);
							neighbour[1].setNeighbour(index1, removable);
							neighbour[2].setNeighbour(index2, removable);
						}
					}
					break;

				default:
					throw new UpdateException(
							Db3dSimpleResourceBundle
									.getString("db3d.model3d.intexc"));
				}
				if (removable == this.getEntryElement()) {
					this.setEntryElement(neighbour[0]);
				}
			}
			this.getSAM().remove(removable);
			// Here an IllegalArgumentException can be thrown.
			return removable;
		}
		throw new ContainmentException("Element not contained !"); // not
		// removable
	}

	/**
	 * Removes the given element from the component without a topology check.
	 * 
	 * @param elt
	 *            Triangle3D
	 * @return the removed triangle or <code>null</code> if not removable.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public TriangleElt3D removeEltWithoutTopologyCheck(Triangle3D elt)
			throws UpdateException {

		// find element and set removable
		TriangleElt3D removable = null;
		Set<Equivalentable> set = this.getSAM().intersects(elt.getMBB());
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D current = (TriangleElt3D) it.next();
			if (current.isGeometryEquivalent(elt, this.getScalarOperator())) {
				removable = current;
				break;
			}
		}

		if (removable == null) {
			return null;
		}

		TriangleElt3D[] neighbour = removable.getNeighbours();

		switch (neighbour.length) {
		case 0: // nothing to do
			break;
		case 1: // only one neighbour -> smooth removal
			neighbour[0].setNeighbourNull(removable, this.getScalarOperator());
			break;

		case 2:
			// set new (potential) neighbourhood first
			neighbour[0].setNeighbourNull(removable, this.getScalarOperator());
			neighbour[1].setNeighbourNull(removable, this.getScalarOperator());
			break;

		case 3:
			// set new (potential) neighbourhood first
			neighbour[0].setNeighbourNull(removable, this.getScalarOperator());
			neighbour[1].setNeighbourNull(removable, this.getScalarOperator());
			neighbour[2].setNeighbourNull(removable, this.getScalarOperator());
			break;

		default:
			throw new UpdateException(
					Db3dSimpleResourceBundle.getString("db3d.model3d.intexc"));
		}
		if (removable == this.getEntryElement()) {
			throw new UpdateException(
					Db3dSimpleResourceBundle
							.getString("db3d.trianglenet.ohgod"));
			// this.setEntryElement(neighbour[0]);
		}

		this.getSAM().remove(removable);
		// Here an IllegalArgumentException could be thrown.
		// System.out.println("Actual removal step ... done");
		return removable;
	}

	/**
	 * Adds the given element to the component. If you need to hold a reference
	 * on the element, update your variable with the return value element !
	 * 
	 * @param elt
	 *            Triangle3D
	 * @return TriangleElt3D - the inserted instance, <code>null</code> if not
	 *         inserted.
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
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt3D addEltWithoutTopologyCheck(Triangle3D elt)
			throws UpdateException {
		TriangleElt3D element = new TriangleElt3D(elt);

		// System.out.println("Triangle " + elt.toString());

		if (this.isEmpty()) { // simplest case
			this.setEntryElement(element);
			this.setMBB(element.getMBB());
			this.setOriented(true);
			this.setConnected(true);
			this.getSAM().insert(element);
			// Here an IllegalArgumentException can be thrown.
			return element;
		}

		if (this.containsElt(elt))
			throw new UpdateException(
					Db3dSimpleResourceBundle
							.getString("db3d.trianglenet.duplicate"));
		// throw new ContainmentException("Element already contained !");

		// this.getSAM().insert(element);
		// return element;

		Set<Equivalentable> triangles = this.getSAM().intersects(elt.getMBB());

		Iterator<Equivalentable> it = triangles.iterator();

		SpatialObject3D.HoldNeighbourStructure[] hns = new SpatialObject3D.HoldNeighbourStructure[3];
		int neighbourCounter = 0;

		while (it.hasNext()) { // test for intersections
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			SimpleGeoObj sgo = element.intersection(triElt,
					this.getScalarOperator());
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.

			if (sgo != null) {
				switch (sgo.getType()) {
				case SimpleGeoObj.TRIANGLE3D: // fall through
					// Strange: both are equal
					// return element;
					break;
				case SimpleGeoObj.WIREFRAME3D:
					throw new GeometryException(
							"New Element intersects net component !");

				case SimpleGeoObj.POINT3D: // allowed if no topology test
					break;

				case SimpleGeoObj.SEGMENT3D:
					Segment3D seg = (Segment3D) sgo;
					int index0 = element.getSegmentIndex(seg,
							this.getScalarOperator());
					if (index0 != -1) {
						int index1 = triElt.getSegmentIndex(seg,
								this.getScalarOperator());
						if (index1 != -1 && neighbourCounter < 3) { // FIXME
							// workaround
							hns[neighbourCounter] = new SpatialObject3D.HoldNeighbourStructure(
									element, index0, triElt, index1);
							neighbourCounter++;
						}
						// else throw new
						// GeometryException("New Element intersects net component !");
						// // seg is not edge of triElt
					}
					// else throw new
					// GeometryException("New Element intersects net component !");
					// // seg is not edge of this
				}
			}
		}

		// the element may be place arbitrary - also without connection to the
		// net
		for (int i = 0; i < neighbourCounter; i++) {
			// set neighborly relations
			((TriangleElt3D) hns[i].getObject(0)).setNeighbour(
					hns[i].getIndex(0), ((TriangleElt3D) hns[i].getObject(1)));
			((TriangleElt3D) hns[i].getObject(1)).setNeighbour(
					hns[i].getIndex(1), ((TriangleElt3D) hns[i].getObject(0)));
		}
		// add element to SAM
		this.getSAM().insert(element);
		return element;
	}

	/**
	 * Checks whether start element is via neighbour elements somehow connected
	 * with given end element.
	 * 
	 * @param start
	 *            TriangleElt3D
	 * @param end
	 *            TriangleElt3D
	 * @return boolean - whether elements are connected or not.
	 */
	public boolean isConnectedWith(TriangleElt3D start, TriangleElt3D end) {
		return isConnectedWith(start, end, new FlagMap());
	}

	/*
	 * Checks whether start element is via neighbour elements somehow connected
	 * with given end element.
	 * 
	 * @param start TriangleElt3D
	 * 
	 * @param end TriangleElt3D
	 * 
	 * @param flags FlagMap
	 * 
	 * @return boolean - whether elements are connected or not.
	 */
	private boolean isConnectedWith(TriangleElt3D start, TriangleElt3D end,
			FlagMap flags) {
		flags.add(start);

		for (int i = 0; i < 3; i++) {
			TriangleElt3D nb = start.getNeighbour(i);
			if (nb != null) {
				if (!flags.check(nb)) // if not already visited
					if (nb.isGeometryEquivalent(end, this.getScalarOperator()))
						return true;
					else if (isConnectedWith(nb, end, flags))
						return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether an element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !
	 * 
	 * @param tri
	 *            Triangle3D
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElt(Triangle3D tri) { // Dag
		Set<Equivalentable> triangles = this.getSAM().intersects(tri.getMBB());

		Iterator<Equivalentable> it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			if (triElt.isGeometryEquivalent(tri, this.getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether an element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElt(Segment3D seg) { // Dag
		Set<Equivalentable> set = this.getSAM().intersects(seg.getMBB());
		set = this.getSegments(set);

		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			Segment3D segment = (Segment3D) it.next();
			if (segment.isGeometryEquivalent(seg, this.getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether an element with the coordinates of given point is contained
	 * in the component.<br>
	 * Identity test based on epsilon equality on coordinates !
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean containsElt(Point3D point) { // Dag
		Set<Equivalentable> set = this.getSAM().intersects(point.getMBB());
		set = this.getPoints(set);

		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			Point3D p = (Point3D) it.next();
			if (p.isEqual(point, this.getScalarOperator()))
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
		return getEdges();
	}

	/**
	 * Returns the number of vertices in the border of this component.
	 * 
	 * @return int - number of vertices in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public int countBorderVertices() { // Dag

		Set<Equivalentable> set = this.getElementsViaRecursion();
		EquivalentableHashSet pointHS = new EquivalentableHashSet(
				(set.size() * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);

		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D triangle = (TriangleElt3D) it.next();
			for (int j = 0; j < 3; j++)
				if (!triangle.hasNeighbour(j)) {
					pointHS.add(triangle.getPoint((j + 1) % 3));
					pointHS.add(triangle.getPoint((j + 2) % 3));
				}
		}
		return (pointHS.size());
	}

	/**
	 * Returns the number of edges in the border of this component.
	 * 
	 * @return int -number of edges in the border.
	 */
	public int countBorderEdges() { // Dag

		Set<Equivalentable> set = this.getElementsViaRecursion();
		EquivalentableHashSet edgeHS = new EquivalentableHashSet(
				(set.size() * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);

		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D triangle = (TriangleElt3D) it.next();
			for (int j = 0; j < 3; j++)
				if (!triangle.hasNeighbour(j))
					edgeHS.add(triangle.getSegment(j));
		}
		return (edgeHS.size());
	}

	/**
	 * Returns the number of faces in the border of this component.
	 * 
	 * @return int - number of faces in the border.
	 */
	public int countBorderFaces() { // Dag

		Set<Equivalentable> set = this.getElementsViaRecursion();
		int counter = 0;

		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D triangle = (TriangleElt3D) it.next();
			for (int i = 0; i < 3; i++)
				if (!triangle.hasNeighbour(i)) {
					counter++;
					break;
				}
		}
		return counter;
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
	public int getEuler() { // Dag
		// Euler formula: vertices - edges + faces
		int verticeCount = this.getVertices();
		int edgeCount = this.getEdges();
		int faceCount = this.getElementsViaRecursion().size();

		return (verticeCount - edgeCount + faceCount);
	}

	/**
	 * Returns the area of this component.
	 * 
	 * @return double - area.
	 */
	public double getArea() { // Dag

		Set<Equivalentable> triangles = this.getElementsViaRecursion();
		Iterator<Equivalentable> it = triangles.iterator();
		double area = 0;

		while (it.hasNext()) {
			TriangleElt3D triangle = (TriangleElt3D) it.next();
			area += triangle.getArea();
		}
		return area;
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
	 * Returns the entry element.
	 * 
	 * @return TriangleElt3D - entry element.
	 */
	public TriangleElt3D getEntryElement() {
		return this.entry;
	}

	/**
	 * Returns the TriangleElt3D objects in a Set.<br>
	 * This method uses the internal SAM of the component to retrieve all
	 * elements. Use this method in the case you also need spatial tests
	 * afterwards.
	 * 
	 * @return Set with TriangleElt3D objects.
	 */
	public Set<?> getElementsViaSAM() {
		return this.sam.getEntries();
	}

	/**
	 * Returns the TriangleElt3D objects in a Set. This method uses a walk over
	 * the neighbours (NOT THE internal SAM) to retrieve all elements. Use this
	 * method only in case you need to process all the elements.
	 * 
	 * @return Set with TriangleElt3D objects.
	 */
	public Set getElementsViaRecursion() {
		Set set = new IdentityHashSet();
		makeSet(set, this.getEntryElement());
		return set;
	}

	/**
	 * Returns an iterator over the elements in this component.<br>
	 * This method walks iteratively over the neighbours of the net. Use this
	 * method in case you expect to process only some objects and want to break
	 * at a certain condition.<br>
	 * After break be sure to release the internal resources by calling the
	 * terminate method.
	 * 
	 * @return TriangleElt3DIterator - iterator over the elements of this.
	 */
	public TriangleElt3DIterator getElementsIterator() {
		return new TriangleElt3DIterator(getEntryElement());
	}

	/**
	 * Returns all Point3D objects of this in a Set.
	 * 
	 * @return Set of all Point3D objects of this.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public Set<Equivalentable> getPoints() { // Dag
		Set points = this.getElementsViaSAM();
		return this.getPoints(points);
	}

	/**
	 * Returns all Segment3D objects of this in a Set.
	 * 
	 * @return Set with all Segment3D objects of this.
	 */
	public Set<Equivalentable> getSegments() { // Dag
		return this.getSegments(this.getElementsViaRecursion());
	}

	/*
	 * Returns all Segment3D objects from given Set of TriangleElt3D objects.
	 * 
	 * @param triangleSet Set of TriangleElt3D elements
	 * 
	 * @return Set of Segment3D objects.
	 */
	private Set<Equivalentable> getSegments(Set<Equivalentable> triangleSet) { // Dag

		TriangleElt3D[] triangles = triangleSet
				.toArray(new TriangleElt3D[triangleSet.size()]);
		EquivalentableHashSet segmentHS = new EquivalentableHashSet(
				(triangles.length * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);

		for (int i = 0; i < triangles.length; i++) {
			Segment3D[] segments = triangles[i].getSegments();
			for (int j = 0; j < 3; j++)
				segmentHS.add(segments[j]);
		}
		return segmentHS;
	}

	/*
	 * Returns all Point3D objects from given Set of TriangleElt3D objects.
	 * 
	 * @param triangleSet Set of TriangleElt3D elements.
	 * 
	 * @return Set of Point3D objects.
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 */
	private Set<Equivalentable> getPoints(Set<Equivalentable> triangleSet) { // Dag

		Set<Equivalentable> pointHS = new IdentityHashSet();
		Iterator<Equivalentable> it = triangleSet.iterator();
		while (it.hasNext()) {
			Triangle3D tri = (Triangle3D) it.next();
			for (int j = 0; j < 3; j++)
				pointHS.add(tri.getPoint(j));
		}
		return pointHS;
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return TriangleNet3D - endlosing net.
	 */
	public TriangleNet3D getNet() {
		return net;
	}

	/**
	 * Performs a deep copy of this component with all its recursive members.<br>
	 * Only the reference to the enclosing net is not copied and must be set
	 * afterwards.
	 * 
	 * @return TriangleNet3DComp - deep copy.
	 */
	public TriangleNet3DComp serializationCopy() {
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
			return (TriangleNet3DComp) ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the reference to enclosing TriangleNet3D.
	 * 
	 * @param net3D
	 *            TriangleNet3D, enclosing net.
	 */
	public void setNet(TriangleNet3D net3D) {
		this.net = net3D;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
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
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Plane3D plane) {// Dag
		SimpleGeoObj sgo = this.getMBB().intersection(plane,
				this.getScalarOperator());
		if (sgo == null)
			return false;
		Set<Equivalentable> set = this.getSAM().intersects(sgo.getMBB());
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D tri = (TriangleElt3D) it.next();
			if (tri.intersects(plane, this.getScalarOperator()))
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
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Line3D line) { // Dag

		SimpleGeoObj sgo = this.getMBB().intersection(line,
				this.getScalarOperator());
		if (sgo == null)
			return false;
		Set<Equivalentable> set = this.getSAM().intersects(sgo.getMBB());
		// Here an IllegalArgumentException can be thrown.
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D tri = (TriangleElt3D) it.next();
			if (tri.intersects(line, this.getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Test whether this intersects with given segment.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Segment3D seg, ScalarOperator sop)
	 *             method of the class Segment3D (which computes the
	 *             intersection of two segments) called by this method returns a
	 *             value that is not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Segment3D seg) { // Dag

		SimpleGeoObj sgo = this.getMBB().intersection(seg.getLine(sop),
				this.getScalarOperator());
		if (sgo == null)
			return false;
		Set<Equivalentable> set = this.getSAM().intersects(sgo.getMBB());
		// Here an IllegalArgumentException can be thrown.
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D tri = (TriangleElt3D) it.next();
			if (tri.intersects(seg, this.getScalarOperator()))
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
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             MBB3D is not a simplex. The exception originates in the
	 *             method intersection(MBB3D, ScalarOperator) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * 
	 */
	public boolean intersects(MBB3D mbb) { // Dag

		if (!this.getMBB().intersects(mbb, this.getScalarOperator()))
			return false;

		Set<Equivalentable> set = this.getSAM().intersects(mbb);
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			if (triElt.intersects(mbb, this.getScalarOperator()))
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
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean contains(Point3D point) { // Dag
		// tests whether a triangle of this contains point until one is found
		// (or all if not)
		// get spatial objects from SAM which contain point
		Set<?> triangles = this.getSAM().contains(point);

		Iterator<?> it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			if (triElt.contains(point, this.getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically.<br>
	 * (even if a part (not only a point) of the given segment is contained the
	 * method returns true)
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.\
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean contains(Segment3D seg) { // Dag

		/*
		 * Problem: it is not enough to check the elements. It is necessary to
		 * find the elements that intersect with the MBB, then intersect them
		 * with the Segment and check if the result (if any) is completely
		 * contained. -> if this is true for all parts and if all parts
		 * completely describe the segment -> then "true".
		 * 
		 * It is only interesting if the segment is partially contained, i.e. in
		 * the mathematical sense min of the two points of the segment - the
		 * case of a possible cutting results in false.
		 */

		MBB3D mbb = seg.getMBB();
		Set<Equivalentable> triangles = this.getSAM().intersects(mbb);

		Iterator<Equivalentable> it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			if (triElt.intersectsInt(seg, sop) == 1)
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given triangle geometrically<br>
	 * (even if a partial area of the given triangle is contained the method
	 * returns true).
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean contains(Triangle3D triangle) { // Dag

		/*
		 * Problem: it is not enough to check the elements. It is necessary to
		 * find the elements that intersect with the MBB, then intersect them
		 * with the Segment and check if the result (if any) is completely
		 * contained. -> if this is true for all parts and if all parts
		 * completely describe the segment -> then "true".
		 * 
		 * Similar to the method above with the Segment. Only when a part that
		 * "has area" (not just a vertex or a segment) is contained -> true.
		 */

		MBB3D mbb = triangle.getMBB();
		Set<Equivalentable> triangles = this.getSAM().intersects(mbb);

		Iterator<Equivalentable> it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D triElt = (TriangleElt3D) it.next();
			if ((triElt.intersectsInt(triangle, sop) == 2))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this net is oriented.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean isOrientationConsistent() {
		return this.oriented;
	}

	/**
	 * Makes this net component consistent in the orientation of its elements.
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	public void makeOrientationConsistent(ScalarOperator sop) { // Dag
		// starting at entry element, orient its neighbours like itself, use
		// flatset to remember already visited elements
		if (this.isOrientationConsistent())
			return;

		FlagMap flags = new FlagMap();

		TriangleElt3D triangle = getEntryElement();

		triangle.makeNeighboursOrientationConsistent(sop, flags);

		// set orientation status
		this.setOriented(true);
	}

	/**
	 * Inverts the orientation of every triangle element.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void invertOrientation() { // Dag

		TriangleElt3DIterator it = this.getElementsIterator();
		while (it.hasNext())
			it.next().invertOrientation();
	}

	/**
	 * Tests whether the given point is a border vertex.<br>
	 * The point has to be geometrically equvalent to a vertex of this<br>
	 * (otherwise <code>false</code> will be returned).
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is a border vertex, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isBorderVertex(Point3D point) { // Dag
		// find all segments containing point
		Set<Equivalentable> set = this.getSAM().intersects(point.getMBB());

		set = this.getSegments(set);
		// find out if one of those segments is a borderSegment -> point is a
		// borderVertex
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			Segment3D seg = (Segment3D) it.next();
			if (this.isBorderEdge(seg))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether the given segment is a border edge.<br>
	 * The segment has to be geometrically equvalent to an edge of this
	 * (otherwise <code>false</code> will be returned).
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is a border edge, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isBorderEdge(Segment3D seg) { // Dag

		// find (one) triangle tri containing seg
		Set<Equivalentable> set = this.getSAM().intersects(seg.getMBB());
		TriangleElt3D tri = null;
		// set = this.getSegments(set);
		// find out if one of those segments is a borderSegment -> point is a
		// borderVertex
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D triangle = (TriangleElt3D) it.next();
			if (triangle.contains(seg, this.getScalarOperator())) {
				// if triangle contains seg, it has not necessarily to be
				// geometrically equvalent to an element of it but that will be
				// checked further below
				tri = triangle;
				break;
			}
		}
		// check whether tri has no neighbour at edge seg -> is border edge
		for (int i = 0; i < 3; i++)
			if ((tri.getSegment(i).isGeometryEquivalent(seg,
					this.getScalarOperator()))
					&& (tri.getNeighbour(i) == null))
				return true;

		return false;
	}

	/**
	 * Tests whether the given triangle is a border face.<br>
	 * The given triangle has to be geometrically equvalent to an element of
	 * this (otherwise <code>false</code> will be returned).
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if is a border face, false otherwise.
	 */
	public boolean isBorderFace(Triangle3D triangle) { // Dag
		// find triangle which fits geomerically the given one
		TriangleElt3D reference = null;
		Set<Equivalentable> set = this.getSAM().intersects(triangle.getMBB());
		Iterator<Equivalentable> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D current = (TriangleElt3D) it.next();
			if (current
					.isGeometryEquivalent(triangle, this.getScalarOperator())) {
				reference = current;
				break;
			}
		}
		if (reference != null) {
			for (int i = 0; i < 3; i++)
				if (reference.getNeighbour(i) == null)
					return true;
		}
		return false;
	}

	/**
	 * Tests if the component is correctly connected.
	 * 
	 * @return boolean - true if correctly connected, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Return the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.COMP_TRIANGLE_NET_3D;
	}

	/**
	 * Sets the oriented flag of the net.
	 * 
	 * @param oriented
	 *            boolean - true if oriented, false otherwise.
	 */
	public void setOriented(boolean oriented) {
		this.oriented = oriented;
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
	 * Returns the ID of this component if associated with a net.
	 * 
	 * @return int -id of this.
	 */
	public int getComponentID() {
		return id;
	}

	/**
	 * Sets the component ID.<br>
	 * Called if the component is added to a net.
	 * 
	 * @param id
	 *            int id to be set
	 */
	public void setComponentID(int id) {
		this.id = id;
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
	 * @param elements Triangle3D[]
	 * 
	 * @throws IllegalArgumentException if an attempt is made to construct a
	 * MBB3D whose maximum point is not greater than its minimum point.
	 */
	private void loadSAM(TriangleElt3D[] elements) {
		for (int i = 0; i < elements.length; i++)
			this.sam.insert(elements[i]);
	}

	/**
	 * Builds the neighbour topology of the net for the given Triangle elements.
	 * 
	 * @param elts
	 *            TriangleElt3D[] for this the neighbour topology should be
	 *            built
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void buildNetTopology(TriangleElt3D[] elts) {
		ScalarOperator so = getScalarOperator();

		for (int i = 0; i < elts.length; i++) {
			if (elts[i].isInterior() != true) {
				SAM sa = this.getSAM();
				Set<Equivalentable> query = sa.intersects(elts[i].getMBB());
				query.remove(elts[i]);

				Point3D po = null;
				Iterator<Equivalentable> it;

				/*
				 * If the neighbours already exist, delete them from the query
				 * set, so that they are not tested again.
				 */
				TriangleElt3D te = null;
				te = elts[i].getNeighbour(0);
				if (te != null)
					query.remove(te);
				te = elts[i].getNeighbour(1);
				if (te != null)
					query.remove(te);
				te = elts[i].getNeighbour(2);
				if (te != null)
					query.remove(te);

				for (int m = 0; m < 3; m++) {
					int p1 = 0;
					int p2 = 0;
					switch (m) {
					// check at what neighbour's points it should be
					case 0:
						p1 = 1;
						p2 = 2;
						break;
					case 1:
						p1 = 0;
						p2 = 2;
						break;
					case 2:
						p1 = 0;
						p2 = 1;
					}

					if ((elts[i].getNeighbour(m)) == null) {
						po = elts[i].getPoint(p1);
						it = query.iterator();

						while (it.hasNext()) {
							TriangleElt3D oTE = (TriangleElt3D) it.next();

							for (int j = 0; j < 3; j++) {
								Point3D ot = oTE.getPoint(j);
								if (po.isEqual(ot, so)) {
									// a point is equal
									Point3D po1 = elts[i].getPoint(p2);

									for (int k = 0; k < 3; k++) {
										if (k != j) {
											Point3D ot1 = oTE.getPoint(k);
											if (po1.isEqual(ot1, so)) {
												// 2nd point => they are
												// neighbours

												elts[i].setNeighbour(m, oTE);
												// neighbour registered at this
												int otind;
												// index for other TriangleElt3D

												if (j != 2 && k != 2)
													// old comparison
													// (j==0
													// &&
													// k==1)
													// ||
													// (j==1&&k==0))
													otind = 2;
												// this is oppossite of point 2
												else if (j != 0 && k != 0)
													// old comparision
													// (j==1
													// &&
													// k==2)
													// ||
													// (j==2
													// &&
													// k==1))
													otind = 0;
												else
													otind = 1;

												oTE.setNeighbour(otind, elts[i]);
												it.remove();
												break;
											}
										}
									}
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
	 * Checks net topology.
	 * 
	 * @param elts
	 *            TriangleElt3D for which the topology should be checked
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void checkNetTopology(TriangleElt3D[] elts) {
		// not checked contains errors
		ScalarOperator so = getScalarOperator();
		FlagMap flags = new FlagMap();

		for (int i = 0; i < elts.length; i++) {
			if (this.isEltInterior(elts[i], flags) != true) {
				Set<Equivalentable> query = sam.intersects(elts[i].getMBB());
				query.remove(elts[i]);
				if (flags.checkFlag(elts[i], FlagMap.F1) == true) { // neighbour
					// 0
					TriangleElt3D el = elts[i].getNeighbour(0);
					query.remove(el);
				}
				if (flags.checkFlag(elts[i], FlagMap.F2) == true) { // neighbour
					// 1
					TriangleElt3D el = elts[i].getNeighbour(1);
					query.remove(el);
				}
				if (flags.checkFlag(elts[i], FlagMap.F3) == true) { // neighbour
					// 2
					TriangleElt3D el = elts[i].getNeighbour(2);
					query.remove(el);
				}
				for (int j = 0; j < 3; j++) { // for every side of the triangle
					int p1;
					int p2;
					short sh;

					if (j == 0) {
						// check at which points the neighbour should be
						p1 = 1;
						p2 = 2;
						sh = FlagMap.F1;
					} else if (j == 1) {
						p1 = 0;
						p2 = 2;
						sh = FlagMap.F2;
					} else { // f?r j=2
						p1 = 0;
						p2 = 1;
						sh = FlagMap.F3;
					}

					if (flags.checkFlag(elts[i], sh) != true) {
						boolean noneighbour = true;

						if (elts[i].hasNeighbour(j)) {
							TriangleElt3D el = elts[i].getNeighbour(j);
							Point3D eltspo = elts[j].getPoint(p1);
							for (int k = 0; k < 3; k++) {
								Point3D elpo = el.getPoint(k);
								if (eltspo.isEqual(elpo, so) == true) {
									// fist poit found
									eltspo = elts[i].getPoint(p2);
									for (int n = 0; n < 3; n++) {
										if (eltspo.isEqual(elpo, so) == true) {
											// second point found
											elts[i].setNeighbour(j, el);
											flags.setFlag(elts[i], sh);
											// neighbour is registered and the
											// flag is set
											int otind;
											// index for other TriangleElt3D
											short fl;
											if ((n == 0 && k == 1)
													|| (n == 1 && k == 0)) {
												otind = 2;
												// this is opposite of point 2
												fl = FlagMap.F3;
											} else if ((n == 1 && k == 2)
													|| (n == 2 && k == 1)) {
												otind = 0;
												fl = FlagMap.F1;
											} else {
												otind = 1;
												fl = FlagMap.F2;
											}
											el.setNeighbour(otind, elts[i]);
											flags.setFlag(el, fl);
											// is set as neighbour for other
											// triangle and the flag is set
											noneighbour = false;
											// query.remove(el); //delete
											// triangle from set
										}
									}
								}
							}
						}

						if (noneighbour == true) {
							// if no neighbour has been set until now
							Iterator<Equivalentable> it = query.iterator();
							Point3D po = elts[i].getPoint(p1);
							while (it.hasNext()) {
								TriangleElt3D oTE = (TriangleElt3D) it.next();

								for (int p = 0; p < 3; p++) {
									Point3D ot = oTE.getPoint(p);
									if (po.isEqual(ot, so)) {
										// a point is equal
										po = elts[i].getPoint(p2);

										for (int k = 0; k < 3; k++) {
											ot = oTE.getPoint(k);
											if (po.isEqual(ot, so)) {
												// they are neighbours

												elts[i].setNeighbour(j, oTE);
												// neighbour registered at this.
												// Attention: wrong variable
												flags.setFlag(elts[i], sh);

												int otind;
												// index for other TriangleElt3D
												short fl;

												if ((p == 0 && k == 1)
														|| (p == 1 && k == 0)) {
													otind = 2;
													// this lies opposite to
													// point 2
													fl = FlagMap.F3;
												} else if ((p == 1 && k == 2)
														|| (p == 2 && k == 1)) {
													otind = 0;
													fl = FlagMap.F1;

												} else {
													otind = 1;
													fl = FlagMap.F2;
												}

												oTE.setNeighbour(otind, elts[i]);
												flags.setFlag(oTE, fl);
												query.remove(oTE);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/*
	 * Tests if the given TriangleElt3D is an interior element.
	 * 
	 * @param elt TriangleElt3D to be tested
	 * 
	 * @param flags FlagMap
	 */
	private boolean isEltInterior(TriangleElt3D elt, FlagMap flags) {
		if (flags.checkFlag(elt, FlagMap.F1)
				&& flags.checkFlag(elt, FlagMap.F2)
				&& flags.checkFlag(elt, FlagMap.F3))
			return true;
		else
			return false;
	}

	/*
	 * Returns the number of vertices in this component.
	 * 
	 * @return vertices - int number of vertices.
	 */
	private int getVertices() {
		return vertices;
	}

	/*
	 * Sets the vertices counter to the given value.
	 * 
	 * @param value int the value to which the vertices counter should be set
	 */
	private void setVertices(int value) {
		this.vertices = value;
	}

	/*
	 * Returns the edges counter of this.
	 * 
	 * @return int - number of edges.
	 */
	private int getEdges() {
		return edges;
	}

	/*
	 * Sets the edges counter to the given value.
	 * 
	 * @param value int the value to which the edges counter should be set
	 */
	private void setEdges(int value) {
		this.edges = value;
	}

	/*
	 * Sets the MBB of this to the given MBB
	 * 
	 * @param mbb MBB to be set
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
		boolean closed = true;
		Set<?> set = this.getSAM().getEntries();
		Iterator<?> it = set.iterator();
		while (it.hasNext()) {
			TriangleElt3D trielt = (TriangleElt3D) it.next();
			if (!trielt.isInterior()) {
				setEntryElement(trielt);
				closed = false;
				break;
			}
		}

		DB3DLogger.logger.log(
				Level.INFO,
				"TriangleNet3DComp: updateEntryElement. set.size = "
						+ set.size() + " verticies count " + vertices);

		if (closed) // if the net is closed take one arbitrary triangle
			setEntryElement((TriangleElt3D) set.iterator().next());
	}

	/*
	 * Sets the entry element to the given TriangleElt3D.
	 * 
	 * @param tri TriangleElt3D to which the entry element is to be set
	 */
	private void setEntryElement(TriangleElt3D tri) {
		this.entry = tri;
	}

	/**
	 * Updates the vertices, edges, faces statistics after changes in the net
	 * component.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	protected void updateEulerStatistics() {
		Set<?> set = this.getSAM().getEntries();
		// TODO: Check if it is okay to use normal HashSets instead of
		// EquivalentableHashSets

		// Set<Point3D> vert = new EquivalentableHashSet((int) (set.size() *
		// 1.5),
		// getScalarOperator(), Equivalentable.GEOMETRY_EQUIVALENT);
		// Set<Segment3D> edg = new EquivalentableHashSet(
		// (int) (set.size() * 1.5), getScalarOperator(),
		// Equivalentable.GEOMETRY_EQUIVALENT);

		// new:
		// TODO: Adapt equal method of Point3D and Segment3D to fit
		// GEOMETRY_EQUIVALENT pattern
		HashSet<Point3D> vert = new HashSet<Point3D>();
		HashSet<Segment3D> edg = new HashSet<Segment3D>();

		Iterator<?> it = set.iterator();
		TriangleElt3D trielt = null;
		int cnt = 0;
		double time = System.currentTimeMillis();
		int size = set.size();
		while (it.hasNext()) {
			cnt++;
			if (cnt % 10000 == 0) {

				DB3DLogger.logger.log(Level.FINEST, "10.000 entries = "
						+ (System.currentTimeMillis() - time) + "\n"
						+ "Rest takes about: " + ((size - cnt) / 10000)
						* (System.currentTimeMillis() - time));

				time = System.currentTimeMillis();
			}
			trielt = (TriangleElt3D) it.next();
			for (int j = 0; j < 3; j++) {
				vert.add(trielt.getPoint(j));
				edg.add(trielt.getSegment(j));
			}
		}
		setVertices(vert.size());
		setEdges(edg.size());
	}

	/*
	 * Method to collect all elements starting at the given element.
	 * 
	 * @param set Set to collect elements
	 * 
	 * @param elt TriangleElt3D to start collection process
	 */
	private void makeSet(Set<Equivalentable> set, TriangleElt3D elt) {

		LinkedList<TriangleElt3D> toVisit = new LinkedList<TriangleElt3D>();
		TreeSet<Integer> checked = new TreeSet<Integer>();
		toVisit.add(elt);
		checked.add(elt.getID());

		TriangleElt3D currTri;
		TriangleElt3D nb;
		// while still triangles "to visit":
		while (!toVisit.isEmpty()) {
			currTri = toVisit.pollFirst();
			set.add(currTri);
			for (int i = 0; i < 3; i++) {
				nb = currTri.getNeighbour(i);
				// if not already visited:
				if (nb != null && !checked.contains(nb.getID())) {
					toVisit.add(nb);
					checked.add(nb.getID());
				}
			}
		}

	}

	/**
	 * Finds the outer boundary of a tin. Runs over all triangles and records
	 * their outer segments.
	 * 
	 * @return HashSet with outer segments.
	 * @throws IllegalStateException
	 *             - if this TIN component is empty
	 * @author Daria Golovko
	 */
	public Set<Segment3D> findTinBorder1() {
		if (this.isEmpty())
			throw new IllegalStateException("The TIN is empty!");

		HashSet<Segment3D> outerSegments = new HashSet<Segment3D>();

		TriangleElt3D tri = null;

		Set s = this.getElementsViaSAM();

		Iterator<Equivalentable> it = s.iterator();

		while (it.hasNext()) {

			// Searching through all triangles:

			tri = (TriangleElt3D) it.next();

			// If an external triangle was found:
			for (int j = 0; j < 3; j++)
				if (!tri.hasNeighbour(j))
					outerSegments.add((Segment3D) tri.getSegment(j));
		}

		return outerSegments;
	}

	/**
	 * Finds the outer boundary of a tin. Finds the fisrt outer triangle,
	 * afterwards follows the edge of the tin.
	 * 
	 * @return HashSet with outer segments
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @author Daria Golovko
	 */

	public Set<Segment3D> findTinBorder2() {
		TriangleElt3D tri = null;
		// Getting all triangles:
		Set s = this.getElementsViaSAM();
		Iterator<Equivalentable> it = s.iterator();
		if (it.hasNext())
			tri = (TriangleElt3D) it.next();

		// Looking for the first outer triangle:
		while (tri.countNeighbours() == 3 && it.hasNext()) {
			tri = (TriangleElt3D) it.next();
		}

		// now tri is the first outer triange
		TriangleElt3D first = tri; // first points to the first outer triangle
		Set<Segment3D> outerSegments = new HashSet<Segment3D>(); // stores outer
		// segments

		if (tri.countNeighbours() == 0) { // if there is only 1 triangle in the
			// TIN
			outerSegments.add(tri.getSegment(0));
			outerSegments.add(tri.getSegment(1));
			outerSegments.add(tri.getSegment(2));
		}

		else { // if there is more than 1 triangle in the TIN
			do {
				int a = 0; // indicates which segments are outer segments; 1 if
				// segment0,
				// 2 if segment1, 4 if segment2, 3 if segments0 and 1, 5 if
				// segments0 and 2,
				// 6 if segments1 and 2

				// recording outer segments:
				if (tri.getNeighbour(0) == null) {
					outerSegments.add(tri.getSegment(0));
					a = a + 1;
				}
				if (tri.getNeighbour(1) == null) {
					outerSegments.add(tri.getSegment(1));
					a = a + 2;
				}
				if (tri.getNeighbour(2) == null) {
					outerSegments.add(tri.getSegment(2));
					a = a + 4;
				}

				Segment3D segTri = null; // pointer to the common segment of tri
				// and its next outer neighbour,
				// regarded as an element of tri

				// Moving to the neighbour in the chosen direction:
				switch (a) {
				case 2:
				case 6:
					segTri = tri.getSegment(0);
					tri = tri.getNeighbour(0);
					break;
				case 4:
				case 5:
					segTri = tri.getSegment(1);
					tri = tri.getNeighbour(1);
					break;
				case 1:
				case 3:
					segTri = tri.getSegment(2);
					tri = tri.getNeighbour(2);
					break;
				}

				// Moving further in the same direction until the neighbour has
				// an outer segment:
				while (tri.countNeighbours() == 3) {
					int internalPoint = 5;
					/*
					 * index of the inner point of the new tri, belongs to
					 * segTri. We look for this point because it has the same
					 * index as the neighbour that has to be found in order to
					 * move to the next triangle.
					 */

					int startPoint = 5;
					// index of the point of the new tri that is equal to the
					// start of segTri

					int endPoint = 5;
					// index of the point of the new tri that is equal to the
					// end of segTri

					// Looking for the point of segTri with a bigger index:
					if (segTri.getPoint(0).isEqual(tri.getPoint(0), sop))
						startPoint = 0;
					if (segTri.getPoint(0).isEqual(tri.getPoint(1), sop))
						startPoint = 1;
					if (segTri.getPoint(0).isEqual(tri.getPoint(2), sop))
						startPoint = 2;
					if (segTri.getPoint(1).isEqual(tri.getPoint(0), sop))
						endPoint = 0;
					if (segTri.getPoint(1).isEqual(tri.getPoint(1), sop))
						endPoint = 1;
					if (segTri.getPoint(1).isEqual(tri.getPoint(2), sop))
						endPoint = 2;

					if ((startPoint == 0 && endPoint == 1)
							|| (startPoint == 1 && endPoint == 0))
						internalPoint = 1;
					if ((startPoint == 1 && endPoint == 2)
							|| (startPoint == 2 && endPoint == 1))
						internalPoint = 2;
					if ((startPoint == 0 && endPoint == 2)
							|| (startPoint == 2 && endPoint == 0))
						internalPoint = 0;

					segTri = tri.getSegment(internalPoint);
					tri = tri.getNeighbour(internalPoint);
				}
			} while (tri != first);
			// while tri is not an already visited triangle
		}
		return outerSegments;
	}

	public Collection<Segment3D> getAllSegmentsWithPoint(Point3D point) {
		LinkedList<Segment3D> resultSet = new LinkedList<Segment3D>();
		Collection<TriangleElt3D> allTrianglesWithPoint = getAllTrianglesWithPoint2(point);
		Segment3D seg;
		for (TriangleElt3D tri : allTrianglesWithPoint) {
			for (int i = 0; i < 3; i++) {
				seg = tri.getSegment(i);
				if (seg.contains(point, sop)) {
					resultSet.add(seg);
				}
			}
		}
		return resultSet;
	}

	/**
	 * This method returns all triangles of this component that the given
	 * <code>point</code> is part of.
	 * 
	 * @param point
	 *            the point that has to be part of the searched triangles
	 * @return the search result as a set of triangles. The set may be empty but
	 *         is never <code>null</code>.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set<TriangleElt3D> getAllTrianglesWithPoint(Point3D point) {
		Set<TriangleElt3D> resultSet = new HashSet<TriangleElt3D>();

		TriangleElt3D first = (TriangleElt3D) this.getSAM().nearest(1, point)[0]
				.getObjectRef();
		TriangleElt3D current = null;
		TriangleElt3D prev = first;

		// if current is null, then the selected point lies on the border, so
		// all triangles will have to be searched
		while (current != first && current != null) {

			TriangleElt3D nb = null;
			for (int i = 0; i < 3; i++) {
				if (prev.getNeighbour(i) != null
						&& prev.getNeighbour(i) != first
						&& prev.getNeighbour(i).hasCorner(point, sop)) {
					nb = prev.getNeighbour(i);
				}
			}

			prev = current;
			current = nb;
			resultSet.add(current);
		}

		// IF THE SEARCHED POINT LIES ON THE COMPONENT BORDER:
		if (current == null) {
			resultSet = new HashSet<TriangleElt3D>();
			boolean goon = true;
			// goon is true until the next nearest neighbor does not contain
			// point

			int iterationNumber = 1;
			while (goon) {
				int end = 10 * iterationNumber;
				int start = end - 10;

				// get the next 10 nearest triangles:
				NNResult[] nearestResult = this.getSAM().nearest(end, point);
				TriangleElt3D[] nearestTris = new TriangleElt3D[nearestResult.length];
				for (int i = 0; i < nearestResult.length; i++) {
					nearestTris[i] = (TriangleElt3D) nearestResult[i]
							.getObjectRef();
				}

				// check if the found nearest triangles contain the point (if
				// not, break the loop):
				for (int i = start; i < end && goon; i++) {
					if (nearestTris[i].hasCorner(point, sop)) {
						resultSet.add(nearestTris[i]);
					} else {
						goon = false;
					}
				}
				iterationNumber++;
			}
		}

		return resultSet;
	}

	public Collection<TriangleElt3D> getAllTrianglesWithPoint2(Point3D point) {

		LinkedList<TriangleElt3D> result = new LinkedList<TriangleElt3D>();

		TriangleElt3DIterator triEltsIt = this.getElementsIterator();

		TriangleElt3D nextTri;
		while (triEltsIt.hasNext()) {
			nextTri = triEltsIt.next();
			if (nextTri.hasCorner(point, sop)) {
				result.add(nextTri);
			}

		}

		return result;
	}

	public Collection<Triangle3D> getAllTrianglesWithPoint3(Point3D point) {

		LinkedList<Triangle3D> result = new LinkedList<Triangle3D>();

		Set<Equivalentable> set = this.getSAM().intersects(point.getMBB());

		Triangle3D tri;
		for (Equivalentable equiv : set) {
			if (equiv instanceof Triangle3D) {
				tri = (Triangle3D) equiv;
				if (tri.hasCorner(point, sop)) {
					result.add(tri);
				}
			}
		}

		return result;

	}

	/**
	 * Creates index of direct connections of the vertices of this component in
	 * the form of a map.
	 * 
	 * @return a map where each vertex is mapped to a list of vertices which are
	 *         its direct connections
	 */
	public Map<Point3D, List<Point3D>> createPointIndex() {

		Map<Point3D, List<Point3D>> map = new HashMap<Point3D, List<Point3D>>();

		Set elements = this.getElementsViaSAM();
		int count = 0;
		for (Object el : elements) {
			TriangleElt3D tri = (TriangleElt3D) el;
			count++;
			// System.out.println("element number " + count );

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3 && j != i; j++) {

					if (map.containsKey(tri.getPoint(i))) {
						List<Point3D> list = (List<Point3D>) map.get(tri
								.getPoint(i));
						if (!list.contains(tri.getPoint(j))) {
							list.add(tri.getPoint(j));
						}
					} else {
						List<Point3D> list = new ArrayList<Point3D>();
						list.add(tri.getPoint(j));
						map.put(tri.getPoint(i), list);
					}

					if (map.containsKey(tri.getPoint(j))) {
						List<Point3D> list = (List<Point3D>) map.get(tri
								.getPoint(j));
						if (!list.contains(tri.getPoint(i))) {
							list.add(tri.getPoint(i));
						}
					} else {
						List<Point3D> list = new ArrayList<Point3D>();
						list.add(tri.getPoint(i));
						map.put(tri.getPoint(j), list);
					}
				}
			}
		}

		return map;
	}

	/**
	 * TriangleElt3DIterator - iterator over the elements of the components.<br>
	 * For releasing the resources occupied by this iterator call the terminate
	 * method ASAP.
	 * 
	 * @author Wolfgang Baer
	 */
	public static final class TriangleElt3DIterator {

		/* the next element */
		private TriangleElt3D next;

		/*
		 * the current element we stop the recusive method to server the
		 * iteration
		 */
		private Actual actual;

		/* the stack for tracking the recursion */
		private Stack<Actual> stack;

		/* a set of visited elements */
		private Set<Equivalentable> visited;

		/* signals if its a start call to hasNext() */
		private boolean start;

		/*
		 * Constructs a TriangleElt3DIterator with the given element as the
		 * current element.
		 * 
		 * @param element TriangleElt3D which is used as the current iteration
		 * element
		 */
		private TriangleElt3DIterator(TriangleElt3D element) {
			this.visited = new IdentityHashSet();
			this.stack = new Stack<Actual>();
			actual = new Actual(element);
			start = true;
			next = null;
		}

		/**
		 * Tests whether there is a next element available.
		 * 
		 * @return boolean - true if available, false otherwise.
		 */
		public boolean hasNext() {
			TriangleElt3D tri = recursiveFunction();
			if (tri != null) {
				if (start)
					start = false;
				next = tri;
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Returns the next TriangleElt3D if hasNext() returned true.
		 * 
		 * @return TriangleElt3D - next element.
		 */
		public TriangleElt3D next() {
			return next;
		}

		/*
		 * Private workhorse method.
		 * 
		 * @return TriangleElt3D - if != null we have a next element.
		 */
		private TriangleElt3D recursiveFunction() {

			while (!stack.isEmpty() || start) {

				if (actual.i < 2) {
					do {
						actual.incI();
						TriangleElt3D tri = actual.element
								.getNeighbour(actual.i);

						if (tri != null && !visited.contains(tri)) {
							visited.add(tri);
							stack.push(actual);
							actual = new Actual(tri);
							return tri;
						} else {
							// do nothing
						}
					} while (actual.i < 2);
				} else
					actual = stack.pop();
			}
			return null;
		}

		/**
		 * Releases immediatly all recources held by this iterator.
		 */
		public void terminate() {
			stack.clear();
			stack = null;
			visited.clear();
			visited = null;
		}

		/**
		 * Class representing the current iteration element
		 */
		public static final class Actual {

			/* TriangleElt3D contained in this */
			private TriangleElt3D element;

			/* index */
			private byte i;

			/*
			 * Constructs a current iteration element from the given
			 * TriangleElt3D elemet
			 * 
			 * @param element TriangleElt3D from which the current iteration
			 * element is constructed
			 */
			private Actual(TriangleElt3D element) {
				this.element = element;
				this.i = -1;
			}

			/*
			 * Increments the index by 1.
			 */
			private void incI() {
				i++;
			}
		}

	}

	/**
	 * Returns the element with the given id.
	 * 
	 * @param id
	 *            int the id to be found
	 * @return TriangleElt3D with the given id.
	 */
	public TriangleElt3D getElement(int id) {

		Iterator<TriangleElt3D> it = getElementsViaRecursion().iterator();
		while (it.hasNext()) {
			TriangleElt3D t = it.next();
			if (t.getID() == id)
				return t;
		}
		return null;
	}

	/**
	 * Returns the set of objects which are inside the given MBB3D.
	 * 
	 * @param mbb
	 *            - the MBB3D object for test
	 * @return Set - a Set object containing the result
	 */
	public Set inside(MBB3D mbb) {
		return getSAM().inside(mbb);
	}

	/**
	 * @return the id
	 */
	public int getID() {
		return id;
	}
}
