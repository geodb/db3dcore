/*
 * Sourcecode of the
 *
 * University of Osnabrueck
 * Institute for Geoinformatics and Remote Sensing
 *
 * Copyright (C) Researchgroup Prof. Dr. Martin Breunig
 * 
 * File created on 10.01.2004
 */
package de.uos.igf.db3d.dbms.geom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import de.uos.igf.db3d.dbms.structure.PersistentObject;
import de.uos.igf.db3d.dbms.util.EquivalentableHashMap;
import de.uos.igf.db3d.dbms.util.EquivalentableHashSet;

/**
 * Wireframe3D is a class modeling a set of 3D nodes where every node can be
 * connected with arbitrary other nodes.
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class Wireframe3D implements SimpleGeoObj, PersistentObject {

	/* dimension of the wireframe */
	private byte dimension;

	/* transient hash map with key point3d and value node */
	private transient EquivalentableHashMap mapPointsNodes;

	/* persistent list with nodes */
	private List<Wireframe3DNode> nodesPersistent;

	/* scalar operator */
	private ScalarOperator sop;

	/* id ( exception for wireframe - must not be set) */
	private int id;

	/**
	 * Constructor.<br>
	 * Constructs an empty Wireframe3D.
	 * 
	 * @param sop
	 *            ScalarOperator
	 */
	public Wireframe3D(ScalarOperator sop) {
		this.dimension = -1;
		this.sop = sop;
		this.mapPointsNodes = new EquivalentableHashMap(sop,
				Equivalentable.STRICT_EQUAL);
		this.nodesPersistent = new ArrayList<Wireframe3DNode>();
	}

	/**
	 * Puts content into the wireframe after its initialization.
	 * 
	 * @see com.odi.IPersistentHooks#postInitializeContents()
	 */
	public void postInitializeContents() {
		this.mapPointsNodes = new EquivalentableHashMap(getSOP(),
				Equivalentable.STRICT_EQUAL);
		Iterator<Wireframe3DNode> it = this.getNodesPersistent().iterator();
		Wireframe3DNode node;
		while (it.hasNext()) {
			node = it.next();
			this.mapPointsNodes.put(node.getPoint3D(), node);
		}
	}

	/**
	 * Returns a deep copy of this.
	 * 
	 * @return Wireframe3D - new object.
	 */
	public Wireframe3D copy() {
		Wireframe3D newWF = new Wireframe3D(this.getSOP());
		Point3D[] pts = this.getPoints();
		int length = pts.length;
		for (int i = 0; i < length; i++) {
			newWF.add(new Point3D(pts[i]));
		}
		newWF.add(this.getSegments());
		return newWF;
	}

	/**
	 * Adds a point to the wireframe. If a node at the point coordinates exists,
	 * nothing happens; otherwise a new node with the point coordinates will be
	 * added.
	 * 
	 * @param point
	 *            Point3D to be added
	 */
	public void add(Point3D point) {
		addNode(point);
	}

	/**
	 * Adds the points to the wireframe. If a node at the points coordinates
	 * exists, nothing happens; otherwise a new node with the point coordinates
	 * will be added.
	 * 
	 * @param points
	 *            Point3D[] to be added
	 */
	public void add(Point3D[] points) {
		int length = points.length;
		for (int i = 0; i < length; i++)
			add(points[i]);
	}

	/**
	 * Adds a segment to the wireframe. If nodes at the segments points
	 * coordinates do not exist, new nodes will be added. A connection between
	 * the nodes will be established.
	 * 
	 * @param segment
	 *            Segment3D to be added
	 */
	public void add(Segment3D segment) {
		Point3D[] points = segment.getPoints();
		Wireframe3DNode[] nodes = addNodes(points);
		// make connection
		makeConnection(nodes[0], nodes[1]);
	}

	/**
	 * Adds the segments to the wireframe. If nodes at the segments points
	 * coordinates do not exist, new nodes will be added. A connection between
	 * the nodes will be established.
	 * 
	 * @param segments
	 *            Segment3D[] to be added
	 */
	public void add(Segment3D[] segments) {
		int length = segments.length;
		for (int i = 0; i < length; i++)
			add(segments[i]);
	}

	/**
	 * Adds a triangle to the wireframe. If nodes at the triangles points
	 * coordinates do not exist, new nodes will be added. A connection between
	 * the nodes will be established.
	 * 
	 * @param triangle
	 *            Triangle3D to be added
	 */
	public void add(Triangle3D triangle) {
		Point3D[] points = triangle.getPoints();
		Wireframe3DNode[] nodes = addNodes(points);
		// make connections
		makeConnection(nodes[0], nodes[1]);
		makeConnection(nodes[1], nodes[2]);
		makeConnection(nodes[2], nodes[0]);
	}

	/**
	 * Adds the triangles to the wireframe. If nodes at the triangles points
	 * coordinates do not exist, new nodes will be added. A connection between
	 * the nodes will be established.
	 * 
	 * @param triangles
	 *            Triangle3D[] to be added
	 */
	public void add(Triangle3D[] triangles) {
		int length = triangles.length;
		for (int i = 0; i < length; i++)
			add(triangles[i]);
	}

	/**
	 * Adds a tetrahedron to the wireframe. If nodes at the tetrahedrons points
	 * coordinates do not exist, new nodes will be added. A connection between
	 * the nodes will be established.
	 * 
	 * @param tetra
	 *            Tetrahedron3D to be added
	 */
	public void add(Tetrahedron3D tetra) {
		Point3D[] points = tetra.getPoints();
		Wireframe3DNode[] nodes = addNodes(points);
		// make connections
		makeConnection(nodes[0], nodes[1]);
		makeConnection(nodes[1], nodes[2]);
		makeConnection(nodes[2], nodes[0]);
		makeConnection(nodes[1], nodes[3]);
		makeConnection(nodes[3], nodes[0]);
		makeConnection(nodes[3], nodes[2]);
	}

	/**
	 * Adds the tetrahedrons to the wireframe. If nodes at the tetrahedrons
	 * points coordinates do not exist, new nodes will be added. A connection
	 * between the nodes will be established.
	 * 
	 * @param tetras
	 *            Tetrahedron3D[] to be added
	 */
	public void add(Tetrahedron3D[] tetras) {
		int length = tetras.length;
		for (int i = 0; i < length; i++)
			add(tetras[i]);
	}

	/**
	 * Adds a wireframe to the existing wireframe.
	 * 
	 * @param wireframe
	 *            Wireframe3D to be added
	 */
	public void add(Wireframe3D wireframe) {
		// All connections + isolated nodes
		Iterator it;
		Wireframe3DNode node = null;
		Set<Point3D> nodeSet = new HashSet<Point3D>();
		Set<Segment3D> segmentSet = new EquivalentableHashSet(getSOP(),
				Equivalentable.GEOMETRY_EQUIVALENT);
		it = wireframe.mapPointsNodes.values().iterator();

		while (it.hasNext()) {
			node = (Wireframe3DNode) it.next();
			if (node.hasConnections())
				segmentSet.addAll(node.getConnectionsAsSegments());
			else
				nodeSet.add(node.getPoint3D());
		}

		it = nodeSet.iterator();
		while (it.hasNext())
			add((Point3D) it.next());

		it = segmentSet.iterator();
		while (it.hasNext())
			add((Segment3D) it.next());
	}

	/**
	 * Returns true if this is empty.
	 * 
	 * @return boolean - true if this wireframe has no nodes, false otherwise.
	 */
	public boolean isEmpty() { // Dag
		if (this.countNodes() == 0)
			return true;
		return false;
	}

	/**
	 * Returns the id of this.
	 * 
	 * @return int - id of this wireframe.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Sets the id of this.
	 * 
	 * @param id
	 *            int id to be set
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * Returns projection of this onto given plane.
	 * 
	 * @param plane
	 *            Plane3D onto which this should be projected
	 * 
	 * @return SimpleGeoObj - intersection result (wireframe).
	 */
	public SimpleGeoObj projection(Plane3D plane) { // Dag
		Wireframe3D newWF = this.copy();

		Point3D[] pts = newWF.getPoints();
		int length = pts.length;
		for (int i = 0; i < length; i++)
			pts[i] = (Point3D) pts[i].projection(plane);

		return newWF;
	}

	/**
	 * Returns intersection of this with given plane.
	 * 
	 * @param plane
	 *            Plane3D for intersection
	 * 
	 * @return SimpleGeoObj - intersection result (wireframe), or
	 *         <code>null</code>.
	 */
	public SimpleGeoObj intersection(Plane3D plane) { // Dag

		if (this.countNodes() == 0)
			return null;

		Wireframe3D newWF = new Wireframe3D(this.sop);

		Point3D[] pts = newWF.getPoints();
		int ptsLength = pts.length;
		for (int i = 0; i < ptsLength; i++) {
			if (pts[i].intersects(plane, this.sop))
				newWF.add(pts[i]);
		}

		Segment3D[] sgs = this.getSegments();
		int sgsLength = sgs.length;
		for (int i = 0; i < sgsLength; i++) {
			SimpleGeoObj sgo = sgs[i].intersection(plane, this.sop);
			if (sgo != null)
				if (sgo.getType() == SimpleGeoObj.POINT3D)
					newWF.add((Point3D) sgo);
				else
					newWF.add((Segment3D) sgo);
		}

		if (newWF.isEmpty())
			return null;

		return newWF;
	}

	/**
	 * Returns the number of nodes in this wireframe.
	 * 
	 * @return int - number of nodes.
	 */
	public int countNodes() {
		return mapPointsNodes.size();
	}

	/**
	 * Returns the geometry of the nodes in the wireframe as a Point3D array.
	 * 
	 * @return Point3D[] - point geometries of nodes.
	 */
	public Point3D[] getPoints() {
		Set<Point3D> set = mapPointsNodes.keySet();
		return set.toArray(new Point3D[set.size()]);
	}

	/**
	 * Returns the center for all points of this. <br>
	 * Each coordinate of this center is calculated as the arithmetic mean of
	 * all corresponding coordinates (x, y or z respectively) of all nodes of
	 * this wireframe.
	 * 
	 * @return Point3D[] - centroid.
	 */
	public Point3D getCentroid() { // Dag

		if (this.isEmpty())
			return null;
		Point3D[] nodes = this.getPoints();
		int count = nodes.length;
		int x = 0;
		int y = 0;
		int z = 0;
		for (int i = 0; i < count; i++) {
			x += nodes[i].getCoord(0);
			y += nodes[i].getCoord(1);
			z += nodes[i].getCoord(2);
		}
		x = x / count;
		y = y / count;
		z = z / count;
		return new Point3D(x, y, z);
	}

	/**
	 * Returns the geometry of the connection between nodes in this wireframe as
	 * a Segment3D array.
	 * 
	 * @return Segment3D[] - connections between nodes.
	 */
	public Segment3D[] getSegments() {
		Set<Segment3D> set = new EquivalentableHashSet(getSOP(),
				Equivalentable.GEOMETRY_EQUIVALENT);
		Iterator<Wireframe3DNode> it = mapPointsNodes.values().iterator();
		while (it.hasNext()) {
			set.addAll(it.next().getConnectionsAsSegments());
		}

		return set.toArray(new Segment3D[set.size()]);
	}

	/**
	 * Returns the mbb for all nodes of this.
	 * 
	 * @return MBB3D - or null if no nodes in wireframe.
	 */
	public MBB3D getMBB() {
		if (this.mapPointsNodes.size() > 0) {
			Iterator<Point3D> it = this.mapPointsNodes.keySet().iterator();
			it.hasNext();
			Point3D p = it.next();

			double xMin = p.getX();
			double yMin = p.getY();
			double zMin = p.getZ();
			double xMax = p.getX();
			double yMax = p.getY();
			double zMax = p.getZ();

			double value;
			while (it.hasNext()) {
				p = (Point3D) it.next();
				value = p.getX();
				if (xMin > value)
					xMin = value;
				else if (xMax < value)
					xMax = value;

				value = p.getY();
				if (yMin > value)
					yMin = value;
				else if (yMax < value)
					yMax = value;

				value = p.getZ();
				if (zMin > value)
					zMin = value;
				else if (zMax < value)
					zMax = value;
			}
			Point3D pMin = new Point3D(xMin, yMin, zMin);
			Point3D pMax = new Point3D(xMax, yMax, zMax);
			return new MBB3D(pMin, pMax);
		}
		return null;
	}

	/**
	 * Returns the dimension of this wireframe.<br>
	 * -1 - if wireframe is empty.<br>
	 * 0 - if wireframe has only one node.<br>
	 * 1 - if wireframe has two nodes or all nodes are on a Line3D.<br>
	 * 2 - if wireframe nodes are in the same Plane3D.<br>
	 * 3 - if wireframe nodes make up a volume.
	 * 
	 * @return int - dimension of this wireframe.
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * If <code>countNodes()</code> = 4 and <code>getDimension()</code> = 2,
	 * then this methods triangulates the nodes in the wireframe and returns the
	 * two or three resulting triangles as a <code>Triangle3D</code> array.<br>
	 * There are three possibilities of geometric alignment of four wireframe
	 * points on a plane; the fourth point is lying relative to the triangle
	 * that is spanned by the other three points either:
	 * <ul>
	 * <li>concave, or</li>
	 * <li>convex, or</li>
	 * <li>on a segment of the triangle.</li>
	 * </ul>
	 * In the case of concave arrangement three triangles are returned, in the
	 * other cases two.
	 * 
	 * @author Edgar Butwilowski (revision)
	 * @return array with two or three adjacent triangles or an empty array if
	 *         the assumptions made by this method are not fulfilled. Never
	 *         returns <code>null</code>.
	 * 
	 * 
	 */
	public Triangle3D[] getTriangulated() {
		/*
		 * Idea: producing two triangles whose common segment is the shortest of
		 * the two variants.
		 */
		LinkedList<Triangle3D> result = new LinkedList<Triangle3D>();

		// check assumptions for this method
		if (!(countNodes() == 4 && getDimension() == 2))
			return new Triangle3D[0];

		Point3D[] points = this.getPoints();
		Triangle3D initialTriangle = new Triangle3D(points[0], points[1],
				points[2], getSOP());
		Line3D line;
		int oppositeIndex = -1;
		int[] across = { -1, -1 };
		int pointOnLine = -1;
		int counter = 0;

		boolean alreadyOnLine = false;
		boolean intersectsDoubleLine = false;
		for (int i = 0; i < 3; i++) {
			line = new Line3D(points[3], points[i], sop);
			if (initialTriangle.intersectsInt(line, sop) == 1) {
				if (alreadyOnLine) {
					pointOnLine = i;
					intersectsDoubleLine = true;
				} else {
					oppositeIndex = i;
					alreadyOnLine = true;
				}
			} else {
				across[counter] = i;
				counter++;
			}
		}

		if (intersectsDoubleLine) {
			// case if fourth point lies on a segment of the triangle
			if (points[3].euclideanDistance(points[oppositeIndex]) < points[3]
					.euclideanDistance(points[pointOnLine])) {
				result.add(initialTriangle);
				result.add(new Triangle3D(points[3], points[oppositeIndex],
						points[across[0]], sop));
			} else {
				result.add(initialTriangle);
				result.add(new Triangle3D(points[3], points[pointOnLine],
						points[across[0]], sop));
			}
		} else {
			// case if fourth point lies concave or convex to the triangle
			Segment3D segAlong = new Segment3D(points[3],
					points[oppositeIndex], sop);
			Segment3D segAcross = new Segment3D(points[across[0]],
					points[across[1]], sop);

			if (!segAlong.intersects(segAcross, sop)) {
				// this is the case when not convex

				result.add(new Triangle3D(points[3], points[oppositeIndex],
						points[across[0]], sop));
				result.add(new Triangle3D(points[3], points[oppositeIndex],
						points[across[1]], sop));
				result.add(new Triangle3D(points[across[0]], points[across[1]],
						points[oppositeIndex], sop));

			} else {
				// here we know that wireframe is convex

				double lengthAlong = points[3]
						.euclideanDistance(points[oppositeIndex]);
				double lengthAcross = points[across[0]]
						.euclideanDistance(points[across[1]]);

				if (lengthAlong < lengthAcross) {
					result.add(initialTriangle);
					result.add(new Triangle3D(points[3], points[across[0]],
							points[across[1]], getSOP()));
				} else {
					result.add(new Triangle3D(points[3], points[oppositeIndex],
							points[across[0]], getSOP()));
					result.add(new Triangle3D(points[3], points[oppositeIndex],
							points[across[1]], getSOP()));
				}

			}

		}

		return result.toArray(new Triangle3D[0]);
	}

	/**
	 * Returns the class type of this (Wireframe3D).
	 * 
	 * @return WIREFRAME3D always.
	 * 
	 * @see db3d.dbms.structure.GeoObj#getType()
	 */
	public byte getType() {
		return SimpleGeoObj.WIREFRAME3D;
	}

	/*
	 * Maintains the dimension flag.
	 * 
	 * @param point Point3D added to the Wireframe3D
	 */
	private void maintainDimension(Point3D point) {
		Point3D[] points;
		Line3D line;

		switch (getDimension()) {
		case -1: // this is the first point in the wireframe
			setDimension((byte) 0);
			break;
		case 0: // this is the second point in wireframe
			setDimension((byte) 1);
			break;
		case 1: // a new point - test if on the Line3D
			points = this.getPoints();
			line = new Line3D(points[0], points[1], getSOP());
			if (!line.contains(point, getSOP()))
				setDimension((byte) 2);

			break;
		case 2:
			points = this.getPoints();
			line = new Line3D(points[0], points[1], getSOP());
			int index = 0;
			int length = points.length;
			for (int i = length - 1; i > 1; i--) {
				if (!line.contains(points[i], getSOP())) {
					index = i;
					break;
				}
			}
			if (index == 0) {
				throw new IllegalStateException(
						"Condition should not be reached - no Plane defineable!");
			}

			Plane3D plane = new Plane3D(points[0], points[1], points[index],
					getSOP());

			if (!plane.contains(point, getSOP()))
				setDimension((byte) 3);

			break;
		case 3: // already highest dimension
			break;
		default:
			throw new IllegalStateException(
					"Wrong dimension of wireframe. Dimension must be -1 or 0 or 1 or 2 or 3");
		}
	}

	/*
	 * Adds the point to this wireframe and returns the new or already existing
	 * node for the points coordinates. If adding a new nodes, this method
	 * maintains the dimension attribute.
	 * 
	 * @param point Point3D to be added
	 * 
	 * @return Wireframe3DNode - node.
	 */
	private Wireframe3DNode addNode(Point3D point) {

		if (!mapPointsNodes.containsKey(point)) {
			Wireframe3DNode node = new Wireframe3DNode(point);
			// maintain dimension information
			maintainDimension(point);
			mapPointsNodes.put(point, node);
			// also put in the persistent map
			getNodesPersistent().add(node);

			return node;
		} else
			return (Wireframe3DNode) mapPointsNodes.get(point);
	}

	/*
	 * Makes a connection between the two given nodes unless one already exists.
	 * 
	 * @param node1 Wireframe3DNode
	 * 
	 * @param node2 Wireframe3DNode
	 */
	private void makeConnection(Wireframe3DNode node1, Wireframe3DNode node2) {
		if (!node1.hasConnection(node2))
			node1.setConnection(node2);
	}

	/*
	 * Adds the points to this wireframe and returns the new or already existing
	 * nodes for the points coordinates. If adding a new node, this method
	 * maintains the dimension attribute.
	 * 
	 * @param points Point3D[] to be added
	 * 
	 * @return Wireframe3DNode[] - nodes.
	 */
	private Wireframe3DNode[] addNodes(Point3D[] points) {
		int length = points.length;
		Wireframe3DNode[] nodes = new Wireframe3DNode[length];
		for (int i = 0; i < length; i++)
			nodes[i] = addNode(points[i]);

		return nodes;
	}

	/*
	 * Sets the dimension of this wireframe
	 * 
	 * @param dim the dimension to be assigned to this wireframe
	 */
	private void setDimension(byte dim) {
		this.dimension = dim;
	}

	/*
	 * Returns the scalar operator of this wireframe
	 * 
	 * @return sop - scalar operator of this wireframe.
	 */
	private ScalarOperator getSOP() {
		return sop;
	}

	/*
	 * Returns the persistent list with the nodes of this wireframe.
	 * 
	 * @return List<Wireframe3DNode> with the nodes of this wireframe.
	 */
	private List<Wireframe3DNode> getNodesPersistent() {
		return nodesPersistent;
	}

	@Override
	public String toString() {
		return "Wireframe3D [dimension=" + dimension + ", id=" + id
				+ ", nodesPersistent=" + nodesPersistent + ", sop=" + sop + "]";
	}

	/*
	 * Class representing the nodes of the wireframe.
	 */
	private final class Wireframe3DNode implements PersistentObject {

		/* set of connections of the wireframe */
		private HashSet<Wireframe3DNode> connections;

		/* point to be represented as a node */
		private Point3D point;

		/*
		 * Constructor. Constructs a wireframe node from a point.
		 * 
		 * @param point Point3D from which the node is constructed
		 */
		private Wireframe3DNode(Point3D point) {
			connections = new HashSet<Wireframe3DNode>();
			this.point = point;
		}

		/*
		 * Tests if this has connection to the given node.
		 * 
		 * @param node wireframe node the connection to which is tested
		 * 
		 * @return boolean - true if the connection exists, false otherwise.
		 */
		private boolean hasConnection(Wireframe3DNode node) {
			if (connections.isEmpty()) {
				return false;
			} else {
				return connections.contains(node);
			}
		}

		/*
		 * Sets the connection of this to the given wireframe node.
		 * 
		 * @param node wireframe node to which the connection should be set
		 */
		private void setConnection(Wireframe3DNode node) {
			connections.add(node);
			node.getConnections().add(this);
		}

		/*
		 * Returns a set of all wireframes nodes to which this is connected.
		 * 
		 * @return Set<Wireframe3DNode> - set of wireframe nodes containing all
		 * nodes to which this is connected.
		 */
		private Set<Wireframe3DNode> getConnections() {
			return connections;
		}

		/*
		 * Tests if this is connected to any nodes.
		 * 
		 * @return boolean - true if this is connected to at least one node,
		 * false otherwise.
		 */
		private boolean hasConnections() {
			return !connections.isEmpty();
		}

		/*
		 * Returns the point from which this wireframe node was constructed.
		 * 
		 * @return Point3D - point from which this wireframe node was
		 * constructed.
		 */
		private Point3D getPoint3D() {
			return point;
		}

		/*
		 * Returns all existing connections of this in the wireframe as a list
		 * of segments.
		 * 
		 * @return ArrayList<Segment3D> - list of all existing connections if
		 * this in the wireframe.
		 */
		private ArrayList<Segment3D> getConnectionsAsSegments() {
			ArrayList<Segment3D> segments = new ArrayList<Segment3D>(
					connections.size());
			Iterator<Wireframe3DNode> it = connections.iterator();
			while (it.hasNext())
				segments.add(new Segment3D(this.point, it.next().getPoint3D(),
						getSOP()));

			return segments;
		}

		// @Override
		// public String toString() {
		// return "Wireframe3DNode [" + /*connections=" + connections + */",
		// point="
		// + point + "]";
		// }

	}

}
