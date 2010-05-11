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
 * This testcase tests the clipping operations between (geometry) methods of the
 * <tt>Tetrahedron3D</tt> and <tt>Triangle3D</tt>.
 * 
 * @author dgolovko
 */
public class TetrahedronTriangle3DTestCase extends TestCase {

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
		// TODO: returns a segment
		// INTERSECTION IN ONE POINT, WHICH IS A VERTEX OF THE TETRAHEDRON:
		ScalarOperator sop = new ScalarOperator();
		Tetrahedron3D tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0),
				new Point3D(1.0, 2.0, 0.0), new Point3D(2.0, 0.0, 0.0),
				new Point3D(1.0, 1.0, 2.0), sop);
		Triangle3D tri = new Triangle3D(new Point3D(2.0, 0.0, 0.0),
				new Point3D(4.0, 0.0, 0.0), new Point3D(4.0, 2.0, 0.0), sop);

		SimpleGeoObj result = tetr.intersection(tri, sop);

		// return type in this case must be a point:
		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.getX() == 2.0);
			assertTrue(resultPoint.getY() == 0.0);
			assertTrue(resultPoint.getZ() == 0.0);
		}

		// INTERSECTION IN ONE POINT, WHICH IS NOT A VERTEX OF THE TETRAHEDRON:
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(2.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(2.0, 0.0,
				2.0), sop);
		tri = new Triangle3D(new Point3D(2.0, 1.0, 1.0), new Point3D(4.0, 0.0,
				1.0), new Point3D(4.0, 2.0, 1.0), sop);

		result = tetr.intersection(tri, sop);

		// return type in this case must be a point:
		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.getX() == 2.0);
			assertTrue(resultPoint.getY() == 1.0);
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
		// TODO: returns a wireframe
		// INTERSECTION IN A SEGMENT:
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(4.0,
				4.0, 0.0), new Point3D(4.0, 0.0, 0.0), new Point3D(4.0, 0.0,
				4.0), sop);
		tri = new Triangle3D(new Point3D(4.0, 1.0, 1.0), new Point3D(4.0, 1.0,
				2.0), new Point3D(6.0, 1.0, 2.0), sop);

		result = tetr.intersection(tri, sop);
	

		// return type in this case must be a segment:
		assertTrue(result instanceof Segment3D);

		
		/**
		 * <tt>
		 *    ++
		 *   // \\
		 *  //   \\
		 * ++-----++
                 * </tt>
		 */
		// THE TRIANGLE IS ONE OF THE SIDES OF THE TETRAHEDRON:
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);
		tri = new Triangle3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0, 2.0,
				0.0), new Point3D(2.0, 0.0, 0.0), sop);

		result = tetr.intersection(tri, sop);

		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

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
		// THE TRIANGLE IS COMPLETELY INSIDE THE TETRAHEDRON:
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(2.0,
				4.0, 0.0), new Point3D(4.0, 0.0, 0.0), new Point3D(2.0, 2.0,
				4.0), sop);
		tri = new Triangle3D(new Point3D(1.0, 1.0, 1.0), new Point3D(3.0, 1.0,
				1.0), new Point3D(2.0, 3.0, 1.0), sop);

		result = tetr.intersection(tri, sop);
		
		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

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
		// PARTIAL INTERSECTION, ONE VERTEX OF THE TRIANGLE LIES INSIDE THE
		// TETRAHEDRON:
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(2.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(2.0, 0.0,
				2.0), sop);
		tri = new Triangle3D(new Point3D(1.0, 1.0, 1.0), new Point3D(4.0, 0.0,
				1.0), new Point3D(4.0, 2.0, 1.0), sop);

		result = tetr.intersection(tri, sop);
		
		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

		// PARTIAL INTERSECTION, NO VERTICES LIE INSIDE THE OTHER OBJECT:
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(1.0,
				2.0, 0.0), new Point3D(2.0, 0.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);
		tri = new Triangle3D(new Point3D(0.0, 0.0, 1.0), new Point3D(8.0, 0.0,
				1.0), new Point3D(8.0, 8.0, 1.0), sop);

		result = tetr.intersection(tri, sop);
		
		// return type in this case must be a triangle:
		assertTrue(result instanceof Triangle3D);

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
		// PARTIAL INTERSECTION, THE TRIANGLE LIES IN THE SAME PLANE AS ONE OF
		// THE SIDES OF THE TETRAHEDRON,
		// THE RESULT SHOULD BE A WIREFRAME:
		tetr = new Tetrahedron3D(new Point3D(1.0, 0.0, 0.0), new Point3D(0.0,
				2.0, 0.0), new Point3D(2.0, 2.0, 0.0), new Point3D(1.0, 1.5,
				2.0), sop);
		tri = new Triangle3D(new Point3D(0.0, 1.0, 0.0), new Point3D(1.0, 3.0,
				0.0), new Point3D(2.0, 1.0, 0.0), sop);

		result = tetr.intersection(tri, sop);
		
		// return type in this case must be a wireframe:
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
		tetr = new Tetrahedron3D(new Point3D(0.0, 0.0, 0.0), new Point3D(2.0,
				0.0, 0.0), new Point3D(1.0, 2.0, 0.0), new Point3D(1.0, 1.0,
				2.0), sop);
		tri = new Triangle3D(new Point3D(2.0, 1.0, 1.0), new Point3D(4.0, 0.0,
				1.0), new Point3D(4.0, 2.0, 1.0), sop);

		result = tetr.intersection(tri, sop);

		// return type in this case must be null:
		assertTrue(result == null);

	}

	public void tearDown() throws Exception {
	}

}
