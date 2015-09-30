/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.geometries3d;

import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * Class Plane3D models an infinite plane in 3D space.<br>
 * The plane is defined through a position vector and the normal vector of the
 * plane. <br>
 * 
 * <pre>
 *  - &gt;     -&gt;     -&gt;  &lt;br&gt; 
 *  n *  ( r(P) -  r1 ) = 0&lt;br&gt;
 * </pre>
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Plane3D extends Geometry3DAbst {

	/* the normalized normal vector of the plane */
	protected Vector3D normvec;

	/* the position vector for the plane equation */
	protected Vector3D posvec;

	/* d parameter is distance to origin */
	protected double d;

	/*
	 * Constructor.
	 * 
	 * Is only used in the copy method
	 */
	private Plane3D() {
	}

	/**
	 * Constructor.<br>
	 * Constructs a plane object for the given norm vector and position point
	 * 
	 * @param norm
	 *            normal vector of plane as Vector3D
	 * @param point
	 *            position vector for a point on the plane
	 * @param epsilon
	 *            GeoEpsilon - used for normalization of norm vector
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Plane3D(Vector3D norm, Vector3D point, GeoEpsilon epsilon) {
		this.normvec = norm;
		this.normvec.normalize(epsilon);
		this.posvec = point;
		this.d = computeD();
	}

	/**
	 * Constructor.<br>
	 * Constructs a plane object defined through the given three Point3D
	 * objects. The points have to define a plane - so they should not lie on a
	 * common line.
	 * 
	 * @param p1
	 *            point 1
	 * @param p2
	 *            point 2
	 * @param p3
	 *            point 3
	 * @param epsilon
	 *            GeoEpsilon - used for normalization of norm vector
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Plane3D(Point3D p1, Point3D p2, Point3D p3, GeoEpsilon epsilon) {
		this.normvec = buildNormVector(p1, p2, p3);
		this.normvec.normalize(epsilon);
		this.posvec = new Vector3D(p1);
		this.d = computeD();
	}

	/**
	 * Constructor.<br>
	 * Constructs a plane object defined through the given Triangle3D.
	 * 
	 * @param triangle
	 *            triangle of type Triangle3D
	 * @param sop
	 *            GeoEpsilon
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Plane3D(Triangle3D triangle, GeoEpsilon sop) {
		this.normvec = triangle.getNormal(sop);
		this.normvec.normalize(sop);
		this.posvec = new Vector3D(triangle.points[0]);
		this.d = computeD();
	}

	/*
	 * Computes the normal vector for the given vectors defined by the three
	 * given points - P1P2 and P1P3
	 * 
	 * @param p1 Point3D
	 * 
	 * @param p2 Point3D
	 * 
	 * @param p3 Point3D
	 * 
	 * @return Vector3D - normal vector
	 */
	private Vector3D buildNormVector(Point3D p1, Point3D p2, Point3D p3) {
		double x = p1.y * (p2.z - p3.z) + p2.y * (p3.z - p1.z) + p3.y
				* (p1.z - p2.z);
		double y = p1.z * (p2.x - p3.x) + p2.z * (p3.x - p1.x) + p3.z
				* (p1.x - p2.x);
		double z = p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x
				* (p1.y - p2.y);
		return new Vector3D(x, y, z);
	}

	/*
	 * Computes the d value of the plane equation.
	 * 
	 * @return double - d value.
	 */
	private double computeD() {
		return Math.abs((this.posvec.x * this.normvec.x)
				+ (this.posvec.y * this.normvec.y)
				+ (this.posvec.z * this.normvec.z));
	}

	// /**
	// * Returns the normal vector of this.
	// *
	// * @return Vector3D - normal vector.
	// */
	// public Vector3D getNormalVector() {
	// return this.normvec;
	// }
	//
	// /**
	// * Returns the position vector of this.
	// *
	// * @return Vector3D - position vector.
	// */
	// public Vector3D getPositionVector() {
	// return this.posvec;
	// }
	//
	// /**
	// * Returns the d parameter of the plane equation.
	// *
	// * @return double - d parameter.
	// */
	// public double getDParameter() {
	// return d;
	// }

	/**
	 * Tests whether this is parallel to given plane or not.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Plane3D plane, GeoEpsilon epsilon) {
		return (this.normvec.isCollinear(plane.normvec, epsilon) != 0);
	}

	/**
	 * Tests whether this is parallel to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Line3D line, GeoEpsilon epsilon) {
		return epsilon.equal(this.normvec.scalarproduct(line.dvec), 0);
	}

	/**
	 * Tests whether this is orthogonal to given plane or not.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if orthogonal, false otherwise.
	 */
	public boolean isOrthogonal(Plane3D plane, GeoEpsilon epsilon) {
		return this.normvec.isOrthogonal(plane.normvec, epsilon);
	}

	/**
	 * Tests for intersection between this and the given plane.<br>
	 * -1 no intersection; 1 intersection (1D - Line3D); 2 intersection (2D-
	 * Plane (=equal)).
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - intersection flag.
	 */
	public int intersectsInt(Plane3D plane, GeoEpsilon epsilon) {
		if (!isParallel(plane, epsilon))
			return 1;
		if (epsilon.equal(this.d, plane.d))
			return 2;
		return -1;
	}

	/**
	 * Tests for intersection between this and the given plane.<br>
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane, GeoEpsilon epsilon) {
		if (this.intersectsInt(plane, epsilon) < 0)
			return false;
		else
			return true;
	}

	/**
	 * Tests for intersection between this and the given line.<br>
	 * -1 no intersection; 0 intersection (0D - Point3D); 1 intersection (1D -
	 * Line3D (=on plane)).
	 * 
	 * @param line
	 *            Line3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - intersection flag.
	 */
	public int intersectsInt(Line3D line, GeoEpsilon epsilon) {
		// if parallel, return 0;
		if (this.isParallel(line, epsilon)) {
			if (epsilon.equal(this.distance(line, epsilon), 0))
				return 1;
			else
				return -1;
		}
		return 0;
	}

	/**
	 * Tests for intersection between this and the given line.<br>
	 * 
	 * @param line
	 *            Line3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if intersect, false otherwise.
	 */
	public boolean intersects(Line3D line, GeoEpsilon epsilon) {
		if (this.intersectsInt(line, epsilon) < 0)
			return false;
		else
			return true;
	}

	/**
	 * Tests for intersection between this and the given line segment.<br>
	 * -1 no intersection; 0 intersection (0D - Point3D); 1 intersection (1D -
	 * Segment3D (on plane)).
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - intersection flag.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int intersectsInt(Segment3D segment, GeoEpsilon epsilon) {
		Geometry3D result = this.intersection(new Line3D(segment, epsilon),
				epsilon);
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.LINE)
			return 1;
		else { // result is Point3D
			if (segment.containsOnLine((Point3D) result, epsilon))
				return 0;
			else
				return -1;
		}
	}

	/**
	 * Tests for intersection between this and the given line segment.<br>
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if intersect, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Segment3D segment, GeoEpsilon sop) {
		if (intersectsInt(segment, sop) < 0)
			return false;
		else
			return true;
	}

	/**
	 * Computes the distance between this and the given line.<br>
	 * If the line and plane are PARALLEL, the distance is computed.<br>
	 * If the line and plane intersect, the distance 0 is returned.
	 * 
	 * @param line
	 *            given Line3D
	 * @param epsilon
	 *            GeoEpsilon
	 * @return double - distance.
	 */
	public double distance(Line3D line, GeoEpsilon epsilon) {
		if (!this.isParallel(line, epsilon))
			return 0;

		Point3D pos = line.origin;
		double x = pos.x - this.posvec.x;
		double y = pos.y - this.posvec.y;
		double z = pos.z - this.posvec.z;
		return Math.abs(this.normvec.scalarproduct(new Vector3D(x, y, z)))
				/ this.normvec.getNorm();
	}

	/**
	 * Computes the distance between this and the given plane.<br>
	 * The method assumes that the planes are PARALLEL.
	 * 
	 * @param plane
	 *            given Plane3D
	 * @return double - distance.
	 */
	public double distance(Plane3D plane) {
		Vector3D pos2pos1 = Vector3D.sub(plane.posvec, this.posvec);
		return Math.abs(this.normvec.scalarproduct(pos2pos1))
				/ this.normvec.getNorm();
	}

	/**
	 * Computes the distance between this and the given point.
	 * 
	 * @param point
	 *            Point3D
	 */
	public double distance(Point3D point) {
		double x = point.x - this.posvec.x;
		double y = point.y - this.posvec.y;
		double z = point.z - this.posvec.z;
		return Math.abs(this.normvec.scalarproduct(new Vector3D(x, y, z)))
				/ this.normvec.getNorm();
	}

	/**
	 * Computes the intersection between this and the given plane.<br>
	 * Returns this if the planes are parallel and their distance is equal 0.<br>
	 * Returns null if the planes are parallel and their distance is != 0.<br>
	 * Returns a Line3D if they intersect.
	 * 
	 * @param plane
	 *            Plane3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Plane3D,
	 *         Line3D).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Plane3D plane, GeoEpsilon epsilon) {
		// if parallel, return null;
		if (this.isParallel(plane, epsilon)) {
			if (epsilon.equal(this.distance(plane), 0))
				return this;
			else
				return null;
		}

		// n1 = this.normvector ; n2 = plane.normvector
		Vector3D q1 = this.posvec;
		Vector3D q2 = plane.posvec;

		Vector3D n1 = this.normvec.copy();
		// normalvector of this plane

		Vector3D n2 = plane.normvec.copy();
		// normalvector of the argument plane

		Vector3D n3 = n1.crossproduct(n2); // direction vector of resulting line

		double c1 = q1.scalarproduct(n1);
		double c2 = q2.scalarproduct(n2);
		double c3 = q1.scalarproduct(n3);

		Matrix3x3 matrixA = new Matrix3x3(n1, n2, n3);

		double determinateD = matrixA.computeDeterminante();
		if (epsilon.equal(determinateD, 0)) {
			// FIXME Should we really stop if determinate is equal 0, what does
			// it mean for the algorithm?
			throw new ArithmeticException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.detequalzero"));
		}

		double determinateD1 = new Matrix3x3(new double[] { c1, n1.y, n1.z, c2,
				n2.y, n2.z, c3, n3.y, n3.z }).computeDeterminante();
		double determinateD2 = new Matrix3x3(new double[] { n1.x, c1, n1.z,
				n2.x, c2, n2.z, n3.x, c3, n3.z }).computeDeterminante();
		double determinateD3 = new Matrix3x3(new double[] { n1.x, n1.y, c1,
				n2.x, n2.y, c2, n3.x, n3.y, c3 }).computeDeterminante();

		double qx = determinateD1 / determinateD;
		double qy = determinateD2 / determinateD;
		double qz = determinateD3 / determinateD;

		return new Line3D(new Point3D(qx, qy, qz), n3, epsilon);
	}

	/**
	 * Computes the intersection between this and the given line.<br>
	 * Returns the line if the line and the plane are parallel and their
	 * distance is equal 0.<br>
	 * Returns null if the line and the plane are parallel and their distance is
	 * ! = 0.<br>
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param line
	 *            Line3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Line3D,
	 *         Point3D).
	 */
	public Geometry3D intersection(Line3D line, GeoEpsilon epsilon) {

		// if parallel, return null;
		if (this.isParallel(line, epsilon)) {
			if (epsilon.equal(this.distance(line, epsilon), 0))
				return line;
			else
				return null;
		}

		Vector3D dvec = line.dvec;
		Vector3D origin = new Vector3D(line.origin);
		Vector3D help = Vector3D.sub(this.posvec, origin);
		double scalar = this.normvec.scalarproduct(help)
				/ this.normvec.scalarproduct(dvec);
		Vector3D intersection = origin.add(dvec.copy().mult(scalar));

		return intersection.getAsPoint3D();
	}

	/**
	 * Computes the intersection between this and the given segment.<br>
	 * Returns the segment if the Segment3D and the Plane3D are parallel and
	 * their distance is equal 0.<br>
	 * Returns null if they are parallel and their distance is ! = 0 or they
	 * don't intersect.<br>
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param segment
	 *            Segment3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Segment3D segment, GeoEpsilon epsilon) {

		Geometry3D result = this.intersection(new Line3D(segment, epsilon),
				epsilon);
		if (result == null)
			return null;
		if (result.getGeometryType() == GEOMETRYTYPES.LINE)
			return segment;
		else { // result is Point3D
			if (segment.containsOnLine((Point3D) result, epsilon))
				return result;
			else
				return null;
		}
	}

	/**
	 * Copies the Plane3D.
	 * 
	 * @return Plane3D - deep copy of this.
	 */
	public Plane3D copy() {
		Plane3D p = new Plane3D();
		p.normvec = this.normvec.copy();
		p.posvec = this.posvec.copy();
		p.d = this.d;
		return p;
	}

	/**
	 * @see Spatial.dbms.geom.SimpleGeoObjectIDTypeDef#getGeometryType()
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.PLANE;
	}

	/**
	 * Returns a MBB from NEGATIV_INFINITY to POSITIVE_INFINITY
	 * 
	 * @return MBB3D of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @see Spatial.dbms.structure.GeoObject#getMBB()
	 */
	public MBB3D getMBB() {
		return new MBB3D(new Point3D(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
				new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
						Double.POSITIVE_INFINITY));
	}

	/**
	 * Tests if the given point is contained in the plane.
	 * 
	 * @param point
	 *            Point3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Point3D point, GeoEpsilon epsilon) {
		if (epsilon.equal(this.distance(point), 0))
			return true;
		else
			return false;
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Plane3D [d=" + d + ", normvec=" + normvec + ", posvec="
				+ posvec + "]";
	}

	@Override
	/**
	 * Returns the hash code of this.
	 * @return int - hash code of this.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(d);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((normvec == null) ? 0 : normvec.hashCode());
		result = prime * result + ((posvec == null) ? 0 : posvec.hashCode());
		return result;
	}

	// @Override
	// /**
	// * Tests if this is equal to the given object.
	// * @param obj Object for test
	// * @return boolean - true if equal, false otherwise.
	// */
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// Plane3D other = (Plane3D) obj;
	// if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
	// return false;
	// if (normvec == null) {
	// if (other.normvec != null)
	// return false;
	// } else if (!normvec.equals(other.normvec))
	// return false;
	// if (posvec == null) {
	// if (other.posvec != null)
	// return false;
	// } else if (!posvec.equals(other.posvec))
	// return false;
	// return true;
	// }

	/**
	 * Tests if this is strict equal to given Plane3D.
	 * 
	 * @param obj
	 *            Plane3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean isEqual(Plane3D obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!this.normvec.isEqual(obj.normvec, sop))
			return false;
		if (!this.posvec.isEqual(obj.posvec, sop))
			return false;
		return true;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Plane3D))
			return false;
		return isEqual((Plane3D) obj, sop);
	}

	@Override
	public int isEqualHC(int factor) {
		throw new NotYetImplementedException();
	}

	/**
	 * Tests if this is geometry equivalent to given Plane3D.
	 * 
	 * @param obj
	 *            Plane3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if equivalent, false otherwise.
	 */
	public boolean isGeometryEquivalent(Plane3D obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (this.normvec.isCollinear(obj.normvec, sop) == 0)
			return false;
		else {
			if (!sop.equal(this.d, obj.d)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Plane3D))
			return false;
		return isEqual((Plane3D) obj, sop);
	}

	@Override
	public int isGeometryEquivalentHC(int factor) {
		throw new NotYetImplementedException();
	}

}
