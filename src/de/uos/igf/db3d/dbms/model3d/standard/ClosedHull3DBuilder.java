/*
 * Sourcecode of the Researchproject
 * "Development of Component-Software for the Internet-Based
 * Access to Geo-Database Services"
 *
 * University of Osnabrueck
 * Research Center for Geoinformatics and Remote Sensing
 *
 * Copyright (C) 2002-2005 Researchgroup Prof. Dr. Martin Breunig
 *
 * File ClosedHullBuilder.java - created on 06.06.2003
 */
package de.uos.igf.db3d.dbms.model3d.standard;

import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.api.Space3DC;
import de.uos.igf.db3d.dbms.model3d.standard.ClosedHull3D;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DBuilder;

/**
 * <p>
 * 
 * 
 * <br>
 * <br>
 * </p>
 * Revision:<br>
 * 
 */
public class ClosedHull3DBuilder extends TriangleNet3DBuilder {

	/**
	 * Constructor.<br>
	 * The space is needed to retrieve the space constraints which also are
	 * valid for the enclosed objects build with that class.
	 * 
	 * @param space
	 *            - enclosing space for triangle net
	 */
	public ClosedHull3DBuilder(Space3DC space) {
		super(space);
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param workspace
	 *            - enclosing Workspace for Triangle net
	 * @param sop
	 *            - ScalarOperator for this object in the workspace
	 */
	public ClosedHull3DBuilder(ScalarOperator sop) {
		super(sop);
	}

	/**
	 * Builds and returns the TriangleNet3D object with the previously added
	 * TriangleNet3DComp objects.<br>
	 * The method returns null if no component was added.
	 * 
	 * @return TriangleNet3D
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public ClosedHull3D getClosedHull() {
		return new ClosedHull3D(super.getTriangleNet());
	}

}
