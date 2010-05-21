/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;

/**
 * Class Vector3D models a vector in 3 dimensional space.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Vector3D implements PersistentObject, SimpleGeoObj, Externalizable {

	/* scalars representing this Vector */

	/* x-coordinate */
	private double x;

	/* y-coordinate */
	private double y;

	/* z-coordinate */
	private double z;

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
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param vector
	 *            Vector3D object to copy
	 */
	public Vector3D(Vector3D vector) {
		this.x = vector.getX();
		this.y = vector.getY();
		this.z = vector.getZ();
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
		this.x = p2.getX() - p1.getX();
		this.y = p2.getY() - p1.getY();
		this.z = p2.getZ() - p1.getZ();
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
		return new Vector3D(vec1.getX() + vec2.getX(), vec1.getY()
				+ vec2.getY(), vec1.getZ() + vec2.getZ());
	}

	/**
	 * Adds the given vector to this.
	 * 
	 * @param vector
	 *            Vector3D to add
	 * @return Vector3D - this after addition.
	 */
	public Vector3D add(Vector3D vector) {
		setX(getX() + vector.getX());
		setY(getY() + vector.getY());
		setZ(getZ() + vector.getZ());
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
		return new Vector3D(vec1.getX() - vec2.getX(), vec1.getY()
				- vec2.getY(), vec1.getZ() - vec2.getZ());
	}

	/**
	 * Subtracts the given vector from this.
	 * 
	 * @param vector
	 *            Vector3D to subtract.
	 * @return Vector3D - this after subtraction.
	 */
	public Vector3D sub(Vector3D vector) {
		setX(getX() - vector.getX());
		setY(getY() - vector.getY());
		setZ(getZ() - vector.getZ());
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
		setX(getX() * scalar);
		setY(getY() * scalar);
		setZ(getZ() * scalar);
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
		return new Vector3D(vec.getX() * scalar, vec.getY() * scalar, vec
				.getZ()
				* scalar);
	}

	/**
	 * Divides this by the given scalar.<br>
	 * If the arguments double value is 0 - this is returned.
	 * 
	 * @param scalar
	 *            double for division
	 * @param sop
	 *            ScalarOperator
	 * @return Vector3D this after division.
	 * @throws ArithmeticException
	 *             if scalar is zero in epsilon range.
	 */
	public Vector3D div(double scalar, ScalarOperator sop) {
		if (sop.equal(scalar, 0)) {
			throw new ArithmeticException(Db3dSimpleResourceBundle
					.getString("db3d.geom.scdivzero"));
		}
		setX(getX() / scalar);
		setY(getY() / scalar);
		setZ(getZ() / scalar);
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
		double value = getX() * vector.getX();
		value += getY() * vector.getY();
		value += getZ() * vector.getZ();
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
		double[] scalars = this.getScalars();
		double _x = (scalars[1] * vector.getZ()) - (scalars[2] * vector.getY());
		double _y = (scalars[2] * vector.getX()) - (scalars[0] * vector.getZ());
		double _z = (scalars[0] * vector.getY()) - (scalars[1] * vector.getX());
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
		double _x = getX();
		double _y = getY();
		double _z = getZ();
		return Math.sqrt((_x * _x) + (_y * _y) + (_z * _z));
	}

	/**
	 * Returns a normalized version of this as new object.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return Vector3D normalized new Vector3D object.
	 * @throws ArithmeticException
	 *             - if norm is equal zero in epsilon range.
	 */
	public Vector3D getNormalized(ScalarOperator sop) {
		double norm = this.getNorm();
		if (sop.equal(norm, 0)) {
			throw new ArithmeticException(Db3dSimpleResourceBundle
					.getString("db3d.geom.normdivzero"));
		}

		return new Vector3D(getX() / norm, getY() / norm, getZ() / norm);
	}

	/**
	 * Normalizes this.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @throws ArithmeticException
	 *             if norm equals zero in epsilon range.
	 */
	public void normalize(ScalarOperator sop) {
		double norm = this.getNorm();
		if (sop.equal(norm, 0)) {
			throw new ArithmeticException(Db3dSimpleResourceBundle
					.getString("db3d.geom.normdivzero"));
		}

		setX(getX() / norm);
		setY(getY() / norm);
		setZ(getZ() / norm);
	}

	/**
	 * Returns the x component of this.
	 * 
	 * @return double - x component.
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * Returns the y component of this.
	 * 
	 * @return double - y component.
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * Returns the z component of this.
	 * 
	 * @return double - z component.
	 */
	public double getZ() {
		return this.z;
	}

	/**
	 * Returns this interpreted as a position vector as Point3D.
	 * 
	 * @return Point3D - position as Point3D.
	 */
	public Point3D getAsPoint3D() {
		return new Point3D(getX(), getY(), getZ());
	}

	/**
	 * Returns the components of this.
	 * 
	 * @return double[] - double array of this.
	 */
	public double[] getScalars() {
		return new double[] { getX(), getY(), getZ() };
	}

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
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.wrindscxyz"));
		}
	}

	/**
	 * Sets the x component of this to given scalar.
	 * 
	 * @param x
	 *            new value as double
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y component of this to given scalar.
	 * 
	 * @param y
	 *            new value as double
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Sets the z component of this to given scalar.
	 * 
	 * @param z
	 *            new value as double
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Tests whether this is the null vector.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean isNullVector(ScalarOperator sop) {
		return ((sop.equal(getX(), 0)) && (sop.equal(getY(), 0)) && (sop.equal(
				getZ(), 0)));
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
	public int isCollinear(Vector3D vector, ScalarOperator sop) {
		if (sop.equal(this.cosinus(vector, sop), 1))
			return 1;
		if (sop.equal(this.cosinus(vector, sop), -1))
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
	public boolean isOrthogonal(Vector3D vector, ScalarOperator sop) {
		return sop.equal(this.cosinus(vector, sop), 0);
	}

	/**
	 * Computes the cosine of the angle between this and the given Vector3D.
	 * 
	 * @param v
	 *            given Vector3D
	 * @return double - value between -1 and +1.
	 */
	public double cosinus(Vector3D v, ScalarOperator sop) {
		double scalar = this.scalarproduct(v);
		scalar = scalar / (this.getNorm() * v.getNorm());
		if (sop.equal(Math.abs(scalar), 1)) {
			if (sop.equal(scalar, 1))
				return 1;
			else
				return -1;
		}
		return scalar;
	}

	/**
	 * Tests whether this is equal to given vector (strong typed).
	 * 
	 * @param vector
	 *            Vector3D to test
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean isEqual(Vector3D vector, ScalarOperator sop) {
		double[] scalars = this.getScalars();
		if (!(sop.equal(scalars[0], vector.getX())))
			return false;
		if (!(sop.equal(scalars[1], vector.getY())))
			return false;
		if (!(sop.equal(scalars[2], vector.getZ())))
			return false;

		return true;
	}

	/**
	 * Copies this.
	 * 
	 * @return Vector3D - deep copy of this.
	 */
	public Vector3D copy() {
		return new Vector3D(this.getX(), this.getY(), this.getZ());
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return VECTOR3D always.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.VECTOR3D;
	}

	/**
	 * For Vector3D a MBB3D is returned modeling the Vector as a Point.
	 * 
	 * @return MBB3D - a minimum bounding box for this vector, which is modeled
	 *         as a point.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @see db3d.dbms.structure.GeoObj#getMBB()
	 */
	public MBB3D getMBB() {
		return new MBB3D(new Point3D(getX(), getY(), getZ()), new Point3D(
				getX(), getY(), getZ()));
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
		Vector3D other = (Vector3D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

}
