package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.uos.igf.db3d.dbms.collections.EquivalentableHashSet;
import de.uos.igf.db3d.dbms.collections.FlagMap;
import de.uos.igf.db3d.dbms.collections.IdentityHashSet;
import de.uos.igf.db3d.dbms.collections.SAM.NNResult;
import de.uos.igf.db3d.dbms.exceptions.ContainmentException;
import de.uos.igf.db3d.dbms.exceptions.GeometryException;
import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * TriangleNet3DComp represents a single triangle net component. All
 * TriangleElt3D objects in this object belong to one semantic component.<br>
 * For triangle nets with several components see @see TriangleNet3D.
 * 
 * @ Markus Jahn
 */
public class Triangle3DComponent extends Component3DAbst {

	/* entry element */
	private Triangle3DElement entry;

	/* orientation clean flag */
	boolean oriented;

	/* connected flag */
	private boolean connected;

	/**
	 * Constructor.<br>
	 * 
	 * @param epsilon
	 *            GeoEpsilon needed for validation
	 */
	public Triangle3DComponent(GeoEpsilon epsilon) {
		super(epsilon);
		this.oriented = false;
		this.connected = false;
	}

	/**
	 * Constructor.<br>
	 * Constructs a TriangleNet3DComp object with the given TriangleElt3D[].<br>
	 * In the given array the neighbourhood topology has not be defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this triangle
	 * array.
	 * 
	 * @param epsilon
	 *            GeoEpsilon needed for validation
	 * @param elements
	 *            TriangleElt3D[]
	 * @throws UpdateException
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public Triangle3DComponent(Triangle3DElement[] elements, GeoEpsilon epsilon)
			throws UpdateException {
		super(epsilon);
		for (Triangle3DElement element : elements) {
			this.addElementWithoutTopologyCheck(element);
		}
		buildNetTopology((Triangle3DElement[]) this.getElementsViaRecursion()
				.toArray());
		this.connected = true;
		updateEntryElement();
		makeOrientationConsistent(epsilon);
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3DElement addElement(Triangle3DElement element)
			throws UpdateException {

		if (this.isEmpty()) { // simplest case
			element.id = this.faceID++;
			this.entry = element;
			this.mbb = element.getMBB();
			this.oriented = true;
			this.connected = true;
			this.sam.insert(element);
			// Here an IllegalArgumentException can be thrown.
			return element;
		}

		if (this.contains(element))
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			throw new ContainmentException("Element already contained !");

		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(element.getMBB());
		Iterator<Triangle3DElement> it = triangles.iterator();

		Net3DAbst.HoldNeighbourStructure[] hns = new Net3DAbst.HoldNeighbourStructure[3];
		int neighbourCounter = 0;

		while (it.hasNext()) { // test for intersections
			Triangle3DElement triElt = it.next();
			Geometry3D sgo = element.intersection(triElt, this.epsilon);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.

			if (sgo != null) {
				switch (sgo.getGeometryType()) {
				case TRIANGLE: // fall through
				case WIREFRAME:
					throw new GeometryException(
							"New Element intersects net component !");

				case POINT:
					Point3D p = (Point3D) sgo;
					if (!(element.hasCorner(p, this.epsilon) || triElt
							.hasCorner(p, this.epsilon)))
						throw new GeometryException(
								"New Element intersects net component !");

					break;
				case SEGMENT:
					Segment3D seg = (Segment3D) sgo;
					int index0 = element.getSegmentIndex(seg, this.epsilon);
					if (index0 != -1) {
						int index1 = triElt.getSegmentIndex(seg, this.epsilon);
						if (index1 != -1) {
							hns[neighbourCounter] = new Net3DAbst.HoldNeighbourStructure(
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
				default:
					break;

				}
			}
		}

		if (neighbourCounter > 0) {
			for (int i = 0; i < neighbourCounter; i++) {
				// set neighborly relations
				((Triangle3DElement) hns[i].getObject(0)).neighbours[hns[i]
						.getIndex(0)] = ((Triangle3DElement) hns[i]
						.getObject(1));
				((Triangle3DElement) hns[i].getObject(1)).neighbours[hns[i]
						.getIndex(1)] = ((Triangle3DElement) hns[i]
						.getObject(0));
			}
			// add element to SAM
			element.id = this.faceID++;
			this.sam.insert(element);
			return element;
		} else
			throw new GeometryException(
					"New Element intersects net component !");
	}

	/**
	 * Removes the given element from the component.<br>
	 * Assumes that an element of geometric equality exists in this.
	 * 
	 * @param element
	 *            Triangle3DElement
	 * @return Triangle3DElement - removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Triangle3DElement removeElement(Triangle3DElement element)
			throws UpdateException { // Dag

		// find element and set removable
		Triangle3DElement removable = null;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(element.getMBB());
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement current = it.next();
			if (current.isGeometryEquivalent(element, this.epsilon)) {
				removable = current;
				break;
			}
		}

		if (removable != null) {
			if (!removable.hasNeighbours()) {
				// element is single
				this.entry = null;
				this.oriented = false;
				this.connected = false;
			} else {
				Triangle3DElement[] neighbour = removable.getNeighbours();

				switch (removable.countNeighbours()) {
				case 1: // only one neighbour -> smooth removal
					neighbour[0].setNeighbourNull(removable, this.epsilon);
					break;

				case 2:
					// set new (potential) neighbourhood first
					int index0 = neighbour[0].setNeighbourNull(removable,
							this.epsilon);
					int index1 = neighbour[1].setNeighbourNull(removable,
							this.epsilon);
					// check if net will still be connected

					if (!isConnectedWith(neighbour[0], neighbour[1])) {
						// reverse settings and return null for
						// "illegal removal operation"
						neighbour[0].neighbours[index0] = removable;
						neighbour[1].neighbours[index1] = removable;
					}
					break;

				case 3:
					// set new (potential) neighbourhood first
					index0 = neighbour[0].setNeighbourNull(removable,
							this.epsilon);
					index1 = neighbour[1].setNeighbourNull(removable,
							this.epsilon);
					int index2 = neighbour[2].setNeighbourNull(removable,
							this.epsilon);
					// check if net will still be connected
					for (int i = 0; i < 2; i++) {
						if (!isConnectedWith(neighbour[i], neighbour[(i + 1)])) {
							// reverse settings and return null for
							// "illegal removal operation"
							neighbour[0].neighbours[index0] = removable;
							neighbour[1].neighbours[index1] = removable;
							neighbour[2].neighbours[index2] = removable;
						}
					}
					break;

				default:
					throw new UpdateException(
							Db3dSimpleResourceBundle
									.getString("db3d.model3d.intexc"));
				}
				if (removable == this.entry) {
					this.entry = neighbour[0];
				}
			}
			this.sam.remove(removable);
			// Here an IllegalArgumentException can be thrown.
			return removable;
		}
		throw new ContainmentException("Element not contained !"); // not
		// removable
	}

	/**
	 * Removes the given element from the component without a topology check.
	 * 
	 * @param element
	 *            Triangle3DElement
	 * @return the removed triangle or <code>null</code> if not removable.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Triangle3DElement removeElementWithoutTopologyCheck(
			Triangle3DElement element) throws UpdateException {

		// find element and set removable
		Triangle3DElement removable = null;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(element.getMBB());
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement current = it.next();
			if (current.isGeometryEquivalent(element, this.epsilon)) {
				removable = current;
				break;
			}
		}

		if (removable == null) {
			return null;
		}

		switch (removable.neighbours.length) {
		case 0: // nothing to do
			break;
		case 1: // only one neighbour -> smooth removal
			removable.neighbours[0].setNeighbourNull(removable, this.epsilon);
			break;

		case 2:
			// set new (potential) neighbourhood first
			removable.neighbours[0].setNeighbourNull(removable, this.epsilon);
			removable.neighbours[1].setNeighbourNull(removable, this.epsilon);
			break;

		case 3:
			// set new (potential) neighbourhood first
			removable.neighbours[0].setNeighbourNull(removable, this.epsilon);
			removable.neighbours[1].setNeighbourNull(removable, this.epsilon);
			removable.neighbours[2].setNeighbourNull(removable, this.epsilon);
			break;

		default:
			throw new UpdateException(
					Db3dSimpleResourceBundle.getString("db3d.model3d.intexc"));
		}
		if (removable == this.entry) {
			throw new UpdateException(
					Db3dSimpleResourceBundle
							.getString("db3d.trianglenet.ohgod"));
			// this.setEntryElement(neighbour[0]);
		}

		this.sam.remove(removable);
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3DElement addElementWithoutTopologyCheck(
			Triangle3DElement element) throws UpdateException {

		// System.out.println("Triangle " + elt.toString());

		if (this.isEmpty()) { // simplest case
			element.id = this.faceID++;
			this.entry = element;
			this.mbb = element.getMBB();
			this.oriented = true;
			this.connected = true;
			this.sam.insert(element);
			// Here an IllegalArgumentException can be thrown.
			return element;
		}

		if (this.containsElement(element))
			throw new UpdateException(
					Db3dSimpleResourceBundle
							.getString("db3d.trianglenet.duplicate"));
		// throw new ContainmentException("Element already contained !");

		// this.getSAM().insert(element);
		// return element;

		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(element.getMBB());

		Iterator<Triangle3DElement> it = triangles.iterator();

		Net3DAbst.HoldNeighbourStructure[] hns = new Net3DAbst.HoldNeighbourStructure[3];
		int neighbourCounter = 0;

		while (it.hasNext()) { // test for intersections
			Triangle3DElement triElt = it.next();
			Geometry3D sgo = element.intersection(triElt, this.epsilon);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.

			if (sgo != null) {
				switch (sgo.getGeometryType()) {
				case TRIANGLE: // fall through
					// Strange: both are equal
					// return element;
					break;
				case WIREFRAME:
					throw new GeometryException(
							"New Element intersects net component !");

				case POINT: // allowed if no topology test
					break;

				case SEGMENT:
					Segment3D seg = (Segment3D) sgo;
					int index0 = element.getSegmentIndex(seg, this.epsilon);
					if (index0 != -1) {
						int index1 = triElt.getSegmentIndex(seg, this.epsilon);
						if (index1 != -1 && neighbourCounter < 3) { // FIXME
							// workaround
							hns[neighbourCounter] = new Net3DAbst.HoldNeighbourStructure(
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
				case LINE:
					break;
				case MBB:
					break;
				case PLANE:
					break;
				case POINTSET:
					break;
				case RECTANGLE:
					break;
				case SEGMENTSET:
					break;
				case TETRAHEDRON:
					break;
				case TETRAHEDRONSET:
					break;
				case TRIANGLESET:
					break;
				case VECTOR:
					break;
				}
			}
		}

		// the element may be place arbitrary - also without connection to the
		// net
		for (int i = 0; i < neighbourCounter; i++) {
			// set neighborly relations
			((Triangle3DElement) hns[i].getObject(0)).neighbours[hns[i]
					.getIndex(0)] = (Triangle3DElement) hns[i].getObject(1);
			((Triangle3DElement) hns[i].getObject(1)).neighbours[hns[i]
					.getIndex(1)] = (Triangle3DElement) hns[i].getObject(0);
		}
		// add element to SAM
		element.id = this.faceID++;
		this.sam.insert(element);
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
	public boolean isConnectedWith(Triangle3DElement start,
			Triangle3DElement end) {
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
	private boolean isConnectedWith(Triangle3DElement start,
			Triangle3DElement end, FlagMap flags) {
		flags.add(start);

		for (int i = 0; i < 3; i++) {
			Triangle3DElement nb = start.neighbours[i];
			if (nb != null) {
				if (!flags.check(nb)) // if not already visited
					if (nb.isGeometryEquivalent(end, this.epsilon))
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
	 * @param triangle
	 *            Triangle3D
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElement(Triangle3D triangle) { // Dag
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(triangle.getMBB());

		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement triElt = it.next();
			if (triElt.isGeometryEquivalent(triangle, this.epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether an element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !
	 * 
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElement(Segment3D segment) { // Dag
		Set<Triangle3DElement> triset = (Set<Triangle3DElement>) this.sam
				.intersects(segment.getMBB());

		Set<Segment3D> segset = this.getSegments(triset);

		Iterator<Segment3D> it = segset.iterator();
		while (it.hasNext()) {
			Segment3D seg = it.next();
			if (seg.isGeometryEquivalent(segment, this.epsilon))
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
	public boolean containsElement(Point3D point) { // Dag
		Set<Triangle3DElement> triset = (Set<Triangle3DElement>) this.sam
				.intersects(point.getMBB());
		Set<Point3D> poiset = this.getPoints(triset);

		Iterator<Point3D> it = poiset.iterator();
		while (it.hasNext()) {
			Point3D poi = it.next();
			if (poi.isEqual(point, this.epsilon))
				return true;
		}
		return false;
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

		Set<Triangle3DElement> set = this.getElementsViaRecursion();
		EquivalentableHashSet pointHS = new EquivalentableHashSet(
				(set.size() * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);

		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement triangle = it.next();
			for (int j = 0; j < 3; j++)
				if (triangle.neighbours[j] == null) {
					pointHS.add(triangle.getPoints()[(j + 1) % 3]);
					pointHS.add(triangle.getPoints()[(j + 2) % 3]);
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

		Set<Triangle3DElement> set = this.getElementsViaRecursion();
		EquivalentableHashSet edgeHS = new EquivalentableHashSet(
				(set.size() * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);

		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement triangle = it.next();
			for (int j = 0; j < 3; j++)
				if (triangle.neighbours[j] == null)
					edgeHS.add(triangle.getSegment(j, this.epsilon));
		}
		return (edgeHS.size());
	}

	/**
	 * Returns the number of faces in the border of this component.
	 * 
	 * @return int - number of faces in the border.
	 */
	public int countBorderFaces() { // Dag

		Set<Triangle3DElement> set = this.getElementsViaRecursion();
		int counter = 0;

		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement triangle = it.next();
			for (int i = 0; i < 3; i++)
				if (triangle.neighbours[i] == null) {
					counter++;
					break;
				}
		}
		return counter;
	}

	/**
	 * Computes and returns the Euler number for this component.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() { // Dag

		// Euler formula: vertices - edges + faces
		int verticeCount = this.vertices.size();
		int edgeCount = this.edges.size();
		int faceCount = this.faces.size();

		return (verticeCount - edgeCount + faceCount);
	}

	/**
	 * Returns the area of this component.
	 * 
	 * @return double - area.
	 */
	public double getArea() { // Dag

		Set<Triangle3DElement> triangles = this.getElementsViaRecursion();
		Iterator<Triangle3DElement> it = triangles.iterator();
		double area = 0;

		while (it.hasNext()) {
			Triangle3DElement triangle = it.next();
			area += triangle.getArea(this.epsilon);
		}
		return area;
	}

	/**
	 * Returns the entry element.
	 * 
	 * @return TriangleElt3D - entry element.
	 */
	public Triangle3DElement getEntryElement() {
		return this.entry;
	}

	/**
	 * Returns the TriangleElt3D objects in a Set. This method uses a walk over
	 * the neighbours (NOT THE internal SAM) to retrieve all elements. Use this
	 * method only in case you need to process all the elements.
	 * 
	 * @return Set with TriangleElt3D objects.
	 */
	public Set<Triangle3DElement> getElementsViaRecursion() {
		Set<Triangle3DElement> set = new IdentityHashSet();
		makeSet(set, this.entry);
		return set;
	}

	/*
	 * Returns all Segment3D objects from given Set of TriangleElt3D objects.
	 * 
	 * @param triangleSet Set of TriangleElt3D elements
	 * 
	 * @return Set of Segment3D objects.
	 */
	private Set<Segment3D> getSegments(Set<Triangle3DElement> triangleSet) { // Dag

		Triangle3DElement[] triangles = triangleSet
				.toArray(new Triangle3DElement[triangleSet.size()]);
		Set<Segment3D> segmentHS = new EquivalentableHashSet(
				(triangles.length * 2), this.epsilon,
				Equivalentable.STRICT_EQUAL);

		for (Triangle3D triangle : triangles) {
			for (int j = 0; j < 3; j++)
				segmentHS.add(triangle.getSegment(j, epsilon));
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
	private Set<Point3D> getPoints(Set<Triangle3DElement> triangleSet) { // Dag

		Triangle3DElement[] triangles = triangleSet
				.toArray(new Triangle3DElement[triangleSet.size()]);
		Set<Point3D> pointHS = new EquivalentableHashSet(
				(triangles.length * 2), this.epsilon,
				Equivalentable.STRICT_EQUAL);

		for (Triangle3D triangle : triangles) {
			for (int j = 0; j < 3; j++)
				pointHS.add(triangle.getPoints()[j]);
		}
		return pointHS;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its miniNet3Dmum point.
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
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Plane3D plane) {// Dag
		Geometry3D sgo = this.getMBB().intersection(plane, this.epsilon);
		if (sgo == null)
			return false;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(sgo.getMBB());
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement tri = it.next();
			if (tri.intersects(plane, this.epsilon))
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Line3D line) { // Dag

		Geometry3D sgo = this.mbb.intersection(line, this.epsilon);
		if (sgo == null)
			return false;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(sgo.getMBB());
		// Here an IllegalArgumentException can be thrown.
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement tri = it.next();
			if (tri.intersects(line, this.epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Test whether this intersects with given segment.
	 * 
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Segment3D seg, GeoEpsilon epsilon)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Segment3D segment) { // Dag

		Geometry3D sgo = this.mbb.intersection(segment.getLine(epsilon),
				this.epsilon);
		if (sgo == null)
			return false;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(sgo.getMBB());
		// Here an IllegalArgumentException can be thrown.
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement tri = (Triangle3DElement) it.next();
			if (tri.intersects(segment, this.epsilon))
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             MBB3D is not a simplex. The exception originates in the
	 *             method intersection(MBB3D, GeoEpsilon) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * 
	 */
	public boolean intersects(MBB3D mbb) { // Dag

		if (!this.mbb.intersects(mbb, this.epsilon))
			return false;

		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(mbb);
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement triElt = it.next();
			if (triElt.intersects(mbb, this.epsilon))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Point3D point) { // Dag
		// tests whether a triangle of this contains point until one is found
		// (or all if not)
		// get spatial objects from SAM which contain point
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.contains(point);

		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement triElt = it.next();
			if (triElt.contains(point, this.epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically.<br>
	 * (even if a part (not only a point) of the given segment is contained the
	 * method returns true)
	 * 
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.\
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Segment3D segment) { // Dag

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

		MBB3D mbb = segment.getMBB();
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(mbb);

		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement triElt = it.next();
			if (triElt.intersectsInt(segment, this.epsilon) == 1)
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
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
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(mbb);

		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement triElt = it.next();
			if ((triElt.intersectsInt(triangle, this.epsilon) == 2))
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
	 * @param epsilon
	 *            GeoEpsilon
	 */
	public void makeOrientationConsistent(GeoEpsilon epsilon) { // Dag
		// starting at entry element, orient its neighbours like itself, use
		// flatset to remember already visited elements
		if (this.isOrientationConsistent())
			return;

		FlagMap flags = new FlagMap();

		Triangle3DElement triangle = entry;

		triangle.makeNeighboursOrientationConsistent(epsilon, flags);

		// set orientation status
		this.oriented = true;
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

		Iterator<Triangle3DElement> it = this.getElementsViaRecursion()
				.iterator();
		while (it.hasNext()) {
			Triangle3DElement t = it.next();
			t.invertOrientation();
		}
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isBorderVertex(Point3D point) { // Dag
		// find all segments containing point
		Set<Triangle3DElement> triset = (Set<Triangle3DElement>) this.sam
				.intersects(point.getMBB());

		Set<Segment3D> segset = this.getSegments(triset);
		// find out if one of those segments is a borderSegment -> point is a
		// borderVertex
		Iterator<Segment3D> it = segset.iterator();
		while (it.hasNext()) {
			Segment3D seg = it.next();
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
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if is a border edge, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isBorderEdge(Segment3D segment) { // Dag

		// find (one) triangle tri containing seg
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(segment.getMBB());
		Triangle3DElement tri = null;
		// set = this.getSegments(set);
		// find out if one of those segments is a borderSegment -> point is a
		// borderVertex
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement triangle = it.next();
			if (triangle.contains(segment, this.epsilon)) {
				// if triangle contains seg, it has not necessarily to be
				// geometrically equvalent to an element of it but that will be
				// checked further below
				tri = triangle;
				break;
			}
		}
		// check whether tri has no neighbour at edge seg -> is border edge
		for (int i = 0; i < 3; i++)
			if ((tri.getSegment(i, this.epsilon).isGeometryEquivalent(segment,
					this.epsilon)) && (tri.neighbours[i] == null))
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
		Triangle3DElement reference = null;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(triangle.getMBB());
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement current = it.next();
			if (current.isGeometryEquivalent(triangle, this.epsilon)) {
				reference = current;
				break;
			}
		}
		if (reference != null) {
			for (int i = 0; i < 3; i++)
				if (reference.neighbours[i] == null)
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
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SURFACE_COMPONENT_E3D;
	}

	/**
	 * Builds the neighbour topology of the net for the given Triangle elements.
	 * 
	 * @param elements
	 *            Triangle3D[] for this the neighbour topology should be built
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void buildNetTopology(Triangle3DElement[] elements) {

		Iterator<Triangle3DElement> it;

		for (int i = 0; i < elements.length; i++) {
			if (elements[i].isInterior() != true) {
				Set<Triangle3DElement> query = (Set<Triangle3DElement>) this.sam
						.intersects(elements[i].getMBB());
				query.remove(elements[i]);

				Point3D po = null;

				/*
				 * If the neighbours already exist, delete them from the query
				 * set, so that they are not tested again.
				 */
				Triangle3DElement te = null;
				te = elements[i].neighbours[0];
				if (te != null)
					query.remove(te);
				te = elements[i].neighbours[1];
				if (te != null)
					query.remove(te);
				te = elements[i].neighbours[2];
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

					if ((elements[i].getNeighbour(m)) == null) {
						po = elements[i].getPoints()[p1];
						it = query.iterator();

						while (it.hasNext()) {
							Triangle3DElement oTE = it.next();

							for (int j = 0; j < 3; j++) {
								Point3D ot = oTE.getPoints()[j];
								if (po.isEqual(ot, this.epsilon)) {
									// a point is equal
									Point3D po1 = elements[i].getPoints()[p2];

									for (int k = 0; k < 3; k++) {
										if (k != j) {
											Point3D ot1 = oTE.getPoints()[k];
											if (po1.isEqual(ot1, this.epsilon)) {
												// 2nd point => they are
												// neighbours

												elements[i].neighbours[m] = oTE;
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

												oTE.neighbours[otind] = elements[i];
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
	 * @param elements
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
	public void checkNetTopology(Triangle3DElement[] elements) {
		// not checked contains errors
		FlagMap flags = new FlagMap();

		for (int i = 0; i < elements.length; i++) {
			if (this.isElementInterior(elements[i], flags) != true) {
				Set<Triangle3DElement> query = (Set<Triangle3DElement>) sam
						.intersects(elements[i].getMBB());
				query.remove(elements[i]);
				if (flags.checkFlag(elements[i], FlagMap.F1) == true) { // neighbour
					// 0
					Triangle3DElement el = elements[i].getNeighbour(0);
					query.remove(el);
				}
				if (flags.checkFlag(elements[i], FlagMap.F2) == true) { // neighbour
					// 1
					Triangle3DElement el = elements[i].getNeighbour(1);
					query.remove(el);
				}
				if (flags.checkFlag(elements[i], FlagMap.F3) == true) { // neighbour
					// 2
					Triangle3DElement el = elements[i].getNeighbour(2);
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

					if (flags.checkFlag(elements[i], sh) != true) {
						boolean noneighbour = true;

						if (elements[i].hasNeighbour(j)) {
							Triangle3DElement el = elements[i].neighbours[j];
							Point3D eltspo = elements[j].getPoints()[p1];
							for (int k = 0; k < 3; k++) {
								Point3D elpo = el.getPoints()[k];
								if (eltspo.isEqual(elpo, this.epsilon) == true) {
									// fist poit found
									eltspo = elements[i].getPoints()[p2];
									for (int n = 0; n < 3; n++) {
										if (eltspo.isEqual(elpo, this.epsilon) == true) {
											// second point found
											elements[i].neighbours[j] = el;
											flags.setFlag(elements[i], sh);
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
											el.neighbours[otind] = elements[i];
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
							Iterator<Triangle3DElement> it = query.iterator();
							Point3D po = elements[i].getPoints()[p1];
							while (it.hasNext()) {
								Triangle3DElement oTE = it.next();

								for (int p = 0; p < 3; p++) {
									Point3D ot = oTE.getPoints()[p];
									if (po.isEqual(ot, this.epsilon)) {
										// a point is equal
										po = elements[i].getPoints()[p2];

										for (int k = 0; k < 3; k++) {
											ot = oTE.getPoints()[k];
											if (po.isEqual(ot, this.epsilon)) {
												// they are neighbours

												elements[i].neighbours[j] = oTE;
												// neighbour registered at this.
												// Attention: wrong variable
												flags.setFlag(elements[i], sh);

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

												oTE.neighbours[otind] = elements[i];
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
	private boolean isElementInterior(Triangle3DElement element, FlagMap flags) {
		if (flags.checkFlag(element, FlagMap.F1)
				&& flags.checkFlag(element, FlagMap.F2)
				&& flags.checkFlag(element, FlagMap.F3))
			return true;
		else
			return false;
	}

	/**
	 * Updates the Entry elements after changes in the net component.
	 */
	protected void updateEntryElement() {
		boolean closed = true;
		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.getEntries();
		Iterator<Triangle3DElement> it = set.iterator();
		while (it.hasNext()) {
			Triangle3DElement trielt = it.next();
			if (!trielt.isInterior()) {
				this.entry = trielt;
				closed = false;
				break;
			}
		}

		if (closed) // if the net is closed take one arbitrary triangle
			this.entry = set.iterator().next();
	}

	/*
	 * Method to collect all elements starting at the given element.
	 * 
	 * @param set Set to collect elements
	 * 
	 * @param elt TriangleElt3D to start collection process
	 */
	private void makeSet(Set<Triangle3DElement> set, Triangle3DElement element) {

		LinkedList<Triangle3DElement> toVisit = new LinkedList<Triangle3DElement>();
		TreeSet<Integer> checked = new TreeSet<Integer>();
		toVisit.add(element);
		checked.add(element.getID());

		Triangle3DElement currTri;
		Triangle3DElement nb;
		// while still triangles "to visit":
		while (!toVisit.isEmpty()) {
			currTri = toVisit.pollFirst();
			set.add(currTri);
			for (int i = 0; i < 3; i++) {
				nb = currTri.neighbours[i];
				// if not already visited:
				if (nb != null && !checked.contains(nb.id)) {
					toVisit.add(nb);
					checked.add(nb.id);
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

		Triangle3DElement tri = null;

		Set<Triangle3DElement> s = this.getElementsViaRecursion();

		Iterator<Triangle3DElement> it = s.iterator();

		while (it.hasNext()) {

			// Searching through all triangles:

			tri = it.next();

			// If an external triangle was found:
			for (int j = 0; j < 3; j++)
				if (tri.neighbours[j] == null)
					outerSegments.add((Segment3D) tri.getSegment(j,
							this.epsilon));
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
		Triangle3DElement tri = null;
		// Getting all triangles:
		Set<Triangle3DElement> s = this.getElementsViaRecursion();
		Iterator<Triangle3DElement> it = s.iterator();
		if (it.hasNext())
			tri = it.next();

		// Looking for the first outer triangle:
		while (tri.countNeighbours() == 3 && it.hasNext()) {
			tri = it.next();
		}

		// now tri is the first outer triange
		Triangle3DElement first = tri; // first points to the first outer
										// triangle
		Set<Segment3D> outerSegments = new HashSet<Segment3D>(); // stores outer
		// segments

		if (tri.countNeighbours() == 0) { // if there is only 1 triangle in the
			// TIN
			outerSegments.add(tri.getSegment(0, this.epsilon));
			outerSegments.add(tri.getSegment(1, this.epsilon));
			outerSegments.add(tri.getSegment(2, this.epsilon));
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
					outerSegments.add(tri.getSegment(0, this.epsilon));
					a = a + 1;
				}
				if (tri.getNeighbour(1) == null) {
					outerSegments.add(tri.getSegment(1, this.epsilon));
					a = a + 2;
				}
				if (tri.getNeighbour(2) == null) {
					outerSegments.add(tri.getSegment(2, this.epsilon));
					a = a + 4;
				}

				Segment3D segTri = null; // pointer to the common segment of tri
				// and its next outer neighbour,
				// regarded as an element of tri

				// Moving to the neighbour in the chosen direction:
				switch (a) {
				case 2:
				case 6:
					segTri = tri.getSegment(0, this.epsilon);
					tri = tri.neighbours[0];
					break;
				case 4:
				case 5:
					segTri = tri.getSegment(1, this.epsilon);
					tri = tri.neighbours[1];
					break;
				case 1:
				case 3:
					segTri = tri.getSegment(2, this.epsilon);
					tri = tri.neighbours[2];
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
					if (segTri.getPoints()[0].isEqual(tri.getPoints()[0],
							this.epsilon))
						startPoint = 0;
					if (segTri.getPoints()[0].isEqual(tri.getPoints()[1],
							this.epsilon))
						startPoint = 1;
					if (segTri.getPoints()[0].isEqual(tri.getPoints()[2],
							this.epsilon))
						startPoint = 2;
					if (segTri.getPoints()[1].isEqual(tri.getPoints()[0],
							this.epsilon))
						endPoint = 0;
					if (segTri.getPoints()[1].isEqual(tri.getPoints()[1],
							this.epsilon))
						endPoint = 1;
					if (segTri.getPoints()[1].isEqual(tri.getPoints()[2],
							this.epsilon))
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

					segTri = tri.getSegment(internalPoint, this.epsilon);
					tri = tri.neighbours[internalPoint];
				}
			} while (tri != first);
			// while tri is not an already visited triangle
		}
		return outerSegments;
	}

	public Collection<Segment3D> getAllSegmentsWithPoint(Point3D point) {
		LinkedList<Segment3D> resultSet = new LinkedList<Segment3D>();
		Collection<Triangle3DElement> allTrianglesWithPoint = getAllTrianglesWithPoint2(point);
		Segment3D seg;
		for (Triangle3DElement tri : allTrianglesWithPoint) {
			for (int i = 0; i < 3; i++) {
				seg = tri.getSegment(i, this.epsilon);
				if (seg.contains(point, this.epsilon)) {
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Set<Triangle3DElement> getAllTrianglesWithPoint(Point3D point) {
		Set<Triangle3DElement> resultSet = new HashSet<Triangle3DElement>();

		Triangle3DElement first = (Triangle3DElement) this.sam
				.nearest(1, point)[0].getObjectRef();
		Triangle3DElement current = null;
		Triangle3DElement prev = first;

		// if current is null, then the selected point lies on the border, so
		// all triangles will have to be searched
		while (current != first && current != null) {

			Triangle3DElement nb = null;
			for (int i = 0; i < 3; i++) {
				if (prev.neighbours[i] != null && prev.neighbours[i] != first
						&& prev.neighbours[i].hasCorner(point, this.epsilon)) {
					nb = prev.neighbours[i];
				}
			}

			prev = current;
			current = nb;
			resultSet.add(current);
		}

		// IF THE SEARCHED POINT LIES ON THE COMPONENT BORDER:
		if (current == null) {
			resultSet = new HashSet<Triangle3DElement>();
			boolean goon = true;
			// goon is true until the next nearest neighbor does not contain
			// point

			int iterationNumber = 1;
			while (goon) {
				int end = 10 * iterationNumber;
				int start = end - 10;

				// get the next 10 nearest triangles:
				NNResult[] nearestResult = this.sam.nearest(end, point);
				Triangle3DElement[] nearestTris = new Triangle3DElement[nearestResult.length];
				for (int i = 0; i < nearestResult.length; i++) {
					nearestTris[i] = (Triangle3DElement) nearestResult[i]
							.getObjectRef();
				}

				// check if the found nearest triangles contain the point (if
				// not, break the loop):
				for (int i = start; i < end && goon; i++) {
					if (nearestTris[i].hasCorner(point, this.epsilon)) {
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

	public Collection<Triangle3DElement> getAllTrianglesWithPoint2(Point3D point) {

		LinkedList<Triangle3DElement> result = new LinkedList<Triangle3DElement>();

		Iterator<Triangle3DElement> it = this.getElementsViaRecursion()
				.iterator();

		while (it.hasNext()) {
			Triangle3DElement t = it.next();
			if (t.hasCorner(point, this.epsilon)) {
				result.add(t);
			}

		}

		return result;
	}

	public Collection<Triangle3D> getAllTrianglesWithPoint3(Point3D point) {

		LinkedList<Triangle3D> result = new LinkedList<Triangle3D>();

		Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
				.intersects(point.getMBB());

		for (Triangle3DElement tri : set) {
			if (tri.hasCorner(point, epsilon)) {
				result.add(tri);
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

		Set<Triangle3DElement> elements = this.getElementsViaRecursion();

		for (Triangle3DElement tri : elements) {
			// System.out.println("element number " + count );

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3 && j != i; j++) {

					if (map.containsKey(tri.getPoints()[i])) {
						List<Point3D> list = (List<Point3D>) map.get(tri
								.getPoints()[i]);
						if (!list.contains(tri.getPoints()[j])) {
							list.add(tri.getPoints()[j]);
						}
					} else {
						List<Point3D> list = new ArrayList<Point3D>();
						list.add(tri.getPoints()[j]);
						map.put(tri.getPoints()[i], list);
					}

					if (map.containsKey(tri.getPoints()[j])) {
						List<Point3D> list = (List<Point3D>) map.get(tri
								.getPoints()[j]);
						if (!list.contains(tri.getPoints()[i])) {
							list.add(tri.getPoints()[i]);
						}
					} else {
						List<Point3D> list = new ArrayList<Point3D>();
						list.add(tri.getPoints()[i]);
						map.put(tri.getPoints()[j], list);
					}
				}
			}
		}

		return map;
	}

	/**
	 * Returns the set of objects which are inside the given MBB3D.
	 * 
	 * @param mbb
	 *            - the MBB3D object for test
	 * @return Set - a Set object containing the result
	 */
	public Set<Triangle3DElement> inside(MBB3D mbb) {
		return (Set<Triangle3DElement>) this.sam.inside(mbb);
	}

	/**
	 * Returns the element with the given id.
	 * 
	 * @param id
	 *            int the id to be found
	 * @return TriangleElt3D with the given id.
	 */
	@Override
	public Triangle3DElement getElement(int id) {

		Iterator<Triangle3DElement> it = this.getElementsViaRecursion()
				.iterator();
		while (it.hasNext()) {
			Triangle3DElement t = it.next();
			if (t.getID() == id)
				return t;
		}
		return null;
	}

}
