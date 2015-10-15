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
	private int id;

	/**
	 * Constructor. Constructs a Segment4D with given pointTubeIDs
	 * 
	 * @param points
	 *            Point3D array.
	 */
	public Segment4D(int iDstart, int iDend, int id) {
		super();
		IDstart = iDstart;
		IDend = iDend;
		this.id = id;
	}

	public int getIDstart() {
		return IDstart;
	}

	public int getIDend() {
		return IDend;
	}

	public int getID() {
		return id;
	}
}
