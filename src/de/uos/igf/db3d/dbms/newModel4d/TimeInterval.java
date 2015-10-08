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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
