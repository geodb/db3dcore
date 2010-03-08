/*
 * Source Code of the Research Project
 * "Development of Component-Software for the Internet-Based
 * Access to Geo-Database Services"
 *
 * University of Osnabrueck
 * Research Center for Geoinformatics and Remote Sensing
 *
 * Copyright (C) 2002-2005 Research Group Prof. Dr. Martin Breunig
 *
 * File SpaceDefinition.java - created on 16.06.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.PersistentObject;

/**
 * The SpaceDefinition class holds all definitions and restrictions for the
 * Space3D from a mathematical viewpoint.
 */
public class SpaceDefinition implements PersistentObject {

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
	public SpaceDefinition(double epsilon, int srid) {
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

}
