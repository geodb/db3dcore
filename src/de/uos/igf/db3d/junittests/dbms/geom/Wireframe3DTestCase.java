/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geom;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.geom.Wireframe3D;

public class Wireframe3DTestCase extends TestCase {

	public void testGetTriangulated() {

		ScalarOperator sop = new ScalarOperator();

		/**
		 * <tt>
		 *      3        4
		 *      +-------+
		 *     / \     /
		 *    /   \   /
		 *   /     \ /
		 *  +-------+
		 *  1       2
		 * </tt>
		 */
		Wireframe3D wf = new Wireframe3D(sop);
		wf.add(new Point3D(1.0, 1.0, 1.0));
		wf.add(new Point3D(3.0, 1.0, 1.0));
		wf.add(new Point3D(2.0, 2.0, 1.0));
		wf.add(new Point3D(3.0, 2.0, 1.0));

		Triangle3D[] tris = wf.getTriangulated();

		assertTrue(tris.length == 2);

		/**
		 * <tt>
		 *           4
		 *           +
		 *         // |
		 *        / / |
		 *       /  / |
		 *      /   / |
		 *     /   +  |
		 *    /  / 3\ |
		 *   / /     \|
		 *  +---------+
		 *  1         2
		 * </tt>
		 */
		wf = new Wireframe3D(sop);
		wf.add(new Point3D(1.0, 1.0, 1.0));
		wf.add(new Point3D(3.0, 1.0, 1.0));
		wf.add(new Point3D(2.0, 2.0, 1.0));
		wf.add(new Point3D(2.5, 4.0, 1.0));

		tris = wf.getTriangulated();

		assertTrue(tris.length == 3);

		/**
		 * <tt>
		 *         3
		 *         +
		 *       / | \
		 *     /   |   \
		 *   /     |     \
		 *  +------+------+
		 *  1      2      4
		 * </tt>
		 */
		wf = new Wireframe3D(sop);
		wf.add(new Point3D(1.0, 2.0, 1.0));
		wf.add(new Point3D(2.0, 1.0, 1.0));
		wf.add(new Point3D(3.0, 1.0, 1.0));
		wf.add(new Point3D(4.0, 1.0, 1.0));

		tris = wf.getTriangulated();

		assertTrue(tris.length == 2);

	}

}
