/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d.standard;

import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.api.SRID;

/**
 * The SpaceDefinition class holds all definitions and restrictions for the
 * Space3D from a mathematical viewpoint.
 */
public class Space3DCDefinition {

	/* ScalarOperator for epsilon arithmetic */
	private ScalarOperator sop;

	/* spatial reference system as defined of OGC SFS */
	private int srid;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            for epsilon arithmetic
	 * @param srid
	 *            spatial reference system id
	 */
	public Space3DCDefinition(double epsilon, int srid) {
		this.sop = new ScalarOperator(epsilon);
		this.srid = srid;
	}

	/**
	 * Returns the ScalarOperator for this Space3D.
	 * 
	 * @return ScalarOperator
	 */
	public ScalarOperator getScalarOperator() {
		return this.sop;
	}

	/**
	 * Returns the SRID of this Space3D.
	 * 
	 * @return int - SRID
	 */
	public int getSRID() {
		return this.srid;
	}

	/**
	 * Returns name for SRID code.
	 * 
	 * @return int - SRID
	 */
	public String getSridDescription() {
		return SRID.getSridDescription(this.srid);
	}

}
