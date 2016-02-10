package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.OID;

/**
 * This class represents an 4D object. The user has to call the function
 * addTimestep() to add the Point information for the PointTubes this Object4D
 * is working with. After that the user has to call the function addGeometry()
 * to add the geometry informations for the last added timestep. This procedure
 * must be done for every timestep with a changing net topology.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class Object4D {


	// contains the spatial of this object
	// spatial objects only consists of the ID information of its Point3D
	// elements
	// every SpatialObject4D object has its own timeinterval
	private SpatialObject4D spatial;

	private ScalarOperator sop;

	private OID ID;
	
	private String name;

	/**
	 * Constructor
	 * 
	 */
	public Object4D() {
		super();
		sop = new ScalarOperator();
		name = "N/A";
	}

	public SpatialObject4D getSpatial() {
		return spatial;
	}

	public void setSpatial(SpatialObject4D spatial) {
		this.spatial = spatial;
	}

	public void setID(OID oid) {
		
		this.ID = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
