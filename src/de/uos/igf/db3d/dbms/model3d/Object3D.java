/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.LinkedHashMap;

import de.uos.igf.db3d.dbms.structure.OID;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * Interface for an Object3D.<br>
 */
public interface Object3D {

	/**
	 * Test if this has a spatial part.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasSpatialPart();

	/**
	 * Test if this has a thematic part.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasThematicPart();

	/**
	 * Returns the spatial part of this.
	 * 
	 * @return Spatial3D - spatial part if this.
	 */
	public Spatial3D getSpatial3D();

	/**
	 * Sets the thematic part of this.
	 * 
	 * @param thematic
	 *            - Object
	 */
	public void setThematicPart(Object thematic);

	/**
	 * Returns the timestamp of this.<br>
	 * Value interpreted as defined in Space3D.
	 * 
	 * @return long - timestamp of this.
	 */
	public long getTimestamp();

	/**
	 * Tests whether this object is already registered in a Space.<br>
	 * If false is returned, the methods getEnclosingSpace and getOID will fail.
	 * 
	 * @return boolean - true if is registered, false otherwise.
	 */
	public boolean isRegisteredInSpace();

	/**
	 * Returns the enclosing space of this.
	 * 
	 * @return Space3D - space.
	 */
	public Space3D getEnclosingSpace();

	/**
	 * Returns the object id of this.
	 * 
	 * @return OID - object id.
	 */
	public OID getOID();

	/**
	 * Returns the thematic part of this.
	 * 
	 * @return Object
	 */
	public Object getThematic3D();

	/**
	 * Returns a hash map with thematic information of this.
	 * 
	 * @return LinkedHashMap - hash map with thematic information of this.
	 */
	public LinkedHashMap<String, String> getThematicinfos();

	/**
	 * Returns thematic information of this to the given key.
	 * 
	 * @param key
	 *            the key of the info you want to get
	 * @return String - thematic information entry.
	 */
	public String getThematicinfo(String key);

	/**
	 * Sets thematic information to the given key to the given String.
	 * 
	 * @param info
	 *            the info value
	 * @param key
	 *            the info key
	 */
	public void setThematicinfo(String info, String key);

	/**
	 * Sets thematicinfos
	 * 
	 * @param infos
	 *            HashMap with thematicinfos
	 */
	public void setThematicinfos(LinkedHashMap<String, String> infos);
}
