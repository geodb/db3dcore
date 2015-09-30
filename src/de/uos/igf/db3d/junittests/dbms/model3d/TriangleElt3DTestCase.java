/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DElement;

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

	private Triangle3DNet net;
	private Triangle3DElement triangleA;
	private Triangle3DElement triangleB;
	private Triangle3DElement triangleC;
	private Triangle3DElement triangleD;

	public void setUp() throws Exception {

		GeoEpsilon sop = new GeoEpsilon();
		triangleA = new Triangle3DElement(P1, P2, P3, sop);
		triangleB = new Triangle3DElement(P1, P3, P4, sop);
		triangleC = new Triangle3DElement(P4, P3, P5, sop);
		triangleD = new Triangle3DElement(P1, P4, P6, sop);

	}

	public void tearDown() throws Exception {
	}

}
