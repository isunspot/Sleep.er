package pt.isec.gps.g22.sleeper.core;

import static pt.isec.gps.g22.sleeper.core.TimeUtils.duration;

/**
 * 
 */
public class SleepDuration {
	private final int minAge, maxAge, minMale, maxMale, minFemale, maxFemale;
	
	private SleepDuration(int minAge, int maxAge, int minMale,
			int maxMale, int minFemale, int maxFemale) {
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.minMale = minMale;
		this.maxMale = maxMale;
		this.minFemale = minFemale;
		this.maxFemale = maxFemale;
	}
	
	private static SleepDuration[] durations;
	
	boolean contains(final long age) {
		return age >= minAge && age <= maxAge;
	}
	
	int meanDuration(final boolean male) {
		return male ? mean(minMale, maxMale) : mean(minFemale, maxFemale);
	}
	
	static int mean(final int min, final int max) {
		return (min + max) / 2;
	}
	
	static {
		durations = new SleepDuration[3];
		durations[0] = new SleepDuration(13, 18, duration(8), duration(9), duration(8), duration(9));
		durations[1] = new SleepDuration(19, 65, duration(7), duration(8), duration(7, 30), duration(8, 30));
		durations[2] = new SleepDuration(66, 120, duration(8), duration(9), duration(8, 30), duration(9, 30));
	}
	
	public static long getDuration(final long age, final boolean male) {
		for(int i = 0; i < durations.length; i++) {
			final SleepDuration duration = durations[i];
			if (duration.contains(age)) {
				return duration.meanDuration(male);
			}
		}
		
		throw new IllegalArgumentException("Invalid age");
	}
	
}