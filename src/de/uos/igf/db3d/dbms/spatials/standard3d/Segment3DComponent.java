package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.collections.IdentityHashSet;
import de.uos.igf.db3d.dbms.collections.SAM;
import de.uos.igf.db3d.dbms.exceptions.ContainmentException;
import de.uos.igf.db3d.dbms.exceptions.DB3DException;
import de.uos.igf.db3d.dbms.exceptions.GeometryException;
import de.uos.igf.db3d.dbms.exceptions.TopologyException;
import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D.GEOMETRYTYPES;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.WireframeGeometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * 
 * Segment3DComponent represents a single segment net component. All
 * Segment3DElements objects in this object belong to one semantic component.<br>
 * For segment nets with several components see @see Segment3DNet
 * 
 * @author Markus Jahn
 * 
 */
public class Segment3DComponent extends Component3DAbst {

	/* entry element (topological start element) */
	private Segment3DElement entry;

	/* orientation clean flag */
	private boolean oriented;

	/* connected flag */
	private boolean connected;

	/**
	 * Constructor.<br>
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 */
	public Segment3DComponent(GeoEpsilon epsilon) {
		super(epsilon);
		this.oriented = false;
		this.connected = false;
	}

	/**
	 * Constructor.<br>
	 * Constructs a SegmentNet3DComp object with the given SegmentElt3D[].<br>
	 * In the given array the neighbourhood topology has not be defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this segment
	 * array.
	 * 
	 * @param epsilon
	 *            geometric error
	 * @param elements
	 *            SegmentElt3D[]
	 * @throws UpdateException
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public Segment3DComponent(Segment3DElement[] elements, GeoEpsilon epsilon)
			throws UpdateException {
		super(epsilon);
		for (Segment3DElement element : elements) {
			this.addElement(element);
		}
		this.buildNetTopology(elements);
		this.connected = true;
		this.makeOrientationConsistent();
		this.oriented = true;
		updateEntryElement();
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
	 *             Point3D, GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Plane3D plane) throws DB3DException { // Dag

		Geometry3D obj = this.getMBB().intersection(plane, this.epsilon);
		// Here an IllegalStateException can be thrown. This exception
		// originates in the getPoint(int) method of the class Rectangle3D.

		// Here an IllegalStateException can be thrown signaling problems with
		// the dimension of the wireframe.

		// Here an IllegalStateException can be thrown signaling problems with
		// the index of a point coordinate.
		if (obj == null)
			return false;

		MBB3D intMbb = new MBB3D();

		switch (obj.getGeometryType()) {
		case POINT:
			intMbb = ((Point3D) obj).getMBB();
			break;
		case SEGMENT:
			intMbb = ((Segment3D) obj).getMBB();
			break;
		case TRIANGLE:
			intMbb = ((Triangle3D) obj).getMBB();
			break;
		case WIREFRAME:
			intMbb = ((WireframeGeometry3D) obj).getMBB();
			// Here an IllegalArgumentException can be thrown.
			break;
		default:
			throw new DB3DException(
					Db3dSimpleResourceBundle.getString("db3d.geom.defrmethod"));
		}

		Set<Segment3DElement> segments = this.sam.intersects(intMbb);

		Iterator<Segment3DElement> it = segments.iterator();
		while (it.hasNext()) {
			Segment3DElement segment = it.next();
			if (segment.intersects(plane, this.epsilon))
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
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Line3D line) { // Dag

		Geometry3D obj = this.getMBB().intersection(line, this.epsilon);
		MBB3D intMbb = new MBB3D();
		if (obj == null)
			return false;
		if (obj.getGeometryType() == GEOMETRYTYPES.POINT)
			intMbb = ((Point3D) obj).getMBB();
		if (obj.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			intMbb = ((Segment3D) obj).getMBB();

		Set<Segment3DElement> segments = this.sam.intersects(intMbb);

		Iterator<Segment3DElement> it = segments.iterator();
		while (it.hasNext()) {
			Segment3DElement segment = it.next();
			if (segment.intersects(line, this.epsilon))
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
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(MBB3D mbb) { // Dag

		if (!mbb.intersects(this.getMBB(), this.epsilon))
			return false;

		Set<Segment3DElement> set = this.sam.intersects(mbb);
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement segelt = it.next();
			if (segelt.intersects(mbb, this.epsilon))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Point3D point) {
		Set<Segment3DElement> set = this.sam.contains(point);
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement segelt = it.next();
			if (segelt.contains(point, this.epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically.
	 * 
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Segment3D segment) {
		Set<Segment3DElement> set = this.sam.intersects(segment.getMBB());
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement segelt = it.next();
			if (segelt.contains(segment, this.epsilon))
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
	public Segment3DElement getEndElement() {// Dag
		if (this.isClosed())
			return (this.entry.neighbours[0]);
		if (this.entry.getNeighbour(1) == null) {
			return this.entry;
		}
		Segment3DElement element = this.entry.getNeighbour(1);
		while (element.neighbours[1] != null) {
			element = element.neighbours[1];
		}
		return element;
	}

	/**
	 * Adds the given element to the component.<br>
	 * The given element reference is not valid anymore after insertion.<br>
	 * If you need to hold a reference on the element, update your variable with
	 * the return value element !
	 * 
	 * @param element
	 *            Segment3D to be added
	 * @return SegmentElt3D - the inserted instance.
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
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Segment3DElement addElement(Segment3DElement element)
			throws UpdateException { // Dag

		if (this.containsElement(element))
			throw new ContainmentException("Element already contained !");

		// if the netcomp is empty
		if (this.isEmpty()) {
			this.entry = element;
			this.sam.insert(element);
			// Here an IllegalArgumentException can be thrown.
			this.oriented = true;
			this.connected = true;
			element.id = this.edgeID++;
			return element;
		}
		// if the netcomp is already closed
		if (this.isClosed())
			throw new TopologyException("Net component is already closed !");

		boolean addAtEntry = false;
		boolean addAtEnd = false;
		Segment3DElement end = this.getEndElement();

		// check where to add
		if (element.getPoints()[0].isEqual(this.entry.getPoints()[0],
				this.epsilon)) {
			addAtEntry = true;
			element.invertOrientation();
		}
		if (element.getPoints()[1].isEqual(this.entry.getPoints()[0],
				this.epsilon)) {
			addAtEntry = true;
		}
		if (element.getPoints()[0].isEqual(end.getPoints()[1], this.epsilon)) {
			addAtEnd = true;
		}
		if (element.getPoints()[1].isEqual(end.getPoints()[1], this.epsilon)) {
			addAtEnd = true;
			element.invertOrientation();
		}
		if (!addAtEntry && !addAtEnd) { // not at entry or end point
			throw new GeometryException(
					"New Element is disjunct to net component !");
		}

		// test for potential illegal intersections
		Iterator<Segment3DElement> it = this.sam.intersects(element.getMBB())
				.iterator();
		while (it.hasNext()) {
			Segment3DElement segment = it.next();

			if (!segment.isEqual(this.entry, this.epsilon)
					&& !segment.isEqual(end, this.epsilon)) {

				Geometry3D intersection = segment.intersection(element,
						this.epsilon);

				if (intersection != null) {
					throw new GeometryException(
							"New Element intersects net component !");
				}
			}
		}

		element.id = this.edgeID++;
		this.sam.insert(element);
		this.edges.put(element.id, element);

		// add newSeg
		if (addAtEntry) {
			this.entry.neighbours[0] = element; // set neighbours
			element.neighbours[1] = this.entry;// set neighbours
			this.entry = element; // update entry element
		}
		if (addAtEnd) {
			end.neighbours[1] = element; // set neighbours
			element.neighbours[0] = end; // set neighbours
		}

		return element;
	}

	/**
	 * Removes the given element from the component. At the moment only entry
	 * and end element can be removed if the SegmentNet3DComp is NOT closed !
	 * 
	 * @param element
	 *            Segment3D to be removed
	 * @return SegmentElt3D - removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Segment3DElement removeElement(Segment3D element)
			throws UpdateException { // Dag

		if (this.isClosed()) { // find element, set entry and remove it
			Segment3DElement removable = null;
			Set<Segment3DElement> set = this.sam.intersects(element.getMBB());
			Iterator<Segment3DElement> it = set.iterator();
			while (it.hasNext()) {
				Segment3DElement current = it.next();
				if (current.isGeometryEquivalent(element, this.epsilon)) {
					removable = current;
					break;
				}
			}
			if (removable != null) {
				this.entry = removable.neighbours[0];
				this.sam.remove(removable);
				// Here an IllegalArgumentException can be thrown.
				return removable;
			}
			// else
			throw new ContainmentException("Element not contained !");
		}

		// check if elt is geometrically equivalent to entry element and handle
		// if
		if (element.isGeometryEquivalent(this.entry, this.epsilon)) {
			if (this.sam.getCount() == 1) { // net will be empty after
				// removal
				this.entry = null;
				this.oriented = false;
				this.connected = false;
			} else
				this.entry = this.entry.neighbours[0];

			this.sam.remove(this.entry);
			return this.entry;
		}
		// else case - check if elt is geometrically equivalent to end element
		// and handle if
		Segment3DElement end = this.getEndElement();
		if (element.isGeometryEquivalent(end, this.epsilon)) {
			this.sam.remove(end);
			return end;
		}

		if (this.containsElement(element)) // as net is not closed it is an
											// inside
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
	 * @param segment
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsElement(Segment3D segment) {
		Set<Segment3DElement> set = this.sam.intersects(segment.getMBB());
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement segelt = it.next();
			if (segelt.isGeometryEquivalent(segment, this.epsilon))
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
	public boolean containsElement(Point3D point) {
		Set<Segment3DElement> set = this.sam.intersects(point.getMBB());
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement segelt = it.next();

			if (segelt.getPoints()[0].isEqual(point, this.epsilon)
					|| segelt.getPoints()[1].isEqual(point, this.epsilon))
				return true;
		}
		return false;
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
		return (verticeCount - edgeCount);
	}

	/**
	 * Returns the length of this component.
	 * 
	 * @return double - length.
	 */
	public double getLength() {
		double length = 0;
		Iterator<Segment3DElement> it = this.getElementsViaRecursion()
				.iterator();
		while (it.hasNext())
			length += ((Segment3DElement) it.next()).getLength();

		return length;
	}

