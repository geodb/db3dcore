package de.uos.igf.db3d.dbms.spatials.geometries3d;

import java.util.Vector;

import de.uos.igf.db3d.dbms.exceptions.ValidationException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.systems.Db3dSimpleResourceBundle;

/**
 * <p>
 * Class Segment3D models a bounded line in 3D space.<br>
 * It provides different constructors for instantiation. It also provides
 * different intersection test and computing methods between Line3D and Plane3D
 * and Segment3D objects. <br>
 * <br>
 * The Segment3D is defined through a start and an end point as Point3D.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Segment3D extends Geometry3DAbst {

	/* array of points */
	protected Point3D[] points;

	/**
	 * Constructor.<br>
	 * Constructs a Segment3D with given start and end point. StartPoint has
	 * index 0, EndPoint index 1
	 * 
	 * @param start
	 *            start point as Point3D
	 * @param end
	 *            end point as Point3D
	 * @param epsilon
	 *            GeoEpsilon needed for validation<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 */
	public Segment3D(Point3D start, Point3D end, GeoEpsilon epsilon) {

		this.points = new Point3D[] { start, end };

		// validate
		if (epsilon != null) {
			if (!isValid(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotval"));
			if (!isRegular(epsilon))
				throw new IllegalArgumentException(
						Db3dSimpleResourceBundle
								.getString("db3d.geom.argnotreg"));
		}
	}

	/**
	 * Constructor.<br>
	 * Constructs a Segment3D with the given vectors interpreted as position
	 * vectors of points in space.
	 * 
	 * @param start
	 *            start point as position vector
	 * @param end
	 *            end point as position vector
	 * @param epsilon
	 *            GeoEpsilon needed for validation<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 */
	public Segment3D(Vector3D start, Vector3D end, GeoEpsilon epsilon) {
		this(start.getAsPoint3D(), end.getAsPoint3D(), epsilon);
	}

	/**
	 * Copy constructor.<br>
	 * Makes a deep copy (also copies the point references).
	 * 
	 * @param segment
	 *            Segment3D
	 */
	public Segment3D(Segment3D segment) {
		this.points = segment.points.clone();
	}

	/**
	 * Returns the length of this.
	 * 
	 * @return double - length.
	 */
	public double getLength() {
		return this.points[0].euclideanDistance(this.points[1]);
	}

	/**
	 * Returns the square length of this.
	 * 
	 * @return double - square length.
	 */
	public double getLengthSQR() {
		return this.points[0].euclideanDistanceSQR(this.points[1]);
	}

	/**
	 * Returns the center of this segment.
	 * 
	 * @return Point3D - center.
	 */
	public Point3D getCenter() {
		return new Point3D((points[0].x + points[1].x) * 0.5,
				(points[0].y + points[1].y) * 0.5,
				(points[0].z + points[1].z) * 0.5);
	}

	/**
	 * Returns the Point3D array of this.<br>
	 * 
	 * @return Point3D array.
	 */
	public Point3D[] getPoints() {
		return this.points;
	}

	/**
	 * Returns (computes) the MBB3D of this.
	 * 
	 * @return MBB3D - mbb of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D getMBB() {
		return new MBB3D(new Point3D(GeomUtils.getMin(this.points[0].x,
				this.points[1].x), GeomUtils.getMin(this.points[0].y,
				this.points[1].y), GeomUtils.getMin(this.points[0].z,
				this.points[1].z)), new Point3D(GeomUtils.getMax(
				this.points[0].x, this.points[1].x), GeomUtils.getMax(
				this.points[0].y, this.points[1].y), GeomUtils.getMax(
				this.points[0].z, this.points[1].z)));
	}

	/**
	 * Returns the vectors of this segment.
	 * 
	 * @return Vector3D[] with the vectors of this.
	 */
	public Vector3D[] getVectors() {
		return new Vector3D[] { new Vector3D(points[0]),
				new Vector3D(points[1]) };
	}

	/**
	 * Returns the normalized direction vector for this segment.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return Vector3D - the normalized direction vector for this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Vector3D getDirectionVector(GeoEpsilon epsilon) {
		Vector3D v = new Vector3D(this.points[1], this.points[0]);
		v.normalize(epsilon);
		return v;
	}

	/**
	 * Returns the line through this segment.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return Line3D - the line through this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Line3D getLine(GeoEpsilon epsilon) {
		return new Line3D(this, epsilon);
	}

	/**
	 * Tests whether this intersects the give MBB.
	 * 
	 * @param mbb
	 *            MBB3D for test
	 * @return boolean - true if this intersects with mbb.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(MBB3D mbb, GeoEpsilon epsilon) { // Dag

		if (!this.getMBB().intersects(mbb, epsilon))
			return false;
		if (this.getMBB().inside(mbb, epsilon))
			return true;

		/*
		 * intersection of this.getLine and mbb -> test result of intersection
		 * against this
		 */
		Geometry3D object = mbb.intersection(this.getLine(epsilon), epsilon);

		if (object.getGeometryType() == GEOMETRYTYPES.POINT)
			if (this.contains(((Point3D) object), epsilon))
				return true;

		if (object.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
			object = this.intersectionOnLine(((Segment3D) object), epsilon);
			if (object != null)
				return true;
		}
		return false;
	}

	/**
	 * Tests whether the inner of this intersects the give MBB.
	 * 
	 * @param mbb
	 *            MBB3D for test
	 * @return boolean - true if inner of this intersects with mbb.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsStrict(MBB3D mbb, GeoEpsilon epsilon) { // Dag

		if (!this.getMBB().intersectsStrict(mbb, epsilon))
			return false;

		if (this.getMBB().inside(mbb, epsilon))
			return true;

		Geometry3D object = mbb.intersection(this.getLine(epsilon), epsilon);
		if (object.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
			object = this.intersectionOnLine(((Segment3D) object), epsilon);
			if (object.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
				if (((Segment3D) object).getMBB().inside(mbb, epsilon)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests if this intersects the given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if intersects, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Plane3D plane, GeoEpsilon epsilon) {
		return plane.intersects(this, epsilon);
	}

	/**
	 * Tests if this intersects the given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Line3D line, GeoEpsilon epsilon) {
		int flag = this.intersectsInt(line, epsilon);
		if (flag < 0)
			return false;
		else
			return true;
	}

	/**
	 * Tests whether this intersects with the given Segment3D.
	 * 
	 * @param segment
	 *            Segment3D for intersection
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Segment3D segment, GeoEpsilon epsilon) {

		if (!this.getMBB().intersects(segment.getMBB(), epsilon))
			return false;

		Geometry3D result = this.intersection(new Line3D(segment, epsilon),
				epsilon);
		// check the result
		if (result == null) // no intersection return null
			return false;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
			// parallel and distance 0 - compute intersection
			Vector<Point3D> points = new Vector<Point3D>(2);
			// test for points of this in segment
			if (segment.containsOnLine(this.points[0], epsilon))
				points.add(this.points[0]);
			if (segment.containsOnLine(this.points[1], epsilon))
				points.add(this.points[1]);
			if (this.containsOnLine(segment.points[0], epsilon))
				points.add(segment.points[0]);
			if (this.containsOnLine(segment.points[1], epsilon))
				points.add(segment.points[1]);
			if (points.get(0) == null)
				return false;
			else
				return true;
		} else { // result == Point3D; Test if on both segments
			if (this.containsOnLine(((Point3D) result), epsilon)) {
				if (segment.containsOnLine(((Point3D) result), epsilon))
					return true;
			}
			return false;
		}
	}

	/**
	 * Tests whether this intersects the given plane and of which dimension the
	 * result would be. (-1 no intersection, 1->1D, 2->2D ...)
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - dimension of result.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int intersectsInt(Plane3D plane, GeoEpsilon epsilon) {
		return plane.intersectsInt(this, epsilon);
	}

	/**
	 * Tests whether this intersects the given line and of which dimension the
	 * result would be. (-1 no intersection, 1->1D, 2->2D ...)
	 * 
	 * @param line
	 *            Line3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int intersectsInt(Line3D line, GeoEpsilon epsilon) {
		Geometry3D result = this.intersection(line, epsilon);
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return 1;
		return 0;
	}

	/**
	 * Tests whether this intersects the given segment and of which dimension
	 * the result would be. (-1 no intersection, 1->1D, 2->2D ...)
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             nots -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int intersectsInt(Segment3D segment, GeoEpsilon epsilon) {
		Geometry3D result = this.intersection(segment, epsilon);
		if (result == null)
			return -1;
		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return 1;
		return 0;
	}

	/**
	 * Tests whether this intersects with given plane in dimension 0.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @return boolean - true if intersects, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsRegular(Plane3D plane, GeoEpsilon epsilon) {
		if (this.intersectsInt(plane, epsilon) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given line in dimension 0.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsRegular(Line3D line, GeoEpsilon epsilon) {
		if (this.intersectsInt(line, epsilon) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given segment in dimension 0.
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if intersects, false otherwise.
	 * @throws Illegal
	 *             StateException - if the intersectsInt(Line3D line, GeoEpsilon
	 *             epsilon) method of the class Line3D (which computes the
	 *             intersection of two lines) called by this method returns a
	 *             value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersectsRegular(Segment3D segment, GeoEpsilon epsilon) {
		if (this.intersectsInt(segment, epsilon) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this projects regular as a segment on given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean projectsRegular(Plane3D plane, GeoEpsilon epsilon) {
		if (projection(plane, epsilon).getGeometryType() == GEOMETRYTYPES.SEGMENT)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether a location is contained on this.
	 * 
	 * @param point
	 *            Location3D to test
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 * @see #containsOnLine(Point3D, GeoEpsilon)
	 */
	public boolean contains(Point3D point, GeoEpsilon epsilon) {
		if (new Line3D(this, epsilon).contains(point, epsilon))
			if (this.containsOnLine(point, epsilon))
				return true;

		return false;
	}

	/**
	 * Tests whether a segment is contained on this.
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Segment3D segment, GeoEpsilon epsilon) {
		if (this.contains(segment.points[0], epsilon)
				&& this.contains(segment.points[1], epsilon))
			return true;
		else
			return false;
	}

	/**
	 * Computes the intersection between this and the given Line3D.<br>
	 * Returns this if the Line3D and this are parallel and their distance is
	 * equal 0.<br>
	 * Returns null if they are parallel and their distance is ! = 0 or they are
	 * skew, or they not intersect.<br>
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param line
	 *            Line3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method (which computes the intersection of two lines) of the
	 *             class Line3D returns a value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Line3D line, GeoEpsilon epsilon) {
		return line.intersection(this, epsilon);
	}

	/**
	 * Computes the intersection between this and the given Segment3D.<br>
	 * Assumes that the given segment lies on line of this (and vice versa)
	 * Returns <code>null</code> if they do not intersect.<br>
	 * Returns a Point3D object if they touch each other in end points.<br>
	 * Returns a Segment3D if they intersect strict.
	 * 
	 * @param segment
	 *            Segment3D for intersection
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 */
	public Geometry3D intersectionOnLine(Segment3D segment, GeoEpsilon epsilon) {

		boolean thisContainsP0 = this
				.containsOnLine(segment.points[0], epsilon);
		boolean thisContainsP1 = this
				.containsOnLine(segment.points[1], epsilon);
		boolean segContainsP0 = segment.containsOnLine(this.points[0], epsilon);
		boolean segContainsP1 = segment.containsOnLine(this.points[1], epsilon);

		if (thisContainsP0) {
			if (thisContainsP1) {
				return segment; // this contains seg, or they are equal
			} else {
				if (segContainsP0) {
					if (!(segment.points[0].isEqual(this.points[0], epsilon)))
						return new Segment3D(segment.points[0], this.points[0],
								epsilon); // segments
											// intersect
											// with
											// end point 0
											// of
					// argument at end point 0 of this
					else if (segContainsP1)
						return new Segment3D(this); // seg contains this
					else
						return new Point3D(segment.points[0]); // segments
																// touch in
																// segP0
					// and
					// thisP0
				} else { // this contains P0 and not P1 and seg doesn't contain
					// P0 -> seg must contain P1
					if (!(segContainsP1))
						throw new IllegalStateException(
								Db3dSimpleResourceBundle
										.getString("db3d.geom.somewrong"));
					else {
						if (!(segment.points[0]
								.isEqual(this.points[1], epsilon)))
							return new Segment3D(segment.points[0],
									this.points[1], epsilon); // segments
						// intersect
						// with end point 0 of
						// argument at end point
						// 1 of this
						else
							return new Point3D(segment.points[0]); // segments
																	// touch
																	// in
						// segP0
						// and thisP1
					}
				}
			}
		} else { // this doesn't contain P0
			if (thisContainsP1) {
				if (segContainsP0) {
					if (segContainsP1)
						return new Segment3D(this);
					else { // this contain P1 and not P0, seg contains P0 and
							// not P1
						if (!(segment.points[1]
								.isEqual(this.points[0], epsilon))) {
							return new Segment3D(segment.points[1],
									this.points[0], epsilon); // segments
							// intersect
							// with end point 1 of
							// argument at end point
							// 0 of this
						} else
							return new Point3D(segment.points[1]); // segments
																	// touch
																	// in
						// segP1
						// and thisP0
					}
				} else { // this contains P1 and not P0 and seg doesn't contain
					// P0 -> seg must contain P1
					if (!(segContainsP1))
						throw new IllegalStateException(
								Db3dSimpleResourceBundle
										.getString("db3d.geom.somewrong"));
					else {
						if (!(segment.points[1]
								.isEqual(this.points[1], epsilon))) {
							return new Segment3D(segment.points[1],
									this.points[1], epsilon); // segments
							// intersect
							// with end point 1 of
							// argument at end point
							// 1 of this
						} else
							return new Point3D(segment.points[1]); // segments
																	// touch
																	// in
						// segP1
						// and thisP1
					}
				}
			} else { // this doesn't contain P1 as well as P0
				if (segContainsP0)
					return new Segment3D(this); // seg must contain this
				else
					return null; // no intersection possible
			}
		}
	}

	/**
	 * Computes the intersection between this and the given Segment3D.<br>
	 * Returns <code>null</code> if they do not intersect.<br>
	 * Returns a Point3D object if they touch each other or intersect in that
	 * point.<br>
	 * Returns a Segment3D if they intersect on a line (touching already
	 * accounted for).
	 * 
	 * @param segment
	 *            Segment3D for intersection
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Segment3D segment, GeoEpsilon epsilon) {

		if (!this.getMBB().intersects(segment.getMBB(), epsilon))
			return null;

		Geometry3D result = this.intersection(new Line3D(segment, epsilon),
				epsilon);

		if (result == null) { // no intersection return null
			return null;
		} else if (result instanceof Segment3D) {
			return intersectionOnLine((Segment3D) segment, epsilon);
		} else if (result instanceof Point3D
				&& containsOnLine((Point3D) result, epsilon)
				&& segment.containsOnLine((Point3D) result, epsilon)) {
			// this touches or intersects line of argument in result
			return result;
		} else
			return null;

	}

	/**
	 * Computes the intersection between this and the given plane.<br>
	 * Returns the segment if the Segment3D and the Plane3D are parallel and
	 * their distance equals 0.<br>
	 * Returns null if they are parallel and their distance is ! = 0 or they
	 * don't intersect.<br>
	 * Returns a Point3D object if they intersect.
	 * 
	 * @param plane
	 *            Plane3D for intersection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Geometry3D intersection(Plane3D plane, GeoEpsilon epsilon) {
		return plane.intersection(this, epsilon);
	}

	/**
	 * Projects this onto the given plane. Returns Point3D or Segment3D.
	 * (returned Segments must not be regular, test in next step by using
	 * isRelular() method)
	 * 
	 * @param plane
	 *            Plane3D for projection
	 * @param epsilon
	 *            GeoEpsilon
	 * @return SimpleGeoObj - result of projection.
	 */
	public Geometry3D projection(Plane3D plane, GeoEpsilon epsilon) {
		Point3D p1 = this.points[0].projection(plane);
		Point3D p2 = this.points[1].projection(plane);
		if (p1.isEqual(p2, epsilon))
			return new Point3D(p1);
		else
			return new Segment3D(p1, p2, epsilon);
	}

	/**
	 * Tests whether a point is contained on this.<br>
	 * This method assumes that the point is on the Line3D "equal" to this. That
	 * means it performs no test if it really is "OnLine"
	 * 
	 * @param point
	 *            Point3DBase to test
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsOnLine(Point3D point, GeoEpsilon epsilon) {

		if (this.points[0].isEqual(point, epsilon)
				|| this.points[1].isEqual(point, epsilon))
			return true;

		double sqrSegLength = this.getLengthSQR();
		return !(epsilon.greaterThan(
				point.euclideanDistanceSQR(this.points[0]), sqrSegLength) || epsilon
				.greaterThan(point.euclideanDistanceSQR(this.points[1]),
						sqrSegLength));
	}

	/**
	 * Validates if this is a valid segment.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * 
	 * @return boolean - true if this is valid, false otherwise.
	 */
	public boolean isValid(GeoEpsilon epsilon) {
		if (this.points[0].isEqual(this.points[1], epsilon))
			return false;

		return true;
	}

	/**
	 * Checks whether this is a regular segment.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if this is a regular segment, false otherwise.
	 */
	public boolean isRegular(GeoEpsilon epsilon) {

		if (getLength() > (epsilon.getEpsilon() * MIN_LENGTH_EPSILON_FACTOR))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether this is a completely validated segment.<br>
	 * This method performs a isValid and isRegular test.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if this is completely validated, false otherwise.
	 */
	public boolean isCompleteValidated(GeoEpsilon epsilon) {
		return isValid(epsilon) && isRegular(epsilon);
	}

	/**
	 * Inverts the orientation of this by changing the vertices.
	 */
	public void invertOrientation() {
		Point3D temp = this.points[0];
		this.points[0] = this.points[1];
		this.points[1] = temp;
	}

	/**
	 * Sets the orientation of this according to given vector.
	 * 
	 * @param vec
	 *            Vector3D with the given orientation
	 * @param epsilon
	 *            GeoEpsilon
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public void setOrientation(Vector3D vec, GeoEpsilon epsilon) {
		double scalarpd = this.getDirectionVector(epsilon).scalarproduct(vec);
		if (epsilon.lessThan(scalarpd, 0))
			invertOrientation();
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return SEGMENT3D always.
	 * @see Spatial.dbms.geom.GeometryTypes#getGeometryType()
	 */
	public GEOMETRYTYPES getGeometryType() {
		return GEOMETRYTYPES.SEGMENT;
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Segment3D [start=" + points[0].toString() + ", end=" + points[1].toString() + "]";
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result + ((points[1] == null) ? 0 : points[1].hashCode());
		result = result + ((points[0] == null) ? 0 : points[0].hashCode());
		return result;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// Segment3D other = (Segment3D) obj;
	// if (end == null) {
	// if (other.end != null)
	// return false;
	// } else if (!end.equals(other.end) && !end.equals(other.start))
	// return false;
	// if (start == null) {
	// if (other.start != null)
	// return false;
	// } else if (!start.equals(other.start) && !start.equals(other.end))
	// return false;
	// return true;
	// }

	/**
	 * Strict equal test.<br>
	 * All the points must be equal in the same index (not in case of Point3D).<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if equal, false otherwise.
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.GeoEpsilon)
	 */
	public boolean isEqual(Segment3D obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!points[0].isEqual(obj.points[0], epsilon))
			return false;
		if (!points[1].isEqual(obj.points[1], epsilon))
			return false;
		return true;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Segment3D))
			return false;
		return isEqual((Segment3D) obj, epsilon);
	}

	/**
	 * Computes the corresponding hash code for isEqual usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * @see db3d.dbms.geom.Equivalentable#isEqualHC(int)
	 */
	public int isEqualHC(int factor) {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.points[0].isEqualHC(factor);
		result = prime * result + this.points[1].isEqualHC(factor);
		return result;
	}

	/**
	 * Geometry equivalence test.<br>
	 * The objects must have the same points, but the index position makes no
	 * difference.<br>
	 * Runtime type is tested with instanceof.
	 * 
	 * @param obj
	 *            Equivalentable object for test
	 * @param epsilon
	 *            GeoEpsilon
	 * @return boolean - true if equal, false otherwise.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.GeoEpsilon)
	 */
	public boolean isGeometryEquivalent(Segment3D obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		Point3D[] ps1 = GeomUtils.getSorted(this.points);
		Point3D[] ps2 = GeomUtils.getSorted(obj.points);
		int length = ps1.length;
		for (int i = 0; i < length; i++)
			if (!ps1[i].isEqual(ps2[i], epsilon))
				return false;

		return true;
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon epsilon) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Segment3D))
			return false;
		return isGeometryEquivalent((Segment3D) obj, epsilon);
	}

	/**
	 * Computes the corresponding hash code for isGeometryEquivalent usage.
	 * 
	 * @param factor
	 *            int for rounding
	 * @return int - hash code value.
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalentHC(int)
	 */
	public int isGeometryEquivalentHC(int factor) {

		final int prime = 31;
		int result = 1;

		Point3D[] ps = GeomUtils.getSorted(this.points);
		int length = ps.length;

		for (int i = 0; i < length; i++)
			result = prime * result + ps[i].isGeometryEquivalentHC(factor);

		return result;
	}

}
