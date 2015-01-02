package pt.isec.gps.g22.sleeper.core;

/**
 * Enumeration of the possible sleep quality levels
 */
public enum SleepQuality {
	LOW(1),
	MEDIUM(2),
	HIGH(3);
	
	private final int level;

	private SleepQuality(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}

	public static SleepQuality fromInt(final int quality) {
		switch (quality) {
		case 1: return LOW;
		case 2: return MEDIUM;
		case 3: return HIGH;
		default: throw new IllegalArgumentException("Invalid quality");
		}
	}
}