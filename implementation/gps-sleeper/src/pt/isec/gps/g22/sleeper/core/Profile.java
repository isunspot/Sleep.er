package pt.isec.gps.g22.sleeper.core;

/**
 * The profile entity
 */
public class Profile {
	private int id;
	private int gender;
	private int dateOfBirth;
	private int firstHourOfTheDay;

	public static final String TABLE_NAME = "profile";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_DATEOFBIRTH = "dateofbirth";
	public static final String COLUMN_FIRSTHOUROFTHEDAY = "firsthouroftheday";

	public Profile() {
	}

	public Profile(int gender, int dateOfBirth, int firstHourOfTheDay) {
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.firstHourOfTheDay = firstHourOfTheDay;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(int dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public int getFirstHourOfTheDay() {
		return firstHourOfTheDay;
	}

	public void setFirstHourOfTheDay(int firstHourOfTheDay) {
		this.firstHourOfTheDay = firstHourOfTheDay;
	}
}
