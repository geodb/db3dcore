/*
 * Copyright (C) Prof. Martin Breunig
 */
package de.uos.igf.db3d.dbms.model3d;

import de.uos.igf.db3d.dbms.api.DB3DException;
import de.uos.igf.db3d.dbms.geom.Line3D;
import de.uos.igf.db3d.dbms.geom.MBB3D;
import de.uos.igf.db3d.dbms.geom.Plane3D;
import de.uos.igf.db3d.dbms.geom.Point3D;
import de.uos.igf.db3d.dbms.geom.ScalarOperator;
import de.uos.igf.db3d.dbms.geom.SimpleGeoObj;
import de.uos.igf.db3d.dbms.util.SAM;

/**
 * This class models as regular deformed grid. ...
 * 
 * SGrids are regular 3D grids which are deformed and cut to fit a structural
 * Surface model. This type of discetized volume is necessary if we want to
 * model the distribution of a property in a geological body. The grid axes are
 * standard parallel to the XYZ axes of the model, but can be defined in a
 * different way by the user.
 * 
 * 
 * Das stratigraphische Gitter (Sgrid) besteht aus Zellen, die durch drei
 * Vektoren aufgespannt werden und in einem Gitter angeordnet sind. Den
 * Eckpunkten der Zellen können Eigenschaften zugeordnet werden. Ein
 * stratigraphisches Gitter besitzt gegenüber dem Voxet die Eigenschaft, daß die
 * Vektoren nicht orthogonal zueinander verlaufen müssen. Damit besteht die
 * Möglichkeit der Deformation des Gitters. Dieser Objekttyp wurde jedoch bei
 * der vorliegenden Modellierung nicht genutzt.
 * 
 * 
 * 
 * SGrid (hexahedral blocks)
 * 
 * 
 * SGRID (hexahedral mesh) were transferred into the numerical finite element
 * program FRACTure
 * 
 * 
 * 
 * The grid is regular in parametric (U;V:W) coordinates, but irregular in
 * spatial (X.Y.Z) coordinates. In spatial (X.Y.Z) coordinate system: the cell
 * is delineated by its 8 apice
 * 
 * @author Edgar Butwilowski
 * 
 */
public class RegularDeformableGrid3D extends SpatialObject3D implements
		ComplexGeoObj {

	// Export: double_precision_binary:on; ascii:on

	private final Point3D[][][] points;
	private final float[][][] properties;

	public String name;
	public String propertyName;
	public String propertyClass;
	public String propertyOriginalUnit;
	public String propertyUnit;
	public long propertyNrDataValue;

	// stores whether the thematic data is aligned at corner-points (otherwise
	// it is aligned at cell-centers):
	public boolean isThematicDataAtPoints; 

	/**
	 * Stadnard constructor for RegularDeformedGrid3D. This is a conviniency
	 * constructor that constructs a regular deformed grid with a grid size of
	 * x=10, y=10, z=10 and that alignes the thematic data at points.
	 * 
	 */

	public RegularDeformableGrid3D() {
		this(10, 10, 10, true);
	}

	/**
	 * Constructor for RegularDeformedGrid3D.
	 * 
	 * @param dimenxionX
	 *            the dimension of the grid in x-direction. Must be >0.
	 * @param dimenxionY
	 *            the dimension of the grid in y-direction. Must be >0.
	 * @param dimenxionZ
	 *            the dimension of the grid in z-direction. Must be >0.
	 * @param isThematicDataAtPoints
	 *            is the thematic data aligned at points (<code>true</code>)?
	 *            Otherwise <code>false</code> if the thematic data is aligned
	 *            at cells.
	 */
	public RegularDeformableGrid3D(int dimensionX, int dimensionY,
			int dimensionZ, boolean isThematicDataAtPoints) {
		super();
		this.points = new Point3D[dimensionX][dimensionY][dimensionZ];
		if (isThematicDataAtPoints) {
			this.properties = new float[dimensionX][dimensionY][dimensionZ];
		} else {
			this.properties = new float[dimensionX - 1][dimensionY - 1][dimensionZ - 1];
		}
		this.isThematicDataAtPoints = isThematicDataAtPoints;
	}

	/**
	 * Constructor for RegularDeformedGrid3D.
	 * 
	 * @param dimenxionX
	 *            the dimension of the grid in x-direction. Must be >0.
	 * @param dimenxionY
	 *            the dimension of the grid in y-direction. Must be >0.
	 * @param dimenxionZ
	 *            the dimension of the grid in z-direction. Must be >0.
	 * @param isThematicDataAtPoints
	 *            is the thematic data aligned at points (<code>true</code>)?
	 *            Otherwise <code>false</code> if the thematic data is aligned
	 *            at cells.
	 */
	public RegularDeformableGrid3D(Point3D[][][] points,
			boolean isThematicDataAtPoints) {
		super();
		this.points = points;
		if (isThematicDataAtPoints) {
			this.properties = new float[points.length][points[0].length][points[0][0].length];
		} else {
			this.properties = new float[points.length - 1][points[0].length - 1][points[0][0].length - 1];
		}
		this.isThematicDataAtPoints = isThematicDataAtPoints;
		updateMBB();
	}

	/**
	 * Add grid point.
	 * 
	 * @param point
	 *            the point to add.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addGridPoint(Point3D point, int x, int y, int z) {
		points[x][y][z] = point;
		updateMBB();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * 
	 */
	public Point3D getGridPoint(int x, int y, int z) {
		return points[x][y][z];
	}

	/**
	 * 
	 * @return
	 */
	public Point3D[][][] getGrid() {
		return points;
	}

	/**
	 * Returns the x-dimension of the grid.
	 * 
	 * @return
	 */
	public int getDimX() {
		return points.length;
	}

	/**
	 * Returns the y-dimension of the grid.
	 * 
	 * @return
	 */
	public int getDimY() {
		if (points.length > 0) {
			return points[0].length;
		}
		return 0;
	}

	/**
	 * Returns the z-dimension of the grid.
	 * 
	 * @return
	 */
	public int getDimZ() {
		if (points.length > 0) {
			if (points[0].length > 0) {
				return points[0][0].length;
			}
		}
		return 0;
	}

	public byte getType() {
		return ComplexGeoObj.REGULAR_DEFORMED_GRID_3D;
	}

	public int countElements() {
		// TODO Auto-generated method stub
		return 0;
	}

	public SimpleGeoObj getElement(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean intersects(Plane3D plane) throws DB3DException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean intersects(Line3D line) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean intersects(MBB3D mbb) {
		// TODO Auto-generated method stub
		return false;
	}

	public int countComponents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns the spatial type of this.
	 * 
	 * @return byte - spatial type.
	 * @see db3d.dbms.model3d.Spatial3D#getSpatial3DType()
	 */
	public byte getSpatial3DType() {
		return Spatial3D.GRID_3D;
	}

	public ComplexGeoObj getComponent(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateMBB() {
		// TODO IMPLEMENT!
		ScalarOperator sop = getScalarOperator();

		// provisorily:
		MBB3D neu = new MBB3D(new Point3D(), new Point3D());

		/*
		 * Update the index if sam exists - means if object is registered in
		 * space.
		 */
		SAM sam = getSAM();
		if (sam != null) { // must be first removed and afterward the new mbb
			// must be
			sam.remove(this); // set before reinsertion
			// Here an IllegalArgumentException can be thrown.
			setMBB(neu);
			sam.insert(this);
			// Here an IllegalArgumentException can be thrown.
		} else {
			// set the SpatialObject mbb
			setMBB(neu);
		}
	}

}
