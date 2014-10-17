/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DElement;

/**
 * This testcase tests the (topology) methods of the <code>TriangleElt3D</code>
 * class.
 * 
 * @author Edgar Butwilowski
 */
public class TriangleElt3DTestCase extends TestCase {

	private final static Point3D P1 = new Point3D(2.0, 2.0, 0.0);
	private final static Point3D P2 = new Point3D(1.0, 3.0, 0.0);
	private final static Point3D P3 = new Point3D(3.0, 3.0, 0.0);
	private final static Point3D P4 = new Point3D(4.0, 2.0, 0.0);
	private final static Point3D P5 = new Point3D(5.0, 3.0, 0.0);
	private final static Point3D P6 = new Point3D(3.0, 1.0, 0.0);

	private TriangleNet3D net;
	private TriangleNet3DElement triangleA;
	private TriangleNet3DElement triangleB;
	private TriangleNet3DElement triangleC;
	private TriangleNet3DElement triangleD;

	public void setUp() throws Exception {

		ScalarOperator sop = new ScalarOperator();
		triangleA = new TriangleNet3DElement(P1, P2, P3, sop);
		triangleB = new TriangleNet3DElement(P1, P3, P4, sop);
		triangleC = new TriangleNet3DElement(P4, P3, P5, sop);
		triangleD = new TriangleNet3DElement(P1, P4, P6, sop);

	}

	public void tearDown() throws Exception {
	}

}
