/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.resources.DB3DLogger;

/**
 * Triangle3D is the geometric representation of a triangle in 3D. <br>
 * A Triangle3D object is modeled by 3 Point3D objects. The points are stored in
 * the variables zero, one and two. For orientation of a triangle in 3D the
 * normal vector is computed from the points in an ascending index order or
 * equivalent direction ( e.g. 0->1->2 or 2->0->1). t
 * 
 * @author Wolfgang Baer - Dag Hammerich / University of Osnabrueck
 */
@SuppressWarnings("serial")
public class Triangle3D implements PersistentObject, SimpleGeoObj,
		Equivalentable, Serializable {

	/* geometry */
	private Point3D zero;
	private Point3D one;
	private Point3D two;
	private String[][] attributes;

	/* normal vector (normalized) - transient */
	private transient Vector3D normvec = null;

	/* line segments of this [0,2] */
	private transient Segment3D[] lines = null;

	/**
	 * Returns the attributes of Triangle3D in a formatted string, or provides an
	 * siutable message if no attributes are stored for the Triangle.
	 * 
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
				attrString = attrString.substring(0, lastIndex);
			} else {
				attrString = "Attributes initialized with count 0, i.e. no names and values provided!";
			}
		} else {
			attrString = "No attributes initialized!";
		}
		return attrString;
	}

	/**
	 * Initializer for attributes and their values. Initializes and sets the
	 * attributes of a Triangle3D object with given values.
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
	 * to hold attributes for the Triangle3D object.
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
	 * Returns the value of a given attribute name of the Triangle3D object.
	 * 
	 */
	public String getAttributeValue(String attributeName) {
		if (this.attributes != null) {
			for (int i = 0; i < this.attributes.length; i++) {
				if (attributeName.trim().toLowerCase()
						.equals(this.attributes[i][0])) {
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
	 * current Triangle3D object if possible (i.e. array initialized AND still free
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
	 * Returns the two-dimensional string array of the Triangle3D object
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

	
//	Constructors with number and attributes itself provided
	/**
	 * Constructor
	 * 
	 * @param pts
	 *            Point3D array
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur
	 * @param numOfAttributes
	 *            maximum number of attributes which are or will be provided
	 * @param attributesArray
	 *            2D String Array holding attributes and values of maximum
	 *            length of numOfAttributes
	 */
	public Triangle3D(Point3D[] pts, ScalarOperator sop, int numOfAttributes,
			String[][] attributesArray) throws IllegalArgumentException  {
		
		this(pts, sop);
		
		if (attributesArray.length > numOfAttributes)
			DB3DLogger.logger.log(Level.WARNING,
					"Info Warning: Too many attributes!");
		
		if (numOfAttributes > 0) {
			AttributeInitializer(numOfAttributes, attributesArray);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param point1
	 *            Point3D 1
	 * @param point2
	 *            Point3D 2
	 * @param point3
	 *            Point3D 3
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur
	 * @param numOfAttributes
	 *            maximum number of attributes which are or will be provided
	 * @param attributesArray
	 *            2D String Array holding attributes and values of maximum
	 *            length of numOfAttributes
	 */
	public Triangle3D(Point3D point1, Point3D point2, Point3D point3,
			ScalarOperator sop, int numOfAttributes, String[][] attributesArray) {

		this(point1, point2, point3, sop);
		
		if (attributesArray.length > numOfAttributes)
			DB3DLogger.logger.log(Level.WARNING,
					"Info Warning: Too many attributes!");
		
		if (numOfAttributes > 0) {
			AttributeInitializer(numOfAttributes, attributesArray);
		}

	}

	/**
	 * Constructor<br>
	 * The given point must not intersect with the segment.
	 * 
	 * @param point
	 *            Point3D
	 * @param seg
	 *            Segment3D
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur.
	 * @param numOfAttributes
	 *            maximum number of attributes which are or will be provided
	 * @param attributesArray
	 *            2D String Array holding attributes and values of maximum
	 *            length of numOfAttributes
	 * 
	 */
	public Triangle3D(Point3D point, Segment3D seg, ScalarOperator sop,
			int numOfAttributes, String[][] attributesArray) {

		this(point, seg, sop);
		
		if (attributesArray.length > numOfAttributes)
			DB3DLogger.logger.log(Level.WARNING,
					"Info Warning: Too many attributes!");

		if (numOfAttributes > 0) {
			AttributeInitializer(numOfAttributes, attributesArray);
		}
	}

//	Constructors only with number of attributes provided

	/**
	 * Constructor
	 * 
	 * @param pts
	 *            Point3D array
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur
	 * @param numOfAttributes
	 *            maximum number of attributes which are or will be provided
	 * @param attributesArray
	 *            2D String Array holding attributes and values of maximum
	 *            length of numOfAttributes
	 */
	public Triangle3D(Point3D[] pts, ScalarOperator sop, int numOfAttributes) throws IllegalArgumentException  {
		
		this(pts, sop);
		
		if (numOfAttributes > 0) {
			initNumOfAttributes(numOfAttributes);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param point1
	 *            Point3D 1
	 * @param point2
	 *            Point3D 2
	 * @param point3
	 *            Point3D 3
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur
	 * @param numOfAttributes
	 *            maximum number of attributes which are or will be provided
	 * @param attributesArray
	 *            2D String Array holding attributes and values of maximum
	 *            length of numOfAttributes
	 */
	public Triangle3D(Point3D point1, Point3D point2, Point3D point3,
			ScalarOperator sop, int numOfAttributes) {

		this(point1, point2, point3, sop);

		if (numOfAttributes > 0) {
			initNumOfAttributes(numOfAttributes);
		}

	}

	/**
	 * Constructor<br>
	 * The given point must not intersect with the segment.
	 * 
	 * @param point
	 *            Point3D
	 * @param seg
	 *            Segment3D
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur.
	 * @param numOfAttributes
	 *            maximum number of attributes which are or will be provided
	 * @param attributesArray
	 *            2D String Array holding attributes and values of maximum
	 *            length of numOfAttributes
	 * 
	 */
	public Triangle3D(Point3D point, Segment3D seg, ScalarOperator sop,
			int numOfAttributes) {

		this(point, seg, sop);

		if (numOfAttributes > 0) {
			initNumOfAttributes(numOfAttributes);
		}
	}

	
//	Constructors without attributes	
	
	/**
	 * Constructor.
	 * 
	 * @param pts
	 *            Point3D array
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Triangle3D(Point3D[] pts, ScalarOperator sop)
			throws IllegalArgumentException {
		if (pts == null || pts.length != 3)
			throw new IllegalArgumentException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.trionlythree"));

		this.zero = pts[0];
		this.one = pts[1];
		this.two = pts[2];
		this.normvec = null;
		this.lines = null;

		// validate
		if (sop != null) {
			if (!isValid(sop))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotval"));
			if (!isRegular(sop))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotreg"));
			if (!isBeautiful(sop))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotbeau"));
		}
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
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if validation fails.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Triangle3D(Point3D point1, Point3D point2, Point3D point3,
			ScalarOperator sop) {
		this.zero = point1;
		this.one = point2;
		this.two = point3;
		this.normvec = null;
		this.lines = null;

		// validate
		if (sop != null) {
			if (!isValid(sop))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotval"));
			if (!isRegular(sop))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotreg"));
			if (!isBeautiful(sop))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotbeau"));
		}
	}

	/**
	 * Constructor.<br>
	 * The given point must not intersect with the segment.
	 * 
	 * @param point
	 *            Point3D
	 * @param seg
	 *            Segment3D
	 * @param sop
	 *            ScalarOperator, needed for validation. If ScalarOperator is
	 *            <code>null</code>, no validation will occur.
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Triangle3D(Point3D point, Segment3D seg, ScalarOperator sop) {
		this(point, seg.getPoint(0), seg.getPoint(1), sop);
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
	public Triangle3D(Triangle3D tr) {
		this(new Point3D(tr.getPoint(0)), new Point3D(tr.getPoint(1)),
				new Point3D(tr.getPoint(2)), null);
		if (tr.attributes != null) {
			initNumOfAttributes(tr.attributes.length);
			initAttributes(tr.attributes);			
		}
	}

	/**
	 * Returns the geometry of the triangle as a newly created array !.<br>
	 * Array gets invalid if a setPointX() method is called !
	 * 
	 * @return Point3D[] - array of Point3D objects.
	 */
	public Point3D[] getPoints() {
		return new Point3D[] { this.zero, this.one, this.two };
	}

	/**
	 * Sets the points of this.
	 * 
	 * @param points
	 *            Point[] to which the points of this should be set
	 */
	public void setPoints(Point3D[] points) {
		this.zero = points[0];
		this.one = points[1];
		this.two = points[2];
		this.lines = null;
		this.normvec = null;
	}

	/**
	 * Returns the Point3D for given index.
	 * 
	 * @param index
	 *            int point index
	 * @return Point3D for given index.
	 * @throws IllegalArgumentException
	 *             - if the index is not 0, 1 or 2. |
	 */
	public Point3D getPoint(int index) {
		switch (index) {
		case 0:
			return this.zero;
		case 1:
			return this.one;
		case 2:
			return this.two;
		default:
			throw new IllegalArgumentException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.wrongindtrionlythree"));
		}
	}

	/**
	 * Sets the Point3D to given index.
	 * 
	 * @param index
	 *            int point index
	 * @param point
	 *            Point3D to given index.
	 * @throws IllegalArgumentException
	 *             - if index is not 0, 1 or 2.
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
		default:
			throw new IllegalArgumentException(
					Db3dSimpleResourceBundle
							.getString("db3d.geom.wrongindtrionlythree"));
		}
		this.lines = null;
		this.normvec = null;
	}

	/**
	 * Tests whether the given point is part of this triangle. This method only
	 * tests if this point is a base point of this triangle. It explicitly does
	 * not test for containment; use <code>contains</code> method instead.
	 * 
	 * @param point
	 *            point to test "inclusion"
	 * @param sop
	 *            ScalarOperator, accuracy value
	 * @return boolean - true if the point is part of this triangle, false
	 *         otherwise.
	 */
	public boolean hasCorner(Point3D point, ScalarOperator sop) {
		return zero.isEqual(point, sop) || one.isEqual(point, sop)
				|| two.isEqual(point, sop);
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
		Point3D[] points = this.getPoints();
		if (points[0] != null && points[1] != null && points[2] != null) {
			Point3D pMin = new Point3D(GeomUtils.getMin(points[0].getX(),
					points[1].getX(), points[2].getX()), GeomUtils.getMin(
					points[0].getY(), points[1].getY(), points[2].getY()),
					GeomUtils.getMin(points[0].getZ(), points[1].getZ(),
							points[2].getZ()));
			Point3D pMax = new Point3D(GeomUtils.getMax(points[0].getX(),
					points[1].getX(), points[2].getX()), GeomUtils.getMax(
					points[0].getY(), points[1].getY(), points[2].getY()),
					GeomUtils.getMax(points[0].getZ(), points[1].getZ(),
							points[2].getZ()));
			return new MBB3D(pMin, pMax);
		} else
			return null;
	}

	/**
	 * Returns the normalized normal vector for this triangle.<br>
	 * The normal vector is computed from the points in the ascending index
	 * order or equivalent direction ( e.g. 0->1->2 or 2->0->1).
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return Vector3D - normal vector for this.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Vector3D getNormal(ScalarOperator sop) {
		if (normvec == null) {
			double x = getPoint(0).getY()
					* (getPoint(1).getZ() - getPoint(2).getZ())
					+ getPoint(1).getY()
					* (getPoint(2).getZ() - getPoint(0).getZ())
					+ getPoint(2).getY()
					* (getPoint(0).getZ() - getPoint(1).getZ());
			double y = getPoint(0).getZ()
					* (getPoint(1).getX() - getPoint(2).getX())
					+ getPoint(1).getZ()
					* (getPoint(2).getX() - getPoint(0).getX())
					+ getPoint(2).getZ()
					* (getPoint(0).getX() - getPoint(1).getX());
			double z = getPoint(0).getX()
					* (getPoint(1).getY() - getPoint(2).getY())
					+ getPoint(1).getX()
					* (getPoint(2).getY() - getPoint(0).getY())
					+ getPoint(2).getX()
					* (getPoint(0).getY() - getPoint(1).getY());

			this.normvec = new Vector3D(x, y, z);
			this.normvec.normalize(sop);
		}
		return this.normvec;
	}

	/**
	 * Returns the angles of this triangle.<br>
	 * Angle at index 0 -> Point at index 0 ...
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return double[] - angles.
	 */
	public double[] getAngles(ScalarOperator sop) { // Dag
		double[] angles = { -1, -1, -1 };
		Vector3D v0 = this.getVectors()[0];
		Vector3D v1 = this.getVectors()[1];
		Vector3D v2 = this.getVectors()[2];

		// angle in corner with index 0
		double phi = java.lang.Math.acos((Vector3D.sub(v1, v0)).cosinus(
				Vector3D.sub(v2, v0), sop));
		angles[0] = (phi / Math.PI) * 180; // umrechnung in grad

		// angle in corner with index 1
		phi = java.lang.Math.acos(Vector3D.sub(v2, v1).cosinus(
				Vector3D.sub(v0, v1), sop));
		angles[1] = (phi / Math.PI) * 180;

		// angle in corner with index 2
		phi = java.lang.Math.acos(Vector3D.sub(v1, v2).cosinus(
				Vector3D.sub(v0, v2), sop));
		angles[2] = (phi / Math.PI) * 180;

		return angles;
	}

	/**
	 * Returns the vectors of the points of this triangle.
	 * 
	 * @return Vector3D[] - vectors of the points of this.
	 */
	public Vector3D[] getVectors() {
		return new Vector3D[] { new Vector3D(this.zero),
				new Vector3D(this.one), new Vector3D(this.two) };
	}

	/**
	 * Returns the plane corresponding to this triangle.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return Plane3D corresponding to this triangle.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Plane3D getPlane(ScalarOperator sop) {
		return new Plane3D(this, sop);
	}

	/**
	 * Returns the Segment3D[] of this.<br>
	 * The array contains for every index the line which lies opposite to the
	 * point with given index.<br>
	 * Index 0 (=P0) - Line [P1,P2] <br>
	 * Index 1 (=P1) - Line [P2,P0] <br>
	 * Index 2 (=P2) - Line [P0,P1] <br>
	 * 
	 * @return Segment3D[] - of this.
	 */
	public Segment3D[] getSegments() {
		if (this.lines == null)
			buildLines();

		return lines;
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
	public Segment3D getSegment(int index) {
		if (this.lines == null)
			buildLines();

		return lines[index];
	}

	/**
	 * Returns the index for given Segment3D,
	 * <code>-1</null> if segment is not an edge.
	 * 
	 * @param seg
	 *            segment to check as edge
	 * @return int - index of given segment, -1 if seg is not an edge of this.
	 */
	public int getSegmentIndex(Segment3D seg, ScalarOperator sop) {

		if (this.getSegment(0).isGeometryEquivalent(seg, sop))
			return 0;
		if (this.getSegment(1).isGeometryEquivalent(seg, sop))
			return 1;
		if (this.getSegment(2).isGeometryEquivalent(seg, sop))
			return 2;
		return -1;
	}

	/**
	 * Returns the area of this.
	 * 
	 * @return double - area.
	 */
	public double getArea() { // Dag
		/*
		 * Alternative: 1/2 of the vector product of two vectors of this: area =
		 * ( this.getVectors()[0].crossproduct(this.getVectors()[1]) ).getNorm()
		 * / 2;
		 */

		/*
		 * Heron's formula F = sqrt( s * (s-a) * (s-b) * (s-c) ); where: s = (a
		 * + b + c)/2 (half perimeter)
		 */
		double a = getSegment(0).getLength();
		double b = getSegment(1).getLength();
		double c = getSegment(2).getLength();
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
	public int getOrientation(Vector3D vec, ScalarOperator sop) { // Dag

		if (vec.isOrthogonal(this.getNormal(sop), sop))
			return 0;
		double scalar = vec.scalarproduct(this.getNormal(sop));
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
	public double getDiameter(ScalarOperator sop) {// Dag

		/*
		 * diameter = 2*R of the circumscribed circle, its centre is the
		 * intersection point of the perpendicular bisectors. Here, the
		 * following property if fortunately true: 2*r = a / sin(alfa), where
		 * alfa is the angle opposite of the triangle edge a (is of course true
		 * for all edges).
		 */

		double diam = this.getSegment(0).getLength()
				/ Math.sin((((this.getAngles(sop)[0]) / 180) * Math.PI));
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

		Point3D[] p = this.getPoints();
		double x = (p[0].getX() + p[1].getX() + p[2].getX()) / 3;
		double y = (p[0].getY() + p[1].getY() + p[2].getY()) / 3;
		double z = (p[0].getZ() + p[1].getZ() + p[2].getZ()) / 3;
		return new Point3D(x, y, z);

		/*
		 * Alternative: (both ways are possible)
		 * 
		 * The center of the triangle is calculated as the intersection of two
		 * medians. Calculating the two medians:
		 * 
		 * Segment3D middel1 = new Segment3D( (
		 * getVectors()[0].add(getVectors()[1]) ).mult(0.5),
		 * this.getVectors()[2], this.getScalarOperator()); Segment3D middel2 =
		 * new Segment3D( ( getVectors()[0].add(getVectors()[2]) ).mult(0.5),
		 * this.getVectors()[1], this.getScalarOperator());
		 * 
		 * It is not necessary to test if the medians intersect because they
		 * MUST intersect in one point.
		 * 
		 * Point3D interceptPoint = (Point3D) (middel1.intersection(middel2,
		 * this.getScalarOperator()) ); return interceptPoint;
		 */

	}

	/**
	 * Test whether this intersects the given MBB.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
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
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
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
	 *             method intersection(MBB3D, ScalarOperator) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(MBB3D mbb, ScalarOperator sop) {

		if (!this.getMBB().intersects(mbb, sop))
			return false;

		// test if at least one corner of this is inside of mbb
		for (int i = 0; i < 3; i++)
			if (mbb.contains(this.getPoint(i), sop))
				return true;

		// intersection of mbb with plane of this and test if result intersects
		// with this
		SimpleGeoObj obj = mbb.intersection(this.getPlane(sop), sop);
		// Here an IllegalStateException can be thrown. This exception
		// originates in the method getPoint(int) of the class Rectangle3D.

		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		// Here an IllegalStateException can be thrown signaling problems with
		// the index of a point coordinate.
		if (obj == null)
			return false;

		switch (obj.getType()) {
		case SimpleGeoObj.POINT3D:
			Point3D p = ((Point3D) obj);
			if (this.containsInPlane(p, sop))
				return true;
			return false;
		case SimpleGeoObj.SEGMENT3D:
			Segment3D seg = ((Segment3D) obj);
			if (this.intersects(seg, sop))
				return true;
			return false;
		case SimpleGeoObj.TRIANGLE3D:
			Triangle3D tri = ((Triangle3D) obj);
			if (this.intersects(tri, sop))
				// Here an IllegalStateException can be thrown signaling
				// problems with the dimensions of the wireframe.
				return true;
			return false;
		case SimpleGeoObj.WIREFRAME3D:
			Wireframe3D wf = ((Wireframe3D) obj);
			Segment3D[] segs = wf.getSegments();
			Point3D centroid = wf.getCentroid();
			int length = segs.length;
			for (int i = 0; i < length; i++) {
				Triangle3D triangle = new Triangle3D(centroid,
						segs[i].getPoint(0), segs[i].getPoint(1), sop);
				if (this.intersects(triangle, sop))
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
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection in this method is not a
	 *             simplex.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsStrict(MBB3D mbb, ScalarOperator sop) {

		if (!this.getMBB().intersectsStrict(mbb, sop))
			return false;
		// test if at least one corner of this is strict inside of mbb
		for (int i = 0; i < 3; i++)
			if (mbb.containsStrict(this.getPoint(i), sop))
				return true;

		SimpleGeoObj obj = mbb.intersection(this.getPlane(sop), sop);
		// Here an IllegalStateException can be thrown. This exception
		// originates in the getPoint(int) method of the class Rectangle3D.

		// Here an IllegalStateException can be thrown signaling problems with
		// the dimension of the wireframe.

		if (obj == null)
			return false;

		switch (obj.getType()) {
		case SimpleGeoObj.POINT3D:
			return false;
		case SimpleGeoObj.SEGMENT3D:
			return false;
		case SimpleGeoObj.TRIANGLE3D:
			Triangle3D tri = ((Triangle3D) obj);
			obj = this.intersectionInPlane(tri, sop);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensinos of the wireframe.
			if (obj == null)
				return false;
			if (obj.getType() == SimpleGeoObj.TRIANGLE3D
					|| obj.getType() == SimpleGeoObj.WIREFRAME3D)
				return true;
			else
				return false;
		case SimpleGeoObj.WIREFRAME3D:
			Wireframe3D wf = ((Wireframe3D) obj);
			Triangle3D[] t = wf.getTriangulated();
			// TODO it seems to be buggy to assume here that getTriangulated()
			// only return 2 triangles:
			for (int i = 0; i < 2; i++) {
				obj = this.intersectionInPlane(t[i], sop);
				if (obj.getType() == SimpleGeoObj.TRIANGLE3D
						|| obj.getType() == SimpleGeoObj.WIREFRAME3D)
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

		Plane3D plane = this.getPlane(sop);

		SimpleGeoObj type = plane.intersection(line, sop);
		if (type == null)
			return false;
		if (type.getType() == SimpleGeoObj.LINE3D) {
			if (this.intersectionInPlane(line, sop) != null)
				return true;
			else
				return false;
		} else { // plane and line intersect - test intersection point for
			// inside
			Point3D point = (Point3D) type;
			if (this.containsInPlane(point, sop))
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
	public boolean intersects(Segment3D segment, ScalarOperator sop) { // Dag

		SimpleGeoObj type = this.getPlane(sop).intersection(segment, sop);
		if (type == null)
			return false;
		if (type.getType() == SimpleGeoObj.SEGMENT3D) {
			Segment3D seg = (Segment3D) type;
			if (this.intersectionInPlane(seg, sop) != null) {
				return true;
			}
			return false;
		} else {
			// plane and line intersect - test intersection point for inside
			Point3D point = (Point3D) type;
			if (this.containsInPlane(point, sop))
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
	public boolean intersects(Plane3D plane, ScalarOperator sop) { // Dag
		// Idee: berechnung der skalarprodukte aus jeweils einem punktvektor und
		// normalvektor
		// mit Ebenendefinition (normalvektor * ortsvektor = c) folgt, dass
		// ein schnitt dann gegeben ist, wenn fuer mind. einen Punkt das
		// ergebnis c ist oder
		// fuer zwei Punkte gilt (scalarprod1 < c && scalarprod2 > c)

		Vector3D norm = plane.getNormalVector();

		double c = norm.scalarproduct(plane.getPositionVector());

		double xValue = norm.scalarproduct(this.getVectors()[0]);
		double yValue = norm.scalarproduct(this.getVectors()[1]);
		double zValue = norm.scalarproduct(this.getVectors()[2]);

		// at least on corner point lies on plane
		if ((sop.equal(xValue, c)) || (sop.equal(yValue, c))
				|| (sop.equal(zValue, c)))
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection in this method is not a
	 *             simplex.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Triangle3D triangle, ScalarOperator sop) {// Dag

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

		if (!(this.getMBB().intersects(triangle.getMBB(), sop)))
			return false;
		else {
			// parallelism of planes
			if ((this.getNormal(sop).isCollinear(triangle.getNormal(sop), sop)) != 0) {

				// equality of planes
				if (!(this.getPlane(sop).contains(triangle.getPoint(0), sop)))
					return false;
				else {
					if (this.intersectionInPlane(triangle, sop) != null)
						// Here an IllegalStateException can be thrown signaling
						// problems with the dimensions of the wireframe.
						return true;
					else
						return false;
				}
			}
			// planes not parallel
			else {
				if (!(this.intersects(triangle.getPlane(sop), sop) && triangle
						.intersects(this.getPlane(sop), sop)))
					return false;
				else { // this intersects argumentPlane AND argument intersects
						// thisPlane

					switch (this.intersection(triangle.getPlane(sop), sop)
							.getType()) {

					case SimpleGeoObj.SEGMENT3D: {

						Segment3D thisSegment = (Segment3D) this.intersection(
								triangle.getPlane(sop), sop);

						switch (triangle.intersection(this.getPlane(sop), sop)
								.getType()) {

						case SimpleGeoObj.SEGMENT3D: {

							Segment3D triangleSegment = (Segment3D) triangle
									.intersection(this.getPlane(sop), sop);

							if (triangleSegment.intersects(thisSegment, sop))
								return true; // thisSegment and triangleSegment
							// do have at least on point in
							// common
							else
								return false;
						}
						case SimpleGeoObj.POINT3D: {

							Point3D trianglePoint = (Point3D) triangle
									.intersection(this.getPlane(sop), sop);

							if (thisSegment.contains(trianglePoint, sop))
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

					case SimpleGeoObj.POINT3D: {

						Point3D thisPoint = (Point3D) this.intersection(
								triangle.getPlane(sop), sop);

						switch (triangle.intersection(this.getPlane(sop), sop)
								.getType()) {

						case SimpleGeoObj.SEGMENT3D: {

							Segment3D triangleSegment = (Segment3D) triangle
									.intersection(this.getPlane(sop), sop);

							if (triangleSegment.contains(thisPoint, sop))
								return true; // triangle touches this in
							// trianglePoint
							else
								return false;
						}

						case SimpleGeoObj.POINT3D: {

							Point3D trianglePoint = (Point3D) triangle
									.intersection(this.getPlane(sop), sop);

							if (thisPoint.isEqual(trianglePoint, sop))
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
	public int intersectsInt(Plane3D plane, ScalarOperator sop) { // Dag
		SimpleGeoObj result = this.intersection(plane, sop);
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.TRIANGLE3D)
			return 2;
		if (result.getType() == SimpleGeoObj.SEGMENT3D)
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
	public int intersectsInt(Line3D line, ScalarOperator sop) { // Dag
		SimpleGeoObj result = this.intersection(line, sop);
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.SEGMENT3D)
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
	public int intersectsInt(Segment3D segment, ScalarOperator sop) { // Dag
		SimpleGeoObj result = this.intersection(segment, sop);
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.SEGMENT3D)
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
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
	public int intersectsInt(Triangle3D triangle, ScalarOperator sop) { // Dag
		SimpleGeoObj result = this.intersection(triangle, sop);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.TRIANGLE3D)
			return 2;
		if (result.getType() == SimpleGeoObj.SEGMENT3D)
			return 1;
		if (result.getType() == SimpleGeoObj.WIREFRAME3D)
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
	public boolean intersectsRegular(Plane3D plane, ScalarOperator sop) {
		if (this.intersectsInt(plane, sop) == 1)
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originat
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.es in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean intersectsRegular(Line3D line, ScalarOperator sop) {
		if (this.intersectsInt(line, sop) == 0)
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
	public boolean intersectsRegular(Segment3D segment, ScalarOperator sop) {
		if (this.intersectsInt(segment, sop) == 0)
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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
	public boolean intersectsRegular(Triangle3D triangle, ScalarOperator sop) {
		if (this.intersectsInt(triangle, sop) == 1)
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
	 *             Point3D, ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean projectsRegular(Plane3D plane, ScalarOperator sop) { // Dag
		if (this.projection(plane, sop).getType() == SimpleGeoObj.TRIANGLE3D)
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * @see Triangle3D#contains(Triangle3D, ScalarOperator).
	 */
	public boolean containsInPlane(Point3D point, ScalarOperator sop) { // Dag
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

		if (this.hasCorner(point, sop))
			return true;

		Segment3D[] segments = this.getSegments();
		Segment3D segment;
		Plane3D plane;
		int counter = 0;

		/*
		 * if for all corners is fulfilled that a plane orthogonal to this
		 * containing the corner point and point intersects the opposite edge,
		 * the point must lie in this
		 */
		for (int i = 0; i < 3; i++) {
			segment = segments[i];
			Point3D p3 = Vector3D.add(new Vector3D(this.getPoint(i)),
					this.getNormal(sop)).getAsPoint3D();

			plane = new Plane3D(this.getPoint(i), point, p3, sop);
			if (!plane.intersects(segment, sop))
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise. containment is not
	 *         strict.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * @see Triangle3D#containsProjection(Segment3D, ScalarOperator).
	 */
	protected boolean containsInPlane(Segment3D segment, ScalarOperator sop) { // Dag
		if (segment.getMBB().inside(this.getMBB(), sop)) {
			if (this.containsInPlane(segment.getPoint(0), sop))
				return this.containsInPlane(segment.getPoint(1), sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise. containment is not
	 *         strict.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * @see Triangle3D#containsProjection(Triangle3D, ScalarOperator).
	 */
	protected boolean containsInPlane(Triangle3D triangle, ScalarOperator sop) { // Dag
		if (triangle.getMBB().inside(this.getMBB(), sop)) {
			if (this.containsInPlane(triangle.getPoint(0), sop)
					&& this.containsInPlane(triangle.getPoint(1), sop)
					&& this.containsInPlane(triangle.getPoint(2), sop))
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
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * @see Triangle3D#containsInPlane(Point3D, ScalarOperator).
	 */
	public boolean contains(Point3D point, ScalarOperator sop) {
		if (sop.equal(new Plane3D(this, sop).distance(point), 0))
			return containsInPlane(point, sop);
		else
			return false;
	}

	/**
	 * Tests whether the given point is contained in border of this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsInBorder(Point3D point, ScalarOperator sop) {
		if (this.getSegment(0).contains(point, sop)
				|| this.getSegment(1).contains(point, sop)
				|| this.getSegment(2).contains(point, sop))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the given segment is contained in this.
	 * 
	 * @param segment
	 *            Segment3D
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
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
		/*
		 * Both points of segment have to lie on plane of this and fulfil
		 * containsInPlane
		 */
		if (segment.getMBB().inside(this.getMBB(), sop))
			if ((sop.equal(
					new Plane3D(this, sop).distance(segment.getPoint(0)), 0))
					&& (sop.equal(new Plane3D(this, sop).distance(segment
							.getPoint(1)), 0)))
				return this.containsInPlane(segment, sop);
			else
				return false;

		return false;
	}

	/**
	 * Tests whether the given triangle is contained in this.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
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

		if (triangle.getMBB().inside(this.getMBB(), sop)) {
			if ((sop.equal(
					new Plane3D(this, sop).distance(triangle.getPoint(0)), 0))
					&& (sop.equal(new Plane3D(this, sop).distance(triangle
							.getPoint(1)), 0))
					&& (sop.equal(new Plane3D(this, sop).distance(triangle
							.getPoint(2)), 0)))
				return this.containsInPlane(triangle, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsProjection(Point3D point, ScalarOperator sop) { // Dag
		Point3D projectionPoint = (Point3D) point
				.projection(this.getPlane(sop));
		if (this.containsInPlane(projectionPoint, sop))
			return true;
		else
			return false;
	}

	/**
	 * Tests whether the projection of given segment is contained in this.
	 * 
	 * @param segment
	 *            Segment3D to test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsProjection(Segment3D segment, ScalarOperator sop) { // Dag
		SimpleGeoObj projectionObj = segment
				.projection(this.getPlane(sop), sop);

		if (projectionObj.getType() == SimpleGeoObj.SEGMENT3D)
			return this.containsInPlane(((Segment3D) projectionObj), sop);
		else
			return containsInPlane(((Point3D) projectionObj), sop);
	}

	/**
	 * Tests whether the projection of given triangle is contained in this.
	 * 
	 * @param triangle
	 *            Triangle3D to test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             if the result of projecting the given Triangle3D on the
	 *             Plane3D of this Triangle3D is a Point3D or is not a simplex.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean containsProjection(Triangle3D triangle, ScalarOperator sop) { // Dag
		SimpleGeoObj projectionObj = triangle.projection(this.getPlane(sop),
				sop);

		switch (projectionObj.getType()) {
		case SimpleGeoObj.TRIANGLE3D:
			return this.containsInPlane(((Triangle3D) projectionObj), sop);
		case SimpleGeoObj.SEGMENT3D:
			return this.containsInPlane(((Segment3D) projectionObj), sop);
		case SimpleGeoObj.POINT3D:
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object.
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
		if (!this.intersects(plane, sop))
			return null;

		/*
		 * Fntersect plane with every edge and find out intersection case by
		 * querying the results.
		 */
		else {
			SimpleGeoObj object0 = plane.intersection(this.getSegment(0), sop);
			SimpleGeoObj object1 = plane.intersection(this.getSegment(1), sop);
			SimpleGeoObj object2 = plane.intersection(this.getSegment(2), sop);

			if (object0 != null) {
				if (object0.getType() == SimpleGeoObj.SEGMENT3D) {
					if (object1.getType() == SimpleGeoObj.SEGMENT3D)
						return new Triangle3D(this);
					else
						return object0;
				} else { // object0 = point
					if (object1 != null) {
						if (object1.getType() == SimpleGeoObj.SEGMENT3D)
							return object1;
						else { // object1 = point

							if (((Point3D) object0).isEqual(
									((Point3D) object1), sop))
								return object0;
							else {
								return new Segment3D(((Point3D) object0),
										((Point3D) object1), sop);
							}
						}
					} else { // object0 !=null and object1==null -> object2 must
						// be Point3D
						if (((Point3D) object0).isEqual(((Point3D) object2),
								sop))
							return object0;
						else
							return new Segment3D(((Point3D) object0),
									((Point3D) object2), sop);
					}
				}
			} else {
				if (object1 != null) {
					if (((Point3D) object1).isEqual(((Point3D) object2), sop))
						return object1;
					else
						return new Segment3D(((Point3D) object1),
								((Point3D) object2), sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return resulting geometry object. May be <code>null</code>,
	 *         <code>Point3D</code> or <code>Segment3D</code>.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	protected SimpleGeoObj intersectionInPlane(Line3D line, ScalarOperator sop) { // Dag
		/*
		 * Idea: structured processing of possible cases by analysing results of
		 * line/edge interesections.
		 * 
		 * We are in the plane. It is always enough to test only two of the
		 * triangle segments for strict interesection.
		 */

		SimpleGeoObj object0 = this.getSegment(0).intersection(line, sop);

		if (object0 != null) {
			// the first segment intersects - compute the result

			if (object0.getType() != SimpleGeoObj.SEGMENT3D) {

				/*
				 * Seems to be a mistake (Edgar): if it is a point return if
				 * (object0.getType() == SimpleGeoObj.POINT3D) return object0;
				 * 
				 * Intersection is via the complete triangle compute the
				 * intersection points at the segments
				 */
				SimpleGeoObj object1 = this.getSegment(1).intersection(line,
						sop);
				SimpleGeoObj object2 = this.getSegment(2).intersection(line,
						sop);

				if (object1 != null) {
					if (object1.getType() != SimpleGeoObj.SEGMENT3D) {
						if (((Point3D) object0).isEqual(((Point3D) object1),
								sop)) {
							if (object2 == null)
								return object0;
							else {
								return new Segment3D(((Point3D) object2),
										((Point3D) object0), sop);
							}
						} else
							return new Segment3D(((Point3D) object0),
									((Point3D) object1), sop);
					} else
						return object1;
				} else {
					if (object2.getType() != SimpleGeoObj.SEGMENT3D) {
						if (((Point3D) object0).isEqual(((Point3D) object2),
								sop))
							return object0;
						else
							return new Segment3D(((Point3D) object0),
									((Point3D) object2), sop);
					} else
						return object2;
				}
			} else
				return object0;
		} else {
			// the first segment does not intersect - try the second
			SimpleGeoObj object1 = this.getSegment(1).intersection(line, sop);

			if (object1 != null) {

				if (object1.getType() != SimpleGeoObj.SEGMENT3D) {

					/*
					 * Seems to be a mistake (Edgar): maybe a Point3D if
					 * (object1.getType() == SimpleGeoObj.POINT3D) return
					 * object1; intersects compute result
					 */
					SimpleGeoObj object2 = this.getSegment(2).intersection(
							line, sop);
					if (((Point3D) object1).isEqual(((Point3D) object2), sop))
						return object1;
					else
						return new Segment3D(((Point3D) object1),
								((Point3D) object2), sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object. May be <code>null</code>,
	 *         <code>Point3D</code> or <code>Segment3D</code>.
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
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) { // Dag
		SimpleGeoObj obj = this.getPlane(sop).intersection(line, sop);

		if (obj == null) // plane and line paralell and their is distance != 0
			return null;

		if (obj.getType() == SimpleGeoObj.POINT3D) {
			if (this.containsInPlane(((Point3D) obj), sop))
				return obj;
			else
				return null;
		} else { // obj is a segment
			return intersectionInPlane(line, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return resulting object. May be <code>null</code>, <code>Point3D</code>
	 *         or <code>Segment3D</code>.
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
	public SimpleGeoObj intersection(Segment3D segment, ScalarOperator sop) { // Dag

		SimpleGeoObj result = this.intersection(segment.getLine(sop), sop);

		if (result == null)
			return null;

		if (result.getType() == SimpleGeoObj.POINT3D) {
			if (segment.containsOnLine(((Point3D) result), sop))
				return result;
			else
				return null;
		} else
			// result is segment, thus intersectionInPlane
			return this.intersectionInPlane(segment, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object. May be <code>null</code>,
	 *         <code>Point3D</code>, <code>Segment3D</code>,
	 *         <code>Trangle3D</code> or <code>PointSet3D</code>.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method (which computes the intersection of two lines) of the
	 *             class Line3D returns a value that is not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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

		if (!this.getMBB().intersects(triangle.getMBB(), sop))
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
		SimpleGeoObj type = this.getPlane(sop).intersection(
				triangle.getPlane(sop), sop);
		if (type == null)
			return null;
		if (type.getType() == SimpleGeoObj.PLANE3D)
			// triangles are in the same plane
			return this.intersectionInPlane(triangle, sop);
		// Here an IllegalStateException can be thrown signaling problems with
		// the dimensions of the wireframe.

		// else intersection of planes is a Line3D
		Line3D line = (Line3D) type;

		// (2)
		// this triangle
		Segment3D[] thisSeg = this.getSegments();
		Point3D[] thisPoints = new Point3D[3];
		int thisCounter = 0;
		for (int i = 0; i < 3; i++) {
			type = line.intersection(thisSeg[i], sop);
			if (!(type == null)) {

				if (type.getType() == SimpleGeoObj.POINT3D) {
					thisPoints[thisCounter] = (Point3D) type;
					thisCounter++;
				}
				if (type.getType() == SimpleGeoObj.SEGMENT3D) {
					/*
					 * This touches the straight line. Because we do know the
					 * triangles are not in the plane, we can compute the result
					 * segment.
					 */
					return triangle
							.intersectionInPlane(((Segment3D) type), sop);
				}
			}
		}

		// (2.5)
		if (thisCounter == 0)
			return null;

		// (2)
		// argument triangle
		Segment3D[] argSeg = triangle.getSegments();
		Point3D[] argPoints = new Point3D[3];
		int argCounter = 0;
		for (int i = 0; i < 3; i++) {
			type = line.intersection(argSeg[i], sop);

			if (!(type == null)) {

				if (type.getType() == SimpleGeoObj.POINT3D) {
					argPoints[argCounter] = (Point3D) type;
					argCounter++;
				}
				if (type.getType() == SimpleGeoObj.SEGMENT3D) {
					/*
					 * This touches the straight line. Because we do know the
					 * triangles are not in the same plane, we can compute the
					 * result segment.
					 */
					return this.intersectionInPlane(((Segment3D) type), sop);
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
				if (thisPoints[i].isEqual(argPoints[j], sop))
					return thisPoints[i];
			}

		if (thisPoints[0].isEqual(thisPoints[1], sop)) {
			if (argCounter == 1 || argPoints[0].isEqual(argPoints[1], sop)) {
				if (thisPoints[0].isEqual(argPoints[0], sop))
					return thisPoints[0];
				else
					return null;
			} else {
				Segment3D s = new Segment3D(argPoints[0], argPoints[1], sop);
				if (s.contains(thisPoints[0], sop))
					return thisPoints[0];
				else
					return null;
			}
		}

		if (argPoints[0].isEqual(argPoints[1], sop)) {
			if (thisCounter == 1 || thisPoints[0].isEqual(thisPoints[1], sop)) {
				if (thisPoints[0].isEqual(argPoints[0], sop))
					return thisPoints[0];
				else
					return null;
			} else {
				Segment3D thisSectionSegment = new Segment3D(thisPoints[0],
						thisPoints[1], null);// sop);

				if (thisSectionSegment.contains(argPoints[0], sop))
					return argPoints[0];
				else
					return null;
			}

		}

		// (3) At this point we know that we have to intersect two segments
		// (one intersection segment from each triangle).
		Segment3D thisSectionSegment = new Segment3D(thisPoints[0],
				thisPoints[1], null);// sop);
		Segment3D argSectionSegment = new Segment3D(argPoints[0], argPoints[1],
				null); // sop);
		return thisSectionSegment.intersectionOnLine(argSectionSegment, sop);

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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object.
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

	protected SimpleGeoObj intersectionInPlane(Segment3D segment,
			ScalarOperator sop) { // Dag
		SimpleGeoObj result = this.intersectionInPlane(segment.getLine(sop),
				sop);

		if (result == null)
			return null;

		if (result.getType() == SimpleGeoObj.POINT3D) {
			Point3D p = (Point3D) result;
			for (int i = 0; i < 3; i++) {
				if (this.getPoint(i).isEqual(p, sop))
					if (segment.contains(p, sop))
						return this.getPoint(i);
					else
						return null;

			}
			return null;
		}

		// result must be of type SimpleGeoObj.SEGMENT3D
		Segment3D thisSectionSegment = (Segment3D) result;
		return thisSectionSegment.intersectionOnLine(segment, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object. May be of type null, Point3D,
	 *         Segment3D, Triangle3D or Wireframe3D.
	 * 
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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
	protected SimpleGeoObj intersectionInPlane(Triangle3D triangle,
			ScalarOperator sop) { // Dag
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

		Wireframe3D resultWireframe = new Wireframe3D(sop);

		// (1) intersections of this with edges of triangle
		Segment3D[] thatSegments = triangle.getSegments();
		for (int i = 0; i < 3; i++) {
			SimpleGeoObj result = this.intersection(thatSegments[i], sop);
			if (!(result == null)) {
				if (result.getType() == SimpleGeoObj.SEGMENT3D) {
					resultWireframe.add(((Segment3D) result));
				} else
					resultWireframe.add(((Point3D) result));
			}
		}
		// (2) intersections of triangle with edges of this
		Segment3D[] thisSegments = this.getSegments();
		for (int i = 0; i < 3; i++) {
			SimpleGeoObj result = triangle.intersection(thisSegments[i], sop);
			if (!(result == null)) {
				if (result.getType() == SimpleGeoObj.SEGMENT3D) {
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
					resultWireframe.getPoints()[1], sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj projection(Plane3D plane, ScalarOperator sop) { // Dag
		Point3D[] points = this.getPoints();
		Point3D[] projectedPoints = new Point3D[3];

		for (int i = 0; i < 3; i++)
			projectedPoints[i] = (Point3D) points[i].projection(plane);

		Line3D line = new Line3D(projectedPoints[0], projectedPoints[1], sop);

		if (line.contains(projectedPoints[2], sop)) {
			Segment3D seg = new Segment3D(projectedPoints[0],
					projectedPoints[1], sop);
			if (seg.contains(projectedPoints[2], sop))
				return seg;
			else {
				seg = new Segment3D(projectedPoints[0], projectedPoints[2], sop);
				if (seg.contains(projectedPoints[1], sop))
					return seg;
				else
					return new Segment3D(projectedPoints[1],
							projectedPoints[2], sop);
			}
		} else
			// projection of this is a triangle object
			return new Triangle3D(projectedPoints[0], projectedPoints[1],
					projectedPoints[2], sop);
	}

	/**
	 * Projects the given line onto this.<br>
	 * Returns <code>null</code>, Point3D or Segment3D object.
	 * 
	 * @param line
	 *            Line3D onto which this should be projected
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - resulting object.
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
	public SimpleGeoObj projectionOf(Line3D line, ScalarOperator sop) { // Dag
		Point3D p1 = line.getOrigin();
		Point3D p2 = (line.getOrigin().getVector().add(line.getDVector()))
				.getAsPoint3D();

		Plane3D plane = this.getPlane(sop);
		Point3D projectionP1 = (Point3D) p1.projection(plane);
		Point3D projectionP2 = (Point3D) p2.projection(plane);

		if (projectionP1.isEqual(projectionP2, sop))
			if (this.contains(projectionP1, sop))
				return projectionP1;
			else
				return null;
		else {
			Line3D projectedLine = new Line3D(projectionP1, projectionP2, sop);
			if (this.intersects(projectedLine, sop))
				return this.intersection(projectedLine, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of the projection.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj projectionOf(Point3D point, ScalarOperator sop) { // Dag
		Point3D projectionOnThisPlane = (Point3D) point.projection(this
				.getPlane(sop));
		if (this.contains(projectionOnThisPlane, sop))
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of the projection.
	 * 
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
	public SimpleGeoObj projectionOf(Segment3D segment, ScalarOperator sop) { // Dag
		Point3D p1 = segment.getPoint(0);
		Point3D p2 = segment.getPoint(1);

		Point3D projectionP1 = (Point3D) p1.projection(this.getPlane(sop));
		Point3D projectionP2 = (Point3D) p2.projection(this.getPlane(sop));

		if (projectionP1.isEqual(projectionP2, sop))
			if (this.contains(projectionP1, sop))
				return projectionP1;
			else
				return null;
		else {
			Segment3D projectedSegment = new Segment3D(projectionP1,
					projectionP2, sop);
			if (this.intersects(projectedSegment, sop))
				return this.intersection(projectedSegment, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of the projection.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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
	public SimpleGeoObj projectionOf(Triangle3D triangle, ScalarOperator sop) { // Dag
		SimpleGeoObj projectedTriangle = triangle.projection(
				this.getPlane(sop), sop);

		if (projectedTriangle.getType() == SimpleGeoObj.TRIANGLE3D) {
			triangle = (Triangle3D) projectedTriangle;
			if ((this.getMBB().intersects(triangle.getMBB(), sop)))
				return this.intersectionInPlane(triangle, sop);
			// Here an IllegalStateException can be thrown signaling problems
			// with the dimensions of the wireframe.
			else
				return null;
		} else
			// projectedTriangle has to be a segment
			return this.intersectionInPlane(((Segment3D) projectedTriangle),
					sop);
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
		Point3D temp = this.getPoint(2);
		setPoint(2, getPoint(1));
		setPoint(1, temp);
	}

	/**
	 * Sets the orientation of this according to given vector. Returns true if
	 * orientation was set, false if the given vector is in plane of this, thus
	 * doesn't "show" any orientaion for this.
	 * 
	 * @param vec
	 *            Vector3D to which the orientation should be set
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true is the orientation was set, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean setOrientation(Vector3D vec, ScalarOperator sop) { // Dag
		if (this.getOrientation(vec, sop) == 0)
			return false;
		if (this.getOrientation(vec, sop) == -1)
			this.invertOrientation();
		// else orientation is already correct (==1)
		return true;
	}

	/**
	 * Validates if the given points are a valid triangle.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if valid, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean isValid(ScalarOperator sop) {

		if (this.zero.isEqual(this.one, sop)
				|| this.zero.isEqual(this.two, sop)
				|| this.one.isEqual(this.two, sop))
			return false;

		return !new Line3D(this.zero, this.one, sop).contains(this.two, sop);
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if the triangle is regular, false otherwise.
	 */
	public boolean isRegular(ScalarOperator sop) { // Dag
		// (1) all segments are regular
		// (2) vectorproduct of two segments of this / 2 (=aerea) > threshold
		Segment3D[] segments = this.getSegments();
		// (1)
		for (int i = 0; i < 3; i++)
			if (!segments[i].isRegular(sop))
				return false;
		// (2)
		// calculate area of triangle and compare with multible of epsilon
		double area = this.getArea();
		if (area > (sop.getEpsilon() * SimpleGeoObj.MIN_AREA_EPSILON_FACTOR))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether this is a "beautiful" triangle. Beautiful accounts the
	 * proportion of a triangle. Very long and narrow triangles with a small
	 * normalized area are going to be evaluated as not "beautiful".
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if the triangle is "beautiful", false otherwise.
	 */
	public boolean isBeautiful(ScalarOperator sop) { // Dag
		// considers the proportion
		// check for longer edges of a triangle (B-A) and (C-A) :
		// | ( (B-A) x (C-A) / |B-A| * |C-A| ) | > threshold

		Vector3D[] longerVectors = this.getLongerVectors();

		double numerator = (longerVectors[0].crossproduct(longerVectors[1]))
				.getNorm();
		double denumerator = longerVectors[0].getNorm()
				* longerVectors[1].getNorm();

		if (numerator > (denumerator * (SimpleGeoObj.MIN_AREA_EPSILON_FACTOR * sop
				.getEpsilon())))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether this is a complete validated triangle.<br>
	 * This method performs a isValid, isRegular and isBeautiful test.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if the triangle is completely validated, false
	 *         otherwise.
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
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isEqual(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Triangle3D))
			return false;
		Point3D[] geom = ((Triangle3D) obj).getPoints();
		Point3D[] points = this.getPoints();
		for (int i = 0; i < 3; i++)
			if (!(points[i].isEqual(geom[i], sop)))
				return false;

		return true;
	}

	/**
	 * Computes the corresponding hash code for isGeometryEquivalent usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isGeometryEquivalent(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Triangle3D))
			return false;
		Point3D[] ps1 = GeomUtils.getSorted(this.getPoints());
		Point3D[] ps2 = GeomUtils.getSorted(((Triangle3D) obj).getPoints());
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
	 * Method for identifying the type of object.
	 * 
	 * @return byte - constant for this type.
	 */
	public byte getType() {
		return SimpleGeoObj.TRIANGLE3D;
	}

	// private methods

	/*
	 * Builds the LineSegments of this and saves them in the transient variable
	 * <code>lines</codes> for further processing.
	 */
	private void buildLines() {
		Point3D[] points = this.getPoints();
		this.lines = new Segment3D[3];
		this.lines[0] = new Segment3D(points[1], points[2], null);
		this.lines[1] = new Segment3D(points[2], points[0], null);
		this.lines[2] = new Segment3D(points[0], points[1], null);
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Triangle3D [lines=" + Arrays.toString(lines) + ", normvec="
				+ normvec + ", one=" + one + ", two=" + two + ", zero=" + zero
				+ ", attributes= " + attributesToString() + "]";
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
		result = prime * result + ((two == null) ? 0 : two.hashCode());
		result = prime * result + ((zero == null) ? 0 : zero.hashCode());
		return result;
	}

} // end Triangle3D

