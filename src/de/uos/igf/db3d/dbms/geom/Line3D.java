/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Serializable;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;

/**
 * Class Line3D models a straight line in 3D space. It provides different
 * constructors for instantiation. It also provides different intersection test
 * and computing methods between Line3D and Plane3D and Segment3D objects. The
 * straight line is defined through an origin position vector and a direction
 * vector of type Vector3D.
 * 
 * 
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 * 
 */
@SuppressWarnings("serial")
public class Line3D implements SimpleGeoObj, Serializable {

	/* origin point of Line3D */
	private Point3D origin;

	/* direction vector of Line3D (normalized) */
	private Vector3D dvec;

	/*
	 * Copy Constructor. Is only used by copy method.
	 */
	private Line3D() {
	}

	/**
	 * Constructor.
	 * 
	 * Constructs a Line3D object with origin as p1 and direction vector
	 * computed from p1 and p2 -> P1P2
	 * 
	 * @param p1
	 *            Point3D point1
	 * @param p2
	 *            Point3D point2
	 * @param sop
	 *            ScalarOperator for normalization
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Line3D(Point3D p1, Point3D p2, ScalarOperator sop) {
		this.origin = p1;
		this.dvec = new Vector3D(p1, p2);
		this.dvec.normalize(sop);
	}

	/**
	 * Constructor.
	 * 
	 * Constructs a Line3D object with the given origin Point3D object p and the
	 * given direction vector dvec
	 * 
	 * @param p
	 *            origin point as Point3D
	 * @param dvec
	 *            direction vector as Vector3D
	 * @param sop
	 *            ScalarOperator for normalization
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Line3D(Point3D p, Vector3D dvec, ScalarOperator sop) {
		this.origin = p;
		this.dvec = dvec;
		this.dvec.normalize(sop);
	}

	/**
	 * Constructor.
	 * 
	 * Constructs a Line3D object with the start point of line as origin and the
	 * direction vector computed from end point minus start point.
	 * 
	 * @param line
	 *            Segment3D object
	 * @param sop
	 *            ScalarOperator
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Line3D(Segment3D line, ScalarOperator sop) {
		this(line.getPoint(0), line.getPoint(1), sop);
	}

	/**
	 * Returns the origin point of this.
	 * 
	 * @return Point3D - origin point.
	 */
	public Point3D getOrigin() {
		return this.origin;
	}

	/**
	 * Returns the direction vector of this.
	 * 
	 * @return Vector3D - direction vector.
	 */
	public Vector3D getDVector() {
		return this.dvec;
	}

	/**
	 * Tests if this is parallel to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Line3D line, ScalarOperator sop) {
		return (this.dvec.isCollinear(line.getDVector(), sop) != 0);
	}

	/**
	 * Tests if this is parallel to given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Plane3D plane, ScalarOperator sop) {
		return plane.isParallel(this, sop);
	}

	/**
	 * Tests if this is skew to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if skew, false otherwise.
	 */
	public boolean isSkew(Line3D line, ScalarOperator sop) {
		if (this.isParallel(line, sop))
			return false;

		double x = line.getOrigin().getX() - this.origin.getX();
		double y = line.getOrigin().getY() - this.origin.getY();
		double z = line.getOrigin().getZ() - this.origin.getZ();
		return (!sop.equal(this.dvec.spatproduct(line.getDVector(),
				new Vector3D(x, y, z)), 0));
	}

	/**
	 * Tests if this is orthogonal to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if orthogonal, false otherwise.
	 */
	public boolean isOrthogonal(Line3D line, ScalarOperator sop) {
		return this.dvec.isOrthogonal(line.getDVector(), sop);
	}

	/**
	 * Tests whether the given point is on this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Point3D point, ScalarOperator sop) {
		return sop.equal(this.distance(point), 0);
	}

	/**
	 * Computes the orthogonal distance between this and the given point.
	 * 
	 * @param point
	 *            given Point3D
	 * @return double - distance.
	 */
	public double distance(Point3D point) {
		double x = point.getX() - this.origin.getX();
		double y = point.getY() - this.origin.getY();
		double z = point.getZ() - this.origin.getZ();
		double top = this.dvec.crossproduct(new Vector3D(x, y, z)).getNorm();
		return top / this.dvec.getNorm();
	}

