package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * PointElt3D represents an element of a PointNet3D object.
 * 
 * @author Markus Jahn
 * 
 */
public class Point3DElement extends Point3D implements Element3D {

	/* id of this - unique in whole net */
	protected int id;

	/* enclosing net component */
	protected Point3DComponent component;

	/**
	 * Default constructor. Constructs a PointElt3D object as a Point3D with
	 * x,y,z = 0.0.
	 */
	public Point3DElement() {
		super();
	}

	/**
	 * Constructor for double coordinates. <br>
	 * Constructs a PointElt3D object as a Point3D with given x, y, z
	 * coordinates.
	 * 
	 * @param x
	 *            double value of x axis
	 * @param y
	 *            double value of y axis
	 * @param z
	 *            double value of z axis
	 */
	public Point3DElement(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Constructor for double coordinates. <br>
	 * Constructs a PointElt3D object as a Point3D with given x, y, z
	 * coordinates.
	 * 
	 * @param coords
	 *            double array with values x,y,z axis
	 */
	public Point3DElement(double[] coords) {
		super(coords);
	}

	/**
	 * Constructor.
	 * 
	 * @param point
	 *            Point3D
	 */
	public Point3DElement(Point3D point) {
		super(point);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public boolean hasNeighbour(int index) {
		return false;
	}

	@Override
	public boolean hasNeighbours() {
		return false;
	}

	@Override
	public Point3DElement[] getNeighbours() {
		return null;
	}

	@Override
	public Element3D getNeighbour(int index) {
		return null;
	}

	@Override
	public boolean isInterior() {
		return false;
	}

	@Override
	public Point3DComponent getComponent() {
		return component;
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SAMPLE_ELEMENT_3D;
	}

	@Override
	public GeoEpsilon getGeoEpsilon() {
		if (this.component != null) {
			return this.component.getGeoEpsilon();
		}
		return null;
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
