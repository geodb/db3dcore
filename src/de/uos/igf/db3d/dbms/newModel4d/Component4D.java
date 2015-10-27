package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

public interface Component4D {

	public Net4D getNet();

	public Map<Integer, List<Point3D>> getPointTubes();	
	
	public TimeInterval getTimeInterval();

	public LinkedList<Date> getTimesteps();
}
