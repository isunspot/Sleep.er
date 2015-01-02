package pt.isec.gps.g22.sleeper.core;

import pt.isec.gps.g22.sleeper.core.time.TimeDelta;

import static pt.isec.gps.g22.sleeper.core.time.TimeDelta.duration;

/**
 * Class for the calculation of the sleep delta
 */
public class SleepDelta {
	
	static final TimeDelta[][] x_axis = {
			{ duration(1, 30, false), duration(1, false), duration(0, 30, false), duration(0), duration(0, 30), duration(1), duration(1, 30) },
			{ duration(1, 10, false), duration(0, 50, false), duration(0, 30, false), duration(0), duration(0, 30), duration(1), duration(1, 30) },
			{ duration(1, false), duration(0, 45), duration(0, 30, false), duration(0), duration(0, 30), duration(1, 15), duration(2) }
	};
	static final TimeDelta[][] y_axis = {
			{ duration(1, 10), duration(0, 50), duration(0, 30), duration(0), duration(0, 10, false), duration(0, 20, false), duration(0, 30, false) },
			{ duration(1), duration(0, 40), duration(0, 20), duration(0), duration(0, 15, false), duration(0, 30, false), duration(0, 45, false) },
			{ duration(0, 30), duration(0, 20), duration(0, 10), duration(0), duration(0, 20, false), duration(0, 40, false), duration(1, false) }
	};
	
	/**
	 * Returns the corresponding sleep delta. If either the exhaustion level or the sleep quality are null, the delta is zero.
	 * @param accumDebt the accumulated sleep debt
	 * @param exhaustionLevel the exhaustion level
	 * @param sleepQuality the previous day sleep quality
	 * @return the sleep delta
	 */
	public static TimeDelta getDelta(final TimeDelta accumDebt, final ExhaustionLevel exhaustionLevel, final SleepQuality sleepQuality) {
		if (exhaustionLevel == null || sleepQuality == null) {
			return duration(0);
		}
		
		final TimeDelta[] x_values = x_axis[exhaustionLevel.getLevel() - 1];
		final TimeDelta[] y_values = y_axis[sleepQuality.getLevel() - 1];
		
		final int x = getXValue(x_values, accumDebt);
		final TimeDelta y = y_values[x];
		
		return y;
	}
	
	/**
	 * Returns the correct x value
	 * @param values the values array
	 * @param value the value
	 * @return the x value
	 */
	private static int getXValue(final TimeDelta[] values, final TimeDelta value) {
		if (value.compareTo(values[0]) <= 0) {
			return 0;
		} else if (value.compareTo(values[0]) > 0 && value.compareTo(values[1]) <= 0) {
			return 1;
		} else if (value.compareTo(values[1]) > 0 && value.compareTo(values[2]) <= 0) {
			return 2;
		} else if (value.compareTo(values[2]) > 0 && value.compareTo(values[3]) <= 0) {
			return 3;
		} else if (value.compareTo(values[3]) > 0 && value.compareTo(values[4]) <= 0) {
			return 4;
		} else if (value.compareTo(values[4]) > 0 && value.compareTo(values[5]) <= 0) {
			return 5;
		} else {
			return 6;
		}
	}
}