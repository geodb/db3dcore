/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.util;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Equivalentable;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.structure.GeoObj;
import de.uos.igf.db3d.dbms.structure.PersistentObject;

// IDEAS: Pool for RStarSplitResult instead of creating temporary Objects.

/**
 * Class RStar - implementation of the RStar spatial access method. <br>
 * <br>
 * Beckmann, N. / Kriegel H.-P. : The R*-tree: An Efficient and Robust Access
 * Method for Points and Rectangles - In Proceedings of the ACM SIGMOND 1990,
 * pages 322 - 331. <br>
 * <br>
 * Portions of the code are ported from the C++ RStar implementation of
 * GeoToolKit University of Bonn - Wolfgang Mueller. <br>
 * NOTE: The insert and remove methods are based on standard equals comparison.
 * If you have objects indexed which needs a comparison based on geometry
 * equivalence you have to search the object first, test for geometry
 * equivalence and then use the retrieved object for removal !<br>
 * <br>
 * Default Serialization (Serializable) - WBaer 07082003
 */
public final class RStar implements SAM, PersistentObject {

	// Members

	/* root node */
	private Node root;

	/* mMin -> m */
	private short mMin;

	/* mMax -> M */
	private short mMax;

	/* internal count variable */
	private int count;

	/* height of tree */
	private int height;

	/* height difference */
	private int heightDiff;

	/* forcedReInsertCount - RStar 4.1, p. 327, p */
	private short forcedReInsertCount;

	/* scalar operator */
	private ScalarOperator sop;

	private final static int INTERSECTS_STRICT_PREDICATE = 0;
	private final static int CONTAINS_STRICT_PREDICATE = 1;
	private final static int INSIDE_STRICT_PREDICATE = 2;

	private final static int INTERSECTS_PREDICATE = 10;
	private final static int CONTAINS_PREDICATE = 11;
	private final static int INSIDE_PREDICATE = 12;

	private final static int EQUALS_PREDICATE = 5;

	// methods

	/**
	 * Constructor.<br>
	 * The minimum number of entries per node is computed as <= mMax/2.
	 * 
	 * @param mMax
	 *            maximum entries per node (must be 4 or more)
	 * @throws IllegalArgumentException
	 *             if mMax is less than 4 or ScalarOperator = null.
	 */
	public RStar(int mMax, ScalarOperator sop) throws IllegalArgumentException {
		if (mMax < 4 || sop == null)
			throw new IllegalArgumentException("IllegalParameters");

		this.mMax = (short) mMax;
		this.mMin = (short) (mMax / 2);
		this.count = 0;
		this.height = 0;
		this.heightDiff = 0;
		this.forcedReInsertCount = (short) (0.3 * mMax);
		this.sop = sop;
		this.root = new Node();
	}

	/**
	 * Inserts the given GeoObj into the RStar.
	 * 
	 * @param obj
	 *            GeoObj to insert.
	 * @return boolean - true if successfull, false otherwise
	 * @throws IllegalArgumentException
	 *             if mbb of given GeoObject is null
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public synchronized boolean insert(GeoObj obj) {
		if (obj == null)
			return false;
		MBB3D mbb = obj.getMBB();
		// Here an IllegalArgumentException can be thrown.
		if (mbb == null)
			throw new IllegalArgumentException(
					"MBB3D of given GeoObject is null.");

		// initialize an array for marking level based forcedreinsertion
		boolean[] levelFRI = new boolean[this.getHeight() + 10];
		for (int i = 0; i < this.getHeight() + 10; i++)
			levelFRI[i] = false;

		// new insert - reset heightdiff
		this.setHeightDiff(0);

		// search subtree - insert in leaf -> -1; deep of root = 0
		Node node = this.getRoot().chooseSubtree(mbb, -1, 0);
		// Here an IllegalArgumentException can be thrown.

		// test if object is not already in entry
		if (node.findEntryIndex(obj) == -1) {
			// make a new Entry object
			Entry entry = new Entry(mbb, obj);
			// append entry to node
			node.append(entry);
			// check of overflow - propagate changes upwards
			node.adjustNode(levelFRI);

			// increment counter
			this.incCount();
			return true;
		} else { // already in entry return false
			return false;
		}
	}

	/**
	 * Removes the given GeoObj from the RStar.
	 * 
	 * @param obj
	 *            GeoObj to remove.
	 * @return boolean - true if successful, false otherwise
	 * @throws IllegalArgumentException
	 *             if mbb of given GeoObject is null.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 * @throws IllegalArgumentException
	 *             if the index of the point of the tetrahedron is not in the
	 *             interval [0;3]. The exception originates in the method
	 *             getPoint(int) of the class Tetrahedron3D.
	 * @throws IllegalArgumentException
	 *             - if index of a triangle point is not 0, 1 or 2. The
	 *             exception originates in the method getPoint(int) of the class
	 *             Triangle3D.
	 */
	public synchronized boolean remove(GeoObj obj) {
		MBB3D mbb = obj.getMBB();
		// Here an IllegalArgumentException can be thrown.
		if (mbb == null)
			throw new IllegalArgumentException(
					"MBB3D of given GeoObject is null.");

		// initialize an array for marking level based forcedreinsertion
		boolean[] levelFRI = new boolean[this.getHeight() + 10];
		for (int i = 0; i < this.getHeight() + 10; i++)
			levelFRI[i] = false;

		// new removal - reset heightdiff
		this.setHeightDiff(0);

		// search for node with remove object
		Node n = this.getRoot().findNode(obj, mbb);

		boolean object_found = false;

		if (n != null) {
			// System.out.println("Geoobj " + obj.toString());

			int index = n.findEntryIndex(obj);

			if (index >= 0) {
				// System.out.println("We found the index");
				object_found = true;
				// List for reinsertion
				List entries = n.remove(index);
				// Here an IllegalArgumentException can be thrown.

				// System.out.println("Constructed list for reinsertion");
				// tree should be ok - now we can start reinsertion
				if (!entries.isEmpty()) {
					// System.out.println("Starting reinsertion");
					Entry e = null;
					Node helpNode = null;
					for (Iterator it = entries.iterator(); it.hasNext();) {
						e = (Entry) it.next();
						helpNode = this.getRoot().chooseSubtree(e.getMBB(),
								e.getHeight() + this.getHeightDiff(), 0);
						// Here an IllegalArgumentException can be thrown.
						helpNode.append(e);
						if (helpNode.adjustNode(levelFRI))
							this.adjustHeightDiff(1);
					}
				}
			}
		}
		if (!object_found)
			return false;

		this.decCount();
		return true;
	}

	/**
	 * Retrieves entries based on the retrieve type.<br>
	 * retrieve type possiblities:<br>
	 * 
	 * @param mbb
	 *            test MBB3D
	 * @param retrieveType
	 *            int the retrieve type
	 * @return Set - result Set.
	 */
	protected Set retrieve(MBB3D mbb, int retrieveType) {
		Set set = new HashSet();
		this.getRoot().retrieve(mbb, retrieveType, set);
		return set;
	}

	/**
	 * Retrieves the n nearest neighbour objects to given point.<br>
	 * The result is an array of NNResult objects. Each result object holds the
	 * distance for a n-th nearest neighbour and the reference to the
	 * corresponding object. The result array is sorted in ascending order based
	 * on the distance value.
	 * 
	 * @param number
	 *            specifies how much nearest neighbours should be retrieved
	 * @param point
	 *            -query Point3D object
	 * @return NNResult[] - array of NNResult objects of length number.
	 */
	protected NNResult[] nNNSearch(int number, Point3D point)
			throws IllegalArgumentException {
		return this.getRoot().nNNSearch(number, point);
	}

	/**
	 * Returns the maximum number of entries per Node.<br>
	 * See - RStar publication.
	 * 
	 * @return int - max entries.
	 */
	protected int getMMax() {
		return mMax;
	}

	/**
	 * Returns the minimum number of entries per Node.<br>
	 * See - RStar publication.
	 * 
	 * @return short - min entries.
	 */
	protected short getMMin() {
		return mMin;
	}

	/**
	 * Returns the root node.
	 * 
	 * @return Node - root node.
	 */
	protected Node getRoot() {
		return root;
	}

