package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;

import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.api.Hull3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Tetrahedron3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;

/**
 * ClosedHull3D models a BoundaryVolume3D object based on closed TriangleNets.
 * This class is based on the TriangleNet3D implementations and adds
 * functionality for volume operations and closed constraints checking.
 * 
 * @author Markus Jahn
 */
public class ClosedHull3DNet extends Triangle3DNet implements Hull3D {

	/* closed flag */
	private boolean closed;

	/**
	 * Constructor.
	 * 
	 * @param tnet
	 *            TriangleNet3D to wrap
	 * @throws UpdateException
	 */
	public ClosedHull3DNet(Triangle3DNet tnet) throws UpdateException {
		super(tnet.getGeoEpsilon());
		this.closed = false;
		this.components = new ClosedHull3DComponent[tnet.components.length];
		for (int i = 0; i < this.components.length; i++) {
			this.components[i] = new ClosedHull3DComponent(tnet.components[i]);
			closed = closed
					&& ((ClosedHull3DComponent) this.components[i]).isClosed();
		}
	}

	/**
	 * Copy Constructor.
	 * 
	 * @param chull
	 *            ClosedHull3D to copy.
	 * @throws UpdateException
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public ClosedHull3DNet(ClosedHull3DNet chull) throws UpdateException {
		this(new Triangle3DNet(chull));
	}

	/**
	 * Tests whether this closed hull object is currently closed.
	 * 
	 * @return boolean - true if this is closed, false otherwise.
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Calculates the volume of the wrapped triangle net of this.
	 * 
	 * @return double - volume of the triangle net.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @see db3d.dbms.model3d.Volume3D#getVolume()
	 */
	public double getVolume() {

		double volume = 0;
		for (int i = 0; i < countComponents(); i++)
			volume = volume
					+ ((ClosedHull3DComponent) this.components[i]).getVolume();

		return volume;
	}

	/**
	 * Returns the data type of this geo-object as a constant number.
	 * 
	 * @return byte - data type of this regarded as a geo-object.
	 * @see db3d.dbms.structure.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.HULL_NET_C_E3D;
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param comp
	 *            ClosedHull3DComp
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(ClosedHull3DComponent component) {
		component.id = this.components.length;
		ClosedHull3DComponent[] temp = new ClosedHull3DComponent[this.components.length + 1];
		for (int i = 0; i < this.components.length; i++)
			temp[i] = (ClosedHull3DComponent) this.components[i];
		temp[this.components.length] = component;
		component.net = this;
		this.components = temp;
		this.updateMBB();
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param component
	 *            TriangleNet3DComp to be added
	 * @throws UpdateException
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(Triangle3DComponent component) {
		try {
			this.addComponent(new ClosedHull3DComponent(component));
		} catch (UpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * point.
	 * 
	 * @param point
	 *            Point3D for test
	 * @return boolean - true if the given point is strictly contained in the
	 *         triangle net, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Point3D)
	 */
	public boolean containsInside(Point3D point) {

		for (int i = 0; i < this.countComponents(); i++)
			if (((ClosedHull3DComponent) this.components[i])
					.containsInside(point))
				return true;
		return false;
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * segment.
	 * 
	 * @param seg
	 *            Segment3D for test
	 * @return boolean - true if the given segment is strictly contained in the
	 *         triangle net, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Segment3D)
	 */
	public boolean containsInside(Segment3D seg) {

		for (int i = 0; i < this.countComponents(); i++)
			if (((ClosedHull3DComponent) this.components[i])
					.containsInside(seg))
				return true;
		return false;
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * tetrahedron.
	 * 
	 * @param tetra
	 *            Tetrahedron3D for test
	 * @return boolean - true if the given tetrahedron is strictly contained in
	 *         the triangle net, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Tetrahedron3D)
	 */
	public boolean containsInside(Tetrahedron3D tetra) {

		for (int i = 0; i < this.countComponents(); i++)
			if (((ClosedHull3DComponent) this.components[i])
					.containsInside(tetra))
				return true;
		return false;
	}

	/**
	 * Tests if the wrapped triangle net of this strictly contains the given
	 * triangle.
	 * 
	 * @param triangle
	 *            Triangle3D for test
	 * @return boolean - true if the given triangle is strictly contained in the
	 *         triangle net, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Triangle3D)
	 */
	public boolean containsInside(Triangle3D triangle) {

		for (int i = 0; i < this.countComponents(); i++)
			if (((ClosedHull3DComponent) this.components[i])
					.containsInside(triangle))
				return true;
		return false;
	}

}
