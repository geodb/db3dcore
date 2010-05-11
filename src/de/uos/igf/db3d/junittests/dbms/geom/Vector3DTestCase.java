/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geom;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.Vector3D;

/**
 * This testcase tests the (vector analysis) methods of the
 * <code>Vector3D</code> class.
 * 
 * @author Edgar Butwilowski
 * 
 */
public class Vector3DTestCase extends TestCase {

	public void testCosine1() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, -45° angle:

		/**
		 * <tt>
		 *              B
		 *             +
		 *             |
		 *             |
		 *             |
		 *             +
		 *              A   -45°
		 *                  A
		 *                  +
		 *                   \
		 *                    \
		 *                     +
		 *                      B
		 * 
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(10.0, 10.0, 0.0),
				new Point3D(10.0, 15.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(5.0, 5.0, 0.0),
				new Point3D(7.0, 3.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be ~ -0.71:
		assertTrue(cosine == -0.7071067811865476);

	}

	public void testCosine2() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 90° angle:

		/**
		 * <tt>
		 *    B
		 *    +
		 *    |  90°
		 *    | A+--------+B
		 *    |
		 *    +
		 *    A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 5.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 3.0, 0.0),
				new Point3D(4.0, 3.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be 0:
		assertTrue(cosine == 0.0);

	}

	public void testCosine3() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 90° angle:

		/**
		 * <tt>
		 *               B
		 *               +
		 *          90°  | 
		 * A+--------+B  |
		 *               |
		 *               +
		 *               A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(5.0, 1.0, 0.0),
				new Point3D(5.0, 5.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(4.0, 3.0, 0.0),
				new Point3D(1.0, 3.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be 0:
		assertTrue(cosine == 0.0);

	}

	public void testCosine4() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 90° angle:

		/**
		 * <tt>
		 *               B
		 *               +
		 *          90°  | 
		 * B+--------+A  |
		 *               |
		 *               +
		 *               A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(5.0, 1.0, 0.0),
				new Point3D(5.0, 5.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(1.0, 3.0, 0.0),
				new Point3D(4.0, 3.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be 0:
		assertTrue(cosine == 0.0);

	}

	public void testCosine5() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 29° angle:

		/**
		 * <tt>
		 *   B      B
		 *   +     +
		 *   | 29°/
		 *   |   /
		 *   |  +
		 *   +  A
		 *   A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 3.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 2.0, 0.0),
				new Point3D(5.0, 3.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be ~ 0.32:
		assertTrue(cosine == 0.316227766016838);

	}

	public void testCosine6() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 45° angle:

		/**
		 * <tt>
		 *              B
		 *             +
		 *             |
		 *             |
		 *             |
		 *             +
		 *              A
		 *          B
		 *         +
		 *     45°/
		 *       /
		 *      +
		 *      A
		 * 
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(10.0, 10.0, 0.0),
				new Point3D(10.0, 15.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 2.0, 0.0),
				new Point3D(5.0, 5.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be ~ 0.71:
		assertTrue(cosine == 0.7071067811865476);

	}

	public void testCosine7() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 45° angle:

		/**
		 * <tt>
		 *   B      B
		 *   +     +
		 *   | 45°/
		 *   |   /
		 *   |  +
		 *   +  A
		 *   A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 3.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 2.0, 0.0),
				new Point3D(5.0, 5.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be ~ 0.71:
		assertTrue(cosine == 0.7071067811865476);

	}

	public void testCosine8() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 45° angle:

		/**
		 * <tt>
		 *  B     B
		 *  +     +
		 *   \ 45°|
		 *    \   |
		 *     +  |
		 *     A  +
		 *        A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 3.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 2.0, 0.0),
				new Point3D(5.0, 5.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be ~ 0.71:
		assertTrue(cosine == 0.7071067811865476);

	}

	public void testCosine9() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 0° angle:

		/**
		 * <tt>
		 *     B
		 *     +
		 *     |
		 *     |
		 * A,B +  0°
		 *     |
		 *     |
		 *     +  
		 *     A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 10.0, 0.0),
				new Point3D(1.0, 15.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 10.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be 1:
		assertTrue(cosine == 1.0);

	}

	public void testCosine10() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 0° angle:

		/**
		 * <tt>
		 *    B    B
		 *    +    +
		 *    |    |
		 *    | 0° |
		 *    |    |
		 *    +    +
		 *    A    A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 5.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 1.0, 0.0),
				new Point3D(2.0, 5.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be 1:
		assertTrue(cosine == 1.0);

	}

	public void testCosine11() {

		ScalarOperator sop = new ScalarOperator();

		// two segments, 77° angle:

		/**
		 * <tt>
		 *   B      B
		 *   +     +
		 *   | 77°/
		 *   |   /
		 *   |  +
		 *   +  A
		 *   A
		 * </tt>
		 */
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(1.0, 3.0, 0.0), sop);
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 2.0, 0.0),
				new Point3D(5.0, 7.0, 0.0), sop);

		// get line of segment 1:
		Line3D line1 = segment1.getLine(sop);
		// get vector of line 1:
		Vector3D vector1 = line1.getDVector();
		// get line of segment 2:
		Line3D line2 = segment2.getLine(sop);
		// get vector of line 2:
		Vector3D vector2 = line2.getDVector();
		// calculate co-sinus between segment 1 and segment 2:
		double cosine = vector1.cosinus(vector2, sop);

		// co-sinus has to be ~ 0.86:
		assertTrue(cosine == 0.8574929257125442);

	}

}
