package de.uos.igf.db3d.dbms.spatials.api;

import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * Interface GeoObj is a common datatype for holding the different geometric
 * object types in the framework. It also describes methods for identifying the
 * different types. The needed constants are defined in the subinterfaces split
 * in simple and complex objects.<br>
 * The only provided methods of geoobjects is getMBB which has to return an MBB
 * in 3D describing the space occupied by the object.
 * 
 * @author Markus Jahn
 * 
 */
public interface Spatial extends Equivalentable {

	public static enum SPATIALTYPES {

		SAMPLE_NET_C_E3D, SAMPLE_NET_C_S_EI, CURVE_NET_C_E3D, SURFACE_NET_C_E3D, HULL_NET_C_E3D, SOLID_NET_C_E3D, GRID_NET_C_E3D, SAMPLE_COMPONENT_E3D, CURVE_COMPONENT_E3D, SURFACE_COMPONENT_E3D, HULL_COMPONENT_E3D, SOLID_COMPONENT_E3D, SAMPLE_SEQUENCE_EI, CURVE_SEQUENCE_EI, SURFACE_SEQUENCE_EI, HULL_SEQUENCE_EI, SOLID_SEQUENCE_EI, SAMPLE_ELEMENT_3D, CURVE_ELEMENT_3D, SURFACE_ELEMENT_3D, SOLID_ELEMENT_3D, RESULT, SPATIALWRAPPER_N_C_E3D, WIREFRAME_ELEMENT_3D, WIREFRAME_NET_C_E3D, WIREFRAME_COMPONENT_E3D, CURVE_NET_C_S_EI, CURVE_COMPONENT_S_EI, CURVE_ELEMENT_I, HULL_COMPONENT_S_EI, HULL_NET_C_S_EI, SAMPLE_COMPONENT_S_EI, SAMPLE_ELEMENT_I, SOLID_COMPONENT_S_EI, SOLID_ELEMENT_I, SOLID_NET_C_S_EI, SURFACE_COMPONENT_S_EI, SURFACE_ELEMENT_I, SURFACE_NET_C_S_EI, SURFACE_SEQUENCE_EI_WRAPPER

	};

	/**
	 * Method for identifying the type of object.
	 * 
	 * @return byte - constant for this type.
	 */
	public SPATIALTYPES getSpatialType();

	public GeoEpsilon getGeoEpsilon();

}
