package pt.isec.gps.g22.sleeper.core.time;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTime implements Comparable<DateTime> {

	private final long value;
	
	private DateTime(final long value) {
		this.value = value;
	}
	
	public DateTime add(final TimeDelta delta) {
		return new DateTime(value + delta.asSeconds());
	}
	
	@Override
	public int compareTo(DateTime another) {
		return Long.valueOf(value).compareTo(Long.valueOf(another.value));
	}
	
	public Calendar asCalendar() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date(value * 1000));
		
		return cal;
	}
	
	public static DateTime now() {
		return new DateTime(System.currentTimeMillis() / 1000);
	}
	
}
