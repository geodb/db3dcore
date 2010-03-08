Basic Documentation
-------------------

This page is an extract of the PDF documentation of the DB3D Core API.
Its purpose is to provide a first step insight in DB4GeO/DB3D programming.
You should have a look into the README.txt of the root folder of this projects
folders first. The latest complete PDF documetation (coreapi_db3d_devdoc.pdf)
is available only from the copyright owners/developers of DB4GeO/DB3D.
Please write an email to mbreunig@uni-osnabrueck.de and ask for full
documentation.

First steps with DB4GeO/DB3D Core API:
--------------------------------------

The provided geometric data types are Simplicial Complexes of dimensions
0-3 (point, segment, triangle, tetrahedron). Simplicial Complexes are used
as a paradigm because they provide the possibility of outstandingly fast
geometrical computation.

The geometric model is the foundation for the topology/net model. Though
the topology/net model ist more difficult to develop, more fragile, needs
a sophisticated framework for keeping consistency, it is also provided for
free with this library. The topology/net model can be a powerful tool at
your fingertips.

Below you will find two application examples: one for the geometry model
and one for the topology/net model.

The Geodatabases Working Group of the Institute for Geoinformatics and
Remote Sensing of the University of Osnabrück is also providing the
following modules that are designed on top of this kernel API:

  * database server
  * complex operations (like cross section operation)
  * project management
  * thematical data
  * time (4D) model
  * ...


Example 1:
Intersection of two segments:

    +
    |
+---+---+
    |
    +

Compute an intersection between two segments is as easy as this:
/////// JAVA CODE SAMPLE ///////
	public static void main(String[] args) throws NameNotUniqueException {
		// model first segment:
		Segment3D segment1 = new Segment3D(new Point3D(1.0, 2.0, 1.0),
				new Point3D(3.0, 2.0, 1.0),
				sop);
		// model second segment:
		Segment3D segment2 = new Segment3D(new Point3D(2.0, 1.0, 1.0),
				new Point3D(2.0, 3.0, 1.0),
				sop);
		// compute the intersection between the two segments and display
		// the result:
		Point3D intersectionResult = (Point3D) segment1.intersection(segment2, sop);
		System.out.println(intersectionResult);
	}
/////// JAVA CODE SAMPLE END ///////


Example 2:
Create a segment net of two segments and test topological relationship:
/////// JAVA CODE SAMPLE ///////
	public static void main(String[] args) throws NameNotUniqueException {
		// define a scalar operator:
		ScalarOperator sop = new ScalarOperator(0.001);
		// create a segment net builder:
		SegmentNetBuilder builder = new SegmentNetBuilder(sop);
		// model two segments:
		SegmentElt3D seg1 = new SegmentElt3D(new Point3D(1.0, 1.0, 1.0),
				new Point3D(3.0, 1.0, 1.0),
				sop);
		SegmentElt3D seg2 = new SegmentElt3D(new Point3D(3.0, 1.0, 1.0),
				new Point3D(3.0, 2.0, 1.0),
				sop);
		// let the builder create a segment net from the segments:
		builder.addComponent(new SegmentElt3D[] { seg1, seg2 });
		// get the segment net:
		SegmentNet3D net = builder.getSegmentNet();
		// get a component of the segment net:
		SegmentNet3DComp comp = net.getComponent(0);
		// print a segment:
		SegmentElt3D seg1Test = (SegmentElt3D) comp.getElement(1);
		System.out.println(seg1Test.getNeighbour(0));
		System.out.println(seg1Test.getNeighbour(1));
	}
/////// JAVA CODE SAMPLE END ///////

