/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.geometries3d;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * Class Vector3D models a vector in 3 dimensional space.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Vector3D extends Geometry3DAbst {

	/* scalars representing this Vector */

	/* x-coordinate */
	protected double x;

	/* y-coordinate */
	protected double y;

	/* z-coordinate */
	protected double z;

	/**
	 * Default Constructor.<br>
	 * Constructs an Vector3D object as null vector.
	 */
	public Vector3D() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}

	/**
	 * Constructor.<br>
	 * Constructs a Vector3D object for given coordinates.
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 * @param z
	 *            z value of Vector3D
	 */
	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Constructor.<br>
	 * Constructs a position vector from given Point3D object.
	 * 
	 * @param point
	 *            Point3D object
	 */
	public Vector3D(Point3D point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param vector
	 *            Vector3D object to copy
	 */
	public Vector3D(Vector3D vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}

	/**
	 * Constructor.<br>
	 * Constructs a Vector3D object from the differences of the vectors p1 and
	 * p2. The resulting vector points from p1 to p2 - so it is a P1P2.
	 * 
	 * @param p1
	 *            Point3D
	 * @param p2
	 *            Point3D
	 */
	public Vector3D(Point3D p1, Point3D p2) {
		this.x = p2.x - p1.x;
		this.y = p2.y - p1.y;
		this.z = p2.z - p1.z;
	}

	// methods

	/**
	 * Add the given two vectors.
	 * 
	 * @param vec1
	 *            first Vector3D
	 * @param vec2
	 *            second Vector3D
	 * @return Vector3D - result after addition.
	 */
	public static Vector3D add(Vector3D vec1, Vector3D vec2) {
		return new Vector3D(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
	}

	/**
	 * Adds the given vector to this.
	 * 
	 * @param vector
	 *            Vector3D to add
	 * @return Vector3D - this after addition.
	 */
	public Vector3D add(Vector3D vector) {
		this.x = this.x + vector.x;
		this.y = this.y + vector.y;
		this.z = this.z + vector.z;
		return this;
	}

	/**
	 * Subtracts the given second vector from the first.
	 * 
	 * @param vec1
	 *            first Vector3D
	 * @param vec2
	 *            second Vector3D
	 * @return Vector3D - result after subtraction.
	 */
	public static Vector3D sub(Vector3D vec1, Vector3D vec2) {
		return new Vector3D(vec1.x - vec2.x, vec1.y - vec2.y, vec1.z - vec2.z);
	}

	/**
	 * Subtracts the given vector from this.
	 * 
	 * @param vector
	 *            Vector3D to subtract.
	 * @return Vector3D - this after subtraction.
	 */
	public Vector3D sub(Vector3D vector) {
		this.x = this.x - vector.x;
		this.y = this.y - vector.y;
		this.z = this.z - vector.z;
		return this;
	}

	/**
	 * Multiplies this with the given scalar.
	 * 
	 * @param scalar
	 *            double to multiply
	 * @return Vector3D - this after multiplication.
	 */
	public Vector3D mult(double scalar) {
		this.x = this.x * scalar;
		this.y = this.y * scalar;
		this.z = this.z * scalar;
		return this;
	}

	/**
	 * Multiplies given vector with the given scalar.
	 * 
	 * @param scalar
	 *            double to multiply
	 * @return Vector3D - new vector from multiplication with scalar.
	 */
	public static Vector3D mult(Vector3D vec, double scalar) {
		return new Vector3D(vec.x * scalar, vec.y * scalar, vec.z * scalar);
	}

	/**
	 * Divides this by the given scalar.<br>
	 * If the arguments double value is 0 - this is returned.
	 * 
	 * @param scalar
	 *            double for division
	 * @param sop
	 *            GeoEpsilon
	 * @return Vector3D this after division.
	 * @throws ArithmeticException
	 *             if scalar is zero in epsilon range.
	 */
	public Vector3D div(double scalar, GeoEpsilon sop) {
		if (sop.equal(scalar, 0)) {
			throw new ArithmeticException(
					Db3dSimpleResourceBundle.getString("db3d.geom.scdivzero"));
		}
		this.x = this.x / scalar;
		this.y = this.y / scalar;
		this.z = this.z / scalar;
		return this;
	}

	/**
	 * Returns the scalar product of this and the given vector.
	 * 
	 * @param vector
	 *            argument Vector3D
	 * @return double - scalar product.
	 */
	public double scalarproduct(Vector3D vector) {
		double value = this.x * vector.x;
		value += this.y * vector.y;
		value += this.z * vector.z;
		return value;
	}

	/**
	 * Computes the cross product of this and the given vector (this X
	 * argument). That is the Vector3D which is orthogonal to this and vector.
	 * 
	 * @param vector
	 *            argument Vector3D
	 * @return Vector3D - cross product of the two vectors.
	 */
	public Vector3D crossproduct(Vector3D vector) {
		double _x = (this.y * vector.z) - (this.z * vector.y);
		double _y = (this.z * vector.x) - (this.x * vector.z);
		double _z = (this.x * vector.y) - (this.y * vector.x);
		return new Vector3D(_x, _y, _z);
	}

	/**
	 * Computes the spat product of this with the given two vectors for cross
	 * product. -> a * ( b x c )
	 * 
	 * @param b
	 *            first parameter for vector product
	 * @param c
	 *            second parameter for vector product
	 * @return double - spat product.
	 */
	public double spatproduct(Vector3D b, Vector3D c) {
		return this.scalarproduct(b.crossproduct(c));
	}

	/**
	 * Returns the norm (length, betrag) of this.
	 * 
	 * @return double - norm.
	 */
	public double getNorm() {
		return Math.sqrt((this.x * this.x) + (this.y * this.y)
				+ (this.z * this.z));
	}

	/**
	 * Returns a normalized version of this as new object.
	 * 
	 * @param sop
	 *            GeoEpsilon
	 * @return Vector3D normalized new Vector3D object.
	 * @throws ArithmeticException
	 *             - if norm is equal zero in epsilon range.
	 */
	public Vector3D getNormalized(GeoEpsilon sop) {
		double norm = this.getNorm();
		if (sop.equal(norm, 0)) {
			throw new ArithmeticException(
					Db3dSimpleResourceBundle.getString("db3d.geom.normdivzero"));
		}

		return new Vector3D(this.x / norm, this.y / norm, this.z / norm);
	}

	/**
	 * Normalizes this.
	 * 
	 * @param sop
	 *            GeoEpsilon
	 * @throws ArithmeticException
	 *             if norm equals zero in epsilon range.
	 */
	public void normalize(GeoEpsilon sop) {
		double norm = this.getNorm();
		if (sop.equal(norm, 0)) {
			throw new ArithmeticException(
					Db3dSimpleResourceBundle.getString("db3d.geom.normdivzero"));
		}
		this.x = this.x / norm;
		this.y = this.y / norm;
		this.z = this.z / norm;
	}

	// /**
	// * Returns the x component of this.
	// *
	// * @return double - x component.
	// */
	// public double getX() {
	// return this.x;
	// }
	//
	// /**
	// * Returns the y component of this.
	// *
	// * @return double - y component.
	// */
	// public double getY() {
	// return this.y;
	// }
	//
	// /**
	// * Returns the z component of this.
	// *
	// * @return double - z component.
	// */
	// public double getZ() {
	// return this.z;
	// }

	/**
	 * Returns this interpreted as a position vector as Point3D.
	 * 
	 * @return Point3D - position as Point3D.
	 */
	public Point3D getAsPoint3D() {
		return new Point3D(this.x, this.y, this.z);
	}

	/**
	 * Returns this interpreted as a position vector as Location.
	 * 
	 * @return Location3D - position as Location.
	 */
	public Point3D getAsLocation3D() {
		return new Point3D(this.x, this.y, this.z);
	}

	// /**
	// * Returns the components of this.
	// *
	// * @return double[] - double array of this.
	// */
	// public double[] getScalars() {
	// return new double[] { this.x, this.y, this.z };
	// }
	//
	/**
	 * Returns the component of this at given index.
	 * 
	 * @return double - component.
	 * @throws IllegalArgumentException
	 *             - wrong index.
	 */
	public double getScalar(int index) {
		switch (index) {
		case 0:
			return this.x;
		case 1:
			return this.y;
		case 2:
			return this.z;
		default:
			throw new IllegalArgumentException(
					Db3dSimpleResourceBundle.getString("db3d.geom.wrindscxyz"));
		}
	}

	// /**
	// * Sets the x component of this to given scalar.
	// *
	// * @param x
	// * new value as double
	// */
	// public void setX(double x) {
	// this.x = x;
	// }
	//
	// /**
	// * Sets the y component of this to given scalar.
	// *
	// * @param y
	// * new value as double
	// */
	// public void setY(double y) {
	// this.y = y;
	// }
	//
	// /**
	// * Sets the z component of this to given scalar.
	// *
	// * @param z
	// * new value as double
	// */
	// public void setZ(double z) {
	// this.z = z;
	// }

	/**
	 * Tests whether this is the null vector.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean isNullVector(GeoEpsilon epsilon) {
		return ((epsilon.equal(this.x, 0)) && (epsilon.equal(this.y, 0)) && (epsilon
				.equal(this.z, 0)));
	}

	/**
	 * Tests if this is collinear to given vector.<br>
	 * If collinear test for parallel/antiparallel takes place.<br>
	 * 0 not collinear; 1 parallel; -1 antiparallel
	 * 
	 * @param vector
	 *            Vector3D to test
	 * @return int - 0 not collinear; 1 parallel; -1 antiparallel.
	 */
	public int isCollinear(Vector3D vector, GeoEpsilon epsilon) {
		if (epsilon.equal(this.cosinus(vector, epsilon), 1))
			return 1;
		if (epsilon.equal(this.cosinus(vector, epsilon), -1))
			return -1;

		return 0;
	}

	/**
	 * Tests if this is orthogonal to given vector.
	 * 
	 * @param vector
	 *            Vector3D to test
	 * @return boolean - true if orthogonal, false otherwise.
	 */
	public boolean isOrthogonal(Vector3D vector, GeoEpsilon epsilon) {
		return epsilon.equal(this.cosinus(vector, epsilon), 0);
	}

	/**
	 * Computes the cosine of the angle between this and the given Vector3D.
	 * 
	 * @param v
	 *            given Vector3D
	 * @return double - value between -1 and +1.
	 */
	public double cosinus(Vector3D v, GeoEpsilon epsilon) {
		double scalar = this.scalarproduct(v);
		scalar = scalar / (this.getNorm() * v.getNorm());
		if (epsilon.equal(Math.abs(scalar), 1)) {
			if (epsilon.equal(scalar, 1))
				return 1;
			else
				return -1;
		}
		return scalar;
	}

	/**
	 * Copies this.
	 * 
	 * @return Vector3D - deep copy of this.
	 */
	public Vector3D copy() {
		return new Vector3D(this.x, this.y, this.z);
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return VECTOR3D always.
	 * @see db3d.dbms.geom.Spatial#getGeometryType()
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.VECTOR;
	}

	/**
	 * For Vector3D a MBB3D is returned modeling the Vector as a Point.
	 * 
	 * @return MBB3D - a minimum bounding box for this vector, which is modeled
	 *         as a point.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @see db3d.dbms.structure.Spatial#getMBB()
	 */
	public MBB3D getMBB() {
		return new MBB3D(new Point3D(this.x, this.y, this.z), new Point3D(
				this.x, this.y, this.z));
	}

	/**
	 * Updates the coordinates of this using values from an external source.
	 * 
	 * @param in
	 *            ObjectInput from which the new values are read
	 * @throws IOException
	 *             if an input error has occurred.
	 * @throws ClassNotFoundException
	 *             if the class of the object has not been found.
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.x = in.readDouble();
		this.y = in.readDouble();
		this.z = in.readDouble();
	}

	/**
	 * Writes the coordinates of this to external output.
	 * 
	 * @param out
	 *            ObjectOutput to which the values are written
	 * @throws IOException
	 *             if an output error has occurred.
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeDouble(this.x);
		out.writeDouble(this.y);
		out.writeDouble(this.z);
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Vector3D [x=" + x + ", y=" + y + ", z=" + z + "]";
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
	// Vector3D other = (Vector3D) obj;
	// if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
	// return false;
	// if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
	// return false;
	// if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
	// return false;
	// return true;
	// }

	/**
	 * Tests whether this is equal to given vector (strong typed).
	 * 
	 * @param obj
	 *            Vector3D to test
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean isEqual(Vector3D obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(sop.equal(x, obj.x)))
			return false;
		if (!(sop.equal(y, obj.y)))
			return false;
		if (!(sop.equal(z, obj.z)))
			return false;

		return true;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Vector3D))
			return false;
		return isEqual((Vector3D) obj, sop);
	}

	@Override
	public int isEqualHC(int factor) {
		throw new NotYetImplementedException();
	}

	public boolean isGeometryEquivalent(Vector3D obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		return isEqual(obj, sop);
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon sop) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Point3D))
			return false;
		return isGeometryEquivalent((Vector3D) obj, sop);
	}

	@Override
	public int isGeometryEquivalentHC(int factor) {
		throw new NotYetImplementedException();
	}

}
