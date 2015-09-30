package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Spatial3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.spatials.standard.SpatialAbst;

public abstract class Spatial3DAbst extends SpatialAbst implements Spatial3D {

	/* MBB of this component */
	protected MBB3D mbb;

	public Spatial3DAbst(GeoEpsilon epsilon) {
		super(epsilon);
		this.mbb = null;
	}

	@Override
	public MBB3D getMBB() {
		return this.mbb;
	}

}
