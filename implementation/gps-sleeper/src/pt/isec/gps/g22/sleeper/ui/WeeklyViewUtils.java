package pt.isec.gps.g22.sleeper.ui;

import java.util.ArrayList;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.ExhaustionLevel;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleepDuration;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.TimeUtils;
import static pt.isec.gps.g22.sleeper.core.TimeUtils.duration;

public class WeeklyViewUtils {
	
	static final int DAY_SECONDS = 24 * 60 * 60;
	
	static List<List<SeriesValue>> recordsToSeries(final Profile profile, final List<DayRecord> records, final List<WeekDay> weekDays) {
		final ChartDay[] chartDays = new ChartDay[weekDays.size()];
		
		int accumDebt = 0;
		for(int i = 0; i < records.size(); i++) {
			final WeekDay weekDay = weekDays.get(i); 
			List<DayRecord> dayRecords = new ArrayList<DayRecord>();
			// Determine which records belong to the day
			for (DayRecord record : records) {
				if (record.getSleepDate() >= weekDay.from && record.getSleepDate() <= weekDay.until) { 
					dayRecords.add(record);
				}
			}
			
			final ExhaustionLevel exhaustionLevel = averageExhaustionLevel(dayRecords);
			final SleepQuality sleepQuality = averageSleepQuality(dayRecords);
			
			int optimumWakingTime = 0;
			int debt = 0;
			if (records.size() == 0) { // didn't sleep
				optimumWakingTime = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality);
				debt = optimumWakingTime - weekDay.from;
			} else if (records.size() == 1) { // 1 sleep period
				optimumWakingTime = optimumWakingTime(dayRecords.get(0).getSleepDate(), profile, accumDebt, exhaustionLevel, sleepQuality);
				debt = optimumWakingTime - dayRecords.get(0).getWakeupDate();
			} else { // multiple sleep periods
				final int sleepSum = sleepSum(records); // Time slept during the day
				int wakeTimeSinceDayStart = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality);
				int lastPeriodDebt = (wakeTimeSinceDayStart - weekDay.from) - sleepSum;
				debt = dayRecords.get(dayRecords.size() - 1).getWakeupDate() + lastPeriodDebt;
				optimumWakingTime = dayRecords.get(dayRecords.size() - 1).getSleepDate() + lastPeriodDebt;
			}

			chartDays[i] = new ChartDay(debt, accumDebt, optimumWakingTime, dayRecords);
			
