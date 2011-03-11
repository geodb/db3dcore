/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.netlib.lapack.DGESV;
import org.netlib.lapack.DSYSV;
import org.netlib.util.intW;

import de.uos.igf.db3d.dbms.api.Db3dSimpleResourceBundle;

/**
 * This is some basic linear algebra without referring to the underlying
 * algebraic notions like fields, rings etc.
 * 
 * A more elaborate linear algebra can be found in linalg.util.Modules.
 * 
 * 
 * The solver relies on the existence of the Java LAPCAK library
 * (jlapack-0.8.zip). It can be found on
 * 
 * http://www.netlib.org/java/f2j/
 * 
 * 
 */
public class LA {

	public static String toString(double d) {
		return String.format(Locale.ENGLISH, "%.4f", d);
	}

	@SuppressWarnings("serial")
	public static class SingularException extends Exception {
	}

	public static class MatrixString {

		double[][] m;

		public String toString() {
			int rows = m.length;
			int cols = 0;

			for (int row = 0; row < rows; ++row) {
				try {
					cols = Math.max(cols, m[row].length);
				} catch (Exception ex) {
				}
			}

			String[][] mStr = new String[rows][cols];
			int[] colWidth = new int[cols];
			for (int row = 0; row < rows; ++row) {
				for (int col = 0; col < cols; ++col) {
					String aij = " ? ";
					try {
						aij = " " + LA.toString(m[row][col]) + " ";
					} catch (ArrayIndexOutOfBoundsException aioobex) {
					}
					mStr[row][col] = aij;
					colWidth[col] = Math.max(aij.length(), colWidth[col]);
				}
			}

			String[] strRows = new String[rows];
			Arrays.fill(strRows, "");

			for (int col = 0; col < cols; ++col) {
				String pad = "";
				for (int i = colWidth[col]; i > 0; --i) {
					pad += " ";
				}
				for (int row = 0; row < rows; ++row) {
					String str = mStr[row][col];
					strRows[row] += pad.substring(str.length());
					strRows[row] += str;
				}
			}

			String result = "";
			for (String strRow : strRows) {
				result += "\n[" + strRow + "]";
			}

			return result.substring(1);

		}

		/**
		 * @param m
		 */
		public MatrixString(double[][] m) {
			super();
			this.m = m;
		}

	}

	/**
	 * the smallest positive double e such that 1+e>e. In other words, the value
	 * of the least significant bit position of 1.0d.
	 */
	public static final double DOUBLE_EPSILON;

	/**
	 * the smallest positive float e such that 1+e>e. In other words, the value
	 * of the least significant bit position of 1.0f.
	 */
	public static final double FLOAT_EPSILON;

	public static final double DOUBLE_MIN_UNIT;

	static {
		double dResult;
		for (double e = dResult = 1.0d; e + 1.0d != 1.0d; e /= 2) {
			dResult = e;
		}
		DOUBLE_EPSILON = dResult;

		for (double e = dResult = 1.0d; isUnit(e / 2); e /= 2) {
			dResult = e;
		}
		DOUBLE_MIN_UNIT = dResult;

		float fResult;
		for (float e = fResult = 1.0f; e + 1.0f != 1.0f; e /= 2) {
			fResult = e;
		}
		FLOAT_EPSILON = fResult;
	}

