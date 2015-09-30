package de.uos.igf.db3d.dbms.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.uos.igf.db3d.dbms.spatials.api.Equivalentable;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Segment3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Triangle3D;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DNet;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DComponent;
import de.uos.igf.db3d.dbms.spatials.standard3d.Triangle3DElement;

/**
 * Some services for Triangles you do not want to miss. initForPointClouds():
 * Creates some HashMaps for a better Handling of Points from a TriangleNet: The
 * Map "points" is a Map of unique Points with unique IDs. The Map "triangles"
 * is a Map of TriangleIDs with an Integer Array of 3 PointIDs. The Map
 * "pointIDs" is a Map to access the Point IDs via Point objects. We need it to
 * fill the Integer Array of Triangles.
 * 
 * You can use this methods for Import/Export functions to sort out duplicate
 * points.
 * 
 * The second init method (initForProgressiveMeshes()) creates even two more
 * useful Containers: Set<Segment3D> segments: A set of all segments of the
 * TriangleNet
 * 
 * HashMap<Integer, LinkedList<Triangle3D>> pointToTriangle: This Maps contains
 * all PointIDs matched to all Triangle3D objects they are present in.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class TriangleNet3DLinkedHashSet {

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

	// This Map contains all componentIDs accessible by their triangleID
	// (triangleID, componentID)
	HashMap<Integer, Integer> components;

	// This Maps contains all PointIDs matched to all Triangle3D objects they
	// are present in.
	// (PointID, List of Triangles containing this point)
	HashMap<Integer, LinkedList<Triangle3D>> pointToTriangle;

	// A set of all segments of the TriangleNet
	HashSet<Segment3D> segments;

	/**
	 * Constructor creates Maps and calls init function.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public TriangleNet3DLinkedHashSet() {
		points = new HashMap<Integer, Point3D>();
		pointIDs = new HashMap<Point3D, Integer>();
		triangles = new HashMap<Integer, int[]>();
		components = new HashMap<Integer, Integer>();
		segments = new HashSet<Segment3D>();
		pointToTriangle = new HashMap<Integer, LinkedList<Triangle3D>>();
	}

	/**
	 * The initial method to create the Maps for our services.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public void initForPointClouds(Triangle3DNet triNet) {

		Triangle3DComponent[] comp = triNet.getComponents();
		Triangle3DElement triangle;

		int id = 0;

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (Triangle3DComponent tri : comp) {

			Set s = tri.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();

			while (it.hasNext()) {

				triangle = (Triangle3DElement) it.next();

				int[] pointsForTriangles = new int[3];

				for (int i = 0; i < 3; i++) {

					if (!unique.contains(triangle.getPoints()[i])) {
						unique.add(triangle.getPoints()[i]);
						points.put(id, triangle.getPoints()[i]);
						pointIDs.put(triangle.getPoints()[i], id);
						pointsForTriangles[i] = id;
						id++;
					} else {
						pointsForTriangles[i] = pointIDs.get(triangle
								.getPoints()[i]);
					}
				}
				triangles.put(triangle.getID(), pointsForTriangles);
				components.put(triangle.getID(), tri.getID());
			}
		}
	}

	/**
	 * The initial method to create the Maps for Progressive Meshes. In this
	 * init method we create two more Containers:
	 * 
	 * Set<Segment3D> segments: A set of all segments of the TriangleNet
	 * 
	 * HashMap<Integer, LinkedList<Triangle3D>> pointToTriangle: This Maps
	 * contains all PointIDs matched to all Triangle3D objects they are present
	 * in.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public void initForProgressiveMeshes(Triangle3DNet triNet) {

		Triangle3DComponent[] comp = triNet.getComponents();
		Triangle3DElement triangle;
		int id = 0;

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (Triangle3DComponent tri : comp) {

			Set s = tri.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();
			int idForPoint = 0;

			while (it.hasNext()) {

				triangle = (Triangle3DElement) it.next();

				for (int i = 0; i < 3; i++) {
					segments.add(triangle.getSegment(i,
							triangle.getGeoEpsilon()));
				}

				int[] pointsForTriangles = new int[3];

				for (int i = 0; i < 3; i++) {

					if (!unique.contains(triangle.getPoints()[i])) {
						unique.add(triangle.getPoints()[i]);
						points.put(id, triangle.getPoints()[i]);
						pointIDs.put(triangle.getPoints()[i], id);
						pointsForTriangles[i] = id;

						pointToTriangle.put(id, new LinkedList<Triangle3D>());
						pointToTriangle.get(id).add(triangle);

						id++;
					} else {
						idForPoint = pointIDs.get(triangle.getPoints()[i]);
						pointsForTriangles[i] = idForPoint;
						pointToTriangle.get(idForPoint).add(triangle);
					}
				}
				triangles.put(triangle.getID(), pointsForTriangles);
				components.put(triangle.getID(), tri.getID());
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

	/**
	 * This Map contains all componentIDs accessible by their triangleID
	 * (triangleID, componentID)
	 * 
	 * @return the components
	 */
	public HashMap<Integer, Integer> getComponents() {
		return components;
	}

	/**
	 * This Map contains all Triangles matched to a single PointID
	 * 
	 * @return the segments
	 */
	public HashMap<Integer, LinkedList<Triangle3D>> getpointToTriangles() {
		return pointToTriangle;
	}

	/**
	 * This Set contains all segments of the TriangleNet
	 * 
	 * @return the segments
	 */
	public Set<Segment3D> getSegments() {
		return segments;
	}

}
