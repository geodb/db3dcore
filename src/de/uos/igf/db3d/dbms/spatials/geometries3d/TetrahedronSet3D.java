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
 * Represents a set of tetrahedrons in 3D.
 */
public class TetrahedronSet3D implements Geometry3D {

	/* internal set instance */
	private transient final Set set3d;

	/** constant for SegmentSet3D with Strict_Equal equality */

	public static final int STRICT_EQUAL = Equivalentable.STRICT_EQUAL;

	/** constant for SegmentSet3D with Geometry_Equal equality/equivalence */
	public static final int GEOMETRY_EQUAL = Equivalentable.GEOMETRY_EQUIVALENT;

	/**
	 * Constructor.<br>
	 * Constructs a transient TetrahedronSet3D.
	 * 
	 * @param sop
	 *            GeoEpsilon
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public TetrahedronSet3D(GeoEpsilon sop, int constant) {
		set3d = new EquivalentableHashSet(sop, constant);
	}

	/**
	 * Constructor.<br>
	 * Constructs a transient TetrahedronSet3D.
	 * 
	 * @param initialCapacity
	 *            initial capacity of set
	 * @param sop
	 *            GeoEpsilon
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public TetrahedronSet3D(int initialCapacity, GeoEpsilon sop,
			int constant) {
		set3d = new EquivalentableHashSet(initialCapacity, sop, constant);
	}

	/**
	 * Constructor.<br>
	 * Constructs a TetrahedronSet3D with given Set.<br>
	 * The elements in the Set must be of type Tetrahedron3D or subclasses.
	 * 
	 * @param initialSet
	 *            Set of type Tetrahedron3D
	 * @param sop
	 *            GeoEpsilon
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public TetrahedronSet3D(Set initialSet, GeoEpsilon sop, int constant) {
		EquivalentableHashSet hset = new EquivalentableHashSet(initialSet
				.size(), sop, constant);
		Iterator it = initialSet.iterator();
		while (it.hasNext())
			hset.add(it.next());

		this.set3d = hset;
	}

	/**
	 * Constructor.<br>
	 * Constructs a TetrahedronSet3D with given Set.<br>
	 * The elements in the Set must be of type Tetrahedron3D or subclasses.
	 * 
	 * @param initialSet
	 *            EquivalentableHashSet of type Tetrahedron3D
	 */
	public TetrahedronSet3D(EquivalentableHashSet initialSet) {
		this.set3d = initialSet;
	}

	/**
	 * Returns the number of elements in this set.
	 * 
	 * @return int - size of the set.
	 */
	public int size() {
		return getSet().size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
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
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean contains(Tetrahedron3D obj) {
		return getSet().contains(obj);
	}

	/**
	 * Returns an array containing all of the elements in this set.
	 * 
	 * @return Tetrahedron3D[] - all elements of the set.
	 */
	public Tetrahedron3D[] toArray() {
		return (Tetrahedron3D[]) getSet().toArray(
				new Tetrahedron3D[getSet().size()]);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param obj
	 *            Tetrahedron3D to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Tetrahedron3D obj) {
		return getSet().add(obj);
	}

	/**
	 * Adds the specified elements to this set if they are not already present.
	 * 
	 * @param obj
	 *            Tetrahedron3D[] to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Tetrahedron3D[] obj) {
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
	 *            Tetrahedron3D to be removed
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean remove(Tetrahedron3D obj) {
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
	 * @param tetraset
	 *            TetrahedronSet3D with elements to be inserted into this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean addAll(TetrahedronSet3D tetraset) {
		return getSet().addAll(tetraset.getAsSet());
	}

	/**
	 * Removes all elements in this result set that are also contained in the
	 * specified result set.
	 * 
	 * @param tetraset
	 *            TetrahedronSet3D with elements to be removed from this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean removeAll(TetrahedronSet3D tetraset) {
		return getSet().removeAll(tetraset.getAsSet());
	}

	/**
	 * Retains only the elements in this result set that are contained in the
	 * specified result set.
	 * 
	 * @param tetraset
	 *            TetrahedronSet3D with elements to be retained in this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean retainAll(TetrahedronSet3D tetraset) {
		return getSet().retainAll(tetraset.getAsSet());
	}

	// iterator

	/**
	 * Returns an iterator over all Tetrahedron3D objects in the set.
	 * 
	 * @return Iterator over the elements of this.
	 */
	public Iterator iterator() {
		return getSet().iterator();
	}

	// for compability

	/**
	 * Returns a Set interface for given TetrahedronSet3D for compatibility
	 * issues.
	 * 
	 * @return Set - the internal set instance of this.
	 */
	public Set getAsSet() {
		return this.set3d;
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return TETRAHEDRONSET3D always.
	 * @see Spatial.dbms.geom.Spatial3D#getGeometryType()
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.TETRAHEDRONSET;
	}

	/**
	 * Returns the MBB of this.
	 * 
	 * @return MBB of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @see Spatial.dbms.structure.Spatial3D#getMBB()
	 */
	public MBB3D getMBB() {
		if (size() > 0) {
			Iterator<Tetrahedron3D> it = this.iterator();
			it.hasNext();
			Tetrahedron3D tetra = it.next();

			double xMin = tetra.points[0].x;
			double yMin = tetra.points[0].y;
			double zMin = tetra.points[0].z;
			for (int i = 1; i < 4; i++) {
				if (xMin > tetra.points[i].x)
					xMin = tetra.points[i].x;
				if (xMin > tetra.points[i].y)
					xMin = tetra.points[i].y;
				if (xMin > tetra.points[i].z)
					xMin = tetra.points[i].z;
			}
			double xMax = xMin;
			double yMax = yMin;
			double zMax = zMin;

			double value;
			while (it.hasNext()) {
				tetra = it.next();

				for (int i = 0; i < 3; i++) {

					value = tetra.points[i].x;
					if (xMin > value)
						xMin = value;
					else if (xMax < value)
						xMax = value;

					value = tetra.points[i].y;
					if (yMin > value)
						yMin = value;
					else if (yMax < value)
						yMax = value;

					value = tetra.points[i].z;
					if (zMin > value)
						zMin = value;
					else if (zMax < value)
						zMax = value;
				}
			}

			Point3D pMin = new Point3D(xMin, yMin, zMin);
			Point3D pMax = new Point3D(xMax, yMax, zMax);
			return new MBB3D(pMin, pMax);
		}
		return null;
	}

	/*
	 * Returns the internal set instance of this.
	 * 
	 * @return Set - the internal set instance of this.
	 */
	private Set getSet() {
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
