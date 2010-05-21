/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import de.uos.igf.db3d.dbms.structure.GeoObj;

/**
 * Interface SimpleGeoObj is a common data type for the simple geo-objects in
 * the framework. Currently it is primarily a marker interface.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public interface SimpleGeoObj extends GeoObj {

	// simple objects (3D)
	/** constant for a Point3D object */
	public static final byte POINT3D = 1;
	
	/** constant for a Segment3D object */
	public static final byte SEGMENT3D = 2;
	
	/** constant for a Triangle3D object */
	
	public static final byte TRIANGLE3D = 3;
	
	/** constant for a Tetrahedron3D object */
	public static final byte TETRAHEDRON3D = 4;

	/** constant for a PointElt3D object */
	public static final byte POINT_ELT_3D = 5;
	
	/** constant for a SegmentElt3D object */
	public static final byte SEGMENT_ELT_3D = 6;
	
	/** constant for a TriangleElt3D object */
	public static final byte TRIANGLE_ELT_3D = 7;
	
	/** constant for a TetrahedronElt3D object */
	public static final byte TETRAHEDRON_ELT_3D = 8;

	// simple objects (4D)
	/** constant for a 4D point element */
	public static final byte POINT4DELEMENT = 100;
	
	/** constant for a 4D segment element */
	public static final byte SEGMENT4DELEMENT = 101;
	
	/** constant for a 4D triangle element */
	public static final byte TRIANGLE4DELEMENT = 102;
	
	/** constant for a 4D tetrahedron element */
	public static final byte TETRAHEDRON4DELEMENT = 103;
	
	/** constant for point tubes */
	public static final byte POINT4DTUBE = 112;

	// Sets
	/** constant for a PointSet3D object */
	public static final byte POINTSET3D = 9;
	
	/** constant for a SegmentSet3D object */
	public static final byte SEGMENTSET3D = 10;
	
	/** constant for a TriangleSet3D object */
	public static final byte TRIANGLESET3D = 11;
	
	/** constant for a TetrahedronSet3D object */
	public static final byte TETRAHEDRONSET3D = 12;

	// Utility classes
	/** constant for a Vector3D object */
	public static final byte VECTOR3D = 13;
	
	/** constant for a Line3D object */
	public static final byte LINE3D = 14;
	
	/** constant for a Plane3D object */
	public static final byte PLANE3D = 15;
	
	/** constant for a MBB3D object */
	public static final byte MBB3D = 16;
	
	/** constant for a WIREFRAME object */
	public static final byte WIREFRAME3D = 17;

	// geometric constants
	/** minimal epsilon factor for length comparison (e.g. in isRegular methods) */
	public static final int MIN_LENGTH_EPSILON_FACTOR = 10;
	
	/** minimal epsilon factor for area comparison (e.g. in isRegular methods) */
	public static final int MIN_AREA_EPSILON_FACTOR = 20;
	
	/** minimal epsilon factor for volume comparison (e.g. in isRegular methods) */
	public static final int MIN_VOLUME_EPSILON_FACTOR = 40;

}
