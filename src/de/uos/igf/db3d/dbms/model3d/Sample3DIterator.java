/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;

/**
 * Iterator interface for the Sample3D objects.
 */
public interface Sample3DIterator extends Iterator {

	/**
	 * Returns true if the iteration has more elements.
	 * 
	 * @return boolean - true if has more elements, false otherwise.
	 */
	public boolean hasNext();

	/**
	 * Returns the next element in the iteration.
	 * 
	 * @return Sample3D - next element.
	 */
	public Sample3D nextElt();

	/**
	 * <b>!! NotSupportedOperation !!</b>
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove();
}
