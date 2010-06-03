/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.api.GeometryException;
import de.uos.igf.db3d.dbms.api.NameNotUniqueException;
import de.uos.igf.db3d.dbms.api.UpdateException;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.SegmentElt3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNet3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNet3DComp;
import de.uos.igf.db3d.dbms.model3d.SegmentNetBuilder;

/**
 * This testcase tests the (topology) methods of the
 * <code>SegmentNet3DComp</code> class.
 * 
 * @author Edgar Butwilowski
 */
public class SegmentNet3DCompTestCase extends TestCase {

	/**
	 * Tests the adding of segments in an already existing net comp.
	 * 
	 * @throws NameNotUniqueException
	 * @throws UpdateException
	 *             - signals an Update problem when adding new segments.
	 */
	public void testAddSegmentElt() throws NameNotUniqueException,
			UpdateException {

		/*
		 * This method tests the adding of segments in an already existing net
		 * comp.
		 */

		ScalarOperator sop = new ScalarOperator();

		SegmentNetBuilder segNetBuilder = new SegmentNetBuilder(sop);

		// creating segment net with two adjacent segments:
		/**
		 * <tt>
		 *       1     2
		 *   +------+------+
		 * </tt>
		 */
		SegmentElt3D seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0), new ScalarOperator());
		SegmentElt3D seg2 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0),
				new Point3D(5.0, 1.0, 1.0), sop);
		segNetBuilder.addComponent(new SegmentElt3D[] { seg1, seg2 });
		SegmentNet3D segNet3D = segNetBuilder.getSegmentNet();
		SegmentNet3DComp segComp = segNet3D.getComponent(0);

		// no adjacency of newly added segment:
		/**
		 * <tt>
		 *       1      2        new
		 *   +------+------+  + - - - +
		 * </tt>
		 */
		// this has to throw a GeometryException since adjacency is inevitable:
		GeometryException ge0 = null;
		try {
			segComp.addElt(new Point3D(6.0, 1.0, 1.0), new Point3D(7.0, 1.0,
					1.0));
		} catch (GeometryException e) {
			ge0 = e;
		}
		assertTrue(ge0 != null);

		// adding a third segment (adjacent to the segment net) afterwards by
		// defining its bounding points:
		/**
		 * <tt>
		 *      1      2      new
		 *   +------+------+ - - - +
		 * </tt>
		 */
		SegmentElt3D newSeg1 = new SegmentElt3D(new Point3D(5.0, 1.0, 1.0),
				new Point3D(6.0, 1.0, 1.0), sop);
		segComp.addElt(newSeg1);

		boolean geomEquiv = seg2.getNeighbour(1)
				.isGeometryEquivalent(seg1, sop);
		assertTrue(geomEquiv);
		geomEquiv = seg2.getNeighbour(0).isGeometryEquivalent(newSeg1, sop);
		assertTrue(geomEquiv);

		// adding a fourth segment (adjacent to the segment net) afterwards:
		/**
		 * <tt>
		 *      new     1      2
		 *   + - - - +------+------+
		 * </tt>
		 */
		SegmentElt3D newSeg2 = new SegmentElt3D(new Point3D(-1.0, 1.0, 1.0),
				new Point3D(1.0, 1.0, 1.0), sop);
		segComp.addElt(newSeg2);

		assertTrue(seg1.getNeighbour(1).isGeometryEquivalent(newSeg2, sop));
		assertTrue(seg1.getNeighbour(0).isGeometryEquivalent(seg2, sop));

		/**
		 * <tt>
		 *               +  new
		 *      1     2  |
		 *   +------+----+-+
		 *               |
		 *               +
		 * </tt>
		 */
		// this has to throw a GeometryException since the new element is
		// disjunct to net component:
		GeometryException ge1 = null;
		try {
			segComp.addElt(new Point3D(4.0, 0.0, 1.0), new Point3D(4.0, 2.0,
					1.0));
		} catch (GeometryException e) {
			ge1 = e;
		}
		assertTrue(ge1 != null);

		// self-intersection of a segment net:
		/**
		 * <tt>
		 *             +
		 *            /|
		 *           / |
		 *      new /  |
		 *         /   | 3
		 *   +----+----+
		 *   1      2
		 * </tt>
		 */
		segComp.addElt(new Point3D(6.0, 1.0, 1.0), new Point3D(6.0, 5.0, 1.0));

		GeometryException ge2 = null;
		try {
			segComp.addElt(new Point3D(6.0, 5.0, 1.0), new Point3D(5.0, 1.0,
					1.0));
		} catch (GeometryException e) {
			ge2 = e;
		}
		assertTrue(ge2 != null);

		segNetBuilder = new SegmentNetBuilder(sop);

		// creating segment net with two adjacent segments:
		/**
		 * <tt>
		 *          +
		 *          |
		 *          | 2
		 *   +------+
		 *      1  
		 * </tt>
		 */
		seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0), new Point3D(3.0,
				1.0, 1.0), new ScalarOperator());
		seg2 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0), new Point3D(3.0,
				4.0, 1.0), sop);
		segNetBuilder.addComponent(new SegmentElt3D[] { seg1, seg2 });
		segNet3D = segNetBuilder.getSegmentNet();
		segComp = segNet3D.getComponent(0);

		// self-intersection of a segment net:
		/**
		 * <tt>
		 *          +
		 *     new /|
		 *        / | 2
		 *   +---/--+
		 *   1  /
		 *     +
		 * </tt>
		 */

		// this is possible since no intersection between the new segment and
		// segment 1 is computed
		SegmentElt3D newSeg4 = new SegmentElt3D(new Point3D(3.0, 4.0, 1.0),
				new Point3D(2.0, 0.0, 1.0), sop);
		newSeg4 = segComp.addElt(newSeg4);

		assertTrue(newSeg4.getNeighbour(1) == null);
		assertTrue(newSeg4.getNeighbour(0).isGeometryEquivalent(seg2, sop));

	}

}
