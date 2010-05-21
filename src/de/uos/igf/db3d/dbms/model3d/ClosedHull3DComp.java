/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.api.UpdateException;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.geom.Vector3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp.TriangleElt3DIterator;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * ClosedHull3DComp represents a single ClosedHull component. All TriangleElt3D
 * objects in this object belong to one semantic component.
 * 
 * @author Wolfgang Baer
 */
public class ClosedHull3DComp implements PersistentObject, ComplexGeoObj,
		Serializable {
	/* the wrapped triangle net component */
	private TriangleNet3DComp tNetComp;

	/* the enclosing ClosedHull */
	private ClosedHull3D backRef;

	/* closed flag */
	private boolean closed;

	/* correct oriented flag - normals point outside */
	private boolean oriented;

	/**
	 * Constructor.
	 * 
	 * @param backRef
	 *            ClosedHull3D to which this component belongs
	 * @param tNetComp
	 *            TriangleNet3DComp to wrap
	 */
	protected ClosedHull3DComp(ClosedHull3D backRef, TriangleNet3DComp tNetComp) {
		this.tNetComp = tNetComp;
		this.backRef = backRef;
		this.closed = false;
	}

	/**
	 * Adds the given element to the component.
	 * 
	 * @param elt
	 *            Triangle3D that should be added
	 * @return TriangleElt3D - the inserted instance.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             -if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
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
		return getTNetComp().addElt(elt);
	}

	/**
	 * Removes the given element from the component.<br>
	 * Assumes that an element of geometric equality exists in this.
	 * 
	 * @param elt
	 *            Triangle3D that should be removed
	 * @return TriangleElt3D - removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public TriangleElt3D removeElt(Triangle3D elt) throws UpdateException {
		return getTNetComp().removeElt(elt);
	}

	/**
	 * Tests whether an element with the coordinates of the given point is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality of coordinates !<br>
	 * 
	 * @param point
	 *            Point3D that is tested
	 * @return boolean - true if contained.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean containsElt(Point3D point) {
		return getTNetComp().containsElt(point);
	}

	/**
	 * Tests whether an element with the coordinates of the given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality of coordinates !<br>
	 * 
	 * @param seg
	 *            Segment3D that is tested
	 * @return boolean - true if contained.
	 */
	public boolean containsElt(Segment3D seg) {
		return getTNetComp().containsElt(seg);
	}

	/**
	 * Tests whether an element with the coordinates of the given segment is
	 * contained in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * 
	 * @param tri
	 *            Triangle3D that is tested
	 * @return boolean - true if contained.
	 */
	public boolean containsElt(Triangle3D tri) {
		return getTNetComp().containsElt(tri);
	}

	/**
	 * Tests if <code>point</code> is strictly contained.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is strictly contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	public boolean containsStrictInside(Point3D point) {

		ScalarOperator sop = this.getScalarOperator();
		if (!this.getTNetComp().isOrientationConsistent())
			this.getTNetComp().makeOrientationConsistent(sop);

		if (!(point.getMBB().inside(this.getMBB(), sop)))
			return false;

		// point in border
		Set triangles = this.getSAM().intersects(point.getMBB());
		Iterator it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D tri = (TriangleElt3D) it.next();
			if (tri.contains(point, sop))
				return false;
		}

		return isInside(point);
	}

	/**
	 * Tests if <code>point</code> is (not strictly) contained.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * 
	 * @return boolean - true if is contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Point3D)
	 */
	public boolean containsInside(Point3D point) {

		ScalarOperator sop = this.getScalarOperator();
		if (!this.getTNetComp().isOrientationConsistent())
			this.getTNetComp().makeOrientationConsistent(sop);

		if (!(point.getMBB().inside(this.getMBB(), sop)))
			return false;

		// point in border
		Set triangles = this.getSAM().intersects(point.getMBB());
		Iterator it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D tri = (TriangleElt3D) it.next();
			if (tri.contains(point, sop))
				return true;
		}

		return isInside(point);
	}

	/*
	 * Tests if the point is inside the border. The algorithm based on the
	 * "count ray crossing method".
	 * 
	 * @param point Point3D to be tested
	 * 
	 * @return boolean - true if the point is inside the border, false
	 * otherwise.
	 * 
	 * @throws IllegalStateException - if the intersectsInt(Line3D line,
	 * ScalarOperator sop) method of the class Line3D (which computes the
	 * intersection of two lines) called by this method returns a value that is
	 * not -2, -1, 0 or 1.
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
	private boolean isInside(Point3D point) {

		ScalarOperator sop = this.getScalarOperator();

		// x-distances to mbb border
		double dMin = point.getX() - this.getMBB().getPMin().getX();
		double dMax = this.getMBB().getPMax().getX() - point.getX();
		Iterator it;

		if (sop.equal(0, dMax) || sop.equal(0, dMin)) {
			Set set = this.getSAM().intersects(point.getMBB());
			it = set.iterator();
			while (it.hasNext()) {
				TriangleElt3D tri = (TriangleElt3D) it.next();
				if (tri.contains(point, sop))
					return true;
			}
			return false;
		}

		double dist = dMax;

		// construct ray parallel to x-axes and shortest distance to mbb border
		Segment3D ray;
		Point3D to;
		if (dMax <= dMin) {
			to = new Point3D(this.getMBB().getPMax());
		} else {
			to = new Point3D(this.getMBB().getPMin());
			dist = dMin;
		}
		to.setY(point.getY());
		to.setZ(point.getZ());
		ray = new Segment3D(point, to, sop);

		// find closest triangle intersecting with ray
		Set triangles = this.getSAM().intersects(ray.getMBB());
		it = triangles.iterator();
		TriangleElt3D closest = null;
		Point3D pt = null;
		while (it.hasNext()) {
			TriangleElt3D tri = (TriangleElt3D) it.next();
			SimpleGeoObj sgo = tri.intersection(ray, sop);
			if (sgo != null) {
				if (sgo.getType() == SimpleGeoObj.POINT3D) {
					Point3D p = (Point3D) sgo;
					if (p.euclideanDistance(point) <= dist) {
						dist = p.euclideanDistance(point);
						closest = tri;
						pt = p;
					}
				}
			}
		}

		if (closest == null)
			return false;

		if (!closest.containsInBorder(pt, sop)) {
			return isInside(point, pt, closest);
		}
		return isInside(point, closest);
	}

	/*
	 * Checks if a virtual line from <code>point</code> to the center of
	 * <code>tri</code> intersects with other triangles. if not inside/outside
	 * is tested. Returns true if <code>point</code> is inside, false if
	 * outside.
	 * 
	 * @param point Point3D to be tested
	 * 
	 * @param tri TriangleElt3D to be tested
	 * 
	 * @return boolean - inside/outside.
	 * 
	 * @throws IllegalStateException - if the intersectsInt(Line3D line,
	 * ScalarOperator sop) method of the class Line3D (which computes the
	 * intersection of two lines) called by this method returns a value that is
	 * not -2, -1, 0 or 1.
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
	private boolean isInside(Point3D point, TriangleElt3D tri) {

		Point3D center = tri.getCenter();
		Segment3D seg = new Segment3D(point, center, this.getScalarOperator());

		Set triangles = this.getSAM().intersects(seg.getMBB());
		triangles.remove(tri);

		Iterator it = triangles.iterator();
		while (it.hasNext()) {
			TriangleElt3D triangle = (TriangleElt3D) it.next();
			SimpleGeoObj sgo = triangle.intersection(seg, this
					.getScalarOperator());
			if (sgo != null) {
				if (sgo.getType() == SimpleGeoObj.POINT3D) {
					Point3D p = (Point3D) sgo;
					if (p.euclideanDistance(point) < seg.getLength()) {
						return isInside(point, triangle);
					}
				}
			}
		}
		return isInside(point, center, tri);
	}

	/*
	 * Tests if <code>point</code> lies left/right from <code>tri</code>,
	 * inside/outside respectively. <code>p</code> is given point on inner of
	 * <code>tri</code>. normal of tri specifies outside direction. Returns true
	 * if inside, false if outside.
	 * 
	 * @param from Point3D, start of the vector
	 * 
	 * @param to Point3D, end of the vectors
	 * 
	 * @param tri TriangleElt3D to be compared
	 * 
	 * @return boolean - inside/outside.
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private boolean isInside(Point3D from, Point3D to, TriangleElt3D tri) {

		Vector3D pointToTri = new Vector3D(from, to);
		if (pointToTri.scalarproduct(tri.getNormal(this.getScalarOperator())) > 0)
			return true;
		return false;
	}

	/**
	 * Tests if <code>segment</code> is contained.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * 
	 * @return boolean - true if is contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Segment3D)
	 */
	public boolean containsInside(Segment3D seg) {

		for (int i = 0; i < 2; i++)
			if (!this.containsInside(seg.getPoint(i)))
				return false;

		Set triangles = this.getSAM().intersects(seg.getMBB());

		if (!triangles.isEmpty()) {
			Iterator it = triangles.iterator();
			while (it.hasNext()) {
				TriangleElt3D tri = (TriangleElt3D) it.next();
				SimpleGeoObj sgo = tri.intersection(seg, this
						.getScalarOperator());
				if (sgo != null) {

					/*
					 * TODO ClosedHull_containsInside( segment ) totally
					 * contained ? to simply return false here is not correct,
					 * "touches" from inside are possible.
					 * 
					 * Touches/intersections with the inside of the segment
					 * should be checked. It could be also possible that two
					 * nodes of the segment are on the hull but the inside of
					 * the segment is outside.
					 */

					return false;
				}

			}
		}
		return true;
	}

	/**
	 * Tests if <code>triangle</code> is contained.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Triangle3D)
	 */
	public boolean containsInside(Triangle3D triangle) {

		for (int i = 0; i < 3; i++)
			if (!this.containsInside(triangle.getPoint(i)))
				return false;

		// TODO ClosedHull_containsInside( triangle ) totally contained ?
		return true;
	}

	/**
	 * Tests if <code>tetrahedron</code> is contained.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Tetrahedron3D)
	 */
	public boolean containsInside(Tetrahedron3D tetra) {

		for (int i = 0; i < 4; i++)
			if (!this.containsInside(tetra.getPoint(i)))
				return false;

		// TODO ClosedHull_containsInside( tetra ) totally contained ?
		return true;
	}

	/**
	 * Builds the neighbour topology of the net for the given Triangle elements.
	 * 
	 * @param elts
	 *            TriangleElt3D[] triangle elements used to build topology
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void buildNetTopology(TriangleElt3D[] elts) {
		getTNetComp().buildNetTopology(elts);
	}

	/**
	 * Tests whether this contains the given point geometrically.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
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
	public boolean contains(Point3D point) {
		return getTNetComp().contains(point);
	}

	/**
	 * Tests whether this contains the given segment geometrically.<br>
	 * (even if a part (not only a point) of the given segment is contained the
	 * method returns true)
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if is contained, false otherwise.
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
	public boolean contains(Segment3D seg) {
		return getTNetComp().contains(seg);
	}

	/**
	 * Tests whether this contains the given triangle geometrically<br>
	 * (even if a partial area of the given triangle is contained the method
	 * returns true)
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 * @throws IllegalStateExceptoin
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
	public boolean contains(Triangle3D triangle) {
		return getTNetComp().contains(triangle);
	}

	/**
	 * Returns the number of elements in the net
	 * 
	 * @return int - number of elements in the net.
	 */
	public int countElements() {
		return getTNetComp().countElements();
	}

	/**
	 * Returns the number of edges in this component.
	 * 
	 * @return int - number of edges in this component.
	 */
	public int countEdges() {
		return getTNetComp().countEdges();
	}

	/**
	 * Returns the number of faces in this component.
	 * 
	 * @return int - number of faces in this component.
	 */
	public int countFaces() {
		return getTNetComp().countElements();
	}

	/**
	 * Returns the number of vertices in this component.
	 * 
	 * @return int - number of vertices in this component.
	 */
	public int countVertices() {
		return getTNetComp().countVertices();
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
		return getTNetComp().getElementsIterator();
	}

	/**
	 * Returns the TriangleElt3D objects in a Set. This method uses a walk over
	 * the neighbours (NOT THE internal SAM) to retrieve all elements. Use this
	 * method only in case you need to process all the elements.
	 * 
	 * @return Set with TriangleElt3D objects.
	 */
	public Set getElementsViaRecursion() {
		return getTNetComp().getElementsViaRecursion();
	}

	/**
	 * Returns the TriangleElt3D objects in a Set.<br>
	 * This method uses the internal SAM of the component to retrieve all
	 * elements. Use this method in the case you also need spatial tests
	 * afterwards.
	 * 
	 * @return Set with TriangleElt3D objects.
	 */
	public Set getElementsViaSAM() {
		return getTNetComp().getElementsViaSAM();
	}

	/**
	 * Returns the entry element.
	 * 
	 * @return TriangleElt3D - entry element.
	 */
	public TriangleElt3D getEntryElement() {
		return getTNetComp().getEntryElement();
	}

	/**
	 * Computes and returns the Euler number for this component.
	 * 
	 * @return int - Euler number for this.
	 */
	public int getEuler() {
		return getTNetComp().getEuler();
	}

	/**
	 * Returns the mbb of this.
	 * 
	 * @return MBB3D of this.
	 */
	public MBB3D getMBB() {
		return getTNetComp().getMBB();
	}

	/**
	 * Returns the enclosing ClosedHull.
	 * 
	 * @return ClosedHull3D of this.
	 */
	public ClosedHull3D getNet() {
		return backRef;
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
	public Set getPoints() {
		return getTNetComp().getPoints();
	}

	/**
	 * Returns the internal spatial access method object (SAM) to the elements
	 * of the component.
	 * 
	 * @return SAM of this.
	 */
	public SAM getSAM() {
		return getTNetComp().getSAM();
	}

	/**
	 * Returns the ScalarOperator of this.
	 * 
	 * @return ScalarOperator of this.
	 */
	public ScalarOperator getScalarOperator() {
		return getTNetComp().getScalarOperator();
	}

	/**
	 * Returns all <code>Segment3D</code> objects of this in a Set.
	 * 
	 * @return Set of all Segment3D objects of this.
	 */
	public Set getSegments() {
		return getTNetComp().getSegments();
	}

	/**
	 * Returns the type of this as a ComplexGeoObj
	 * 
	 * @return byte - type of this.
	 * @see db3d.dbms.model3d.ComplexGeoObj#getType()
	 */
	public byte getType() {
		return ComplexGeoObj.COMP_CLOSED_HULL_3D;
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
		return getTNetComp().intersects(line);
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
	 */
	public boolean intersects(MBB3D mbb) {
		return getTNetComp().intersects(mbb);
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
	public boolean intersects(Plane3D plane) {
		return getTNetComp().intersects(plane);
	}

	/**
	 * Tests if the component is correctly connected.
	 * 
	 * @return boolean - true if is correctly connected, false otherwise.
	 */
	public boolean isConnected() {
		return getTNetComp().isConnected();
	}

	/**
	 * Tests via algorithm if the component is really closed.
	 * 
	 * @return boolean - true if is closed, false otherwise.
	 */
	public boolean checkClosed() {
		TriangleElt3DIterator it = this.getTNetComp().getElementsIterator();
		while (it.hasNext()) {
			if (it.next().countNeighbours() != 3) {
				this.setClosed(false);
				return false;
			}
		}
		this.setClosed(true);
		return true;
	}

	/*
	 * Sets the parameter closed.
	 * 
	 * @param b boolean to which the parameter closed should be set
	 */
	private void setClosed(boolean b) {
		this.closed = b;
	}

	/**
	 * Tests if the component is closed.
	 * 
	 * @return boolean - true if closed, false otherwise.
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Tests if this component is empty.
	 * 
	 * @return boolean - true if is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return getTNetComp().isEmpty();
	}

	/**
	 * Tests whether the triangle net belonging to this is oriented - does not
	 * include outside direction of normal vectors.
	 * 
	 * @return boolean - true if is oriented, false otherwise.
	 */
	public boolean isOrientationConsistent() {
		return getTNetComp().isOrientationConsistent();
	}

	/**
	 * Tests if the component is correctly oriented - normal vectors point
	 * outside.
	 * 
	 * @return boolean - true if is oriented, false otherwise.
	 */
	public boolean isOriented() {
		return this.oriented;
	}

	/**
	 * Makes this component consistent in the orientation of its elements -
	 * normal vectors point outside.
	 * 
	 * @param sop
	 *            ScalarOperator of this
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void makeOrientationConsistent(ScalarOperator sop) {
		if (!getTNetComp().isOrientationConsistent())
			getTNetComp().makeOrientationConsistent(sop);

		if (!this.isClosed())
			if (!this.checkClosed())
				return;

		// check if outside oriented => sign of volume positive
		double vol = this.calculateSignedVolume();

		if (vol < 0) {
			getTNetComp().invertOrientation();
		}

		this.setOriented(true);
	}

	/**
	 * Sets the oriented flag of the net.
	 * 
	 * @param oriented
	 *            boolean true if oriented
	 */
	public void setOriented(boolean oriented) {
		getTNetComp().setOriented(oriented);
		this.oriented = true;
	}

	/**
	 * Updates the Entry elements after changes in the net component.
	 */
	protected void updateEntryElement() {
		getTNetComp().updateEntryElement();
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
		getTNetComp().updateEulerStatistics();
	}

	/**
	 * Updates the MBB after changes in the net component.
	 * 
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected void updateMBB() {
		getTNetComp().updateMBB();
	}

	// extended methods of the ClosedHull3DComponent

	/*
	 * Calculates a signed volume of this. If sign is negative, the orientation
	 * must be changed.
	 * 
	 * @return double - signed volume of this.
	 */
	private double calculateSignedVolume() {

		if (!this.isClosed()) {
			this.checkClosed();
			return 0;
		}

		// calculate signed volume
		Iterator it = getElementsViaRecursion().iterator();
		double volume = 0;
		Vector3D[] vec = null;
		TriangleElt3D elt = null;

		Vector3D center = this.getMBB().getCenter().getVector();

		while (it.hasNext()) {
			elt = (TriangleElt3D) it.next();
			vec = elt.getVectors();

			Vector3D v0 = Vector3D.add(center, vec[0]);
			Vector3D v1 = Vector3D.add(center, vec[1]);
			Vector3D v2 = Vector3D.add(center, vec[2]);

			volume += v0.spatproduct(v1, v2) * (1.0 / 6.0);
		}

		return volume;

	}

	/**
	 * Calculates volume of this.
	 * 
	 * @return double - volume of this.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public double getVolume() {

		if (!this.isOriented())
			this.makeOrientationConsistent(getScalarOperator());

		return this.calculateSignedVolume();
	}

	protected TriangleNet3DComp getTNetComp() {
		return tNetComp;
	}

	public int getComponentID() {
		return tNetComp.getComponentID();
	}

	protected void setComponentID(int id) {
		tNetComp.setComponentID(id);
	}

	public SimpleGeoObj getElement(int id) {
		return getTNetComp().getElement(id);
	}
}
