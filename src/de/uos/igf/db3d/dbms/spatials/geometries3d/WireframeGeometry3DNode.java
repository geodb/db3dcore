package de.uos.igf.db3d.dbms.spatials.geometries3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * Class representing the nodes of the wireframe.
 */
public class WireframeGeometry3DNode {

	/**
	 * 
	 */
	private final WireframeGeometry3D wireframe;

	/* set of connections of the wireframe */
	protected HashSet<WireframeGeometry3DNode> connections;

	/* point to be represented as a node */
	protected Point3D point;

	/*
	 * Constructor. Constructs a wireframe node from a point.
	 * 
	 * @param point Point3D from which the node is constructed
	 */
	protected WireframeGeometry3DNode(WireframeGeometry3D wireframe, Point3D point) {
		this.wireframe = wireframe;
		connections = new HashSet<WireframeGeometry3DNode>();
		this.point = point;
	}

	/*
	 * Tests if this has connection to the given node.
	 * 
	 * @param node wireframe node the connection to which is tested
	 * 
	 * @return boolean - true if the connection exists, false otherwise.
	 */
	boolean hasConnection(WireframeGeometry3DNode node) {
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
	void setConnection(WireframeGeometry3DNode node) {
		connections.add(node);
		node.getConnections().add(this);
	}

	/*
	 * Returns a set of all wireframes nodes to which this is connected.
	 * 
	 * @return Set<Wireframe3DNode> - set of wireframe nodes containing all
	 * nodes to which this is connected.
	 */
	Set<WireframeGeometry3DNode> getConnections() {
		return connections;
	}

	/*
	 * Tests if this is connected to any nodes.
	 * 
	 * @return boolean - true if this is connected to at least one node, false
	 * otherwise.
	 */
	boolean hasConnections() {
		return !connections.isEmpty();
	}

	/*
	 * Returns the point from which this wireframe node was constructed.
	 * 
	 * @return Point3D - point from which this wireframe node was constructed.
	 */
	Point3D getPoint3D() {
		return point;
	}

	/*
	 * Returns all existing connections of this in the wireframe as a list of
	 * segments.
	 * 
	 * @return ArrayList<Segment3D> - list of all existing connections if this
	 * in the wireframe.
	 */
	ArrayList<Segment3D> getConnectionsAsSegments() {
		ArrayList<Segment3D> segments = new ArrayList<Segment3D>(
				connections.size());
		Iterator<WireframeGeometry3DNode> it = connections.iterator();
		while (it.hasNext())
			segments.add(new Segment3D(this.point, it.next().getPoint3D(),
					this.wireframe.getSOP()));

		return segments;
	}

}