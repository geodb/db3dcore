/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

/**
 * Interface for geometry classes based on the reference to one or more Point3D
 * objects. There are different ways to test for equality to equivalence.
 * Classes implementing this interface has to provide the specified test
 * methods.
 * 
 * @author Wolfgang Baer
 * @author Bjoern Schilberg
 * @author Paul Vincent Kuper
 * @author Edgar Butwilowski
 */
public interface Equivalentable {

	/** constant for use of strict equality test */
	public final static byte STRICT_EQUAL = 1;

	/** constant for use of a geometry equivalence test */
	public final static byte GEOMETRY_EQUIVALENT = 2;

	/**
	 * Strict equal test.<br>
	 * All the points must be equal in the same index (not in case of Point3D).<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean isEqual(Equivalentable obj, ScalarOperator sop);

	/**
	 * Computes the corresponding hash code for isEqual usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 */
	public int isEqualHC(int factor);

	/**
	 * Geometry equivalence test.<br>
	 * The objects must have the same points, but the index position makes no
	 * difference.<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean isGeometryEquivalent(Equivalentable obj, ScalarOperator sop);

	/**
	 * Computes the corresponding hash code for isGeometryEquivalent usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 */
	public int isGeometryEquivalentHC(int factor);

	/**
	 * Tests if given object is the same class type.
	 * 
	 * @param obj
	 *            Object
	 * @return boolean - true if this and the given object are of the same class
	 *         type.
	 */
	public boolean isEqualClass(Object obj);

}
