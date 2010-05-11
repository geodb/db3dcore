/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;

/**
 * Tetrahedron3D is the geometric representation of a tetrahedron simplex in 3D.
 * 
 * A Tetrahedron3D object is modeled by four Point3D objects. The retrieved
 * triangles from this tetrahedron are oriented by their normal vector facing
 * outside of the tetrahedron. This orientation relies on the two internal
 * methods ensureOrder and buildTriangles.
 */
@SuppressWarnings("serial")
public class Tetrahedron3D implements PersistentObject, SimpleGeoObj,
		Equivalentable, Serializable {

	/* geometry */

	/* point 0 */
	private Point3D zero;

	/* point 1 */
	private Point3D one;

	/* point 2 */
	private Point3D two;

	/* point 3 */
	private Point3D three;

	/* triangles of this [0,3] */
	private transient Triangle3D[] triangles = null;

	/**
	 * Constructor.
	 * 
	 * @param pts
	 *            Point3D array
	 * @param sop
	 *            ScalarOperator needed for validation<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Tetrahedron3D(Point3D[] pts, ScalarOperator sop) {
		if (pts == null || pts.length != 4)
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.tetraconsfour"));

		this.zero = pts[0];
		this.one = pts[1];
		this.two = pts[2];
		this.three = pts[3];
		this.triangles = null;

		// validate
		if (sop != null) {
			if (!isValid(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotval"));
			if (!isRegular(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotreg"));
			if (!isBeautiful(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotbeau"));
		}
		ensureOrder();
	}

	/**
	 * Constructor.
	 * 
	 * @param point1
	 *            Point3D 1
	 * @param point2
	 *            Point3D 2
	 * @param point3
	 *            Point3D 3
	 * @param point4
	 *            Point3D 4
	 * @param sop
	 *            ScalarOperator needed for validation<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if the validation of the tetrahedron fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Tetrahedron3D(Point3D point1, Point3D point2, Point3D point3,
			Point3D point4, ScalarOperator sop) {
		this.zero = point1;
		this.one = point2;
		this.two = point3;
		this.three = point4;
		this.triangles = null;

		// validate
		if (sop != null) {
			if (!isValid(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotval"));
			if (!isRegular(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotreg"));
			if (!isBeautiful(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotbeau"));
		}
		ensureOrder();
	}

	/**
	 * Constructor.
	 * 
	 * @param point
	 *            Point3D
	 * @param triangle
	 *            Triangle3D
	 * @param sop
	 *            ScalarOperator needed for validation<br>
	 *            If ScalarOperator is <code>null</code> no validation will
	 *            occur.
	 * @throws ValidationException
	 *             signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Tetrahedron3D(Point3D point, Triangle3D triangle, ScalarOperator sop) {
		this(point, triangle.getPoint(0), triangle.getPoint(1), triangle
				.getPoint(2), sop);
	}

	/**
	 * Constructor.
	 * 
	 * @param seg1
	 *            Segment3D 1
	 * @param seg2
	 *            Segment3D 2
	 * @param sop
	 *            ScalarOperator needed for validation<br>
	 *            If ScalarOperator is <code>null</code> no validation will
	 *            occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Tetrahedron3D(Segment3D seg1, Segment3D seg2, ScalarOperator sop) {
		this(seg1.getPoint(0), seg1.getPoint(1), seg2.getPoint(0), seg2
				.getPoint(1), sop);
	}

	/**
	 * Copy constructor.<br>
	 * Makes a deep copy, points are copied too.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be copied
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Tetrahedron3D(Tetrahedron3D tetra) {
		this(new Point3D(tetra.getPoint(0)), new Point3D(tetra.getPoint(1)),
				new Point3D(tetra.getPoint(2)), new Point3D(tetra.getPoint(3)),
				null);
	}

	/**
	 * Returns the geometry of the tetrahedron as a newly created array !.<br>
	 * Array gets invalid if a setPointX() method is called !.
	 * 
	 * @return Point3D[] - array of Point3D objects.
	 */
	public Point3D[] getPoints() {
		return new Point3D[] { this.zero, this.one, this.two, this.three };
	}

	/**
	 * Sets the points of this.
	 * 
	 * @param points
	 *            Point
	 */
	public void setPoints(Point3D[] points) {
		this.zero = points[0];
		this.one = points[1];
		this.two = points[2];
		this.three = points[3];
		this.triangles = null;
		this.ensureOrder();
	}

	/**
	 * Returns the Point3D to given index.
	 * 
	 * @param index
	 *            int index of the point
	 * @return Point3D - point to given index.
	 * @throws IllegalArgumentException
	 *             if the index is not in the interval [0;3].
	 */
	public Point3D getPoint(int index) {
		switch (index) {
		case 0:
			return this.zero;
		case 1:
			return this.one;
		case 2:
			return this.two;
		case 3:
			return this.three;
		default:
			// FIXME fix this weird switch(index) stuff
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.wronindtetraconsfour"));

		}
	}

	/**
	 * Sets the Point3D to given index.
	 * 
	 * @param index
	 *            int index of the point
	 * @param point
	 *            Point3D to given index.
	 * @throws IllegalArgumentException
	 *             - if the index is not in the interval [0;3].
	 */
	public void setPoint(int index, Point3D point) {
		switch (index) {
		case 0:
			this.zero = point;
			break;
		case 1:
			this.one = point;
			break;
		case 2:
			this.two = point;
			break;
		case 3:
			this.three = point;
			break;
		default:
			// FIXME fix this weird switch(index) stuff
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.wronindtetraconsfour"));
		}
		this.triangles = null;
		this.ensureOrder();
	}

	/**
	 * Checks whether <code>this</code> has given point as corner.
	 * 
	 * @param p
	 *            Point3D for test
	 * @param sop
	 *            ScalarOperator
	 * 
	 * @return boolean - true if the given point is present in the tetrahedron,
	 *         false otherwise.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean hasCorner(Point3D p, ScalarOperator sop) {
		if (this.getPoint(0).isEqual(p, sop)
				|| this.getPoint(1).isEqual(p, sop)
				|| this.getPoint(2).isEqual(p, sop)
				|| this.getPoint(3).isEqual(p, sop))
			return true;
		return false;
	}

	/**
	 * Checks whether <code>this</code> has given segment as edge.
	 * 
	 * @param seg
	 *            Segment3D for test
	 * @param sop
	 *            ScalarOperator
	 * 
	 * @return boolean - true if the given segment is one of the edges of the
	 *         tetrahedron, false otherwise.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean hasEdge(Segment3D seg, ScalarOperator sop) {

		if (this.hasCorner(seg.getPoint(0), sop)
				&& this.hasCorner(seg.getPoint(1), sop))
			return true;
		return false;
	}

	/*
	 * Returns the steradiant (solid angle) between three vectors.<br> The solid
	 * angle is defined as the ratio of the area of the corresponding spherical
	 * triangle to the area of the corresponding sphere.
	 * 
	 * The area of the sphere is 4Pi*square(R).
	 * 
	 * The area of the spherical triangle is square(R)*(angular sum - Pi), the
	 * angular sum of the spherical triangle is defined as follows: the angles
	 * are are angles between the planes and the great circles.
	 * 
	 * If a, b, c are the angles between the vectors, then for the angles in the
	 * spherical triangle is the following true: alpha = acos((cos(a)-cos(b) *
	 * cos(c))/(sin(c) * sin(b)))<br> beta = acos((cos(b)-cos(c) *
	 * cos(a))/(sin(c) * sin(a)))<br> gamma = acos((cos(c)-cos(a) *
	 * cos(b))/(sin(a) * sin(b)))<br>
	 * 
	 * The spherical excess = unit sphere - area of the triangle: (range of
	 * values 2pi > s > 0)<br> e = alpha + beta + gamma - pi
	 * 
	 * @param v0 vector no 1
	 * 
	 * @param v1 vector no 2
	 * 
	 * @param v2 vector no 3
	 * 
	 * @param sop ScalarOperator
	 * 
	 * @return double - solid angle in degree ( between 0 and 360 ).
	 */
	private double getAngle(Vector3D v0, Vector3D v1, Vector3D v2,
			ScalarOperator sop) { // Dag

		double cosa = v0.cosinus(v1, sop);
		double cosb = v0.cosinus(v2, sop);
		double cosc = v1.cosinus(v2, sop);

		double a = Math.acos(cosa);
		double b = Math.acos(cosb);
		double c = Math.acos(cosc);

		double sina = Math.sin(a);
		double sinb = Math.sin(b);
		double sinc = Math.sin(c);

		// this cases already must have been treated by the isValid() method
		//
		// double solidAngle = -1;
		//		
		// if ( (a+b+c) >= 360 )
		// return solidAngle;
		//		
		// if ( a > b && a > c ) {
		// if ( (b+c) <= a )
		// return solidAngle;
		// }
		// else
		// if ( b > c ) {
		// if ( (a+c) <= b )
		// return solidAngle;
		// }
		// else
		// if ( (a+b) <= c )
		// return solidAngle;

		// corner angles in degree
		double pi = Math.PI;
		double alpha = ((Math.acos((cosa - (cosb * cosc)) / (sinc * sinb))) / pi) * 180;
		double beta = ((Math.acos((cosb - (cosc * cosa)) / (sinc * sina))) / pi) * 180;
		double gamma = ((Math.acos((cosc - (cosa * cosb)) / (sina * sinb))) / pi) * 180;

		return alpha + beta + gamma - 180;
	}

	/**
	 * Returns the angles (steradiants) of this tetrahedron.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * 
	 * @return double[] - angles.
	 */
	public double[] getAngles(ScalarOperator sop) { // Dag
		Vector3D[] v = this.getVectors();
		double[] angles = new double[4];

		Vector3D[] vectors = new Vector3D[3];

		// vectors "originating" in point 0
		vectors[0] = Vector3D.sub(v[1], v[0]);
		vectors[1] = Vector3D.sub(v[2], v[0]);
		vectors[2] = Vector3D.sub(v[3], v[0]);
		angles[0] = getAngle(vectors[0], vectors[1], vectors[2], sop);

		// vectors "originating" in point 1
		vectors[0] = Vector3D.sub(v[0], v[1]);
		vectors[1] = Vector3D.sub(v[2], v[1]);
		vectors[2] = Vector3D.sub(v[3], v[1]);
		angles[1] = getAngle(vectors[0], vectors[1], vectors[2], sop);

		// vectors "originating" in point 2
		vectors[0] = Vector3D.sub(v[0], v[2]);
		vectors[1] = Vector3D.sub(v[1], v[2]);
		vectors[2] = Vector3D.sub(v[3], v[2]);
		angles[2] = getAngle(vectors[0], vectors[1], vectors[2], sop);

		// vectors "originating" in point 3
		vectors[0] = Vector3D.sub(v[0], v[3]);
		vectors[1] = Vector3D.sub(v[1], v[3]);
		vectors[2] = Vector3D.sub(v[2], v[3]);
		angles[3] = getAngle(vectors[0], vectors[1], vectors[2], sop);

		return angles;
	}

	/**
	 * Returns the vectors of the points of this tetrahedron.
	 * 
	 * @return Vector3D[] - vectors.
	 */
	public Vector3D[] getVectors() {
		return new Vector3D[] { new Vector3D(this.zero),
				new Vector3D(this.one), new Vector3D(this.two),
				new Vector3D(this.three) };
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
		Point3D pMin = new Point3D(GeomUtils.getMin(zero.getX(), one.getX(),
				this.two.getX(), three.getX()), GeomUtils.getMin(zero.getY(),
				one.getY(), two.getY(), three.getY()), GeomUtils.getMin(zero
				.getZ(), one.getZ(), two.getZ(), three.getZ()));
		Point3D pMax = new Point3D(GeomUtils.getMax(zero.getX(), one.getX(),
				two.getX(), three.getX()), GeomUtils.getMax(zero.getY(), one
				.getY(), two.getY(), three.getY()), GeomUtils.getMax(zero
				.getZ(), this.one.getZ(), two.getZ(), three.getZ()));
		return new MBB3D(pMin, pMax);
	}

	/**
	 * Returns the Triangle3D[] of this.<br>
	 * The array contains for every index the triangle which lies in opposite to
	 * the point of the given index.<br>
	 * Index 0 (=P0) - Triangle [P1,P3,P2] <br>
	 * Index 1 (=P1) - Triangle [P0,P2,P3] <br>
	 * Index 2 (=P2) - Triangle [P1,P0,P3] <br>
	 * Index 3 (=P3) - Triangle [P0,P1,P2] <br>
	 * 
	 * @return Triangle3D[] - of this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Triangle3D[] getTriangles() {
		if (this.triangles == null)
			buildTriangles();

		return triangles;
	}

	/**
	 * Returns the Triangle3D of this lying opposite to the point with given
	 * index.<br>
	 * Index 0 (=P0) - Triangle [P1,P3,P2] <br>
	 * Index 1 (=P1) - Triangle [P0,P2,P3] <br>
	 * Index 2 (=P2) - Triangle [P1,P0,P3] <br>
	 * Index 3 (=P3) - Triangle [P0,P1,P2] <br>
	 * 
	 * @return Triangle3D of this lying opposite to the point with given index.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Triangle3D getTriangle(int index) {
		if (this.triangles == null)
			buildTriangles();

		return triangles[index];
	}

	/**
	 * Returns the index for given Triangle3D,
	 * <code>-1</null> if triangle is not a face.<br>
	 * 
	 * @param triangle
	 *            triangle to check as face
	 * @return int - index of given triangle, -1 if triangle is not a face of
	 *         this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int getTriangleIndex(Triangle3D triangle, ScalarOperator sop) {

		if (this.getTriangle(0).isGeometryEquivalent(triangle, sop))
			return 0;
		if (this.getTriangle(1).isGeometryEquivalent(triangle, sop))
			return 1;
		if (this.getTriangle(2).isGeometryEquivalent(triangle, sop))
			return 2;
		if (this.getTriangle(3).isGeometryEquivalent(triangle, sop))
			return 3;
		return -1;
	}

	/**
	 * Returns the volume of this.
	 * 
	 * @return double - volume.
	 */
	public double getVolume() { // Dag

		Point3D[] p = this.getPoints();

		Vector3D v1 = new Vector3D((p[0].getX() - p[1].getX()),
				(p[0].getY() - p[1].getY()), (p[0].getZ() - p[1].getZ()));
		Vector3D v2 = new Vector3D((p[0].getX() - p[2].getX()),
				(p[0].getY() - p[2].getY()), (p[0].getZ() - p[2].getZ()));
		Vector3D v3 = new Vector3D((p[0].getX() - p[3].getX()),
				(p[0].getY() - p[3].getY()), (p[0].getZ() - p[3].getZ()));

		Matrix3x3 matrix = new Matrix3x3(v1, v2, v3);

		return (Math.abs(matrix.computeDeterminante()) / 6.0);
	}

	/**
	 * Returns the diameter of this (implemented as approximation using diagonal
	 * expansion of MBB).
	 * 
	 * @return double - diameter.
	 */
	public double getDiameter() { // Dag

		double diam = this.getMBB().getDiagonalLength();
		/*
		 * The diameter of the circumscribed sphere results from: -> the
		 * circumscribed sphere contains all the 4 points of the tetrahedron on
		 * its surface ! r = root [ S*(S-aa')*(S-bb')*(S-cc') ] / (6*v), where a
		 * and a' are opposite edges, v is the volume and S = (aa' + bb' + cc')
		 * / 2
		 * 
		 * Calculation by calculating the intersection of planes:
		 * 
		 * Vector3D[] vec = this.getVectors(); Plane3D[] plane = new Plane3D[4];
		 * for ( int i = 0; i < 4 ; i++ ) { // write 4 of 6 possible planes
		 * Vector3D location = Vector3D.mult( Vector3D.add(vec[i],
		 * vec[((i+1)%4)]) , 0.5 ); Vector3D normal = Vector3D.sub(vec[i],
		 * vec[((i+1)%4)]); plane[i] = new Plane3D(normal, location, sop ); }
		 * Line3D line = (Line3D) plane[0].intersection(plane[1]); SimpleGeoObj
		 * sgo = plane[3].intersection(line); if ( sgo.getType() ==
		 * SimpleGeoObj.POINT3D ) diam = ((Point3D)
		 * sgo).euclideanDistance(vec[0].getAsPoint3D()); else { sgo =
		 * plane[2].intersection(line); diam = ((Point3D)
		 * sgo).euclideanDistance(vec[0].getAsPoint3D()); }
		 * 
		 * Calculation by using the determinante:
		 * 
		 * Point3D[] p = this.getPoints(); double a =
		 * p[0].euclideanDistance(p[3]); double aa =
		 * p[1].euclideanDistance(p[2]); double b =
		 * p[0].euclideanDistance(p[2]); double bb =
		 * p[1].euclideanDistance(p[3]); double c =
		 * p[0].euclideanDistance(p[1]); double cc =
		 * p[2].euclideanDistance(p[3]);
		 * 
		 * double v = this.getVolume(); double S = ((a*aa) + (b*bb) + (c*cc)) /
		 * 2.0; double temp = S * (S - (a*aa)) * (S - (b*bb)) * (S - (c*cc));
		 * double r = Math.sqrt(temp) / (6.0*v); diam = 2.0*r;
		 */
		return diam;
	}

	/**
	 * Returns the center of this.
	 * 
	 * @return Point3D - center.
	 */
	public Point3D getCenter() { // Dag
		Point3D[] p = this.getPoints();

		double x = (p[0].getX() + p[1].getX() + p[2].getX() + p[3].getX()) / 4;
		double y = (p[0].getY() + p[1].getY() + p[2].getY() + p[3].getY()) / 4;
		double z = (p[0].getZ() + p[1].getZ() + p[2].getZ() + p[3].getZ()) / 4;
		return new Point3D(x, y, z);

		/*
		 * Alternative: calculation of the centre by calculating the
		 * intersection of two lines, each of which connects the centre of a
		 * tetrahedron face with the opposite vertex (of course 4 lines
		 * intersect in the same point but only 2 are enough to calculate the
		 * point).
		 * 
		 * Line3D line1 = new Line3D( this.getPoint(0) ,
		 * this.getTriangle(0).getCenter() , this.getScalarOperator() ); Line3D
		 * line2 = new Line3D( this.getPoint(1) ,
		 * this.getTriangle(1).getCenter() , this.getScalarOperator() ); //
		 * intersection point must exist return ( (Point3D)
		 * line1.intersection(line2) );
		 */
	}

	/**
	 * Tests whether this intersects the given MBB.
	 * 
	 * @param mbb
	 *            MBB3D for test
	 * @return boolean - true if this intersects with mbb.
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
	public boolean intersects(MBB3D mbb, ScalarOperator sop) { // Dag

		if (!this.getMBB().intersects(mbb, sop))
			return false;
		// test if at least one corner of this is inside of mbb
		for (int i = 0; i < 4; i++) {
			if (mbb.contains(this.getPoint(i), sop))
				return true;
		}
		// test if at least one mbb corner is inside of this
		Point3D[] corner = mbb.getCorners();

		for (int i = 0; i < 8; i++) {
			if (this.contains(corner[i], sop))
				return true;
		}
		// test if at least one face of this intersects
		for (int i = 0; i < 4; i++) {
			Triangle3D tri = this.getTriangle(i);
			if (tri.intersects(mbb, sop))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether the inner of this intersects the given MBB.
	 * 
	 * @param mbb
	 *            MBB3D for test
	 * @return boolean - true if inner of this intersects with mbb.
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
	 *             - if the result of the strict intersection of a Triangle3D
	 *             and a MBB3D is not a simplex. The exception originates in the
	 *             method intersectsStrict(MBB3D, ScalarOperator) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsStrict(MBB3D mbb, ScalarOperator sop) { // Dag

		if (!this.getMBB().intersectsStrict(mbb, sop))
			return false;
		// test if at least one corner of this is strict inside of mbb
		for (int i = 0; i < 4; i++) {
			if (mbb.containsStrict(this.getPoint(i), sop))
				return true;
		}
		// test if all mbb corners are outside this
		Point3D[] corner = mbb.getCorners();

		for (int i = 0; i < 8; i++) {
			if (this.containsStrict(corner[i], sop))
				return true;
		}
		// test if at least one face of this intersects strict
		for (int i = 0; i < 4; i++) {
			Triangle3D tri = this.getTriangle(i);
			if (tri.intersectsStrict(mbb, sop))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this intersects with given Line3D.
	 * 
	 * @param line
	 *            Line3D to test
	 * @return boolean - true if they intersect, false otherwise.
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
	public boolean intersects(Line3D line, ScalarOperator sop) { // Dag
		// intersects(line) for every face of this
		if (this.getTriangle(0).intersects(line, sop)
				|| this.getTriangle(1).intersects(line, sop)
				|| this.getTriangle(2).intersects(line, sop)
				|| this.getTriangle(3).intersects(line, sop))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given Segment3D.
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @return boolean - true if they intersect, false otherwise.
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
	public boolean intersects(Segment3D segment, ScalarOperator sop) { // Dag

		if (this.contains(segment, sop))
			return true;
		// intersects(segment) for every face of this
		if (this.getTriangle(0).intersects(segment, sop)
				|| this.getTriangle(1).intersects(segment, sop)
				|| this.getTriangle(2).intersects(segment, sop)
				|| this.getTriangle(3).intersects(segment, sop))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given Plane3D.
	 * 
	 * @param plane
	 *            Plane3D to test
	 * @return boolean - true if they intersect, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Plane3D plane, ScalarOperator sop) { // Dag
		// check intersects(plane) for every face of this
		if (this.getTriangle(0).intersects(plane, sop)
				|| this.getTriangle(1).intersects(plane, sop)
				|| this.getTriangle(2).intersects(plane, sop)
				|| this.getTriangle(3).intersects(plane, sop))
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	public boolean intersects(Triangle3D triangle, ScalarOperator sop) { // Dag

		if (this.contains(triangle, sop))
			return true;
		// intersects(triangle) for every face of this
		if (this.getTriangle(0).intersects(triangle, sop)
				|| this.getTriangle(1).intersects(triangle, sop)
				|| this.getTriangle(2).intersects(triangle, sop)
				|| this.getTriangle(3).intersects(triangle, sop))
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given Tetrahedron3D.
	 * 
	 * @param tetra
	 *            Tetrahedron3D.
	 * @return boolean - true if they intersect, false otherwise.
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
	public boolean intersects(Tetrahedron3D tetra, ScalarOperator sop) { // Dag

		if (!this.getMBB().intersects(tetra.getMBB(), sop))
			return false;
		// test if this contains tetra totally or vise versa
		if (this.contains(tetra.getPoint(0), sop)
				&& this.contains(tetra.getPoint(1), sop)
				&& this.contains(tetra.getPoint(2), sop)
				&& this.contains(tetra.getPoint(3), sop))
			return true;
		if (tetra.contains(this.getPoint(0), sop)
				&& tetra.contains(this.getPoint(1), sop)
				&& tetra.contains(this.getPoint(2), sop)
				&& tetra.contains(this.getPoint(3), sop))
			return true;

		/*
		 * test intersection for every Segment of tetra with every face of this,
		 * and vice versa
		 */
		Point3D[] points = tetra.getPoints();
		Point3D[] pts = this.getPoints();
		for (int i = 0; i < 3; i++) {
			for (int j = (i + 1); j < 4; j++) {
				Segment3D segment = new Segment3D(points[i], points[j], sop);
				if (this.getTriangle(0).intersects(segment, sop)
						|| this.getTriangle(1).intersects(segment, sop)
						|| this.getTriangle(2).intersects(segment, sop)
						|| this.getTriangle(3).intersects(segment, sop))
					return true;
				segment = new Segment3D(pts[i], pts[j], sop);
				if (tetra.getTriangle(0).intersects(segment, sop)
						|| tetra.getTriangle(1).intersects(segment, sop)
						|| tetra.getTriangle(2).intersects(segment, sop)
						|| tetra.getTriangle(3).intersects(segment, sop))
					return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether this intersects the given plane and returns a value for the
	 * dimension of the resulting object. Possible results are: [ -1 for no
	 * intersection, 0 ->0D (point), 1 ->1D (segment), 2 ->2D (triangle) or 3
	 * ->(PointSet in 2D) ].
	 * 
	 * @param plane
	 *            Plane3D
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, ScalarOperator) of this class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Plane3D plane, ScalarOperator sop) { // Dag
		// evaluation of intersection(plane) return
		SimpleGeoObj result = this.intersection(plane, sop);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		if (result == null)
			return -1;

		switch (result.getType()) {
		case SimpleGeoObj.POINT3D:
			return 0;
		case SimpleGeoObj.SEGMENT3D:
			return 1;
		default:
			// result must be SimpleGeoObj.TRIANGLE3D or WIREFRAME3D with dim=2
			return 2;
		}
	}

	/**
	 * Tests whether this intersects the given line and returns a value for the
	 * dimension of the resulting object. Possible results are: [ -1 for no
	 * intersection, 0 ->0D (point) or 1 ->1D (segment) ].
	 * 
	 * @param line
	 *            Line3D for test
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, ScalarOperator) of this
	 *             class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Line3D line, ScalarOperator sop) { // Dag
		// evaluation of intersection(line) return
		SimpleGeoObj result = this.intersection(line, sop);

		if (result == null)
			return -1;

		switch (result.getType()) {
		case SimpleGeoObj.SEGMENT3D:
			return 1;
		default: // result must be SimpleGeoObj.POINT3D:
			return 0;
		}
	}

	/**
	 * Tests whether this intersects the given segment and returns a value for
	 * the dimension of the resulting object. Possible results are: [ -1 for no
	 * intersection, 0 ->0D (point) or 1 ->1D (segment) ].
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, ScalarOperator) of this
	 *             class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Segment3D segment, ScalarOperator sop) { // Dag
		// evaluation of intersection(segment) return
		SimpleGeoObj result = this.intersection(segment, sop);

		if (result == null)
			return -1;

		switch (result.getType()) {
		case SimpleGeoObj.SEGMENT3D:
			return 1;
		default: // result must be SimpleGeoObj.POINT3D:
			return 0;
		}
	}

	/**
	 * Tests whether this intersects the given triangle and returns a value for
	 * the dimension of the resulting object. Possible results are: [ -1 for no
	 * intersection, 0 ->0D (point), 1 ->1D (segment) or 2 ->2D (triangle or
	 * PointSet in 2D) ].
	 * 
	 * @param triangle
	 *            Triangle3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Tetrahedron3D and a
	 *             Plane3D is not a 3D simplex. The exception originates in the
	 *             method intersection(Plane3D, ScalarOperator) of the class
	 *             Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public int intersectsInt(Triangle3D triangle, ScalarOperator sop) { // Dag
		// evaluation of intersection(triangle) return
		SimpleGeoObj result = this.intersection(triangle, sop);

		if (result == null)
			return -1;

		switch (result.getType()) {
		case SimpleGeoObj.TRIANGLE3D:
			return 2;
		case SimpleGeoObj.SEGMENT3D:
			return 1;
		case SimpleGeoObj.POINT3D:
			return 0;
		default: // must be SimpleGeoObj.WIREFRAME3D with points in a plane
			return 2;
		}
	}

	/**
	 * Tests whether this intersects the given tetrahedra and returns a value
	 * for the dimension of the resulting object. Possible results are: [ -1 for
	 * no intersection, 0 ->0D (point), 1 ->1D (segment), 2 ->2D (triangle) or 3
	 * ->3D (Wireframe) ].
	 * 
	 * @param tetra
	 *            Tetrahedron3D for test
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection is not a simplex.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this and a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, ScalarOperator) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
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
	public int intersectsInt(Tetrahedron3D tetra, ScalarOperator sop) { // Dag

		// Auswertung der Rueckgabe von intersection(tetra)
		SimpleGeoObj result = this.intersection(tetra, sop);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		if (result == null)
			return -1;
		else {

			switch (result.getType()) {
			case SimpleGeoObj.WIREFRAME3D:
				return ((Wireframe3D) result).getDimension();
			case SimpleGeoObj.TETRAHEDRON3D:
				return 3;
			case SimpleGeoObj.TRIANGLE3D:
				return 2;
			case SimpleGeoObj.SEGMENT3D:
				return 1;
			case SimpleGeoObj.POINT3D:
				return 0;
			default: // must be NULL:
				throw new IllegalStateException(Db3dSimpleResourceBundle
						.getString("db3d.geom.resnotsimplex"));
			}
		}
	}

	/**
	 * Tests whether this intersects with given plane in dimension 2.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this and a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, ScalarOperator) of this class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsRegular(Plane3D plane, ScalarOperator sop) {
		if (this.intersectsInt(plane, sop) == 2)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given line in dimension 1.
	 * 
	 * @param line
	 *            Line3D for test
	 * @return boolean - tests if interesects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, ScalarOperator) of this
	 *             class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsRegular(Line3D line, ScalarOperator sop) {
		if (this.intersectsInt(line, sop) == 1)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given segment in dimension 1.
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @return boolean - tests if interesects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, ScalarOperator) of this
	 *             class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsRegular(Segment3D segment, ScalarOperator sop) {
		if (this.intersectsInt(segment, sop) == 1)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given triangle in dimension 2.
	 * 
	 * @param triangle
	 *            Triangle3D for test
	 * @return boolean - tests if interesects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Tetrahedron3D and a
	 *             Plane3D is not a 3D simplex. The exception originates in the
	 *             method intersection(Plane3D, ScalarOperator) of the class
	 *             Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean intersectsRegular(Triangle3D triangle, ScalarOperator sop) {
		if (this.intersectsInt(triangle, sop) == 2)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given tetrahedron in dimension 3.
	 * 
	 * @param tetra
	 *            Tetrahedron3D for test
	 * @return boolean - tests if interesects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of two tetrahedrons is
	 *             not a simplex. The exception originates in the method
	 *             intersectsInt(Tetrahedron3D, ScalarOperator) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this and a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, ScalarOperator) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
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
	public boolean intersectsRegular(Tetrahedron3D tetra, ScalarOperator sop) {
		if (this.intersectsInt(tetra, sop) == 3)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given point is contained in this.<br>
	 * Method doesn't assume that normvectors of triangles of this show in
	 * specific direction.
	 * 
	 * @param point
	 *            Point3D for test
	 * @return boolean - true if contained, false otherwise.
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
	public boolean contains(Point3D point, ScalarOperator sop) { // Dag
		// Testing for MBB
		if (!this.getMBB().contains(point, sop))
			return false;

		/*
		 * The point is cointained if: (1) it belongs to one of the triangles of
		 * the tetrahedron; (2) for ALL four triangles of the tetrahedron, it
		 * lies on the "inner side" of the plane of each triangle.
		 */
		for (int i = 0; i < 4; i++) {
			Triangle3D triangle = this.getTriangle(i);

			if (triangle.contains(point, sop))
				return true;
			else {
				Vector3D showInsideVector = new Vector3D(triangle.getPoint(0),
						this.getPoint(i));
				int orientation = triangle
						.getOrientation(showInsideVector, sop);

				Vector3D showToPointVector = new Vector3D(triangle.getPoint(0),
						point);
				if ((triangle.getOrientation(showToPointVector, sop) != orientation)
						|| (triangle.getOrientation(showToPointVector, sop) == 0))
					return false;
			}
		}

		/*
		 * This point can only be reached if "point is inside" is true for all
		 * triangles of this.
		 */
		return true;
	}

	/**
	 * Tests whether the given point is STRICTLY contained in this.<br>
	 * Method doesn't assume that normvectors of triangles of this show in
	 * specific direction.
	 * 
	 * @param point
	 *            Point3D for test
	 * @return boolean - true if contained, false otherwise.
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
	public boolean containsStrict(Point3D point, ScalarOperator sop) { // Dag
		// Testing for MBB
		if (!this.getMBB().containsStrict(point, sop))
			return false;

		/*
		 * The point is cointained if: (1) it belongs to one of the triangles of
		 * the tetrahedron; (2) for ALL four triangles of the tetrahedron, it
		 * lies on the "inner side" of the plane of each triangle.
		 */
		for (int i = 0; i < 4; i++) {
			Triangle3D triangle = this.getTriangle(i);

			if (triangle.contains(point, sop))
				return false;
			else {
				Vector3D showInsideVector = new Vector3D(triangle.getPoint(0),
						this.getPoint(i));
				int orientation = triangle
						.getOrientation(showInsideVector, sop);

				Vector3D showToPointVector = new Vector3D(triangle.getPoint(0),
						point);
				if ((triangle.getOrientation(showToPointVector, sop) != orientation)
						|| (triangle.getOrientation(showToPointVector, sop) == 0))
					return false;
			}
		}

		/*
		 * This point can only be reached if "point is inside" is true for all
		 * triangles of this.
		 */
		return true;
	}

	/**
	 * Tests whether the given segment is contained in this.
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @return boolean - true if contained, false otherwise.
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
	public boolean contains(Segment3D segment, ScalarOperator sop) { // Dag
		// all points are contained <=> the segment is contained
		if (this.contains(segment.getPoint(0), sop)
				&& this.contains(segment.getPoint(1), sop))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given triangle is contained in this.
	 * 
	 * @param triangle
	 *            Triangle3D for test
	 * @return boolean - true if contained, false otherwise.
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
	public boolean contains(Triangle3D triangle, ScalarOperator sop) { // Dag
		// all points are contained <=> the triangle is contained
		if (this.contains(triangle.getPoint(0), sop)
				&& this.contains(triangle.getPoint(1), sop)
				&& this.contains(triangle.getPoint(2), sop))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given tetrahedron is contained in this.
	 * 
	 * @param tetra
	 *            Tetrahedron for test
	 * @return boolean - true if contained, false otherwise.
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
	public boolean contains(Tetrahedron3D tetra, ScalarOperator sop) { // Dag
		// all points are contained <=> the tetrahedron is contained
		if (this.contains(tetra.getPoint(0), sop)
				&& this.contains(tetra.getPoint(1), sop)
				&& this.contains(tetra.getPoint(2), sop)
				&& this.contains(tetra.getPoint(3), sop))
			return true;
		else
			return false;
	}

	/**
	 * Computes the intersection of this and the given line.<br>
	 * Returns <code>null</code> if no intersection occures.<br>
	 * Retruns a Point3D or Segment3D object dependent on how the line
	 * touches/intersects this.
	 * 
	 * @param line
	 *            Line3D for computation
	 * @return SimpleGeoObj - result of intersection.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection is not Segment3D, Point3D
	 *             or null.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) { // Dag
		/*
		 * Intersection of line with every face of this. If intersection returns
		 * a Segment3D object, this is returned as result. Returned Point3D
		 * objects get collected in a PointSet3D.
		 */

		Triangle3D[] triangles1 = this.getTriangles();

		PointSet3D points = new PointSet3D(sop);
		for (int i = 0; i < 4; i++) {

			SimpleGeoObj lineTriangleIntersection = triangles1[i].intersection(
					line, sop);

			if (lineTriangleIntersection != null) {
				if (lineTriangleIntersection.getType() == SimpleGeoObj.SEGMENT3D)
					return lineTriangleIntersection;
				else
					// type is POINT3D
					points.add((Point3D) lineTriangleIntersection);
			}
		}

		switch (points.size()) {

		case 0:
			return null;

		case 1:
			Iterator<Point3D> it = points.iterator();
			return it.next();

		case 2:
			it = points.iterator();
			return new Segment3D(it.next(), it.next(), sop);

		default:
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.resnotnullposeg"));
		}
	}

	/**
	 * Computes the intersection of this and the given segment.<br>
	 * Returns <code>null</code> if no intersection occures.<br>
	 * Retruns a Point3D or Segment3D object dependent on how the segment
	 * touches/intersects this.
	 * 
	 * @param segment
	 *            Segment3D for computation
	 * @return SimpleGeoObj - result of intersection.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, ScalarOperator) of this
	 *             class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Segment3D segment, ScalarOperator sop) { // Dag
		/*
		 * Use intersection(line) - if result is Segment3D ->
		 * Segment.intersectionOnLine(segment)
		 */

		if (!this.getMBB().intersects(segment.getMBB(), sop))
			return null;

		SimpleGeoObj lineIntersection = this.intersection(segment.getLine(sop),
				sop);

		if (lineIntersection == null)
			return null;

		if (lineIntersection.getType() == SimpleGeoObj.POINT3D)
			return lineIntersection;
		// else
		return segment.intersectionOnLine(((Segment3D) lineIntersection), sop);
	}

	/**
	 * Computes the intersection of this and the given plane.<br>
	 * Returns <code>null</code> if no intersection occures. Returns Point3D,
	 * Segment3D, Triangle3D or Wireframe3D dependent on how this and the given
	 * plane intersect.
	 * 
	 * @param plane
	 *            Plane3D for computation
	 * @return SimpleGeoObj - resulting object.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection is not a 3D simplex.
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
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) { // Dag

		Triangle3D[] triangles1 = this.getTriangles();

		Wireframe3D wf = new Wireframe3D(sop);

		for (int i = 0; i < 4; i++) {
			SimpleGeoObj result = triangles1[i].intersection(plane, sop);

			if (result != null) {
				switch (result.getType()) {
				case SimpleGeoObj.TRIANGLE3D:
					return result;
				case SimpleGeoObj.SEGMENT3D:
					wf.add(((Segment3D) result));
					break;
				case SimpleGeoObj.POINT3D:
					wf.add(((Point3D) result));
					break;
				default:
					throw new IllegalStateException(Db3dSimpleResourceBundle
							.getString("db3d.geom.resnotthreedsimplex"));
				}
			}
		}

		switch (wf.countNodes()) {

		case 4: // could max be four !!
			return wf;

		case 3: // result is a triangle given by the three segments
			return new Triangle3D(wf.getPoints()[0], wf.getPoints()[1], wf
					.getPoints()[2], sop);

		case 2: // result is a segment
			return wf.getSegments()[0];

		case 1: // result is a point
			return wf.getPoints()[0];

		case 0: // result is null
			return null;

		default:
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.resnotsimplex"));
		}
	}

	/**
	 * Computes the intersection of this and the given triangle.<br>
	 * Returns <code>null</code> if no intersection occures. Returns Point3D,
	 * Segment3D, Triangle3D or Wireframe3D dependent on how this and the given
	 * triangle intersect.
	 * 
	 * @param triangle
	 *            Triangle3D for computation
	 * @return SimpleGeoObj - result of intersection.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, ScalarOperator) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
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
	public SimpleGeoObj intersection(Triangle3D triangle, ScalarOperator sop) { // Dag
		// idea: intersect the intersection result of this.intersection(plane of
		// triangle) with triangle
		if (!(this.getMBB().intersects(triangle.getMBB(), sop)))
			return null;

		SimpleGeoObj planeIntersectionResult = this.intersection(triangle
				.getPlane(sop), sop);
		if (planeIntersectionResult != null) {
			switch (planeIntersectionResult.getType()) {

			case SimpleGeoObj.POINT3D:
				Point3D p = (Point3D) planeIntersectionResult;
				if (triangle.containsInPlane(p, sop))
					return p;
				else
					return null;

			case SimpleGeoObj.SEGMENT3D:
				return triangle.intersectionInPlane(
						((Segment3D) planeIntersectionResult), sop);

			case SimpleGeoObj.TRIANGLE3D:
				return triangle.intersectionInPlane(
						((Triangle3D) planeIntersectionResult), sop);

			case SimpleGeoObj.WIREFRAME3D:
				Wireframe3D wf = (Wireframe3D) planeIntersectionResult;
				return intersectionInPlane(triangle, wf, sop);

			default: // no intersection
				return null;
			}
		} else
			return null;
	}

	/*
	 * Special method to compute the intersection of a given triangle and the
	 * given wireframe which has to be triangulatable into two triangles (flat
	 * wireframe with four segments) in the same plane as the given triangle.
	 * Method is used in .projection(triangle) and .intersection(triangle).
	 * 
	 * Returns <code>null</code> if no intersection occures. Returns Point3D,
	 * Segment3D, Triangle3D or Wireframe3D dependent on how the triangle and
	 * the wireframe intersect.
	 * 
	 * @param triangle Triangle3D
	 * 
	 * @param wf Wireframe3D (must be triangulatable into two triangles lying in
	 * the same plane as triangle)
	 * 
	 * @return SimpleGeoObj - result of intersection.
	 * 
	 * @throws IllegalStateException - if the intersectsInt(Line3D line,
	 * ScalarOperator sop) method of the class Line3D (which computes the
	 * intersection of two lines) called by this method returns a value that is
	 * not -2, -1, 0 or 1.
	 * 
	 * @throws IllegalStateException - signals Problems with the dimension of
	 * the wireframe.
	 * 
	 * @throws IllegalStateException - if the result of the intersection is not
	 * a 3D simplex.
	 * 
	 * @throws IllegalArgumentException - if validation of a Triangle3D fails.
	 * The exception originates in the constructor Triangle3D(Point3D, Point3D,
	 * Point3D, ScalarOperator).
	 * 
	 * @throws IllegalArgumentException - if validation of a Triangle3D fails.
	 * The exception originates in the constructor Triangle3D(Point3D, Point3D,
	 * Point3D, ScalarOperator).
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private SimpleGeoObj intersectionInPlane(Triangle3D triangle,
			Wireframe3D wf, ScalarOperator sop) { // Dag

		Wireframe3D resultWireframe = new Wireframe3D(sop);
		Segment3D[] s = wf.getSegments(); // must contain four segments
		// (1) intersect segments of wireframe with triangle
		for (int i = 0; i < 4; i++) {
			SimpleGeoObj obj = triangle.intersectionInPlane(s[i], sop);

			if (obj != null) {
				if (obj.getType() == SimpleGeoObj.POINT3D)
					resultWireframe.add((Point3D) obj);
				else {// must be Segment3D
					resultWireframe.add((Segment3D) obj);
				}
			}
		}

		// (2) intersect triangles of wireframe with segments of triangle and
		// collect results in a wireframe object
		Triangle3D[] triangles1 = wf.getTriangulated();
		Segment3D remember = null;
		Segment3D seg;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				SimpleGeoObj obj = triangles1[j].intersectionInPlane(triangle
						.getSegment(i), sop);
				if (obj != null) {
					if (obj.getType() == SimpleGeoObj.SEGMENT3D) {
						if (j == 0)
							remember = (Segment3D) obj;
						else {
							seg = (Segment3D) obj;
							if (remember != null) {
								Point3D[] points = { seg.getPoint(0),
										seg.getPoint(1), remember.getPoint(0),
										remember.getPoint(1) };
								Point3D[] p = GeomUtils
										.getPointsWithMaxDistance(points);
								remember = null;
								resultWireframe.add(new Segment3D(p[0], p[1],
										sop));
							} else {
								resultWireframe.add(seg);
							}
						}
					} else if (remember != null) {
						resultWireframe.add(remember);
						remember = null;
					}
				} else if (remember != null) {
					resultWireframe.add(remember);
					remember = null;
				}
			}
		}

		// analyse resultWireframe by counting its nodes -> different
		// intersection cases
		int nodeCount = resultWireframe.countNodes();
		if (nodeCount == 0)
			return null;
		if (nodeCount >= 4)
			return resultWireframe;

		Point3D[] p = resultWireframe.getPoints();
		switch (nodeCount) {
		case 1:
			return p[0];
		case 2:
			return new Segment3D(p[0], p[1], sop);
		case 3:
			return new Triangle3D(p[0], p[1], p[2], sop);
		default:
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.resnotthreedsimplex"));
		}
	}

	/**
	 * Computes the intersection of this and the given tetrahedron.<br>
	 * Returns <code>null</code> if no intersection occures. Returns Point3D,
	 * Segment3D, Triangle3D, Tetrahedron3D or Wireframe3D dependent on how this
	 * and the given tetrahedron intersect.
	 * 
	 * @param tetra
	 *            Tetrahedron3D for computation
	 * @return SimpleGeoObj - result of intersection.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this and a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, ScalarOperator) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
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
	 * 
	 */
	public SimpleGeoObj intersection(Tetrahedron3D tetra, ScalarOperator sop) { // Dag

		if (!(this.getMBB().intersects(tetra.getMBB(), sop)))
			return null;
		// check if this contains tetra and vice versa
		if (this.contains(tetra, sop))
			return tetra;
		if (tetra.contains(this, sop))
			return new Tetrahedron3D(this);

		Wireframe3D resultWF = new Wireframe3D(sop);
		Triangle3D[] triangles1 = this.getTriangles();
		Tetrahedron3D tet = tetra;

		// intersect this.faces with tetra and tetra.faces with this - collect
		// results in a wireframe object
		for (int k = 0; k < 2; k++) {

			if (k == 1) {
				triangles1 = tetra.getTriangles();
				tet = this;
			}

			for (int i = 0; i < 4; i++) {
				SimpleGeoObj obj = tet.intersection(triangles1[i], sop);
				if (obj != null) {
					switch (obj.getType()) {

					case SimpleGeoObj.POINT3D:
						resultWF.add((Point3D) obj);
						break;

					case SimpleGeoObj.SEGMENT3D:
						resultWF.add((Segment3D) obj);
						break;

					case SimpleGeoObj.TRIANGLE3D:
						resultWF.add((Triangle3D) obj);
						// Here an IllegalStateException can be thrown signaling
						// problems with the dimensions of the wireframe.
						break;

					case SimpleGeoObj.WIREFRAME3D:
						resultWF.add((Wireframe3D) obj);
						// Here an IllegalStateException can be thrown signaling
						// problems with the dimensions of the wireframe.
						break;

					default:
						break;
					}
				}
			}
		}

		// return result dependent on count of nodes
		switch (resultWF.countNodes()) {
		case 4:
			Point3D[] p = resultWF.getPoints();
			SimpleGeoObj result;
			try {
				result = new Tetrahedron3D(p[0], p[1], p[2], p[3], sop);
			} catch (IllegalArgumentException e) {
				result = new Wireframe3D(sop);
				((Wireframe3D) result).add(p);
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimension of the wireframe.
			}
			return result;
		case 3: // result is a triangle given by the three segments
			p = resultWF.getPoints();
			return new Triangle3D(p[0], p[1], p[2], sop);
		case 2: // result is a segment
			p = resultWF.getPoints();
			return new Segment3D(p[0], p[1], sop);
		case 1: // result is a point
			return resultWF.getPoints()[0];
		case 0: // result is null
			return null;
		default:
			return resultWF;
		}
	}

	/**
	 * Projects this onto the given plane. Returns a Triangle3D or Wireframe3D
	 * object.
	 * 
	 * @param plane
	 *            Plane3D for projection
	 * @return SimpleGeoObj - result of projection.
	 * @throws IllegalStateExcetion
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj projection(Plane3D plane, ScalarOperator sop) { // Dag
		// projection of four corner points and test if on of projected results
		// is contained in the triangle of other three

		Point3D[] projectedPoints = new Point3D[4];
		for (int i = 0; i < 4; i++)
			projectedPoints[i] = (Point3D) this.getPoint(i).projection(plane);

		Triangle3D triangle = null;

		triangle = new Triangle3D(projectedPoints[1], projectedPoints[2],
				projectedPoints[3], null);
		if (triangle.isValid(sop))
			if (triangle.contains(projectedPoints[0], sop))
				return triangle;

		triangle = new Triangle3D(projectedPoints[2], projectedPoints[3],
				projectedPoints[0], null);
		if (triangle.isValid(sop))
			if (triangle.contains(projectedPoints[1], sop))
				return triangle;

		triangle = new Triangle3D(projectedPoints[3], projectedPoints[0],
				projectedPoints[1], null);
		if (triangle.isValid(sop))
			if (triangle.contains(projectedPoints[2], sop))
				return triangle;

		triangle = new Triangle3D(projectedPoints[0], projectedPoints[1],
				projectedPoints[2], null);
		if (triangle.isValid(sop))
			if (triangle.contains(projectedPoints[3], sop))
				return triangle;

		Line3D line;
		int oppositeIndex = -1;

		for (int i = 0; i < 3; i++) {
			line = new Line3D(projectedPoints[3], projectedPoints[i], sop);
			if (triangle.intersectsInt(line, sop) == 1) {
				oppositeIndex = i;
				break;
			}
		}

		Segment3D[] s = new Segment3D[4];

		s[0] = triangle.getSegment(((oppositeIndex + 1) % 3));
		s[1] = triangle.getSegment(((oppositeIndex + 2) % 3));
		s[2] = new Segment3D(projectedPoints[((oppositeIndex + 1) % 3)],
				projectedPoints[3], sop);
		s[3] = new Segment3D(projectedPoints[((oppositeIndex + 2) % 3)],
				projectedPoints[3], sop);

		Wireframe3D resultWireframe = new Wireframe3D(sop);
		resultWireframe.add(s);
		return resultWireframe;
	}

	/**
	 * Projects this onto the given triangle. Returns a Point3D, Segment3D,
	 * Triangle3D or PointSet3D object.
	 * 
	 * @param triangle
	 *            Triangle3D for projection
	 * @return SimpleGeoObj - result of projection.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             ScalarOperator3D) of this class.
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
	public SimpleGeoObj projection(Triangle3D triangle, ScalarOperator sop) { // Dag
		// projection on plane of triangle and following intersecton with it
		SimpleGeoObj projectionOnPlane = this.projection(
				triangle.getPlane(sop), sop);

		if (projectionOnPlane.getType() == SimpleGeoObj.TRIANGLE3D)
			return ((Triangle3D) projectionOnPlane).intersectionInPlane(
					triangle, sop);

		else { // projectionOnPlane must be of type WIREFRAME3D
			Wireframe3D wf = (Wireframe3D) projectionOnPlane;
			return intersectionInPlane(triangle, wf, sop);
		}
	}

	/**
	 * Checks whether this is equal to given Tetrahedron3D.<br>
	 * Coordinate equality test / instanceof test.<br>
	 * Test is against the coordinates - so subclasses can be tested also !
	 * 
	 * @param tetra
	 *            Tetrahedron3D for test
	 * @return boolean - true if equal.
	 */
	public boolean isEqual(Tetrahedron3D tetra, ScalarOperator sop) {
		Point3D[] geom = tetra.getPoints();
		Point3D[] points = this.getPoints();
		for (int i = 0; i < 4; i++)
			if (points[i].isEqual(geom[i], sop))
				return false;

		return true;
	}

	/**
	 * Validates if the given points are a valid tetrahedron.
	 * 
	 * @param sop
	 *            ScalarOperator for test
	 * @return boolean - true if valid, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isValid(ScalarOperator sop) {
		// test for geometric equality between all points
		if (this.zero.isEqual(this.one, sop)
				|| this.zero.isEqual(this.two, sop)
				|| this.zero.isEqual(this.three, sop)
				|| this.one.isEqual(this.two, sop)
				|| this.one.isEqual(this.three, sop)
				|| this.two.isEqual(this.three, sop))
			return false;

		return !sop.equal(new Plane3D(this.zero, this.one, this.two, sop)
				.distance(this.three), 0);
	}

	/**
	 * Checks whether this is a regular tetrahedron.
	 * 
	 * @param sop
	 *            ScalarOperator for test
	 * @return boolean - true if regular, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isRegular(ScalarOperator sop) { // Dag
		// test if volume exceeds minimum threshold

		for (int i = 0; i < 4; i++)
			if (!this.getTriangle(i).isRegular(sop))
				return false;

		if (!(this.getVolume() > (SimpleGeoObj.MIN_VOLUME_EPSILON_FACTOR * sop
				.getEpsilon())))
			return false;
		else
			return true;

	}

	/**
	 * Checks whether this is a "beautiful" tetrahedron. "Beautiful" accounts
	 * for the proportion of a tetrahedron. Very long and narrow tetrahedrons
	 * (small solid angle) are going to be evaluated as not "beautiful".
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if this is "well proportionate", false if
	 *         disproportionate.
	 */
	public boolean isBeautiful(ScalarOperator sop) { // Dag
		// test if solid angles exceed threshold
		double angles[] = getAngles(sop);

		for (int i = 0; i < 4; i++) {
			if (angles[i] < (SimpleGeoObj.MIN_AREA_EPSILON_FACTOR * sop
					.getEpsilon()))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether this is a completely validated triangle.<br>
	 * This method performs an isValid,isRegular and isBeautiful test.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if validated, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isCompleteValidated(ScalarOperator sop) {
		return isValid(sop) && isRegular(sop) && isBeautiful(sop);
	}

	/**
	 * Strict equal test.<br>
	 * All the points must be equal in the same index (not in case of Point3D).<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equal, false otherwise.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isEqual(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Tetrahedron3D))
			return false;
		Point3D[] geom = ((Tetrahedron3D) obj).getPoints();
		Point3D[] points = this.getPoints();
		for (int i = 0; i < 4; i++)
			if (points[i].isEqual(geom[i], sop))
				return false;

		return true;
	}

	/**
	 * Geometry equivalence test.<br>
	 * The objects must have the same points, but the index position makes no
	 * difference.<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equal, false otherwise.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isGeometryEquivalent(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Tetrahedron3D))
			return false;
		Point3D[] ps1 = GeomUtils.getSorted(this.getPoints());
		Point3D[] ps2 = GeomUtils.getSorted(((Tetrahedron3D) obj).getPoints());
		int length = ps1.length;
		for (int i = 0; i < length; i++)
			if (!ps1[i].isEqual(ps2[i], sop))
				return false;

		return true;
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
		result = prime * result + getPoint(0).isEqualHC(factor);
		result = prime * result + getPoint(1).isEqualHC(factor);
		result = prime * result + getPoint(2).isEqualHC(factor);
		result = prime * result + getPoint(3).isEqualHC(factor);
		return result;

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
		// FIXME fix to work without apache common hashcodebuilder
		final int prime = 31;
		int result = 1;
		Point3D[] ps = GeomUtils.getSorted(this.getPoints());
		int length = ps.length;
		for (int i = 0; i < length; i++)
			result = prime * result + ps[i].isGeometryEquivalentHC(factor);
		return result;
	}

	/**
	 * Tests if given object is the same class type.
	 * 
	 * @param obj
	 *            Object
	 * @return boolean - true if this and the given object are of the same class
	 *         type.
	 * @see db3d.dbms.geom.Equivalentable#isEqualClass(java.lang.Object)
	 */
	public boolean isEqualClass(Object obj) {
		if (this.getClass() != obj.getClass())
			return false;
		return true;
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return TETRAHEDRON3D always.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.TETRAHEDRON3D;
	}

	// private methods

	/*
	 * Builds the Triangles of this and saves them in the transient variable
	 * <code>triangles</codes> for further processing. The normalvectors of
	 * <code>triangles</codes> point to outside direction. Method assumes the
	 * ensureOrder method has oriented face zero before.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(ScalarOperator) of the class
	 * Vector3D.
	 */
	private void buildTriangles() { // Dag
		Point3D[] points = this.getPoints();
		this.triangles = new Triangle3D[4];
		// create triangles
		this.triangles[0] = new Triangle3D(points[1], points[2], points[3],
				null);
		this.triangles[1] = new Triangle3D(points[0], points[3], points[2],
				null);
		this.triangles[2] = new Triangle3D(points[0], points[1], points[3],
				null);
		this.triangles[3] = new Triangle3D(points[0], points[2], points[1],
				null);
	}

	/*
	 * Ensures the order of face opposite to point[0] - normal vector has
	 * outside direction. Needed by method buildTriangle - relies on correct
	 * orientation of triangle opposite to point[0] made by this method.
	 */
	private void ensureOrder() {
		Point3D[] points = this.getPoints();
		// ensure orientation
		Vector3D showInsideVector = new Vector3D(points[1], points[0]);
		// calc vector pointing in normvector direction
		double x = points[1].getY() * (points[2].getZ() - points[3].getZ())
				+ points[0].getY() * (points[3].getZ() - points[1].getZ())
				+ points[3].getY() * (points[1].getZ() - points[2].getZ());
		double y = points[1].getZ() * (points[2].getX() - points[3].getX())
				+ points[0].getZ() * (points[3].getX() - points[1].getX())
				+ points[3].getZ() * (points[1].getX() - points[2].getX());
		double z = points[1].getX() * (points[2].getY() - points[3].getY())
				+ points[0].getX() * (points[3].getY() - points[1].getY())
				+ points[3].getX() * (points[1].getY() - points[2].getY());
		Vector3D normvecDirection = new Vector3D(x, y, z);

		double orientation = showInsideVector.scalarproduct(normvecDirection);

		if (orientation > 0) { // exchange P2 and P3, else orientation is ok
			Point3D temp = points[2];
			points[2] = points[3];
			points[3] = temp;
		}
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Tetrahedron3D [one=" + one + ", three=" + three
				+ ", triangles=" + Arrays.toString(triangles) + ", two=" + two
				+ ", zero=" + zero + "]";
	}

	@Override
	/**
	 * Returns the hash code of this.
	 * @return int - hash code of this.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((one == null) ? 0 : one.hashCode());
		result = prime * result + ((three == null) ? 0 : three.hashCode());
		result = prime * result + ((two == null) ? 0 : two.hashCode());
		result = prime * result + ((zero == null) ? 0 : zero.hashCode());
		return result;
	}

}
