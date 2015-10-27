package de.uos.igf.db3d.dbms.newModel4d;

/**
 * This class represents a 4D Tetrahedron object.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class Tetrahedron4D implements Element4D {

	/* ID of the points */
	private int IDzero;
	private int IDone;
	private int IDtwo;
	private int IDthree;

	/* id of this - unique in whole net */
	private int id;

	/**
	 * Constructor. Constructs a Tetrahedron4D with given pointTube IDs.
	 * 
	 * @param pointID1
	 *            PointTube ID 1
	 * @param pointID2
	 *            PointTube ID 2
	 * @param pointID3
	 *            PointTube ID 3
	 * @param pointID4
	 *            PointTube ID 4
	 * @param ID
	 *            - ID of the Tetrahedron
	 */
	public Tetrahedron4D(int pointID1, int pointID2, int pointID3,
			int pointID4, Integer ID) {
		this(new int[] { pointID1, pointID2, pointID3, pointID4 }, ID);
	}

	/**
	 * Constructor. Constructs a Tetrahedron4D with given pointTube IDs.
	 * 
	 * @param IDs
	 *            PointTube ID array.
	 */
	public Tetrahedron4D(int[] IDs, Integer ID) throws IllegalArgumentException {

		this.IDzero = IDs[0];
		this.IDone = IDs[1];
		this.IDtwo = IDs[2];
		this.IDthree = IDs[3];

		this.id = ID;
	}

	// Getter methods for the fields:

	public int getID() {
		return id;
	}

	public int getIDone() {
		return IDone;
	}

	public int getIDthree() {
		return IDthree;
	}

	public int getIDtwo() {
		return IDtwo;
	}
	
	public int getIDzero() {
		return IDzero;
	}
}
