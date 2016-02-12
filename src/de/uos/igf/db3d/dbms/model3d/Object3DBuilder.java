/*
 * Sourcecode of the Researchproject
 * "Development of Component-Software for the Internet-Based
 * Access to Geo-Database Services"
 *
 * University of Osnabrueck
 * Research Center for Geoinformatics and Remote Sensing
 *
 * Copyright (C) 2002-2005 Researchgroup Prof. Dr. Martin Breunig
 *
 * File Object3DBuilder.java - created on 16.06.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.impl.Object3DImpl;
import de.uos.igf.db3d.dbms.structure.OID;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * <p>
 * Object3DBuilder is for constructing Object3D objects.<br>
 * Because all constructors are protected this is the only way to instantiate an
 * Object3D outside the package. <br>
 * <br>
 * You first instanciate an object of Object3DBuilder and then retrieve the
 * different Net3D- and ThematicBuilder you need for constructing this Object3D
 * object. <br>
 * <br>
 * </p>
 * Revision:<br>
 * 
 */
public class Object3DBuilder {

	private Space3D space;
	/** the ScalarOperator to be used if build in Workspace */
	private ScalarOperator wsSOP;
	private SpatialObject3D spatial;
	private long time;
	private OID oid;

	/**
	 * Constructor.
	 * 
	 * @param space
	 *            - Space3D
	 * @param update
	 *            - if true the object is updated
	 */
	public Object3DBuilder(Space3D space) {
		this.space = space;
		this.time = 0;
		this.spatial = null;
		this.oid = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param workspace
	 *            - Workspace
	 */
	public Object3DBuilder(ScalarOperator sop) {
		this.space = null;
		this.wsSOP = sop;
		this.time = 0;
		this.spatial = null;
		this.oid = null;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param workspace
	 *            - Workspace
	 */
	public Object3DBuilder(ScalarOperator sop, Space3D space) {
		this.space = space;
		this.wsSOP = sop;
		this.time = 0;
		this.spatial = null;
		this.oid = null;
	}

	/**
	 * Returns a ScalarOperator as copy.<br>
	 * 
	 * @return ScalarOperator sop
	 */
	public ScalarOperator getScalarOperator() {
		if (this.space != null)
			return space.getScalarOperator().copy();
		else
			return this.wsSOP.copy();
	}

	/**
	 * Returns a builder for a PointNet3D Object
	 * 
	 * @return PointNetBuilder
	 */
	public PointNetBuilder getPointNetBuilder() {
		if (this.space != null)
			return new PointNetBuilder(space);
		else
			return new PointNetBuilder(wsSOP);
	}

	/**
	 * Returns a builder for a SegmentNet3D Object
	 * 
	 * @return SegmentNetBuilder
	 */
	public SegmentNetBuilder getSegmentNetBuilder() {
		if (this.space != null)
			return new SegmentNetBuilder(space);
		else
			return new SegmentNetBuilder(wsSOP);
	}

	/**
	 * Returns a builder for a TriangleNet3D Object
	 * 
	 * @return TriangleNetBuilder
	 */
	public TriangleNetBuilder getTriangleNetBuilder() {
		if (this.space != null)
			return new TriangleNetBuilder(space);
		else
			return new TriangleNetBuilder(wsSOP);
	}

	/**
	 * Returns a builder for a TetrahedronNet3D Object
	 * 
	 * @return TetrahedronNetBuilder
	 */
	public TetrahedronNetBuilder getTetrahedronNetBuilder() {
		if (this.space != null)
			return new TetrahedronNetBuilder(space);
		else
			return new TetrahedronNetBuilder(wsSOP);
	}

	/**
	 * Sets the spatial part of this after building with a NetBuilder.
	 * 
	 * @param spatial
	 *            - Spatial3D
	 */
	public void setSpatialPart(Spatial3D spatial) {
		this.spatial = (SpatialObject3D) spatial;
	}

	/**
	 * Sets the timestamp of the Object3D
	 * 
	 * @param time
	 *            - long
	 */
	public void setTimestamp(long time) {
		this.time = time;
	}

	/**
	 * Sets the OID of the Object3D for update
	 * 
	 * @param oid
	 *            - OID
	 */
	public void setOID(OID oid) {
		this.oid = oid;
	}

	/**
	 * Resets this Object3DBuilder for futher use. Sets all internal states to
	 * the one given in the constructor.
	 */
	public void reset() {
		this.time = 0;
		this.spatial = null;
		this.oid = null;
	}

	/**
	 * Build the whole object and registeres with space.<br>
	 * Returns null if neither the thematic part nor the spatial part is set.
	 * 
	 * @return Object3D - builded object
	 */
	public Object3D getObject3D() {
		if (this.spatial == null)
			return null;

		// Build object
		Object3DImpl obj = new Object3DImpl();

		// set spatial part and back reference
		if (this.spatial != null) {
			obj.setSpatialPart(this.spatial);
			this.spatial.setObject3D(obj);
		}

		obj.setTimestamp(this.time);

		if (this.space != null) { // should get registered with space
			if (this.oid == null) {// put into space

				this.space.insert(obj);
			} else { // put as update into space

				obj.setOID(this.oid);
				this.space.update(obj);
			}
		} else { // should get registered with workspace
			// will get registered through its oid
		}

		return obj;
	}

	protected Space3D getSpace3D() {
		return this.space;
	}

	protected SpatialObject3D getSpatial() {
		return this.spatial;
	}

	protected long getTime() {
		return this.time;
	}

}
