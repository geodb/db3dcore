/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.geom;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.api.GeometryException;
import de.uos.igf.db3d.dbms.api.UpdateException;
import de.uos.igf.db3d.dbms.geom.*;
import de.uos.igf.db3d.dbms.model3d.SegmentElt3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNet3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNet3DComp;
import de.uos.igf.db3d.dbms.model3d.SegmentNetBuilder;

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
		SegmentElt3D seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), new ScalarOperator());
		SegmentElt3D seg2 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), new ScalarOperator());
		assertTrue(seg1.equals(seg2));		
		SegmentElt3D seg3 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0), new Point3D(1.0, 1.0, 1.0), new ScalarOperator());
		assertTrue(seg1.equals(seg3));
	}

	public void testIntersection() {

		/*
		 * This method tests the intersection algorithms of a segment.
		 */

		ScalarOperator sop = new ScalarOperator();

		// two segments only touching in one point:
		/**
		 * <tt>
		 *             +
		 *            /
		 *           /
		 *   +------+
		 * </tt>
		 */
		SegmentElt3D seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), new ScalarOperator());
		SegmentElt3D seg2 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0),
				new Point3D(5.0, 2.0, 1.0), new ScalarOperator());
		SimpleGeoObj result = seg1.intersection(seg2, sop);

		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.isEqual(new Point3D(3.0, 1.0, 1.0), sop));
		}

		// two segments only touching in one point, segments are on one line:
		/**
		 * <tt>
		 *   +------+------+
		 * </tt>
		 */
		seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0), new Point3D(3.0,
				1.0, 1.0), new ScalarOperator());
		seg2 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0), new Point3D(5.0,
				1.0, 1.0), new ScalarOperator());
		result = seg1.intersection(seg2, new ScalarOperator());

		assertTrue(result instanceof Point3D);
		if (result instanceof Point3D) {
			Point3D resultPoint = (Point3D) result;
			assertTrue(resultPoint.isEqual(new Point3D(3.0, 1.0, 1.0), sop));
		}

	}

	public void testDisjunctNetComponent() throws UpdateException {

		ScalarOperator sop = new ScalarOperator();
		SegmentNetBuilder segNetBuilder = new SegmentNetBuilder(sop);
		SegmentElt3D seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), sop);
		SegmentElt3D seg2 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0),
				new Point3D(5.0, 1.0, 1.0), sop);
		SegmentElt3D seg3 = new SegmentElt3D(new Point3D(5.0, 1.0, 1.0),
				new Point3D(7.0, 1.0, 1.0), sop);
		segNetBuilder.addComponent(new SegmentElt3D[] { seg1, seg2, seg3 });
		SegmentNet3D segNet3D = segNetBuilder.getSegmentNet();
		SegmentNet3DComp segComp = segNet3D.getComponent(0);

		assertTrue(segComp.countElements() == 3);

		GeometryException ge = null;
		try {
			segComp.addElt(new Segment3D(new Point3D(4.0, 1.0, 1.0),
					new Point3D(5.0, 1.0, 1.0), new ScalarOperator()));
		} catch (GeometryException e) {
			ge = e;
		}
		assertTrue(ge != null);

	}

}
