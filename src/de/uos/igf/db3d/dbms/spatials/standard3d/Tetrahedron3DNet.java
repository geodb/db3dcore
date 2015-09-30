package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Solid3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Tetrahedron3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * TetrahedronNet3D is the concrete subclass of Net3DAbst representing a
 * tetrahedron net with several components. All components are referenced as
 * TetrahedronNet3DComp objects.
 * 
 * @author Markus Jahn
 * 
 */
public class Tetrahedron3DNet extends Net3DAbst implements Solid3D {

	/** components of this */
	protected Tetrahedron3DComponent[] components;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            geometric error
	 */
	public Tetrahedron3DNet(GeoEpsilon epsilon) {
		super(epsilon);
		this.components = new Tetrahedron3DComponent[0];
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the TetrahedronNetBuilder class.
	 * 
	 * @param components
	 *            TetrahedronNet3DComp[]
	 * @param epsilon
	 *            geometric error
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Tetrahedron3DNet(Tetrahedron3DComponent[] components,
			GeoEpsilon epsilon) {
		super(epsilon);
		for (Tetrahedron3DComponent component : components)
			this.addComponent(component);
		this.updateMBB();
	}

	/**
	 * Copy Constructor.<br>
	 * Deep recursive clone - only the reference to the Object3D instance of the
	 * given TetrahedronNet3D is not copied - so this is a free
	 * TetrahedronNet3D. It is not registered in the Space3D and the
	 * corresponding thematic is gone away !
	 * 
	 * @param net
	 *            TetrahedronNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Tetrahedron3DNet(Tetrahedron3DNet net) {
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
	 * Returns the number of elements in the net.
	 * 
	 * @return int - number of elements.
	 */
	public int countElements() {
		int sum = 0;
		for (int i = 0; i < countComponents(); i++)
			sum = sum + this.components[i].countElements();

		return sum;
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param id
	 *            int index
	 * @return TetrahedronNet3DComp.
	 */
	public Tetrahedron3DComponent getComponent(int id) {
		return this.components[id];
	}

	/**
	 * Returns the components of this net.
	 * 
	 * @return TetrahedronNet3DComponent[] - array of components.
	 */
	public Tetrahedron3DComponent[] getComponents() {
		return this.components;
	}

	/**
	 * Adds a new component to the net.
	 * 
	 * @param component
	 *            TetrahedronNet3DComp to be added
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(Tetrahedron3DComponent component) {
		component.id = this.components.length;
		Tetrahedron3DComponent[] temp = new Tetrahedron3DComponent[this.components.length + 1];
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
	 *            TetrahedronNet3DComp to be removed
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void removeComponent(Tetrahedron3DComponent component) {
		Tetrahedron3DComponent[] temp = new Tetrahedron3DComponent[this.components.length - 1];
		int x = 0;
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i] != component) {
				temp[x] = this.components[i];
				x++;
			}
		}
		this.components = temp;
		// Here an IllegalArgumentException can be thrown.
	}

	/**
	 * Creates and returns a new (empty) component of the net.
	 * 
	 * @return TetrahedronNet3DComp - empty.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Tetrahedron3DComponent createComponent() {
		Tetrahedron3DComponent component = new Tetrahedron3DComponent(
				this.epsilon);
		this.addComponent(component);
		return component;
	}

	/**
	 * Splits the current net into two nets.<br>
	 * The returned new net object will have the components of given indexes.<br>
	 * <code>This</code> will contain the remaining components.
	 * 
	 * @param ids
	 *            int[]
	 * @return TetrahedronNet3D - new TetrahedronNet3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Tetrahedron3DNet splitTetrahedronNet(int[] ids, GeoEpsilon epsilon) {
		Tetrahedron3DComponent[] newTetrahedronNetComps = new Tetrahedron3DComponent[ids.length];

		for (int i = 0; i < ids.length; i++) {
			newTetrahedronNetComps[i] = this.components[i];
			this.removeComponent(this.components[i]);
		}
		return new Tetrahedron3DNet(newTetrahedronNetComps, epsilon);
	}

	/**
	 * Returns the boundary area of this net as the sum of the areas of its
	 * components.
	 * 
	 * @return double - boundary area.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public double getArea() {
		double temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].getArea();
		return temp;
	}

	/**
	 * Returns the volume of this net as the sum of the volumes of its
	 * components.
	 * 
	 * @return double - volume.
	 */
	public double getVolume() {
		double temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp = temp + this.components[i].getVolume();
		return temp;
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
	 * Returns the number of vertices in this net.
	 * 
	 * @return int - number of vertices.
	 */
	public int countVertices() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp += this.components[i].countVertices();
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
			temp += this.components[i].countEdges();
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
			temp += this.components[i].countFaces();
		return temp;
	}

	/**
	 * Returns the number of tetrahedrons in this net.
	 * 
	 * @return int - number of tetrahedrons.
	 */
	public int countTetras() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp += this.components[i].countSolids();
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
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderVertices() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp += this.components[i].countBorderVertices();
		return temp;
	}

	/**
	 * Returns the number of edges in the border of this net.
	 * 
	 * @return int - number of edges in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderEdges() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp += this.components[i].countBorderEdges();
		return temp;
	}

	/**
	 * Returns the number of faces in the border of this net.
	 * 
	 * @return int - number of faces in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderFaces() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp += this.components[i].countBorderFaces();
		return temp;
	}

	/**
	 * Returns the number of tetrahedrons in the border of this net.
	 * 
	 * @return int - number of tetrahedrons in the border.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public int countBorderTetras() {
		int temp = 0;
		for (int i = 0; i < this.components.length; i++)
			temp += this.components[i].countBorderTetras();
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
	 * Tests whether this contains the given point geometrically inside.
	 * 
	 * @param point
	 *            Point3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public boolean containsInside(Point3D point) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].containsInside(point))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given segment geometrically inside.
	 * 
	 * @param seg
	 *            Segment3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, GeoEpsilon epsilon) method
	 *             of the class Line3D (which computes the intersection of two
	 *             lines) called by this method returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
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
	public boolean containsInside(Segment3D seg) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].containsInside(seg))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given triangle geometrically inside.
	 * 
	 * @param triangle
	 *            Triangle3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, GeoEpsilon epsilon) method
	 *             of the class Line3D (which computes the intersection of two
	 *             lines) called by this method returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon).
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
	public boolean containsInside(Triangle3D triangle) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].containsInside(triangle))
				return true;
		}
		return false;
	}

	/**
	 * Tests whether this contains the given tetrahedron geometrically inside.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be tested
	 * @return boolean - true if contained, false otherwise.
	 * @throws IllegalStateException
	 *             if the intersectsInt(Line3D line, GeoEpsilon epsilon) method
	 *             of the class Line3D (which computes the intersection of two
	 *             lines) called by this method returns a value that is not -2,
	 *             -1, 0 or 1.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the
	 * @throws IllegalArgumentException
	 *             - if validation of a Triangle3D fails. The exception
	 *             originates in the constructor Triangle3D(Point3D, Point3D,
	 *             Point3D, GeoEpsilon). method getPoint(int) of the class
	 *             Tetrahedron3D.
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
	public boolean containsInside(Tetrahedron3D tetra) {
		for (int i = 0; i < this.components.length; i++) {
			if (this.components[i].containsInside(tetra))
				return true;
		}
		return false;
	}

	/**
	 * Returns the type of this as a <code>ComplexGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.model3d.Spatial#getGeometryType()
	 */
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.SOLID_NET_C_E3D;
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
	public Tetrahedron3DElement getElement(int id) {
		for (Tetrahedron3DComponent component : components) {
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
