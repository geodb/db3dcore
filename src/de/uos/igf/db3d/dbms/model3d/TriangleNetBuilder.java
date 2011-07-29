/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.Space3D;
import de.uos.igf.db3d.dbms.util.TriangleServices;

/**
 * TriangleNetBuilder is designed for constructing TriangleNet3D objects.<br>
 * Because the constructors for TriangleNet3D and their component objects
 * TriangleNet3DComp are protected, this is the only way to instantiate a
 * TriangleNet3D object outside the package.
 */
public class TriangleNetBuilder {

	/* Space3D with which constraints the net should be built */
	private Space3D space;

	/* workspace in which the net should be built */
	// private Workspace workspace;

	/* the ScalarOperator to be used if build in Workspace */
	private ScalarOperator wsSOP;

	/** the components for the net */
	protected List<TriangleNet3DComp> components;

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
	 *            enclosing space for triangle net
	 */
	public TriangleNetBuilder(Space3D space) {
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
	 *            enclosing Workspace for Triangle net
	 * @param sop
	 *            ScalarOperator for this object in the workspace
	 */
	public TriangleNetBuilder(ScalarOperator sop) {
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
	 * Builds a new TriangleNet3DComp object and adds it to the TriangleNet3D
	 * class we are currently building.
	 * 
	 * @param elements
	 *            TriangleElt3D[] - array of net elements<br>
	 *            In the given array the neighbourhood topology does not have to
	 *            be defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this triangle array.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void addComponent(TriangleElt3D[] elements) {

		// Build a new Component and fill the SAM
		TriangleNet3DComp comp = new TriangleNet3DComp(getScalarOperator(),
				elements);
		// Here an IllegalArgumentException can be thrown.

		for (int i = 0; i < elements.length; i++) {
			// set ID
			// TODO: do we need to set the id if we got one from the import
			// process?
			elements[i].setID(counter++);
			// set the reference to the component
			elements[i].setNetComponent(comp);
		}
		components.add(comp);
	}

	/**
	 * Builds a new TriangleNet3DComp object and adds it to the TriangleNet3D
	 * class we are currently building.
	 * 
	 * @param elements
	 *            TriangleElt3D[] - array of net elements
	 * @param id
	 *            the component id In the given array the neighbourhood topology
	 *            has not to be defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this triangle array.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public void addComponent(TriangleElt3D[] elements, int id) {
		TriangleNet3DComp comp = new TriangleNet3DComp(getScalarOperator(),
				elements);
		// Here an IllegalArgumentException can be thrown.
		comp.setComponentID(id);

		for (int i = 0; i < elements.length; i++) {
			// set id
			elements[i].setID(counter++);
			// set the reference to the component
			elements[i].setNetComponent(comp);
		}
		components.add(comp);
	}

	/**
	 * Builds and returns the TriangleNet3D object with the previously added
	 * TriangleNet3DComp objects.<br>
	 * The method returns <code>null</code> if no component was added.
	 * 
	 * @return TriangleNet3D if a component was added, <code>null</code>
	 *         otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public TriangleNet3D getTriangleNet() {
		if (components.size() <= 0)
			return null;

		TriangleNet3DComp[] compnet = new TriangleNet3DComp[components.size()];

		// alteration in array
		for (int i = 0; i < compnet.length; i++)
			compnet[i] = (TriangleNet3DComp) components.get(i);

		// instantiation of the net
		TriangleNet3D net = new TriangleNet3D(compnet, getScalarOperator());
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

		// remove redundant points of this net
		// TODO: Are there redundant points? Try following code:
//		TriangleServices service = new TriangleServices();
//		service.initForPointClouds(net);
//
//		HashMap<Integer, Point3D> points = service.getPoints();
//		HashMap<Integer, int[]> triangles = service.getTriangles();
//
//		Set elements = net.getComponent(0).getElementsViaSAM();
//		Iterator it = elements.iterator();
//		while (it.hasNext()) {
//			TriangleElt3D triangle = (TriangleElt3D) it.next();
//			int[] triPoints = triangles.get(triangle.getID());
//			triangle.setPoint(0, points.get(triPoints[0]));
//			triangle.setPoint(1, points.get(triPoints[1]));
//			triangle.setPoint(2, points.get(triPoints[2]));
//		}

		return net;
	}

}
