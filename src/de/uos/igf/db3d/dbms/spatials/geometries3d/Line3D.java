/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.geometries3d;

import java.io.Serializable;

import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

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
public class Line3D implements Geometry3D, Serializable {

	/* origin point of Line3D */
	protected Point3D origin;

	/* direction vector of Line3D (normalized) */
	protected Vector3D dvec;

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
	 * @param epsilon
	 *            GeoEpsilon for normalization
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Line3D(Point3D p1, Point3D p2, GeoEpsilon epsilon) {
		this.origin = p1;
		this.dvec = new Vector3D(p1, p2);
		this.dvec.normalize(epsilon);
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
	 *            GeoEpsilon for normalization
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Line3D(Point3D p, Vector3D dvec, GeoEpsilon sop) {
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
	 *            GeoEpsilon
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Line3D(Segment3D line, GeoEpsilon sop) {
		this(line.points[0], line.points[1], sop);
	}

	// /**
	// * Returns the origin point of this.
	// *
	// * @return Point3D - origin point.
	// */
	// public Location3D getOrigin() {
	// return this.origin;
	// }

	// /**
	// * Returns the direction vector of this.
	// *
	// * @return Vector3D - direction vector.
	// */
	// public Vector3D getDVector() {
	// return this.dvec;
	// }

	/**
	 * Tests if this is parallel to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Line3D line, GeoEpsilon sop) {
		return (this.dvec.isCollinear(line.dvec, sop) != 0);
	}

	/**
	 * Tests if this is parallel to given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Plane3D plane, GeoEpsilon sop) {
		return plane.isParallel(this, sop);
	}

	/**
	 * Tests if this is skew to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if skew, false otherwise.
	 */
	public boolean isSkew(Line3D line, GeoEpsilon sop) {
		if (this.isParallel(line, sop))
			return false;

		double x = line.origin.x - this.origin.x;
		double y = line.origin.y - this.origin.y;
		double z = line.origin.z - this.origin.z;
		return (!sop.equal(
				this.dvec.spatproduct(line.dvec, new Vector3D(x, y, z)), 0));
	}

	/**
	 * Tests if this is orthogonal to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if orthogonal, false otherwise.
	 */
	public boolean isOrthogonal(Line3D line, GeoEpsilon sop) {
		return this.dvec.isOrthogonal(line.dvec, sop);
	}

	/**
	 * Tests whether the given point is on this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Point3D point, GeoEpsilon sop) {
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
		double x = point.x - this.origin.x;
		double y = point.y - this.origin.y;
		double z = point.z - this.origin.z;
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
	 *            GeoEpsilon
	 * @return double distance.
	 */
	public double distance(Plane3D plane, GeoEpsilon sop) {
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
	 *            GeoEpsilon
	 * @return double - distance.
	 */
	public double distance(Line3D line, GeoEpsilon sop) {
		if (this.isParallel(line, sop)) {
			double x = line.origin.x - this.origin.x;
			double y = line.origin.y - this.origin.y;
			double z = line.origin.z - this.origin.z;

			double top = this.dvec.crossproduct(new Vector3D(x, y, z))
					.getNorm();
			return top / this.dvec.getNorm();
		}
		double x1 = line.origin.x - this.origin.x;
		double y1 = line.origin.y - this.origin.y;
		double z1 = line.origin.z - this.origin.z;
		double value = this.dvec.spatproduct(line.dvec,
				new Vector3D(x1, y1, z1));
		if (!sop.equal(value, 0))
			return Math.abs(value)
					/ this.dvec.crossproduct(line.dvec).getNorm();
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
	 *            GeoEpsilon
	 * @return int - intersection flag.
	 */
	public int intersectsInt(Line3D line, GeoEpsilon sop) {
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
	 *            GeoEpsilon
	 * @return boolean - true if, else otherwise.
	 */
	public boolean intersects(Line3D line, GeoEpsilon sop) {
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
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Line3D,
	 *         Point3D).
	 */
	public Geometry3D intersection(Plane3D plane, GeoEpsilon sop) {
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
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Line3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon sop) method
	 *             returns a value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Line3D line, GeoEpsilon sop) {
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
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
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
	 * @param epsilon
	 *            GeoEpsilon
	 * @return Point3D - result intersection point.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	protected Point3D intersectionOPT(Line3D line, GeoEpsilon epsilon) { // Dag

		Vector3D orthoVec = (this.dvec).crossproduct(line.dvec);
		Point3D p1 = (line.origin.getVector().add(line.dvec)).getAsLocation3D();
		Point3D p2 = (line.origin.getVector().add(orthoVec)).getAsLocation3D();
		// plane orthogonal to both direction vectors and containing line
		Plane3D plane = new Plane3D(line.origin, p1, p2, epsilon);

		/*
		 * Alternative implementation: Idea: searching for the intersection
		 * point of this with the plain that is perpendicular to both direction
		 * vectors and contains the line.
		 */

		Vector3D dvec1 = this.dvec;
		Vector3D origin1 = new Vector3D(this.origin);
		Vector3D help = Vector3D.sub(line.origin.getVector(), origin1);

		double scalar = plane.normvec.scalarproduct(help)
				/ plane.normvec.scalarproduct(dvec1);
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
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon sop) method of
	 *             the class Line3D returns a value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Segment3D segment, GeoEpsilon epsilon) {
		Line3D line = new Line3D(segment, epsilon);
		// test for intersection
		switch (this.intersectsInt(line, epsilon)) {
		case -1:
			return null;
		case -2:
			return null;
		case 1:
			return segment;

		case 0: // they would intersect in space - verify the point
			Point3D point = this.intersectionOPT(line, epsilon);
			if (segment.points[0].isEqual(point, epsilon)) {
				return segment.points[0];
			}
			if (segment.points[1].isEqual(point, epsilon)) {
				return segment.points[1];
			}
			// test if point is on the line segment
			if (segment.containsOnLine(point, epsilon))
				return point;
			else
				return null;

		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
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
		line.dvec = this.dvec.copy();
		line.origin = new Point3D(this.origin);
		return line;
	}

	/**
	 * 
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.LINE;
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

	/**
	 * Tests if this is strictly equal to given Line3D. It is tested if
	 * this.origin is equal to arg.origin and the same for the direction
	 * vectors.
	 * 
	 * @param obj
	 *            Line3D
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if this is strictly equivalent to the given
	 *         Line3D, false otherwise.
	 */
	public boolean isEqual(Line3D obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (dvec.isEqual(obj.dvec, sop) && origin.isEqual(obj.origin, sop))
			return true;
		return false;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Line3D))
			return false;
		return isEqual((Line3D) obj, sop);
	}

	@Override
	public int isEqualHC(int factor) {
		throw new NotYetImplementedException();
	}

	/**
	 * Tests if this is geometry equivalent to given Line3D.
	 * 
	 * @param obj
	 *            Line3D to be tested
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if equivalent, false otherwise.
	 */
	public boolean isGeometryEquivalent(Line3D obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!this.contains(obj.origin, sop))
			return false;
		else {
			if (this.dvec.isCollinear(obj.dvec, sop) == 0)
				return false;
		}
		return true;
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Line3D))
			return false;
		return isGeometryEquivalent((Line3D) obj, sop);
	}

	@Override
	public int isGeometryEquivalentHC(int factor) {
		throw new NotYetImplementedException();
	}

}
