package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.model3d.ComplexGeoObj;
import de.uos.igf.db3d.dbms.model3d.Object3D;
import de.uos.igf.db3d.dbms.model3d.Object3DBuilder;
import de.uos.igf.db3d.dbms.model3d.PointElt3D;
import de.uos.igf.db3d.dbms.model3d.PointNetBuilder;
import de.uos.igf.db3d.dbms.model3d.SegmentElt3D;
import de.uos.igf.db3d.dbms.model3d.SegmentNetBuilder;
import de.uos.igf.db3d.dbms.model3d.TetrahedronElt3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNetBuilder;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNetBuilder;
import de.uos.igf.db3d.dbms.structure.Space3D;

public class ServicesFor4DObjects {

	/**
	 * The 4D-to-3D Service
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the
	 * specified time
	 * 
	 * Note: A Object4D can only have one type of Spatial Part!
	 * 
	 * @param date
	 *            - the effective date for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the
	 *         specified date
	 * @author Paul Vincent Kuper (kuper@kit.edu)
	 */
	public static Object3D getInstanceAt(Object4D object, Date date) {

		SpatialObject4D spatial = object.getSpatial();

		ScalarOperator sop = spatial.getScalarOperator();

		Net4D net = spatial.getNet();

		// check if the specified date is not in the timeinterval:
		if (!checkIfDateIsValid(date, spatial))
			return null;

		// create a Object3DBuilder with the right ScalarOperator
		Object3DBuilder builder = new Object3DBuilder(object.getSpatial()
				.getScalarOperator());

		// one builder for every type
		PointNetBuilder pNB = builder.getPointNetBuilder();
		SegmentNetBuilder sNB = builder.getSegmentNetBuilder();
		TriangleNetBuilder tNB = builder.getTriangleNetBuilder();
		TetrahedronNetBuilder tetraNB = builder.getTetrahedronNetBuilder();

		// get all the geometry of this object at the specified date and
		// create 3D components with this information.
		// SpatialObject4D geometry = object.getGeometry()
		// .get(indexOfGeometry);

		// create all the Point3D objects

		if (net.getType() == ComplexGeoObj.POINT_NET_4D) {

			PointNet4D pointNet = (PointNet4D) net;

			Map<Integer, Point3D> interpolatedPoints = new HashMap<Integer, Point3D>();

			for (Component4D component : pointNet.getValidComponents(date)) {
				interpolatedPoints.putAll(TimeStepBuilder
						.getPointTubesAtInstance(component, date));
			}

			Map<Integer, Element4D> elements4D = pointNet.getNetElements(date);

			PointElt3D[] elements = new PointElt3D[elements4D.size()];

			int cnt = 0;

			for (Integer pointID : elements4D.keySet()) {

				Point4D tmp = (Point4D) elements4D.get(pointID);

				PointElt3D point = new PointElt3D(interpolatedPoints.get(tmp
						.getID()));

				elements[cnt] = point;
				cnt++;
			}

			// add the component to the PointNetBuilder
			pNB.addComponent(elements);

			builder.setSpatialPart(pNB.getPointNet());

		}

		// create all the Segments3D objects
		else if (net.getType() == ComplexGeoObj.SEGMENT_NET_4D) {

			SegmentNet4D segmentNet = (SegmentNet4D) net;

			Map<Integer, Point3D> interpolatedPoints = new HashMap<Integer, Point3D>();

			for (Component4D component : segmentNet.getValidComponents(date)) {
				interpolatedPoints.putAll(TimeStepBuilder
						.getPointTubesAtInstance(component, date));
			}

			Map<Integer, Element4D> elements4D = segmentNet
					.getNetElements(date);

			SegmentElt3D[] elements = new SegmentElt3D[elements4D.size()];

			int cnt = 0;

			for (Integer segmentID : elements4D.keySet()) {

				Segment4D tmp = (Segment4D) elements4D.get(segmentID);

				SegmentElt3D segment = new SegmentElt3D(
						interpolatedPoints.get(tmp.getIDstart()),
						interpolatedPoints.get(tmp.getIDend()), sop);

				elements[cnt] = segment;
				cnt++;
			}

			// add the component to the SegmentNetBuilder
			sNB.addComponent(elements);

			builder.setSpatialPart(sNB.getSegmentNet());
		}

		// create all the Triangel3D objects
		// for every component:

		// Was benötige ich? PointTubes und Elemente unterteilt nach
		// Components!
		// Vermaschung = Elemente kommen von den Netzen! PTs von den
		// Components!

		// get the right components:

		// now we have the index of the right topology for this date.
		// so get the right points:
		// TODO: Do this for every component!

		else if (net.getType() == ComplexGeoObj.TRIANGLE_NET_4D) {

			TriangleNet4D triangleNet = (TriangleNet4D) net;

			Map<Integer, Point3D> interpolatedPoints = new HashMap<Integer, Point3D>();

			for (Component4D component : triangleNet.getValidComponents(date)) {
				interpolatedPoints.putAll(TimeStepBuilder
						.getPointTubesAtInstance(component, date));
			}

			Map<Integer, Element4D> elements4D = triangleNet
					.getNetElements(date);

			TriangleElt3D[] elements = new TriangleElt3D[elements4D.size()];

			int cnt = 0;

			for (Integer triangleID : elements4D.keySet()) {

				Triangle4D tmp = (Triangle4D) elements4D.get(triangleID);

				TriangleElt3D triangle = new TriangleElt3D(
						interpolatedPoints.get(tmp.getIDzero()),
						interpolatedPoints.get(tmp.getIDone()),
						interpolatedPoints.get(tmp.getIDtwo()), sop);

				elements[cnt] = triangle;
				cnt++;
			}

			// add the component to the TriangleNetBuilder
			tNB.addComponent(elements);

			builder.setSpatialPart(tNB.getTriangleNet());
		}

		// create all the Tetrahedron3D objects
		else if (net.getType() == ComplexGeoObj.TETRAHEDRON_NET_4D) {

			TetrahedronNet4D tetrahedronNet = (TetrahedronNet4D) net;

			Map<Integer, Point3D> interpolatedPoints = new HashMap<Integer, Point3D>();

			for (Component4D component : tetrahedronNet
					.getValidComponents(date)) {
				interpolatedPoints.putAll(TimeStepBuilder
						.getPointTubesAtInstance(component, date));
			}

			Map<Integer, Element4D> elements4D = tetrahedronNet
					.getNetElements(date);

			TetrahedronElt3D[] elements = new TetrahedronElt3D[elements4D
					.size()];

			int cnt = 0;

			for (Integer tetrahedronID : elements4D.keySet()) {

				Tetrahedron4D tmp = (Tetrahedron4D) elements4D
						.get(tetrahedronID);

				TetrahedronElt3D tetrahedron = new TetrahedronElt3D(
						interpolatedPoints.get(tmp.getIDzero()),
						interpolatedPoints.get(tmp.getIDone()),
						interpolatedPoints.get(tmp.getIDtwo()),
						interpolatedPoints.get(tmp.getIDthree()), sop);

				elements[cnt] = tetrahedron;
				cnt++;
			}

			// add the component to the TetrahedronNetBuilder
			tetraNB.addComponent(elements);

			builder.setSpatialPart(tetraNB.getTetrahedronNet());
		}

		// return the Object3D
		return builder.getObject3D();
	}

