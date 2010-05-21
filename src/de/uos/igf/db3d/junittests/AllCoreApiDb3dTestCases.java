/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.uos.igf.db3d.junittests.dbms.geom.Point3DTestCase;
import de.uos.igf.db3d.junittests.dbms.geom.SegmentTestCase;
import de.uos.igf.db3d.junittests.dbms.geom.Tetrahedron3DTestCase;
import de.uos.igf.db3d.junittests.dbms.geom.TetrahedronTriangle3DTestCase;
import de.uos.igf.db3d.junittests.dbms.geom.Triangle3DTestCase;
import de.uos.igf.db3d.junittests.dbms.geom.Vector3DTestCase;
import de.uos.igf.db3d.junittests.dbms.geom.Wireframe3DTestCase;
import de.uos.igf.db3d.junittests.dbms.model3d.SegmentElt3DTestCase;
import de.uos.igf.db3d.junittests.dbms.model3d.SegmentNet3DCompTestCase;
import de.uos.igf.db3d.junittests.dbms.model3d.TriangleElt3DTestCase;
import de.uos.igf.db3d.junittests.dbms.model3d.TriangleNet3DCompTestCase;

/**
 * This is a test suite of <tt>ALL</tt> DB3D kernel related testcases. This
 * suite should be used if a <tt>complete</tt> test of all the functionalities
 * of the DB3D kernel has to be performed. Typically this test suite should be
 * run after substantial changes to the DB3D kernel have been made and before
 * the new code is commited to the repository.
 * 
 */
public class AllCoreApiDb3dTestCases {

	public static Test suite() {

		// do not print logging output to the console, i.e. remove all console
		// handlers from logger:
		Logger logger = Logger.getLogger(AllCoreApiDb3dTestCases.class.getCanonicalName());
		Handler[] handlersOfDb3d = logger.getHandlers();
		for (Handler handlerOfDb3d : handlersOfDb3d) {
			if (handlerOfDb3d instanceof ConsoleHandler)
				logger.removeHandler(handlerOfDb3d);
		}

		// initialize the suite:
		TestSuite suite = new TestSuite(
				"Test suite of ALL DB3D kernel related testcases.");

		/*
		 * Please add new testcases assorted in such manner that the more
		 * basic/kernel related testcases come first whilst the more high level
		 * operation come later.
		 */

		// $JUnit-BEGIN$
		suite.addTestSuite(Point3DTestCase.class);
		suite.addTestSuite(SegmentTestCase.class);
		suite.addTestSuite(Tetrahedron3DTestCase.class);
		suite.addTestSuite(TetrahedronTriangle3DTestCase.class);
		suite.addTestSuite(Triangle3DTestCase.class);
		suite.addTestSuite(Vector3DTestCase.class);
		suite.addTestSuite(Wireframe3DTestCase.class);
		suite.addTestSuite(SegmentElt3DTestCase.class);
		suite.addTestSuite(SegmentNet3DCompTestCase.class);
		// TODO suite.addTestSuite(TriangleElt3DTestCase.class);
		suite.addTestSuite(TriangleNet3DCompTestCase.class);
		// suite.addTestSuite(TransientDBMSTestCase.class);
		// suite.addTestSuite(PointNet4DComponentTestCase.class);
		// suite.addTestSuite(DrillingOperationTestCase.class);

		// $JUnit-END$

		return suite;
	}
}
