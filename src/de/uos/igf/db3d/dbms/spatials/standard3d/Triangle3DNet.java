package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Surface3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * TriangleNet3D is the concrete subclass of Net3DAbst representing a triangle
 * net with several components. All components are referenced as
 * TriangleNet3DComponent objects.
 * 
 * @author Markus Jahn
 * 
 */
public class Triangle3DNet extends Net3DAbst implements Surface3D {

	/** components of this */
	protected Triangle3DComponent[] components;

	/**
	 * Constructor.<br>
	 * 
	 * @param epsilon
	 *            GeoEpsilon needed for validation
	 */
	public Triangle3DNet(GeoEpsilon epsilon) {
		super(epsilon);
		this.components = new Triangle3DComponent[0];
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the TriangleNetBuilder class.
	 * 
	 * @param components
	 *            TriangleNet3DComp[]
	 * @param epsilon
	 *            GeoEpsilon needed for validation
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Triangle3DNet(Triangle3DComponent[] components, GeoEpsilon epsilon) {
		this(epsilon);
		for (Triangle3DComponent component : components)
			this.addComponent(component);
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given TriangleNet3D is not copied - so this is a free TriangleNet3D. It
	 * is not registered in the Space3D and the corresponding thematic is gone
	 * away !
	 * 
	 * @param net
	 *            TriangleNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Triangle3DNet(Triangle3DNet net) {
		super(net.epsilon);
		for (int i = 0; i < net.components.length; i++)
			this.addComponent(net.components[i]);
		for (int i = 0; i < this.components.length; i++)
			this.components[i].net = this;
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
	 * Returns the component with given index.
	 * 
	 * @param id
	 *            int
	 * @return TriangleNet3DComp.
	 */
	public Triangle3DComponent getComponent(int id) {
		return this.components[id];
	}

	/**
	 * Returns the components of this net.
	 * 
	 * @return TriangleNet3DComponent[] - array of components.
	 */
	public Triangle3DComponent[] getComponents() {
		return this.components;
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param component
	 *            TriangleNet3DComp to be added
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(Triangle3DComponent component) {
		component.id = this.components.length;
		Triangle3DComponent[] temp = new Triangle3DComponent[this.components.length + 1];
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
	 *            TriangleNet3DComp to be removed
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void removeComponent(Triangle3DComponent component) {
		Triangle3DComponent[] temp = new Triangle3DComponent[this.components.length - 1];
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
	 * <code>This</code> will afterwards contain the remaining components.
	 * 
	 * @param ids
	 *            int[]
	 * @return TriangleNet3D - new TriangleNet3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Triangle3DNet splitTriangleNet(int[] ids, GeoEpsilon epsilon) { // Dag
		Triangle3DComponent[] newTriangleNetComps = new Triangle3DComponent[ids.length];
		for (int i = 0; i < ids.length; i++) {
			newTriangleNetComps[i] = this.components[i];
			this.removeComponent(this.components[i]);
		}
		return new Triangle3DNet(newTriangleNetComps, epsilon);
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
	 * Returns the area of this net as the sum of the areas of its components.
	 * 
	 * @return double - area.
	 */
	public double getArea() {
		double temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].getArea();
		return temp;
	}

	/**
	 * Returns the number of elements in the net
	 * 
	 * @return int - number of elements.
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
	 * Returns the number of faces in this net.
	 * 
	 * @return int - number of faces.
	 */
	public int countFaces() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countElements();
		return temp;
	}

	/**
	 * Returns the number of vertices in the border of his net.
	 * 
	 * @return int - number of vertices in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public int countBorderVertices() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countBorderVertices();
		return temp;
	}

	/**
	 * Returns the number of edges in the border of this net.
	 * 
	 * @return int - number of edges in the border.
	 */
	public int countBorderEdges() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countBorderEdges();
		return temp;
	}

	/**
	 * Returns the number of faces in the border of this net.
	 * 
	 * @return int - number of faces in the border.
	 */
	public int countBorderFaces() { // Dag
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].countBorderFaces();
		return temp;
	}

	/**
	 * Test whether this intersects with given plane.
	 * 
	 * @param plane
	 *            Plane3D to be tested
	 * @return boolean - true if intersects, false otherwise.
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
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
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
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
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
	 * Test whether this intersects with given segment.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if intersects, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Segment3D line, GeoEpsilon epsilon)
	 *             method of the class Segment3D (which computes the
	 *             intersection of two segments) called by this method returns a
	 *             value that is not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - if the index of the point coordinate is not 0 , 1 or 2
	 *             (that stands for the x-, y- and z-coordinate).
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public boolean intersects(Segment3D seg) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].intersects(seg))
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
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws IllegalStateException
	 *             - if the result of the intersection of a Triangle3D and a
	 *             MBB3D is not a simplex. The exception originates in the
	 *             method intersection(MBB3D, GeoEpsilon) of the class
	 *             Triangle3D.
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
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
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
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
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
	 * Tests whether this contains the given triangle geometrically.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             - if the intersectsInt(Line3D line, GeoEpsilon epsilon)
	 *             method of the class Line3D (which computes the intersection
	 *             of two lines) called by this method returns a value that is
	 *             not -2, -1, 0 or 1.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
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
	public boolean contains(Triangle3D triangle) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].contains(triangle))
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
		for (int i = 0; i < this.components.length; i++)
			if (!this.components[i].isOrientationConsistent())
				return false;

		return true;
	}

	/**
	 * Returns the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SURFACE_NET_C_E3D;
	}

}
