package de.uos.igf.db3d.dbms.newModel4d;

/**
 * This class represents a 4D segment object.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class Segment4D implements Element4D {	

	/* ID of the points */
	private int IDstart;
	private int IDend;

	/* id of this - unique in whole net */
	private int ID;

	/**
	 * Constructor. Constructs a Segment4D with given pointTubeIDs
	 */
	public Segment4D(int IDstart, int IDend, int ID) {
		super();
		this.IDstart = IDstart;
		this.IDend = IDend;
		this.ID = ID;
	}
	
	/**
	 * Constructor. Constructs a Segment4D with given pointTubeID array
	 * 
	 * @param points
	 *            Point3D array.
	 */
	public Segment4D(int[] IDs, Integer id) {
		super();
		IDstart = IDs[0];
		IDend = IDs[1];
		
		this.ID = id;
	}

	public int getIDstart() {
		return IDstart;
	}

	public int getIDend() {
		return IDend;
	}

	public int getID() {
		return ID;
	}
}
