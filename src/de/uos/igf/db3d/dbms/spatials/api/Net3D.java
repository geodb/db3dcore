package de.uos.igf.db3d.dbms.spatials.api;

import de.uos.igf.db3d.dbms.exceptions.DB3DException;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;

/**
 * 
 * Interface for a spatial Net3D part of a Cell
 * 
 * @author Markus Jahn
 * 
 */
public interface Net3D extends Spatial3D {

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

	public Component3D getComponent(int id);

	public Component3D[] getComponents();

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

}
