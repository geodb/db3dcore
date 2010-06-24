/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;

/**
 * TetrahedronElt3D represents an element of a TetrahedronNet3D object.<br>
 * A TetrahedronElt3D inherits the geometric attributes and methods of
 * Tetrahedron3D.<br>
 * Topological information is stored as the four neighbour tetrahedrons in the
 * net. <br>
 * Persistent through inheritance !
 */
@SuppressWarnings("serial")
public class TetrahedronElt3D extends Tetrahedron3D implements NetElement3D {

	/* neighbour 0 */
	private TetrahedronElt3D eltZero;

	/* neighbour 1 */
	private TetrahedronElt3D eltOne;

	/* neighbour 2 */
	private TetrahedronElt3D eltTwo;

	/* neighbour 3 */
	private TetrahedronElt3D eltThree;

	/* id of this - unique in whole net */
	private int id;

	/* enclosing net component */
	private TetrahedronNet3DComp comp;

	/**
	 * Constructor. <br>
	 * Constructs a TetrahedronElt3D as a Tetrahedron3D with given points.
	 * 
	 * @param points
	 *            Point3D array.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TetrahedronElt3D(Point3D[] points, ScalarOperator sop)
			throws IllegalArgumentException {
		super(points, sop);
		this.eltZero = null;
		this.eltOne = null;
		this.eltTwo = null;
		this.eltThree = null;
	}

	/**
	 * Constructor. <br>
	 * Constructs a TetrahedronElt3D as a Tetrahedron3D with given points.
	 * 
	 * @param point1
	 *            Point3D.
	 * @param point2
	 *            Point3D.
	 * @param point3
	 *            Point3D.
	 * @param point4
	 *            Point3D.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TetrahedronElt3D(Point3D point1, Point3D point2, Point3D point3,
			Point3D point4, ScalarOperator sop) {
		this(new Point3D[] { point1, point2, point3, point4 }, sop);
	}

	/**
	 * Constructor. <br>
	 * 
	 * @param point
	 *            Point3D.
	 * @param triangle
	 *            Triangle3D.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             ScalarOperator).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TetrahedronElt3D(Point3D point, Triangle3D triangle,
			ScalarOperator sop) {
		this(new Point3D[] { point, triangle.getPoint(0), triangle.getPoint(1),
				triangle.getPoint(2) }, sop);
	}

	/**
	 * Constructor. <br>
	 * 
	 * @param seg1
	 *            Segment3D.
	 * @param seg2
	 *            Segment3D.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TetrahedronElt3D(Segment3D seg1, Segment3D seg2, ScalarOperator sop) {
		this(new Point3D[] { seg1.getPoint(0), seg1.getPoint(1),
				seg2.getPoint(0), seg2.getPoint(1) }, sop);
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param tetra
	 *            Tetrahedron3D
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
	public TetrahedronElt3D(Tetrahedron3D tetra) {
		super(tetra);
		this.eltZero = null;
		this.eltOne = null;
		this.eltTwo = null;
		this.eltThree = null;
	}

	/**
	 * Returns the identifier of this.
	 * 
	 * @return int - id.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Tests if this has a neighbour element at given index
	 * 
	 * @param index
	 *            int index
	 * @return boolean - true if has neighbour at given index, false otherwise
	 */
	public boolean hasNeighbour(int index) {
		switch (index) {
		case 0:
			return this.eltZero != null;
		case 1:
			return this.eltOne != null;
		case 2:
			return this.eltTwo != null;
		case 3:
			return this.eltThree != null;
		default:
			return false;
		}
	}

	/**
	 * Returns the count of neighbour elements
	 * 
	 * @return int - count of neighbour elements.
	 */
	public int countNeighbours() {
		int count = 0;
		if (this.hasNeighbour(0))
			count++;
		if (this.hasNeighbour(1))
			count++;
		if (this.hasNeighbour(2))
			count++;
		if (this.hasNeighbour(3))
			count++;
		return count;
	}

	/**
	 * Tests if this has neighbour elements at all.
	 * 
	 * @return boolean - true if has neighbour elements, false otherwise
	 */
	public boolean hasNeighbours() {
		if (this.eltZero != null || this.eltOne != null || this.eltTwo != null
				|| this.eltThree != null)
			return true;
		else
			return false;
	}

