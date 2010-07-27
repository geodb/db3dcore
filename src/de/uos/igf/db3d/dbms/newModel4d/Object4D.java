package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.uos.igf.db3d.dbms.geom.Point3D;

public class Object4D {

	// The pointTubes of this 4D Object
	// <ID, <Zeitschritt, Point3D>>
	Map<Integer, Map<Integer, Point3D>> pointTubes;
	
	// contains the spatial of this object 
	// spatial objects only consists of the ID information of its Point3D elements
	// every SpatialObject4D object has its own timeinterval
	List<SpatialObject4D> geometry;

	// List of timesteps with their effective date
	LinkedList<Date> timesteps;

	public Object4D() {
		super();
		pointTubes = new HashMap<Integer, Map<Integer, Point3D>>();
		timesteps = new LinkedList<Date>();
		geometry = new Vector<SpatialObject4D>();
	}

	public void addTimestep(HashMap<Integer, Point3D> newPoints, Date date) {

		// check if it is the first timestep
		if (timesteps.isEmpty()) {

			// add the effective date as first timestep
			timesteps.add(date);

			Iterator<Integer> it = newPoints.keySet().iterator();

			// add all Points with their ID to the pointTube Map
			while (it.hasNext()) {

				Integer id = it.next();

				// It is the initial step, so we have to create a new HashMap
				// for
				// every Point.
				HashMap<Integer, Point3D> newTube = new HashMap<Integer, Point3D>();
				newTube.put(0, newPoints.get(id));

				pointTubes.put(id, newTube);
			}

			// if there are already some timesteps in the Map, check if the new
			// timestep is higher than the last one
		} else if (!date.before(timesteps.getLast())) {

			// this is the Polthier und Rumpf model
			// do we have a change of topology?
			// check if this is an Post-object!
			if (timesteps.getLast().equals(date)) {

				timesteps.add(date);

				Iterator<Integer> it = newPoints.keySet().iterator();

				// add all Points with their ID to the pointTube Map
				while (it.hasNext()) {

					// TODO: Code for the Deltaspeicherung:

					Integer id = it.next();

					// It is the initial step of a new interval defined by the
					// Postobject and the next Preobject/Lastobject, so we have
					// to create a new HashMap for every not already existing
					// Point.
					if (!pointTubes.containsKey(id)) {
						HashMap<Integer, Point3D> newTube = new HashMap<Integer, Point3D>();
						newTube.put(timesteps.size(), newPoints.get(id));

						pointTubes.put(id, newTube);
					} else {
						// If the id already exists in the PointTubes extend its
						// timeinterval by the new point.
						pointTubes.get(id).put(timesteps.size(),
								newPoints.get(id));
					}
				}

				// no Post-object, should be the same topology
			} else {

				// check if the new step has the same ids of points as the last
				// step:
				Iterator<Integer> it = newPoints.keySet().iterator();

				while (it.hasNext()) {
					if (pointTubes.get(it.next()).get(timesteps.size()) == null)
						throw new IllegalArgumentException(
								"New Object is neither a Postobject nor it fits the size of the last object");

				}

				// are there more points at the last timestep than in the new
				// one?
				Set<Integer> ids = pointTubes.keySet();
				int cnt = 0;
				for (final Integer id : ids) {
					if (pointTubes.get(id).get(timesteps.size()) != null)
						cnt++;
				}
				if (cnt != newPoints.size())
					throw new IllegalArgumentException(
							"New Object is neither a Postobject nor it fits the size of the last object");

				// everything is alright? Add the new Points!
				timesteps.add(date);

				it = newPoints.keySet().iterator();

				// add all Points with their ID to the pointTube Map
				while (it.hasNext()) {

					// TODO: Code for the Deltaspeicherung:

					Integer id = it.next();

					// we know that we extend our pointTubes without building
					// new one, so lets do so:
					pointTubes.get(id).put(timesteps.size(), newPoints.get(id));
				}
			}
		}
	}
	
	public void addGeometry(SpatialObject4D spatial) {
		geometry.add(spatial);
	}
}
