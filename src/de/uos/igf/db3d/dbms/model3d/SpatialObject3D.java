/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.Set;

import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * SpatialObject3D is the abstract superclass of all geometric objects in 3D.<br>
 * It represents the geometric part of an Object3D.
 */
public abstract class SpatialObject3D implements Spatial3D, PersistentObject {

	/* back reference to Object3D */
	private Object3D object;

	/* MBB3D of this */
	private MBB3D mbb;

	/* ScalarOperator */
	private ScalarOperator sop;

	/* flag if update optimization is on */
	private boolean update;

	/* SAM of the enclosing space the object is registered in */
	private SAM sam;

	/* current element id state */
	private int elementID;

	/* current component id state */
	private int componentID;

	/**
	 * Constructor.
	 */
	protected SpatialObject3D() {
		this.object = null;
		this.mbb = null;
		this.update = false;
		this.elementID = 0;
		this.componentID = 0;
	}

	/**
	 * Returns the MBB of this.
	 * 
	 * @return mbb - MBB of this.
	 * @see db3d.dbms.model3d.Spatial3D#getMBB()
	 */
	public MBB3D getMBB() {
		return mbb;
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
	public Object3D getObject3D() {
		return this.object;
	}

	/**
	 * Marks the begin of an update period.<br>
	 * During an update period insertion and removal of new elements to the
	 * components of the net are made without updating the mbbs and indexes
	 * every time. With the call to endUpdate the update routines will take
	 * place.
	 */
	public void beginUpdate() {
		setUpdate(true);
	}

	/**
	 * Marks the end of an update.<br>
	 * Resets the update flag and begins updating the net.
	 */
	public abstract void endUpdate();

	/**
	 * Tests whether this net object is in an update phase.
	 * 
	 * @return boolean - true if, else otherwise
	 */
	public boolean isUpdate() {
		return update;
	}

	/**
	 * Sets the Object3D of this.
	 * 
	 * @param object
	 *            - Object3D.
	 */
	protected void setObject3D(Object3D object) {
		this.object = object;
	}

	/**
	 * Tests it the given two elements are equal based on their ID.<br>
	 * Only correct if the elements are from the same net.
	 * 
	 * @param elt1
	 *            NetElement3D
	 * @param elt2
	 *            NetElement3D
	 * @return boolean - true if equal, else otherwise
	 */
	public boolean isEqualID(NetElement3D elt1, NetElement3D elt2) {
		if (elt1.getClass() != elt2.getClass())
			return false;
		if (elt1.getID() == elt2.getID())
			return true;
		return false;
	}

	/**
	 * Sets the mbb of this SpatialObject3D.
	 * 
	 * @param mbb
	 *            MBB3D
	 */
	protected void setMBB(MBB3D mbb) {
		this.mbb = mbb;
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

	/**
	 * Returns the SAM in which this is indexed.
	 * 
	 * @return SAM.
	 */
	protected SAM getSAM() {
		return this.sam;
	}

	/**
	 * Sets the SAM in which this is indexed to given SAM.
	 * 
	 * @param sam
	 *            SAM
	 */
	protected void setSAM(SAM sam) {
		this.sam = sam;
	}

	/**
	 * Updates the MBB of this net.<br>
	 * Iterates over all components updating and union their mbbs. Sets the MBB
	 * in the abstract SpatialObject.
	 */
	protected abstract void updateMBB();

	/**
	 * Sets the update flag to given boolean value.
	 * 
	 * @param update
	 *            boolean
	 */
	protected void setUpdate(boolean update) {
		this.update = update;
	}

	/**
	 * Returns the next element ID.
	 * 
	 * @return int - id of the next element.
	 */
	protected int nextElementID() {
		return ++elementID;
	}

	/**
	 * Returns the next component ID.
	 * 
	 * @return int - id of the next component.
	 */
	public int nextComponentID() {
		return ++componentID;
	}

	/**
	 * Sets the elementID to the given counter. Use only if element ids from
	 * outside are given and you want to justify the current counter.
	 * 
	 * @param counter
	 *            int
	 */
	public void setElementID(int counter) {
		this.elementID = counter;
	}

	/**
	 * Sets the componentID to the given counter. Use only if component ids from
	 * outside are given and you want to justify the current counter.
	 * 
	 * @param counter
	 *            int
	 */
	public void setComponentID(int counter) {
		this.componentID = counter;
	}

	/**
	 * Returns the component id counter state.
	 * 
	 * @return int - the current component id state.
	 */
	public int getComponentIDCounter() {
		return this.componentID;
	}
	
	public static class HoldNeighbourStructure {

		/* first object */
		private NetElement3D object0;

		/* second object */
		private NetElement3D object1;

		/* first objects index for neighbourhood to second object */
		private int index0;

		/* second objects index for neighbourhood to first object */
		private int index1;

		/**
		 * Default Constructor.<br>
		 * Sets the objects to <code>null</code> and the indexes to -1.
		 */
		public HoldNeighbourStructure() {
			object0 = null;
			object1 = null;
			index0 = -1;
			index1 = -1;
		}

		public HoldNeighbourStructure(NetElement3D obj0, int ind0,
				NetElement3D obj1, int ind1) {
			this.object0 = obj0;
			this.index0 = ind0;
			this.object1 = obj1;
			this.index1 = ind1;
		}

		public void setParameters(NetElement3D obj0, int ind0,
				NetElement3D obj1, int ind1) {
			this.object0 = obj0;
			this.index0 = ind0;
			this.object1 = obj1;
			this.index1 = ind1;
		}

		public NetElement3D getObject(int index) {
			if (index == 0)
				return this.object0;
			if (index == 1)
				return this.object1;
			return null;
		}

		public int getIndex(int index) {
			if (index == 0)
				return this.index0;
			if (index == 1)
				return this.index1;
			return -1;
		}
	}

}
