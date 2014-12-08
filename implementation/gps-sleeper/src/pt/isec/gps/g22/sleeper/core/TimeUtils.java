package pt.isec.gps.g22.sleeper.core;

/**
 * Time utility methods
 */
public class TimeUtils {
	public static int duration(final int hours) {
		return duration(hours, 0);
	}

	public static int duration(final int hours, final int minutes) {
		return hours * 60 + minutes;
	}
	
	public static int ageFromDateOfBirth(final int dateOfBirth, final int now) {
		if (dateOfBirth > now) {
			throw new IllegalArgumentException("The date of birth cannot be in the future");
		}
		
		return (now - dateOfBirth) / (365 * 24 * 60 * 60);
	}
}
