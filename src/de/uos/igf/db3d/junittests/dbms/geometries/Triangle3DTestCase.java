/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geometries;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D.GEOMETRYTYPES;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * This testcase tests the (geometry) methods of the <code>Triangle3D</code>
 * class.
 * 
 * @author Edgar Butwilowski
 */
public class Triangle3DTestCase extends TestCase {

	public final static Point3D P1 = new Point3D(1.0,1.0,1.0);
	public final static Point3D P2 = new Point3D(2.0,2.0,2.0);
	public final static Point3D P3 = new Point3D(1.0,1.0,2.0);
	public final static GeoEpsilon sop = new GeoEpsilon();
	
	
	public final static Triangle3D T1 = new Triangle3D(P1, P2, P3, sop);
	
	public void setUp() throws Exception {

	}

	public void testIntersectionTriangleSegment() {

		/**
		 * <tt>
		 *  +-------+
		 *   \     /
		 * +--\---/--+
		 *     \ /
		 *      +
		 * </tt>
		 */
		GeoEpsilon sop = new GeoEpsilon();
		Triangle3D triangle = new Triangle3D(new Point3D(2.0, 3.0, 0.0),
				new Point3D(1.0, 4.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);
		Segment3D segment = new Segment3D(new Point3D(0.0, 3.5, 0.0),
				new Point3D(5.0, 3.5, 0.0), sop);

		Geometry3D result = triangle.intersection(segment, sop);

		// return type in this case must be a segment:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.POINT);

		if (result.getGeometryType() == GEOMETRYTYPES.SEGMENT) {
			Segment3D cutSegment = (Segment3D) result;
			// cutting points must be [2.5,3.5] and [1.5,3.5]:
			assertTrue(cutSegment.getPoint(0).isEqual(
					new Point3D(2.5, 3.5, 0.0), sop));
			assertTrue(cutSegment.getPoint(1).isEqual(
					new Point3D(1.5, 3.5, 0.0), sop));
		}

		/**
		 * <tt>
		 *  +-------+
		 *   \     /
		 *    \   /
		 *     \ /
		 * +----+----+
		 * </tt>
		 */
		triangle = new Triangle3D(new Point3D(2.0, 3.0, 0.0), new Point3D(1.0,
				4.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);
		segment = new Segment3D(new Point3D(1.0, 3.0, 0.0), new Point3D(3.0,
				3.0, 0.0), sop);

		result = triangle.intersection(segment, sop);

		// return type in this case must be a point:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.POINT);

		if (result.getGeometryType() == GEOMETRYTYPES.POINT) {
			Point3D cutPoint = (Point3D) result;
			// cutting points must be [1.5,3.5] and [2.5,3.5]:
			assertTrue(cutPoint.isEqual(new Point3D(2.0, 3.0, 0.0), sop));
		}

		/**
		 * <tt>
		 *             +
		 *            /
		 *           /
		 *  +-------+
		 *   \     /
		 *    \   /
		 *     \ /
		 *      +
		 *     /
		 *    /
		 *   +
		 * </tt>
		 */
		triangle = new Triangle3D(new Point3D(2.0, 2.0, 0.0), new Point3D(1.0,
				4.0, 0.0), new Point3D(4.0, 4.0, 0.0), sop);
		segment = new Segment3D(new Point3D(1.0, 1.0, 0.0), new Point3D(5.0,
				5.0, 0.0), sop);

		result = triangle.intersection(segment, sop);

		// return type in this case must be a segment:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.SEGMENT);

		// TODO test points...

	}

	public void testIntersectionTriangleTriangle() {

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
		GeoEpsilon sop = new GeoEpsilon();
		Triangle3D triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0),
				new Point3D(3.0, 1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		Triangle3D triangleB = new Triangle3D(new Point3D(2.0, 3.0, 0.0),
				new Point3D(1.0, 4.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);

		Geometry3D result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a point:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.POINT);
		if (result.getGeometryType() == GEOMETRYTYPES.POINT) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.getX() == 2.0);
			assertTrue(resultPoint.getY() == 3.0);
			assertTrue(resultPoint.getZ() == 0.0);
		}

		/**
		 * <tt>
		 *    +-----+
		 *   / \   /
		 *  /   \ /
		 * +-----+
		 * </tt>
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(2.0, 3.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(4.0, 2.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a segment:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.SEGMENT);

		/**
		 * <tt>
		 *    ++
		 *   // \\
		 *  //   \\
		 * ++-----++
		 * </tt>
		 * 
		 * the triangles are congruent
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a triangle:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.TRIANGLE);

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
		 * 
		 * one triangle completely lies in the other
		 */
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(5.0,
				1.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(2.0, 2.0, 0.0), new Point3D(4.0,
				2.0, 0.0), new Point3D(3.0, 3.0, 0.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a triangle:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.TRIANGLE);

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
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(1.0, 2.0, 0.0), new Point3D(3.0,
				2.0, 0.0), new Point3D(2.0, 4.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return type in this case must be a triangle:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.TRIANGLE);

		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(2.0, 2.0, 0.0), new Point3D(4.0,
				2.0, 0.0), new Point3D(3.0, 4.0, 0.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a triangle:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.TRIANGLE);

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
		triangleA = new Triangle3D(new Point3D(3.0, 3.0, 0.0), new Point3D(5.0,
				6.0, 0.0), new Point3D(7.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(5.0, 2.0, 0.0), new Point3D(7.0,
				5.0, 0.0), new Point3D(3.0, 5.0, 0.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.WIREFRAME);
		
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
		triangleA = new Triangle3D(new Point3D(0.0, 0.0, 2.0), new Point3D(2.0,
				2.0, 2.0), new Point3D(4.0, 0.0, 2.0), sop);
		triangleB = new Triangle3D(new Point3D(0.0, 2.0, 2.0), new Point3D(4.0,
				2.0, 2.0), new Point3D(2.0, 0.0, 2.0), sop);

		result = triangleA.intersection(triangleB, sop);

		// return type in this case must be a wireframe:
		assertTrue(result.getGeometryType() == GEOMETRYTYPES.WIREFRAME);

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
		triangleA = new Triangle3D(new Point3D(1.0, 1.0, 0.0), new Point3D(3.0,
				1.0, 0.0), new Point3D(2.0, 3.0, 0.0), sop);
		triangleB = new Triangle3D(new Point3D(3.0, 2.0, 0.0), new Point3D(5.0,
				2.0, 0.0), new Point3D(4.0, 4.0, 0.0), sop);

		result = triangleB.intersection(triangleA, sop);

		// return value in this case must be a null pointer:
		assertTrue(result == null);

	}

	public void tearDown() throws Exception {
	}

}
