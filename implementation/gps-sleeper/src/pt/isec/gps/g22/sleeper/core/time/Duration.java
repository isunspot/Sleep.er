package pt.isec.gps.g22.sleeper.core.time;

public class Duration {
	
	private static final int SECONDS_MS = 1000;
	private static final int MINUTES_MS = 60 * SECONDS_MS;
	private static final int HOURS_MS = 60 * MINUTES_MS;
	
	private final long amount;

	private Duration(final long amount) {
		super();
		this.amount = amount;
	}
	
	public static Duration duration(final int hours) {
		return duration(hours, 0);
	}
	
	public static Duration duration(final int hours, final int minutes) {
		return duration(hours, minutes, 0);
	}
	
	public static Duration duration(final int hours, final int minutes, final int seconds) {
		return new Duration(0);
	}
	
}
