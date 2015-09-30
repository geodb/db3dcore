package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D.GEOMETRYTYPES;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Tetrahedron3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Vector3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * ClosedHull3DComp represents a single ClosedHull component. All TriangleElt3D
 * objects in this object belong to one semantic component.
 * 
 * @author Markus Jahn
 * 
 */
public class ClosedHull3DComponent extends Triangle3DComponent {

	/* closed flag */
	private boolean closed;

	/**
	 * Constructor.
	 * 
	 * @param tNetComp
	 *            TriangleNet3DComp to wrap
	 * @throws UpdateException
	 */
	public ClosedHull3DComponent(Triangle3DComponent tNetComp)
			throws UpdateException {
		super(tNetComp.getElementsViaSAM().toArray(new Triangle3DElement[] {}),
				tNetComp.getGeoEpsilon());
		this.checkClosed();
	}

	/**
	 * Tests if <code>point</code> is strictly contained.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is strictly contained, false otherwise.
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
	 */
	public boolean containsStrictInside(Point3D point) {

		if (!this.isOrientationConsistent())
			this.makeOrientationConsistent(this.epsilon);

		if (!(point.getMBB().inside(this.mbb, this.epsilon)))
			return false;

		// point in border
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(point.getMBB());
		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement tri = it.next();
			if (tri.contains(point, this.epsilon))
				return false;
		}

