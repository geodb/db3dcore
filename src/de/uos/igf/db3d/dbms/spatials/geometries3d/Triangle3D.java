/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.geometries3d;

import de.uos.igf.db3d.dbms.exceptions.ValidationException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * Triangle3D is the geometric representation of a triangle in 3D. <br>
 * A Triangle3D object is modeled by 3 Point3D objects. The points are stored in
 * the variables zero, one and two. For orientation of a triangle in 3D the
 * normal vector is computed from the points in an ascending index order or
 * equivalent direction ( e.g. 0->1->2 or 2->0->1). t
 * 
 * @author Wolfgang Baer - Dag Hammerich / University of Osnabrueck
 */
public class Triangle3D extends Geometry3DAbst {

	// /* array of points */
	protected Point3D[] points = null;

	/* normal vector (normalized) - transient */
	protected Vector3D normvec = null;

	/**
	 * Constructor.
	 * 
	 * @param points
	 *            Point3D array
	 * @param epsilon
	 *            GeoEpsilon, needed for validation. If GeoEpsilon is
	 *            <code>null</code>, no validation will occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3D(Point3D[] points, GeoEpsilon epsilon)
			throws IllegalArgumentException {
		if (points == null || points.length != 3)
			throw new IllegalArgumentException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.trionlythree"));

		this.points = points;
		this.normvec = null;

		// validate
		if (epsilon != null) {
			if (!isValid(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotval"));
			if (!isRegular(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotreg"));
			if (!isBeautiful(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotbeau"));
		}

	}

	/**
	 * Constructor.
	 * 
	 * @param p1
	 *            Point3D 1
	 * @param p2
	 *            Point3D 2
	 * @param p3
	 *            Point3D 3
	 * @param epsilon
	 *            GeoEpsilon, needed for validation. If GeoEpsilon is
	 *            <code>null</code>, no validation will occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if validation fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3D(Point3D p1, Point3D p2, Point3D p3, GeoEpsilon epsilon) {
		this.points = new Point3D[] { p1, p2, p3 };
		this.normvec = null;

		// validate
		if (epsilon != null) {
			if (!isValid(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotval"));
			if (!isRegular(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotreg"));
			if (!isBeautiful(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotbeau"));
		}
	}

	/**
	 * Constructor.<br>
	 * The given point must not intersect with the segment.
	 * 
	 * @param p
	 *            Point3D
	 * @param segment
	 *            Segment3D
	 * @param epsilon
	 *            GeoEpsilon, needed for validation. If GeoEpsilon is
	 *            <code>null</code>, no validation will occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3D(Point3D p, Segment3D segment, GeoEpsilon epsilon) {
		this(p, segment.points[0], segment.points[1], epsilon);
	}

	/**
	 * Copy constructor.<br>
	 * Makes a deep copy with also copied points.
	 * 
	 * @param tr
	 *            Triangle3D to be copied
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
	public Triangle3D(Triangle3D tr) {
		this(new Point3D(tr.points[0]), new Point3D(tr.points[1]), new Point3D(
				tr.points[2]), null);
	}

	/**
	 * Tests whether the given point is part of this triangle. This method only
	 * tests if this point is a base point of this triangle. It explicitly does
	 * not test for containment; use <code>contains</code> method instead.
	 * 
	 * @param point
	 *            point to test "inclusion"
	 * @param epsilon
	 *            GeoEpsilon, accuracy value
	 * @return boolean - true if the point is part of this triangle, false
	 *         otherwise.
	 */
	public boolean hasCorner(Point3D point, GeoEpsilon epsilon) {
		return this.points[0].isEqual(point, epsilon)
				|| this.points[1].isEqual(point, epsilon)
				|| this.points[2].isEqual(point, epsilon);
	}

	/**
	 * Computes and returns the MBB3D of this.
	 * 
	 * @return MBB3D - MBB3D of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D getMBB() {
		if (this.points[0] != null && this.points[1] != null
				&& this.points[2] != null) {
			Point3D pMin = new Point3D(GeomUtils.getMin(this.points[0].x,
					this.points[1].x, this.points[2].x), GeomUtils.getMin(
					this.points[0].y, this.points[1].y, this.points[2].y),
					GeomUtils.getMin(this.points[0].z, this.points[1].z,
							this.points[2].z));
			Point3D pMax = new Point3D(GeomUtils.getMax(this.points[0].x,
					this.points[1].x, this.points[2].x), GeomUtils.getMax(
					this.points[0].y, this.points[1].y, this.points[2].y),
					GeomUtils.getMax(this.points[0].z, this.points[1].z,
							this.points[2].z));
			return new MBB3D(pMin, pMax);
		} else
			return null;
	}

	/**
	 * Returns the normalized normal vector for this triangle.<br>
	 * The normal vector is computed from the points in the ascending index
	 * order or equivalent direction ( e.g. 0->1->2 or 2->0->1).
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return Vector3D - normal vector for this.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Vector3D getNormal(GeoEpsilon epsilon) {
		if (normvec == null) {
			double x = this.points[0].y * (this.points[1].z - this.points[2].z)
					+ this.points[1].y * (this.points[2].z - this.points[0].z)
					+ this.points[2].y * (this.points[0].z - this.points[1].z);
			double y = this.points[0].z * (this.points[1].x - this.points[2].x)
					+ this.points[1].z * (this.points[2].x - this.points[0].x)
					+ this.points[2].z * (this.points[0].x - this.points[1].x);
			double z = this.points[0].x * (this.points[1].y - this.points[2].y)
					+ this.points[1].x * (this.points[2].y - this.points[0].y)
					+ this.points[2].x * (this.points[0].y - this.points[1].y);

			this.normvec = new Vector3D(x, y, z);
			this.normvec.normalize(epsilon);
		}
		return this.normvec;
	}

	/**
	 * Returns the angles of this triangle.<br>
	 * Angle at index 0 -> Point at index 0 ...
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return double[] - angles.
	 */
	public double[] getAngles(GeoEpsilon epsilon) { // Dag
		double[] angles = { -1, -1, -1 };
		Vector3D v0 = this.getVectors()[0];
		Vector3D v1 = this.getVectors()[1];
		Vector3D v2 = this.getVectors()[2];

		// angle in corner with index 0
		double phi = java.lang.Math.acos((Vector3D.sub(v1, v0)).cosinus(
				Vector3D.sub(v2, v0), epsilon));
		angles[0] = (phi / Math.PI) * 180; // umrechnung in grad

		// angle in corner with index 1
		phi = java.lang.Math.acos(Vector3D.sub(v2, v1).cosinus(
				Vector3D.sub(v0, v1), epsilon));
		angles[1] = (phi / Math.PI) * 180;

		// angle in corner with index 2
		phi = java.lang.Math.acos(Vector3D.sub(v1, v2).cosinus(
				Vector3D.sub(v0, v2), epsilon));
		angles[2] = (phi / Math.PI) * 180;

		return angles;
	}

	/**
	 * Returns the vectors of the points of this triangle.
	 * 
	 * @return Vector3D[] - vectors of the points of this.
	 */
	public Vector3D[] getVectors() {
		return new Vector3D[] { new Vector3D(this.points[0]),
				new Vector3D(this.points[1]), new Vector3D(this.points[2]) };
	}

	/**
	 * Returns the plane corresponding to this triangle.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return Plane3D corresponding to this triangle.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Plane3D getPlane(GeoEpsilon epsilon) {
		return new Plane3D(this, epsilon);
	}

	/**
	 * Returns the index for given Segment3D,
	 * <code>-1</null> if segment is not an edge.
	 * 
	 * @param seg
	 *            segment to check as edge
	 * @return int - index of given segment, -1 if seg is not an edge of this.
	 */
	public int getSegmentIndex(Segment3D seg, GeoEpsilon epsilon) {

		if (this.getSegment(0, epsilon).isGeometryEquivalent(seg, epsilon))
			return 0;
		if (this.getSegment(1, epsilon).isGeometryEquivalent(seg, epsilon))
			return 1;
		if (this.getSegment(2, epsilon).isGeometryEquivalent(seg, epsilon))
			return 2;
		return -1;
	}

