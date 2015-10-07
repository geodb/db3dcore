package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

/**
 * This class represents a 4D TriangleNet. 
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleComponent4D implements Component4D {
	
	// ID
	int id;
	
	// Back reference to the corresponding TriangleNet object
	Net4D net;
	
	// TODO: Need? Better from the net object!
	// the start date of the existence interval for this TriangleComponent4D object
	Date start;
	
	// the end date of the existence interval for this TriangleComponent4D object
	Date end;
	
	// TODO: Ab in die Components! Getter Methoden für die Objekte zum 
	// Zeitpunkt XY in die Components!
	// The pointTubes of this 4D Object
	// <ID, <Zeitschritt, Point3D>>
	private Map<Integer, Map<Integer, Point3D>> pointTubes;

	// TODO: Put this into the components!!!
	// List of timesteps with their effective date
	protected LinkedList<Date> timesteps;

	/**
	 * Constructor for a TriangleNet4D.
	 * The initial start date is set. 
	 * Call createEndOfExistenceInterval() function to set an end date.   
	 * 
	 * @param start - Start Date
	 */
	public TriangleComponent4D(TriangleNet4D net, int id) {
		this.net = net;
		this.id = id;
	}
		
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public Map<Integer, Map<Integer, Point3D>> getPointTubes() {
		return pointTubes;
	}

	public LinkedList<Date> getTimesteps() {
		return timesteps;
	}

	public Net4D getNet() {
		return net;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
