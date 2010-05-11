/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObjIterator;

/**
 * ResultGeometry3D is the common interface for all results during a computation
 * between two or more Objects in Space3D.<br>
 * It is currently capable to process the following classes:<br>
 * Sample3D, Curve3D, Surface3D and Volume3D<br>
 * Point3D, Segment3D, Triangle3D, Tetrahedron3D and subclasses.<br>
 * 
 * @author Dag Hammerich
 */
public interface ResultGeometry3D {

	// filling methods

	/**
	 * Returns the number of elements in this set.
	 * 
	 * @return int - number of elements.
	 */
	public int size();

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 * 
	 * @return boolean - true if empty, false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this set contains the specified element.
	 * 
	 * @param obj
	 *            SimpleGeoObj whose presence in this set is to be tested.
	 * @return boolean - true if this collection contains the element.
	 */
	public boolean contains(SimpleGeoObj obj);

	/**
	 * Returns an array containing all of the elements in this set.
	 * 
	 * @return SimpleGeoObj[] - array of element of this.
	 */
	public SimpleGeoObj[] toArray();

	/**
	 * Adds the specified element to this set if it is not already present.<br>
	 * 
	 * @param obj
	 *            SimpleGeoObj to be added to this set.
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 * @throws IllegalArgumentException
	 *             - signals not supported SimpleGeoObj type.
	 */
	public boolean add(SimpleGeoObj obj) throws IllegalArgumentException;

	/**
	 * Removes the specified element from this set if it is present.
	 * 
	 * @param obj
	 *            SimpleGeoObj to be removed from this set, if present.
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean remove(SimpleGeoObj obj);

	/**
	 * Removes all of the elements from this set.
	 */
	public void clear();

	/**
	 * Computes the current MBB3D of all elements in the ResultGeometry3D.
	 * 
	 * @return MBB3D computed MBB, or null if size = 0.
	 */
	public MBB3D computeMBB();

	/**
	 * Adds all of the elements in the specified result set to this result set.<br>
	 * The behavior of this operation is undefined if the specified result set
	 * is modified while the operation is in progress.
	 * 
	 * @param resultset
	 *            ResultSet with elements to be inserted into this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean addAll(ResultGeometry3D resultset);

	/**
	 * Removes all elements in this result set that are also contained in the
	 * specified result set.
	 * 
	 * @param resultset
	 *            ResultSet with elements to be removed from this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean removeAll(ResultGeometry3D resultset);

	// inquiry functions
	/**
	 * Tests whether the resultset has Point3D objects.
	 * 
	 * @return boolean - true if.
	 */
	public boolean hasPoint3D();

	/**
	 * Tests whether the resultset has Segment3D objects.
	 * 
	 * @return boolean - true if.
	 */
	public boolean hasSegment3D();

	/**
	 * Tests whether the resultset has Triangle3D objects.
	 * 
	 * @return boolean - true if.
	 */
	public boolean hasTriangle3D();

	/**
	 * Tests whether the resultset has Tetrahedron3D objects.
	 * 
	 * @return boolean - true if.
	 */
	public boolean hasTetrahedron3D();

	/**
	 * Tests whether the resultset has Wireframe3D objects.
	 * 
	 * @return boolean - true if.
	 */
	public boolean hasWireframe3D();

	/**
	 * Returns the number of Point3D objects in the result set.
	 * 
	 * @return int - number of Point3D objects.
	 */
	public int countPoint3D();

	/**
	 * Returns the number of Segment3D objects in the result set.
	 * 
	 * @return int - number of Segment3D objects.
	 */
	public int countSegment3D();

	/**
	 * Returns the number of Triangle3D objects in the result set.
	 * 
	 * @return int - number of Triangle3D objects.
	 */
	public int countTriangle3D();

	/**
	 * Returns the number of Tetrahedron3D objects in the result set.
	 * 
	 * @return int - number of Tetrahedron3D objects.
	 */
	public int countTetrahedron3D();

	/**
	 * Returns the number of Wireframe3D objects in the result set.
	 * 
	 * @return int - number of Wireframe3D objects.
	 */
	public int countWireframe3D();

	// filter functions

	/**
	 * Returns the Point3D objects from the result set.
	 * 
	 * @return Set of Point3D objects.
	 */
	public Set filterPoint3D();

	/**
	 * Returns the Segment3D objects from the result set.
	 * 
	 * @return Set of Segment3D Objects.
	 */
	public Set filterSegment3D();

	/**
	 * Returns the Triangle3D objects from the result set.
	 * 
	 * @return Set of Triangle3D objects.
	 */
	public Set filterTriangle3D();

	/**
	 * Returns the Tetrahedron3D objects from the result set.
	 * 
	 * @return Set of Tetrahedron3D objects.
	 */
	public Set filterTetrahedron3D();

	/**
	 * Returns the Wireframe3D objects from the result set.
	 * 
	 * @return Set of Wireframe3D objects.
	 */
	public Set filterWireframe3D();

	// iterators

	/**
	 * Returns an SimpleGeoObjIterator over all elements in the result set.<br>
	 * 
	 * @return SimpleGeoObjIterator - iterator.
	 */
	public SimpleGeoObjIterator iteratorObjects();

	/**
	 * Returns an iterator over all Point3D objects in the result set.
	 * 
	 * @return Iterator over Point3D objects.
	 */
	public Iterator iteratorPoint3D();

	/**
	 * Returns an iterator over all Segment3D objects in the result set.
	 * 
	 * @return Iterator pver Segment3D objects.
	 */
	public Iterator iteratorSegment3D();

	/**
	 * Returns an iterator over all Triangle3D objects in the result set.
	 * 
	 * @return Iterator over Triangle3D objects.
	 */
	public Iterator iteratorTriangle3D();

	/**
	 * Returns an iterator over all Tetrahedron3D objects in the result set.
	 * 
	 * @return Iterator over Tetrahedron3D objects.
	 */
	public Iterator iteratorTetrahedron3D();

	/**
	 * Returns an iterator over all Wireframe3D objects in the result set.
	 * 
	 * @return Iterator over Wireframe3D objects.
	 */
	public Iterator iteratorWireframe3D();

	/**
	 * Returns an SimpleGeoObjIterator for all elements passing the given
	 * Predicate evaluation.
	 * 
	 * @param pred
	 *            Predicate
	 * @return SimpleGeoObjIterator.
	 */
	public SimpleGeoObjIterator iteratorFilter(Predicate pred);

	/**
	 * Interface defining the predicate method for an FilterIterator in the
	 * ResultGeometry3D.
	 */
	public interface Predicate {

		/**
		 * Returns true if the input object matches this predicate.
		 * 
		 * @return boolean - true if matches the predicate, false otherwise.
		 */
		public boolean evaluate(SimpleGeoObj obj);
	}

}
