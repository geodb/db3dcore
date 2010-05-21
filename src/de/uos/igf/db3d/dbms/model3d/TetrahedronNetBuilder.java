/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.ArrayList;
import java.util.List;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * TetrahedronNetBuilder is for constructing TetrahedronNet3D objects.<br>
 * Because the constructors for TetrahedronNet3D and their component objects
 * TetrahedronNet3DComp are protected this is the only way to instantiate a
 * TetrahedronNet3D object outside the package.
 */
public class TetrahedronNetBuilder {

	/* Space3D with whose constraints the net should be built */
	private Space3D space;

	/* workspace in which the net should be built */
	// private Workspace workspace;

	/* the ScalarOperator to be used if build in Workspace */
	private ScalarOperator wsSOP;

	/** the components for the net */
	protected List components;

	/** id counter */
	protected int counter;

	/** comp id counter */
	protected int compIDCounter;

	/**
	 * Constructor.<br>
	 * The space is needed to retrieve the space constraints which also are
	 * valid for the enclosed objects build with that class.
	 * 
	 * @param space
	 *            enclosing space for tetrahedron net
	 */
	public TetrahedronNetBuilder(Space3D space) {
		this.compIDCounter = 0;
		this.space = space;
		this.wsSOP = null;
		// this.workspace = null;
		this.components = new ArrayList();
		this.counter = 1;
	}

	/**
	 * Constructor.
	 * 
	 * @param workspace
	 *            enclosing Workspace for tetrahedron net.
	 * @param sop
	 *            ScalarOperator for this object in the workspace
	 */
	public TetrahedronNetBuilder(ScalarOperator sop) {
		this.compIDCounter = 0;
		this.space = null;
		// this.workspace = workspace;
		this.wsSOP = sop;
		this.components = new ArrayList();
		this.counter = 1;
	}

	/**
	 * Sets the component ID counter to the correct value. Must be set if the
	 * net is imported.
	 * 
	 * @param counter
	 *            int id counter to be set
	 */
	public void setComponentIDCounter(int counter) {
		this.compIDCounter = counter;
	}

	/**
	 * Returns a ScalarOperator copy.
	 * 
	 * @return ScalarOperator sop.
	 */
	public ScalarOperator getScalarOperator() {
		if (space != null)
			return space.getScalarOperator().copy();
		else
			return this.wsSOP.copy();
	}

	/**
	 * Builds a new TetrahedronNet3DComp object and adds it to the
	 * TetrahedronNet3D class we are currently building. The DB3DException is
	 * not thrown due a try/catch block.
	 * 
	 * @param elements
	 *            TetrahedronElt3D[] - array of net elements<br>
	 *            In the given array the neighbourhood topology does not have to
	 *            be defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this tetrahedron array.
	 * @return addition successful or not.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean addComponent(TetrahedronElt3D[] elements) {
		TetrahedronNet3DComp comp = null;
		try {
			comp = new TetrahedronNet3DComp(getScalarOperator(), elements);
			// Here an IllegalArgumentException can be thrown.
		} catch (DB3DException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (comp != null) {
			for (int i = 0; i < elements.length; i++) {
				elements[i].setID(counter++);
				elements[i].setNetComponent(comp);
			}

			return components.add(comp);
		} else
			return false;

	}

	/**
	 * Builds a new TetrahedronNet3DComp object and adds it to the
	 * TetrahedronNet3D class we are currently building.
	 * 
	 * @param elements
	 *            TetrahedronElt3D[] - array of net elements
	 * @param id
	 *            the component id<br>
	 *            In the given array the neighbourhood topology has not to be
	 *            defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this tetrahedron array.
	 * @return addition successful or not.
	 * @throws DB3DException
	 *             - during building net topology of TetrahedronNet3DComp and
	 *             registering neighbours, a DB3DException is thrown if the
	 *             neighbour index is not 0, 1, 2 or 3.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public boolean addComponent(TetrahedronElt3D[] elements, int id)
			throws DB3DException {
		TetrahedronNet3DComp comp = null;
		comp = new TetrahedronNet3DComp(getScalarOperator(), elements);
		// Here an IllegalArgumentException can be thrown.
		if (comp != null) {
			comp.setComponentID(id);

			for (int i = 0; i < elements.length; i++) {
				elements[i].setID(counter++);
				elements[i].setNetComponent(comp);
			}
			return components.add(comp);
		} else
			return false;
	}

	/**
	 * Builds and returns the TetrahedronNet3D object with the previously added
	 * TetrahedronNet3DComp objects.<br>
	 * The method returns null if no component was added.
	 * 
	 * @return TetrahedronNet3D if one was built, false otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public TetrahedronNet3D getTetrahedronNet() {
		if (components.size() <= 0)
			return null;

		TetrahedronNet3DComp[] compnet = new TetrahedronNet3DComp[components
				.size()];
		for (int i = 0; i < compnet.length; i++)
			compnet[i] = (TetrahedronNet3DComp) components.get(i);

		TetrahedronNet3D net = new TetrahedronNet3D(compnet,
				getScalarOperator());
		// Here an IllegalArgumentException can be thrown.

		// set component counter
		net.setComponentID(this.compIDCounter);

		// set the parent net and the component ids if needed
		for (int i = 0; i < compnet.length; i++) {
			compnet[i].setNet(net);
			if (compnet[i].getComponentID() == -1)
				compnet[i].setComponentID(net.nextComponentID());
		}

		// set element id
		net.setElementID(counter);
		return net;
	}

}
