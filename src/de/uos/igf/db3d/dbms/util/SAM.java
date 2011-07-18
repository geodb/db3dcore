/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.util;

import java.io.Serializable;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.structure.GeoObj;

/**
 * The Interface SAM defines the methods used for spatial access in the
 * geodatabase. Spatial access methods designed to be used with the geodatabase
 * system have to implement this interface and all of its methods. All
 * implementations must be Serializable !
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public interface SAM extends Serializable {

	/**
	 * The Interface NNResult defines the methods for retrieving information
	 * from nearest neighbour search result objects - NNResult.
	 */
	public interface NNResult {

		/**
		 * Returns the distance of this result object to the query point used in
		 * the nearest neighbour search.
		 * 
		 * @return double - distance as double.
		 */
		public double getDistance();

		/**
		 * Returns the reference to the result object.
		 * 
		 * @return Object - reference to the result object.
		 */
		public Object getObjectRef();
	}

	/**
	 * Returns the number of objects in the SAM
	 * 
	 * @return int - number of objects.
	 */
	public int getCount();

	/**
	 * Returns the complete MBB3D of all objects in the SAM.
	 * 
	 * @return MBB3D - MBB3D of SAM.
	 */
	public MBB3D getMBB();

	/**
	 * Retrieves all entries in the SAM.
	 * 
	 * @return Set - all entries.
	 */
	public Set getEntries();

	/**
	 * Inserts an GeoObj into the SAM.
	 * 
	 * @param go
	 *            a GeoObj to be inserted
	 * @return boolean - true if insertion successful.
	 */
	public boolean insert(GeoObj go);

	/**
	 * Removes an GeoObj from the SAM.
	 * 
	 * @param go
	 *            an GeoObj to be removed
	 * @return boolean - true if removal successful.
	 */
	public boolean remove(GeoObj go);

	/**
	 * Returns the set of SpatialObject objects which <b>strictly</b> intersect
	 * the given MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set intersectsStrict(MBB3D mbb);

	/**
	 * Returns the set of SpatialObject objects which intersect the given MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set intersects(MBB3D mbb);

	/**
	 * Returns the set of SpatialObject objects which <b>strictly</b>contain the
	 * given MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set containsStrict(MBB3D mbb);

	/**
	 * Returns the set of SpatialObject objects which contain the given MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set contains(MBB3D mbb);

	/**
	 *Returns the set of SpatialObject objects which are <b>strictly</b> inside
	 * the given MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set insideStrict(MBB3D mbb);

	/**
	 *Returns the set of SpatialObject objects which are inside the given
	 * MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set inside(MBB3D mbb);

	/**
	 * Returns the set of SpatialObject objects which contain the given point.
	 * 
	 * @param point
	 *            the Point3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set contains(Point3D point);

	/**
	 * Returns the set of the <code>number</code> SpatialObject objects which
	 * are the nearest neighbours of the given point.<br>
	 * If implementations will not support multiple nearest neighbour search, it
	 * should throw an IllegalArgumentException for <code>number</code> > 1.
	 * 
	 * @param number
	 *            number of nearest neighbours to search
	 * @param point
	 *            the Point3D object for test
	 * @return NNResult[] - an array of NNResult objects containing the result.
	 */
	public NNResult[] nearest(int number, Point3D point);

} // end SAM

