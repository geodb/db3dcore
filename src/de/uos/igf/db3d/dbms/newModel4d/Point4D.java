package de.uos.igf.db3d.dbms.newModel4d;

/**
 * This class represents a 4D point object. 
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class Point4D implements Element4D {

	/* id of this - unique in whole net */
	private int id;

	/**
	 * Constructor. 
	 * Constructs a Point4D with given point ID.
	 * 
	 * @param id
	 *            PointTube ID
	 */
	public Point4D(int id) {
		super();
		this.id = id;
	}
	
	public int getID() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
}
