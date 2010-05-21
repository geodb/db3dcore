/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.util.Arrays;
import java.util.Comparator;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;
import de.uos.igf.db3d.dbms.geom.LA.SingularException;

/**
 * This class contains static and dynamic methods to compare two ore more double
 * values.
 * 
 * @author Wolfgang Baer
 * @author Jens Pollehn
 */
public final class GeomUtils {

	/** implements a Point3DComparator based on x,y,z sorting */
	public final static Point3DComparator POINT_COMP;

	/** implements a Point3DComparator based on x sorting */
	public final static Point3DComparator POINT_X_COMP;

	/** implements a Point3DComparator based on y sorting */
	public final static Point3DComparator POINT_Y_COMP;

	/** implements a Point3DComparator based on z sorting */
	public final static Point3DComparator POINT_Z_COMP;

	static {
		POINT_X_COMP = new Point3D_X_Comparator();
		POINT_Y_COMP = new Point3D_Y_Comparator();
		POINT_Z_COMP = new Point3D_Z_Comparator();
		POINT_COMP = new Point3D_XYZ_Comparator();
	}

	/**
	 * Returns the sum of the specified dimensions where each negative value is
	 * treated as minus infinity.
	 * 
	 * @param dimensions
	 *            the dimensions to be added
	 * @return the sum of the specified dimensions, if at least one dimension is
	 *         specified and all of them are nonnegative. Otherwise a negative
	 *         value is returned.
	 */
	public static int addDimensions(int... dimensions) {

		if (dimensions.length == 0) {
			return -1;
		}

		int dimSum = 0; // @see http://en.wikipedia.org/wiki/Dim_sum

		for (int dim : dimensions) {
			if (dim < 0) {
				return -1;
			}
			dimSum += dim;
		}
		return dimSum;
	}

	private GeomUtils() {
		// to restrict visibility
	}

	/**
	 * A comparison for two non-nan doubles according to their numeric order as
	 * defined by the primitive comparison operators.
	 * 
	 * Note that Double.compare(double,double) behaves differently from the
	 * standard comparison operators (e.g. <code>-0d &lt; +0d</code>) and also
	 * accepts NaN values.
	 * 
	 * 
	 * @param d1
	 *            a non-NaN- double value which may also be infinite
	 * @param d2
	 *            another non-NaN- double value which may also be infinite
	 * @return the numeric order of the specified arguments in the same fashion
	 *         as a comparator does.
	 * @throws IllegalArgumentException
	 *             - if the given doubles are NaN values and cannot be compared.
	 */
	public static int doubleCompare(double d1, double d2) {

		if (d1 > d2) {
			return +1;
		}
		if (d1 < d2) {
			return -1;
		}
		if (d1 == d2) {
			return 0;
		}

		throw new IllegalArgumentException(Db3dSimpleResourceBundle
				.getString("db3d.geom.comparenan"));

	}