			accumDebt += debt;
		}
		
		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>();
		for(final ChartDay chartDay : chartDays) {
			final List<SeriesValue> dayValues = new ArrayList<SeriesValue>();
			final int recordCount = chartDay.records.size();
			
			if (recordCount == 1) {
				dayValues.addAll(recordValues(chartDay.records.get(0), chartDay.optimumWakingTime));
			} else if (recordCount > 1) {
				final int lastRecordIndex = recordCount - 1;
				for (int i = 0; i < recordCount; i++) {
					final DayRecord dayRecord = chartDay.records.get(i);
					final boolean isLast = i == lastRecordIndex;
					
					if (isLast) {
						dayValues.addAll(recordValues(dayRecord, chartDay.optimumWakingTime));
					} else {
						dayValues.addAll(recordValues(dayRecord, dayRecord.getWakeupDate()));
					}
				}
			}
			
			dayValuesList.add(dayValues);
		}

		return dayValuesList; 
	}
	
	/**
	 * Calculates the sum of the time slept over a list of day recoreds.
	 * @param records the day records list
	 * @return the sum of the time slept in minutes
	 */
	static int sleepSum(final List<DayRecord> records) {
		int sleepSum = 0;
		for (DayRecord record : records) {
			sleepSum += record.getWakeupDate() - record.getSleepDate();
		}
		
		return sleepSum;
	}
	
	/**
	 * Calculates the average exhaustion level over a list of day records. Only the days with
	 * exhaustion level set (level > 0) are considered for the average calculation.
	 * @param records the day records list
	 * @return the calculated average or 0 if the days don't have exhaustion level set.
	 */
	static ExhaustionLevel averageExhaustionLevel(final List<DayRecord> records) {
		int exaustion = 0;
		int count = 0;
		
		for(final DayRecord record : records) {
			if (record.getExhaustion() > 0) {
				count++;
				exaustion += record.getExhaustion();
			}
		}
		
		return ExhaustionLevel.fromInt(exaustion / count);
	}
	
	/**
	 * Calculates the average sleep quality level over a list of day records. Only the days with
	 * sleep quality set (level > 0) are considered for the average calculation.
	 * @param records the day records list
	 * @return the calculated average or 0 if the days don't have sleep quality set.
	 */
	static SleepQuality averageSleepQuality(final List<DayRecord> records) {
		int quality = 0;
		int count = 0;
		
		for(final DayRecord record : records) {
			if (record.getSleepQuality() > 0) {
				count++;
				quality += record.getSleepQuality();
			}
		}
		
		return SleepQuality.fromInt(quality / count);
	}
	
	/**
	 * Returns the list of series values that correspond to a day record
	 * @param record the day records
	 * @param optimumWakingTime the optimum waking time for the period in the record
	 * @return the list of series values that matches the record
	 */
	static List<SeriesValue> recordValues(final DayRecord record, final int optimumWakingTime) {
		final List<SeriesValue> values = new ArrayList<SeriesValue>();
		
		values.add(new SeriesValue(record.getSleepDate(), SeriesType.SLEEP));
		if (optimumWakingTime < record.getWakeupDate()) { // oversleep
			values.add(new SeriesValue(optimumWakingTime, SeriesType.OVERSLEEP));
			values.add(new SeriesValue(record.getWakeupDate(), SeriesType.WAKE));
		} else if (optimumWakingTime > record.getWakeupDate()) { // undersleep
			values.add(new SeriesValue(record.getWakeupDate(), SeriesType.UNDERSLEEP));
			values.add(new SeriesValue(optimumWakingTime, SeriesType.WAKE));
		} else {
			values.add(new SeriesValue(record.getWakeupDate(), SeriesType.WAKE));
		}
		
		return values;
	}
	
	/**
	 * Calculates the optimum wake time for a sleep period
	 * @param start the start of the sleep period
	 * @param profile the user profile
	 * @param accumDebt the accumulated sleep debt 
	 * @param exhaustionLevel the exhaustion level at the start of the period
	 * @param sleepQuality the sleep quality of the previous period
	 * @return the optimum waking time
	 */
	static int optimumWakingTime(final int start, final Profile profile, final int accumDebt, final ExhaustionLevel exhaustionLevel, 
			final SleepQuality sleepQuality, final int now) {
		final boolean isMale = profile.getGender() == 0;
		final int age = TimeUtils.ageFromDateOfBirth(profile.getDateOfBirth(), now);
		int base = SleepDuration.getDuration(age, isMale);
		int delta = SleepDelta.getDelta(accumDebt, exhaustionLevel, sleepQuality);
		
		return start + base + delta;
	}
	
	/**
	 * Returns the list of days in a week, starting at the time in the start parameter
	 * @param start the time when the week starts
	 * @return the list of week days the correspond to the week
	 */
	static List<WeekDay> getWeek(final int start) {
		final List<WeekDay> week = new ArrayList<WeekDay>(7);
		int seconds = start;
		for (int i = 0; i < 7; i++) {
			week.add(new WeekDay(seconds, seconds + DAY_SECONDS - 1));
			seconds += DAY_SECONDS;
		}
		
		return week;
	}
}

class SleepDelta {
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
	
	public static int getDelta(final int accumDebt, final ExhaustionLevel exhaustionLevel, final SleepQuality sleepQuality) {
//		if (exhaustionLevel == null || sleepQuality == null) {
//			return 0;
//		}
//		
//		final int[] x_values = x_axis[exhaustionLevel.getLevel() - 1];
//		final int[] y_values = y_axis[sleepQuality.getLevel() - 1];
		
		return 0;
	}
	
//	private static int getXValue(final int[] values, final int value) {
//		
//	}
}

/**
 * Represents a week day
 */
class WeekDay {
	final int from;
	final int until;

	public WeekDay(final int from, final int until) {
		super();
		this.from = from;
		this.until = until;
	}
}

/**
 * Represents a day in the bar chart
 */
class ChartDay {
	final int debt;
	final int accumulatedDebt;
	final int optimumWakingTime;
	final List<DayRecord> records;
	
	public ChartDay(int debt, int accumulatedDebt, int optimumWakingTime, List<DayRecord> records) {
		super();
		this.debt = debt;
		this.accumulatedDebt = accumulatedDebt;
		this.optimumWakingTime = optimumWakingTime;
		this.records = records;
	}
}

/**
 * Enumeration of the bar chart series types
 */
enum SeriesType {
	WAKE,
	SLEEP,
	UNDERSLEEP,
	OVERSLEEP
}

class SeriesValue {
	final int value;
	final SeriesType type;

	public SeriesValue(final int value, final SeriesType type) {
		super();
		this.value = value;
		this.type = type;
	}
}