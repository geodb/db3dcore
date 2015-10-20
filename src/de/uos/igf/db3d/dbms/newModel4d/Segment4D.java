package de.uos.igf.db3d.dbms.newModel4d;

import de.uos.igf.db3d.dbms.geom.Segment3D;

/**
 * This class represents a 4D segment object.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class Segment4D implements Element4D {	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + IDend + IDstart;
		result = prime * result;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment4D other = (Segment4D) obj;
		if (IDend != other.IDend && IDend !=other.IDstart)
			return false;
		if (IDstart != other.IDstart && IDstart !=other.IDend)
			return false;
		return true;	
	}

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

	public void setID(int ID) {
		this.ID = ID;
	}

	@Override
	public String toString() {
		return "Segment4D [IDstart=" + IDstart + ", IDend=" + IDend + ", ID="
				+ ID + "]";
	}
}
