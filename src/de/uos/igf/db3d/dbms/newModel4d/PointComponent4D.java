package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uos.igf.db3d.dbms.geom.Point3D;

/**
 * This class represents a 4D PointComponent. 
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class PointComponent4D implements Component4D {
	
	// ID
	int id;
	
	// Back reference to the corresponding PointNet object
	Net4D net;
	
	// TimeInterval of this component
	TimeInterval timeInterval;
	
	// The pointTubes of this 4D Object
	// <ID, <Zeitschritt, Point3D>>
	private Map<Integer, List<Point3D>> pointTubes;

	// List of timesteps with their effective date
	protected LinkedList<Date> timesteps;

	/**
	 * Constructor for a PointComponent4D.
	 * The initial start date is set. 
	 * Call createEndOfExistenceInterval() function to set an end date.   
	 * 
	 * @param start - Start Date
	 */
	public PointComponent4D(PointNet4D net, int id) {
		this.net = net;
		this.id = id;
		timeInterval = new TimeInterval(net.getLastChangeDate(), null);
		timesteps = new LinkedList<Date>();
		pointTubes = new TreeMap<Integer, List<Point3D>>();
	}
		
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public Map<Integer, List<Point3D>> getPointTubes() {
		return pointTubes;
	}

	public LinkedList<Date> getTimesteps() {
		return timesteps;
	}

	public Net4D getNet() {
		return net;
	}

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}
}
