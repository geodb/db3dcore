/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.junittests.dbms;

import java.util.Iterator;
import java.util.Set;

import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.PointSet3D;
import de.uos.igf.db3d.dbms.geom.Rectangle3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.Segment3D;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.geom.Tetrahedron3D;
import de.uos.igf.db3d.dbms.geom.TetrahedronSet3D;
import de.uos.igf.db3d.dbms.geom.Triangle3D;
import de.uos.igf.db3d.dbms.geom.Vector3D;
import de.uos.igf.db3d.dbms.geom.Wireframe3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNet3D;
import de.uos.igf.db3d.dbms.model3d.TetrahedronNet3DComp;


public class Utils {

	/**
	 * Returns Textausgabe fuer beliebiges SimpleGeoObj
	 * @return String -
	 */
	public static String getText(SimpleGeoObj obj) {

		String text = "testcases.Utils: uebergebener Wert ist= 'null'";

		if (obj == null)
			return text;

		switch (obj.getType()) {

			case SimpleGeoObj.POINT3D :
				Point3D p = (Point3D) obj;
				text = "P:(" + p.getX() + "," + p.getY() + "," + p.getZ() + ") ";
				return text;

			case SimpleGeoObj.SEGMENT3D :
				Segment3D seg = (Segment3D) obj;
				Point3D[] point = seg.getPoints();
				StringBuffer b = new StringBuffer();
				b.append("Segment3D[ ");
				for (int i = 0; i < point.length; i++) {
					b.append("P:(" + point[i].getX() + "," + point[i].getY() + "," + point[i].getZ() + ") ");
				}
				b.append("]");
				return b.toString();

			case SimpleGeoObj.TRIANGLE3D :
				Triangle3D tri = (Triangle3D) obj;
				Point3D[] points = tri.getPoints();
				StringBuffer b2 = new StringBuffer();
				b2.append("Triangle3D[ ");
				for (int i = 0; i < points.length; i++) {
					b2.append("P" + i + ":(" + points[i].getX() + "," + points[i].getY() + "," + points[i].getZ() + ") ");
				}
				b2.append("]");
				return b2.toString();

			case SimpleGeoObj.VECTOR3D :
				Point3D vecPoint = ((Vector3D) obj).getAsPoint3D();
				text = "Vector3D[ vecP( " + vecPoint.getX() + "," + vecPoint.getY() + "," + vecPoint.getZ() + " )";

				return text;

			case SimpleGeoObj.LINE3D :

				Line3D line = ((Line3D) obj);
				Point3D origin = line.getOrigin();
				Point3D direcVecPoint = line.getDVector().getAsPoint3D();

				text = "Line3D[ P( " + origin.getX() + "," + origin.getY() + "," + origin.getZ() + " )";
				text = text + "VectorP( " + direcVecPoint.getX() + "," + direcVecPoint.getY() + "," + direcVecPoint.getZ() + " )";

				return text;

			case SimpleGeoObj.TETRAHEDRON3D :
				Tetrahedron3D tet = (Tetrahedron3D) obj;
				Point3D[] pts = tet.getPoints();
				StringBuffer b3 = new StringBuffer();
				b3.append("Tetra3D[ ");
				for (int i = 0; i < pts.length; i++) {
					b3.append("P" + i + ":(" + pts[i].getX() + "," + pts[i].getY() + "," + pts[i].getZ() + ") ");
				}
				b3.append("]");
				return b3.toString();

			case SimpleGeoObj.TETRAHEDRONSET3D :
				Tetrahedron3D[] tetSet = ((TetrahedronSet3D) obj).toArray();
				StringBuffer b4 = new StringBuffer();
				b4.append("TetraSet3D[ ");
				for (int i = 0; i < tetSet.length; i++)
					b4.append(getText(tetSet[i], (i + 1)) + "\n");
				
				b4.append("]");
				return b4.toString();

			case SimpleGeoObj.POINTSET3D :
				PointSet3D ps = (PointSet3D) obj;
				Point3D[] pots = ps.toArray();
				StringBuffer b5 = new StringBuffer();
				b5.append("PointSet3D[ ");
				for (int i = 0; i < pots.length; i++) {
					b5.append(getText(pots[i], i+1));
				}
				b5.append("]");
				return b5.toString();

			case SimpleGeoObj.MBB3D :
				Point3D max = ((MBB3D) obj).getPMax();
				Point3D min = ((MBB3D) obj).getPMin();
				text = "MBB3D[ Pmin(" + min.getX() + "," + min.getY() + "," + min.getZ() + ") ";
				text = text + "Pmax(" + max.getX() + "," + max.getY() + "," + max.getZ() + ") ]";

				return text;
				
			case SimpleGeoObj.PLANE3D :
				Plane3D plane = (Plane3D) obj;
				Vector3D normv = plane.getNormalVector();
				Vector3D posv = plane.getPositionVector();
				
				text = "Plane3D[ NormalVector("+normv.getX()+","+normv.getY()+","+normv.getZ()+") ";
				text = text + "PositionVector("+posv.getX()+","+posv.getY()+","+posv.getZ()+") "+" ]";
				return text;

			case SimpleGeoObj.WIREFRAME3D :
				Wireframe3D wf = (Wireframe3D) obj;
				StringBuffer b6 = new StringBuffer();
				b6.append("\n--------------------  Wireframe: \n");
				Point3D[] pt = wf.getPoints();
				
				b6.append("  NodeCount= "+wf.countNodes()+" :\n");
				for ( int i = 0; i < pt.length; i++ ) {
					b6.append("   - "+getText(pt[i])+"\n");
							}

				if ( wf.countNodes() == 4 && wf.getDimension() == 2){
					b6.append("  Trianguliert (bei 4 Knoten):\n");
				Triangle3D[] tris = wf.getTriangulated();
				b6.append("   - "+getText(tris[0]) +"\n");
				b6.append("   - "+getText(tris[1]) +"\n");
				}
				
				
				Segment3D[] segs = wf.getSegments();
				
				b6.append("  SegmentCout= "+segs.length +" :\n");
				for ( int i = 0; i < segs.length; i++ ) {
					b6.append("   - "+getText(segs[i])+"\n");
				}
		
				b6.append("--------------------  Wireframe ENDE. \n");
				return b6.toString();


			default :
				text = "Klasse 'Utils' meldet: 'Ausgabe fuer diesen Typ noch nicht implementiert -> bitte sofort nachholen!'";
				return text;
		}
	}


