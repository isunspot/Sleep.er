package pt.isec.gps.g22.sleeper.core;

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
}