	/**
	 * Computes the (square of) the distance between the affine hulls of the two
	 * given point sets and eventually also the affine coefficients of points at
	 * minimal distance.
	 * 
	 * If a coeffs parameter is specified the corresponding coefficients of the
	 * affine combination of a closest point is specified. {@link http
	 * ://en.wikipedia.org/wiki/Affine_combination}
	 * 
	 * The algorithm solves the optimization problem minimize length(coeffsA[0]
	 * * a[0] + ... + coeffsA[n] * a[n] - coeffsB[0]*b[0] - ... -
	 * coeffsB[m]*b[m]) as a function in the coefficients respecting the
	 * conditions coeffsA[0] + ... + coeffsA[n] = 1 coeffsB[0] + ... +
	 * coeffsB[m] = 1.
	 * 
	 * The linear equation system kktM * kktX = kktY is iteratively constructed
	 * using the Lagrangians of the previous solution and at each iteration step
	 * adding the point which is most "inclined" towards the other currently
	 * considered subset. {@link http
	 * ://en.wikipedia.org/wiki/Quadratic_programming}
	 * 
	 * 
	 * @param a
	 *            an array of points defining an affine space clA
	 * @param b
	 *            an array of points defining another affine space clB
	 * @param coeffsA
	 *            if not null, the affine coefficients for a point pa at minimal
	 *            distance to clB, i.o.w.
	 *            <code>pa = sum coeffsA[i]*a[i] for i in 0..a.length</code>,
	 *            will be stored here. If the specified array is too short a
	 *            ArrayIndexOutOfBoundsException will occur.
	 * @param coeffsB
	 *            if not null, the affine coefficients for a point pb at minimal
	 *            distance to clA, i.o.w.
	 *            <code>pb = sum coeffsB[j]*b[j] for j in 0..b.length</code>,
	 *            will be stored here. If the specified array is too short a
	 *            ArrayIndexOutOfBoundsException will occur.
	 * @param scOp
	 *            the Scalar operator which compares double values up to a
	 *            specified error.
	 * @return the square of the distance between the affine closure of a and
	 *         the affine closure of b. TODO: Upload the file containing the
	 *         description of the algorithmic technique used here.
	 */
	static double affineDistance(double[][] a, double[][] b, double[] coeffsA,
			double[] coeffsB, ScalarOperator scOp) {

		if (a.length == 0 || b.length == 0) {
			return Double.POSITIVE_INFINITY;
		}

		final int dim;
		{
			int tmp = 0;
			for (double[] pA : a) {
				tmp = Math.max(tmp, pA.length);
			}
			for (double[] pB : b) {
				tmp = Math.max(tmp, pB.length);
			}
			dim = tmp;
		}

		if (dim == 0) {
			return 0d;
		}

		// the maximal number of inactive points.
		int maxInactive = Math.min(a.length + b.length, dim + 2);
		// Note: dim is also the index of the last inactive entry in the
		// KKT-system.

		// dim+1+2 is the maximal size of the KKT-system:
		// n+1 non-active points and two affine constraints
		int maxSysSize = maxInactive + 2;

		// the matrix of the KKT-system
		double[][] kktM = new double[maxSysSize][maxSysSize];
		for (int i = 0; i < maxInactive; ++i) { // fill the diagonal for active
			// entries.
			kktM[i][i] = 1d;
		}

		// the right-hand side of the KKT-system.
		double[] kktY = new double[maxSysSize];
		Arrays.fill(kktY, 0, maxInactive, 0d); // many zeros
		kktY[maxInactive + 0] = 1d; // and two
		kktY[maxInactive + 1] = 1d; // ones (for the Lagrangians)

		double result = 0d;

		int[] piA = new int[a.length]; // the current permutation of points a
		for (int i = 0; i < a.length; ++i) {
			piA[i] = i;
		}

		int[] piB = new int[b.length]; // the current permutation of points b
		for (int j = 0; j < b.length; ++j) {
			piB[j] = j;
		}

		// store the first values into matrix:
		// storage schema: points from a go from "left" 0 to "right" 1, 2, ...
		// points from b go from "right" dim-0 to "left" dim-1, dim-2, ...
		final int aStart = 0;
		final int bStart = maxInactive - 1;

		kktM[aStart][aStart] = LA.dot(a[0], a[0]);
		kktM[bStart][bStart] = LA.dot(b[0], b[0]);
		kktM[aStart][bStart] = kktM[bStart][aStart] = -LA.dot(a[0], b[0]);
		// negative!!!
		// store the coefficients for inactive Lagrangians:
		kktM[aStart][maxInactive + 0] = kktM[maxInactive + 0][aStart] = 1d;
		// to compute Lagrangian of a
		kktM[bStart][maxInactive + 1] = kktM[maxInactive + 1][bStart] = 1d;
		// to compute Lagrangian of b

		int inactiveAlength = 1;
		int inactiveBlength = 1;

		boolean systemHasChanged = true;
		while (systemHasChanged) {
			systemHasChanged = false;

			double[] kktX;
			try {
				double[][] M = new double[kktM.length][kktM.length];
				for (int i = 0; i < kktM.length; ++i)
					for (int j = 0; j < kktM.length; ++j) {
						M[i][j] = kktM[i][j];
					}
				kktX = LA.laPacksolveSym(M, kktY);
			} catch (SingularException sex) {
				return result;
			}

			// distance is greater than zero, so look for a point to add to the
			// system:
			// the "distance vector" from b to a:
			double[] currentVec = {}; // LA-routines expand vector when
			// necessary.
			for (int i = 0; i < inactiveAlength; ++i) {
				currentVec = LA.plus(currentVec, LA.mul(+kktX[aStart + i],
						a[piA[i]]));
			}
			for (int j = 0; j < inactiveBlength; ++j) {
				currentVec = LA.plus(currentVec, LA.mul(-kktX[bStart - j],
						b[piB[j]]));
			}

			result = LA.dot(currentVec, currentVec);

			// if the distance is zero, the Lagrangians are both zero or the
			// maximal inactive points are set,
			// then the affine spaces intersect.
			if (scOp.equal(result, 0d) || scOp.equal(kktX[maxInactive + 0], 0d)
					&& scOp.equal(kktX[maxInactive + 1], 0d)
					|| inactiveAlength + inactiveBlength == maxInactive) {

				if (coeffsA != null) {
					// return intersection location at a
					Arrays.fill(coeffsA, 0d);
					for (int i = 0; i < inactiveAlength; ++i) {
						coeffsA[piA[i]] = kktX[aStart + i];
					}
				}
				if (coeffsB != null) {
					// return intersection location at b
					Arrays.fill(coeffsB, 0d);
					for (int j = 0; j < inactiveBlength; ++j) {
						coeffsB[piB[j]] = kktX[bStart - j]; // note: MINUS j
						// (see storage
						// schema above)
					}
				}
				return result; // which should be zero;
			}

			/*
			 * Now find the Lagrangian of the active point which has maximal
			 * absolute value. Note that the sign of the Lagrangian doesn't
			 * matter as this method computes the intersection of affine spaces.
			 */

			int iMax = -1;
			double iMaxLagrangian = 0d;
			for (int i = inactiveAlength; i < a.length; ++i) {
				double iLagrangian = Math.abs(LA.dot(a[piA[i]], currentVec)
						+ kktX[maxInactive + 0]);
				if (iLagrangian > iMaxLagrangian) {
					iMaxLagrangian = iLagrangian;
					iMax = i;
				}
			}

			int jMax = -1;
			double jMaxLagrangian = 0d;
			for (int j = inactiveBlength; j < b.length; ++j) {
				double jLagrangian = Math.abs(LA.dot(b[piB[j]], currentVec)
						+ kktX[maxInactive + 1]);
				if (jLagrangian > jMaxLagrangian) {
					jMaxLagrangian = jLagrangian;
					jMax = j;
				}
			}

			if (scOp.equal(iMaxLagrangian, 0d)
					&& scOp.equal(jMaxLagrangian, 0d)) {

				// then all active points are perpendicular to the current
				// vector

				if (coeffsA != null) {
					// return intersection location at a
					Arrays.fill(coeffsA, 0d);
					for (int i = 0; i < inactiveAlength; ++i) {
						coeffsA[piA[i]] = kktX[aStart + i];
					}
				}
				if (coeffsB != null) {
					// return intersection location at b
					Arrays.fill(coeffsB, 0d);
					for (int j = 0; j < inactiveBlength; ++j) {
						coeffsB[piB[j]] = kktX[bStart - j];
						// note: MINUS j(see storage schema above)
					}
				}

				return LA.dot(currentVec, currentVec); // distance is, hence,
				// zero.
			}

			// now one of the Lagrangians is (significantly) different to zero.

			if (iMaxLagrangian >= jMaxLagrangian) {
				// a point from a will be added to the KKT-system:

				// first swap indexes in the permutation:
				int newIdx = piA[iMax];
				piA[iMax] = piA[inactiveAlength];
				piA[inactiveAlength] = newIdx;

				double[] point = a[newIdx];

				// and add the point to the KKT-system:
				kktM[inactiveAlength][inactiveAlength] = LA.dot(point, point);

				for (int i = 0; i < inactiveAlength; ++i) {
					kktM[i][inactiveAlength] = kktM[inactiveAlength][i] = LA
							.dot(a[piA[i]], point);
				}
				for (int j = 0; j < inactiveBlength; ++j) {
					// note: strictly less
					kktM[bStart - j][inactiveAlength] = kktM[inactiveAlength][bStart
							- j] = -LA.dot(b[piB[j]], point);
				}
				kktM[maxInactive + 0][inactiveAlength] = kktM[inactiveAlength][maxInactive + 0] = 1d;

				++inactiveAlength;

				systemHasChanged = true;

			} else {
				// a point from b will be added to the KKT-system

				int newIdx = piB[jMax];
				piB[jMax] = piB[inactiveBlength];
				piB[inactiveBlength] = newIdx;

				double[] point = b[newIdx];

				// and add the point to the KKT-system:
				final int rowCol = bStart - inactiveBlength;

				kktM[rowCol][rowCol] = LA.dot(point, point);

				for (int j = 0; j < inactiveBlength; ++j) {
					kktM[bStart - j][rowCol] = kktM[rowCol][bStart - j] = LA
							.dot(b[piB[j]], point);
				}
				for (int i = 0; i < inactiveAlength; ++i) {
					kktM[i][rowCol] = kktM[rowCol][i] = -LA.dot(a[piA[i]],
							point);
				}
				kktM[maxInactive + 1][rowCol] = kktM[rowCol][maxInactive + 1] = 1d;

				++inactiveBlength;

				systemHasChanged = true;
			}
		}

		return result;
	}

