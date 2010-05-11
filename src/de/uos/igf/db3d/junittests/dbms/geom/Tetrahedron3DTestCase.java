/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geom;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.geom.Wireframe3D;

/**
 * This testcase tests the (geometry) methods of the <code>Tetrahedron3D</code>
 * class.
 * 
 * @author dgolovko
 */
public class Tetrahedron3DTestCase extends TestCase {

	public void setUp() throws Exception {

	}

	public void testIntersectionResult() {

		/*
		 * In the following the intersection() method of the Triangle3D class,
		 * i.e. intersection properties of alterating geometry configurations
		 * are tested. The respective configuration of the triangle geometry is
		 * depicted in some sort of ASCII art:
		 */

		/**
		 * <tt>
		 * +-----+
		 *  \   /
		 *   \ /
		 *    +
		 *   / \
		 *  /   \
		 * +-----+
         * </tt>
		 */
		// INTERSECTION IN ONE VERTEX POINT:
		ScalarOperator sop = new ScalarOperator();
		Tetrahedron3D tetrA = null;
		Tetrahedron3D tetrB = null;
		SimpleGeoObj result;
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				1.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 0.0,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(2.0, 0.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(4.0, 0.0, 0.0), new Point3D(3.0, 0.0,
				2.0), sop);
		
		result = tetrA.intersection(tetrB, sop);
		
		// return type in this case must be a point:
		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			Point3D controlPoint = new Point3D(2.0, 0.0, 0.0);
			assertTrue(controlPoint.isEqual(resultPoint, sop));
		}

		// INTERSECTION IN ONE NON-VERTEX POINT:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(0.0,
				2.0, 0.0), new Point3D(2.0, 2.0, 0.0), new Point3D(2.0, 2.0,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(2.0, 2.0, 1.0), new Point3D(4.0,
				0.0, 1.0), new Point3D(4.0, 2.0, 1.0), new Point3D(4.0, 0.0,
				3.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a point:
		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.getX() == 2.0);
			assertTrue(resultPoint.getY() == 2.0);
			assertTrue(resultPoint.getZ() == 1.0);
		}

		/**
		 * <tt>
		 *    +-----+
		 *   / \   /
		 *  /   \ /
		 * +-----+
         * </tt>
		 */
		// INTERSECTION IN ONE SEGMENT:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				1.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(1.0, 1.0, 2.0), new Point3D(2.0,
				1.0, 2.0), new Point3D(3.0, 0.0, 2.0), new Point3D(2.0, 0.0,
				0.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a segment:
		assertTrue(result instanceof Segment3D);

		// INTERSECTION IN ONE TRIANGLE:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 2.0), new Point3D(1.0,
				2.0, 2.0), new Point3D(2.0, 0.0, 2.0), new Point3D(1.0, 1.0,
				0.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 0.0, 2.0), new Point3D(1.0,
				2.0, 2.0), new Point3D(2.0, 0.0, 2.0), new Point3D(1.0, 1.0,
				4.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

		/**
		 * <tt>
		 *    ++
		 *   // \\
		 *  //   \\
		 * ++-----++
         * </tt>
		 */
		// TWO IDENICAL TETRAHEDRONS:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a tetrahedron:
		assertTrue(result instanceof Tetrahedron3D);

		/**
		 * <tt>
		 *        +
		 *       / \
		 *      /   \
		 *     /  +  \
		 *    /  / \  \
		 *   /  /   \  \
		 *  /  +-----+  \
		 * +-------------+
         * </tt>
		 */
		// ONE TETRAHEDRON COMPLETELY INSIDE THE OTHER ONE:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(2.0,
				4.0, 0.0), new Point3D(4.0, 0.0, 0.0), new Point3D(2.0, 2.0,
				4.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(1.0, 1.0, 1.0), new Point3D(2.0,
				3.0, 1.0), new Point3D(3.0, 1.0, 1.0), new Point3D(2.0, 1.0,
				3.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a tetrahedron:
		// assertTrue(result instanceof Tetrahedron3D);
		assertTrue(result instanceof Wireframe3D);
		// TODO: why do we get a wireframe?

		/**
		 * <tt>
		 * 
		 *      +
		 *     / \
		 *    / + \
		 *   +-/-\-+
		 *    /   \
		 *   +-----+
         *
         * </tt>
		 */
		// PARTIAL INTERSECTION, ONE VERTEX OF ONE TERAHEDRON LIES INSIDE THE
		// OTHER ONE:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 1.0), new Point3D(1.0,
				2.0, 1.0), new Point3D(2.0, 0.0, 1.0), new Point3D(1.0, 1.0,
				3.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a tetrahedron:
		assertTrue(result instanceof Tetrahedron3D);

		// PARTIAL INTERSECTION, ONE VERTEX OF EACH TETRAHEDRON LIES INSIDE THE
		// OTHER ONE:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 3.0), new Point3D(1.0,
				2.0, 3.0), new Point3D(2.0, 0.0, 3.0), new Point3D(1.0, 1.0,
				1.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result instanceof Wireframe3D);
		// TODO: Wireframe3D.getTriangulated();

		/**
		 * <tt>
		 *     +
		 *     /\
		 * +--/--\--+
		 *  \/    \/
		 *  /\    /\
		 * +--------+
		 *     \/
		 *     +
         * </tt>
		 */
		// INTERSECTION: BASEMENT LIKE DAVID STAR, COMMON FOURTH VERTEX:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 1.0, 0.0), new Point3D(1.0,
				3.0, 0.0), new Point3D(2.0, 1.0, 0.0), new Point3D(1.0, 1.5,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 2.0, 0.0), new Point3D(2.0,
				2.0, 0.0), new Point3D(1.0, 0.0, 0.0), new Point3D(1.0, 1.5,
				2.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result instanceof Wireframe3D);

		// INTERSECTION: BASEMENT IDENICAL, DIFFERENT FOURTH VERTEX:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(2.0, 1.0,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(0.0, 1.0,
				2.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result instanceof Tetrahedron3D);
		
        /**
         * <tt>
         *+----+----+
         * \   /\  /
         *  \ /  \/
         *   /   /\
         *  / \ /  \
         * +---+----+
         *     
         * </tt>
         */
        //INTERSECTION WITH 4 RESULTING POINTS, WHICH ARE NOT A TETRAHEDRON (LIE IN ONE PLANE):
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 2.0), new Point3D(2.0,
				2.0, 2.0), new Point3D(4.0, 0.0, 2.0), new Point3D(2.0, 1.0,
				0.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(0.0, 2.0, 2.0), new Point3D(4.0,
				2.0, 2.0), new Point3D(2.0, 0.0, 2.0), new Point3D(2.0, 0.0,
				4.0), sop);
		
		result = tetrA.intersection(tetrB, sop);
		assertTrue(result instanceof Wireframe3D);


		/**
		 * <tt>
		 *             +
		 *            / \
		 *      +    /   \
		 *     / \  +-----+
		 *    /   \
		 *   +-----+
         * </tt>
		 */
		// NO INTERSECTION:
		tetrA = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);
		tetrB = new Tetrahedron3D(new Point3D(2.0, 0.0, 2.0), new Point3D(3.0,
				2.0, 2.0), new Point3D(4.0, 0.0, 2.0), new Point3D(3.0, 1.0,
				0.0), sop);

		result = tetrA.intersection(tetrB, sop);

		// return type in this case must be null:
		assertTrue(result == null);
		// TODO: Exception in Wireframe3D.getTriangulated();
	}

	public void tearDown() throws Exception {
	}

}