	/**
	 * Computes the distance between this and the given plane.<br>
	 * The method assumes that the line and the plane are PARALLEL.
	 * 
	 * @param plane
	 *            given Plane3D
	 * @param sop
	 *            ScalarOperator
	 * @return double distance.
	 */
	public double distance(Plane3D plane, ScalarOperator sop) {
		return plane.distance(this, sop);
	}

	/**
	 * Computes the distance between this and the given line.<br>
	 * For PARALLEL lines the distance is computed.<br>
	 * For skew lines the smallest distance is computed.<br>
	 * For intersecting lines 0 is returned.<br>
	 * 
	 * @param line
	 *            given Line3D
	 * @param sop
	 *            ScalarOperator
	 * @return double - distance.
	 */
	public double distance(Line3D line, ScalarOperator sop) {
		if (this.isParallel(line, sop)) {
			double x = line.getOrigin().getX() - this.origin.getX();
			double y = line.getOrigin().getY() - this.origin.getY();
			double z = line.getOrigin().getZ() - this.origin.getZ();

			double top = this.dvec.crossproduct(new Vector3D(x, y, z))
					.getNorm();
			return top / this.dvec.getNorm();
		}
		double x1 = line.getOrigin().getX() - this.origin.getX();
		double y1 = line.getOrigin().getY() - this.origin.getY();
		double z1 = line.getOrigin().getZ() - this.origin.getZ();
		double value = this.dvec.spatproduct(line.getDVector(), new Vector3D(
				x1, y1, z1));
		if (!sop.equal(value, 0))
			return Math.abs(value)
					/ this.dvec.crossproduct(line.getDVector()).getNorm();
		else
			return 0;
	}

	/**
	 * Tests for intersection between this and the given line.<br>
	 * -2 no intersection (skew); -1 no intersection (parallel); 0 intersection
	 * (0D - Point3D); 1 intersection (1D - Line (=equal))
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return int - intersection flag.
	 */
	public int intersectsInt(Line3D line, ScalarOperator sop) {
		if (this.isParallel(line, sop)) {
			if (sop.equal(this.distance(line, sop), 0))
				return 1;
			else
				return -1;
		}

		if (this.isSkew(line, sop))
			return -2;
		else
			return 0;
	}

	/**
	 * Tests for intersection between this and the given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if, else otherwise.
	 */
	public boolean intersects(Line3D line, ScalarOperator sop) {
		if (this.intersectsInt(line, sop) < 0)
			return false;
		else
			return true;
	}

