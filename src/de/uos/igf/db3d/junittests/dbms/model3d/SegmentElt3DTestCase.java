/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.SegmentElt3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNetBuilder;

/**
 * This testcase tests the (topology) methods of the <code>SegmentElt3D</code>
 * class.
 * 
 * @author Edgar Butwilowski
 */
public class SegmentElt3DTestCase extends TestCase {

	private final static Point3D P1 = new Point3D(1.0, 2.0, 0.0);
	private final static Point3D P2 = new Point3D(2.0, 1.0, 0.0);
	private final static Point3D P3 = new Point3D(3.0, 1.0, 0.0);
	private final static Point3D P4 = new Point3D(4.0, 2.0, 0.0);

	private SegmentElt3D segmentA;
	private SegmentElt3D segmentB;
	private SegmentElt3D segmentC;

	/**
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void setUp() throws Exception {

		ScalarOperator sop = new ScalarOperator();
		segmentA = new SegmentElt3D(P1, P2, sop);
		segmentB = new SegmentElt3D(P2, P3, sop);
		segmentC = new SegmentElt3D(P3, P4, sop);

		SegmentNetBuilder netBuilder = new SegmentNetBuilder(sop);
		netBuilder.addComponent(new SegmentElt3D[] { segmentA, segmentB,
				segmentC });
		// Here an IllegalArgumentException can be thrown.

	}

	/**
	 * This method tests the neighbourship in very simple geometrical
	 * configuration of three segments:
	 * 
	 * <tt>
	 * +            +
	 *  \          /
	 *   \        /
	 *    +------+
	 * </tt>
	 * 
	 */
	public void testNeighbourshipSimple() {

		// the middle segment ("segmentB") has whether "segmentA" or "segmentC"
		// as its left or right neighbour (depending on orientation):
		assertTrue((segmentB.getNeighbour(0) == segmentA && segmentB
				.getNeighbour(1) == segmentC)
				|| (segmentB.getNeighbour(0) == segmentC && segmentB
						.getNeighbour(1) == segmentA));
		assertTrue((segmentA.getNeighbour(0) == segmentB && segmentA
				.getNeighbour(1) == null)
				|| (segmentA.getNeighbour(1) == segmentB && segmentA
						.getNeighbour(0) == null));
		assertTrue((segmentC.getNeighbour(0) == segmentB && segmentC
				.getNeighbour(1) == null)
				|| (segmentC.getNeighbour(1) == segmentB && segmentC
						.getNeighbour(0) == null));

	}

	public void tearDown() throws Exception {
	}

}
