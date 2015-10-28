package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.model3d.ComplexGeoObj;

/**
 * This class represents a 4D TetrahedronNet. A TetrahedronNet objects consists of one
 * to many TetrahedronComponent objects.
 * 
 * Jedes Component Objekt hat ein Zeitinterval, was dem des Netzes
 * Objektes entspricht. Allerdings kann jedes Component Objekt eine
 * andere zeitliche Diskretisierung besitzen (s. Dissertation).
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TetrahedronNet4D implements Net4D {

	// The Components of this Net
	// Due to the Components we can handle different parts of the net
	// with a different temporal discretisation.
	private Map<Integer, Component4D> components;

	// Connects TimeIntervals to Component objects
	private Map<TimeInterval, List<Integer>> timeIntervals;

	// Dates with a change of net topology
	private LinkedList<Date> changeDates;

	// the start date of the existence interval for this Net4D object
	private Date start;

	// the end date of the existence interval for this Net4D object
	private Date end;

	// TimeInterval to Component Mapper
	private	TimeInterval currentInterval;

	// Die Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel eine Map:
	private List<Map<Integer, Element4D>> elements;
	
	// indicates if Boundary elements should be handled explicitly
	private boolean boundaryElements;

	// Die Boundary-Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel
	// eine Map:
	private List<Map<Integer, Element4D>> boundaryElements1D;
	
	// Die Boundary-Elemente dieses Netzes mit ID. Fuer jeden Topologiewechsel
	// eine Map:
	private List<Map<Integer, Element4D>> boundaryElements2D;

	/**
	 * Constructor for a TetrahedronNet4D. The initial start date is set. Call
	 * createEndOfExistenceInterval() function to set an end date.
	 * 
	 * @param start
	 *            - Start Date
	 */
	public TetrahedronNet4D(Date start) {
		super();
		components = new HashMap<Integer, Component4D>();
		timeIntervals = new HashMap<TimeInterval, List<Integer>>();
		changeDates = new LinkedList<Date>();
		elements = new LinkedList<Map<Integer, Element4D>>();

		this.start = start;
		this.end = new Date(Long.MAX_VALUE);

		currentInterval = new TimeInterval(start, null);

		timeIntervals.put(currentInterval, new LinkedList<Integer>());
		boundaryElements = false;
	}

	public void addBoundaryElement(Segment4D seg) {

		boundaryElements1D.get(boundaryElements1D.size() - 1).put(seg.getID(),
				seg);
	}

	public void addBoundaryElement(Triangle4D tri) {

		boundaryElements2D.get(boundaryElements2D.size() - 1).put(tri.getID(),
				tri);
	}

	@Override
	public void addChangeTimestep(Date date) {
		changeDates.add(date);
		// Add new Post object:
		elements.add(new TreeMap<Integer, Element4D>());	
	}

	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTetrahedron(Tetrahedron4D tetrahedron) {

		// Immer an der aktuellen Stelle einfuegen:
		if (elements.get(elements.size()-1).containsKey(tetrahedron.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangle that already exists to the TriangleNet.");
		}
		elements.get(elements.size()-1).put(tetrahedron.getID(), tetrahedron);
	}

	/**
	 * Add a single Component to the net.
	 * 
	 * @param TetrahedronComponent4D
	 */
	public void addTetrahedronComponent(TetrahedronComponent4D component) {

		if (components.containsKey(component.getID())) {
			throw new IllegalArgumentException(
					"You tried to add a triangleComponent that already exists to the TriangleNet.");
		}
		components.put(component.getID(), component);

		// update TimeInterval to Component Mapper
		timeIntervals.get(currentInterval).add(component.getID());
	}

	public void addTimestep(Component4D component,
			HashMap<Integer, Point3D> newPoints, Date date) {

	}

	/**
	 * We need to close all TimeIntervals of all current components of this net
	 * 
	 * @param date
	 */
	private void closeAllComponents(Date date) {

		for (Integer ID : timeIntervals.get(currentInterval)) {
			Component4D comp = components.get(ID);
			comp.getTimeInterval().setEnd(date);
		}
	}

	/**
	 * We need to close the TimeIntervals of this net
	 * 
	 * @param date
	 */
	private void closeTimeInterval(Date date) {

		// close old TimeInterval
		currentInterval.setEnd(date);

		// start new TimeInterval
		currentInterval = new TimeInterval(date, null);

		// generate new List of Components for the new TimeInterval
		timeIntervals.put(currentInterval, new LinkedList<Integer>());
	}

	/**
	 * Creates the end of the time interval.
	 * 
	 * @param end
	 */
	public void createEndOfExistenceInterval(Date end) {
		this.end = end;
	}

	public Map<Integer, Element4D> getBoundaryElements1D(Date date) {
		int index = 0;

		// is it invalid?
		if (date.before(changeDates.get(0)))
			return null;

		for (Date check : changeDates) {
			if (check.before(date) || check.equals(date))
				index++;
		}

		return boundaryElements1D.get(index - 1);
	}

	public Map<Integer, Element4D> getBoundaryElements2D(Date date) {
		int index = 0;

		// is it invalid?
		if (date.before(changeDates.get(0)))
			return null;

		for (Date check : changeDates) {
			if (check.before(date) || check.equals(date))
				index++;
		}

		return boundaryElements2D.get(index - 1);
	}

	public LinkedList<Date> getChangeDates() {
		return changeDates;
	}

	/**
	 * Returns a specific Components of this Net.
	 * 
	 * @return Component4D
	 */
	public Component4D getComponent(int ID) {
		return components.get(ID);
	}

	/**
	 * Returns the Components of this Net.
	 * 
	 * @return Map<Integer, Component4D> - All Components of
	 *         this net.
	 */
	public Map<Integer, Component4D> getComponents() {
		return components;
	}

	/**
	 * Returns elements of this Net.
	 * 
	 * @return Map<Integer, Element4D> - All elements of this net.
	 */
	public List<Map<Integer, Element4D>> getElements() {
		return elements;
	}

	public Date getEnd() {
		return end;
	}

	public Date getLastChangeDate() {
		return changeDates.getLast();
	}

	@Override
	public Map<Integer, Element4D> getNetElements(Date date) {
		
		int index = 0;
		
		// is it invalid?
		if(date.before(changeDates.get(0)))
			return null;
		
		for(Date check : changeDates) {
			if(check.before(date) || check.equals(date)) index++;
		}
		
		return elements.get(index-1);
	}
	
	public Date getStart() {
		return start;
	}	
	
	@Override
	public byte getType() {		
		return ComplexGeoObj.TETRAHEDRON_NET_4D;
	}

	@Override
	public List<Component4D> getValidComponents(Date date) {
		
		for(TimeInterval interval : timeIntervals.keySet()) {
			if(interval.getStart().before(date) && interval.getEnd().after(date)) {
				
				List<Component4D> tmp = new LinkedList<Component4D>();
				
				for(Integer ID : timeIntervals.get(interval))
					tmp.add(components.get(ID));
				
				return tmp;
			}
		}
		return null;
	}
	
	public boolean isBoundaryElements() {
		return boundaryElements;
	}
	
	public void preparePostObject(Date date) {
		closeAllComponents(date);
		closeTimeInterval(date);
		addChangeTimestep(date);
	}

	public void setBoundaryElements(boolean boundaryElements) {
		this.boundaryElements = boundaryElements;
	}
}
