package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.api.Net3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

/**
 * Net3DAbst is the abstract superclass of all geometric net objects in 3D.
 * 
 * @author Markus Jahn
 * 
 */
public abstract class Net3DAbst extends Spatial3DAbst implements Net3D {

	/**
	 * Constructor.
	 */
	public Net3DAbst(GeoEpsilon epsilon) {
		super(epsilon);
	}

	public static class HoldNeighbourStructure {

		/* first object */
		private Element3D object0;

		/* second object */
		private Element3D object1;

		/* first objects index for neighbourhood to second object */
		private int index0;

		/* second objects index for neighbourhood to first object */
		private int index1;

		/**
		 * Default Constructor.<br>
		 * Sets the objects to <code>null</code> and the indexes to -1.
		 */
		public HoldNeighbourStructure() {
			this.object0 = null;
			this.object1 = null;
			this.index0 = -1;
			this.index1 = -1;
		}

		public HoldNeighbourStructure(Element3D obj0, int ind0, Element3D obj1,
				int ind1) {
			this.object0 = obj0;
			this.index0 = ind0;
			this.object1 = obj1;
			this.index1 = ind1;
		}

		public void setParameters(Element3D obj0, int ind0, Element3D obj1,
				int ind1) {
			this.object0 = obj0;
			this.index0 = ind0;
			this.object1 = obj1;
			this.index1 = ind1;
		}

		public Element3D getObject(int index) {
			if (index == 0)
				return this.object0;
			if (index == 1)
				return this.object1;
			return null;
		}

		public int getIndex(int index) {
			if (index == 0)
				return this.index0;
			if (index == 1)
				return this.index1;
			return -1;
		}
	}

}