	/*
	 * Sets the count to the given value
	 * 
	 * @param value int to which the count should be set
	 */
	private void setCount(int value) {
		this.count = value;
	}

	/*
	 * Increments the counter
	 */
	private void incCount() {
		setCount(getCount() + 1);
	}

	/*
	 * Decrements the counter
	 */
	private void decCount() {
		setCount(getCount() - 1);
	}

	/**
	 * Sets the root node and adjust the height.
	 * 
	 * @param root
	 *            the new root node to set
	 * @param heightdifference
	 *            height difference to add
	 */
	protected void setRoot(Node root, int heightdifference) {
		this.root = root;
		this.height = this.height + heightdifference;
	}

	/**
	 * Adjusts the height difference of the tree with given value.
	 * 
	 * @param diff
	 *            difference as int
	 */
	protected void adjustHeightDiff(int diff) {
		setHeightDiff(getHeightDiff() + diff);
	}

	/**
	 * Returns the height of the tree.
	 * 
	 * @return int - height.
	 */
	protected int getHeight() {
		return height;
	}

	/**
	 * Returns the height difference of the tree.
	 * 
	 * @return int - height difference.
	 */
	protected int getHeightDiff() {
		return heightDiff;
	}

	/*
	 * Sets the height difference of the tree to given value.
	 * 
	 * @param value new height difference.
	 */
	private void setHeightDiff(int value) {
		this.heightDiff = value;
	}

	/**
	 * Returns the number of entries to forced for a reinsert.
	 * 
	 * @return int - number of entries to force reinsertion.
	 */
	protected int getForcedReInsertCount() {
		return forcedReInsertCount;
	}

	/**
	 * Test method.
	 * 
	 * @param out
	 *            PrintStream for String output into file ...
	 */
	public void print(PrintStream out) {

		out.println("Ausgabe R*-Baum");
		out.println("Anzahl der Eintraege: " + this.getCount());
		out.println("");

		this.getRoot().printRec(out, 0);
	}

	// SAM methods

	/**
	 * Returns the number of objects in the RStar.
	 * 
	 * @return int - number of objects.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Returns the MBB3D of the objects in this.
	 * 
	 * @return MBB3D - MBB3D of this.
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public MBB3D getMBB() {
		if (this.getCount() == 0)
			return null;
		else
			return this.root.computeNodeMBB();
	}

	/**
	 * Retrieves all entries in the SAM.
	 * 
	 * @return Set - all entries.
	 */
	public Set getEntries() {
		Set set = new HashSet();
		this.root.retrieveAll(set);
		return set;
	}

	/**
	 * Returns the set of SpatialObject3D3D objects which intersect the given
	 * MBB3D strict.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set intersectsStrict(MBB3D mbb) {
		return this.retrieve(mbb, INTERSECTS_STRICT_PREDICATE);
	}

	/**
	 * Returns the set of SpatialObject3D3D objects which intersect the given
	 * MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set intersects(MBB3D mbb) {
		return this.retrieve(mbb, INTERSECTS_PREDICATE);
	}

	/**
	 * Returns the set of SpatialObject3D3D objects which contain the given
	 * MBB3D strict.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set containsStrict(MBB3D mbb) {
		return this.retrieve(mbb, CONTAINS_STRICT_PREDICATE);
	}

	/**
	 * Returns the set of SpatialObject3D3D objects which contain the given
	 * MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set contains(MBB3D mbb) {
		return this.retrieve(mbb, CONTAINS_PREDICATE);
	}

	/**
	 *Returns the set of SpatialObject3D3D objects which are inside the given
	 * MBB3D strict.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set insideStrict(MBB3D mbb) {
		return this.retrieve(mbb, INSIDE_STRICT_PREDICATE);
	}

	/**
	 *Returns the set of SpatialObject3D3D objects which are inside the given
	 * MBB3D.
	 * 
	 * @param mbb
	 *            the MBB3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public Set inside(MBB3D mbb) {
		return this.retrieve(mbb, INSIDE_PREDICATE);
	}

	/**
	 * Returns the set of SpatialObject3D3D objects which contain the given
	 * point.
	 * 
	 * @param point
	 *            the Point3D object for test
	 * @return Set - a Set object containing the result.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Set contains(Point3D point) {
		return this.contains(new MBB3D(point, point));
	}

	/**
	 * Returns the set of the <code>number</code> SpatialObject3D3D objects
	 * which are the nearest neighbours of the given point.
	 * 
	 * @param number
	 *            number of nearest neighbours to search
	 * @param point
	 *            the Point3D object for test
	 * @return Set - a Set object containing the result.
	 */
	public NNResult[] nearest(int number, Point3D point) {
		return this.nNNSearch(number, point);
	}

	/**
	 * Performs a spatial intersection join on this RStar with the given RStar.<br>
	 * 
	 * @param rstar
	 *            RStar object for intersection join computation
	 * @return JoinResult[] - result of intersection join as JoinResult array.
	 */
	public JoinResult[] intersectionJoin(RStar rstar) {
		List resultList = new ArrayList();
		resultList = this.getRoot().join(rstar.getRoot(), resultList,
				INTERSECTS_STRICT_PREDICATE);
		return processJoinResult(resultList);
	}

	/**
	 * Performs a spatial equals join on this RStar with the given RStar.<br>
	 * 
	 * @param rstar
	 *            RStar object for equals join computation
	 * @return JoinResult[] - result of equals join as JoinResult array.
	 */
	public JoinResult[] equalsJoin(RStar rstar) {
		List resultList = new ArrayList();
		resultList = this.getRoot().join(rstar.getRoot(), resultList,
				EQUALS_PREDICATE);
		return processJoinResult(resultList);
	}

	private JoinResult[] processJoinResult(List resultList) {
		JoinResult[] result = new JoinResult[resultList.size() / 2];
		int j = 0;
		for (int i = 0; i < resultList.size(); i = i + 2) {
			result[j] = new JoinResult(resultList.get(i), resultList.get(i + 1));
			j++;
		}
		return result;
	}

	/**
	 * Returns the scalar operator of this.
	 * 
	 * @return ScalarOperator of this.
	 */
	protected ScalarOperator getSOP() {
		return this.sop;
	}

	/**
	 * Converts this to string.
	 * 
	 * @return String with the information of this.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer b = new StringBuffer("<RStar mMin=\"" + getMMin()
				+ "\" mMax=\"" + getMMax() + "\">");
		b.append(getRoot().toString());
		b.append("</RStar>");
		return b.toString();
	}

	/**
	 * <p>
	 * Node in the RStar tree.<br>
	 * 
	 * Default Serialization (Serializable) - WBaer 07082003
	 */
	private final class Node implements PersistentObject, Serializable {

		// Members

		/* array of Nodeentries */
		private Entry[] entries;

		/* reference to father node */
		private Node father;

		/* currently used entries in node */
		private short used;

		/* index in entryarray of father for this node */
		private short fatherEntryIndex;

		/* leaf flag */
		private boolean leaf;

		// Methods

		/*
		 * Constructor.
		 */
		protected Node() {
			this.entries = new Entry[getMMax() + 1];
			this.father = null;
			this.used = 0;
			this.fatherEntryIndex = 0;
			this.leaf = true;
		}

