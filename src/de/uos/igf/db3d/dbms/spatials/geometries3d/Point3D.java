package de.uos.igf.db3d.dbms.spatials.geometries3d;

import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * Point3D is the geometric representation of a point in the 3rd dimension.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Point3D extends Geometry3DAbst {

	/* coordinates */
	protected double x;
	protected double y;
	protected double z;

	/**
	 * Default constructor. Constructs a Point3D object with x,y,z = 0.0
	 */
	public Point3D() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}

	/**
	 * Constructor for double coordinates. Constructs a Point3D object with
	 * given x, y, z coordinates.
	 * 
	 * @param x
	 *            double value of x axis
	 * @param y
	 *            double value of y axis
	 * @param z
	 *            double value of z axis
	 */
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructor for double coordinates. <br>
	 * Constructs a Point3D object with given x, y, z coordinates.
	 * 
	 * @param coords
	 *            double array with values x,y,z axis
	 */
	public Point3D(double[] coords) {
		this(coords[0], coords[1], coords[2]);
	}

	/**
	 * Copy constructor.<br>
	 * Makes a deep copy for the x,y,z coordinates.
	 * 
	 * @param point
	 *            Point3D
	 */
	public Point3D(Point3D point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}

	/**
	 * Returns the MBB of this point.<br>
	 * No copy constructor used - this is referenced in MBB3D.
	 * 
	 * @return MBB3D of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	@Override
	public MBB3D getMBB() {
		return new MBB3D(this, this);
	}

	/**
	 * Returns a new vector of this location.
	 * 
	 * @return Vector3D
	 */
	public Vector3D getVector() {
		return new Vector3D(this);
	}

	/**
	 * Returns the Point3D array of this.<br>
	 * 
	 * @return Point3D array.
	 */
	public Point3D[] getPoints() {
		return new Point3D[] { this };
	}

	// Getter/Setter Methods

	/**
	 * Returns the double value for given index. 0 -> x, 1 -> y, 2 -> z
	 * 
	 * @param index
	 *            [0;2]
	 * @return double - coordinate.
	 */
	public double getCoord(int index) {
		switch (index) {
		case 0:
			return this.x;
		case 1:
			return this.y;
		case 2:
			return this.z;
		default:
			return Double.NaN;
		}
	}

	/**
	 * Sets the value for given for given index. 0 -> x, 1 -> y, 2 -> z
	 * 
	 * @param index
	 *            [0;2]
	 * @param value
	 *            double value
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0, 1 or 2.
	 */
	public void setCoord(int index, double value) {
		switch (index) {
		case 0:
			this.x = value;
			break;
		case 1:
			this.y = value;
			break;
		case 2:
			this.z = value;
			break;
		default:
			// FIXME We should fix this weird stuff
			throw new IllegalStateException(
					Db3dSimpleResourceBundle.getString("db3d.geom.onlythree"));
		}
	}

	//
	// /**
	// * Returns the x value as double
	// *
	// * @return double - x value.
	// */
	// public double getX() {
	// return this.x;
	// }
	//
	// /**
	// * Sets the x value to given value.
	// *
	// * @param x
	// * new value
	// */
	// public void setX(double x) {
	// this.x = x;
	// }
	//
	// /**
	// * Returns the y value as double
	// *
	// * @return double - y value.
	// */
	// public double getY() {
	// return this.y;
	// }
	//
	// /**
	// * Sets the y value to given value.
	// *
	// * @param y
	// * new value.
	// */
	// public void setY(double y) {
	// this.y = y;
	// }
	//
	// /**
	// * Returns the z value as double.
	// *
	// * @return double - z value.
	// */
	// public double getZ() {
	// return this.z;
	// }
	//
	// /**
	// * Sets the z value to given value.
	// *
	// * @param z
	// * new value
	// */
	// public void setZ(double z) {
	// this.z = z;
	// }

	// /**
	// * Returns the point coordinates as NEW double array.
	// *
	// * @return double[] - point coordinates.
	// */
	// public double[] getCoordinates() {
	// return new double[] { this.x, this.y, this.z };
	// }

	// Geometric Operations

	/**
	 * Tests if this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if intersects.
	 */
	public boolean intersects(Plane3D plane, GeoEpsilon sop) {
		double dist = plane.distance(this);
		if (sop.equal(dist, 0))
			return true;
		else
			return false;
	}

	/**
	 * Tests if this intersects with given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return boolean - true if intersects.
	 */
	public boolean intersects(Line3D line, GeoEpsilon sop) {
		double dist = line.distance(this);
		if (sop.equal(dist, 0))
			return true;
		else
			return false;
	}

	/**
	 * Computes the intersection of this and the given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result, or null if no intersection.
	 */
	public Geometry3D intersection(Plane3D plane, GeoEpsilon sop) {
		if (this.intersects(plane, sop))
			return new Point3D(this);

		return null;
	}

	/**
	 * Computes the intersection of this and the given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result, or null if no intersection.
	 */
	public Geometry3D intersection(Line3D line, GeoEpsilon sop) {
		if (this.intersects(line, sop))
			return new Point3D(this);

		return null;
	}

	/**
	 * Projects this onto the given plane. Returns Point3D as SimpleGeoObj.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @return Location3D - must be a point object.
	 */
	public Point3D projection(Plane3D plane) { // Dag

		/*
		 * Projection Q of the point p onto the plane (x , n) with n =normal
		 * vector, x = radius vector of a point Q = p - <p-x , n> * n mit "< >"
		 * for scalar product of the contained arguments
		 */

		Vector3D p = this.getVector();
		Vector3D _x = plane.posvec;
		Vector3D n = plane.normvec;
		double factor = Vector3D.sub(p, _x).scalarproduct(n);
		Vector3D Q = Vector3D.sub(p, Vector3D.mult(n, factor));

		return Q.getAsLocation3D();
	}

	/**
	 * Projects this onto the given line.
	 * 
	 * @param line
	 *            Line3D onto which this should be projected
	 * @return Location3D - result of projection.
	 */
	public Point3D projection(Line3D line) { // Dag

		/*
		 * Projection Q of the point p onto the plane (x , n) with n =normal
		 * vector, x = radius vector of a point Q = p - <p-x , n> * n mit "< >"
		 * for scalar product of the contained arguments
		 */

		Vector3D p = this.getVector();
		Vector3D _x = line.origin.getVector(); // new Vector
		Vector3D u = line.dvec;

		double factor = Vector3D.sub(p, _x).scalarproduct(u);
		Vector3D q = _x.add(Vector3D.mult(u, factor));

		return q.getAsLocation3D();
	}

	/**
	 * Projects this onto the given segment. Returns Point3D (projection of this
	 * onto the given segment) if the projected point is contained in the
	 * segment, <code>null</code> otherwise. (In the latter case, the projected
	 * point lies on the line that contains the given segment but outside the
	 * given segment).
	 * 
	 * @param segment
	 *            Segment3D onto which this should be projected
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result of projection.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Point3D projection(Segment3D segment, GeoEpsilon epsilon) { // Dag
		Point3D projectionToLine = this.projection(segment.getLine(epsilon));
		if (segment.contains(projectionToLine, epsilon))
			return projectionToLine;
		else
			return null;
	}

	/**
	 * Computes the square distance between this and the given Point3D.
	 * 
	 * @param location
	 *            Point3D for computation
	 * @return double - value.
	 */
	public double euclideanDistanceSQR(Point3D location) {
		double distx = this.x - location.x;
		double disty = this.y - location.y;
		double distz = this.z - location.z;
		return Math.pow(distx, 2) + Math.pow(disty, 2) + Math.pow(distz, 2);
	}

	/**
	 * Computes distance to given Point3D.
	 * 
	 * @param location
	 *            Point3D for computation
	 * @return double - distance.
	 */
	public double euclideanDistance(Point3D location) {
		return Math.sqrt(euclideanDistanceSQR(location));
	}

	@Override
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.POINT;
	}

	/**
	 * Converts this to string.
	 * 
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      GeoEpsilon.dbms.geom.GeoEpsilon)
	 */
	public boolean isEqual(Point3D obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(epsilon.equal(this.x, obj.x)))
			return false;
		if (!(epsilon.equal(this.y, obj.y)))
			return false;
		if (!(epsilon.equal(this.z, obj.z)))
			return false;

		return true;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Point3D))
			return false;
		return isEqual((Point3D) obj, sop);
	}

	/**
	 * Computes the corresponding hash code for isEqual usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * 
	 * @see db3d.dbms.geom.Equivalentable#isEqualHC(int)
	 */
	public int isEqualHC(int factor) {

		double _x = Math.round(this.x * factor) / factor;
		double _y = Math.round(this.y * factor) / factor;
		double _z = Math.round(this.z * factor) / factor;

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(_x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(_y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(_z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;

	}

	/**
	 * Geometry equivalence test.<br>
	 * The objects must have the same points, but the index position makes no
	 * difference.<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param location
	 *            Equivalentable object for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if equal, false otherwise.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      GeoEpsilon.dbms.geom.GeoEpsilon)
	 */
	public boolean isGeometryEquivalent(Point3D location, GeoEpsilon epsilon) {
		if (location == null)
			return false;
		if (this == location)
			return true;
		return isEqual(location, epsilon);
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Point3D))
			return false;
		return isGeometryEquivalent((Point3D) obj, sop);
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
		return isEqualHC(factor);
	}

}
