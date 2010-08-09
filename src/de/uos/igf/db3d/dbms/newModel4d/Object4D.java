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

/**
 * This class represents an 4D object. The user has to call the function
 * addTimestep() to add the Point information for the PointTubes this Object4D
 * is working with. After that the user has to call the function addGeometry()
 * to add the geometry informations for the last added timestep. This procedure
 * must be done for every timestep with a changing net topology.
 * 
 * @author Paul Vincent Kuper (pkuper@uni-osnabrueck.de)
 */
public class Object4D {

	// The pointTubes of this 4D Object
	// <ID, <Zeitschritt, Point3D>>
	private Map<Integer, Map<Integer, Point3D>> pointTubes;

	// contains the spatial of this object
	// spatial objects only consists of the ID information of its Point3D
	// elements
	// every SpatialObject4D object has its own timeinterval
	private List<SpatialObject4D> geometry;

	// List of timesteps with their effective date
	private LinkedList<Date> timesteps;

	private int postobjectCNT;

	/**
	 * Constructor
	 * 
	 */
	public Object4D() {
		super();
		pointTubes = new HashMap<Integer, Map<Integer, Point3D>>();
		timesteps = new LinkedList<Date>();
		postobjectCNT = 0;
	}

	/**
	 * Add one new timestep. This function will only add the Points for the
	 * PointTubes at this timestep. You need to add the geometry for this
	 * timestep after you called this function.
	 * 
	 * @param newPoints
	 * @param date
	 */
	public void addTimestep(HashMap<Integer, Point3D> newPoints, Date date) {

		// check if it is the first timestep
		if (timesteps.isEmpty()) {

			// initialize the geometry vector:
			geometry = new Vector<SpatialObject4D>();

			// first object, geometry informations are needed, increment counter
			postobjectCNT++;

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

			// Now we need to know if the user already added the geometry for
			// the last Postobject (can be whether the first object or the
			// Postobject of a new interval)
			if (postobjectCNT != geometry.size())
				throw new IllegalArgumentException(
						"The net topology changed. You need to add the geometrydata for the last Postobject first. Call the addGeometry() function.");

			// this is the Polthier und Rumpf model
			// do we have a change of topology?
			// check if this is an Post-object!
			if (timesteps.getLast().equals(date)) {

				timesteps.add(date);

				// new Postobject, increment counter
				postobjectCNT++;

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

	/**
	 * Function to add the information of the geometry for an object.
	 * 
	 * We check if the geometry will be added at the right place of the geometry
	 * List.
	 * 
	 * @param spatial
	 *            - the spatial information for the last added timestep.
	 */
	public void addGeometry(SpatialObject4D spatial) {
		if (postobjectCNT == geometry.size() + 1)
			geometry.add(spatial);
		else
			throw new IllegalArgumentException(
					"You can not add the geometry. You already have the geometry information for the actual step.");
	}

	/**
	 * This function creates a Map of Point3D objects which contains the
	 * information of the location of the Points at the specified date with the
	 * help of linear interpolation.
	 * 
	 * @return Map - contains the Point3D objects and their IDs at the specified date
	 */
	public Map<Integer, Point3D> getPointTubesAtInstance(Date date) {
		
		HashMap<Integer, Point3D> points = new HashMap<Integer, Point3D>();
		
		// TODO: Methode auslagern und so aufbauen: 
		// Interpoliert wird zwischen dem interval was gerade aktuell ist. Date anschauen und so... 
		// am besten mal in das alte 4D Ding gucken.
		
		return points;
	}

	public Map<Integer, Map<Integer, Point3D>> getPointTubes() {
		return pointTubes;
	}

	public List<SpatialObject4D> getGeometry() {
		return geometry;
	}

	public LinkedList<Date> getTimesteps() {
		return timesteps;
	}

	public int getPostobjectCNT() {
		return postobjectCNT;
	}
}
