package de.uos.igf.db3d.dbms.spatials.standard;

import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.api.Spatial;

/**
 * Implementation of Spatial
 * 
 * @author Markus Jahn
 * 
 */
public abstract class SpatialAbst implements Spatial {

	/* GeoEpsilon */
	protected GeoEpsilon epsilon;

	public SpatialAbst(GeoEpsilon epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * Returns the GeoEpsilon of this.
	 * 
	 * @return epsilon - the geometric error of this.
	 * @see db3d.dbms.model.api.Spatial#getGeoEpsilon()
	 */
	public GeoEpsilon getGeoEpsilon() {
		return epsilon;
	}

	@Override
	public boolean isEqual(Equivalentable obj, GeoEpsilon sop) {
		throw new NotYetImplementedException();
	}

	@Override
	public int isEqualHC(int factor) {
		throw new NotYetImplementedException();
	}

	@Override
	public boolean isGeometryEquivalent(Equivalentable obj, GeoEpsilon sop) {
		throw new NotYetImplementedException();
	}

	@Override
	public int isGeometryEquivalentHC(int factor) {
		throw new NotYetImplementedException();
	}
}
