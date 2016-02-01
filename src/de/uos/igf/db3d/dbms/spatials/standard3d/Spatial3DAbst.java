package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.spatials.api.Spatial3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.dbms.spatials.standard.SpatialAbst;

public abstract class Spatial3DAbst extends SpatialAbst implements Spatial3D {

	public Spatial3DAbst(GeoEpsilon epsilon) {
		super(epsilon);
	}

}
