/*
 * Source Code of the Research Project
 * "Development of Component-Software for the Internet-Based
 * Access to Geo-Database Services"
 *
 * University of Osnabrueck
 * Research Center for Geoinformatics and Remote Sensing
 *
 * Copyright (C) 2002-2005 Research Group Prof. Dr. Martin Breunig
 *
 * File Solid3D.java - created on 04.08.2003
 */
package de.uos.igf.db3d.dbms.model3d;

/**
 * Interface for the specifics of a Solid3D.<br>
 * Extends Volume3D for methods common to all volumes.
 */
public interface Solid3D extends Volume3D {

	/**
	 * Returns the number of tetrahedrons in this net.
	 * 
	 * @return int - number of tetrahedrons.
	 */
	public int countTetras();

	/**
	 * Returns the number of tetrahedrons in the border of this net.
	 * 
	 * @return int - number of tetrahedrons
	 */
	public int countBorderTetras();

}
