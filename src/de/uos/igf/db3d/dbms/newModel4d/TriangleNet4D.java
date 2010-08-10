package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TriangleNet4D {
	
	// the elements of this TriangleNet
	Map<Integer, Triangle4D> elements;
	
	// the start date of the existence interval for this TriangleNet4D object
	Date start;
	
	// the end date of the existence interval for this TriangleNet4D object
	Date end;
	

	public TriangleNet4D(Date start) {
		super();
		elements = new HashMap<Integer, Triangle4D>();
		this.start = start;
		this.end = null;
	}
	
	public void addTriangle(Triangle4D triangle) {
	
		if(elements.containsKey(triangle.getID())) {
			throw new IllegalArgumentException(
			"You tried to add a triangle that already exists to the TriangleNet.");
		}
		elements.put(triangle.getID(), triangle);		
	}
	
	public void createEndOfExistenceInterval(Date end) {
		this.end = end;
	}

	public Map<Integer, Triangle4D> getElements() {
		return elements;
	}
}
