package de.uos.igf.db3d.dbms.newModel4d;

import java.util.HashMap;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.ScalarOperator;

/**
 * SpatialObject4D is the abstract superclass of all geometric objects in 4D. It
 * represents the geometric part of an Object4D for one Interval ([Post,Pre])
 */
public class SpatialObject4D {

	// back reference to Object4D
	private Object4D object;

	// Geometry information for all kind of simplices and their components
	// (componentID = key)
	// TODO Netze zu Components!
	// TODO TreeMap nehmen!
	private Map<Integer, TetrahedronNet4D> tetrahedronNets;
	private Map<Integer, TriangleNet4D> triangleNets;
	private Map<Integer, SegmentNet4D> segmentNets;

	// TODO: do we need components for points?
	private Map<Integer, Point4D> points;

	// ScalarOperator
	private ScalarOperator sop;

	/**
	 * Constructor.
	 */
	public SpatialObject4D() {
		this.object = null;
		tetrahedronNets = new HashMap<Integer, TetrahedronNet4D>();
		triangleNets = new HashMap<Integer, TriangleNet4D>();
		segmentNets = new HashMap<Integer, SegmentNet4D>();
		points = new HashMap<Integer, Point4D>();
		sop = new ScalarOperator();
	}

	/**
	 * Returns the ScalarOperator of this.
	 * 
	 * @return sop - the ScalarOperator of this.
	 * @see db3d.dbms.model3d.Spatial3D#getScalarOperator()
	 */
	public ScalarOperator getScalarOperator() {
		return sop;
	}

	/**
	 * Returns the Object4D aggregation object.
	 * 
	 * @return Object4D.
	 */
	public Object4D getObject4D() {
		return this.object;
	}

	/**
	 * Sets the Object4D of this.
	 * 
	 * @param object
	 *            - Object4D.
	 */
	protected void setObject4D(Object4D object) {
		this.object = object;
	}

	// Getter and Setter methods

	public void setScalarOperator(ScalarOperator sop) {
		this.sop = sop;
	}

	public Map<Integer, TetrahedronNet4D> getTetrahedronNets() {
		return tetrahedronNets;
	}

	public void setTetrahedronNets(
			Map<Integer, TetrahedronNet4D> tetrahedronNets) {
		this.tetrahedronNets = tetrahedronNets;
	}

	public Map<Integer, TriangleNet4D> getTriangleNets() {
		return triangleNets;
	}

	public void setTriangleNets(Map<Integer, TriangleNet4D> triangleNets) {
		this.triangleNets = triangleNets;
	}

	public Map<Integer, SegmentNet4D> getSegmentNets() {
		return segmentNets;
	}

	public void setSegmentNets(Map<Integer, SegmentNet4D> segmentNets) {
		this.segmentNets = segmentNets;
	}

	public Map<Integer, Point4D> getPoints() {
		return points;
	}

	public void setPoints(Map<Integer, Point4D> points) {
		this.points = points;
	}	
}
