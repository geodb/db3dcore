/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.structure.GeoObj;

/**
 * Interface SimpleGeoObj is a common datatype for the simple geo objects in the
 * framework.
 */
public interface ComplexGeoObj extends GeoObj {

	/** defines the maximum entries per node in the SAM */
	public static final short MAX_SAM = 8;

	// complex objects (3D)
	/** constant for a PointNet3D object */
	public static final byte POINT_NET_3D = 20;

	/** constant for a SegmentNet3D object */
	public static final byte SEGMENT_NET_3D = 21;

	/** constant for a TriangleNet3D object */
	public static final byte TRIANGLE_NET_3D = 22;

	/** constant for a ClosedHull3D object */
	public static final byte CLOSED_HULL_3D = 23;

	/** constant for a TetrahedronNet3D object */
	public static final byte TETRAHEDRON_NET_3D = 24;

	/** constant for a RegularDeformedGrid3D object */
	public static final byte REGULAR_DEFORMED_GRID_3D = 19;

	// complex objects (4D)
	/** constant for a 4D point net object */
	public static final byte POINTNET4D = 50;

	/** constant for a 4D segment sequence */
	public static final byte SEGMENTNET4D = 51;

	/** constant for a 4D triangle sequence */
	public static final byte TRIANGLENET4D = 52;

	/** constant for a 4D tetrahedron sequence */
	public static final byte TETRAHEDRONNET4D = 53;

	/** constant for a ResultObject3D object */
	public static final byte RESULT_OBJECT_3D = 28;

	// net components (3D)
	/** constant for a PointNet3DComp object */
	public static final byte COMP_POINT_NET_3D = 25;

	/** constant for a SegmentNet3DComp object */
	public static final byte COMP_SEGMENT_NET_3D = 26;

	/** constant for a TriangleNet3DComp object */
	public static final byte COMP_TRIANGLE_NET_3D = 27;

	/** constant for a TetrahedronNet3DComp object */
	public static final byte COMP_TETRAHEDRON_NET_3D = 29;

	/** constant for a ClosedHull3DComp object */
	public static final byte COMP_CLOSED_HULL_3D = 30;

	// net components (4D)
	/** constant for a 4D point component */
	public static final byte POINTNET4DCOMPONENT = 60;

	/** constant for a 4D segment component */
	public static final byte SEGMENTNET4DCOMPONENT = 61;

	/** constant for a 4D triangle component */
	public static final byte TRIANGLENET4DCOMPONENT = 62;

	/** constant for a 4D tetrahedron component */
	public static final byte TETRAHEDRONNET4DCOMPONENT = 63;

	/** constant for a 4D closed hull component */
	public static final byte CLOSEDHULL4DCOMPONENT = 63;

	// space-time-sequences
	/** constant for a 4D point sequence */
	public static final byte POINT4DSEQUENCE = 90;

	/** constant for a 4D segment sequence */
	public static final byte SEGMENT4DSEQUENCE = 91;

	/** constant for a 4D triangle sequence */
	public static final byte TRIANGLE4DSEQUENCE = 92;

	/** constant for a 4D tetrahedron sequence */
	public static final byte TETRAHEDRON4DSEQUENCE = 93;

	// net components (4D)
	/** constant for a PointNet4D object */
	public static final byte POINT_NET_4D = 71;

	/** constant for a SegmentNet4D object */
	public static final byte SEGMENT_NET_4D = 72;

	/** constant for a TriangleNet4D object */
	public static final byte TRIANGLE_NET_4D = 73;

	/** constant for a TetrahedronNet4D object */
	public static final byte TETRAHEDRON_NET_4D = 74;

	public int countElements();

	public SimpleGeoObj getElement(int id);
}
