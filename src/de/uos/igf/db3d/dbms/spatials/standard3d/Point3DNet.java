package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Sample3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * PointNet3D is the concrete subclass of SpatialObject3D representing a point
 * net with several components. All components are referenced as PointNet3DComp
 * objects.
 * 
 * @author Markus Jahn
 * 
 */
public class Point3DNet extends Net3DAbst implements Sample3D {

	/* components of this */
	protected Point3DComponent[] components;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            geometric error
	 */
	public Point3DNet(GeoEpsilon epsilon) {
		super(epsilon);
		this.components = new Point3DComponent[0];
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the PointNetBuilder class.
	 * 
	 * @param components
	 *            PointNet3DComp[]
	 * @param epsilon
	 *            geometric error
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Point3DNet(Point3DComponent[] components, GeoEpsilon epsilon) {
		this(epsilon);
		for (Point3DComponent component : components)
			this.addComponent(component);
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given PointNet3D is not copied - so this is a free PointNet3D. It is not
	 * registered in the Space3D and the corresponding thematic is gone away ! <br>
	 * 
	 * @param net
	 *            PointNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Point3DNet(Point3DNet net) {
		super(net.epsilon);
		for (int i = 0; i < net.components.length; i++)
			this.addComponent(net.components[i]);
	}

	/**
	 * Returns the number of components in the net.
	 * 
	 * @return int - number of components.
	 */
	public int countComponents() {
		return this.components.length;
	}

	/**
	 * Returns the number of elements in the net.
	 * 
	 * @return int - number of elements.
	 */
	public int countElements() {
		int sum = 0;
		for (int i = 0; i < this.components.length; i++)
			sum = sum + this.components[i].countElements();

		return sum;
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param id
	 *            int index
	 * @return PointNet3DComp with the given index.
	 */
	public Point3DComponent getComponent(int id) {
		return this.components[id];
	}

	/**
	 * Returns the components of this net.
	 * 
	 * @return PointNet3DComponent[] - array of components.
	 */
	public Point3DComponent[] getComponents() {
		return this.components;
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param component
	 *            PointNet3DComp to be added
	 */
	public void addComponent(Point3DComponent component) {
		component.id = this.components.length;
		Point3DComponent[] temp = new Point3DComponent[this.components.length + 1];
		for (int i = 0; i < this.components.length; i++)
			temp[i] = this.components[i];
		temp[this.components.length] = component;
		component.net = this;
		this.components = temp;
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param component
	 *            PointNet3DComp to be removed
	 */
	public void removeComponent(Point3DComponent component) {
		Point3DComponent[] temp = new Point3DComponent[this.components.length - 1];
		int x = 0;
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i] != component) {
				temp[x] = this.components[i];
				this.components[i].id = x;
				x++;
			}
		}
		this.components = temp;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned net will have the components with given indexes.<br>
	 * The <code>this</code> net will have the remaining components.
	 * 
	 * @param ids
	 *            int[]
	 * @return PointNet3D - new PointNet3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Point3DNet splitPointNet(int[] ids) { // Dag
		Point3DComponent[] newPointNetComps = new Point3DComponent[ids.length];

		for (int i = 0; i < ids.length; i++) {
			newPointNetComps[i] = this.components[i];
			this.removeComponent(this.components[i]);
		}
		return new Point3DNet(newPointNetComps, epsilon);
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Computes and returns the Euler number for this net.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() { // Dag
		// Euler formular: vertices - edges + faces

		// number of PointElt3D elements = vertices = Euler
		return this.countVertices();
	}

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		int count = 0;
		for (int i = 0; i < this.components.length; i++)
			count += this.components[i].countVertices();

		return count;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Plane3D plane) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].intersects(plane))
				return true;
		}
		return false;
	}

	/**
	 * Test whether this intersects with given line.
	 * 
	 * @param line
	 *            Line3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(Line3D line) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].intersects(line))
				return true;
		}
		return false;
	}

	/**
	 * Test whether this intersects with given bounding box.
	 * 
	 * @param mbb
	 *            MBB3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 */
	public boolean intersects(MBB3D mbb) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].intersects(mbb))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this geometrically contains the given point.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if is contained, false otherwise.
	 */
	public boolean contains(Point3DElement point) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].contains(point))
				return true;
		}
		return false;
	}

	/**
	 * Returns the type of this as as <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SAMPLE_NET_C_E3D;
	}

}
