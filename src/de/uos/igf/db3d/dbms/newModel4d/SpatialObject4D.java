/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.newModel4d;

import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * SpatialObject4D is the abstract superclass of all geometric objects in 4D.<br>
 * It represents the geometric part of an Object4D.
 */
public abstract class SpatialObject4D {

	/* back reference to Object4D */
	private Object4D object;

		/* ScalarOperator */
	private ScalarOperator sop;

	/**
	 * Constructor.
	 */
	protected SpatialObject4D() {
		this.object = null;
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
	 * Returns the Object3D aggregation object.
	 * 
	 * @return Object3D.
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

	/**
	 * Sets the ScalarOperator of this.
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	protected void setScalarOperator(ScalarOperator sop) {
		this.sop = sop;
	}
}
