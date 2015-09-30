/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms.model3d;

import junit.framework.TestCase;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DComponent;
import de.uos.igf.db3d.dbms.spatials.standard3d.Segment3DElement;

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

	private Segment3DElement segmentA;
	private Segment3DElement segmentB;
	private Segment3DElement segmentC;

	/**
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public void setUp() throws Exception {

		GeoEpsilon sop = new GeoEpsilon();
		segmentA = new Segment3DElement(P1, P2, sop);
		segmentB = new Segment3DElement(P2, P3, sop);
		segmentC = new Segment3DElement(P3, P4, sop);

		Segment3DNet net = new Segment3DNet(
				new Segment3DComponent[] { new Segment3DComponent(
						new Segment3DElement[] { segmentA, segmentB,
								segmentC }, sop) }, sop);

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
