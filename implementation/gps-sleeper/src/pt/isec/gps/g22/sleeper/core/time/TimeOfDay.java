package pt.isec.gps.g22.sleeper.core.time;

public class TimeOfDay implements Comparable<TimeOfDay> {

	private static final int MINUTES = 60;
	private static final int HOURS = 60 * MINUTES;
	
	private final int value;
	
	private TimeOfDay(final int value) {
		this.value = value;
	}
	
	public int getHours() {
		return value / 60 / 60;
	}
	
	public int getMinutes() {
		return (value / 60) - getHours() * 60;
	}
	
	public int getSeconds() {
		return value - getHours() * HOURS - getMinutes() * MINUTES;
	}
	
	public TimeDelta asTimeDelta() {
		return TimeDelta.fromSeconds(value);
	}
	
	@Override
	public int compareTo(final TimeOfDay another) {
		return Integer.valueOf(value).compareTo(Integer.valueOf(another.value));
	}
	
	public static TimeOfDay at(final int hours, final int minutes) {
		return at(hours, minutes, 0);
	}
	
	public static TimeOfDay at(final int hours, final int minutes, final int seconds) {
		return fromSeconds(hours * HOURS + minutes * MINUTES + seconds);
	}
	
	public static TimeOfDay fromSeconds(final int seconds) {
		return new TimeOfDay(seconds);
	}
	
}
