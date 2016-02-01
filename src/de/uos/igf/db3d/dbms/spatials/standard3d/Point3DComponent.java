package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.exceptions.ContainmentException;
import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * PointNet3DComp represents a single point net component. All PointElt3D
 * objects in this object belong to one semantic component.<br>
 * For point nets with several components see @see PointNet3D. Serialization
 * (reference to enclosing net skipped) - WBaer 06082003
 * 
 * @author Markus Jahn
 * 
 */
public class Point3DComponent extends Component3DAbst {

	/**
	 * Constructor.<br>
	 * 
	 * @param epsilon
	 *            geometric error
	 */
	public Point3DComponent(GeoEpsilon epsilon) {
		super(epsilon);
	}

	/**
	 * Constructor.<br>
	 * Constructs a PointNet3DComp object with the given PointElt3D[].<br>
	 * In the given array the neighbourhood topology has not been defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this triangle
	 * array.
	 * 
	 * @param epsilon
	 *            geometric error
	 * @param elements
	 *            Point3D[]
	 * @throws UpdateException
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Point3DComponent(Point3DElement[] elements, GeoEpsilon epsilon)
			throws UpdateException {
		super(epsilon);
		for (Point3DElement element : elements) {
			this.addElement(element);
		}
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) { // Dag
		Set<Point3DElement> set = (Set<Point3DElement>) this
				.getElementsViaSAM();
		Point3DElement[] points = set.toArray(new Point3DElement[set.size()]);
		for (int i = 0; i < points.length; i++)
			if (points[i].intersects(plane, this.epsilon))
				return true;

		return false;
	}

	/**
	 * Test whether this intersects with given line.
	 * 
	 * @param line
	 *            Line3D to be tested
	 * @return boolean - true if intersects, false itherwise.
	 */
	public boolean intersects(Line3D line) { // Dag
		Set<Point3DElement> set = (Set<Point3DElement>) this
				.getElementsViaSAM();
		Point3DElement[] points = set.toArray(new Point3DElement[set.size()]);
		for (int i = 0; i < points.length; i++)
			if (points[i].intersects(line, this.epsilon))
				return true;

		return false;
	}

	/**
	 * Test whether this intersects with given bounding box.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(MBB3D mbb) {
		if (!mbb.intersects(this.getMBB(), this.epsilon))
			return false;

		Set<Point3DElement> set = (Set<Point3DElement>) this.sam
				.intersects(mbb);
		if (set.isEmpty())
			return false;

		return true;
	}

	/**
	 * Tests whether this geometrically contains the given point.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is cointained, false otherwise.
	 */
	public boolean contains(Point3D point) {
		return containsElement(point);
	}

	/**
	 * Creates a PointElt3D from given point and adds it to the component.
	 * 
	 * @param element
	 *            Point3D
	 * @return PointElt3D - the inserted instance.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Point3DElement addElement(Point3DElement element)
			throws UpdateException {
		if (this.containsElement(element))
			throw new ContainmentException("Element already contained !");

		element.id = this.verticeID++;

		this.sam.insert(element);
		// Here an IllegalArgumentException can be thrown.
		return element;
	}

	/**
	 * Removes the given element from the component.
	 * 
	 * @param elt
	 *            Point3D to be removed
	 * @return PointElt3D - the removed element.
	 * @throws UpdateException
	 *             - or subclass of it, signals an Update problem.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Point3DElement removeElement(Point3DElement element)
			throws UpdateException {
		// find element
		Point3DElement removable = null;
		Set<Point3DElement> set = (Set<Point3DElement>) this.sam
				.intersects(element.getMBB());
		Iterator<Point3DElement> it = set.iterator();
		while (it.hasNext()) {
			Point3DElement current = it.next();
			if (current.isGeometryEquivalent(element, this.epsilon)) {
				removable = current;
				break;
			}
		}
		if (removable != null) {

			/*
			 * Working with the retrieved removable element implies that the
			 * remove method which is currently based on equals comparison
			 * removes the element correctly.
			 */
			this.sam.remove(removable);
			// Here an IllegalArgumentException can be thrown.

			return removable;
		}
		// else
		throw new ContainmentException("Element not contained !");
	}

	/**
	 * Tests whether a element with the coordinates of given point is contained
	 * in the component.<br>
	 * Identity test based on epsilon equality on coordinates !<br>
	 * In the case of a point net this test is equal to a geometric test of
	 * containment !<br>
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean containsElement(Point3D point) {
		Set<Point3DElement> set = (Set<Point3DElement>) this.sam
				.intersects(point.getMBB());
		Iterator<Point3DElement> it = set.iterator();
		while (it.hasNext()) {
			Point3DElement pointelt = it.next();
			if (pointelt.isGeometryEquivalent(point, this.epsilon))
				return true;
		}
		return false;
	}

	/**
	 * Computes and returns the Euler number for this component.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() {
		/*
		 * Euler formula: vertices - edges + faces number of PointElt3D elements
		 * = vertices = Euler
		 */
		return (this.vertices.size());
	}

	/**
	 * Returns the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SAMPLE_COMPONENT_E3D;
	}

	@Override
	public Point3DElement getElement(int id) {
		Set<Point3DElement> set = (Set<Point3DElement>) this
				.getElementsViaSAM();
		Iterator<Point3DElement> it = set.iterator();
		while (it.hasNext()) {
			Point3DElement p = it.next();
			if (p.id == id)
				return p;
		}
		return null;
	}

}
