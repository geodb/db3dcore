/*
 * Copyright (C) Prof. Martin Breunig
 */


package de.uos.igf.db3d.dbms.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * AbstractHashSet provides a HashSet where the derived class has to provide the
 * meaning of hash code and equality of the objects.<br>
 * This implementation is backed by AbstractHashMap for hashing.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public abstract class AbstractHashSet extends AbstractCollection implements
		Set, Cloneable {

	/* the backing AbstractHashMap instance */
	private transient DelegateAbstractHashMap map;

	/* dummy value to associate with an Object in the backing Map */
	private static final Object PRESENT = new Object();

	/**
	 * Constructs a new, empty set; the backing <code>AbstractHashMap</code>
	 * instance has default initial capacity (16) and load factor (0.75).
	 */
	public AbstractHashSet() {
		map = new DelegateAbstractHashMap(this);
	}

	/**
	 * Constructs a new set containing the elements in the specified collection.
	 * The <code>AbstractHashMap</code> is created with default load factor
	 * (0.75) and an initial capacity sufficient to contain the elements in the
	 * specified collection.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this set
	 * @throws NullPointerException
	 *             if the specified collection is null.
	 */
	public AbstractHashSet(Collection c) {
		map = new DelegateAbstractHashMap(Math.max((int) (c.size() / .75f) + 1,
				16), this);
		addAll(c);
	}

	/**
	 * Constructs a new, empty set; the backing <code>AbstractHashMap</code>
	 * instance has the specified initial capacity and the specified load
	 * factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the hash map
	 * @param loadFactor
	 *            the load factor of the hash map
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero, or if the load
	 *             factor is non-positive.
	 */
	public AbstractHashSet(int initialCapacity, float loadFactor) {
		map = new DelegateAbstractHashMap(initialCapacity, loadFactor, this);
	}

	/**
	 * Constructs a new, empty set; the backing <code>AbstractHashMap</code>
	 * instance has the specified initial capacity and default load factor,
	 * which is <code>0.75</code>.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the hash table
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 */
	public AbstractHashSet(int initialCapacity) {
		map = new DelegateAbstractHashMap(initialCapacity, this);
	}

	/**
	 * Returns the hash code for the given key object
	 * 
	 * @param key
	 *            Object to perform as key in the HashMap
	 * @return int - hash code
	 */
	protected abstract int hashOfObject(Object key);

	/**
	 * Returns the result of the equality test between the two given key
	 * objects.
	 * 
	 * @param key
	 *            Object 1
	 * @param key2
	 *            Object 2
	 * @return boolean - true if equal, false otherwise.
	 */
	protected abstract boolean equalityTest(Object key, Object key2);

	/**
	 * Returns an iterator over the elements in this set. The elements are
	 * returned in no particular order.
	 * 
	 * @return an Iterator over the elements in this set.
	 */
	public Iterator iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 * 
	 * @return the number of elements in this set (its cardinality).
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <code>true</code> if this set contains no elements.
	 * 
	 * @return <code>true</code> if this set contains no elements.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <code>true</code> if this set contains the specified element.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested.
	 * @return <code>true</code> if this set contains the specified element.
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param o
	 *            element to be added to this set
	 * @return <code>true</code> if the set did not already contain the
	 *         specified element.
	 */
	public boolean add(Object o) {
		return map.put(o, PRESENT) == null;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 * 
	 * @param o
	 *            object to be removed from this set, if present
	 * @return <code>true</code> if the set contained the specified element.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * Removes all of the elements from this set.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <code>AbstractHashSet</code> instance: the
	 * elements themselves are not cloned.
	 * 
	 * @return a shallow copy of this set.
	 */
	public Object clone() {
		try {
			AbstractHashSet newSet = (AbstractHashSet) super.clone();
			newSet.map = (DelegateAbstractHashMap) map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/*
	 * Class extending AbstractHashMap to include an AbstractHashSet
	 */
	private static final class DelegateAbstractHashMap extends AbstractHashMap {

		/* the AbstractHashSet */
		private AbstractHashSet set;

		/*
		 * Constructs a DelegateAbstractHashMap with the given capacity, load
		 * factor and an AbstrachHashSet.
		 */
		public DelegateAbstractHashMap(int initialCapacity, float loadFactor,
				AbstractHashSet set) {
			super(initialCapacity, loadFactor);
			this.set = set;
		}

		/*
		 * Constructs a DelegateAbstractHashMap with the given capacity and an
		 * AbstractHashSet.
		 */
		public DelegateAbstractHashMap(int initialCapacity, AbstractHashSet set) {
			super(initialCapacity);
			this.set = set;
		}

		/*
		 * Constructs a DelegateAbstractHashMap with an AbstractHashSet.
		 */
		public DelegateAbstractHashMap(AbstractHashSet set) {
			super();
			this.set = set;
		}

		/*
		 * Constructs a new map with the same mappings as the given map and an
		 * AbstractHashSet. The map is created with a capacity of twice the
		 * number of mappings in the given map or 11 (whichever is greater), and
		 * a default load factor, which is <code>0.75</code>.
		 */
		public DelegateAbstractHashMap(Map t, AbstractHashSet set) {
			super(t);
			this.set = set;
		}

		/**
		 * Returns the result of the equality test between the two given key
		 * objects.
		 * 
		 * @param key
		 *            Object 1
		 * @param key2
		 *            Object 2
		 * @return boolean - true if equal, false otherwise.
		 * @throws IllegalArgumentException
		 *             if the index of the point of the tetrahedron is not in
		 *             the interval [0;3]. The exception originates in the
		 *             method getPoint(int) of the class Tetrahedron3D.
		 * @see db3d.dbms.util.AbstractHashMap#equalityTest(java.lang.Object,
		 *      java.lang.Object)
		 */
		protected boolean equalityTest(Object key, Object key2) {
			return this.set.equalityTest(key, key2);
		}

		/**
		 * Sets the key to the given Object.
		 * 
		 * @param key
		 *            Object to which the key should be set
		 * 
		 * @see db3d.dbms.util.AbstractHashMap#hashOfObject(java.lang.Object)
		 */
		protected int hashOfObject(Object key) {
			return set.hashOfObject(key);
		}

	}

	// Problem with multiple implementations
	// /**
	// * Save the state of this <tt>AbstractHashSet</tt> instance to a stream
	// (that is,
	// * serialize this set).
	// *
	// * @serialData The capacity of the backing <tt>AbstractHashMap</tt>
	// instance
	// * (int), and its load factor (float) are emitted, followed by
	// * the size of the set (the number of elements it contains)
	// * (int), followed by all of its elements (each an Object) in
	// * no particular order.
	// */
	// private void writeObject(java.io.ObjectOutputStream s) throws
	// java.io.IOException {
	// // Write out any hidden serialization magic
	// s.defaultWriteObject();
	//
	// // Write out AbstractHashMap capacity and load factor
	// s.writeInt(map.capacity());
	// s.writeFloat(map.loadFactor());
	//
	// // Write out size
	// s.writeInt(map.size());
	//
	// // Write out all elements in the proper order.
	// for (Iterator i = map.keySet().iterator(); i.hasNext();)
	// s.writeObject(i.next());
	// }
	//
	// /**
	// * Reconstitute the <tt>AbstractHashSet</tt> instance from a stream (that
	// is,
	// * deserialize it).
	// */
	// private void readObject(java.io.ObjectInputStream s) throws
	// java.io.IOException, ClassNotFoundException {
	// // Read in any hidden serialization magic
	// s.defaultReadObject();
	//
	// // Read in AbstractHashMap capacity and load factor and create backing
	// AbstractHashMap
	// int capacity = s.readInt();
	// float loadFactor = s.readFloat();
	// map = (this instanceof LinkedHashSet ? new
	// LinkedAbstractHashMap(capacity, loadFactor) : new
	// AbstractHashMap(capacity, loadFactor));
	//
	// // Read in size
	// int size = s.readInt();
	//
	// // Read in all elements in the proper order.
	// for (int i = 0; i < size; i++) {
	// Object e = s.readObject();
	// map.put(e, PRESENT);
	// }
	// }

}
