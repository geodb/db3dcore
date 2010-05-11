/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.util.Iterator;

/**
 * Iterator interface for the SimpleGeoObj objects
 */
public interface SimpleGeoObjIterator extends Iterator {

	/**
	 * Returns true if the iteration has more elements.
	 * 
	 * @return boolean - true if has more elements, false otherwise.
	 */
	public boolean hasNext();

	/**
	 * Returns the next SimpleGeoObj element in the iteration.
	 * 
	 * @return SimpleGeoObj - next element.
	 */
	public SimpleGeoObj nextElt();

	/**
	 * <b>!! NotSupportedOperation !!</b>
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove();
}
