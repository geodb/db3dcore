/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Serializable;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;

/**
 * Matrix4x4 implements a Matrix with 4 rows and 4 columns.
 * 
 * @author dhammerich
 */
@SuppressWarnings("serial")
public class Matrix4x4 implements Serializable {

	/* value array of dimension 2 - first is row - second is column */
	private double[][] v;

	/**
	 * Default constructor.<br>
	 * Initializes an empty internal matrix.
	 */
	public Matrix4x4() {
		this.v = new double[4][4];
	}

	/**
	 * Constructor.<br>
	 * Initializes the matrix with the given double array of length 16 !!.<br>
	 * The array is interpreted row by row (index 0-3 first row, 4-7 second row,
	 * 8-11 third row, 12-16 fourth row).
	 * 
	 * @param valueArray
	 *            double array of length 16 !
	 * @throws IllegalArgumentException
	 *             if length of array is != 16
	 */
	public Matrix4x4(double[] valueArray) {
		if (valueArray.length != 16) {
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.arraylengthsixteen"));
		}

		v = new double[4][4];
		for (int i = 0; i < 4; i++) {
			v[0][i] = valueArray[i];
			v[1][i] = valueArray[i + 4];
			v[2][i] = valueArray[i + 8];
			v[3][i] = valueArray[i + 12];
		}
	}