	private static String getText(Triangle3D tri) {
		Point3D[] point = tri.getPoints();
		StringBuffer b = new StringBuffer();
		b.append("Triangle3D[ ");
		for (int i = 0; i < point.length; i++) {
			b.append("P" + i + ":(" + point[i].getX() + "," + point[i].getY() + "," + point[i].getZ() + ") ");
		}
		b.append("]");

		return b.toString();
	}

	public static String getText(Rectangle3D rec) {
		StringBuffer b = new StringBuffer();
		b.append("Rec[ ");
		for (int i = 0; i < 3; i++)
			b.append("("+ rec.getPoint(i).getX() + "," + rec.getPoint(i).getY() + "," + rec.getPoint(i).getZ() + ") , ");
		
		b.append("("+ rec.getPoint(3).getX() + "," + rec.getPoint(3).getY() + "," + rec.getPoint(3).getZ() + ") ]");
		return b.toString();
	}

	private static String getText(Segment3D seg) {
		Point3D[] point = seg.getPoints();
		StringBuffer b = new StringBuffer();
		b.append("Segment3D[ ");
		for (int i = 0; i < point.length; i++) {
			b.append("P:(" + point[i].getX() + "," + point[i].getY() + "," + point[i].getZ() + ") ");
		}
		b.append("]");

		return b.toString();
	}

	private static String getText(Point3D p, int number) {
		String text = "P_" + number + ":(" + p.getX() + "," + p.getY() + "," + p.getZ() + ") ";
		return text;
	}

	private static String getText(Tetrahedron3D tet, int number) {
		Point3D[] pts = tet.getPoints();
		StringBuffer b = new StringBuffer();
		b.append("Tetra3D_" + number + " [ ");
		for (int i = 0; i < pts.length; i++) {
			b.append("P" + i + ":(" + pts[i].getX() + "," + pts[i].getY() + "," + pts[i].getZ() + ") ");
		}
		b.append("]");
		return b.toString();
	}
	



// *********************************************
// ** Section writeVRML 			ANFANG
// ** 
// ** Methoden dieser "Sektion" erzeugen	
// ** VRMLcode zur Visualisierung von Testcases
// *********************************************
	
