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

	/* ID of segments */
	private int IDseg1;
	private int IDseg2;
	private int IDseg3;
	private int IDseg4;
	private int IDseg5;
	private int IDseg6;

	/* ID of triangles */
	private int IDtri1;
	private int IDtri2;
	private int IDtri3;
	private int IDtri4;

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

	public int[] getSegments() {
		return new int[] { IDseg1, IDseg2, IDseg3, IDseg4, IDseg5, IDseg6 };
	}

	public int[] getTriangles() {
		return new int[] { IDtri1, IDtri2, IDtri3, IDtri4 };
	}

	public void setSegments(int[] segmentIDs) {
		this.IDseg1 = segmentIDs[0];
		this.IDseg2 = segmentIDs[1];
		this.IDseg3 = segmentIDs[2];
		this.IDseg4 = segmentIDs[3];
		this.IDseg5 = segmentIDs[4];
		this.IDseg6 = segmentIDs[5];
	}

	public void setTriangles(int[] triangleIDs) {
		this.IDtri1 = triangleIDs[0];
		this.IDtri2 = triangleIDs[1];
		this.IDtri3 = triangleIDs[2];
		this.IDtri3 = triangleIDs[3];
	}
}
