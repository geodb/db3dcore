package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.ComplexGeoObj;
import de.uos.igf.db3d.dbms.util.TetrahedronServices;
import de.uos.igf.db3d.dbms.util.TriangleServices;
import de.uos.igf.db3d.resources.DB3DLogger;

/**
 * This class handles the pointTube logic, when a new time-step is added to a 4D
 * component.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TimeStepBuilder {

	private static ScalarOperator sop = new ScalarOperator();

	/**
	 * Add one new timestep. This function will only add the Points for the
	 * PointTubes at this timestep. You need to add the geometry for this
	 * timestep after you called this function.
	 * 
	 * TODO: Constraints beachten!
	 * 
	 * @param newPoints
	 * @param date
	 */
	public static void addTimestep(Component4D component,
			Map<Integer, Point3D> newPoints, Date date) {

		// check if it is the first timestep
		if (component.getTimesteps().isEmpty()) {

			firstStep(component, newPoints, date);

			// if there are already some timesteps in the Map, check if the new
			// timestep is higher than the last one

		} else if (!date.before(component.getTimesteps().getLast())) {

			// Now we need to know if the user already added the geometry for
			// the last Postobject (can be whether the first object or the
			// Postobject of a new interval)

			// We do not need this anymore! One component is always in one time
			// interval
			// if (geometry.size() < countToplogogyChanges(component))
			// throw new IllegalArgumentException(
			// "The net topology changed. You need to add the geometrydata for the last Postobject first. Call the addGeometry() function.");

			// TODO: Das muss irgendwie raus... P+R wird jetzt durch die
			// Komponenten geregelt!

			// this is the Polthier und Rumpf model
			// do we have a change of topology?
			// check if this is an Post-object!
			if (component.getTimesteps().getLast().equals(date)) {

				// TODO Wird hier nicht funktionieren, da wir neue Componenten
				// haben fuer die P+R Geschichte!
				// Also: Dead Code!!! Wahrscheinlich
				DB3DLogger.logger
						.log(Level.INFO,
								"Polthier und Rumpf! Should not be called (TimeStepBuilder.java)");

				polthierAndRumpf(component, newPoints, date);

				// Notify corresponding net
				// TODO: vorher oder hier?
				// Bei dem GOCAD Importer wird das quasi auch gemacht! Checken!

				
				// no Post-object, should be the same topology
			} else {

				addNormalTimeStep(component, newPoints, date);
			}
		}
	}

	/**
	 * Adds a new timestep to the component object. This method checks if the
	 * new data has the same topology as the last timestep. Checks the
	 * correlation of partitions between the current and the last timestep.
	 * 
	 * @param newPoints
	 * @param date
	 */
	private static void addNormalTimeStep(Component4D component,
			Map<Integer, Point3D> newPoints, Date date) {

		// check if the new step has the same ids of points as the last
		// step:
		Iterator<Integer> it = newPoints.keySet().iterator();

		Map<Integer, List<Point3D>> pointTubes = component.getPointTubes();

		LinkedList<Date> timesteps = component.getTimesteps();

		while (it.hasNext()) {
			if (pointTubes.get(it.next()).get(timesteps.size() - 1) == null)
				throw new IllegalArgumentException(
						"New Object is neither a Postobject nor it fits the size of the last object");

		}

		// are there more points at the last timestep than in the new
		// one?

		// TODO: entrys nehmen oder einfach irgendwelche groessen der container
		// klassen!
		Set<Integer> ids = pointTubes.keySet();
		int cnt = 0;
		for (final Integer id : ids) {
			if (pointTubes.get(id).get(timesteps.size() - 1) != null)
				cnt++;
		}

		if (cnt != newPoints.size())
			throw new IllegalArgumentException(
					"New Object is neither a Postobject nor it fits the size of the last object. Number of points: OLD: " + newPoints.size() + " New: " + cnt);

		// everything is alright? Add the new Points!
		timesteps.add(date);

		// add all Points with their ID to the pointTube Map

		for (Integer id : newPoints.keySet()) {

			// Deltaspeicherung:
			if (pointTubes.get(id).get(timesteps.size() - 2)
					.isEqual(newPoints.get(id), sop)) {
				pointTubes.get(id).add(
						pointTubes.get(id).get(timesteps.size() - 2));
			} else {

				// we know that we extend our pointTubes without
				// building
				// new one, so lets do so:
				pointTubes.get(id).add(newPoints.get(id));
			}
		}
	}

	/**
	 * Adds a Postobject based on the concept of Polthier und Rumpf. Checks the
	 * correlation of partitions between the current and the last timestep.
	 * 
	 * TODO: NO NEED FOR THIS FUNCTION ANYMORE?
	 * 
	 * @param newPoints
	 * @param date
	 */
	private static void polthierAndRumpf(Component4D component,
			Map<Integer, Point3D> newPoints, Date date) {

		Map<Integer, List<Point3D>> pointTubes = component.getPointTubes();

		LinkedList<Date> timesteps = component.getTimesteps();

		timesteps.add(date);

		Iterator<Integer> it = newPoints.keySet().iterator();

		Map<Integer, Point3D> correlation = new TreeMap<Integer, Point3D>();

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

				List<Point3D> newTube = new LinkedList<Point3D>();

				// Deltaspeicherung:
				if (correlation.containsKey(id)) {
					newTube.add(correlation.get(id));
				} else {
					newTube.add(newPoints.get(id));
				}

				pointTubes.put(id, newTube);
			} else {
				// If the id already exists in the PointTubes extend its
				// timeinterval by the new point.

				// Deltaspeicherung:
				if (correlation.containsKey(id)) {
					pointTubes.get(id).add(correlation.get(id));
				} else {
					pointTubes.get(id).add(newPoints.get(id));
				}
			}
		}

		// Boundary Elements
		if (component.getNet().getType() == ComplexGeoObj.TRIANGLE_NET_4D) {
			TriangleNet4D net = (TriangleNet4D) component.getNet();

			if (net.isBoundaryElements()) {
				TriangleServices services = new TriangleServices();
				services.createBoundaryElements(net, newPoints, date);
			}
		}

		// Boundary Elements for Tetrahedrons
		if (component.getNet().getType() == ComplexGeoObj.TETRAHEDRON_NET_4D) {
			TetrahedronNet4D net = (TetrahedronNet4D) component.getNet();

			if (net.isBoundaryElements()) {
				TetrahedronServices services = new TetrahedronServices();
				services.createBoundaryElements(net, newPoints, date);
			}
		}
	}

	/**
	 * Adds the first step of a Object4D object.
	 * 
	 * @param newPoints
	 * @param date
	 */
	private static void firstStep(Component4D component,
			Map<Integer, Point3D> newPoints, Date date) {

		Map<Integer, List<Point3D>> pointTubes = component.getPointTubes();

		LinkedList<Date> timesteps = component.getTimesteps();

		// add the effective date as first timestep to the component and net
		timesteps.add(date);

		Iterator<Integer> it = newPoints.keySet().iterator();

		// add all Points with their ID to the pointTube Map
		while (it.hasNext()) {

			Integer id = it.next();

			// It is the initial step, so we have to create a new HashMap
			// for every Point.
			List<Point3D> newTube = new LinkedList<Point3D>();
			newTube.add(newPoints.get(id));

			pointTubes.put(id, newTube);
		}

		// Boundary Elements
		if (component.getNet().getType() == ComplexGeoObj.TRIANGLE_NET_4D) {
			TriangleNet4D net = (TriangleNet4D) component.getNet();

			if (net.isBoundaryElements()) {
				TriangleServices services = new TriangleServices();
				services.createBoundaryElements(net, newPoints, date);
			}
		}

		// Boundary Elements for Tetrahedrons
		if (component.getNet().getType() == ComplexGeoObj.TETRAHEDRON_NET_4D) {
			TetrahedronNet4D net = (TetrahedronNet4D) component.getNet();

			if (net.isBoundaryElements()) {
				TetrahedronServices services = new TetrahedronServices();
				services.createBoundaryElements(net, newPoints, date);
			}
		}
	}

	/**
	 * This function creates a Map of Point3D objects which contains the
	 * information of the location of the Points at the specified date with the
	 * help of linear interpolation.
	 * 
	 * @return Map - contains the Point3D objects and their IDs at the specified
	 *         date
	 */
	public static Map<Integer, Point3D> getPointTubesAtInstance(
			Component4D component, Date date) {

		Map<Integer, List<Point3D>> pointTubes = component.getPointTubes();

		LinkedList<Date> timesteps = component.getTimesteps();

		Map<Integer, Point3D> points = new TreeMap<Integer, Point3D>();

		// the case that the date is similar to a date of one timestep we only
		// need to get the right Points from the PointTube
		if (timesteps.contains(date)) {

			// It always returns the Pre object if this is a timestep with a
			// change of topology
			int timestep = timesteps.indexOf(date);

			// for (int id = 1; id <= pointTubes.size(); id++) {
			for (int id : pointTubes.keySet()) {

				if (pointTubes.get(id).size() >= timestep)
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

				if (pointTubes.get(id).size() >= intervalStartStep + 1)
					allIDs.add(id);
			}

			for (Integer id : allIDs) {

				// check if this ID is active in this timeinterval
				if (pointTubes.get(id).size() >= intervalStartStep) {

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
}
