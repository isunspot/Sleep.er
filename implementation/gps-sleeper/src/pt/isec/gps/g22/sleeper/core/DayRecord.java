package pt.isec.gps.g22.sleeper.core;

/**
 * The day record entity
 * 
 * @author marcos
 *
 */
public class DayRecord {
	private int id;
	private int sleepDate;
	private int exhaustion; // if unset, 0
	private int wakeupDate;
	private int sleepQuality; // if unset, 0

	public static final String TABLE_NAME = "dayrecord";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SLEEPDATE = "sleepdate";
	public static final String COLUMN_EXHAUSTION = "exhaustion";
	public static final String COLUMN_WAKEUPDATE = "wakeupdate";
	public static final String COLUMN_SLEEPQUALITY = "sleepquality";

	public DayRecord() {
	}

	public DayRecord(int sleepDate, int wakeupDate) {
		this.sleepDate = sleepDate;
		this.wakeupDate = wakeupDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSleepDate() {
		return sleepDate;
	}

	public void setSleepDate(int sleepDate) {
		this.sleepDate = sleepDate;
	}

	public int getExhaustion() {
		return exhaustion;
	}

	public void setExhaustion(int exhaustion) {
		this.exhaustion = exhaustion;
	}

	public int getWakeupDate() {
		return wakeupDate;
	}

	public void setWakeupDate(int wakeupDate) {
		this.wakeupDate = wakeupDate;
	}

	public int getSleepQuality() {
		return sleepQuality;
	}

	public void setSleepQuality(int sleepQuality) {
		this.sleepQuality = sleepQuality;
	}
}