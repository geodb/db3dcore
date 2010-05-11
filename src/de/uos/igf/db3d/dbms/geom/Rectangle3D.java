/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.util.Iterator;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;

/**
 * Class Rectangle3D - represents faces of a minimum bounding box oriented to
 * axes in the 3rd dimension. <br>
 * Provides geometric operations like intersects, intersection computation...
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Rectangle3D {

	/* geometry */
	/* point 0 */
	private Point3D zero;

	/* point 1 */
	private Point3D one;

	/* point 2 */
	private Point3D two;

	/* point 3 */
	private Point3D three;

	/**
	 * Constructor for single points. <br>
	 * Constructs a Rectangle3D object with given Point3Ds p0, p1, p2, p3.
	 * Points must be in order p0->p1->p2->p3 - so that there is no edge between
	 * p0 and p2 and between p1 and p3, respectively p0 is opposite corner to p2
	 * and p1 to p3. <br>
	 * Validity is not tested !
	 * 
	 * @param p0
	 *            Point3D 0
	 * @param p1
	 *            Point3D 1
	 * @param p2
	 *            Point3D 2
	 * @param p3
	 *            Point3D 3
	 */
	public Rectangle3D(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
		this.zero = p0;
		this.one = p1;
		this.two = p2;
		this.three = p3;
	}

	/**
	 * Returns point at given index.
	 * 
	 * @param index
	 *            int index of the point
	 * @return Point3D at the given index.
	 * @throws IllegalStateException
	 *             - if index is not 0, 1, 2 or 3.
	 */
	public Point3D getPoint(int index) {
		switch (index) {
		case 0:
			return this.zero;
		case 1:
			return this.one;
		case 2:
			return this.two;
		case 3:
			return this.three;
		default:
			// FIXME Fix this weird switch(index) stuff
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.rectonlyfour"));
		}
	}

	/**
	 * Returns the points as an array of length = 4.<br>
	 * Order is zero, one, two and three.
	 * 
	 * @return Point3D[] with the points of this.
	 */
	public Point3D[] getPoints() {
		Point3D[] points = { this.zero, this.one, this.two, this.three };
		return points;
	}

	/**
	 * Returns Segment3D object for point i (in direction (i+1)). Returns
	 * <code>null</null> if case of flat rectangle when point i equals point (i+1).
	 * 
	 * @param index
	 *            int index of the point
	 * @return Segment3D.
	 * @throws IllegalStateException
	 *             - if the index of a Point3D in a Rectangle3D is not in the
	 *             interval [0, 3].
	 */
	public Segment3D getSegment(int index) {
		if (this.getPoint(index).equals(this.getPoint((index + 1) % 4)))
			return null;
		return new Segment3D(this.getPoint(index), this
				.getPoint((index + 1) % 4), null);
	}

	/**
	 * Returns the segments of this rectangle. Returns an empty array if the
	 * rectangle is a point.
	 * 
	 * @return Segment3D[] of this.
	 * @throws IllegalStateException
	 *             - if the index of a Point3D in a Rectangle3D is not in the
	 *             interval [0, 3].
	 */
	public Segment3D[] getSegments() {

		if (this.getPoint(0).equals(this.getPoint(2)))
			return new Segment3D[0];

		if (this.getPoint(0).equals(this.getPoint(1)))
			return new Segment3D[] { this.getSegment(1) };

		if (this.getPoint(0).equals(this.getPoint(3)))
			return new Segment3D[] { this.getSegment(0) };

		Segment3D[] seg = new Segment3D[4];
		for (int i = 0; i < 4; i++) {
			seg[i] = this.getSegment(i);
		}
		return seg;
	}

	/**
	 * Sets the given point at given index.
	 * 
	 * @param index
	 *            int index of the point
	 * @param point
	 *            Point3D to be set
	 * @throws IllegalStateException
	 *             if index is not 0, 1, 2 or 3.
	 */
	public void setPoint(int index, Point3D point) {
		switch (index) {
		case 0:
			this.zero = point;
			break;
		case 1:
			this.one = point;
			break;
		case 2:
			this.two = point;
			break;
		case 3:
			this.three = point;
			break;
		default:
			// FIXME Fix this weird switch(index) stuff
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.rectonlyfour"));
		}
	}

	/**
	 * Returns the intersection of this with given line.<br>
	 * 
	 * @param line
	 *            Line3D for intersection
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of intersection.
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
	public SimpleGeoObj intersectionInPlane(Line3D line, ScalarOperator sop) {

		SimpleGeoObj obj = null;
		EquivalentableHashSet pointHS = new EquivalentableHashSet(10, sop,
				Equivalentable.GEOMETRY_EQUIVALENT);

		for (int n = 0; n < 4; n++) { // for all segments

			int k = (n + 1) % 4;
			Segment3D seg = new Segment3D(this.getPoint(n), this.getPoint(k),
					sop);
			obj = seg.intersection(line, sop);
			if (obj != null) {
				if (obj.getType() == SimpleGeoObj.SEGMENT3D)
					return obj;
				// else obj must be of type POINT3D
				pointHS.add(obj);
			}
		}

		Point3D[] points = (Point3D[]) pointHS.toArray(new Point3D[pointHS
				.size()]);
		int length = points.length;
		if (length == 1)
			return points[0];
		if (length == 2)
			return new Segment3D(points[0], points[1], sop);

		return null; // if line doesn't intersect rectangle
	}

	/**
	 * Returns intersection result of this with plane.<br>
	 * Result can be <code>null</code>, Point3D, Segment3D or Wireframe3D.
	 * 
	 * @param plane
	 *            Plane3D for intersection
	 * @param sop
	 *            ScalarOperator
	 * @return SimpleGeoObj - result of intersection.
	 * @throws IllegalStateException
	 *             - if the index of a Point3D in a Rectangle3D is not in the
	 *             interval [0, 3].
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) {

		Segment3D[] seg = this.getSegments();
		int length = seg.length;
		if (length == 0)
			return this.getPoint(0).intersection(plane, sop);

		EquivalentableHashSet pointHS = new EquivalentableHashSet(6, sop,
				Equivalentable.GEOMETRY_EQUIVALENT);
		int segCounter = 0;

		for (int i = 0; i < length; i++) {

			SimpleGeoObj obj = seg[i].intersection(plane, sop);
			if (obj != null) {
				if (obj.getType() == SimpleGeoObj.POINT3D)
					pointHS.add(obj);
				if (obj.getType() == SimpleGeoObj.SEGMENT3D) {
					segCounter++;
					pointHS.add(seg[i].getPoint(0));
					pointHS.add(seg[i].getPoint(1));
					if (segCounter == 2) {
						Wireframe3D wf = new Wireframe3D(sop);
						wf.add(seg);
						return wf;
					}
				}
			}
		}
		Iterator<Point3D> it = pointHS.iterator();
		if (pointHS.size() == 2)
			return new Segment3D(it.next(), it.next(), sop);
		else if (pointHS.size() == 1)
			return it.next();
		else
			return null;
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "Rectangle3D [one=" + one + ", three=" + three + ", two=" + two
				+ ", zero=" + zero + "]";
	}

}
