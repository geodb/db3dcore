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

	/* ID of segments */
	private int IDseg1;
	private int IDseg2;
	private int IDseg3;

	/* id of this - unique in whole net */
	private int ID;

	/**
	 * Constructor. Constructs a Triangle4D as a Triangle4D with given pointTube
	 * IDs.
	 * 
	 * @param IDs
	 *            PointTube ID array.
	 */
	public Triangle4D(int[] IDs, Integer ID) throws IllegalArgumentException {

		this.IDzero = IDs[0];
		this.IDone = IDs[1];
		this.IDtwo = IDs[2];

		this.ID = ID;
	}

	/**
	 * Constructor. Constructs a Triangle4D with given pointTube IDs.
	 * 
	 * @param pointID1
	 *            PointTube ID 1
	 * @param pointID2
	 *            PointTube ID 2
	 * @param pointID3
	 *            PointTube ID 3
	 * @param ID
	 *            - ID of the Triangle
	 */
	public Triangle4D(int pointID1, int pointID2, int pointID3, Integer ID) {
		this(new int[] { pointID1, pointID2, pointID3 }, ID);
	}

	// Getter methods for the fields:

	public int getID() {
		return ID;
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

	public void setSegments(int [] segmentIDs) {
		this.IDseg1 = segmentIDs[0];
		this.IDseg2 = segmentIDs[1];
		this.IDseg3 = segmentIDs[2];
	}

	public int[] getSegments() {
		return new int[] { IDseg1, IDseg2, IDseg3 };
	}
}