		/*
		 * Performs a join on this node and the given based on given
		 * PREDICATE.<br> The algorithm tests for PREDICATE if both nodes are
		 * leafs and puts the resulting object pairs (first is a "this" object,
		 * second of second node) into the given result List object.
		 * 
		 * @param second second Node object
		 * 
		 * @param result List object for the result pairs
		 * 
		 * @return List - the result List.
		 */
		protected List join(Node second, List result, int predicate) {
			ScalarOperator _sop = getSOP();
			// both leaf - check for their PREDICATE pairs
			if (this.isLeaf() && second.isLeaf()) {
				for (int i = 0; i < this.getUsed(); i++) {
					for (int j = 0; j < second.getUsed(); j++) {
						// check for PREDICATE - if put in result list "this"
						// objects always first
						switch (predicate) {
						case EQUALS_PREDICATE:
							if (this.getEntry(i).getMBB().isEqual(
									second.getEntry(j).getMBB(), _sop)) {
								result.add(this.getEntry(i).getSon());
								result.add(second.getEntry(j).getSon());
							}
							break;
						case INTERSECTS_STRICT_PREDICATE:
							if (this.getEntry(i).getMBB().intersectsStrict(
									second.getEntry(j).getMBB(), _sop)) {
								result.add(this.getEntry(i).getSon());
								result.add(second.getEntry(j).getSon());
							}
							break;
						default:
							break;
						}
					}
				}
			} else {
				// both inner nodes - check their entries and go recursivly down
				if (!(this.isLeaf()) && !(second.isLeaf())) {
					for (int i = 0; i < this.getUsed(); i++) {
						for (int j = 0; j < second.getUsed(); j++) {
							// check for intersection - if intersects perform
							// intersection join on their son nodes
							if (this.getEntry(i).getMBB().intersectsStrict(
									second.getEntry(j).getMBB(), _sop))
								result = ((Node) this.getEntry(i).getSon())
										.join((Node) second.getEntry(j)
												.getSon(), result, predicate);
						}
					}
				} else { // one is leaf, the other inner node
					if (this.isLeaf()) { // this is leaf
						// check for all leaf entry objects of second inner node
						// for PREDICATE
						for (int i = 0; i < this.getUsed(); i++) {
							Set retrieveSet = new HashSet();

							switch (predicate) {
							case INTERSECTS_STRICT_PREDICATE:

								second.retrieve(this.getEntry(i).getMBB(),
										INTERSECTS_STRICT_PREDICATE,
										retrieveSet);
								break;
							case EQUALS_PREDICATE:

								second.retrieve(this.getEntry(i).getMBB(),
										EQUALS_PREDICATE, retrieveSet);
								break;
							default:
								break;
							}
							for (Iterator it = retrieveSet.iterator(); it
									.hasNext();) {
								result.add(this.getEntry(i).getSon());
								result.add(it.next());
							}
						}
					} else { // second is leaf
						// check for all leaf entry objects of this inner node
						// for PREDICATE
						for (int i = 0; i < second.getUsed(); i++) {
							Set retrieveSet = new HashSet();
							switch (predicate) {
							case INTERSECTS_STRICT_PREDICATE:
								second.retrieve(this.getEntry(i).getMBB(),
										INTERSECTS_STRICT_PREDICATE,
										retrieveSet);
								break;
							case EQUALS_PREDICATE:
								second.retrieve(this.getEntry(i).getMBB(),
										EQUALS_PREDICATE, retrieveSet);
								break;
							default:
								break;
							}

							for (Iterator it = retrieveSet.iterator(); it
									.hasNext();) {
								result.add(it.next());
								result.add(second.getEntry(i).getSon());
							}
						}
					}
				}
			}
			return result;
		}