	/**
	 * Computes the (square of) the distance between the convex hulls of the two
	 * given point sets and eventually also the convex coordinates of points at
	 * minimal distance.
	 * 
	 * If a coeffs parameter is specified the corresponding coefficients of the
	 * convex combination of an accordung closest point is specified.
	 * {@link http://en.wikipedia.org/wiki/Convex_combination}
	 * 
	 * The algorithm solves the optimization problem minimize length(coeffsA[0]
	 * * a[0] + ... + coeffsA[n] * a[n] - coeffsB[0]*b[0] - ... -
	 * coeffsB[m]*b[m]) as a function in the coefficients respecting the
	 * conditions coeffsA[0] + ... + coeffsA[n] = 1 coeffsB[0] + ... +
	 * coeffsB[m] = 1 coeffsA[i] >= 0 for all i = 0...n coeffsB[j] >= 0 for all
	 * j = 0...n.
	 * 
	 * The linear equation system kktM * kktX = kktY is iteratively constructed
	 * using the Lagrangians of the previous solution and at each iteration step
	 * adding the point which is most "inclined" towards the other simplex.
	 * {@link http://en.wikipedia.org/wiki/Quadratic_programming}
	 * 
	 * The algorithm uses the modified version of the active set method
	 * {@link http://en.wikipedia.org/wiki/Active_set} as described at the Forum
	 * Bauinformatik 2009 {@link http
	 * ://digbib.ubka.uni-karlsruhe.de/volltexte/1000012014} (pp. 93-103), by
	 * the author:
	 * 
	 * @param a
	 *            an array of points defining an affine space clA
	 * @param b
	 *            an array of points defining another affine space clB
	 * @param coeffsA
	 *            if not null, the affine coefficients for a point pa at minimal
	 *            distance to clB, i.o.w.
	 *            <code>pa = sum coeffsA[i]*a[i] for i in 0..a.length</code>,
	 *            will be stored here. If the specified array is too short a
	 *            ArrayIndexOutOfBoundsException will occur.
	 * @param coeffsB
	 *            if not null, the affine coefficients for a point pb at minimal
	 *            distance to clA, i.o.w.
	 *            <code>pb = sum coeffsB[j]*b[j] for j in 0..b.length</code>,
	 *            will be stored here. If the specified array is too short a
	 *            ArrayIndexOutOfBoundsException will occur.
	 * @param scOp
	 *            the Scalar operator which compares double values up to a
	 *            specified error.
	 * @return the square of the distance between the convex closure of a and
	 *         the convex closure of b.
	 */
	public static double simplexDistance(double[][] a, double[][] b,
			double[] coeffsA, double[] coeffsB, ScalarOperator scOp) {

		if (a.length == 0 || b.length == 0) {
			return Double.POSITIVE_INFINITY;
		}

		final int dim;
		{
			int tmp = 0;
			for (double[] pA : a) {
				tmp = Math.max(tmp, pA.length);
			}
			for (double[] pB : b) {
				tmp = Math.max(tmp, pB.length);
			}
			dim = tmp;
		}

		if (dim == 0) {
			return 0d;
		}

		// the maximal number of inactive points.
		int maxInactive = Math.min(a.length + b.length, dim + 2);
		// Note: dim is also the index of the last inactive entry in the
		// KKT-system.

		// dim+1+2 is the maximal size of the KKT-system:
		// n+1 non-active points and two affine constraints
		int maxSysSize = maxInactive + 2;

		// the matrix of the KKT-system
		double[][] kktM = new double[maxSysSize][maxSysSize];
		for (int i = 0; i < maxInactive; ++i) { // fill the diagonal for active
			// entries.
			kktM[i][i] = 1d;
		}

		// the right-hand side of the KKT-system.
		double[] kktY = new double[maxSysSize];
		Arrays.fill(kktY, 0, maxInactive, 0d); // many zeros
		kktY[maxInactive + 0] = 1d; // and two
		kktY[maxInactive + 1] = 1d; // ones (for the Lagrangians)

		double result = 0d;

		// Set up the permutation of points used by the algorithm
		// Each permutation is separated into three departments:
		// active points: points which have been activated during the iteration
		// inactive points: points which are currently inactive
		// forthcoming points: point which are currently active but may become
		// inactive in future.
		int[] piA = new int[a.length]; // the current permutation of points a
		for (int i = 0; i < a.length; ++i) {
			piA[i] = i;
		}

		int[] piB = new int[b.length]; // the current permutation of points b
		for (int j = 0; j < b.length; ++j) {
			piB[j] = j;
		}

		// store the first values into matrix:
		// storage schema: points from a go from "left" 0 to "right" 1, 2, ...
		// points from b go from "right" dim-0 to "left" dim-1, dim-2, ...
		final int aStart = 0;
		final int bStart = maxInactive - 1;

		kktM[aStart][aStart] = LA.dot(a[0], a[0]);
		kktM[bStart][bStart] = LA.dot(b[0], b[0]);
		kktM[aStart][bStart] = kktM[bStart][aStart] = -LA.dot(a[0], b[0]); // negative!!!
		// store the coefficients for inactive Lagrangians:
		kktM[aStart][maxInactive + 0] = kktM[maxInactive + 0][aStart] = 1d;
		// to compute Lagrangian of a

		kktM[bStart][maxInactive + 1] = kktM[maxInactive + 1][bStart] = 1d;
		// to compute Lagrangian of b

		int activeAlength = 0;
		int activeBlength = 0;
		int inactiveAlength = 1;
		int inactiveBlength = 1;

		double[] kktX = null;

		boolean systemHasChanged = true;
		mainLoop: while (systemHasChanged) {
			systemHasChanged = false;

			try {
				// FIRST(!) copy the matrix, because lapack is an
				// in-place-solver:
				double[][] M = new double[kktM.length][kktM.length];
				for (int i = 0; i < kktM.length; ++i)
					for (int j = 0; j < kktM.length; ++j) {
						M[i][j] = kktM[i][j];
					}
				// THEN solve the (symmetric) system.
				kktX = LA.laPacksolveSym(M, kktY);
			} catch (SingularException sex) {
				// then old KKT-system solution will be returned.
				// note: the loop will b iterated at least once, because the
				// initial KKT system cannot be singular.
				break mainLoop;
			}

			// this is one of the differences to the affineDistance-algorithm:
			// if some coefficient is less than or equal to zero, its
			// corresponding vertex will be made active:
			int iMin = -1;
			int jMin = -1;
			double coeffMin = Double.POSITIVE_INFINITY;

			for (int i = 0; i < inactiveAlength; ++i) {
				if (kktX[aStart + i] < coeffMin) {
					coeffMin = kktX[aStart + i];
					iMin = i;
				}
			}

			for (int j = 0; j < inactiveBlength; ++j) {
				if (kktX[bStart - j] < coeffMin) {
					coeffMin = kktX[bStart - j];
					jMin = j;
					iMin = -1;
				}
			}

			if (scOp.lessOrEqual(coeffMin, 0d)) {

				if (iMin >= 0) {

					int i = activeAlength + iMin;
					int iPos = aStart + i;

					// overwrite current matrix row/col with last incative
					// row/col
					int lastInactiveMatrixIndex = aStart
							+ (inactiveAlength - 1);
					int lastInactiveIndex = activeAlength
							+ (inactiveAlength - 1);

					for (int rowCol = 0; rowCol < maxSysSize; ++rowCol) {
						kktM[iPos][rowCol] = kktM[rowCol][iPos] = kktM[iPos][lastInactiveMatrixIndex];
						// set lastInactiveIndex rowCol to "unused;
						kktM[iPos][lastInactiveMatrixIndex] = kktM[lastInactiveMatrixIndex][iPos] = 0d;
					}
					// still set lastInactiveIndex rowCol to "unused;
					kktM[lastInactiveMatrixIndex][lastInactiveMatrixIndex] = 1d;

					// now lastInactiveIndex must go to i
					// i must be rotated inactiveBlengthinto activeAlength
					// and L must be rotated to i :
					// [ * * * | a b i c d L | F * * * * * ]
					// |<--active->|<-- inactive -->|<-- forthcoming -->
					// shall become:
					// [ * * * i | a b L c d | F * * * * * ]
					// |<-- active -->|<-- inactive -->|<-- forthcoming -->

					int piAi = piA[i];
					for (int pos = i; pos > activeAlength; --pos) {
						piA[pos] = piA[pos - 1];
					}
					piA[activeAlength] = piAi;
					++activeAlength;
					--inactiveAlength;

					int piALast = piA[lastInactiveIndex];
					for (int pos = lastInactiveIndex; pos > i; --pos) {
						piA[pos] = piA[pos - 1];
					}
					piA[i] = piALast;
					systemHasChanged = true;

					continue mainLoop;

				} else { // jMin >= 0;

					int j = activeBlength + jMin;
					int jPos = bStart - j;

					// overwrite current matrix row/col with last incative
					// row/col
					int lastInactiveMatrixIndex = bStart
							- (inactiveBlength - 1);
					int lastInactiveIndex = activeBlength
							+ (inactiveBlength - 1);

					for (int rowCol = 0; rowCol < maxSysSize; ++rowCol) {
						kktM[jPos][rowCol] = kktM[rowCol][jPos] = kktM[jPos][lastInactiveMatrixIndex];
						// set lastInactiveIndex rowCol to "unused;
						kktM[jPos][lastInactiveMatrixIndex] = kktM[lastInactiveMatrixIndex][jPos] = 0d;
					}
					// still set lastInactiveIndex rowCol to "unused;
					kktM[lastInactiveMatrixIndex][lastInactiveMatrixIndex] = 1d;

					// now lastInactiveIndex must go to j
					// j must be rotated into activeBlength
					// and L must be rotated to j :
					// [ * * * | a b j c d L | F * * * * * ]
					// |<--active->|<-- inactive -->|<-- forthcoming -->
					// shall become:
					// [ * * * j | a b L c d | F * * * * * ]
					// |<-- active -->|<-- inactive -->|<-- forthcoming -->

					int piBj = piB[j];
					for (int pos = j; pos > activeBlength; --pos) {
						piB[pos] = piB[pos - 1];
					}
					piB[activeBlength] = piBj;
					++activeBlength;
					--inactiveBlength;

					int piBLast = piB[lastInactiveIndex];
					for (int pos = lastInactiveIndex; pos > j; --pos) {
						piB[pos] = piB[pos - 1];
					}
					piB[j] = piBLast;
					systemHasChanged = true;

					continue mainLoop;

				}
			}

			// now there are only forthcoming active points.

			// Now look for a point to add to the system:

			// the "distance vector" from b to a:
			double[] currentVec = {}; // LA-routines expand vector dimension
			// when necessary.
			for (int i = 0; i < inactiveAlength; ++i) {
				currentVec = LA.plus(currentVec, LA.mul(+kktX[aStart + i],
						a[piA[i]]));
			}
			for (int j = 0; j < inactiveBlength; ++j) {
				currentVec = LA.plus(currentVec, LA.mul(-kktX[bStart - j],
						b[piB[j]]));
			}
			result = LA.dot(currentVec, currentVec);

			// if the distance is zero, the Lagrangians are both zero or the
			// maximal inactive points are set,
			// then the simplexes intersect.
			if (scOp.equal(result, 0d) || scOp.equal(kktX[maxInactive + 0], 0d)
					&& scOp.equal(kktX[maxInactive + 1], 0d) // the Lagrangians
					// for a and b
					|| inactiveAlength + inactiveBlength == maxInactive) {

				break mainLoop;
			}

			// Now find the Lagrangian of the active point which has maximal
			// value.
			// Note that the sign of the Lagrangian DOES matter as this method
			// computes the intersection of simplexes.
			int iMax = -1;
			double iMaxLagrangian = 0d;
			for (int i = activeAlength + inactiveAlength; i < a.length; ++i) {
				// difference to affine spaces: take sign into account (no
				// Math.abs(...)).
				double iLagrangian = -LA.dot(a[piA[i]], currentVec)
						- kktX[maxInactive + 0];
				if (iLagrangian > iMaxLagrangian) {
					iMaxLagrangian = iLagrangian;
					iMax = i;
				}
			}

			int jMax = -1;
			double jMaxLagrangian = 0d;
			for (int j = activeBlength + inactiveBlength; j < b.length; ++j) {
				// difference to affine spaces: take sign into account (no
				// Math.abs(...)).
				double jLagrangian = -LA.dot(b[piB[j]], currentVec)
						- kktX[maxInactive + 1];
				if (jLagrangian > jMaxLagrangian) {
					jMaxLagrangian = jLagrangian;
					jMax = j;
				}
			}

			if (scOp.equal(iMaxLagrangian, 0d)
					&& scOp.equal(jMaxLagrangian, 0d)) {

				// then all forthcoming active points are perpendicular to the
				// current vector

				if (coeffsA != null) {
					// return intersection location at a
					Arrays.fill(coeffsA, 0d);
					for (int i = activeAlength + inactiveAlength; i > activeAlength;) {
						--i;
						coeffsA[piA[i]] = kktX[aStart + i];
					}
				}
				if (coeffsB != null) {
					// return intersection location at b
					Arrays.fill(coeffsB, 0d);
					for (int j = activeBlength + inactiveBlength; j > activeBlength;) {
						--j;
						coeffsB[piB[j]] = kktX[bStart - j]; // note: MINUS j
						// (see storage
						// schema above)
					}
				}

				return LA.dot(currentVec, currentVec); // distance is, hence,
				// zero.
			}

			// now one of the Lagrangians is (significantly) greater than zero.

			if (iMaxLagrangian >= jMaxLagrangian) {
				// a point from a will be added to the KKT-system:

				// first swap indexes in the permutation:
				int nextForthcoming = activeAlength + inactiveAlength;
				int newIdx = piA[iMax];
				piA[iMax] = piA[nextForthcoming];
				piA[nextForthcoming] = newIdx;

				double[] point = a[newIdx];

				// and add the point to the KKT-system:
				kktM[inactiveAlength][inactiveAlength] = LA.dot(point, point);

				for (int i = 0; i < inactiveAlength; ++i) {
					kktM[i][inactiveAlength] = kktM[inactiveAlength][i] = LA
							.dot(a[piA[i]], point);
				}
				for (int j = 0; j < inactiveBlength; ++j) { // note: strictly
					// less.
					kktM[bStart - j][inactiveAlength] = kktM[inactiveAlength][bStart
							- j] = -LA.dot(b[piB[j]], point);
				}
				kktM[maxInactive + 0][inactiveAlength] = kktM[inactiveAlength][maxInactive + 0] = 1d;

				++inactiveAlength;

				systemHasChanged = true;

			} else {
				// a point from b will be added to the KKT-system

				int nextForthcoming = activeBlength + inactiveBlength;
				int newIdx = piB[jMax];
				piB[jMax] = piB[nextForthcoming];
				piB[nextForthcoming] = newIdx;

				double[] point = b[newIdx];

				// and add the point to the KKT-system:
				final int rowCol = bStart - inactiveBlength;

				kktM[rowCol][rowCol] = LA.dot(point, point);

				for (int j = 0; j < inactiveBlength; ++j) {
					kktM[bStart - j][rowCol] = kktM[rowCol][bStart - j] = LA
							.dot(b[piB[j]], point);
				}
				for (int i = 0; i < inactiveAlength; ++i) {
					kktM[i][rowCol] = kktM[rowCol][i] = -LA.dot(a[piA[i]],
							point);
				}
				kktM[maxInactive + 1][rowCol] = kktM[rowCol][maxInactive + 1] = 1d;

				++inactiveBlength;

				systemHasChanged = true;
			}
		}

		if (coeffsA != null) {
			// return closest point location at a
			Arrays.fill(coeffsA, 0d);
			for (int i = activeAlength + inactiveAlength; i > activeAlength;) {
				--i;
				coeffsA[piA[i]] = kktX[aStart + i];
			}
		}
		if (coeffsB != null) {
			// return closest point location at b
			Arrays.fill(coeffsB, 0d);
			for (int j = activeBlength + inactiveBlength; j > activeBlength;) {
				--j;
				coeffsB[piB[j]] = kktX[bStart - j]; // note: MINUS j (see
				// storage schema above)
			}
		}

		return result;
	}

