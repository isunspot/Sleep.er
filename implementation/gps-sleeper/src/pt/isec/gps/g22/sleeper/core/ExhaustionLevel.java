package pt.isec.gps.g22.sleeper.core;

/**
 * Enumeration of the possible exhaustion levels
 */
public enum ExhaustionLevel {
	LOW(1),
	MEDIUM(2),
	HIGH(3);
	
	private final int level;

	public int getLevel() {
		return level;
	}

	private ExhaustionLevel(int level) {
		this.level = level;
	}
	
	public static ExhaustionLevel fromInt(final int level) {
		switch (level) {
		case 1: return LOW;
		case 2: return MEDIUM;
		case 3: return HIGH;
		default: throw new IllegalArgumentException("Invalid level");
		}
	}
}
