package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.EmptyStackException;
import java.util.Stack;

import de.uos.igf.db3d.dbms.collections.FlagMap;
import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * TriangleElt3D represents an element of a TriangleNet3D object.<br>
 * A TriangleElt3D inherits the geometric attributes and methods of Triangle3D.<br>
 * Topological information is stored as the tree neighbour triangles in the net.<br>
 * Persistent through inheritance !<br>
 * 
 * @author Markus Jahn
 * 
 */
public class Triangle3DElement extends Triangle3D implements Element3D {

	/* neighbours */
	protected Triangle3DElement[] neighbours;

	/* id of this - unique in whole net */
	protected int id;

	/* enclosing net component */
	protected Triangle3DComponent component;

	/**
	 * Constructor. <br>
	 * Constructs a TriangleElt3D as a Triangle3D with given points.
	 * 
	 * @param segments
	 *            Point3D array.
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3DElement(Point3D[] points, GeoEpsilon epsilon)
			throws IllegalArgumentException {
		super(points, epsilon);
		this.neighbours = new Triangle3DElement[3];
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
	 * @param epsilon
	 *            GeoEpsilon needed for validation.<br>
	 *            If GeoEpsilon is <code>null</code>, no validation will occur.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Triangle3D from a
	 *             point array whose length is not 3 or the validation of the
	 *             constructed Triangle3D fails. The exception originates in the
	 *             constructor Triangle3D(Point3D[], GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3DElement(Point3D point1, Point3D point2, Point3D point3,
			GeoEpsilon epsilon) {
		this(new Point3D[] { point1, point2, point3 }, epsilon);
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param triangle
	 *            Triangle3D
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Triangle3DElement(Triangle3D triangle) {
		super(triangle);
		this.neighbours = new Triangle3DElement[3];
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
		return count;
	}

	@Override
	public boolean hasNeighbours() {
		if (this.neighbours[0] != null || this.neighbours[1] != null
				|| this.neighbours[2] != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isInterior() {
		if (this.neighbours[0] != null && this.neighbours[1] != null
				&& this.neighbours[2] != null)
			return true;
		else
			return false;
	}

	/**
	 * Sets the neighbour for corresponding index to <code>null</null>.  
	 * Returns index for removed neighbour information or -1 if given element was not neighbour.
	 * 
	 * @param element
	 *            TriangleElt3D
	 * @param epsilon
	 *            GeoEpsilon needed for validation
	 * @return int - index for removed neighbour.
	 */
	public int setNeighbourNull(Triangle3DElement element, GeoEpsilon epsilon) {

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

		return -1;
	}

	@Override
	public Triangle3DElement[] getNeighbours() {
		return neighbours;
	}

	@Override
	public Triangle3DElement getNeighbour(int index) {
		return neighbours[index];
	}

	@Override
	public Triangle3DComponent getComponent() {
		return component;
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SURFACE_ELEMENT_3D;
	}

	/**
	 * Checks whether neighbours of this have already been visited, makes them
	 * orientation consistent if not.
	 * 
	 * @param epsilon
	 *            GeoEpsilon needed for validation
	 * @param flags
	 *            FlagMap to store the visited neighbours
	 * @author Dag<br>
	 *         Revision: Edgar Butwilowski
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void makeNeighboursOrientationConsistent(GeoEpsilon epsilon,
			FlagMap flags) {
		Stack<Object[]> upcoming = new Stack<Object[]>();
		Triangle3DElement current = this;
		do {
			// set visited status
			flags.add(current);

			for (int i = 0; i < 3; i++) {
				Triangle3DElement nb = current.getNeighbour(i);
				if (nb != null && (!flags.check(nb))) {
					upcoming.push(new Object[] { i, nb, current });
				}
			}

			Object[] nbObj = null;
			try {
				nbObj = upcoming.pop();
			} catch (EmptyStackException ese) {
				// do nothing
			}
			int index = -1;
			Triangle3DElement nb = null;
			Triangle3DElement current2 = null;
			if (nbObj != null) {
				index = (Integer) nbObj[0];
				nb = (Triangle3DElement) nbObj[1];
				current2 = (Triangle3DElement) nbObj[2];
			}

			if ((nb != null) && (!flags.check(nb))) {
				// if not already visited

				// point indices of common edge (common edge has (in this)
				// direction p1->p2)
				int p1 = (index + 1) % 3;
				int p2 = (index + 2) % 3;

				int j = 0;

				// find nb's index j for opposite point of common edge
				for (j = 0; j < 3; j++)
					if (!(current2.points[p1].isEqual(nb.points[j], epsilon) || current2.points[p2]
							.isEqual(nb.points[j], epsilon)))
						break;

				// nb's index for first point of common edge
				j = (j + 1) % 3;

				if (current2.points[p1].isEqual(nb.points[j], epsilon))
					nb.invertOrientation();

				// orientNeighbours for nb
			}
			current = nb;

		} while (upcoming.size() != 0 || current != null);

	}

	/**
	 * Inverts the orientation of the vertices. Overrides invertOrientation
	 * method of Triangle3D - in addition to the orientation inversion it
	 * inverts the neighbours.
	 * 
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * 
	 * @see db3d.dbms.geom.Triangle3D#invertOrientation()
	 */
	public void invertOrientation() {
		super.invertOrientation();

		Triangle3DElement temp2 = this.neighbours[2];
		this.neighbours[2] = this.neighbours[1];
		this.neighbours[1] = temp2;
		if (this.component != null)
			this.component.oriented = false;
	}

	@Override
	public GeoEpsilon getGeoEpsilon() {
		return component.getGeoEpsilon();
	}

	@Override
	public String toString() {

		String string = "ID " + this.id + " " + super.toString();

		return string;
	}
	
	public void setID(int id) {
		this.id = id;
	}

}