	/**
	 * Returns the relative position of the specified point to the specified
	 * simplex. The point is thereby orthogonally projected onto the simplex'
	 * affine subspace.
	 * 
	 * The result is either 0, representing the interior of the simplex, 1, for
	 * the simplex boundary or 2 for the simplex exterior. If coeffs is
	 * specified, the barycentric coordinates of the projected point are stored
	 * into theis array. If specified, the array must be big enough to store
	 * alls the coordinates or else the method will throw an
	 * ArrayIndexOutOfBoundsException. Hence
	 * <code> coeffs == null || simplex.length <= coeffs.length </code> must
	 * always hold.
	 * 
	 * If the siplex is not a simplex (if the vertices do not generate a
	 * n-1-dimensional affine subspace, where n is the number of specified
	 * vertices) the result my be wrong (because then the barycentric
	 * coordinates of the point are not unique). *
	 * 
	 * Note that the topological space of the specified simplex is always the
	 * simplex' affine subspace. Hence a point is always in the interior of a
	 * 0-simplex.
	 * 
	 * @param simplex
	 *            the simplex which may or may not contain the point
	 * @param vertex
	 *            the point which may or may not be element of the simplex.
	 * @param coeffs
	 *            if not null, the barycentric coordinates of the point will be
	 *            stored in that array
	 * @param scOp
	 *            the comparison operator who decides if a coordinate is less
	 *            than, equal or greater than zero.
	 * @param projected
	 *            if true, the point will be projeted onto the affine space
	 *            generated by that simplex. Otherwise a vertex outside the
	 *            simplex' affine space will be considered exterior.
	 * @return the relative position of the point to the simplex as index in the
	 *         famous 3x3-matrix: 0 means interior, 1 means boundary, and 2
	 *         means exterior.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the coeffs are not null and of length less than simplex'
	 *             length.
	 */
	public static int simplexVertexRelation(double[][] simplex,
			double[] vertex, double[] coeffs, ScalarOperator scOp,
			boolean projected) {

		double[][] vertexSimplex = { vertex };
		double[] simplexCoeffs = new double[simplex.length];
		// just in case coeffs is too long and may then contain garbage data

		// use affine distance to actually carry out computation.
		double distance = affineDistance(simplex, vertexSimplex, simplexCoeffs,
				null, scOp);
		if (coeffs != null) {
			// store result
			System.arraycopy(simplexCoeffs, 0, coeffs, 0, simplexCoeffs.length);
		}

		if ((!projected) && scOp.greaterThan(distance, 0d)) {
			return 2;
		}

		double minCoeff = getMin(simplexCoeffs);

		if (scOp.equal(minCoeff, 0d)) {
			return 1; // point is at boundary
		}

		// now minCoeff is outside scOp's tolerance interval around 0d.
		// hence another call to scOp is not necessary.
		if (minCoeff < 0d) { // equivalent to scOp.lessThan(minCoeff, od);
			return 2; // point is in exterior
		}
		// equivalent to scOp.lessThan(minCoeff, od);
		return 0;
	}

