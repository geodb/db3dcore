package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;

/**
 * This class represents an 4D object. The user has to call the function
 * addTimestep() to add the Point information for the PointTubes this Object4D
 * is working with. After that the user has to call the function addGeometry()
 * to add the geometry informations for the last added timestep. This procedure
 * must be done for every timestep with a changing net topology.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
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
	protected LinkedList<Date> timesteps;

	private ScalarOperator sop;

	/**
	 * Constructor
	 * 
	 */
	public Object4D() {
		super();
		pointTubes = new HashMap<Integer, Map<Integer, Point3D>>();
		timesteps = new LinkedList<Date>();
		sop = new ScalarOperator();
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

			firstStep(newPoints, date);

			// if there are already some timesteps in the Map, check if the new
			// timestep is higher than the last one
		} else if (!date.before(timesteps.getLast())) {

			// Now we need to know if the user already added the geometry for
			// the last Postobject (can be whether the first object or the
			// Postobject of a new interval)
			if (geometry.size() < countToplogogyChanges())
				throw new IllegalArgumentException(
						"The net topology changed. You need to add the geometrydata for the last Postobject first. Call the addGeometry() function.");

			// this is the Polthier und Rumpf model
			// do we have a change of topology?
			// check if this is an Post-object!
			if (timesteps.getLast().equals(date)) {

				polthierAndRumpf(newPoints, date);

				// no Post-object, should be the same topology
			} else {

				addTimeStep(newPoints, date);
			}
		}
	}

	/**
	 * Adds a new timestep to the Object4D object. This method checks if the new
	 * data has the same topology as the last timestep. Checks the correlation
	 * of partitions between the actual and the last timestep.
	 * 
	 * @param newPoints
	 * @param date
	 */
	private void addTimeStep(HashMap<Integer, Point3D> newPoints, Date date) {
		// check if the new step has the same ids of points as the last
		// step:
		Iterator<Integer> it = newPoints.keySet().iterator();

		while (it.hasNext()) {
			if (pointTubes.get(it.next()).get(timesteps.size() - 1) == null)
				throw new IllegalArgumentException(
						"New Object is neither a Postobject nor it fits the size of the last object");

		}

		// are there more points at the last timestep than in the new
		// one?

		// TODO: entrys nehmen
		Set<Integer> ids = pointTubes.keySet();
		int cnt = 0;
		for (final Integer id : ids) {
			if (pointTubes.get(id).get(timesteps.size() - 1) != null)
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

			Integer id = it.next();

			// Deltaspeicherung:
			if (pointTubes.get(id).get(timesteps.size() - 2)
					.isEqual(newPoints.get(id), sop)) {
				pointTubes.get(id).put(timesteps.size() - 1,
						pointTubes.get(id).get(timesteps.size() - 2));
			} else {

				// we know that we extend our pointTubes without
				// building
				// new one, so lets do so:
				pointTubes.get(id).put(timesteps.size() - 1, newPoints.get(id));
			}
		}
	}

	/**
	 * Adds a Postobject based on the concept of Polthier und Rumpf. Checks the
	 * correlation of partitions between the actual and the last timestep.
	 * 
	 * 
	 * @param newPoints
	 * @param date
	 */
	private void polthierAndRumpf(HashMap<Integer, Point3D> newPoints, Date date) {

		timesteps.add(date);

		Iterator<Integer> it = newPoints.keySet().iterator();

		HashMap<Integer, Point3D> correlation = new HashMap<Integer, Point3D>();

		// TODO: Code for the Deltaspeicherung:

		// HashSet<Point3D> oldPoints = new HashSet<Point3D>();
		//
		// Iterator<Integer> it2 = pointTubes.keySet().iterator();
		//
		// while (it2.hasNext()) {
		// oldPoints.add(pointTubes.get(it2.next()).get(timesteps.size() - 2));
		// }
		//
		// double time = System.currentTimeMillis();
		//
		// quite expansive...
		// while(it.hasNext()) {
		// int id = it.next();
		// if(oldPoints.contains(newPoints.get(id))) {
		// Iterator<Point3D> itOldPoints = oldPoints.iterator();
		// while(itOldPoints.hasNext()) {
		// Point3D tmp = itOldPoints.next();
		// if(tmp.equals(newPoints.get(id))) {
		// correlation.put(id, tmp);
		// break;
		// }
		// }
		// }
		// }

		// System.out.println();
		// System.out.println("Zeitverbrauch: " + (System.currentTimeMillis() -
		// time));
		// System.out.println();

		// System.out.println(oldPoints.size());
		// System.out.println(newPoints.size());
		// System.out.println(correlation.size());

		// TODO: End of Deltaspeicherung

		// add all Points with their ID to the pointTube Map
		// reset iterator
		it = newPoints.keySet().iterator();

		while (it.hasNext()) {

			Integer id = it.next();

			// It is the initial step of a new interval defined by the
			// Postobject and the next Preobject/Lastobject, so we have
			// to create a new HashMap for every not already existing
			// Point.
			if (!pointTubes.containsKey(id)) {

				HashMap<Integer, Point3D> newTube = new HashMap<Integer, Point3D>();

				// Deltaspeicherung:
				if (correlation.containsKey(id)) {
					newTube.put(timesteps.size() - 1, correlation.get(id));
				} else {
					newTube.put(timesteps.size() - 1, newPoints.get(id));
				}

				pointTubes.put(id, newTube);
			} else {
				// If the id already exists in the PointTubes extend its
				// timeinterval by the new point.

				// Deltaspeicherung:
				if (correlation.containsKey(id)) {
					pointTubes.get(id).put(timesteps.size() - 1,
							correlation.get(id));
				} else {
					pointTubes.get(id).put(timesteps.size() - 1,
							newPoints.get(id));
				}
			}
		}
	}

	/**
	 * Adds the first step of a Object4D object.
	 * 
	 * @param newPoints
	 * @param date
	 */
	private void firstStep(HashMap<Integer, Point3D> newPoints, Date date) {
		// initialize the geometry vector:
		geometry = new Vector<SpatialObject4D>();

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
		int changes = countToplogogyChanges();
		if (changes == geometry.size() + 1)
			geometry.add(spatial);
		// else {
		// if (!spatial.equals(geometry.get(geometry.size() - 1)))
		// throw new IllegalArgumentException(
		// "You can not add the geometry. You already have the geometry information for the actual step.");
		// }
	}

	/**
	 * This function creates a Map of Point3D objects which contains the
	 * information of the location of the Points at the specified date with the
	 * help of linear interpolation.
	 * 
	 * @return Map - contains the Point3D objects and their IDs at the specified
	 *         date
	 */
	public Map<Integer, Point3D> getPointTubesAtInstance(Date date) {

		HashMap<Integer, Point3D> points = new HashMap<Integer, Point3D>();

		// the case that the date is similar to a date of one timestep we only
		// need to get the right Points from the PointTube
		if (timesteps.contains(date)) {

			// It always returns the Pre object if this is a timestep with a
			// change of topology
			int timestep = timesteps.indexOf(date);

			// for (int id = 1; id <= pointTubes.size(); id++) {
			for (int id : pointTubes.keySet()) {

				if (pointTubes.get(id).containsKey(timestep))
					points.put(id, pointTubes.get(id).get(timestep));
			}
			return points;
			// otherwise we need to check if the date is in the interval of the
			// timesteps and interpolate all Points for this date.
		} else if (timesteps.getFirst().before(date)
				&& timesteps.getLast().after(date)) {

			// check which two dates of the timesteps build the interval of the
			// specified date
			// within this interval the topology will not change
			Date intervalStart = timesteps.get(0);
			Date intervalEnd = timesteps.get(1);
			int cnt = 2;

			while (!intervalEnd.after(date)) {
				intervalStart = intervalEnd;
				intervalEnd = timesteps.get(cnt);
				cnt++;
			}

			// Compute the factor which indicates the position of the desired
			// point. 0 corresponds to the first support point, 1 to the second.
			double factor = (double) (date.getTime() - intervalStart.getTime())
					/ (intervalEnd.getTime() - intervalStart.getTime());

			// System.out.println(date.getTime() - intervalStart.getTime());
			// System.out.println(intervalEnd.getTime() -
			// intervalStart.getTime());
			// System.out.println(factor);

			// for all Points which are active in this timeinterval we need to
			// interpolate a new point with the help of the computed factor.
			int intervalStartStep = timesteps.indexOf(intervalStart);

			Set<Integer> allIDs = new HashSet<Integer>();

			for (int id : pointTubes.keySet()) {

				if (pointTubes.get(id).containsKey(intervalStartStep + 1))
					allIDs.add(id);
			}

			Iterator<Integer> ids = allIDs.iterator();

			while (ids.hasNext()) {

				Integer id = ids.next();

				// check if this ID is active in this timeinterval
				if (pointTubes.get(id).containsKey(intervalStartStep)) {

					// get the Point of the start
					Point3D intervalStartPoint = pointTubes.get(id).get(
							intervalStartStep);

					double x = intervalStartPoint.getX();
					double y = intervalStartPoint.getY();
					double z = intervalStartPoint.getZ();

					// get the end Point
					Point3D intervalEndPoint = pointTubes.get(id).get(
							intervalStartStep + 1);

					// create a new interpolated point and add it to the point
					// Map
					points.put(id, new Point3D(x
							+ (intervalEndPoint.getX() - x) * factor, y
							+ (intervalEndPoint.getY() - y) * factor, z
							+ (intervalEndPoint.getZ() - z) * factor));
				}

			}
			// return the new Map with interpolated points.
			return points;

			// if the date is not in the closed interval of the timesteps return
			// null
		} else {
			return null;
		}
	}

	private int countToplogogyChanges() {
		int cnt = 0;
		Date oldDate = timesteps.getFirst();
		for (Date d : timesteps) {
			if (oldDate.equals(d))
				cnt++;
			oldDate = d;
		}
		return cnt;
	}

	// Getter methods

	public Map<Integer, Map<Integer, Point3D>> getPointTubes() {
		return pointTubes;
	}

	public List<SpatialObject4D> getGeometry() {
		return geometry;
	}

	public LinkedList<Date> getTimesteps() {
		return timesteps;
	}

	protected static int getIndexOfGeometry(LinkedList<Date> dateInterval,
			Date date) {

		int indexOfGeometry = 0;

		// check if there is more than one timestep:
		if (dateInterval.size() > 1) {

			// find out which is the right topology for this specified date:
			Date firstDate = dateInterval.get(0);
			Date currentDate = dateInterval.get(1);
			int cnt = 2;
			while (currentDate.before(date)) {
				if (currentDate == firstDate)
					indexOfGeometry++;
				firstDate = currentDate;
				currentDate = dateInterval.get(cnt);
				cnt++;
			}
		}

		return indexOfGeometry;

	}

}
