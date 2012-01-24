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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;
import de.uos.igf.db3d.dbms.util.FlagMap;
import de.uos.igf.db3d.dbms.util.IdentityHashSet;
import de.uos.igf.db3d.dbms.util.RStar;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * TetrahedronNet3DComp represents a single tetrahedron net component. All
 * TetrahedronElt3D objects in this object belong to one semantic component.<br>
 * For tetrahedron nets with several components see @see TetrahedronNet3D.
 */
public class TetrahedronNet3DComp implements PersistentObject, ComplexGeoObj,
		Serializable {

	/* for serialization - ref to enclosing net skipped */
	private static final ObjectStreamField[] serialPersistentFields;

	static {
		serialPersistentFields = new ObjectStreamField[] {
				new ObjectStreamField("sop", ScalarOperator.class),
				new ObjectStreamField("entry", TetrahedronElt3D.class),
				new ObjectStreamField("connected", Boolean.TYPE),
				new ObjectStreamField("mbb", MBB3D.class),
				new ObjectStreamField("sam", SAM.class),
				new ObjectStreamField("vertices", Integer.TYPE),
				new ObjectStreamField("edges", Integer.TYPE),
				new ObjectStreamField("faces", Integer.TYPE),
				new ObjectStreamField("id", Integer.TYPE) };
	}

	/* id */
	private int id;

	/* ScalarOperator */
	private ScalarOperator sop;

	/* entry element */
	private TetrahedronElt3D entry;

	/* connected flag */
	private boolean connected;

	/* MBB of this component */
	private MBB3D mbb;

	/* reference to net */
	private TetrahedronNet3D net;

	/* spatial tree */
	private SAM sam;

	/* vertices counter */

	private int vertices;
	/* edges counter */

	private int edges;
	/* faces counter */

	private int faces;

	/**
	 * Constructor.<br>
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	protected TetrahedronNet3DComp(ScalarOperator sop) {
		this.id = -1;
		this.sop = sop;
		this.connected = false;
		this.sam = new RStar(MAX_SAM, sop);
	}

	/**
	 * Constructor.<br>
	 * Constructs a TetrahedronNet3DComp object with the given
	 * TetrahedronElt3D[].<br>
	 * In the given array the neighbourhood topology has not been defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this
	 * tetrahedron array.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @param elements
	 *            TetrahedronElt3D[]
	 * @throws DB3DException
	 *             - during building net topology and registering neighbours, a
	 *             DB3DException is thrown if the neighbour index is not 0, 1, 2
	 *             or 3.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	protected TetrahedronNet3DComp(ScalarOperator sop,
			TetrahedronElt3D[] elements) throws DB3DException {
		this.id = -1;
		this.sop = sop;
		this.sam = new RStar(MAX_SAM, sop);
		loadSAM(elements);
		// Here an IllegalArgumentException can be thrown.

		this.mbb = this.sam.getMBB();
		// Here an IllegalArgumentException can be thrown.
		this.buildNetTopology(elements);
		this.connected = true;
		updateEntryElement();
		updateEulerStatistics();
	}

	/**
	 * Adds the given element to the component. Returns added element or
	 * <code>null</code> if it couldn't get added.
	 * 
	 * @param elt
	 *            TetrahedronElt3D
	 * @return TetrahedronElt3D - the inserted instance, <code>null</code> if
	 *         not inserted.
	 * @throws DB3DException
	 *             - during setting the neighbourhood relations, a DB3DException
	 *             is thrown if the neighbour index is not 0, 1, 2 or 3.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of a wireframe.
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Tetrahedron3D and a
	 *             Plane3D is not a 3D simplex. The exception originates in the
	 *             method intersection(Plane3D, ScalarOperator) of the class
	 *             Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of the class Tetrahedron3D.
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
	public TetrahedronElt3D addElt(Tetrahedron3D elt) throws DB3DException {

		TetrahedronElt3D element = new TetrahedronElt3D(elt);

		// check first if component is empty
		if (this.isEmpty()) {
			this.setEntryElement(element);
			this.setMBB(element.getMBB());
			this.getSAM().insert(element);
			// Here an IllegalArgumentException can be thrown.
			return element;
		}

		// check if element not already contained
		if (this.containsElt(elt)) {
			return null;
		}

		// check if tetra fits in - get set of spatially close elements and
		// iterate over them
		Set tetras = this.getSAM().intersects(elt.getMBB());
		Iterator it = tetras.iterator();

		SpatialObject3D.HoldNeighbourStructure[] hns = new SpatialObject3D.HoldNeighbourStructure[4];
		int neighbourCounter = 0;

		// search for common corners, edges or faces an keep topological
		// information - check every element for illegal composition
		// simultaneously
		while (it.hasNext()) {
			TetrahedronElt3D tetraElt = (TetrahedronElt3D) it.next();
			SimpleGeoObj sgo = element.intersection(tetraElt, this
					.getScalarOperator());
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			if (sgo != null) {

				switch (sgo.getType()) {

				case SimpleGeoObj.WIREFRAME3D:
					return null;

				case SimpleGeoObj.TETRAHEDRON3D:
					return null;

				case SimpleGeoObj.POINT3D:
					Point3D p = (Point3D) sgo;
					if (!(element.hasCorner(p, this.getScalarOperator()) || tetraElt
							.hasCorner(p, this.getScalarOperator()))) {
						return null;
					}
					break;

				case SimpleGeoObj.SEGMENT3D:
					Segment3D seg = (Segment3D) sgo;
					if (!(element.hasEdge(seg, this.getScalarOperator()) || tetraElt
							.hasEdge(seg, this.getScalarOperator()))) {
						return null;
					}
					break;

				case SimpleGeoObj.TRIANGLE3D:
					Triangle3D tri = (Triangle3D) sgo;
					int index0 = element.getTriangleIndex(tri, this
							.getScalarOperator());
					if (index0 != -1) {
						int index1 = tetraElt.getTriangleIndex(tri, this
								.getScalarOperator());
						if (index1 != -1) {
							hns[neighbourCounter] = new SpatialObject3D.HoldNeighbourStructure(
									element, index0, tetraElt, index1);
							neighbourCounter++;
						} else
							return null; // tri is not a face of tetraElt
					} else
						return null; // tri is not a face of this
					break;
					
				}
			}
		}

		/*
		 * If no illegal composition/intersection exists with any element, the
		 * new element can be added.
		 */
		if (neighbourCounter > 0) {
			for (int i = 0; i < neighbourCounter; i++) {
				// set neighbour relations
				((TetrahedronElt3D) hns[i].getObject(0)).setNeighbour(hns[i]
						.getIndex(0), ((TetrahedronElt3D) hns[i].getObject(1)));
				((TetrahedronElt3D) hns[i].getObject(1)).setNeighbour(hns[i]
						.getIndex(1), ((TetrahedronElt3D) hns[i].getObject(0)));
			}
			// add element to SAM
			this.getSAM().insert(element);
			return element;
		} else
			return null;
	}

	/**
	 * Removes the given element from the component.<br>
	 * Assumes that an element of geometrical equality exists in this.
	 * 
	 * @param elt
	 *            Tetrahedron3D
	 * @return TetrahedronElt3D - removed element, <code>null</code> if given
	 *         element couldn't be removed.
	 * @throws DB3DException
	 *             - during re-registering neighbours in the net, a
	 *             DB3DException is thrown if the neighbour index is not 0, 1, 2
	 *             or 3.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public TetrahedronElt3D removeElt(Tetrahedron3D elt) throws DB3DException { // Dag

		// look for geometrically equivalent element in this component
		TetrahedronElt3D removable = null;
		Set set = this.getSAM().intersects(elt.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D current = (TetrahedronElt3D) it.next();
			if (current.isGeometryEquivalent(elt, this.getScalarOperator())) {
				removable = current;
				break;
			}
		}

		/*
		 * When found an element, check if its removal would split the component
		 * by testing if its neighbours would still be connected afterwards.
		 */
		if (removable != null) {

			if (!removable.hasNeighbours()) {
				// element is single
				this.setEntryElement(null);
			} else {
				TetrahedronElt3D[] neighbour = removable.getNeighbours();
				switch (removable.countNeighbours()) {

				case 1: // only one neighbour -> smooth removal
					neighbour[0].setNeighbourNull(removable, this
							.getScalarOperator());
					break;

				case 2:
					// set new (potential) neighbourhood first
					int index0 = neighbour[0].setNeighbourNull(removable, this
							.getScalarOperator());
					int index1 = neighbour[1].setNeighbourNull(removable, this
							.getScalarOperator());
					// check if net will still be connected
					if (!isConnectedWith(neighbour[0], neighbour[1])) {
						// reverse settings and return null for
						// "illegal removal operation"
						neighbour[0].setNeighbour(index0, removable);
						neighbour[1].setNeighbour(index1, removable);
						return null;
					}
					break;

				case 3:
					// set new (potential) neighbourhood first
					index0 = neighbour[0].setNeighbourNull(removable, this
							.getScalarOperator());
					index1 = neighbour[1].setNeighbourNull(removable, this
							.getScalarOperator());
					int index2 = neighbour[2].setNeighbourNull(removable, this
							.getScalarOperator());
					// check if net will still be connected
					for (int i = 0; i < 2; i++)
						if (!isConnectedWith(neighbour[i], neighbour[(i + 1)])) {
							// reverse settings and return null for
							// "illegal removal operation"
							neighbour[0].setNeighbour(index0, removable);
							neighbour[1].setNeighbour(index1, removable);
							neighbour[2].setNeighbour(index2, removable);
							return null;
						}
					break;

				case 4:
					// set new (potential) neighbourhood first
					index0 = neighbour[0].setNeighbourNull(removable, this
							.getScalarOperator());
					index1 = neighbour[1].setNeighbourNull(removable, this
							.getScalarOperator());
					index2 = neighbour[2].setNeighbourNull(removable, this
							.getScalarOperator());
					int index3 = neighbour[3].setNeighbourNull(removable, this
							.getScalarOperator());
					// check if net will still be connected
					for (int i = 0; i < 3; i++)
						if (!isConnectedWith(neighbour[i], neighbour[(i + 1)])) {
							// reverse settings and return null for
							// "illegal removal operation"
							neighbour[0].setNeighbour(index0, removable);
							neighbour[1].setNeighbour(index1, removable);
							neighbour[2].setNeighbour(index2, removable);
							neighbour[3].setNeighbour(index3, removable);
							return null;
						}
					break;

				default:
					throw new DB3DException(Db3dSimpleResourceBundle
							.getString("db3d.geom.defr"));
				}
				if (removable == this.getEntryElement())
					this.setEntryElement(neighbour[0]);
			}
			this.getSAM().remove(removable);
			// Here an IllegalArgumentException can be thrown
			return removable;
		}
		return null; // not removable
	}

	/**
	 * Checks whether start element is via neighbour elements somehow connected
	 * with given end element.
	 * 
	 * @param start
	 *            TetrahedronElt3D
	 * @param end
	 *            TetrahedronElt3D
	 * @return boolean - whether elements are connected or not.
	 */
	public boolean isConnectedWith(TetrahedronElt3D start, TetrahedronElt3D end) {
		return isConnectedWith(start, end, new FlagMap());
	}

	/*
	 * Checks whether given start element is via neighbour elements somehow
	 * connected with given end element. Uses recursive pass over all elements
	 * until searched end element id found (or all elements have been visited).
	 * 
	 * @param start TetrahedronElt3D
	 * 
	 * @param end TetrahedronElt3D
	 * 
	 * @param flags FlagMap
	 * 
	 * @return boolean - whether elements are connected or not.
	 */
	private boolean isConnectedWith(TetrahedronElt3D start,
			TetrahedronElt3D end, FlagMap flags) {

		flags.add(start); // set visited
		for (int i = 0; i < 4; i++) {
			TetrahedronElt3D nb = start.getNeighbour(i);
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
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param tet
	 *            Tetrahedron3D
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElt(Tetrahedron3D tet) { // Dag
		ScalarOperator sop = this.getScalarOperator();
		Set tetras = this.getSAM().intersects(tet.getMBB());

		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (tetra.isGeometryEquivalent(tet, sop))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether a element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param tri
	 *            Triangle3D
	 * @return boolean - true if contained, false otherwise
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsElt(Triangle3D tri) { // Dag
		ScalarOperator sop = this.getScalarOperator();
		Set set = this.getSAM().intersects(tri.getMBB());
		set = this.getTriangles(set);

		Iterator it = set.iterator();
		while (it.hasNext()) {
			Triangle3D triangle = (Triangle3D) it.next();
			if (triangle.isGeometryEquivalent(tri, sop))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether a element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param seg
	 *            Segment3D
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsElt(Segment3D seg) { // Dag
		ScalarOperator sop = this.getScalarOperator();
		Set set = this.getSAM().intersects(seg.getMBB());
		set = this.getSegments(set);

		Iterator it = set.iterator();
		while (it.hasNext()) {
			Segment3D segment = (Segment3D) it.next();
			if (segment.isGeometryEquivalent(seg, sop))
				return true;
		}

		return false;
	}

	/**
	 * Tests whether a element with the coordinates of given point is contained
	 * in the component.<br>
	 * Identity test based on epsilon equality on coordinates !
	 * 
	 * @param point
	 *            Point3D
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean containsElt(Point3D point) { // Dag
		ScalarOperator sop = this.getScalarOperator();
		Set set = this.getSAM().intersects(point.getMBB());
		set = this.getPoints(set);

		Iterator it = set.iterator();
		while (it.hasNext()) {
			Point3D p = (Point3D) it.next();
			if (p.isEqual(point, sop))
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
	 * Returns the number of faces in this component.
	 * 
	 * @return int - number if faces.
	 */
	public int countFaces() {
		return getFaces();
	}

	/**
	 * Returns the number of tetrahedrons in this component.
	 * 
	 * @return int - number of tetrahedrons.
	 */
	public int countTetras() {
		return getSAM().getCount();
	}

	/*
	 * Returns the number of elements of specified type in the border of this
	 * component. Possible types of elements to count are: tetrahedrons,
	 * triangles, segments or vertices.
	 * 
	 * @param typ SimpleGeoObj constants
	 * 
	 * @return int - count of elements of the specified type.
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private int countBorderElements(byte typ) { // Dag
		Set tetras = this.getElementsViaSAM();

		int counter = 0;
		HashSet triangleHS = new HashSet();
		// iterate over all elements, check if current is border element and
		// count specified (sub)elements if true
		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (typ == SimpleGeoObj.TETRAHEDRON3D) {
				for (int i = 0; i < 4; i++)
					if (!tetra.hasNeighbour(i)) {
						counter++;
						break;
					}
			} else {
				for (int i = 0; i < 4; i++)
					if (!tetra.hasNeighbour(i))
						triangleHS.add(tetra.getTriangle(i));
			}
		}

		if (typ == SimpleGeoObj.TETRAHEDRON3D)
			return counter;

		if (typ == SimpleGeoObj.TRIANGLE3D)
			return triangleHS.size();

		EquivalentableHashSet segmentHS = new EquivalentableHashSet((tetras
				.size() * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);
		EquivalentableHashSet pointHS = new EquivalentableHashSet((tetras
				.size() * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);
		it = triangleHS.iterator();
		while (it.hasNext()) {
			Triangle3D tri = (Triangle3D) it.next();
			if (typ == SimpleGeoObj.SEGMENT3D)
				for (int i = 0; i < 3; i++)
					segmentHS.add(tri.getSegment(i));
			else
				for (int i = 0; i < 3; i++)
					pointHS.add(tri.getPoint(i));

		}

		if (typ == SimpleGeoObj.SEGMENT3D)
			return segmentHS.size();

		return pointHS.size();
	}

	/**
	 * Returns the number of vertices in the border of this component
	 * 
	 * @return int - number of vertices in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int countBorderVertices() {
		return countBorderElements(SimpleGeoObj.POINT3D);
	}

	/**
	 * Returns the number of edges in the border of this component
	 * 
	 * @return int - number of edges in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int countBorderEdges() {
		return countBorderElements(SimpleGeoObj.SEGMENT3D);
	}

	/**
	 * Returns the number of faces in the border of this component
	 * 
	 * @return int - number of faces in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int countBorderFaces() {
		return countBorderElements(SimpleGeoObj.TRIANGLE3D);
	}

	/**
	 * Returns the number of tetrahedrons in the border of this component
	 * 
	 * @return int - number of tetrahedrons in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int countBorderTetras() {
		return countBorderElements(SimpleGeoObj.TETRAHEDRON3D);
	}

	/*
	 * Returns a set of elements of specified type in the border of this
	 * component. Possible types of border elements to look for are:
	 * tetrahedrons, triangles, segments or vertices.
	 * 
	 * @param typ SimpleGeoObj constants
	 * 
	 * @return int - count of elements of the specified type.
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private Set getBorderElements(byte typ) { // Dag
		Set tetras = this.getElementsViaSAM();
		HashSet tetraHS = new HashSet();
		HashSet triangleHS = new HashSet();

		// iterate over all elements, check if current is border element and get
		// specified (sub)elements if true
		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (typ == SimpleGeoObj.TETRAHEDRON3D) {
				for (int i = 0; i < 4; i++)
					if (tetra.isBorderElement())
						tetraHS.add(tetra);
			} else {
				for (int i = 0; i < 4; i++)
					if (!tetra.hasNeighbour(i))
						triangleHS.add(tetra.getTriangle(i));
			}
		}

		if (typ == SimpleGeoObj.TETRAHEDRON3D)
			return tetraHS;

		if (typ == SimpleGeoObj.TRIANGLE3D)
			return triangleHS;

		if (typ == SimpleGeoObj.SEGMENT3D) {
			EquivalentableHashSet segmentHS = new EquivalentableHashSet((tetras
					.size() * 2), this.getScalarOperator(),
					Equivalentable.GEOMETRY_EQUIVALENT);
			it = triangleHS.iterator();
			while (it.hasNext()) {
				Triangle3D tri = (Triangle3D) it.next();
				for (int i = 0; i < 3; i++) {
					segmentHS.add(tri.getSegment(i));
				}
			}
			return segmentHS;
		}

		EquivalentableHashSet pointHS = new EquivalentableHashSet((tetras
				.size() * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);
		it = triangleHS.iterator();
		while (it.hasNext()) {
			Triangle3D tri = (Triangle3D) it.next();
			for (int i = 0; i < 3; i++) {
				pointHS.add(tri.getPoint(i));
			}
		}
		return pointHS;
	}

	/**
	 * Returns a set of border vertices of this component.
	 * 
	 * @return Set of border vertices.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set getBorderVertices() {
		return getBorderElements(SimpleGeoObj.POINT3D);
	}

	/**
	 * Returns a set of border edges of this component.
	 * 
	 * @return Set of border edges.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set getBorderEdges() {
		return getBorderElements(SimpleGeoObj.SEGMENT3D);
	}

	/**
	 * Returns a set of border faces of this component.
	 * 
	 * @return Set of border faces.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set getBorderFaces() {
		return getBorderElements(SimpleGeoObj.TRIANGLE3D);
	}

	/**
	 * Returns a set of border elements (tetrahedrons) of this component.
	 * 
	 * @return Set of border elements of this component.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * 
	 */
	public Set getBorderTetras() {
		return getBorderElements(SimpleGeoObj.TETRAHEDRON3D);
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
	 * Returns the boundary area of this component.
	 * 
	 * @return double - area.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public double getArea() { // Dag

		Set set = this.getTriangles(this.getElementsViaRecursion());
		double area = 0;

		Iterator it = set.iterator();
		while (it.hasNext()) {
			Triangle3D triangle = (Triangle3D) it.next();
			if (this.isBorderFace(triangle))
				area += triangle.getArea();
		}
		return area;
	}

	/**
	 * Returns the volume of this component.
	 * 
	 * @return double - volume.
	 */
	public double getVolume() { // Dag

		Set tetras = this.getElementsViaRecursion();
		Iterator it = tetras.iterator();
		double volume = 0;

		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			volume += tetra.getVolume();
		}
		return volume;
	}

	/**
	 * Computes and returns the Euler number for this component.
	 * 
	 * @return int - Euler number
	 */
	public int getEuler() { // Dag
		// Euler formula: vertices - edges + faces
		int verticeCount = this.getVertices();
		int edgeCount = this.getEdges();
		int faceCount = this.getFaces();

		return (verticeCount - edgeCount + faceCount);
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
	 * @return TetrahedronElt3D - entry element.
	 */
	public TetrahedronElt3D getEntryElement() {
		return this.entry;
	}

	/**
	 * Returns the TetrahedronElt3D objects in a Set.<br>
	 * This method uses the internal SAM of the component to retrieve all
	 * elements. Use this method in the case you also need spatial tests
	 * afterwards.
	 * 
	 * @return Set with TetrahedronElt3D objects.
	 */
	public Set getElementsViaSAM() {
		return this.sam.getEntries();
	}

	/**
	 * Returns the TetrahedronElt3D objects in a Set. This method uses a walk
	 * over the neighbours (NOT THE internal SAM) to retrieve all elements. Use
	 * this method only in case you need to process all the elements.
	 * 
	 * @return Set with TetrahedronElt3D objects.
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
	 * After break be sure to release the internal recsurces by calling the
	 * terminate method.
	 * 
	 * @return TetrahedronElt3DIterator - iterator over elements of this.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public TetrahedronElt3DIterator getElementsIterator() {
		return new TetrahedronElt3DIterator(getEntryElement());
	}

	/**
	 * Returns all Point3D objects of this in a Set.
	 * 
	 * @return Set of all Point3D objects of this.
	 */
	public Set getPoints() { // Dag
		return this.getPoints(this.getElementsViaRecursion());
	}

	/**
	 * Returns all Segment3D objects of this in a Set.
	 * 
	 * @return Set of all Segment3D objects of this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set getSegments() { // Dag
		return this.getSegments(this.getElementsViaRecursion());
	}

	/**
	 * Returns all Segment3D objects of this in a Set.
	 * 
	 * @return Set of all Segment3D objects of this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set getTriangles() { // Dag
		return this.getTriangles(this.getElementsViaRecursion());
	}

	/*
	 * Returns a set of all Triangle3D objects for a given Set of
	 * TetrahedronElt3D objects.
	 * 
	 * @param tetraSet Set of TetrahedronElt3D objects
	 * 
	 * @return Set of Triangle3D objects.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private Set getTriangles(Set tetraSet) { // Dag
		TetrahedronElt3D[] tetras = (TetrahedronElt3D[]) tetraSet
				.toArray(new TetrahedronElt3D[tetraSet.size()]);
		EquivalentableHashSet triangleHS = new EquivalentableHashSet(
				(tetras.length * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);

		for (int i = 0; i < tetras.length; i++) {
			Triangle3D[] triangles = tetras[i].getTriangles();
			for (int j = 0; j < 4; j++)
				triangleHS.add(triangles[j]);
		}
		return triangleHS;
	}

	/*
	 * Returns a set of all Sement3D objects for a given Set of TetrahedronElt3D
	 * objects.
	 * 
	 * @param tetraSet Set of TetrahedronElt3D objects
	 * 
	 * @return Set of Segment3D objects.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private Set getSegments(Set tetraSet) { // Dag

		Set triangleHS = getTriangles(tetraSet);
		EquivalentableHashSet segmentHS = new EquivalentableHashSet((triangleHS
				.size() * 2), this.getScalarOperator(),
				Equivalentable.GEOMETRY_EQUIVALENT);
		Iterator it = triangleHS.iterator();
		while (it.hasNext()) {
			Triangle3D triangle = (Triangle3D) it.next();
			for (int i = 0; i < 3; i++)
				segmentHS.add(triangle.getSegment(i));
		}
		return segmentHS;
	}

	/*
	 * Returns a set of all Point3D objects for a given Set of TetrahedronElt3D
	 * objects.
	 * 
	 * @param tetras Set of TetrahedronElt3D objects
	 * 
	 * @return Set of Point3D objects.
	 * 
	 * @throws IllegalArgumentException if the index of the point of the
	 * tetrahedron is not in the interval [0;3]. The exception originates in the
	 * method getPoint(int) of the class Tetrahedron3D.
	 */
	private Set getPoints(Set tetras) { // Dag

		Set pointHS = new IdentityHashSet();
		Iterator it = tetras.iterator();
		TetrahedronElt3D tetra;
		while (it.hasNext()) {
			tetra = (TetrahedronElt3D) it.next();
			for (int j = 0; j < 4; j++)
				pointHS.add(tetra.getPoint(j));
		}
		return pointHS;
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
	 * Tests whether the given point is a border vertex.<br>
	 * The point has geometrically to be a vertex of this<br>
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

		MBB3D mbb = point.getMBB();
		Set tetras = this.getSAM().intersects(mbb);
		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = (TetrahedronElt3D) it.next();
			for (int i = 0; i < 4; i++) { // find face containing point and
				// check if no neighbour tetrahedron
				// exists for its index -> true
				Triangle3D tri = tet.getTriangle(i);
				for (int j = 0; j < 3; j++)
					if (point
							.isEqual(tri.getPoint(j), this.getScalarOperator()))
						if (tet.getNeighbour(i) == null)
							// triangle is BorderFace -> point must be
							// BorderVertex
							return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether the given segment is a border edge.<br>
	 * The segment has geometrically to be an edge of at least one tetrahedron
	 * of this<br>
	 * (otherwise <code>false</code> will be returned).
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is a border edge, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isBorderEdge(Segment3D seg) { // Dag

		MBB3D mbb = seg.getMBB();
		Set tetras = this.getSAM().intersects(mbb);
		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = (TetrahedronElt3D) it.next();
			for (int i = 0; i < 4; i++) { // find triangle containing seg and
				// check if no neighbour in this
				// direction exists -> true
				Triangle3D tri = tet.getTriangle(i);
				for (int j = 0; j < 3; j++)
					if (seg.isGeometryEquivalent(tri.getSegment(j), this
							.getScalarOperator()))
						if (tet.getNeighbour(i) == null)
							// triangle is BorderFace -> seg must be BorderEdge
							return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether the given triangle is a border face.<br>
	 * The triangle has geometrically to be a face of at lease one tetra of this<br>
	 * (otherwise <code>false</code> will be returned).
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if is a border face, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isBorderFace(Triangle3D triangle) { // Dag

		MBB3D mbb = triangle.getMBB();
		Set tetras = this.getSAM().intersects(mbb);
		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = (TetrahedronElt3D) it.next();
			for (int i = 0; i < 4; i++) { // find index i for triangle in tet
				// and return true if no
				// neighbour(i) exists
				Triangle3D tri = tet.getTriangle(i);
				if (tri
						.isGeometryEquivalent(triangle, this
								.getScalarOperator()))
					if (tet.getNeighbour(i) == null)
						return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether the given tetrahedron is a border tetrahedron.<br>
	 * The tetrahedron must be geometrically equivalent to an element of this<br>
	 * (otherwise <code>false</code> will be returned).
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if is a border tetrahedron, false otherwise.
	 */
	public boolean isBorderElement(Tetrahedron3D tetra) { // Dag

		MBB3D mbb = tetra.getMBB();
		Set tetras = this.getSAM().intersects(mbb);
		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = (TetrahedronElt3D) it.next();
			if (tet.isGeometryEquivalent(tetra, this.getScalarOperator()))
				for (int i = 0; i < 4; i++)
					if (tet.getNeighbour(i) == null)
						return true;
		}
		return false;
	}

	/**
	 * Tests if the component is correctly connected.<br>
	 * 
	 * @return boolean - true if correctly connected, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
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
	public boolean intersects(Plane3D plane) {

		SimpleGeoObj sgo = this.getMBB().intersection(plane,
				this.getScalarOperator());
		if (sgo == null)
			return false;
		Set set = this.getSAM().intersects(sgo.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (tetra.intersects(plane, this.getScalarOperator()))
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
	public boolean intersects(Line3D line) {

		SimpleGeoObj sgo = this.getMBB().intersection(line,
				this.getScalarOperator());
		if (sgo == null)
			return false;
		Set set = this.getSAM().intersects(sgo.getMBB());
		Iterator it = set.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (tetra.intersects(line, this.getScalarOperator()))
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
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             MBB3D is not a simplex. The exception originates in the
	 *             method intersection(MBB3D, ScalarOperator) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(MBB3D mbb) { // Dag

		if (!this.getMBB().intersects(mbb, this.getScalarOperator()))
			return false;

		Set set = this.getSAM().intersects(mbb);
		Iterator it = set.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (tetra.intersects(mbb, this.getScalarOperator()))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given point geometrically.
	 * 
	 * @param point
	 *            Point3D tp be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean containsInside(Point3D point) { // Dag
		if (getTetraContainingPoint(point) != null)
			return true;
		return false;
	}

	/*
	 * Tests whether this contains the given point geometrically. Returns the
	 * Tetrahedron which contains the point or <code>null</code> if not.
	 * 
	 * @param point Point3D to be tested
	 * 
	 * @return TetrahedronElt3D if contains the point or <code>null</code>
	 * otherwise.
	 * 
	 * @throws IllegalArgumentException if an attempt is made to construct a
	 * MBB3D whose maximum point is not greater than its minimum point.
	 * 
	 * @throws IllegalArgumentException if the index of the point of the
	 * tetrahedron is not in the interval [0;3]. The exception originates in the
	 * method getPoint(int) of the class Tetrahedron3D.
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private TetrahedronElt3D getTetraContainingPoint(Point3D point) { // Dag

		Set tetras = this.getSAM().contains(point);

		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tetra = (TetrahedronElt3D) it.next();
			if (tetra.contains(point, sop))
				return tetra;
		}
		return null;
	}

	/**
	 * Tests whether this contains the given segment geometrically.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, ScalarOperator sop) method
	 *             of the class Line3D (which computes the intersection of two
	 *             lines) called by this method returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
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
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsInside(Segment3D seg) {

		if (!(seg.getMBB().inside(this.getMBB(), this.getScalarOperator())))
			return false;

		if (!(this.containsInside(seg.getPoint(0)) && this.containsInside(seg
				.getPoint(1))))
			return false;

		TetrahedronElt3D current = getTetraContainingPoint(seg.getPoint(0));
		Point3D second = seg.getPoint(1);
		FlagMap flag = new FlagMap();

		while (!current.contains(second, this.getScalarOperator())) {
			flag.add(current);
			for (int i = 0; i < 4; i++) {
				if (current.hasNeighbour(i)
						&& (!flag.check(current.getNeighbour(i)))
						&& current.getNeighbour(i).intersects(seg,
								this.getScalarOperator())) {
					current = current.getNeighbour(i);
					break;
				} else if (i == 3)
					return false;
			}
		}

		return true;
	}

	/**
	 * Tests whether this contains the given triangle geometrically.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, ScalarOperator sop) method
	 *             of the class Line3D (which computes the intersection of two
	 *             lines) called by this method returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Triangle3D is not a simplex. The exception originates in the
	 *             method intersects(Triangle3D, ScalarOperator) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsInside(Triangle3D triangle) {

		if (!(triangle.getMBB().inside(this.getMBB(), this.getScalarOperator())))
			return false;
		if (!(this.containsInside(triangle.getPoint(0))
				&& this.containsInside(triangle.getPoint(1)) && this
				.containsInside(triangle.getPoint(2))))
			return false;

		// check if the border faces of all triangle intersecting tetrahedrons
		// intersects with triangle. return false in this case, true if not
		Set tetras = this.getSAM().intersects(triangle.getMBB());
		HashSet hull = new HashSet();

		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = (TetrahedronElt3D) it.next();
			if (tet.intersects(triangle, this.getScalarOperator()))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				for (int i = 0; i < 4; i++) {
					if (!tet.hasNeighbour(i))
						hull.add(tet.getTriangle(i));
				}
		}

		it = hull.iterator();
		while (it.hasNext()) {
			Triangle3D tri = (Triangle3D) it.next();
			if (tri.intersects(triangle, this.getScalarOperator()))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				return false;
		}

		return true;
	}

	/**
	 * Tests whether this contains the given tetrahedron geometrically.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, ScalarOperator sop) method
	 *             of the class Line3D (which computes the intersection of two
	 *             lines) called by this method returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             MBB3D is not a simplex. The exception originates in the
	 *             method intersection(MBB3D, ScalarOperator) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsInside(Tetrahedron3D tetra) {

		if (!(tetra.getMBB().inside(this.getMBB(), this.getScalarOperator())))
			return false;
		if (!(this.containsInside(tetra.getPoint(0))
				&& this.containsInside(tetra.getPoint(1))
				&& this.containsInside(tetra.getPoint(2)) && this
				.containsInside(tetra.getPoint(3))))
			return false;

		// check if the border faces of all tetra intersecting tetrahedrons
		// intersects with triangle. return false in this case, true if not
		Set tetras = this.getSAM().intersects(tetra.getMBB());
		HashSet hull = new HashSet();

		Iterator it = tetras.iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = (TetrahedronElt3D) it.next();
			if (tet.intersects(tetra, this.getScalarOperator()))
				for (int i = 0; i < 4; i++) {
					if (!tet.hasNeighbour(i))
						hull.add(tet.getTriangle(i));
				}
		}

		it = hull.iterator();
		while (it.hasNext()) {
			Triangle3D tri = (Triangle3D) it.next();
			if (tetra.intersects(tri, this.getScalarOperator()))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				return false;
		}

		return true;
	}

	/**
	 * Return the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.COMP_TETRAHEDRON_NET_3D;
	}

	/**
	 * Returns the enclosing net
	 * 
	 * @return TetrahedronNet3D - enclosing net.
	 */
	public TetrahedronNet3D getNet() {
		return net;
	}

	/**
	 * Performs a deep copy of this component with all its recursive members.<br>
	 * Only the reference to the enclosing net is not copied and must be set
	 * afterwards.
	 * 
	 * @return TetrahedronNet3DComp - deep copy.
	 */
	public TetrahedronNet3DComp serializationCopy() {
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
			return (TetrahedronNet3DComp) ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the reference to enclosing TetrahedronNet3D.
	 * 
	 * @param net3D
	 *            enclosing TetrahedronNet3D
	 */
	public void setNet(TetrahedronNet3D net3D) {
		this.net = net3D;
	}

	/*
	 * Load the sam at construction time and counts the vertices.
	 * 
	 * @param elements TetrahedronElt3D whose elements should be inserted into
	 * SAM
	 * 
	 * @throws IllegalArgumentException if an attempt is made to construct a
	 * MBB3D whose maximum point is not greater than its minimum point.
	 */
	private void loadSAM(TetrahedronElt3D[] elements) {
		for (int i = 0; i < elements.length; i++)
			this.sam.insert(elements[i]);
	}

	/**
	 * Builds the neighbour topology of the net for the given Tetrahedron
	 * elements.
	 * 
	 * @param elts
	 *            TetrahedronElt3D[]
	 * @throws DB3DException
	 *             - during registering neighbours, a DB3DException is thrown if
	 *             the neighbour index is not 0, 1, 2 or 3.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public void buildNetTopology(TetrahedronElt3D[] elts) throws DB3DException {
		ScalarOperator so = getScalarOperator();

		for (int i = 0; i < elts.length; i++) {
			if (elts[i].isInterior() != true) {
				SAM sa = this.getSAM();
				Set query = sa.intersects(elts[i].getMBB());
				query.remove(elts[i]);

				Point3D po = null;
				Iterator it;
				TetrahedronElt3D te = null;
				// wenn schon Nachbarn vorhanden - diese aus Query-Set
				te = elts[i].getNeighbour(0);
				// entfernen damit diese nicht getestet werden
				if (te != null)
					query.remove(te);
				te = elts[i].getNeighbour(1);
				if (te != null)
					query.remove(te);
				te = elts[i].getNeighbour(2);
				if (te != null)
					query.remove(te);
				te = elts[i].getNeighbour(3);
				if (te != null)
					query.remove(te);

				for (int m = 0; m < 4; m++) {
					int p1 = 0;
					int p2 = 0;
					int p3 = 0;
					switch (m) { // checken an welchen punkten der nachbar
					// liegen muss
					case 0:
						p1 = 1;
						p2 = 2;
						p3 = 3;
						break;
					case 1:
						p1 = 0;
						p2 = 2;
						p3 = 3;
						break;
					case 2:
						p1 = 0;
						p2 = 1;
						p3 = 3;
						break;
					case 3:
						p1 = 1;
						p2 = 2;
						p3 = 0;
					}

					if ((elts[i].getNeighbour(m)) == null) {
						po = elts[i].getPoint(p1);
						it = query.iterator();

						while (it.hasNext()) {
							TetrahedronElt3D oTE = (TetrahedronElt3D) it.next();

							for (int j = 0; j < 4; j++) {
								Point3D ot = oTE.getPoint(j);
								if (po.isEqual(ot, so)) {
									// ein punkt ist gleich
									Point3D po1 = elts[i].getPoint(p2);

									for (int k = 0; k < 4; k++) {
										if (k != j) {
											Point3D ot1 = oTE.getPoint(k);
											if (po1.isEqual(ot1, so)) {
												// 2.Punkt => sie haben
												// gemeinsame Kante
												Point3D po2 = elts[i]
														.getPoint(p3);

												for (int n = 0; n < 4; n++) {
													if (n != k && n != j) {
														Point3D ot2 = oTE
																.getPoint(n);

														if (po2
																.isEqual(ot2,
																		so)) {
															// nachbarn
															elts[i]
																	.setNeighbour(
																			m,
																			oTE);
															// nachbar
															// regestriert bei
															// this
															int otind; // index
															// fuer
															// other
															// triangleelt3d

															// alle !=3 und
															// j+k+n ==3 und j
															// != k != n
															if (j != 3
																	&& k != 3
																	&& n != 3) // (
																// (j==0
																// &&
																// k==1
																// &&
																// n==2)
																// ||
																// (j==0
																// &&
																// k==2
																// &&
																// n==1)
																// ||
																// (j==1
																// &&
																// k==0
																// &&
																// n==2)
																// ||
																// (j==1
																// &&
																// k==2
																// &&
																// n==0)
																// ||
																// (j==2
																// &&
																// k==1
																// &&
																// n==0)
																// ||
																// (j==2
																// &&
																// k==0
																// &&
																// n==1))
																otind = 3;
															else if (j != 1
																	&& k != 1
																	&& n != 1) // (
																// (j==0
																// &&
																// k==2
																// &&
																// n==3)
																// ||
																// (j==0
																// &&
																// k==3
																// &&
																// n==2)
																// ||
																// (j==2
																// &&
																// k==0
																// &&
																// n==3)
																// ||
																// (j==2
																// &&
																// k==3
																// &&
																// n==0)
																// ||
																// (j==3
																// &&
																// k==0
																// &&
																// n==2)
																// ||
																// (j==3
																// &&
																// k==2
																// &&
																// n==0))
																otind = 1;
															else if (j != 2
																	&& k != 2
																	&& n != 2) // (
																// (j==1
																// &&
																// k==2
																// &&
																// n==3)
																// ||
																// (j==1
																// &&
																// k==3
																// &&
																// n==2)
																// ||
																// (j==2
																// &&
																// k==3
																// &&
																// n==1)
																// ||
																// (j==2
																// &&
																// k==1
																// &&
																// n==3)
																// ||
																// (j==3
																// &&
																// k==1
																// &&
																// n==2)
																// ||
																// (j==3
																// &&
																// k==2
																// &&
																// n==1))
																otind = 2; // gegenueber
															// 2
															else
																otind = 0;

															oTE.setNeighbour(
																	otind,
																	elts[i]);
															it.remove();
															break;
														}
													}
												}
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
	 * Returns the internal spatial access method object (SAM) to the elements
	 * of the component.
	 * 
	 * @return SAM.
	 */
	public SAM getSAM() {
		return this.sam;
	}

	/*
	 * Returns the vertices count.
	 * 
	 * @return int - vertices count.
	 */
	private int getVertices() {
		return vertices;
	}

	/*
	 * Sets the vertices count to the given value.
	 * 
	 * @param value int the given value
	 */
	private void setVertices(int value) {
		this.vertices = value;
	}

	/*
	 * Return the edges count.
	 * 
	 * @return int - edges count.
	 */
	private int getEdges() {
		return edges;
	}

	/*
	 * Set the edges count to given value.
	 * 
	 * @param value int the given value
	 */
	private void setEdges(int value) {
		this.edges = value;
	}

	/*
	 * Returns the faces count.
	 * 
	 * @return int - faces count.
	 */
	private int getFaces() {
		return faces;
	}

	/*
	 * Sets the faces count to the given value.
	 * 
	 * @param value int the given value.
	 */
	private void setFaces(int value) {
		this.faces = value;
	}

	private void setMBB(MBB3D mbb) {
		this.mbb = mbb;
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
	 *            int id
	 */
	public void setComponentID(int id) {
		this.id = id;
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
			TetrahedronElt3D tetelt = (TetrahedronElt3D) it.next();
			if (!tetelt.isInterior()) {
				setEntryElement(tetelt);
				break;
			}
		}
	}

	/*
	 * Sets the entry element to the given TerahedronElt3D.
	 * 
	 * @param tet TetrahedronElt3D to which the entry element should be set
	 */
	private void setEntryElement(TetrahedronElt3D tet) {
		this.entry = tet;
	}

	/**
	 * Updates the vertices, edges, faces statistics after changes in the net
	 * component.
	 * 
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	protected void updateEulerStatistics() {
		SAM sam = this.getSAM();
		Set set = sam.getEntries();
		Set vert = new EquivalentableHashSet((int) (set.size() * 1.5),
				getScalarOperator(), Equivalentable.GEOMETRY_EQUIVALENT);
		Set edg = new EquivalentableHashSet((int) (set.size() * 1.5),
				getScalarOperator(), Equivalentable.GEOMETRY_EQUIVALENT);
		Set fac = new EquivalentableHashSet((int) (set.size() * 1.5),
				getScalarOperator(), Equivalentable.GEOMETRY_EQUIVALENT);

		Triangle3D tri = null;
		TetrahedronElt3D tetelt = null;
		Iterator it = set.iterator();

		while (it.hasNext()) {
			tetelt = (TetrahedronElt3D) it.next();
			for (int j = 0; j < 4; j++) {
				vert.add(tetelt.getPoint(j));
				tri = tetelt.getTriangle(j);
				fac.add(tri);
				for (int k = 0; k < 3; k++)
					edg.add(tri.getSegment(k));
			}
		}
		setVertices(vert.size());
		setEdges(edg.size());
		setFaces(fac.size());
	}

	/**
	 * Finds the outer boundary of a tin. Runs over all tetrahedrons and records
	 * their outer triangles.
	 * 
	 * @return HashSet with outer triangles.
	 * @throws IllegalStateException
	 *             - if this TIN component is empty.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set<Triangle3D> findTinBorder1() {
		if (this.isEmpty())
			throw new IllegalStateException("The TIN is empty!");

		HashSet<Triangle3D> outerTriangles = new HashSet<Triangle3D>();

		TetrahedronElt3D tetr = null;

		Set<Equivalentable> s = this.getElementsViaRecursion();

		Iterator<Equivalentable> it = s.iterator();

		while (it.hasNext()) {

			// Searching through all tetrahedrons:

			tetr = (TetrahedronElt3D) it.next();

			// If an external tetrahedron was found:
			for (int j = 0; j < 4; j++)
				if (!tetr.hasNeighbour(j))
					outerTriangles.add((Triangle3D) tetr.getTriangle(j));
		}
		return outerTriangles;
	}

	/**
	 * Finds the outer boundary of a tin. Finds the fisrt outer tetrahedron,
	 * afterwards follows the edge of the tin.
	 * 
	 * @return HashSet with outer triangles.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Set<Triangle3D> findTinBorder2() {
		TetrahedronElt3D tetr = null;
		// Getting all tetrahedrons:
		Set<Equivalentable> s = this.getElementsViaRecursion();
		Iterator<Equivalentable> it = s.iterator();
		if (it.hasNext())
			tetr = (TetrahedronElt3D) it.next();

		// Looking for the first outer tetrahedron:
		while (tetr.countNeighbours() == 4 && it.hasNext()) {
			tetr = (TetrahedronElt3D) it.next();
		}

		System.out.println(tetr.toString());
		// now tetr is the first outer tetrahedron
		Set<Triangle3D> outerTriangles = new HashSet<Triangle3D>();
		// stores outer triangles
		Set<Tetrahedron3D> visited = new HashSet<Tetrahedron3D>();
		// stores visited tetrahedrons

		visited.add(tetr);

		// Looking for outer triangles recursively:
		tinBorderViaRecursion(outerTriangles, visited, tetr);

		return outerTriangles;
	}

	/*
	 * Help method for the method findTinBorder2(). Adds triangles to the
	 * Set<Triangle3D> outerTriangles recursively.
	 * 
	 * @param outerTriangles a set that contains outer triangles
	 * 
	 * @param visited a set that contains triangles that have already been
	 * visited
	 * 
	 * @param tetr the Tetrahedron from which the method starts
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private void tinBorderViaRecursion(Set<Triangle3D> outerTriangles,
			Set<Tetrahedron3D> visited, TetrahedronElt3D tetr) {
		// The following is done for every of the 4 neighbours of tetr:
		for (int i = 0; i < 4; i++) {
			if (tetr.getNeighbour(i) == null) {
				// Adding outer triangles to the set:
				outerTriangles.add(tetr.getTriangle(i));
			} else {
				// Getting the neighbour:
				TetrahedronElt3D nb = tetr.getNeighbour(i);

				if (nb.countNeighbours() == 4) { // if nb is an inner
					// tetrahedron
					for (int j = 0; j < 4; j++) {
						Triangle3D outerTri = tetr.getTriangle(i);

						/*
						 * seg is used to remember the common segment of the
						 * last outer triangle that was found and of the next
						 * outer triangle that is being searched for.
						 */

						Segment3D seg = getCommonSegment(outerTri, nb);

						getOuterNeighbourViaRecursion(nb, seg);
						// After the segment was found, the next outer neighbour
						// must be found.
					}
				}

				if (!visited.contains(nb)) { // if nb has not been visited
					visited.add(nb);
					tinBorderViaRecursion(outerTriangles, visited, nb); // repeating
					// the same for nb
				}

			}
		}
	}

	/*
	 * Help method for the method findTinBorder2(). The current outer triangle
	 * has an outer segment seg. The goal of this method is to find the outer
	 * tetrahedron that also contains seg and thus borders to the current outer
	 * tetrahedron. However, it is possible that the two outer tetrahedrons do
	 * not have common triangles and therefore are not identified as neighbours
	 * as they have some inner tetrahedrons between them. This is why this
	 * method is necessary.
	 * 
	 * @param nb the first neighbour of the current outer tetrahedron
	 * 
	 * @param seg the common segment of the current outer tetrahedron and the
	 * next outer tetrahedron that needs to be found
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private void getOuterNeighbourViaRecursion(TetrahedronElt3D nb,
			Segment3D seg) {
		for (int i = 0; i < 4; i++) {
			TetrahedronElt3D nb2 = nb.getNeighbour(i);
			if (haveCommonSegment(seg, nb2)) {
				if (nb2.countNeighbours() < 4) {
					// if nb2 is an outer tetrahedron, stop the recursion
					nb = nb2;
				} else {
					// if nb2 is an inner tetrahedron, continue looking for the
					// outer tetrahedron:
					getOuterNeighbourViaRecursion(nb2, seg);
				}
			}
		}
	}

	/*
	 * Help method for the method findTinBorder2(). Returns a common segment of
	 * triangle tri and tetrahedron nb. Returns null if they have no common
	 * segments. Other variants (more complex geometrical shapes) are not
	 * possible because tri and nb originate from the same net component.
	 * 
	 * @param tri triangle
	 * 
	 * @param nb tetrahedron
	 * 
	 * @return Segment3D if tri and nb have a common segment, null otherwise.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private Segment3D getCommonSegment(Triangle3D tri, TetrahedronElt3D nb) {
		Segment3D[] outerSeg = tri.getSegments();
		Triangle3D[] nbTri = nb.getTriangles();
		Segment3D[] nbSeg = new Segment3D[9];
		// It is enough to regard just 3 triangles of the tetrahedron,
		// because the segments repeat.
		nbSeg[0] = nbTri[0].getSegment(0);
		nbSeg[1] = nbTri[0].getSegment(1);
		nbSeg[2] = nbTri[0].getSegment(2);
		nbSeg[3] = nbTri[1].getSegment(0);
		nbSeg[4] = nbTri[1].getSegment(1);
		nbSeg[5] = nbTri[1].getSegment(1);
		nbSeg[6] = nbTri[2].getSegment(0);
		nbSeg[7] = nbTri[2].getSegment(1);
		nbSeg[8] = nbTri[2].getSegment(2);
		for (int a = 0; a < 3; a++) {
			for (int b = 0; b < 9; b++) {
				if (nbSeg[b] == outerSeg[a]) {
					return outerSeg[a];
				}
			}
		}
		return null;
	}

	/*
	 * Help method for the method findTinBorder2(). Checks if the tetrahedron nb
	 * contains the segment seg.
	 * 
	 * @param seg segment
	 * 
	 * @param nb tetrahedron
	 * 
	 * @return true if nb contains the segment, false otherwise.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private boolean haveCommonSegment(Segment3D seg, TetrahedronElt3D nb) {
		Triangle3D[] nbTri = nb.getTriangles();
		Segment3D[] nbSeg = new Segment3D[9];
		// It is enough to regard just 3 triangles of the tetrahedron,
		// because the segments repeat.
		nbSeg[0] = nbTri[0].getSegment(0);
		nbSeg[1] = nbTri[0].getSegment(1);
		nbSeg[2] = nbTri[0].getSegment(2);
		nbSeg[3] = nbTri[1].getSegment(0);
		nbSeg[4] = nbTri[1].getSegment(1);
		nbSeg[5] = nbTri[1].getSegment(1);
		nbSeg[6] = nbTri[2].getSegment(0);
		nbSeg[7] = nbTri[2].getSegment(1);
		nbSeg[8] = nbTri[2].getSegment(2);
		for (int b = 0; b < 9; b++) {
			if (nbSeg[b] == seg) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Recursive method to collect all elements starting at the given element.
	 * 
	 * @param set Set to collect elements
	 * 
	 * @param elt TetrahedronElt3D to start recursion
	 */
	private void makeSet(Set set, TetrahedronElt3D elt) {

		// make visited
		set.add(elt);

		for (int i = 0; i < 4; i++) {
			TetrahedronElt3D nb = elt.getNeighbour(i);
			if (nb != null && !set.contains(nb)) // if not already visited
				makeSet(set, nb);
		}
	}

	/**
	 * TetrahedronElt3DIterator - iterator over the elements of the components.<br>
	 * For releasing the resources occupied by this iterator call the terminate
	 * method ASAP.
	 * 
	 * @author Wolfgang Baer
	 */
	public static class TetrahedronElt3DIterator {

		/* the next element */
		private TetrahedronElt3D next;

		/*
		 * the actual element we stop the recursive method to server the
		 * iteration
		 */
		private Actual actual;

		/* the stack for tracking the recursion */
		private Stack stack;

		/* a set of visited elements */
		private Set visited;

		/* signals if its a start call to hasNext() */
		private boolean start;

		/*
		 * Constructor
		 * 
		 * @param element TetrahedronElt3D actual element
		 */
		private TetrahedronElt3DIterator(TetrahedronElt3D element) {
			this.visited = new IdentityHashSet();
			this.stack = new Stack();
			actual = new Actual(element);
			start = true;
			next = null;
		}

		/**
		 * Tests whether there is a next element available.
		 * 
		 * @return boolean - true if there is a next element.
		 */
		public boolean hasNext() {
			TetrahedronElt3D tri = recursiveFunction();
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
		 * Returns the next TetrahedronElt3D if hasNext() returned true.
		 * 
		 * @return TetrahedronElt3D.
		 */
		public TetrahedronElt3D next() {
			return next;
		}

		/*
		 * Private workhorse method.
		 * 
		 * @return TetrahedronElt3D - if != null we have a next element.
		 */
		private TetrahedronElt3D recursiveFunction() {

			while (!stack.isEmpty() || start) {

				if (actual.i < 3) {
					do {
						actual.incI();
						TetrahedronElt3D tri = actual.element
								.getNeighbour(actual.i);

						if (tri != null && !visited.contains(tri)) {
							visited.add(tri);
							stack.push(actual);
							actual = new Actual(tri);
							return tri;
						} else {
							// do nothing
						}
					} while (actual.i < 3);
				} else
					actual = (Actual) stack.pop();
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

		/*
		 * Class to represent the actual element that serves to stop the
		 * recursion.
		 */
		private static class Actual {

			/* TetrahedronElt3D contained in Actual */
			private TetrahedronElt3D element;

			/* index */
			private byte i;

			/*
			 * Constuctor.<br> Constructs an Actual element from a
			 * TetrahedronElt3D.
			 * 
			 * @param element TetrahedronElt3D
			 */
			private Actual(TetrahedronElt3D element) {
				this.element = element;
				this.i = -1;
			}

			/*
			 * Increments the indes.
			 */
			private void incI() {
				i++;
			}
		}

	}

	/**
	 * Searches for an element with the given id and returns it. If it was not
	 * found, returns <code>null</code>.
	 * 
	 * @return SimpleGeoObj - element with the given id.
	 */
	public SimpleGeoObj getElement(int id) {

		Iterator<TetrahedronElt3D> it = getElementsViaRecursion().iterator();
		while (it.hasNext()) {
			TetrahedronElt3D tet = it.next();
			if (tet.getID() == id)
				return tet;
		}
		return null;
	}
	
	/**
	 * @return the id
	 */
	public int getID() {
		return id;
	}
}
