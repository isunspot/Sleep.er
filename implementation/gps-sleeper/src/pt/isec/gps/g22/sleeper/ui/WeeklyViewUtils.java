package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.ExhaustionLevel;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleepDelta;
import pt.isec.gps.g22.sleeper.core.SleepDuration;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.TimeUtils;

public class WeeklyViewUtils {
	
	static final int MINUTE_SECONDS = 60;
	static final int HOUR_SECONDS = 60 * MINUTE_SECONDS;
	static final int DAY_SECONDS = 24 * HOUR_SECONDS;
	static final int WEEK_SECONDS = 7 * DAY_SECONDS;
	
	static long getWeekStart(final long now, final long dayStart) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(now * 1000)); // Date accepts milliseconds
		final int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - 1) * DAY_SECONDS; // Sunday is 1
		final int hours = cal.get(Calendar.HOUR_OF_DAY) * HOUR_SECONDS;
		final int minutes = cal.get(Calendar.MINUTE) * MINUTE_SECONDS;
		final int seconds = cal.get(Calendar.SECOND);
		
		return now - dayOfWeek - hours - minutes - seconds - (HOUR_SECONDS - dayStart);
	}
	
	/**
	 * Calculates a list of the series values for each chart day
	 * @param chartDays an array of chart days
	 * @return a list with lists of series values
	 */
	static List<List<SeriesValue>> recordsToSeries(final ChartDay[] chartDays) {
		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>();
		for(int i = 0; i < chartDays.length; i++) {
			final ChartDay chartDay = chartDays[i];
			final List<SeriesValue> dayValues = new ArrayList<SeriesValue>();
			final int recordCount = chartDay.records.size();
			
			if (recordCount == 1) {
				dayValues.addAll(recordValues(chartDay.from, chartDay.until, chartDay.records.get(0), chartDay.optimumWakingTime));
			} else if (recordCount > 1) {
				final int lastRecordIndex = recordCount - 1;
				for (int j = 0; j < recordCount; j++) {
					final DayRecord dayRecord = chartDay.records.get(j);
					final boolean isLast = j == lastRecordIndex;
					
					if (isLast) {
						dayValues.addAll(recordValues(chartDay.from, chartDay.until, dayRecord, chartDay.optimumWakingTime));
					} else {
						dayValues.addAll(recordValues(chartDay.from, chartDay.until, dayRecord, dayRecord.getWakeupDate()));
					}
				}
			}
			
			dayValuesList.add(dayValues);
		}

		return dayValuesList; 
	}
	
	/**
	 * Calculates the chart days
	 * @param profile the user profile
	 * @param records a list of day records
	 * @param weekDays a list of week days
	 * @param now the current moment
	 * @return an array of chart days
	 */
	static ChartDay[] getChartDays(final Profile profile, final List<DayRecord> records, final List<WeekDay> weekDays, final long now) {
		final ChartDay[] chartDays = new ChartDay[weekDays.size()];
		
		long accumDebt = 0;
		for(int i = 0; i < weekDays.size(); i++) {
			/*
			 * Determine which records belong to the day
			 */
			final WeekDay weekDay = weekDays.get(i); 
			List<DayRecord> dayRecords = new ArrayList<DayRecord>();
			
			for (DayRecord record : records) {
				final boolean recordInDay = 
						(record.getSleepDate() >= weekDay.from && record.getSleepDate() <= weekDay.until) ||
						(record.getWakeupDate() >= weekDay.from && record.getWakeupDate() <= weekDay.until);

				if (recordInDay) { 
					dayRecords.add(record);
				}
			}
			
			final ExhaustionLevel exhaustionLevel = averageExhaustionLevel(dayRecords);
			final SleepQuality sleepQuality = averageSleepQuality(dayRecords);
			
			/*
			 * Calculate the optimum waking time and the sleep debt
			 */
			long optimumWakingTime = 0;
			long debt = 0;
			if (dayRecords.size() == 0) { // didn't sleep
				optimumWakingTime = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality, now);
				debt = optimumWakingTime - weekDay.from;
			} else if (dayRecords.size() == 1) { // 1 sleep period
				optimumWakingTime = optimumWakingTime(dayRecords.get(0).getSleepDate(), profile, accumDebt, exhaustionLevel, sleepQuality, now);
				debt = optimumWakingTime - dayRecords.get(0).getWakeupDate();
			} else { // multiple sleep periods
				final long sleepSum = sleepSum(records); // Time slept during the day
				long wakeTimeSinceDayStart = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality, now);
				long lastPeriodDebt = (wakeTimeSinceDayStart - weekDay.from) - sleepSum;
				debt = dayRecords.get(dayRecords.size() - 1).getWakeupDate() + lastPeriodDebt;
				optimumWakingTime = dayRecords.get(dayRecords.size() - 1).getSleepDate() + lastPeriodDebt;
			}

			chartDays[i] = new ChartDay(weekDay.from, weekDay.until, debt, accumDebt, optimumWakingTime, dayRecords);
			
			accumDebt += debt;
		}
		
		// Instantiate the days that don't have 
		accumDebt = 0;
		for (int i = 0; i < chartDays.length; i++) {
			if (i == 0) {
				accumDebt = 0;
			} else {
				accumDebt = chartDays[i - 1].accumulatedDebt;
			}
			
			if (chartDays[i] == null) {
				final WeekDay weekDay = weekDays.get(i);
				chartDays[i] = new ChartDay(weekDay.from, weekDay.until, 0, 0, 0, new ArrayList<DayRecord>());
			}
		}
		
		return chartDays;
	}
	
	/**
	 * Calculates the week sleep debt
	 * @param chartDays the week days
	 * @return the week sleep debt
	 */
	static long getWeekSleepDebt(final ChartDay[] chartDays) {
		return chartDays[chartDays.length - 1].accumulatedDebt;
	}
	
	/**
	 * Calculates the minimum time slept in a day
	 * @param chartDays an array of chart days
	 * @return the minimum time slept in a day
	 */
	static int getMinTimeSleptInADay(final ChartDay[] chartDays) {
		if (chartDays.length < 1) {
			throw new IllegalArgumentException("Invalid chart days");
		}
		
		int min = sleepSum(chartDays[0].records);
		
		for (int i = 1; i < chartDays.length; i++) {
			int slept = sleepSum(chartDays[i].records);
			
			if (slept < min) {
				min = slept;
			}
		}
		
		return min;
	}
	
	/**
	 * Calculates the maximum time slept in a day
	 * @param chartDays an array of chart days
	 * @return the maximum time slept in a day
	 */
	static int getMaxTimeSleptInADay(final ChartDay[] chartDays) {
		if (chartDays.length < 1) {
			throw new IllegalArgumentException("Invalid chart days");
		}
		
		int max = sleepSum(chartDays[0].records);
		
		for (int i = 1; i < chartDays.length; i++) {
			int slept = sleepSum(chartDays[i].records);
			
			if (slept > max) {
				max = slept;
			}
		}
		
		return max;
	}
	
	/**
	 * Calculates the sum of the time slept over a list of day records.
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
	 * Calculates the average exhaustion level over a list of day records. Only the records with
	 * exhaustion level set (level > 0) are considered for the average calculation.
	 * @param records the day records list
	 * @return the calculated average or null if the records don't have exhaustion level set
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
		
		return count == 0 ? null : ExhaustionLevel.fromInt(exaustion / count);
	}
	
	/**
	 * Calculates the average exhaustion level over an array of chart days. Only the days with
	 * exhaustion level set (level > 0) are considered for the average calculation.
	 * @param chartDays the day records
	 * @return the calculated average or null if the days don't have exhaustion level set 
	 */
	static ExhaustionLevel averageExhaustionLevel(final ChartDay[] chartDays) {
		final ExhaustionLevel[] levels = new ExhaustionLevel[chartDays.length];
		
		for (int i = 0; i < chartDays.length; i++) {
			levels[i] = averageExhaustionLevel(chartDays[i].records);
		}
		
		int count = 0;
		int sum = 0;
		for (int i = 0; i < levels.length; i++) {
			final ExhaustionLevel level = levels[i];
			if (level != null) {
				count++;
				sum += level.getLevel();
			}
		}
		
		return count == 0 ? null : ExhaustionLevel.fromInt(sum / count);
	}
	
	/**
	 * Calculates the average sleep quality level over a list of day records. Only the records with
	 * sleep quality set (level > 0) are considered for the average calculation.
	 * @param records the day records list
	 * @return the calculated average or null if the records don't have sleep quality set.
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
		
		return count == 0 ? null : SleepQuality.fromInt(quality / count);
	}
	
	/**
	 * Calculates the average sleep quality level over a list of chart days. Only the days with
	 * sleep quality set (level > 0) are considered for the average calculation.
	 * @param records the day records
	 * @return the calculated average or null if the days don't have sleep quality set.
	 */
	static SleepQuality averageSleepQuality(final ChartDay[] chartDays) {
		final SleepQuality[] levels = new SleepQuality[chartDays.length];
		
		for (int i = 0; i < chartDays.length; i++) {
			levels[i] = averageSleepQuality(chartDays[i].records);
		}
		
		int count = 0;
		int sum = 0;
		for (int i = 0; i < levels.length; i++) {
			final SleepQuality level = levels[i];
			if (level != null) {
				count++;
				sum += level.getLevel();
			}
		}
		
		return count == 0 ? null : SleepQuality.fromInt(sum / count);
	}
	
	/**
	 * Returns the list of series values that correspond to a day record
	 * @param record the day records
	 * @param optimumWakingTime the optimum waking time for the period int in the record
	 * @return the list of series values that matches the record
	 */
	static List<SeriesValue> recordValues(final long dayStart, final long dayEnd, final DayRecord record, final long optimumWakingTime) {
		final List<SeriesValue> values = new ArrayList<SeriesValue>();
		
		if (record.getSleepDate() < dayStart) {
			/*
			 * sleep period starts in previous day 
			 */
			if (optimumWakingTime > dayStart) {
				if (optimumWakingTime < record.getWakeupDate()) { // oversleep
					values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.SLEEP));
					values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.OVERSLEEP));
					values.add(new SeriesValue(barValue(dayStart, record.getWakeupDate()), SeriesType.WAKE));
				} else if (optimumWakingTime > record.getWakeupDate()) { // undersleep
					
				} else { // exact sleep
					values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.SLEEP));
					values.add(new SeriesValue(barValue(dayStart, record.getWakeupDate()), SeriesType.WAKE));
				}
			} else {
				
			}
		} else if (record.getWakeupDate() > dayEnd) {
			/*
			 * sleep period ends in the next day
			 */
		} else {
			/*
			 * sleep period starts and ends in the current day
			 */
			values.add(new SeriesValue(barValue(dayStart, record.getSleepDate()), SeriesType.SLEEP));
			if (optimumWakingTime < record.getWakeupDate()) { // oversleep
				values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.OVERSLEEP));
				values.add(new SeriesValue(barValue(dayStart, record.getWakeupDate()), SeriesType.WAKE));
			} else if (optimumWakingTime > record.getWakeupDate()) { // undersleep
				values.add(new SeriesValue(barValue(dayStart, record.getWakeupDate()), SeriesType.UNDERSLEEP));
				values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.WAKE));
			} else { // exact sleep
				values.add(new SeriesValue(barValue(dayStart, record.getWakeupDate()), SeriesType.WAKE));
			}
		}
		
		return values;
	}
	
	static long barValue(final long dayStart, final long value) {
		return (24 * 60) - (value - dayStart) / 60;
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
	static long optimumWakingTime(final long start, final Profile profile, final long accumDebt, final ExhaustionLevel exhaustionLevel, 
			final SleepQuality sleepQuality, final long now) {
		final boolean isMale = profile.getGender() == 0;
		final long age = TimeUtils.ageFromDateOfBirth(profile.getDateOfBirth(), now);
		long base = SleepDuration.getDuration(age, isMale); 
		long delta = SleepDelta.getDelta(accumDebt, exhaustionLevel, sleepQuality);
		
		return start + base + delta;
	}
	
	/**
	 * Returns the list of days in a week, starting at the time in the start parameter
	 * @param start the time when the week starts
	 * @return the list of week days the correspond to the week
	 */
	static List<WeekDay> getWeek(final long start) {
		final List<WeekDay> week = new ArrayList<WeekDay>(7);
		long seconds = start;
		for (int i = 0; i < 7; i++) {
			week.add(new WeekDay(seconds, seconds + DAY_SECONDS - 1));
			seconds += DAY_SECONDS;
		}
		
		return week;
	}
}

/**
 * Represents a week day
 */
class WeekDay {
	final long from;
	final long until;

	public WeekDay(final long from, final long until) {
		super();
		this.from = from;
		this.until = until;
	}
}

/**
 * Represents a day in the bar chart
 */
class ChartDay {
	final long from;
	final long until;
	final long debt;
	final long accumulatedDebt;
	final long optimumWakingTime;
	final List<DayRecord> records;
	
	public ChartDay(final long from, final long until, final long debt, final long accumulatedDebt, final long optimumWakingTime, final List<DayRecord> records) {
		super();
		this.from = from;
		this.until = until;
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

/**
 * Represents a series value
 */
class SeriesValue {
	final long value;
	final SeriesType type;

	public SeriesValue(final long value, final SeriesType type) {
		super();
		this.value = value;
		this.type = type;
	}
}