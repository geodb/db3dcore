package de.uos.igf.db3d.dbms.spatials.api;

import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;

public interface Geometry3D extends Equivalentable {

	public enum GEOMETRYTYPES {

		POINT, SEGMENT, TRIANGLE, TETRAHEDRON, RECTANGLE, POINTSET, SEGMENTSET, TRIANGLESET, TETRAHEDRONSET, VECTOR, LINE, PLANE, MBB, WIREFRAME

	};

	// geometric constants
	/** minimal epsilon factor for length comparison (e.g. in isRegular methods) */
	public static final int MIN_LENGTH_EPSILON_FACTOR = 10;

	/** minimal epsilon factor for area comparison (e.g. in isRegular methods) */
	public static final int MIN_AREA_EPSILON_FACTOR = 20;

	/** minimal epsilon factor for volume comparison (e.g. in isRegular methods) */
	public static final int MIN_VOLUME_EPSILON_FACTOR = 40;

	/**
	 * Method for identifying the type of object.
	 * 
	 * @return byte - constant for this type.
	 */
	public GEOMETRYTYPES getGeometryType();

	/**
	 * Returns the mbb of the geoobject.
	 * 
	 * @return MBB3D of the geoobject.
	 */
	public MBB3D getMBB();

	// public SRID getSRID();
	//
	// public void setSRID(SRID srid);

}
