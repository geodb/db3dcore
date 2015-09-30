package de.uos.igf.db3d.dbms.spatials.api;

import java.util.Collection;
import java.util.TreeMap;

import de.uos.igf.db3d.dbms.collections.SAM;

/**
 * 
 * Interface for the components of Net3Ds
 * 
 * @author Markus Jahn
 * 
 */
public interface Component3D extends Spatial3D {

	public int getID();

	public Net3D getNet();

	public SAM getSAM();

	public boolean isEmpty();

	public int countElements();

	public Element3D getElement(int id);

	public Collection<?> getElementsViaSAM();

	public TreeMap<Integer, ?> getVertices();

	public TreeMap<Integer, ?> getEdges();

	public TreeMap<Integer, ?> getFaces();

	public TreeMap<Integer, ?> getSolids();

	public int countVertices();

	public int countEdges();

	public int countFaces();

	public int countSolids();

	public int getEuler();

}
