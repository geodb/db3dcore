/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;

/**
 * Represents a set of Triangles in 3D.
 */
public class TriangleSet3D implements SimpleGeoObj {

	/* internal set instance */
	private transient final Set set3d;

	/** constant for SegmentSet3D with Strict_Equal equality */
	public static final int STRICT_EQUAL = Equivalentable.STRICT_EQUAL;

	/** constant for SegmentSet3D with Geometry_Equal equality/equivalence */
	public static final int GEOMETRY_EQUAL = Equivalentable.GEOMETRY_EQUIVALENT;

	/**
	 * Constructor.<br>
	 * Constructs a transient TriangleSet3D.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public TriangleSet3D(ScalarOperator sop, int constant) {
		set3d = new EquivalentableHashSet(sop, constant);
	}

	/**
	 * Constructor.<br>
	 * Constructs a transient TriangleSet3D.
	 * 
	 * @param initialCapacity
	 *            initial capacity of set
	 * @param sop
	 *            ScalarOperator
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public TriangleSet3D(int initialCapacity, ScalarOperator sop, int constant) {
		set3d = new EquivalentableHashSet(initialCapacity, sop, constant);
	}

	/**
	 * Constructor.<br>
	 * Constructs a TriangleSet3D with given Set.<br>
	 * The elements in the Set must be of typeTriangle3D or subclasses.
	 * 
	 * @param initialSet
	 *            Set of type Triangle3D
	 * @param sop
	 *            ScalarOperator
	 * @param constant
	 *            int constant for SegmentSet3D
	 */
	public TriangleSet3D(Set initialSet, ScalarOperator sop, int constant) {
		EquivalentableHashSet hset = new EquivalentableHashSet(initialSet
				.size(), sop, constant);
		Iterator it = initialSet.iterator();
		while (it.hasNext())
			hset.add(it.next());

		this.set3d = hset;
	}

	/**
	 * Constructor.<br>
	 * Constructs a TriangleSet3D with given Set.<br>
	 * The elements in the Set must be of type Triangle3D or subclasses.
	 * 
	 * @param initialSet
	 *            EquivalentableHashSet of type Triangle3D
	 */
	public TriangleSet3D(EquivalentableHashSet initialSet) {
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
	 * @return boolean - true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return getSet().isEmpty();
	}

	/**
	 * Returns <code>true</code> if this set contains the specified element.
	 * 
	 * @param obj
	 *            Triangle3D to test
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean contains(Triangle3D obj) {
		return getSet().contains(obj);
	}

	/**
	 * Returns an array containing all of the elements in this set.
	 * 
	 * @return Triangle3D[] with all elements of this.
	 */
	public Triangle3D[] toArray() {
		return (Triangle3D[]) getSet().toArray(new Triangle3D[getSet().size()]);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param obj
	 *            Triangle3D to be added.
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Triangle3D obj) {
		return getSet().add(obj);
	}

	/**
	 * Adds the specified elements to this set if they are not already present.
	 * 
	 * @param obj
	 *            Triangle3D[] to be added
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean add(Triangle3D[] obj) {
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
	 *            Triangle3D to be removed
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean remove(Triangle3D obj) {
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
	 *            TriangleSet3D with elements to be inserted into this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean addAll(TriangleSet3D pointset) {
		return getSet().addAll(pointset.getAsSet());
	}

	/**
	 * Removes all elements in this result set that are also contained in the
	 * specified result set.
	 * 
	 * @param pointset
	 *            TriangleSet3D with elements to be removed from this
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean removeAll(TriangleSet3D pointset) {
		return getSet().removeAll(pointset.getAsSet());
	}

	/**
	 * Retains only the elements in this result set that are contained in the
	 * specified result set.
	 * 
	 * @param pointset
	 *            TriangleSet3D with elements to be retained in this.
	 * @return boolean - true if this collection changed as a result of the
	 *         call.
	 */
	public boolean retainAll(TriangleSet3D pointset) {
		return getSet().retainAll(pointset.getAsSet());
	}

	// iterator

	/**
	 * Returns an iterator over all Triangle3D objects in the set.
	 * 
	 * @return Iterator over elements of this.
	 */
	public Iterator<Triangle3D> iterator() {
		return getSet().iterator();
	}

	// for compatibility

	/**
	 * Returns a Set interface for given TriangleSet3D for compatibility issues.
	 * 
	 * @return Set - the internal set instance of this.
	 */
	public Set getAsSet() {
		return this.set3d;
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return TRIANGLESET3D always.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.TRIANGLESET3D;
	}

	/**
	 * Returns the MBB of this.
	 * 
	 * @return MBB of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @see db3d.dbms.structure.GeoObj#getMBB()
	 */
	public MBB3D getMBB() {
		if (size() > 0) {
			Iterator<Triangle3D> it = this.iterator();
			it.hasNext();
			Triangle3D tri = it.next();

			double xMin = tri.getPoint(0).getX();
			double yMin = tri.getPoint(0).getY();
			double zMin = tri.getPoint(0).getZ();
			for (int i = 1; i < 3; i++) {
				if (xMin > tri.getPoint(i).getX())
					xMin = tri.getPoint(i).getX();
				if (xMin > tri.getPoint(i).getY())
					xMin = tri.getPoint(i).getY();
				if (xMin > tri.getPoint(i).getZ())
					xMin = tri.getPoint(i).getZ();
			}
			double xMax = xMin;
			double yMax = yMin;
			double zMax = zMin;

			double value;
			while (it.hasNext()) {
				tri = (Triangle3D) it.next();

				for (int i = 0; i < 3; i++) {

					value = tri.getPoint(i).getX();
					if (xMin > value)
						xMin = value;
					else if (xMax < value)
						xMax = value;

					value = tri.getPoint(i).getY();
					if (yMin > value)
						yMin = value;
					else if (yMax < value)
						yMax = value;

					value = tri.getPoint(i).getZ();
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
	 * @return Set - internal set instance of this.
	 */
	private Set getSet() {
		return this.set3d;
	}

}
