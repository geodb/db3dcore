/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.geometries3d;

import java.util.Iterator;

import de.uos.igf.db3d.dbms.exceptions.ValidationException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * Tetrahedron3D is the geometric representation of a tetrahedron simplex in 3D.
 * 
 * A Tetrahedron3D object is modeled by four Point3D objects. The retrieved
 * triangles from this tetrahedron are oriented by their normal vector facing
 * outside of the tetrahedron. This orientation relies on the two internal
 * methods ensureOrder and buildTriangles.
 */
public class Tetrahedron3D extends Geometry3DAbst {

	/* array of points */
	protected Point3D[] points = null;

	/**
	 * Constructor.
	 * 
	 * @param pts
	 *            Point3D array
	 * @param epsilon
	 *            GeoEpsilon needed for validation<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3D(Point3D[] pts, GeoEpsilon epsilon) {
		if (pts == null || pts.length != 4)
			throw new IllegalArgumentException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.tetraconsfour"));

		this.points = pts;

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
		this.ensureOrder();
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
	 * @param epsilon
	 *            GeoEpsilon needed for validation<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if the validation of the tetrahedron fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3D(Point3D point1, Point3D point2, Point3D point3,
			Point3D point4, GeoEpsilon epsilon) {
		this.points = new Point3D[] { point1, point2, point3, point4 };

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
		this.ensureOrder();
	}

	/**
	 * Constructor.
	 * 
	 * @param point
	 *            Point3D
	 * @param triangle
	 *            Triangle3D
	 * @param epsilon
	 *            GeoEpsilon needed for validation<br>
	 *            If GeoEpsilon is <code>null</code> no validation will occur.
	 * @throws ValidationException
	 *             signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3D(Point3D point, Triangle3D triangle, GeoEpsilon epsilon) {
		this(point, triangle.points[0], triangle.points[1], triangle.points[2],
				epsilon);
	}

	/**
	 * Constructor.
	 * 
	 * @param seg1
	 *            Segment3D 1
	 * @param seg2
	 *            Segment3D 2
	 * @param epsilon
	 *            GeoEpsilon needed for validation<br>
	 *            If GeoEpsilon is <code>null</code> no validation will occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3D(Segment3D seg1, Segment3D seg2, GeoEpsilon epsilon) {
		this(seg1.points[0], seg1.points[1], seg2.points[0], seg2.points[1],
				epsilon);
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
	 *             Point3D, Point3D, Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3D(Tetrahedron3D tetra) {
		this(new Point3D(tetra.points[0]), new Point3D(tetra.points[1]),
				new Point3D(tetra.points[2]), new Point3D(tetra.points[3]),
				null);
	}

	/**
	 * Checks whether <code>this</code> has given point as corner.
	 * 
	 * @param p
	 *            Point3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * 
	 * @return boolean - true if the given point is present in the tetrahedron,
	 *         false otherwise.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean hasCorner(Point3D p, GeoEpsilon epsilon) {
		if (this.points[0].isEqual(p, epsilon)
				|| this.points[1].isEqual(p, epsilon)
				|| this.points[2].isEqual(p, epsilon)
				|| this.points[3].isEqual(p, epsilon))
			return true;
		return false;
	}

	/**
	 * Checks whether <code>this</code> has given segment as edge.
	 * 
	 * @param seg
	 *            Segment3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * 
	 * @return boolean - true if the given segment is one of the edges of the
	 *         tetrahedron, false otherwise.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean hasEdge(Segment3D seg, GeoEpsilon epsilon) {

		if (this.hasCorner(seg.points[0], epsilon)
				&& this.hasCorner(seg.points[1], epsilon))
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
	 * @param epsilon GeoEpsilon
	 * 
	 * @return double - solid angle in degree ( between 0 and 360 ).
	 */
	private double getAngle(Vector3D v0, Vector3D v1, Vector3D v2,
			GeoEpsilon epsilon) { // Dag

		double cosa = v0.cosinus(v1, epsilon);
		double cosb = v0.cosinus(v2, epsilon);
		double cosc = v1.cosinus(v2, epsilon);

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
	 * @param epsilon
	 *            GeoEpsilon
	 * 
	 * @return double[] - angles.
	 */
	public double[] getAngles(GeoEpsilon epsilon) { // Dag
		Vector3D[] v = this.getVectors();
		double[] angles = new double[4];

		Vector3D[] vectors = new Vector3D[3];

		// vectors "originating" in point 0
		vectors[0] = Vector3D.sub(v[1], v[0]);
		vectors[1] = Vector3D.sub(v[2], v[0]);
		vectors[2] = Vector3D.sub(v[3], v[0]);
		angles[0] = getAngle(vectors[0], vectors[1], vectors[2], epsilon);

		// vectors "originating" in point 1
		vectors[0] = Vector3D.sub(v[0], v[1]);
		vectors[1] = Vector3D.sub(v[2], v[1]);
		vectors[2] = Vector3D.sub(v[3], v[1]);
		angles[1] = getAngle(vectors[0], vectors[1], vectors[2], epsilon);

		// vectors "originating" in point 2
		vectors[0] = Vector3D.sub(v[0], v[2]);
		vectors[1] = Vector3D.sub(v[1], v[2]);
		vectors[2] = Vector3D.sub(v[3], v[2]);
		angles[2] = getAngle(vectors[0], vectors[1], vectors[2], epsilon);

		// vectors "originating" in point 3
		vectors[0] = Vector3D.sub(v[0], v[3]);
		vectors[1] = Vector3D.sub(v[1], v[3]);
		vectors[2] = Vector3D.sub(v[2], v[3]);
		angles[3] = getAngle(vectors[0], vectors[1], vectors[2], epsilon);

		return angles;
	}

	/**
	 * Returns the vectors of the points of this tetrahedron.
	 * 
	 * @return Vector3D[] - vectors.
	 */
	public Vector3D[] getVectors() {
		return new Vector3D[] { new Vector3D(this.points[0]),
				new Vector3D(this.points[1]), new Vector3D(this.points[2]),
				new Vector3D(this.points[3]) };
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
		Point3D pMin = new Point3D(GeomUtils.getMin(points[0].x, points[1].x,
				this.points[2].x, points[3].x), GeomUtils.getMin(points[0].y,
				points[1].y, points[2].y, points[3].y), GeomUtils.getMin(
				points[0].z, points[1].z, points[2].z, points[3].z));
		Point3D pMax = new Point3D(GeomUtils.getMax(points[0].x, points[1].x,
				points[2].x, points[3].x), GeomUtils.getMax(points[0].y,
				points[1].y, points[2].z, points[3].y), GeomUtils.getMax(
				points[0].z, this.points[1].z, points[2].z, points[3].z));
		return new MBB3D(pMin, pMax);
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int getTriangleIndex(Triangle3D triangle, GeoEpsilon epsilon) {

		if (this.getTriangle(0, epsilon)
				.isGeometryEquivalent(triangle, epsilon))
			return 0;
		if (this.getTriangle(1, epsilon)
				.isGeometryEquivalent(triangle, epsilon))
			return 1;
		if (this.getTriangle(2, epsilon)
				.isGeometryEquivalent(triangle, epsilon))
			return 2;
		if (this.getTriangle(3, epsilon)
				.isGeometryEquivalent(triangle, epsilon))
			return 3;
		return -1;
	}

	/**
	 * Returns the volume of this.
	 * 
	 * @return double - volume.
	 */
	public double getVolume() { // Dag

		Vector3D v1 = new Vector3D((points[0].x - points[1].x),
				(points[0].y - points[1].y), (points[0].z - points[1].z));
		Vector3D v2 = new Vector3D((points[0].x - points[2].x),
				(points[0].y - points[2].y), (points[0].z - points[2].z));
		Vector3D v3 = new Vector3D((points[0].x - points[3].x),
				(points[0].y - points[3].y), (points[0].z - points[3].z));

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
		 * vec[((i+1)%4)]); plane[i] = new Plane3D(normal, location, epsilon );
		 * } Line3D line = (Line3D) plane[0].intersection(plane[1]);
		 * SimpleGeoObj sgo = plane[3].intersection(line); if ( sgo.getType() ==
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

		double x = (points[0].x + points[1].x + points[2].x + points[3].x) / 4;
		double y = (points[0].y + points[1].y + points[2].y + points[3].y) / 4;
		double z = (points[0].z + points[1].z + points[2].z + points[3].z) / 4;
		return new Point3D(x, y, z);

		/*
		 * Alternative: calculation of the centre by calculating the
		 * intersection of two lines, each of which connects the centre of a
		 * tetrahedron face with the opposite vertex (of course 4 lines
		 * intersect in the same point but only 2 are enough to calculate the
		 * point).
		 * 
		 * Line3D line1 = new Line3D( this.getPoint(0) ,
		 * this.getTriangle(0).getCenter() , this.getGeoEpsilon() ); Line3D
		 * line2 = new Line3D( this.getPoint(1) ,
		 * this.getTriangle(1).getCenter() , this.getGeoEpsilon() ); //
		 * intersection point must exist return ( (Point3D)
		 * line1.intersection(line2) );
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3D getTriangle(int id, GeoEpsilon epsilon) { // Dag
		switch (id) {
		case 0:
			return new Triangle3D(points[1], points[2], points[3], epsilon);
		case 1:
			return new Triangle3D(points[0], points[3], points[2], epsilon);
		case 2:
			return new Triangle3D(points[0], points[1], points[3], epsilon);
		case 3:
			return new Triangle3D(points[0], points[2], points[1], epsilon);
		default:
			return null;

		}
	}

	/**
	 * Tests whether this intersects the given MBB.
	 * 
	 * @param mbb
	 *            MBB3D for test
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
	public boolean intersects(MBB3D mbb, GeoEpsilon epsilon) { // Dag

		if (!this.getMBB().intersects(mbb, epsilon))
			return false;
		// test if at least one corner of this is inside of mbb
		for (int i = 0; i < 4; i++) {
			if (mbb.contains(this.points[i], epsilon))
				return true;
		}
		// test if at least one mbb corner is inside of this
		Point3D[] corner = mbb.getCorners();

		for (int i = 0; i < 8; i++) {
			if (this.contains(corner[i], epsilon))
				return true;
		}
		// test if at least one face of this intersects
		for (int i = 0; i < 4; i++) {
			Triangle3D tri = this.getTriangle(i, epsilon);
			if (tri.intersects(mbb, epsilon))
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
	 *             - if the result of the strict intersection of a Triangle3D
	 *             and a MBB3D is not a simplex. The exception originates in the
	 *             method intersectsStrict(MBB3D, GeoEpsilon) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsStrict(MBB3D mbb, GeoEpsilon epsilon) { // Dag

		if (!this.getMBB().intersectsStrict(mbb, epsilon))
			return false;
		// test if at least one corner of this is strict inside of mbb
		for (int i = 0; i < 4; i++) {
			if (mbb.containsStrict(this.points[i], epsilon))
				return true;
		}
		// test if all mbb corners are outside this
		Point3D[] corner = mbb.getCorners();

		for (int i = 0; i < 8; i++) {
			if (this.containsStrict(corner[i], epsilon))
				return true;
		}
		// test if at least one face of this intersects strict
		for (int i = 0; i < 4; i++) {
			Triangle3D tri = this.getTriangle(i, epsilon);
			if (tri.intersectsStrict(mbb, epsilon))
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
		// intersects(line) for every face of this
		if (this.getTriangle(0, epsilon).intersects(line, epsilon)
				|| this.getTriangle(1, epsilon).intersects(line, epsilon)
				|| this.getTriangle(2, epsilon).intersects(line, epsilon)
				|| this.getTriangle(3, epsilon).intersects(line, epsilon))
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Segment3D segment, GeoEpsilon epsilon) { // Dag

		if (this.contains(segment, epsilon))
			return true;
		// intersects(segment) for every face of this
		if (this.getTriangle(0, epsilon).intersects(segment, epsilon)
				|| this.getTriangle(1, epsilon).intersects(segment, epsilon)
				|| this.getTriangle(2, epsilon).intersects(segment, epsilon)
				|| this.getTriangle(3, epsilon).intersects(segment, epsilon))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Plane3D plane, GeoEpsilon epsilon) { // Dag
		// check intersects(plane) for every face of this
		if (this.getTriangle(0, epsilon).intersects(plane, epsilon)
				|| this.getTriangle(1, epsilon).intersects(plane, epsilon)
				|| this.getTriangle(2, epsilon).intersects(plane, epsilon)
				|| this.getTriangle(3, epsilon).intersects(plane, epsilon))
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
	public boolean intersects(Triangle3D triangle, GeoEpsilon epsilon) { // Dag

		if (this.contains(triangle, epsilon))
			return true;
		// intersects(triangle) for every face of this
		if (this.getTriangle(0, epsilon).intersects(triangle, epsilon)
				|| this.getTriangle(1, epsilon).intersects(triangle, epsilon)
				|| this.getTriangle(2, epsilon).intersects(triangle, epsilon)
				|| this.getTriangle(3, epsilon).intersects(triangle, epsilon))
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Tetrahedron3D tetra, GeoEpsilon epsilon) { // Dag

		if (!this.getMBB().intersects(tetra.getMBB(), epsilon))
			return false;
		// test if this contains tetra totally or vise versa
		if (this.contains(tetra.points[0], epsilon)
				&& this.contains(tetra.points[1], epsilon)
				&& this.contains(tetra.points[2], epsilon)
				&& this.contains(tetra.points[3], epsilon))
			return true;
		if (tetra.contains(this.points[0], epsilon)
				&& tetra.contains(this.points[1], epsilon)
				&& tetra.contains(this.points[2], epsilon)
				&& tetra.contains(this.points[3], epsilon))
			return true;

		/*
		 * test intersection for every Segment of tetra with every face of this,
		 * and vice versa
		 */
		for (int i = 0; i < 3; i++) {
			for (int j = (i + 1); j < 4; j++) {
				Segment3D segment = new Segment3D(tetra.points[i],
						tetra.points[j], epsilon);
				if (this.getTriangle(0, epsilon).intersects(segment, epsilon)
						|| this.getTriangle(1, epsilon).intersects(segment,
								epsilon)
						|| this.getTriangle(2, epsilon).intersects(segment,
								epsilon)
						|| this.getTriangle(3, epsilon).intersects(segment,
								epsilon))
					return true;
				segment = new Segment3D(this.points[i], this.points[j], epsilon);
				if (tetra.getTriangle(0, epsilon).intersects(segment, epsilon)
						|| tetra.getTriangle(1, epsilon).intersects(segment,
								epsilon)
						|| tetra.getTriangle(2, epsilon).intersects(segment,
								epsilon)
						|| tetra.getTriangle(3, epsilon).intersects(segment,
								epsilon))
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
	 *             intersection(Plane3D, GeoEpsilon) of this class.
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
		// evaluation of intersection(plane) return
		Geometry3D result = this.intersection(plane, epsilon);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		if (result == null)
			return -1;

		switch (result.getGeometryType()) {
		case POINT:
			return 0;
		case SEGMENT:
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, GeoEpsilon) of this class.
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
		// evaluation of intersection(line) return
		Geometry3D result = this.intersection(line, epsilon);

		if (result == null)
			return -1;

		switch (result.getGeometryType()) {
		case SEGMENT:
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, GeoEpsilon) of this class.
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
		// evaluation of intersection(segment) return
		Geometry3D result = this.intersection(segment, epsilon);

		if (result == null)
			return -1;

		switch (result.getGeometryType()) {
		case SEGMENT:
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
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Tetrahedron3D and a
	 *             Plane3D is not a 3D simplex. The exception originates in the
	 *             method intersection(Plane3D, GeoEpsilon) of the class
	 *             Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of this class.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public int intersectsInt(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		// evaluation of intersection(triangle) return
		Geometry3D result = this.intersection(triangle, epsilon);

		if (result == null)
			return -1;

		switch (result.getGeometryType()) {
		case TRIANGLE:
			return 2;
		case SEGMENT:
			return 1;
		case POINT:
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             intersection(Plane3D, GeoEpsilon) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of this class.
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
	public int intersectsInt(Tetrahedron3D tetra, GeoEpsilon epsilon) { // Dag

		// Auswertung der Rueckgabe von intersection(tetra)
		Geometry3D result = this.intersection(tetra, epsilon);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		if (result == null)
			return -1;
		else {

			switch (result.getGeometryType()) {
			case WIREFRAME:
				return ((WireframeGeometry3D) result).getDimension();
			case TETRAHEDRON:
				return 3;
			case TRIANGLE:
				return 2;
			case SEGMENT:
				return 1;
			case POINT:
				return 0;
			default: // must be NULL:
				throw new IllegalStateException(
						Db3dSimpleResourceBundle
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
	 *             intersection(Plane3D, GeoEpsilon) of this class.
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
		if (this.intersectsInt(plane, epsilon) == 2)
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, GeoEpsilon) of this class.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsRegular(Line3D line, GeoEpsilon epsilon) {
		if (this.intersectsInt(line, epsilon) == 1)
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, GeoEpsilon) of this class.
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
		if (this.intersectsInt(segment, epsilon) == 1)
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Tetrahedron3D and a
	 *             Plane3D is not a 3D simplex. The exception originates in the
	 *             method intersection(Plane3D, GeoEpsilon) of the class
	 *             Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of this class.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean intersectsRegular(Triangle3D triangle, GeoEpsilon epsilon) {
		if (this.intersectsInt(triangle, epsilon) == 2)
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             intersectsInt(Tetrahedron3D, GeoEpsilon) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this and a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, GeoEpsilon) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of this class.
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
	public boolean intersectsRegular(Tetrahedron3D tetra, GeoEpsilon epsilon) {
		if (this.intersectsInt(tetra, epsilon) == 3)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Point3D point, GeoEpsilon epsilon) { // Dag
		// Testing for MBB
		if (!this.getMBB().contains(point, epsilon))
			return false;

		/*
		 * The point is cointained if: (1) it belongs to one of the triangles of
		 * the tetrahedron; (2) for ALL four triangles of the tetrahedron, it
		 * lies on the "inner side" of the plane of each triangle.
		 */
		for (int i = 0; i < 4; i++) {
			Triangle3D triangle = this.getTriangle(i, epsilon);

			if (triangle.contains(point, epsilon))
				return true;
			else {
				Vector3D showInsideVector = new Vector3D(triangle.points[0],
						this.points[i]);
				int orientation = triangle.getOrientation(showInsideVector,
						epsilon);

				Vector3D showToPointVector = new Vector3D(triangle.points[0],
						point);
				if ((triangle.getOrientation(showToPointVector, epsilon) != orientation)
						|| (triangle.getOrientation(showToPointVector, epsilon) == 0))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean containsStrict(Point3D point, GeoEpsilon epsilon) { // Dag
		// Testing for MBB
		if (!this.getMBB().containsStrict(point, epsilon))
			return false;

		/*
		 * The point is cointained if: (1) it belongs to one of the triangles of
		 * the tetrahedron; (2) for ALL four triangles of the tetrahedron, it
		 * lies on the "inner side" of the plane of each triangle.
		 */
		for (int i = 0; i < 4; i++) {
			Triangle3D triangle = this.getTriangle(i, epsilon);

			if (triangle.contains(point, epsilon))
				return false;
			else {
				Vector3D showInsideVector = new Vector3D(triangle.points[0],
						this.points[i]);
				int orientation = triangle.getOrientation(showInsideVector,
						epsilon);

				Vector3D showToPointVector = new Vector3D(triangle.points[0],
						point);
				if ((triangle.getOrientation(showToPointVector, epsilon) != orientation)
						|| (triangle.getOrientation(showToPointVector, epsilon) == 0))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Segment3D segment, GeoEpsilon epsilon) { // Dag
		// all points are contained <=> the segment is contained
		if (this.contains(segment.points[0], epsilon)
				&& this.contains(segment.points[1], epsilon))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		// all points are contained <=> the triangle is contained
		if (this.contains(triangle.points[0], epsilon)
				&& this.contains(triangle.points[1], epsilon)
				&& this.contains(triangle.points[2], epsilon))
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Tetrahedron3D tetra, GeoEpsilon epsilon) { // Dag
		// all points are contained <=> the tetrahedron is contained
		if (this.contains(tetra.points[0], epsilon)
				&& this.contains(tetra.points[1], epsilon)
				&& this.contains(tetra.points[2], epsilon)
				&& this.contains(tetra.points[3], epsilon))
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Line3D line, GeoEpsilon epsilon) { // Dag
		/*
		 * Intersection of line with every face of this. If intersection returns
		 * a Segment3D object, this is returned as result. Returned Point3D
		 * objects get collected in a PointSet3D.
		 */

		PointSet3D points = new PointSet3D(epsilon);
		for (int i = 0; i < 4; i++) {

			Geometry3D lineTriangleIntersection = this.getTriangle(i, epsilon)
					.intersection(line, epsilon);

			if (lineTriangleIntersection != null) {
				if (lineTriangleIntersection.getGeometryType() == GEOMETRYTYPES.SEGMENT)
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
			return new Segment3D(it.next(), it.next(), epsilon);

		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Line3D is
	 *             not Segment3D, Point3D or null. The exception originates in
	 *             the method intersection (Line3D, GeoEpsilon) of this class.
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
		/*
		 * Use intersection(line) - if result is Segment3D ->
		 * Segment.intersectionOnLine(segment)
		 */

		if (!this.getMBB().intersects(segment.getMBB(), epsilon))
			return null;

		Geometry3D lineIntersection = this.intersection(
				segment.getLine(epsilon), epsilon);

		if (lineIntersection == null)
			return null;

		if (lineIntersection.getGeometryType() == GEOMETRYTYPES.POINT)
			return lineIntersection;
		// else
		return segment.intersectionOnLine(((Segment3D) lineIntersection),
				epsilon);
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

		WireframeGeometry3D wf = new WireframeGeometry3D(epsilon);

		for (int i = 0; i < 4; i++) {
			Geometry3D result = this.getTriangle(i, epsilon).intersection(
					plane, epsilon);

			if (result != null) {
				switch (result.getGeometryType()) {
				case TRIANGLE:
					return result;
				case SEGMENT:
					wf.add(((Segment3D) result));
					break;
				case POINT:
					wf.add(((Point3D) result));
					break;
				default:
					throw new IllegalStateException(
							Db3dSimpleResourceBundle
									.getString("db3d.geom.resnotthreedsimplex"));
				}
			}
		}

		switch (wf.countNodes()) {

		case 4: // could max be four !!
			return wf;

		case 3: // result is a triangle given by the three segments
			return new Triangle3D(wf.getPoints()[0], wf.getPoints()[1],
					wf.getPoints()[2], epsilon);

		case 2: // result is a segment
			return wf.getSegments()[0];

		case 1: // result is a point
			return wf.getPoints()[0];

		case 0: // result is null
			return null;

		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this with a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, GeoEpsilon) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of this class.
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
		// idea: intersect the intersection result of this.intersection(plane of
		// triangle) with triangle
		if (!(this.getMBB().intersects(triangle.getMBB(), epsilon)))
			return null;

		Geometry3D planeIntersectionResult = this.intersection(
				triangle.getPlane(epsilon), epsilon);
		if (planeIntersectionResult != null) {
			switch (planeIntersectionResult.getGeometryType()) {

			case POINT:
				Point3D p = (Point3D) planeIntersectionResult;
				if (triangle.containsInPlane(p, epsilon))
					return p;
				else
					return null;

			case SEGMENT:
				return triangle.intersectionInPlane(
						((Segment3D) planeIntersectionResult), epsilon);

			case TRIANGLE:
				return triangle.intersectionInPlane(
						((Triangle3D) planeIntersectionResult), epsilon);

			case WIREFRAME:
				WireframeGeometry3D wf = (WireframeGeometry3D) planeIntersectionResult;
				return intersectionInPlane(triangle, wf, epsilon);

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
	 * GeoEpsilon epsilon) method of the class Line3D (which computes the
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
	 * Point3D, GeoEpsilon).
	 * 
	 * @throws IllegalArgumentException - if validation of a Triangle3D fails.
	 * The exception originates in the constructor Triangle3D(Point3D, Point3D,
	 * Point3D, GeoEpsilon).
	 * 
	 * @throws IllegalArgumentException - if index of a triangle point is not 0,
	 * 1 or 2. The exception originates in the method getPoint(int) of the class
	 * Triangle3D.
	 * 
	 * @throws ArithmeticException - if norm equals zero in epsilon range. This
	 * exception originates in the method normalize(GeoEpsilon) of the class
	 * Vector3D.
	 */
	private Geometry3D intersectionInPlane(Triangle3D triangle,
			WireframeGeometry3D wf, GeoEpsilon epsilon) { // Dag

		WireframeGeometry3D resultWireframe = new WireframeGeometry3D(epsilon);
		Segment3D[] s = wf.getSegments(); // must contain four segments
		// (1) intersect segments of wireframe with triangle
		for (int i = 0; i < 4; i++) {
			Geometry3D obj = triangle.intersectionInPlane(s[i], epsilon);

			if (obj != null) {
				if (obj.getGeometryType() == GEOMETRYTYPES.POINT)
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
				Geometry3D obj = triangles1[j].intersectionInPlane(
						triangle.getSegment(i, epsilon), epsilon);
				if (obj != null) {
					if (obj.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
						if (j == 0)
							remember = (Segment3D) obj;
						else {
							seg = (Segment3D) obj;
							if (remember != null) {
								Point3D[] points = { seg.points[0],
										seg.points[1], remember.points[0],
										remember.points[1] };
								Point3D[] p = GeomUtils
										.getPointsWithMaxDistance(points);
								remember = null;
								resultWireframe.add(new Segment3D(p[0], p[1],
										epsilon));
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
			return new Segment3D(p[0], p[1], epsilon);
		case 3:
			return new Triangle3D(p[0], p[1], p[2], epsilon);
		default:
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of this and a Plane3D is
	 *             not a 3D simplex. The exception originates in the method
	 *             intersection(Plane3D, GeoEpsilon) of this class.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             Wireframe3D is not a 3D simplex. The exception originates in
	 *             the method intersectionInPlane(Triangle3D, Wireframe3D,
	 *             GeoEpsilon3D) of this class.
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
	 * 
	 */
	public Geometry3D intersection(Tetrahedron3D tetra, GeoEpsilon epsilon) { // Dag

		if (!(this.getMBB().intersects(tetra.getMBB(), epsilon)))
			return null;
		// check if this contains tetra and vice versa
		if (this.contains(tetra, epsilon))
			return tetra;
		if (tetra.contains(this, epsilon))
			return new Tetrahedron3D(this);

		WireframeGeometry3D resultWF = new WireframeGeometry3D(epsilon);
		Triangle3D[] triangles1 = new Triangle3D[] {
				this.getTriangle(0, epsilon), this.getTriangle(1, epsilon),
				this.getTriangle(2, epsilon), this.getTriangle(3, epsilon) };
		Tetrahedron3D tet = tetra;

		// intersect this.faces with tetra and tetra.faces with this - collect
		// results in a wireframe object
		for (int k = 0; k < 2; k++) {

			if (k == 1) {
				triangles1 = new Triangle3D[] { tetra.getTriangle(0, epsilon),
						tetra.getTriangle(1, epsilon),
						tetra.getTriangle(2, epsilon),
						tetra.getTriangle(3, epsilon) };
				tet = this;
			}

			for (int i = 0; i < 4; i++) {
				Geometry3D obj = tet.intersection(triangles1[i], epsilon);
				if (obj != null) {
					switch (obj.getGeometryType()) {

					case POINT:
						resultWF.add((Point3D) obj);
						break;

					case SEGMENT:
						resultWF.add((Segment3D) obj);
						break;

					case TRIANGLE:
						resultWF.add((Triangle3D) obj);
						// Here an IllegalStateException can be thrown signaling
						// problems with the dimensions of the wireframe.
						break;

					case WIREFRAME:
						resultWF.add((WireframeGeometry3D) obj);
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
			Geometry3D result;
			try {
				result = new Tetrahedron3D(p[0], p[1], p[2], p[3], epsilon);
			} catch (IllegalArgumentException e) {
				result = new WireframeGeometry3D(epsilon);
				((WireframeGeometry3D) result).add(p);
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimension of the wireframe.
			}
			return result;
		case 3: // result is a triangle given by the three segments
			p = resultWF.getPoints();
			return new Triangle3D(p[0], p[1], p[2], epsilon);
		case 2: // result is a segment
			p = resultWF.getPoints();
			return new Segment3D(p[0], p[1], epsilon);
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	public Geometry3D projection(Plane3D plane, GeoEpsilon epsilon) { // Dag
		// projection of four corner points and test if on of projected results
		// is contained in the triangle of other three

		Point3D[] projectedPoints = new Point3D[4];
		for (int i = 0; i < 4; i++)
			projectedPoints[i] = (Point3D) this.points[i].projection(plane);

		Triangle3D triangle = null;

		triangle = new Triangle3D(projectedPoints[1], projectedPoints[2],
				projectedPoints[3], null);
		if (triangle.isValid(epsilon))
			if (triangle.contains(projectedPoints[0], epsilon))
				return triangle;

		triangle = new Triangle3D(projectedPoints[2], projectedPoints[3],
				projectedPoints[0], null);
		if (triangle.isValid(epsilon))
			if (triangle.contains(projectedPoints[1], epsilon))
				return triangle;

		triangle = new Triangle3D(projectedPoints[3], projectedPoints[0],
				projectedPoints[1], null);
		if (triangle.isValid(epsilon))
			if (triangle.contains(projectedPoints[2], epsilon))
				return triangle;

		triangle = new Triangle3D(projectedPoints[0], projectedPoints[1],
				projectedPoints[2], null);
		if (triangle.isValid(epsilon))
			if (triangle.contains(projectedPoints[3], epsilon))
				return triangle;

		Line3D line;
		int oppositeIndex = -1;

		for (int i = 0; i < 3; i++) {
			line = new Line3D(projectedPoints[3], projectedPoints[i], epsilon);
			if (triangle.intersectsInt(line, epsilon) == 1) {
				oppositeIndex = i;
				break;
			}
		}

		Segment3D[] s = new Segment3D[4];

		s[0] = triangle.getSegment(((oppositeIndex + 1) % 3), epsilon);
		s[1] = triangle.getSegment(((oppositeIndex + 2) % 3), epsilon);
		s[2] = new Segment3D(projectedPoints[((oppositeIndex + 1) % 3)],
				projectedPoints[3], epsilon);
		s[3] = new Segment3D(projectedPoints[((oppositeIndex + 2) % 3)],
				projectedPoints[3], epsilon);

		WireframeGeometry3D resultWireframe = new WireframeGeometry3D(epsilon);
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
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
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
	 *             GeoEpsilon3D) of this class.
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
	public Geometry3D projection(Triangle3D triangle, GeoEpsilon epsilon) { // Dag
		// projection on plane of triangle and following intersecton with it
		Geometry3D projectionOnPlane = this.projection(
				triangle.getPlane(epsilon), epsilon);

		if (projectionOnPlane.getGeometryType() == GEOMETRYTYPES.TRIANGLE)
			return ((Triangle3D) projectionOnPlane).intersectionInPlane(
					triangle, epsilon);

		else { // projectionOnPlane must be of type WIREFRAME3D
			WireframeGeometry3D wf = (WireframeGeometry3D) projectionOnPlane;
			return intersectionInPlane(triangle, wf, epsilon);
		}
	}

	/**
	 * Validates if the given points are a valid tetrahedron.
	 * 
	 * @param epsilon
	 *            GeoEpsilon for test
	 * @return boolean - true if valid, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isValid(GeoEpsilon epsilon) {
		// test for geometric equality between all points
		if (this.points[0].isEqual(this.points[1], epsilon)
				|| this.points[0].isEqual(this.points[2], epsilon)
				|| this.points[0].isEqual(this.points[3], epsilon)
				|| this.points[1].isEqual(this.points[2], epsilon)
				|| this.points[1].isEqual(this.points[3], epsilon)
				|| this.points[2].isEqual(this.points[3], epsilon))
			return false;

		return !epsilon.equal(new Plane3D(this.points[0], this.points[1],
				this.points[2], epsilon).distance(this.points[3]), 0);
	}

	/**
	 * Checks whether this is a regular tetrahedron.
	 * 
	 * @param epsilon
	 *            GeoEpsilon for test
	 * @return boolean - true if regular, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isRegular(GeoEpsilon epsilon) { // Dag
		// test if volume exceeds minimum threshold

		for (int i = 0; i < 4; i++)
			if (!this.getTriangle(i, epsilon).isRegular(epsilon))
				return false;

		if (!(this.getVolume() > (MIN_VOLUME_EPSILON_FACTOR * epsilon
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
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if this is "well proportionate", false if
	 *         disproportionate.
	 */
	public boolean isBeautiful(GeoEpsilon epsilon) { // Dag
		// test if solid angles exceed threshold
		double angles[] = getAngles(epsilon);

		for (int i = 0; i < 4; i++) {
			if (angles[i] < (MIN_AREA_EPSILON_FACTOR * epsilon.getEpsilon()))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether this is a completely validated triangle.<br>
	 * This method performs an isValid,isRegular and isBeautiful test.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if validated, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean isCompleteValidated(GeoEpsilon epsilon) {
		return isValid(epsilon) && isRegular(epsilon) && isBeautiful(epsilon);
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return TETRAHEDRON3D always.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @see Spatial.dbms.geom.Spatial#getGeometryType()
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.TETRAHEDRON;
	}

	/*
	 * Ensures the order of face opposite to point[0] - normal vector has
	 * outside direction. Needed by method buildTriangle - relies on correct
	 * orientation of triangle opposite to point[0] made by this method.
	 */
	private void ensureOrder() {
		// ensure orientation
		Vector3D showInsideVector = new Vector3D(points[1], points[0]);
		// calc vector pointing in normvector direction
		double x = this.points[1].y * (this.points[2].z - this.points[3].z)
				+ this.points[0].y * (this.points[3].z - this.points[1].z)
				+ this.points[3].y * (this.points[1].z - this.points[2].z);
		double y = this.points[1].z * (this.points[2].x - this.points[3].x)
				+ this.points[0].z * (this.points[3].x - this.points[1].x)
				+ this.points[3].z * (this.points[1].x - this.points[2].x);
		double z = this.points[1].x * (this.points[2].y - this.points[3].y)
				+ this.points[0].x * (this.points[3].y - this.points[1].y)
				+ this.points[3].x * (this.points[1].y - this.points[2].y);
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
		return "Tetrahedron3D [one=" + points[1] + ", three=" + points[3]
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
				+ ((points[3] == null) ? 0 : points[3].hashCode());
		result = prime * result
				+ ((points[2] == null) ? 0 : points[2].hashCode());
		result = prime * result
				+ ((points[0] == null) ? 0 : points[0].hashCode());
		return result;
	}

	/**
	 * Checks whether this is equal to given Tetrahedron3D.<br>
	 * Coordinate equality test / instanceof test.<br>
	 * Test is against the coordinates - so subclasses can be tested also !
	 * 
	 * @param obj
	 *            Tetrahedron3D for test
	 * @return boolean - true if equal.
	 */
	public boolean isEqual(Tetrahedron3D obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		for (int i = 0; i < 4; i++)
			if (this.points[i].isEqual(obj.points[i], epsilon))
				return false;

		return true;
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
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.GeoEpsilon)
	 */
	public boolean isEqual(Equivalentable obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Tetrahedron3D))
			return false;
		return isEqual((Tetrahedron3D) obj, epsilon);
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
		result = prime * result + this.points[3].isEqualHC(factor);
		return result;

	}

	/**
	 * Geometry equivalence test.<br>
	 * The objects must have the same points, but the index position makes no
	 * difference.<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if equal, false otherwise.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.GeoEpsilon)
	 */
	public boolean isGeometryEquivalent(Tetrahedron3D obj, GeoEpsilon epsilon) {
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
		if (!(obj instanceof Tetrahedron3D))
			return false;
		Point3D[] ps1 = GeomUtils.getSorted(this.points);
		Point3D[] ps2 = GeomUtils.getSorted(((Tetrahedron3D) obj).points);
		int length = ps1.length;
		for (int i = 0; i < length; i++)
			if (!ps1[i].isEqual(ps2[i], epsilon))
				return false;

		return true;
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
		Point3D[] ps = GeomUtils.getSorted(this.points);
		int length = ps.length;
		for (int i = 0; i < length; i++)
			result = prime * result + ps[i].isGeometryEquivalentHC(factor);
		return result;
	}

}
