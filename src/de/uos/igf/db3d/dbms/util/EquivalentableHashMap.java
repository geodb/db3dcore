/*
 * Copyright (C) Prof. Martin Breunig
 */


package de.uos.igf.db3d.dbms.util;

import java.util.Map;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;

/**
 * Implementation of the Map interface as a HashMap for objects implementing the
 * Equivalentable interface.<br>
 * <br>
 * All <b>key</b> objects put into this Map implementation must be of type
 * Equivalentable, otherwise all operations will fail with unexpected results or
 * exceptions !!!!<br>
 * <br>
 * All constructors need a ScalarOperator for the equality/equivalence tests
 * shared by all the objects in this Map and a constant from interface
 * Equivalentable indicating which equality/equivalence test in this Map
 * implementation should be used !
 */
public final class EquivalentableHashMap extends AbstractHashMap {

	/* scalar operator for equality/equivalence tests */
	private final ScalarOperator sop;

	/* constant from the Equivalentable interface */
	private byte equConst = -2;

	/* factor used for hash code computing */
	private final int factor;

	/**
	 * Default constructor.
	 * 
	 * @param _sop
	 *            ScalarOperator for equality/equivalence tests
	 * @param equivalConstant
	 *            constant from the Equivalentable interface.<br>
	 *            Specifies the used equality/equivalence method used in this
	 *            class.
	 */
	public EquivalentableHashMap(ScalarOperator _sop, int equivalConstant) {
		super();
		this.sop = _sop;
		this.equConst = (byte) equivalConstant;
		this.factor = computeFactor(this.sop);
	}

	/**
	 * Constructor.
	 * 
	 * @param initialCapacity
	 * @param _sop
	 *            ScalarOperator for equality/equivalence tests
	 * @param equivalConstant
	 *            constant from the Equivalentable interface.<br>
	 *            Specifies the used equality/equivalence method used in this
	 *            class.
	 */
	public EquivalentableHashMap(int initialCapacity, ScalarOperator _sop,
			int equivalConstant) {
		super(initialCapacity);
		this.sop = _sop;
		this.equConst = (byte) equivalConstant;
		this.factor = computeFactor(this.sop);
	}

	/**
	 * Constructor.
	 * 
	 * @param initialCapacity
	 *            see HashMap
	 * @param loadFactor
	 *            see HashMap
	 * @param _sop
	 *            ScalarOperator for equality/equivalence tests
	 * @param equivalConstant
	 *            constant from teh Equivalentable interface.<br>
	 *            Specifies the used equality/equivalence method used in this
	 *            class.
	 */
	public EquivalentableHashMap(int initialCapacity, float loadFactor,
			ScalarOperator _sop, int equivalConstant) {
		super(initialCapacity, loadFactor);
		this.sop = _sop;
		this.equConst = (byte) equivalConstant;
		this.factor = computeFactor(this.sop);
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param m
	 *            Map
	 * @param _sop
	 *            ScalarOperator for equality/equivalence tests
	 * @param equivalConstant
	 *            constant from teh Equivalentable interface.<br>
	 *            Specifies the used equality/equivalence method used in this
	 *            class.
	 */
	public EquivalentableHashMap(Map m, ScalarOperator _sop, int equivalConstant) {
		super();
		this.sop = _sop;
		this.equConst = (byte) equivalConstant;
		this.factor = computeFactor(this.sop);
		putAll(m);
	}

	/**
	 * Tests the equality of two Object keys
	 * 
	 * @param key
	 *            Object 1
	 * @param key2
	 *            Object 2
	 * @return boolean - true if equal, false otherwise.
	 * @throws DB3DException
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @see db3d.dbms.util.AbstractHashMap#equalityTest(java.lang.Object,
	 *      java.lang.Object)
	 */
	protected boolean equalityTest(Object key, Object key2) {
		switch (this.equConst) {
		case Equivalentable.GEOMETRY_EQUIVALENT:
			return ((Equivalentable) key).isGeometryEquivalent(
					(Equivalentable) key2, this.sop);
		case Equivalentable.STRICT_EQUAL:
			return ((Equivalentable) key).isEqual((Equivalentable) key2,
					this.sop);
		default:
			return false;
		}
	}

	/**
	 * Returns the hash code for the given key object.
	 * 
	 * @param key
	 *            Object to perform as key in the HashMap
	 * @return int - hash code.
	 * @see db3d.dbms.util.AbstractHashMap#hashOfObject(java.lang.Object)
	 */
	protected int hashOfObject(Object key) {
		switch (this.equConst) {
		case Equivalentable.GEOMETRY_EQUIVALENT:
			return ((Equivalentable) key).isGeometryEquivalentHC(this.factor);
		case Equivalentable.STRICT_EQUAL:
			return ((Equivalentable) key).isEqualHC(this.factor);
		default:
			return Integer.MAX_VALUE;
		}
	}

	/*
	 * Computes the factor used for rounding during hash code computing.
	 * 
	 * @param _sop ScalarOperator for equality/equivalence tests
	 * 
	 * @return int - the factor for rounding.
	 */
	private int computeFactor(ScalarOperator _sop) {
		String epsilon = Double.toString(_sop.getEpsilon());
		int k = epsilon.lastIndexOf("E-");
		int factor1;
		if (k != -1)
			factor1 = (int) Math.pow(10, Integer.parseInt(epsilon.substring(
					k + 2, epsilon.length())) - 1);
		else
			factor1 = (int) Math.pow(10, (epsilon.lastIndexOf('1') - 2));

		return factor1;
	}

}
