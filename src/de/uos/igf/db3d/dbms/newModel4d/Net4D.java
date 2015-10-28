package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uos.igf.db3d.dbms.geom.Point3D;

public interface Net4D {

	public void addChangeTimestep(Date date);

	public Component4D getComponent(int ID);

	public Map<Integer, Component4D> getComponents();

	public List<Map<Integer, Element4D>> getElements();
	
	public Date getEnd();

	public Date getLastChangeDate();

	public Map<Integer, Element4D> getNetElements(Date date);
	
	public Date getStart();
	
	public byte getType();

	List<Component4D> getValidComponents(Date date);

	public void preparePostObject(Date date);	
}
