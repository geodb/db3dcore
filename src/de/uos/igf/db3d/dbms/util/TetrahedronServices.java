package de.uos.igf.db3d.dbms.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.TetrahedronElt3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNet3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNet3DComp;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp;
import de.uos.igf.db3d.dbms.newModel4d.Element4D;
import de.uos.igf.db3d.dbms.newModel4d.Segment4D;
import de.uos.igf.db3d.dbms.newModel4d.Tetrahedron4D;
import de.uos.igf.db3d.dbms.newModel4d.TetrahedronNet4D;
import de.uos.igf.db3d.dbms.newModel4d.Triangle4D;

/**
 * Some services for Tetrahedrons you dont want to miss. 1) initForPointClouds()
 * - Creates some HashMaps for a better Handling of Points from a
 * TetrahedronNet: The Map "points" is a Map of unique Points with unique IDs.
 * The Map "tetrahedrons" is a Map of TetrahedronIDs with an Integer Array of 4
 * PointIDs. The Map "pointIDs" is a Map to access the Point IDs via Point
 * objects. We need it to fill the Integer Array of Tetrahedrons.
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
	 *            - A Tetrahedron Net
	 */
	public void initForPointClouds(TetrahedronNet3D tetNet) {

		TetrahedronNet3DComp[] comp = tetNet.getComponents();
		TetrahedronElt3D tetrahedron;
		Point3D[] p;
		int id = 0;

		// save the IDs of Points to create the Tetrahedrons:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (TetrahedronNet3DComp tetComp : comp) {

			Set s = tetComp.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();

			while (it.hasNext()) {

				tetrahedron = (TetrahedronElt3D) it.next();

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
	 * The initial method to create the Maps for our services.
	 */
	public void initFor4DPointClouds(Map<Integer, Element4D> tetrahedron4dNet,
			Map<Integer, Point3D> pointTubes4D) {

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();
		
		Point3D zero, one, two, three;

		for (Integer tetrahedronID : tetrahedron4dNet.keySet()) {

			Tetrahedron4D tmpTetrahedron = (Tetrahedron4D) tetrahedron4dNet
					.get(tetrahedronID);

			int point4DIDs[] = { tmpTetrahedron.getIDzero(),
					tmpTetrahedron.getIDone(), tmpTetrahedron.getIDtwo(),
					tmpTetrahedron.getIDthree() };

			zero = pointTubes4D.get(point4DIDs[0]);
			one = pointTubes4D.get(point4DIDs[1]);
			two = pointTubes4D.get(point4DIDs[2]);
			three = pointTubes4D.get(point4DIDs[3]);

			Point3D[] p = { zero, one, two, three };

			int[] pointsForTetrahedrons = new int[4];

			for (int i = 0; i < 4; i++) {

				if (!unique.contains(p[i])) {
					unique.add(p[i]);
					points.put(point4DIDs[i], p[i]);
					pointIDs.put(p[i], point4DIDs[i]);
					pointsForTetrahedrons[i] = point4DIDs[i];
				} else {
					pointsForTetrahedrons[i] = pointIDs.get(p[i]);
				}
			}
			tetrahedrons.put(tetrahedronID, pointsForTetrahedrons);
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
	 * need it to fill the Integer Array of Tetrahedrons.
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

	/**
	 * This Map contains the attributes from the tetrahedron net moved to RGB
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

	public void createBoundaryElements(TetrahedronNet4D net,
			Map<Integer, Point3D> pointTubes4D, Date date) {		

		// save the unique Segments of this TetrahedronNet
		Map<Segment4D, Integer> uniqueSegments = new HashMap<Segment4D, Integer>();
		
		// save the unique Triangles of this TetrahedronNet
		Map<Triangle4D, Integer> uniqueTriangles = new HashMap<Triangle4D, Integer>();
		
		// first segID
		int segID = net.getBoundaryElements1D(date).size();

		// first triID
		int triID = net.getBoundaryElements2D(date).size();
		
		Map<Integer, Element4D> tetrahedronElements = net.getNetElements(date);

		for (Integer tetrahedronID : tetrahedronElements.keySet()) {

			Tetrahedron4D tmpTetrahedron = (Tetrahedron4D) tetrahedronElements
					.get(tetrahedronID);

			int point4DIDs[] = { tmpTetrahedron.getIDzero(),
					tmpTetrahedron.getIDone(), tmpTetrahedron.getIDtwo(), tmpTetrahedron.getIDthree() };

			/** BUILD SEGMENTS **/ 
			Segment4D segZero = new Segment4D(point4DIDs[0], point4DIDs[1], 0);
			Segment4D segOne = new Segment4D(point4DIDs[0], point4DIDs[2], 0);
			Segment4D segTwo = new Segment4D(point4DIDs[0], point4DIDs[3], 0);
			Segment4D segThree = new Segment4D(point4DIDs[1], point4DIDs[2], 0);
			Segment4D segFour = new Segment4D(point4DIDs[1], point4DIDs[3], 0);
			Segment4D segFive = new Segment4D(point4DIDs[2], point4DIDs[3], 0);

			Segment4D[] segs = { segZero, segOne, segTwo, segThree, segFour, segFive };

			int[] segmentsForTetrahedrons = new int[6];

			for (int i = 0; i < 6; i++) {

				// Boundary Elements
				if (!uniqueSegments.keySet().contains(segs[i])) {
					uniqueSegments.put(segs[i], segID);
					segs[i].setID(segID);
					segmentsForTetrahedrons[i] = segID;
					segID++;
				} else {
					segmentsForTetrahedrons[i] = uniqueSegments.get(segs[i]);
				}
			}
			tmpTetrahedron.setSegments(segmentsForTetrahedrons);
			
			/** BUILD TRIANGLES **/ 
			
			//TODO EQUALS METHODE FUER DIE TRIANGELS!!!
			Triangle4D triZero = new Triangle4D(point4DIDs[0], point4DIDs[1], point4DIDs[2], 0);
			Triangle4D triOne = new Triangle4D(point4DIDs[0], point4DIDs[1], point4DIDs[3], 0);
			Triangle4D triTwo = new Triangle4D(point4DIDs[0], point4DIDs[2], point4DIDs[3], 0);
			Triangle4D triThree = new Triangle4D(point4DIDs[1], point4DIDs[2], point4DIDs[3], 0);

			Triangle4D[] tris = { triZero, triOne, triTwo, triThree };

			int[] trianglesForTetrahedrons = new int[4];

			for (int i = 0; i < 4; i++) {

				// Boundary Elements
				if (!uniqueTriangles.keySet().contains(tris[i])) {
					uniqueTriangles.put(tris[i], triID);
					tris[i].setID(triID);
					trianglesForTetrahedrons[i] = triID;
					triID++;
				} else {
					trianglesForTetrahedrons[i] = uniqueTriangles.get(tris[i]);
				}
			}
			tmpTetrahedron.setTriangles(trianglesForTetrahedrons);
		}

		// Die Boundary Elemente setzen
		for (Segment4D seg : uniqueSegments.keySet()) {
			net.addBoundaryElement(seg);
		}
		
		// Die Boundary Elemente setzen
		for (Triangle4D tri : uniqueTriangles.keySet()) {
			net.addBoundaryElement(tri);
		}
	}
}
