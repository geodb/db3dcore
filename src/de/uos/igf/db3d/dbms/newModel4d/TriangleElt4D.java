package de.uos.igf.db3d.dbms.newModel4d;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;

public class TriangleElt4D {	


	private static final long serialVersionUID = 4121395257446711350L;
	
	/* geometry */
	private Point3D zero;
	private Point3D one;
	private Point3D two;
	
	/* neighbour 0 */
	private TriangleElt3D eltZero;

	/* neighbour 1 */
	private TriangleElt3D eltOne;

	/* neighbour 2 */
	private TriangleElt3D eltTwo;

	/* id of this - unique in whole net */
	private int id;

	/**
	 * Constructor. <br>
	 * Constructs a TriangleElt4D as a Triangle4D with given points.
	 * 
	 * @param points
	 *            Point3D array.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt4D(Point3D[] points, ScalarOperator sop)
			throws IllegalArgumentException {
		this.eltZero = null;
		this.eltOne = null;
		this.eltTwo = null;
		
		this.zero = points[0];
		this.one = points[1];
		this.two = points[2];
	}

	/**
	 * Constructor. <br>
	 * Constructs a TriangleElt3D as a Triangle3D with given points.
	 * 
	 * @param point1
	 *            Point3D.
	 * @param point2
	 *            Point3D.
	 * @param point3
	 *            Point3D.
	 * @param sop
	 *            ScalarOperator needed for validation.<br>
	 *            If ScalarOperator is <code>null</code>, no validation will
	 *            occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], ScalarOperator).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public TriangleElt4D(Point3D point1, Point3D point2, Point3D point3,
			ScalarOperator sop) {
		this(new Point3D[] { point1, point2, point3 }, sop);
	}
}
