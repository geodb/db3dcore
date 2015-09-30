/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Vector3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * SegmentElt3D represents an element of a SegmentNet3D object.<br>
 * A SegmentElt3D inherits the geometric attributes and method of Segment3D.<br>
 * Topological information is stored as the neighbour SegmentElt3D objects in
 * the net. Neighbour with index 0 is the neighbour opposite to Point 0,
 * neighbour with index 1 the other.
 * 
 * @author Markus Jahn
 * 
 */
public class Segment3DElement extends Segment3D implements Element3D {

	/* neighbour faced index 0 */
	protected Segment3DElement[] neighbours;

	/* id of this - unique in whole net */
	protected int id;

	/* enclosing net component */
	protected Segment3DComponent component;

	/**
	 * Constructor.<br>
	 * Constructs a SegmentElt3D as a Segment3D with given start and end point.
	 * StartPoint has index 0, EndPoint index 1
	 * 
	 * @param start
	 *            start point as Point3D
	 * @param end
	 *            end point as Point3D
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 */
	public Segment3DElement(Point3D start, Point3D end, GeoEpsilon epsilon) {
		super(start, end, epsilon);
		this.neighbours = new Segment3DElement[2];
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
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 */
	public Segment3DElement(Vector3D start, Vector3D end, GeoEpsilon epsilon) {
		this(start.getAsPoint3D(), end.getAsPoint3D(), epsilon);
	}

	/**
	 * Constructor.
	 * 
	 * @param segment
	 *            Segment3D
	 */
	public Segment3DElement(Segment3D segment) {
		super(segment);
		this.neighbours = new Segment3DElement[2];
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public boolean hasNeighbour(int id) {
		return this.neighbours[id] != null;
	}

	@Override
	public boolean hasNeighbours() {
		if (this.neighbours[0] != null || this.neighbours[1] != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isInterior() {
		if (this.neighbours[0] != null || this.neighbours[1] != null)
			return true;
		else
			return false;
	}

	@Override
	public Segment3DElement[] getNeighbours() {
		return this.neighbours;
	}

	@Override
	public Segment3DElement getNeighbour(int index) {
		return this.neighbours[index];
	}

	@Override
	public Segment3DComponent getComponent() {
		return this.component;
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.CURVE_ELEMENT_3D;
	}

	/**
	 * Overrides invertOrientation in Segment3D - in addition to the orientation
	 * inversion it inverts the neighbours.
	 * 
	 * @see db3d.dbms.geom.Segment3D#invertOrientation()
	 */
	public void invertOrientation() {
		this.invertOrientation();

		Segment3DElement temp2 = this.neighbours[0];
		this.neighbours[0] = this.neighbours[1];
		this.neighbours[1] = temp2;
	}

	@Override
	public GeoEpsilon getGeoEpsilon() {
		if (this.component != null) {
			return this.component.getGeoEpsilon();
		}
		return null;
	}

}
