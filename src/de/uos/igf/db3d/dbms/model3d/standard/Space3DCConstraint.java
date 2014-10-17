/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.model3d.standard;

import de.uos.igf.db3d.dbms.model3d.api.Object3DC;
import de.uos.igf.db3d.dbms.model3d.api.Space3DC;

/**
 * Interface for a SpaceConstraint for a Space3D.<br>
 * If a new Object is inserted into the Space3D an instance of a subclass of
 * this SpaceConstraint class gets called with the new Object and the Space3D
 * for validation.
 */
public abstract class Space3DCConstraint {

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
	public abstract boolean validateConstraint(Space3DC space, Object3DC newObject);

}
