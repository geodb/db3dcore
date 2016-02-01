package de.uos.igf.db3d.dbms.spatials.standard3d;

import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.api.Component3D;
import de.uos.igf.db3d.dbms.spatials.api.Element3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.Point3D;
import de.uos.igf.db3d.dbms.spatials.geometries3d.WireframeGeometry3D;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

public class Wireframe3DElement extends WireframeGeometry3D implements
		Element3D {

	protected int id;

	/* enclosing net component */
	protected Wireframe3DComponent component;

	public Wireframe3DElement(GeoEpsilon epsilon) {
		super(epsilon);
	}

	/**
	 * Constructor.
	 * 
	 * @param wireframe
	 *            WireframeGeometry3D
	 */
	public Wireframe3DElement(WireframeGeometry3D wireframe) {
		super(wireframe);
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.WIREFRAME_ELEMENT_3D;
	}

	@Override
	public Component3D getComponent() {
		return component;
	}

	@Override
	public boolean hasNeighbour(int index) {
		throw new NotYetImplementedException();
	}

	@Override
	public boolean hasNeighbours() {
		throw new NotYetImplementedException();
	}

	@Override
	public Wireframe3DElement getNeighbour(int index) {
		throw new NotYetImplementedException();
	}

	@Override
	public Wireframe3DElement[] getNeighbours() {
		throw new NotYetImplementedException();
	}

	@Override
	public boolean isInterior() {
		throw new NotYetImplementedException();
	}

	/**
	 * Returns a deep copy of this.
	 * 
	 * @return Wireframe3D - new object.
	 * @throws IllegalStateException
	 *             - signals Problems with the dimension of the wireframe.
	 * @throws ArithmeticException
	 *             - if norm equals zero in epsilon range. This exception
	 *             originates in the method normalize(GeoEpsilon) of the class
	 *             Vector3D.
	 */
	public Wireframe3DElement copy() {
		Wireframe3DElement newwf = new Wireframe3DElement(this.epsilon);
		newwf.component = this.component;
		Point3D[] pts = this.getPoints();
		int length = pts.length;
		for (int i = 0; i < length; i++) {
			newwf.add(new Point3D(pts[i]));
		}
		newwf.add(this.getSegments());
		return newwf;
	}

	@Override
	public GeoEpsilon getGeoEpsilon() {
		return this.component.getGeoEpsilon();
	}
	
	@Override
	public String toString() {

		String string = "ID " + this.id + " " + super.toString();

		return string;
	}

}