	public static final double getMin(double... values) {
		double result = Double.POSITIVE_INFINITY;
		for (double value : values) {
			result = Math.min(result, value);
		}
		return result;
	}

	public static final double getMax(double... values) {
		double result = Double.NEGATIVE_INFINITY;
		for (double value : values) {
			result = Math.max(result, value);
		}
		return result;
	}

	/**
	 * Returns the minimum double of given two parameters No epsilon test !
	 * 
	 * @param val1
	 *            first parameter
	 * @param val2
	 *            second parameter
	 * @return double - min of the two parameters.
	 */
	public static final double getMin(double val1, double val2) {
		return Math.min(val1, val2);
	}

	/**
	 * Returns the minimum double of given three parameters No epsilon test !
	 * 
	 * @param val1
	 *            first parameter
	 * @param val2
	 *            second parameter
	 * @param val3
	 *            third parameter
	 * @return double - min of the three parameters.
	 */
	public static final double getMin(double val1, double val2, double val3) {
		return Math.min(Math.min(val1, val2), val3);
	}

	/**
	 * Returns the minimum double of given four parameters No epsilon test !
	 * 
	 * @param val1
	 *            first parameter
	 * @param val2
	 *            second parameter
	 * @param val3
	 *            third parameter
	 * @param val4
	 *            fourth parameter
	 * @return double - min og the four parameters.
	 */
	public static final double getMin(double val1, double val2, double val3,
			double val4) {
		return Math.min(Math.min(val1, val2), Math.min(val3, val4));
	}

