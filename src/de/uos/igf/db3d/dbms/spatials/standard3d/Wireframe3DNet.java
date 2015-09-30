package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.exceptions.DB3DException;
import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Wireframe3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Line3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Plane3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * 
 * @author Markus Jahn
 * 
 */
public class Wireframe3DNet extends Net3DAbst implements Wireframe3D {

	/* components of this */
	private Wireframe3DComponent[] components;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 */
	public Wireframe3DNet(GeoEpsilon epsilon) {
		super(epsilon);
		this.components = new Wireframe3DComponent[0];
	}

	/**
	 * Constructor.<br>
	 * This constructor can only be used by the PointNetBuilder class.
	 * 
	 * @param components
	 *            WireframeNet3DComp[]
	 * @param epsilon
	 *            GeoEpsilon
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Wireframe3DNet(Wireframe3DComponent[] components, GeoEpsilon epsilon) {
		super(epsilon);
		for (Wireframe3DComponent component : components)
			this.addComponent(component);
		this.updateMBB();
	}

	@Override
	public int countComponents() {
		return components.length;
	}

	@Override
	public int countElements() {
		int sum = 0;
		for (int i = 0; i < countComponents(); i++)
			sum = sum + getComponent(i).countElements();

		return sum;
	}

	/**
	 * Returns the component with given index.
	 * 
	 * @param id
	 *            int index
	 * @return TetrahedronNet3DComp.
	 */
	public Wireframe3DComponent getComponent(int id) {
		return this.components[id];
	}

	/**
	 * Returns the components of this net.
	 * 
	 * @return WireframeNet3DComponent[] - array of components.
	 */
	public Wireframe3DComponent[] getComponents() {
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
	public void addComponent(Wireframe3DComponent component) {

		component.id = this.components.length;
		Wireframe3DComponent[] temp = new Wireframe3DComponent[this.components.length + 1];
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
	public void removeComponent(Wireframe3DComponent component) {
		Wireframe3DComponent[] temp = new Wireframe3DComponent[this.components.length - 1];
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
	 * Creates and returns a new (empty) component of the net.
	 * 
	 * @return TetrahedronNet3DComp - empty.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Wireframe3DComponent createComponent() {
		Wireframe3DComponent component = new Wireframe3DComponent(this.epsilon);
		this.addComponent(component);
		return component;
	}

	@Override
	public boolean intersects(Plane3D plane) throws DB3DException {
		throw new NotYetImplementedException();
	}

	@Override
	public boolean intersects(Line3D line) {
		throw new NotYetImplementedException();
	}

	@Override
	public boolean intersects(MBB3D mbb) {
		throw new NotYetImplementedException();
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.WIREFRAME_NET_C_E3D;
	}

	protected void updateMBB() {
		MBB3D neu = null;
		GeoEpsilon epsilon = getGeoEpsilon();
		if (components[0] != null) {
			neu = components[0].getMBB();
			for (int i = 1; i < components.length; i++)
				neu = neu.union(components[i].getMBB(), epsilon);
		}
		this.mbb = neu;
	}

	/**
	 * Searches for an element with the given id in the components of this and
	 * returns it. If it was not found, returns <code>null</code>.
	 * 
	 * @return SimpleGeoObj - element with the given id.
	 */
	public Wireframe3DElement getElement(int id) {
		for (Wireframe3DComponent component : components) {
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
