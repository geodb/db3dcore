package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a 4D TriangleNet. 
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleNet4D {
	
	// the elements of this TriangleNet
	Map<Integer, Triangle4D> elements;
	
	// the start date of the existence interval for this TriangleNet4D object
	Date start;
	
	// the end date of the existence interval for this TriangleNet4D object
	Date end;
	

	/**
	 * Constructor for a TriangleNet4D.
	 * The initial start date is set. 
	 * Call createEndOfExistenceInterval() function to set an end date.   
	 * 
	 * @param start - Start Date
	 */
	public TriangleNet4D(Date start) {
		super();
		elements = new HashMap<Integer, Triangle4D>();
		this.start = start;
		this.end = null;
	}
	
	/**
	 * Add a single triangle to the net.
	 * 
	 * @param triangle
	 */
	public void addTriangle(Triangle4D triangle) {
	
		if(elements.containsKey(triangle.getID())) {
			throw new IllegalArgumentException(
			"You tried to add a triangle that already exists to the TriangleNet.");
		}
		elements.put(triangle.getID(), triangle);		
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
	 * Returns elements of this TriangleNet.
	 * 
	 * @return Map<Integer, Triangle4D> - All elements of this net.
	 */
	public Map<Integer, Triangle4D> getElements() {
		return elements;
	}
}
