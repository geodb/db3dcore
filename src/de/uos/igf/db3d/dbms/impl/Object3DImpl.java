/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.impl;

import java.util.LinkedHashMap;

import de.uos.igf.db3d.dbms.model3d.Object3D;
import de.uos.igf.db3d.dbms.model3d.Spatial3D;
import de.uos.igf.db3d.dbms.model3d.SpatialObject3D;
import de.uos.igf.db3d.dbms.structure.OID;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * Implementation class for Object3D.<br>
 * Represents the complete Object in 3D space with an OID and a geometric part.
 * 
 * @author unknown
 * @author Revision: Edgar Butwilowski
 * 
 */
public class Object3DImpl implements Object3D, PersistentObject {

	/* reference to spatial part */
	private SpatialObject3D spatial;

	/* OID reference */
	private OID oid;

	/* time stamp of this */
	private long time;

	/* back reference to Space3D this belongs to */
	private Space3D space;

	/* new thematic handling */
	private LinkedHashMap<String, String> thematicinfos;

	/**
	 * Default Constructor.
	 */
	public Object3DImpl() {
		this.spatial = null;
		this.oid = null;
		this.space = null;
		this.thematicinfos = new LinkedHashMap<String, String>();
	}
	

	/**
	 * Tests if this has a spatial part.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasSpatialPart() {
		return spatial != null;
	}

	/**
	 * Test if this has a thematic part.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasThematicPart() {
		return false;
	}

	/**
	 * Not implemented for this class.
	 * 
	 * @return always returns <code>null</code>.
	 */
	public Object getThematic3D() {
		return null;
	}

	/**
	 * Sets the thematic part of this.
	 * 
	 * @param thematic
	 *            - Object
	 */
	public void setThematicPart(Object thematic) {
	}

	/**
	 * Returns the enclosing space of this.
	 * 
	 * @return Space3D - enclosing space.
	 */
	public Space3D getEnclosingSpace() {
		return space;
	}

	/**
	 * Returns the spatial part of this.
	 * 
	 * @return Spatial3D - spatial part.
	 */
	public Spatial3D getSpatial3D() {
		return spatial;
	}

	/**
	 * Returns the time stamp of this.<br>
	 * Value interpreted as defined in Space3D.
	 * 
	 * @return long - time stamp.
	 */
	public long getTimestamp() {
		return time;
	}

	/**
	 * Returns the object id of this.
	 * 
	 * @return OID - object id.
	 */
	public OID getOID() {
		return this.oid;
	}

	/**
	 * Tests whether this object is already registered in a Space.
	 * 
	 * @return boolean - true if registered, false otherwise.
	 * @see db3d.dbms.model3d.Object3D#isRegisteredInSpace()
	 */
	public boolean isRegisteredInSpace() {
		return this.space != null;
	}

	/**
	 * Sets the object id of this.
	 * 
	 * @param oid
	 *            OID to set
	 */
	public void setOID(OID oid) {
		this.oid = oid;
	}

	/**
	 * Sets the enclosing space of this.
	 * 
	 * @param space
	 *            enclosing Space3D to be set
	 */
	public void setSpace(Space3D space) {
		this.space = (Space3D) space;
	}

	/**
	 * Sets the spatial part of this.
	 * 
	 * @param spatial
	 *            Spatial3D to be set
	 */
	public void setSpatialPart(Spatial3D spatial) {
		this.spatial = (SpatialObject3D) spatial;
	}

	/**
	 * Sets the time stamp of this.
	 * 
	 * @param time
	 *            long to be set
	 */
	public void setTimestamp(long time) {
		this.time = time;
	}

	/**
	 * Returns the <code>HashMap</code> with all thematicinfos.
	 * 
	 * @return the HashMap with all thematicsinfos.
	 */
	public LinkedHashMap<String, String> getThematicinfos() {
		return thematicinfos;
	}

	/**
	 * 
	 * @param key
	 *            the key of the info you want to get
	 * @return String - thematic information to the given key.
	 */
	public String getThematicinfo(String key) {
		return thematicinfos.get(key);
	}

	/**
	 * 
	 * @param info
	 *            the info value to be set
	 * @param key
	 *            the info key
	 */
	public void setThematicinfo(String info, String key) {
		thematicinfos.put(key, info);
	}

	/**
	 * Sets thematicinfos
	 * 
	 * @param infos
	 *            HashMap with thematicinfos
	 */
	public void setThematicinfos(LinkedHashMap<String, String> infos) {
		if (getEnclosingSpace().getObjectByName(infos.get("name")) != null) {
			this.thematicinfos = (LinkedHashMap<String, String>) infos.clone();
			setThematicinfo(
					"unknown" + new Integer(oid.getObject()).toString(), "name");
			// An object called \"" + infos.get("name")
			// + "\" already exists in this space! Name changed to "
			// + getThematicinfo("name") + "!"
		} else if (infos.get("name") == null) {
			this.thematicinfos = (LinkedHashMap<String, String>) infos.clone();
			setThematicinfo(
					"unknown" + new Integer(oid.getObject()).toString(), "name");
		} else {
			this.thematicinfos = (LinkedHashMap<String, String>) infos.clone();
		}
	}
}
