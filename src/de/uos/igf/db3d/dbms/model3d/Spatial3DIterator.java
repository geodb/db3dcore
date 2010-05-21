/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;

/**
 * Iterator interface for the Spatial3D objects
 */
public interface Spatial3DIterator extends Iterator {

	/**
	 * Tests true if the iterator has more elements.
	 * 
	 * @return boolean - true if the iterator has more elements, false
	 *         otherwise.
	 */
	public boolean hasNext();

	/**
	 * Returns the next element in the iteration.
	 * 
	 * @return Spatial3D - next element.
	 */
	public Spatial3D nextElt();

	/**
	 * <b>!! NotSupportedOperation !!</b>
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove();
}
