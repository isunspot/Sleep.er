package pt.isec.gps.g22.sleeper.core.time;

import java.util.Calendar;

/**
 * Time utility methods
 */
public class TimeUtils {
	
	public static final String DATE_SEPARATOR = "/";
	public static final String TIME_SEPARATOR = ":";
	
	public static String formatDuration(final TimeDelta value) {
		final long hours = value.asSeconds() / 60 / 60;
		final long minutes = (value.asSeconds() / 60) - hours * 60;
		
		if (hours > 0) {
			return formatHoursMinutes(hours, minutes);
		} else {
			return minutes % 60 + "m";
		}
	}
	
	public static TimeDelta ageFromDateOfBirth(final DateTime dateOfBirth, final DateTime now) {
		if (dateOfBirth.after(now)) {
			throw new IllegalArgumentException(
					"The date of birth cannot be in the future");
		}

		return now.diff(dateOfBirth);
	}

	public static String formatDate(final DateTime dateTime) {
		return dateTime.getYear() + DATE_SEPARATOR + dateTime.getMonth() + DATE_SEPARATOR + dateTime.getDay();
	}

	public static String getTime(final long time) {
		final long hours = minutesToHours(time);
		final long minutes = minutesToMinutes(time);
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
	
	public static String formatHoursMinutes(final TimeOfDay time) {
		return formatHoursMinutes(time.getHours(), time.getMinutes());
	}
	
	public static String formatHoursMinutes(final long hours, final long minutes) {
		return pad(hours) + TIME_SEPARATOR + pad(minutes);
	}
	
	public static String pad(final long value) {
		return value < 10 ? "0" + value : Long.toString(value);
	}

	public static long minutesToHours(final long time) {
		return time / 60;
	}

	public static long minutesToMinutes(final long time) {
		return time % 60;
	}

	public static long convertToMinutes(final long hours, final long minutes) {
		return hours * 60 + minutes;
	}
	
	public static TimeDelta days(final int value) {
		return days(value, true);
	}
	
	public static TimeDelta days(final int value, final boolean positive) {
		return TimeDelta.duration(value * 24, positive);
	}
	
	public static TimeDelta weeks(final int value) {
		return weeks(value, true);
	}
	
	public static TimeDelta weeks(final int value, final boolean positive) {
		return TimeDelta.duration(value * 7 * 24, positive);
	}
	
	public static TimeDelta years(final int value) {
		return years(value, true);
	}
	
	public static TimeDelta years(final int value, final boolean positive) {
		return TimeDelta.duration(value * 365 * 24, positive);
	}
}
