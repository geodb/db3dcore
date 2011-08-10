package de.uos.igf.db3d.dbms.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp;

/**
 * Some services for Triangles you dont want to miss. 1) initForPointClouds() -
 * Creates some HashMaps for a better Handling of Points from a TriangleNet: The
 * Map "points" is a Map of unique Points with unique IDs. The Map "triangles"
 * is a Map of TriangleIDs with an Integer Array of 3 PointIDs. The Map
 * "pointIDs" is a Map to access the Point IDs via Point objects. We need it to
 * fill the Integer Array of Triangles.
 * 
 * You can use this methods for Import/Export functions to sort out duplicate
 * points.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleServices {

	// ID + Point3D
	// The Map "points" is a Map of unique Points with unique IDs.
	HashMap<Integer, Point3D> points;

	// Point3D + ID
	// The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	// need it to fill the Integer Array of Triangles.
	HashMap<Point3D, Integer> pointIDs;

	// Triangle ID + 3 Point3D IDs
	// The Map "triangles" is a Map of TriangleIDs with an Integer Array of 3
	// PointIDs.
	HashMap<Integer, int[]> triangles;

	/**
	 * Constructor creates Maps and calls init function.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public TriangleServices() {
		points = new HashMap<Integer, Point3D>();
		pointIDs = new HashMap<Point3D, Integer>();
		triangles = new HashMap<Integer, int[]>();
	}

	/**
	 * The initial method to create the Maps for our services.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public void initForPointClouds(TriangleNet3D triNet) {

		TriangleNet3DComp[] comp = triNet.getComponents();
		TriangleElt3D triangle;
		Point3D[] p;
		int id = 0;

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (TriangleNet3DComp tri : comp) {

			Set s = tri.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();

			int cnt = 0;

			while (it.hasNext()) {
				cnt++;
				triangle = (TriangleElt3D) it.next();

				p = triangle.getPoints();

				int[] pointsForTriangles = new int[3];

				for (int i = 0; i < 3; i++) {

					if (!unique.contains(p[i])) {
						unique.add(p[i]);
						points.put(id, p[i]);
						pointIDs.put(p[i], id);
						pointsForTriangles[i] = id;
						id++;
					} else {
						pointsForTriangles[i] = pointIDs.get(p[i]);
					}
				}
				triangles.put(triangle.getID(), pointsForTriangles);
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
	 * The Map "triangles" is a Map of TriangleIDs with an Integer Array of 3
	 * PointIDs.
	 * 
	 * @return the triangles
	 */
	public HashMap<Integer, int[]> getTriangles() {
		return triangles;
	}
}
