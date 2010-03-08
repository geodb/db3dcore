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
 * File Volume3DIterator.java - created on 06.08.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;

/**
 * Iterator interface for the Volume3D objects
 */
public interface Volume3DIterator extends Iterator {

	/**
	 * Returns true if the iteration has more elements.
	 * 
	 * @return boolean - true if there are more elements, false otherwise.
	 */
	public boolean hasNext();

	/**
	 * Returns the next element in the iteration.
	 * 
	 * @return Volume3D - next element.
	 */
	public Volume3D nextElt();

	/**
	 * Removes the last element returned by the iterator.<br>
	 * <b>!! NotSupportedOperation !!</b>
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove();

}
