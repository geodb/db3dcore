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

	// the net of this spatial part
	private Net4D net;

	// ScalarOperator
	private ScalarOperator sop;

	/**
	 * Constructor.
	 */
	public SpatialObject4D() {
		this.object = null;
		sop = new ScalarOperator(0.0000000001);
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

	public Net4D getNet() {
		return net;
	}
	
	public void setNet(Net4D net) {
		this.net = net;
	}
}