	/**
	 * Returns the area of this.
	 * 
	 * @return double - area.
	 */
	public double getArea(GeoEpsilon epsilon) { // Dag
		/*
		 * Alternative: 1/2 of the vector product of two vectors of this: area =
		 * ( this.getVectors()[0].crossproduct(this.getVectors()[1]) ).getNorm()
		 * / 2;
		 */

		/*
		 * Heron's formula F = sqrt( s * (s-a) * (s-b) * (s-c) ); where: s = (a
		 * + b + c)/2 (half perimeter)
		 */
		double a = getSegment(0, epsilon).getLength();
		double b = getSegment(1, epsilon).getLength();
		double c = getSegment(2, epsilon).getLength();
		double s = (a + b + c) / 2;
		return java.lang.Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	/**
	 * Returns relation of the orientation of this triangle with given vector.<br>
	 * Computed by scalarproduct of normal.
	 * 
	 * @param vec
	 *            given Vector3D
	 * @return int (+1/0/-1).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public int getOrientation(Vector3D vec, GeoEpsilon epsilon) { // Dag

		if (vec.isOrthogonal(this.getNormal(epsilon), epsilon))
			return 0;
		double scalar = vec.scalarproduct(this.getNormal(epsilon));
		if (scalar < 0)
			return -1;
		else
			return 1;
	}

	/**
	 * Returns the diameter of this.
	 * 
	 * @return double - diameter.
	 */
	public double getDiameter(GeoEpsilon epsilon) {// Dag

		/*
		 * diameter = 2*R of the circumscribed circle, its centre is the
		 * intersection point of the perpendicular bisectors. Here, the
		 * following property if fortunately true: 2*r = a / sin(alfa), where
		 * alfa is the angle opposite of the triangle edge a (is of course true
		 * for all edges).
		 */

		double diam = this.getSegment(0, epsilon).getLength()
				/ Math.sin((((this.getAngles(epsilon)[0]) / 180) * Math.PI));
		return diam;
	}

	/**
	 * Returns the center of this.
	 * 
	 * @return Point3D - center.
	 */
	public Point3D getCenter() {// Dag
		/*
		 * Idea: the coordinates of the center are calculated as the mean of the
		 * corresponding coordinates of three vertices.
		 * 
		 * The radius vector S directed to the center S of the triangle ABC is
		 * calculated from the radius vectors of the three vertices: S = [xS ,
		 * yS , zS] = (A + B + C) / 3
		 */

		double x = (points[0].x + points[1].x + points[2].x) / 3;
		double y = (points[0].y + points[1].y + points[2].y) / 3;
		double z = (points[0].z + points[1].z + points[2].z) / 3;
		return new Point3D(x, y, z);

		/*
		 * Alternative: (both ways are possible)
		 * 
		 * The center of the triangle is calculated as the intersection of two
		 * medians. Calculating the two medians:
		 * 
		 * Segment3D middel1 = new Segment3D( (
		 * getVectors()[0].add(getVectors()[1]) ).mult(0.5),
		 * this.getVectors()[2], this.getGeoEpsilon()); Segment3D middel2 = new
		 * Segment3D( ( getVectors()[0].add(getVectors()[2]) ).mult(0.5),
		 * this.getVectors()[1], this.getGeoEpsilon());
		 * 
		 * It is not necessary to test if the medians intersect because they
		 * MUST intersect in one point.
		 * 
		 * Point3D interceptPoint = (Point3D) (middel1.intersection(middel2,
		 * this.getGeoEpsilon()) ); return interceptPoint;
		 */

	}

	/**
	 * Returns the Point3D array of this.<br>
	 * 
	 * @return Point3D array.
	 */
	public Point3D[] getPoints() {
		return this.points;
	}

	/**
	 * Returns the Segment3D of this lying opposite to the point with given
	 * index.<br>
	 * Index 0 (=P0) - Line [P1,P2] <br>
	 * Index 1 (=P1) - Line [P2,P0] <br>
	 * Index 2 (=P2) - Line [P0,P1] <br>
	 * 
	 * @return Segment3D lying opposite to the point with given index.
	 */
	public Segment3D getSegment(int id, GeoEpsilon epsilon) {
		switch (id) {
		case 0:
			return new Segment3D(points[1], points[2], epsilon);
		case 1:
			return new Segment3D(points[2], points[0], epsilon);
		case 2:
			return new Segment3D(points[0], points[1], epsilon);
		default:
			return null;

		}
	}

	/**
	 * Test whether this intersects the given MBB.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
	 * @return boolean - true if this intersects with mbb.
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
	 *             if the result of the intersection in this method is not a
	 *             simplex
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
	public boolean intersects(MBB3D mbb, GeoEpsilon epsilon) {

		if (!this.getMBB().intersects(mbb, epsilon))
			return false;

		// test if at least one corner of this is inside of mbb
		for (int i = 0; i < 3; i++)
			if (mbb.contains(this.points[i], epsilon))
				return true;

		// intersection of mbb with plane of this and test if result intersects
		// with this
		Geometry3D obj = mbb.intersection(this.getPlane(epsilon), epsilon);
		// Here an IllegalStateException can be thrown. This exception
		// originates in the method getPoint(int) of the class Rectangle3D.

		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		// Here an IllegalStateException can be thrown signaling problems with
		// the index of a point coordinate.
		if (obj == null)
			return false;

		switch (obj.getGeometryType()) {
		case POINT:
			Point3D p = ((Point3D) obj);
			if (this.containsInPlane(p, epsilon))
				return true;
			return false;
		case SEGMENT:
			Segment3D seg = ((Segment3D) obj);
			if (this.intersects(seg, epsilon))
				return true;
			return false;
		case TRIANGLE:
			Triangle3D tri = ((Triangle3D) obj);
			if (this.intersects(tri, epsilon))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				return true;
			return false;
		case WIREFRAME:
			WireframeGeometry3D wf = ((WireframeGeometry3D) obj);
			Segment3D[] segs = wf.getSegments();
			Point3D centroid = wf.getCentroid();
			int length = segs.length;
			for (int i = 0; i < length; i++) {
				Triangle3D triangle = new Triangle3D(centroid,
						segs[i].points[0], segs[i].points[1], epsilon);
				if (this.intersects(triangle, epsilon))
					return true;
			}
			return false;
		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.resnotsimplex"));
		}
	}

	/**
	 * Test whether the inner of this intersects the give MBB.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
	 * @return boolean - true if inner of this intersects with mbb.
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
	 *             - if the result of the intersection in this method is not a
	 *             simplex.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsStrict(MBB3D mbb, GeoEpsilon epsilon) {

		if (!this.getMBB().intersectsStrict(mbb, epsilon))
			return false;
		// test if at least one corner of this is strict inside of mbb
		for (int i = 0; i < 3; i++)
			if (mbb.containsStrict(this.points[i], epsilon))
				return true;

		Geometry3D obj = mbb.intersection(this.getPlane(epsilon), epsilon);
		// Here an IllegalStateException can be thrown. This exception
		// originates in the getPoint(int) method of the class Rectangle3D.

		// Here an IllegalStateException can be thrown signaling problems with
		// the dimension of the wireframe.

		if (obj == null)
			return false;

		switch (obj.getGeometryType()) {
		case POINT:
			return false;
		case SEGMENT:
			return false;
		case TRIANGLE:
			Triangle3D tri = ((Triangle3D) obj);
			obj = this.intersectionInPlane(tri, epsilon);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensinos of the wireframe.
			if (obj == null)
				return false;
			if (obj.getGeometryType() == GEOMETRYTYPES.TRIANGLE
					|| obj.getGeometryType() == GEOMETRYTYPES.WIREFRAME)
				return true;
			else
				return false;
		case WIREFRAME:
			WireframeGeometry3D wf = ((WireframeGeometry3D) obj);
			Triangle3D[] t = wf.getTriangulated();
			// TODO it seems to be buggy to assume here that getTriangulated()
			// only return 2 triangles:
			for (int i = 0; i < 2; i++) {
				obj = this.intersectionInPlane(t[i], epsilon);
				if (obj.getGeometryType() == GEOMETRYTYPES.TRIANGLE
						|| obj.getGeometryType() == GEOMETRYTYPES.WIREFRAME)
					return true;
			}
			return false;
		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.resnotsimplex"));
		}
	}

	/**
	 * Tests whether this intersects with given Line3D.
	 * 
	 * @param line
	 *            Line3D to test
	 * @return boolean - true if they intersect, false otherwise.
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
	public boolean intersects(Line3D line, GeoEpsilon epsilon) { // Dag

		Plane3D plane = this.getPlane(epsilon);

		Geometry3D obj = plane.intersection(line, epsilon);
		if (obj == null)
			return false;
		if (obj.getGeometryType() == GEOMETRYTYPES.LINE) {
			if (this.intersectionInPlane(line, epsilon) != null)
				return true;
			else
				return false;
		} else { // plane and line intersect - test intersection point for
			// inside
			Point3D point = (Point3D) obj;
			if (this.containsInPlane(point, epsilon))
				return true;
			else
				return false;
		}
	}

	/**
	 * Tests whether this intersects with given Segment3D.
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @return boolean - true if they intersect, false otherwise.
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
	public boolean intersects(Segment3D segment, GeoEpsilon epsilon) { // Dag

		Geometry3D type = this.getPlane(epsilon).intersection(segment, epsilon);
		if (type == null)
			return false;
		if (type.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
			Segment3D seg = (Segment3D) type;
			if (this.intersectionInPlane(seg, epsilon) != null) {
				return true;
			}
			return false;
		} else {
			// plane and line intersect - test intersection point for inside
			Point3D point = (Point3D) type;
			if (this.containsInPlane(point, epsilon))
				return true;
			else
				return false;
		}
	}

	/**
	 * Tests whether this intersects with given Plane3D.
	 * 
	 * @param plane
	 *            Plane3D to test
	 * @return boolean - true if they intersect, false otherwise.
	 */
	public boolean intersects(Plane3D plane, GeoEpsilon epsilon) { // Dag
		// Idee: berechnung der skalarprodukte aus jeweils einem punktvektor und
		// normalvektor
		// mit Ebenendefinition (normalvektor * ortsvektor = c) folgt, dass
		// ein schnitt dann gegeben ist, wenn fuer mind. einen Punkt das
		// ergebnis c ist oder
		// fuer zwei Punkte gilt (scalarprod1 < c && scalarprod2 > c)

		Vector3D norm = plane.normvec;

		double c = norm.scalarproduct(plane.posvec);

		double xValue = norm.scalarproduct(this.getVectors()[0]);
		double yValue = norm.scalarproduct(this.getVectors()[1]);
		double zValue = norm.scalarproduct(this.getVectors()[2]);

		// at least on corner point lies on plane
		if ((epsilon.equal(xValue, c)) || (epsilon.equal(yValue, c))
				|| (epsilon.equal(zValue, c)))
			return true;

		/*
		 * From two scalar products with different sign follows that
		 * corresponding points lie on different sides of plane, this triangle
		 * intersects.
		 */
		if (((xValue < c) && ((yValue > c) || (zValue > c)))
				|| ((xValue > c) && ((yValue < c) || (zValue < c)))
				|| ((yValue < c) && (zValue > c))
				|| ((yValue > c) && (zValue < c)))
			return true;

		else
			return false;
	}

	/**
	 * Tests whether this intersects with given Triangle3D.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @return boolean - true if they intersect, false otherwise.
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
	 * @throws IllegalStateException
	 *             - if the result of the intersection in this method is not a
	 *             simplex.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Triangle3D triangle, GeoEpsilon epsilon) {// Dag

		/*
		 * Idea:
		 * 
		 * 1. If the planes are the same, check if the triangles have common
		 * points.
		 * 
		 * 2. If not, check if the plane that corresponds with each triangle
		 * interesects with the other triangle.
		 * 
		 * 3. If yes, compare the reslulting sets of interesecting parts (these
		 * can also be points).
		 * 
		 * An alternative is to use the method intersection(Triangle3D).
		 */

		if (!(this.getMBB().intersects(triangle.getMBB(), epsilon)))
			return false;

		else {
			// parallelism of planes
			if ((this.getNormal(epsilon).isCollinear(
					triangle.getNormal(epsilon), epsilon)) != 0) {

				// equality of planes
				if (!(this.getPlane(epsilon).contains(triangle.points[0],
						epsilon)))
					return false;
				else {
					if (this.intersectionInPlane(triangle, epsilon) != null)
						// Here an IllegalStateException can be thrown signaling
						// problems with the dimensions of the wireframe.
						return true;
					else
						return false;
				}
			}
			// planes not parallel
			else {
				if (!(this.intersects(triangle.getPlane(epsilon), epsilon) && triangle
						.intersects(this.getPlane(epsilon), epsilon)))
					return false;
				else { // this intersects argumentPlane AND argument intersects
						// thisPlane

					switch (this.intersection(triangle.getPlane(epsilon),
							epsilon).getGeometryType()) {

					case SEGMENT: {

						Segment3D thisSegment = (Segment3D) this.intersection(
								triangle.getPlane(epsilon), epsilon);

						switch (triangle.intersection(this.getPlane(epsilon),
								epsilon).getGeometryType()) {

						case SEGMENT: {

							Segment3D triangleSegment = (Segment3D) triangle
									.intersection(this.getPlane(epsilon),
											epsilon);

							if (triangleSegment
									.intersects(thisSegment, epsilon))
								return true; // thisSegment and triangleSegment
							// do have at least on point in
							// common
							else
								return false;
						}
						case POINT: {

							Point3D trianglePoint = (Point3D) triangle
									.intersection(this.getPlane(epsilon),
											epsilon);

							if (thisSegment.contains(trianglePoint, epsilon))
								return true; // triangle touches this in
							// trianglePoint
							else
								return false;
						}
						default:
							throw new IllegalStateException(
									Db3dSimpleResourceBundle
											.getString("db3d.geom.resnotsimplex"));

						}
					}

					case POINT: {

						Point3D thisPoint = (Point3D) this.intersection(
								triangle.getPlane(epsilon), epsilon);

						switch (triangle.intersection(this.getPlane(epsilon),
								epsilon).getGeometryType()) {

						case SEGMENT: {

							Segment3D triangleSegment = (Segment3D) triangle
									.intersection(this.getPlane(epsilon),
											epsilon);

							if (triangleSegment.contains(thisPoint, epsilon))
								return true; // triangle touches this in
							// trianglePoint
							else
								return false;
						}

						case POINT: {

							Point3D trianglePoint = (Point3D) triangle
									.intersection(this.getPlane(epsilon),
											epsilon);

							if (thisPoint.isEqual(trianglePoint, epsilon))
								return true;
							// triangles touch each other in one point
							else
								return false;
						}
						default:
							throw new IllegalStateException(
									Db3dSimpleResourceBundle
											.getString("db3d.geom.resnotsimplex"));

						}
					}
					default:
						throw new IllegalStateException(
								Db3dSimpleResourceBundle
										.getString("db3d.geom.resnotsimplex"));

					}
				}
			}
		}
	}

