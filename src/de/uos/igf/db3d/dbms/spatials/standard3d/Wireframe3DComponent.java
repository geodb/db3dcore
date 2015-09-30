package de.uos.igf.db3d.dbms.spatials.standard3d;

import java.util.Iterator;

import de.uos.igf.db3d.dbms.exceptions.NotYetImplementedException;
import de.uos.igf.db3d.dbms.spatials.standard.GeoEpsilon;

public class Wireframe3DComponent extends Component3DAbst {

	/**
	 * Constructor.<br>
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 */
	public Wireframe3DComponent(GeoEpsilon epsilon) {
		super(epsilon);
	}

	/**
	 * Constructor.<br>
	 * Constructs a WireframeNet3DComp object with the given PointElt3D[].<br>
	 * In the given array the neighbourhood topology has not been defined.<br>
	 * It is assumed that there are NO ! redundant Point3D used in this triangle
	 * array.
	 * 
	 * @param epsilon
	 *            GeoEpsilon
	 * @param elements
	 *            WireframeNet3DElement[]
	 * @throws IllegalArgumentException
	 *             - if an attempt is made to construct a MBB3D whose maximum
	 *             point is not greater than its minimum point.
	 */
	public Wireframe3DComponent(Wireframe3DElement[] elements,
			GeoEpsilon epsilon) {
		super(epsilon);
		for (Wireframe3DElement element : elements) {
			this.addElement(element);
		}
	}

	public void addElement(Wireframe3DElement element) {
		throw new NotYetImplementedException();
	}

	@Override
	public Wireframe3DElement getElement(int id) {
		Iterator<Wireframe3DElement> it = (Iterator<Wireframe3DElement>) this.sam
				.getEntries().iterator();
		while (it.hasNext()) {
			Wireframe3DElement p = it.next();
			if (p.getID() == id)
				return p;
		}
		return null;
	}

	@Override
	public SPATIALTYPES getSpatialType() {
		return SPATIALTYPES.WIREFRAME_COMPONENT_E3D;
	}

	@Override
	public int getEuler() {
		throw new NotYetImplementedException();
	}
}