	/**
	 * Returns the maximum double of given two parameters No epsilon test !
	 * 
	 * @param val1
	 *            first parameter
	 * @param val2
	 *            second parameter
	 * @return double - max of the two parameters.
	 */
	public static final double getMax(double val1, double val2) {
		return Math.max(val1, val2);
	}

	/**
	 * Returns the maximum double of given three parameters No epsilon test !
	 * 
	 * @param val1
	 *            first parameter
	 * @param val2
	 *            second parameter
	 * @param val3
	 *            third parameter
	 * @return double - max of the three parameters.
	 */
	public static final double getMax(double val1, double val2, double val3) {
		return Math.max(Math.max(val1, val2), val3);
	}

	/**
	 * Returns the maximum double of given four parameters No epsilon test !
	 * 
	 * @param val1
	 *            first parameter
	 * @param val2
	 *            second parameter
	 * @param val3
	 *            third parameter
	 * @param val4
	 *            fourth parameter
	 * @return double - max of the four parameters.
	 */
	public static final double getMax(double val1, double val2, double val3,
			double val4) {
		return Math.max(Math.max(val1, val2), Math.max(val3, val4));
	}

	/**
	 * Returns the given Point3D array sorted after x,y and z coordinates.
	 * 
	 * @param points
	 *            Point3D[] to be sorted
	 * @return Point3D[] - with points sorted (length = args.length).
	 */
	public static final Point3D[] getSorted(Point3D[] points) {
		return getSorted(points, POINT_COMP);
	}

