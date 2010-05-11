/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Vector3D;

/**
 * SegmentElt3D represents an element of a SegmentNet3D object.<br>
 * A SegmentElt3D inherits the geometric attributes and method of Segment3D.<br>
 * Topological information is stored as the neighbour SegmentElt3D objects in
 * the net. Neighbour with index 0 is the neighbour opposite to Point 0,
 * neighbour with index 1 the other.
 */
public class SegmentElt3D extends Segment3D implements NetElement3D {

	/* neighbour faced index 0 */
	private SegmentElt3D eltZero;

	/* neighbour faced index 1 */
	private SegmentElt3D eltOne;

	/* id of this - unique in whole net */
	private int id;

	/* enclosing net component */
	private SegmentNet3DComp comp;

	/**
	 * Constructor.<br>
	 * Constructs a SegmentElt3D as a Segment3D with given start and end point.
	 * StartPoint has index 0, EndPoint index 1
	 * 
	 * @param start
	 *            start point as Point3D
	 * @param end
	 *            end point as Point3D
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 */
	public SegmentElt3D(Point3D start, Point3D end, ScalarOperator sop) {
		super(start, end, sop);
		this.eltZero = null;
		this.eltOne = null;
	}

	/**
	 * Constructor.<br>
	 * Constructs a SegmentElt3D as a Segment3D object with the given vectors
	 * interpreted as position vectors of points in space.
	 * 
	 * @param start
	 *            start point as position vector
	 * @param end
	 *            end point as position vector
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 */
	public SegmentElt3D(Vector3D start, Vector3D end, ScalarOperator sop) {
		this(start.getAsPoint3D(), end.getAsPoint3D(), sop);
	}

	/**
	 * Constructor.
	 * 
	 * @param segment
	 *            Segment3D
	 */
	public SegmentElt3D(Segment3D segment) {
		super(segment);
		this.eltZero = null;
		this.eltOne = null;
	}

	/**
	 * Returns the identifier of this.
	 * 
	 * @return int - id
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Tests if this has a neighbour element faced to given index.
	 * 
	 * @param index
	 *            int index
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasNeighbour(int index) {
		if (index == 0)
			return this.eltZero != null;
		else
			return this.eltOne != null;
	}

	/**
	 * Tests if this has neighbour elements at all.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasNeighbours() {
		if (this.eltZero != null || this.eltOne != null)
			return true;
		else
			return false;
	}

	/**
	 * Tests if this is an interior element - it has two neighbours.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean isInterior() {
		if (this.eltZero != null && this.eltOne != null)
			return true;
		else
			return false;
	}

	/**
	 * Returns the neighbour faced to given index.
	 * 
	 * @param index
	 *            has to be 0 or 1.
	 * @return the neighbouring segment or <code>null</code> if no neighbour
	 *         available or parameter outside index range.
	 */
	public SegmentElt3D getNeighbour(int index) {
		if (index == 0)
			return this.eltZero;
		else if (index == 1)
			return this.eltOne;
		else
			return null;
	}

	/**
	 * Returns the neighbours as array.
	 * 
	 * @return SegmentElt3D[] - ordered as explained.
	 */
	public SegmentElt3D[] getNeighbours() {
		return new SegmentElt3D[] { this.eltZero, this.eltOne };
	}

	/**
	 * Returns the enclosing net component.
	 * 
	 * @return SegmentNet3DComp - endclosing net component.
	 */
	public SegmentNet3DComp getNetComponent() {
		return comp;
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return SegmentNet3D - enclosing net.
	 */
	public SegmentNet3D getNet() {
		if (comp != null)
			return comp.getNet();
		else
			return null;
	}

	/**
	 * Sets the id to given value.
	 * 
	 * @param i
	 *            int value to which the id shold be set.
	 */
	public void setID(int i) {
		this.id = i;
	}

	/**
	 * Sets the neighbour which is faced to the given index. Index 0 - neighbour
	 * opposite to P0 Index 1 - neighbour opposite to P1.
	 * 
	 * @param index
	 *            int index
	 * @param element
	 *            SegmentElt3D, neighbour that should be updated.
	 */
	protected void setNeighbour(int index, SegmentElt3D element) {
		if (index == 0)
			this.eltZero = element;
		else
			this.eltOne = element;
	}

	/**
	 * Sets the reference to the enclosing net component.
	 * 
	 * @param net3D
	 *            SegmentNet3DComp
	 */
	protected void setNetComponent(SegmentNet3DComp net3D) {
		comp = net3D;
	}

	/**
	 * Returns the type of this as a <code>SimpleGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.SEGMENT_ELT_3D;
	}

	/**
	 * Overrides invertOrientation in Segment3D - in addition to the orientation
	 * inversion it inverts the neighbours.
	 * 
	 * @see db3d.dbms.geom.Segment3D#invertOrientation()
	 */
	public void invertOrientation() {
		Point3D temp = getPoint(0);
		setPoint(0, getPoint(1));
		setPoint(1, temp);

		SegmentElt3D temp2 = this.getNeighbour(0);
		this.setNeighbour(0, this.getNeighbour(1));
		this.setNeighbour(1, temp2);
	}
}
