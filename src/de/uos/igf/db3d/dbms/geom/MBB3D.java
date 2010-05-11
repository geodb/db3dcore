/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;

/**
 * Class MBB3D - represents a axes parallel Minimum Bounding Box in 3rd
 * dimension. <br>
 * Provides geometric operations on bounding boxes like intersects, distance,
 * intersection computation...
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class MBB3D implements PersistentObject, SimpleGeoObj, Externalizable {

	/* minimum and maximum point */
	private Point3D pMin;

	/* minimum and maximum point */
	private Point3D pMax;

	/**
	 * Constructor.<br>
	 * Constructs a MBB3D with pMin with all Double.MIN_VALUE and pMax with all
	 * Double.MAX_VALUE
	 */
	public MBB3D() {
		this.pMin = new Point3D(Double.MIN_VALUE, Double.MIN_VALUE,
				Double.MIN_VALUE);
		this.pMax = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE);
	}

	/**
	 * Constructor
	 * 
	 * @param pMin
	 *            minimum point of bounding box
	 * @param pMax
	 *            maximum point of bounding box
	 * @throws IllegalArgumentException
	 *             if the maximum point of the bounding box is not greater than
	 *             its minimum point.
	 */
	public MBB3D(Point3D pMin, Point3D pMax) {
		this.pMin = pMin;
		if (!testMaxPoint(pMax)) {
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.pmaxpmin"));
		}

		this.pMax = pMax;
	}

	/**
	 * Computes the volume of this.
	 * 
	 * @return double - volume as double
	 */
	public double computeVolume() {
		double volume = 1;
		volume *= (getPMax().getX() - getPMin().getX());
		volume *= (getPMax().getY() - getPMin().getY());
		volume *= (getPMax().getZ() - getPMin().getZ());
		return volume;
	}

	/**
	 * Tests whether this contains the given MBB3D.<br>
	 * If there are boundaries in common, the given MBB3D IS contained.
	 * 
	 * @see MBB3D#containsStrict(MBB3D, ScalarOperator)
	 * @param mbb
	 *            MBB3D for containment
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean contains(MBB3D mbb, ScalarOperator sop) {
		for (int i = 0; i < 3; i++) {
			if (sop.greaterThan(getPMin().getCoord(i), mbb.getPMin()
					.getCoord(i))
					|| sop.lessThan(getPMax().getCoord(i), mbb.getPMax()
							.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Tests whether this strictly contains the given MBB3D. <br>
	 * If there are boundaries in common the given MBB3D IS NOT contained.
	 * 
	 * @see MBB3D#contains(MBB3D, ScalarOperator)
	 * @param mbb
	 *            MBB3D for containment
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean containsStrict(MBB3D mbb, ScalarOperator sop) {
		for (int i = 0; i < 3; i++) {
			if (sop.greaterOrEqual(getPMin().getCoord(i), mbb.getPMin()
					.getCoord(i))
					|| sop.lessOrEqual(getPMax().getCoord(i), mbb.getPMax()
							.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Test whether the give point is contained in this.<br>
	 * Returns true if point is contained in boundary.
	 * 
	 * @param point
	 *            Point3D
	 * @return boolean - true if point is contained.
	 */
	public boolean contains(Point3D point, ScalarOperator sop) { // Dag
		for (int i = 0; i < 3; i++) {
			if (sop.greaterThan(getPMin().getCoord(i), point.getCoord(i))
					|| sop.lessThan(getPMax().getCoord(i), point.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Test whether the give point is STRICT contained in this. Returns false if
	 * point is contained in boundary.
	 * 
	 * @param point
	 *            Point3D
	 * @return boolean - true if point is contained in the inner of this.
	 */
	public boolean containsStrict(Point3D point, ScalarOperator sop) { // Dag
		for (int i = 0; i < 3; i++) {
			if (sop.greaterOrEqual(getPMin().getCoord(i), point.getCoord(i))
					|| sop
							.lessOrEqual(getPMax().getCoord(i), point
									.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Copies the MBB3D.
	 * 
	 * @return MBB3D - deep copy of this.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D copy() {
		return new MBB3D(new Point3D(getPMin()), new Point3D(getPMax()));
	}

	/**
	 * Checks whether this MBB3D is equal to the given MBB3D. Strong typed test
	 * method.
	 * 
	 * @param mbb
	 *            MBB3D for test
	 * @return boolean - true if equal.
	 */
	public boolean isEqual(MBB3D mbb, ScalarOperator sop) {
		if (!getPMin().isEqual(mbb.getPMin(), sop))
			return false;
		if (!getPMax().isEqual(mbb.getPMax(), sop))
			return false;

		return true;
	}

	/**
	 * Returns array of segments of this.
	 * 
	 * @return Segment3D[] - segments of this.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0, 1 or 2.
	 */
	public Set<Segment3D> getSegments(ScalarOperator sop) { // Dag

		Point3D[] pt = { this.getPMin(), this.getPMax() };
		Point3D[] pts = new Point3D[4];
		int m = 1;

		EquivalentableHashSet segmentHS = new EquivalentableHashSet(15, sop,
				Equivalentable.GEOMETRY_EQUIVALENT);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {

				pts[0] = new Point3D(pt[i]);
				pts[1] = new Point3D(pt[i]);
				pts[2] = new Point3D(pt[i]);
				pts[3] = new Point3D(pt[i]);

				if (i == 1)
					m = 0;

				int k = (j + 1) % 3;
				pts[1].setCoord(j, pt[m].getCoord(j));
				pts[3].setCoord(k, pt[m].getCoord(k));
				pts[2].setCoord(j, pt[m].getCoord(j));
				pts[2].setCoord(k, pt[m].getCoord(k));

				for (int r = 0; r < 4; r++) {
					int s = (r + 1) % 4;
					Segment3D seg = new Segment3D(pts[r], pts[s], sop);
					segmentHS.add(seg);
				}
			}
		}
		return segmentHS;
	}

	/**
	 * Returns array of faces of this as field of Rectangle3D objects. Returns
	 * empty array if BoundingBox is a point, array of size one if BouningBox is
	 * flat.
	 * 
	 * @return Rectangle3D[] - faces of this.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0, 1 or 2.
	 */
	public Rectangle3D[] getFaces(ScalarOperator sop) { // Dag

		Point3D[] pt = { this.getPMin(), this.getPMax() };
		Point3D[] pts = new Point3D[4];

		// Point BoundingBox -> no faces
		if (pt[0].equals(pt[1])) {
			return new Rectangle3D[0];
		}
		// flat BoundingBox -> one face
		if (sop.equal(pt[0].getX(), pt[1].getX())) {
			for (int i = 0; i < 4; i++)
				pts[i] = new Point3D(pt[0]);
			pts[1].setCoord(1, pt[1].getY());
			pts[2].setCoord(1, pt[1].getY());
			pts[2].setCoord(2, pt[1].getZ());
			pts[3].setCoord(2, pt[1].getZ());
			return new Rectangle3D[] { new Rectangle3D(pts[0], pts[1], pts[2],
					pts[3]) };
		}
		if (sop.equal(pt[0].getY(), pt[1].getY())) {
			for (int i = 0; i < 4; i++)
				pts[i] = new Point3D(pt[0]);
			pts[1].setCoord(1, pt[1].getX());
			pts[2].setCoord(1, pt[1].getX());
			pts[2].setCoord(2, pt[1].getZ());
			pts[3].setCoord(2, pt[1].getZ());
			return new Rectangle3D[] { new Rectangle3D(pts[0], pts[1], pts[2],
					pts[3]) };
		}
		if (sop.equal(pt[0].getZ(), pt[1].getZ())) {
			for (int i = 0; i < 4; i++)
				pts[i] = new Point3D(pt[0]);
			pts[1].setCoord(1, pt[1].getX());
			pts[2].setCoord(1, pt[1].getX());
			pts[2].setCoord(2, pt[1].getY());
			pts[3].setCoord(2, pt[1].getY());
			return new Rectangle3D[] { new Rectangle3D(pts[0], pts[1], pts[2],
					pts[3]) };
		}
		// regular BoundingBox -> six faces
		int m = 1;
		Rectangle3D[] rectangles = new Rectangle3D[6];
		int counter = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				pts[0] = new Point3D(pt[i]);
				pts[1] = new Point3D(pt[i]);
				pts[2] = new Point3D(pt[i]);
				pts[3] = new Point3D(pt[i]);
				if (i == 1)
					m = 0;
				int k = (j + 1) % 3;
				pts[1].setCoord(j, pt[m].getCoord(j));
				pts[3].setCoord(k, pt[m].getCoord(k));
				pts[2].setCoord(j, pt[m].getCoord(j));
				pts[2].setCoord(k, pt[m].getCoord(k));

				rectangles[counter] = new Rectangle3D(pts[0], pts[1], pts[2],
						pts[3]);
				counter++;
			}
		}
		return rectangles;
	}

	/**
	 * Returns the center of this.
	 * 
	 * @return Point3D - point representing the center.
	 */
	public Point3D getCenter() {
		double x = ((getPMax().getX() - getPMin().getX()) / 2)
				+ getPMin().getX();
		double y = ((getPMax().getY() - getPMin().getY()) / 2)
				+ getPMin().getY();
		double z = ((getPMax().getZ() - getPMin().getZ()) / 2)
				+ getPMin().getZ();
		return new Point3D(x, y, z);
	}

	/**
	 * Returns the corner points as array.
	 * 
	 * @return corners - as array of Point3D.
	 */
	public Point3D[] getCorners() { // Dag
		Point3D[] corners = new Point3D[8];
		corners[0] = getPMin();
		corners[1] = getPMax();
		corners[2] = new Point3D(corners[0].getCoord(0),
				corners[0].getCoord(1), corners[1].getCoord(2));
		corners[3] = new Point3D(corners[0].getCoord(0),
				corners[1].getCoord(1), corners[0].getCoord(2));
		corners[4] = new Point3D(corners[1].getCoord(0),
				corners[0].getCoord(1), corners[0].getCoord(2));
		corners[5] = new Point3D(corners[1].getCoord(0),
				corners[1].getCoord(1), corners[0].getCoord(2));
		corners[6] = new Point3D(corners[1].getCoord(0),
				corners[0].getCoord(1), corners[1].getCoord(2));
		corners[7] = new Point3D(corners[0].getCoord(0),
				corners[1].getCoord(1), corners[1].getCoord(2));
		return corners;
	}

	/**
	 * Returns the max point.
	 * 
	 * @return max point - as Point3D.
	 */
	public Point3D getPMax() {
		return pMax;
	}

	/**
	 * Returns the min point.
	 * 
	 * @return min point - as Point3D.
	 */
	public Point3D getPMin() {
		return pMin;
	}

	/**
	 * Returns Euclidean distance of PMin-PMax (value for approx. size/diameter
	 * of object).
	 * 
	 * @return double - diagonal expansion of MBB.
	 */
	public double getDiagonalLength() { // Dag
		return this.getPMin().euclideanDistance(this.getPMax());
	}

	/**
	 * Returns the type if this as a SimpleGeoObj.
	 * 
	 * @return MBB3D always.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.MBB3D;
	}

	/**
	 * Tests whether this is inside the given MBB3D.<br>
	 * If there are boundaries in common this IS inside the given MBB3D.
	 * 
	 * @see MBB3D#insideStrict(MBB3D, ScalarOperator)
	 * @param mbb
	 *            MBB3D for inclusion
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean inside(MBB3D mbb, ScalarOperator sop) {
		for (int i = 0; i < 3; i++) {
			if (sop.lessThan(getPMin().getCoord(i), mbb.getPMin().getCoord(i))
					|| sop.greaterThan(getPMax().getCoord(i), mbb.getPMax()
							.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Tests whether this strictly is inside the given MBB3D. If there are
	 * boundaries in common, this is NOT inside the given MBB3D.
	 * 
	 * @see MBB3D#inside(MBB3D, ScalarOperator)
	 * @param mbb
	 *            - MBB3D for inclusion
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean insideStrict(MBB3D mbb, ScalarOperator sop) {
		for (int i = 0; i < 3; i++) {
			if (sop.lessOrEqual(getPMin().getCoord(i), mbb.getPMin()
					.getCoord(i))
					|| sop.greaterOrEqual(getPMax().getCoord(i), mbb.getPMax()
							.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Tests whether this intersects with the given MBB3D.<br>
	 * If the two MBBs have a boundary in common, they DO intersect.
	 * 
	 * @see MBB3D#intersectsStrict(MBB3D, ScalarOperator)
	 * @param mbb
	 *            MBB3D for intersection
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(MBB3D mbb, ScalarOperator sop) {
		for (int i = 0; i < 3; i++) {
			if ((sop.greaterThan(getPMin().getCoord(i), mbb.getPMax().getCoord(
					i)))
					|| (sop.lessThan(getPMax().getCoord(i), mbb.getPMin()
							.getCoord(i))))
				return false;
		}
		return true;
	}

	/**
	 * Tests whether this strictly intersects with the given MBB3D.<br>
	 * If the two MBBs have a boundary in common they do NOT intersect.
	 * 
	 * @see MBB3D#intersects(MBB3D, ScalarOperator)
	 * @param mbb
	 *            MBB3D for intersection
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersectsStrict(MBB3D mbb, ScalarOperator sop) {
		for (int i = 0; i < 3; i++) {
			if (sop.greaterOrEqual(getPMin().getCoord(i), mbb.getPMax()
					.getCoord(i))
					|| sop.lessOrEqual(getPMax().getCoord(i), mbb.getPMin()
							.getCoord(i)))
				return false;
		}
		return true;
	}

	/**
	 * Computes the intersection MBB3D between this and the given MBB3D.<br>
	 * There is an intersectsStrict test first - so all computed intersections
	 * are valid MBB3D's !
	 * 
	 * @param mbb
	 *            MBB3D for intersection computation
	 * @return MBB3D - intersection MBB3D (null if no intersection).
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D intersection(MBB3D mbb, ScalarOperator sop) {
		if (!this.intersectsStrict(mbb, sop))
			return null;

		double[] pmin = new double[3];
		double[] pmax = new double[3];
		double min, max;

		for (int i = 0; i < 3; i++) {
			min = sop.greaterThan(mbb.getPMin().getCoord(i), this.getPMin()
					.getCoord(i)) ? mbb.getPMin().getCoord(i) : this.getPMin()
					.getCoord(i);
			max = sop.lessThan(mbb.getPMax().getCoord(i), this.getPMax()
					.getCoord(i)) ? mbb.getPMax().getCoord(i) : this.getPMax()
					.getCoord(i);
			pmin[i] = min;
			pmax[i] = max;
		}
		return new MBB3D(new Point3D(pmin[0], pmin[1], pmin[2]), new Point3D(
				pmax[0], pmax[1], pmax[2]));
	}

	/**
	 * Computes resulting object of intersection between this and the given
	 * Line3D.<br>
	 * Returns <code>null</code>, Point3D or Segment3D object.
	 * 
	 * @param line
	 *            Line3D for intersection computation
	 * @return SimpleGeoObj - resulting intersection object.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, ScalarOperator sop)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0, 1 or 2.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Line3D line, ScalarOperator sop) { // Dag

		/*
		 * normvectors for xy, yz and xz parallel planes - attention: order is
		 * important!
		 */
		Vector3D[] norm = { new Vector3D(0, 0, 1), new Vector3D(1, 0, 0),
				new Vector3D(0, 1, 0) };
		Vector3D[] p = { this.getPMin().getVector(), this.getPMax().getVector() };

		EquivalentableHashSet pointHS = new EquivalentableHashSet(10, sop,
				Equivalentable.GEOMETRY_EQUIVALENT);
		Plane3D plane = null;

		Point3D[] pt = { this.getPMin(), this.getPMax() };
		Point3D[] pts = { new Point3D(pt[0]), new Point3D(pt[0]),
				new Point3D(pt[0]), new Point3D(pt[0]) };
		int m = 1;

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				plane = new Plane3D(norm[j], p[i], sop);

				SimpleGeoObj obj = line.intersection(plane, sop);
				if (obj != null) {
					if (obj.getType() == SimpleGeoObj.LINE3D) { // obj == line

						// get points for according mbb face (rectangle)
						if (i == 1) {
							m = 0;
							pts[0] = new Point3D(pt[i]);
							pts[1] = new Point3D(pt[i]);
							pts[2] = new Point3D(pt[i]);
							pts[3] = new Point3D(pt[i]);
						}
						int k = (j + 1) % 3;
						pts[1].setCoord(j, pt[m].getCoord(j));
						pts[3].setCoord(k, pt[m].getCoord(k));
						pts[2].setCoord(j, pt[m].getCoord(j));
						pts[2].setCoord(k, pt[m].getCoord(k));

						Rectangle3D rec = new Rectangle3D(pts[0], pts[1],
								pts[2], pts[3]);
						return rec.intersectionInPlane(line, sop);

					} // obj must be of type Point3D
					Point3D intersectionPoint = (Point3D) obj;
					if (this.contains(intersectionPoint, sop)) {
						pointHS.add(intersectionPoint);
					}
				}
			}
		}

		Point3D[] points = (Point3D[]) pointHS.toArray(new Point3D[pointHS
				.size()]);
		if (points.length == 1)
			return points[0];
		if (points.length == 2)
			return new Segment3D(points[0], points[1], sop);

		// else: pointHS.size() == 0 -> no intersection
		return null;
	}

	/**
	 * Computes the intersection MBB3D between this and the given Plane3D.<br>
	 * Returns <code>null</code>, Point3D or Segment3D, Triangle3D or
	 * Wireframe3D object.
	 * 
	 * @param plane
	 *            Plane3D for intersection computation
	 * @return SimpleGeoObj - result of intersection.
	 * @throws IllegalStateException
	 *             - if the index of a Point3D in a Rectangle3D is not in the
	 *             interval [0, 3]. This exception originates in the getPoint
	 *             (int index) method of the class Rectangle3D called by this
	 *             method.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public SimpleGeoObj intersection(Plane3D plane, ScalarOperator sop) { // Dag

		Rectangle3D[] face = this.getFaces(sop);
		Wireframe3D resultWF = new Wireframe3D(sop);
		int length = face.length;
		if (length == 0)
			return this.getPMin().intersection(plane, sop);

		for (int i = 0; i < length; i++) {

			SimpleGeoObj obj = face[i].intersection(plane, sop);
			// Here IllegalStateExceptions can be thrown.
			if (obj != null) {
				switch (obj.getType()) {
				case SimpleGeoObj.POINT3D:
					resultWF.add(((Point3D) obj));
					break;
				case SimpleGeoObj.SEGMENT3D:
					resultWF.add(((Segment3D) obj));
					// Here an IllegalStateException can be thrown signaling
					// problems with the dimensions of the wireframe.
					break;
				case SimpleGeoObj.WIREFRAME3D:
					return ((Wireframe3D) obj);
				}
			}
		}

		int nodeCount = resultWF.countNodes();
		if (nodeCount == 0)
			return null;
		Point3D[] p = resultWF.getPoints();
		switch (nodeCount) {
		case 1:
			return p[0];
		case 2:
			return new Segment3D(p[0], p[1], sop);
		case 3:
			return new Triangle3D(p[0], p[1], p[2], sop);
		default: // nodeCount must be 4 to 6
			return resultWF;
		}
	}

	/**
	 * Computes the margin of this.
	 * 
	 * @return double - margin.
	 */
	public double margin() {
		Point3D max = getPMax();
		Point3D min = getPMin();
		return max.getX() - min.getX() + max.getY() - min.getY() + max.getZ()
				- min.getZ();
	}

	/**
	 * Computes the minimal distance between this and the given point.
	 * 
	 * @param point
	 *            Point3D point for distance
	 * @return double - minimal distance.
	 */
	public double minDist(Point3D point, ScalarOperator sop) {
		return Math.sqrt(minDistSquare(point, sop));
	}

	/**
	 * Computes the minimal distance square between this and the given point.
	 * According to Roussopoulos Nick: Nearest Neighbor Queries - MINDIST
	 * 
	 * @param point
	 *            Point3D defining a point for distance
	 * @return double - minimal distance.
	 */
	public double minDistSquare(Point3D point, ScalarOperator sop) {
		double min = 0.0;
		double ri = 0.0;

		for (int i = 0; i < 3; i++) {
			if (sop.lessThan(point.getCoord(i), this.getPMin().getCoord(i)))
				ri = this.getPMin().getCoord(i);
			else {
				if (sop.greaterThan(point.getCoord(i), this.getPMax().getCoord(
						i)))
					ri = this.getPMax().getCoord(i);
				else
					ri = point.getCoord(i);
			}
			min = min + ((point.getCoord(i) - ri) * (point.getCoord(i) - ri));
		}
		return min;
	}

	/**
	 * Sets the max point.
	 * 
	 * @param pMax
	 *            Point3D
	 * @throws IllegalArgumentException
	 *             if the max point is less or equal to the current min point.
	 */
	public void setPMax(Point3D pMax) {
		if (!testMaxPoint(pMax)) {
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.pmaxpmin"));
		}

		this.pMax = pMax;
	}

	/**
	 * Sets the min point.
	 * 
	 * @param pMin
	 *            Point3D
	 * @throws IllegalArgumentException
	 *             if the min point is greater or equal to the current max
	 *             point.
	 */
	public void setPMin(Point3D pMin) {
		if (!testMinPoint(pMin)) {
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.pmaxpminsmall"));
		}
		this.pMin = pMin;
	}

	/**
	 * Computes the union of this and the given MBB3D.
	 * 
	 * @param mbb
	 *            MBB3D for union
	 * @return MBB3D - union MBB3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D union(MBB3D mbb, ScalarOperator sop) {
		double[] min = new double[3];
		double[] max = new double[3];

		for (int i = 0; i < 3; i++) {
			if (sop.lessOrEqual(this.getPMin().getCoord(i), mbb.getPMin()
					.getCoord(i)))
				min[i] = this.getPMin().getCoord(i);
			else
				min[i] = mbb.getPMin().getCoord(i);
			if (sop.greaterOrEqual(this.getPMax().getCoord(i), mbb.getPMax()
					.getCoord(i)))
				max[i] = this.getPMax().getCoord(i);
			else
				max[i] = mbb.getPMax().getCoord(i);
		}
		return new MBB3D(new Point3D(min[0], min[1], min[2]), new Point3D(
				max[0], max[1], max[2]));
	}

	/**
	 * Test whether the given point is greater as current PMin.
	 * 
	 * @param pmax
	 *            Point3D to be tested
	 * @return boolean - true if greater, false otherwise.
	 */
	protected boolean testMaxPoint(Point3D pmax) {
		Point3D p = getPMin();
		for (int i = 0; i < 3; i++) {
			if (p.getCoord(i) > pmax.getCoord(i))
				return false;
		}
		return true;
	}

	/**
	 * Test whether the given point is smaller as current PMax.
	 * 
	 * @param pmin
	 *            Point3D to be tested
	 * @return boolean - true if smaller, false otherwise.
	 */
	protected boolean testMinPoint(Point3D pmin) {
		Point3D p = getPMax();
		for (int i = 0; i < 3; i++) {
			if (p.getCoord(i) < pmin.getCoord(i))
				return false;
		}
		return true;
	}

	/**
	 * Returns this.
	 * 
	 * @return MBB3D - this.
	 * 
	 * @see db3d.dbms.structure.GeoObj#getMBB()
	 */
	public MBB3D getMBB() {
		return this;
	}

	/**
	 * Creates and returns the bounding box of a list of given bounding boxes.
	 * 
	 * @param boxes
	 *            The box list of which the bounding box is to be computed.
	 *            Please note that all objects contained in this list must be of
	 *            type <code>MBB3D</code>, otherwise a
	 *            <code>ClassCastException</code> will occur.
	 * 
	 * @return MBB3D - the bounding box of the given boxes if <code>boxes</code>
	 *         is not empty, <code>null</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */

	public static MBB3D getMBB(List<MBB3D> boxes) {
		Iterator<MBB3D> it = boxes.iterator();

		if (!it.hasNext())
			return null;

		Point3D p;
		MBB3D box;

		// Initialization
		box = it.next();
		p = box.pMin;
		double min_x = p.getX();
		double min_y = p.getY();
		double min_z = p.getZ();
		p = box.pMax;
		double max_x = p.getX();
		double max_y = p.getY();
		double max_z = p.getZ();

		// Iterate over the remaining boxes
		while (it.hasNext()) {
			box = it.next();
			p = box.pMin;
			if (p.getX() < min_x)
				min_x = p.getX();
			if (p.getY() < min_y)
				min_y = p.getY();
			if (p.getZ() < min_z)
				min_z = p.getZ();
			p = box.pMax;
			if (p.getX() > max_x)
				max_x = p.getX();
			if (p.getY() > max_y)
				max_y = p.getY();
			if (p.getZ() > max_z)
				max_z = p.getZ();
		}

		return new MBB3D(new Point3D(min_x, min_y, min_z), new Point3D(max_x,
				max_y, max_z));
	}

	/**
	 * Reads point coordinates from an external source and assignes them to the
	 * min and max points.
	 * 
	 * @param in
	 *            ObjectInput from which the coordinate values are read
	 * @throws IOException
	 *             if an input error occurred.
	 * @throws ClassNotFoundException
	 *             if the class type of the input object was not found.
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.pMin = (Point3D) in.readObject();
		this.pMax = (Point3D) in.readObject();
	}

	/**
	 * Writes the coordinates of the min and max points to an external
	 * ObjectOutput.
	 * 
	 * @param out
	 *            ObjectOutput to which the coordinate values are written
	 * @throws IOException
	 *             if an output error occurred.
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getPMin());
		out.writeObject(getPMax());
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "MBB3D [pMax=" + pMax + ", pMin=" + pMin + "]";
	}

	@Override
	/**
	 * Returns the hash code of this.
	 * @return int - hash code of this.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pMax == null) ? 0 : pMax.hashCode());
		result = prime * result + ((pMin == null) ? 0 : pMin.hashCode());
		return result;
	}

	@Override
	/**
	 * Tests if this is equal to a given object.
	 * @param obj Object for test
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBB3D other = (MBB3D) obj;
		if (pMax == null) {
			if (other.pMax != null)
				return false;
		} else if (!pMax.equals(other.pMax))
			return false;
		if (pMin == null) {
			if (other.pMin != null)
				return false;
		} else if (!pMin.equals(other.pMin))
			return false;
		return true;
	}

}
