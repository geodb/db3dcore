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
 * File SpaceConstraint.java - created on 14.07.2003
 */
package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.structure.Space3D;

/**
 * Interface for a SpaceConstraint for a Space3D.<br>
 * If a new Object is inserted into the Space3D an instance of a subclass of
 * this SpaceConstraint class gets called with the new Object and the Space3D
 * for validation.
 */
public abstract class SpaceConstraint implements PersistentObject {

	/**
	 * Validation method.<br>
	 * Gets called with the Space3D object and the new Object3D.<br>
	 * Subclasses implementing this method are responsible for validating the
	 * new object against the constraint.
	 * 
	 * @param space
	 *            Space3D
	 * @param newObject
	 *            Object3D
	 * @return boolean - true if validation successful.
	 */
	public abstract boolean validateConstraint(Space3D space, Object3D newObject);

}
