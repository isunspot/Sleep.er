package pt.isec.gps.g22.sleeper.core;

import static pt.isec.gps.g22.sleeper.core.time.TimeDelta.duration;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;

/**
 * 
 */
public class SleepDuration {
	private final int minAge, maxAge;
	private final TimeDelta minMale, maxMale, minFemale, maxFemale;
	
	private SleepDuration(int minAge, int maxAge, TimeDelta minMale,
			TimeDelta maxMale, TimeDelta minFemale, TimeDelta maxFemale) {
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
	
	TimeDelta meanTimeDelta(final boolean male) {
		return male ? mean(minMale, maxMale) : mean(minFemale, maxFemale);
	}
	
	static TimeDelta mean(final TimeDelta min, final TimeDelta max) {
		return TimeDelta.fromSeconds((min.asSeconds() + max.asSeconds()) / 2);
	}
	
	static {
		durations = new SleepDuration[3];
		durations[0] = new SleepDuration(13, 18, duration(8), duration(9), duration(8), duration(9));
		durations[1] = new SleepDuration(19, 65, duration(7), duration(8), duration(7, 30), duration(8, 30));
		durations[2] = new SleepDuration(66, 120, duration(8), duration(9), duration(8, 30), duration(9, 30));
	}
	
	public static TimeDelta getTimeDelta(final long age, final boolean male) {
		for(int i = 0; i < durations.length; i++) {
			final SleepDuration duration = durations[i];
			if (duration.contains(age)) {
				return duration.meanTimeDelta(male);
			}
		}
		
		throw new IllegalArgumentException("Invalid age");
	}
	
}