	/**
	 * Tests whether this intersects the given plane and of which dimension the
	 * result would be. (-1 no intersection, 0->0D(Point),1->1D(Segment) or
	 * 2->2D(Triangle)).
	 * 
	 * @param plane
	 *            Plane3D to test
	 * @return int - dimension of result.
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
	public int intersectsInt(Plane3D plane, GeoEpsilon epsilon) { // Dag
		Geometry3D result = this.intersection(plane, epsilon);
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.TRIANGLE)
			return 2;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return 1;
		return 0;
	}

	/**
	 * Tests whether this intersects the given line and of which dimension the
	 * result would be. (-1 no intersection, 0->Point3D, 1->Segment3D).
	 * 
	 * @param line
	 *            Line3D to test
	 * @return int - dimension of result.
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
	public int intersectsInt(Line3D line, GeoEpsilon epsilon) { // Dag
		Geometry3D result = this.intersection(line, epsilon);
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return 1;
		return 0;
	}

	/**
	 * Tests whether this intersects the given segment and of which dimension
	 * the result would be. (-1 no intersection, 0->Point3D, 1->Segment3D).
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @return int - dimension of result.
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
	public int intersectsInt(Segment3D segment, GeoEpsilon epsilon) { // Dag
		Geometry3D result = this.intersection(segment, epsilon);
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return 1;
		return 0;
	}

	/**
	 * Tests whether this intersects the given triangle and of which dimension
	 * the result would be (-1 no intersection, 0->Point3D, 1->Segment3D,
	 * 2->Trianle3D or 3->Wireframe3D).
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	public int intersectsInt(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		Geometry3D result = this.intersection(triangle, epsilon);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.TRIANGLE)
			return 2;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return 1;
		if (result.getGeometryType() == GEOMETRYTYPES.WIREFRAME)
			return 3;
		return 0; // Point3D
	}

	/**
	 * Tests whether this intersects with given plane in dimension 1, thus
	 * intersection() returns a Segment3D.
	 * 
	 * @param plane
	 *            Plane3D to test
	 * @return boolean - true if intersects, false otherwise.
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
	public boolean intersectsRegular(Plane3D plane, GeoEpsilon epsilon) {
		if (this.intersectsInt(plane, epsilon) == 1)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given line in dimension 0, thus
	 * intersection() returns a Point3D.
	 * 
	 * @param line
	 *            Line3D to test
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originat
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.es in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean intersectsRegular(Line3D line, GeoEpsilon epsilon) {
		if (this.intersectsInt(line, epsilon) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given segment in dimension 0, thus
	 * intersection() returns a Point3D.
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @return boolean - true if intersects, false otherwise.
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
	public boolean intersectsRegular(Segment3D segment, GeoEpsilon epsilon) {
		if (this.intersectsInt(segment, epsilon) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given triangle in dimension 1, thus
	 * intersection() returns a Segment3D.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @return boolean - true if intersects, false otherwise.
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
	public boolean intersectsRegular(Triangle3D triangle, GeoEpsilon epsilon) {
		if (this.intersectsInt(triangle, epsilon) == 1)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this projects regular as a triangle on given plane.
	 * 
	 * @param plane
	 *            Plane3D to test
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean projectsRegular(Plane3D plane, GeoEpsilon epsilon) { // Dag
		if (this.projection(plane, epsilon).getGeometryType() == GEOMETRYTYPES.TRIANGLE)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given point is contained in this. Assumes that the
	 * point is known to lay on the plane of this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see Triangle3D#contains(Triangle3D, GeoEpsilon).
	 */
	public boolean containsInPlane(Point3D point, GeoEpsilon epsilon) { // Dag
		/*
		 * Alternative: using the given point and another point inside this
		 * triangle to check of the given point is inside the triangle. The
		 * other point can be the intersection of the medians of the triangle or
		 * the "middle point" of the triangle. (The coordinates of the
		 * "middle point" are obtained by calculating the mean of the
		 * corresponding coordinates of the three vertices.) Any of these two
		 * points lies inside the triangle.
		 * 
		 * This other inner point and the given Point3D are used to construct a
		 * ray. Afterwards the intersection of this ray with each edge of the
		 * triangle is calculated. If the ray intersects the edges of the
		 * triangle just once, it means the given point is inside the triangle.
		 * Otherwise (if there are two intersection points) the given point lies
		 * outside the triangle.
		 * 
		 * This has first to be checked because otherwiese the plane(s) in
		 * further process coult not be constructed.
		 */

		if (this.hasCorner(point, epsilon))
			return true;

		Segment3D segment;
		Plane3D plane;
		int counter = 0;

		/*
		 * if for all corners is fulfilled that a plane orthogonal to this
		 * containing the corner point and point intersects the opposite edge,
		 * the point must lie in this
		 */
		for (int i = 0; i < 3; i++) {
			segment = this.getSegment(i, epsilon);
			Point3D p3 = Vector3D.add(new Vector3D(this.points[i]),
					this.getNormal(epsilon)).getAsPoint3D();

			plane = new Plane3D(this.points[i], point, p3, epsilon);
			if (!plane.intersects(segment, epsilon))
				return false;
			else
				counter++;
		}
		if (counter == 3)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given segment is contained in this. Assumes that the
	 * segment is known to lay on the plane of this.
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise. containment is not
	 *         strict.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see Triangle3D#containsProjection(Segment3D, GeoEpsilon).
	 */
	protected boolean containsInPlane(Segment3D segment, GeoEpsilon epsilon) { // Dag
		if (segment.getMBB().inside(this.getMBB(), epsilon)) {
			if (this.containsInPlane(segment.points[0], epsilon))
				return this.containsInPlane(segment.points[1], epsilon);
			else
				return false;
		} else
			return false;
	}

	/**
	 * Tests whether the given triangle is contained in this. Assumes that the
	 * triangle is known to lay on the plane of this.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise. containment is not
	 *         strict.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see Triangle3D#containsProjection(Triangle3D, GeoEpsilon).
	 */
	protected boolean containsInPlane(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		if (triangle.getMBB().inside(this.getMBB(), epsilon)) {
			if (this.containsInPlane(triangle.points[0], epsilon)
					&& this.containsInPlane(triangle.points[1], epsilon)
					&& this.containsInPlane(triangle.points[2], epsilon))
				return true;
			else
				return false;
		} else
			return false;
	}

	/**
	 * Tests whether the given point is contained in this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see Triangle3D#containsInPlane(Point3D, GeoEpsilon).
	 */
	public boolean contains(Point3D point, GeoEpsilon epsilon) {
		if (epsilon.equal(new Plane3D(this, epsilon).distance(point), 0))
			return containsInPlane(point, epsilon);
		else
			return false;
	}

	/**
	 * Tests whether the given point is contained in border of this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsInBorder(Point3D point, GeoEpsilon epsilon) {
		if (this.getSegment(0, epsilon).contains(point, epsilon)
				|| this.getSegment(1, epsilon).contains(point, epsilon)
				|| this.getSegment(2, epsilon).contains(point, epsilon))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given segment is contained in this.
	 * 
	 * @param segment
	 *            Segment3D
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Segment3D segment, GeoEpsilon epsilon) { // Dag
		/*
		 * Both points of have to lie on plane of this and fulfil
		 * containsInPlane
		 */
		if (segment.getMBB().inside(this.getMBB(), epsilon))
			if ((epsilon.equal(
					new Plane3D(this, epsilon).distance(segment.points[0]), 0))
					&& (epsilon.equal(new Plane3D(this, epsilon)
							.distance(segment.points[1]), 0)))
				return this.containsInPlane(segment, epsilon);
			else
				return false;

		return false;
	}

