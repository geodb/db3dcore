package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

/**
 * This class represents a 4D TriangleNet. A TriangleNet objects consists of one
 * to many TriangleComponent objects.
 * 
 * Jedes TriangleComponent Objekt hat ein Zeitinterval, was dem des TriangleNet
 * Objektes entspricht. Allerdings kann jedes TriangleComponent objekt eine
 * andere zeitliche Diskretisierung besitzen (s. Dissertation).
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleNet4D implements Net4D {

	// The TriangleComponents of this TriangleNet
	// Due to the TriangleComponents we can handle different parts of the net
	// with a different temporal discretisation.
	Map<Integer, TriangleComponent4D> components;

	// Connects TimeIntervals to TriangleComponent4D objects
	Map<TimeInterval, List<Integer>> timeIntervals;

	// Dates with a explicit timestep
	LinkedList<Date> dates;

	// the start date of the existence interval for this TriangleNet4D object
	Date start;

	// the end date of the existence interval for this TriangleNet4D object
	Date end;

	// Die Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel eine Map:
	
	List<Map<Integer, Element4D>> elements;

	/**
	 * Constructor for a TriangleNet4D. The initial start date is set. Call
	 * createEndOfExistenceInterval() function to set an end date.
	 * 
	 * @param start
	 *            - Start Date
	 */
	public TriangleNet4D(Date start) {
		super();
		components = new HashMap<Integer, TriangleComponent4D>();
		timeIntervals = new HashMap<TimeInterval, List<Integer>>();
		dates = new LinkedList<Date>();
		elements = new LinkedList<Map<Integer, Element4D>>();
		
		// Add first Post object:
		elements.add(new HashMap<Integer, Element4D>());
		
		this.start = start;
		this.end = null;
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangleComponent(TriangleComponent4D component) {

		if (components.containsKey(component.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangleComponent that already exists to the TriangleNet.");
		}
		components.put(component.getID(), component);
	}

	/**
	 * Creates the end of the time interval.
	 * 
	 * @param end
	 */
	public void createEndOfExistenceInterval(Date end) {
		this.end = end;
	}

	/**
	 * Returns TriangleComponents of this TriangleNet.
	 * 
	 * @return Map<Integer, TriangleComponent4D> - All TriangleComponents of
	 *         this net.
	 */
	public Map<Integer, TriangleComponent4D> getComponents() {
		return components;
	}
	
	/**
	 * Returns a specific TriangleComponents of this TriangleNet.
	 * 
	 * @return TriangleComponent4D
	 */
	public TriangleComponent4D getComponent(int ID) {
		return components.get(ID);
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangle(Triangle4D triangle) {		
		
		// Immer an der aktuellen Stelle einfuegen:
		if (elements.get(elements.size()).containsKey(triangle.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangle that already exists to the TriangleNet.");
		}
		elements.get(elements.size()).put(triangle.getID(), triangle);
	}

	/**
	 * Returns elements of this TriangleNet.
	 * 
	 * @return Map<Integer, Element4D> - All elements of this net.
	 */
	public List<Map<Integer, Element4D>> getElements() {
		return elements;
	}

	public void addTimestep(Component4D component,
			HashMap<Integer, Point3D> newPoints, Date date) {

	}

	@Override
	public void TopologyChange(Date date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTimestep(Date date) {
		dates.add(date);
		java.util.Collections.sort(dates);
	}

	public LinkedList<Date> getDates() {
		return dates;
	}

	public Date getLastDate() {
		return dates.getLast();
	}

	public void preparePostObject(Date date) {
		closeTimeInterval(date);
		closeAllComponents(date);
	}
}
