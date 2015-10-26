package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

public interface Net4D {

	public void topologyChange(Date date);

	public void addChangeTimestep(Date date);

	public void preparePostObject(Date date);

	public Date getStart();
	
	public Date getEnd();

	List<Component4D> getValidComponents(Date date);

	public Map<Integer, Element4D> getNetElements(Date date);
	
	public Date getLastChangeDate();
	
	public byte getType();

	public Map<Integer, Component4D> getComponents();

	public List<Map<Integer, Element4D>> getElements();
	
	public Component4D getComponent(int ID);
}
