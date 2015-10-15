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
	private TetrahedronNet4D tetrahedronNet;
	private TriangleNet4D triangleNet;
	private SegmentNet4D segmentNet;

	// TODO: do we need components for points?
	private PointNet4D pointCloud;

	// ScalarOperator
	private ScalarOperator sop;

	/**
	 * Constructor.
	 */
	public SpatialObject4D() {
		this.object = null;
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

	public TetrahedronNet4D getTetrahedronNet() {
		return tetrahedronNet;
	}

	public void setTetrahedronNet(TetrahedronNet4D tetrahedronNet) {
		this.tetrahedronNet = tetrahedronNet;
	}

	public TriangleNet4D getTriangleNet() {
		return triangleNet;
	}

	public void setTriangleNet(TriangleNet4D triangleNet) {
		this.triangleNet = triangleNet;
	}

	public SegmentNet4D getSegmentNet() {
		return segmentNet;
	}

	public void setSegmentNet(SegmentNet4D segmentNet) {
		this.segmentNet = segmentNet;
	}

	public PointNet4D getPointNet() {
		return pointCloud;
	}

	public void setPointCloud(PointNet4D pointCloud) {
		this.pointCloud = pointCloud;
	}
}
