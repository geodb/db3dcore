/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d.api;

import java.util.LinkedHashMap;

/**
 * Interface for an Object3D.<br>
 */
public interface Object3DC {

	/**
	 * Test if this has a spatial part.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasSpatialPart();

	/**
	 * Returns the spatial part of this.
	 * 
	 * @return Spatial3D - spatial part if this.
	 */
	public Spatial3D getSpatialPart();
	
	/**
	 * Returns the spatial part of this.
	 * 
	 * @return Spatial3D - spatial part if this.
	 */
	public void setSpatialPart(Spatial3D spatial);

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
	public Space3DC getSpace();

	/**
	 * Returns the object id of this.
	 * 
	 * @return OID - object id.
	 */
	public OID getOID();

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
