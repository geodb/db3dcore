package de.uos.igf.db3d.dbms.spatials.api;

import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DElement;

/**
 * Interface defining common methods for all net element objects in 3D.
 */
public interface Element3D extends Spatial3D {

	/**
	 * Returns the identifier of this.
	 * 
	 * @return int - id.
	 */
	public int getID();

	/**
	 * Returns the Component3D aggregation object.
	 * 
	 * @return Component3D.
	 */
	public Component3D getComponent();

	/**
	 * Tests if this has a neighbour element at given index
	 * 
	 * @param index
	 *            int index
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasNeighbour(int index);

	/**
	 * Tests if this has neighbour elements at all.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean hasNeighbours();

	/**
	 * Returns the neighbours element
	 * 
	 * 
	 * @return Element3D[] - array of neighbours.
	 */
	public Element3D[] getNeighbours();

	/**
	 * Returns the neighbour element at given index
	 * 
	 * @param index
	 *            int index
	 * 
	 * @return Element3D - ordered as explained.
	 */
	public Element3D getNeighbour(int index);

	/**
	 * Tests if this is an interior element - it has two neighbours.
	 * 
	 * @return boolean - true if, false otherwise.
	 */
	public boolean isInterior();

	/**
	 * Returns the Point3D array of this.<br>
	 * 
	 * @return Point3D array.
	 */
	public Point3D[] getPoints();

}
