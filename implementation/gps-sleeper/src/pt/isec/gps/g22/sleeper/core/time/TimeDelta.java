package pt.isec.gps.g22.sleeper.core.time;

public class TimeDelta implements Comparable<TimeDelta> {
	
	private static final int SECONDS_MS = 1000;
	private static final int MINUTES_S = 60;
	private static final int HOURS_S = 60 * MINUTES_S;
	
	private final long amount;

	private TimeDelta(final long amount) {
		super();
		this.amount = amount;
	}
	
	public int asSeconds() {
		return (int) amount / SECONDS_MS;
	}
	
	public boolean isPositive() {
		return amount >= 0;
	}
	
	public TimeDelta add(final TimeDelta another) {
		return new TimeDelta(amount + another.amount);
	}
	
	public TimeDelta subtract(final TimeDelta another) {
		return new TimeDelta(amount - another.amount);
	}
	
	@Override
	public int compareTo(final TimeDelta another) {
		return Long.valueOf(amount).compareTo(Long.valueOf(another.amount));
	}
	
	public static TimeDelta fromSeconds(final long seconds) {
		return fromSeconds(seconds, true);
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
		return duration(hours, minutes, seconds, true);
	}
	
	public static TimeDelta duration(final long hours, final long minutes, final long seconds, final boolean positive) {
		return fromSeconds(hours * HOURS_S + minutes * MINUTES_S + seconds, positive);
	}
	
}