	/**
	 * Checks if the date is inside of one timeinterval of the nets
	 * 
	 * @param date
	 * @param netObjects
	 * @return true if we found a net, false otherwise
	 */
	private static boolean checkIfDateIsValid(Date date, SpatialObject4D spatial) {

		boolean valid = false;

		Net4D net = spatial.getNet();

		if (net != null) {
			if (((net.getStart().before(date) && net.getEnd().after(date))
					|| net.getEnd().equals((date)) || net.getStart().equals(
					(date)))) {
				valid = true;
			}
		}

		return valid;
	}

	public static double getAverageSpeed(Object4D object, Date date) {

		Net4D net = object.getSpatial().getNet();

		List<Component4D> comps = net.getValidComponents(date);
		double speedSum = 0;
		int timesteps = 1;

		for (Component4D comp : comps) {

			Map<Integer, List<Point3D>> pts = comp.getPointTubes();
			
			for(List<Point3D> l : pts.values()) {

				for(int i = 0; i < l.size()-1; i++) {
					
					Point3D p1 = l.get(i);
					Point3D p2 = l.get(i+1);

					speedSum +=  p1.euclideanDistance(p2);
				}
			}			

			timesteps = pts.values().iterator().next().size();
			
			speedSum = speedSum / pts.size();
			speedSum = speedSum / timesteps;
		}

		return speedSum;
	}
}
