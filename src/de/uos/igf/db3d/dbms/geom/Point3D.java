/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.resources.DB3DLogger;

/**
 * Point3D is the geometric representation of a point in the 3rd dimension.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Point3D implements PersistentObject, SimpleGeoObj, Equivalentable,
		Externalizable {
	
	public int id;

	/* coordinates */
	private double x;
	private double y;
	private double z;
	private String[][] attributes;

	
	
	/**
	 * Returns the attributes of Point3D in a formatted string,
	 * or provides an siutable message if no attributes are stored for the Point. 
	 * @return String
	 */
	private String attributesToString() {
		String attrString = "";
		if (this.attributes != null) {
			if (this.attributes.length != 0) {
				for (int i = 0; i < this.attributes.length; i++) {
					attrString = attrString + this.attributes[i][0] + ":"
							+ this.attributes[i][1] + ", ";
				}
				int lastIndex = attrString.lastIndexOf(", ");
				attrString = ", attributes= " + attrString.substring(0, lastIndex);
			} else {
				attrString = "Attributes initialized with count 0, i.e. no names and values provided!";
			}
		} else {
//			attrString = "No attributes initialized!";
			attrString = "";
		}
		return attrString;
	}

	/**
	 * Initializer for attributes and their values. Initializes and sets the
	 * attributes of a Point3D object with given values.
	 * 
	 */
	private void AttributeInitializer(int numOfAttributes,
			String[][] attributesArray) {
		if (numOfAttributes >= attributesArray.length) {
			initNumOfAttributes(numOfAttributes);
			initAttributes(attributesArray);
		} else {
			DB3DLogger.logger
					.log(Level.FINER,
							"Info Warning: Too many attributes provided for initialized number of attributes.");
		}
	}

	/**
	 * Initializer for empty two-dimensional string array with provided length,
	 * to hold attributes for the Point3D object.
	 * 
	 */
	private void initNumOfAttributes(int numOfAttributes) {
		this.attributes = new String[numOfAttributes][2];
	}

	/**
	 * Fills attributes array initialized with initNumOfAttributes(...) with
	 * given two-dimensional string array.
	 * 
	 */
	private void initAttributes(String[][] attributesArray) {

		for (int i = 0; i < attributesArray.length; i++) {

			try {
				this.attributes[i][0] = attributesArray[i][0].toLowerCase();
				this.attributes[i][1] = attributesArray[i][1].toLowerCase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the value of a given attribute name of the Point3D object.
	 * 
	 */
	public String getAttributeValue(String attributeName) {
		if (this.attributes != null) {
			for (int i = 0; i < this.attributes.length; i++) {
				if (attributeName.trim().toLowerCase().equals(this.attributes[i][0])) {
					return this.attributes[i][1];
				}
			}
			DB3DLogger.logger.log(
					Level.FINER,
					"Info Warning: No attribute named "
							+ attributeName.toLowerCase()
							+ " found. Returned null!");
			return null;
		} else {
			DB3DLogger.logger
					.log(Level.FINER,
							"Info Warning: No attributes initialized. Returned null!!!");
			return null;
		}
	}

	/**
	 * Adds a new pair of attribute name and value to the attribute array of the
	 * current Point3D object if possible (i.e. array initialized AND still free
	 * space in array).
	 * 
	 */
	public boolean setAttribute(String attributeName, String attributeValue) {
		if (this.attributes != null) {

			int indexOfNull = -2;
			for (int i = 0; i < this.attributes.length; i++) {
				if (this.attributes[i][0] == null) {
					this.attributes[i][0] = attributeName.toLowerCase().trim();
					this.attributes[i][1] = attributeValue.toLowerCase().trim();
					indexOfNull = i;
					return true;
				} else if (this.attributes[i][0].equals(attributeName
						.toLowerCase().trim())) {
					DB3DLogger.logger.log(Level.FINER,
							"Info Warning: Attribute already exists.");
					indexOfNull = 0;
					return false;
				}
			}
			if (indexOfNull == -2) {
				DB3DLogger.logger.log(Level.FINER,
						"Info Warning: All attribute names are set.");
				return false;
			}
			return false;

		} else {
			DB3DLogger.logger.log(Level.FINER,
					"Info Warning: No attributes initialized.");
			return false;
		}
	}

	/**
	 * Fills attributes array initialized with initNumOfAttributes(...) only
	 * with given attribute names in string array (i.e. without values).
	 * 
	 */
	private void initAttributeNames(String[] names) {
		if (names.length == this.attributes.length) {
			for (int i = 0; i < names.length; i++) {
				this.attributes[i][0] = names[i];
			}
		} else if (names.length > this.attributes.length) {
			System.out.println("Too much names provided for given count");
		} else if (names.length < this.attributes.length) {
			System.out.println("Too view names provided for given count");
		}

	}

	/**
	 * Returns the two-dimensional string array of the Point3D object
	 * 
	 * @return String[][] - attributes.
	 */
	public String[][] getAttributes() {
		return this.attributes;
	}

	/*
	 * private void initAttributeValues(String[] values) { if (values.length ==
	 * this.attributes.length) { for (int i = 0; i < values.length; i++) {
	 * this.attributes[i][1] = values[i]; } } else if (values.length >
	 * this.attributes.length) {
	 * System.out.println("Too much values provided for given count"); } else if
	 * (values.length < this.attributes.length) {
	 * System.out.println("Too view values provided for given count"); }
	 * 
	 * }
	 */

	/*
	 * private void updateAttribute(int updateID, String oldString, String
	 * newString){ Boolean nameTest = false; for (int i = 0; i <
	 * this.attributes.length; i++) { if
	 * (oldString.toLowerCase().equals(this.attributes[i][0])) { nameTest =
	 * true; this.attributes[i][updateID] = newString.toLowerCase(); } } if
	 * (!nameTest) { DB3DLogger.logger.log( Level.FINER,
	 * "Info Warning: Searched attribute does not exist!"); } }
	 */

	/*
	 * public void updateAttributeValue(String name, String newValue) { if
	 * (this.attributes != null) { updateAttribute(1, name, newValue); } else {
	 * DB3DLogger.logger.log(Level.FINER, "Info Warning: No attribute named " +
	 * name + "!"); } }
	 */

	/*
	 * public void updateAttributeName(String oldName, String newName) { if
	 * (this.attributes != null) { updateAttribute(0, oldName, newName); } else
	 * { DB3DLogger.logger.log(Level.FINER, "Info Warning: No attribute named "
	 * + oldName + "!"); } }
	 */

	/**
	 * Constructor for double coordinates and attributes. Constructs a Point3D
	 * object with given x, y, z coordinates and the provided attributes and
	 * their values.
	 * 
	 ** @param x
	 *            double value of x axis
	 * @param y
	 *            double value of y axis
	 * @param z
	 *            double value of z axis
	 */
	public Point3D(double x, double y, double z, int numOfAttributes,
			String[][] attributesArray) {
		if (attributesArray.length > numOfAttributes)
			DB3DLogger.logger.log(Level.WARNING,
					"Info Warning: Too many attributes!");

		this.x = x;
		this.y = y;
		this.z = z;
		
		if (numOfAttributes > 0) {
			AttributeInitializer(numOfAttributes, attributesArray);
		}

	}

	/**
	 * 
	 * Constructor for double coordinates and attributes. <br>
	 * Constructs a Point3D object with given x, y, z coordinates and he
	 * provided attributes and their values.
	 * 
	 * @param coords
	 *            double array with values x,y,z axis
	 */
	public Point3D(double[] coords, int numOfAttributes,
			String[][] attributesArray) {
		
		if (attributesArray.length > numOfAttributes)
			DB3DLogger.logger.log(Level.WARNING,
					"Info Warning: Too many attributes!");
		
		this.x = coords[0];
		this.y = coords[1];
		this.z = coords[2];

		if (numOfAttributes > 0) {
			AttributeInitializer(numOfAttributes, attributesArray);
		}

	}

	/**
	 * Constructor for double coordinates and initializing the number of
	 * attributes. Constructs a Point3D object with given x, y, z coordinates
	 * and initializes a two-dimensional String array for the attributes.
	 * 
	 ** @param x
	 *            double value of x axis
	 * @param y
	 *            double value of y axis
	 * @param z
	 *            double value of z axis
	 */
	public Point3D(double x, double y, double z, int numOfAttributes) {
		this(x, y, z);

		if (numOfAttributes > 0) {
			initNumOfAttributes(numOfAttributes);
		}
	}

	/**
	 * Constructor for double coordinates and initializing the number of
	 * attributes. Constructs a Point3D object with given x, y, z coordinates
	 * and initializes a two-dimensional String array for the attributes.
	 * 
	 ** @param x
	 *            double value of x axis
	 * @param y
	 *            double value of y axis
	 * @param z
	 *            double value of z axis
	 */
	public Point3D(double[] coords, int numOfAttributes) {
		this(coords[0], coords[1], coords[2]);

		if (numOfAttributes > 0) {
			initNumOfAttributes(numOfAttributes);
		}
	}

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
		
		if (point.attributes != null) {
			initNumOfAttributes(point.attributes.length);
			initAttributes(point.attributes);			
		}
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
			throw new IllegalStateException(
					Db3dSimpleResourceBundle.getString("db3d.geom.onlythree"));
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
			throw new IllegalStateException(
					Db3dSimpleResourceBundle
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

	// @Override
	// /**
	// * Converts this to string.
	// * @return String with the information of this.
	// */
	// public String toString() {
	// return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	// }

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
	 * Equals method! TODO: Use the scalarOperator from the actual space!
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		double[] objcoords = ((Point3D) obj).getCoordinates();

		ScalarOperator sop = new ScalarOperator();

		for (int i = 0; i < 3; i++) {
			if (!(sop.equal(this.getCoord(i), objcoords[i])))
				return false;
		}
		
//		for (int i = 0; i < 3; i++) {
//			if (!(this.getCoord(i) == objcoords[i]))
//				return false;
//		}
		return true;
	}

	@Override
	public String toString() {
		return "Point3D [x=" + x + ", y=" + y + ", z=" + z + attributesToString() + "]";
	}
}
