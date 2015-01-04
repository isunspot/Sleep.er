package pt.isec.gps.g22.sleeper.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The day record entity
 * 
 * @author marcos
 *
 */
public class DayRecord {
	private int id;
	private long sleepDate;
	private int exhaustion; // if unset, 0
	private long wakeupDate;
	private int sleepQuality; // if unset, 0

	public static final String TABLE_NAME = "dayrecord";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SLEEPDATE = "sleepdate";
	public static final String COLUMN_EXHAUSTION = "exhaustion";
	public static final String COLUMN_WAKEUPDATE = "wakeupdate";
	public static final String COLUMN_SLEEPQUALITY = "sleepquality";

	public DayRecord() {
	}

	public DayRecord(long sleepDate, long wakeupDate) {
		this.sleepDate = sleepDate;
		this.wakeupDate = wakeupDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getSleepDate() {
		return sleepDate;
	}

	public void setSleepDate(long sleepDate) {
		this.sleepDate = sleepDate;
	}

	public int getExhaustion() {
		return exhaustion;
	}

	public void setExhaustion(int exhaustion) {
		this.exhaustion = exhaustion;
	}

	public long getWakeupDate() {
		return wakeupDate;
	}

	public void setWakeupDate(long wakeupDate) {
		this.wakeupDate = wakeupDate;
	}

	public int getSleepQuality() {
		return sleepQuality;
	}

	public void setSleepQuality(int sleepQuality) {
		this.sleepQuality = sleepQuality;
	}
	
	public boolean recordOverlap(final List<DayRecord> tempList) {
		if(tempList == null) {
			return false;
		} else {
			for(final DayRecord tempRecord : tempList) {
				if (this.getId() == tempRecord.getId()) continue;

				if(this.sleepDate >= tempRecord.getSleepDate() && this.sleepDate <= tempRecord.getWakeupDate()) // Intersection at the left side with another record
					return true;
				else if(this.sleepDate <= tempRecord.getSleepDate() && this.wakeupDate >= tempRecord.getWakeupDate()) // This record overlaps a record entirely
					return true;
				else if(this.wakeupDate >= tempRecord.getSleepDate() && this.wakeupDate <= tempRecord.getWakeupDate()) // Intersection at the right side with another record
					return true;
				else if(this.sleepDate >= tempRecord.getSleepDate() && this.wakeupDate <= tempRecord.getWakeupDate()) // This record is overlapped by another record
					return true;
			}

			return false;
		}			
	}
}