	// Color definitions: R-G-B
   private final static double[] singlePointColor = { 1,0,0 };
   private final static double[] vectorColor = { 0,0,1 };
   private final static double[] segmentColor = { 0.4,1,.2 };
   private final static double[] lineColor = { 1,0,0.3 };
   private final static double[] mbbColor = { 0,1,0 };
   private final static double[] tetraColor = { 1,0.5,0 };
   private final static double[] triangleColor = { 1,0,0 };
   private final static double[] wireframeColor = { 0,0,1 };
   private final static double[] planeColor = { 1,0,0 };
   


//	/**
//	* Erzeugt Koordinatenachsen
//	* @return String - VRML-Group element for coordinate axes
//	*/
//	private static String writeCoordAxes () {
//		String text = "\n# ---------- Coordinate Axes ----------\n\n";
//		
//		double[] color = { 0,0,0 }; // RGB color
//		
//		// lines for axes
//		text += "Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		String tab = "       ";
//		text += 	tab+"geometry IndexedLineSet { \n"+tab+"coord Coordinate { \n"+tab+"point [ \n";
//		// coordinates
//		text += 	tab+"0 0 0,  #Index0\n"+   
//						tab+"20 0 0,  #Index1\n"+
//						tab+"0 20 0,  #Index2\n"+
//						tab+"0 0 20,  #Index3\n";
//
//		text+= tab+"] \n"+tab+"} \n"+tab+"coordIndex [ \n";
//		// coordIndexes
//		text+= 	tab+"0, 1, -1 \n"+
//						tab+"0, 2, -1 \n"+
//						tab+"0, 3 \n";
//						
//		text+= tab+"]\n"+tab+"}";
//		text += "\n  }\n ] \n}";
//		
//		// arrow x-axes
//		text += "\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		text += 	tab+"geometry Cone {\n"+tab+"bottomRadius 0.2\n"+tab+"height 1.3\n"+tab+"}\n";  
//		text+= tab+"  }\n ]\n translation 20 0 0\n rotation 0 0 1 4.712\n}";
//		// arrow y-axes
//		text += "\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		text += 	tab+"geometry Cone {\n"+tab+"bottomRadius 0.2\n"+tab+"height 1.3\n"+tab+"}\n";  
//		text+= tab+"  }\n ]\n translation 0 20 0\n rotation 0 1 0 4.712\n}";
//		// arrow z-axes
//		text += "\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		text += 	tab+"geometry Cone {\n"+tab+"bottomRadius 0.2\n"+tab+"height 1.3\n"+tab+"}\n";  
//		text+= tab+"  }\n ]\n translation 0 0 20\n rotation 1 0 0 1.572\n}";
//
//		// text x-axes
//		text += "\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		text += 	tab+"geometry Text {\n"+tab+"string \"x-axes\"\n"+tab+"fontStyle FontStyle {\n"+tab+"size 0.5 }\n"+tab+"}\n";  
//		text+= "  }\n ]\n translation 17 0.1 0\n rotation 0 0 0 0\n}";
//		// text y-axes
//		text += "\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		text += 	tab+"geometry Text {\n"+tab+"string \"y-axes\"\n"+tab+"fontStyle FontStyle {\n"+tab+"size 0.5 }\n"+tab+"}\n";  
//		text+= "  }\n ]\n translation -0.1 17 0\n rotation 0 0 1 1.572\n}";
//		// text z-axes
//		text += "\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {diffuseColor "+color[0]+" "+color[1]+" "+color[2]+" }\n  }\n";
//		text += 	tab+"geometry Text {\n"+tab+"string \"z-axes\"\n"+tab+"fontStyle FontStyle {\n"+tab+"size 0.5 }\n"+tab+"}\n";  
//		text+= "  }\n ]\n translation 0 0.1 18.5\n rotation 0 1 0 1.572\n}";
//
//		return text;
//	}

	/**
	* Erzeugt VRMLcode fuer ein Segment3D Objekt
	* @return String - VRML-Group{} element
	*/
	private static String writeSegment(Segment3D seg, double[] rgb) {
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Segment3D object ----------\n\n");
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { }\n  }\n");
		String tab = "       ";
		b.append(tab+"geometry IndexedLineSet { \n"+tab+"coord Coordinate { \n"+tab+"point [ \n");
		// coordinates
		b.append(tab+""+seg.getPoint(0).getCoord(0)+" "+seg.getPoint(0).getCoord(1)+" "+seg.getPoint(0).getCoord(2)+",  #Index0\n"+  
						tab+""+seg.getPoint(1).getCoord(0)+" "+seg.getPoint(1).getCoord(1)+" "+seg.getPoint(1).getCoord(2)+",  #Index1\n");

		b.append(tab+"] \n"+tab+"} \n"+tab+"coordIndex [ \n");
		// coordIndexes
		b.append(tab+"0, 1\n");


