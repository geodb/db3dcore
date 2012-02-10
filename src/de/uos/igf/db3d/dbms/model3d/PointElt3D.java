/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;

/**
 * PointElt3D represents an element of a PointNet3D object.
 */
public class PointElt3D extends Point3D implements NetElement3D {

	/* id of this - unique in whole net */
	private int id;

	/* enclosing net component */
	private PointNet3DComp comp;

	/**
	 * Default constructor. Constructs a PointElt3D object as a Point3D with
	 * x,y,z = 0.0.
	 */
	public PointElt3D() {
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
	public PointElt3D(double x, double y, double z) {
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
	public PointElt3D(double[] coords) {
		super(coords);
	}

	/**
	 * Constructor.
	 * 
	 * @param point
	 *            Point3D
	 */
	public PointElt3D(Point3D point) {
		super(point);
	}
	
	/**
	 * Constructor for double coordinates and initializing the number of
	 * attributes. Constructs a Point3DElt object with given x, y, z coordinates
	 * and initializes a two-dimensional String array for the attributes.
	 * 
	 ** @param x
	 *            double value of x axis
	 * @param y
	 *            double value of y axis
	 * @param z
	 *            double value of z axis
	 */
	public PointElt3D(double x, double y,
			double z, int numberOfAttributes) {
		super(x, y, z, numberOfAttributes);
	}

	/**
	 * Returns the identifier of this.
	 * 
	 * @return int - id.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Tests if this has a neighbour element at given index.<br>
	 * PointElt3D has no neighbour - this method always returns false !
	 * 
	 * @param index
	 *            int index
	 * @return boolean - false.
	 */
	public boolean hasNeighbour(int index) {
		return false;
	}

	/**
	 * Tests if this has neighbour elements at all. <br>
	 * PointElt3D has no neighbour - this method always returns false !
	 * 
	 * @return boolean - false.
	 */
	public boolean hasNeighbours() {
		return false;
	}

	/**
	 * Tests if this is an interior element - it has two neighbours<br>
	 * PointElt3D has no neighbour - this method always returns false !
	 * 
	 * @return boolean - false.
	 */
	public boolean isInterior() {
		return false;
	}

	/**
	 * Returns the enclosing net component.
	 * 
	 * @return PointNet3DComp - the enclosing net component.
	 */
	public PointNet3DComp getNetComponent() {
		return comp;
	}

	/**
	 * Returns the enclosing net.
	 * 
	 * @return PointNet3D - the enclosing net.
	 */
	public PointNet3D getNet() {
		return comp.getNet();
	}

	/**
	 * Sets the id to given value.
	 * 
	 * @param i
	 *            int value to which the id should be set
	 */
	public void setID(int i) {
		this.id = i;
	}

	/**
	 * Sets the reference to the enclosing net component.
	 * 
	 * @param net3D
	 *            PointNet3DComp enclosing net component
	 */
	protected void setNetComponent(PointNet3DComp net3D) {
		comp = net3D;
	}

	/**
	 * Returns the type of this a <code>SimpleGeoObj</code>.
	 * 
	 * @return byte - type.
	 * @see db3d.dbms.geom.SimpleGeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.POINT_ELT_3D;
	}

	/**
	 * Reads an external object.
	 * 
	 * @param in
	 *            ObjectInput to be read
	 * @throws IOException
	 *             - if an input error occurred.
	 * @throws ClassNotFoundException
	 *             - if the class of the input object was not found.
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		this.id = in.readInt();
		this.comp = (PointNet3DComp) in.readObject();
	}

	/**
	 * Writes this to an external object.
	 * 
	 * @param out
	 *            ObjectOutput to which this should be written
	 * 
	 * @throws IOException
	 *             if an output error occurred.
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(getID());
		out.writeObject(getNetComponent());
	}
}
