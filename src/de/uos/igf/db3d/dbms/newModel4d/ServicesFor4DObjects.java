package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.model3d.Object3D;
import de.uos.igf.db3d.dbms.model3d.Object3DBuilder;
import de.uos.igf.db3d.dbms.model3d.PointNetBuilder;
import de.uos.igf.db3d.dbms.model3d.SegmentNetBuilder;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNetBuilder;
import de.uos.igf.db3d.dbms.model3d.TriangleElt3D;
import de.uos.igf.db3d.dbms.model3d.TriangleNetBuilder;

public class ServicesFor4DObjects {

	/**
	 * The 4D-to-3D Service
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the
	 * specified time
	 * 
	 * @param date
	 *            - the effective date for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the
	 *         specified date
	 */
	public Object3D getInstanceAt(Object4D object, Date date) {

		int indexOfGeometry = 0;

		// check if the specified date is not in the timeinterval:
		if (!((object.getTimesteps().getFirst().before(date) && object
				.getTimesteps().getLast().after(date)) || object.getTimesteps()
				.contains(date)))
			return null;
		else {

			// find out which is the right topology for this specified date:
			Date lastDate = object.getTimesteps().get(0);
			Date actualDate = object.getTimesteps().get(1);
			int cnt = 2;
			while (actualDate.before(date)) {
				if (actualDate == lastDate)
					indexOfGeometry++;
				lastDate = actualDate;
				actualDate = object.getTimesteps().get(cnt);
				cnt++;
			}

			// now we have the index of the right topology for this date.
			// so get the right points:
			Map<Integer, Point3D> interpolatedPoints = object
					.getPointTubesAtInstance(date);

			// create a Object3DBuilder with the right ScalarOperator
			Object3DBuilder builder = new Object3DBuilder(object.getGeometry()
					.get(indexOfGeometry).getScalarOperator());
			
			// one builder for every type
			PointNetBuilder pNB = builder.getPointNetBuilder();
			SegmentNetBuilder sNB = builder.getSegmentNetBuilder();
			TriangleNetBuilder tNB = builder.getTriangleNetBuilder();
			TetrahedronNetBuilder tetraNB = builder.getTetrahedronNetBuilder();

			// get all the geometry of this object at the specified date and
			// create 3D components with this information.
			SpatialObject4D geometry = object.getGeometry()
					.get(indexOfGeometry);

			// create all the Point3D objects
			for (int i = 0; i < geometry.getPoints().size(); i++) {
				// TODO implement
			}

			// create all the Segments3D objects
			for (int i = 0; i < geometry.getSegmentNets().size(); i++) {
				// TODO implement
			}

			// create all the Triangel3D objects
			// for every component:
			
			Iterator<Integer> keys = geometry.getTriangleNets().keySet().iterator();
			
			while(keys.hasNext()) {
				
				Integer id = keys.next();
							
				TriangleElt3D[] elements = new TriangleElt3D[geometry
						.getTriangleNets().get(id).getElements().size()];

				// for every Triangle of this component
				
				// TODO über die einträge iterieren. (Entryset)
				
				Iterator<Integer> ids = geometry.getTriangleNets().get(id)
				.getElements().keySet().iterator();
				
				while(ids.hasNext()) {
					
					Integer triangleID = ids.next(); 

					Triangle4D tmp = geometry.getTriangleNets().get(id)
							.getElements().get(triangleID);

					TriangleElt3D triangle = new TriangleElt3D(
							interpolatedPoints.get(tmp.getIDzero()),
							interpolatedPoints.get(tmp.getIDone()),
							interpolatedPoints.get(tmp.getIDtwo()), null);

					elements[triangleID] = triangle;
				}
				// add the component to the TriangleNetBuilder
				tNB.addComponent(elements, id);
			}
			
			builder.setSpatialPart(tNB.getTriangleNet());

			// create all the Tetrahedron3D objects
			for (int i = 0; i < geometry.getTetrahedronNets().size(); i++) {
				// TODO implement
			}

			// return the Object3D
			return builder.getObject3D();
		}
	}

	/**
	 * The 4D-to-3D Service
	 * 
	 * The main service for 4D objects which creates suitable 3D objects at the
	 * specified timestep
	 * 
	 * @param timestep
	 *            - the timestep for the object3D which will be created
	 * @return the Object3D object which represents the Object4D at the
	 *         specified timestep
	 */
	public Object3D getInstanceAt(Object4D object, Integer timestep) {

		// TODO

		return null;

	}
}
