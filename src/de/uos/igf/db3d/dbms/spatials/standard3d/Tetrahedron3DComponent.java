package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.collections.EquivalentableHashSet;
import de.uos.igf.db3d.dbms.collections.FlagMap;
import de.uos.igf.db3d.dbms.collections.IdentityHashSet;
import de.uos.igf.db3d.dbms.exceptions.DB3DException;
import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.api.Net3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Tetrahedron3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * 
 * TetrahedronNet3DComp represents a single tetrahedron net component. All
 * TetrahedronElt3D objects in this object belong to one semantic component.<br>
 * For tetrahedron nets with several components see @see TetrahedronNet3D.
 * 
 * @author Markus Jahn
 * 
 */
public class Tetrahedron3DComponent extends Component3DAbst {

	/* entry element */
	private Tetrahedron3DElement entry;

	/* connected flag */
	private boolean connected;

	/**
	 * Constructor.<br>
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 */
	public Tetrahedron3DComponent(GeoEpsilon epsilon) {
		super(epsilon);
		this.connected = false;
	}

	/**
	 * Constructor.<br>
	 * Constructs a TetrahedronNet3DComp object with the given
	 * TetrahedronElt3D[].<br>
	 * In the given array the neighbourhood topology has not been defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this
	 * tetrahedron array.
	 * 
	 * @param epsilon
	 *            geometric error
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3DComponent(Tetrahedron3DElement[] elements,
			GeoEpsilon epsilon) throws UpdateException {
		super(epsilon);
		for (Tetrahedron3DElement element : elements) {
			this.addElement(element);
		}
		this.buildNetTopology(elements);
		this.connected = true;
		updateEntryElement();
	}

	/**
	 * Adds the given element to the component. Returns added element or
	 * <code>null</code> if it couldn't get added.
	 * 
	 * @param element
	 *            TetrahedronElt3D
	 * @return TetrahedronElt3D - the inserted instance, <code>null</code> if
	 *         not inserted.
	 * @throws DB3DException
	 *             - during setting the neighbourhood relations, a DB3DException
	 *             is thrown if the neighbour index is not 0, 1, 2 or 3.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             Point3D, Point3D, Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Tetrahedron3D and a
	 *             Plane3D is not a 3D simplex. The exception originates in the
	 *             method intersection(Plane3D, GeoEpsilon) of the class
	 *             Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of the class Tetrahedron3D.
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
	public Tetrahedron3DElement addElement(Tetrahedron3DElement element)
			throws UpdateException {

		// check first if component is empty
		if (this.isEmpty()) {
			this.entry = element;
			this.mbb = element.getMBB();
			this.sam.insert(element);
			// Here an IllegalArgumentException can be thrown.
			return element;
		}

		// check if element not already contained
		if (this.containsElement(element)) {
			return null;
		}

		// check if tetra fits in - get set of spatially close elements and
		// iterate over them
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(element.getMBB());
		Iterator<Tetrahedron3DElement> it = tetras.iterator();

		Net3DAbst.HoldNeighbourStructure[] hns = new Net3DAbst.HoldNeighbourStructure[4];
		int neighbourCounter = 0;

		// search for common corners, edges or faces an keep topological
		// information - check every element for illegal composition
		// simultaneously
		while (it.hasNext()) {
			Tetrahedron3DElement tetraElt = it.next();
			Geometry3D sgo = element.intersection(tetraElt, this.epsilon);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			if (sgo != null) {

				switch (sgo.getGeometryType()) {

				case WIREFRAME:
					return null;

				case TETRAHEDRON:
					return null;

				case POINT:
					Point3D p = (Point3D) sgo;
					if (!(element.hasCorner(p, this.epsilon) || tetraElt
							.hasCorner(p, this.epsilon))) {
						return null;
					}
					break;

				case SEGMENT:
					Segment3D seg = (Segment3D) sgo;
					if (!(element.hasEdge(seg, this.epsilon) || tetraElt
							.hasEdge(seg, this.epsilon))) {
						return null;
					}
					break;

				case TRIANGLE:
					Triangle3D tri = (Triangle3D) sgo;
					int index0 = element.getTriangleIndex(tri, this.epsilon);
					if (index0 != -1) {
						int index1 = tetraElt.getTriangleIndex(tri,
								this.epsilon);
						if (index1 != -1) {
							hns[neighbourCounter] = new Net3DAbst.HoldNeighbourStructure(
									element, index0, tetraElt, index1);
							neighbourCounter++;
						} else
							return null; // tri is not a face of tetraElt
					} else
						return null; // tri is not a face of this
					break;
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
				case TETRAHEDRONSET:
					break;
				case TRIANGLESET:
					break;
				case VECTOR:
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
				((Tetrahedron3DElement) hns[i].getObject(0)).neighbours[hns[i]
						.getIndex(0)] = (Tetrahedron3DElement) hns[i]
						.getObject(1);
				((Tetrahedron3DElement) hns[i].getObject(1)).neighbours[hns[i]
						.getIndex(1)] = (Tetrahedron3DElement) hns[i]
						.getObject(0);
			}
			// add element to SAM
			this.sam.insert(element);
			return element;
		} else
			return null;
	}

	/**
	 * Removes the given element from the component.<br>
	 * Assumes that an element of geometrical equality exists in this.
	 * 
	 * @param element
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
	public Tetrahedron3DElement removeElt(Tetrahedron3D element)
			throws UpdateException { // Dag

		// look for geometrically equivalent element in this component
		Tetrahedron3DElement removable = null;
		Set<Tetrahedron3DElement> set = (Set<Tetrahedron3DElement>) this.sam
				.intersects(element.getMBB());
		Iterator<Tetrahedron3DElement> it = set.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement current = it.next();
			if (current.isGeometryEquivalent(element, this.epsilon)) {
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
				this.entry = null;
			} else {
				switch (removable.countNeighbours()) {

				case 1: // only one neighbour -> smooth removal
					removable.neighbours[0].setNeighbourNull(removable,
							this.epsilon);
					break;

				case 2:
					// set new (potential) neighbourhood first
					int index0 = removable.neighbours[0].setNeighbourNull(
							removable, this.epsilon);
					int index1 = removable.neighbours[1].setNeighbourNull(
							removable, this.epsilon);
					// check if net will still be connected
					if (!isConnectedWith(removable.neighbours[0],
							removable.neighbours[1])) {
						// reverse settings and return null for
						// "illegal removal operation"
						removable.neighbours[0].neighbours[index0] = removable;
						removable.neighbours[1].neighbours[index1] = removable;
						return null;
					}
					break;

				case 3:
					// set new (potential) neighbourhood first
					index0 = removable.neighbours[0].setNeighbourNull(
							removable, this.epsilon);
					index1 = removable.neighbours[1].setNeighbourNull(
							removable, this.epsilon);
					int index2 = removable.neighbours[2].setNeighbourNull(
							removable, this.epsilon);
					// check if net will still be connected
					for (int i = 0; i < 2; i++)
						if (!isConnectedWith(removable.neighbours[i],
								removable.neighbours[(i + 1)])) {
							// reverse settings and return null for
							// "illegal removal operation"
							removable.neighbours[0].neighbours[index0] = removable;
							removable.neighbours[1].neighbours[index1] = removable;
							removable.neighbours[2].neighbours[index2] = removable;
							return null;
						}
					break;

				case 4:
					// set new (potential) neighbourhood first
					index0 = removable.neighbours[0].setNeighbourNull(
							removable, this.epsilon);
					index1 = removable.neighbours[1].setNeighbourNull(
							removable, this.epsilon);
					index2 = removable.neighbours[2].setNeighbourNull(
							removable, this.epsilon);
					int index3 = removable.neighbours[3].setNeighbourNull(
							removable, this.epsilon);
					// check if net will still be connected
					for (int i = 0; i < 3; i++)
						if (!isConnectedWith(removable.neighbours[i],
								removable.neighbours[(i + 1)])) {
							// reverse settings and return null for
							// "illegal removal operation"
							removable.neighbours[0].neighbours[index0] = removable;
							removable.neighbours[1].neighbours[index1] = removable;
							removable.neighbours[2].neighbours[index2] = removable;
							removable.neighbours[3].neighbours[index3] = removable;
							return null;
						}
					break;

				default:
					throw new UpdateException(
							Db3dSimpleResourceBundle
									.getString("db3d.geom.defr"));
				}
				if (removable == this.entry)
					this.entry = removable.neighbours[0];
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
	public boolean isConnectedWith(Tetrahedron3DElement start,
			Tetrahedron3DElement end) {
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
	private boolean isConnectedWith(Tetrahedron3DElement start,
			Tetrahedron3DElement end, FlagMap flags) {

		flags.add(start); // set visited
		for (int i = 0; i < 4; i++) {
			Tetrahedron3DElement nb = start.neighbours[i];
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
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param tetrahedron
	 *            Tetrahedron3D
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElement(Tetrahedron3D tetrahedron) { // Dag
		GeoEpsilon epsilon = this.getGeoEpsilon();
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(tetrahedron.getMBB());

		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
			if (tetra.isGeometryEquivalent(tetrahedron, epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether a element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param triangle
	 *            Triangle3D
	 * @return boolean - true if contained, false otherwise
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsElt(Triangle3D triangle) { // Dag
		Set<Tetrahedron3DElement> tetset = (Set<Tetrahedron3DElement>) this.sam
				.intersects(triangle.getMBB());
		Set<Triangle3D> triset = this.getTriangles(tetset);

		Iterator<Triangle3D> it = triset.iterator();
		while (it.hasNext()) {
			Triangle3D tri = it.next();
			if (tri.isGeometryEquivalent(triangle, this.epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether a element with the coordinates of given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param segment
	 *            Segment3D
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsElt(Segment3D segment) { // Dag
		Set<Tetrahedron3DElement> tetset = (Set<Tetrahedron3DElement>) this.sam
				.intersects(segment.getMBB());
		Set<Segment3D> segset = this.getSegments(tetset);

		Iterator<Segment3D> it = segset.iterator();
		while (it.hasNext()) {
			Segment3D seg = it.next();
			if (seg.isGeometryEquivalent(segment, this.epsilon))
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
		Set<Tetrahedron3DElement> tetset = (Set<Tetrahedron3DElement>) this.sam
				.intersects(point.getMBB());
		Set<Point3D> poiset = this.getPoints(tetset);

		Iterator<Point3D> it = poiset.iterator();
		while (it.hasNext()) {
			Point3D poi = it.next();
			if (poi.isEqual(point, this.epsilon))
				return true;
		}
		return false;
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private int countBorderElements(SPATIALTYPES typ) { // Dag
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this
				.getElementsViaSAM();

		int counter = 0;
		Set<Triangle3D> triangleHS = new HashSet<Triangle3D>();
		// iterate over all elements, check if current is border element and
		// count specified (sub)elements if true
		Iterator<Tetrahedron3DElement> tetit = tetras.iterator();
		while (tetit.hasNext()) {
			Tetrahedron3DElement tetra = tetit.next();
			if (typ == SPATIALTYPES.SOLID_ELEMENT_3D) {
				for (int i = 0; i < 4; i++)
					if (tetra.neighbours[i] == null) {
						counter++;
						break;
					}
			} else {
				for (int i = 0; i < 4; i++)
					if (tetra.neighbours[i] == null)
						triangleHS.add(tetra.getTriangle(i, this.epsilon));
			}
		}

		if (typ == SPATIALTYPES.SOLID_ELEMENT_3D)
			return counter;

		if (typ == SPATIALTYPES.SURFACE_ELEMENT_3D)
			return triangleHS.size();

		EquivalentableHashSet segmentHS = new EquivalentableHashSet(
				(tetras.size() * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);
		EquivalentableHashSet pointHS = new EquivalentableHashSet(
				(tetras.size() * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);
		Iterator<Triangle3D> triit = triangleHS.iterator();
		while (triit.hasNext()) {
			Triangle3D tri = triit.next();
			if (typ == SPATIALTYPES.CURVE_ELEMENT_3D)
				for (int i = 0; i < 3; i++)
					segmentHS.add(tri.getSegment(i, this.epsilon));
			else
				for (int i = 0; i < 3; i++)
					pointHS.add(tri.getPoints()[i]);

		}

		if (typ == SPATIALTYPES.CURVE_ELEMENT_3D)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderVertices() {
		return countBorderElements(SPATIALTYPES.SAMPLE_ELEMENT_3D);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderEdges() {
		return countBorderElements(SPATIALTYPES.CURVE_ELEMENT_3D);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderFaces() {
		return countBorderElements(SPATIALTYPES.SURFACE_ELEMENT_3D);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderTetras() {
		return countBorderElements(SPATIALTYPES.SOLID_ELEMENT_3D);
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private Set<?> getBorderElements(SPATIALTYPES typ) { // Dag
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this
				.getElementsViaSAM();
		Set<Tetrahedron3DElement> tetraHS = new HashSet<Tetrahedron3DElement>();
		Set<Triangle3D> triangleHS = new HashSet<Triangle3D>();

		// iterate over all elements, check if current is border element and get
		// specified (sub)elements if true
		Iterator<Tetrahedron3DElement> tetit = tetras.iterator();
		while (tetit.hasNext()) {
			Tetrahedron3DElement tetra = tetit.next();
			if (typ == SPATIALTYPES.SOLID_ELEMENT_3D) {
				for (int i = 0; i < 4; i++)
					if (tetra.isBorderElement())
						tetraHS.add(tetra);
			} else {
				for (int i = 0; i < 4; i++)
					if (!tetra.hasNeighbour(i))
						triangleHS.add(tetra.getTriangle(i, this.epsilon));
			}
		}

		if (typ == SPATIALTYPES.SOLID_ELEMENT_3D)
			return tetraHS;

		if (typ == SPATIALTYPES.SURFACE_ELEMENT_3D)
			return triangleHS;

		if (typ == SPATIALTYPES.CURVE_ELEMENT_3D) {
			EquivalentableHashSet segmentHS = new EquivalentableHashSet(
					(tetras.size() * 2), this.epsilon,
					Equivalentable.GEOMETRY_EQUIVALENT);
			Iterator<Triangle3D> triit = triangleHS.iterator();
			while (triit.hasNext()) {
				Triangle3D tri = triit.next();
				for (int i = 0; i < 3; i++) {
					segmentHS.add(tri.getSegment(i, this.epsilon));
				}
			}
			return segmentHS;
		}

		EquivalentableHashSet pointHS = new EquivalentableHashSet(
				(tetras.size() * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);
		Iterator<Triangle3D> triit = triangleHS.iterator();
		while (triit.hasNext()) {
			Triangle3D tri = triit.next();
			for (int i = 0; i < 3; i++) {
				pointHS.add(tri.getPoints()[i]);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Set<Point3D> getBorderVertices() {
		return (Set<Point3D>) getBorderElements(SPATIALTYPES.SAMPLE_ELEMENT_3D);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Set<Segment3D> getBorderEdges() {
		return (Set<Segment3D>) getBorderElements(SPATIALTYPES.CURVE_ELEMENT_3D);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Set<Triangle3D> getBorderFaces() {
		return (Set<Triangle3D>) getBorderElements(SPATIALTYPES.SURFACE_ELEMENT_3D);
	}

	/**
	 * Returns a set of border elements (tetrahedrons) of this component.
	 * 
	 * @return Set of border elements of this component.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * 
	 */
	public Set<Tetrahedron3D> getBorderTetras() {
		return (Set<Tetrahedron3D>) getBorderElements(SPATIALTYPES.SOLID_ELEMENT_3D);
	}