		/*
		 * Retrieves entries based on the retrieve type.<br> retrieve type
		 * possibilities:<br> 0 - intersects - result entries mbbs must
		 * intersect with given MBB3D<br> 1 - contain - result entries mbbs must
		 * contain the given MBB3D<br> 2 - inside - result entries mbbs have to
		 * be inside the given MBB3D<br>
		 * 
		 * @param mbb test MBB3D
		 * 
		 * @param retrieveType int retrieve type
		 * 
		 * @param set result Set
		 * 
		 * @throws DB3DException.
		 */
		protected void retrieve(MBB3D mbb, int retrieveType, Set set) {

			for (int i = 0; i < getUsed(); i++) {
				Entry iEntry = this.getEntry(i);
				MBB3D thisMBB = iEntry.getMBB();

				switch (retrieveType) {

				case EQUALS_PREDICATE:
					if (isLeaf()) { // here test with equals
						if (thisMBB.isEqual(mbb, getSOP()))
							set.add(iEntry.getSon());
					} else { // the node can only contain equal boxes if search
						// box is contained in it
						if (thisMBB.contains(mbb, getSOP()))
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				case INTERSECTS_STRICT_PREDICATE:
					if (thisMBB.intersectsStrict(mbb, getSOP())) {
						if (isLeaf())
							set.add(iEntry.getSon());
						else
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				case CONTAINS_STRICT_PREDICATE:
					if (thisMBB.containsStrict(mbb, getSOP())) {
						if (isLeaf())
							set.add(iEntry.getSon());
						else
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				case INSIDE_STRICT_PREDICATE:
					if (isLeaf()) { // here test with inside
						if (thisMBB.insideStrict(mbb, getSOP()))
							set.add(iEntry.getSon());
					} else { // union mbb of internal node test
						if (thisMBB.intersects(mbb, getSOP()))
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				case INTERSECTS_PREDICATE:
					if (thisMBB.intersects(mbb, getSOP())) {
						if (isLeaf())
							set.add(iEntry.getSon());
						else
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				case CONTAINS_PREDICATE:
					if (thisMBB.contains(mbb, getSOP())) {
						if (isLeaf())
							set.add(iEntry.getSon());
						else
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				case INSIDE_PREDICATE: // inside - special case - go down with
					// intersects and test only on leafs
					// with inside
					if (isLeaf()) { // here test with inside
						if (thisMBB.inside(mbb, getSOP()))
							set.add(iEntry.getSon());
					} else { // union mbb of internal node test with intersects
						if (thisMBB.intersects(mbb, getSOP()))
							((Node) iEntry.getSon()).retrieve(mbb,
									retrieveType, set);
					}
					break;

				}
			}
		}

		/*
		 * Retrieves the given number of nearest neighbours to the query point.
		 * 
		 * @param number number of neighbours to retrieve
		 * 
		 * @param point query Point as Point3D
		 * 
		 * @return NNResult[] - result array of NNResult objects.
		 */
		protected NNResult[] nNNSearch(int number, Point3D point) {
			if (number == 1)
				return new NNResult[] { singleNNSearchPrivate(point,
						new NNResultImpl()) };

			NNResultImpl[] result = new NNResultImpl[number];
			for (int i = 0; i < number; i++)
				result[i] = new NNResultImpl();

			return nNNSearchPrivate(point, result);
		}

		/*
		 * Private method for the n-th nearest neighbour search.
		 * 
		 * @param point query point
		 * 
		 * @param result array for result
		 * 
		 * @return NNResultImpl[] - result.
		 */
		private NNResultImpl[] nNNSearchPrivate(Point3D point,
				NNResultImpl[] result) {

			if (isLeaf()) {
				for (int i = 0; i < this.getUsed(); i++) {
					double dist = this.getEntry(i).getMBB().minDistSquare(
							point, getSOP());
					if (dist < result[result.length - 1].getDistance())
						result[result.length - 1].setNNResult(dist, this
								.getEntry(i).getSon()); // update nth data entry
					// sort
					Arrays.sort(result);
				}
			} else {
				// generate ActiveBranchList of node
				ABL[] abl = new ABL[this.getUsed()];
				Node help = null;
				for (int i = 0; i < this.getUsed(); i++) {
					help = (Node) this.getEntry(i).getSon();
					abl[i] = new ABL(help, help.getNodeMBB().minDistSquare(
							point, getSOP()));
				}
				// sort activebranchlist
				Arrays.sort(abl);

				for (int i = 0; i < abl.length; i++) {
					// apply heuristic 3
					if (abl[i].minDist <= result[result.length - 1]
							.getDistance()) {
						result = abl[i].node.nNNSearchPrivate(point, result);
					}
				}
			}
			return result;
		}

		/*
		 * Private method for the single nearest neighbour search.
		 * 
		 * @param point query point
		 * 
		 * @param result NNResultImpl object
		 * 
		 * @return NNResultImpl - result.
		 */
		private NNResultImpl singleNNSearchPrivate(Point3D point,
				NNResultImpl result) {

			if (isLeaf()) {
				for (int i = 0; i < this.getUsed(); i++) {
					double dist = this.getEntry(i).getMBB().minDistSquare(
							point, getSOP());
					if (dist < result.getDistance())
						result.setNNResult(dist, this.getEntry(i).getSon()); // update
					// data
					// entry
				}
			} else {
				// generate ActiveBranchList of node
				ABL[] abl = new ABL[this.getUsed()];
				Node help = null;
				for (int i = 0; i < this.getUsed(); i++) {
					help = (Node) this.getEntry(i).getSon();
					abl[i] = new ABL(help, help.getNodeMBB().minDistSquare(
							point, getSOP()));
				}
				// sort activebranchlist
				Arrays.sort(abl);

				for (int i = 0; i < abl.length; i++) {
					// apply heuristic 3
					if (abl[i].minDist <= result.getDistance()) {
						result = abl[i].node.singleNNSearchPrivate(point,
								result);
					}
				}
			}
			return result;
		}

		/*
		 * Retrieves all entries of the node if it is leaf, else goes recursivly
		 * down the tree.
		 * 
		 * @param set - result Set
		 */
		protected void retrieveAll(Set set) {
			for (int i = 0; i < this.getUsed(); i++) {
				Entry iEntry = this.getEntry(i);

				if (isLeaf())
					set.add(iEntry.getSon());
				else
					((Node) iEntry.getSon()).retrieveAll(set);
			}
		}

		/*
		 * Searches, if the given object is a entry of this.
		 * 
		 * @param obj - Entry object to search
		 * 
		 * @return int - index if found, -1 otherwise.
		 */
		protected int findEntryIndex(Object obj) {
			for (int i = 0; i < this.getUsed(); i++) {
				if (getEntry(i).getSon().equals(obj))
					return i;
			}
			return -1;
		}

		/*
		 * Finds the node which contains the given obj associated with given
		 * MBB3D
		 * 
		 * @param obj Object contained in searched node
		 * 
		 * @param mbb MBB3D of obj
		 * 
		 * @return Node - Node containing the searched object or null if not
		 * found.
		 */
		protected Node findNode(Object obj, MBB3D mbb) {
			Node n = null;
			Entry entry = null;
			for (int i = 0; i < this.getUsed(); i++) {
				entry = this.getEntry(i);
				if (this.isLeaf()) {
					if (obj instanceof Equivalentable) {
						// System.out.println("Equivalentable");
						Equivalentable son = (Equivalentable) entry.getSon();
						Equivalentable arg = (Equivalentable) obj;
						if (son.isGeometryEquivalent(arg, getSOP())) {
							// System.out.println("Geoobj found " +
							// son.toString());
							return this;
						}
					} else {
						// System.out.println("Not Equivalentable");
						if (entry.getSon().equals(obj))
							return this;
					}
				} else {
					if (mbb.intersects(entry.getMBB(), getSOP())) {
						n = ((Node) entry.getSon()).findNode(obj, mbb);
						if (n != null)
							return n;
					}
				}
			}
			return null;
		}

		/*
		 * Chooses the correct subtree for insert of mbb.
		 * 
		 * @param mbb the MBB3D for insert
		 * 
		 * @param level the level (height) for insert (-1 is leaf insert)
		 * 
		 * @param currentHeight the current height of this
		 * 
		 * @return Node - node for insert.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		protected Node chooseSubtree(MBB3D mbb, int level, int currentHeight) {
			// if wanted insertion level == currentHeight OR we are on leaf
			// level
			// return this because we only want a subtree and not the data
			if ((level == currentHeight) || this.isLeaf())
				return this;
			else {
				// check the entries of this (-> subtrees) for best son
				// and go on until level or leaf
				Node node = this.chooseNode(mbb);
				// Here an IllegalArgumentException can be thrown.
				return node.chooseSubtree(mbb, level, currentHeight + 1);
				// Here an IllegalArgumentException can be thrown.
			}
		}

		/*
		 * Appends the given entry to the entries of this. Does nothing else (no
		 * adjustment ...)
		 * 
		 * @param entry - Entry to append
		 * 
		 * @return boolean - true if successful, false otherwise.
		 */
		protected boolean append(Entry entry) {

			if (getUsed() <= getMMax()) {
				// set entry at last position (index starts with 0)
				setEntry(entry, getUsed());
				incUsed();
			} else
				return false;

			// if not in leaf level - it is a insertion of a subtree at a
			// certain level
			// so we must update the father of the son of the entry
			// we incremented used after insertion - so now the last entry is
			// used - 1
			if ((!isLeaf()) && this.getEntry(getUsed() - 1).getSon() != null) {
				((Node) this.getEntry(getUsed() - 1).getSon()).setFather(this,
						getUsed() - 1);
			}
			return true;
		}

		/*
		 * Removes the entry at given index from this node and adjusts the tree
		 * after removal, returning entries for reinsertion as List
		 * 
		 * @param index index of entry to remove
		 * 
		 * @return List - entries for reinsertion.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		protected List remove(int index) {

			this.cutAndCompressEntry(index);

			Set delete = new HashSet();
			List _entries = new ArrayList();
			adjustNode_4remove(_entries, delete);
			// Here an IllegalArgumentException can be thrown.

			// tree should be ok now
			// database specific deletion code
			if (!delete.isEmpty()) {
				Iterator it = delete.iterator();
				while (it.hasNext()) {
					it.next();
					// database specific deletion code
				}
			}

			return _entries;
		}

		/*
		 * Adjusts the node after a removal and looks for underflow in the
		 * entries.
		 * 
		 * @param entries List with entries for reinsertion
		 * 
		 * @param delete Set for nodes to be deleted on the way up (DB specific)
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		protected void adjustNode_4remove(List entries, Set delete) {

			if (this.getFather() != null) { // no root
				if (this.getUsed() < getMMin()) {
					// node has too less entries -so it gets killed
					// delete this from father and insert entries in set for
					// later reinsertion
					if (this.getFatherEntryIndex() >= 0) {
						// delete this from father
						this.getFather().cutAndCompressEntry(
								this.getFatherEntryIndex());
						// copy all entries in set
						int height = this.computeHeight();
						// Here an IllegalArgumentException can be thrown.
						Entry entry = null;
						for (int i = 0; i < this.getUsed(); i++) {
							entry = this.getEntry(i);
							entry.putHeight(height);
							entries.add(0, entry);
						}
						// put this to delete set - DB specific code
						delete.add(this);
					}
				} else {
					// ok node has not too less entries - so only update his mbb
					this.adjustNodeMBB();
					// Here an IllegalArgumentException can be thrown.
				}
				// go to father node and look if there is something to do
				this.getFather().adjustNode_4remove(entries, delete);
				// Here an IllegalArgumentException can be thrown.
			}
		}

		/*
		 * Adjusts the node after an insertion (of subtree or data entry). Tests
		 * if an overflow has taken place and processes if. Tests if a split
		 * occurred on root node ....
		 * 
		 * @param levelFRI array for marking level based forced reinsertion
		 * 
		 * @return boolean - true if height of tree changed, false otherwise.
		 * 
		 * @throws IllegalArgumentException if an attempt is made to construct a
		 * MBB3D whose maximum point is not greater than its minimum point.
		 */
		protected boolean adjustNode(boolean[] levelFRI) {

			boolean split = false;
			boolean mbbUpdate = false;
			Node newNode = null;

			// check for overflow
			if (getUsed() > getMMax()) {
				// overflow - treatment after RStar algorithm 4.3/I3 p. 327
				// current height of node this.
				int currentHeight = computeHeight();
				// Here an IllegalArgumentException can be thrown.

				// check if a forced reinsertion or a split should occur
				// if not root and at this level has no forcedreinsert already
				// occured
				if ((levelFRI[currentHeight] == false) && currentHeight > 0) {
					// forcedReinsert - set levelFRI to happened
					levelFRI[currentHeight] = true;
					forcedReInsert(levelFRI);
					// Here an IllegalArgumentException can be thrown.
				} else {
					// split
					newNode = splitNode();
					split = true;

					// test if Error condition
					if (getUsed() < getMMin() || newNode.getUsed() < getMMin()) {
						// do nothing. (for test purposes insert
						// System.out.println(...) here)
					}

					// ////////////////////////////////////////// /
					// another check
					if (!isLeaf()) { // if this was not a leaf before,
						newNode.setLeaf(false);
						// then it's not a leaf in the row
						newNode.adjustNodeSons(); // dann aber auch Soehne
						// then also hang the sons again
					}
				}
			}
			// adjust the node after forcedreinsert or split
			// test if inner node and not root
			if (this.getFather() != null) {
				mbbUpdate = adjustNodeMBB();
				// Here an IllegalArgumentException can be thrown.

				if (split) {
					// node got splitted
					Entry r = new Entry(newNode.computeNodeMBB(), newNode);
					// Here an IllegalArgumentException can be thrown.
					this.getFather().append(r);
					newNode.setFather(this.getFather(), this.getFather()
							.getUsed() - 1);
					newNode.updateFather(newNode.computeNodeMBB());
				}

				/*
				 * Attention: if the adjustVertex is carried out, if there was
				 * no change and the node was not split, it can be interrupted.
				 */
				if (!mbbUpdate && !split)
					return false;

				this.getFather().adjustNode(levelFRI);
				// done go further to father
			} else { // this is root
				if (split) {
					// if root is split - new root needed
					Node newRoot = new Node();

					Entry e1 = new Entry(this.computeNodeMBB(), this);
					Entry e2 = new Entry(newNode.computeNodeMBB(), newNode);

					newRoot.append(e1);
					newRoot.append(e2);
					newRoot.setLeaf(false);

					// change the father pointers to correct references
					setFather(newRoot, 0); // index 0 points to old subtree
					newNode.setFather(newRoot, 1); // and 1 to the new

					// tree has increased height
					// make new root known
					setRoot(newRoot, 1);
					adjustHeightDiff(1);

					// shift because of heightdiff through new root
					for (int i = getHeight() - 1; i >= 0; i--)
						levelFRI[i + 1] = levelFRI[i];

					levelFRI[0] = false;
					return true;
				}
			}
			return false;
		}

		/*
		 * Choose a Node in the entries of this for best insertion of given mbb
		 * after the criteria of RStar algorithm
		 * 
		 * @param mbb - MBB3D to best fit
		 * 
		 * @return Node - best node for MBB3D.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private Node chooseNode(MBB3D mbb) {
			// chooseNode - after RStar algorithm
			// make a difference if son of this is a leaf or not
			boolean sonIsLeaf = false;
			int i = 0;
			int iBest = 0;
			double bestIndex = 0.0;
			double tempIndex = 0.0;

			// test for leaf
			if (((Node) this.getEntry(0).getSon()).isLeaf())
				sonIsLeaf = true;

			// son is leaf
			if (sonIsLeaf) {
				// compute overlap enlargement index
				// beginn with entry 0 and assume the return index is the best
				bestIndex = computeOverlapEnlargeIndex(0, mbb);
				// Here an IllegalArgumentException can be thrown.

				// go through the rest
				for (i = 1; i < getUsed(); i++) {
					// compute the index temporarly
					tempIndex = computeOverlapEnlargeIndex(i, mbb);

					// test if new index is better or equal
					if (tempIndex <= bestIndex) {

						if (tempIndex == bestIndex) {
							// new index is equal
							// resolve the ties by choosen the one with least
							// enlargement by volume
							double enlargeTemp = 0.0;
							double enlargeBest = 0.0;

							enlargeTemp = computeVolumeEnlargeIndex(getEntry(i)
									.getMBB(), mbb);
							// Here an IllegalArgumentException can be thrown.
							enlargeBest = computeVolumeEnlargeIndex(getEntry(
									iBest).getMBB(), mbb);

							// check which one is better
							if (enlargeTemp <= enlargeBest) {

								if (enlargeTemp == enlargeBest) {
									// they are equal so check further
									// resolve the ties by choosen the one with
									// smallest volume
									double volumeTemp = 0.0;
									double volumeBest = 0.0;

									volumeTemp = this.getEntry(i).getMBB()
											.computeVolume();
									volumeBest = this.getEntry(iBest).getMBB()
											.computeVolume();

									if (volumeTemp < volumeBest) {
										iBest = i;
										bestIndex = tempIndex;
									}
								} else {
									// temp is better in volume enlargement - so
									// change it
									iBest = i;
									bestIndex = tempIndex;
								}
							}
						} else {
							// new index is better
							bestIndex = tempIndex;
							iBest = i;
						}
					}
				} // end for
			} // end son is leaf

			// son is not leaf
			else {
				// compute volume enlargement index
				// begin with entry 0 and assume the return index is the best
				bestIndex = computeVolumeEnlargeIndex(
						this.getEntry(0).getMBB(), mbb);

				// go through the rest
				for (i = 1; i < getUsed(); i++) {
					// compute index temporarily
					tempIndex = computeVolumeEnlargeIndex(this.getEntry(i)
							.getMBB(), mbb);

					if (tempIndex <= bestIndex) {
						if (tempIndex == bestIndex) {
							// resolve ties by choosen the one with least volume
							double volumeTemp = 0.0;
							double volumeBest = 0.0;

							volumeTemp = this.getEntry(i).getMBB()
									.computeVolume();
							volumeBest = this.getEntry(iBest).getMBB()
									.computeVolume();
							// test if volume of temp is smaller than of
							// currently best
							if (volumeTemp < volumeBest) {
								// yes - so choose the temp
								bestIndex = tempIndex;
								iBest = i;
							}
						} else {
							// not equal so temp is better
							bestIndex = tempIndex;
							iBest = i;
						}
					}
				} // end for
			} // end son is not leaf
			return (Node) this.getEntry(iBest).getSon();
		}

		/*
		 * Computes a index indicating the overlap enlargement of the MBB3D at
		 * given index in the entries of this and the given MBB3D. The sum of
		 * the volumes (in 3D) of every intersection of the MBB3D mbb unioned
		 * with the MBB3D of given index with the other MBBs in entries.
		 * 
		 * @param index - index of the entry which MBB3D should be used for
		 * computation
		 * 
		 * @param mbb - the MBB3D we want to know the overlap enlargement
		 * 
		 * @return double - the computed index number for overlap enlargement.
		 * 
		 * @throws IllegalArgumentException if an attempt is made to construct a
		 * MBB3D whose maximum point is not greater than its minimum point.
		 */
		private double computeOverlapEnlargeIndex(int index, MBB3D mbb) {
			ScalarOperator sop = getSOP();
			double before = 0.0;
			double after = 0.0;

			MBB3D testBox = this.getEntry(index).getMBB();
			// compute the value of overlap for index MBB3D without union with
			// given MBB3D mbb
			for (int i = 0; i < getUsed(); i++) {
				if (index != i
						&& this.getEntry(index).getMBB().intersectsStrict(
								testBox, sop))
					before = before
							+ this.getEntry(index).getMBB().intersection(
									testBox, sop).computeVolume();
			}
			// union with given MBB3D mbb and compute the value of overlap a
			// second time
			testBox = testBox.union(mbb, sop);
			// Here an IllegalArgumentException can be thrown.
			for (int i = 0; i < getUsed(); i++) {
				if (index != i
						&& this.getEntry(index).getMBB().intersectsStrict(
								testBox, sop))
					after = after
							+ this.getEntry(index).getMBB().intersection(
									testBox, sop).computeVolume();
			}
			// return difference
			return after - before;
		}

		/*
		 * Computes a index indicating the enlargement of the volume of mbb1 by
		 * union with mbb2 - mbb1.union(mbb2).volume() - mbb1.volume()
		 * 
		 * @param mbb1 the existing MBB3D to enlarge
		 * 
		 * @param mbb2 the test MBB3D
		 * 
		 * @return double - volume enlargement index.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private double computeVolumeEnlargeIndex(MBB3D mbb1, MBB3D mbb2) {
			// compute before and after volume - return difference
			double before = mbb1.computeVolume();
			double after = mbb1.union(mbb2, getSOP()).computeVolume();
			return after - before;
		}

		/*
		 * Computes the current MBB3D of node this.
		 * 
		 * @return MBB3D - computed MBB3D of this.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		protected MBB3D computeNodeMBB() {
			// compute a union of the mbbs of the entries
			MBB3D mbb = this.getEntry(0).getMBB();

			for (int i = 1; i < this.getUsed(); i++)
				mbb = mbb.union(this.getEntry(i).getMBB(), getSOP());
			// Here an IllegalArgumentException can be thrown.

			return mbb;
		}

		/*
		 * Performs a split after the RStar criteria on this. After the split
		 * the two resulting nodes are this and the return value.
		 * 
		 * @return Node - second split node.
		 * 
		 * @throws IllegalArgumentException if an attempt is made to construct a
		 * MBB3D whose maximum point is not greater than its minimum point.
		 */
		private Node splitNode() {
			Node v1 = new Node();
			Node v2 = new Node(); // later copied to this

			short axis = chooseSplitAxis();
			// Here an IllegalArgumentException can be thrown.
			short[] splitresult = chooseSplitIndex(axis);

			short[] splitA = new short[splitresult.length - 1];
			System.arraycopy(splitresult, 0, splitA, 0, splitA.length);
			short splitIndex = splitresult[splitresult.length - 1];

			// System.out.println("SplitAxis: " + axis);
			// System.out.println("splitindex " + splitIndex);

			// fill v1 with entries 0 to splitindex
			for (int i = 0; i <= splitIndex; i++)
				v1.append(this.getEntry(splitA[i]));

			// fill v2 with entires splitIndex +1 to getUsed()
			for (int i = splitIndex + 1; i < getUsed(); i++)
				v2.append(this.getEntry(splitA[i]));

			this.setUsed(0);
			// kopiere die Eintraege von v2 in this
			for (int i = 0; i < v2.getUsed(); i++)
				this.append(v2.getEntry(i));

			v2.setUsed(0);
			v2 = null;

			return v1;
		}

		/*
		 * Chooses the best split axis to perform a split on this.
		 * 
		 * @return int - best axis (axis begin with 1).
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private short chooseSplitAxis() {

			short used = this.getUsed();
			short rstarMin = getMMin();

			short[] sort = new short[used];
			short maxK = (short) ((getMMax() - 2 * rstarMin + 2) / 2); // Anzahl
			// der
			// Teilmengen
			short minMax = 0;
			short value = 0;
			double vK = 0;
			double bestValue = -1.0;
			short bestAxis = -1;

			for (short axis = 1; axis <= 3; axis++) { // every axis
				value = 0;
				for (minMax = 1; minMax < 3; minMax++) {

					for (short i = 0; i < used; i++)
						// init Array
						sort[i] = i;

					// Sort the array of index values depending on the
					// value of their MBB3D points - minMax = 1 MinPoint, minMax
					// = 2 MaxPoint
					quickSort(sort, (short) 0, (short) (used - 1), minMax, axis);

					// Compute every distribution into groups
					for (int k = 0; k < maxK; k++) {
						vK = computeMarginValue(sort, (short) 0,
								(short) (rstarMin - 1 + k))
								+ computeMarginValue(sort,
										(short) (rstarMin + k),
										(short) (used - 1));
						// Here an IllegalArgumentException can be thrown.
						value += vK;
					}
				}
				// search for best distribution and remember axis
				if (value < bestValue || bestValue == -1.0) {
					bestValue = value;
					bestAxis = axis;
				}
			}
			return bestAxis;
		}

		/*
		 * Computes the Areavalue (after RStar criteria) for the given indexes
		 * in the array based on the left / right criteria.
		 * 
		 * @param sort index array for entries indexes
		 * 
		 * @param left left index
		 * 
		 * @param right right index
		 * 
		 * @return double - area (volume...) value.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private double computeAreaValue(short[] sort, short left, short right) {
			return computeMBBValue(sort, left, right).computeVolume();
		}

		/*
		 * Computes the volume of the overlap of the bounding boxes given
		 * through the sort array and the bounds l to m and m+1 to r
		 * 
		 * @param sort sort array
		 * 
		 * @param left left bound for 1. box
		 * 
		 * @param middle right bound for 1. box and m+1 left bound for 2. box
		 * 
		 * @param right right bound for 2. box
		 * 
		 * @return double - overlap value.
		 * 
		 * @throws IllegalArgumentException if an attempt is made to construct a
		 * MBB3D whose maximum point is not greater than its minimum point.
		 */
		private double computeOverlapValue(short[] sort, short left,
				short middle, short right) {
			MBB3D leftMiddle = computeMBBValue(sort, left, middle);
			MBB3D middleRight = computeMBBValue(sort, (short) (middle + 1),
					right);

			if (leftMiddle.intersectsStrict(middleRight, getSOP()))
				return leftMiddle.intersection(middleRight, getSOP())
						.computeVolume();
			else
				return 0;
		}

		/*
		 * Computes the margin value of this entries MBBs for the given indexes
		 * in the array sort between left and right.
		 * 
		 * @param sort array with indexes
		 * 
		 * @param left left index to compute
		 * 
		 * @param right right index to compute
		 * 
		 * @return double - margin of the MBB3D.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private double computeMarginValue(short[] sort, short left, short right) {
			return computeMBBValue(sort, left, right).margin();
		}

		/*
		 * Computes the union MBB3D of the given indexes for the entries of this
		 * between the left and right index.
		 * 
		 * @param sort array with indexes
		 * 
		 * @param left left index
		 * 
		 * @param right right index
		 * 
		 * @return MBB3D - unioned MBB3D.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private MBB3D computeMBBValue(short[] sort, short left, short right) {
			MBB3D mbb = null;

			for (short i = left; i <= right; i++) {
				if (i == left)
					mbb = this.getEntry(sort[i]).getMBB();
				else
					mbb = mbb.union(this.getEntry(sort[i]).getMBB(), getSOP());
				// Here an IllegalArgumentException can be thrown.
			}
			return mbb;
		}

		/*
		 * Returns the value of the given MBB3D for given criteria
		 * 
		 * @param mbb the MBB3D
		 * 
		 * @param minMax int, 1 = PMin 2 = PMax
		 * 
		 * @param axis int for choosen axis
		 * 
		 * @return double - value.
		 */
		private double quickMBBValue(MBB3D mbb, int minMax, int axis) {
			if (minMax == 1) // use Point pMin
				return mbb.getPMin().getCoordinates()[axis - 1];
			else
				// minMax==2 use Point pMax
				return mbb.getPMax().getCoordinates()[axis - 1];
		}

		/*
		 * Sorts the given array of indexes from this entries following the
		 * given crieterias.
		 * 
		 * @param sort index array to sort
		 * 
		 * @param left left index to sort
		 * 
		 * @param right right index to sort
		 * 
		 * @param minMax PMin or PMax sorting criteria
		 * 
		 * @param axis axis sorting criteria
		 */
		private void quickSort(short[] sort, short left, short right,
				short minMax, short axis) {
			double comp;
			short l, r;
			short temp;

			if (right > left) {
				l = (short) (left - 1);
				r = right;
				comp = quickMBBValue(this.getEntry(sort[right]).getMBB(),
						minMax, axis); // Sortierelement
				do {
					// increasing sort - means a close reinsert
					while (l < right
							&& quickMBBValue(this.getEntry(sort[++l]).getMBB(),
									minMax, axis) > comp) {
					}
					while (r > left
							&& quickMBBValue(this.getEntry(sort[--r]).getMBB(),
									minMax, axis) < comp) {
					}

					if (l < r) {
						temp = sort[l];
						sort[l] = sort[r];
						sort[r] = temp;
					}
				} while (l < r);

				temp = sort[l];
				sort[l] = sort[right];
				sort[right] = temp;

				quickSort(sort, left, (short) (l - 1), minMax, axis);
				quickSort(sort, (short) (l + 1), right, minMax, axis);
			}
		}

		/*
		 * Chooses the best distribution along the given split axis.
		 * 
		 * @param axis number of axis
		 * 
		 * @return short[] - split result (last index place is the splitindex).
		 * 
		 * @throws IllegalArgumentException if an attempt is made to construct a
		 * MBB3D whose maximum point is not greater than its minimum point.
		 */
		private short[] chooseSplitIndex(short axis) {

			int used = this.getUsed();
			int rstarMin = getMMin();

			short[] sort = new short[used];
			short[] bestSort = new short[used + 1];

			int k;
			int maxK = (getMMax() - 2 * rstarMin + 2); // Anzahl der Teilmengen
			// M - 2m + 2
			double value = 0.0;
			double bestValue = Double.MAX_VALUE;
			short bestSplitIndex = -1;
			double bestAreaValue = -1.0;
			double areaValue = -1.0;
			boolean newBestValue = false;

			for (short minMax = 1; minMax < 3; minMax++) {

				for (short i = 0; i < used; i++)
					// init Array
					sort[i] = i;

				quickSort(sort, (short) 0, (short) (used - 1), minMax, axis);

				// Search for best distribution
				for (k = 0; k < maxK; k++) {
					// based on the overlap value
					value = computeOverlapValue(sort, (short) 0,
							(short) (rstarMin - 1 + k), (short) (used - 1));

					if (value <= bestValue) {
						newBestValue = true;

						if (value == bestValue) { // based on area value
							bestAreaValue = computeAreaValue(bestSort,
									(short) 0, bestSplitIndex)
									+ computeAreaValue(bestSort,
											(short) (bestSplitIndex + 1),
											(short) (used - 1));
							// Here an IllegalArgumentException can be thrown.

							areaValue = computeAreaValue(sort, (short) 0,
									(short) (rstarMin - 1 + k))
									+ computeAreaValue(sort,
											(short) (rstarMin + k),
											(short) (used - 1));

							if (areaValue >= bestAreaValue)
								newBestValue = false; // doch kein neuer Bester
							// Wert
						}
					}
					if (newBestValue) {
						newBestValue = false;
						bestValue = value;
						bestSplitIndex = (short) (rstarMin - 1 + k);

						for (int i = 0; i < used; i++)
							// copy Array
							bestSort[i] = sort[i];
					}
				}
			}

			bestSort[bestSort.length - 1] = bestSplitIndex;
			return bestSort;
		}

		/*
		 * Adjusts the father pointer in the entries of the sons
		 */
		private void adjustNodeSons() {
			for (int i = 0; i < this.getUsed(); i++)
				((Node) this.getEntry(i).getSon()).setFather(this, i);
		}

		/*
		 * Forces a reinsert of the RStar.forcedReInsertCount entries of this.
		 * 
		 * @param levelFRI boolean array indicating reinserts on levels
		 * 
		 * @throws IllegalArgumentException if an attempt is made to construct a
		 * MBB3D whose maximum point is not greater than its minimum point.
		 */
		private void forcedReInsert(boolean[] levelFRI) {

			// entry array of size forcedreinsertcount for storing the entries
			// to reinsert
			Entry[] entrySet = new Entry[getForcedReInsertCount()];

			// initialize int array with entries indexes
			int[] sort = new int[this.getUsed()];
			for (int i = 0; i < this.getUsed(); i++)
				sort[i] = i;

			// sort entries of this - write sorted indexes in sort-array
			centerSort(sort, 0, this.getUsed() - 1);

			int height = this.computeHeight();
			// Here an IllegalArgumentException can be thrown.

			for (int i = 0; i < getForcedReInsertCount(); i++)
				entrySet[i] = new Entry(this.getEntry(sort[i]), height);

			// delete the copied entries from this
			cutAndCompressEntry(sort, getForcedReInsertCount());

			// adjust this after the delete - compute MBB3D ...
			adjustNode(levelFRI);

			// ReInsert the entries from set
			for (int i = 0; i < entrySet.length; i++) {
				// Errorcondition
				// if (entrySet[i].getHeight() == 0 ) throw
				// NodeError("Error - Insert into root node");

				Node node = getRoot().chooseSubtree(entrySet[i].getMBB(),
						entrySet[i].getHeight() + getHeightDiff(), 0);
				// Here an IllegalArgumentException can be thrown.
				node.append(entrySet[i]);

				if (node.adjustNode(levelFRI))
					adjustHeightDiff(1);
			}
		}

		/*
		 * Deletes the entry with given number and moves the last entry
		 * foreward.
		 * 
		 * @param number index of entry to delete
		 */
		private void cutAndCompressEntry(int number) {

			// denke das ist falsch - testen
			// sollte number == this.getUsed() -1 sein (indexe starten bei 0
			// macht aber keine fehler nur kann man sich die arbeit sparen

			if (number == this.getUsed()) {
				// delete last
				// do nothing
			} else {
				// move last to the entry which should be deleted
				if (!this.isLeaf())
					((Node) this.getEntry(getUsed() - 1).getSon()).setFather(
							this, number);

				this.setEntry(this.getEntry(getUsed() - 1), number);
			}
			this.decUsed();
		}

		/*
		 * Deletes the first "count" entries of this in the order specified by
		 * the sort array and moves the last forewards
		 * 
		 * @param sort array with indexes
		 * 
		 * @param count number of entries to delete
		 */
		private void cutAndCompressEntry(int[] sort, int count) {
			int[] temp = new int[count];
			int iTemp;

			// copy sort array in temp array for sorting
			for (int i = 0; i < count; i++)
				temp[i] = sort[i];

			// we must sort because lower entries must changed first
			// // / ??? really ????
			boolean changed = true;
			while (changed) {
				changed = false;
				for (int i = 0; i < count - 1; i++) {
					if (temp[i] < temp[i + 1]) { // then swap elements
						iTemp = temp[i + 1];
						temp[i + 1] = temp[i];
						temp[i] = iTemp;
						changed = true;
					}
				}
			}
			// now cut and compress the entries
			for (int i = 0; i < count; i++)
				cutAndCompressEntry(temp[i]);
		}

		/*
		 * Sorts the entries index in the int array after the square distance of
		 * the center point of the corresponding MBBs.
		 * 
		 * @param sort array with entry indexes to sort
		 * 
		 * @param left left sort start in array
		 * 
		 * @param right right sort stop in array
		 */
		private void centerSort(int[] sort, int left, int right) {
			double comp;
			int l, r;
			int temp;

			if (right > left) {
				l = left - 1;
				r = right;
				comp = this.getNodeMBB().getCenter().euclideanDistanceSQR(
						this.getEntry(sort[right]).getMBB().getCenter());
				do {
					// operators for decreasing sort
					while ((l < right)
							&& (this.getNodeMBB().getCenter()
									.euclideanDistanceSQR(
											this.getEntry(sort[++l]).getMBB()
													.getCenter()) < comp)) {
					}
					while ((r > left)
							&& (this.getNodeMBB().getCenter()
									.euclideanDistanceSQR(
											this.getEntry(sort[--r]).getMBB()
													.getCenter()) > comp)) {
					}

					if (l < r) {
						temp = sort[l];
						sort[l] = sort[r];
						sort[r] = temp;
					}
				} while (l < r);

				temp = sort[l];
				sort[l] = sort[right];
				sort[right] = temp;

				centerSort(sort, left, l - 1);
				centerSort(sort, l + 1, right);
			}
		}

		/*
		 * Adjusts the MBBs of node this and updates the father.
		 * 
		 * @return boolean - true if update took place.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private boolean adjustNodeMBB() {
			MBB3D newMBB = computeNodeMBB();

			if (!newMBB.isEqual(this.getFather()
					.getEntry(getFatherEntryIndex()).getMBB(), getSOP())) {
				updateFather(newMBB);
				return true;
			}
			return false;
		}

		/*
		 * Adjust the father of this with the given MBB3D.
		 * 
		 * @param mbb the MBB3D for father
		 * 
		 * @return <code>true</code> if update successfull <code>false</code>
		 * otherwise.
		 */
		private boolean updateFather(MBB3D mbb) {
			if (this.getFather() != null) {
				if (this.getFather().getFatherEntryIndex() >= 0) {
					this.getFather().getEntry(this.getFatherEntryIndex())
							.setMBB(mbb);
					return true;
				}
			}
			return false;
		}

		/*
		 * Computes the height from node this to root recursivley.
		 * 
		 * @return int - height of node this.
		 * 
		 * @throws IllegalArgumentException - if an attempt is made to construct
		 * a MBB3D whose maximum point is not greater than its minimum point.
		 */
		private int computeHeight() {
			if (this.getFather() != null)
				return (this.getFather().computeHeight() + 1);

			return 0;
		}

		/*
		 * Returns the MBB3D of this or computes it.
		 * 
		 * @return MBB3D - MBB3D of this.
		 */
		private MBB3D getNodeMBB() {
			if (this.getFather() != null)
				return this.getFather().getEntry(this.getFatherEntryIndex())
						.getMBB();

			return computeNodeMBB();
		}

		/*
		 * Returns the entry at given index.
		 * 
		 * @param index int
		 * 
		 * @return Entry at given index.
		 */
		private Entry getEntry(int index) {
			return entries[index];
		}

		/*
		 * Returns the father node.
		 * 
		 * @return Node - father of this.
		 */
		private Node getFather() {
			return father;
		}

		/*
		 * Returns the fatherEntryIndex.
		 * 
		 * @return int - index of this in father.
		 */
		private int getFatherEntryIndex() {
			return fatherEntryIndex;
		}

		/*
		 * Tests whether this node is leaf.
		 * 
		 * @return boolean - true if leaf, false otherwise.
		 */
		protected boolean isLeaf() {
			return leaf;
		}

		/*
		 * Returns the number of used entry places.
		 * 
		 * @return short - current number of entries in node.
		 */
		private short getUsed() {
			return used;
		}

		/*
		 * Sets the entry to given index
		 * 
		 * @param entry the entry to set
		 * 
		 * @param index the index to set
		 */
		private void setEntry(Entry entry, int index) {
			this.entries[index] = entry;
		}

		/*
		 * Sets the father node of this.
		 * 
		 * @param father the father to set
		 * 
		 * @param index the fatherEntryIndex to set
		 */
		private void setFather(Node father, int index) {
			this.father = father;
			this.fatherEntryIndex = (short) index;
		}

		/*
		 * Sets if this node is a leaf or not
		 * 
		 * @param leaf leaf flag as boolean
		 */
		private void setLeaf(boolean leaf) {
			this.leaf = leaf;
		}

		/*
		 * Sets the number of currently used entry places
		 * 
		 * @param used current number of entries
		 */
		private void setUsed(int used) {
			this.used = (short) used;
		}

		/*
		 * Increments number of used entries.
		 */
		private void incUsed() {
			setUsed(getUsed() + 1);
		}

		/*
		 * Decrements number of used entries.
		 */
		private void decUsed() {
			setUsed(getUsed() - 1);
		}

		/*
		 * Test method
		 * 
		 * @param out PrintStream for String output into file ...
		 * 
		 * @param level level of the tree
		 */
		public void printRec(PrintStream out, int level) {

			String whitespace = "";

			for (int i = 0; i <= level; i++)
				whitespace += "  ";

			if (this.isLeaf()) {
				out.print(whitespace + "LeafNode-Level: " + level
						+ " Entries: " + this.getUsed());
				out.println("  MBB3D: " + this.getNodeMBB().toString());
				for (int i = 0; i < getUsed(); i++) {
					out.println(whitespace + "Entry " + i
							+ this.getEntry(i).toString());
				}
			} else {
				out.print(whitespace + "Node-Level: " + level);
				out.println("  MBB3D: " + this.getNodeMBB().toString());
				for (int i = 0; i < getUsed(); i++)
					((Node) this.getEntry(i).getSon()).printRec(out, level + 1);
			}
		}

		/*
		 * Converts this to string.
		 * 
		 * @return String with the information of this.
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer b = new StringBuffer("<Node leaf=\"" + leaf + "\">");

			for (int i = 0; i < getUsed(); i++)
				b.append(getEntry(i).toString());

			b.append("</Node>");
			return b.toString();
		}

		// inner Class ABL

		/*
		 * Inner class ABL (Active Branch List) for sorting entries in node
		 * according to the MINDIST heuristic for nearest neighbour search. <br>
		 * <br> Transient classes
		 */
		private final class ABL implements Comparable {

			/* node */
			private final Node node;

			/* minimal distance */
			private final double minDist;

			/*
			 * Constructor.
			 * 
			 * @param node Node
			 * 
			 * @param minDist MINDIST value for node
			 */
			private ABL(Node node, double minDist) {
				this.node = node;
				this.minDist = minDist;
			}

			/*
			 * Compares this to the given object.
			 * 
			 * @param obj Object to be comared
			 * 
			 * @return 0 if the minimal distances are equal, -1 if the minimal
			 * distance of this is less than of ob, 1 if the minimal distance of
			 * this is greater than of obj.
			 * 
			 * @see java.lang.Comparable#compareTo(java.lang.Object)
			 */
			public int compareTo(Object obj) {
				ABL help = (ABL) obj;
				if (this.minDist < help.minDist)
					return -1;
				if (this.minDist == help.minDist)
					return 0;

				return 1;
			}
		}

	}

	/*
	 * Entry of a Node object in a RStar.<br> The Entry holds the MBB3D of all
	 * sons below and also the reference to the next son below and the height
	 * from root node to his node in the tree. <br> <br>
	 * 
	 * @author Wolfgang Baer / University of Osnabrueck Default Serialization
	 * (Serializable) - WBaer 07082003
	 */
	private final static class Entry implements PersistentObject, Serializable {

		// Members

		/* Minimum BoundingBox of Entry */
		private MBB3D mbb;

		/* reference to son - Node or SO */
		private Object son;

		/* height from root to Entry */
		private transient short entryHeight;

		// Operations
		/*
		 * Constructor.
		 * 
		 * @param mbb the MBB3D of this Entry
		 * 
		 * @param son reference to son of this - Node or SO
		 */
		protected Entry(MBB3D mbb, Object son) {
			this.mbb = mbb;
			this.son = son;
		}

		/*
		 * Copy constructor - copies mbb and son from given entry and sets the
		 * height to given value.
		 * 
		 * @param entry the entry to copy
		 * 
		 * @param height height from root to node of this entry (-1 is not set)
		 */
		protected Entry(Entry entry, int height) {
			this.mbb = entry.getMBB();
			this.son = entry.getSon();
			this.entryHeight = (short) height;
		}

		/*
		 * Returns the mbb of this entry.
		 * 
		 * @return MBB3D - mbb of entry.
		 */
		protected MBB3D getMBB() {
			return mbb;
		}

		/*
		 * Returns the son of this entry.<br> If this is entry of inner node
		 * return value can be casted to type Node.
		 * 
		 * @return Object - son of entry.
		 */
		protected Object getSon() {
			return son;
		}

		/*
		 * Sets the mbb of this entry.
		 * 
		 * @param mbb mbb of type MBB3D
		 */
		protected void setMBB(MBB3D mbb) {
			this.mbb = mbb;
		}

		/**
		 * Returns the height of this entry.
		 * 
		 * @return int - height.
		 */
		protected int getHeight() {
			return entryHeight;
		}

		/*
		 * Sets the height of this entry.
		 * 
		 * @param height height as int
		 */
		protected void putHeight(int height) {
			this.entryHeight = (short) height;
		}

		/*
		 * Converts this to string.
		 * 
		 * @return String with the information of this.
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer b = new StringBuffer("<Entry>");
			b.append(getMBB().toString());
			b.append(getSon().toString());
			b.append("</Entry>");
			return b.toString();
		}
	}

	/**
	 * Implementation for SAM.NNResult
	 */
	final class NNResultImpl implements Comparable, SAM.NNResult {

		/* distance_square of this result object to query point */
		private double distance_square;

		/* result object */
		private Object reference;

		/**
		 * Constructor.
		 */
		protected NNResultImpl() {
			this.distance_square = Double.POSITIVE_INFINITY;
			this.reference = null;
		}

		/**
		 * Sets this NNResultImpl object to given result values.
		 * 
		 * @param distance_square
		 *            distance_square to query point
		 * @param obj
		 *            result object as Object
		 */
		protected void setNNResult(double distance_square, Object obj) {
			this.distance_square = distance_square;
			this.reference = obj;
		}

		/**
		 * Returns the distance of this result object.
		 * 
		 * @return double - distance as double.
		 */
		public double getDistance() {
			return Math.sqrt(distance_square);
		}

		/**
		 * Returns the reference to the object.
		 * 
		 * @return Object - reference.
		 */
		public Object getObjectRef() {
			return reference;
		}

		/**
		 * Compares this to the given object.
		 * 
		 * @return 0 if the distance of this to the query point is equal to the
		 *         one of obj, -1 if it is less, 1 if it is greater.
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			NNResultImpl obj = (NNResultImpl) o;
			if (this.distance_square < obj.distance_square)
				return -1;
			if (this.distance_square == obj.distance_square)
				return 0;

			return 1;
		}
	}

}
