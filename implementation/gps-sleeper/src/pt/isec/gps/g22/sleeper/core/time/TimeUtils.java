package pt.isec.gps.g22.sleeper.core.time;

import java.util.Calendar;

/**
 * Time utility methods
 */
public class TimeUtils {
	
	public static final String DATE_SEPARATOR = "-";
	public static final String TIME_SEPARATOR = ":";
	
	public static String formatDuration(final TimeDelta value) {
		final int minutes = value.asSeconds() / 60;
		final int hours = minutes / 60;
		
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

	public static String getDate(final DateTime dateTime) {
		final Calendar cal = dateTime.asCalendar();
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1;
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		
		return year + "/" + month + "/" + day;
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
	
	public static String formatHoursMinutes(final TimeOfDay time) {
		return formatHoursMinutes(time.getHours(), time.getMinutes());
	}
	
	public static String formatHoursMinutes(final int hours, final int minutes) {
		return pad(hours) + TIME_SEPARATOR + pad(minutes);
	}
	
	public static String pad(final int value) {
		return value < 10 ? "0" + value : Long.toString(value);
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
