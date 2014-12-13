package pt.isec.gps.g22.sleeper.core.time;

public class TimeDelta implements Comparable<TimeDelta> {
	
	private static final int SECONDS_MS = 1000;
	private static final int MINUTES_MS = 60 * SECONDS_MS;
	private static final int HOURS_MS = 60 * MINUTES_MS;
	
	private final long amount;

	private TimeDelta(final long amount) {
		super();
		this.amount = amount;
	}
	
	public int asSeconds() {
		return (int) amount * SECONDS_MS;
	}
	
	public boolean isPositive() {
		return amount >= 0;
	}
	
	public TimeDelta add(final TimeDelta another) {
		return new TimeDelta(amount + another.amount);
	}
	
	@Override
	public int compareTo(final TimeDelta another) {
		return Long.valueOf(amount).compareTo(Long.valueOf(another.amount));
	}
	
	public static TimeDelta fromSeconds(final long seconds) {
		return new TimeDelta(seconds * SECONDS_MS);
	}
	
	public static TimeDelta fromSeconds(final long seconds, final boolean positive) {
		return new TimeDelta(seconds * SECONDS_MS * (positive ? 1 : -1));
	}
	
	public static TimeDelta duration(final long hours) {
		return duration(hours, 0, true);
	}
	
	public static TimeDelta duration(final long hours, final boolean positive) {
		return duration(hours, 0, positive);
	}
	
	public static TimeDelta duration(final long hours, final long minutes) {
		return duration(hours, minutes, 0, true);
	}
	
	public static TimeDelta duration(final long hours, final long minutes, final boolean positive) {
		return duration(hours, minutes, 0, positive);
	}
	
	public static TimeDelta duration(final long hours, final long minutes, final long seconds) {
		return fromSeconds(hours * HOURS_MS + minutes * MINUTES_MS + seconds * SECONDS_MS, true);
	}
	
	public static TimeDelta duration(final long hours, final long minutes, final long seconds, final boolean positive) {
		return fromSeconds(hours * HOURS_MS + minutes * MINUTES_MS + seconds * SECONDS_MS, positive);
	}
	
}
