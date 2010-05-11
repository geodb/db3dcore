/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.ArrayList;
import java.util.List;

import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * PointNetBuilder is for constructing PointNet3D objects.<br>
 * Because the constructors for PointNet3D and their component objects
 * PointNet3DComp are protected, this is the only way to instantiate a
 * PointNet3D object outside the package.
 */
public class PointNetBuilder {

	/* Space3D with whose constraints the net should be build */
	private Space3D space;

	/* workspace in which the net should be build */
	// private Workspace workspace;

	/* the ScalarOperator to be used if built in Workspace */
	private ScalarOperator wsSOP;

	/* the components for the net */
	private List components;

	/* id counter */
	private int counter;

	/* comp id counter */
	private int compIDCounter;

	/**
	 * Constructor.<br>
	 * The space is needed to retrieve the space constraints which also are
	 * valid for the enclosed objects built with that class.
	 * 
	 * @param space
	 *            enclosing space for Point net
	 */
	public PointNetBuilder(Space3D space) {
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
	 *            enclosing Workspace for Point net
	 * @param sop
	 *            ScalarOperator for this object in the workspace
	 */
	public PointNetBuilder(ScalarOperator sop) {
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
	 *            int
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
	 * Builds a new PointNet3DComp object and adds it to the PointNet3D class we
	 * are currently building.
	 * 
	 * @param elements
	 *            PointElt3D[] - array of net elements<br>
	 *            In the given array the neighbourhood topology does not have to
	 *            be defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this point array.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(PointElt3D[] elements) {
		PointNet3DComp comp = new PointNet3DComp(elements, getScalarOperator());
		// Here an IllegalArgumentException can be thrown.

		for (int i = 0; i < elements.length; i++) {
			elements[i].setID(counter++);
			elements[i].setNetComponent(comp);
		}

		components.add(comp);
	}

	/**
	 * Builds a new PointNet3DComp object and adds it to the PointNet3D class we
	 * are currently building.
	 * 
	 * @param elements
	 *            PointElt3D[] - array of net elements
	 * @param id
	 *            the component id<br>
	 *            In the given array the neighbourhood topology has not to be
	 *            defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this point array.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void addComponent(PointElt3D[] elements, int id) {
		PointNet3DComp comp = new PointNet3DComp(elements, getScalarOperator());
		// Here an IllegalArgumentException can be thrown.
		comp.setComponentID(id);

		for (int i = 0; i < elements.length; i++) {
			elements[i].setID(counter++);
			elements[i].setNetComponent(comp);
		}

		components.add(comp);
	}

	/**
	 * Builds and returns the PointNet3D object with the previously added
	 * PointNet3DComp objects.<br>
	 * The method returns null if no component was added.
	 * 
	 * @return PointNet3D - the built point net.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public PointNet3D getPointNet() {
		if (components.size() <= 0)
			return null;

		PointNet3DComp[] compnet = new PointNet3DComp[components.size()];
		for (int i = 0; i < compnet.length; i++)
			compnet[i] = (PointNet3DComp) components.get(i);

		PointNet3D net = new PointNet3D(compnet, getScalarOperator());
		// Here an IllegalArgumentException can be thrown.

		// set component counter
		net.setComponentID(this.compIDCounter);

		// set the parent net and the component ids if needed
		for (int i = 0; i < compnet.length; i++) {
			compnet[i].setNet(net);
			if (compnet[i].getComponentID() == -1)
				compnet[i].setComponentID(net.nextComponentID());
		}

		// set the element id
		net.setElementID(counter);
		return net;
	}

}
