/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Serializable;
import java.util.Vector;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;

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
@SuppressWarnings("serial")
public class Segment3D implements PersistentObject, SimpleGeoObj,
		Equivalentable, Serializable {

	/* start point of Segment3D */
	private Point3D start;

	/* end point of Segment3D */
	private Point3D end;

	/**
	 * Constructor.<br>
	 * Constructs a Segment3D with given start and end point. StartPoint has
	 * index 0, EndPoint index 1
	 * 
	 * @param start
	 *            start point as Point3D
	 * @param end
	 *            end point as Point3D
	 * @param sop
	 *            ScalarOperator needed for validation<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 */
	public Segment3D(Point3D start, Point3D end, ScalarOperator sop) {
		this.start = start;
		this.end = end;

		// validate
		if (sop != null) {
			if (!isValid(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
						.getString("db3d.geom.argnotval"));
			if (!isRegular(sop))
				throw new IllegalArgumentException(Db3dSimpleResourceBundle
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
	 * @param sop
	 *            ScalarOperator needed for validation<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur
	 * @throws ValidationException
	 *             - signals inappropriate parameters.<br>
	 */
	public Segment3D(Vector3D start, Vector3D end, ScalarOperator sop) {
		this(start.getAsPoint3D(), end.getAsPoint3D(), sop);
	}

	/**
	 * Copy constructor.<br>
	 * Makes a deep copy (also copies the point references).
	 * 
	 * @param segment
	 *            Segment3D
	 */
	public Segment3D(Segment3D segment) {
		this.start = new Point3D(segment.getPoint(0));
		this.end = new Point3D(segment.getPoint(1));
	}

	/**
	 * Returns the points of this.
	 * 
	 * @return Point3D[] - points.
	 */
	public Point3D[] getPoints() {
		return new Point3D[] { this.start, this.end };
	}

	/**
	 * Returns the point of this at given index.
	 * 
	 * @param index
	 *            int index of the point
	 * @return Point3D - point.
	 */
	public Point3D getPoint(int index) {
		switch (index) {
		case 0:
			return this.start;
		case 1:
			return this.end;
		default:
			// FIXME fix this weird switch(index) stuff
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.segonlystartend"));
		}
	}

	/**
	 * Sets the points of this.
	 * 
	 * @param points
	 *            Point3D[].
	 */
	public void setPoints(Point3D[] points) {
		this.start = points[0];
		this.end = points[1];
	}

	/**
	 * Sets the point at given index.
	 * 
	 * @param index
	 *            int index of the point
	 * @param point
	 *            Point3D
	 */
	public void setPoint(int index, Point3D point) {
		switch (index) {
		case 0:
			this.start = point;
			break;
		case 1:
			this.end = point;
			break;
		default:
			// FIXME fix this weird switch(index) stuff
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.segonlystartend"));
		}
	}

	/**
	 * Returns the length of this.
	 * 
	 * @return double - length.
	 */
	public double getLength() {
		return this.start.euclideanDistance(this.end);
	}

	/**
	 * Returns the square length of this.
	 * 
	 * @return double - square length.
	 */
	public double getLengthSQR() {
		return this.start.euclideanDistanceSQR(this.end);
	}

	/**
	 * Returns the center of this segment.
	 * 
	 * @return Point3D - center.
	 */
	public Point3D getCenter() {
		Point3D[] points = this.getPoints();
		return new Point3D((points[0].getX() + points[1].getX()) * 0.5,
				(points[0].getY() + points[1].getY()) * 0.5,
				(points[0].getZ() + points[1].getZ()) * 0.5);
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
		return new MBB3D(
				new Point3D(GeomUtils
						.getMin(this.start.getX(), this.end.getX()), GeomUtils
						.getMin(this.start.getY(), this.end.getY()), GeomUtils
						.getMin(this.start.getZ(), this.end.getZ())),
				new Point3D(GeomUtils
						.getMax(this.start.getX(), this.end.getX()), GeomUtils
						.getMax(this.start.getY(), this.end.getY()), GeomUtils
						.getMax(this.start.getZ(), this.end.getZ())));
	}

	/**
	 * Returns the vectors of this segment.
	 * 
	 * @return Vector3D[] with the vectors of this.
	 */
	public Vector3D[] getVectors() {
		return new Vector3D[] { new Vector3D(start), new Vector3D(end) };
	}

	/**
	 * Returns the normalized direction vector for this segment.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return Vector3D - the normalized direction vector for this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Vector3D getDirectionVector(ScalarOperator sop) {
		Vector3D v = new Vector3D(this.end, this.start);
		v.normalize(sop);
		return v;
	}

	/**
	 * Returns the line through this segment.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return Line3D - the line through this.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public Line3D getLine(ScalarOperator sop) {
		return new Line3D(this, sop);
	}

	/**
	 * Tests whether this intersects the give MBB.
	 * 
	 * @param mbb
	 *            MBB3D for test
	 * @return boolean - true if this intersects with mbb.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(MBB3D mbb, ScalarOperator sop) { // Dag

		if (!this.getMBB().intersects(mbb, sop))
			return false;
		if (this.getMBB().inside(mbb, sop))
			return true;

		/*
		 * intersection of this.getLine and mbb -> test result of intersection
		 * against this
		 */
		SimpleGeoObj object = mbb.intersection(this.getLine(sop), sop);

		if (object.getType() == SimpleGeoObj.POINT3D)
			if (this.contains(((Point3D) object), sop))
				return true;

		if (object.getType() == SimpleGeoObj.SEGMENT3D) {
			object = this.intersectionOnLine(((Segment3D) object), sop);
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsStrict(MBB3D mbb, ScalarOperator sop) { // Dag

		if (!this.getMBB().intersectsStrict(mbb, sop))
			return false;

		if (this.getMBB().inside(mbb, sop))
			return true;

		SimpleGeoObj object = mbb.intersection(this.getLine(sop), sop);
		if (object.getType() == SimpleGeoObj.SEGMENT3D) {
			object = this.intersectionOnLine(((Segment3D) object), sop);
			if (object.getType() == SimpleGeoObj.SEGMENT3D) {
				if (((Segment3D) object).getMBB().inside(mbb, sop)) {
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
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersects, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Plane3D plane, ScalarOperator sop) {
		return plane.intersects(this, sop);
	}

	/**
	 * Tests if this intersects the given line.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Line3D line, ScalarOperator sop) {
		int flag = this.intersectsInt(line, sop);
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersects(Segment3D segment, ScalarOperator sop) {

		if (!this.getMBB().intersects(segment.getMBB(), sop))
			return false;

		SimpleGeoObj result = this.intersection(new Line3D(segment, sop), sop);
		// check the result
		if (result == null) // no intersection return null
			return false;
		if (result.getType() == SimpleGeoObj.SEGMENT3D) {
			// parallel and distance 0 - compute intersection
			Vector<Point3D> points = new Vector<Point3D>(2);
			// test for points of this in segment
			if (segment.containsOnLine(this.start, sop))
				points.add(this.start);
			if (segment.containsOnLine(this.end, sop))
				points.add(this.end);
			if (this.containsOnLine(segment.getPoint(0), sop))
				points.add(segment.getPoint(0));
			if (this.containsOnLine(segment.getPoint(1), sop))
				points.add(segment.getPoint(1));
			if (points.get(0) == null)
				return false;
			else
				return true;
		} else { // result == Point3D; Test if on both segments
			if (this.containsOnLine((Point3D) result, sop)) {
				if (segment.containsOnLine((Point3D) result, sop))
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
	 * @param sop
	 *            ScalarOperator
	 * @return int - dimension of result.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Plane3D plane, ScalarOperator sop) {
		return plane.intersectsInt(this, sop);
	}

	/**
	 * Tests whether this intersects the given line and of which dimension the
	 * result would be. (-1 no intersection, 1->1D, 2->2D ...)
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Line3D line, ScalarOperator sop) {
		SimpleGeoObj result = this.intersection(line, sop);
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.SEGMENT3D)
			return 1;
		return 0;
	}

	/**
	 * Tests whether this intersects the given segment and of which dimension
	 * the result would be. (-1 no intersection, 1->1D, 2->2D ...)
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return int - dimension of result.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             nots -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public int intersectsInt(Segment3D segment, ScalarOperator sop) {
		SimpleGeoObj result = this.intersection(segment, sop);
		if (result == null)
			return -1;
		if (result.getType() == SimpleGeoObj.SEGMENT3D)
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
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsRegular(Plane3D plane, ScalarOperator sop) {
		if (this.intersectsInt(plane, sop) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given line in dimension 0.
	 * 
	 * @param line
	 *            Line3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsRegular(Line3D line, ScalarOperator sop) {
		if (this.intersectsInt(line, sop) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this intersects with given segment in dimension 0.
	 * 
	 * @param segment
	 *            Segment3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersects, false otherwise.
	 * @throws Illegal
	 *             StateException - if the intersectsInt(Line3D line,
	 *             ScalarOperator sop) method of the class Line3D (which
	 *             computes the intersection of two lines) called by this method
	 *             returns a value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean intersectsRegular(Segment3D segment, ScalarOperator sop) {
		if (this.intersectsInt(segment, sop) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether this projects regular as a segment on given plane.
	 * 
	 * @param plane
	 *            Plane3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean projectsRegular(Plane3D plane, ScalarOperator sop) {
		if (projection(plane, sop).getType() == SimpleGeoObj.SEGMENT3D)
			return true;
		else
			return false;
	}

	/**
	 * Tests whether a point is contained on this.
	 * 
	 * @param point
	 *            Point3D to test
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 * @see #containsOnLine(Point3D, ScalarOperator)
	 */
	public boolean contains(Point3D point, ScalarOperator sop) {
		if (new Line3D(this, sop).contains(point, sop))
			if (this.containsOnLine(point, sop))
				return true;

		return false;
	}

	/**
	 * Tests whether a segment is contained on this.
	 * 
	 * @param seg
	 *            Segment3D for test
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean contains(Segment3D seg, ScalarOperator sop) {
		Point3D[] points = seg.getPoints();
		if (this.contains(points[0], sop) && this.contains(points[1], sop))
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method (which computes the intersection of two lines) of the
	 *             class Line3D returns a value that is not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) {
		return line.intersection(this, sop);
	}

	/**
	 * Computes the intersection between this and the given Segment3D.<br>
	 * Assumes that the given segment lies on line of this (and vice versa)
	 * Returns <code>null</code> if they do not intersect.<br>
	 * Returns a Point3D object if they touch each other in end points.<br>
	 * Returns a Segment3D if they intersect strict.
	 * 
	 * @param seg
	 *            Segment3D for intersection
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 */
	public SimpleGeoObj intersectionOnLine(Segment3D seg, ScalarOperator sop) {

		boolean thisContainsP0 = this.containsOnLine(seg.getPoint(0), sop);
		boolean thisContainsP1 = this.containsOnLine(seg.getPoint(1), sop);
		boolean segContainsP0 = seg.containsOnLine(this.getPoint(0), sop);
		boolean segContainsP1 = seg.containsOnLine(this.getPoint(1), sop);

		if (thisContainsP0) {
			if (thisContainsP1) {
				return seg; // this contains seg, or they are equal
			} else {
				if (segContainsP0) {
					if (!(seg.getPoint(0).isEqual(this.getPoint(0), sop)))
						return new Segment3D(seg.getPoint(0), this.getPoint(0),
								sop); // segments intersect with end point 0 of
					// argument at end point 0 of this
					else if (segContainsP1)
						return new Segment3D(this); // seg contains this
					else
						return seg.getPoint(0); // segments touch in segP0 and
					// thisP0
				} else { // this contains P0 and not P1 and seg doesn't contain
					// P0 -> seg must contain P1
					if (!(segContainsP1))
						throw new IllegalStateException(
								Db3dSimpleResourceBundle
										.getString("db3d.geom.somewrong"));
					else {
						if (!(seg.getPoint(0).isEqual(this.getPoint(1), sop)))
							return new Segment3D(seg.getPoint(0), this
									.getPoint(1), sop); // segments intersect
						// with end point 0 of
						// argument at end point
						// 1 of this
						else
							return seg.getPoint(0); // segments touch in segP0
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
						if (!(seg.getPoint(1).isEqual(this.getPoint(0), sop))) {
							return new Segment3D(seg.getPoint(1), this
									.getPoint(0), sop); // segments intersect
							// with end point 1 of
							// argument at end point
							// 0 of this
						} else
							return seg.getPoint(1); // segments touch in segP1
						// and thisP0
					}
				} else { // this contains P1 and not P0 and seg doesn't contain
					// P0 -> seg must contain P1
					if (!(segContainsP1))
						throw new IllegalStateException(
								Db3dSimpleResourceBundle
										.getString("db3d.geom.somewrong"));
					else {
						if (!(seg.getPoint(1).isEqual(this.getPoint(1), sop))) {
							return new Segment3D(seg.getPoint(1), this
									.getPoint(1), sop); // segments intersect
							// with end point 1 of
							// argument at end point
							// 1 of this
						} else
							return seg.getPoint(1); // segments touch in segP1
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
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Segment3D segment, ScalarOperator sop) {

		if (!this.getMBB().intersects(segment.getMBB(), sop))
			return null;

		SimpleGeoObj result = this.intersection(new Line3D(segment, sop), sop);

		if (result == null) { // no intersection return null
			return null;
		} else if (result instanceof Segment3D) {
			return intersectionOnLine((Segment3D) segment, sop);
		} else if (result instanceof Point3D
				&& containsOnLine((Point3D) result, sop)
				&& segment.containsOnLine((Point3D) result, sop)) {
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
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result SimpleGeoObj object (null, Segment3D,
	 *         Point3D).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) {
		return plane.intersection(this, sop);
	}

	/**
	 * Projects this onto the given plane. Returns Point3D or Segment3D.
	 * (returned Segments must not be regular, test in next step by using
	 * isRelular() method)
	 * 
	 * @param plane
	 *            Plane3D for projection
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of projection.
	 */
	public SimpleGeoObj projection(Plane3D plane, ScalarOperator sop) {
		Point3D p1 = (Point3D) this.getPoint(0).projection(plane);
		Point3D p2 = (Point3D) this.getPoint(1).projection(plane);
		if (p1.isEqual(p2, sop))
			return p1;
		else
			return new Segment3D(p1, p2, sop);
	}

	/**
	 * Tests whether a point is contained on this.<br>
	 * This method assumes that the point is on the Line3D "equal" to this. That
	 * means it performs no test if it really is "OnLine"
	 * 
	 * @param point
	 *            Point3D to test
	 * @return boolean - true if contained, false otherwise.
	 */
	public boolean containsOnLine(Point3D point, ScalarOperator sop) {

		if (this.getPoint(0).isEqual(point, sop)
				|| this.getPoint(1).isEqual(point, sop))
			return true;

		double sqrSegLength = this.getLengthSQR();
		return !(sop.greaterThan(point.euclideanDistanceSQR(this.start),
				sqrSegLength) || sop.greaterThan(point
				.euclideanDistanceSQR(this.end), sqrSegLength));
	}

	/**
	 * Validates if this is a valid segment.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * 
	 * @return boolean - true if this is valid, false otherwise.
	 */
	public boolean isValid(ScalarOperator sop) {
		if (this.start.isEqual(this.end, sop))
			return false;

		return true;
	}

	/**
	 * Checks whether this is a regular segment.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if this is a regular segment, false otherwise.
	 */
	public boolean isRegular(ScalarOperator sop) {

		if (getLength() > (sop.getEpsilon() * SimpleGeoObj.MIN_LENGTH_EPSILON_FACTOR))
			return true;
		else
			return false;
	}

	/**
	 * Checks whether this is a completely validated segment.<br>
	 * This method performs a isValid and isRegular test.
	 * 
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if this is completely validated, false otherwise.
	 */
	public boolean isCompleteValidated(ScalarOperator sop) {
		return isValid(sop) && isRegular(sop);
	}

	/**
	 * Inverts the orientation of this by changing the vertices.
	 */
	public void invertOrientation() {
		Point3D temp = getPoint(0);
		setPoint(0, getPoint(1));
		setPoint(1, temp);
	}

	/**
	 * Sets the orientation of this according to given vector.
	 * 
	 * @param vec
	 *            Vector3D with the given orientation
	 * @param sop
	 *            ScalarOperator
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public void setOrientation(Vector3D vec, ScalarOperator sop) {
		double scalarpd = this.getDirectionVector(sop).scalarproduct(vec);
		if (sop.lessThan(scalarpd, 0))
			invertOrientation();
	}

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
	 * @see db3d.dbms.geom.Equivalentable#isEqual(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isEqual(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Segment3D))
			return false;
		Segment3D seg = (Segment3D) obj;
		if (!getPoint(0).isEqual(seg.getPoint(0), sop))
			return false;
		if (!getPoint(1).isEqual(seg.getPoint(1), sop))
			return false;
		return true;
	}

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
	 * @see db3d.dbms.geom.Equivalentable#isGeometryEquivalent(db3d.dbms.geom.Equivalentable,
	 *      db3d.dbms.geom.ScalarOperator)
	 */
	public boolean isGeometryEquivalent(Equivalentable obj, ScalarOperator sop) {
		if (!(obj instanceof Segment3D))
			return false;
		Point3D[] ps1 = GeomUtils.getSorted(this.getPoints());
		Point3D[] ps2 = GeomUtils.getSorted(((Segment3D) obj).getPoints());
		int length = ps1.length;
		for (int i = 0; i < length; i++)
			if (!ps1[i].isEqual(ps2[i], sop))
				return false;

		return true;
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
		result = prime * result + getPoint(0).isEqualHC(factor);
		result = prime * result + getPoint(1).isEqualHC(factor);
		return result;
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

		Point3D[] ps = GeomUtils.getSorted(this.getPoints());
		int length = ps.length;

		for (int i = 0; i < length; i++)
			result = prime * result + ps[i].isGeometryEquivalentHC(factor);

		return result;
	}

	/**
	 * Tests if given object is the same class type.
	 * 
	 * @param obj
	 *            Object
	 * @return boolean - true if this and the given object are of the same class
	 *         type.
	 * @see db3d.dbms.geom.Equivalentable#isEqualClass(java.lang.Object)
	 */
	public boolean isEqualClass(Object obj) {
		if (this.getClass() != obj.getClass())
			return false;
		return true;
	}

	/**
	 * Returns the type of this as a SimpleGeoObj.
	 * 
	 * @return SEGMENT3D always.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.SEGMENT3D;
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Segment3D [end=" + end + ", start=" + start + "]";
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result + ((end == null) ? 0 : end.hashCode());
		result = result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment3D other = (Segment3D) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end) && !end.equals(other.start))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start) && !start.equals(other.end))
			return false;
		return true;
	}
}
