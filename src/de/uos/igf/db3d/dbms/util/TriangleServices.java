package de.uos.igf.db3d.dbms.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp;
import de.uos.igf.db3d.dbms.newModel4d.Element4D;
import de.uos.igf.db3d.dbms.newModel4d.Segment4D;
import de.uos.igf.db3d.dbms.newModel4d.Triangle4D;
import de.uos.igf.db3d.dbms.newModel4d.TriangleNet4D;

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
public class TriangleServices {

	// ID + Point3D
	// The Map "points" is a Map of unique Points with unique IDs.
	Map<Integer, Point3D> points;

	// Point3D + ID
	// The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	// need it to fill the Integer Array of Triangles.
	Map<Point3D, Integer> pointIDs;

	// Triangle ID + 3 Point3D IDs
	// The Map "triangles" is a Map of TriangleIDs with an Integer Array of 3
	// PointIDs.
	Map<Integer, int[]> triangles;

	// This Map contains all componentIDs accessible by their triangleID
	// (triangleID, componentID)
	Map<Integer, Integer> components;

	// This Maps contains all PointIDs matched to all Triangle3D objects they
	// are present in.
	// (PointID, List of Triangles containing this point)
	Map<Integer, LinkedList<Triangle3D>> pointToTriangle;

	// A set of all segments of the TriangleNet
	HashSet<Segment3D> segments;

	/**
	 * Constructor creates Maps and calls init function.
	 * 
	 * @param triNet
	 *            - A Triangle Net
	 */
	public TriangleServices() {
		points = new TreeMap<Integer, Point3D>();
		pointIDs = new HashMap<Point3D, Integer>();
		triangles = new TreeMap<Integer, int[]>();
		components = new TreeMap<Integer, Integer>();
		segments = new HashSet<Segment3D>();
		pointToTriangle = new TreeMap<Integer, LinkedList<Triangle3D>>();
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
	 * This methods creates unique points for a 4D triangle net
	 * 
	 * @param triangle4dNet
	 *            - Map with IDs and Triangle4D objects (just point IDs)
	 * @param pointTubes4D
	 *            - Point3D objects with an ID
	 */
	public void initFor4DPointCloud(Map<Integer, Element4D> triangle4dNet,
			Map<Integer, Point3D> pointTubes4D) {

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (Integer triangleID : triangle4dNet.keySet()) {

			Triangle4D tmpTriangle = (Triangle4D) triangle4dNet.get(triangleID);

			int point4DIDs[] = { tmpTriangle.getIDzero(),
					tmpTriangle.getIDone(), tmpTriangle.getIDtwo() };

			Point3D zero = pointTubes4D.get(point4DIDs[0]);
			Point3D one = pointTubes4D.get(point4DIDs[1]);
			Point3D two = pointTubes4D.get(point4DIDs[2]);

			Point3D[] p = { zero, one, two };

			int[] pointsForTriangles = new int[3];

			for (int i = 0; i < 3; i++) {

				if (!unique.contains(p[i])) {
					unique.add(p[i]);
					points.put(point4DIDs[i], p[i]);
					pointIDs.put(p[i], point4DIDs[i]);
					pointsForTriangles[i] = point4DIDs[i];
				} else {
					pointsForTriangles[i] = pointIDs.get(p[i]);
				}
			}
			triangles.put(triangleID, pointsForTriangles);
		}
	}

	public void createBoundaryElements(TriangleNet4D net,
			Map<Integer, Point3D> pointTubes4D, Date date) {

		// save the unique Segments of this TriangleNet
		Map<Segment4D, Integer> uniqueSegments = new HashMap<Segment4D, Integer>();

		// first segID
		int segID = net.getBoundaryElements1D(date).size();

		Map<Integer, Element4D> triangleElements = net.getNetElements(date);

		for (Integer triangleID : triangleElements.keySet()) {

			Triangle4D tmpTriangle = (Triangle4D) triangleElements
					.get(triangleID);

			int point4DIDs[] = { tmpTriangle.getIDzero(),
					tmpTriangle.getIDone(), tmpTriangle.getIDtwo() };

			Segment4D segZero = new Segment4D(point4DIDs[0], point4DIDs[1], 0);
			Segment4D segOne = new Segment4D(point4DIDs[1], point4DIDs[2], 0);
			Segment4D segTwo = new Segment4D(point4DIDs[2], point4DIDs[0], 0);

			Segment4D[] segs = { segZero, segOne, segTwo };

			int[] segmentsForTriangles = new int[3];

			for (int i = 0; i < 3; i++) {

				// Boundary Elements
				if (!uniqueSegments.keySet().contains(segs[i])) {
					uniqueSegments.put(segs[i], segID);
					segs[i].setID(segID);
					segmentsForTriangles[i] = segID;
					segID++;
				} else {
					segmentsForTriangles[i] = uniqueSegments.get(segs[i]);
				}
			}
			tmpTriangle.setSegments(segmentsForTriangles);
		}

		// Die Boundary Elemente setzen
		for (Segment4D seg : uniqueSegments.keySet()) {
			net.addBoundaryElement(seg);
		}
	}

	/**
	 * The Map "points" is a Map of unique Points with unique IDs. Starts at 0.
	 * 
	 * @return the points
	 */
	public Map<Integer, Point3D> getPoints() {
		return points;
	}

	/**
	 * The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	 * need it to fill the Integer Array of Triangles.
	 * 
	 * @return the pointIDs
	 */
	public Map<Point3D, Integer> getPointIDs() {
		return pointIDs;
	}

	/**
	 * The Map "triangles" is a Map of TriangleIDs with an Integer Array of 3
	 * PointIDs.
	 * 
	 * @return the triangles
	 */
	public Map<Integer, int[]> getTriangles() {
		return triangles;
	}

	/**
	 * This Map contains all componentIDs accessible by their triangleID
	 * (triangleID, componentID)
	 * 
	 * @return the components
	 */
	public Map<Integer, Integer> getComponents() {
		return components;
	}

	/**
	 * This Map contains all Triangles matched to a single PointID
	 * 
	 * @return the segments
	 */
	public Map<Integer, LinkedList<Triangle3D>> getpointToTriangles() {
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
