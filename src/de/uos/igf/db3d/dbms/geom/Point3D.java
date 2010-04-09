/*
 * University of Osnabrueck
 * Department for Geoinformatics and Remote Sensing
 *
 * Copyright (C) 2008 Researchgroup Prof. Dr. Martin Breunig
 *
 * File created on 12.02.2003
 */
package de.uos.igf.db3d.dbms.geom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;

/**
 * Point3D is the geometric representation of a point in the 3rd dimension.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Point3D implements PersistentObject, SimpleGeoObj, Equivalentable,
		Externalizable {

	/* coordinates */
	private double x;
	private double y;
	private double z;

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
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
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
	public MBB3D getMBB() {
		return new MBB3D(this, this);
	}

	/**
	 * Returns a new vector of this point.
	 * 
	 * @return Vector3D
	 */
	public Vector3D getVector() {
		return new Vector3D(this);
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
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.onlythree"));
		}
	}

	/**
	 * Returns the Point3D for given index. As this is a Point3D object, only
	 * index=0 is valid. and this will be returned
	 * 
	 * @param index
	 *            int
	 * @return Point3D for the given index.
	 * @throws IllegalStateException
	 *             - if the index is not 0.
	 */
	public Point3D getPoint(int index) {
		switch (index) {
		case 0:
			return this;
		default:
			// FIXME We should fix this weird stuff
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.onlyindexzero"));
		}
	}

	/**
	 * Returns the geometry of the Point3D as a newly created array !.<br>
	 * As this is a Point3D object, a Point3D Array of length 1 with this inside
	 * is returned.
	 * 
	 * @return Point3D[] - array of Point3D objects.
	 */
	public Point3D[] getPoints() {
		return new Point3D[] { this };
	}

	/**
	 * Returns the x value as double
	 * 
	 * @return double - x value.
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Sets the x value to given value.
	 * 
	 * @param x
	 *            new value
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Returns the y value as double
	 * 
	 * @return double - y value.
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * Sets the y value to given value.
	 * 
	 * @param y
	 *            new value.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Returns the z value as double.
	 * 
	 * @return double - z value.
	 */
	public double getZ() {
		return this.z;
	}

	/**
	 * Sets the z value to given value.
	 * 
	 * @param z
	 *            new value
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Returns the point coordinates as double array.
	 * 
	 * @return double[] - point coordinates.
	 */
	public double[] getCoordinates() {
		return new double[] { this.x, this.y, this.z };
	}

	// Geometric Operations

	/**
	 * Tests if this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersects.
	 */
	public boolean intersects(Plane3D plane, ScalarOperator sop) {
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
	 *            ScalarOperator
	 * @return boolean - true if intersects.
	 */
	public boolean intersects(Line3D line, ScalarOperator sop) {
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
	 *            ScalarOperator
	 * @return SimpleGeoObj - result, or null if no intersection.
	 */
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) {
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
	 *            ScalarOperator
	 * @return SimpleGeoObj - result, or null if no intersection.
	 */
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) {
		if (this.intersects(line, sop))
			return new Point3D(this);

		return null;
	}

	/**
	 * Projects this onto the given plane. Returns Point3D as SimpleGeoObj.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @return SimpleGeoObj - must be a point object.
	 */
	public SimpleGeoObj projection(Plane3D plane) { // Dag

		/*
		 * Projection Q of the point p onto the plane (x , n) with n =normal
		 * vector, x = radius vector of a point Q = p - <p-x , n> * n mit "< >"
		 * for scalar product of the contained arguments
		 */

		Vector3D p = this.getVector();
		Vector3D _x = plane.getPositionVector();
		Vector3D n = plane.getNormalVector();
		double factor = Vector3D.sub(p, _x).scalarproduct(n);
		Vector3D Q = Vector3D.sub(p, Vector3D.mult(n, factor));

		return Q.getAsPoint3D();
	}

	/**
	 * Projects this onto the given line.
	 * 
	 * @param line
	 *            Line3D onto which this should be projected
	 * @return SimpleGeoObj - result of projection.
	 */
	public SimpleGeoObj projection(Line3D line) { // Dag

		/*
		 * Projection Q of the point p onto the plane (x , n) with n =normal
		 * vector, x = radius vector of a point Q = p - <p-x , n> * n mit "< >"
		 * for scalar product of the contained arguments
		 */

		Vector3D p = this.getVector();
		Vector3D _x = line.getOrigin().getVector(); // new Vector
		Vector3D u = line.getDVector();

		double factor = Vector3D.sub(p, _x).scalarproduct(u);
		Vector3D q = _x.add(Vector3D.mult(u, factor));

		return q.getAsPoint3D();
	}

	/**
	 * Projects this onto the given segment. Returns Point3D (projection of this
	 * onto the given segment) if the projected point is contained in the
	 * segment, <code>null</code> otherwise. (In the latter case, the projected
	 * point lies on the line that contains the given segment but outside the
	 * given segment).
	 * 
	 * @param seg
	 *            Segment3D onto which this should be projected
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of projection.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj projection(Segment3D seg, ScalarOperator sop) { // Dag

		Point3D projectionToLine = (Point3D) this.projection(seg.getLine(sop));
		if (seg.contains(projectionToLine, sop))
			return projectionToLine;
		else
			return null;
	}

	/**
	 * Computes the square distance between this and the given Point3D.
	 * 
	 * @param point
	 *            Point3D for computation
	 * @return double - value.
	 */
	public double euclideanDistanceSQR(Point3D point) {
		double distx = this.getX() - point.getX();
		double disty = this.getY() - point.getY();
		double distz = this.getZ() - point.getZ();
		return Math.pow(distx, 2) + Math.pow(disty, 2) + Math.pow(distz, 2);
	}

	/**
	 * Computes distance to given Point3D.
	 * 
	 * @param point
	 *            Point3D for computation
	 * @return double - distance.
	 */
	public double euclideanDistance(Point3D point) {
		return Math.sqrt(euclideanDistanceSQR(point));
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
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isEqual(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Point3D))
			return false;

		double[] objcoords = ((Point3D) obj).getCoordinates();

		for (int i = 0; i < 3; i++) {
			if (!(sop.equal(this.getCoord(i), objcoords[i])))
				return false;
		}
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
		return isEqual(obj, sop);
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

		double _x = Math.round(getX() * factor) / factor;
		double _y = Math.round(getY() * factor) / factor;
		double _z = Math.round(getZ() * factor) / factor;

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
	 * @return POINT3D always.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.POINT3D;
	}

	/**
	 * Reads and updates the coordinates of this from an external source.
	 * 
	 * @param in
	 *            ObjectInput from which the new coordinate vales are read
	 * @throws IOException
	 *             if an input error occurred.
	 * @throws ClassNotFoundException
	 *             if the class type of the input object was not found.
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.x = in.readDouble();
		this.y = in.readDouble();
		this.z = in.readDouble();
	}

	/**
	 * Writes the coordinates of this to an external ObjectOutput.
	 * 
	 * @param out
	 *            ObjectOutput to which the coordinate values are written
	 * @throws IOException
	 *             if an output error occurred.
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeDouble(getX());
		out.writeDouble(getY());
		out.writeDouble(getZ());
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
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
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

}
