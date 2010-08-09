package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;

import de.uos.igf.db3d.dbms.model3d.Object3D;
import de.uos.igf.db3d.dbms.model3d.Object3DBuilder;

public class ServicesFor4DObjects {

	/**
	 * The 4D-to-3D Service
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the
	 * specified time
	 * 
	 * @param date
	 *            - the effective date for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the
	 *         specified date
	 */
	public Object3D getInstanceAt(Object4D object, Date date) {

		int indexOfGeometry = 0;

		// check if the specified date is not in the timeinterval:
		if (!((object.getTimesteps().getFirst().before(date) && object
				.getTimesteps().getLast().after(date)) || object.getTimesteps()
				.contains(date)))
			return null;
		else {

			// check if this is an object with different net topologies.
			if (!(object.getPostobjectCNT() == 1)) {

				// if there are different topologies, find out which is the one
				// for this specified date:
				Date lastDate = object.getTimesteps().get(0);
				Date actualDate = object.getTimesteps().get(1);
				int cnt = 2;
				while (actualDate.before(date)) {
					if (actualDate == lastDate)
						indexOfGeometry++;
					lastDate = actualDate;
					actualDate = object.getTimesteps().get(cnt);
					cnt++;
				}
			}

			// now we have the index of the right topology for this date.
			
			// create a Object3DBuilder with the right ScalarOperator
			Object3DBuilder builder = new Object3DBuilder(object.getGeometry().get(indexOfGeometry).getScalarOperator());

			builder.getObject3D();
			
			
			// TODO: Anschauen wie das gemacht wird und wo der SpatialKrams herkommt, den man so benutzt. 
//			builder.setSpatialPart(spatial);
			
			// Stuetzpunkte mit geometrie verknüpfen und was feines aufbauen!
			object.getPointTubesAtInstance(date);
			
			return builder.getObject3D();
		}
	}

	/**
	 * The 4D-to-3D Service
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the
	 * specified timestep
	 * 
	 * @param timestep
	 *            - the timestep for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the
	 *         specified timestep
	 */
	public Object3D getInstanceAt(Object4D object, Integer timestep) {

		// TODO

		return null;

	}
}