	/**
	 * Computes the intersection between this and the given plane.<br>
	 * Returns this if the line and the plane are parallel and their distance is
	 * equal 0.<br>
	 * Returns null if the line and the plane are parallel and their distance is
	 * ! = 0.<br>
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param plane
	 *            Plane3D for intersection
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Line3D,
	 *         Point3D).
	 */
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) {
		return plane.intersection(this, sop);
	}

	/**
	 * Computes the intersection between this and the given line.<br>
	 * Returns this if the two lines are parallel and their distance is equal 0.<br>
	 * Returns null if the two lines are parallel and their distance is ! = 0.<br>
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param line
	 *            Line3D for intersection
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Line3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method returns a value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) {
		// test for intersection
		switch (this.intersectsInt(line, sop)) {
		case -1:
			return null;
		case -2:
			return null;
		case 0:
			return this.intersectionOPT(line, sop);
		case 1:
			return this;
		default:
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.failureinterint"));
		}
	}

	/**
	 * Computes the intersection between this and the given line.<br>
	 * This is an OPTIMIZED version which performs no tests for parallel,
	 * skew... If you know the two lines will intersect, use this version. The
	 * combination of intersects test with this method will have the same
	 * runtime performance as the normal intersection method. <br>
	 * RETURNS a Point3D object - if the lines do not intersect, the result will
	 * be undefined!
	 * 
	 * @param line
	 *            Line3D for intersection
	 * @param sop
	 *            ScalarOperator
	 * @return Point3D - result intersection point.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	protected Point3D intersectionOPT(Line3D line, ScalarOperator sop) { // Dag

		Point3D lineOrigin = line.getOrigin();
		Vector3D lineDVec = line.getDVector();

		Vector3D orthoVec = (this.getDVector()).crossproduct(lineDVec);
		Point3D p1 = (lineOrigin.getVector().add(lineDVec)).getAsPoint3D();
		Point3D p2 = (lineOrigin.getVector().add(orthoVec)).getAsPoint3D();
		// plane orthogonal to both direction vectors and containing line
		Plane3D plane = new Plane3D(lineOrigin, p1, p2, sop);

		/*
		 * Alternative implementation: Idea: searching for the intersection
		 * point of this with the plain that is perpendicular to both direction
		 * vectors and contains the line.
		 */

		Vector3D dvec1 = this.getDVector();
		Vector3D origin1 = new Vector3D(this.getOrigin());
		Vector3D help = Vector3D.sub(lineOrigin.getVector(), origin1);

		double scalar = plane.getNormalVector().scalarproduct(help)
				/ plane.getNormalVector().scalarproduct(dvec1);
		Vector3D intersection = origin1.add(dvec1.copy().mult(scalar));

		return intersection.getAsPoint3D();
	}

	/**
	 * Computes the intersection between this and the given Segment3D. <br>
	 * Returns the Segment3D if this and the segment are parallel and their
	 * distance is equal 0. <br>
	 * Returns null if this and the segment are parallel and their distance is !
	 * = 0 or they are skew, or they not intersect.
	 * 
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param segment
	 *            Segment3D for intersection
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Segment3D segment, ScalarOperator sop) {
		Line3D line = new Line3D(segment, sop);
		// test for intersection
		switch (this.intersectsInt(line, sop)) {
		case -1:
			return null;
		case -2:
			return null;
		case 1:
			return segment;

		case 0: // they would intersect in space - verify the point
			Point3D point = this.intersectionOPT(line, sop);
			if (segment.getPoint(0).isEqual(point, sop)) {
				return segment.getPoint(0);
			}
			if (segment.getPoint(1).isEqual(point, sop)) {
				return segment.getPoint(1);
			}
			// test if point is on the line segment
			if (segment.containsOnLine(point, sop))
				return point;
			else
				return null;

		default:
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.failureinterint"));
		}
	}

	/**
	 * Copies this.
	 * 
	 * @return Line3D - deep copy of this.
	 */
	public Line3D copy() {
		Line3D line = new Line3D();
		line.dvec = this.getDVector().copy();
		line.origin = new Point3D(this.getOrigin());
		return line;
	}

	/**
	 * 
	 */
	public byte getType() {
		return SimpleGeoObj.LINE3D;
	}

	/**
	 * Returns a MBB from NEGATIV_INFINITY to POSITIVE_INFINITY.
	 * 
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D getMBB() {
		return new MBB3D(new Point3D(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
				new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
						Double.POSITIVE_INFINITY));
	}

	/**
	 * Tests if this is strictly equal to given Line3D. It is tested if
	 * this.origin is equal to arg.origin and the same for the direction
	 * vectors.
	 * 
	 * @param obj
	 *            Line3D
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if this is strictly equivalent to the given
	 *         Line3D, false otherwise.
	 */
	public boolean isEqual(Line3D obj, ScalarOperator sop) {
		if (!this.origin.isEqual(obj.getOrigin(), sop))
			return false;
		if (!this.dvec.isEqual(obj.getDVector(), sop))
			return false;
		return true;
	}

	/**
	 * Tests if this is geometry equivalent to given Line3D.
	 * 
	 * @param obj
	 *            Line3D to be tested
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equivalent, false otherwise.
	 */
	public boolean isGeometryEquivalent(Line3D obj, ScalarOperator sop) {
		if (!this.contains(obj.getOrigin(), sop))
			return false;
		else {
			if (this.dvec.isCollinear(obj.getDVector(), sop) == 0)
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Line3D [dvec=" + dvec + ", origin=" + origin + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dvec == null) ? 0 : dvec.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		return result;
	}

}
