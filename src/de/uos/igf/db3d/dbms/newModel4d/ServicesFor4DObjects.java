package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;

import de.uos.igf.db3d.dbms.model3d.Object3D;

public class ServicesFor4DObjects {

	/**
	 * The 4D-to-3D Service 
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the specified time 
	 * 
	 * @param date - the effective date for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the specified date
	 */
	public Object3D getInstanceAt(Date date) {
		
		// TODO
		
		return null;
		
	}
	
	/**
	 * The 4D-to-3D Service 
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the specified timestep 
	 * 
	 * @param timestep - the timestep for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the specified timestep
	 */
	public Object3D getInstanceAt(Integer timestep) {
		
		// TODO
		
		return null;
		
	}	
}
