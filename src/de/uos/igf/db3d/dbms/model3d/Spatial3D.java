/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;

/**
 * Interface for the spatial object part of an Object3D
 */
public interface Spatial3D {

	/** constants for a Sample3D object */
	public static final byte SAMPLE_3D = 1;

	/** constants for a Curve3D object */
	public static final byte CURVE_3D = 2;

	/** constants for a Surface3D object */
	public static final byte SURFACE_3D = 3;

	/** constants for a Hull3D object */
	public static final byte HULL_3D = 4;

	/** constants for a Solid3D object */
	public static final byte SOLID_3D = 5;

	/** constants for a Grid3D object */
	public static final byte GRID_3D = 6;

	/**
	 * Returns the ScalarOperator of this.
	 * 
	 * @return ScalarOperator.
	 */
	public ScalarOperator getScalarOperator();

	/**
	 * Returns the MBB3D of this.
	 * 
	 * @return MBB3D.
	 */
	public MBB3D getMBB();

	/**
	 * Returns the Object3D aggregation object.
	 * 
	 * @return Object3D.
	 */
	public Object3D getObject3D();

	/**
	 * Test whether this intersects with the given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) throws DB3DException;

	/**
	 * Test whether this intersects with the given line.
	 * 
	 * @param line
	 *            Line3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Line3D line);

	/**
	 * Test whether this intersects with the given bounding box.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(MBB3D mbb);

	/**
	 * Returns the number of components in the net.
	 * 
	 * @return int - number of components.
	 */
	public int countComponents();

	/**
	 * Returns the number of elements in the net.
	 * 
	 * @return int - number of elements.
	 */
	public int countElements();

	/**
	 * Marks the begin of an update period.<br>
	 * During an update period insertion and removal of new elements to the
	 * components of the net are made without updating the mbbs and indexes
	 * every time. With the call to endUpdate the update routines will take
	 * place.
	 */
	public void beginUpdate();

	/**
	 * Marks the end of an update.<br>
	 * Resets the update flag and begins updating the net.
	 */
	public void endUpdate();

	/**
	 * Tests whether this net object is in an update phase.
	 * 
	 * @return boolean - true if, else otherwise.
	 */
	public boolean isUpdate();

	/**
	 * Returns the type of this object specified by constants
	 * 
	 * @return byte - as specified by constants.
	 */
	public byte getSpatial3DType();

	public ComplexGeoObj getComponent(int id);
}