	/**
	 * Computes the number of non-zero binary digits of the given double number.
	 * 
	 * @param d
	 * @return
	 * @throws IllegalArgumentException
	 *             - if the given double is Infinity or NaN.
	 */
	public static int digits(final double d) throws IllegalArgumentException {
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			throw new IllegalArgumentException(Db3dSimpleResourceBundle
					.getString("db3d.geom.infinandnan"));
		}
		if (d == 0.0d) {
			return 0;
		}
		double nd = Math.abs(d);
		int result = 0;
		while (nd < 1.0d) {
			nd *= 2;
		}
		while (nd >= 2.0d) {
			nd /= 2;
		}
		while (nd > 0.0d) {
			if (nd >= 1.0d) {
				++result;
				nd -= 1.0d;
				nd *= 2;
			}
		}
		return result;
	}

	public static double[] project(Collection<Integer> idx, double[] vec) {
		double[] result = new double[idx.size()];
		int pos = 0;
		for (int i : idx) {
			result[pos] = vec[i];
			++pos;
		}
		return result;
	}

	public static double[] inject(int length, Collection<Integer> idx,
			double[] vec) {
		double[] result = new double[length];
		inject(result, idx, vec);
		return result;
	}

	public static void inject(double[] result, Collection<Integer> idx,
			double[] vec) {
		int pos = 0;
		for (int i : idx) {
			result[i] = vec[pos];
			++pos;
		}
	}

	public static double[] project(int[] idx, double[] vec) {
		double[] result = new double[idx.length];
		int pos = 0;
		for (int i : idx) {
			result[pos] = vec[i];
			++pos;
		}
		return result;
	}

	public static double[][] project(int[] rIdx, int[] cIdx, double[][] M) {
		double[][] result = new double[rIdx.length][cIdx.length];
		int rPos = 0;
		for (int i : rIdx) {
			result[rPos] = project(cIdx, M[i]);
			++rPos;
		}
		return result;
	}

	public static double[][] project(Collection<Integer> idx, double[][] M) {
		return project(idx, idx, M);
	}

	public static double[][] project(Collection<Integer> rIdx,
			Collection<Integer> cIdx, double[][] M) {
		double[][] result = new double[rIdx.size()][cIdx.size()];
		int rPos = 0;
		for (int i : rIdx) {
			result[rPos] = project(cIdx, M[i]);
			++rPos;
		}
		return result;
	}

	public static double[][] project(int[] idx, double[][] M) {
		return project(idx, idx, M);
	}

	public static double[] inject(int length, int[] idx, double[] vec) {
		double[] result = new double[length];
		inject(result, idx, vec);
		return result;
	}

	public static void inject(double[] result, int[] idx, double[] vec) {
		int pos = 0;
		for (int i : idx) {
			result[i] = vec[pos];
			++pos;
		}
	}

	/**
	 * A simple Gausssian solver which fails if the matrix of the equations
	 * system is singular.
	 * 
	 * @param M
	 *            the matrix of the equations system
	 * @param y
	 *            the inhomogenious part of the system
	 * @return an array x of doubles such that Mx = y;
	 * @throws Exception
	 *             if the equation system doesn't have exactly one such
	 *             solution.
	 */
	public static double[] solve(double[][] M, double[] y) throws Exception {

		int mRows = M.length;
		int mCols = 0;
		for (double[] mRow : M) {
			mCols = Math.max(mCols, mRow.length);
		}
		if (mRows != mCols) {
			throw new SingularException();
		}

		double[][] My = new double[mRows][mCols + 1];

		for (int c = 0; c < mCols; ++c) {
			try {
				My[c][mCols] = y[c];
			} catch (ArrayIndexOutOfBoundsException ex) {
				My[c][mCols] = 0d;
			}
		}

		for (int r = 0; r < mRows; ++r) {
			for (int c = 0; c < mCols; ++c) {
				try {
					My[r][c] = M[r][c];
				} catch (ArrayIndexOutOfBoundsException ex) {
					My[r][c] = 0d;
				}
			}
		}

		try {
			return solveInplace(My);
			//						
			// }catch(SingularException sex){
			// return rationalSolve(My);
		} finally {
		}
	}

	public static double[] laPacksolve(double[][] M, double[] y)
			throws SingularException {
		// double[][] vresult = new double[1][y.length];
		// for(int i = 0 ; i< y.length ; ++ i ){
		// vresult[0][i]=y[1];
		// }
		double[][] vresult = new double[][] { y.clone() };
		double[][] vresultt = LA.transpose(vresult);
		int[] iPiv = new int[y.length];
		intW info = new intW(1);
		DGESV.DGESV(/* int n */M.length,
		/* int nrhs */1,
		/* double[][]a */M,
		/* int[] ipiv */iPiv,
		/* double[][] b */vresultt,
		/* intW info */info);

		if (info.val > 0) {
			throw new SingularException();
		}

		double[] result = new double[y.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = vresultt[i][0];
		}

		return result;
	}

	public static double[] laPacksolveSym(double[][] M, double[] y)
			throws SingularException {
		// double[][] vresult = new double[1][y.length];
		// for(int i = 0 ; i< y.length ; ++ i ){
		// vresult[0][i]=y[1];
		// }

		int N = M.length;
		int i = 0;
		double[][] vresult = new double[][] { y.clone() };
		double[][] vresultt = LA.transpose(vresult);
		int[] iPiv = new int[y.length];
		intW info = new intW(1);
		double[] work = new double[1];
		int lWork = -1;

		DSYSV.DSYSV(/* String uplo */"U",
		/* int n */N,
		/* int nrhs */1,
		/* double[] ap */M,
		/* int[] iPiv */iPiv,
		/* double[][] b */vresultt, work, lWork,
		/* intW info */info);

		if (info.val > 0) {
			throw new SingularException();
		}

		lWork = (int) (work[0]);
		work = new double[lWork];

		DSYSV.DSYSV(/* String uplo */"U",
		/* int n */N,
		/* int nrhs */1,
		/* double[] ap */M,
		/* int[] iPiv */iPiv,
		/* double[][] b */vresultt, work, lWork,
		/* intW info */info);

		if (info.val > 0) {
			throw new SingularException();
		}

		double[] result = new double[y.length];
		for (i = 0; i < result.length; ++i) {
			result[i] = vresultt[i][0];
		}

		return result;
	}

	public static double[] solveInplace(double[][] My) throws SingularException {
		return solveInplace(My, false);
	}

	/**
	 * A simple in-place Gausssian solver which fails if the matrix of the
	 * equations system is singular.
	 * 
	 * @param My
	 *            the matrix and the inhomogenious part of the system
	 * @return an array x of doubles such that Mx = y;
	 * @throws Exception
	 *             if the equation system doesn't have exactly one such
	 *             solution.
	 */
	public static double[] solveInplace(double[][] My, boolean safe)
			throws SingularException {

		int mRows = My.length;
		int mCols = 0;
		for (double[] mRow : My) {
			mCols = Math.max(mCols, mRow.length);
		}
		--mCols;

		if (mRows != mCols) {
			throw new SingularException();
		}

		int mIterations = Math.min(mCols, mRows);

		iteration: for (int i = 0; i < mIterations; ++i) {

			double pivot = Math.abs(My[i][i]);
			int pivotRow = i;
			for (int iPiv = i + 1; iPiv < mRows; ++iPiv) {
				double absMy = Math.abs(My[iPiv][i]);
				if (pivot < absMy || absMy == 1.0d) {
					pivot = absMy;
					pivotRow = iPiv;
					if (absMy == 1.0d)
						break;
				}
			}
			if (pivotRow != i) {
				double[] tmpRow = My[i];
				My[i] = My[pivotRow];
				My[pivotRow] = tmpRow;
			}

			pivot = My[i][i];

			if (Math.abs(pivot) <= LA.FLOAT_EPSILON) {
				if (safe) {
					My[i][i] = 0d;
					for (int c = i + 1; c <= mCols; ++c) { // "normalize" the
						// row
						My[i][c] = 0d;
					}
					for (int r = i + 1; r < mRows; ++r) { // "normalize" the
						// column
						My[r][i] = 0d;
					}
					// don't subtract.
					continue iteration;
				} else {
					throw new SingularException();
				}
			}

			My[i][i] = 1d;

			for (int c = i + 1; c <= mCols; ++c) { // normalize the row;
				My[i][c] /= pivot;
			}

			for (int iDown = i + 1; iDown < mRows; ++iDown) { // subtract this
				// row from
				// normalized
				// rows below;
				double pivotDown = My[iDown][i];
				if (pivotDown != 0d) {
					for (int cRight = i + 1; cRight <= mCols; ++cRight) {
						double cell;
						cell = My[iDown][cRight];
						cell /= pivotDown;
						cell -= My[i][cRight];
						My[iDown][cRight] = cell;
					}
				}
				My[iDown][i] = 0d;
			}
		}

		// ideal case:
		// [ 1 a b c | y1 ]
		// [ 0 1 d e | y2 ]
		// [ 0 0 1 f | y3 ]
		// [ 0 0 0 1 | y4 ]

		// other cases throw Exceptions:
		// [ 1 a b c | y1 ]
		// [ 0 1 d e | y2 ]
		// [ 0 0 1 f | y3 ]
		//
		// [ 1 a b | y1 ]
		// [ 0 1 d | y2 ]
		// [ 0 0 1 | y3 ]
		// [ 0 0 0 | y4 ]

		// [ 1 a b c | y1 ]
		// [ 0 0 d e | y2 ]
		// [ 0 0 1 f | y3 ]
		// [ 0 0 0 1 | y4 ]

		double[] result = new double[mCols];

		// [ 1 a b c | y1 ]
		// [ 0 1 d e | y2 ]
		// [ 0 0 1 f | y3 ]
		// [ 0 0 0 1 | y4 ]
		// [ 0 0 0 0 ] // result_0

		// [ 1 a b c | y1 ]
		// [ 0 0 d e | y2 ]
		// [ 0 0 1 f | y3 ]
		// [ 0 0 0 1 | y4 ]
		// [ x1 x2 x3 x4 ] // result

		for (int i = mIterations - 1; i >= 0; --i) {
			double result_i = result[i] = My[i][mCols];
			for (int iUp = i - 1; iUp >= 0; --iUp) {
				double d = result_i * My[iUp][i];
				My[iUp][mCols] -= d;
				My[iUp][i] = 0d;
			}
		}

		return result;
	}

	public static void minor(int r, int c, double[][] M, double[][] result) {
		int iOffset = 0;
		int nRows = M.length - 1;
		rows: for (int i = 0; i < nRows; ++i) {
			if (i == r) {
				iOffset = 1;
				continue rows;
			}
			double[] Mi = M[i + iOffset];
			int mCols = Mi.length - 1;
			int jOffset = 0;
			colums: for (int j = 0; j < mCols; ++j) {
				if (j == c) {
					jOffset = 1;
					continue colums;
				}
				result[i][j] = Mi[j + jOffset];
			}
		}
	}

	public static double[][] minor(int r, int c, double[][] M) {
		int iOffset = 0;
		int nRows = M.length - 1;
		double[][] result = new double[nRows][];
		rows: for (int i = 0; i < nRows; ++i) {
			if (i == r) {
				iOffset = 1;
				continue rows;
			}
			double[] Mi = M[i + iOffset];
			int mCols = Mi.length - 1;
			double[] resulti = result[i] = new double[mCols];
			int jOffset = 0;
			colums: for (int j = 0; j < mCols; ++j) {
				if (j == c) {
					jOffset = 1;
					continue colums;
				}
				resulti[j] = Mi[j + jOffset];
			}
		}
		return result;
	}

	public static double[][] identity(int n) {
		double[][] result = new double[n][n];
		for (int i = 0; i < n; ++i) {
			result[i][i] = 1d;
		}
		return result;
	}

	public static BigDecimal[][] identity(int n, Object big) {
		BigDecimal[][] result = new BigDecimal[n][n];
		for (int i = 0; i < n; ++i) {
			result[i][i] = BigDecimal.ONE;
		}
		return result;
	}

	public static double[] plus(double[] a) {
		if (a == null)
			return new double[0];
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = a[i];
		}
		return result;
	}

	public static BigDecimal[] plus(BigDecimal[] a) {
		if (a == null)
			return new BigDecimal[0];
		BigDecimal[] result = new BigDecimal[a.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = a[i];
		}
		return result;
	}

	public static double[] plus(double[] a, double[] b) {
		if (a.length > b.length) {
			return plus(b, a);
		}
		double[] result = new double[b.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = a[i] + b[i];
		}
		for (int i = a.length; i < b.length; ++i) {
			result[i] = b[i];
		}
		return result;
	}

	public static BigDecimal[] plus(BigDecimal[] a, BigDecimal[] b) {
		if (a.length > b.length) {
			return plus(b, a);
		}
		BigDecimal[] result = new BigDecimal[b.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = a[i].add(b[i]);
		}
		for (int i = a.length; i < b.length; ++i) {
			result[i] = b[i];
		}
		return result;
	}

	public static double[] minus(double[] a) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = -a[i];
		}
		return result;
	}

	public static BigDecimal[] minus(BigDecimal[] a) {
		BigDecimal[] result = new BigDecimal[a.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = a[i].negate();
		}
		return result;
	}

	public static double[][] minus(double[][] A) {
		final int n = A.length;
		double[][] result = new double[n][];
		for (int i = 0; i < n; ++i) {
			result[i] = minus(A[i]);
		}
		return result;
	}

	public static BigDecimal[][] minus(BigDecimal[][] A) {
		final int n = A.length;
		BigDecimal[][] result = new BigDecimal[n][];
		for (int i = 0; i < n; ++i) {
			result[i] = minus(A[i]);
		}
		return result;
	}

	public static double[] minus(double[] a, double[] b) {
		return plus(a, minus(b));
	}

	public static BigDecimal[] minus(BigDecimal[] a, BigDecimal[] b) {
		return plus(a, minus(b));
	}

	public static double[] mul(double d, double[] a) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = d * a[i];
		}
		return result;
	}

	public static BigDecimal[] mul(BigDecimal d, BigDecimal[] a) {
		BigDecimal[] result = new BigDecimal[a.length];
		for (int i = 0; i < a.length; ++i) {
			result[i] = d.multiply(a[i]);
		}
		return result;
	}

	public static double dot(double[] a, double[] b) {
		if (a == null)
			return 0d;
		if (b == null)
			return 0d;
		if (a.length > b.length) {
			return dot(b, a);
		}
		double result = 0d;
		for (int i = 0; i < a.length; i++) {
			result += a[i] * b[i];
		}
		return result;
	}

	/**
	 * Computes the generalized cross products of n-1 vectors in an
	 * n-dimensional vector space. The dimension of the result is solely
	 * determined by the number of the specified vectors, which are padded with
	 * zeros or truncated if they do not fit into the dimension.
	 * 
	 * Hence, cross([a,b,c,...]) gives a two-dimensoinal result [b,-a] and
	 * cross([1],[2]) gives the three-dimensional zero vector [0,0,0].
	 * 
	 * Formally, cross it is the "determinant"
	 * 
	 * | e1 e2 ... en+1 | |v11 v12 ... v1n+1 | |. . . | |vn1 vn2 ... vnn+1 |
	 * 
	 * where v1=[v11,...], ... , vn=[vn1,...] are the given vectors and e1, e2,
	 * ... the canonical unit vectors e1 = [1,0,0,...] e2=[0,1,0,...] etc.
	 * 
	 * @param vecs
	 *            an arbitrary number of n vectors
	 * @return the generalized n+1-dimensional cross product of these vectors.
	 * 
	 * @throws UnsupportedOperationException
	 *             - always. This method should be corrected.
	 */
	public static double[] cross(double[]... vecs) {

		if (true) {
			throw new UnsupportedOperationException(Db3dSimpleResourceBundle
					.getString("db3d.geom.unsuppopcross"));
		}

		int nVecs = vecs.length;
		int dim = nVecs + 1;
		double[] result = new double[dim];
		Double[][] M = new Double[nVecs][dim];
		for (int r = 0; r < nVecs; ++r) {
			for (int c = 0; c < dim; ++c) {
				try {
					M[r][c] = vecs[r][c];
				} catch (ArrayIndexOutOfBoundsException arr/* rrrghhh!!! */) {
					M[r][c] = 0d;
				}
			}
		}

		boolean negative = false;
		for (int i = 0; i < dim; ++i) {

			double posVal = 0d; // FIXME: must solve something...
			// solver.solve(0);

			if (posVal == -0d) {
				result[i] = 0d; // avoid negative zeros to ease testing.
				// Note: Double.valueOf(-0d).equals(Double.valueOf(0d)) gives
				// false.
			} else {
				result[i] = negative ? -posVal : posVal;
			}
			negative = !negative;
		}
		return result;
	}

	/**
	 * Returns true, if the specified number is multiplicative invertible. Note
	 * that <code>isUnit(d)==isUnit(1d/d)</code> always holds.
	 * 
	 * @param d
	 *            a number to test if it is invertible by multiplication.
	 * @return true iff (1d/d)*d==1d.
	 */
	public static boolean isUnit(double d) {
		if (d == 0d || Double.isInfinite(d) || Double.isNaN(d)) {
			return false;
		}

		double invD = 1d / d;
		return invD * d == 1d;

	}

	public static boolean isZero(double[] v) {
		for (double c : v) {
			if (c != 0d) {
				return false;
			}
		}
		return true;
	}

	public static BigDecimal dot(BigDecimal[] a, BigDecimal[] b) {
		if (a == null)
			return BigDecimal.ZERO;
		if (b == null)
			return BigDecimal.ZERO;
		if (a.length > b.length) {
			return dot(b, a);
		}
		BigDecimal result = BigDecimal.ZERO;
		for (int i = 0; i < a.length; i++) {
			result = result.add(a[i].multiply(b[i]));
		}
		return result;
	}

	public static double[][] gramnian(double[][] v) {
		int vlen = v.length;
		double[][] result = new double[vlen][vlen];
		for (int i = 0; i < vlen; ++i) {
			double[] vi = v[i];
			for (int j = i; j < vlen; ++j) {
				result[i][j] = result[j][i] = LA.dot(vi, v[j]);
			}
		}
		return result;
	}

	public static BigDecimal[][] gramnian(BigDecimal[][] v) {
		int vlen = v.length;
		BigDecimal[][] result = new BigDecimal[vlen][vlen];
		for (int i = 0; i < vlen; ++i) {
			BigDecimal[] vi = v[i];
			for (int j = i; j < vlen; ++j) {
				result[i][j] = result[j][i] = LA.dot(vi, v[j]);
			}
		}
		return result;
	}

	public static double[][] mul(double[][] a, double[][] b) {

		// { { b11 , b12 , b13 , b14 } ,
		// { b21 , b22 , b23 , b24 } ,
		// { b31 , b32 , b33 , b34 } }
		// { { a11 , a12 , a13 },
		// { a21 , a22 , a23 } }
		//

		int width = 0;
		for (double[] bRow : b) {
			int bRowLength = bRow.length;
			if (bRowLength > width) {
				width = bRowLength;
			}
		}
		double[][] result = new double[a.length][width];
		for (int r = 0; r < result.length; ++r) {
			for (int c = 0; c < width; ++c) {
				double resultrc = 0;
				if (a[r] == null) {
					continue;
				}
				double[] ar = a[r];
				for (int i = Math.min(ar.length, b.length); i > 0;) {
					--i;
					resultrc += ar[i] * b[i][c];
				}
				result[r][c] = resultrc;
			}
		}
		return result;
	}

	public static double[] mul(double[][] a, double[] x) {

		// { x11 ,
		// x21 ,
		// X31 }
		// { { a11 , a12 , a13 },
		// { a21 , a22 , a23 } }
		//
		double[] result = new double[a.length];
		for (int r = 0; r < result.length; ++r) {
			result[r] = dot(a[r], x);
		}
		return result;
	}

	public static double[] mul(double[] x, double[][] a) {

		// { { a11 , a12 , a13 },
		// { a21 , a22 , a23 } }
		// { x1, x2 }
		//
		double[] result = new double[0];
		for (int r = 0; r < x.length; ++r) {
			result = plus(result, mul(x[r], a[r]));
		}
		return result;
	}

	public static BigDecimal[][] mul(BigDecimal[][] a, BigDecimal[][] b) {

		// { { b11 , b12 , b13 , b14 } ,
		// { b21 , b22 , b23 , b24 } ,
		// { b31 , b32 , b33 , b34 } }
		// { { a11 , a12 , a13 },
		// { a21 , a22 , a23 } }
		//

		int width = 0;
		for (BigDecimal[] bRow : b) {
			int bRowLength = bRow.length;
			if (bRowLength > width) {
				width = bRowLength;
			}
		}
		BigDecimal[][] result = new BigDecimal[a.length][width];
		for (int r = 0; r < result.length; ++r) {
			for (int c = 0; c < width; ++c) {
				BigDecimal resultrc = BigDecimal.ZERO;
				if (a[r] == null) {
					continue;
				}
				BigDecimal[] ar = a[r];
				for (int i = Math.min(ar.length, b.length); i > 0;) {
					--i;
					resultrc = resultrc.add(ar[i].multiply(b[i][c]));
				}
				result[r][c] = resultrc;
			}
		}
		return result;
	}

	public static double[] solve(int[] piRowCols, double[][] M, double[] y)
			throws Exception {
		return solve(piRowCols, piRowCols, M, y);
	}

	public static double[] solveSym(int[] piRowCols, double[][] M, double[] y)
			throws Exception {
		return solveSym(piRowCols, piRowCols, M, y);
	}

	/**
	 * Using the LAPACK solver.
	 * 
	 * @param M
	 *            the matrix of the equations system
	 * @param y
	 *            the inhomogenious part of the system
	 * @return an array x of doubles such that Mx = y;
	 * @throws Exception
	 *             if the equation system doesn't have exactly one such
	 *             solution.
	 */
	public static double[] solve(int[] piRows, int[] piCols, double[][] M,
			double[] y) throws Exception {

		int mRows = piRows.length;
		int mCols = piCols.length;

		if (mRows != mCols) {
			throw new Exception(Db3dSimpleResourceBundle
					.getString("db3d.geom.notsqmat"));
		}

		double[][] pM = new double[mRows][mCols + 1];
		double[] py = new double[mRows];

		for (int c = 0; c < mCols; ++c) {
			try {
				py[c] = y[piCols[c]];
			} catch (ArrayIndexOutOfBoundsException ex) {
				py[c] = 0d;
			}
		}

		for (int r = 0; r < mRows; ++r) {
			for (int c = 0; c < mCols; ++c) {
				try {
					pM[r][c] = M[piRows[r]][piCols[c]];
				} catch (ArrayIndexOutOfBoundsException ex) {
					pM[r][c] = 0d;
				}
			}
		}

		return laPacksolve(pM, py);

	}

	/**
	 * Using the LAPACK solver for Symmetric packed.
	 * 
	 * @param M
	 *            the matrix of the equations system
	 * @param y
	 *            the inhomogenious part of the system
	 * @return an array x of doubles such that Mx = y;
	 * @throws Exception
	 *             if the equation system doesn't have exactly one such
	 *             solution.
	 */
	public static double[] solveSym(int[] piRows, int[] piCols, double[][] M,
			double[] y) throws Exception {

		int mRows = piRows.length;
		int mCols = piCols.length;

		if (mRows != mCols) {
			throw new Exception(Db3dSimpleResourceBundle
					.getString("db3d.geom.notsqmat"));
		}

		double[][] pM = new double[mRows][mCols + 1];
		double[] py = new double[mRows];

		for (int c = 0; c < mCols; ++c) {
			try {
				py[c] = y[piCols[c]];
			} catch (ArrayIndexOutOfBoundsException ex) {
				py[c] = 0d;
			}
		}

		for (int r = 0; r < mRows; ++r) {
			for (int c = 0; c < mCols; ++c) {
				try {
					pM[r][c] = M[piRows[r]][piCols[c]];
				} catch (ArrayIndexOutOfBoundsException ex) {
					pM[r][c] = 0d;
				}
			}
		}

		return laPacksolveSym(pM, py);

	}

	public static double[][] transpose(double[][] M) {
		int Mrows = M.length;
		int Mcols = 0;
		for (int i = 0; i < Mrows; ++i) {
			int ilen = 0;
			try {
				ilen = M[i].length;
			} catch (Exception ex) {
			}
			if (ilen > Mcols) {
				Mcols = ilen;
			}
		}
		double[][] result = new double[Mcols][Mrows];
		for (int r = 0; r < Mcols; ++r)
			for (int c = 0; c < Mrows; ++c) {
				try {
					result[r][c] = M[c][r];
				} catch (Exception ex) {
				}
			}
		return result;

	}

}