	/**
	 * Returns the boundary area of this component.
	 * 
	 * @return double - area.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public double getArea() { // Dag

		Set<Triangle3D> set = this.getTriangles(this.getElementsViaRecursion());
		double area = 0;

		Iterator<Triangle3D> it = set.iterator();
		while (it.hasNext()) {
			Triangle3D triangle = it.next();
			if (this.isBorderFace(triangle))
				area += triangle.getArea(this.epsilon);
		}
		return area;
	}

	/**
	 * Returns the volume of this component.
	 * 
	 * @return double - volume.
	 */
	public double getVolume() { // Dag

		Set<Tetrahedron3DElement> tetras = this.getElementsViaRecursion();
		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		double volume = 0;

		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
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
		int verticeCount = this.vertices.size();
		int edgeCount = this.edges.size();
		int faceCount = this.faces.size();

		return (verticeCount - edgeCount + faceCount);
	}

	/**
	 * Returns the entry element.
	 * 
	 * @return TetrahedronElt3D - entry element.
	 */
	public Tetrahedron3DElement getEntryElement() {
		return this.entry;
	}

	/**
	 * Returns the TetrahedronElt3D objects in a Set. This method uses a walk
	 * over the neighbours (NOT THE internal SAM) to retrieve all elements. Use
	 * this method only in case you need to process all the elements.
	 * 
	 * @return Set with TetrahedronElt3D objects.
	 */
	public Set<Tetrahedron3DElement> getElementsViaRecursion() {
		Set<Tetrahedron3DElement> set = new IdentityHashSet();
		makeSet(set, this.entry);
		return set;
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private Set<Triangle3D> getTriangles(Set<Tetrahedron3DElement> tetrahedrons) { // Dag
		Tetrahedron3DElement[] tetras = (Tetrahedron3DElement[]) tetrahedrons
				.toArray(new Tetrahedron3DElement[tetrahedrons.size()]);
		Set<Triangle3D> triangleHS = new EquivalentableHashSet(
				(tetras.length * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);

		for (int i = 0; i < tetras.length; i++) {
			for (int j = 0; j < 4; j++)
				triangleHS.add(tetras[i].getTriangle(j, this.epsilon));
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private Set<Segment3D> getSegments(Set<Tetrahedron3DElement> tetrahedrons) { // Dag

		Set<Triangle3D> triangleHS = getTriangles(tetrahedrons);
		Set<Segment3D> segmentHS = new EquivalentableHashSet(
				(triangleHS.size() * 2), this.epsilon,
				Equivalentable.GEOMETRY_EQUIVALENT);
		Iterator<Triangle3D> it = triangleHS.iterator();
		while (it.hasNext()) {
			Triangle3D triangle = it.next();
			for (int i = 0; i < 3; i++)
				segmentHS.add(triangle.getSegment(i, this.epsilon));
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
	private Set<Point3D> getPoints(Set<Tetrahedron3DElement> tetrahedrons) { // Dag

		Set<Point3D> pointHS = new IdentityHashSet();
		Iterator<Tetrahedron3DElement> it = tetrahedrons.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
			for (int j = 0; j < 4; j++)
				pointHS.add(tetra.getPoints()[j]);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isBorderVertex(Point3D point) { // Dag

		MBB3D mbb = point.getMBB();
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this
				.getSAM().intersects(mbb);
		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tet = it.next();
			for (int i = 0; i < 4; i++) { // find face containing point and
				// check if no neighbour tetrahedron
				// exists for its index -> true
				Triangle3D tri = tet.getTriangle(i, this.epsilon);
				for (int j = 0; j < 3; j++)
					if (point.isEqual(tri.getPoints()[j], this.epsilon))
						if (tet.neighbours[i] == null)
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
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if is a border edge, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isBorderEdge(Segment3D segment) { // Dag

		MBB3D mbb = segment.getMBB();
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(mbb);
		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tet = it.next();
			for (int i = 0; i < 4; i++) { // find triangle containing seg and
				// check if no neighbour in this
				// direction exists -> true
				Triangle3D tri = tet.getTriangle(i, this.epsilon);
				for (int j = 0; j < 3; j++)
					if (segment.isGeometryEquivalent(
							tri.getSegment(j, this.epsilon), this.epsilon))
						if (tet.neighbours[i] == null)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isBorderFace(Triangle3D triangle) { // Dag

		MBB3D mbb = triangle.getMBB();
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(mbb);
		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tet = it.next();
			for (int i = 0; i < 4; i++) { // find index i for triangle in tet
				// and return true if no
				// neighbour(i) exists
				Triangle3D tri = tet.getTriangle(i, this.epsilon);
				if (tri.isGeometryEquivalent(triangle, this.epsilon))
					if (tet.neighbours[i] == null)
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
	 * @param tetrahedron
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if is a border tetrahedron, false otherwise.
	 */
	public boolean isBorderElement(Tetrahedron3D tetrahedron) { // Dag

		MBB3D mbb = tetrahedron.getMBB();
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(mbb);
		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tet = it.next();
			if (tet.isGeometryEquivalent(tetrahedron, this.epsilon))
				for (int i = 0; i < 4; i++)
					if (tet.neighbours[i] == null)
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
	public boolean intersects(Plane3D plane) {

		Geometry3D sgo = this.mbb.intersection(plane, this.epsilon);
		if (sgo == null)
			return false;
		Set<Tetrahedron3DElement> set = (Set<Tetrahedron3DElement>) this.sam
				.intersects(sgo.getMBB());
		Iterator<Tetrahedron3DElement> it = set.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
			if (tetra.intersects(plane, this.epsilon))
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
	public boolean intersects(Line3D line) {

		Geometry3D sgo = this.mbb.intersection(line, this.epsilon);
		if (sgo == null)
			return false;
		Set<Tetrahedron3DElement> set = (Set<Tetrahedron3DElement>) this.sam
				.intersects(sgo.getMBB());
		Iterator<Tetrahedron3DElement> it = set.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
			if (tetra.intersects(line, this.epsilon))
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
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             MBB3D is not a simplex. The exception originates in the
	 *             method intersection(MBB3D, GeoEpsilon) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(MBB3D mbb) { // Dag

		if (!this.mbb.intersects(mbb, this.epsilon))
			return false;

		Set<Tetrahedron3DElement> set = (Set<Tetrahedron3DElement>) this.sam
				.intersects(mbb);
		Iterator<Tetrahedron3DElement> it = set.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
			if (tetra.intersects(mbb, this.epsilon))
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private Tetrahedron3DElement getTetraContainingPoint(Point3D point) { // Dag

		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.contains(point);

		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetra = it.next();
			if (tetra.contains(point, epsilon))
				return tetra;
		}
		return null;
	}

	/**
	 * Tests whether this contains the given segment geometrically.
	 * 
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, GeoEpsilon epsilon) method
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsInside(Segment3D segment) {

		if (!(segment.getMBB().inside(this.mbb, this.epsilon)))
			return false;

		if (!(this.containsInside(segment.getPoints()[0]) && this
				.containsInside(segment.getPoints()[1])))
			return false;

		Tetrahedron3DElement current = getTetraContainingPoint(segment
				.getPoints()[0]);
		Point3D second = segment.getPoints()[1];
		FlagMap flag = new FlagMap();

		while (!current.contains(second, this.epsilon)) {
			flag.add(current);
			for (int i = 0; i < 4; i++) {
				if (current.neighbours[i] != null
						&& (!flag.check(current.neighbours[i]))
						&& current.neighbours[i].intersects(segment,
								this.epsilon)) {
					current = current.neighbours[i];
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
	 *             if the intersectsInt(Line3D line, GeoEpsilon epsilon) method
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
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Triangle3D is not a simplex. The exception originates in the
	 *             method intersects(Triangle3D, GeoEpsilon) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsInside(Triangle3D triangle) {

		if (!(triangle.getMBB().inside(this.getMBB(), this.epsilon)))
			return false;
		if (!(this.containsInside(triangle.getPoints()[0])
				&& this.containsInside(triangle.getPoints()[1]) && this
					.containsInside(triangle.getPoints()[2])))
			return false;

		// check if the border faces of all triangle intersecting tetrahedrons
		// intersects with triangle. return false in this case, true if not
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(triangle.getMBB());
		HashSet<Triangle3D> hull = new HashSet<Triangle3D>();

		Iterator<Tetrahedron3DElement> it = tetras.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tet = it.next();
			if (tet.intersects(triangle, this.epsilon))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				for (int i = 0; i < 4; i++) {
					if (tet.neighbours[i] == null)
						hull.add(tet.getTriangle(i, this.epsilon));
				}
		}

		Iterator<Triangle3D> triit = hull.iterator();
		while (triit.hasNext()) {
			Triangle3D tri = triit.next();
			if (tri.intersects(triangle, this.epsilon))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				return false;
		}

		return true;
	}

	/**
	 * Tests whether this contains the given tetrahedron geometrically.
	 * 
	 * @param tetrahedron
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, GeoEpsilon epsilon) method
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
	 */
	public boolean containsInside(Tetrahedron3D tetrahedron) {

		if (!(tetrahedron.getMBB().inside(this.mbb, this.epsilon)))
			return false;
		if (!(this.containsInside(tetrahedron.getPoints()[0])
				&& this.containsInside(tetrahedron.getPoints()[1])
				&& this.containsInside(tetrahedron.getPoints()[2]) && this
					.containsInside(tetrahedron.getPoints()[3])))
			return false;

		// check if the border faces of all tetra intersecting tetrahedrons
		// intersects with triangle. return false in this case, true if not
		Set<Tetrahedron3DElement> tetras = (Set<Tetrahedron3DElement>) this.sam
				.intersects(tetrahedron.getMBB());
		HashSet<Triangle3D> hull = new HashSet<Triangle3D>();

		Iterator<Tetrahedron3DElement> tetit = tetras.iterator();
		while (tetit.hasNext()) {
			Tetrahedron3DElement tet = tetit.next();
			if (tet.intersects(tetrahedron, this.epsilon))
				for (int i = 0; i < 4; i++) {
					if (tet.neighbours[i] == null)
						hull.add(tet.getTriangle(i, this.epsilon));
				}
		}

		Iterator<Triangle3D> triit = hull.iterator();
		while (triit.hasNext()) {
			Triangle3D tri = triit.next();
			if (tetrahedron.intersects(tri, this.epsilon))
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
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SOLID_COMPONENT_E3D;
	}

	/**
	 * Sets the reference to enclosing TetrahedronNet3D.
	 * 
	 * @param net3D
	 *            enclosing TetrahedronNet3D
	 */
	public void setNet(Net3D net3D) {
		this.net = (Tetrahedron3DNet) net3D;
	}

	/**
	 * Builds the neighbour topology of the net for the given Tetrahedron
	 * elements.
	 * 
	 * @param elements
	 *            Tetrahedron3DElement[]
	 * @throws DB3DException
	 *             - during registering neighbours, a DB3DException is thrown if
	 *             the neighbour index is not 0, 1, 2 or 3.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public void buildNetTopology(Tetrahedron3DElement[] elements) {
		GeoEpsilon so = getGeoEpsilon();

		for (int i = 0; i < elements.length; i++) {
			if (elements[i].isInterior() != true) {
				Set<Tetrahedron3DElement> query = (Set<Tetrahedron3DElement>) this.sam
						.intersects(elements[i].getMBB());
				query.remove(elements[i]);

				Point3D po = null;
				Tetrahedron3DElement te = null;
				// wenn schon Nachbarn vorhanden - diese aus Query-Set
				te = elements[i].neighbours[0];
				// entfernen damit diese nicht getestet werden
				if (te != null)
					query.remove(te);
				te = elements[i].neighbours[1];
				if (te != null)
					query.remove(te);
				te = elements[i].neighbours[2];
				if (te != null)
					query.remove(te);
				te = elements[i].neighbours[3];
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

					if ((elements[i].neighbours[m]) == null) {
						po = elements[i].getPoints()[p1];
						Iterator<Tetrahedron3DElement> it = query.iterator();

						while (it.hasNext()) {
							Tetrahedron3DElement oTE = it.next();

							for (int j = 0; j < 4; j++) {
								Point3D ot = oTE.getPoints()[j];
								if (po.isEqual(ot, so)) {
									// ein punkt ist gleich
									Point3D po1 = elements[i].getPoints()[p2];

									for (int k = 0; k < 4; k++) {
										if (k != j) {
											Point3D ot1 = oTE.getPoints()[k];
											if (po1.isEqual(ot1, so)) {
												// 2.Punkt => sie haben
												// gemeinsame Kante
												Point3D po2 = elements[i]
														.getPoints()[p3];

												for (int n = 0; n < 4; n++) {
													if (n != k && n != j) {
														Point3D ot2 = oTE
																.getPoints()[n];

														if (po2.isEqual(ot2, so)) {
															// nachbarn
															elements[i].neighbours[m] = oTE;
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
	 * Updates the Entry elements after changes in the net component.
	 */
	protected void updateEntryElement() {
		Set<Tetrahedron3DElement> set = (Set<Tetrahedron3DElement>) this.sam
				.getEntries();
		Iterator<Tetrahedron3DElement> it = set.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tetelt = it.next();
			if (!tetelt.isInterior()) {
				this.entry = tetelt;
				break;
			}
		}
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Set<Triangle3D> findTinBorder1() {
		if (this.isEmpty())
			throw new IllegalStateException("The TIN is empty!");

		HashSet<Triangle3D> outerTriangles = new HashSet<Triangle3D>();

		Tetrahedron3DElement tetr = null;

		Set<Tetrahedron3DElement> s = this.getElementsViaRecursion();

		Iterator<Tetrahedron3DElement> it = s.iterator();

		while (it.hasNext()) {

			// Searching through all tetrahedrons:

			tetr = it.next();

			// If an external tetrahedron was found:
			for (int j = 0; j < 4; j++)
				if (tetr.neighbours[j] == null)
					outerTriangles.add((Triangle3D) tetr.getTriangle(j,
							this.epsilon));
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Set<Triangle3D> findTinBorder2() {
		Tetrahedron3DElement tetr = null;
		// Getting all tetrahedrons:
		Set<Tetrahedron3DElement> s = this.getElementsViaRecursion();
		Iterator<Tetrahedron3DElement> it = s.iterator();
		if (it.hasNext())
			tetr = it.next();

		// Looking for the first outer tetrahedron:
		while (tetr.countNeighbours() == 4 && it.hasNext()) {
			tetr = it.next();
		}
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private void tinBorderViaRecursion(Set<Triangle3D> outerTriangles,
			Set<Tetrahedron3D> visited, Tetrahedron3DElement tetrahedron) {
		// The following is done for every of the 4 neighbours of tetr:
		for (int i = 0; i < 4; i++) {
			if (tetrahedron.neighbours[i] == null) {
				// Adding outer triangles to the set:
				outerTriangles.add(tetrahedron.getTriangle(i, this.epsilon));
			} else {
				// Getting the neighbour:
				Tetrahedron3DElement nb = tetrahedron.neighbours[i];

				if (nb.countNeighbours() == 4) { // if nb is an inner
					// tetrahedron
					for (int j = 0; j < 4; j++) {
						Triangle3D outerTri = tetrahedron.getTriangle(i,
								this.epsilon);

						/*
						 * seg is used to remember the common segment of the
						 * last outer triangle that was found and of the next
						 * outer triangle that is being searched for.
						 */

						Segment3D seg = getCommonSegment(outerTri, nb);

						getOuterNeighbourViaRecursion(seg, nb);
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
	 * @param segment the common segment of the current outer tetrahedron and
	 * the next outer tetrahedron that needs to be found
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private void getOuterNeighbourViaRecursion(Segment3D segment,
			Tetrahedron3DElement nb) {
		for (int i = 0; i < 4; i++) {
			Tetrahedron3DElement nb2 = nb.neighbours[i];
			if (haveCommonSegment(segment, nb2)) {
				if (nb2.countNeighbours() < 4) {
					// if nb2 is an outer tetrahedron, stop the recursion
					nb = nb2;
				} else {
					// if nb2 is an inner tetrahedron, continue looking for the
					// outer tetrahedron:
					getOuterNeighbourViaRecursion(segment, nb2);
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private Segment3D getCommonSegment(Triangle3D triangle,
			Tetrahedron3DElement nb) {
		Segment3D[] outerSeg = new Segment3D[] {
				triangle.getSegment(0, this.epsilon),
				triangle.getSegment(1, this.epsilon),
				triangle.getSegment(2, this.epsilon) };
		Triangle3D[] nbTri = new Triangle3D[] {
				nb.getTriangle(0, this.epsilon),
				nb.getTriangle(1, this.epsilon),
				nb.getTriangle(2, this.epsilon),
				nb.getTriangle(3, this.epsilon) };
		Segment3D[] nbSeg = new Segment3D[9];
		// It is enough to regard just 3 triangles of the tetrahedron,
		// because the segments repeat.
		nbSeg[0] = nbTri[0].getSegment(0, this.epsilon);
		nbSeg[1] = nbTri[0].getSegment(1, this.epsilon);
		nbSeg[2] = nbTri[0].getSegment(2, this.epsilon);
		nbSeg[3] = nbTri[1].getSegment(0, this.epsilon);
		nbSeg[4] = nbTri[1].getSegment(1, this.epsilon);
		nbSeg[5] = nbTri[1].getSegment(2, this.epsilon);
		nbSeg[6] = nbTri[2].getSegment(0, this.epsilon);
		nbSeg[7] = nbTri[2].getSegment(1, this.epsilon);
		nbSeg[8] = nbTri[2].getSegment(2, this.epsilon);
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
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private boolean haveCommonSegment(Segment3D segment, Tetrahedron3DElement nb) {
		Triangle3D[] nbTri = new Triangle3D[] {
				nb.getTriangle(0, this.epsilon),
				nb.getTriangle(1, this.epsilon),
				nb.getTriangle(2, this.epsilon),
				nb.getTriangle(3, this.epsilon) };
		Segment3D[] nbSeg = new Segment3D[9];
		// It is enough to regard just 3 triangles of the tetrahedron,
		// because the segments repeat.
		nbSeg[0] = nbTri[0].getSegment(0, this.epsilon);
		nbSeg[1] = nbTri[0].getSegment(1, this.epsilon);
		nbSeg[2] = nbTri[0].getSegment(2, this.epsilon);
		nbSeg[3] = nbTri[1].getSegment(0, this.epsilon);
		nbSeg[4] = nbTri[1].getSegment(1, this.epsilon);
		nbSeg[5] = nbTri[1].getSegment(1, this.epsilon);
		nbSeg[6] = nbTri[2].getSegment(0, this.epsilon);
		nbSeg[7] = nbTri[2].getSegment(1, this.epsilon);
		nbSeg[8] = nbTri[2].getSegment(2, this.epsilon);
		for (int b = 0; b < 9; b++) {
			if (nbSeg[b] == segment) {
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
	private void makeSet(Set<Tetrahedron3DElement> set, Tetrahedron3DElement elt) {

		// make visited
		set.add(elt);

		for (int i = 0; i < 4; i++) {
			Tetrahedron3DElement nb = elt.neighbours[i];
			if (nb != null && !set.contains(nb)) // if not already visited
				makeSet(set, nb);
		}
	}

	/**
	 * Searches for an element with the given id and returns it. If it was not
	 * found, returns <code>null</code>.
	 * 
	 * @return SimpleGeoObj - element with the given id.
	 */
	public Tetrahedron3DElement getElement(int id) {

		Iterator<Tetrahedron3DElement> it = getElementsViaRecursion()
				.iterator();
		while (it.hasNext()) {
			Tetrahedron3DElement tet = it.next();
			if (tet.id == id)
				return tet;
		}
		return null;
	}

}
