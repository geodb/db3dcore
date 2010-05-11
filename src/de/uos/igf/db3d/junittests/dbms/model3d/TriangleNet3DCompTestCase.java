/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.api.NameNotUniqueException;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp;
import de.uos.igf.db3d.dbms.model3d.TriangleNetBuilder;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp.TriangleElt3DIterator;

/**
 * This testcase tests the (topology) methods of the
 * <code>TriangleNet3DComp</code> class.
 * 
 * @author Edgar Butwilowski
 */
public class TriangleNet3DCompTestCase extends TestCase {

	public void testElementReferences() throws NameNotUniqueException {

		ScalarOperator sop = new ScalarOperator();
		TriangleNetBuilder triNetBuilder = new TriangleNetBuilder(sop);
		TriangleElt3D tri1 = new TriangleElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), new Point3D(2.0, 3.0, 1.0),
				sop);
		TriangleElt3D tri2 = new TriangleElt3D(new Point3D(3.0, 1.0, 1.0),
				new Point3D(2.0, 3.0, 1.0), new Point3D(3.0, 3.0, 1.0),
				sop);
		TriangleElt3D tri111 = new TriangleElt3D(new Point3D(3.0, 1.0, 1.0),
				new Point3D(3.0, 3.0, 1.0), new Point3D(4.0, 1.0, 1.0),
				sop);
		triNetBuilder.addComponent(new TriangleElt3D[] { tri1, tri2, tri111 });
		TriangleNet3D triNet3D = triNetBuilder.getTriangleNet();
		TriangleNet3DComp triComp = triNet3D.getComponent(0);

		TriangleElt3D tri3 = triComp.getElement(1);
		TriangleElt3D tri4 = triComp.getElement(2);
		TriangleElt3D tri5 = triComp.getElement(3);

		assertTrue(tri3 != null);
		assertTrue(tri4 != null);
		// TODO this behaves odd:
		// assertTrue(tri5 != null);

		TriangleElt3DIterator it = triComp.getElementsIterator();
		int i = 0;
		while (it.hasNext()) {
			assertTrue(it.next() != null);
			i++;
		}
		// TODO this behaves odd:
		// assertTrue(i == 3);

	}

}