	/**
	 * Tests whether the given triangle is contained in this.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class V
	 */

	public boolean contains(Triangle3D triangle, GeoEpsilon epsilon) { // Dag

		if (triangle.getMBB().inside(this.getMBB(), epsilon)) {
			if ((epsilon.equal(
					new Plane3D(this, epsilon).distance(triangle.points[0]), 0))
					&& (epsilon.equal(new Plane3D(this, epsilon)
							.distance(triangle.points[1]), 0))
					&& (epsilon.equal(new Plane3D(this, epsilon)
							.distance(triangle.points[2]), 0)))
				return this.containsInPlane(triangle, epsilon);
			else
				// points are not on plane
				return false;
		} else
			return false;
	}

	/**
	 * Tests whether the projection of given point is contained in this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsProjection(Point3D point, GeoEpsilon epsilon) { // Dag
		Point3D projectionPoint = (Point3D) point.projection(this
				.getPlane(epsilon));
		if (this.containsInPlane(projectionPoint, epsilon))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the projection of given segment is contained in this.
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsProjection(Segment3D segment, GeoEpsilon epsilon) { // Dag
		Geometry3D projectionObj = segment.projection(this.getPlane(epsilon),
				epsilon);

		if (projectionObj.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return this.containsInPlane(((Segment3D) projectionObj), epsilon);
		else
			return containsInPlane(((Point3D) projectionObj), epsilon);
	}

	/**
	 * Tests whether the projection of given triangle is contained in this.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             if the result of projecting the given Triangle3D on the
	 *             Plane3D of this Triangle3D is a Point3D or is not a simplex.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsProjection(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		Geometry3D projectionObj = triangle.projection(this.getPlane(epsilon),
				epsilon);

		switch (projectionObj.getGeometryType()) {
		case TRIANGLE:
			return this.containsInPlane(((Triangle3D) projectionObj), epsilon);
		case SEGMENT:
			return this.containsInPlane(((Segment3D) projectionObj), epsilon);
		case POINT:
			// never should happen
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.resprojnotpoint"));

		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.resprojnotsimplex"));
		}
	}

	/**
	 * Computes the intersection of this and the given plane.<br>
	 * possible cases: 0 (no intersection), 1 (touching in one point), 2
	 * (segment) and 3 (in plane -> this) Returns <code>null</code>, Point3D,
	 * Segment3D or Triangle3D.
	 * 
	 * @param plane
	 *            Plane3D to test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object.
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
	public Geometry3D intersection(Plane3D plane, GeoEpsilon epsilon) { // Dag
		if (!this.intersects(plane, epsilon))
			return null;

		/*
		 * Fntersect plane with every edge and find out intersection case by
		 * querying the results.
		 */
		else {
			Geometry3D object0 = plane.intersection(
					this.getSegment(0, epsilon), epsilon);
			Geometry3D object1 = plane.intersection(
					this.getSegment(1, epsilon), epsilon);
			Geometry3D object2 = plane.intersection(
					this.getSegment(2, epsilon), epsilon);

			if (object0 != null) {
				if (object0.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
					if (object1.getGeometryType() == GEOMETRYTYPES.SEGMENT)
						return new Triangle3D(this);
					else
						return object0;
				} else { // object0 = point
					if (object1 != null) {
						if (object1.getGeometryType() == GEOMETRYTYPES.SEGMENT)
							return object1;
						else { // object1 = point

							if (((Point3D) object0).isEqual(
									((Point3D) object1), epsilon))
								return object0;
							else {
								return new Segment3D(((Point3D) object0),
										((Point3D) object1), epsilon);
							}
						}
					} else { // object0 !=null and object1==null -> object2 must
						// be Point3D
						if (((Point3D) object0).isEqual(((Point3D) object2),
								epsilon))
							return object0;
						else
							return new Segment3D(((Point3D) object0),
									((Point3D) object2), epsilon);
					}
				}
			} else {
				if (object1 != null) {
					if (((Point3D) object1).isEqual(((Point3D) object2),
							epsilon))
						return object1;
					else
						return new Segment3D(((Point3D) object1),
								((Point3D) object2), epsilon);
				} else
					return null;
				// object0=null and object1=null -> no intersection
			}
		}
	}

	/**
	 * Computes the intersection of this and the given line. Assumes that the
	 * line is in-plane with this. Possible cases are
	 * <ul>
	 * <li>0 (no intersection),</li>
	 * <li>1 (line "passes" without touch, is tangent or secant).</li>
	 * </ul>
	 * Returns <code>null</code>, <code>Point3D</code> or <code>Segment3D</code>
	 * .
	 * 
	 * @param line
	 *            the line that has to be tested for intersection with this
	 *            triangle
	 * @param epsilon
	 *            GeoEpsilon
	 * @return resulting geometry object. May be <code>null</code>,
	 *         <code>Point3D</code> or <code>Segment3D</code>.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	protected Geometry3D intersectionInPlane(Line3D line, GeoEpsilon epsilon) { // Dag
		/*
		 * Idea: structured processing of possible cases by analysing results of
		 * line/edge interesections.
		 * 
		 * We are in the plane. It is always enough to test only two of the
		 * triangle segments for strict interesection.
		 */

		Geometry3D object0 = this.getSegment(0, epsilon).intersection(line,
				epsilon);

		if (object0 != null) {
			// the first segment intersects - compute the result

			if (object0.getGeometryType() != GEOMETRYTYPES.SEGMENT) {

				/*
				 * Seems to be a mistake (Edgar): if it is a point return if
				 * (object0.getType() == SimpleGeoObj.GEOMETRYTYPES.POINT)
				 * return object0;
				 * 
				 * Intersection is via the complete triangle compute the
				 * intersection points at the segments
				 */
				Geometry3D object1 = this.getSegment(1, epsilon).intersection(
						line, epsilon);
				Geometry3D object2 = this.getSegment(2, epsilon).intersection(
						line, epsilon);

				if (object1 != null) {
					if (object1.getGeometryType() != GEOMETRYTYPES.SEGMENT) {
						if (((Point3D) object0).isEqual(((Point3D) object1),
								epsilon)) {
							if (object2 == null)
								return object0;
							else {
								return new Segment3D(((Point3D) object2),
										((Point3D) object0), epsilon);
							}
						} else
							return new Segment3D(((Point3D) object0),
									((Point3D) object1), epsilon);
					} else
						return object1;
				} else {
					if (object2.getGeometryType() != GEOMETRYTYPES.SEGMENT) {
						if (((Point3D) object0).isEqual(((Point3D) object2),
								epsilon))
							return object0;
						else
							return new Segment3D(((Point3D) object0),
									((Point3D) object2), epsilon);
					} else
						return object2;
				}
			} else
				return object0;
		} else {
			// the first segment does not intersect - try the second
			Geometry3D object1 = this.getSegment(1, epsilon).intersection(line,
					epsilon);

			if (object1 != null) {

				if (object1.getGeometryType() != GEOMETRYTYPES.SEGMENT) {

					/*
					 * Seems to be a mistake (Edgar): maybe a Point3D if
					 * (object1.getType() == SimpleGeoObj.GEOMETRYTYPES.POINT)
					 * return object1; intersects compute result
					 */
					Geometry3D object2 = this.getSegment(2, epsilon)
							.intersection(line, epsilon);
					if (((Point3D) object1).isEqual(((Point3D) object2),
							epsilon))
						return object1;
					else
						return new Segment3D(((Point3D) object1),
								((Point3D) object2), epsilon);
				} else
					return object1;
			} else {
				// no segment intersected so we have no intersection -> return
				// null
				return null;
			}
		}
	}

	/**
	 * Computes the intersection of this and the given line.<br>
	 * possible cases: 0 (no intersection), 1 (line "transfixes" triangle), 2
	 * (line is on plane -> line "passes" without touch, is tangent or secant)
	 * Returns <code>null</code>, Point3D, Segment3D.
	 * 
	 * @param line
	 *            Line3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object. May be <code>null</code>,
	 *         <code>Point3D</code> or <code>Segment3D</code>.
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
	public Geometry3D intersection(Line3D line, GeoEpsilon epsilon) { // Dag
		Geometry3D obj = this.getPlane(epsilon).intersection(line, epsilon);

		if (obj == null) // plane and line paralell and their is distance != 0
			return null;

		if (obj.getGeometryType() == GEOMETRYTYPES.POINT) {
			if (this.containsInPlane(((Point3D) obj), epsilon))
				return obj;
			else
				return null;
		} else { // obj is a segment
			return intersectionInPlane(line, epsilon);
		}
	}

	/**
	 * Computes the intersection of this and the given segment.<br>
	 * possible cases: 0 (no intersection), 1 (segment "transfixes" triangle), 2
	 * (segment is on plane -> no intersection, touching or section) Returns
	 * <code>null</code>, Point3D or Segment3D.
	 * 
	 * @param segment
	 *            the segment to intersect this triangle with.
	 * @param epsilon
	 *            GeoEpsilon
	 * @return resulting object. May be <code>null</code>, <code>Point3D</code>
	 *         or <code>Segment3D</code>.
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
	public Geometry3D intersection(Segment3D segment, GeoEpsilon epsilon) { // Dag

		Geometry3D result = this
				.intersection(segment.getLine(epsilon), epsilon);

		if (result == null)
			return null;

		if (result.getGeometryType() == GEOMETRYTYPES.POINT) {
			if (segment.containsOnLine(((Point3D) result), epsilon))
				return result;
			else
				return null;
		} else
			// result is segment, thus intersectionInPlane
			return this.intersectionInPlane(segment, epsilon);
	}

	/**
	 * Computes the intersection of this and the given triangle.<br>
	 * Returns <code>null</code> if no intersection occures.<br>
	 * Returns a Point3D object if the triangles touch in a point.<br>
	 * Returns a Segment3D if the triangles intersect in a segment.<br>
	 * Returns a Triangle3D object if one triangle contains the other one or
	 * they do intersect in one.<br>
	 * Returns a PointSet3D object if the triangles intersect in more than three
	 * intersection points (4 to 6 are possible).<br>
	 * In this case a new triangulation step for the resulting PointSet has to
	 * be done. and this results in a more komplex surface than triangle<br>
	 * 
	 * @param triangle
	 *            Triangle3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object. May be <code>null</code>,
	 *         <code>Point3D</code>, <code>Segment3D</code>,
	 *         <code>Trangle3D</code> or <code>PointSet3D</code>.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method (which computes the intersection of two lines) of the
	 *             class Line3D returns a value that is not -2, -1, 0 or 1.
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

	public Geometry3D intersection(Triangle3D triangle, GeoEpsilon epsilon) { // Dag

		if (!this.getMBB().intersects(triangle.getMBB(), epsilon))
			return null;

		/*
		 * (1) Calculating the intersection line of the planes that correspond
		 * to the two triangles.
		 * 
		 * (2) Calculating the intersections of this line with each segment of
		 * each of the two triangles.
		 * 
		 * (2.5) It is possible that the position of the triangles is such that
		 * the line does not interesect one or both triangles. It is also
		 * possible that triangles intersect only on one edge or in one point.
		 * (This is taken care of in the method seg.interesectionOnLine(seg).)
		 * 
		 * (3) Finding the position of the edges of the resulting intersection
		 * segment:
		 * 
		 * Two points of the second triangle lie between the two points of the
		 * first triangle 1a 2a 2b 1b -> intersection - result: points 2 a/b.
		 * 
		 * Two points of the first triangle lie between the two points of the
		 * second triangle 2a 1a 1b 2b -> intersection - result: points 1 a/b.
		 * 
		 * One point inside and the other one is outside 1a 2a 1b 2b ->
		 * intersection - result: points 1a and 2b.
		 * 
		 * No points of the first triangle between the points of the second
		 * triangle and vice versa -> no intersection.
		 */

		// (1)
		Geometry3D type = this.getPlane(epsilon).intersection(
				triangle.getPlane(epsilon), epsilon);
		if (type == null)
			return null;
		if (type.getGeometryType() == GEOMETRYTYPES.PLANE)
			// triangles are in the same plane
			return this.intersectionInPlane(triangle, epsilon);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		// else intersection of planes is a Line3D
		Line3D line = (Line3D) type;

		// (2)
		// this triangle
		Point3D[] thisPoints = new Point3D[3];
		int thisCounter = 0;
		for (int i = 0; i < 3; i++) {
			type = line.intersection(this.getSegment(i, epsilon), epsilon);
			if (!(type == null)) {

				if (type.getGeometryType() == GEOMETRYTYPES.POINT) {
					thisPoints[thisCounter] = (Point3D) type;
					thisCounter++;
				}
				if (type.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
					/*
					 * This touches the straight line. Because we do know the
					 * triangles are not in the plane, we can compute the result
					 * segment.
					 */
					return triangle.intersectionInPlane(((Segment3D) type),
							epsilon);
				}
			}
		}

		// (2.5)
		if (thisCounter == 0)
			return null;

		// (2)
		// argument triangle

		Point3D[] argPoints = new Point3D[3];
		int argCounter = 0;
		for (int i = 0; i < 3; i++) {
			type = line.intersection(triangle.getSegment(i, epsilon), epsilon);

			if (!(type == null)) {

				if (type.getGeometryType() == GEOMETRYTYPES.POINT) {
					argPoints[argCounter] = (Point3D) type;
					argCounter++;
				}
				if (type.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
					/*
					 * This touches the straight line. Because we do know the
					 * triangles are not in the same plane, we can compute the
					 * result segment.
					 */
					return this
							.intersectionInPlane(((Segment3D) type), epsilon);
				}
			}
		}

		// (2.5)
		if (argCounter == 0)
			return null;

		/*
		 * At this point we know that we have found two points for each triangle
		 * from intersection with line (intersection line of intersecting planes
		 * of this and triangle).
		 * 
		 * Special cases - the triangles touches each other in at least one
		 * corner point
		 */
		for (int i = 0; i < thisCounter; i++)
			for (int j = 0; j < argCounter; j++) {
				if (thisPoints[i].isEqual(argPoints[j], epsilon))
					return thisPoints[i];
			}

		if (thisPoints[0].isEqual(thisPoints[1], epsilon)) {
			if (argCounter == 1 || argPoints[0].isEqual(argPoints[1], epsilon)) {
				if (thisPoints[0].isEqual(argPoints[0], epsilon))
					return thisPoints[0];
				else
					return null;
			} else {
				Segment3D s = new Segment3D(argPoints[0], argPoints[1], epsilon);
				if (s.contains(thisPoints[0], epsilon))
					return thisPoints[0];
				else
					return null;
			}
		}

		if (argPoints[0].isEqual(argPoints[1], epsilon)) {
			if (thisCounter == 1
					|| thisPoints[0].isEqual(thisPoints[1], epsilon)) {
				if (thisPoints[0].isEqual(argPoints[0], epsilon))
					return thisPoints[0];
				else
					return null;
			} else {
				Segment3D thisSectionSegment = new Segment3D(thisPoints[0],
						thisPoints[1], null);// epsilon);

				if (thisSectionSegment.contains(argPoints[0], epsilon))
					return argPoints[0];
				else
					return null;
			}

		}

		// (3) At this point we know that we have to intersect two segments
		// (one intersection segment from each triangle).
		Segment3D thisSectionSegment = new Segment3D(thisPoints[0],
				thisPoints[1], null);// epsilon);
		Segment3D argSectionSegment = new Segment3D(argPoints[0], argPoints[1],
				null); // epsilon);
		return thisSectionSegment
				.intersectionOnLine(argSectionSegment, epsilon);

	}

	/**
	 * Computes the intersection of this and the given Segment3D.<br>
	 * The given segment is in the same plane as this - different cases can
	 * occur: 0 (outside), 1 (touches in endpoint or triangle edge) or 2
	 * intersection points with this. <br>
	 * Returns a Point3D object if they touch in a point.<br>
	 * Returns a PointSet3D object if they intersect.<br>
	 * 
	 * @param segment
	 *            Segment3D in plane for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object.
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

	protected Geometry3D intersectionInPlane(Segment3D segment,
			GeoEpsilon epsilon) { // Dag
		Geometry3D result = this.intersectionInPlane(segment.getLine(epsilon),
				epsilon);

		if (result == null)
			return null;

		if (result.getGeometryType() == GEOMETRYTYPES.POINT) {
			Point3D p = (Point3D) result;
			for (int i = 0; i < 3; i++) {
				if (this.points[i].isEqual(p, epsilon))
					if (segment.contains(p, epsilon))
						return this.points[i];
					else
						return null;

			}
			return null;
		}

		// result must be of type SimpleGeoObj.GEOMETRYTYPES.SEGMENT
		Segment3D thisSectionSegment = (Segment3D) result;
		return thisSectionSegment.intersectionOnLine(segment, epsilon);
	}

	/**
	 * Computes the intersection of this and the given triangle.<br>
	 * Assumes that both are on the same plane.<br>
	 * Returns <code>null</code> if triangles don't intersect.<br>
	 * Returns a Point3D object if they touch in a point.<br>
	 * Returns a Segment3D if they intersect in a segment.<br>
	 * Returns a Triangle3D object if one triangle contains the other one or
	 * they do intersect in a triangle.<br>
	 * Returns a Wireframe3D object if they intersect in an object with more
	 * than three corner points (4 to 6 are possible)<br>
	 * In this case a new triangulation step for the resulting PointSet is
	 * necessary. and this results in a more komplex surface than triangle<br>
	 * 
	 * @param triangle
	 *            Triangle3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object. May be of type null, Point3D,
	 *         Segment3D, Triangle3D or Wireframe3D.
	 * 
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
	protected Geometry3D intersectionInPlane(Triangle3D triangle,
			GeoEpsilon epsilon) { // Dag
		/*
		 * Procedure:
		 * 
		 * (1)-(2) Calculation of all intersections between every edge and the
		 * other triangle.
		 * 
		 * (3) Count of in resultWireframe contained points "decides" on return
		 * type.
		 * 
		 * Remark: BoundingBox test has in this case already taken place (in
		 * super method).
		 */

		WireframeGeometry3D resultWireframe = new WireframeGeometry3D(epsilon);

		// (1) intersections of this with edges of triangle
		for (int i = 0; i < 3; i++) {
			Geometry3D result = this.intersection(
					triangle.getSegment(i, epsilon), epsilon);
			if (!(result == null)) {
				if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
					resultWireframe.add(((Segment3D) result));
				} else
					resultWireframe.add(((Point3D) result));
			}
		}
		// (2) intersections of triangle with edges of this
		for (int i = 0; i < 3; i++) {
			Geometry3D result = triangle.intersection(
					this.getSegment(i, epsilon), epsilon);
			if (!(result == null)) {
				if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
					resultWireframe.add(((Segment3D) result));
				} else
					resultWireframe.add(((Point3D) result));
			}
		}

		// (3) return
		int size = resultWireframe.countNodes();

		if (size == 0)
			return null;
		if (size == 1)
			return resultWireframe.getPoints()[0];
		if (size == 2)
			return new Segment3D(resultWireframe.getPoints()[0],
					resultWireframe.getPoints()[1], epsilon);
		if (size == 3)
			return new Triangle3D(resultWireframe.getPoints()[0],
					resultWireframe.getPoints()[1],
					resultWireframe.getPoints()[2], null); // no validation

		return resultWireframe;
	}

	/**
	 * Projects this onto the given plane. Returns a Segment3D or Triangle3D
	 * object.
	 * 
	 * @param plane
	 *            Plane3D onto which this should be projected
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D projection(Plane3D plane, GeoEpsilon epsilon) { // Dag
		Point3D[] projectedPoints = new Point3D[3];

		for (int i = 0; i < 3; i++)
			projectedPoints[i] = (Point3D) points[i].projection(plane);

		Line3D line = new Line3D(projectedPoints[0], projectedPoints[1],
				epsilon);

		if (line.contains(projectedPoints[2], epsilon)) {
			Segment3D seg = new Segment3D(projectedPoints[0],
					projectedPoints[1], epsilon);
			if (seg.contains(projectedPoints[2], epsilon))
				return seg;
			else {
				seg = new Segment3D(projectedPoints[0], projectedPoints[2],
						epsilon);
				if (seg.contains(projectedPoints[1], epsilon))
					return seg;
				else
					return new Segment3D(projectedPoints[1],
							projectedPoints[2], epsilon);
			}
		} else
			// projection of this is a triangle object
			return new Triangle3D(projectedPoints[0], projectedPoints[1],
					projectedPoints[2], epsilon);
	}

	/**
	 * Projects the given line onto this.<br>
	 * Returns <code>null</code>, Point3D or Segment3D object.
	 * 
	 * @param line
	 *            Line3D onto which this should be projected
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - resulting object.
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
	public Geometry3D projectionOf(Line3D line, GeoEpsilon epsilon) { // Dag
		Point3D p1 = line.origin;
		Point3D p2 = (line.origin.getVector().add(line.dvec)).getAsPoint3D();

		Plane3D plane = this.getPlane(epsilon);
		Point3D projectionP1 = (Point3D) p1.projection(plane);
		Point3D projectionP2 = (Point3D) p2.projection(plane);

		if (projectionP1.isEqual(projectionP2, epsilon))
			if (this.contains(projectionP1, epsilon))
				return projectionP1;
			else
				return null;
		else {
			Line3D projectedLine = new Line3D(projectionP1, projectionP2,
					epsilon);
			if (this.intersects(projectedLine, epsilon))
				return this.intersection(projectedLine, epsilon);
			else
				return null;
		}
	}

	/**
	 * Projects the given point onto this. Returns <code>null</code> or Point3D
	 * object.
	 * 
	 * @param point
	 *            Point3D to be projected
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result of the projection.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D projectionOf(Point3D point, GeoEpsilon epsilon) { // Dag
		Point3D projectionOnThisPlane = (Point3D) point.projection(this
				.getPlane(epsilon));
		if (this.contains(projectionOnThisPlane, epsilon))
			return projectionOnThisPlane;
		else
			return null;
	}

	/**
	 * Projects the given segment onto this. Returns <code>null</code>, Point3D
	 * or Segment3D object.
	 * 
	 * @param segment
	 *            Segment3D to be projected
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result of the projection.
	 * 
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
	public Geometry3D projectionOf(Segment3D segment, GeoEpsilon epsilon) { // Dag
		Point3D p1 = segment.points[0];
		Point3D p2 = segment.points[1];

		Point3D projectionP1 = (Point3D) p1.projection(this.getPlane(epsilon));
		Point3D projectionP2 = (Point3D) p2.projection(this.getPlane(epsilon));

		if (projectionP1.isEqual(projectionP2, epsilon))
			if (this.contains(projectionP1, epsilon))
				return projectionP1;
			else
				return null;
		else {
			Segment3D projectedSegment = new Segment3D(projectionP1,
					projectionP2, epsilon);
			if (this.intersects(projectedSegment, epsilon))
				return this.intersection(projectedSegment, epsilon);
			else
				return null;
		}
	}

	/**
	 * Projects the given triangle onto this. Returns <code>null</code> if no
	 * intersection occures. Can return a Point3D or Segment3D if the on plane
	 * of this projected triangle is a segment. Can return a Point3D, Segment3D,
	 * Triangle3D or PointSet3D if the projection of the triangle on plane of
	 * this is a triangle.
	 * 
	 * @param triangle
	 *            Triangle3D to be projected
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result of the projection.
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
	public Geometry3D projectionOf(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		Geometry3D projectedTriangle = triangle.projection(
				this.getPlane(epsilon), epsilon);

		if (projectedTriangle.getGeometryType() == GEOMETRYTYPES.TRIANGLE) {
			triangle = (Triangle3D) projectedTriangle;
			if ((this.getMBB().intersects(triangle.getMBB(), epsilon)))
				return this.intersectionInPlane(triangle, epsilon);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			else
				return null;
		} else
			// projectedTriangle has to be a segment
			return this.intersectionInPlane(((Segment3D) projectedTriangle),
					epsilon);
	}

	/**
	 * Inverts the orientation of this by changing two vertices.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void invertOrientation() { // Dag
		Point3D temppoi = this.points[2];
		this.points[2] = this.points[1];
		this.points[1] = temppoi;
	}

	/**
	 * Sets the orientation of this according to given vector. Returns true if
	 * orientation was set, false if the given vector is in plane of this, thus
	 * doesn't "show" any orientaion for this.
	 * 
	 * @param vec
	 *            Vector3D to which the orientation should be set
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true is the orientation was set, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean setOrientation(Vector3D vec, GeoEpsilon epsilon) { // Dag
		if (this.getOrientation(vec, epsilon) == 0)
			return false;
		if (this.getOrientation(vec, epsilon) == -1)
			this.invertOrientation();
		// else orientation is already correct (==1)
		return true;
	}

	/**
	 * Validates if the given points are a valid triangle.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if valid, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isValid(GeoEpsilon epsilon) {

		if (this.points[0].isEqual(this.points[1], epsilon)
				|| this.points[0].isEqual(this.points[2], epsilon)
				|| this.points[1].isEqual(this.points[2], epsilon))
			return false;

		return !new Line3D(this.points[0], this.points[1], epsilon).contains(
				this.points[2], epsilon);
	}

	/*
	 * Returns vectors for the two longer segments of this.
	 * 
	 * @return Vector3D[] with the two longer segments of this.
	 */
	private Vector3D[] getLongerVectors() { // Dag
		Vector3D[] vectors = this.getVectors();
		Vector3D AB = Vector3D.sub(vectors[1], vectors[0]);
		Vector3D AC = Vector3D.sub(vectors[2], vectors[0]);
		Vector3D BC = Vector3D.sub(vectors[2], vectors[1]);

		double lengthAB = AB.getNorm();
		double lengthAC = AC.getNorm();
		double lengthBC = BC.getNorm();

		Vector3D[] longerVectors = new Vector3D[2];

		if (lengthAB > lengthAC)
			if (lengthAC > lengthBC) {
				longerVectors[0] = AB;
				longerVectors[1] = AC;
			} else {
				longerVectors[0] = AB;
				longerVectors[1] = BC;
			}
		else if (lengthAB < lengthBC) {
			longerVectors[0] = AC;
			longerVectors[1] = BC;
		} else {
			longerVectors[0] = AB;
			longerVectors[1] = AC;
		}
		return longerVectors;
	}

	/**
	 * Checks wether this is a regular triangle.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if the triangle is regular, false otherwise.
	 */
	public boolean isRegular(GeoEpsilon epsilon) { // Dag
		// (1) all segments are regular
		// (2) vectorproduct of two segments of this / 2 (=aerea) > threshold

		// (1)
		for (int i = 0; i < 3; i++)
			if (!this.getSegment(i, epsilon).isRegular(epsilon))
				return false;
		// (2)
		// calculate area of triangle and compare with multible of epsilon
		double area = this.getArea(epsilon);
		if (area > (epsilon.getEpsilon() * MIN_AREA_EPSILON_FACTOR))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether this is a "beautiful" triangle. Beautiful accounts the
	 * proportion of a triangle. Very long and narrow triangles with a small
	 * normalized area are going to be evaluated as not "beautiful".
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if the triangle is "beautiful", false otherwise.
	 */
	public boolean isBeautiful(GeoEpsilon epsilon) { // Dag
		// considers the proportion
		// check for longer edges of a triangle (B-A) and (C-A) :
		// | ( (B-A) x (C-A) / |B-A| * |C-A| ) | > threshold

		Vector3D[] longerVectors = this.getLongerVectors();

		double numerator = (longerVectors[0].crossproduct(longerVectors[1]))
				.getNorm();
		double denumerator = longerVectors[0].getNorm()
				* longerVectors[1].getNorm();

		if (numerator > (denumerator * (MIN_AREA_EPSILON_FACTOR * epsilon
				.getEpsilon())))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether this is a complete validated triangle.<br>
	 * This method performs a isValid, isRegular and isBeautiful test.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if the triangle is completely validated, false
	 *         otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isCompleteValidated(GeoEpsilon epsilon) {
		return isValid(epsilon) && isRegular(epsilon) && isBeautiful(epsilon);
	}

	/**
	 * Method for identifying the type of object.
	 * 
	 * @return byte - constant for this type.
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.TRIANGLE;
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Triangle3D [normvec=" + normvec + ", one=" + points[1]
				+ ", two=" + points[2] + ", zero=" + points[0] + "]";
	}

	@Override
	/**
	 * Returns the hash code of this.
	 * @return int - hash code of this.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((points[1] == null) ? 0 : points[1].hashCode());
		result = prime * result
				+ ((points[2] == null) ? 0 : points[2].hashCode());
		result = prime * result
				+ ((points[0] == null) ? 0 : points[0].hashCode());
		return result;
	}

	/**
	 * Strict equal test.<br>
	 * All the points must be equal in the same index (not in case of Point3D).<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if equal, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.GeoEpsilon)
	 */
	public boolean isEqual(Triangle3D obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		for (int i = 0; i < 3; i++)
			if (!(this.points[i].isEqual(obj.points[i], epsilon)))
				return false;

		return true;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Triangle3D))
			return false;
		return isEqual((Triangle3D) obj, epsilon);
	}

	/**
	 * Computes the corresponding hash code for isEqual usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * @see db3d.dbms.geom.Equivalentable#isEqualHC(int)
	 */
	public int isEqualHC(int factor) {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.points[0].isEqualHC(factor);
		result = prime * result + this.points[1].isEqualHC(factor);
		result = prime * result + this.points[2].isEqualHC(factor);
		return result;

	}

	/**
	 * Computes the corresponding hash code for isGeometryEquivalent usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.GeoEpsilon)
	 */
	public boolean isGeometryEquivalent(Triangle3D obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		Point3D[] ps1 = GeomUtils.getSorted(this.points);
		Point3D[] ps2 = GeomUtils.getSorted(obj.points);
		int length = ps1.length;
		for (int i = 0; i < length; i++)
			if (!ps1[i].isEqual(ps2[i], epsilon))
				return false;

		return true;
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Triangle3D))
			return false;
		return isGeometryEquivalent((Triangle3D) obj, epsilon);
	}

	/**
	 * Computes the corresponding hash code for isGeometryEquivalent usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalentHC(int)
	 */
	public int isGeometryEquivalentHC(int factor) {
		final int prime = 31;
		int result = 1;
		Point3D[] ps = GeomUtils.getSorted(this.points);
		int length = ps.length;
		for (int i = 0; i < length; i++)
			result = prime * result + ps[i].isGeometryEquivalentHC(factor);

		return result;

	}

} // end Triangle3D