	/**
	 * Tests if this is an interior element - must have four neighbours
	 * 
	 * @return boolean - true if interiour, false otherwise.
	 */
	public boolean isInterior() {
		if (this.eltZero != null && this.eltOne != null && this.eltTwo != null
				&& this.eltThree != null)
			return true;
		else
			return false;
	}

	/**
	 * Returns the neighbour for the given index.
	 * 
	 * @param index
	 *            the index of the neighbour to return
	 * @return the neighbouring tetrahedron of given index or <code>null</code>
	 *         if out of range.
	 */
	public TetrahedronElt3D getNeighbour(int index) {
		switch (index) {
		case 0:
			return this.eltZero;
		case 1:
			return this.eltOne;
		case 2:
			return this.eltTwo;
		case 3:
			return this.eltThree;
		default:
			return null;
		}
	}

	/**
	 * Returns the neighbours as array
	 * 
	 * @return TetrahedronElt3D[] - orderd as explained.
	 */
	public TetrahedronElt3D[] getNeighbours() {
		return new TetrahedronElt3D[] { this.eltZero, this.eltOne, this.eltTwo,
				this.eltThree };
	}

	/**
	 * Returns whether this is a border element or not.
	 * 
	 * @return boolean - true if is a border element, false otherwise.
	 */
	public boolean isBorderElement() {

		for (int i = 0; i < 4; i++)
			if (!this.hasNeighbour(i))
				return true;

		return false;
	}

	/**
	 * Returns the enclosing net component.
	 * 
	 * @return TetrahedronNet3DComp - enclosing net component.
	 */
	public TetrahedronNet3DComp getNetComponent() {
		return comp;
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return TetrahedronNet3D - enclosing net.
	 */
	public TetrahedronNet3D getNet() {
		return comp.getNet();
	}

	/**
	 * Sets the neighbour for the given index. Index 0 - neighbour opposite to
	 * P0, Index 1 - neighbour opposite to P1, Index 2 - neighbour opposite to
	 * P2, Index 3 - neighbour opposite to P3.
	 * 
	 * @param index
	 *            int index
	 * @param element
	 *            TetrahedronElt3D that should be set to the given index
	 * 
	 * @throws DB3DException
	 *             if the index is not 0, 1, 2 or 3.
	 */
	public void setNeighbour(int index, TetrahedronElt3D element)
			throws DB3DException {
		switch (index) {
		case 0:
			this.eltZero = element;
			break;
		case 1:
			this.eltOne = element;
			break;
		case 2:
			this.eltTwo = element;
			break;
		case 3:
			this.eltThree = element;
			break;
		default:
			throw new DB3DException(Db3dSimpleResourceBundle.getString("db3d.geom.defr"));
		}
	}

	/**
	 * Sets the neighbour for corresponding index to <code>null</null>.  
	 * Returns index for removed neighbour information or -1 if given element was not a neighbour.
	 * 
	 * @param element
	 *            TetrahedronElt3D
	 * @param sop
	 *            ScalarOperator
	 * @return int - index for removed neighbour.
	 */
	public int setNeighbourNull(TetrahedronElt3D element, ScalarOperator sop) {
		
		TetrahedronElt3D neighbour = this.getNeighbour(0);
		if (neighbour != null && neighbour.isGeometryEquivalent(element, sop)) {
			this.eltZero = null;
			return 0;
		}
		neighbour = this.getNeighbour(1);
		if (neighbour != null && neighbour.isGeometryEquivalent(element, sop)) {
			this.eltOne = null;
			return 1;
		}
		neighbour = this.getNeighbour(2);
		if (neighbour != null && neighbour.isGeometryEquivalent(element, sop)) {
			this.eltTwo = null;
			return 2;
		}
		neighbour = this.getNeighbour(3);
		if (neighbour != null && neighbour.isGeometryEquivalent(element, sop)) {
			this.eltThree = null;
			return 3;
		}
		return -1;
	}

	/**
	 * Sets the id to given value
	 * 
	 * @param i
	 *            int value to which the id should be set
	 */
	public void setID(int i) {
		this.id = i;
	}

	/**
	 * Sets the reference to the enclosing net component.
	 * 
	 * @param net3D
	 *            TetrahedronNet3DComp
	 */
	public void setNetComponent(TetrahedronNet3DComp net3D) {
		comp = net3D;
	}

	/**
	 * Returns the type of this as a <code>SimpleGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.TETRAHEDRON_ELT_3D;
	}

}
