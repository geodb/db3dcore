package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Tetrahedron3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * TetrahedronElt3D represents an element of a TetrahedronNet3D object.<br>
 * A TetrahedronElt3D inherits the geometric attributes and methods of
 * Tetrahedron3D.<br>
 * Topological information is stored as the four neighbour tetrahedrons in the
 * net. <br>
 * Persistent through inheritance !
 * 
 * @author Markus Jahn
 * 
 */
public class Tetrahedron3DElement extends Tetrahedron3D implements Element3D {

	/* neighbours */
	protected Tetrahedron3DElement[] neighbours;

	/* id of this - unique in whole net */
	protected int id;

	/* enclosing net component */
	protected Tetrahedron3DComponent component;

	/**
	 * Constructor. <br>
	 * Constructs a TetrahedronElt3D as a Tetrahedron3D with given points.
	 * 
	 * @param points
	 *            Point3D array.
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3DElement(Point3D[] points, GeoEpsilon epsilon)
			throws IllegalArgumentException {
		super(points, epsilon);
		this.neighbours = new Tetrahedron3DElement[4];
	}

	/**
	 * Constructor. <br>
	 * Constructs a TetrahedronElt3D as a Tetrahedron3D with given points.
	 * 
	 * @param point1
	 *            Point3D.
	 * @param point2
	 *            Point3D.
	 * @param point3
	 *            Point3D.
	 * @param point4
	 *            Point3D.
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3DElement(Point3D point1, Point3D point2, Point3D point3,
			Point3D point4, GeoEpsilon epsilon) {
		this(new Point3D[] { point1, point2, point3, point4 }, epsilon);
	}

	/**
	 * Constructor. <br>
	 * 
	 * @param point
	 *            Point3D.
	 * @param triangle
	 *            Triangle3D.
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3DElement(Point3D point, Triangle3D triangle,
			GeoEpsilon epsilon) {
		this(new Point3D[] { new Point3D(point),
				new Point3D(triangle.getPoints()[0]),
				new Point3D(triangle.getPoints()[1]),
				new Point3D(triangle.getPoints()[2]) }, epsilon);
	}

	/**
	 * Constructor. <br>
	 * 
	 * @param seg1
	 *            Segment3D.
	 * @param seg2
	 *            Segment3D.
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - signals inappropriate parameters.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Tetrahedron3D from an
	 *             empty Point3D array whose length is not 4 or if the
	 *             validation of the Tetrahedron3D fails. The exception
	 *             originates in the constructor Tetrahedron3D(Point3D[],
	 *             GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3DElement(Segment3D seg1, Segment3D seg2,
			GeoEpsilon epsilon) {
		this(new Point3D[] { new Point3D(seg1.getPoints()[0]),
				new Point3D(seg1.getPoints()[1]),
				new Point3D(seg2.getPoints()[0]),
				new Point3D(seg2.getPoints()[1]) }, epsilon);
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param tetra
	 *            Tetrahedron3D
	 * @throws IllegalArgumentException
	 *             - if the validation of a tetrahedron fails. The exception
	 *             originates in the constructor Tetrahedroin3D(Point3D,
	 *             Point3D, Point3D, Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Tetrahedron3DElement(Tetrahedron3D tetra) {
		super(tetra);
		this.neighbours = new Tetrahedron3DElement[4];
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public boolean hasNeighbour(int id) {
		return this.neighbours[id] != null;
	}

	/**
	 * Returns the count of neighbour elements
	 * 
	 * @return int - count of neighbour elements.
	 */
	public int countNeighbours() {
		int count = 0;
		if (this.neighbours[0] != null)
			count++;
		if (this.neighbours[1] != null)
			count++;
		if (this.neighbours[2] != null)
			count++;
		if (this.neighbours[3] != null)
			count++;
		return count;
	}

	@Override
	public boolean hasNeighbours() {
		if (this.neighbours[0] != null || this.neighbours[1] != null
				|| this.neighbours[2] != null || this.neighbours[3] != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isInterior() {
		if (this.neighbours[0] != null && this.neighbours[1] != null
				&& this.neighbours[2] != null && this.neighbours[3] != null)
			return true;
		else
			return false;
	}

	@Override
	public Tetrahedron3DElement[] getNeighbours() {
		return this.neighbours;
	}

	@Override
	public Tetrahedron3DElement getNeighbour(int index) {
		return this.neighbours[index];
	}

	/**
	 * Returns whether this is a border element or not.
	 * 
	 * @return boolean - true if is a border element, false otherwise.
	 */
	public boolean isBorderElement() {

		for (int i = 0; i < 4; i++)
			if (this.neighbours[i] == null)
				return true;

		return false;
	}

	@Override
	public Tetrahedron3DComponent getComponent() {
		return component;
	}

	/**
	 * Sets the neighbour for corresponding index to <code>null</null>.  
	 * Returns index for removed neighbour information or -1 if given element was not a neighbour.
	 * 
	 * @param element
	 *            TetrahedronElt3D
	 * @param epsilon
	 *            GeoEpsilon
	 * @return int - index for removed neighbour.
	 */
	public int setNeighbourNull(Tetrahedron3DElement element, GeoEpsilon epsilon) {

		if (this.neighbours[0] != null
				&& this.neighbours[0].isGeometryEquivalent(element, epsilon)) {
			this.neighbours[0] = null;
			return 0;
		}
		if (this.neighbours[1] != null
				&& this.neighbours[1].isGeometryEquivalent(element, epsilon)) {
			this.neighbours[1] = null;
			return 1;
		}
		if (this.neighbours[2] != null
				&& this.neighbours[2].isGeometryEquivalent(element, epsilon)) {
			this.neighbours[2] = null;
			return 2;
		}
		if (this.neighbours[3] != null
				&& this.neighbours[3].isGeometryEquivalent(element, epsilon)) {
			this.neighbours[3] = null;
			return 3;
		}

		return -1;
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SOLID_ELEMENT_3D;
	}

	@Override
	public GeoEpsilon getGeoEpsilon() {
		return component.getGeoEpsilon();
	}

}
