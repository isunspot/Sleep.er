package pt.isec.gps.g22.sleeper.dal;

import pt.isec.gps.g22.sleeper.core.Profile;

/**
 * DAO for the profile
 */
public interface ProfileDAO {
	/**
	 * Inserts the user profile
	 *
	 * @param profile the profile to insert
	 * @return the row id, or -1 if an error occurred
	 */
	int insertProfile(Profile profile);

	/**
	 * Updates the user profile
	 *
	 * @param profile the profile to update
	 * @return the number of rows affected
	 */
	int updateProfile(Profile profile);

	/**
	 * Returns the user profile
	 *
	 * @return the user profile, or null if the profile is not defined
	 */
	Profile loadProfile();
}
