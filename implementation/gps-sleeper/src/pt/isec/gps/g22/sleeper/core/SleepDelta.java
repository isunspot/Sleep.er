package pt.isec.gps.g22.sleeper.core;

import static pt.isec.gps.g22.sleeper.core.TimeUtils.duration;

/**
 * Class for the calculation of the sleep delta
 */
public class SleepDelta {
	
	static final int[][] x_axis = {
			{ -duration(1, 30), -duration(1), -duration(0, 30), 0, duration(0, 30), duration(1), duration(1, 30) },
			{ -duration(1, 10), -duration(0, 50), -duration(0, 30), 0, duration(0, 30), duration(1), duration(1, 30) },
			{ -duration(1), -duration(0, 45), -duration(0, 30), 0, duration(0, 30), duration(1, 15), duration(2) }
	};
	static final int[][] y_axis = {
			{ duration(1, 10), duration(0, 50), duration(0, 30), 0, -duration(0, 10), -duration(0, 20), -duration(0, 30) },
			{ duration(1), duration(0, 40), duration(0, 20), 0, -duration(0, 15), -duration(0, 30), -duration(0, 45) },
			{ duration(0, 30), duration(0, 20), duration(0, 10), 0, -duration(0, 20), -duration(0, 40), -duration(1) }
	};
	
	/**
	 * Returns the corresponding sleep delta. If either the exhaustion level or the sleep quality are null, the delta is zero.
	 * @param accumDebt the accumulated sleep debt
	 * @param exhaustionLevel the exhaustion level
	 * @param sleepQuality the previous day sleep quality
	 * @return the sleep delta
	 */
	public static int getDelta(final long accumDebt, final ExhaustionLevel exhaustionLevel, final SleepQuality sleepQuality) {
		if (exhaustionLevel == null || sleepQuality == null) {
			return 0;
		}
		
		final int[] x_values = x_axis[exhaustionLevel.getLevel() - 1];
		final int[] y_values = y_axis[sleepQuality.getLevel() - 1];
		
		final int x = getXValue(x_values, accumDebt);
		final int y = y_values[x];
		
		return y;
	}
	
	/**
	 * Returns the correct x value
	 * @param values the values array
	 * @param value the value
	 * @return the x value
	 */
	private static int getXValue(final int[] values, final long value) {
		if (value <= values[0]) {
			return 0;
		} else if (value > values[0] && value <= values[1]) {
			return 1;
		} else if (value > values[1] && value <= values[2]) {
			return 2;
		} else if (value > values[2] && value < values[3]) {
			return 3;
		} else if (value >= values[3] && value < values[4]) {
			return 4;
		} else if (value >= values[4] && value < values[5]) {
			return 5;
		} else {
			return 6;
		}
	}
}