package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;

import de.uos.igf.db3d.dbms.exceptions.DB3DException;
import de.uos.igf.db3d.dbms.spatials.api.Component3D;
import de.uos.igf.db3d.dbms.spatials.api.Curve3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * SegmentNet3D is the subclass of Net3DAbst representing a segment net with
 * several components. All components are referenced as SegmentNet3DComponent
 * objects.
 * 
 * @autor Markus Jahn
 * 
 */
public class Segment3DNet extends Net3DAbst implements Curve3D {

	/* components of this */
	protected Segment3DComponent[] components;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 */
	public Segment3DNet(GeoEpsilon epsilon) {
		super(epsilon);
		this.components = new Segment3DComponent[0];
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the SegmentNetBuilder class.
	 * 
	 * @param components
	 *            SegmentNet3DComp[]
	 * @param epsilon
	 *            GeoEpsilon
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Segment3DNet(Segment3DComponent[] components, GeoEpsilon epsilon) {
		super(epsilon);
		for (Segment3DComponent component : components)
			this.addComponent(component);
		this.updateMBB();
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given SegmentNet3D is not copied - so this is a free SegmentNet3D. It is
	 * not registered in the Space3D and the corresponding thematic is gone
	 * away!
	 * 
	 * @param net
	 *            SegmentNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Segment3DNet(Segment3DNet net) {
		super(net.epsilon);
		for (int i = 0; i < net.components.length; i++)
			this.addComponent(net.components[i]);
		this.updateMBB();
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
	 * Returns the components of this net.
	 * 
	 * @return SegmentNet3DComponent[] - array of components.
	 */
	public Segment3DComponent[] getComponents() {
		return this.components;
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param id
	 *            int index
	 * @return SegmentNet3DComp or <code>null</code>, if no component exists at
	 *         index position.
	 */
	public Segment3DComponent getComponent(int id) {
		return this.components[id];
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param component
	 *            SegmentNet3DComp to be added
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(Segment3DComponent component) {
		component.id = this.components.length;
		Segment3DComponent[] temp = new Segment3DComponent[this.components.length + 1];
		for (int i = 0; i < this.components.length; i++)
			temp[i] = this.components[i];
		temp[this.components.length] = component;
		component.net = this;
		this.components = temp;
		this.updateMBB();
	}

	/**
	 * Removes the given component from the net.
	 * 
	 * @param component
	 *            SegmentNet3DComp to be removed
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void removeComponent(Segment3DComponent component) {
		Segment3DComponent[] temp = new Segment3DComponent[this.components.length - 1];
		int x = 0;
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i] != component) {
				temp[x] = this.components[i];
				x++;
			}
		}
		this.components = temp;
	}

	/**
	 * Creates and returns a new component of the net.
	 * 
	 * @return SegmentNet3DComp that has been created.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Segment3DComponent createComponent() {
		Segment3DComponent component = new Segment3DComponent(this.epsilon);
		this.addComponent(component);
		return component;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned net will have the components with the given indexes.<br>
	 * The <code>this</code> net will have the remaining components.
	 * 
	 * @param ids
	 *            int[]
	 * @return SegmentNet3D - new SegmentNet3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Segment3DNet splitSegmentNet(int[] ids, GeoEpsilon epsilon) {
		Segment3DComponent[] newSegmentNetComps = new Segment3DComponent[ids.length];
		for (int i = 0; i < ids.length; i++) {
			newSegmentNetComps[i] = this.components[i];
			this.removeComponent(this.components[i]);
		}
		return new Segment3DNet(newSegmentNetComps, epsilon);
	}

	/**
	 * Computes and returns the Euler number for this net.
	 * 
	 * @return int - Euler number.
	 */
	public int getEuler() {
		// Euler formula: vertices - edges + faces
		int euler = 0;

		for (int i = 0; i < this.components.length; i++)
			euler += this.components[i].getEuler();

		return euler;
	}

	/**
	 * Returns the length of this net as the sum of the length of its
	 * components.
	 * 
	 * @return double - length of the net.
	 */
	public double getLength() {
		double temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].getLength();

		return temp;
	}

	/**
	 * Returns the number of elements in this net.
	 * 
	 * @return int number of elements.
	 */
	public int countElements() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countElements();

		return temp;
	}

	/**
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countVertices();

		return temp;
	}

	/**
	 * Returns the number of edges in this net.
	 * 
	 * @return int - number of edges.
	 */
	public int countEdges() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countEdges();

		return temp;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws DB3DException
	 *             - if the type if the <code>SimpleGeoObj</code> resulting from
	 *             the intersection of the components of this and the given
	 *             <code>Plane3D</code> cannot be identified.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
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
	 *             Point3D, GeoEpsilon).
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Plane3D plane) throws DB3DException {
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
	public boolean intersects(MBB3D mbb) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].intersects(mbb))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given point geometrically.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Point3D point) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].contains(point))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean contains(Segment3D seg) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].contains(seg))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether all components in the net are orientation consistent.
	 * 
	 * @return boolean - true if orientation consistent, false otherwise.
	 */
	public boolean isOrientationConsistent() {
		for (int i = 0; i < components.length; i++)
			if (!components[i].isOrientationConsistent())
				return false;

		return true;
	}

	/**
	 * Returns the type of this as as <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * 
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.CURVE_NET_C_E3D;
	}

	/**
	 * Updates the MBB of this net.<br>
	 * Iterates over all components updating and union their mbbs.<br>
	 * Sets the updated MBB in the abstract SpatialObject.<br>
	 * Updates the index in which the net is.
	 * 
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	protected void updateMBB() {
		MBB3D neu = this.components[0].mbb;
		for (int i = 1; i < this.components.length; i++)
			neu = neu.union(this.components[i].mbb, this.epsilon);
		this.mbb = neu;
	}

	/**
	 * Searches for an element with the given id in the components of this and
	 * returns it. If it was not found, returns <code>null</code>.
	 * 
	 * @return SimpleGeoObj - element with the given id.
	 */
	public Segment3DElement getElement(int id) {
		for (Segment3DComponent component : components) {

			/*
			 * TODO: which is faster: tempComp = comp.getElement(id) and then
			 * return tempComp or this method without direct assignment but if
			 * run it twice if an element was found?
			 */
			if (component.getElement(id) != null)
				return component.getElement(id);
		}
		return null;
	}

}