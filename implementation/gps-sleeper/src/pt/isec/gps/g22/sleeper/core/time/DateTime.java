package pt.isec.gps.g22.sleeper.core.time;

import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.DATE_SEPARATOR;
import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.TIME_SEPARATOR;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTime implements Comparable<DateTime> {

	private final long value;
	private Calendar calendar;
	
	private DateTime(final long value) {
		this.value = value;
	}
	
	public DateTime add(final TimeDelta delta) {
		return new DateTime(value + delta.asSeconds());
	}
	
	public int getYear() {
		return getCalendar().get(Calendar.YEAR);
	}
	
	public int getMonth() {
		return getCalendar().get(Calendar.MONTH);
	}
	
	public int getDay() {
		return getCalendar().get(Calendar.DAY_OF_MONTH) + 1;
	}
	
	public int getHours() {
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinutes() {
		return getCalendar().get(Calendar.MINUTE);
	}
	
	public int getSeconds() {
		return getCalendar().get(Calendar.SECOND);
	}
	
	@Override
	public int compareTo(DateTime another) {
		return Long.valueOf(value).compareTo(Long.valueOf(another.value));
	}
	
	public boolean before(final DateTime another) {
		return compareTo(another) < 0;
	}
	
	public boolean same(final DateTime another) {
		return compareTo(another) == 0;
	}
	
	public boolean beforeOrSame(final DateTime another) {
		return before(another) || same(another);
	}
	
	public boolean after(final DateTime another) {
		return !beforeOrSame(another);
	}
	
	public boolean afterOrSame(final DateTime another) {
		return !before(another);
	}
	
	public TimeDelta diff(final DateTime another) {
		return TimeDelta.fromSeconds(value - another.value);
	}
	
	public Calendar asCalendar() {
		return getCalendar();
	}
	
	public long toUnixTimestamp() {
		return value;
	}
	
	public long toMillis() {
		return value * 1000;
	}
	
	public TimeOfDay toTimeOfDay() {
		return TimeOfDay.at(getHours(), getMinutes(), getSeconds());
	}
	
	private Calendar getCalendar() {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			calendar.setTime(new Date(value * 1000));
		}
		
		return calendar;
	}
	
	@Override
	public String toString() {
		return getYear() + DATE_SEPARATOR + getMonth() + DATE_SEPARATOR + getDay() +
				" " + getHours() + TIME_SEPARATOR + getMinutes() + TIME_SEPARATOR + getSeconds();
	}

	public static DateTime now() {
		return new DateTime(System.currentTimeMillis() / 1000);
	}
	
	public static DateTime fromSeconds(final long seconds) {
		return new DateTime(seconds);
	}
	
	public static DateTime fromDate(final int year, final int month, final int day) {
		return fromDateTime(year, month, day, 0, 0, 0);
	}
	
	public static DateTime fromDateTime(final DateTime date, final TimeOfDay time) {
		return fromDateTime(date.getYear(), date.getMonth(), date.getDay(), time.getHours(), time.getMinutes(), 0);
	}
	
	public static DateTime fromDateTime(final int year, final int month, final int day, final int hours, final int minutes, final int seconds) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		cal.set(Calendar.MILLISECOND, 0);
		
		return fromSeconds(cal.getTimeInMillis() / 1000);
	}
	
}
