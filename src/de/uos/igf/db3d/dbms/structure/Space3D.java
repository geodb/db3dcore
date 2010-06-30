/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.structure;

import java.util.List;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.Object3D;
import de.uos.igf.db3d.dbms.model3d.Object3DIterator;
import de.uos.igf.db3d.dbms.model3d.SpaceConstraint;
import de.uos.igf.db3d.dbms.model3d.SpaceDefinition;
import de.uos.igf.db3d.dbms.model3d.Spatial3DIterator;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * Space3D is the entry point to the 3D objects stored in a project of this
 * DBMS.<br>
 * <br>
 * Space3D defines the housekeeping methods for a storage object. The indexing
 * and search part is covered by the interfaces Space3D.ThematicPart and
 * Space3D.SpatialPart.
 */
public interface Space3D {

	/**
	 * Space3D.SpatialPart interface provides methods for retrieving the stored
	 * spatial objects in different ways by their spatial nature. <br>
	 */
	public interface SpatialPart {
		/**
		 * Returns the number of indexed objects.
		 * 
		 * @return int - number of objects.
		 */
		public int getCount();

		/**
		 * Returns the complete MBB3D of all objects in the spatial part.
		 * 
		 * @return MBB3D of all objects in the spatial part.
		 */
		public MBB3D getMBB();

		/**
		 * Returns the set of objects which intersect the given MBB3D.
		 * 
		 * @param mbb
		 *            the MBB3D object for test
		 * @return Set - a Set object containing the resul
		 */
		public Set intersects(MBB3D mbb);

		/**
		 * Returns the set of objects which contain the given MBB3D.
		 * 
		 * @param mbb
		 *            the MBB3D object for test
		 * @return Set - a Set object containing the result.
		 */
		public Set contains(MBB3D mbb);

		/**
		 *Returns the set of objects which are inside the given MBB3D.
		 * 
		 * @param mbb
		 *            the MBB3D object for test
		 * @return Set - a Set object containing the result.
		 */
		public Set inside(MBB3D mbb);

		/**
		 * Returns the set of objects which contain the given point.
		 * 
		 * @param point
		 *            the Point3D object for test
		 * @return Set - a Set object containing the result.
		 */
		public Set contains(Point3D point);

		/**
		 * Returns the set of the <code>number</code> objects which are the
		 * nearest neighbours of the given point
		 * 
		 * @param number
		 *            number of nearest neighbours to search
		 * @param point
		 *            the Point3D object for test
		 * @return NNResult[] - an array of NNResult objects containing the
		 *         result.
		 */
		public SAM.NNResult[] nearest(int number, Point3D point);

		/**
		 * Returns an iterator for all Spatial3D objects in this Space.
		 * 
		 * @return Spatial3DIterator over Spatial3D objects in this Space.
		 */
		public Spatial3DIterator iteratorSpatial3D();

		/**
		 * Returns the Space whose part this is.
		 * 
		 * @return Space3D whose part this is.
		 */
		public Space3D getSpace();
	}

	/**
	 * Returns the number of objects in this space.
	 * 
	 * @return int - number of objects in this space.
	 */
	public int getCount();

	/**
	 * Returns the SpaceDefinition for this Space3D.
	 * 
	 * @return SpaceDefinition for this Space3D.
	 */
	public SpaceDefinition getSpaceDefinition();

	/**
	 * Returns the ScalarOperator for this Space3D.
	 * 
	 * @return ScalarOperator for this Space3D.
	 */
	public ScalarOperator getScalarOperator();

	/**
	 * Sets the description of the Space3D.
	 * 
	 * @param description
	 *            String to be set as description
	 */
	public void setDescription(String description);

	/**
	 * Returns the description of this Space3D
	 * 
	 * @return String - description.
	 */
	public String getDescription();

	/**
	 * Returns the name of this Space3D.
	 * 
	 * @return String - name of this Space3D.
	 */
	public String getName();

	/**
	 * Returns an iterator for iterating over all objects in this space.
	 * 
	 * @return Iterator over all objects in this space.
	 */
	public Object3DIterator iteratorObject3D();

	/**
	 * Returns all the objects in this space as an Object3D array.
	 * 
	 * @return Object[] - all objects as array.
	 */
	public Object3D[] getObjects();

	/**
	 * Returns all the objects in this space as List.
	 * 
	 * @return List - all objects as list.
	 */
	public List getObjectsAsList();

	/**
	 * Returns the Object3D specified by the id.
	 * 
	 * @param id
	 *            id as int
	 * @return Object3D.
	 */
	public Object3D getObjectByID(int id);

	/**
	 * Returns the Object3D specified by the name.
	 * 
	 * @param name
	 *            of the object
	 * @return Object3D.
	 */
	public Object3D getObjectByName(String n);

	// /**
	// * Adds a ThematicGroupConstraint object for given ThematicGroup.<br>
	// * If this ThematicGroup already has one constraint, it will be replaced
	// with
	// * the new one.
	// *
	// * @param group
	// * ThematicGroup
	// * @param constraint
	// * ThematicGroupConstraint
	// */
	// public void addTGConstraint(
	// ThematicGroup group,
	// ThematicGroupConstraint constraint);

	/**
	 * Adds a new SpaceConstraint to this Space3D.
	 * 
	 * @param constraint
	 *            - SpaceConstraint
	 */
	public void addSpaceConstraint(SpaceConstraint constraint);

	// /**
	// * Checks for an existing RecordDefinition in the cache of the space.<br>
	// * If one exists, it is returned. Otherwise the given test
	// RecordDefinition
	// * is activated, stored in the cache and returned.
	// *
	// * @param recDef
	// * RecordDefinition for test
	// * @return RecordDefinition.
	// */
	// public RecordDefinition getRecordDefinition(RecordDefinition recDef);

	// /**
	// * Returns the thematic part of this Space3D.
	// *
	// * @return ThematicPart
	// */
	// public ThematicPart getThematicPart();

	/**
	 * Returns the spatial part of this Space3D.
	 * 
	 * @return SpatialPart if this Space3D.
	 */
	public SpatialPart getSpatialPart();

	/**
	 * Returns the id of this space in the project.
	 * 
	 * @return int - space id of this space in the project.
	 */
	public int getSpaceID();

	// /**
	// * Returns the project of this Space3D.
	// *
	// * @return Project of this Space3D.
	// */
	// public Project getProject();

	/**
	 * Inserts the given Object3D into space.<br>
	 * The Object3D instance must not already have a OID instance associated.
	 * 
	 * @param object
	 *            Object3D to be inserted
	 * @return boolean - true if inserted, false otherwise.
	 */
	public boolean insert(Object3D object);

	/**
	 * Updates the given Object3D into space.<br>
	 * The Object3D instance must already have a OID instance associated.
	 * 
	 * @param object
	 *            Object3D to be updated
	 * @return boolean - true if updated, false otherwise.
	 */
	public boolean update(Object3D object);

	/**
	 * Returns all SpaceConstraints as Set.
	 * 
	 * @return Set of all SpaceConstraints.
	 */
	public Set getSpaceConstraints();

	/**
	 * Persistent initialization.
	 */
	public void persistentInitialization();

}