		b.append(tab+"] \n \n"+
								tab+"colorPerVertex FALSE \n\n"+
								tab+"color Color { \n"+
								tab+"color [ \n"+
								tab+rgb[0]+" "+rgb[1]+" "+rgb[2]+"\n"+
								tab+"] \n" +
								tab+"} \n"+
								tab+"colorIndex [ 1 ] \n" +
								tab+"} \n"); //color index nicht vergessen zu setzen
								
		b.append("  }\n ] \n}");
		
		//writing points
		b.append(writePoint(seg.getPoint(1), rgb));
		b.append(writePoint(seg.getPoint(0), rgb));
		
		return b.toString();	
	}

	/**
		* Erzeugt VRMLcode fuer ein Line3D Objekt als Segment3D
		* @return String - VRML-Group{} element
		*/
	private static String writeLine(Line3D line, double[] rgb) {
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Line3D object -> written as:\n");

		Point3D p = new Point3D(0,0,0);
		Point3D projected = (Point3D) p.projection(line);
		Point3D p1 = (Vector3D.add(projected.getVector(), (Vector3D.mult(line.getDVector(), 100)) )).getAsPoint3D();
		Point3D p2 = (Vector3D.sub(projected.getVector(), (Vector3D.mult(line.getDVector(), 100)) )).getAsPoint3D();

		Segment3D segment = new Segment3D(p1,p2, new ScalarOperator(TestConstants.EPSILON));
	
		b.append(writeSegment(segment, rgb));
		return b.toString()
		;
	}
	
	
	/**
		* Erzeugt VRMLcode fuer ein Triangle3D Objekt in 'WIREFRAME' Darstellung
		* @return String - VRML-Group{} element
		*/
	private static String writeTriangleAsWireframe(Triangle3D tri, double[] rgb){
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Triangle object as 'Wireframe' ----------\n\n");
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { }\n  }\n");
		String tab = "       ";
		b.append(tab+"geometry IndexedLineSet { \n"+ 
						tab+"coord Coordinate { \n" +
						tab+"point [ \n");
			
			for ( int i = 0; i < 2; i++) {
				b.append(tab+""+tri.getPoint(i).getCoord(0)+" "+tri.getPoint(i).getCoord(1)+" "+tri.getPoint(i).getCoord(2)+",  #Index"+i+"\n");
			}
			b.append(tab+""+tri.getPoint(2).getCoord(0)+" "+tri.getPoint(2).getCoord(1)+" "+tri.getPoint(2).getCoord(2)+"   #Index2 \n");

			b.append(tab+"] \n" +
						tab+"} \n"+
						tab+"coordIndex [ \n");
			
			b.append(tab+"0, 1, 2, 0 \n");  //coordIndexe werden statisch geschrieben

			b.append(tab+"] \n \n"+
						tab+"colorPerVertex FALSE \n\n"+
						tab+"color Color { \n"+
						tab+"color [ \n"+
						tab+""+rgb[0]+" "+rgb[1]+" "+rgb[2]+"\n"+
						tab+"] \n" +
						tab+"} \n\n"+
						tab+"colorIndex [ 1 ] \n" +
						tab+"} \n"); //color index nicht vergessen zu setzen
	
			b.append("  }\n ] \n}");
	
// writing points for tri
		  Point3D[] pts = tri.getPoints();
		  for ( int i = 0; i<pts.length; i++ )
			  b.append(writePoint(pts[i], rgb));
		
		  
// writing normal vector from center of tri
  //		  Vector3D norm = tri.getNormal(sop);
  //		  text += writeVector(norm, tri.getCenter(), rgb);
		  
		  
		return b.toString();	
	}
	

	/**
		* Erzeugt VRMLcode fuer ein Triangle3D Objekt in 'SOLID' Darstellung
		* @return String - VRML-Group{} element
		*/
	private static String writeTriangleAsSolid(Triangle3D tri, double[] rgb){
		
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Triangle3D object as 'Solid' ----------\n\n");
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0 0 0  diffuseColor "+rgb[0]+" "+rgb[1]+" "+rgb[2]+"  transparency 0.5 }\n  }\n");
		String tab = "       ";
		b.append(tab+"geometry IndexedFaceSet { \n"+ 
						tab+"coord Coordinate { \n" +
						tab+"point [ \n");
			
					// writing coordinates
					for ( int i = 0; i < 2; i++) {
						b.append(tab+""+tri.getPoint(i).getCoord(0)+" "+tri.getPoint(i).getCoord(1)+" "+tri.getPoint(i).getCoord(2)+",  #Index"+i+"\n");
					}
					b.append(tab+""+tri.getPoint(2).getCoord(0)+" "+tri.getPoint(2).getCoord(1)+" "+tri.getPoint(2).getCoord(2)+"   #Index2 \n");

					b.append(tab+"] \n" +
								tab+"} \n"+
								tab+"coordIndex [ \n");
			
					b.append(tab+"0, 1, 2 \n");  //coordIndexe werden statisch geschrieben
			
					b.append(tab+"]\n \n"+tab+"solid FALSE\n"+tab+"}\n");
		
					b.append("  }\n ] \n}");

		// writing points for tri
		  Point3D[] pts = tri.getPoints();
		  for ( int i = 0; i<pts.length; i++ ) 
			  b.append(writePoint(pts[i], rgb));
		  
		return b.toString();
		
		//old:
