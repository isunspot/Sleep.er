package pt.isec.gps.g22.sleeper.core;

/**
 * The profile entity
 */
public class Profile {
	private int id;
	private int gender;
	private int dateOfBirth;

	public Profile() {
	}

	public Profile(int gender, int dateOfBirth) {
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
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
}
