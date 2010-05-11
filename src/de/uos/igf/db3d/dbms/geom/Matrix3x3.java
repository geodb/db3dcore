/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;

/**
 * Matrix3x3 implements a Matrix with 3 rows and 3 columns.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Matrix3x3 implements Externalizable {

	/* value array of dimension 2 - first is row - second is column */
	private double[][] v;

	/**
	 * Default constructor.<br>
	 * Initializes an empty internal matrix.
	 */
	public Matrix3x3() {
		this.v = new double[3][3];
	}

	/**
	 * Constructor.<br>
	 * Initializes the matrix with the given three vectors.<br>
	 * Every vector is one row in the matrix (v1 = first row, v2 = second row,
	 * v3 = third row).
	 * 
	 * @param v1
	 *            Vector3D 1
	 * @param v2
	 *            Vector3D 2
	 * @param v3
	 *            Vector3D 3
	 */
	public Matrix3x3(Vector3D v1, Vector3D v2, Vector3D v3) {
		v = new double[3][3];
		for (int i = 0; i < 3; i++) {
			v[0][i] = v1.getScalar(i);
			v[1][i] = v2.getScalar(i);
			v[2][i] = v3.getScalar(i);
		}
	}

	/**
	 * Constructor.<br>
	 * Initializes the matrix with the given three points.<br>
	 * Every point is one row in the matrix (p1 = first row, p2 = second row, p3
	 * = third row).<br>
	 * 
	 * @param p1
	 *            Point3D 1
	 * @param p2
	 *            Point3D 2
	 * @param p3
	 *            Point3D 3
	 */
	public Matrix3x3(Point3D p1, Point3D p2, Point3D p3) {
		v = new double[3][3];
		for (int i = 0; i < 3; i++) {
			v[0][i] = p1.getCoord(i);
			v[1][i] = p2.getCoord(i);
			v[2][i] = p3.getCoord(i);
		}
	}

	/**
	 * Constructor.<br>
	 * Initializes the matrix with the given double array of length 9 !!.<br>
	 * The array is interpreted row by row (index 0-2 first row, 3-5 second row,
	 * 6-8 third row).<br>
	 * 
	 * @param valueArray
	 *            double array of length 9!
	 * @throws IllegalArgumentException
	 *             if length of array is != 9
	 */
	public Matrix3x3(double[] valueArray) {
		if (valueArray.length != 9) {
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.arraylengthnine"));
		}

		v = new double[3][3];
		for (int i = 0; i < 3; i++) {
			v[0][i] = valueArray[i];
			v[1][i] = valueArray[i + 3];
			v[2][i] = valueArray[i + 6];
		}
	}

	/**
	 * Computes the determinante of this.
	 * 
	 * @return double - determinante.
	 */
	public double computeDeterminante() {
		// according to Sarrus' rule
		return (v[0][0] * v[1][1] * v[2][2]) + (v[0][1] * v[1][2] * v[2][0])
				+ (v[0][2] * v[1][0] * v[2][1]) - (v[2][0] * v[1][1] * v[0][2])
				- (v[2][1] * v[1][2] * v[0][0]) - (v[2][2] * v[1][0] * v[0][1]);
	}

	/**
	 * Reads values from an external source and updates this martix.
	 * 
	 * @param in
	 *            ObjectInput from which the values are read
	 * @throws IOException
	 *             if an input error occurred.
	 * @throws ClassNotFoundException
	 *             if the class type of the input object was not found.
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.v = (double[][]) in.readObject();
	}

	/**
	 * Writes the values of this to an externalObjectOutput.
	 * 
	 * @param out
	 *            ObjectOutput to which the values are written
	 * @throws IOException
	 *             if an output error occurred.
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.v);
	}

}
