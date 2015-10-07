package de.uos.igf.db3d.dbms.newModel4d;

import java.util.Date;

public class TimeInterval {

	Date start;
	Date end;

	public TimeInterval(Date start, Date end) {

		this.start = start;
		if (end == null)
			this.end = new Date(Long.MAX_VALUE);
		else
			this.end = end;
	}
}