	/**
	 * Returns the entry element.<br>
	 * Entry element is the topological start element - it has a neighbour at
	 * index 0.
	 * 
	 * @return SegmentElt3D - entry element.
	 */
	public Segment3DElement getEntryElement() {
		return this.entry;
	}

	/**
	 * Returns the SegmentElt3D objects in a Set. This method uses a walk over
	 * the neighbours (NOT THE internal SAM) to retrieve all elements. Use this
	 * method only in case you need to process all the elements.
	 * 
	 * @return Set with SegmentElt3D objects.
	 */
	public Set<Segment3DElement> getElementsViaRecursion() {
		Set<Segment3DElement> set = new IdentityHashSet();

		int count = this.countElements();
		if (count == 0) // return set
			return set;

		if (this.entry == null) { // maybe not yet set
			updateEntryElement();
			if (count == 1) {
				set.add(this.entry);
				return set;
			}
			if (count > 1) {
				updateEntryElement();
			}
		}

		if (count == 1) {
			set.add(this.entry);
			return set;
		}

		if (this.isClosed()) { // if closed we must check for loop
			Segment3DElement begin = this.entry;
			do {
				set.add(this.entry);
				this.entry = this.entry.neighbours[0];
			} while (this.entry.neighbours[0] != begin);
		} else {
			do {
				set.add(this.entry);
				this.entry = this.entry.neighbours[0];
			} while (this.entry.neighbours[0] != null);
		}

		set.add(this.entry); // add the last
		return set;
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
			this.oriented = true;
			return;
		}

