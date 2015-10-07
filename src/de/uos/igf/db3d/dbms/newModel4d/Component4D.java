package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

public interface Component4D {

	public Map<Integer, Map<Integer, Point3D>> getPointTubes();

	public LinkedList<Date> getTimesteps();	
	
	public Net4D getNet();
}
