/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.util.ArrayList;
import java.util.List;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * SegmentNetBuilder is for constructing SegmentNet3D objects.<br>
 * Because the constructors for SegmentNet3D and their component objects
 * SegmentNet3DComp are protected this is the only way to instantiate a
 * SegmentNet3D object outside the package.
 */
public class SegmentNetBuilder {
	
    public static void main(String[] args) {
        SegmentNetBuilder snb = new SegmentNetBuilder(new ScalarOperator());

        SegmentElt3D[] segArr = new SegmentElt3D[2];
        segArr[0] = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0), new Point3D(
                      2.0, 1.0, 1.0), new ScalarOperator());
        segArr[1] = new SegmentElt3D(new Point3D(2.0, 1.0, 1.0), new Point3D(
                      3.0, 1.0, 1.0), new ScalarOperator());

        snb.addComponent(segArr);

        SegmentElt3D segElt = (SegmentElt3D) snb.getSegmentNet()
                      .getComponent(0).getElement(1);

        System.out.println("hasNeighbours?: " + segElt.hasNeighbours());
        System.out.println("Neighbour0: " + segElt.getNeighbour(0));
        System.out.println("Neighbour1: " + segElt.getNeighbour(1));

  }


	/* Space3D with whose constraints the net should be built */
	private Space3D space;

	/* workspace in which the net should be built */
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
	 * valid for the enclosed objects build with that class.
	 * 
	 * @param space
	 *            enclosing space for Segment net
	 */
	public SegmentNetBuilder(Space3D space) {
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
	 *            enclosing Workspace for Segment net
	 * @param sop
	 *            ScalarOperator for this object in the workspace
	 */
	public SegmentNetBuilder(ScalarOperator sop) {
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
	 * Builds a new SegmentNet3DComp object and adds it to the SegmentNet3D
	 * class we are currently building.
	 * 
	 * @param elements
	 *            SegmentElt3D[] - array of net elements<br>
	 *            In the given array the neighbourhood topology does not have to
	 *            be defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this segment array.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public void addComponent(SegmentElt3D[] elements) {
		SegmentNet3DComp comp = new SegmentNet3DComp(
				getScalarOperator().copy(), elements);
		// Here an IllegalArgumentException can be thrown.

		for (int i = 0; i < elements.length; i++) {
			elements[i].setID(counter++);
			elements[i].setNetComponent(comp);
		}

		components.add(comp);
	}

	/**
	 * Builds a new SegmentNet3DComp object and adds it to the SegmentNet3D
	 * class we are currently building.
	 * 
	 * @param elements
	 *            SegmentElt3D[] - array of net elements
	 * @param id
	 *            the component id <br>
	 *            In the given array the neighbourhood topology has not to be
	 *            defined.<br>
	 *            It is assumed that there are NO ! redundant Point3D used in
	 *            this segment array.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 */
	public void addComponent(SegmentElt3D[] elements, int id) {
		SegmentNet3DComp comp = new SegmentNet3DComp(space.getScalarOperator()
				.copy(), elements);
		// Here an IllegalArgumentException can be thrown.
		comp.setComponentID(id);

		for (int i = 0; i < elements.length; i++) {
			elements[i].setID(counter++);
			elements[i].setNetComponent(comp);
		}

		components.add(comp);
	}

	/**
	 * Builds and returns the SegmentNet3D object with the previously added
	 * SegmentNet3DComp objects.<br>
	 * The method returns <code>null</code> if no component was added.
	 * 
	 * @return SegmentNet3D if one was built, <code>null</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public SegmentNet3D getSegmentNet() {
		if (components.size() <= 0)
			return null;

		SegmentNet3DComp[] compnet = new SegmentNet3DComp[components.size()];
		for (int i = 0; i < compnet.length; i++)
			compnet[i] = (SegmentNet3DComp) components.get(i);

		SegmentNet3D net = new SegmentNet3D(compnet, getScalarOperator());
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
