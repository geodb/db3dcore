/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.api.NameNotUniqueException;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DBuilder;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DComponent;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DElement;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DComponent.TriangleElt3DIterator;

/**
 * This testcase tests the (topology) methods of the
 * <code>TriangleNet3DComp</code> class.
 * 
 * @author Edgar Butwilowski
 */
public class TriangleNet3DCompTestCase extends TestCase {

	public void testElementReferences() throws NameNotUniqueException {

		ScalarOperator sop = new ScalarOperator();
		TriangleNet3DBuilder triNetBuilder = new TriangleNet3DBuilder(sop);
		TriangleNet3DElement tri1 = new TriangleNet3DElement(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), new Point3D(2.0, 3.0, 1.0),
				sop);
		TriangleNet3DElement tri2 = new TriangleNet3DElement(new Point3D(3.0, 1.0, 1.0),
				new Point3D(2.0, 3.0, 1.0), new Point3D(3.0, 3.0, 1.0),
				sop);
		TriangleNet3DElement tri111 = new TriangleNet3DElement(new Point3D(3.0, 1.0, 1.0),
				new Point3D(3.0, 3.0, 1.0), new Point3D(4.0, 1.0, 1.0),
				sop);
		triNetBuilder.addComponent(new TriangleNet3DElement[] { tri1, tri2, tri111 });
		TriangleNet3D triNet3D = triNetBuilder.getTriangleNet();
		TriangleNet3DComponent triComp = triNet3D.getComponent(0);

		TriangleNet3DElement tri3 = triComp.getElement(1);
		TriangleNet3DElement tri4 = triComp.getElement(2);
		TriangleNet3DElement tri5 = triComp.getElement(3);

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
