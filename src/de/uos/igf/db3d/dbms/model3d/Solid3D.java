/*
 * Copyright (C) Prof. Martin Breunig
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
