package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Net4D {

	public void TopologyChange(Date date);

	public void addChangeTimestep(Date date);

	public void preparePostObject(Date date);

	public Date getStart();
	
	public Date getEnd();

	List<Component4D> getValidComponents(Date date);

	public Map<Integer, Element4D> getNetElements(Date date);
	
	public Date getLastChangeDate();
}
