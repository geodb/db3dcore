package de.uos.igf.db3d.dbms.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.model3d.PointElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp;

/**
 * Some services for Triangles you do not want to miss. 
 * initForPointClouds(): 
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
 * useful Containers: 
 * Set<Segment3D> segments: A set of all segments of the TriangleNet
 * 
 * HashMap<Integer, LinkedList<Triangle3D>> pointToTriangle: This Maps contains
 * all PointIDs matched to all Triangle3D objects they are present in.
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
	public TriangleServices() {
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

			while (it.hasNext()) {

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
	public void initForProgressiveMeshes(TriangleNet3D triNet) {

		TriangleNet3DComp[] comp = triNet.getComponents();
		TriangleElt3D triangle;
		Point3D[] p;
		int id = 0;

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (TriangleNet3DComp tri : comp) {

			Set s = tri.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();
			int idForPoint = 0;

			while (it.hasNext()) {

				triangle = (TriangleElt3D) it.next();
				
				for (Segment3D seg : triangle.getSegments()) {
					segments.add(seg);
				}
				
				p = triangle.getPoints();

				int[] pointsForTriangles = new int[3];

				for (int i = 0; i < 3; i++) {

					if (!unique.contains(p[i])) {
						unique.add(p[i]);
						points.put(id, p[i]);
						pointIDs.put(p[i], id);
						pointsForTriangles[i] = id;
						
						pointToTriangle.put(id, new LinkedList<Triangle3D>());
						pointToTriangle.get(id).add(triangle);
						
						id++;
					} else {
						idForPoint = pointIDs.get(p[i]);
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

	/**
	 * This Map contains the attributes from the triangle net moved to RGB
	 * (pointID, color)
	 * 
	 * @return the components
	 */
	public HashMap<Integer, String> getAttributeAsColor(String attribute) {

		HashMap<Integer, String> attributeColors = new HashMap<Integer, String>();

		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;

		// check out the range of values:
		for (Point3D point : points.values()) {
			double tmp = Double.valueOf(point.getAttributeValue(attribute));
			if (tmp > maxValue)
				maxValue = tmp;
			else if (tmp < minValue)
				minValue = tmp;
		}

		minValue = Math.abs(minValue);
		maxValue = maxValue + minValue;

		// build colors:
		for (Point3D point : points.values()) {

			double tmp = Double.valueOf(point.getAttributeValue(attribute));
			tmp = tmp + minValue;
			tmp = (510. * tmp) / maxValue;
			String hex, hexTmp;

			if (tmp <= 255) {
				hexTmp = Integer.toHexString((int) tmp);
				if (hexTmp.length() == 1)
					hexTmp = "0" + hexTmp;
				hex = hexTmp + "ff";
			} else {
				hexTmp = Integer.toHexString((int) tmp - 255);
				if (hexTmp.length() == 1)
					hexTmp = "0" + hexTmp;
				hex = "ff" + hexTmp;
			}

			attributeColors.put(pointIDs.get(point), "0xff" + hex + "00");
		}
		return attributeColors;
	}

	/**
	 * Converts RGB values to hex!
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	private static String colorToHex(String r, String g, String b) {
		String rHex = Integer.toHexString(Integer.parseInt(r));
		String gHex = Integer.toHexString(Integer.parseInt(g));
		String bHex = Integer.toHexString(Integer.parseInt(b));

		return ("0xff" + rHex + gHex + bHex);
	}
}
