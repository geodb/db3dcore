/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geometries;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.exceptions.GeometryException;
import de.uos.igf.db3d.dbms.exceptions.UpdateException;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D;
import de.uos.igf.db3d.dbms.spatials.api.Spatial3D;
import de.uos.igf.db3d.dbms.spatials.api.Geometry3D.GEOMETRYTYPES;
import de.uos.igf.db3d.dbms.spatials.geometries3d.*;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DComponent;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DElement;

/**
 * This testcase tests the (geometry) methods of the <code>Segment3D</code>
 * class.
 * 
 * @author Edgar Butwilowski
 * @author dgolovko
 * 
 */
public class SegmentTestCase extends TestCase {

	public void testEquals() {
		Segment3DElement seg1 = new Segment3DElement(new Point3D(1.0,
				1.0, 1.0), new Point3D(3.0, 1.0, 1.0), new GeoEpsilon());
		Segment3DElement seg2 = new Segment3DElement(new Point3D(1.0,
				1.0, 1.0), new Point3D(3.0, 1.0, 1.0), new GeoEpsilon());
		assertTrue(seg1.equals(seg2));
		Segment3DElement seg3 = new Segment3DElement(new Point3D(3.0,
				1.0, 1.0), new Point3D(1.0, 1.0, 1.0), new GeoEpsilon());
		assertTrue(seg1.equals(seg3));
	}

	public void testIntersection() {

		/*
		 * This method tests the intersection algorithms of a segment.
		 */

		GeoEpsilon sop = new GeoEpsilon();

		// two segments only touching in one point:
		/**
		 * <tt>
		 *             +
		 *            /
		 *           /
		 *   +------+
		 * </tt>
		 */
		Segment3DElement seg1 = new Segment3DElement(new Point3D(1.0,
				1.0, 1.0), new Point3D(3.0, 1.0, 1.0), new GeoEpsilon());
		Segment3DElement seg2 = new Segment3DElement(new Point3D(3.0,
				1.0, 1.0), new Point3D(5.0, 2.0, 1.0), new GeoEpsilon());
		Geometry3D result = seg1.intersection(seg2, sop);

		assertTrue(result.getGeometryType() == GEOMETRYTYPES.POINT);
		if (result.getGeometryType() == GEOMETRYTYPES.POINT) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.isEqual(new Point3D(3.0, 1.0, 1.0), sop));
		}

		// two segments only touching in one point, segments are on one line:
		/**
		 * <tt>
		 *   +------+------+
		 * </tt>
		 */
		seg1 = new Segment3DElement(new Point3D(1.0, 1.0, 1.0), new Point3D(
				3.0, 1.0, 1.0), new GeoEpsilon());
		seg2 = new Segment3DElement(new Point3D(3.0, 1.0, 1.0), new Point3D(
				5.0, 1.0, 1.0), new GeoEpsilon());
		result = seg1.intersection(seg2, new GeoEpsilon());

		assertTrue(result.getGeometryType() == GEOMETRYTYPES.POINT);
		if (result.getGeometryType() == GEOMETRYTYPES.POINT) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.isEqual(new Point3D(3.0, 1.0, 1.0), sop));
		}

	}

	public void testDisjunctNetComponent() throws UpdateException {

		GeoEpsilon epsilon = new GeoEpsilon();
		Segment3DElement seg1 = new Segment3DElement(new Point3D(1.0,
				1.0, 1.0), new Point3D(3.0, 1.0, 1.0), epsilon);
		Segment3DElement seg2 = new Segment3DElement(new Point3D(3.0,
				1.0, 1.0), new Point3D(5.0, 1.0, 1.0), epsilon);
		Segment3DElement seg3 = new Segment3DElement(new Point3D(5.0,
				1.0, 1.0), new Point3D(7.0, 1.0, 1.0), epsilon);
		Segment3DComponent comp1 = new Segment3DComponent(
				new Segment3DElement[] { seg1, seg2, seg3 }, epsilon);

		Segment3DNet segNet3D = new Segment3DNet(
				new Segment3DComponent[] { comp1 }, epsilon);
		Segment3DComponent segComp = segNet3D.getComponent(0);

		assertTrue(segComp.countElements() == 3);

		GeometryException ge = null;
		try {
			segComp.addElement(new Segment3D(new Point3D(4.0, 1.0, 1.0),
					new Point3D(5.0, 1.0, 1.0), new GeoEpsilon()));
		} catch (GeometryException e) {
			ge = e;
		}
		assertTrue(ge != null);

	}

}
