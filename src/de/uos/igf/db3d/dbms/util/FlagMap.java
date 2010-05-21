/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a FlagMap.<br>
 * After creation one can choose one of the two operation modes:<br>
 * If only the check for is in or not needed, then use ONLY the methods marked
 * as SINGLE_FLAG.<br>
 * If the check for different flags is needed, then use ONLY the methods marked
 * as MULTI_FLAG.<br>
 * 
 * @author Wolfgang Baer
 */
public final class FlagMap {

	/** flag 1 constant */
	public static final short F1 = 1;

	/** flag 2 constant */
	public static final short F2 = 2;

	/** flag 3 constant */
	public static final short F3 = 4;

	/** flag 4 constant */
	public static final short F4 = 8;

	/** flag 5 constant */
	public static final short F5 = 16;

	/** flag 6 constant */
	public static final short F6 = 32;

	/** flag 7 constant */
	public static final short F7 = 64;

	/** flag 8 constant */
	public static final short F8 = 128;

	/** flag 9 constant */
	public static final short F9 = 256;

	/** flag 10 constant */
	public static final short F10 = 512;

	/** flag 11 constant */
	public static final short F11 = 1024;

	/** flag 12 constant */
	public static final short F12 = 2048;

	/** flag 13 constant */
	public static final short F13 = 4096;

	/** flag 14 constant */
	public static final short F14 = 8192;

	/** flag 15 constant */
	public static final short F15 = 16384;

	/* the flag map */
	private final Map<Object, ShortValue> map;

	/**
	 * Constructor. Creates a new FlagMap.
	 */
	public FlagMap() {
		// this.map = CollectionFactory.getIdentityHashMap();
		this.map = new HashMap();
	}

	// METHODS TO WORK WITH DIFFERENT FLAGS

	/**
	 * Sets the given flag on provided object.<br>
	 * If the object is not yet in the FlagMap, it will be added.<br>
	 * MULTI_FLAG
	 * 
	 * @param obj
	 *            Object on which the flag should be set
	 * @param flag
	 *            short constant for flag
	 */
	public void setFlag(Object obj, short flag) {
		if (this.map.containsKey(obj))
			this.map.get(obj).setFlag(flag);
		else {
			ShortValue s = new ShortValue();
			s.setFlag(flag);
			this.map.put(obj, s);
		}
	}

	/**
	 * Clears the given flag on provided object.<br>
	 * If the object is not yet in the FlagMap, it will not be cleared.<br>
	 * MULTI_FLAG
	 * 
	 * @param obj
	 *            Object to be cleared
	 * @param flag
	 *            short constant for flag
	 */
	public void clearFlag(Object obj, short flag) {
		if (this.map.containsKey(obj))
			this.map.get(obj).clearFlag(flag);
	}

	/**
	 * Checks if the given flag on provided object is set.<br>
	 * If the object is not yet in the FlagMap, this method returns false.<br>
	 * MULTI_FLAG
	 * 
	 * @param obj
	 *            Object to be checked
	 * @param flag
	 *            short constant for flag
	 * @return boolean - true if the flag is set, false otherwise.
	 */
	public boolean checkFlag(Object obj, short flag) {
		if (this.map.containsKey(obj))
			return this.map.get(obj).checkFlag(flag);
		else
			return false;
	}

	// METHOD IF ONLY ONE FLAG IS NEEDED

	/**
	 * Adds the object to the flag map.<br>
	 * SINGLE_FLAG
	 * 
	 * @param obj
	 *            Object to be added
	 */
	public void add(Object obj) {
		this.map.put(obj, null);
	}

	/**
	 * Checks if the obj is contained in flag map.<br>
	 * SINGLE_FLAG
	 * 
	 * @param obj
	 *            Object to be checked
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean check(Object obj) {
		return this.map.containsKey(obj);
	}

	/**
	 * Removes the object from the flag map.<br>
	 * SINGLE_FLAG
	 * 
	 * @param obj
	 *            Object to be removed
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public void remove(Object obj) {
		this.map.remove(obj);
	}

	/**
	 * Resets the complete FlagMap.<br>
	 * SINGLE_FLAG & MULTI_FLAG This method removes also all Objects from the
	 * map.
	 */
	public void clearFlagMap() {
		this.map.clear();
	}

	/**
	 * Returns the size of the flag map.
	 * 
	 * @return int - size of the flag map.
	 */
	public int size() {
		return this.map.size();
	}

	private final class ShortValue {
		private short value;

		private ShortValue() {
			this.value = 0;
		}

		private void setFlag(short flag) {
			this.value = (short) (this.value | flag);
		}

		private void clearFlag(short flag) {
			this.value = (short) (this.value & (~flag));
		}

		private boolean checkFlag(short flag) {
			return (this.value & flag) == flag;
		}
	}
}
