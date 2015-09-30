package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.collections.RStar;
import de.uos.igf.db3d.dbms.collections.SAM;
import de.uos.igf.db3d.dbms.spatials.api.Component3D;
import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.api.Net3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;
import de.uos.igf.db3d.resources.DB3DLogger;

/**
 * Implementation of a Component
 * 
 * @author Markus Jahn
 * 
 */
public abstract class Component3DAbst extends Spatial3DAbst implements
		Component3D {

	/* id - if not set its -1 */
	protected int id;

	/* reference to net */
	protected Net3D net;

	/* spatial tree */
	protected SAM sam;

	/* current vertices id state */
	protected int verticeID;

	/* current edges id state */
	protected int edgeID;

	/* current edges id state */
	protected int faceID;

	/* current solids id state */
	protected int solidID;

	/* vertices */
	protected TreeMap<Integer, Point3DElement> vertices;

	/* edges */
	protected TreeMap<Integer, Segment3DElement> edges;

	/* faces */
	protected TreeMap<Integer, Triangle3DElement> faces;

	/* solids */
	protected TreeMap<Integer, Tetrahedron3DElement> solids;

	public Component3DAbst(GeoEpsilon epsilon) {
		super(epsilon);
		this.id = -1;
		this.verticeID = 0;
		this.edgeID = 0;
		this.faceID = 0;
		this.solidID = 0;
		this.sam = new RStar(MAX_SAM, epsilon);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public Net3D getNet() {
		return this.net;
	}

	@Override
	public SAM getSAM() {
		return this.sam;
	}

	@Override
	public int countElements() {
		return this.sam.getCount();
	}

	@Override
	public boolean isEmpty() {
		return this.sam.getCount() == 0;
	}

	@Override
	public Set<?> getElementsViaSAM() {
		return this.sam.getEntries();
	}

	/**
	 * @return the vertices
	 */
	@Override
	public TreeMap<Integer, Point3DElement> getVertices() {
		return vertices;
	}

	/**
	 * @return the edges
	 */
	@Override
	public TreeMap<Integer, Segment3DElement> getEdges() {
		return edges;
	}

	/**
	 * @return the faces
	 */
	@Override
	public TreeMap<Integer, Triangle3DElement> getFaces() {
		return faces;
	}

	/**
	 * @return the solids
	 */
	@Override
	public TreeMap<Integer, Tetrahedron3DElement> getSolids() {
		return solids;
	}

	/**
	 * Returns the number of vertices in this component.
	 * 
	 * @return int - number of vertices.
	 */
	@Override
	public int countVertices() {
		return vertices.size();
	}

	/**
	 * Returns the number of edges in this component.
	 * 
	 * @return int - number of edges.
	 */
	@Override
	public int countEdges() {
		return edges.size();
	}

	/**
	 * Returns the number of faces in this component.
	 * 
	 * @return int - number if faces.
	 */
	@Override
	public int countFaces() {
		return faces.size();
	}

	/**
	 * Returns the number of solids in this component.
	 * 
	 * @return int - number if solids.
	 */
	@Override
	public int countSolids() {
		return solids.size();
	}

	/**
	 * Tests it the given two elements are equal based on their ID.<br>
	 * 
	 * @param elt1
	 *            NetElement3D
	 * @param elt2
	 *            NetElement3D
	 * @return boolean - true if equal, else otherwise
	 */
	public boolean isEqualID(Element3D elt1, Element3D elt2) {
		if (elt1.getClass() != elt2.getClass())
			return false;
		if (elt1.getID() == elt2.getID())
			return true;
		return false;
	}

	/**
	 * Snaps the vertices of neighbor elements (by epsilon) to their geometric
	 * mean, builds a new vertices TreeMap.
	 */
	public void buildVertices() {

		this.vertices = new TreeMap<Integer, Point3DElement>();
		this.verticeID = 0;

		TreeMap<Point3D, TreeMap<Point3D, Element3D>> treemap = new TreeMap<Point3D, TreeMap<Point3D, Element3D>>();

		Set<Element3D> set = (Set<Element3D>) this.sam.getEntries();

		Iterator<Element3D> it = set.iterator();
		int cnt = 0;
		double time = System.currentTimeMillis();
		int size = set.size();
		while (it.hasNext()) {
			cnt++;
			if (cnt % 10000 == 0) {

				DB3DLogger.logger.log(Level.FINEST, "10.000 entries = "
						+ (System.currentTimeMillis() - time) + "\n"
						+ "Rest takes about: " + ((size - cnt) / 10000)
						* (System.currentTimeMillis() - time));

				time = System.currentTimeMillis();
			}
			Element3D element = it.next();
			Point3D[] points = element.getPoints();
			for (Point3D point : points) {
				boolean contained = false;
				for (Point3D key_point : treemap.keySet()) {
					if (key_point.isGeometryEquivalent(point, this.epsilon)) {
						treemap.get(key_point).put(point, element);
						contained = true;
					} else {
						TreeMap<Point3D, Element3D> key_point_treemap = treemap
								.get(key_point);
						for (Point3D key_point_treemap_key_point : key_point_treemap
								.keySet()) {
							if (key_point_treemap_key_point
									.isGeometryEquivalent(point, this.epsilon)) {
								treemap.get(key_point).put(point, element);
								contained = true;
							}
						}
					}
				}
				if (!contained) {
					TreeMap<Point3D, Element3D> sub_treemap = new TreeMap<Point3D, Element3D>();
					sub_treemap.put(point, element);
					treemap.put(point, sub_treemap);
				}
			}
		}

		for (TreeMap<Point3D, Element3D> subtreemap : treemap.values()) {
			Point3DElement vertex_element = new Point3DElement();
			Point3D vertex_point = vertex_element.getPoints()[0];
			for (Point3D point : subtreemap.keySet()) {
				vertex_point.setCoord(0,
						vertex_point.getCoord(0) + point.getCoord(0));
				vertex_point.setCoord(1,
						vertex_point.getCoord(1) + point.getCoord(1));
				vertex_point.setCoord(2,
						vertex_point.getCoord(2) + point.getCoord(2));
				Element3D element = subtreemap.get(point);
				Point3D[] element_points = element.getPoints();
				for (int id = 0; id < element_points.length; id++) {
					if (element_points[id] == point) {
						element.getPoints()[id] = vertex_point;
					}
				}
			}
			int anz = subtreemap.size();
			vertex_point.setCoord(0, vertex_point.getCoord(0) / anz);
			vertex_point.setCoord(1, vertex_point.getCoord(1) / anz);
			vertex_point.setCoord(2, vertex_point.getCoord(2) / anz);
			vertex_element.id = this.verticeID++;
			this.vertices.put(vertex_element.getID(), vertex_element);
		}

	}

}
