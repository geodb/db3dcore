/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;

/**
 * Represents a set of segments in 3D.
 */
public class SegmentSet3D implements SimpleGeoObj {

	/* internal set instance */
	private transient final Set set3d;

	/** constant for SegmentSet3D with Strict_Equal equality */
	public static final int STRICT_EQUAL = Equivalentable.STRICT_EQUAL;

	/** constant for SegmentSet3D with Geometry_Equal equality/equivalence */
	public static final int GEOMETRY_EQUAL = Equivalentable.GEOMETRY_EQUIVALENT;

	/**
	 * Constructor.<br>
	 * Constructs a transient SegmentSet3D.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public SegmentSet3D(ScalarOperator sop, int constant) {
		set3d = new EquivalentableHashSet(sop, constant);
	}

	/**
	 * Constructor.<br>
	 * Constructs a transient SegmentSet3D.
	 * 
	 * @param initialCapacity
	 *            initial capacity of set
	 * @param sop
	 *            ScalarOperator
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public SegmentSet3D(int initialCapacity, ScalarOperator sop, int constant) {
		set3d = new EquivalentableHashSet(initialCapacity, sop, constant);
	}

	/**
	 * Constructor.<br>
	 * Constructs a SegmentSet3D with given Set.<br>
	 * The elements in the Set must be of type Segment3D or subclasses.
	 * 
	 * @param initialSet
	 *            Set of type Segment3D
	 * @param sop
	 *            ScalarOperator
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public SegmentSet3D(Set initialSet, ScalarOperator sop, int constant) {
		EquivalentableHashSet hset = new EquivalentableHashSet(initialSet
				.size(), sop, constant);
		Iterator it = initialSet.iterator();
		while (it.hasNext())
			hset.add(it.next());

		this.set3d = hset;
	}

	/**
	 * Constructor.<br>
	 * Constructs a SegmentSet3D with given Set.<br>
	 * The elements in the Set must be of type Segment3D or subclasses.
	 * 
	 * @param initialSet
	 *            EquivalentableHashSet of type Segment3D
	 */
	public SegmentSet3D(EquivalentableHashSet initialSet) {
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
	 * Returns <code>true</code> if this set contains no elements.
	 * 
	 * @return boolean - true if the set is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return getSet().isEmpty();
	}

	/**
	 * Returns <code>true</code > if this set contains the specified element.
	 * 
	 * @param obj
	 *            Segment3D for test
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean contains(Segment3D obj) {
		return getSet().contains(obj);
	}

	/**
	 * Returns an array containing all of the elements in this set.
	 * 
	 * @return Segment3D[] - array of all elements of the set.
	 */
	public Segment3D[] toArray() {
		return (Segment3D[]) getSet().toArray(new Segment3D[getSet().size()]);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param obj
	 *            Segment3D to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Segment3D obj) {
		return getSet().add(obj);
	}

	/**
	 * Adds the specified elements to this set if they are not already present.
	 * 
	 * @param obj
	 *            Segment3D[] to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Segment3D[] obj) {
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
	 *            Segment3D to be removed
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean remove(Segment3D obj) {
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
	 * @param segmentset
	 *            SegmentSet3D with elements to be inserted into this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean addAll(SegmentSet3D segmentset) {
		return getSet().addAll(segmentset.getAsSet());
	}

	/**
	 * Removes all elements in this result set that are also contained in the
	 * specified result set.
	 * 
	 * @param segmentset
	 *            SegmentSet3D with elements to be removed from this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean removeAll(SegmentSet3D segmentset) {
		return getSet().removeAll(segmentset.getAsSet());
	}

	/**
	 * Retains only the elements in this result set that are contained in the
	 * specified result set.
	 * 
	 * @param segmentset
	 *            SegmentSet3D with elements to be retained in this.
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean retainAll(SegmentSet3D segmentset) {
		return getSet().retainAll(segmentset.getAsSet());
	}

	// iterator

	/**
	 * Returns an iterator over all Segment3D objects in the set.
	 * 
	 * @return Iterator over all elements of this set.
	 */
	public Iterator<Segment3D> iterator() {
		return getSet().iterator();
	}

	// for compability

	/**
	 * Returns a Set interface for given SegmentSet3D for compatibility issues.
	 * 
	 * @return Set.
	 */
	public Set getAsSet() {
		return this.set3d;
	}

	/**
	 * Returns the type of this as a SimpleGeoObj,
	 * 
	 * @return SEGMENTSET3D always.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.SEGMENTSET3D;
	}

	/**
	 * Returns the MBB of this.
	 * 
	 * @return MBB of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @see db3d.dbms.structure.GeoObj#getMBB()
	 */
	public MBB3D getMBB() {
		if (size() > 0) {
			Iterator<Segment3D> it = this.iterator();
			it.hasNext();
			Segment3D seg = it.next();

			double xMin = seg.getPoint(0).getX();
			double yMin = seg.getPoint(0).getY();
			double zMin = seg.getPoint(0).getZ();
			if (xMin > seg.getPoint(1).getX())
				xMin = seg.getPoint(1).getX();
			if (xMin > seg.getPoint(1).getY())
				xMin = seg.getPoint(1).getY();
			if (xMin > seg.getPoint(1).getZ())
				xMin = seg.getPoint(1).getZ();
			double xMax = xMin;
			double yMax = yMin;
			double zMax = zMin;

			double value;
			while (it.hasNext()) {
				seg = it.next();

				for (int i = 0; i < 2; i++) {

					value = seg.getPoint(i).getX();
					if (xMin > value)
						xMin = value;
					else if (xMax < value)
						xMax = value;

					value = seg.getPoint(i).getY();
					if (yMin > value)
						yMin = value;
					else if (yMax < value)
						yMax = value;

					value = seg.getPoint(i).getZ();
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
	 * @return the internal set instance of this.
	 */
	private Set getSet() {
		return this.set3d;
	}

}
