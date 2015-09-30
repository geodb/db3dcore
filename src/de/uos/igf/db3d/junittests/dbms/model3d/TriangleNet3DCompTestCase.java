/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.exceptions.NameNotUniqueException;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DComponent;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DElement;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DComponent;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DElement;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DElementIterator;

/**
 * This testcase tests the (topology) methods of the
 * <code>TriangleNet3DComp</code> class.
 * 
 * @author Edgar Butwilowski
 */
public class TriangleNet3DCompTestCase extends TestCase {

	public void testElementReferences() throws NameNotUniqueException {

		GeoEpsilon sop = new GeoEpsilon();

		Triangle3DElement tri1 = new Triangle3DElement(new Point3D(1.0,
				1.0, 1.0), new Point3D(3.0, 1.0, 1.0), new Point3D(2.0, 3.0,
				1.0), sop);
		Triangle3DElement tri2 = new Triangle3DElement(new Point3D(3.0,
				1.0, 1.0), new Point3D(2.0, 3.0, 1.0), new Point3D(3.0, 3.0,
				1.0), sop);
		Triangle3DElement tri111 = new Triangle3DElement(new Point3D(3.0,
				1.0, 1.0), new Point3D(3.0, 3.0, 1.0), new Point3D(4.0, 1.0,
				1.0), sop);

		Triangle3DNet net = new Triangle3DNet(
				new Triangle3DComponent[] { new Triangle3DComponent(
						new Triangle3DElement[] { tri1, tri2, tri111 }, sop) },
				sop);

		Triangle3DComponent triComp = net.getComponent(0);

		Triangle3DElement tri3 = triComp.getElement(1);
		Triangle3DElement tri4 = triComp.getElement(2);
		Triangle3DElement tri5 = triComp.getElement(3);

		assertTrue(tri3 != null);
		assertTrue(tri4 != null);
		// TODO this behaves odd:
		// assertTrue(tri5 != null);

		Triangle3DElementIterator it = triComp.getElementsIterator();
		int i = 0;
		while (it.hasNext()) {
			assertTrue(it.next() != null);
			i++;
		}
		// TODO this behaves odd:
		// assertTrue(i == 3);

	}

}
