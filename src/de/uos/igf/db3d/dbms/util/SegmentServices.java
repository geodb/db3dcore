package de.uos.igf.db3d.dbms.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.model3d.SegmentElt3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNet3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNet3DComp;
import de.uos.igf.db3d.dbms.model3d.TetrahedronElt3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNet3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNet3DComp;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNet3DComp;
import de.uos.igf.db3d.dbms.newModel4d.Element4D;
import de.uos.igf.db3d.dbms.newModel4d.Segment4D;
import de.uos.igf.db3d.dbms.newModel4d.Tetrahedron4D;
import de.uos.igf.db3d.dbms.newModel4d.Triangle4D;

/**
 * Some services for Segments you dont want to miss. 1) initForPointClouds() -
 * Creates some HashMaps for a better Handling of Points from a SegmentNet: The
 * Map "points" is a Map of unique Points with unique IDs. The Map "segments" is
 * a Map of SegmentIDs with an Integer Array of 2 PointIDs. The Map "pointIDs"
 * is a Map to access the Point IDs via Point objects. We need it to fill the
 * Integer Array of Segments.
 * 
 * You can use this methods for Import/Export functions to sort out duplicate
 * points.
 * 
 * @author Paul Vincent Kuper (kuper@kit.edu)
 */
public class SegmentServices {

	// ID + Point3D
	// The Map "points" is a Map of unique Points with unique IDs.
	HashMap<Integer, Point3D> points;

	// Point3D + ID
	// The Map "pointIDs" is a Map to access the Point IDs via Point objects. We
	// need it to fill the Integer Array of Segment.
	HashMap<Point3D, Integer> pointIDs;

	// Tetrahedron ID + 3 Point3D IDs
	// The Map "segments" is a Map of SegmentIDs with an Integer Array
	// of 2 PointIDs.
	HashMap<Integer, int[]> segments;

	// This Map contains all componentIDs accessible by their segmentID
	// (segmentID, componentID)
	HashMap<Integer, Integer> components;

	/**
	 * Constructor creates Maps and calls init function.
	 * 
	 */
	public SegmentServices() {
		points = new HashMap<Integer, Point3D>();
		pointIDs = new HashMap<Point3D, Integer>();
		segments = new HashMap<Integer, int[]>();
		components = new HashMap<Integer, Integer>();
	}

	/**
	 * The initial method to create the Maps for our services.
	 * 
	 * @param tetNet
	 *            - A Segment Net
	 */
	public void initForPointClouds(SegmentNet3D segNet) {

		SegmentNet3DComp[] comp = segNet.getComponents();
		SegmentElt3D segment;
		Point3D[] p;
		int id = 0;

		// save the IDs of Points to create the Segments:
		Set<Point3D> unique = new HashSet<Point3D>();

		for (SegmentNet3DComp segComp : comp) {

			Set s = segComp.getElementsViaSAM();
			Iterator<Equivalentable> it = s.iterator();

			while (it.hasNext()) {

				segment = (SegmentElt3D) it.next();

				p = segment.getPoints();

				int[] pointsForSegments = new int[2];

				for (int i = 0; i < 2; i++) {

					if (!unique.contains(p[i])) {
						unique.add(p[i]);
						points.put(id, p[i]);
						pointIDs.put(p[i], id);
						pointsForSegments[i] = id;
						id++;
					} else {
						pointsForSegments[i] = pointIDs.get(p[i]);
					}
				}
				segments.put(segment.getID(), pointsForSegments);
				components.put(segment.getID(), segComp.getID());
			}
		}
	}

	/**
	 * The initial method to create the Maps for our services.
	 */
	public void initFor4DPointClouds(Map<Integer, Element4D> segment4dNet,
			Map<Integer, Point3D> pointTubes4D) {

		// save the IDs of Points to create the Triangles:
		Set<Point3D> unique = new HashSet<Point3D>();
		
		Point3D zero, one;		

		for (Integer segmentID : segment4dNet.keySet()) {

			Segment4D tmpSegment = (Segment4D) segment4dNet.get(segmentID);

			int point4DIDs[] = { tmpSegment.getIDstart(), tmpSegment.getIDend() };

			zero = pointTubes4D.get(point4DIDs[0]);
			one = pointTubes4D.get(point4DIDs[1]);

			Point3D[] p = { zero, one };

			int[] pointsForSegments = new int[2];

			for (int i = 0; i < 2; i++) {

				if (!unique.contains(p[i])) {
					unique.add(p[i]);
					points.put(point4DIDs[i], p[i]);
					pointIDs.put(p[i], point4DIDs[i]);
					pointsForSegments[i] = point4DIDs[i];
				} else {
					pointsForSegments[i] = pointIDs.get(p[i]);
				}
			}
			segments.put(segmentID, pointsForSegments);
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
	 * need it to fill the Integer Array of Segments.
	 * 
	 * @return the pointIDs
	 */
	public HashMap<Point3D, Integer> getPointIDs() {
		return pointIDs;
	}

	/**
	 * The Map "segments" is a Map of SegmentIDs with an Integer Array
	 * of 2 PointIDs.
	 * 
	 * @return the triangles
	 */
	public HashMap<Integer, int[]> getSegments() {
		return segments;
	}

	/**
	 * This Map contains all componentIDs accessible by their segmentID
	 * (segmentID, componentID)
	 * 
	 * @return the components
	 */
	public HashMap<Integer, Integer> getComponents() {
		return components;
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
