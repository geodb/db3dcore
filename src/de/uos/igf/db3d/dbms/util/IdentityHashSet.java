/*
 * Copyright (C) Prof. Martin Breunig
 */


package de.uos.igf.db3d.dbms.util;

import java.util.Collection;

/**
 * Implements a IdentityHashSet based on AbstractHashSet.<br>
 * For this Set the objects hash code computation and equality test is based on
 * object identity and not on the contents of the objects. <br>
 * This implementation breaks the contract of hash code and equals as given by
 * the Java Language specification !! <br>
 * <br>
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public final class IdentityHashSet extends AbstractHashSet {

	/**
	 * Constructs a new, empty set; the backing <code>AbstractHashMap</code>
	 * instance has default initial capacity (16) and load factor (0.75).
	 */
	public IdentityHashSet() {
		super();
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
	public IdentityHashSet(Collection c) {
		super(c);
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
	public IdentityHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new, empty set; the backing <code>AbstractHashMap</code>
	 * instance has the specified initial capacity and default load factor,
	 * which is <tt>0.75</tt>.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the hash table
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 */
	public IdentityHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Returns the hash code for the given key object
	 * 
	 * @param key
	 *            Object to perform as key in the HashMap
	 * @return int - hash code
	 * @see db3d.dbms.util.AbstractHashSet#hashOfObject(java.lang.Object)
	 */
	protected int hashOfObject(Object key) {
		return System.identityHashCode(key);
	}

	/**
	 * Tests the equality of two Object keys. Only the equality of pointers is
	 * tested !
	 * 
	 * @param key
	 *            Object 1
	 * @param key2
	 *            Object 2
	 * @return boolean - true if equal, false otherwise.
	 * @see db3d.dbms.util.AbstractHashMap#equalityTest(java.lang.Object,
	 *      java.lang.Object)
	 */
	protected boolean equalityTest(Object key, Object key2) {
		return key == key2;
	}

}
