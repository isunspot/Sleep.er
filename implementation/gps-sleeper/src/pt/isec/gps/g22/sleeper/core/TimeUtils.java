package pt.isec.gps.g22.sleeper.core;

import java.util.Calendar;

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

	public static long ageFromDateOfBirth(final long dateOfBirth, final long now) {
		if (dateOfBirth > now) {
			throw new IllegalArgumentException(
					"The date of birth cannot be in the future");
		}

		return (now - dateOfBirth) / (365 * 24 * 60 * 60);
	}

	public static String getDate(long unixtime) {
		Calendar cal = Calendar.getInstance();
		unixtime = unixtime * 1000;
		cal.setTimeInMillis(unixtime);
		int month = cal.get(Calendar.MONTH) + 1;
		String s = cal.get(Calendar.YEAR) + "/" + month + "/"
				+ cal.get(Calendar.DAY_OF_MONTH);
		return s;
	}

	public static String getTime(int time) {
		int hours = minutesToHours(time);
		int minutes = minutesToMinutes(time);
		StringBuilder sb = new StringBuilder();

		if (hours < 10)
			sb.append("0");
		sb.append(hours);
		sb.append(":");
		if (minutes < 10)
			sb.append("0");
		sb.append(minutes);
		return sb.toString();
	}

	public static int minutesToHours(int time) {
		return time / 60;
	}

	public static int minutesToMinutes(int time) {
		return time % 60;
	}

	public static int convertToMinutes(int hours, int minutes) {
		return hours * 60 + minutes;
	}
}