	/**
	 * Returns the given Point3D array sorted after the given Point3DComparator.
	 * 
	 * @param points
	 *            Point3D[] to be sorted
	 * @param comp
	 *            Point3DComparator implementation
	 * @return Point3D[] - with points sorted (length = args.length).
	 */
	public static final Point3D[] getSorted(Point3D[] points,
			Point3DComparator comp) {
		Arrays.sort(points, comp);
		return points;
	}

	/**
	 * Returns an Point3D array with the given points sorted after x,y and z
	 * coordinates.
	 * 
	 * @param p1
	 *            first Point3D to be sorted
	 * @param p2
	 *            second Point3D to be sorted
	 * @return Point3D[] - with points sorted (length = args.length).
	 */
	public static final Point3D[] getSorted(Point3D p1, Point3D p2) {
		Point3D[] ps = new Point3D[] { p1, p2 };
		Arrays.sort(ps, POINT_COMP);
		return ps;
	}

	/**
	 * Returns an Point3D array with the given points sorted after x,y and z
	 * coordinates.
	 * 
	 * @param p1
	 *            first Point3D to be sorted
	 * @param p2
	 *            second Point3D to be sorted
	 * @param p3
	 *            third Point3D to be sorted
	 * 
	 * @return Point3D[] - with points sorted (length = args.length).
	 */
	public static final Point3D[] getSorted(Point3D p1, Point3D p2, Point3D p3) {
		Point3D[] ps = new Point3D[] { p1, p2, p3 };
		Arrays.sort(ps, POINT_COMP);
		return ps;
	}

	/**
	 * Returns an Point3D array with the given points sorted after x,y and z
	 * coordinates.
	 * 
	 * @param p1
	 *            first Point3D to be sorted
	 * @param p2
	 *            second Point3D to be sorted
	 * @param p3
	 *            third Point3D to be sorted
	 * @param p4
	 *            fourth Point3D to be sorted
	 * @return Point3D[] - with points sorted (length = args.length).
	 */
	public static final Point3D[] getSorted(Point3D p1, Point3D p2, Point3D p3,
			Point3D p4) {
		Point3D[] ps = new Point3D[] { p1, p2, p3, p4 };
		Arrays.sort(ps, POINT_COMP);
		return ps;
	}

	/**
	 * returns the MBB of the given Tetrahedron3D extended by psi.
	 * 
	 * @param tetra
	 *            Tetrahedron3D
	 * @param psi
	 *            double buffer
	 * @return MBB3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public final static MBB3D getExtendedMBB(Tetrahedron3D tetra, double psi) {
		Point3D[] points = tetra.getPoints();
		Point3D pMin = new Point3D(GeomUtils.getMin(points[0].getX(), points[1]
				.getX(), points[2].getX(), points[3].getX())
				- psi, GeomUtils.getMin(points[0].getY(), points[1].getY(),
				points[2].getY(), points[3].getY())
				- psi, GeomUtils.getMin(points[0].getZ(), points[1].getZ(),
				points[2].getZ(), points[3].getZ())
				- psi);
		Point3D pMax = new Point3D(GeomUtils.getMax(points[0].getX(), points[1]
				.getX(), points[2].getX(), points[3].getX())
				+ psi, GeomUtils.getMax(points[0].getY(), points[1].getY(),
				points[2].getY(), points[3].getY())
				+ psi, GeomUtils.getMax(points[0].getZ(), points[1].getZ(),
				points[2].getZ(), points[3].getZ())
				+ psi);
		return new MBB3D(pMin, pMax);
	}

	/**
	 * returns the MBB of the given Triangle3D extended by psi.
	 * 
	 * @param tri
	 *            the given Triangle3D
	 * @param psi
	 *            double buffer
	 * @return MBB3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public final static MBB3D getExtendedMBB(Triangle3D tri, double psi) {
		Point3D[] points = tri.getPoints();
		Point3D pMin = new Point3D(GeomUtils.getMin(points[0].getX(), points[1]
				.getX(), points[2].getX())
				- psi, GeomUtils.getMin(points[0].getY(), points[1].getY(),
				points[2].getY())
				- psi, GeomUtils.getMin(points[0].getZ(), points[1].getZ(),
				points[2].getZ())
				- psi);
		Point3D pMax = new Point3D(GeomUtils.getMax(points[0].getX(), points[1]
				.getX(), points[2].getX())
				+ psi, GeomUtils.getMax(points[0].getY(), points[1].getY(),
				points[2].getY())
				+ psi, GeomUtils.getMax(points[0].getZ(), points[1].getZ(),
				points[2].getZ())
				+ psi);
		return new MBB3D(pMin, pMax);
	}

	/**
	 * Returns the MBB of the given Segment3D extended by the constant psi.
	 * 
	 * @param se
	 *            the given Segment3D
	 * @param psi
	 *            double buffer
	 * @return MBB3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public final static MBB3D getExtendedMBB(Segment3D se, double psi) {
		Point3D[] point = se.getPoints();
		return new MBB3D(new Point3D(GeomUtils.getMin(point[0].getX(), point[1]
				.getX())
				- psi,
				GeomUtils.getMin(point[0].getY(), point[1].getY()) - psi,
				GeomUtils.getMin(point[0].getZ(), point[1].getZ()) - psi),
				new Point3D(GeomUtils.getMax(point[0].getX(), point[1].getX())
						+ psi, GeomUtils.getMax(point[0].getY(), point[1]
						.getY())
						+ psi, GeomUtils.getMax(point[0].getZ(), point[1]
						.getZ())
						+ psi));
	}

	/**
	 * Returns the MBB of the given Point3D extended by the constant psi.
	 * 
	 * @param point
	 *            the given Point3D
	 * @param psi
	 *            double buffer
	 * @return MBB3D.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public final static MBB3D getExtendedMBB(Point3D point, double psi) {
		return new MBB3D(new Point3D(point.getX() - psi, point.getY() - psi,
				point.getZ() - psi), new Point3D(point.getX() + psi, point
				.getY()
				+ psi, point.getZ() + psi));
	}

	/*
	 * Returns the two points with max. or min. Euclidean Distance dependent on
	 * flag minMax.
	 * 
	 * @param minMax true for max, false for min distance calculation
	 * 
	 * @param points array of Point3D objects
	 * 
	 * @return Point3D[] - resultPoints.
	 */
	private final static Point3D[] getPointsWithDistance(boolean minMax,
			Point3D[] points) {

		Point3D[] resultPoints = { points[0], points[1] };
		double distance = points[0].euclideanDistance(points[1]);
		double dist = -1;
		int length = points.length;
		for (int i = 0; i < length; i++) {
			for (int j = (i + 1); j < length; j++) {
				dist = points[i].euclideanDistance(points[j]);

				if (minMax == true) {
					if (dist > distance) {
						resultPoints[0] = points[i];
						resultPoints[1] = points[j];
						distance = dist;
					}
				} else // minMax == false
				if (dist < distance) {
					resultPoints[0] = points[i];
					resultPoints[1] = points[j];
					distance = dist;
				}
			}
		}
		return resultPoints;
	}

