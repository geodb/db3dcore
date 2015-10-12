package de.uos.igf.db3d.dbms.newModel4d;

/**
 * This class represents a 4D Triangle object. 
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class Triangle4D implements Element4D {	


	/* ID of the points */
	private int IDzero;
	private int IDone;
	private int IDtwo;
	
	/* id of this - unique in whole net */
	private int id;

	/**
	 * Constructor. 
	 * Constructs a TriangleElt4D as a Triangle4D with given points.
	 * 
	 * @param points
	 *            Point3D array.
	 */
	public Triangle4D(int[] IDs, Integer ID)
			throws IllegalArgumentException {

		this.IDzero = IDs[0];
		this.IDone = IDs[1];
		this.IDtwo = IDs[2];
		
		this.id = ID;
	}

	/**
	 * Constructor.
	 * Constructs a TriangleElt4D as a Triangle4D with given points.
	 * 
	 * @param point1
	 *            Point3D.
	 * @param point2
	 *            Point3D.
	 * @param point3
	 *            Point3D.		 
	 */
	public Triangle4D(int pointID1, int pointID2, int pointID3, Integer ID) {
		this(new int[] { pointID1, pointID2, pointID3 }, ID);
	}
	
	// Getter methods for the fields:
	
	public int getID() {
		return id;
	}

	public int getIDzero() {
		return IDzero;
	}

	public int getIDone() {
		return IDone;
	}

	public int getIDtwo() {
		return IDtwo;
	}
}