		Segment3DElement current;
		Segment3DElement old = this.entry;

		if (old == null) { // method called in constructor - there must be first
			// an entry element
			updateEntryElement();
			old = this.entry;
		}

		int edgeCount = countEdges();
		int counter = 1;

		do {
			current = old.neighbours[0];
			if (current != null) {
				Point3D check = current.getPoints()[0];
				// check if current is in the right orientation
				if (!check.isEqual(old.getPoints()[1], this.epsilon))
					current.invertOrientation();
				old = current;
			}
			counter++;
		} while (current != null && current.neighbours[0] != null
				&& counter < edgeCount + 1);

		this.oriented = true;
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
		Segment3DElement current = this.entry;
		if (point.isEqual(current.getPoints()[0], this.epsilon))
			return true;

		current = this.getEndElement();

		if (point.isEqual(current.getPoints()[1], this.epsilon))
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
		if (seg.isGeometryEquivalent(this.entry, this.epsilon))
			return true;
		if (seg.isGeometryEquivalent(this.getEndElement(), this.epsilon))
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
		if (this.entry == null)
			return false;

		if (this.entry.neighbours[0] != null)
			return true;
		else
			return false;
	}

	/**
	 * Returns the type of this as a <code>ComlexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.CURVE_COMPONENT_E3D;
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
	public void buildNetTopology(Segment3DElement[] elts) {
		GeoEpsilon so = getGeoEpsilon();

		for (int i = 0; i < elts.length; i++) {
			if (elts[i].isInterior() != true) {
				Set<Segment3DElement> query = this.sam.intersects(elts[i]
						.getMBB());
				query.remove(elts[i]);

				Point3D po = null;

				/*
				 * If neighbours already exist, delete them from the Query-Set,
				 * so that they are not tested.
				 */
				Segment3DElement te = null;
				te = elts[i].neighbours[0];
				if (te != null)
					query.remove(te);
				te = elts[i].neighbours[1];
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
						po = elts[i].getPoints()[p1];
						Iterator<Segment3DElement> it = query.iterator();

						while (it.hasNext()) {
							Segment3DElement oSE = it.next();

							for (int j = 0; j < 2; j++) {
								Point3D ot = oSE.getPoints()[j];
								if (po.isEqual(ot, so)) {
									// a point is equal
									elts[i].neighbours[m] = oSE;

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

									oSE.neighbours[otind] = elts[i];
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
	 * Updates the Entry elements after changes in the net component.
	 */
	protected void updateEntryElement() {
		Set<Segment3DElement> set = this.sam.getEntries();
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement segelt = it.next();
			if (!segelt.isInterior()) {
				if (segelt.neighbours[0] != null) { // neighbour 0 is at point
													// end
					// (Point 1)
					this.entry = segelt;
				} else {
					segelt.invertOrientation();
					this.entry = segelt;
				}
				break;
			}
		}

		if (this.entry == null) { // every element was interior so
			// its a closed comp
			SAM.NNResult[] result = this.sam
					.nearest(1, this.getMBB().getPMin());
			this.entry = (Segment3DElement) result[0].getObjectRef();
		}
	}

	/**
	 * Searches for an element with the given id and returns it. If it was not
	 * found, returns <code>null</code>.
	 * 
	 * @return Segment3DElement - element with the given id.
	 */
	public Segment3DElement getElement(int id) {
		Set<Segment3DElement> set = (Set<Segment3DElement>) this
				.getElementsViaSAM();
		Iterator<Segment3DElement> it = set.iterator();
		while (it.hasNext()) {
			Segment3DElement seg = it.next();
			if (seg.id == id)
				return seg;
		}
		return null;
	}

}