		return isInside(point);
	}

	/**
	 * Tests if <code>point</code> is (not strictly) contained.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * 
	 * @return boolean - true if is contained, false otherwise.
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

		if (!this.isOrientationConsistent())
			this.makeOrientationConsistent(this.epsilon);

		if (!(point.getMBB().inside(this.mbb, this.epsilon)))
			return false;

		// point in border
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(point.getMBB());
		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement tri = it.next();
			if (tri.contains(point, this.epsilon))
				return true;
		}

		return isInside(point);
	}

	/**
	 * Tests if the point is inside the border. The algorithm based on the
	 * "count ray crossing method".
	 * 
	 * @param point
	 *            Point3D to be tested
	 * 
	 * @return boolean - true if the point is inside the border, false
	 *         otherwise.
	 * 
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * 
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * 
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	private boolean isInside(Point3D point) {

		// x-distances to mbb border
		double dMin = point.getCoord(0) - this.mbb.getPMin().getCoord(0);
		double dMax = this.mbb.getPMax().getCoord(0) - point.getCoord(0);

		if (this.epsilon.equal(0, dMax) || this.epsilon.equal(0, dMin)) {
			Set<Triangle3DElement> set = (Set<Triangle3DElement>) this.sam
					.intersects(point.getMBB());
			Iterator<Triangle3DElement> it = set.iterator();
			while (it.hasNext()) {
				Triangle3DElement tri = (Triangle3DElement) it.next();
				if (tri.contains(point, epsilon))
					return true;
			}
			return false;
		}

		double dist = dMax;

		// construct ray parallel to x-axes and shortest distance to mbb border
		Segment3D ray;
		Point3D to;
		if (dMax <= dMin) {
			to = new Point3D(this.getMBB().getPMax());
		} else {
			to = new Point3D(this.getMBB().getPMin());
			dist = dMin;
		}
		to.setCoord(1, point.getCoord(1));
		to.setCoord(2, point.getCoord(2));
		ray = new Segment3D(point, to, epsilon);

		// find closest triangle intersecting with ray
		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(ray.getMBB());
		Iterator<Triangle3DElement> it = triangles.iterator();
		Triangle3DElement closest = null;
		Point3D pt = null;
		while (it.hasNext()) {
			Triangle3DElement tri = it.next();
			Geometry3D sgo = tri.intersection(ray, epsilon);
			if (sgo != null) {
				if (sgo.getGeometryType() == GEOMETRYTYPES.POINT) {
					Point3D p = (Point3D) sgo;
					if (p.euclideanDistance(point) <= dist) {
						dist = p.euclideanDistance(point);
						closest = tri;
						pt = p;
					}
				}
			}
		}

		if (closest == null)
			return false;

		if (!closest.containsInBorder(pt, epsilon)) {
			return isInside(point, pt, closest);
		}
		return isInside(point, closest);
	}

	/**
	 * Checks if a virtual line from <code>point</code> to the center of
	 * <code>tri</code> intersects with other triangles. if not inside/outside
	 * is tested. Returns true if <code>point</code> is inside, false if
	 * outside.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * 
	 * @param tri
	 *            TriangleElt3D to be tested
	 * 
	 * @return boolean - inside/outside.
	 * 
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * 
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * 
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	private boolean isInside(Point3D point, Triangle3DElement tri) {

		Point3D center = tri.getCenter();
		Segment3D seg = new Segment3D(point, center, this.epsilon);

		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(seg.getMBB());
		triangles.remove(tri);

		Iterator<Triangle3DElement> it = triangles.iterator();
		while (it.hasNext()) {
			Triangle3DElement triangle = it.next();
			Geometry3D sgo = triangle.intersection(seg, this.epsilon);
			if (sgo != null) {
				if (sgo.getGeometryType() == GEOMETRYTYPES.POINT) {
					Point3D p = (Point3D) sgo;
					if (p.euclideanDistance(point) < seg.getLength()) {
						return isInside(point, triangle);
					}
				}
			}
		}
		return isInside(point, center, tri);
	}

	/**
	 * Tests if <code>point</code> lies left/right from <code>tri</code>,
	 * inside/outside respectively. <code>p</code> is given point on inner of
	 * <code>tri</code>. normal of tri specifies outside direction. Returns true
	 * if inside, false if outside.
	 * 
	 * @param from
	 *            Point3D, start of the vector
	 * 
	 * @param to
	 *            Point3D, end of the vectors
	 * 
	 * @param tri
	 *            TriangleElt3D to be compared
	 * 
	 * @return boolean - inside/outside.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * 
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	private boolean isInside(Point3D from, Point3D to, Triangle3DElement tri) {

		Vector3D pointToTri = new Vector3D(from, to);
		if (pointToTri.scalarproduct(tri.getNormal(this.getGeoEpsilon())) > 0)
			return true;
		return false;
	}

	/**
	 * Tests if <code>segment</code> is contained.
	 * 
	 * @param segment
	 *            Segment3D to be tested
	 * 
	 * @return boolean - true if is contained, false otherwise.
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
	 * @see db3d.dbms.model3d.Volume3D#containsInside(db3d.dbms.geom.Segment3D)
	 */
	public boolean containsInside(Segment3D segment) {

		Point3D[] points = segment.getPoints();

		for (int i = 0; i < 2; i++)
			if (!this.containsInside(points[i]))
				return false;

		Set<Triangle3DElement> triangles = (Set<Triangle3DElement>) this.sam
				.intersects(segment.getMBB());

		if (!triangles.isEmpty()) {
			Iterator<Triangle3DElement> it = triangles.iterator();
			while (it.hasNext()) {
				Triangle3DElement tri = it.next();
				Geometry3D sgo = tri.intersection(segment, this.epsilon);
				if (sgo != null) {

					/*
					 * TODO ClosedHull_containsInside( segment ) totally
					 * contained ? to simply return false here is not correct,
					 * "touches" from inside are possible.
					 * 
					 * Touches/intersections with the inside of the segment
					 * should be checked. It could be also possible that two
					 * nodes of the segment are on the hull but the inside of
					 * the segment is outside.
					 */

					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Tests if <code>triangle</code> is contained.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if is contained, false otherwise.
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

		Point3D[] points = triangle.getPoints();

		for (int i = 0; i < 3; i++)
			if (!this.containsInside(points[i]))
				return false;

		// TODO ClosedHull_containsInside( triangle ) totally contained ?
		return true;
	}

	/**
	 * Tests if <code>tetrahedron</code> is contained.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if is contained, false otherwise.
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

		Point3D[] points = tetra.getPoints();

		for (int i = 0; i < 4; i++)
			if (!this.containsInside(points[i]))
				return false;

		// TODO ClosedHull_containsInside( tetra ) totally contained ?
		return true;
	}

	/**
	 * Returns the type of this as a ComplexGeoObj
	 * 
	 * @return byte - type of this.
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.HULL_COMPONENT_E3D;
	}

	/**
	 * Tests via algorithm if the component is really closed.
	 * 
	 * @return boolean - true if is closed, false otherwise.
	 */
	public void checkClosed() {
		Iterator<Triangle3DElement> it = this.getElementsViaRecursion()
				.iterator();
		while (it.hasNext()) {
			if (it.next().countNeighbours() != 3) {
				this.closed = false;
			}
		}
		this.closed = true;
	}

	/**
	 * Tests if the component is closed.
	 * 
	 * @return boolean - true if closed, false otherwise.
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Makes this component consistent in the orientation of its elements -
	 * normal vectors point outside.
	 * 
	 * @param epsilon
	 *            GeoEpsilon of this
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void makeOrientationConsistent(GeoEpsilon epsilon) {
		if (isOrientationConsistent())
			return;

		if (!this.isClosed()) {
			super.makeOrientationConsistent(epsilon);
			return;
		}

		// check if outside oriented => sign of volume positive
		double vol = this.calculateSignedVolume();

		if (vol < 0) {
			invertOrientation();
		}

		this.oriented = true;
	}

	// extended methods of the ClosedHull3DComponent

	/*
	 * Calculates a signed volume of this. If sign is negative, the orientation
	 * must be changed.
	 * 
	 * @return double - signed volume of this.
	 */
	private double calculateSignedVolume() {

		if (!this.isClosed()) {
			this.checkClosed();
			return 0;
		}

		// calculate signed volume
		Iterator<Triangle3DElement> it = this.getElementsViaRecursion()
				.iterator();
		double volume = 0;
		Vector3D[] vec = null;
		Triangle3DElement elt = null;

		Vector3D center = this.getMBB().getCenter().getVector();

		while (it.hasNext()) {
			elt = it.next();
			vec = elt.getVectors();

			Vector3D v0 = Vector3D.add(center, vec[0]);
			Vector3D v1 = Vector3D.add(center, vec[1]);
			Vector3D v2 = Vector3D.add(center, vec[2]);

			volume += v0.spatproduct(v1, v2) * (1.0 / 6.0);
		}

		return volume;

	}

	/**
	 * Calculates volume of this.
	 * 
	 * @return double - volume of this.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public double getVolume() {

		if (!this.oriented)
			this.makeOrientationConsistent(getGeoEpsilon());

		return this.calculateSignedVolume();
	}

}
