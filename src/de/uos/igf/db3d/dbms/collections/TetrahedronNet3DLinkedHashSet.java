package de.uos.igf.db3d.dbms.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard3d.Tetrahedron3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Tetrahedron3DComponent;
import de.uos.igf.db3d.dbms.spatials.standard3d.Tetrahedron3DElement;

/**
 * Some services for Tetrahedrons you dont want to miss. 1) initForPointClouds()
 * - Creates some HashMaps for a better Handling of Points from a
 * TetrahedronNet: The Map "points" is a Map of unique Points with unique IDs.
 * The Map "tetrahedrons" is a Map of TetrahedronIDs with an Integer Array of 3
 * PointIDs. The Map "pointIDs" is a Map to access the Point IDs via Point
 * objects. We need it to fill the Integer Array of Tetrahedrons.
 * 
 * You can use this methods for Import/Export functions to sort out duplicate
 * points.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TetrahedronNet3DLinkedHashSet {

	// ID + Point3D
	// The Map "points" is a Map of unique Points with unique IDs.
	HashMap<Integer, Point3D> points;

	// Point3D + ID
	// The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	// need it to fill the Integer Array of Tetrahedrons.
	HashMap<Point3D, Integer> pointIDs;

	// Tetrahedron ID + 3 Point3D IDs
	// The Map "tetrahedrons" is a Map of TetrahedronIDs with an Integer Array
	// of 4
	// PointIDs.
	HashMap<Integer, int[]> tetrahedrons;

	// This Map contains all componentIDs accessible by their tetrahedronID
	// (tetrahedronID, componentID)
	HashMap<Integer, Integer> components;

	/**
	 * Constructor creates Maps and calls init function.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public TetrahedronNet3DLinkedHashSet() {
		points = new HashMap<Integer, Point3D>();
		pointIDs = new HashMap<Point3D, Integer>();
		tetrahedrons = new HashMap<Integer, int[]>();
		components = new HashMap<Integer, Integer>();
	}

	/**
	 * The initial method to create the Maps for our services.
	 * 
	 * @param tetNet
	 *            - A Triangle Net
	 */
	public void initForPointClouds(Tetrahedron3DNet tetNet) {

		Tetrahedron3DComponent[] comp = tetNet.getComponents();
		Tetrahedron3DElement tetrahedron;

		int id = 0;

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (Tetrahedron3DComponent tetComp : comp) {

			Set s = tetComp.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();

			while (it.hasNext()) {

				tetrahedron = (Tetrahedron3DElement) it.next();

				int[] pointsForTetrahedrons = new int[4];

				for (int i = 0; i < 4; i++) {

					if (!unique.contains(tetrahedron.getPoints()[i])) {
						unique.add(tetrahedron.getPoints()[i]);
						points.put(id, tetrahedron.getPoints()[i]);
						pointIDs.put(tetrahedron.getPoints()[i], id);
						pointsForTetrahedrons[i] = id;
						id++;
					} else {
						pointsForTetrahedrons[i] = pointIDs.get(tetrahedron
								.getPoints()[i]);
					}
				}
				tetrahedrons.put(tetrahedron.getID(), pointsForTetrahedrons);
				components.put(tetrahedron.getID(), tetComp.getID());
			}
		}
	}

	/**
	 * The Map "points" is a Map of unique Points with unique IDs. Starts at 0.
	 * 
	 * @return the points
	 */
	public HashMap<Integer, Point3D> getPoints() {
		return points;
	}

	/**
	 * The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	 * need it to fill the Integer Array of Triangles.
	 * 
	 * @return the pointIDs
	 */
	public HashMap<Point3D, Integer> getPointIDs() {
		return pointIDs;
	}

	/**
	 * The Map "tetrahedrons" is a Map of TetrahedronIDs with an Integer Array
	 * of 4 PointIDs.
	 * 
	 * @return the triangles
	 */
	public HashMap<Integer, int[]> getTetrahedrons() {
		return tetrahedrons;
	}

	/**
	 * This Map contains all componentIDs accessible by their tetrahedronID
	 * (tetrahedronID, componentID)
	 * 
	 * @return the components
	 */
	public HashMap<Integer, Integer> getComponents() {
		return components;
	}

}