//		String text = "\n# ---------- Triangle3D object as 'Solid' ----------\n\n";
//		text += "Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0 0 0  diffuseColor "+rgb[0]+" "+rgb[1]+" "+rgb[2]+"  transparency 0.5 }\n  }\n";
//		String tab = "       ";
//		text += 	tab+"geometry IndexedFaceSet { \n"+ 
//						tab+"coord Coordinate { \n" +
//						tab+"point [ \n";
//			
//					// writing coordinates
//					for ( int i = 0; i < 2; i++) {
//						text += tab+""+tri.getPoint(i).getCoord(0)+" "+tri.getPoint(i).getCoord(1)+" "+tri.getPoint(i).getCoord(2)+",  #Index"+i+"\n";
//					}
//						text +=  tab+""+tri.getPoint(2).getCoord(0)+" "+tri.getPoint(2).getCoord(1)+" "+tri.getPoint(2).getCoord(2)+"   #Index2 \n";
//
//					text+= tab+"] \n" +
//								tab+"} \n"+
//								tab+"coordIndex [ \n";
//			
//					text+= tab+"0, 1, 2 \n";  //coordIndexe werden statisch geschrieben
//			
//					text+= tab+"]\n \n"+tab+"solid FALSE\n"+tab+"}\n";
//		
//		text += "  }\n ] \n}";
//
//		// writing points for tri
//		  Point3D[] pts = tri.getPoints();
//		  for ( int i = 0; i<pts.length; i++ ) 
//		  text += writePoint(pts[i], rgb);
//		  
//		return text;
	}
	
	/**
		* Erzeugt VRMLcode fuer ein Tetraeder3D Objekt in 'WIREFRAME' Darstellung
		* @return String - VRML-Group{} element
		*/
	private static String writeTetraAsWireframe(Tetrahedron3D tetra, double[] rgb){
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Tetrahedron object as 'Wireframe' ----------\n\n");
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0 0 0  }\n  }\n");
		String tab = "       ";
		b.append(tab+"geometry IndexedLineSet { \n"+ 
						tab+"coord Coordinate { \n" +
						tab+"point [ \n");

			for ( int i = 0; i < 3; i++) {
				b.append(tab+""+tetra.getPoint(i).getCoord(0)+" "+tetra.getPoint(i).getCoord(1)+" "+tetra.getPoint(i).getCoord(2)+",  #Index"+i+"\n");
			}
			b.append(tab+""+tetra.getPoint(3).getCoord(0)+" "+tetra.getPoint(3).getCoord(1)+" "+tetra.getPoint(3).getCoord(2)+"   #Index3 \n");

			
			b.append(tab+"] \n" +
						tab+"} \n"+
						tab+"coordIndex [ \n");
			
			b.append(tab+"0, 1, 2, 0, -1 \n"+  //coordIndexe werden statisch geschrieben
						tab+"1, 2, 3, 1, -1 \n"+
						tab+"2, 3, 0, 2, -1 \n"+
						tab+"1, 0, 3, 1 \n");
			
			b.append(tab+"] \n \n"+
						tab+"colorPerVertex FALSE \n\n"+
						tab+"color Color { \n"+
						tab+"color [ \n"+
						tab+""+rgb[0]+" "+rgb[1]+" "+rgb[2]+"\n"+
						tab+"] \n" +
						tab+"} \n"+
						tab+"colorIndex [0,0,0,0] \n" +
						tab+"} \n"); //color index nicht vergessen zu setzen
	
			b.append("\n  }\n ] \n}");
		return b.toString();
	}

	/**
		* Erzeugt VRMLcode fuer ein Tetraeder3D Objekt in 'SOLID' Darstellung
		* @return String - VRML-Group{} element
		*/
	private static String writeTetraAsSolid(Tetrahedron3D tetra, double[] rgb){
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Tetrahedron object as 'Solid' ----------\n\n");
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0.5 0.5 0  diffuseColor "+rgb[0]+" "+rgb[1]+" "+rgb[2]+"  transparency 0.4 }\n  }\n");
		String tab = "       ";
		
		b.append(tab+"geometry IndexedFaceSet { \n"+ 
						tab+"coord Coordinate { \n" +
						tab+"point [ \n");
			
					// writing coordinates
					for ( int i = 0; i < 3; i++) {
						b.append(tab+""+tetra.getPoint(i).getCoord(0)+" "+tetra.getPoint(i).getCoord(1)+" "+tetra.getPoint(i).getCoord(2)+",  #Index"+i+"\n");
					}
						b.append(tab+""+tetra.getPoint(3).getCoord(0)+" "+tetra.getPoint(3).getCoord(1)+" "+tetra.getPoint(3).getCoord(2)+"   #Index3 \n");

						b.append(tab+"] \n" +
								tab+"} \n"+
								tab+"coordIndex [ \n");
			
						b.append(tab+"0, 1, 2, -1 \n"+  //coordIndexe werden statisch geschrieben
								tab+"1, 2, 3, -1 \n"+
								tab+"2, 3, 0, -1 \n"+
								tab+"1, 0, 3 \n");
			
						b.append(tab+"] \n \n"+tab+"solid FALSE\n"+tab+"} \n"); //color index nicht vergessen zu setzen
		
					b.append("  }\n ] \n}");
		
		//writing points for wf
		Point3D[] pts = tetra.getPoints();
		for ( int i = 0; i<pts.length; i++ )
			b.append(writePoint(pts[i], rgb));
		
		return b.toString();
	}
	
	/**
		* Erzeugt VRMLcode fuer ein Wireframe3D Objekt (nur in 'WIREFRAME' Darstellung moeglich)
		* @return String - VRML-Group{} element
		*/
	private static String writeWireframe(Wireframe3D wf, double[] rgb){
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- Wireframe object ----------\n\n");
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0 0 0  }\n  }\n");
		String tab = "       ";
		
		b.append(tab+"geometry IndexedLineSet { \n"+ 
						tab+"coord Coordinate { \n" +
						tab+"point [ \n");
			
			Segment3D[] seg = wf.getSegments();
			int count = (seg.length-1);
			int counter = 0;
			for ( int i = 0; i < count; i++) {
				b.append(tab+""+seg[i].getPoint(0).getCoord(0)+" "+seg[i].getPoint(0).getCoord(1)+" "+seg[i].getPoint(0).getCoord(2)+",  #Index"+counter+"\n");
				counter++;
				b.append(tab+""+seg[i].getPoint(1).getCoord(0)+" "+seg[i].getPoint(1).getCoord(1)+" "+seg[i].getPoint(1).getCoord(2)+",  #Index"+counter+"\n");
				counter++;
			}
			b.append(tab+""+seg[count].getPoint(0).getCoord(0)+" "+seg[count].getPoint(0).getCoord(1)+" "+seg[count].getPoint(0).getCoord(2)+"  #Index"+counter+"\n");
				counter++;
				b.append(tab+""+seg[count].getPoint(1).getCoord(0)+" "+seg[count].getPoint(1).getCoord(1)+" "+seg[count].getPoint(1).getCoord(2)+"  #Index"+counter+"\n");

			
				b.append(tab+"] \n" +
						tab+"} \n"+
						tab+"coordIndex [ \n");

			for ( int i = 0; i < (counter-1); i++) {
				b.append(tab+""+i+", "+(i+1)+", -1 \n");  //segmente werden einzelnd ausgegeben
				i++;
			}
			b.append(tab+""+(counter-1)+", "+counter+" \n"+tab+"] \n \n"+
						
						tab+"colorPerVertex FALSE \n\n"+
						tab+"color Color { \n"+
						tab+"color [ \n"+
						tab+""+rgb[0]+" "+rgb[1]+" "+rgb[2]+"\n"+
						tab+"] \n" +
						tab+"} \n"+
						tab+"colorIndex [");
						
						for ( int i = 0; i < count; i++) {
							b.append("0,");
						}
						b.append("0] \n" +
								tab+"} \n");
	
						b.append("  }\n ] \n}");
		
		
		//writing points for wf
		Point3D[] pts = wf.getPoints();
		for ( int i = 0; i<pts.length; i++ )
			b.append(writePoint(pts[i], rgb));

		return b.toString();
	}
	
	/**
		* Erzeugt VRMLcode fuer ein Point3D Objekt - Darstellung als kleine Kugel
		* rgb sind variablen fuer die farbe
		* @return String - VRML-Group{} element
		*/
	private static String writePoint(Point3D p, double[] rgb) {
		
		StringBuffer b = new StringBuffer();
		b.append("\n# ---------- point object ----------\n\n");
		String tab = "       ";
		b.append("\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0 0 0  diffuseColor "+rgb[0]+" "+rgb[1]+" "+rgb[2]+" }\n  }\n");
		b.append(tab+"geometry Sphere {\n"+tab+"radius 0.05\n"+tab+"}\n");  
		b.append(tab+"  }\n ]\n translation "+p.getX()+" "+p.getY()+" "+p.getZ()+"\n}");

	return b.toString();
	}
	
	
	/**
			* Erzeugt VRMLcode fuer ein MBB3D Objekt
			* @return String - VRML-Group{} element
			*/
		private static String writeMbb(MBB3D mbb, double[] rgb) {
		
			StringBuffer b = new StringBuffer();
			
			b.append("\n# ---------- MBB object ----------\n\n");
			String tab = "       ";
			b.append("\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { emissiveColor 0 0 0    diffuseColor  "+rgb[0]+" "+rgb[1]+" "+rgb[2]+"  transparency 0.7 }\n  }\n");

			b.append(tab+"geometry IndexedFaceSet { \n"+ 
							tab+"coord Coordinate { \n" +
							tab+"point [ \n");
			
			Point3D[] pts = mbb.getCorners();
						
			for ( int i = 0; i < 7; i++) {
				b.append(tab+""+pts[i].getCoord(0)+" "+pts[i].getCoord(1)+" "+pts[i].getCoord(2)+",  #Index"+i+"\n");
			}
			b.append(tab+""+pts[7].getCoord(0)+" "+pts[7].getCoord(1)+" "+pts[7].getCoord(2)+",  #Index7\n");

			b.append(tab+"] \n" +
							tab+"} \n"+
							tab+"coordIndex [ \n");
			
			b.append(tab+"0, 2, 7, 3, 0, -1 \n"+  //coordIndexe werden statisch geschrieben
							tab+"0, 3, 5, 4, 0, -1 \n"+
							tab+"0, 2, 6, 4, 0, -1 \n"+
							tab+"1, 5, 3, 7, 1, -1 \n"+
							tab+"1, 6, 2, 7, 1, -1 \n"+
							tab+"1, 5, 4, 6, 0 \n");
							
			b.append(tab+"]\n \n"+tab+"solid FALSE\n"+tab+"}\n"); //color index nicht vergessen zu setzen
		
			b.append("  }\n ] \n}");

			//writing points for mbb			
			for ( int i = 0; i < 8; i++ )
				b.append(writePoint(pts[i], rgb));

		return b.toString();
		}
	
	
	/**
		* Erzeugt VRMLcode fuer ein Vector3D Objekt an der Ortskoordinate p
		* @return String - VRML-Group{} element
		*/
	private static String writeVector(Vector3D vec, Point3D from, double[] rgb) {
		
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- vector object ----------\n\n");
		String tab = "       ";
		
		if (from == null)
			from = new Point3D(0,0,0);
		
		Vector3D temp = vec.copy();
		temp.normalize(new ScalarOperator(TestConstants.EPSILON));
		temp.mult( (vec.getNorm() - 0.25) );
		
		Point3D to = (Vector3D.add(from.getVector(), temp)).getAsPoint3D();
		
		// vector starting point
		// text += writePoint(from, rgb);
		
		// segment of vector
		Segment3D seg = new Segment3D(from, to, new ScalarOperator(TestConstants.EPSILON));
		b.append("Transform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material { }\n  }\n");
		b.append(tab+"geometry IndexedLineSet { \n"+tab+"coord Coordinate { \n"+tab+"point [ \n");
		// coordinates
		b.append(tab+""+seg.getPoint(0).getCoord(0)+" "+seg.getPoint(0).getCoord(1)+" "+seg.getPoint(0).getCoord(2)+",  #Index0\n"+  
						tab+""+seg.getPoint(1).getCoord(0)+" "+seg.getPoint(1).getCoord(1)+" "+seg.getPoint(1).getCoord(2)+",  #Index1\n");

		b.append(tab+"] \n"+tab+"} \n"+tab+"coordIndex [ \n");
		// coordIndexes
		b.append(tab+"0, 1\n");

		b.append(tab+"] \n \n"+
								tab+"colorPerVertex FALSE \n\n"+
								tab+"color Color { \n"+
								tab+"color [ \n"+
								tab+""+rgb[0]+" "+rgb[1]+" "+rgb[2]+"\n"+
								tab+"] \n" +
								tab+"} \n"+
								tab+"colorIndex [ 0 ] \n" +
								tab+"} \n");
		b.append("  }\n ] \n}");

		//arrow
		//Rotationsberechnung	
		Vector3D yVec = new Vector3D(0,1,0); // steht fuer ausgangsrichtung des Kegels
		Vector3D normDirection = vec.copy();
		normDirection.normalize(new ScalarOperator(TestConstants.EPSILON));
		Vector3D rotVec = Vector3D.add(yVec, normDirection); // Rotationsachse
		double x = rotVec.getX();
		double y = rotVec.getY();
		double z = rotVec.getZ();

		b.append("\nTransform {\n center 0 0 0\n children [\n  Shape {\n  appearance Appearance {\n   material Material {emissiveColor 0 0 0    diffuseColor "+rgb[0]+" "+rgb[1]+" "+rgb[2]+" }\n  }\n");
		b.append(tab+"geometry Cone {\n"+tab+"bottomRadius 0.05\n"+tab+"height 0.3\n"+tab+"}\n");  
		b.append("  }\n ]\n translation "+to.getX()+" "+to.getY()+" "+to.getZ()+"\n");
		b.append("  rotation "+x+" "+y+" "+z+" 3.14\n}");

	return b.toString();
	}
	
	
	
	/**
		* Erzeugt VRMLcode fuer ein Vector3D Objekt an der Ortskoordinate p
		* @return String - VRML-Group{} element
		*/
	private static String writePlane(Plane3D plane, double[] rgb) {
	
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- plane object ----------\n\n");
		String tab = "       ";
		
		// normvector
		Point3D pos = plane.getPositionVector().getAsPoint3D();
		Vector3D norm = plane.getNormalVector();
		b.append(writeVector(norm, pos, rgb));
	
		//Rotation
		Vector3D yVec = new Vector3D(0,1,0); // steht fuer ausgangsrichtung des Kegels
		Vector3D normDirection = norm.copy();
		normDirection.normalize(new ScalarOperator(TestConstants.EPSILON));
		Vector3D rotVec = Vector3D.add(yVec, normDirection); // Rotationsachse
		double x = rotVec.getX();
		double y = rotVec.getY();
		double z = rotVec.getZ();
	
		// write flat cylinder as plane representation
		b.append("\nTransform {\n children [\n  Shape {\n  appearance Appearance {\n    material Material {emissiveColor 0 0.5 0.3   diffuseColor "+rgb[0]+" "+rgb[1]+" "+rgb[2]+"  transparency 0.5 }\n  }\n");
		b.append(tab+"geometry Cylinder {\n"+tab+"height 0\n"+tab+"radius 11\n"+tab+"}\n");  
		b.append("  }\n ]\n translation "+pos.getX()+" "+pos.getY()+" "+pos.getZ()+"\n");
		b.append("  rotation "+x+" "+y+" "+z+" 3.14\n}");

		return b.toString();
	}
	

	
	
	/**
	* Erzeugt VRMLcode fuer ein Tetra3DNet Objekt
	* @return String - VRML-Group element
	*/
	private static String writeTetraNet (TetrahedronNet3D net, double[] rgb) {
		StringBuffer b = new StringBuffer();
		
		b.append("\n# ---------- TetraNet3D object as 'Solid' ----------\n\n");

		for ( int i = 0; i < net.countComponents(); i++ ) {
			
			TetrahedronNet3DComp tetComp = net.getComponent(i);
			
			Set set = tetComp.getElementsViaRecursion();
			Iterator it = set.iterator();
			while ( it.hasNext() ) {
				Tetrahedron3D tet = (Tetrahedron3D) it.next();
				
				b.append(writeTetraAsWireframe(tet, rgb));
				//text += writeTetraAsSolid(tet, rgb);
			}
			
		}
		return b.toString();
	}
	
	
//	********************************************
//	** Section writeVRML 			 ENDE
//	********************************************
}
