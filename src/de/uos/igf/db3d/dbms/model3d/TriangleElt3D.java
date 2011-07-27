/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.EmptyStackException;
import java.util.Stack;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.util.FlagMap;

/**
 * TriangleElt3D represents an element of a TriangleNet3D object.<br>
 * A TriangleElt3D inherits the geometric attributes and methods of Triangle3D.<br>
 * Topological information is stored as the tree neighbour triangles in the net.<br>
 * Persistent through inheritance !<br>
 */
@SuppressWarnings("serial")
public class TriangleElt3D extends Triangle3D implements NetElement3D {

	/* neighbour 0 */
	private TriangleElt3D eltZero;

	/* neighbour 1 */
	private TriangleElt3D eltOne;

	/* neighbour 2 */
	private TriangleElt3D eltTwo;

	/* id of this - unique in whole net */
	private int id;

	/* enclosing net component */
	private TriangleNet3DComp comp;

	/**
	 * Constructor. <br>
	 * Constructs a TriangleElt3D as a Triangle3D with given points.
	 * 
	 * @param points
	 *            Point3D array.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt3D(Point3D[] points, ScalarOperator sop)
			throws IllegalArgumentException {
		super(points, sop);
		this.eltZero = null;
		this.eltOne = null;
		this.eltTwo = null;
	}

	/**
	 * Constructor. <br>
	 * Constructs a TriangleElt3D as a Triangle3D with given points.
	 * 
	 * @param point1
	 *            Point3D.
	 * @param point2
	 *            Point3D.
	 * @param point3
	 *            Point3D.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt3D(Point3D point1, Point3D point2, Point3D point3,
			ScalarOperator sop) {
		this(new Point3D[] { point1, point2, point3 }, sop);
	}

	/**
	 * Constructor.<br>
	 * The given point must not intersect with the segment.
	 * 
	 * @param point
	 *            Point3D.
	 * @param seg
	 *            Segment3D
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt3D(Point3D point, Segment3D seg, ScalarOperator sop) {
		this(new Point3D[] { point, seg.getPoint(0), seg.getPoint(1) }, sop);
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param triangle
	 *            Triangle3D
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
	public TriangleElt3D(Triangle3D triangle) {
		super(triangle);
		this.eltZero = null;
		this.eltOne = null;
		this.eltTwo = null;
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
	 *            int
	 * @return boolean true if has a neighbour element at the given index, false
	 *         otherwise.
	 */
	public boolean hasNeighbour(int index) {
		switch (index) {
		case 0:
			return this.eltZero != null;
		case 1:
			return this.eltOne != null;
		case 2:
			return this.eltTwo != null;
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
		return count;
	}

	/**
	 * Tests if this has neighbour elements at all.
	 * 
	 * @return boolean - true if has neighbours, false otherwise.
	 */
	public boolean hasNeighbours() {
		if (this.eltZero != null || this.eltOne != null || this.eltTwo != null)
			return true;
		else
			return false;
	}

	/**
	 * Tests if this is an interior element - it has two neighbours.
	 * 
	 * @return boolean - true if interior element, false otherwise.
	 */
	public boolean isInterior() {
		if (this.eltZero != null && this.eltOne != null && this.eltTwo != null)
			return true;
		else
			return false;
	}

	/**
	 * Sets the neighbour for the given index. Index 0 - neighbour opposite to
	 * P0, Index 1 - neighbour opposite to P1, Index 2 - neighbour opposite to
	 * P2.
	 * 
	 * @param index
	 *            int
	 * @param element
	 *            TriangleElt3D
	 */
	public boolean setNeighbour(int index, TriangleElt3D element) {
		switch (index) {
		case 0:
			this.eltZero = element;
			return true;
		case 1:
			this.eltOne = element;
			return true;
		case 2:
			this.eltTwo = element;
			return true;
		}
		return false;
	}

	/**
	 * Sets the neighbour for corresponding index to <code>null</null>.  
	 * Returns index for removed neighbour information or -1 if given element was not neighbour.
	 * 
	 * @param element
	 *            TriangleElt3D
	 * @param sop
	 *            ScalarOperator needed for validation
	 * @return int - index for removed neighbour.
	 */
	public int setNeighbourNull(TriangleElt3D element, ScalarOperator sop) {

		if (this.getNeighbour(0) != null
				&& this.getNeighbour(0).isGeometryEquivalent(element, sop)) {
			this.setNeighbour(0, null);
			return 0;
		}
		if (this.getNeighbour(1) != null
				&& this.getNeighbour(1).isGeometryEquivalent(element, sop)) {
			this.setNeighbour(1, null);
			return 1;
		}
		if (this.getNeighbour(2) != null
				&& this.getNeighbour(2).isGeometryEquivalent(element, sop)) {
			this.setNeighbour(2, null);
			return 2;
		}

		return -1;
	}

	/**
	 * Returns the neighbour for the given index.
	 * 
	 * @param index
	 *            int
	 * @return the neighbouring triangle or <code>null</code> if
	 *         <code>index</code> out of range.
	 */
	public TriangleElt3D getNeighbour(int index) {
		switch (index) {
		case 0:
			return this.eltZero;
		case 1:
			return this.eltOne;
		case 2:
			return this.eltTwo;
		default:
			return null;
		}
	}

	/**
	 * Returns the neighbours as array (of size of neighbours count).
	 * 
	 * @return TriangleElt3D[] - orderd as explained.
	 */
	public TriangleElt3D[] getNeighbours() {
		int counter = 0;
		if (this.eltZero != null)
			counter++;
		if (this.eltOne != null)
			counter++;
		if (this.eltTwo != null)
			counter++;

		TriangleElt3D[] neighbours = new TriangleElt3D[counter];

		counter = 0;
		if (this.eltZero != null) {
			neighbours[counter] = this.eltZero;
			counter++;
		}
		if (this.eltOne != null) {
			neighbours[counter] = this.eltOne;
			counter++;
		}
		if (this.eltTwo != null) {
			neighbours[counter] = this.eltTwo;
		}

		return neighbours;
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return TriangleNet3D - enclosing net.
	 */
	TriangleNet3D getNet() {
		return comp.getNet();
	}

	/**
	 * Returns the enclosing net component
	 * 
	 * @return TriangleNet3DComp - enclosing net component.
	 */
	public TriangleNet3DComp getNetComponent() {
		return comp;
	}

	/**
	 * Sets the reference to the enclosing net component.
	 * 
	 * @param net3D
	 *            TriangleNet3DComp enclosing net component
	 */
	public void setNetComponent(TriangleNet3DComp net3D) {
		comp = net3D;
	}

	/**
	 * Sets the id to given value
	 * 
	 * @param i
	 *            int to which the id should be set
	 */
	public void setID(int i) {
		this.id = i;
	}

	/**
	 * Returns the type of this as a <code>SimpleGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.TRIANGLE_ELT_3D;
	}

	/**
	 * Checks whether neighbours of this have already been visited, makes them
	 * orientation consistent if not.
	 * 
	 * @param sop
	 *            ScalarOperator needed for validation
	 * @param flags
	 *            FlagMap to store the visited neighbours
	 * @author Dag<br>
	 *         Revision: Edgar Butwilowski
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void makeNeighboursOrientationConsistent(ScalarOperator sop,
			FlagMap flags) {
		Stack<Object[]> upcoming = new Stack<Object[]>();
		TriangleElt3D current = this;
		do {
			// set visited status
			flags.add(current);

			for (int i = 0; i < 3; i++) {
				TriangleElt3D nb = current.getNeighbour(i);
				if (nb != null && (!flags.check(nb))) {
					upcoming.push(new Object[] { i, nb, current });
				}
			}

			Object[] nbObj = null;
			try {
				nbObj = upcoming.pop();
			} catch (EmptyStackException ese) {
				// do nothing
			}
			int index = -1;
			TriangleElt3D nb = null;
			TriangleElt3D current2 = null;
			if (nbObj != null) {
				index = (Integer) nbObj[0];
				nb = (TriangleElt3D) nbObj[1];
				current2 = (TriangleElt3D) nbObj[2];
			}

			if ((nb != null) && (!flags.check(nb))) {
				// if not already visited

				// point indices of common edge (common edge has (in this)
				// direction p1->p2)
				int p1 = (index + 1) % 3;
				int p2 = (index + 2) % 3;

				int j = 0;

				// find nb's index j for opposite point of common edge
				for (j = 0; j < 3; j++)
					if (!(current2.getPoint(p1).isEqual(nb.getPoint(j), sop) || current2
							.getPoint(p2).isEqual(nb.getPoint(j), sop)))
						break;

				// nb's index for first point of common edge
				j = (j + 1) % 3;

				if (current2.getPoint(p1).isEqual(nb.getPoint(j), sop))
					nb.invertOrientation();

				// orientNeighbours for nb
			}
			current = nb;

		} while (upcoming.size() != 0 || current != null);

	}

	/**
	 * Inverts the orientation of the vertices. Overrides invertOrientation
	 * method of Triangle3D - in addition to the orientation inversion it
	 * inverts the neighbours.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * 
	 * @see db3d.dbms.geom.Triangle3D#invertOrientation()
	 */
	public void invertOrientation() {
		Point3D temp = getPoint(2);
		setPoint(2, getPoint(1));
		setPoint(1, temp);

		TriangleElt3D temp2 = getNeighbour(2);
		setNeighbour(2, getNeighbour(1));
		setNeighbour(1, temp2);
		if (this.getNetComponent() != null)
			this.getNetComponent().setOriented(false);
	}

}