	public final static Point3D[] getPointsWithMaxDistance(Point3D[] points) {
		return getPointsWithDistance(true, points);
	}

	public final static Point3D[] getPointsWithMinDistance(Point3D[] points) {
		return getPointsWithDistance(false, points);
	}

	/**
	 * Tests if the given two triangles are equivalent in geometry and their
	 * orientation.
	 * 
	 * @param tr1
	 *            Triangle3D 1
	 * @param tr2
	 *            Triangle3D 2
	 * @param sop
	 *            ScalarOperator
	 * @return boolean - true if equivalent, false otherwise.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(ScalarOperator) of the
	 *             class Vector3D.
	 */
	public final static boolean isOrientationEquivalentTriangle(Triangle3D tr1,
			Triangle3D tr2, ScalarOperator sop) {
		if (!tr1.isGeometryEquivalent(tr2, sop))
			return false;

		if (tr1.getNormal(sop).isEqual(tr2.getNormal(sop), sop))
			return true;
		else
			return false;
	}

	/*
	 * Comparator to compare Point3D by comparing their x-coordinates.
	 */
	private static final class Point3D_X_Comparator implements
			Point3DComparator {

		/*
		 * Compares points by comparing their x-coordinates.
		 * 
		 * @param o1 first Object
		 * 
		 * @param 02 second Object
		 * 
		 * @throws IllegalArgumentException - if the x-coordinates of the given
		 * points are NaN doubles and cannot be compared.
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			return doubleCompare(((Point3D) o1).getX(), ((Point3D) o2).getX());
		}
	}

	/*
	 * Comparator to compare Point3D by comparing their y-coordinates.
	 */
	private static final class Point3D_Y_Comparator implements
			Point3DComparator {

		/*
		 * Compares points by comparing their y-coordinates.
		 * 
		 * @param o1 first Object
		 * 
		 * @param 02 second Object
		 * 
		 * @throws IllegalArgumentException - if the y-coordinates of the given
		 * points are NaN doubles and cannot be compared.
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			return doubleCompare(((Point3D) o1).getY(), ((Point3D) o2).getY());
		}
	}

	/*
	 * Comparator to compare Point3D by comparing their z-coordinates.
	 */
	private static final class Point3D_Z_Comparator implements
			Point3DComparator {

		/*
		 * Compares points by comparing their z-coordinates.
		 * 
		 * @param o1 first Object
		 * 
		 * @param 02 second Object
		 * 
		 * @throws IllegalArgumentException - if the z-coordinates of the given
		 * points are NaN doubles and cannot be compared.
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			return doubleCompare(((Point3D) o1).getZ(), ((Point3D) o2).getZ());
		}
	}

	/*
	 * Comparator to compare Point3D by comparing their x-, y- and
	 * z-coordinates.
	 */
	private static final class Point3D_XYZ_Comparator implements
			Point3DComparator {

		/*
		 * Compares points by comparing their x-, y- and z-coordinates.
		 * 
		 * @param o1 first Object
		 * 
		 * @param 02 second Object
		 * 
		 * @throws IllegalArgumentException - if the coordinates of the given
		 * points are NaN doubles and cannot be compared.
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			Point3D p1 = (Point3D) o1;
			Point3D p2 = (Point3D) o2;

			int result;

			result = doubleCompare(p1.getX(), p2.getX());
			if (result != 0) {
				return result;
			}

			result = doubleCompare(p1.getY(), p2.getY());
			if (result != 0) {
				return result;
			}

			return doubleCompare(p1.getZ(), p2.getZ());
		}
	}

	/**
	 * Interface used just for type specification.
	 */
	interface Point3DComparator extends Comparator {
	}

}
