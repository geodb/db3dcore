/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Serializable;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;

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
@SuppressWarnings("serial")
public class Plane3D implements SimpleGeoObj, Serializable {

	/* the normalized normal vector of the plane */
	private Vector3D normvec;

	/* the position vector for the plane equation */
	private Vector3D posvec;

	/* d parameter is distance to origin */
	private double d;

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
	 * @param sop
	 *            ScalarOperator - used for normalization of norm vector
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Plane3D(Vector3D norm, Vector3D point, ScalarOperator sop) {
		this.normvec = norm;
		this.normvec.normalize(sop);
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
	 * @param sop
	 *            ScalarOperator - used for normalization of norm vector
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Plane3D(Point3D p1, Point3D p2, Point3D p3, ScalarOperator sop) {
		this.normvec = buildNormVector(p1, p2, p3);
		this.normvec.normalize(sop);
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
	 *            ScalarOperator
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Plane3D(Triangle3D triangle, ScalarOperator sop) {
		this.normvec = triangle.getNormal(sop);
		this.normvec.normalize(sop);
		this.posvec = new Vector3D(triangle.getPoint(0));
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
		double x = p1.getY() * (p2.getZ() - p3.getZ()) + p2.getY()
				* (p3.getZ() - p1.getZ()) + p3.getY() * (p1.getZ() - p2.getZ());
		double y = p1.getZ() * (p2.getX() - p3.getX()) + p2.getZ()
				* (p3.getX() - p1.getX()) + p3.getZ() * (p1.getX() - p2.getX());
		double z = p1.getX() * (p2.getY() - p3.getY()) + p2.getX()
				* (p3.getY() - p1.getY()) + p3.getX() * (p1.getY() - p2.getY());
		return new Vector3D(x, y, z);
	}

	/*
	 * Computes the d value of the plane equation.
	 * 
	 * @return double - d value.
	 */
	private double computeD() {
		return Math.abs((posvec.getX() * normvec.getX())
				+ (posvec.getY() * normvec.getY())
				+ (posvec.getZ() * normvec.getZ()));
	}

	/**
	 * Returns the normal vector of this.
	 * 
	 * @return Vector3D - normal vector.
	 */
	public Vector3D getNormalVector() {
		return this.normvec;
	}

	/**
	 * Returns the position vector of this.
	 * 
	 * @return Vector3D - position vector.
	 */
	public Vector3D getPositionVector() {
		return this.posvec;
	}

	/**
	 * Returns the d parameter of the plane equation.
	 * 
	 * @return double - d parameter.
	 */
	public double getDParameter() {
		return d;
	}

	/**
	 * Tests whether this is parallel to given plane or not.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Plane3D plane, ScalarOperator sop) {
		return (this.normvec.isCollinear(plane.getNormalVector(), sop) != 0);
	}

	/**
	 * Tests whether this is parallel to given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if parallel, false otherwise.
	 */
	public boolean isParallel(Line3D line, ScalarOperator sop) {
		return sop.equal(this.normvec.scalarproduct(line.getDVector()), 0);
	}

	/**
	 * Tests whether this is orthogonal to given plane or not.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if orthogonal, false otherwise.
	 */
	public boolean isOrthogonal(Plane3D plane, ScalarOperator sop) {
		return this.normvec.isOrthogonal(plane.getNormalVector(), sop);
	}

	/**
	 * Tests for intersection between this and the given plane.<br>
	 * -1 no intersection; 1 intersection (1D - Line3D); 2 intersection (2D-
	 * Plane (=equal)).
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return int - intersection flag.
	 */
	public int intersectsInt(Plane3D plane, ScalarOperator sop) {
		if (!isParallel(plane, sop))
			return 1;
		if (sop.equal(this.d, plane.getDParameter()))
			return 2;
		return -1;
	}

	/**
	 * Tests for intersection between this and the given plane.<br>
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane, ScalarOperator sop) {
		if (this.intersectsInt(plane, sop) < 0)
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
	 * @param sop
	 *            ScalarOperator
	 * @return int - intersection flag.
	 */
	public int intersectsInt(Line3D line, ScalarOperator sop) {
		// if parallel, return 0;
		if (this.isParallel(line, sop)) {
			if (sop.equal(this.distance(line, sop), 0))
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersect, false otherwise.
	 */
	public boolean intersects(Line3D line, ScalarOperator sop) {
		if (this.intersectsInt(line, sop) < 0)
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
	 * @param sop
	 *            ScalarOperator
	 * @return int - intersection flag.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Segment3D segment, ScalarOperator sop) {
		SimpleGeoObj result = this.intersection(new Line3D(segment, sop), sop);
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.LINE3D)
			return 1;
		else { // result is Point3D
			if (segment.containsOnLine((Point3D) result, sop))
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
	 *            ScalarOperator
	 * @return boolean - true if intersect, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Segment3D segment, ScalarOperator sop) {
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
	 * @param sop
	 *            ScalarOperator
	 * @return double - distance.
	 */
	public double distance(Line3D line, ScalarOperator sop) {
		if (!this.isParallel(line, sop))
			return 0;

		Point3D pos = line.getOrigin();
		double x = pos.getX() - this.posvec.getX();
		double y = pos.getY() - this.posvec.getY();
		double z = pos.getZ() - this.posvec.getZ();
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
		Vector3D pos2pos1 = Vector3D
				.sub(plane.getPositionVector(), this.posvec);
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
		double x = point.getX() - this.posvec.getX();
		double y = point.getY() - this.posvec.getY();
		double z = point.getZ() - this.posvec.getZ();
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Plane3D,
	 *         Line3D).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) {
		// if parallel, return null;
		if (this.isParallel(plane, sop)) {
			if (sop.equal(this.distance(plane), 0))
				return this;
			else
				return null;
		}

		// n1 = this.normvector ; n2 = plane.normvector
		Vector3D q1 = this.getPositionVector();
		Vector3D q2 = plane.getPositionVector();

		Vector3D n1 = this.getNormalVector().copy();
		// normalvector of this plane

		Vector3D n2 = plane.getNormalVector().copy();
		// normalvector of the argument plane

		Vector3D n3 = n1.crossproduct(n2); // direction vector of resulting line

		double c1 = q1.scalarproduct(n1);
		double c2 = q2.scalarproduct(n2);
		double c3 = q1.scalarproduct(n3);

		Matrix3x3 matrixA = new Matrix3x3(n1, n2, n3);

		double determinateD = matrixA.computeDeterminante();
		if (sop.equal(determinateD, 0)) {
			// FIXME Should we really stop if determinate is equal 0, what does
			// it mean for the algorithm?
			throw new ArithmeticException(Db3dSimpleResourceBundle
					.getString("db3d.geom.detequalzero"));
		}

		double determinateD1 = new Matrix3x3(new double[] { c1, n1.getY(),
				n1.getZ(), c2, n2.getY(), n2.getZ(), c3, n3.getY(), n3.getZ() })
				.computeDeterminante();
		double determinateD2 = new Matrix3x3(new double[] { n1.getX(), c1,
				n1.getZ(), n2.getX(), c2, n2.getZ(), n3.getX(), c3, n3.getZ() })
				.computeDeterminante();
		double determinateD3 = new Matrix3x3(new double[] { n1.getX(),
				n1.getY(), c1, n2.getX(), n2.getY(), c2, n3.getX(), n3.getY(),
				c3 }).computeDeterminante();

		double qx = determinateD1 / determinateD;
		double qy = determinateD2 / determinateD;
		double qz = determinateD3 / determinateD;

		return new Line3D(new Point3D(qx, qy, qz), n3, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Line3D,
	 *         Point3D).
	 */
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) {

		// if parallel, return null;
		if (this.isParallel(line, sop)) {
			if (sop.equal(this.distance(line, sop), 0))
				return line;
			else
				return null;
		}

		Vector3D dvec = line.getDVector();
		Vector3D origin = new Vector3D(line.getOrigin());
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Segment3D segment, ScalarOperator sop) {

		SimpleGeoObj result = this.intersection(new Line3D(segment, sop), sop);
		if (result == null)
			return null;
		if (result.getType() == SimpleGeoObj.LINE3D)
			return segment;
		else { // result is Point3D
			if (segment.containsOnLine((Point3D) result, sop))
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
		p.normvec = this.getNormalVector().copy();
		p.posvec = this.getPositionVector().copy();
		p.d = this.getDParameter();
		return p;
	}

	/**
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.PLANE3D;
	}

	/**
	 * Returns a MBB from NEGATIV_INFINITY to POSITIVE_INFINITY
	 * 
	 * @return MBB3D of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @see db3d.dbms.structure.GeoObj#getMBB()
	 */
	public MBB3D getMBB() {
		return new MBB3D(new Point3D(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
				new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
						Double.POSITIVE_INFINITY));
	}

	/**
	 * Tests if this is strict equal to given Plane3D.
	 * 
	 * @param obj
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean isEqual(Plane3D obj, ScalarOperator sop) {
		if (!this.normvec.isEqual(obj.getNormalVector(), sop))
			return false;
		if (!this.posvec.isEqual(obj.getPositionVector(), sop))
			return false;
		return true;
	}

	/**
	 * Tests if this is geometry equivalent to given Plane3D.
	 * 
	 * @param obj
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equivalent, false otherwise.
	 */
	public boolean isGeometryEquivalent(Plane3D obj, ScalarOperator sop) {
		if (this.normvec.isCollinear(obj.getNormalVector(), sop) == 0)
			return false;
		else {
			if (!sop.equal(this.d, obj.getDParameter())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if the given point is contained in the plane.
	 * 
	 * @param point
	 *            Point3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean contains(Point3D point, ScalarOperator sop) {
		if (sop.equal(this.distance(point), 0))
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

	@Override
	/**
	 * Tests if this is equal to the given object.
	 * @param obj Object for test
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Plane3D other = (Plane3D) obj;
		if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
			return false;
		if (normvec == null) {
			if (other.normvec != null)
				return false;
		} else if (!normvec.equals(other.normvec))
			return false;
		if (posvec == null) {
			if (other.posvec != null)
				return false;
		} else if (!posvec.equals(other.posvec))
			return false;
		return true;
	}

}
