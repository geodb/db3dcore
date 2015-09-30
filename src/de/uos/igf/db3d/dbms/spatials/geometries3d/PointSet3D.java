/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.spatials.geometries3d;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.collections.EquivalentableHashSet;
import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * Represents a set of points in 3D space.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class PointSet3D implements Geometry3D {

	/* internal set instance */
	private transient final Set<Point3D> set3d;

	/**
	 * Constructor.<br>
	 * Constructs a transient PointSet3D.
	 * 
	 * @param sop
	 *            GeoEpsilon
	 */
	public PointSet3D(GeoEpsilon sop) {
		set3d = new EquivalentableHashSet(sop, Equivalentable.STRICT_EQUAL);
	}

	/**
	 * Constructor.<br>
	 * Constructs a transient PointSet3D.
	 * 
	 * @param initialCapacity
	 *            initial capacity of set
	 * @param sop
	 *            GeoEpsilon
	 */
	public PointSet3D(int initialCapacity, GeoEpsilon sop) {
		set3d = new EquivalentableHashSet(initialCapacity, sop,
				Equivalentable.STRICT_EQUAL);
	}

	/**
	 * Constructor.<br>
	 * Constructs a PointSet3D with given Set.<br>
	 * The elements in the Set must be of type Point3D or subclasses.
	 * 
	 * @param initialSet
	 *            Set of type Point3D
	 * @param sop
	 *            GeoEpsilon
	 */
	public PointSet3D(Set initialSet, GeoEpsilon sop) {
		EquivalentableHashSet hset = new EquivalentableHashSet(
				initialSet.size(), sop, Equivalentable.STRICT_EQUAL);
		Iterator it = initialSet.iterator();
		while (it.hasNext())
			hset.add(it.next());

		this.set3d = hset;
	}

	/**
	 * Constructor.<br>
	 * Constructs a PointSet3D with given Set.<br>
	 * The elements in the Set must be of type Point3D or subclasses.
	 * 
	 * @param initialSet
	 *            EquivalentableHashSet of type Point3D
	 */
	public PointSet3D(EquivalentableHashSet initialSet) {
		this.set3d = initialSet;
	}

	/**
	 * Returns the MBB of all points.<br>
	 * 
	 * @return MBB3D - or null if size of set is zero.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D getMBB() { // Dag
		if (size() > 0) {
			Iterator<Point3D> it = this.iterator();
			it.hasNext();
			Point3D p = it.next();

			double xMin = p.x;
			double yMin = p.y;
			double zMin = p.z;
			double xMax = xMin;
			double yMax = yMin;
			double zMax = zMin;

			double value;
			while (it.hasNext()) {
				p = it.next();
				value = p.x;
				if (xMin > value)
					xMin = value;
				else if (xMax < value)
					xMax = value;

				value = p.y;
				if (yMin > value)
					yMin = value;
				else if (yMax < value)
					yMax = value;

				value = p.z;
				if (zMin > value)
					zMin = value;
				else if (zMax < value)
					zMax = value;
			}
			Point3D pMin = new Point3D(xMin, yMin, zMin);
			Point3D pMax = new Point3D(xMax, yMax, zMax);
			return new MBB3D(pMin, pMax);
		}
		return null;
	}

	/**
	 * Returns the number of elements in this set.
	 * 
	 * @return int - the size of the set.
	 */
	public int size() {
		return getSet().size();
	}

	/**
	 * Returns <code>true</code> if this set contains no elements.
	 * 
	 * @return boolean - true if the set is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return getSet().isEmpty();
	}

	/**
	 * Returns <code>true</code> if this set contains the specified element.
	 * 
	 * @param obj
	 *            Point3D
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean contains(Point3D obj) {
		return getSet().contains(obj);
	}

	/**
	 * Returns an array containing all of the elements in this set.
	 * 
	 * @return Point3D[] with all elements of the set.
	 */
	public Point3D[] toArray() {
		return getSet().toArray(new Point3D[getSet().size()]);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param obj
	 *            Point3D to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Point3D obj) {
		return getSet().add(obj);
	}

	/**
	 * Adds the specified elements to this set if they are not already present.
	 * 
	 * @param obj
	 *            Point3D[] to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Point3D[] obj) {
		boolean result = false;
		int length = obj.length;
		for (int i = 0; i < length; i++)
			result = add(obj[i]);

		return result;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 * 
	 * @param obj
	 *            Point3D to be removed
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean remove(Point3D obj) {
		return getSet().remove(obj);
	}

	/**
	 * Removes all of the elements from this set.
	 */
	public void clear() {
		getSet().clear();
	}

	/**
	 * Adds all of the elements in the specified result set to this result set.
	 * The behavior of this operation is undefined if the specified result set
	 * is modified while the operation is in progress.
	 * 
	 * @param pointset
	 *            PointSet3D with elements to be inserted into this
	 * @return boolean - true if this collection changed as a result of the call
	 */
	public boolean addAll(PointSet3D pointset) {
		return getSet().addAll(pointset.getAsSet());
	}

	/**
	 * Removes all elements in this result set that are also contained in the
	 * specified result set.
	 * 
	 * @param pointset
	 *            PointSet3D with elements to be removed from this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean removeAll(PointSet3D pointset) {
		return getSet().removeAll(pointset.getAsSet());
	}

	/**
	 * Retains only the elements in this result set that are contained in the
	 * specified result set.
	 * 
	 * @param pointset
	 *            PointSet3D with elements to be retained in this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean retainAll(PointSet3D pointset) {
		return getSet().retainAll(pointset.getAsSet());
	}

	// iterator

	/**
	 * Returns an iterator over all Point3D objects in the set.
	 * 
	 * @return Iterator over all Point3D objects in the set.
	 */
	public Iterator<Point3D> iterator() {
		return getSet().iterator();
	}

	// for compatibility

	/**
	 * Returns a Set interface for given PointSet3D for compatibility issues.
	 * 
	 * @return Set of Point3D.
	 */
	public Set<Point3D> getAsSet() {
		return this.set3d;
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return POINTSET3D always.
	 * @see Spatial.dbms.geom.Geometry#getGeometryType()
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.POINTSET;
	}

	/*
	 * Returns the internal set instance of this.
	 * 
	 * @return Set - the internal set instance of this.
	 */
	private Set<Point3D> getSet() {
		return this.set3d;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon sop) {
		throw new NotYetImplementedException();
	}

	@Override
	public int isEqualHC(int factor) {
		throw new NotYetImplementedException();
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon sop) {
		throw new NotYetImplementedException();
	}

	@Override
	public int isGeometryEquivalentHC(int factor) {
		throw new NotYetImplementedException();
	}
}
