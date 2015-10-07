package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;
import java.util.List;

public interface Net4D {

	public void TopologyChange(Date date);

	public void addTimestep(Date date);

	public void preparePostObject(Date date);
}
