package pt.isec.gps.g22.sleeper.dal;

import pt.isec.gps.g22.sleeper.core.Profile;

/**
 * DAO for the profile
 */
public interface ProfileDAO {
	/**
	 * Creates the user profile
	 * 
	 * @param profile
	 *            the profile to insert
	 */
	void insertProfile(Profile profile);

	/**
	 * Updates the user profile
	 * 
	 * @param profile
	 *            the profile to update
	 */
	void updateProfile(Profile profile);

	/**
	 * Returns the user profile
	 * 
	 * @return the user profile, or null if the profile is not defined
	 */
	Profile getProfile();
}
