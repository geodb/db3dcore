package de.uos.igf.db3d.dbms.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.model3d.standard.TetrahedronNet3D;
import de.uos.igf.db3d.dbms.model3d.standard.TetrahedronNet3DComponent;
import de.uos.igf.db3d.dbms.model3d.standard.TetrahedronNet3DElement;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DComponent;
import de.uos.igf.db3d.dbms.model3d.standard.TriangleNet3DElement;

/**
 * Some services for Tetrahedrons you dont want to miss. 1) initForPointClouds() -
 * Creates some HashMaps for a better Handling of Points from a TetrahedronNet: The
 * Map "points" is a Map of unique Points with unique IDs. The Map "tetrahedrons"
 * is a Map of TetrahedronIDs with an Integer Array of 3 PointIDs. The Map
 * "pointIDs" is a Map to access the Point IDs via Point objects. We need it to
 * fill the Integer Array of Tetrahedrons.
 * 
 * You can use this methods for Import/Export functions to sort out duplicate
 * points.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TetrahedronServices {

	// ID + Point3D
	// The Map "points" is a Map of unique Points with unique IDs.
	HashMap<Integer, Point3D> points;

	// Point3D + ID
	// The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	// need it to fill the Integer Array of Tetrahedrons.
	HashMap<Point3D, Integer> pointIDs;

	// Tetrahedron ID + 3 Point3D IDs
	// The Map "tetrahedrons" is a Map of TetrahedronIDs with an Integer Array of 4
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
	public TetrahedronServices() {
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
	public void initForPointClouds(TetrahedronNet3D tetNet) {

		TetrahedronNet3DComponent[] comp = tetNet.getComponents();
		TetrahedronNet3DElement tetrahedron;
		Point3D[] p;
		int id = 0;

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (TetrahedronNet3DComponent tetComp : comp) {

			Set s = tetComp.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();

			while (it.hasNext()) {

				tetrahedron = (TetrahedronNet3DElement) it.next();

				p = tetrahedron.getPoints();

				int[] pointsForTetrahedrons = new int[4];

				for (int i = 0; i < 4; i++) {

					if (!unique.contains(p[i])) {
						unique.add(p[i]);
						points.put(id, p[i]);
						pointIDs.put(p[i], id);
						pointsForTetrahedrons[i] = id;
						id++;
					} else {
						pointsForTetrahedrons[i] = pointIDs.get(p[i]);
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
	 * The Map "tetrahedrons" is a Map of TetrahedronIDs with an Integer Array of 4
	 * PointIDs.
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
