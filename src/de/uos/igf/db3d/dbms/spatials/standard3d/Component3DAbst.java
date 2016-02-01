package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import de.uos.igf.db3d.dbms.collections.RStar;
import de.uos.igf.db3d.dbms.collections.SAM;
import de.uos.igf.db3d.dbms.spatials.api.Component3D;
import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.api.Net3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.MBB3D;
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
		this.vertices = new TreeMap<Integer, Point3DElement>();
		this.edges = new TreeMap<Integer, Segment3DElement>();
		this.faces = new TreeMap<Integer, Triangle3DElement>();
		this.solids = new TreeMap<Integer, Tetrahedron3DElement>();
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
	public Set<? extends Element3D> getElementsViaSAM() {
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

		HashMap<Point3D, HashMap<Point3D, Element3D>> hashmap = new HashMap<Point3D, HashMap<Point3D, Element3D>>();

		Set<Element3D> set = (Set<Element3D>) this.getElementsViaSAM();

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
			Element3D element = (Element3D) it.next();
			Point3D[] points = element.getPoints();
			for (Point3D point : points) {
				boolean contained = false;
				for (Point3D key_point : hashmap.keySet()) {
					if (key_point.isGeometryEquivalent(point, this.epsilon)) {
						hashmap.get(key_point).put(point, element);
						contained = true;
					} else {
						HashMap<Point3D, Element3D> key_point_hashmap = hashmap
								.get(key_point);
						for (Point3D key_point_hashmap_key_point : key_point_hashmap
								.keySet()) {
							if (key_point_hashmap_key_point
									.isGeometryEquivalent(point, this.epsilon)) {
								hashmap.get(key_point).put(point, element);
								contained = true;
							}
						}
					}
				}
				if (!contained) {
					HashMap<Point3D, Element3D> sub_hashmap = new HashMap<Point3D, Element3D>();
					sub_hashmap.put(point, element);
					hashmap.put(point, sub_hashmap);
				}
			}
		}

		for (HashMap<Point3D, Element3D> subtreemap : hashmap.values()) {
			Point3DElement vertex_element = new Point3DElement();
			Point3D vertex_point = vertex_element.getPoints()[0];
			for (Point3D point : subtreemap.keySet()) {
				vertex_point.setCoord(0,
						vertex_point.getCoord(0) + point.getCoord(0));
				vertex_point.setCoord(1,
						vertex_point.getCoord(1) + point.getCoord(1));
				vertex_point.setCoord(2,
						vertex_point.getCoord(2) + point.getCoord(2));
			}
			int anz = subtreemap.size();
			vertex_point.setCoord(0, vertex_point.getCoord(0) / anz);
			vertex_point.setCoord(1, vertex_point.getCoord(1) / anz);
			vertex_point.setCoord(2, vertex_point.getCoord(2) / anz);
			vertex_element.id = this.verticeID++;
			this.vertices.put(vertex_element.getID(), vertex_element);
		}

	}

	@Override
	public String toString() {

		String string = "\nComponent " + this.id;
		string += "\nNumberOfElements: " + this.countElements();
		string += "\nelements:";
		Set<Element3D> set = (Set<Element3D>) this.getElementsViaSAM();
		Iterator<Element3D> it = set.iterator();
		while (it.hasNext()) {
			Element3D element = (Element3D) it.next();
			string += "\n";
			string += element.toString();
		}
		string += "\n\nvertices:";
		for (Point3DElement vertex : this.vertices.values()) {
			string += "\n";
			string += vertex.toString();
		}
		string += "\n\nedges:";
		for (Segment3DElement edge : this.edges.values()) {
			string += "\n";
			string += edge.toString();
		}
		string += "\n\nfaces:";
		for (Triangle3DElement face : this.faces.values()) {
			string += "\n";
			string += face.toString();
		}
		string += "\n\nsolids:";
		for (Tetrahedron3DElement solid : this.solids.values()) {
			string += "\n";
			string += solid.toString();
		}

		return string;
	}

	@Override
	public MBB3D getMBB() {

		if (this.getElementsViaSAM().isEmpty()) {
			return null;
		}

		Set<Element3D> elements = (Set<Element3D>) this.getElementsViaSAM();

		MBB3D neu = null;

		for (Element3D element : elements) {
			if (neu == null) {
				neu = element.getMBB().copy();
			} else {
				neu = neu.union(element.getMBB(), this.getGeoEpsilon());
			}
		}

		return neu;
	}

}