	/**
	 * Returns a copy of <code>this</code> as new Martix4x4 object.
	 * 
	 * @return Matrix4x4 - copy of <code>this</code>.
	 * @throws IllegalArgumentException
	 *             if length of array is != 16. This exception is not thrown in
	 *             this implementation because the length of the array is always
	 *             16.
	 */
	public Matrix4x4 copy() {

		double[] values = new double[16];
		int counter = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				values[counter] = this.v[i][j];
				counter++;
			}
		return new Matrix4x4(values);
	}

	/**
	 * Returns all entries of <code>this</code> as an array.
	 * 
	 * @return double[][] - values of <code>this</code>.
	 */
	public double[][] getValues() {
		return this.v;
	}

	/**
	 * Computes the determinante of this.
	 * 
	 * @return double - determinante
	 */
	public double computeDeterminante() {
		// expand the determinant along the first row (Laplace's formula)
		double det = 0;
		Matrix3x3 um = new Matrix3x3(new double[] { v[1][1], v[1][2], v[1][3],
				v[2][1], v[2][2], v[2][3], v[3][1], v[3][2], v[3][3] });
		det += v[0][0] * um.computeDeterminante();
		um = new Matrix3x3(new double[] { v[1][0], v[1][2], v[1][3], v[2][0],
				v[2][2], v[2][3], v[3][0], v[3][2], v[3][3] });
		det += (-1) * v[0][1] * um.computeDeterminante();
		um = new Matrix3x3(new double[] { v[1][0], v[1][1], v[1][3], v[2][0],
				v[2][1], v[2][3], v[3][0], v[3][1], v[3][3] });
		det += v[0][2] * um.computeDeterminante();
		um = new Matrix3x3(new double[] { v[1][0], v[1][1], v[1][2], v[2][0],
				v[2][1], v[2][2], v[3][0], v[3][1], v[3][2] });
		det += (-1) * v[0][3] * um.computeDeterminante();

		return det;
	}

	/**
	 * Returns matrix entry for given indices (row/column).
	 * 
	 * @param row
	 *            index for row
	 * @param col
	 *            index for column
	 * @return double - value at given index position.
	 */
	public double getMatrixEntry(int row, int col) {
		return this.v[row][col];
	}

	/**
	 * Multiplies <code>this</code> with the given scalar.
	 * 
	 * @param scalar
	 *            double value
	 * @return Matrix4x4 - <code>this</code> after multiplication.
	 */
	public Matrix4x4 mult(double scalar) {

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				this.v[i][j] = this.v[i][j] * scalar;
		return this;
	}

	/**
	 * Multiplies given matrix with the given scalar and returns result as new
	 * Matrix4x4 object - given object keeps unchanged.
	 * 
	 * @param scalar
	 *            double value
	 * @param matrix
	 *            Matrix4x4 object
	 * @return Matrix4x4 - new matrix from multiplication.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a Matrix4x4 from an array
	 *             whose length != 16. This exception is not thrown in this
	 *             implementation.
	 */
	public Matrix4x4 mult(Matrix4x4 matrix, double scalar) {
		Matrix4x4 newMatrix = matrix.copy();
		return newMatrix.mult(scalar);
	}

	/**
	 * Multiplies <code>this</code> with the given matrix.
	 * 
	 * @param matrix
	 *            Matrix4x4 to multiply
	 * @return Matrix4x4 - <code>this</code> after multiplication.
	 */
	public Matrix4x4 mult(Matrix4x4 matrix) {

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				this.v[i][j] = this.v[i][j] * matrix.getMatrixEntry(i, j);
		return this;
	}

	/**
	 * Multiplies the given matrices and returns a new matrix as result - given
	 * matrix objects stays unchanged.
	 * 
	 * @param matrix1
	 *            first matrix
	 * @param matrix2
	 *            second matrix
	 * @return Matrix4x4 - new matrix form multiplication.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a Matrix4x4 from an array
	 *             whose length != 16. This exception is not thrown in this
	 *             implementation because the length of the array is always 16.
	 */
	public Matrix4x4 mult(Matrix4x4 matrix1, Matrix4x4 matrix2) {

		double[] newMatrix = new double[16];
		int count = 0;

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				newMatrix[count] = matrix1.getMatrixEntry(i, j)
						* matrix2.getMatrixEntry(i, j);
				count++;
			}
		return new Matrix4x4(newMatrix);
	}

	/*
	 * Transforms given x-,y-,z-values and returns an array of transformed
	 * values.
	 * 
	 * @param x x-value to be transformed
	 * 
	 * @param y y-value to be transformed
	 * 
	 * @param z z-value to be transformed
	 * 
	 * @return double[] - new array of transformed values.
	 */
	private double[] transform(double x, double y, double z) {
		// explanation: enhanced point = (wx,wy,wz,w)

		if (this.v[3][3] != 1) {
			throw new IllegalStateException(Db3dSimpleResourceBundle
					.getString("db3d.geom.expectswone"));
		}

		double[] vec = { x, y, z, 1 };
		double wx = 0;
		double wy = 0;
		double wz = 0;
		double w = 0;

		for (int j = 0; j < 4; j++) {
			wx += this.v[0][j] * vec[j];
			wy += this.v[1][j] * vec[j];
			wz += this.v[2][j] * vec[j];
			w += this.v[3][j] * vec[j];
			// the forth coordinate is only for completeness
		}
		return new double[] { wx, wy, wz };
	}

	/**
	 * Transforms given Point and returns new Point3D object.
	 * 
	 * @param point
	 *            Point3D object to be transformed
	 * @return Point3D - new Point3D object from transformation of point.
	 */
	public Point3D transformPoint(Point3D point) {

		double[] values = transform(point.getX(), point.getY(), point.getZ());
		return new Point3D(values[0], values[1], values[2]);
	}

	/**
	 * Transforms and overwrites Coordinates of given Point3D object.
	 * 
	 * @param point
	 *            Point3D object to be transformed
	 */
	public void transformThisPoint(Point3D point) {

		double[] values = transform(point.getX(), point.getY(), point.getZ());
		point.setX(values[0]);
		point.setY(values[1]);
		point.setZ(values[2]);
	}

	/**
	 * Returns inverse transformation matrix if this is not singular (-> det=0)
	 * 
	 * @return Matrix4x4 - inverse matrix, <code>null</code> if det=0
	 *         (invertation not simple).
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a Matrix4x4 from an array
	 *             whose length != 16.
	 */
	public Matrix4x4 getInverseMatrix() {
		/*
		 * inverse matrix is given by A^(-1) = 1/det(A) * CT where CT is the
		 * transpond matrix of cofactors c(i,j) = (-1)^(i+j)*det(A(i,j))
		 * [source: "Principles of Interactive Computer Graphics",
		 * W.M.Newman/R.F.Sproull]
		 */

		double det = this.computeDeterminante();
		if (det == 0)
			// actually close to 0 should be considered, here just IS 0
			return null;

		// matrix of cofactors
		double[] cofactorMatrix = new double[16];
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				double[] matrix_ij = new double[9];
				int count = 0;
				for (int k = 0; k < 4; k++) {
					for (int l = 0; l < 4; l++) {
						if (k != i && l != j) {
							matrix_ij[count] = this.getMatrixEntry(k, l);
							count++;
						}
					}
				}
				Matrix3x3 mat_ij = new Matrix3x3(matrix_ij);
				cofactorMatrix[counter] = Math.pow(-1, (i + j))
						* mat_ij.computeDeterminante();
				counter++;
			}
		}

		Matrix4x4 coMatrix = new Matrix4x4(cofactorMatrix);
		double[] invMatrix = new double[16];
		counter = 0;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				invMatrix[counter] = (1 / det) * coMatrix.getMatrixEntry(j, i);
				counter++;
			}
		}

		return new Matrix4x4(invMatrix);
	}

	/**
	 * Returns the transformation matrix for a simple translation in direction
	 * of given values.
	 * 
	 * @param x
	 *            value for translation in X-direction
	 * @param y
	 *            value for translation in Y-direction
	 * @param z
	 *            value for translation in Z-direction
	 * @return Matrix4x4 - translation matrix.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a Matrix4x4 from an array
	 *             whose length != 16. This exception is not thrown in this
	 *             implementation because the length of the array is always 16.
	 */
	public static Matrix4x4 getSimpleTranslationMatrix(double x, double y,
			double z) {

		double[] values = new double[] { 1, 0, 0, x, 0, 1, 0, y, 0, 0, 1, z, 0,
				0, 0, 1 };
		return new Matrix4x4(values);
	}

	/**
	 * Returns the transformation matrix for a given plane - transforms
	 * coordinates to XY-plane (Z=0 -> transformation in 2D-space)
	 * 
	 * @param plane
	 *            Plane for which the matrix should be created
	 * @param sop
	 *            ScalarOperator
	 * @return Matrix4x4 - transformation matrix.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a Matrix4x4 from an
	 *             array whose length != 16. This exception is not thrown in
	 *             this implementation because the length of the array is always
	 *             16.
	 * @throws IllegalArgumentException
	 *             - if the parameter index of the method getScalar(int index)
	 *             of the class Vector3D is not 0, 1 or 2.
	 */
	public static Matrix4x4 getTransformationMatrix(Plane3D plane,
			ScalarOperator sop) {

		double[] values = null;
		Vector3D nn = plane.getNormalVector(); // normalizedNormal
		nn.normalize(sop);

		double c = 0.0;
		// plane.distance(new Point3D(0,0,0));
		/*
		 * Actually c is the distance from the plane to the coordinate start.
		 * Should be 0 now (because z = 0)
		 */

		// special case: plane rises orthographicly to xy-plane
		if (sop.equal(nn.getScalar(2), 0)) {
			values = new double[] { -nn.getScalar(1), nn.getScalar(0), 0, 0, 0,
					0, 1, 0, 0, 0, 0, c, 0, 0, 0, 1 };
		}

		// special case: plane rises orthographicly to xz-plane
		if (sop.equal(nn.getScalar(1), 0)) {
			values = new double[] { nn.getScalar(2), 0, -nn.getScalar(0), 0, 0,
					1, 0, 0, 0, 0, 0, c, 0, 0, 0, 1 };
		}

		// special case: plane rises orthographic to yz-plane
		if (sop.equal(nn.getScalar(0), 0)) {
			values = new double[] { 0, -nn.getScalar(2), nn.getScalar(1), 0, 1,
					0, 0, 0, 0, 0, 0, c, 0, 0, 0, 1 };
		}
		if (values != null)
			return new Matrix4x4(values);

		/*
		 * at the moment the parameter axes is as default set to 0, should
		 * possibly be handled in signature
		 */
		int axes = 0;

		/*
		 * x-axes projected onto plane as u-axes (equals x-axes after
		 * transformation)
		 */
		if (axes == 0) {
			// denominator
			double den = Math.sqrt(1 - Math.pow(nn.getScalar(0), 2));
			values = new double[] { ((1 - Math.pow(nn.getScalar(0), 2)) / den),
					((-nn.getScalar(0) * nn.getScalar(1)) / den),
					((-nn.getScalar(0) * nn.getScalar(2)) / den), 0, 0,
					(nn.getScalar(2) / den), (-nn.getScalar(1) / den), 0, 0, 0,
					0, c, 0, 0, 0, 1 };
		}

		// y-axes projected onto plane as u-axes (equals x-axes after
		// transformation)
		if (axes == 1) {
			// denominator
			double den = Math.sqrt(1 - Math.pow(nn.getScalar(1), 2));
			values = new double[] {
					((-nn.getScalar(0) * nn.getScalar(1)) / den),
					((1 - Math.pow(nn.getScalar(1), 2)) / den),
					((-nn.getScalar(1) * nn.getScalar(2)) / den), 0,
					(-nn.getScalar(2) / den), 0, (nn.getScalar(0) / den), 0, 0,
					0, 0, c, 0, 0, 0, 1 };
		}

		// z-axes projected onto plane as u-axes (equals x-axes after
		// transformation)
		if (axes == 2) {
			// denominator
			double den = Math.sqrt(1 - Math.pow(nn.getScalar(2), 2));
			values = new double[] {
					((-nn.getScalar(0) * nn.getScalar(2)) / den),
					((-nn.getScalar(1) * nn.getScalar(2)) / den),
					((1 - Math.pow(nn.getScalar(2), 2)) / den), 0,
					(nn.getScalar(1) / den), (-nn.getScalar(0) / den), 0, 0, 0,
					0, 0, c, 0, 0, 0, 1 };
		}

		return new Matrix4x4(values);
	}

}
